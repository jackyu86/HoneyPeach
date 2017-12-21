package com.open.zk;

import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * Watch children changes against give path.
 * 
 * @see DataChangedListener
 */
public interface ChildrenNodeChangedListener {
    /**
     * fire when child node changed: new added, removed, data changed,
     * watched(EvenType=None)
     * 
     * @param eventType
     *            NodeDeleted, NodeCreated, NodeDataChanged, None
     * @param simpleName
     *            changed child node's name. <b>NOTE: not absolute path but
     *            simple name</b>
     * @see DataChangedListener#onDataChanged(EventType, byte[])
     */
    void onChildNodeChanged(EventType eventType,
            String simpleName);
}
