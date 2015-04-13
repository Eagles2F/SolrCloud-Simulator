package edu.cmu.ece845.loadbalancer;

import java.util.concurrent.ConcurrentHashMap;

/*
 * @Author: Yifan Li
 * @Description: 
 * This class will maintain the status table of all the nodes in the system from the heart beat messages.
 * Whenever there is some change of states in the system, the manager will send the notifications
 * to the nodes. 
 * @Date: April 12, 2015
 */
public class NodeManager implements Runnable{
	
	public ConcurrentHashMap<Integer,Boolean> nodeStatusMap;
	public ConcurrentHashMap<Integer,Integer> nodePortMap; // we assume the port for each node won't change
	public int masterID;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
