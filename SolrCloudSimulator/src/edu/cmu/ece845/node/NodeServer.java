package edu.cmu.ece845.node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;

/**
 * @author darshs
 *
 */
public class NodeServer implements Runnable {

	private Socket socket;
	private ObjectOutputStream outstream;
    private ObjectInputStream instream;
    private int nodesJoined;
    private NodeMain nodeMain;
    private LinkedBlockingQueue<Message> localQueue;
    private int replicaid;
    
	public NodeServer(NodeMain nodemain, Socket sock, int id) {
		this.socket = sock;	
		this.nodesJoined = id;
		this.nodeMain = nodemain;
		this.localQueue = new LinkedBlockingQueue<Message>();
	}

	@Override
	public void run() {
		try {
		
		// Replica joined
		System.out.println("In NodeServer connection thread where replicas join");
		System.out.println("connection established " + socket.getLocalPort() + " remote " + socket.getPort());
		outstream = new ObjectOutputStream(socket.getOutputStream());
		instream = new ObjectInputStream(socket.getInputStream());
		System.out.println("thread " + nodesJoined +" " );
		Message msg;
		
		// for the first time, the node will send the sync msg -  replica send syn msg to master
		msg = (Message) instream.readObject();
		
		if (msg.getMessageType() == MessageType.syncwithleader) 
		{
			nodeMain.addReplicaQueueinQueueList(localQueue);
			String syncid = msg.getValue();
			replicaid = msg.getAssignedID();
			
			// Save the replica ids in some table (in the NodeMain class)
			nodeMain.queueHashMap.put(replicaid, localQueue);
			
			FileReader fr = new FileReader(nodeMain.logFile.getAbsoluteFile());
			BufferedReader br = new BufferedReader(fr);
		
			Message syncm;
			// TODO: Add a hook for the sync system
			
			// new replica. So sync from beginning
			if (Integer.parseInt(syncid) == -1)
			{
				System.out.println("sync required from starting");
				// Read the file and sync from beginning
				String currline="";
				
				while ((currline = br.readLine()) != null) {
					syncm = new Message(MessageType.syncwithleader);
					currline = currline + "\n";
					syncm.setDataString(currline);
					outstream.writeObject(syncm);
					// TODO: Wait for ACK ?
				}
				
			} // restarted replica. So sync from some id
			else {
				System.out.println("sync required after id: " + Integer.parseInt(syncid));
				// Read the file and find the id to send the remaining ids to the replica
	
				String currline="";
				String [] tok;
				
				while ((currline = br.readLine()) != null) {
					tok = currline.split(" ");
					if (tok[0].equals(syncid))  // find the last id
						break;
				}
						
				// Found the last id. Now send new data
				while ((currline = br.readLine()) != null) {
					syncm = new Message(MessageType.syncwithleader);
					currline = currline + "\n";
					syncm.setDataString(currline);
					outstream.writeObject(syncm);					
					//Thread.sleep(2000); // for testing
					// TODO: Wait for ACK ?
				}
			}
			
			br.close();
		}
		
		else {
			// do something else - shouldn't reach here
			System.out.println("shouldn't reach here");
		}
		
		
		while(true) {
			// get msg from the queue. the queue is populated by the LB thread which gets the data from the LB.
			msg = localQueue.take();
			System.out.println("msg received " + msg.getValue());
			
			// write the data to replica
			outstream.writeObject(msg);
		}
		
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			//e.printStackTrace();
			// remove if the node dies - 
			System.out.println(" nodeserver died 1" );
			System.out.println("Thread died. This thread was of the leader and replica id: " + replicaid + " was connected to it");
			nodeMain.queueList.remove(localQueue);
			nodeMain.queueHashMap.remove(replicaid);
		}
	}
}
