package com.reader.athos.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.reader.athos.model.Config;

public class BuilderConfigProperties {

	public static Config execute() {
		Config config = new Config();
		config.setTargetReportsPath("C:\\Users\\phelipe.galiotti\\Desktop\\teste\\BASE XMLs");
		config.setFolderPath("C:\\Users\\phelipe.galiotti\\Desktop\\teste");
		
		try (InputStream input = new FileInputStream("C:\\Users\\phelipe.galiotti\\Desktop\\teste\\config\\config.properties")) {
			Properties prop = new Properties();

			// load a properties file
			prop.load(input);
			config.setTargetReportsPath(prop.getProperty("target.reports.path"));
			config.setFolderPath(prop.getProperty("folder.path"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return config;
	}

}
