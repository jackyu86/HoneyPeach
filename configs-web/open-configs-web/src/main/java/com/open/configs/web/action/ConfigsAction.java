package com.open.configs.web.action;

import com.open.configs.core.ClientFactory;
import com.open.configs.domain.AdminConfig;
import com.open.configs.domain.ConfigOther;
import com.open.configs.domain.SimpleConfigDomain;
import com.open.configs.domain.ZkServerGroupStat;
import com.open.configs.util.GZipUtils;
import com.open.configs.util.JsonUtils;
import com.open.configs.util.LocalConfigUtils;
import com.open.configs.web.util.ZkTelentUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;

import java.util.*;

public class ConfigsAction extends BaseAction {

	private static final long serialVersionUID = 2432572613921675002L;

	public static final String JSON = "JSON";

	private Map<String, Object> root;
	private String path;

	private String result;

	public String securityIndex() {
        ValueStack context = ActionContext.getContext().getValueStack();
        context.set("readonly", ConfigsAction.readOnly);

		return SUCCESS;
	}

	/**
	 * 获取path内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getPathContent() throws Exception {
		root = new HashMap<String, Object>();
		boolean success = true;
		String msg = null;
        com.open.configs.core.OpenZookeeperClient zk = getJdZookeeperClient(getRequest().getParameter("zkConfigs"));
		if(!zk.exists(path)){
			success = false;
			msg = "路径不存在";
			root.put("time", System.currentTimeMillis());
			root.put("success", success);
			root.put("msg", msg);
			return JSON;
		}
		
		try{
			byte[] data = zk.readData(path);
			try{
				data = GZipUtils.decompress(data);
			}catch (Exception e) {
				msg = "gzip出错!!!";
			}
			root.put("result", new String(data, "utf-8"));
		}catch (Exception e) {
			msg = "获取出错!!!";
			success = false;
		}
		
		root.put("time", System.currentTimeMillis());
		root.put("success", success);
		root.put("msg", msg);
		return JSON;
	}

	/**
	 * 保存修改内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public String save() throws Exception {
		
		root = new HashMap<String, Object>();
		boolean success = true;
        String msg = null;
		
		String json = getRequest().getParameter("json");
		
		if(StringUtils.isBlank(result)){
			success = false;
			msg = "内容不能为空";
		}
        if ("json".equals(json)) {
            try {
				JsonUtils.readValue(result, HashMap.class);
            } catch (Exception e1) {
                success = false;
                msg = "内容json格式错误!!!";
            }
        }
        List<String> messages = new ArrayList<String>();
        if (success) {
            // for (String zkServerKey :
            // Arrays.asList(request.getParameter("zkConfigs"), "backup")) {
            for (String zkServerKey : Arrays.asList(getRequest().getParameter("zkConfigs"))) {
                try {
                    saveImpl(zkServerKey, path, result, getRequest().getParameter("unCompress"), json);
                    messages.add(zkServerKey + " : ok.");
                } catch (Exception e) {
                    success = false;
                    messages.add(zkServerKey + " : " + e.getMessage());
                }
            }
        } else {
            messages.add(msg);
        }

        root.put("messages", join(messages, "<br/>"));
		root.put("time", System.currentTimeMillis());
		root.put("success", success);
		return JSON;
	}

    private void saveImpl(String serverKey, String path, String content, String unCompress, String json) throws Exception {
        if (readOnly) {
            throw new RuntimeException("当前处于只读模式");
        }
        com.open.configs.core.OpenZookeeperClient zk = getJdZookeeperClient(serverKey);

        if (!zk.exists(path)) {
            throw new RuntimeException("路径不存在");
        } else {
            byte[] data = content.getBytes("utf-8");
            if ("true".equals(unCompress)) {

            } else {
                data = GZipUtils.compress(data);
            }

            byte[] oldData = zk.readData(path, true);
            String pathBak = path + "/bak";
            if (!zk.exists(pathBak)) {
                zk.recursiveSafeCreate(pathBak, oldData, CreateMode.PERSISTENT);
            } else {
                if (!ArrayUtils.isEquals(oldData, data)) {
                    // 备份
                    zk.writeData(pathBak, oldData);
                    String versionPath = "/back" + path + "." + System.currentTimeMillis();
                    LocalConfigUtils.writeConfig2Local(versionPath, data, ClientFactory.getTypeConfigService().getZookeeperConfig());

                    // 增加最后保存时间
                    content = addLastSaveTime(content);

                    if ("json".equals(json)) {
                        JsonUtils.readValue(content, HashMap.class);
                    }

                    data = content.getBytes("utf-8");
                    if ("true".equals(unCompress)) {

                    } else {
                        data = GZipUtils.compress(data);
                    }

                }
            }

            zk.writeData(path, data);
        }

    }

    private static String addLastSaveTime(String result) {
		String lastSaveTimeKey = "\"lastSaveTime\""; 
		String doubleQuotes = "\"";
		int lastSaveIndex = result.indexOf(lastSaveTimeKey);
		if(lastSaveIndex < 0){
			int addIndex = result.indexOf("\"configName\"");
			
			if(addIndex > 0){
				result = result.substring(0, addIndex) + lastSaveTimeKey +" : " + JsonUtils.writeValue(new Date())
				+ ",\r\n    " + result.substring(addIndex, result.length());
			}
		}else{
			StringBuilder dd = new StringBuilder(result.substring(0, lastSaveIndex));
			dd.append(lastSaveTimeKey);
			int ss = result.indexOf(doubleQuotes, lastSaveIndex  + lastSaveTimeKey.length());
			int end = result.indexOf(doubleQuotes, ss+doubleQuotes.length());
			if(ss < 0 || end < 0){
                return result;
			}
			dd.append(result.substring(lastSaveIndex + lastSaveTimeKey.length(), ss));
			dd.append(JsonUtils.writeValue(new Date()));
			dd.append(result.substring(end+doubleQuotes.length()));
			result = dd.toString();
		}
        return result;
	}
	
	
	/**
	 * 获取path内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public String create() throws Exception {
		root = new HashMap<String, Object>();
		boolean success = true;
        List<String> messages = new ArrayList<String>();
        // for (String zkServerKey :
        // Arrays.asList(request.getParameter("zkConfigs"), "backup")) {
        for (String zkServerKey : Arrays.asList(getRequest().getParameter("zkConfigs"))) {
    		try{
                createImpl(zkServerKey, path);
                messages.add(zkServerKey + " : ok.");
            } catch (Exception e) {
                success = false;
                messages.add(zkServerKey + " : " + e.getMessage());
    		}
	    }
        root.put("messages", join(messages, "<br/>"));
		root.put("time", System.currentTimeMillis());
		root.put("success", success);
		return JSON;
	}
	
    private static String join(List<String> messages, String separator) {
        StringBuilder buffer = new StringBuilder();
        for (String msg : messages) {
            buffer.append(msg).append(separator);
        }
        return buffer.toString();
    }

    private void createImpl(String serverKey, String path) throws Exception {
        if (readOnly) {
            throw new RuntimeException("当前处于只读模式");
        }
        com.open.configs.core.OpenZookeeperClient zk = getJdZookeeperClient(serverKey);

        if (zk.exists(path)) {
            throw new RuntimeException("路径已经存在!!!");
        } else {
            SimpleConfigDomain d = new SimpleConfigDomain();
            ConfigOther configOther = new ConfigOther();
            configOther.setConfigName("shguo");
            configOther.setYn(1);
            configOther.setLastSaveTime(new Date());
            d.setZother(configOther);
            String configs = JsonUtils.writeValue(d, true);
            byte[] configData = configs.getBytes("utf-8");
            configData = GZipUtils.compress(configData);
            zk.recursiveSafeCreate(path, configData, CreateMode.PERSISTENT);
        }

    }

	/**
	 * 获取path内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getChildren() throws Exception {
		root = new HashMap<String, Object>();
		boolean success = true;
        com.open.configs.core.OpenZookeeperClient zk = getJdZookeeperClient(getRequest().getParameter("zkConfigs"));
		
		String msg = null;
		if(!zk.exists(path)){
			success = false;
			msg = "路径不存在";
			root.put("time", System.currentTimeMillis());
			root.put("success", success);
			root.put("msg", msg);
			return JSON;
		}
		
		List<String> result = null;
		try{
			result = zk.getChildren(path);
			Collections.sort(result);
		}catch (Exception e) {
			success = false;
			root.put("msg", "获取出错!!!");
		}
		
		root.put("result", result);
		root.put("time", System.currentTimeMillis());
		root.put("success", success);
		return JSON;
	}
	
	
	/**
	 * 获取path内容
	 * 
	 * @return
	 * @throws Exception
	 */
	public String list() throws Exception {
		root = new HashMap<String, Object>();
		boolean success = true;

        List<String> result = new ArrayList<String>();
        // for (String zkServerKey :
        // Arrays.asList(request.getParameter("zkConfigs"), "backup")) {
        for (String zkServerKey : Arrays.asList(getRequest().getParameter("zkConfigs"))) {
            try {
                List<String> acks = listImpl(zkServerKey, path + "/ack");
                for (String ack : acks) {
                    result.add(zkServerKey + ">>" + ack);
                }
            } catch (Exception e) {
                success = false;
                result.add(zkServerKey + " : " + e.getMessage());
            }
        }

		root.put("result", result);
		root.put("time", System.currentTimeMillis());
		root.put("success", success);
		return JSON;
	}
	
