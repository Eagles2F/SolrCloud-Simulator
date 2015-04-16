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
		
			// Setup the timer for heartbeat
			Timer timer = new Timer();
			TimerTask task = new HeartBeat(nodeMain.outstream);
			timer.schedule(task, new Date(), 1000);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String content;
			
			if (isLeader) {
				
				fw = new FileWriter(nodeMain.logFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);	
			}
			
			while(true) {
				msg = (Message) nodeMain.instream.readObject();
				
				if (isLeader && msg.getMessageType() == MessageType.writeData) {
					// if msg is write
					// leader, then write to local file and forward to replicas 
					System.out.println("Normal write message");
			/*		content =  msg.getSeqNum() + " " + dateFormat.format(new Date()) + 
							" key:" + msg.getKey() + " value:" + msg.getValue();
					bw.write(content);
					
					// the message is stored in data and this string data is to be stored in replicas
					msg.setDataString(content);
					
					// put the message in all the queues. Each queue belongs to one replica. The server will push send the data
					for (LinkedBlockingQueue<Message> q : nodeMain.queueList)
						q.put(msg);
					// TODO: Write in the hashmap for local caching
					
					// if message is query, look in the local hashmap and file
			*/		
				
					
				} else {
					// the msg can be that I am the new leader or the leader has changed. 
					// So, add logic to respond to these kinds of messages
					if (msg.getMessageType() == MessageType.leaderReelection) {
						System.out.println("Leader re-election msg");
						System.out.println("My id is " + nodeMain.myID);
						System.out.println(msg.toString());
						System.out.println("My id is " + nodeMain.myID);
					}
					else {
						System.out.println("shouldn't reach here");
					}
				}
				
				// Do something with the msg object. 
							
			}
					        
		} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
		}
	    
		 try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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