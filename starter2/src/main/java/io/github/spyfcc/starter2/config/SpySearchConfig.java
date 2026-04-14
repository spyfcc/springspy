package io.github.spyfcc.starter2.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.search.SpyFileSearchService;
import io.github.spyfcc.core.search.SpySearchService;

@Configuration
public class SpySearchConfig {
	@Bean
	@ConditionalOnProperty(
	name = "traffic.spy.search.type",
	havingValue = "file",
	matchIfMissing = true)
	public SpySearchService spySearchService (PropsConfig props) {
		return new SpyFileSearchService(props);
	}

}