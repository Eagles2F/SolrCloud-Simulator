package edu.cmu.ece845.node;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		
		 try {
			 
				socket = new Socket(leaderIP, leaderPort);
				
				ObjectOutputStream outstream =  new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());	
				 
				// initialize the files stuff
				FileWriter fw = new FileWriter(nodeMain.logFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);	
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String content, lastWriteId;
				
				// sync me
				if (!isNewNode) {
					System.out.println("lets sync");
					
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
					
					System.out.println("The last written id is " + lastWriteId);
					br.close();
					
					
				}
				else {
					lastWriteId = "-1";
				}
				
				// send sync/join message to the leader with the last written id
				// lastWriteId == -1 if new node. sync and join are same for the replica
				Message m = new Message(MessageType.syncwithleader);
				m.setValue(lastWriteId);
				outstream.writeObject(m);
					
				// wait for messages - multithread?
				while(true) {
					
					msg = (Message) instream.readObject();
					
					// write msg to file
					if (msg.getMessageType() == MessageType.syncwithleader) {
					// pull the message content from the incoming message. It will be type sync
						content = msg.getDataString();
						bw.write(content);
					}
					else if (msg.getMessageType() == MessageType.writeData) {
						content = msg.getDataString();
						bw.write(content);
					}
					else {
						// TODO: handle any other message type
					}
				}
				
				
				
				
			} catch ( ClassNotFoundException | IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			 
	}
	

}
