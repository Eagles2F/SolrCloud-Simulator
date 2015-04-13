package edu.cmu.ece845.utility;

public enum MessageType {
	
        nodeInitilization,  //initialization message sent from loadBalancer to the nodes
        heartbeat, // the heart beat message from node to loadBalancer
   
}
