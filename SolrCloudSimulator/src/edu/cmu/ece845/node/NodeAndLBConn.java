package edu.cmu.ece845.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;

/**
 * @author darshs
 *
 */
public class NodeAndLBConn implements Runnable {
		
    private boolean isLeader;
    private NodeMain nodeMain;

	public NodeAndLBConn(NodeMain nodemain, boolean leaderStatus) {
		/*
		this.myid = myID2;
		this.instream = in;
		this.outstream = out;
		this.queue = q;
		*/
		this.nodeMain = nodemain;
		this.isLeader = leaderStatus;
	}

	@Override
	public void run() {
		 try {
			
			
			// Setup the timer for heartbeat
			Timer timer = new Timer();
			TimerTask task = new HeartBeat(nodeMain.outstream);
			timer.schedule(task, new Date(), 1000);
			
					
			while(true) {
				Message msg = (Message) nodeMain.instream.readObject();
				
				if (isLeader) {
					// if msg is write
					// leader, then write to local file and forward to replicas 
					// queue.add(msg);
					
					// if message is query, look in the local hashmap and file
					// 
					
					
				} else {
					// the msg can be that I am the new leader or the leader has changed. 
					// So, add logic to respond to these kinds of messages
				}
				
				// Do something with the msg object. 
							
			}
					        
		} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
		}
	    
	}
}

/*
 * Write heartbeat messages to the loadbalancer
 * 
 */
class HeartBeat extends TimerTask {

	ObjectOutputStream outstream;
	
	public HeartBeat(ObjectOutputStream outstream2) {
		this.outstream = outstream2;
	}

	@Override
	public void run() {
		
		try {
			outstream.writeObject(new Message(MessageType.heartbeat));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}