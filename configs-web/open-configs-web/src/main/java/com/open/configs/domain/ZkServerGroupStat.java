package com.open.configs.domain;

import java.util.HashMap;
import java.util.Map;

public class ZkServerGroupStat {
	public String name;
	public String servers;
	
	public Map<String, Map<String, Zkconn>> zkconns = new HashMap<String, Map<String,Zkconn>>();
	
	public Map<String, Map<String,ZkPathWatch>> zkWatches =  new HashMap<String, Map<String,ZkPathWatch>>();
	
	
	public Map<String,Zkconn> cons = new HashMap<String,Zkconn>();
	
	public Map<String,ZkPathWatch> watches = new HashMap<String,ZkPathWatch>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public Map<String, Map<String, Zkconn>> getZkconns() {
		return zkconns;
	}

	public void setZkconns(Map<String, Map<String, Zkconn>> zkconns) {
		this.zkconns = zkconns;
	}

	public Map<String, Map<String, ZkPathWatch>> getZkWatches() {
		return zkWatches;
	}

	public void setZkWatches(Map<String, Map<String, ZkPathWatch>> zkWatches) {
		this.zkWatches = zkWatches;
	}

	public void gropStat() {
		for(String key: zkconns.keySet()){
			Map<String, Zkconn> oneServerConns = zkconns.get(key);
			cons.putAll(oneServerConns);
		}
		
		
		for(String key: zkWatches.keySet()){
			Map<String, ZkPathWatch> oneServer = zkWatches.get(key);
			
			for(String path: oneServer.keySet()){
				ZkPathWatch serverWatch = oneServer.get(path);
				
				ZkPathWatch  groupWatch = watches.get(path);
				if(groupWatch == null){
					groupWatch = new ZkPathWatch();
					watches.put(path, groupWatch);
				}
				
				groupWatch.path = path;
				for(String sid: serverWatch.watches){
					groupWatch.addWatchSid(sid);
					
					Zkconn conn = cons.get(sid);
					if(conn != null){
						groupWatch.addWatchIp(conn.ip);
						serverWatch.addWatchIp(conn.ip);
						
						conn.addPath(path);
					}
				}
				
			}
			
		}
		
	}
	
	
	public String getShowString(){
		StringBuffer sb = new StringBuffer();
		sb.append("servers:").append(servers).append("\r\n");
		sb.append("conn-count:").append(cons.size()).append("\r\n");
		for(String key: zkconns.keySet()){
			Map<String, Zkconn> oneServerConns = zkconns.get(key);
			sb.append(key).append("===>").append(oneServerConns.size()).append("\r\n");
		}
		sb.append("path-count:").append(watches.size()).append("##path may be in other nodes\r\n");
		for(String key: zkWatches.keySet()){
			Map<String, ZkPathWatch> oneServer = zkWatches.get(key);
			sb.append(key).append("===>").append(oneServer.size()).append("\r\n");
		}
		
		sb.append("==============================conns==========================").append("\r\n");
		for(Zkconn conn: cons.values()){
			sb.append(conn.getShowString()).append("\r\n");
		}
		
		sb.append("==============================paths==========================").append("\r\n");
		for(ZkPathWatch watch: watches.values()){
			sb.append(watch.getShowString()).append("\r\n");
		}
		
		return sb.toString();
	}
	
}
