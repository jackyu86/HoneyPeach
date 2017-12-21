package com.open.configs.service;

import java.util.Map;

import com.open.configs.core.GateWayConfigClient;
import com.open.configs.domain.BaseConfig;
import com.open.configs.domain.GateWayZookeeperConfig;
import com.open.configs.domain.ZookeeperConfig;
import org.codehaus.jackson.type.JavaType;


/**
 * gateway configs service
 *
 */
public interface GateWayTypeConfigService {
	
	/**
	 * 根据path获取配置
	 * @param <T>
	 * @param path
	 * @param cls
	 * @return
	 */
	<T extends  BaseConfig> T getConfigByClass(String path, Class<T> cls) ;
	
	
	/**
	 * 根据path获取配置
	 * @param <T>
	 * @param path
	 * @param readLocal
	 * @param cls
	 * @return
	 */
	<T extends BaseConfig> T getConfigByClass(String path, boolean readLocal, Class<T> cls) ;
	
	/**
	 * 保存配置
	 * @param <T>
	 * @param path
	 * @param obj
	 * @return
	 */
	<T extends  BaseConfig>  boolean saveConfig(String path, T obj);
	void setGateWayZookeeperClient(GateWayConfigClient zookeeperClient);
	GateWayConfigClient getGateWayZookeeperClient();
	ZookeeperConfig getGateWayZookeeperConfig() ;
	void setGateWayZookeeperConfig(ZookeeperConfig zookeeperConfig);
	Map readConfigsBytype(String path, boolean readLocal, final JavaType type);


}
