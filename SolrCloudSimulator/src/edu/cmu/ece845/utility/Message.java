package edu.cmu.ece845.utility;

import java.io.Serializable;

public class Message implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8708278714887971382L;
	
	MessageType messageType;
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	private String source;
	private String dest;
	private int seqNum; 
	
	//fields for nodeInitialization
	private int assignedID;
	private int leaderID;
	private int leaderPort;
	private String leaderIP;
	private Boolean is_new; //true - if the node is a newbie.
	
	// dataitem
	private String key;
	private String value;
	
	private String dataString;//using in the node sync process
		
	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	public Boolean getIs_new() {
		return is_new;
	}

	public void setIs_new(Boolean is_new) {
		this.is_new = is_new;
	}
	
	public Message(MessageType type) {
		messageType = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public int getAssignedID() {
		return assignedID;
	}

	public void setAssignedID(int assignedID) {
		this.assignedID = assignedID;
	}

	public int getLeaderPort() {
		return leaderPort;
	}

	public void setLeaderPort(int leaderPort) {
		this.leaderPort = leaderPort;
	}

	public int getLeaderID() {
		return leaderID;
	}

	public void setLeaderID(int leaderID) {
		this.leaderID = leaderID;
	}

	public String getLeaderIP() {
		return leaderIP;
	}

	public void setLeaderIP(String leaderIP) {
		this.leaderIP = leaderIP;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Message [messageType=" + messageType + ", source=" + source
				+ ", dest=" + dest +  ", seqNum=" + seqNum
				+ ", assignedID=" + assignedID + ", leaderID=" + leaderID
				+ ", leaderPort=" + leaderPort + ", leaderIP=" + leaderIP + "]";
	}

	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
}
