package com.open.zk;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

public class CompareZk {
    private final ZkClient zkClient1;
    private final ZkClient zkClient2;

    public CompareZk(String zk1, String zk2) throws InterruptedException, IOException {
        zkClient1 = new ZkClient(zk1, 30000);
        zkClient2 = new ZkClient(zk2, 30000);
    }

    private void walk(ZkClient client, String parent, Map<String, Integer> container) throws KeeperException, InterruptedException {
        List<String> children = client.getChildren(parent);
        Collections.sort(children);
        for (String child : children) {

            String fullPath = parent + (parent.endsWith("/") ? "" : "/") + child;
            Stat stat = client.getHandler().exists(fullPath, false);
            if (stat == null || stat.getEphemeralOwner() > 0) {
                continue;
            }

            container.put(fullPath, stat.getDataLength());
            if (stat.getNumChildren() > 0) {
                walk(client, fullPath, container);
            }
        }

    }

    public void start() throws KeeperException, InterruptedException {
        Map<String, Integer> container1 = new LinkedHashMap<String, Integer>();
        Map<String, Integer> container2 = new LinkedHashMap<String, Integer>();
        walk(zkClient1, "/", container1);
        walk(zkClient2, "/", container2);

        for (Entry<String, Integer> entry1 : container1.entrySet()) {
            if (!container2.containsKey(entry1.getKey())) {
                System.out.println("In left but not in right key:" + entry1.getKey());
            } else if (!entry1.getValue().equals(container2.get(entry1.getKey()))) {
                System.out.println("Left length not equals right,key:" + entry1.getKey() + "," + entry1.getValue() + "," + container2.get(entry1.getKey()));
            }
        }

        for (Entry<String, Integer> entry2 : container2.entrySet()) {
            if (!container1.containsKey(entry2.getKey())) {
                System.out.println("In right but not in left key:" + entry2.getKey());
            } else if (!entry2.getValue().equals(container1.get(entry2.getKey()))) {
                System.out.println("Right length not equals left,key:" + entry2.getKey() + "," + entry2.getValue() + "," + container1.get(entry2.getKey()));
            }
        }

    }

    public void stop() throws InterruptedException {
        zkClient1.close();
        zkClient2.close();
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        CompareZk comp = new CompareZk(args[0], args[1]);
        comp.start();
        comp.stop();
    }
}
