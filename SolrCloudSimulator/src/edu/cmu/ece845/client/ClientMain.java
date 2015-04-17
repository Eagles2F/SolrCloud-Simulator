package edu.cmu.ece845.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {
	private int serverPort;
	private String serverHost;
	private ClientListener listener;
	
	public ClientMain(){
		this.serverPort = 11112;
		this.serverHost = "";
		
	}
		
	private void startListenThread(){
		
	}
	
	public static void main(String[] args) {
		
		ClientMain client = new ClientMain();
		
		try {
			Socket soc = new Socket(client.serverHost, client.serverPort);
			
			//doing write or read request
			ObjectInputStream objInput = new ObjectInputStream(soc.getInputStream());
			ObjectOutputStream objOutput = new ObjectOutputStream(soc.getOutputStream());
			
			client.listener = new ClientListener(objInput);
		    //listening thread
			client.startListenThread();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
