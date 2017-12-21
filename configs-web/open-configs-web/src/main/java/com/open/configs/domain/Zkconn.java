package com.open.configs.domain;

import java.util.HashSet;
import java.util.Set;

public class Zkconn {
	public String ip;
	public String sid;
	public Set<String> paths = new HashSet<String>();
	
	public void addPath(String path){
		paths.add(path);
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Set<String> getPaths() {
		return paths;
	}
	public void setPaths(Set<String> paths) {
		this.paths = paths;
	}

	@Override
	public String toString() {
		return "Zkconn [ip=" + ip + ", paths=" + paths + ", sid=" + sid + "]";
	}

	public static Zkconn create(String clientIp, String sid2) {
		Zkconn zkconn = new Zkconn();
		zkconn.ip = clientIp;
		zkconn.sid = sid2;
		return zkconn;
	}

	public Object getShowString() {
		return "{ip=" + ip + ", paths=" + paths +"}";
	}
	
}
