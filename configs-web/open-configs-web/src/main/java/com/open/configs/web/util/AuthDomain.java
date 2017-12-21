package com.open.configs.web.util;

import java.util.Map;

import com.open.configs.domain.BaseConfig;
import com.open.configs.domain.ConfigDomainInitBean;

/**
 * 
 * @author Administrator
 *
 */
public class AuthDomain extends BaseConfig implements ConfigDomainInitBean {
	public static final String STATIC_SUPPER_TOKEN = "360buy.com##zookeeper";
	private Map<String, String> auth;
	private String supperToken;

	
	public String getSupperToken() {
		return supperToken;
	}

	public void setSupperToken(String supperToken) {
		this.supperToken = supperToken;
	}

	public Map<String, String> getAuth() {
		return auth;
	}

	public void setAuth(Map<String, String> auth) {
		this.auth = auth;
	}
	
	/**
	 * auth token
	 * @param path
	 * @param password
	 * @return
	 */
	public boolean auth(String path, String password){
		//super password pass
        if (STATIC_SUPPER_TOKEN.equalsIgnoreCase(password) || auth == null || password.equalsIgnoreCase(supperToken)) {
			return true;
		}
		
		//auth need supper password
		if(path.equals("auth") && (!STATIC_SUPPER_TOKEN.equalsIgnoreCase(password) || password.equalsIgnoreCase(supperToken))){
			return false;
		}
		String dbPwd = auth.get(path);
		if(dbPwd == null || !password.equalsIgnoreCase(dbPwd)){
			return false;
		}
		return true;
	}
	
}