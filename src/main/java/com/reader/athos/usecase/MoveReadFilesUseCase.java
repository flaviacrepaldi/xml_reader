package com.reader.athos.usecase;

import java.io.File;
import java.util.Arrays;

public class MoveReadFilesUseCase {

	private static final String originReports = "C:\\Users\\Phelipe\\Desktop\\Teste\\BASE XMLs";
	private static final String destinationReports = "C:\\Users\\Phelipe\\Desktop\\Teste\\LIDOS\\";

	public static void execute() {
		try {
			System.out.println("Movendo arquivos ja processados");
			File fileOriginReports = new File(originReports);
			File fileDestinationReports = new File(destinationReports);
			if(!fileDestinationReports.exists()) {
				fileDestinationReports.mkdir();
			}
			
			Arrays.asList(fileOriginReports.listFiles()).stream().forEach(file -> { 
				if (file.isFile()) {
					file.renameTo(new File(destinationReports + file.getName()));
				}
			});


		} catch (Exception e) {
			System.out.println("Erro ao mover arquivos ja processados");
			e.printStackTrace();
		}

	}
}
