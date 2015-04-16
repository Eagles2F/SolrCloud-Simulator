package edu.cmu.ece845.utility;

public enum MessageType {
	
        nodeInitialization,  //initialization message sent from loadBalancer to the nodes
        heartbeat, // the heart beat message from node to loadBalancer
        leaderReelection, // the message sent from loadBalancer to the nodes to notify the change of leader
        syncwithleader, // the message to send for syncing with the leader
        writeData, // writing the new data to the leader
        queryData // query the data from the leader
}
