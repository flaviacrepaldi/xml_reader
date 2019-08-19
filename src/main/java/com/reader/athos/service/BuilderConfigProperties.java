package com.reader.athos.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.reader.athos.model.Config;

public class BuilderConfigProperties {

	private static final String DEFAULT_CONFIG_PATH = "C:\\Users\\phelipe.galiotti\\Desktop\\teste\\config\\config.properties";
	private static final String DEFAULT_DESTINATION_PATH = "C:\\Users\\phelipe.galiotti\\Desktop\\teste\\LIDOS";
	private static final String DEFAULT_TARGET_PATH = "C:\\Users\\phelipe.galiotti\\Desktop\\teste\\BASE XMLs";
	private static final String DEFAULT_FOLDER_PATH = "C:\\Users\\phelipe.galiotti\\Desktop\\teste";

	public static Config execute() {
		Config config = new Config();
		config.setTargetReportsPath(DEFAULT_TARGET_PATH);
		config.setFolderPath(DEFAULT_FOLDER_PATH);
		config.setDestinationPath(DEFAULT_DESTINATION_PATH);
		
		try (InputStream input = new FileInputStream(DEFAULT_CONFIG_PATH)) {
			Properties prop = new Properties();

			// load a properties file
			prop.load(input);
			config.setTargetReportsPath(prop.getProperty("target.reports.path"));
			config.setFolderPath(prop.getProperty("folder.path"));
			config.setDestinationPath(prop.getProperty("destination.reports.path"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return config;
	}

}
