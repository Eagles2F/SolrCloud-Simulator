package edu.cmu.ece845.utility;

import java.io.Serializable;

public class DataItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8594726794900270717L;
	private String key;
	private String value;
	
	public DataItem(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "DataItem [key=" + key + ", value=" + value + "]";
	}
	
	
}
