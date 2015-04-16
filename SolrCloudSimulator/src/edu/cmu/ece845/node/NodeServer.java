package edu.cmu.ece845.node;

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
			String id = msg.getValue();
			replicaid = msg.getAssignedID();
			
			// Save the replica ids in some table (in the NodeMain class)
			nodeMain.queueHashMap.put(replicaid, localQueue);
			
			// new replica. So sync from beginning
			if (Integer.parseInt(id) == -1)
			{
				System.out.println("sync required from starting");
				// TODO: Read the file and sync from beginning
				
			} // restarted replica. So sync from some id
			else {
				System.out.println("sync required from id: " + Integer.parseInt(id));
				// TODO: Read the file and find the id to send the remaining ids to the replica
			}
		}
		
		else {
			// do something else - shouldn't reach here
		}
		
		
		while(true) {
			// get msg from the queue. the queue is populated by the LB thread which gets the data from the LB.
			msg = localQueue.take();
			System.out.println("msg received " + msg.getValue());
			
			// write the data to replica
			outstream.writeObject(msg);
		}
		
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
			// remove if the node dies - 
			System.out.println("Thread died. This thread was of the leader and replica id: " + replicaid + " was connected to it");
			nodeMain.queueList.remove(localQueue);
			nodeMain.queueHashMap.remove(replicaid);
		}
	}
}
