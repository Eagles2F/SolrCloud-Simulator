package edu.cmu.ece845.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import edu.cmu.ece845.utility.Message;

public class ClientListener implements Runnable {

	private ObjectInputStream objInput;
	private ClientMain clientMain; 
	private boolean running;
	
	public ClientListener(ObjectInputStream input,ClientMain main){
		objInput = input;
		running = true;
		clientMain = main;
	}
	
	private void handleWriteAck(Message msg){
		System.out.println("write request: " + msg.getSeqNum() + " has been acked successfully! at " + System.currentTimeMillis() + "ms");
		this.clientMain.writeAckTimestamp.put(msg.getSeqNum(), System.currentTimeMillis());
	}
	
	private void handleQueryAck(Message msg){

		System.out.println("read request: "+msg.getKey() + " seqNum: "+ msg.getSeqNum() + " has been acked successfully!");

		this.clientMain.readAckList.add(msg.getSeqNum());
	}
	
	@Override
	public void run() {
		System.out.println("Client listener created!");
		while(running){
		try {
			Message msg = (Message)objInput.readObject();
			
			switch(msg.getMessageType()){
			    case writeAck:
			    	handleWriteAck(msg);
			    	break;
			    case queryAck:
			    	handleQueryAck(msg);
			    	break;
				default:
					break;
			}
		} catch (EOFException e){
			return ;
		}
		catch (ClassNotFoundException | IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
	}

}
