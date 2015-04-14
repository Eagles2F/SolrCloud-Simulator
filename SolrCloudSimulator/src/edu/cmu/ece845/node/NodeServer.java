package edu.cmu.ece845.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NodeServer implements Runnable {

	private Socket socket;
	private ObjectOutputStream outstream;
    private ObjectInputStream instream;
    private int threadnum;
	
	public NodeServer(Socket sock, int threadnum) {
		this.socket = sock;
		this.threadnum = threadnum;
	}
	
	@Override
	public void run() {
		try {
		System.out.println("connection established " + socket.getLocalPort() + " remote " + socket.getPort());
		
		outstream = new ObjectOutputStream(socket.getOutputStream());
	
		instream = new ObjectInputStream(socket.getInputStream());
		
		while(true) {
			String s = instream.readObject().toString();
			System.out.println(s);
			outstream.writeObject("thread " + threadnum +" "  + s);
		}
	
		
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
