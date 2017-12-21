package com.open.configs.domain;



/**
 */
public abstract class BaseConfig implements ConfigDomainInitBean {
	
	protected ConfigOther zother;
	
	@Override
	public void initMySelf() {
	}

	public ConfigOther getZother() {
		return zother;
	}

	public void setZother(ConfigOther other) {
		this.zother = other;
	}

}
