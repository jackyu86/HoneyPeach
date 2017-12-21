package com.open.configs.service;

import com.open.configs.core.OpenZookeeperClient;
import com.open.configs.domain.BaseConfig;
import com.open.configs.domain.ZookeeperConfig;
import org.codehaus.jackson.type.JavaType;

import java.util.Map;


/**
 * zookeeper configs service
 * 实例需要实现BaseConfig接口.<br/>
 * 
 * 获取到的配置实例都是ConfigDB<? extends BaseConfig>
 * @author leishouguo
 *
 */
public interface TypeConfigService {
	
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
	
	public void setZookeeperClient(OpenZookeeperClient zookeeperClient);
	public OpenZookeeperClient getZookeeperClient();
	public ZookeeperConfig getZookeeperConfig() ;
	public void setZookeeperConfig(ZookeeperConfig zookeeperConfig);
	 Map readConfigsBytype(String path, boolean readLocal, final JavaType type);


}
