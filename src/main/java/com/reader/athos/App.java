package com.reader.athos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.reader.athos.model.Duplicata;
import com.reader.athos.model.Report;
import com.reader.athos.service.BuilderReports;

public class App {	
//	private static String folderPath = "/home/flavia/Documents/athos/PLANILHAS";
//	private static String folderPath = "C:\\Users\\Phelipe\\Desktop\\Teste";
	private static String folderPath = "C:\\Users\\phelipe.galiotti\\Desktop\\teste";
    
    public static void main(String[] args) {
        try{
        	Map<String, List<Report>> reportsOrderByDhEmiDate = BuilderReports.builderReports().stream().collect(Collectors.groupingBy(Report::formmaterDhEmi));

        	System.out.println("Inserindo dados...");
        	StopWatch monitorWritingExcel = new StopWatch();
        	monitorWritingExcel.start();	
        	for(Entry<String, List<Report>> entry : reportsOrderByDhEmiDate.entrySet()) {
        		String dhEmiDateFormatted = entry.getKey();
        		List<Report> reportsByDate = entry.getValue();
        		insertReportsToWorkbook(dhEmiDateFormatted, reportsByDate);
        	}
        	monitorWritingExcel.stop();
        	System.out.println("Tempo de total de escrita: " + monitorWritingExcel.getTime() + "ms");
        }catch(Exception e){
            e.printStackTrace();
        }  
        System.out.println("FIM DA EXECUÇÃO");
    }

	private static void insertReportsToWorkbook(String dhEmiDateFormatted, List<Report> reports) {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet("XML_FILES");
		int columnCount = 0;
		
		for (Report report : reports) {
			System.out.println("inserindo cnpj -> "+ report.getCnpj());
			if (report.hasDuplicatas()) {
				insertReportsWithDuplicatas(report, sheet, columnCount);
			} else {
				insertReports(report, sheet, columnCount);
			}
		}
		writeSpreadsheetWithSameDateOfReport(dhEmiDateFormatted, workbook);
		closeWorkBookSession(workbook);
	}

	private static void insertReports(Report report, Sheet sheet, int columnCount) {
		Row row = createNewRow(sheet);	
		writingCell(row, report.getCnpj(), columnCount++);
		writingCell(row, report.getxNome(), columnCount++);
		writingCell(row, report.getnFat(), columnCount++);
		writingCell(row, report.getvOrig(), columnCount++);
		writingCell(row, report.getvDesc(), columnCount++);
		writingCell(row, report.getvLiq(), columnCount++);
		writingCell(row, "N/A não há duplicata", columnCount++);
		writingCell(row, "N/A não há duplicata", columnCount++);
		writingCell(row, "N/A não há duplicata", columnCount++);
		writingCell(row, report.getPaymentType(), columnCount++);
	}

	private static void insertReportsWithDuplicatas(Report report, Sheet sheet, int columnCount) {
		System.out.println("Tem "+ report.getDuplicatas().size() + " duplicatas");
		Row row = createNewRow(sheet);			
		for (Duplicata duplicata : report.getDuplicatas()) {
			writingCell(row, report.getCnpj(), columnCount++);
			writingCell(row, report.getxNome(), columnCount++);
			writingCell(row, report.getnFat(), columnCount++);
			writingCell(row, report.getvOrig(), columnCount++);
			writingCell(row, report.getvDesc(), columnCount++);
			writingCell(row, report.getvLiq(), columnCount++);
			writingCell(row, duplicata.getnDup(), columnCount++);
			writingCell(row, duplicata.getdVenc(), columnCount++);
			writingCell(row, duplicata.getvDup(), columnCount++);
			writingCell(row, report.getPaymentType(), columnCount++);
		}
		
	}
	
	private static void writeSpreadsheetWithSameDateOfReport(String dhEmiDateFormatted, Workbook workbook) {
		StringBuilder fileCompletePath = new StringBuilder();
		fileCompletePath.append(folderPath);
		fileCompletePath.append("\\");
		fileCompletePath.append(dhEmiDateFormatted);
		fileCompletePath.append(".xlsx");
		
		try  {
			File f = new File(fileCompletePath.toString());
			if (! f.exists()) {
				System.out.println("Criando planilha " + dhEmiDateFormatted + ".xlsx");
				f.createNewFile();
			}

			 FileOutputStream outputStream = new FileOutputStream(fileCompletePath.toString());
             workbook.write(outputStream);
             outputStream.flush();
             outputStream.close();
         }catch(Exception ex){
             System.out.println(ex);
         }
	}

	private static void writingCell(Row row, String value, int columnCount) {
		Cell cell = row.createCell(columnCount);
    	cell.setCellValue(value);
	}

	private static Row createNewRow(Sheet sheet) {
		System.out.println("na linha -> " + (sheet.getLastRowNum() + 1));
		Row row = sheet.createRow(sheet.getLastRowNum() + 1); 
		return row;
	}

	private static void closeWorkBookSession(Workbook workbook) {
		try {
		    if (Objects.nonNull(workbook)) 
		    	workbook.close();
		} catch (IOException e) {
		   e.printStackTrace();
		}
	}

	
	
	
}
