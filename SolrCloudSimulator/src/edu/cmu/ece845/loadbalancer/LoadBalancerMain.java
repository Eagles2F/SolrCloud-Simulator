package edu.cmu.ece845.loadbalancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Set;

import edu.cmu.ece845.utility.LBConfiguration;
import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;

/*
 * @Author: Yifan Li
 * @Description: This is the LoadBalancer class in the simulator. It will do the following things:
 *  1. Serve the Query Request from the client
 *  2. Serve the Index Request from the client
 *  3. Serve the Node Register from the nodes
 *  4. Maintain the states of all the nodes by heart beating messages
 *  5. Server the node recovery request
 * @Date: April 12, 2015
 */
public class LoadBalancerMain {

	private NodeHiringServer nodeHiringServer;
	private BufferedReader console;
	private boolean running;
	
	public LoadBalancerMain(LBConfiguration lb){
		//initialization
		nodeHiringServer = new NodeHiringServer(lb.getNodeHiring_port());
		console = new BufferedReader(new InputStreamReader(System.in));
		running = true;
	}
	
	private void startNodeHiringServer(){
        Thread t1 = new Thread(nodeHiringServer);
        t1.start();
	}
	
	public void startConsole(){
        System.out.println("This is SolrCloud simulator: LoadBalancer, type help for more information");
        
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
            	case "ls":
            		handleLS();
            	case "quit":
            		handleQuit();
            		break;
            	case "help":
            		handleHelp();
            		break;
            	case "writemaster":
				
				try {
					String key = console.readLine();
					String value = console.readLine();
            		handleWrite(key, value);
            		
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            		
            		break;
                default:
                    System.out.println(inputLine[0]+" is not a valid command");
            }
        }
	}

	private void handleWrite(String key, String value){
		Message msg = new Message(MessageType.writeData);
		msg.setKey(key);
		msg.setValue(value);
		try {
			this.nodeHiringServer.nodeListenerMap.get(this.nodeHiringServer.masterID).sendToNode(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleLS(){
		final Set<Entry<Integer, Boolean>> entries = this.nodeHiringServer.nodeStatusMap.entrySet();
		for(Entry<Integer, Boolean> entry : entries){
			System.out.println("nodeID:" + entry.getKey() + " is " + entry.getValue());
		}
	}
	// shut down the loadBalancer and notify others about this event.
	private void handleQuit(){
		
	}
	
	// print out the list of commands.
	private void handleHelp(){
		
	}
	
	public static void main(String[] args) {
		// configure
		LBConfiguration lbConf = new LBConfiguration();
		
		LoadBalancerMain lb = new LoadBalancerMain(lbConf);
		// start Query Server and Index Server
		
		// start NodeHiring Server
		lb.startNodeHiringServer();
		// start console
		lb.startConsole();
	}

}
