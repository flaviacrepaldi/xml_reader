package com.reader.athos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class App {	
    private static final String DH_EMI = "dhEmi";
	private static final String EMIT = "emit";
	private static final String IDE = "ide";
//	private static String folderPath = "/home/flavia/Documents/athos/BASE XMLs";
	private static String folderPath = "C:\\Users\\Phelipe\\Desktop\\Teste";
//    private static String reportsPath = "/home/flavia/Documents/athos/PLANILHAS";
    private static String reportsPath = "C:\\Users\\Phelipe\\Desktop\\Teste\\BASE XMLs";
    
    
    public static void main(String[] args) {
        try{
        	List<Report> reports = builderReports();
        	XSSFWorkbook workbook = new XSSFWorkbook();
        	
        	reports.stream().forEach(report -> {
	        	int rowCount = 0; 
	            XSSFSheet sheet = workbook.createSheet("XML_FILES");
	            XSSFRow row = sheet.createRow(++rowCount);                
	            int columnCount = 0;   
	            
	            Iterable<Report> iterableReport = Arrays.asList(report);
	            for (Object field : iterableReport) {
	                XSSFCell cell = row.createCell(++columnCount);
	                if (field instanceof String) {
	                    cell.setCellValue((String) field);
	                }
	            }
	         
	            try (FileOutputStream outputStream = new FileOutputStream("XMLFile.xlsx")) {
	                workbook.write(outputStream);
	            }catch(Exception ex){
	                System.out.println(ex);
	            }
		        
        	});
        	
        	closeWorkBookSession(workbook);
        	
        }catch(Exception e){
            e.printStackTrace();
        }  
    }

	private static void closeWorkBookSession(XSSFWorkbook workbook) {
		try {
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
		    report.setnDup(getValueElementsByTagName(doc, "dup", "nDup"));
		    report.setdVenc(getValueElementsByTagName(doc, "dup", "dVenc"));
		    report.setvDup(getValueElementsByTagName(doc, "dup", "vDup"));
		    report.setIndPag(getValueElementsByTagName(doc, "detPag", "indPag"));
		    report.settPag(getValueElementsByTagName(doc, "detPag", "tPag"));
		    report.setvPag(getValueElementsByTagName(doc, "detPag", "vPag"));
		    
		    reports.add(report);
		});
		
		return reports;
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
		return null;
	}
    
	static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }
}
