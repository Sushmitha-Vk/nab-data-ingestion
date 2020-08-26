package com.nab.eda.syndatagen.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface GenerateData {
	
	public interface RandomDataConfig {
		String getStartDate();
		String getEndDate();
		//String getPropertyValue();
		String getPropertyName();
		String getPropertyQName();
		String getDataType();
		int getMinLength();
		int getMaxLength();
		boolean isDuplicated();
		String getValueType();
		String getPrefix();
		String getPostfix();
		int getLowValue();
		int getHighValue();
		List<String> getSrcData();
	}
	
	public String getCustomRandomValue(RandomDataConfig conf);
	public String getCustomRandomValue(String startDate, String endDate, List<String> wordList, String propertyName, String dataType, int minLength, int maxLength, boolean isDuplicated, String valueType, String prefix, String postfix, int lowValue, int highValue);
	public String getCustomRandomValue(String startDate, String endDate, String propertyValue, String propertyName, String dataType, int minLength, int maxLength, boolean isDuplicated, String valueType, String prefix, String postfix, int lowValue, int highValue);
	public String createCustomRandomString(String propertyID, List<String> wordList, int valueSize, String prefix, String postfix);
	public String createRandomString(int valueLength, String prefix, String postfix);
	public String createDateValue(String startDate, String endDate);
	public Map<String, Integer> getYMDValuesFromString(String dateString);
	public int createRandomNum(int valueLength);
	public int getRandomNumberInRange(int min, int max);
	public String createAccountNumber(int totalLength, int minLength, int maxLength, String prefix);
	public List<String> getRequiredList(String propertyName);
	public String getContentUrl(Properties prop);
	public String getContentUrl(org.json.JSONObject config);
}
