package edu.cmu.ece845.utility;

public class DataItem {
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
