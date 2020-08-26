package com.nab.eda.syndatagen.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nab.eda.syndatagen.constants.TemplateConstants;
import com.nab.eda.syndatagen.service.GenerateData;


/**
 * 
 * @author P787553
 *
 */
public class GenerateDataImpl implements GenerateData{

	
	static Random finalRandom = new Random();
	static GenerateData generateData = new GenerateDataImpl();
	static Map<String,List<String>> listCache = new HashMap<String,List<String>>();
	static List<String> urls = null;
	

	static String tempDocFormat;
	
	/**
	 * 
	 * 
	 * valueType - Random || Custom
	 * dataType - StringVal || IntVal || DateVal || FloatVal || OtherVal
	 * propertyValue - List of all words - for example 'Branch Banking, Mobile Banking, ATM Channel of Banking, Phone Banking'
	 * 
	 * 
	 * @param propertyValue
	 * @param propertyName
	 * @param dataType
	 * @param maxLength
	 * @param isDuplicated
	 * @param valueType
	 * @param prefix
	 * @param postfix
	 * 
	 * @return custRanVal
	 */

	@Override
	public String getCustomRandomValue(RandomDataConfig conf) {
		return getCustomRandomValue(conf.getStartDate(), conf.getEndDate(), conf.getSrcData(), conf.getPropertyName(), conf.getDataType(), 
				conf.getMinLength(), conf.getMaxLength(), conf.isDuplicated(), conf.getValueType(), conf.getPrefix(), 
				conf.getPostfix(), conf.getLowValue(), conf.getHighValue());
	}
	
	@Override
	public String getCustomRandomValue(String startDate, String endDate, String propertyValue, String propertyName,
			String dataType, int minLength, int maxLength, boolean isDuplicated, String valueType, String prefix,
			String postfix, int lowValue, int highValue) {
		
		List<String> wordList = Arrays.asList(propertyValue.split(", "));
		return getCustomRandomValue(startDate, endDate, wordList, propertyName, dataType, minLength,maxLength, isDuplicated, valueType, prefix, postfix, lowValue, highValue);
	}
	
	
	@Override
	public String getCustomRandomValue(String startDate, String endDate, List<String> wordList, String propertyID, String dataType, int minLength, int maxLength, boolean isDuplicated, String valueType, String prefix, String postfix, int lowValue, int highValue) {
		String custRanVal = null;
		if(!isDuplicated) {

			switch (dataType) {
			  case "String":

			    if(valueType.equalsIgnoreCase("AccNo")) {
			    	custRanVal = createAccountNumber(12, 500, 10001000, "AC");
			    } else if(valueType.equalsIgnoreCase("Random")) { 
			    	custRanVal = createRandomString(maxLength, prefix, postfix);
			    } else if(valueType.equalsIgnoreCase("Custom")) {
			    	custRanVal = createCustomRandomString(propertyID, wordList, maxLength, prefix, postfix);
			    } else if(valueType.equalsIgnoreCase("FromList")) {
			    	custRanVal = pickValueFromList(wordList);
			    } else if(valueType.equalsIgnoreCase("ItIsName")) {
			    	custRanVal = createRandomString4Name(propertyID, wordList, maxLength, prefix, postfix);
			    }
			    
			  
			    
			    
			    
			    break;
			    
			  case "Integer":
			    
		    	 if(valueType.equalsIgnoreCase("Random")) {
		    		 	custRanVal = Integer.toString(createRandomNum(highValue));
				   } else if(valueType.equalsIgnoreCase("Custom")) {
					   	custRanVal = Integer.toString(getRandomNumberInRange(lowValue, highValue));
				   }
			    
			    
			    
			    
			    break;
			    
			  case "Date":
			    
			    custRanVal = createDateValue(startDate, endDate);
			    
			    
			    break;
			    
			  case "Float":
			    
			    custRanVal = Float.toString(getRandomFloatInRange(lowValue, highValue));
			    
			    
			    break;
			    
			  case "isOther":
			      System.out.println("Didn't you tell me about this new data type? - "+propertyID);
			    
			    
			    
			    
			    break;
			}
			handleDuplicateList(propertyID, "add", custRanVal);
			
		} else {
			
			custRanVal = handleDuplicateList(propertyID, "get", null);
			
		}
		
		return custRanVal;		
	}
	
	
	
	@Override
	public String createCustomRandomString(String propertyID, List<String> wordList, int valueSize, String prefix, String postfix) {
		String randomValue = null;

		//List<String> wordList = Arrays.asList(property.split(", "));
		randomValue = generateGreeking(10, valueSize, wordList);
		randomValue = randomValue.replace(" ", "_");
		if(propertyID.equalsIgnoreCase("name")) {
			postfix = "."+tempDocFormat.toLowerCase();
		}
		if(prefix!=null) {
			randomValue = prefix + randomValue;
		}
		if(postfix!=null) {
			randomValue = randomValue + postfix;
		}
		
		return randomValue;
	}
	
	public String createRandomString4Name(String propertyID, List<String> wordList, int valueSize, String prefix, String postfix) {
		String randomValue = null;

		//List<String> wordList = Arrays.asList(property.split(", "));
		randomValue = generateGreeking(10, valueSize, wordList);
		randomValue = randomValue.replace(" ", "_");
//		randomValue = randomValue + System.currentTimeMillis();
		randomValue = randomValue + System.nanoTime();
		if(propertyID.equalsIgnoreCase("name")) {
			postfix = "."+tempDocFormat.toLowerCase();
		}
		if(prefix!=null) {
			randomValue = prefix + randomValue;
		}
		if(postfix!=null) {
			randomValue = randomValue + postfix;
		}
		
		return randomValue;
	}
	
	
	/**
	 * This method creates random value using UUID library based on the length of the value given as input
	 * @param PropertyName
	 * @param valueSize
	 * @return
	 */
	@Override
	public String createRandomString(int valueLength, String prefix, String postfix) {
		
		String randomValue = null;
		randomValue = UUID.randomUUID().toString();
		if(valueLength>randomValue.length()) {
			valueLength = randomValue.length();
		}
			randomValue = randomValue.substring(0, valueLength-1);
		
		if(prefix!=null) {
			randomValue = prefix + randomValue;
		}
		if(postfix!=null) {
			randomValue = randomValue + postfix;
		}
		
		return randomValue;
	}
	
