package edu.cmu.ece845.utility;

import java.io.Serializable;

public class Message implements Serializable {

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
	private DataItem data;
	private int seqNum; 
	
	//fields for nodeInitialization
	private int assignedID;
	
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

	public DataItem getData() {
		return data;
	}

	public void setData(DataItem data) {
		this.data = data;
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
	
}
