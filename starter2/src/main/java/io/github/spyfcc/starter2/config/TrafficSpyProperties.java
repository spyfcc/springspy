package io.github.spyfcc.starter2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.spyfcc.core.config.AbstractTrafficSpyProperties;


@ConfigurationProperties(prefix = "traffic.spy")
public class TrafficSpyProperties extends AbstractTrafficSpyProperties {

	
}
