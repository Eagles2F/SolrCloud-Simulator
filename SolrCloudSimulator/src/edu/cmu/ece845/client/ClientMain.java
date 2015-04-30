package edu.cmu.ece845.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;

public class ClientMain {
	private int serverPort;
	private String serverHost;
	private ClientListener listener;
	
	private int writeSeqNum;
	private int readSeqNum;
	
	public List<Integer> readList;
	public List<Integer> readAckList;
	
	public ConcurrentHashMap<Integer, Long> writeAckTimestamp;
	public ConcurrentHashMap<Integer, Long> writeTimestamp;
	
	private BufferedReader console;
	
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private boolean running;
	
	public ClientMain(){
		this.serverPort = 11112;
		this.serverHost = "localhost";

		this.readAckList = new ArrayList<Integer>();
		
		this.writeAckTimestamp = new ConcurrentHashMap<Integer,Long>();
		this.writeTimestamp = new ConcurrentHashMap<Integer, Long>();
		
		this.console = new BufferedReader(new InputStreamReader(System.in));
		this.writeSeqNum = 0;
		this.readSeqNum = 0;
		this.running = true;
	}
	
	//send a message to the LoadBalancer
	public void sendToLB(Message msg){
		try {
			output.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleWrite(String size){// rate in req/sec, length in sec
		int isize = Integer.valueOf(size);
		System.out.println("write: "+size);
		for(int i=0;i<isize;i++){
			Message  msg = new Message(MessageType.writeData);
			msg.setSeqNum(this.writeSeqNum);
			msg.setKey(String.valueOf(this.writeSeqNum));
			msg.setValue(String.valueOf(this.writeSeqNum));
			
			this.writeTimestamp.put(this.writeSeqNum, System.currentTimeMillis());
			System.out.println("write " + this.writeSeqNum + " at " + System.currentTimeMillis() + " ms");
			this.sendToLB(msg);
			this.writeSeqNum++;
		}
	}
	
	public void handleQuery(String startingPoint, String rate, String length){
		int istartingPoint = Integer.valueOf(startingPoint);
		int irate = Integer.valueOf(rate);
		int ilength = Integer.valueOf(length);
		
		System.out.println("query from "+ ilength +" at speed of "+rate + " for " + length+ "secs");
		
		for(int i=0;i<ilength;i++){
			//generating the number of rate request in 1 sec
			for(int j = 0; j<irate; j++){
				Message  msg = new Message(MessageType.queryData);
				msg.setSeqNum(this.readSeqNum);
				msg.setKey(String.valueOf(istartingPoint+i*irate +j ));
				this.sendToLB(msg);
				System.out.println("query request: "+msg.getKey() + " seqNum: "+ msg.getSeqNum());
				this.readSeqNum++;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double success_rate = (double)(this.readAckList.size())/(double)(irate*ilength);
		System.out.println("The success rate of this Query is:" + success_rate);
		this.readAckList.clear();
	}
	
	public void handleQuit(){
		System.exit(0);
	}
	
	public void handleHelp(){
		System.out.println("write [rate] [time_length] \n"+
								"query [starting point] [rate] [time_length] \n");
	}
	
	public void startConsole(){
        System.out.println("This is SolrCloud simulator: Client, type help for more information");
        
        String cmdLine=null;
        while(running){
            System.out.print(">>");
            try{
                cmdLine = console.readLine();
                
            }catch(IOException e){
                System.out.println("IO error while reading the command,console will be closed");
            }            
            
            String[] inputLine = cmdLine.split(" ");
           
            switch(inputLine[0]){
            	case "write":
            		handleWrite(inputLine[1]);
            		break;
            	case "query":
            		handleQuery(inputLine[1],inputLine[2],inputLine[3]);
            		break;
            	case "quit":
            		handleQuit();
            		break;
            	case "help":
            		handleHelp();
            		break;
                default:
                    System.out.println(inputLine[0]+"is not a valid command");
            }
        }
	}
	
	private void startClientServer(){
		Thread t2 = new Thread(listener);
		t2.start();
	}
	
	public static void main(String[] args) {
		
		ClientMain client = new ClientMain();
		Socket soc=null;
		ObjectOutputStream objOutput=null;
		ObjectInputStream objInput=null;
		try {
			soc = new Socket(client.serverHost, client.serverPort);
			System.out.println("Connected with server!");
			//doing write or read request
			objOutput = new ObjectOutputStream(soc.getOutputStream());
			objInput = new ObjectInputStream(soc.getInputStream());

			
			client.output = objOutput;
			client.input = objInput;
			//start listener
			client.listener = new ClientListener(objInput,client);
			client.startClientServer();
	
			client.startConsole();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				objOutput.close();
				objInput.close();
				soc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
