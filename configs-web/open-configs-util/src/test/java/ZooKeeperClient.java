import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;


public class ZooKeeperClient {

	/** 
     * server列表, 以逗号分割 
     */  
    protected static String hosts = "192.168.153.150:2181,192.168.153.140:2181,192.168.153.142:2181";  
	/** 
     * 连接的超时时间, 毫秒 
     */  
	private static final int SESSION_TIMEOUT = 5000;  
    private CountDownLatch connectedSignal = new CountDownLatch(1);  
    private static ZooKeeper zk = null;  
    private ZooKeeperClient(){}
    public ZooKeeper getInstance() throws Exception {
    	if (zk == null) {
    		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnWatcher());
    		// 等待连接完成  
            connectedSignal.await();
    	}
    	
    	return zk;
    }
    
    public class ConnWatcher implements Watcher {  
        public void process(WatchedEvent event) {  
            // 连接建立, 回调process接口时, 其event.getState()为KeeperState.SyncConnected  
            if (event.getState() == KeeperState.SyncConnected) {  
                // 放开闸门, wait在connect方法上的线程将被唤醒  
                connectedSignal.countDown();  
            }  
        }  
    }  
}
