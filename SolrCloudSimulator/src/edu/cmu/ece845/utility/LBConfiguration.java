package edu.cmu.ece845.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/*
 * This class offers the configuration parameters loaded from the configuration file.
 */
public class LBConfiguration extends ConfigurationBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 988346665358020534L;
	
	
	public LBConfiguration(){
	    prop = new Properties();
        try {
            input = new FileInputStream("LBConfig.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public int getNodeHiring_port() {
		return Integer.valueOf(prop.getProperty("nodeHiringPort"));
	}
	
	public int getClientListeningPort(){
		return Integer.valueOf(prop.getProperty("clientServingPort"));
	}
	
	public boolean getIsQuorum(){
		return Boolean.valueOf(prop.getProperty("is_quorum"));
	}
	
	public  Properties getProperties(){
	    return prop;
	}
}
