package com.open.configs.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.open.configs.domain.ZookeeperConfig;
import com.open.configs.service.TypeConfigService;
import com.open.configs.service.impl.TypeConfigServiceImpl;
import com.open.configs.util.JsonUtils;
import com.open.configs.util.LocalConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取zookeeper客户端
 *
 */
public class ClientFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ClientFactory.class);
	static Map<String, OpenZookeeperClient> clients = new ConcurrentHashMap<String, OpenZookeeperClient>();
	
	/**
	 * 中心配置服务实例
	 */
	private static TypeConfigService typeConfigService;
	
	/**
	 * zookeeper配置类
	 */
	private static ZookeeperConfig config;
	
	
	/**
	 * 获取zookeeper客户端
	 * @param configs	服务器地址 
	 * @param timeout　	超时时间
	 * @return
	 */
	public static synchronized OpenZookeeperClient getZookeeperClient(String configs, int timeout) {
		if (clients.containsKey(configs)) {
			return clients.get(configs);
		}

		try {
			OpenZookeeperClient client = new OpenZookeeperClient(configs, timeout);
			clients.put(configs, client);
			return client;
		} catch (IOException e) {
			LOG.error("getJdZookeeperClient error!!!", e);
		} finally {
		}
		return null;
	}
	
	/**
	 * 获取zookeeper客户端
	 * @param config
	 * @return
	 */
	public static OpenZookeeperClient getZookeeperClient(ZookeeperConfig config) {
		return getZookeeperClient(config.getZkConfigs(), config.getZkCheckInterval());
	}
	
	
	/**
	 * 获取配置服务类
	 * @return
	 * @throws Exception
	 */
	public static  synchronized TypeConfigService getTypeConfigService() throws Exception{
		if(typeConfigService == null){
			OpenZookeeperClient client = ClientFactory.getZookeeperClient(getConfig());
				typeConfigService = new TypeConfigServiceImpl();
				typeConfigService.setZookeeperClient(client);
				typeConfigService.setZookeeperConfig(getConfig());
				
		}
		return typeConfigService;
	}
	
	/**
	 * 获取配置文件
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static synchronized ZookeeperConfig getConfig() throws URISyntaxException, IOException{
		if(config == null){
			byte[] configData = LocalConfigUtils.readLocalConfigByClassPath("/zookeeper.json");
			config = JsonUtils.readValue(new ByteArrayInputStream(configData), ZookeeperConfig.class);
		}
		return config;
	}
	
}
