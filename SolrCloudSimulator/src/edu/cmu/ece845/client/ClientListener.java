package edu.cmu.ece845.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.cmu.ece845.utility.Message;

public class ClientListener implements Runnable {

	private ObjectInputStream objInput;
	private boolean running;
	public ClientListener(ObjectInputStream input){
		objInput = input;
		running = true;
	}
	@Override
	public void run() {
		while(running){
		try {
			Message msg = (Message)objInput.readObject();
			
			switch(msg.getMessageType()){
				default:
					break;
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
	}

}
