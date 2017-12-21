package com.open.configs.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public class GateWayAuthAction extends BaseAction {

	private static final long serialVersionUID = 2432572613921675002L;

	public static final String JSON = "JSON";

	private Map<String, Object> root;

	private String result;

    public String adminIndex() {
        ValueStack context = ActionContext.getContext().getValueStack();
        context.set("readonly", GateWayConfigsAction.readOnly);

		return SUCCESS;
	}

    /**
     * addZkServer
     * http://xxx/auth/addZkServer.action?key=one&servers
     * =10.10.225.38:2181,10.10.225.38:2182
     * 
     * @throws Exception
     */
	public String addZkServer() throws Exception {
		root = new HashMap<String, Object>();
		boolean success = true;
		
		String key = getRequest().getParameter("key");
        String server = getRequest().getParameter("server");
		
		if (server.indexOf(":") < 0) {
			throw new RuntimeException("server error!!");
		}

        GateWayConfigsAction.zkServers.put(key, server);
        result = "add ";

		root.put("result", result);
		root.put("time", System.currentTimeMillis());
		root.put("success", success);
		return JSON;
	}
	
    public String deleteZkServer() throws Exception {
        root = new HashMap<String, Object>();
        boolean success = true;

        String key = getRequest().getParameter("key");

        GateWayConfigsAction.zkServers.remove(key);
        result = "delete";

        root.put("result", result);
        root.put("time", System.currentTimeMillis());
        root.put("success", success);
        return JSON;
    }

    public String listZkServers() throws Exception {
        root = new HashMap<String, Object>();
        boolean success = true;

        List<KeyedServer> servers = new ArrayList<KeyedServer>(GateWayConfigsAction.zkServers.size());
        for (Entry<String, String> entry : GateWayConfigsAction.zkServers.entrySet()) {
            servers.add(new KeyedServer(entry.getKey(), entry.getValue()));
        }

        root.put("zkServers", servers);

        root.put("time", System.currentTimeMillis());
        root.put("success", success);
        return JSON;
    }

    public String readonlySwitch() throws Exception {
        root = new HashMap<String, Object>();
        boolean success = true;

        GateWayConfigsAction.readOnly = !GateWayConfigsAction.readOnly;

        List<KeyedServer> servers = new ArrayList<KeyedServer>(GateWayConfigsAction.zkServers.size());
        for (Entry<String, String> entry : GateWayConfigsAction.zkServers.entrySet()) {
            servers.add(new KeyedServer(entry.getKey(), entry.getValue()));
        }

        root.put("readonly", GateWayConfigsAction.readOnly);
        root.put("time", System.currentTimeMillis());
        root.put("success", success);
        return JSON;
    }

    public static class KeyedServer {
        private String key;
        private String value;

        KeyedServer(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

	public Map<String, Object> getRoot() {
		return root;
	}

	public void setRoot(Map<String, Object> root) {
		this.root = root;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}