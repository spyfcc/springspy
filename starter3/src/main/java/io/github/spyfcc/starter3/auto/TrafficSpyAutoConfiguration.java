package io.github.spyfcc.starter3.auto;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.manager.StorageManager;
import io.github.spyfcc.core.store.FileStore;
import io.github.spyfcc.core.store.JdbcStore;
import io.github.spyfcc.core.store.SpyStore;
import io.github.spyfcc.core.support.SpyBanner;
import io.github.spyfcc.starter3.config.SpyWebConfig;
import io.github.spyfcc.starter3.config.TrafficSpyProperties;
import io.github.spyfcc.starter3.filter.SpyAuthFilter;
import io.github.spyfcc.starter3.filter.TrafficSpyFilter;

@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(TrafficSpyProperties.class)
@ConditionalOnProperty(name = "traffic.spy.enabled", havingValue = "true", matchIfMissing = true)
@Import({
        SpyWebConfig.class,
        io.github.spyfcc.starter3.controller.SpyUIScreenController.class,
        io.github.spyfcc.starter3.controller.SpyLogsRestController.class,
        io.github.spyfcc.starter3.controller.SpySearchController.class
})
public class TrafficSpyAutoConfiguration {

    public TrafficSpyAutoConfiguration() {
        SpyBanner.print();
    }

    @Bean
    public FilterRegistrationBean<SpyAuthFilter> spyAuthFilterRegistration(PropsConfig props) {
        FilterRegistrationBean<SpyAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SpyAuthFilter(props));

        String pattern = "/" + props.getUiPath().replaceAll("^/+", "") + "/*";

        registration.addUrlPatterns(pattern);
        registration.setName("spyAuthFilter");
        registration.setOrder(-100);

        return registration;
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService spyExecutor(TrafficSpyProperties props) {
        ClassLoader systemCl = ClassLoader.getSystemClassLoader();

        return Executors.newFixedThreadPool(
                props.getWorkingthread(),
                new java.util.concurrent.ThreadFactory() {

                    private final java.util.concurrent.atomic.AtomicInteger counter =
                            new java.util.concurrent.atomic.AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("traffic-spy-worker-" + counter.getAndIncrement());
                        t.setDaemon(true);
                        t.setContextClassLoader(systemCl);
                        return t;
                    }
                }
        );
    }

    @Bean
    @ConditionalOnProperty(name = "traffic.spy.storage.type", havingValue = "jdbc")
    public SpyStore jdbcSpyStore(
            TrafficSpyProperties props,
            ObjectProvider<DataSource> dataSourceProvider) {

        DataSource dataSource = dataSourceProvider.getIfAvailable();

        if (dataSource == null) {
            throw new IllegalStateException(
                    "traffic.spy.storage.type=rdbms selected but no DataSource bean found. " +
                    "Please add spring-boot-starter-jdbc and configure spring.datasource."
            );
        }

        return new JdbcStore(dataSource);
    }
    
    @Bean
    @ConditionalOnProperty(
            name = "traffic.spy.storage.type",
            havingValue = "file",
            matchIfMissing = true
    )
    public SpyStore fileSpyStore(TrafficSpyProperties props) {
        return new FileStore(props.getFilePath());
    }

    @Bean
    public StorageManager storageManager(
            ExecutorService spyExecutor,
            TrafficSpyProperties props,
            SpyStore spyStore) {

        return new StorageManager(
                spyExecutor,
                props.getMemorySize(),
                spyStore
        );
    }

    @Bean
    public FilterRegistrationBean<TrafficSpyFilter> trafficSpyFilter(
            StorageManager storageManager,
            TrafficSpyProperties props) {

        TrafficSpyFilter filter = new TrafficSpyFilter(
                storageManager,
                props.getMaxBodySize(),
                props.isMaskSensitive(),
                props
        );

        FilterRegistrationBean<TrafficSpyFilter> bean =
                new FilterRegistrationBean<>(filter);

        bean.addUrlPatterns("/*");
        bean.setOrder(org.springframework.core.Ordered.LOWEST_PRECEDENCE);
        return bean;
    }
}