package edu.cmu.ece845.node;


/* 
 * This is the node in the system. Each node will be able to receive a message and send a message
 * 
 * on becoming live - (i.e. either new or from dead)
 * The node will message to the loadbalancer(with message kind:JOIN) 
 * The load balancer will send id to get the information regarding the other nodes. If the 
 * node is new, it will get a new id and the message kind will be NEWNODE. If NEWNODE, then 
 * create a file to write and do other initializations. If the message is RECOVER, then the 
 * node was down and is up again. In this case, read the last log entry and ask the master for 
 * missing/new data. 
 * In reply message from loadbalancer, the message will contain information regarding other 
 * nodes in the system
 * 
 * write thread:
 * If the node is the master, it will receive the data(write - the message kind:WR) from the load balancer and will
 * write that value in its cache(KV hashmap). It will also log the same message in the file on 
 * disk with a unique positive always increasing id and timestamp. 
 * It will also forward the message (with message kind:SYN) to all the replicas. It will get
 * the information about the replicas from the loadbalancer
 * 
 * read thread:
 * If the node is a replica, it will receive data (with message kind:SYN) and it will write that
 * data to the file on disk.
 * 
 * timer thread:
 * A timer thread which will send heartbeat message to the loadbalancer every 1 sec. 
 * 
 * processing thread:
 * Process the data from the blocking queue.
 */

public class NodeMain {

	public static void main(String[] args) {
		
		String port = args[0];
		
	}
}
