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
		
    private int myid;
    private ObjectOutputStream outstream;
    private ObjectInputStream instream;
    private LinkedBlockingQueue<Message> queue;
    private boolean isLeader;

	public NodeAndLBConn(ObjectInputStream in,ObjectOutputStream out, int myID2,
			LinkedBlockingQueue<Message> q, boolean status) {

			this.myid = myID2;
			this.instream = in;
			this.outstream = out;
			this.queue = q;
			this.isLeader = status;
	}

	@Override
	public void run() {
		 try {
			
			
			// Setup the timer for heartbeat
			Timer timer = new Timer();
			TimerTask task = new HeartBeat(outstream);
			timer.schedule(task, new Date(), 1000);
			
					
			while(true) {
				Message msg = (Message) instream.readObject();
				
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