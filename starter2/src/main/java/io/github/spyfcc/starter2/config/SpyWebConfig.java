package io.github.spyfcc.starter2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.support.SpyPathSupport;
import io.github.spyfcc.starter2.interceptor.SpyAnnotationInterceptor;


@Configuration
public class SpyWebConfig implements WebMvcConfigurer {
	
	private final PropsConfig props;
	
	public SpyWebConfig(PropsConfig props) {
		this.props = props;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SpyAnnotationInterceptor());
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String uiPath = SpyPathSupport.uiBasePath(props);
		registry.addResourceHandler(uiPath + "/static/**").addResourceLocations("classpath:/static/");
	}
	
	
}