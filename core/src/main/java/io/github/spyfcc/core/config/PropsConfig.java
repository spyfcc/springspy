package io.github.spyfcc.core.config;

public interface PropsConfig {
	
	String getUiPath();
	String getFilePath();
	
	SecurityConfig getSecurity();
	
	interface SecurityConfig {
		String getUsername();
		String getPassword();
	}

}
