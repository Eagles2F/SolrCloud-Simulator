package edu.cmu.ece845.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NodeClient implements Runnable {
	
	private Socket loadBalSocket;
	private ObjectOutputStream outstream;
    private ObjectInputStream instream;
    
	public NodeClient(Socket socketToLB) {
		this.loadBalSocket = socketToLB;
	}


	@Override
	public void run() {
		 try {
			outstream =  new ObjectOutputStream(loadBalSocket.getOutputStream());
			instream = new ObjectInputStream(loadBalSocket.getInputStream());
			
		//	Timer timer = new Timer();
		//	TimerTask task = new HeartBeat(outstream);
		//	timer.schedule(task, new Date(), 5000);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			while(true) {
				String s = br.readLine();
				s = "client: " + s;
				outstream.writeObject(s);
				System.out.println(instream.readObject().toString());
				
			}
			
		        
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	    
	}
}

class HeartBeat extends TimerTask {

	ObjectOutputStream outstream;
	
	public HeartBeat(ObjectOutputStream outstream2) {
		this.outstream = outstream2;
	}

	@Override
	public void run() {
		System.out.println("hello");
	}
	
}