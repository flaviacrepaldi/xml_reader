package com.reader.athos.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.time.StopWatch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.reader.athos.model.Config;
import com.reader.athos.model.Duplicata;
import com.reader.athos.model.Report;

public class BuilderReports {
	private static final String DUP = "dup";
	private static final String DET_PAG = "detPag";
	private static final String FAT = "fat";
	private static final String EMIT = "emit";
	private static final String IDE = "ide";
	
//  private static String reportsPath = "/home/flavia/Documents/athos/BASE XMLs";
//  private static String reportsPath = "C:\\Users\\Phelipe\\Desktop\\Teste\\BASE XMLs";
//  private static String reportsPath = "C:\\Users\\phelipe.galiotti\\Desktop\\teste\\BASE XMLs";
	
	
	public static List<Report> builderReports(Config config) throws ParserConfigurationException, SAXException, IOException {
		StopWatch monitorExecutionReports = new StopWatch();
    	monitorExecutionReports.start();

		File fileReportsPath = new File(config.getTargetReportsPath());
		List<Report> reports = new ArrayList<>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    
		Arrays.asList(fileReportsPath.listFiles()).stream().forEach(file -> { 
            System.out.println("Lendo o arquivo -> " + file.getName());
            
            if (hasPermitedExtension(file)) {
			    Document doc = parseDocument(dBuilder, file);
			    doc.getDocumentElement().normalize();
			    
			    Report report = new Report();
			    report.setDhEmi(getValueElementsByTagName(doc, IDE, "dhEmi"));
			    report.setCnpj(getValueElementsByTagName(doc, EMIT, "CNPJ"));
			    report.setxNome(getValueElementsByTagName(doc, EMIT, "xNome"));
			    report.setnFat(getValueElementsByTagName(doc, FAT, "nFat"));
			    report.setvOrig(getValueElementsByTagName(doc, FAT, "vOrig"));
			    report.setvDesc(getValueElementsByTagName(doc, FAT, "vDesc"));
			    report.setvLiq(getValueElementsByTagName(doc, FAT, "vLiq"));
			    report.setIndPag(getValueElementsByTagName(doc, DET_PAG, "indPag"));
			    report.settPag(getValueElementsByTagName(doc, DET_PAG, "tPag"));
			    report.setvPag(getValueElementsByTagName(doc, DET_PAG, "vPag"));
			    
			    builderDuplicatas(report, doc);
		    
			    reports.add(report);
            }
		});
		
		monitorExecutionReports.stop();
    	System.out.println("Tempo de leitura dos XMLs: " + monitorExecutionReports.getTime() + "ms");
    	
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
		NodeList nodes = doc.getElementsByTagName(DUP);

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
