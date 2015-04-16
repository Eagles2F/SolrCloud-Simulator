package edu.cmu.ece845.node;

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

	private int myID;
	private int leaderID;
	private int leaderPort;
	private String leaderIP;
	private boolean isNewNode;
	private NodeMain nodeMain;
	
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
		
		 try {
				socket = new Socket(leaderIP, leaderPort);
				
				ObjectOutputStream outstream =  new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
				
				// syn me
				if (!isNewNode) {
					System.out.println("lets sync");
					boolean syncNotFinished = true;
					int lastwriteid = 1; // read id from file

					Message m = new Message(MessageType.syncwithleader);
					m.setValue(String.valueOf(lastwriteid));
					outstream.writeObject(m);
					
					while (syncNotFinished) {
						
						msg = (Message) instream.readObject();
						// write to file
					}
				}
				
				// new message from leader
				while(true) {
				
					msg = (Message) instream.readObject();
					outstream.writeObject("ACK");
		
					// write msg to file
					
				}
				
				
			} catch ( ClassNotFoundException | IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			 
	}

}
