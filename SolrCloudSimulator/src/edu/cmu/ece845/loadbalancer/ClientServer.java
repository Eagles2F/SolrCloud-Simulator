package edu.cmu.ece845.loadbalancer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cmu.ece845.utility.Message;

/*
 * @Author: Yifan Li
 * @Description: This class will handle the index&query requests from the client(only single client process is allowed).
 * @Date: April 12, 2015
 */
public class ClientServer implements Runnable{
	
	private int portNum;
	private volatile boolean running;
	
	private NodeHiringServer nodeServer;
	
	private Socket clientSoc;
	
	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;
	
	public volatile boolean is_quorum;
	
	public ClientServer(int port, NodeHiringServer server, boolean is_q){
		this.portNum = port;
		this.running = true;
		this.nodeServer = server;
		this.is_quorum = is_q;
	}
	@Override
	public void run() {
		System.out.println("ClientServer starts to listen on port:" + this.portNum);
		
		ServerSocket server;
		try {
			server = new ServerSocket(this.portNum);
			while(running){	
				this.clientSoc = server.accept();
				System.out.println("Client joined!");
				listenToClient();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void listenToClient(){
		try {
			this.objInput = new ObjectInputStream(clientSoc.getInputStream());
			this.objOutput = new ObjectOutputStream(clientSoc.getOutputStream());
			
			while(running){
				Message msg = (Message)this.objInput.readObject();
				System.out.println(msg.getMessageType()+ "  "+ msg.getAssignedID()+"  "+msg.getKey());
				switch(msg.getMessageType()){
					case writeData:
						handleWrite(msg);
						break;
					case queryData:
						handleQuery(msg);
						break;
					default:
						break;
				}
			}
			
		} catch (EOFException e1){
			return;
		} catch (IOException e) {
			e.printStackTrace();		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private void handleWrite(Message msg){
		//read key and send the write request to the leader node
		if(this.is_quorum){
			try {
				if(this.nodeServer.nodeStatusMap.entrySet().size() > 1){
				System.out.println("write request: " + msg.getSeqNum() + " key:" + msg.getKey()
						+ " value " + msg.getValue());
					this.nodeServer.nodeListenerMap.get(this.nodeServer.masterID).sendToNode(msg);
				}else {
					System.out.println("write request: "+ msg.getSeqNum() +" get rejected due to lack of Quorum!");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else{
			System.out.println("write request: " + msg.getSeqNum() + " key:" + msg.getKey()
					+ " value " + msg.getValue());
				try {
					this.nodeServer.nodeListenerMap.get(this.nodeServer.masterID).sendToNode(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private void handleQuery(Message msg){
		//read data and send to the leader node
		try {
			System.out.println("query request:"+msg.getSeqNum()+" key:" + msg.getKey());
			this.nodeServer.nodeListenerMap.get(this.nodeServer.masterID).sendToNode(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendToClient(Message msg){
		try {
			this.objOutput.writeObject(msg);
			this.objOutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
