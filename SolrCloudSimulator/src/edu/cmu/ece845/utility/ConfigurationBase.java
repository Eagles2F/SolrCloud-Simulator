package edu.cmu.ece845.utility;

import java.io.InputStream;
import java.util.Properties;

public abstract class ConfigurationBase{
    protected Properties prop;
    protected InputStream input;
    public abstract Properties getProperties();
    
    
}