    private List<String> listImpl(String serverKey, String path) throws Exception {
        com.open.configs.core.OpenZookeeperClient zk = getJdZookeeperClient(serverKey);

        if (!zk.exists(path)) {
            throw new RuntimeException("路径不存在!!!");
        } else {
            List<String> result = zk.getChildren(path);
            Collections.sort(result);
            return result;
        }

    }
	
    static Map<String, String> zkServers = new HashMap<String, String>();

    static {
        try {
            zkServers.put("default", ClientFactory.getConfig().getZkConfigs());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static boolean readOnly;
	
	/**
	 * zkServerStat
	 * http://zkconfigs.360buy.com/client/zkServerStat.action?servers=10.10.225.38:2181,10.10.225.38:2182
	 * @throws Exception
	 */
	public String zkServerStat() throws Exception {
		//Content-Type	text/html;charset=GBK
		getResponse().setContentType("text/html;charset=UTF-8");
		String servers = getRequest().getParameter("servers");
		String group = getRequest().getParameter("group");
		
		if(StringUtils.isNotEmpty(group)){
			servers = AdminConfig.getInstance().getZkServerGroups().get(group);
		}
		
		if(StringUtils.isEmpty(servers)){
			throw new RuntimeException("server error!!");
		}
		
		if(servers.indexOf(":") < 0){
			throw new RuntimeException("server error!!");
		}

		ZkServerGroupStat groupStat =  ZkTelentUtils.gropStat(servers);
		getResponse().getWriter().write(groupStat.getShowString().replaceAll("\r", "<br/>"));
		getResponse().getWriter().flush();
		return null;
	}
	
	
    public com.open.configs.core.OpenZookeeperClient getJdZookeeperClient(String zkServerKey) throws Exception {
		com.open.configs.core.OpenZookeeperClient zk = null;
        if (StringUtils.isNotEmpty(zkServerKey)) {
            String servers = zkServers.get(zkServerKey);
			if(StringUtils.isNotBlank(servers)){
				zk = ClientFactory.getZookeeperClient(servers, 15000);
			}else{
                // if (zkServerKey.indexOf(":") < 0) {
                // throw new RuntimeException("server error!!");
                // }
                zk = ClientFactory.getZookeeperClient(zkServerKey, 15000);
			}
        }
        if (zk == null) {
            throw new RuntimeException("No such zkserver:" + zkServerKey);
        }
		
		return zk;
	}
	public Map<String, Object> getRoot() {
		return root;
	}

	public void setRoot(Map<String, Object> root) {
		this.root = root;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}