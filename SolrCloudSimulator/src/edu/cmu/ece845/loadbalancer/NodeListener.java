package edu.cmu.ece845.loadbalancer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.Set;

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
    
    // initialization messages to a newbie node
    private void initializeNode(Boolean is_new) throws IOException{
    	Message msg = new Message(MessageType.nodeInitialization);
    	msg.setAssignedID(nodeId);
    	msg.setIs_new(is_new);
    	int mID = this.hiringServer.masterID;
    	msg.setLeaderID(mID);
    	msg.setLeaderPort(this.hiringServer.nodeComPortMap.get(mID));
    	msg.setLeaderIP(this.hiringServer.nodeSocMap.get(mID).getInetAddress().getHostAddress());
    	sendToNode(msg);
    }
    
    /*
     * This function will update the state of node status on LB to 'active'.
     */
    private void handleHeartbeat(HeartbeatTimer timer, Message msg){
  
    	this.hiringServer.nodeStatusMap.replace(this.nodeId, false, true);
    	timer.reset();
    }

    private void handleInit(Message msg){
    	int port=Integer.valueOf(msg.getValue());
    	
    	//check this port to see whether it has existed or not
    	if(this.hiringServer.nodeComPortMap.containsValue(port)){
    		System.out.println("old node:"+this.nodeId+" is listening on port: "+port +" for other nodes");
    		//delete the old port-id entry
    		int old_id = 0;
    		final Set<Entry<Integer,Integer>> entries = this.hiringServer.nodeComPortMap.entrySet();
    		
    		for(Entry<Integer, Integer> entry: entries){
    			if(entry.getValue() == port){
    				old_id = entry.getKey();
    			}
    		}
    		this.hiringServer.nodeComPortMap.remove(old_id);
    		this.hiringServer.nodeComPortMap.put(this.nodeId, port);
    		
    		// send initialization message to the old Node
			try {
				initializeNode(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    	}
    	else{
    	
    		System.out.println("the node:"+this.nodeId+" is listening on port:"+port +" for other nodes");
    		this.hiringServer.nodeComPortMap.put(this.nodeId, port);
    	
    		// send initialization message to the new Node
    			try {
    				initializeNode(true);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    	}
    }
	@Override
	public void run() {
		
		
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
				case nodeInitialization:
					handleInit(msg);
					break;
				case heartbeat:
					handleHeartbeat(timer,msg);
					break;
				default:
					break;
				}
			} catch (EOFException e1){
				System.out.println("node "+this.nodeId + "died!");
				//if the dead node is the leader, we should do a re-election
				if(this.nodeId == this.hiringServer.masterID){
					this.hiringServer.masterID++; // choose the min id in healthy nodes
					//notify all the nodes there is a new leader
					
				}
				this.hiringServer.nodeStatusMap.remove(this.nodeId);
				this.hiringServer.nodeSocMap.remove(this.nodeId);
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
