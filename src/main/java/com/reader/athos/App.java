package com.reader.athos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.FileOutputStream;
import java.io.IOException; 

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {	
    private static final String N_NF = "nNF";
	private static final String IDE = "ide";
	private static String folderPath = "/home/flavia/Documents/athos/BASE XMLs";
    private static String reportsPath = "/home/flavia/Documents/athos/PLANILHAS";

    public static void main(String[] args) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("XML_FILES");
        Report report = new Report();
        Iterable<Report> iterableReport = Arrays.asList(report);

        Arrays.asList(listOfFiles).stream().forEach(f -> {
            System.out.println(f.getName());
            try{
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(f);
                doc.getDocumentElement().normalize();
            
                NodeList nodes = doc.getElementsByTagName(IDE);

                for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        //ATRIBUIR VALORES AO OBJETO
                        System.out.println("nNF " + getValue(N_NF, element));
                    }
                }
            }catch(Exception e){
                System.out.println(e);
            }
                
            int rowCount = 0;            
            XSSFRow row = sheet.createRow(++rowCount);                
            int columnCount = 0;                
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

        try {
            workbook.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        StringBuilder name = new StringBuilder();
        name.append(N_NF);
        name.append(" ");
        name.append(node.getNodeValue());
        
        return name.toString();
    }
}
