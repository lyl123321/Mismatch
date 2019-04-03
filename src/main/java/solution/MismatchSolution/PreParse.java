package solution.MismatchSolution;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;

import solution.MismatchSolution.dtd2xml.Docu;
import solution.MismatchSolution.dtd2xml.Parser;
import solution.MismatchSolution.xmlParser.XmlFlat;
import solution.MismatchSolution.xmlParser.XmlParser;

public class PreParse {
	private String[] Q;
	
	public PreParse(String[] Q) {
		this.Q = Q;
	}
	
	public HashMap parse() {
		
		String xml = "data/reed/reed.xml"; 
        String dtd = "data/reed/reed.dtd";
        String dtdxml = "data/reed/dtd.xml";
       
        /*
		String xml = "data/dblp/dblp.xml"; 
        String dtd = "data/dblp/dblp.dtd";
        String dtdxml = "data/dblp/dtd.xml";
        */
        HashMap info = new HashMap();
        Vector<String> typeList = new Vector<String>();
        Docu docu = new Docu();
        SAXReader saxReader = new SAXReader();
        
        try {
        	Parser parser = new Parser(docu);
        	parser.parse(new File(dtd));            
        } catch( Exception e ) {
            System.err.println("ERROR: file can not be opened");
        }
        
      //将DTD转成XML
        try {
            PrintWriter output1 = new PrintWriter(dtdxml);
            output1.print(docu.toXml(dtd));
            output1.close();
        } catch( Exception e ) {
        	System.err.println("ERROR: can not change dtd to xml");
        }
        
        //解析dtd.xml
        try {
            Document document = saxReader.read(new File(dtdxml));
            XmlFlat xmlFlat = new XmlFlat();
            xmlFlat.flat(document.getRootElement());
            typeList = xmlFlat.typeList;
        } catch (DocumentException e) {
            System.err.println("ERROR: due to an IOException,the parser could not encode "+ dtdxml); 
		}
        
        //解析reed.xml
        try {
        	Document document = saxReader.read(new File(xml));
        	Element rootElement = document.getRootElement();
        	//---------no-------OutOfMemory
        	org.jsoup.nodes.Element root = Jsoup.parse(new File(xml), docu.getEncoding()).getElementsByTag(rootElement.getQualifiedName()).get(0);
	        XmlParser xmlParser = new XmlParser(Q);
	        info = xmlParser.process1(rootElement, "", 0, typeList);
	        xmlParser.process2(root, "", 0);
	        xmlParser.process3(rootElement, "", 0);
	        xmlParser.close();
	    } catch (DocumentException | IOException e) { 
	        System.err.println("ERROR: due to an IOException,the parser could not encode "+ xml); 
	    }
        
        return info;
	}
}