	@Override
	public int createRandomNum(int maxVal) {
		
		int randomNum;
		Random random = new Random();
		randomNum = random.nextInt(maxVal);
		
		return randomNum;
	}
	
	
	public static String generateGreeking(int min, int max, List<String> wordList) {
		List<String> words = new ArrayList<String>();
		int currMax = ThreadLocalRandom.current().nextInt(min, max+1);
		if (currMax == 0) return "";
		int currLen= -1; //account for no trailing space
		while(currLen < currMax) {
			String nextWord = pickRandomString(wordList);
			currLen += (nextWord.length() + 1);
			words.add(nextWord);
		}
		return String.join(" ", words).substring(0, Math.min(currLen, currMax));	
	}
	
	public static String pickRandomString(List<String> list) {
		return list.get(finalRandom.nextInt(list.size()));
	}


	public int getRandomNumberInRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public float getRandomFloatInRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return min + r.nextFloat() * (max - min);
	}
	
	
	@Override
	public String createDateValue(String startDate, String endDate) {
		
		String createdDate = null;

		if(startDate==null && endDate==null) {
			startDate = TemplateConstants.CONST_NAB_START_DATE;
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
			LocalDate localDate = LocalDate.now();
			endDate = dtf.format(localDate);
		}
		Map<String, Integer> startDateMap = getYMDValuesFromString(startDate);
		Map<String, Integer> endDateMap = getYMDValuesFromString(endDate);
		
		LocalDate startLocaleDate = LocalDate.of(startDateMap.get("year"), startDateMap.get("month"), startDateMap.get("day"));
		long start = startLocaleDate.toEpochDay();
		LocalDate endLocaleDate = LocalDate.of(endDateMap.get("year"), endDateMap.get("month"), endDateMap.get("day"));
		long end = endLocaleDate.toEpochDay();
		long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
		createdDate = LocalDate.ofEpochDay(randomEpochDay).toString();
		
		return createdDate;
	}
	
	
	
	public Map<String, Integer> getYMDValuesFromString(String dateString) {
		Map<String, Integer> dateMap = new HashMap<String, Integer>();
		String[] dateArray = dateString.split(TemplateConstants.CONST_HYPHEN);
		int year = Integer.parseInt(dateArray[0]);
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);
		dateMap.put("year", year);
		dateMap.put("month", month);
		dateMap.put("day", day);
		
		return dateMap;
	}
	
	
	
	
	public String createAccountNumber(int totalLength, int minLength, int maxLength, String prefix) {

		String squenceNo = null;
		try {
		
				int luckyint = getRandomNumberInRange(minLength, maxLength);
				int reqLength = (totalLength-prefix.length());
				squenceNo = String.valueOf(luckyint);
				squenceNo = String.format("%0"+reqLength+"d", luckyint);
				squenceNo = prefix + squenceNo;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return squenceNo;

	}
	
	
	
	public static String handleDuplicateList(String propertyId, String addOrGetValue, String propertyValue) {
		String retrievedValue = null;
		List<String> listRequired;
		
		if(addOrGetValue.equalsIgnoreCase("get")) {
			
			listRequired = generateData.getRequiredList(propertyId);
			int randomNum = generateData.getRandomNumberInRange(0, listRequired.size()-1);
			retrievedValue = listRequired.get(randomNum);
			
		} else if(addOrGetValue.equalsIgnoreCase("add")) {
			listRequired = generateData.getRequiredList(propertyId);
			listRequired.add(propertyValue);
		} else {
			System.out.println("Found incorrect value. Expected - 'add' or 'get'");
		}
		
		return retrievedValue;
	}
	
	public List<String> getRequiredList(String propertyName) {
		if (!listCache.containsKey(propertyName)) {
			listCache.put(propertyName, new ArrayList<String>());
		}
		return listCache.get(propertyName);
		

		
	}
	
	
	public String pickValueFromList(List<String> wordList) {
		String randomValue = null;
		Random random = new Random();
		//List<String> wordList = Arrays.asList(propertyValue.split(", "));
		int randomNum = random.nextInt(3);
		randomValue = wordList.get(randomNum);
		tempDocFormat = randomValue;
		return randomValue;
	}
	
	public String getContentUrl(Properties prop) {

		String contentUrl = "";
		try {
			contentUrl = pickValueFromListGeneric( Arrays.asList(prop.getProperty("src_data_contenturls").split(", ")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentUrl;
	}
	
	@Override
	public String getContentUrl(JSONObject config) {
		JSONArray uarr = config.getJSONArray("contentUrls");
		if (urls == null) {
			urls = new ArrayList<String>();
			for (int i =0; i < uarr.length(); i++) {
				urls.add(uarr.getString(i));
			}
		}
		String contentUrl = "";
		try {
			contentUrl = pickValueFromListGeneric(urls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentUrl;
	}
	
	public String pickValueFromListGeneric(List<String> wordList) {
		String randomValue = null;
		Random random = new Random();
		//List<String> wordList = Arrays.asList(propertyValue.split(", "));
		int randomNum = random.nextInt(3);
		randomValue = wordList.get(randomNum);
		return randomValue;
	}



	
}
