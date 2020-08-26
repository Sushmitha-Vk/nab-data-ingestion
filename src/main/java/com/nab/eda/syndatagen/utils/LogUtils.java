package com.nab.eda.syndatagen.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogUtils {
	
	
	
	public static void generateLogFile(String content) {
		
		try {
			BufferedWriter bufferedWriter = null;
			FileWriter fileWriter = new FileWriter("Syn_Data_eReport_"+System.nanoTime()+".log");
			bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(content);
			bufferedWriter.close();
		} catch (Exception e) {
			System.out.println("Exception occured while writing log file"+e.getMessage());
		}

	}
	
	
	
	public static BufferedWriter createLogFile() {
		BufferedWriter bufferedWriter = null;
		try {
			
			FileWriter fileWriter = new FileWriter("Syn_Data_eReport_"+System.nanoTime()+".log");
			bufferedWriter = new BufferedWriter(fileWriter);
			
		} catch (IOException ioex) {
			System.out.println("Exception occured while creating log File "+ioex.getMessage());
			ioex.printStackTrace();
		}
		
		return bufferedWriter;
		
	}

	public static void writeContent2LogFile(BufferedWriter bufferedWriter1, String contentStrings) {
		try {
			bufferedWriter1.write(contentStrings);
		} catch (IOException e) {
			System.out.println("Exception occured while writnig content to log File "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
}
