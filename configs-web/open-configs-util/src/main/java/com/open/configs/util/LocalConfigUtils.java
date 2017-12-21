package com.open.configs.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.open.configs.domain.ZookeeperConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * read local config data uitl class
 * 
 * @author leishouguo
 *
 */
public class LocalConfigUtils {
	private static final Logger LOG = LoggerFactory.getLogger(LocalConfigUtils.class);
	
	public static final String ZOOKEEPER_CONFIG_DIR = "zookeeperConfig";
	
	/**
	 * 获取本地配置文件内容
	 * @param path
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static  byte[] readLocalConfigByClassPath(String path) throws URISyntaxException, IOException {
		String localFileName  = getLocalPath(path);
		return FileUtils.readFileToByteArray(new File(localFileName));
	}
	
	/**
	 * 获取本地配置文件内容
	 * @param path
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static  byte[] readLocalConfig(String path, ZookeeperConfig config) throws URISyntaxException, IOException {
		String localFileName = null;
		
		if(config != null && config.isUserLocalPath()){
			localFileName = config.getLocalPath() + "/" + ZOOKEEPER_CONFIG_DIR + path;
		}else{
			localFileName = getLocalPath(path);
		}
		
		return FileUtils.readFileToByteArray(new File(localFileName));
	}
	
	/**
	 * 获取本地配置文件内容
	 * @param path
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static  byte[] readMapLocalConfig(String path, ZookeeperConfig config) throws URISyntaxException, IOException {
		String localFileName = null;
		
		if(config != null && config.isUserLocalPath()){
			localFileName = config.getLocalPath() + "/" + ZOOKEEPER_CONFIG_DIR + path+"/mapString";
		}else{
			localFileName = getLocalPath(path+"/mapString");
		}
		
		return FileUtils.readFileToByteArray(new File(localFileName));
	}
	
	/**
	 * 获取本地配置文件地址
	 * @param path
	 * @return
	 * @throws URISyntaxException
	 */
	public static String getLocalPath(String path) throws URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("");

		if (url == null) {
			throw new RuntimeException(path + " is not exit!");
		}

		File file = new File(url.toURI());
		LOG.debug("{}====>{}", new Object[] { path, file });
		return file.getAbsolutePath() + "/" + ZOOKEEPER_CONFIG_DIR + path;
	}
	
	/**
	 * 写本地配置
	 * @param path
	 * @param data
	 */
	public static void writeConfig2Local(String path, byte[] data, ZookeeperConfig config) {
		try {
			writeConfig2LocalEx(path, data, config);
		} catch (Exception e) {
			LOG.error("saveConfig2Local error!!!", e);
		}
	}
	/**
	 * 写本地配置 map 字符串
	 * @param path
	 * @param data
	 */
	public static void writeMapConfig2Local(String path, byte[] data, ZookeeperConfig config) {
		try {
			writeMapConfig2LocalEx(path, data, config);
		} catch (Exception e) {
			LOG.error("saveConfig2Local error!!!", e);
		}
	}
	
	/**
	 * 获取本地配置文件内容(有异常)
	 * @param path
	 * @param data
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void writeConfig2LocalEx(String path, byte[] data, ZookeeperConfig config) throws URISyntaxException, IOException {
			String localFileName = null;
		
			if(config != null && config.isUserLocalPath()){
				localFileName = config.getLocalPath() + "/" + ZOOKEEPER_CONFIG_DIR + path;
			}else{
				localFileName = getLocalPath(path);
			}
			
			File file = new File(localFileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			FileUtils.writeByteArrayToFile(file, data);
	}
	
	/**
	 * 获取本地配置文件内容(有异常)
	 * @param path
	 * @param data
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void writeMapConfig2LocalEx(String path, byte[] data, ZookeeperConfig config) throws URISyntaxException, IOException {
			String localFileName = null;
		
			if(config != null && config.isUserLocalPath()){
				localFileName = config.getLocalPath() + "/" + ZOOKEEPER_CONFIG_DIR + path+"/mapString";
			}else{
				localFileName = getLocalPath(path);
			}
			
			File file = new File(localFileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			FileUtils.writeByteArrayToFile(file, data);
	}
}
