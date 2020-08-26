# Synthetic Data Generator

## Overall Operation

This is run as a standalone JAR file an option config properties file as the initial argument


Even though this is built using the Spring Boot Application Archetype, it is not a spring boot app.  It does not have any REST Controllers or listen on a port

#### To Build....

```
mvn clean install -DskipTests=true
```

#### Sample run using properties file

```
java -jar target/Synthetic_Data_Generation-0.0.1-SNAPSHOT.jar configs/rich-test.properties
```

#### Sample run using JSON file

```
java -jar target/Synthetic_Data_Generation-0.0.1-SNAPSHOT.jar configs/rich-test.json
```

This was run from the top folder -- beware that log files will be added to that folder

Note: **_The properties file is being left in for backwards compatibility, but it is depracated.  We will focus on the JSON version and the configuration will be more intuitive._**

## Configuring

Here is a sample config

```
{
	"jsonarray4eachFile": 400,
	"noOfJsonFiles": 50,
	"folderRootPath": "/eReport/Batch1/",
	"FolderNumber": 100,
	"outputFilePath": "/tmp/run7/",
	"type": "acme:otherType",
	"contentUrls": [
		"store://2019/5/14/14/20/d7916a1b-3af9-4486-a8ab-d8094dbfa10f.bin",
		"store://2019/5/14/14/20/006430ed-36b1-4ede-bcb8-681d1c466091.bin",
		"store://2019/5/14/14/20/a287359d-01c3-4c70-80ba-11321d9d3a5e.bin",
		"store://2019/5/14/14/20/4c281b9e-8a23-49e0-8a9d-161276a3a5b4.bin",
		"store://2019/5/14/14/20/77848898-a504-4c61-b01d-aa44468af983.bin"
	],
	"properties": {
		"name": {
			"type": "String",
			"length": "120",
			"mandatory": "Y",
			"density": "476131922",
			"uqratio": "476131922",
			"valuetype": "ItIsName",
			"nsprefix": "cm",
			"srcData": [
				"accounts",
				"fund",
				"invest",
				"invoice",
				"premium",
				"profit",
				"return",
				"revenue"
			]
		},
		"tempi": {
			"type": "String",
			"length": "20",
			"mandatory": "N",
			"density": "476131922",
			"uqratio": "1500",
			"valuetype": "Custom",
			"nsprefix": "acme",
			"srcData": [
				"134_Liverpool_Street",
				"255_George_St_Business_Centre",
				"345_George_Street",
				"75_Elizabeth_Street_BBC",
				"CT_2067_Pitt__Hunter_St_Invest_Ctr",
				"CT_2077_George_Street_FSC_PBRs",
				"Property_Finance_NSW",
				"Senior_Partner_NSW_South_1",
				"Senior_Partner_NSW_South_4",
				"Sutherland_Shire_Partnership",
				"Toyota_Financial_Services"
			]
		},
		"busDate": {
			"type": "Date",
			"length": "10",
			"mandatory": "Y",
			"density": "333292345",
			"uqratio": "9125",
			"low": "1998-01-01",
			"high": "2013-01-01",
			"valuetype": "Random",
			"nsprefix": "acme"
		},
		"tempj": {
			"type": "Integer",
			"length": "20",
			"mandatory": "N",
			"density": "476131922",
			"uqratio": "1500",
			"low": "333451",
			"high": "835625",
			"valuetype": "Random",
			"nsprefix": "acme"
		},
		"tempo": {
			"type": "String",
			"length": "50",
			"mandatory": "N",
			"density": "476131922",
			"uqratio": "5200",
			"valuetype": "Random",
			"nsprefix": "acme"
		},
		"tempd": {
			"type": "String",
			"length": "50",
			"mandatory": "N",
			"density": "476131922",
			"uqratio": "1100000",
			"low": "000000500",
			"high": "AC010001000",
			"valuetype": "AccNo",
			"nsprefix": "acme"
		},
		"tc": {
			"type": "Integer",
			"length": "2",
			"mandatory": "N",
			"density": "476131922",
			"uqratio": "40",
			"valuetype": "Random",
			"low": "1",
			"high": "99",
			"nsprefix": "acme"
		},
		"docType": {
			"type": "String",
			"length": "150",
			"mandatory": "N",
			"density": "476131922",
			"uqratio": "2340",
			"valuetype": "FromList",
			"nsprefix": "acme",
			"srcData": [
				"AFP",
				"PDF",
				"TXT"
			]
		},
		"amount": {
			"type": "Float",
			"length": "7",
			"mandatory": "Y",
			"density": "238065961",
			"uqratio": "166646172",
			"low": "99.01",
			"high": "1999999.99",
			"valuetype": "Random",
			"nsprefix": "acme"
		}
	}
}
```

### List of key config Properties

* `jsonarray4eachFile`: The number of objects in each manifest
* `noOfJsonFiles`: The number of JSON files to create
* `folderRootPath`: The folder that these documents will live in in the repositoruy
* `FolderNumber`: The starting Folder format like `/Folder100`
* `outputFilePath`: The local file system folder to put the manifests in. This folder must exist.
* `type`: The prefixed QName of the type of object in the manifest
* `properties`: A map of the metadata properties and the parameters for generating them.  the key is the localname of the property (this means that having two metadata fields with the same local name and different name spaces would not be supported)

### List of configuration paramaters for each property

* `type`: The data type of the property. Valid values are:
    * `String`
	* `Integer`
	* `Float	`
	* `Date`
* `length`: The length of the field (Max length?)
* `mandatory`: Valid Values Y/N
* `density`: It is basically the percenatage of its presence. Say if the density is 70 and total documents are 100. Then The value is present in 70 documents and rest of the 30 values are empty.
* `uqratio`: It is basically the uniqueness ratio. . Say if the uqratio is 70. Then The value is present in 70 documents are unique.
* `valuetype`: The type of value being generated. Valid Values are:
    * `AccNo`: Account Number with Prefix AC+00..+random number. Zeroes after AC depends on length of random number. Total length should be 12 I guess.
	* `Custom`: List of words to be passed as property  and new string is formed picking some random words from it and appending with delimiter as “_”.
	* `FromList`: There would be an array of String and one value from the array is randomly picked.
	* `ItIsName`: Used for names -- Same as Custom
	* `Random`: Unique String value is generated with given length. UUID is used here.
* `low`: Low value for Integers and Floats (currently ignored for Dates)
* `high`:  High value for Integers and Floats (currently ignored for Dates)
* `nsprefix`: namespace prefix to be added to the property (note that the properties file needed the trailing colon, we do not need that in the JSON config)
* `srcData`: An array of strings used for "greeking" and selecting random values from a bounded list of valid values

## Project Status

### Updates Made

* Removed Hard Coded Paths that were preventing the application from running
* Externalized Config
* Added this README
* Removed hard coded set of lists to allow for new properties
* Added JSON config to make configs more readable

The updates have not added any additional data generation functionality.  It makes the current functionality available to be used to generate manifests for different requirements.

### Questions and Answers

#### Is there a reason that the high and low dates are ignored?

* I think they are used as variables startDate and endDate

 