package edu.cmu.ece845.loadbalancer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;


/*
 * @Author: Yifan Li
 * @Description:
 * This class will handle the communication between loadBalancer and nodes.
 * And serve the requests from the node depending on the type of the requests.
 * @Date: April 12, 2015
 */
public class NodeListener implements Runnable{
	private int nodeId;
	private Socket nodeSoc;
    private volatile boolean running;

    private ObjectInputStream objInput;
    private ObjectOutputStream objOutput;
    
    public NodeListener(int id, Socket s) throws IOException {

        nodeId = id;
        running = true;
        System.out.println("adding a new node listener for node "+id);
        objInput = new ObjectInputStream(s.getInputStream());
        objOutput = new ObjectOutputStream(s.getOutputStream());
        running = true;
    }
    
    private void sendToNode(Message msg) throws IOException{
    	objOutput.writeObject(msg);
        objOutput.flush();
    }
    
    private void initializeNode() throws IOException{
    	Message msg = new Message(MessageType.nodeInitilization);
    	msg.setAssignedID(nodeId);
    	sendToNode(msg);
    }
    
	@Override
	public void run() {
		// send initialization message to the new Node
		try {
			initializeNode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//listening to coming new messages
		while(running){
			try {
				Message msg = (Message) objInput.readObject();
				
				switch(msg.getMessageType()){
				case heartbeat:
					break;
				default:
					break;
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
