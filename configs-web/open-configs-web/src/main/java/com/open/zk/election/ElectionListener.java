package com.open.zk.election;

import java.util.List;

/**
 * Get and watch election event: member/leader/me changed.
 * 
 */
public interface ElectionListener {

    /**
     * Fire when leader or member list was changed. Please compare leaderName
     * and myName to estimate if i am a leader.
     * 
     * @param leaderName
     * @param myName
     * @param memberNames
     */
    void onElectionChanged(String leaderName, String myName,
            List<String> memberNames);

}
