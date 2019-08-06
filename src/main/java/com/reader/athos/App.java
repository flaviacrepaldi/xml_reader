package com.reader.athos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.reader.athos.model.Duplicata;
import com.reader.athos.model.Report;

public class App {	
    private static final String DH_EMI = "dhEmi";
	private static final String EMIT = "emit";
	private static final String IDE = "ide";
//	private static String folderPath = "/home/flavia/Documents/athos/PLANILHAS";
//	private static String folderPath = "C:\\Users\\Phelipe\\Desktop\\Teste";
	private static String folderPath = "C:\\Users\\phelipe.galiotti\\Desktop\\teste";
//  private static String reportsPath = "/home/flavia/Documents/athos/BASE XMLs";
//  private static String reportsPath = "C:\\Users\\Phelipe\\Desktop\\Teste\\BASE XMLs";
    private static String reportsPath = "C:\\Users\\phelipe.galiotti\\Desktop\\teste\\BASE XMLs";
    
//    private static String lastDateVerified = null;
//    private static Workbook workbook;
    
    public static void main(String[] args) {
        try{
        	StopWatch monitorExecutionReports = new StopWatch();
        	monitorExecutionReports.start();
//        	List<Report> reports = builderReports().stream()
//        			.sorted(Comparator.comparing(Report::convertDhEmiInDate))
//        			.collect(Collectors.toList());
        	
        	Map<String, List<Report>> collect = builderReports().stream().collect(Collectors.groupingBy(Report::formmaterDhEmi));
        	
        	monitorExecutionReports.stop();
        	System.out.println("Tempo de leitura dos XMLs: " + monitorExecutionReports.getTime() + "ms");
        	
        	System.out.println("Inserindo dados na planilha...");
        	StopWatch monitorWritingExcel = new StopWatch();
        	monitorWritingExcel.start();	
        	for(Entry<String, List<Report>> entry : collect.entrySet()) {
        		//A data
        		String data = entry.getKey();
        		//Os registros agrupados por data
        		List<Report> reports = entry.getValue();
        		insertReportsToWorkbook(data, reports);
        	}
        	monitorWritingExcel.stop();
        	System.out.println("Tempo de escrita na planilha: " + monitorWritingExcel.getTime() + "ms");
        }catch(Exception e){
            e.printStackTrace();
        }  
        
        System.out.println("FIM da execução");
    }

	private static void insertReportsToWorkbook(String dhEmiDateFormatted, List<Report> reports) {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet("XML_FILES");
		int columnCount = 0;
		
		//Crie o arquivo 
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

//	private static void setNewLastDateVerified(String formmaterDhEmiDate) {
//		lastDateVerified = formmaterDhEmiDate;
//	}

//	private static Sheet getXMLFileSheet() {
//		 if (Objects.isNull(workbook.getSheet("XML_FILES"))) {
//		 } else {
//			 return workbook.createSheet("XML_FILES");
//			 return workbook.getSheet("XML_FILES");
//		 }
//	}
//
//	private static void setSXSSFWorkbook(String formmaterDhEmi) {
//		if (! formmaterDhEmi.equals(lastDateVerified)) {
//			System.out.println("vai dar new");
//			closeWorkBookSession();
//			workbook = new SXSSFWorkbook();
//		}
//	}
	
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

	private static List<Report> builderReports() throws ParserConfigurationException, SAXException, IOException {
		File fileReportsPath = new File(reportsPath);
		List<Report> reports = new ArrayList<>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    
		Arrays.asList(fileReportsPath.listFiles()).stream().forEach(file -> { 
            System.out.println("Lendo o arquivo -> " + file.getName());
            
            if (hasPermitedExtension(file)) {
			    Document doc = parseDocument(dBuilder, file);
			    doc.getDocumentElement().normalize();
			    
			    Report report = new Report();
			    report.setDhEmi(getValueElementsByTagName(doc, IDE, DH_EMI));
			    report.setCnpj(getValueElementsByTagName(doc, EMIT, "CNPJ"));
			    report.setxNome(getValueElementsByTagName(doc, EMIT, "xNome"));
			    report.setnFat(getValueElementsByTagName(doc, "fat", "nFat"));
			    report.setvOrig(getValueElementsByTagName(doc, "fat", "vOrig"));
			    report.setvDesc(getValueElementsByTagName(doc, "fat", "vDesc"));
			    report.setvLiq(getValueElementsByTagName(doc, "fat", "vLiq"));
			    report.setIndPag(getValueElementsByTagName(doc, "detPag", "indPag"));
			    report.settPag(getValueElementsByTagName(doc, "detPag", "tPag"));
			    report.setvPag(getValueElementsByTagName(doc, "detPag", "vPag"));
			    
			    builderDuplicatas(report, doc);
		    
			    reports.add(report);
            }
		});
		
		return reports;
	}

	private static boolean hasPermitedExtension(File file) {
		if (file.getName().contains(".xml")) {
			return true;
		} else {
			System.out.println("Erro! A extensão do arquivo '" + file.getName() + "' não é permitida. Extensões permitidas (.xml)");
			return false; 
		}
	}

	private static void builderDuplicatas(Report report, Document doc) {
		NodeList nodes = doc.getElementsByTagName("dup");

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
		    if (node.getNodeType() == Node.ELEMENT_NODE) {
		        Element element = (Element) node;
		        
		        String nDup = getValue("nDup", element);
		        String dVenc = getValue("dVenc", element);
		        String vDup = getValue("vDup", element);
		        
		        report.addDuplicata(new Duplicata(nDup, dVenc, vDup));
		    }
		}
	}

	private static Document parseDocument(DocumentBuilder dBuilder, File file) {
		Document doc = null;
		try {
			doc = dBuilder.parse(file);
		} catch (SAXException | IOException e) {
			throw new RuntimeException();
		}
		return doc;
	}

	private static String getValueElementsByTagName(Document doc, String tagName, String nodeValue) {
		NodeList nodes = doc.getElementsByTagName(tagName);

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
		    if (node.getNodeType() == Node.ELEMENT_NODE) {
		        Element element = (Element) node;
		        return getValue(nodeValue, element);
		    }
		}
		return "Não encontrado";
	}
    
	static String getValue(String tag, Element element) {
		Node tagElement = element.getElementsByTagName(tag).item(0);
		
		if(Objects.nonNull(tagElement)){
			NodeList childNodes = tagElement.getChildNodes();
			Node node = childNodes.item(0);
			return node.getNodeValue().equals("") ? "Não encontrado" : node.getNodeValue();
		}
		
		return "Não encontrado";
    }
	
	
}
