package com.open.zk.election;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import com.open.zk.ChildrenChangedListener;
import com.open.zk.SessionListener;
import com.open.zk.ZkClient;

/**
 * Dynamic leader election based on zookeeper's EPHEMERAL_SEQUENTIAL node.
 * 
 */
public class LeaderElection {
	private final String electionPath;
	private final ElectionListener listener;
	private final AtomicReference<String> myName = new AtomicReference<String>();

	/**
	 * 
	 * @param client
	 *            ZkClient instance
	 * @param electionPath
	 *            node used for election. please make sure that node exists and
	 *            only for election.
	 * @param listener
	 *            watch leader or member list changed. can't be null.
	 * @throws IllegalArgumentException
	 *             listener is null
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public LeaderElection(final ZkClient client, final String electionPath,
			final ElectionListener listener) throws KeeperException,
			InterruptedException {
		if (listener == null) {
			throw new IllegalArgumentException("listener can't be null.");
		}
		this.electionPath = electionPath;
		this.listener = listener;
		registerElection(client);
		client.registerSessionListener(new SessionListener() {
			@Override
			public void onSessionCreated(ZkClient client)
					throws KeeperException, InterruptedException {
				registerElection(client);
			}
		});
	}

	public String getMyName() {
		return myName.get();
	}

	void registerElection(ZkClient client) throws KeeperException,
			InterruptedException {

		String path = client.create(electionPath + "/member-", null,
				CreateMode.EPHEMERAL_SEQUENTIAL);
		myName.set(path.substring(electionPath.length() + 1));

		client.watchChildren(electionPath, new ChildrenChangedListener() {

			@Override
			public void onChildrenChanged(List<String> children) {
				if (!children.isEmpty()) {
					List<String> copy = new ArrayList<String>(children);
					Collections.sort(copy);
					String leaderName = copy.get(0);
					listener.onElectionChanged(leaderName, myName.get(), copy);
				}
			}
		});

	}

}
