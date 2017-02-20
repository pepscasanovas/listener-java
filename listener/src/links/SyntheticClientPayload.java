package links;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class SyntheticClientPayload {
	public JSONObject payload;
	
	public SyntheticClientPayload(){
	}
//	public String strinize(JSONObject received){
//		
//	}
	public JSONObject destrinize(String mqttMsg){
		int res = 1;
		Document doc = loadXMLFromString(mqttMsg);
		if(doc ==null){
			System.out.println("Error: could not parse XML stream");
			return null;
		}
		doc.getDocumentElement().normalize();
		if(doc.getDocumentElement().getNodeName()==null){
			res = 0;
			return null;
		}
		payload = new JSONObject();
		if(res!=0){
			if(doc.getElementsByTagName("timeStamp").getLength()>0){
				payload.put("timeStamp",doc.getElementsByTagName("timeStamp").item(0).getTextContent().trim());				
			}
			if(doc.getElementsByTagName("element_id").getLength()>0){
				payload.put("elementId",doc.getElementsByTagName("element_id").item(0).getTextContent().trim());			
			}
			if(doc.getElementsByTagName("source_id").getLength()>0){
				payload.put("sourceId",doc.getElementsByTagName("source_id").item(0).getTextContent().trim());				
			}
			if(doc.getElementsByTagName("trigger").getLength()>0){
				payload.put("trigger",doc.getElementsByTagName("trigger").item(0).getTextContent().trim());				
			}
			if(doc.getElementsByTagName("readingType").getLength()>0){
				payload.put("readingType",doc.getElementsByTagName("readingType").item(0).getTextContent().trim());				
			}
			if(doc.getElementsByTagName("payload").getLength()>0){
				payload.put("payload",doc.getElementsByTagName("payload").item(0).getTextContent().trim());		
			}
			if(doc.getElementsByTagName("notes").getLength()>0){
				payload.put("notes",doc.getElementsByTagName("notes").item(0).getTextContent().trim());
			}
		}
		return payload;
	}
	public Document loadXMLFromString(String xml){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			StringReader lector = new StringReader(xml);
			InputSource inp = new InputSource(lector);
			Document doc = builder.parse(inp);
			return doc;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
