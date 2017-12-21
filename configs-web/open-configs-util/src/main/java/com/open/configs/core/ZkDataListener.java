package com.open.configs.core;

/**
 * 节点数据监听器
 *
 */
public interface ZkDataListener {

	/**
	 * 处理节点数据变化事件
	 * @param path
	 * @param data
	 */
	public void handleDataChange(String path, Object data) ;
	
	/**
	 * 处理节点删除事件
	 * @param path
	 */
	public void handleDataDeleted(String path);

}
