package edu.cmu.ece845.node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;
import edu.cmu.ece845.utility.TuneableVars;

/**
 * @author darshs
 *
 */
public class NodeAndLBConn implements Runnable {
		
    private boolean isLeader;
    private NodeMain nodeMain;
    private FileWriter fw;
    private BufferedWriter bw;
    private Message msg;
    
	public NodeAndLBConn(NodeMain nodemain, boolean leaderStatus) {
		this.nodeMain = nodemain;
		this.isLeader = leaderStatus;
	}

	@Override
	public void run() {
		 try {
		
			// Setup the timer for heartbeat. Currently it is 1 sec
			System.out.println("In NodeAndLBConn connection thread");
			Timer timer = new Timer();
			TimerTask task = new HeartBeat(nodeMain.outstream, nodeMain.myID);
			timer.schedule(task, new Date(), TuneableVars.HEARTBEAT_TIMER);
			
			String content;
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			// if I am the leader, then open file for writing
			if (isLeader) {
				
				fw = new FileWriter(nodeMain.logFile.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);	
			}
			
			// get the message from loadbalancer to write
			while(!Thread.currentThread().isInterrupted()) {
				
				msg = (Message) nodeMain.instream.readObject();
				
				if (isLeader && msg.getMessageType() == MessageType.writeData) {
					// if msg is write - then write to local file and forward to replicas 
					System.out.println("Normal write message");
			
					content =  nodeMain.timestamp + " " + dateFormat.format(new Date()) + 
							" key:" + msg.getKey() + " value:" + msg.getValue() + "\n";
					bw.write(content);
					bw.flush();
					
					// increment the timestamp for the next log entry. This number is unique to each master
					nodeMain.timestamp++;
					
					// the message is stored in data and this string data is to be stored in replicas
					msg.setDataString(content);
					
					// put the message in all the queues. Each queue belongs to one replica. The server will push send the data
					for (LinkedBlockingQueue<Message> q : nodeMain.queueList)
						q.put(msg);
					
					// Write in the hashmap for local caching
					nodeMain.writeToDataCache(msg.getKey(), msg.getValue());
					
					
					// sending ack form the other thread
					
					// dead code
				//	Message m = new Message(MessageType.writeAck);
				//	m.setSeqNum(msg.getSeqNum());
				//	nodeMain.outstream.writeObject(m);
					
					
				} else if (isLeader && msg.getMessageType() == MessageType.queryData) {
					
					// message is query, look in the local hashmap
					String val = nodeMain.getValueFromDataCache(msg.getKey());
					
					// send reply if query was present in the cache
					if (val != null)
					{
							Message m = new Message(MessageType.queryAck);
							m.setSeqNum(msg.getSeqNum());
							m.setKey(msg.getKey());
							nodeMain.outstream.writeObject(m);
					}
					
					// don't send anything back if query not found
					
				} else {
					// the msg can be that I am the new leader or the leader has changed. 
					// So, add logic to respond to these kinds of messages
					if (msg.getMessageType() == MessageType.leaderReelection) {
						System.out.println("Leader re-election msg");
						System.out.println("My id is " + nodeMain.myID);
						System.out.println(msg.toString());
						
						// method to take care of restarting the threads
						nodeMain.restartNodeThreadtoLB(msg);
						
					}
					else {
						System.out.println("shouldn't reach here in nodeandlbconnection");
					}
				}
							
			}
					        
		} catch (IOException | ClassNotFoundException | InterruptedException  e) {
				//e.printStackTrace();
				System.out.println(" nodeandlb died 3" );
		}
	    
		 try {
			 if (isLeader) {
				 bw.close();
			 }
		} catch (IOException e) {
			System.out.println(" nodeandlb died 4" );
			// e.printStackTrace();
		}
	}
}

/*
 * Write heartbeat messages to the loadbalancer
 * 
 */
class HeartBeat extends TimerTask {

	ObjectOutputStream outstream;
	int myid;
	
	public HeartBeat(ObjectOutputStream outstream2, int id ) {
		this.outstream = outstream2;
		this.myid = id;
	}

	@Override
	public void run() {
		
		try {
			Message m = new Message(MessageType.heartbeat);
			m.setAssignedID(myid);
			outstream.writeObject(m);
		
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println(" nodeandlb died 5" );
			
		}
	}
	
}