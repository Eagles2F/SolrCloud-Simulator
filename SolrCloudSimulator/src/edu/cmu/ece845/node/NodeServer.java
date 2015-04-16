package edu.cmu.ece845.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.ece845.utility.Message;

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
    
	public NodeServer(NodeMain nodemain, Socket sock, int id) {
		this.socket = sock;	
		this.nodesJoined = id;
		this.nodeMain = nodemain;
	}

	@Override
	public void run() {
		try {
		System.out.println("connection established " + socket.getLocalPort() + " remote " + socket.getPort());
		outstream = new ObjectOutputStream(socket.getOutputStream());
		instream = new ObjectInputStream(socket.getInputStream());
		System.out.println("thread " + nodesJoined +" " );
		Message msg;
		
		while(true) {
			// get msg from the queue. the queue is populated by the other thread which gets the data from the lb.
			msg = nodeMain.queue.take();
			outstream.writeObject(msg);
		}
		// replicas send syn msg to master
		// save the replica ids in some table (in the NodeMain class)
		
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
