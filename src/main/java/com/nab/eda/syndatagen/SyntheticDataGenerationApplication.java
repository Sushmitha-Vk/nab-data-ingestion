package com.nab.eda.syndatagen;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nab.eda.syndatagen.controller.DataGenController;

@SpringBootApplication
public class SyntheticDataGenerationApplication {

	public static void main(String[] args) {
		//SpringApplication.run(SyntheticDataGenerationApplication.class, args);
		String propFilename = null;
		if (args.length > 0) {
			propFilename = args[0];
		}
		generateSynData(propFilename);
	}
	
	/**
	 * This method is the starting point of generating the synthetic data. This is called from main method
	 */
	public static void generateSynData(String propFilename) {
		try {
			DataGenController.generateFiles(propFilename);
		} catch (ParseException e) {
			System.out.println(" Parsing issues from the Controller "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	
	
}
