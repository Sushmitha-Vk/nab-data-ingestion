package com.nab.eda.syndatagen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PropertyUtils {

	static Properties prop = new Properties();
	
	public Properties getProps(String propFilename) {
		try {
			if (propFilename == null) {
				String propertiesPath = null;
				propertiesPath = getParentDirectoryFromJar();
				propFilename = propertiesPath + File.separator + "config.properties";
			}
			prop.load(new FileInputStream(propFilename));
			if (prop == null) {
				throw new NullPointerException("Cannot use null Properties List");
			}
		} catch (Exception e) {
			System.err.println("Exception occured while "+e.getMessage());
			e.printStackTrace();
			System.exit(255);
		}
		return prop;
	}
	
	
	
	
	public String getParentDirectoryFromJar() {
	    String dirtyPath = getClass().getResource("").toString();
	    String jarPath = dirtyPath.replaceAll("^.*file:/", ""); //removes file:/ and everything before it
	    jarPath = jarPath.replaceAll("jar!.*", "jar"); //removes everything after .jar, if .jar exists in dirtyPath
	    jarPath = jarPath.replaceAll("%20", " "); //necessary if path has spaces within
	    if (!jarPath.endsWith(".jar")) { // this is needed if you plan to run the app using Spring Tools Suit play button. 
	        jarPath = jarPath.replaceAll("/classes/.*", "/classes/");
	    }
	    String directoryPath = Paths.get(jarPath).getParent().toString(); //Paths - from java 8
	    return directoryPath;
	}
	
	
	
	
	
	
	
	
	
    public void retrieveAll(Properties prop) {
    	
    	
    	try {

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                System.out.println(isJSONValid(value)+"--------Key=" + key + value);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static boolean isJSONValid(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(jsonString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    
    
    
}
