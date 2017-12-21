package com.open.configs.domain;

/**
 * 各应用配置实体要实现此类,通过此类的{@link ConfigDomainInitBean#initMySelf()}方法来初始化一些信息.
 * 
 *
 */
public interface ConfigDomainInitBean {
	
	/**
	 * init by deserialize
	 */
	public void initMySelf();
}
