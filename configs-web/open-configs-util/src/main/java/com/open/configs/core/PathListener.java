package com.open.configs.core;

import java.util.Map;

/**
 * 节点数据监听器
 *
 */
public interface PathListener {

	/**
	 * 处理节点数据变化事件
	 * @param map appid 接口映射关系
	 */
	 void ZkDataListener(Map<String, String> map) ;


	void exceptionCaught(Throwable throwable);
}
