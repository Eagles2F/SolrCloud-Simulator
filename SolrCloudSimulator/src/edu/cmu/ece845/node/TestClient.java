package edu.cmu.ece845.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author darshs
 *
 */
public class TestClient {
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 try {
			Socket socket = new Socket("localhost", 9900);
			
			ObjectOutputStream outstream =  new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			while(true) {
				String s = br.readLine();
				
				outstream.writeObject(s);
				System.out.println(instream.readObject().toString());
				
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}
