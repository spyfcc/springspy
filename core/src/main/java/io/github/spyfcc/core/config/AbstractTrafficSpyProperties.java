package io.github.spyfcc.core.config;

public class AbstractTrafficSpyProperties implements PropsConfig {

	 private boolean enabled = true;
	    private boolean maskSensitive = true;
	    private int maxBodySize = 2048;
	    private int memorySize = 1000;
	    private String filePath = "./logs/spy";
	    private String uiPath = "/spy";
	    private int workingthread = 2;
	    private Search search = new Search();
	    private Security security = new Security();

	    public static class Search {
	        private String type = "file";
	        public String getType() { return type; }
	        public void setType(String type) { this.type = type; }
	    }

	    public static class Security implements PropsConfig.SecurityConfig {
	        private String username = "spy";
	        private String password = "spy123";

	        @Override
	        public String getUsername() { return username; }
	        public void setUsername(String username) { this.username = username; }

	        @Override
	        public String getPassword() { return password; }
	        public void setPassword(String password) { this.password = password; }
	    }

	    @Override
	    public String getUiPath() { return uiPath; }
	    public void setUiPath(String uiPath) { this.uiPath = uiPath; }

	    @Override
	    public String getFilePath() { return filePath; }
	    public void setFilePath(String filePath) { this.filePath = filePath; }

	    @Override
	    public PropsConfig.SecurityConfig getSecurity() { return security; }
	    public Security getSecurityProperties() { return security; }
	    public void setSecurity(Security security) { this.security = security; }

	    public boolean isEnabled() { return enabled; }
	    public void setEnabled(boolean enabled) { this.enabled = enabled; }

	    public boolean isMaskSensitive() { return maskSensitive; }
	    public void setMaskSensitive(boolean maskSensitive) { this.maskSensitive = maskSensitive; }

	    public int getMaxBodySize() { return maxBodySize; }
	    public void setMaxBodySize(int maxBodySize) { this.maxBodySize = maxBodySize; }

	    public int getMemorySize() { return memorySize; }
	    public void setMemorySize(int memorySize) { this.memorySize = memorySize; }

	    public int getWorkingthread() { return workingthread; }
	    public void setWorkingthread(int workingthread) { this.workingthread = workingthread; }

	    public Search getSearch() { return search; }
	    public void setSearch(Search search) { this.search = search; }
	}