package edu.cmu.ece845.loadbalancer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.cmu.ece845.utility.HeartbeatTimer;
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
	private NodeHiringServer hiringServer;
    public volatile boolean running;

    private ObjectInputStream objInput;
    private ObjectOutputStream objOutput;
    
    public NodeListener(NodeHiringServer ns,int id, Socket s) throws IOException {
    	hiringServer = ns;
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
    
    // initialization messages to the newbie node
    private void initializeNode() throws IOException{
    	Message msg = new Message(MessageType.nodeInitilization);
    	msg.setAssignedID(nodeId);
    	int mID = this.hiringServer.masterID;
    	msg.setLeaderID(mID);
    	msg.setLeaderPort(this.hiringServer.nodeSocMap.get(mID).getPort());
    	msg.setLeaderIP(this.hiringServer.nodeSocMap.get(mID).getInetAddress().getHostAddress());
    	sendToNode(msg);
    }
    
    /*
     * This function will update the state of node status on LB to 'active'.
     */
    private void handleHeartbeat(HeartbeatTimer timer, Message msg){
    	System.out.println("heartbeat:" + this.nodeId);
    	this.hiringServer.nodeStatusMap.replace(this.nodeId, false, true);
    	timer.reset();
    }

	@Override
	public void run() {
		// send initialization message to the new Node
		try {
			initializeNode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HeartbeatTimer timer = new HeartbeatTimer(this,3000){   // time unit: millisecond
			public void timeout(){
				this.nl.running = false; //pause the thread, i'm not sure what will happen here.
				this.nl.setNodeFailure();//change the status of node in the global map
			}
		};
		timer.start();
		
		//listening to coming new messages
		while(running){
			try {
				Message msg = (Message) objInput.readObject();
				
				switch(msg.getMessageType()){
				case heartbeat:
					handleHeartbeat(timer,msg);
					break;
				default:
					break;
				}
			} catch (EOFException e1){
				System.out.println("node "+this.nodeId + "died!");
				this.hiringServer.nodeStatusMap.replace(this.nodeId, true, false);
				return ;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			} 
	
		}
	}
	
	public int getNodeID(){
		return this.nodeId;
	}
	
	public void setNodeFailure(){
		this.hiringServer.nodeStatusMap.replace(this.nodeId, true, false);
	}

}
