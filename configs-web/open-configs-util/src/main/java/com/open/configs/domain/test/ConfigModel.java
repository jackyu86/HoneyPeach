package com.open.configs.domain.test;


import com.open.configs.core.ClientFactory;
import com.open.configs.domain.BaseConfig;
import com.open.configs.domain.ConfigDomainInitBean;
import com.open.configs.domain.ConfigOther;
import com.open.configs.service.TypeConfigService;
import com.open.configs.util.JsonUtils;

public class ConfigModel extends BaseConfig implements ConfigDomainInitBean {
	
	private int flag;
	//...

	
	@Override
	public void initMySelf() {
//		System.out.println("=====================================");
//		System.out.println("=============initMySelf==============");
//		System.out.println("=====================================");
	}
	
	
	public int getFlag() {
		return flag;
	}


	public void setFlag(int flag) {
		this.flag = flag;
	}


	public static void main(String[] args) {
		final ConfigModel configModel = new ConfigModel();
		configModel.setFlag(100);
		
		final ConfigOther db = new ConfigOther();
		db.setCacheSecond(1000);
		db.setConfigName("shguo");
		db.setYn(1);// 要配置成1不然不更新
		configModel.setZother(db);
		//生成json串
		String configStr = JsonUtils.writeValue(configModel, true);
		
		System.out.println(configStr);
		
		TypeConfigService configServiceImpl;
		try {
			configServiceImpl = ClientFactory.getTypeConfigService();
			for(int i=0;i<10;i++){
				ConfigModel getConfigModel = configServiceImpl.getConfigByClass(
					"/config/order-middleware-config", true,
					ConfigModel.class);
				System.out.println(getConfigModel != null ? getConfigModel.getFlag() : null);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
