package com.open.configs.domain;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 配置实体基本类
 * 所有应用的配置实体都是此类型,　其中泛型为各应用的实体
 *
 */
public class ConfigOther {
	
    
    /**
	 * config name
	 */
	private String configName;
	
	/**
	 * config description
	 */
	private String desc;
	
	/**
	 * check cache
	 */
	private Integer cacheSecond;
	
	
	/**
	 * system operator
	 */
	private String operator;
	
	/**
	 * effect flag
	 */
	private int yn;

	
	/**
	 * 配置人工版本号
	 */
	private int version;
	
	
	/**
	 * 是否需要人工版本号
	 */
	private boolean versionFlag;
	
	
	private long regetInterval = -1; //30 * 60;//Interval seconds 
	
	@JsonIgnore
	private Date lastUpdateTime =  new Date();
	
	@JsonIgnore
	private Exception ex;
	
	/**
	 * 最后更新时间
	 */
	private Date lastSaveTime;
	

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	

	public Integer getCacheSecond() {
		return cacheSecond;
	}

	public void setCacheSecond(Integer cacheSecond) {
		this.cacheSecond = cacheSecond;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getYn() {
		return yn;
	}

	public void setYn(int yn) {
		this.yn = yn;
	}
	
	@JsonIgnore
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@JsonIgnore
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@JsonIgnore
	public Exception getEx() {
		return ex;
	}
	
	@JsonIgnore
	public void setEx(Exception ex) {
		this.ex = ex;
	}

	/**
	 * 获取配置信息是是否有效
	 * @return
	 */
	public boolean effect(){
		if(this.yn == 1){
			return true;
		}
		return false;
	}

	public int getVersion() {
		return version;
	}

	public boolean isVersionFlag() {
		return versionFlag;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setVersionFlag(boolean versionFlag) {
		this.versionFlag = versionFlag;
	}

	public long getRegetInterval() {
		return regetInterval;
	}

	public void setRegetInterval(long regetInterval) {
		this.regetInterval = regetInterval;
	}
	
	public Date getLastSaveTime() {
		return lastSaveTime;
	}

	public void setLastSaveTime(Date lastSaveTime) {
		this.lastSaveTime = lastSaveTime;
	}

	/**
	 * 判断是否重新从zookeeper取
	 * @return
	 */
	public boolean checkReget(){
		
		if(regetInterval < 1 || lastUpdateTime == null){
			return false;
		}
		
		if((System.currentTimeMillis() - lastUpdateTime.getTime()) > regetInterval * 1000){
			lastUpdateTime = new Date(); 
			return true;
		}
		return false;
	}

	/**
	 * 成立条件为:<br/>
	 * yn == 1 && (versionFlag && oldConfig.zothor.version == version)<p>
	 * @param oldConfig
	 * @return
	 */
	public boolean versionEffect(BaseConfig oldConfig) {
		if(!effect()){
			return false;
		}
		//
		//主要是为上线时配置不兼容的情况,如覆盖某个url时,　新重启的机器用新,　未重启的机器就用旧的了.　
		//
		if(versionFlag && oldConfig.getZother().version != version) {
			return false;
		}
		return true;
	}
}