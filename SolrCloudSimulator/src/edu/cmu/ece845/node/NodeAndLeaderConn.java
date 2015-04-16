package edu.cmu.ece845.node;

/**
 * @author darshs
 * Connect with the leader and get data
 */
public class NodeAndLeaderConn implements Runnable {

	private int myID;
	private int leaderID;
	private int leaderPort;
	private String leaderIP;
	

	public NodeAndLeaderConn(int leaderId, int myID2, String leaderIP,
			int leaderPort) {
		
		this.myID = myID2;
		
	}

	@Override
	public void run() {

	}

}
