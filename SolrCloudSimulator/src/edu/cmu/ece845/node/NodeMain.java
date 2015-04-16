package edu.cmu.ece845.node;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;


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

/**
 * @author darshs
 *
 */
public class NodeMain {
	
	public LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>(); 
	public String logfileName = "";
	public int myID;
	public ObjectOutputStream outstream;
	public ObjectInputStream instream;
	
	public void runNodeMain(String args[]) {
		
		Socket socketToLB = null;
	
		try {
	
			String myPort = args[0];
			logfileName = "logfile_" + myPort;
			
			String[] loadBalArgs = args[1].split(":");
			
			String loadBalIP = loadBalArgs[0];
			String loadBalPort = loadBalArgs[1];
			
		    System.out.println(loadBalIP);
			System.out.println(loadBalPort);
			
			// Connect to LB
			socketToLB = new Socket(loadBalIP, Integer.parseInt(loadBalPort));
			
		    outstream =  new ObjectOutputStream(socketToLB.getOutputStream());
			instream = new ObjectInputStream(socketToLB.getInputStream());

			Message m = new Message(MessageType.nodeInitialization);
			
			m.setKey("nodeInitialization");
			m.setValue(myPort);
			
			// tell the loadbalancer that you are joining
			outstream.writeObject(m);
			
			// get the reply and parse it
			Message msg = (Message) instream.readObject();
			
			myID = msg.getAssignedID();
			
			System.out.println(msg.toString());
				
			// delete the following line and uncomment the below if-else
			new Thread(new NodeAndLBConn(this, false)).start();			
		

			// check if I am old guy or existing guy. If I am oldguy, I am I have the file and i need to sync
			 if(msg.getIs_new()) {
				// File file = new File(logfileName + ".txt");
			 }
			
		/*	
			if (myID != msg.getLeaderID()) {
				new Thread(new NodeAndLBConn(this, false)).start();			
				new Thread(new NodeAndLeaderConn(this, msg.getLeaderID(), msg.getLeaderIP(), msg.getLeaderPort(), msg.getIs_new())).start();
			}
			else {
				new Thread(new NodeAndLBConn(this, true)).start();	
				startServer(myPort);
			}
		
		*/
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	/* start the server if you are the leader*/
	private void startServer(String myPort) {
		
		ServerSocket listener = null;
		int id = 0;
		try {
			listener = new ServerSocket(Integer.parseInt(myPort));
			
	            while (true) {
	            	Socket sock = listener.accept();
	                new Thread(new NodeServer(this, sock, id++)).start();
	            }
	        
		} catch( IOException e) {
	        	e.printStackTrace();
	        
	    } finally { 
	            try {
					listener.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	          
	        }
	}
	
	public static void main(String[] args) {
		
		NodeMain nm = new NodeMain();
		nm.runNodeMain(args);
		
	}
}
