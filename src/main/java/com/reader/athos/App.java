package com.reader.athos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.reader.athos.model.Duplicata;
import com.reader.athos.model.Report;
import com.reader.athos.service.BuilderReports;

public class App {	
private static final String XML_FILES = "XML_FILES";
//	private static String folderPath = "/home/flavia/Documents/athos/PLANILHAS";
	private static String folderPath = "C:\\Users\\Phelipe\\Desktop\\Teste";
//	private static String folderPath = "C:\\Users\\phelipe.galiotti\\Desktop\\teste";
    
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
        	
        	//MoveReadFilesUseCase.execute();
        }catch(Exception e){
            e.printStackTrace();
        }  
        System.out.println("FIM DA EXECUÇÃO");
    }

	private static void insertReportsToWorkbook(String dhEmiDateFormatted, List<Report> reports) throws IOException {
		SXSSFWorkbook workbook = getWorkbook(dhEmiDateFormatted);
		Sheet sheet = getSheet(workbook, dhEmiDateFormatted); 
		int columnCount = 0;
		
		for (Report report : reports) {
			if (report.hasDuplicatas()) {
				insertReportsWithDuplicatas(report, sheet, columnCount);
			} else {
				insertReports(report, sheet, columnCount);
			}
		}
		setAutoSizeToColumn(sheet);
		
		writeSpreadsheetWithSameDateOfReport(dhEmiDateFormatted, workbook);
		closeWorkBookSession(workbook);
	}

	private static Sheet getSheet(Workbook workbook, String dhEmiDateFormatted) {
		if (xlsxFileExists(dhEmiDateFormatted)) {
			return workbook.getSheet(XML_FILES);
		} 
		
		return workbook.createSheet(XML_FILES);
	}

	private static SXSSFWorkbook getWorkbook(String dhEmiDateFormatted) throws IOException {
		if (xlsxFileExists(dhEmiDateFormatted)) {
			FileInputStream excelFile = new FileInputStream(new File(generateCompletePathToSave(dhEmiDateFormatted)));
			return new SXSSFWorkbook(new XSSFWorkbook(excelFile));
		} 
		return new SXSSFWorkbook();
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
		
		String fileCompletePath = generateCompletePathToSave(dhEmiDateFormatted);
		try  {
			File f = new File(fileCompletePath);
			if (! f.exists()) {
				System.out.println("Criando planilha " + dhEmiDateFormatted + ".xlsx");
				createHeaders(workbook);
				f.createNewFile();
			}

			 FileOutputStream outputStream = new FileOutputStream(fileCompletePath);
             workbook.write(outputStream);
             outputStream.flush();
             outputStream.close();
         }catch(Exception ex){
             System.out.println(ex);
         }
	}
	
	private static boolean xlsxFileExists(String dhEmiDateFormatted){
		File f = new File(generateCompletePathToSave(dhEmiDateFormatted));
		return f.exists();
	}
	
	private static String generateCompletePathToSave(String dhEmiDateFormatted){
		StringBuilder fileCompletePath = new StringBuilder();
		fileCompletePath.append(folderPath);
		fileCompletePath.append("\\");
		fileCompletePath.append(dhEmiDateFormatted);
		fileCompletePath.append(".xlsx");
		return fileCompletePath.toString();
	}
	

	private static void createHeaders(Workbook workbook) {
		int columnCount = 0;
	    Sheet sheet = workbook.getSheet(XML_FILES);
	    Row firstRow = sheet.createRow(sheet.getFirstRowNum()-1);
	    List<String> headers = getTitleHeaders();
	    for (String header : headers) {
	    	writingCell(firstRow, header, columnCount++);
		}
	}

	private static List<String> getTitleHeaders() {
		List<String> headers = new ArrayList<String>();
		headers.add("CNPJ");
		headers.add("NOME");
		headers.add("FATURA");
		headers.add("VALOR ORIGINAL");
		headers.add("VALOR DESCONTO");
		headers.add("VALOR LIQUIDO");
		headers.add("QUANTIDADE DE PARCELAS");
		headers.add("DATA DE VENCIMENTO");
		headers.add("VALOR DA DUPLICATA");
		headers.add("VISTA / PRAZO");
		return headers;
	}

	private static void writingCell(Row row, String value, int columnCount) {
		Cell cell = row.createCell(columnCount);
		CellStyle cellStyle = cell.getCellStyle();
		cellStyle.setWrapText(true);
		cell.setCellStyle(cellStyle);;
    	cell.setCellValue(value);
	}
	
	private static void setAutoSizeToColumn(Sheet sheet){
		Row row = sheet.getRow(sheet.getLastRowNum());
		for (int i = 0; i <= row.getLastCellNum() -1; i++) {
			sheet.autoSizeColumn(i);
		}
		
	}

	private static Row createNewRow(Sheet sheet) {
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
