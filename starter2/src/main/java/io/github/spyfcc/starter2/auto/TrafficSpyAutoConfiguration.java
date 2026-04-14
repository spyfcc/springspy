package io.github.spyfcc.starter2.auto;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.manager.StorageManager;
import io.github.spyfcc.core.support.SpyBanner;
import io.github.spyfcc.starter2.config.SpySearchConfig;
import io.github.spyfcc.starter2.config.SpyWebConfig;
import io.github.spyfcc.starter2.config.TrafficSpyProperties;
import io.github.spyfcc.starter2.filter.SpyAuthFilter;
import io.github.spyfcc.starter2.filter.TrafficSpyFilter;


@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(TrafficSpyProperties.class)
@ConditionalOnProperty(name = "traffic.spy.enabled", havingValue = "true", matchIfMissing = true)
@Import({ SpyWebConfig.class, SpySearchConfig.class, io.github.spyfcc.starter2.controller.SpyUIScreenController.class,
		io.github.spyfcc.starter2.controller.SpyLogsRestController.class,
		io.github.spyfcc.starter2.controller.SpySearchController.class })
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
		return Executors.newFixedThreadPool(props.getWorkingthread(), r -> {
			Thread t = new Thread(r, "traffic-spy-worker");
			t.setDaemon(true);
			t.setContextClassLoader(systemCl);
			return t;
		});
	}

	@Bean
	public StorageManager storageManager(ExecutorService spyExecutor, TrafficSpyProperties props) {
		return new StorageManager(spyExecutor, props.getMemorySize(), props.getFilePath());
	}

	@Bean
	public FilterRegistrationBean<TrafficSpyFilter> trafficSpyFilter(StorageManager storageManager,
			TrafficSpyProperties props) {
		TrafficSpyFilter filter = new TrafficSpyFilter(storageManager, props.getMaxBodySize(), props.isMaskSensitive(),
				props);
		FilterRegistrationBean<TrafficSpyFilter> bean = new FilterRegistrationBean<>(filter);
		bean.addUrlPatterns("/*");
		bean.setOrder(org.springframework.core.Ordered.LOWEST_PRECEDENCE);
		return bean;
	}
}