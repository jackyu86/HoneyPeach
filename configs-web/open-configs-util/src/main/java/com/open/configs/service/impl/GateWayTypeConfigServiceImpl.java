package com.open.configs.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.open.configs.core.GateWayConfigClient;
import com.open.configs.core.OpenZookeeperClient;
import com.open.configs.core.PathListener;
import com.open.configs.core.ZkDataListener;
import com.open.configs.domain.BaseConfig;
import com.open.configs.domain.GateWayZookeeperConfig;
import com.open.configs.domain.ZookeeperConfig;
import com.open.configs.service.GateWayTypeConfigService;
import com.open.configs.util.GZipUtils;
import com.open.configs.util.JsonUtils;
import com.open.configs.util.LocalConfigUtils;
import com.open.configs.util.LocalIpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateWayTypeConfigServiceImpl implements GateWayTypeConfigService {
	private static final String UTF_8 = "UTF-8";

	private static final Logger LOG = LoggerFactory.getLogger(GateWayTypeConfigServiceImpl.class);

	/**
	 * zookeeper连接客户端
	 */
	protected GateWayConfigClient zookeeperClient;

	protected ZookeeperConfig zookeeperConfig;

	/**
	 * 本地配置缓存
	 */
	protected static Map<String, BaseConfig> configs = new ConcurrentHashMap<String, BaseConfig>();

	protected static Map<String, String> ackCache = new ConcurrentHashMap<String, String>();
	/**
	 * 注册
	 */
	protected static Map<String, String> dataListenerCache = new ConcurrentHashMap<String, String>();




	@Override
	public <T extends BaseConfig> T getConfigByClass(String path, Class<T> cls) {

		return (T)getConfigByType(path, false, TypeFactory.type(cls));
	}

	@Override
	public <T extends BaseConfig> T getConfigByClass(String path, boolean readLocal, Class<T> cls) {
		return (T)getConfigByType(path, readLocal, TypeFactory.type(cls));
	}

	public  <T extends  BaseConfig> T getConfigByType(String path, final TypeReference<T> type) {
		return (T)getConfigByType(path, false, type);
	}

	public <T extends BaseConfig> T getConfigByType(String path, boolean readLocal, final TypeReference<T> type) {
		return (T)getConfigByType(path, readLocal, TypeFactory.type(type));
	}

	public <T extends  BaseConfig> T getConfigByType(String path, boolean readLocal, final JavaType type) {
		//check zk is connected
		//jdZookeeperClient.getZk();

		path = zookeeperConfig.getPath(path);
		if (configs.containsKey(path)) {
			T result =  (T) configs.get(path);
			checkReget(result, path, type);
			return result;
		} else {

			registerListener(path, type);

			T base = (T)readConfigs(path, readLocal, type);

			// init bean msg, use initMyself method
			if (base != null) {
				base.initMySelf();
				configs.put(path, base);
			} else {
				throw new RuntimeException("first read configs error!!!");
			}
			return (T) base;
		}

	}



	@Override
	public <T extends  BaseConfig> boolean saveConfig(String path, T obj) {
		path = zookeeperConfig.getPath(path);
		try{
			String configStr = JsonUtils.writeValue(obj, true);
			byte[] data = GZipUtils.compress(configStr.getBytes(UTF_8));
			zookeeperClient.writeData(path, data);
			return true;
		}catch (Exception e) {
			LOG.error("saveConfig　{}　error!!!", new Object[]{path});
			return false;
		}
	}

	/**
	 * 获取配置内容
	 *
	 * @param <T>
	 * @param path
	 * @param
	 * @return
	 */
	private <T extends BaseConfig> T readConfigs(String path, boolean readLocal, final JavaType type) {
		boolean isReadFromLocal = false;
		byte[] data = null;
		try {
			data = zookeeperClient.readByteData(path, null, true);
		} catch (Exception e) {
			if (readLocal) {
				LOG.error("jdZookeeperClient read  error!!!", e);
				LOG.error("read data from local");

				try {
					data = LocalConfigUtils.readLocalConfig(path, zookeeperConfig);
					isReadFromLocal = true;
				} catch (URISyntaxException e1) {
					LOG.error("readLocalConfig error!!!", e);
				} catch (IOException e1) {
					LOG.error("readLocalConfig error!!!", e);
				}
			}
		}
		if (data != null) {
			T base =  (T)deserialize(type, data);
			if (base != null && !isReadFromLocal && readLocal) {
				LocalConfigUtils.writeConfig2Local(path, data, zookeeperConfig);
			}
			return base;
		}

		return null;

	}



	/**
	 * 注册内容监听
	 *
	 * @param <T>
	 * @param path
	 * @param
	 */
	public  synchronized <T extends BaseConfig> void registerListener(String path, final JavaType type) {
		if(dataListenerCache.containsKey(path)){
			return ;
		}

		PathListener zkDataListener  = new PathListener() {

			@Override
			public void ZkDataListener(Map<String,String> data) {
				LOG.error("handleDataChange:" + path);

				final T base = (T)deserialize(type,data.toString().getBytes());
				if (base != null) {
					BaseConfig oldConfig = configs.get(path);
					if(base.getZother().versionEffect(oldConfig)){
						base.initMySelf();
						configs.put(path, base);
						LocalConfigUtils.writeConfig2Local(path, data.toString().getBytes(), zookeeperConfig);
						dealAck(path);
					}
				}
			}

			private void dealAck(final String path) {
				if(zookeeperConfig.isUpdateAck()){
					try {
						String ackPath = path + "/ack";
						if(!zookeeperClient.exists(ackPath)){
							zookeeperClient.create(ackPath, new byte[1], CreateMode.PERSISTENT);
						}
						String oldAckSessionPath = ackCache.get(path);
						if(StringUtils.isNotEmpty(oldAckSessionPath)){
							zookeeperClient.delete(oldAckSessionPath);
						}
						String ackSessionPath = ackPath + "/" + LocalIpUtils.getAckData(zookeeperClient.getZk().getSessionId()+"");
						zookeeperClient.recursiveSafeCreate(ackSessionPath, new byte[1], CreateMode.EPHEMERAL);
						ackCache.put(path, ackSessionPath);
					} catch (Exception e) {
						LOG.error("set ack error!!!", e);
					}
				}
			}

			@Override
			public void exceptionCaught(Throwable throwable) {
				//
			}
		};
		zookeeperClient.subscribeDataChanges(path, zkDataListener);
		dataListenerCache.put(path, "True");
	}


	public void setGateWayZookeeperClient(
			GateWayConfigClient zookeeperClient) {
		this.zookeeperClient = zookeeperClient;
	}


	public GateWayConfigClient getGateWayZookeeperClient() {
		return zookeeperClient;
	}

	public ZookeeperConfig getGateWayZookeeperConfig() {
		return zookeeperConfig;
	}

	public void setGateWayZookeeperConfig(ZookeeperConfig zookeeperConfig) {
		this.zookeeperConfig = zookeeperConfig;
	}

	public static <T extends BaseConfig> void putConfigCache(String path, T value) {
		configs.put(path, value);
	}

	/**
	 * 反序列化配置信息
	 * @param <T>
	 * @param type
	 * @param data
	 * @return
	 */
	public static <T> T deserialize(final JavaType type, byte[] data) {
		T base = null;
		try {
			data = GZipUtils.decompress(data);
			base = (T)JsonUtils.readValue(new ByteArrayInputStream(data), type);
		} catch (Exception e) {
			LOG.error("deserialize data error!!!", e);
			throw new RuntimeException(e);
		}

		return base;
	}


	private static Map<String, Boolean> regetWorkers = new ConcurrentHashMap<String, Boolean>();

	/**
	 * 异步重新获取(断开后会重新取)
	 * @param <T>
	 * @param oldResult		旧值
	 * @param path			路径
	 * @param type			类型
	 */
	private <T extends BaseConfig> void checkReget(final T oldResult, final String path, final JavaType type) {
		synchronized (this) {
			try {
				// 重取操作
				BaseConfig c = (BaseConfig) oldResult;
				if (OpenZookeeperClient.pathShouldReget(path) || c.getZother().checkReget()) {
					if (!regetWorkers.containsKey(path)) {// 一个path启动一个worker
						regetWorkers.put(path, true);
						Thread t = new Thread() {

							@Override
							public void run() {
								boolean flag = false;
								while (!flag) {
									LOG.error("[jdzk] reget " + path);
									try {
										T resultZk = (T) readConfigs(path, false, type);
										if (resultZk != null) {
											resultZk.initMySelf();
											configs.put(path, resultZk);
											OpenZookeeperClient.changeReget(path, false);
										}
										regetWorkers.remove(path);
										flag = true;
										return;
									} catch (Exception e) {
										LOG.error("[jdzk] reget " + path + " error!!!");
										try {
											Thread.sleep(5000);
										} catch (InterruptedException e1) {
											regetWorkers.remove(path);
										}
									}
								}
							}

						};
						LOG.error("==start==reget " + path + " thread!!!");
						t.start();
					}
				}
			} catch (Exception e) {
				LOG.error("==reget error!!!", e);
			}

		}
	}
	/**
	 * 获取配置内容
	 * 
	 * @param
	 * @param path
	 * @param
	 * @return
	 */
	public Map readConfigsBytype(String path, boolean readLocal, final JavaType type) {
		boolean isReadFromLocal = false;
		byte[] data = null;
		try {
			data = zookeeperClient.readByteData(path, null, true);
		} catch (Exception e) {
			if (readLocal) {
				LOG.error("jdZookeeperClient read  error!!!", e);
				LOG.error("read data from local");

				try {
					data = LocalConfigUtils.readMapLocalConfig(path, zookeeperConfig);
					isReadFromLocal = true;
				} catch (URISyntaxException e1) {
					LOG.error("readLocalConfig error!!!", e);
				} catch (IOException e1) {
					LOG.error("readLocalConfig error!!!", e);
				}
			}
		}
		if (data != null) {
			Map base =  (Map)deserialize(type, data);
			if (base != null && !isReadFromLocal && readLocal) {
				LocalConfigUtils.writeMapConfig2Local(path, data, zookeeperConfig);
			}
			return base;
		}

		return null;

	}

}
