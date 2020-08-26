package com.nab.eda.syndatagen.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONTokener;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nab.eda.syndatagen.constants.TemplateConstants;
import com.nab.eda.syndatagen.service.GenerateData;
import com.nab.eda.syndatagen.service.PrepareJSON;
import com.nab.eda.syndatagen.service.impl.GenerateDataImpl;
import com.nab.eda.syndatagen.service.impl.PrepareJSONImpl;
import com.nab.eda.syndatagen.utils.LogUtils;
import com.nab.eda.syndatagen.utils.PropertyUtils;

public class DataGenController {
	
	static long startTime = 0;
	static GenerateData generateData= new GenerateDataImpl();
	static PropertyUtils propertyUtils = new PropertyUtils();
	static Properties prop=null;
	static org.json.JSONObject config=null;
	static int jsonarray4eachFile;
	static int noOfJsonFiles;
	static int folderNumber;
	static String jsonFilePath;
	static String folderRootPath;
	static PrepareJSON prepareJSON = new PrepareJSONImpl();
	static LogUtils logUtils = new LogUtils();
	static StringBuffer contentForLogStringBuffer = new StringBuffer();
	
	public static void main(String[] args) throws ParseException, IOException {
		
		//generateFiles();
		
		
		
	}
	
	/**
	 *  This method starts the actual process of Synthetic Data Genration.
	 * @param propFilename 
	 *  
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void generateFiles(String propFilename) throws ParseException, IOException{
		
		
		if (propFilename.endsWith(".json")) {
			config = new org.json.JSONObject(new JSONTokener(new FileReader(propFilename)));
			jsonarray4eachFile = config.getInt("jsonarray4eachFile");
			noOfJsonFiles = config.getInt("noOfJsonFiles");
			jsonFilePath = config.getString("outputFilePath");
			folderRootPath = config.getString("folderRootPath");
			folderNumber = config.optInt("FolderNumber",0);			
		} else {
			prop = propertyUtils.getProps(propFilename);
			jsonarray4eachFile = Integer.parseInt((String)prop.get("jsonarray4eachFile"));
			noOfJsonFiles = Integer.parseInt((String)prop.get("noOfJsonFiles"));
			jsonFilePath = prop.get("outputFilePath").toString();
			folderRootPath = prop.get("folderRootPath").toString();
			folderNumber=Integer.parseInt((String)prop.getProperty("FolderNumber"));			
		}

		File directory = new File(jsonFilePath);
		if (! directory.exists()){
			directory.mkdirs();
		}

		System.out.println("JSON File Generation has Started Config="+propFilename);
		contentForLogStringBuffer.append("JSON File Generation has Started\n");
		System.out.println("=================================");
		
		startTime = System.nanoTime();
		System.out.println("Start time: "+"0 sec @ " );
		contentForLogStringBuffer.append("Start time: "+"0 sec @ "+startTime+"\n");
		
		createJSONFiles();
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		
		String endTimeString = (float)totalTime/60/60/60/60/60+"sec"+" or "+(float)totalTime/60/60/60/60/60/60+"min";
		
		System.out.println("=================================");
		System.out.println("JSON File Generation has Ended");
		System.out.println("Total time taken to genrate JSON File: "+endTimeString);
	
		
		contentForLogStringBuffer.append("JSON File Generation has Ended\n");
		contentForLogStringBuffer.append("Total time taken to genrate entire JSON data: "+endTimeString+"\n");
		contentForLogStringBuffer.append("Total no. of files generated: "+noOfJsonFiles+"\n");
		contentForLogStringBuffer.append("JSON Array for each JSON file: "+jsonarray4eachFile+"\n");
		contentForLogStringBuffer.append("Total JSON Data for entire Data Generation: "+jsonarray4eachFile*noOfJsonFiles+"\n");
		LogUtils.generateLogFile(contentForLogStringBuffer.toString());

		
	}
	
	
	
	public static void createJSONFiles() throws ParseException, IOException {
		

		for(int j = 1; j<= noOfJsonFiles; j++) {

			StringBuilder jsonStringBuilder = new StringBuilder(); 
			jsonStringBuilder.append(TemplateConstants.CONST_START_SQUARE_BRACKET);
			jsonStringBuilder = prepareJsonForEachFile(j, jsonStringBuilder);
			
			jsonStringBuilder.append(TemplateConstants.CONST_END_SQUARE_BRACKET);
//			FileWriter file = new FileWriter(jsonFilePath+"object_mapper_batch"+j+".json");
			
			//edited by Sagar to include folder number from properties into file name
			FileWriter file = new FileWriter(jsonFilePath+"object_mapper_batch"+Integer.toString(folderNumber+j)+".json");
			file.write(jsonStringBuilder.toString());
			jsonStringBuilder = null;
			System.out.println("Successfully Copied JSON Data to File: "+"object_mapper_bacth_"+Integer.toString(folderNumber+j)+".json");
			file.close();	
		}
	
	}
	
	public static StringBuilder prepareJsonForEachFile(int fileNo, StringBuilder jsonStringBuilder) throws ParseException{
		String prefix= "";
		JSONObject jsonObj = null;
		String prettyJson = null; 
		for(int i = 0; i< jsonarray4eachFile; i++) {

			if (config != null) {
				jsonObj = prepareJSON.prepareJSON(fileNo, config, i, jsonarray4eachFile, folderRootPath);
			} else {
				jsonObj = prepareJSON.prepareJSON(fileNo, prop, i, jsonarray4eachFile, folderRootPath);
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			prettyJson = gson.toJson(jsonObj);
			jsonStringBuilder.append(prefix);
			prefix = ",";
		    jsonStringBuilder.append(prettyJson);
		   
		    jsonObj = null;
		    // System.out.println("JSON String prepared for file "+i);
		}

		return jsonStringBuilder;
		
	}
	
	
	


}
