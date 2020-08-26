package com.nab.eda.syndatagen.service.impl;

import java.util.Enumeration;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nab.eda.syndatagen.service.GenerateData;
import com.nab.eda.syndatagen.service.GenerateData.RandomDataConfig;
import com.nab.eda.syndatagen.service.PrepareJSON;
import com.nab.eda.syndatagen.utils.PropertyUtils;
import com.nab.eda.syndatagen.utils.RandomDataConfigImpl;

public class PrepareJSONImpl implements PrepareJSON{

	static GenerateData generateData = new GenerateDataImpl();
	
	@SuppressWarnings("unchecked")
	public JSONObject prepareJSON(int fileNo, Properties prop, int density,  int jsonarray4eachFile, String folderRootPath) throws ParseException {
		JSONObject JsonObj = new JSONObject();
		JSONObject propJsonObj = new JSONObject();
		JSONParser parser = new JSONParser();
		
		  Enumeration<?> e = prop.propertyNames();
          while (e.hasMoreElements()) {
              String propertyID = (String) e.nextElement();
              String value = prop.getProperty(propertyID);
              
              	int noOfJsonFiles = Integer.parseInt((String)prop.get("noOfJsonFiles"));
              	
              
        		
              if(PropertyUtils.isJSONValid(value)) {
            		JSONObject propertyValueJson = (JSONObject) parser.parse(prop.getProperty(propertyID));
            		
            		int propertyDensity = Integer.parseInt((String)propertyValueJson.get("density"))/noOfJsonFiles;
            		int propertyUniq =  Integer.parseInt((String)propertyValueJson.get("uqratio"))/noOfJsonFiles;
            		String dataType = (String)propertyValueJson.get("type");
            		int highValue = 0;
            		int lowValue = 0; 
            		try {
            			if(dataType.equals("Float")) {
    						highValue = Math.round(Float.parseFloat((String)propertyValueJson.get("high")));
                			lowValue = Math.round(Float.parseFloat((String)propertyValueJson.get("low")));
    					} else {
            				highValue = Integer.parseInt((String)propertyValueJson.get("high"));
                			lowValue = Integer.parseInt((String)propertyValueJson.get("low"));
            			}
            			
					} catch (NumberFormatException e2) {
						//System.out.println("highValue is not available");
					}
            		

            		if(propertyUniq==0) {
            			propertyUniq=1;
            		}
            		
           		String valueType = (String)propertyValueJson.get("valuetype");
            		String nsprefix = (String)propertyValueJson.get("nsprefix");
            		int maxLength = Integer.parseInt((String)propertyValueJson.get("length"));
            	
            		String srcData=null;
          		
            	  
            	  if(! valueType.equalsIgnoreCase("Random")) {
            		  srcData = (String)prop.getProperty("src_data_"+propertyID);
            	  }

      			if(density < propertyDensity) {
    				if(density <= propertyUniq) {
    					propJsonObj.put(nsprefix+propertyID, generateData.getCustomRandomValue(null, null, srcData, propertyID, dataType, 5, maxLength, false, valueType, "", "", lowValue, highValue));
    				} else {
    					propJsonObj.put(nsprefix+propertyID, generateData.getCustomRandomValue(null, null, srcData, propertyID, dataType, 5, maxLength, true, valueType, "", "", lowValue, highValue));
    				}
    			
    			}
            	  
              } else {
            	  
            	  	JsonObj.put("type", prop.get("type"));
          			int postFix4Batch = (density+(fileNo-1)*jsonarray4eachFile)/1000;
//          			JsonObj.put("path", folderRootPath+"Folder"+Integer.toString(postFix4Batch));
          			
          			//Modified by Sagar: Pickup foldernumber from config
          			int folderNumber=Integer.parseInt((String)prop.getProperty("FolderNumber"));
          			JsonObj.put("path", folderRootPath+"Folder"+Integer.toString(folderNumber+postFix4Batch));
          			
          			JsonObj.put("properties", propJsonObj);
          			JSONObject contentURLJsonObj = new JSONObject();
          			contentURLJsonObj.put("cm:content", generateData.getContentUrl(prop));
          			JsonObj.put("contentUrls", contentURLJsonObj);
            	  
              }
          }
		
		
		
		
		return JsonObj;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject prepareJSON(int fileNo, org.json.JSONObject config, int density,  int jsonarray4eachFile, String folderRootPath) throws ParseException {
		JSONObject JsonObj = new JSONObject();
		JSONObject propJsonObj = new JSONObject();
		JSONParser parser = new JSONParser();
		
          for (String propertyID : config.getJSONObject("properties").keySet()) {
        	  org.json.JSONObject obj = config.getJSONObject("properties").getJSONObject(propertyID);
              int noOfJsonFiles = config.getInt("noOfJsonFiles");
      		  int propertyDensity = obj.getInt("density")/noOfJsonFiles;
    		  if(density < propertyDensity) {
        	  
        	      RandomDataConfig dataConfig = new RandomDataConfigImpl(propertyID,config,density);
        	      propJsonObj.put(dataConfig.getPropertyQName(), generateData.getCustomRandomValue(dataConfig));
    			
    			}
              propJsonObj.put("cm:isContentIndexed", false);  
		  }
		  
		  
  	  	JsonObj.put("type", config.getString("type"));
		int postFix4Batch = (density+(fileNo-1)*jsonarray4eachFile)/1000;
//		JsonObj.put("path", folderRootPath+"Folder"+Integer.toString(postFix4Batch));
			
		//Modified by Sagar: Pickup foldernumber from config
		int folderNumber=config.optInt("FolderNumber",0);
		JsonObj.put("path", folderRootPath+"Folder"+Integer.toString(folderNumber+postFix4Batch));
			
		JsonObj.put("properties", propJsonObj);
		JSONObject contentURLJsonObj = new JSONObject();
		contentURLJsonObj.put("cm:content", generateData.getContentUrl(config));
		JsonObj.put("contentUrls", contentURLJsonObj);
		
		
		return JsonObj;
	}

}
