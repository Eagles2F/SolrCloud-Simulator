package edu.cmu.ece845.loadbalancer;

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
	
	public ClientServer(int port, NodeHiringServer server){
		this.portNum = port;
		this.running = true;
		this.nodeServer = server;
	}
	@Override
	public void run() {
		System.out.println("ClientServer starts to listen on port:" + this.portNum);
		try {
			ServerSocket server = new ServerSocket(this.portNum);

			this.clientSoc = server.accept();
			listenToClient();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void listenToClient(){
		try {
			this.objInput = new ObjectInputStream(clientSoc.getInputStream());
			this.objOutput = new ObjectOutputStream(clientSoc.getOutputStream());
			
			while(running){
				Message msg = (Message)this.objInput.readObject();
				
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
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void handleWrite(Message msg){
		//read key and send the write request to the leader node
		System.out.println("write request: " + msg.getSeqNum() + " key:" + msg.getKey()
				+ " value " + msg.getValue());
		try {
			this.nodeServer.nodeListenerMap.get(this.nodeServer.masterID).sendToNode(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
