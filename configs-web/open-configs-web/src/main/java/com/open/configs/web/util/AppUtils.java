package com.open.configs.web.util;

import org.apache.zookeeper.common.PathUtils;

public class AppUtils {
	public static final String AUTH = "/config/auth/erp";
	
	public static String getApp(String path){
		String app = null;
		PathUtils.validatePath(path);
		int index = path.indexOf('/', 1);
		if(index > 0){
			app = path.substring(index);
		}
		index = app.indexOf('/', 1);
		if(index > 0){
			app = app.substring(1,index);
		}
		if(app == null){
			throw new IllegalArgumentException("path error!!path=>" + path);
		}
		return app;
	}
}	
