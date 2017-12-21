package com.open.zk;

import org.apache.zookeeper.KeeperException;

/**
 * Watch session created event when zkClient got session (connected to server),
 * or got new session (reconnect after session expired) .
 * 
 */
public interface SessionListener {
    /**
     * fire on session created or recreated after session expired.
     * <p>
     * <b>NOTE</b>: all EPHEMERAL nodes, all watchers on
     * exists/getData/getChildren should be recreate after session recreated.
     * 
     * @param client
     *            instance of ZkClient. If you don't want to use zkClient, you
     *            can use native zookeeper client instance by
     *            zkClient.getHandler()
     * 
     * @throws KeeperException
     * @throws InterruptedException
     */
    void onSessionCreated(ZkClient client) throws KeeperException,
            InterruptedException;
}
