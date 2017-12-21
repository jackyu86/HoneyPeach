package com.open.configs.domain;

import org.apache.commons.lang.StringUtils;

/**
 */
public class ZookeeperConfig {
	
	/**
	 * zookeeper servers 
	 */
	private String zkConfigs;
	
	/**
	 * timeouts
	 */
	private int zkCheckInterval = 3000;
	
	
	public static final String CONFIG_TYPE_ZK = "ZK";
	public static final String CONFIG_TYPE_WS = "WS";
	
	/**
	 * 获取类型
	 */
	private String configType = CONFIG_TYPE_ZK;
	
	/**
	 * check ws interval
	 */
	private int wsCheckInterval = 1000;
	
	private String environment;
	
	/**
	 * 版本
	 */
	private int version;
	
	
	/**
	 * 存储在本地的文件
	 */
	private String localPath;
	
	/**
	 * 是否更新后
	 */
	private boolean  updateAck = true;
	

	public String getZkConfigs() {
		return zkConfigs;
	}

	public void setZkConfigs(String zkConfigs) {
		this.zkConfigs = zkConfigs;
	}

	public int getZkCheckInterval() {
		return zkCheckInterval;
	}

	public void setZkCheckInterval(int zkCheckInterval) {
		this.zkCheckInterval = zkCheckInterval;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public int getWsCheckInterval() {
		return wsCheckInterval;
	}

	public void setWsCheckInterval(int wsCheckInterval) {
		this.wsCheckInterval = wsCheckInterval;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	/**
	 * 根据环境来获取路径
	 * @param path
	 * @return
	 */
	public String getPath(String path){
		if(path.endsWith("/")){
			return path + environment;
		}else{
			return path +"/" + environment;
		}
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public boolean isUpdateAck() {
		return updateAck;
	}

	public void setUpdateAck(boolean updateAck) {
		this.updateAck = updateAck;
	}
	
	/**
	 * 是否配置存储在本地的文件
	 * @return
	 */
	public boolean isUserLocalPath(){
		return StringUtils.isNotBlank(localPath);
	}
	
}
