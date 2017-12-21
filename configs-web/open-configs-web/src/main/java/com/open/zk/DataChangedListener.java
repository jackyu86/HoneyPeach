package com.open.zk;

import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * Get and watch node's data.
 * 
 */
public interface DataChangedListener {
    /**
     * <li>watcher was first attached: eventType is None, newData is node's
     * data. If no such node, newData is null.</li> <li>node was created:
     * eventType is NodeCreated, newData is node's data</li> <li>node data
     * changed: eventType is NodeDataChanged, newData is changed data. <li>node
     * was deleted: eventType is NodeDeleted, newData is always null</li>
     * 
     * @param eventType
     *            NodeDeleted, NodeCreated, NodeDataChanged, None
     * @param newData
     *            null when NodeDeleted or no such node when watcher attached.
     */
    void onDataChanged(EventType eventType, byte[] newData);

}
