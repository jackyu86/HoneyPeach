package com.open.configs.domain;

import java.util.HashSet;
import java.util.Set;

public class ZkPathWatch {
	
	public String path;
	public Set<String> watches = new HashSet<String>();
	public Set<String> watchIps = new HashSet<String>();
	
	
	public Set<String> addWatchSid(String sid){
		watches.add(sid);
		return watches;
	}
	
	public Set<String> addWatchIp(String ip){
		watchIps.add(ip);
		return watchIps;
	}
	
	public Set<String> getWatchIps() {
		return watchIps;
	}




	public void setWatchIps(Set<String> watchIps) {
		this.watchIps = watchIps;
	}




	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Set<String> getWatches() {
		return watches;
	}
	public void setWatches(Set<String> watches) {
		this.watches = watches;
	}

	@Override
	public String toString() {
		return "ZkPathWatch [path=" + path + ", watchIps=" + watchIps + ", watches=" + watches + "]";
	}

	public Object getShowString() {
		StringBuffer sb = new StringBuffer();
		sb.append("path:" + path).append("\r\n");
		sb.append("count:" + watchIps.size()).append("\r\n");
		sb.append("client:" + watchIps).append("\r\n");
		return sb.toString();
	}
	
	
}
