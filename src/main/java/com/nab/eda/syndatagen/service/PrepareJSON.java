package com.nab.eda.syndatagen.service;

import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface PrepareJSON {
	
	
	public JSONObject prepareJSON(int fileNo, Properties prop, int density, int jsonarray4eachFile, String folderRootPath) throws ParseException;
	public JSONObject prepareJSON(int fileNo, org.json.JSONObject config, int density, int jsonarray4eachFile, String folderRootPath) throws ParseException;
	

}
