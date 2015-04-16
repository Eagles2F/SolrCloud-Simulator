package edu.cmu.ece845.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author darshs
 *
 */
public class LBTest {

	public static void main(String[] args) {
		
		try {
			ServerSocket listener = new ServerSocket(Integer.parseInt("9999"));
			
			Socket socket = listener.accept();
			
			ObjectOutputStream outstream =  new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
			

			while(true) {
				System.out.println("*");
				String s = instream.readObject().toString();
				System.out.println(s);
				outstream.writeObject("LB: "  + s);
			}
			
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
