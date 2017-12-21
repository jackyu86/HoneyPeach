package com.open.configs.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.open.configs.domain.ZookeeperConfig;
import com.open.configs.exception.ZkException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * gateway接口auth客户端
 */
public class GateWayConfigClient implements Watcher {
    private final static Logger LOG = LoggerFactory.getLogger(GateWayConfigClient.class);

    /**
     * data listener
     */
    private final ConcurrentHashMap<String, Set<PathListener>> registerDataListener = new ConcurrentHashMap<String, Set<PathListener>>();
    /**
     * 断开后重连要重新取
     */
    private final static ConcurrentHashMap<String, Boolean> regets = new ConcurrentHashMap<String, Boolean>();

    /**
     * zookeeper instance
     */
    private ZooKeeper zk = null;

    String zkServers;
    int sessionTimeout;
    //public static volatile boolean sessionExpired = false;

    public GateWayConfigClient(String zkServers, int sessionTimeout) throws IOException {
        this.zkServers = zkServers;
        this.sessionTimeout = sessionTimeout;
        zk = new ZooKeeper(zkServers, sessionTimeout, this);
        renewAfter();

        startCheckThread();
    }

    /**
     *
     */
    private void startCheckThread() {
        Thread t = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10 * 60 * 1000);
                    } catch (InterruptedException e) {
                    }

                    try {
                        LOG.info("check zk state....." + zk.getState());
                        if (zk == null || !zk.getState().isAlive()) {
                            LOG.info("check zk state..... expired, renew one..");
                            expired();
                        }
                    } catch (Exception e) {

                    }
                }
            }

        };

        t.setName("CheckThread");
        t.setDaemon(true);
        t.start();

    }

    public GateWayConfigClient(ZookeeperConfig config) throws IOException {
        this(config.getZkConfigs(), config.getZkCheckInterval());
    }


    /**
     * 注册内容监听
     *
     * @param path
     * @param listener
     */
    public void subscribeDataChanges(String path, PathListener listener) {
        Set<PathListener> listeners;
        listeners = registerDataListener.get(path);
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<PathListener>();
            registerDataListener.put(path, listeners);
        }
        listeners.add(listener);
        watchForData(path);
        LOG.error("Subscribed data changes for " + path);
    }

    /**
     * 取消内容监听
     *
     * @param path
     * @param dataListener
     */
    public void unsubscribeDataChanges(String path, PathListener dataListener) {
        final Set<PathListener> listeners = registerDataListener.get(path);
        if (listeners != null) {
            listeners.remove(dataListener);
        }
        if (listeners == null || listeners.isEmpty()) {
            registerDataListener.remove(path);
        }
    }

    /**
     * 创建节点
     *
     * @param path
     * @param data
     * @param mode
     * @return
     * @throws ZkException
     */
    public String create(final String path, byte[] data, final CreateMode mode) throws ZkException {
        if (path == null) {
            throw new NullPointerException("path must not be null.");
        }
        final byte[] bytes = data;

        try {
            return getZk().create(path, bytes, Ids.OPEN_ACL_UNSAFE, mode);
        } catch (KeeperException e) {
            throw new ZkException(e);
        } catch (InterruptedException e) {
            throw new ZkException(e);
        }
    }

    public String recursiveSafeCreate(String node, byte[] data, CreateMode createMode) throws KeeperException, InterruptedException {
        if (node == null || node.length() < 0)
            return node;
        else if ("/".equals(node))
            return node;
        else {
            int index = node.lastIndexOf("/");
            if (index == -1)
                return node;
            String parent = node.substring(0, index);

            recursiveSafeCreate(parent, data, createMode);
            try {
                return create(node, data, createMode);
            } catch (ZkException e) {
                if (e.getCode() != Code.NODEEXISTS) {//print zk other exception
                    LOG.info(e.getCode().toString(), e);
                    throw e;
                }
                return node;
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return node;
            }
        }
    }


    /**
     * 处理节点事件
     */
    public void process(WatchedEvent event) {
        String path = event.getPath();
        boolean dataChanged = event.getType() == EventType.NodeDataChanged
                || event.getType() == EventType.NodeDeleted
                || event.getType() == EventType.NodeCreated;

        LOG.debug("Process: " + event.getType() + "===>" + event.getState() + "====>" + dataChanged);

        if (dataChanged) {
            processDataChanged(event);
        }

        //　重连时得重新注册监听
        if (path == null && event.getState() == KeeperState.SyncConnected) {
            subscribeAll();
        }

        if (event.getState() == KeeperState.Expired) {
            expired();
        }
    }


    public void expired() {
        LOG.error("[[session expired]] try reconnect....");

        synchronized (this) {
            try {
                if (zk != null) {
                    try {
                        zk.close();
                    } catch (Exception e) {
                        // ingore
                    }
                }
                zk = null;

                zk = new ZooKeeper(zkServers, sessionTimeout, this);
                renewAfter();

            } catch (IOException e) {
                LOG.error("reconnect error!", e);
            }
        }
    }

    /**
     * 重新注册watch
     */
    private synchronized void subscribeAll() {
        LOG.error("===================subscribeAll========================");
        for (String path : registerDataListener.keySet()) {
            watchForData(path);
        }
        renewAfter();
    }

    /**
     * 获取子节点
     *
     * @param path 　节点路径
     * @return
     */
    public List<String> getChildren(String path) {
        return getChildren(path, hasListeners(path));
    }

    /**
     * 获取子节点
     *
     * @param path
     * @param watch
     * @return
     */
    protected List<String> getChildren(final String path, final boolean watch) {
        try {
            return getZk().getChildren(path, watch);
        } catch (KeeperException e) {
            throw new ZkException(e);
        } catch (InterruptedException e) {
            throw new ZkException(e);
        }
    }

    /**
     * 获取子节点长度
     *
     * @param path
     * @return
     */
    public int countChildren(String path) {
        try {
            return getChildren(path).size();
        } catch (ZkException e) {
            return 0;
        }
    }

    /**
     * 判断节点为是否存在
     *
     * @param path
     * @param watch
     * @return
     */
    public boolean exists(final String path, final boolean watch) {
        try {
            Stat stat = getZk().exists(path, watch);
            return stat != null ? true : false;
        } catch (KeeperException e) {
            LOG.error("path==>{} error!", new Object[]{path}, e);
            return false;
        } catch (InterruptedException e) {
            LOG.error("path==>{} error!", new Object[]{path}, e);
            return false;
        }
    }

    /**
     * 判断节点为是否存在
     *
     * @param path
     * @return
     */
    public boolean exists(final String path) {
        return exists(path, hasListeners(path));
    }


    /**
     * 判断节点上是否有监听
     *
     * @param path
     * @return
     */
    private boolean hasListeners(String path) {
        Set<PathListener> dataListeners = registerDataListener.get(path);
        if (dataListeners != null && dataListeners.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 删除节点节点(包含子节点)
     *
     * @param path
     * @return
     */
    public boolean deleteRecursive(String path) {
        List<String> children;
        try {
            children = getChildren(path, false);
        } catch (ZkException e) {
            return true;
        }

        for (String subPath : children) {
            if (!deleteRecursive(path + "/" + subPath)) {
                return false;
            }
        }

        return delete(path);
    }

    private void processDataChanged(WatchedEvent event) {
        final String path = event.getPath();

        if (event.getType() == EventType.NodeDataChanged || event.getType() == EventType.NodeDeleted || event.getType() == EventType.NodeCreated) {
            Set<PathListener> listeners = registerDataListener.get(path);
            if (listeners != null && !listeners.isEmpty()) {
                fireDataChangedEvents(event.getPath(), listeners);
            }
        }
    }

    /***
     * 数据格式
     * [{"appId":"","methods":""}]
     */
    private void fireDataChangedEvents(final String path, Set<PathListener> listeners) {
        for (final PathListener listener : listeners) {
            // reinstall watch
            exists(path, true);
            for (int i = 0; i < 3; i++) {
                try {
                    //sync data from leader
                    getZk().sync(path, null, null);
                    Map<String, String> data = readData(path, null, true);
                    listener.ZkDataListener(data);
                    break;
                } catch (ZkException e) {
                    LOG.error("fireDataChangedEvents error!!", e);
                    //触发异常监听
                    listener.exceptionCaught(e);
                }
            }
        }
    }


    protected Set<PathListener> getDataListener(String path) {
        return registerDataListener.get(path);
    }


    /**
     * 删除节点
     *
     * @param path
     * @return
     */
    public boolean delete(final String path) {
        try {
            getZk().delete(path, -1);
            return true;
        } catch (ZkException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        } catch (KeeperException e) {
            return false;
        }
    }


    /**
     * 取节点数据
     *
     * @param path
     * @return
     */
    public Map<String,String> readData(String path) {
        return readData(path, false);
    }

    public byte[] readByteData(String path) {
        return readByteData(path, false);
    }
    /**
     * 取节点数据
     *
     * @param path
     * @param returnNullIfPathNotExists
     * @return
     */
    public Map<String,String> readData(String path, boolean returnNullIfPathNotExists) {
        Map<String,String>  data = null;
        try {
            data = readData(path, null);
        } catch (ZkException e) {
            if (!returnNullIfPathNotExists) {
                throw e;
            }
        }
        return data;
    }

    public byte[] readByteData(String path, boolean returnNullIfPathNotExists) {
        byte[] data = null;
        try {
            data = readByteData(path, null);
        } catch (ZkException e) {
            if (!returnNullIfPathNotExists) {
                throw e;
            }
        }
        return data;
    }
    public byte[] readByteData(String path, Stat stat) {
        return readByteData(path, stat, hasListeners(path));
    }

    public  byte[] readByteData(final String path, final Stat stat, final boolean watch) {
        byte[] data = null;
        data = getDataBytes(path, stat, watch);
        return data;
    }

    private byte[] getDataBytes(String path, Stat stat, boolean watch) {
        byte[] data;
        try {
            data = getZk().getData(path, watch , stat);
        } catch (KeeperException e) {
            LOG.error("read data error!!!", e);
            throw new ZkException(e);
        } catch (InterruptedException e) {
            throw new ZkException(e);
        }
        return data;
    }

    /**
     * 取节点数据
     *
     * @param path
     * @param stat
     * @return
     */
    public Map<String,String> readData(String path, Stat stat) {
        return readData(path, stat, hasListeners(path));
    }

    public Map<String, String> readData(final String path, final Stat stat, final boolean watch) {
        Map<String, String> mapdata = null;
        try {
            byte[] data = getZk().getData(path, watch, stat);
            ObjectMapper mapper = new ObjectMapper();
            if(data!=null&&data.length>0) {
                String json = mapper.writeValueAsString(data);
                mapdata = mapper.convertValue(json, HashMap.class);
            }
        } catch (KeeperException e) {
            LOG.error("read data error!!!", e);
            throw new ZkException(e);
        } catch (InterruptedException e) {
            LOG.error("parse json error!!!", e);
            throw new ZkException(e);
        } catch (JsonGenerationException e) {
            LOG.error("parse json error jackson!!!", e);
            throw new ZkException(e);
        } catch (JsonMappingException e) {
            LOG.error("parse json error jackson mapping!!!", e);
            throw new ZkException(e);
        } catch (IOException e) {
            LOG.error("parse json error io", e);
            throw new ZkException(e);
        }
        return mapdata;
    }

    /**
     * 写节点数据
     *
     * @param path
     * @param data
     */
    public void writeData(String path, byte[] data) {
        writeData(path, data, -1);
    }

    /**
     * 写节点数据
     *
     * @param path
     * @param data
     * @param version
     */
    public void writeData(final String path, byte[] data, final int version) {
        try {
            getZk().setData(path, data, version);
        } catch (KeeperException e) {
            throw new ZkException(e);
        } catch (InterruptedException e) {
            throw new ZkException(e);
        }
    }

    /**
     * 监控节点
     *
     * @param path
     */
    public void watchForData(final String path) {
        try {
            getZk().exists(path, true);
        } catch (KeeperException e) {
            //throw new ZkException(e);
        } catch (InterruptedException e) {
            //throw new ZkException(e);
        }
    }

    public ZooKeeper getZk() {
        /*
		
		if(zk != null && zk.getState().isAlive() && !JdZookeeperClient.sessionExpired){
    		return zk;
    	}
    	
    	synchronized (this) {
			try {
				if(zk != null){
					zk.close();
					zk = null;
				}
				zk = new ZooKeeper(zkServers, sessionTimeout, this);
				renewAfter();
			} catch (IOException e) {
				LOG.error("reconnect error!", e);
			} catch (InterruptedException e) {
				LOG.error("reconnect error!", e);
			}
		} */
        return zk;
    }

    private synchronized void renewAfter() {
        //JdZookeeperClient.sessionExpired = false;

        LOG.error("===================reget zookeeper values========================");
        for (String path : registerDataListener.keySet()) {
            LOG.error("===================" + path);
            changeReget(path, true);
        }

    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    //
    public static void changeReget(String path, boolean v) {
        regets.put(path, v);
    }

    public static boolean pathShouldReget(String path) {
        Boolean b = regets.get(path);
        if (b != null && b) {
            return true;
        }

        return false;
    }

}
