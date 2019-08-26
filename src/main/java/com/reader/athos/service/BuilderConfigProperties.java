package com.reader.athos.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.reader.athos.model.Config;

public class BuilderConfigProperties {
	private final static Logger logger = Logger.getLogger(BuilderConfigProperties.class);
	private final static String DEFAULT_PATTERN_TO_PARSE = "yyyy-MM-dd'T'HH:mm:ssX";

	private static final String DEFAULT_CONFIG_PATH = "C:\\xml_reader\\config\\config.properties";
	private static final String DEFAULT_DESTINATION_PATH = "C:\\xml_reader\\LIDOS";
	private static final String DEFAULT_TARGET_PATH = "C:\\xml_reader\\PENDENTES";
	private static final String DEFAULT_FOLDER_PATH = "C:\\xml_reader\\";

	public static Config execute() throws ParseException {
		Config config = new Config();
		config.setTargetReportsPath(DEFAULT_TARGET_PATH);
		config.setFolderPath(DEFAULT_FOLDER_PATH);
		config.setDestinationPath(DEFAULT_DESTINATION_PATH);

		try (InputStream input = new FileInputStream(DEFAULT_CONFIG_PATH)) {
			Properties prop = new Properties();
			prop.load(input);

			config.setTargetReportsPath(prop.getProperty("target.reports.path"));
			config.setFolderPath(prop.getProperty("folder.path"));
			config.setDestinationPath(prop.getProperty("destination.reports.path"));

			enableUse(prop);
		} catch (IOException ex) {
			logger.error("Falha ao recuperar arquivo de configuracao: ", ex);
			ex.printStackTrace();
		}
		return config;
	}

	private static void enableUse(Properties prop) throws ParseException {
		if (Objects.isNull(prop.getProperty("enable.client"))) {
			SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN_TO_PARSE);
			Date futureDate = formatter.parse("2019-10-12T09:00:00-00:00");

			Calendar calendarFutureDate = Calendar.getInstance();
			calendarFutureDate.setTime(futureDate);

			if (Calendar.getInstance().compareTo(calendarFutureDate) >= 0) {
				logger.error("Falha ao executar a aplicacao. Por favor, contate o administrador");
				throw new RuntimeException();
			}
		}
	}

}
