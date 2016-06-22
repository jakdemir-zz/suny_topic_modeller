package edu.albany.cs.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigManager {

	private static Configuration config;
	private static ConfigManager configManager;

	//XXX make this synchronized for thread safe-
	public static ConfigManager getInstance() {
		if (configManager == null) {
			configManager = new ConfigManager();
		} else {
			return configManager;
		}
		return configManager;
	}

	public ConfigManager(String pathToConfigFile) {
		try {
			config = new PropertiesConfiguration(pathToConfigFile);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to load configration");
			e.printStackTrace();
		}
	}

	public ConfigManager() {
		try {
			config = new PropertiesConfiguration("resources/config.properties");
		} catch (ConfigurationException e) {
			System.out.println("Unable to load configration");
			e.printStackTrace();
		}

	}

	public String getSolrHostname() {
		return config.getString("solrHostname");
	}

	public String getSolrPort() {
		return config.getString("solrPort");
	}

	public String getSolrColl() {
		return config.getString("solrColl");
	}

	public String getSolrVersion() {
		return config.getString("solrVersion");
	}

	public String getNumberOfTopics() {
		return config.getString("numberOfTopics");
	}

	public String getMysqlHostname() {
		return config.getString("mysqlHostname");
	}

	public String getSchema() {
		return config.getString("schema");
	}

	public String getUsername() {
		return config.getString("username");
	}

	public String getPassword() {
		return config.getString("password");
	}

	public String getViewId() {
		return config.getString("viewId");
	}

	public String getNoteIds() {
		return config.getString("noteIds");
	}

	public String getNumberOfIterations() {
		return config.getString("numberOfIterations");
	}
	
	public String getNoteTable() {
		return config.getString("noteTable");
	}
	public String getDataDirectory() {
		return config.getString("dataDirectory");
	}
	public String getTopicFilePrefix() {
		return config.getString("topicFilePrefix");
	}
	
	public static void main(String[] args) {
		System.out.println("MYSQL HOSTNAME : "+ConfigManager.getInstance().getMysqlHostname());
	}
	
	public static void main1(String[] args) {
		ConfigManager testConfig = new ConfigManager("C:\\Users\\alptilev\\Desktop\\dev\\topic_modeller\\resources\\config.properties");
		String solrHostName = testConfig.config.getString("solrHostname");
		System.out.println("test output " + solrHostName);

		// test the configuration is working
	}

	public String getSolrHitCount() {
		return config.getString("solrHitCount");
	}
}
