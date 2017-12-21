package com.open.configs.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.open.configs.domain.GateWayZookeeperConfig;
import com.open.configs.domain.ZookeeperConfig;
import com.open.configs.service.GateWayTypeConfigService;
import com.open.configs.service.TypeConfigService;
import com.open.configs.service.impl.GateWayTypeConfigServiceImpl;
import com.open.configs.service.impl.TypeConfigServiceImpl;
import com.open.configs.util.JsonUtils;
import com.open.configs.util.LocalConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gateway接口auth链接工厂
 *
 */
public class GateWayConfigClientFactory {
	private static final Logger LOG = LoggerFactory.getLogger(GateWayConfigClientFactory.class);
	static Map<String, GateWayConfigClient> clients = new ConcurrentHashMap<String, GateWayConfigClient>();
	
	/**
	 * 中心配置服务实例
	 */
	private static GateWayTypeConfigService gateWayTypeConfigService;
	
	/**
	 * zookeeper配置类
	 */
	private static ZookeeperConfig zookeeperConfig;
	
	
	/**
	 * 获取zookeeper客户端
	 * @return
	 */
	public static synchronized GateWayConfigClient getConfigClient() {
		String configs = zookeeperConfig.getZkConfigs();
		if(zookeeperConfig==null&&configs==null&&configs.trim().isEmpty()){
			return null;
		}
		if (clients.containsKey(configs)) {
			return clients.get(configs);
		}
		return getConfigClient(configs, zookeeperConfig.getZkCheckInterval());
	}

	/**
	 * 获取zookeeper客户端
	 * @param configs	服务器地址
	 * @param timeout　	超时时间
	 * @return
	 */
	private static  GateWayConfigClient getConfigClient(String configs,int timeout) {
		if (clients.containsKey(configs)) {
			return clients.get(configs);
		}

		try {
			GateWayConfigClient client = new GateWayConfigClient(configs, timeout);
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
	public static GateWayConfigClient getConfigClient(ZookeeperConfig config) {
		return getConfigClient(zookeeperConfig.getZkConfigs(), zookeeperConfig.getZkCheckInterval());
	}
	
	
	/**
	 * 获取配置服务类
	 * @return
	 * @throws Exception
	 */
	public static  synchronized GateWayTypeConfigService getTypeConfigService() throws Exception{
		if(gateWayTypeConfigService == null){
			GateWayConfigClient client = GateWayConfigClientFactory.getConfigClient(getConfig());
			gateWayTypeConfigService = new GateWayTypeConfigServiceImpl();
			gateWayTypeConfigService.setGateWayZookeeperClient(client);
			gateWayTypeConfigService.setGateWayZookeeperConfig(getConfig());
				
		}
		return gateWayTypeConfigService;
	}
	
	/**
	 * 获取配置文件
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static synchronized ZookeeperConfig getConfig() throws URISyntaxException, IOException{
		if(zookeeperConfig == null){
			byte[] configData = LocalConfigUtils.readLocalConfigByClassPath("/zookeeper.json");
			zookeeperConfig = JsonUtils.readValue(new ByteArrayInputStream(configData), ZookeeperConfig.class);
		}
		return zookeeperConfig;
	}
	
}
