package io.github.spyfcc.core.config;

public class AbstractTrafficSpyProperties implements PropsConfig {

	private boolean enabled = true;
	private boolean maskSensitive = true;
	private int maxBodySize = 2048;
	private int memorySize = 1000;
	private String filePath = "./logs/spy";
	private String uiPath = "/spy";
	private int workingthread = 2;
	private Storage storage = new Storage();
	private NoSql noSql = new NoSql();

	private Security security = new Security();
	
	public static class NoSql {
	    /**
	     * mongo | elastic (future) | redis (future)
	     */
	    private String provider = "mongo";

	    public String getProvider() {
	        return provider;
	    }

	    public void setProvider(String provider) {
	        this.provider = (provider == null || provider.trim().isEmpty())
	                ? "mongo"
	                : provider.toLowerCase();
	    }

	    public boolean isMongo() {
	        return "mongo".equalsIgnoreCase(provider);
	    }
	}

	public static class Storage {

	    /**
	     * file | rdbms | nosql
	     */
	    private String type = "file";

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = (type == null || type.trim().isEmpty())
	                ? "file"
	                : type.trim().toLowerCase();
	    }

	    public boolean isFile() {
	        return "file".equalsIgnoreCase(type);
	    }

	    public boolean isRdbms() {
	        return "jdbc".equalsIgnoreCase(type);
	    }

	    public boolean isNoSql() {
	        return "nosql".equalsIgnoreCase(type);
	    }
	}

	public static class Security implements PropsConfig.SecurityConfig {
		private String username = "spy";
		private String password = "spy123";

		@Override
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		@Override
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	@Override
	public String getUiPath() {
		return uiPath;
	}

	public void setUiPath(String uiPath) {
		this.uiPath = uiPath;
	}

	@Override
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public PropsConfig.SecurityConfig getSecurity() {
		return security;
	}

	public Security getSecurityProperties() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isMaskSensitive() {
		return maskSensitive;
	}

	public void setMaskSensitive(boolean maskSensitive) {
		this.maskSensitive = maskSensitive;
	}

	public int getMaxBodySize() {
		return maxBodySize;
	}

	public void setMaxBodySize(int maxBodySize) {
		this.maxBodySize = maxBodySize;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public int getWorkingthread() {
		return workingthread;
	}

	public void setWorkingthread(int workingthread) {
		this.workingthread = workingthread;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public NoSql getNoSql() {
		return noSql;
	}

	public void setNoSql(NoSql noSql) {
		this.noSql = noSql;
	}

}