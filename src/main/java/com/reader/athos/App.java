package com.reader.athos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.reader.athos.model.Config;
import com.reader.athos.model.Duplicata;
import com.reader.athos.model.Report;
import com.reader.athos.service.BuilderConfigProperties;
import com.reader.athos.service.BuilderReports;
import com.reader.athos.usecase.MoveReadFilesUseCase;

public class App {	
	private static Config config;
	private static final String XML_FILES = "XML_FILES";
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main(String[] args) {
        try{
        	logger.debug("inicio da execução: " + getNowTime());
        	
        	config = BuilderConfigProperties.execute();
        	Map<String, List<Report>> reportsOrderByDhEmiDate = BuilderReports
        			.builderReports(config)
        			.stream()
        			.collect(Collectors.groupingBy(Report::getYearByDhEmi));

        	if (reportsOrderByDhEmiDate.isEmpty()) {
        		System.out.println("Nao ha arquivos a serem processados");
	        	logger.debug("nao ha arquivos a serem processados");
        	} else {
	        	System.out.println("Inserindo dados... Por favor, aguarde");
	        	logger.debug("inserindo dados");
	        	
	        	StopWatch monitorWritingExcel = new StopWatch();
	        	monitorWritingExcel.start();	
	        	for(Entry<String, List<Report>> entry : reportsOrderByDhEmiDate.entrySet()) {
	        		String dhEmiYear = entry.getKey();
	        		List<Report> reportsByYear = entry.getValue();
	        		insertReportsToWorkbook(dhEmiYear, reportsByYear);
	        	}
	        	monitorWritingExcel.stop();
	        	System.out.println("Tempo de total de escrita: " + monitorWritingExcel.getTime() + "ms");
	        	logger.debug("tempo de total de escrita: " + monitorWritingExcel.getTime() + "ms");
	        	
	        	MoveReadFilesUseCase.execute();
        	}
        	
        	logger.debug("FIM DA EXECUCAO");
        	System.out.println("FIM DA EXECUCAO");
        }catch(Exception e){
        	logger.error("Erro: " ,e);
            e.printStackTrace();
        }
        
    }

	private static String getNowTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd - HH:mm");
		return formatter.format(new Date());
	}

	private static void insertReportsToWorkbook(String dhEmiYear, List<Report> reports) throws IOException {
		Workbook workbook = getWorkbook(dhEmiYear);
		Sheet sheet = getSheet(workbook, dhEmiYear); 
		int columnCount = 0;
		int qtdeDeArquivosProcessados = 0;
		
		for (Report report : reports) {
			if (report.hasDuplicatas()) {
				qtdeDeArquivosProcessados += report.getDuplicatas().size();
				logger.debug("com DUPLICATA|FileName:" + report.getFileName() +"|qtde:" + report.getDuplicatas().size());
				insertReportsWithDuplicatas(report, sheet, columnCount);
			} else {
				qtdeDeArquivosProcessados++;
				logger.debug("sem DUPLICATA|FileName:" + report.getFileName());
				insertReports(report, sheet, columnCount);
			}
		}
		logger.debug("quantidade final de linhas inseridas: " + qtdeDeArquivosProcessados);
		setAutoSizeToColumn(sheet);
		
		writeSpreadsheetWithSameDateOfReport(dhEmiYear, workbook);
		closeWorkBookSession(workbook);
	}

	private static Sheet getSheet(Workbook workbook, String dhEmiYear) {
		if (xlsxFileExists(dhEmiYear)) {
			return workbook.getSheet(XML_FILES);
		} 
		
		return workbook.createSheet(XML_FILES);
	}

	private static Workbook getWorkbook(String dhEmiYear) throws IOException {
		if (xlsxFileExists(dhEmiYear)) {
			FileInputStream excelFile = new FileInputStream(new File(generateCompletePathToSave(dhEmiYear)));
			return new XSSFWorkbook(excelFile);
		} 
		return new XSSFWorkbook();
	}

	private static void insertReports(Report report, Sheet sheet, int columnCount) {
		Row row = createNewRow(sheet);	
		writingCell(row, report.getCnpj(), columnCount++);
		writingCell(row, report.getxNome(), columnCount++);
		writingCell(row, report.getNatOp(), columnCount++);
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
			writingCell(row, report.getNatOp(), columnCount++);
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
				logger.debug("Criando planilha " + dhEmiDateFormatted + ".xlsx");
				createHeaders(workbook);
				f.createNewFile();
			}

			 FileOutputStream outputStream = new FileOutputStream(f);
             workbook.write(outputStream);
             outputStream.flush();
             outputStream.close();
         }catch(Exception e){
        	 logger.error("Erro ao escrever na planilha: " , e);
             System.out.println(e);
         }
	}
	
	private static boolean xlsxFileExists(String dhEmiDateFormatted){
		File f = new File(generateCompletePathToSave(dhEmiDateFormatted));
		return f.exists();
	}
	
	private static String generateCompletePathToSave(String dhEmiDateFormatted){
		StringBuilder fileCompletePath = new StringBuilder();
		fileCompletePath.append(config.getFolderPath());
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
		headers.add("OPERAÇÃO");
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
		Row row = sheet.getRow(sheet.getFirstRowNum());
		if (Objects.nonNull(row)) {
			for (int i = 0; i <= row.getLastCellNum() -1; i++) {
				sheet.autoSizeColumn(i);
			}
		}
	}

	private static Row createNewRow(Sheet sheet) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1); 
		return row;
	}

	private static void closeWorkBookSession(Workbook workbook) {
		try {
		    if (Objects.nonNull(workbook)) workbook.close();
		} catch (IOException e) {
			logger.error("Erro: " , e);
		   e.printStackTrace();
		}
	}

	
	
	
}
