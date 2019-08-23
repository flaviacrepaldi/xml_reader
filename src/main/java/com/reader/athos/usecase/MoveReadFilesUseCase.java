package com.reader.athos.usecase;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.reader.athos.model.Config;
import com.reader.athos.service.BuilderConfigProperties;

public class MoveReadFilesUseCase {
	final static Logger logger = Logger.getLogger(MoveReadFilesUseCase.class);

	public static void execute() {
		try {
			Config config = BuilderConfigProperties.execute();
			
			System.out.println("Movendo arquivos ja processados");
			logger.debug("Movendo arquivos ja processados");
			
			File fileOriginReports = new File(config.getTargetReportsPath());
			File fileDestinationReports = new File(config.getDestinationPath());
			if(!fileDestinationReports.exists()) {
				fileDestinationReports.mkdir();
			}
			
			Arrays.asList(fileOriginReports.listFiles()).stream().forEach(file -> { 
				if (file.isFile()) {
					file.renameTo(new File(config.getDestinationPath() + "//" + file.getName()));
				}
			});


		} catch (Exception e) {
			System.out.println("Erro ao mover arquivos ja processados");
			e.printStackTrace();
		}

	}
}
