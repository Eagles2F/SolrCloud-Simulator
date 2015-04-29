package edu.cmu.ece845.node;

import java.io.IOException;
import java.util.Map;

import edu.cmu.ece845.utility.Message;
import edu.cmu.ece845.utility.MessageType;

public class LeaderAckManager implements Runnable {
	
	private NodeMain nodeMain;
 
	
	
	public LeaderAckManager(NodeMain nm) {
		this.nodeMain = nm;
	} 
	
	@Override
	public void run() {
		
		while (true) {
			
			for (Map.Entry<Integer, Integer> entry : nodeMain.ackMetaData.entrySet()) {
			    Integer key = entry.getKey();
			    Integer value = entry.getValue();
			    
			    // if it has been ACKed by atleast 1, then remove from hashMap and send ack.
			    if (value > 0 ) {
			    	synchronized (nodeMain.ackMetaData) {
			    		nodeMain.ackMetaData.remove(key);
				    	Message m = new Message(MessageType.writeAck);
				    	m.setSeqNum(key);
				    	try {
				    	nodeMain.outstream.writeObject(m);
				    	} catch (IOException e) {
				    		e.printStackTrace();
				    	}
				    
				    	nodeMain.alreadySentAck.add(key);
					}
			    	
			    }

			}
		}
	}

}
