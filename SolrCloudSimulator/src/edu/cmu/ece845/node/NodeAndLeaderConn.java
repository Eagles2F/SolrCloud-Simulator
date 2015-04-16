package edu.cmu.ece845.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.ece845.utility.Message;

/**
 * @author darshs
 * Connect with the leader and get data
 */
public class NodeAndLeaderConn implements Runnable {

	private int myID;
	private int leaderID;
	private int leaderPort;
	private String leaderIP;
	

	public NodeAndLeaderConn(int leaderId, int myID2, String leaderIp, int leaderP) {
		
		this.myID = myID2;
		this.leaderID = leaderId;
		this.leaderPort = leaderP;
		this.leaderIP = leaderIp;
	}

	@Override
	public void run() {

		Socket socket = null;
		Message msg;
		
		 try {
				socket = new Socket(leaderIP, leaderPort);
				
				ObjectOutputStream outstream =  new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
				
				while(true) {
					
					msg = (Message) instream.readObject();
					
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
