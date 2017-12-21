package com.open.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkClient {
    private static final Logger logger = LoggerFactory
            .getLogger(ZkClient.class);
    private final String servers;
    private final int sessionTimeout;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile ZooKeeper zk;
    private final AtomicLong zkSessionId = new AtomicLong();
    private final List<SessionListener> sessionListeners = new CopyOnWriteArrayList<SessionListener>();

    public ZkClient(String servers, int sessionTimeout)
            throws InterruptedException, IOException {
        this.servers = servers;
        this.sessionTimeout = sessionTimeout;
        connect();
    }

    /**
     * Get native zookeeper client.
     * <p>
     * <b>NOTE</b>: zookeeper client instance hold by zkClient will be changed
     * against session expired.
     * 
     * @return native zookeeper client instance.
     */
    public ZooKeeper getHandler() {
        return zk;
    }

    public void registerSessionListener(SessionListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener is null.");
        }
        if (closed.get()) {
            throw new IllegalStateException(
                    "ZkClient was closed. Please use new instance.");
        }
        synchronized (sessionListeners) {
            sessionListeners.add(listener);
        }
    }

    public void unregisterSessionListener(SessionListener listener) {
        synchronized (sessionListeners) {
            sessionListeners.remove(listener);
        }
    }

    /**
     * Connect to zookeeper, block invoker until connected or connect timeout.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    private void connect() throws InterruptedException, IOException {
        final CountDownLatch connectionLatch = new CountDownLatch(1);
        final CountDownLatch assignLatch = new CountDownLatch(1);

        if (zk != null) {
            zk.close();
            zk = null;
        }
        zk = new ZooKeeper(servers,
                sessionTimeout,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (closed.get()) {
                            return;
                        }
                        try {
                            sessionEvent(assignLatch, connectionLatch,
                                    event);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (IOException e) {
                            throw new RuntimeException();
                        }

                    }
                });

        assignLatch.countDown();
        logger.info("Attempting to connect to zookeeper servers {}", servers);
        connectionLatch.await();
    }

    private void sessionEvent(final CountDownLatch assignLatch,
            final CountDownLatch connectionLatch,
            final WatchedEvent event) throws InterruptedException, IOException {
        logger.info("Zookeeper event: {}", event);
        assignLatch.await();
        switch (event.getState()) {
        case SyncConnected: {
            long newSessionId = zk.getSessionId();
            long oldSessionId = zkSessionId.getAndSet(newSessionId);
            /*
             * Reconnected after disconnected, sessionId is not changed.
             * sessionId will be changed if reconnected after session expired or
             * first connected.
             */
            if (oldSessionId != newSessionId) {
                logger.info("SyncConnected to Zookeeper.");
                for (SessionListener listener : sessionListeners) {
                    try {
                        listener.onSessionCreated(this);
                    } catch (Exception e) {
                        logger.error(
                                "Exception during zookeeper connection established callback",
                                e);
                    }
                }
            } else {
                logger.info("ReConnected to Zookeeper.");
            }
            connectionLatch.countDown();
            break;
        }
        case Expired: {
            // Session was expired; create a new zookeeper connection
            logger.info("Zookeeper session expired, recreating zkClient and connect...");
            connect();
            break;
        }
        default:
            // Disconnected -- zookeeper library will handle reconnects
            break;
        }

    }

    /**
     * 
     * Given a string representing a path, return each subpath.
     * <p>
     * subPaths("/a/b/c") == ["/a", "/a/b", "/a/b/c"]
     * 
     * @param path
     * @return
     */
    public static List<String> subPaths(final String path) {
        List<String> parts = Arrays.asList(path.split("/"));

        List<String> result = new ArrayList<String>(parts.size());
        for (int i = 1, size = parts.size(); i < size; i++) {
            StringBuilder b = new StringBuilder(path.length());
            for (int j = 1; j <= i; j++) {
                b.append("/").append(parts.get(j));
            }
            result.add(b.toString());
        }

        return result;
    }

    public List<String> getChildren(String path) throws KeeperException,
            InterruptedException {
        return zk.getChildren(path, false);
    }

    /**
     * Explicit close zookeeper connection. After shutdown, all watcher will not
     * be triggered, and this instance shouldn't be used.
     * 
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if (closed.compareAndSet(false, true)) {
            zk.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public boolean isAlive() throws KeeperException, InterruptedException {
        /*
         * If we can get the root, then we're alive. Do not watch root.
         */
        return zk.exists("/", false).getVersion() >= 0;
    }

    public String create(String path, byte[] data, CreateMode createMode)
            throws KeeperException, InterruptedException {
        return zk.create(path, data, Ids.OPEN_ACL_UNSAFE, createMode);
    }

    /**
     * ZooKeeper version of mkdir -p
     * 
     * @param path
     *            absolute path
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void createPath(String path) throws KeeperException,
            InterruptedException {
        for (String sub : subPaths(path)) {
            try {
                logger.debug("Creating path in createPath: {}", sub);
                zk.create(sub, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("Path created: {}", sub);
            } catch (KeeperException.NodeExistsException ignore) {

            }
        }
    }

    public byte[] get(String path) throws KeeperException, InterruptedException {
        return zk.getData(path, false, null);
    }

    public void set(String path, byte[] data) throws KeeperException,
            InterruptedException {
        zk.setData(path, data, -1);
    }

    public void delete(String path) throws InterruptedException,
            KeeperException {
        zk.delete(path, -1);
    }

    /**
     * Delete a node along with all of its children, like rm -r
     * 
     * @param path
     *            parent node absolute path
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void deleteRecursive(String path) throws InterruptedException,
            KeeperException {
        try {
            List<String> children = getChildren(path);
            for (String node : children) {
                deleteRecursive(path + "/" + node);
            }
            delete(path);
        } catch (KeeperException.NoNodeException ignore) {

        }
    }

    private void updateData(EventType eventType, String path,
            Watcher dataGetter,
            DataChangedListener listener) throws InterruptedException,
            KeeperException {
        try {
            listener.onDataChanged(eventType,
                    zk.getData(path, dataGetter, null));
        } catch (KeeperException e) {
            logger.warn("Failed to read path {}: {}", path, e);
            deleteData(eventType, path, dataGetter, listener);
        }
    }

    private void deleteData(EventType eventType, String path,
            Watcher dataGetter,
            DataChangedListener listener) throws KeeperException,
            InterruptedException {
        listener.onDataChanged(eventType, null);
        if (zk.exists(path, dataGetter) != null) {
            // Node was re-created by the time we called zk.exist
            updateData(eventType, path, dataGetter, listener);
        }
    }

    /**
     * Gets node's data, and watches the node when NodeDataChanged, NodeCreated.
     * When node deleted, track the node's re-creation with an existence watch.
     * <p>
     * <b>Note:</b>Normally, watch* method should be performed in
     * SessionListener, because watcher will be destroyed after session expired.
     * 
     * @param path
     *            node absolute path
     * @param listener
     *            watcher to get node's data.
     * @throws KeeperException
     * @throws InterruptedException
     * 
     * @see SessionListener#onSessionCreated(ZkClient)
     */
    public void watchNode(final String path, final DataChangedListener listener)
            throws InterruptedException, KeeperException {
        logger.debug("Watching path {}", path);

        Watcher dataGetter = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (closed.get()) {
                    return;
                }
                EventType eventType = event.getType();
                if (eventType == EventType.NodeDataChanged
                        || eventType == EventType.NodeCreated) {
                    try {
                        updateData(eventType, path, this, listener);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    }
                } else if (eventType == EventType.NodeDeleted) {
                    try {
                        deleteData(eventType, path, this, listener);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        };

        updateData(EventType.None, path, dataGetter, listener);
    }

    /**
     * Monitor a node's children list. Gets the children' name for a node,
     * watches for each NodeChildrenChanged event and fire and re-watches the
     * node's children.
     * 
     * <p>
     * <b>Note:</b>Normally, watch* method should be performed in
     * SessionListener, because watcher will be destroyed after session expired.
     * 
     * @param path
     *            parent node absolute path
     * @param listener
     *            watcher to get newly children list.
     * @throws InterruptedException
     * @throws KeeperException
     * 
     * @see SessionListener#onSessionCreated(ZkClient)
     */
    public void watchChildren(final String path,
            final ChildrenChangedListener listener)
            throws InterruptedException, KeeperException {

        Watcher childWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (closed.get()) {
                    return;
                }
                if (event.getType() == EventType.NodeChildrenChanged ||
                        event.getType() == EventType.NodeCreated ||
                        event.getType() == EventType.NodeDeleted) {
                    try {
                        watchChildren(path, listener);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        try {
            List<String> children = zk.getChildren(path, childWatcher);
            listener.onChildrenChanged(children);
        } catch (KeeperException e) {
            // Node was deleted -- fire a watch on node re-creation
            logger.warn("Failed to read path {}: {}", path, e);
            listener.onChildrenChanged(Collections.<String> emptyList());
            zk.exists(path, childWatcher);
        }
    }

    /**
     * Monitor a node's children and their data.
     * <p>
     * <b>WARNING</b>: watchMap must be thread-safe. Writing is synchronized on
     * the watchMap. Readers MUST also synchronize on the watchMap for safety.
     * <p>
     * <b>Note:</b>Normally, watch* method should be performed in
     * SessionListener, because watcher will be destroyed after session expired.
     * 
     * @param <T>
     *            type of children's node data
     * @param path
     *            parent node absolute path
     * @param watchMap
     *            view of children' data. key is child's node short name, value
     *            is child's node data
     * @param serializer
     *            serialize watchMap's value
     * @throws InterruptedException
     * @throws KeeperException
     * 
     * @see SessionListener#onSessionCreated(ZkClient)
     */
    public <T> void watchChildrenWithData(final String path,
            final Map<String, T> watchMap,
            final ZkSerializer<T> serializer)
            throws InterruptedException, KeeperException {
        watchChildrenWithData(path, watchMap, serializer, null);
    }

    /**
     * Get and monitor a node's children and their data, with an explicit
     * notifier.The notifier will be called whenever the watchMap is modified.
     * <p>
     * <b>WARNING</b>: watchMap must be thread-safe. Writing is synchronized on
     * the watchMap. Readers MUST also synchronize on the watchMap for safety.
     * <p>
     * <b>Note:</b>Normally, watch* method should be performed in
     * SessionListener, because watcher will be destroyed after session expired.
     * 
     * @param <T>
     *            type of children's node data
     * @param path
     *            parent node absolute path
     * @param watchMap
     *            view of children' data. key is child's node short name, value
     *            is child's node data
     * @param serializer
     *            serialize watchMap's value
     * @param notifier
     *            to watch which child node changed
     * @throws InterruptedException
     * @throws KeeperException
     * 
     * @see SessionListener#onSessionCreated(ZkClient)
     */
    public <T> void watchChildrenWithData(final String path,
            final Map<String, T> watchMap,
            final ZkSerializer<T> serializer,
            final ChildrenNodeChangedListener notifier)
            throws InterruptedException, KeeperException {

        ChildrenChangedListener parentWatcher = new ChildrenChangedListener() {

            @Override
            public void onChildrenChanged(final List<String> children) {
                Collection<String> removedChildren, addedChildren;
                synchronized (watchMap) {
                    removedChildren = subtract(watchMap.keySet(), children);
                    addedChildren = subtract(children, watchMap.keySet());
                    // remove deleted children from the watch map
                    for (String child : removedChildren) {
                        logger.debug("Path {}: child {} removed", path, child);
                        watchMap.remove(child);
                    }
                }
                // add new children to the watch map
                for (final String child : addedChildren) {
                    // node is added via nodeChanged callback
                    logger.debug("Path {}: child {} added", path, child);
                    try {
                        final String childPath = path + "/" + child;
                        watchNode(childPath,
                                new DataChangedListener() {

                                    @Override
                                    public void onDataChanged(
                                            EventType eventType, byte[] newData) {
                                        if (eventType == EventType.NodeDeleted) {
                                            // deletion handled via parent watch
                                            return;
                                        }
                                        T newValue;
                                        try {
                                            newValue = serializer
                                                    .deserialize(childPath,
                                                            newData);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                        synchronized (watchMap) {
                                            watchMap.put(child, newValue);
                                        }
                                        if (notifier != null) {
                                            notifier.onChildNodeChanged(
                                                    eventType, child);
                                        }
                                    }

                                });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (notifier != null) {
                    for (String child : removedChildren) {
                        notifier.onChildNodeChanged(EventType.NodeDeleted,
                                child);
                    }
                }
            }

        };

        watchChildren(path, parentWatcher);

    }

    private static <T> Collection<T> subtract(final Collection<T> a,
            final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        for (T o : new ArrayList<T>(b)) {
            result.remove(o);
        }
        return result;
    }
}
