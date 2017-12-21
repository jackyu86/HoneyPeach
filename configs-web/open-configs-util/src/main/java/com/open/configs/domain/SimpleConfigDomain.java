package com.open.configs.domain;


/**
 * 
 *
 */
public class SimpleConfigDomain extends BaseConfig implements ConfigDomainInitBean {
	int hello;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6168128949505180855L;

	@Override
	public void initMySelf() {
	
	}

	public int getHello() {
		return hello;
	}

	public void setHello(int hello) {
		this.hello = hello;
	}
	
}
