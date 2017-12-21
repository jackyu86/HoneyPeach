package com.open.zk;

import java.util.List;

/**
 * Watch children list changes against give path.
 * 
 */
public interface ChildrenChangedListener {
    /**
     * fire when children list changed.
     * 
     * @param children
     *            new list of the monitored node's children, empty if no child.
     *            <b>NOTE: not absolute path but simple name of child.The list
     *            of children returned is not sorted and no guarantee is
     *            provided as to its natural or lexical order. </b>
     */
    void onChildrenChanged(List<String> children);
}
