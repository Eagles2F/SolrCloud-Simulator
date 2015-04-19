package edu.cmu.ece845.node;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;

/**
 * @author darshs
 * Connect with the leader and get data
 */
public class NodeAndLeaderConn implements Runnable {

	private int leaderID;
	private int leaderPort;
	private String leaderIP;
	private boolean isNewNode;
	private NodeMain nodeMain;
	private BufferedWriter bw;
	
	public NodeAndLeaderConn(NodeMain nodemain, int leaderId, String leaderIp, int leaderP, Boolean isNew) {
		
		this.nodeMain = nodemain;
		this.leaderID = leaderId;
		this.leaderPort = leaderP;
		this.leaderIP = leaderIp;
		this.isNewNode = isNew;
	}

	@Override
	public void run() {

		Socket socket = null;
		Message msg;
		
		System.out.println("In NodeAndLeaderConn connection. I am the replica and I connected to master");
		
		 try {
			 	// i am a replica. So connect to the leader.
				socket = new Socket(leaderIP, leaderPort);
				
				// initialize streams
				ObjectOutputStream outstream =  new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());	
				 
	
				String content, lastWriteId;
				
				// sync the dead node
				if (!isNewNode) {
					
					System.out.println("lets re sync this  dead node");
					
					// filereader to read the last line of the exisiting log file
					FileReader fr = new FileReader(nodeMain.logFile.getAbsoluteFile());
					BufferedReader br = new BufferedReader(fr);
					
					String lastline="";
					String currline="";
					
					while ((currline = br.readLine()) != null) {
						lastline = currline;
					}
					
					System.out.println(lastline);
					String [] tok = lastline.split(" ");
					lastWriteId = tok[0];
					
					if (lastWriteId.equals(""))
						lastWriteId = "-1";
				
					
					// get the last id to sync after
					System.out.println("The sync id is after " + lastWriteId);
					br.close();
					
					
				}
				else { // I am a new node. So my last id is -1.
					lastWriteId = "-1";
				}
				
				// send sync/join message to the leader with the last written id
				// lastWriteId == -1 if new node. sync and join are same for the replica
				Message m = new Message(MessageType.syncwithleader);
				m.setValue(lastWriteId);
				m.setAssignedID(nodeMain.myID);
				
				outstream.writeObject(m);
					
				// initialize the files stuff
				FileWriter fw = new FileWriter(nodeMain.logFile.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				
				
				// wait for messages - multithread?
				while(true) {
					
					// message from leader
					msg = (Message) instream.readObject();
					
					// write msg to file
					if (msg.getMessageType() == MessageType.syncwithleader) {
					// pull the message content from the incoming message. It will be type sync
						System.out.println("Got sync data from the master");
						content = msg.getDataString();
						bw.write(content);
						bw.flush();
						
						// write to cache
						nodeMain.writeToDataCache(msg.getKey(), msg.getValue());
						// do we ack syn data?
					}
					else if (msg.getMessageType() == MessageType.writeData) {
						System.out.println("got new data from master. Let's save it to my log file ");
						content = msg.getDataString();
						bw.write(content);
						bw.flush();
						
						// write to cache
						nodeMain.writeToDataCache(msg.getKey(), msg.getValue());

						//replica doesn't send back ack to leader for writedata
					}
					else {
						// TODO: handle any other message type - shouldnt come here
						System.out.println("shouldn't come here in nodeandleaderconnection");
					}
				}
	
		 	} catch ( ClassNotFoundException | IOException e) {
				
				System.out.println(" master died 1" );
				//e.printStackTrace();
				System.out.println(" master died 2" );
			} finally {
				try {
					System.out.println(" master died 3" );
					socket.close();
					bw.close();
					System.out.println(" master died 4" );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(" master died 5" );
					//e.printStackTrace();
					System.out.println(" master died 6" );
				}
			}
			 
	}
	

}
