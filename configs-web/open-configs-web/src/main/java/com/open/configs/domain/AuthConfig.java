package com.open.configs.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.open.configs.core.ClientFactory;
import com.open.configs.core.GateWayConfigClientFactory;
import com.open.configs.service.GateWayTypeConfigService;
import com.open.configs.service.TypeConfigService;
import com.open.configs.util.JsonUtils;

public class AuthConfig extends BaseConfig implements ConfigDomainInitBean {
	
	private static AuthConfig instance = null;
	
	public static AuthConfig getInstance(){
			GateWayTypeConfigService configServiceImpl;
			try {
				configServiceImpl = GateWayConfigClientFactory.getTypeConfigService();
				AuthConfig newInstance = configServiceImpl.getConfigByClass("/configs/auth/config", true, AuthConfig.class);
				if(newInstance != null){
					instance = newInstance;
				}
			} catch (Exception e) {
				throw new RuntimeException("init AuthConfig error!!!");
			}
		
		return instance;
	}
	/**
	 * zk服务组
	 */
	private Map<String, String> zkServerGroups;
	
	/**
	 * 后台path修改权限
	 */
	private Map<String, String> appTokens;

	public Map<String, String> getZkServerGroups() {
		return zkServerGroups;
	}

	public void setZkServerGroups(Map<String, String> zkServerGroups) {
		this.zkServerGroups = zkServerGroups;
	}

	public Map<String, String> getAppTokens() {
		return appTokens;
	}

	public void setAppTokens(Map<String, String> appTokens) {
		this.appTokens = appTokens;
	}
	
	
	public static void main(String[] args) {
		AuthConfig config = new AuthConfig();
		
		Map<String, String> servers = new HashMap<String, String>();
		servers.put("default", "10.10.225.38:2182,10.10.225.38:2181,10.10.225.38:2183");
		
		config.setZkServerGroups(servers);
		
		
		//配置项的其他信息
		final ConfigOther db = new ConfigOther();
		db.setCacheSecond(1000);
		db.setConfigName("shguo");
		db.setYn(1);//要配置成1不然不更新
		db.setLastSaveTime(new Date());
		
		config.setZother(db);

		//c. 生成json串
		String configStr = JsonUtils.writeValue(config, true);
		
		System.out.println(configStr);
	}
	
}
