package com.nab.eda.syndatagen.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nab.eda.syndatagen.service.GenerateData.RandomDataConfig;

public class RandomDataConfigImpl implements RandomDataConfig {
	
	List<String> _srcData = new ArrayList<String>();
	String propertyName;
	String nsprefix;
	boolean isUnique;
	int maxLength;
	String dataType;
	String valueType;
	int highValue;
	int lowValue; 
	
	public RandomDataConfigImpl(String propName,JSONObject config, int density) {
  	    JSONObject obj = config.getJSONObject("properties").getJSONObject(propName);
		// Can Combine srcDataString and srcData
	    if(obj.has("srcDataString")) {
		    _srcData = Arrays.asList(obj.getString("srcDataString").split(", "));
	    }
		if (obj.has("srcData")) {
			JSONArray arr =  obj.getJSONArray("srcData");
			for (int i = 0; i < arr.length() ; i++) {
				_srcData.add(arr.getString(i));
			}
		}
		this.propertyName = propName;
        int noOfJsonFiles = config.getInt("noOfJsonFiles");
		int propertyUniq =  obj.getInt("uqratio")/noOfJsonFiles;
		dataType = obj.getString("type");
		highValue = 0;
		lowValue = 0; 
		try {
			if(dataType.equals("Float")) {
				highValue = Math.round(obj.getFloat("high"));
    			lowValue = Math.round(obj.getFloat("low"));
			} else {
				highValue = obj.getInt("high");
    			lowValue = obj.getInt("low");
			}
			
		} catch (NumberFormatException | org.json.JSONException e2) {
			//System.out.println("highValue is not available");
		}
		

		if(propertyUniq==0) {
			propertyUniq=1;
		}
		
		valueType = obj.getString("valuetype");
		nsprefix = obj.getString("nsprefix");
		maxLength = obj.getInt("length");
		isUnique = density <= propertyUniq;
		
	}

	@Override
	public String getStartDate() {
		//Always Null
		return null;
	}

	@Override
	public String getEndDate() {
		//Always Null
		return null;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public String getPropertyQName() {
		return nsprefix + ":" + propertyName; 
	}

	@Override
	public String getDataType() {
		//TODO: Is String the Default Data Type?
		return dataType;
	}

	@Override
	public int getMinLength() {
		return 5;
	}

	@Override
	public int getMaxLength() {
		return maxLength;
	}

	@Override
	public boolean isDuplicated() {
		return !isUnique;
	}

	@Override
	public String getValueType() {
		return valueType;
	}

	@Override
	public String getPrefix() {
		return "";
	}

	@Override
	public String getPostfix() {
		return "";
	}

	@Override
	public int getLowValue() {
		return lowValue;
	}

	@Override
	public int getHighValue() {
		return highValue;
	}

	@Override
	public List<String> getSrcData() {
		return _srcData;
	}

}
