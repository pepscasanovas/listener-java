package listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.paho.client.*;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.PostgreWrapper;
import links.SyntheticClientPayload;
import mqtt.MosquittoWrapper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class MqttListener implements MqttCallback{
	
	public static MosquittoWrapper mqtt_listener, mqtt_publisher;
	public static PostgreWrapper db;
	public static ListenerParseConfiguration ParseCfg; 
	public static Connection dbConn;
	
	public MqttListener(){
		mqtt_listener = new MosquittoWrapper(this);
		mqtt_publisher = new MosquittoWrapper();
		ParseCfg = new ListenerParseConfiguration();
		db = new PostgreWrapper();
	}

	public static void main(String[] args){
		new MqttListener();
		//Read the configuration file
		try {
			if(args[0].isEmpty()){
				System.out.println("A configuration file is required");
				System.exit(1);
			}
			FileReader reader = new FileReader(args[0]);
			if(!ParseCfg.parse(reader)){
				System.out.println("Error parsing the configuration file: "+args[0]);
				System.exit(1);
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Open connection to the DB
//******************************************************************************************************
		//A LA ESPERA DE EL MODEL DE BBDD_MATCHER_LISTENER A ESCOLLIR(tema streams interns BBDD)
//******************************************************************************************************
//		db.setHostInfo(ParseCfg.hostname, ParseCfg.hostaddr, ParseCfg.hostport);
//		db.setDbInfo(ParseCfg.dbname, ParseCfg.dbuser, ParseCfg.dbpassword);
//		db.setURLString();
//		dbConn = db.connect();
		
		//Open connection to the MQTT Listener Broker
		mqtt_listener.connectBroker(ParseCfg.broker_addr_listener, ParseCfg.client_id_listener, ParseCfg.broker_port_listener);
		mqtt_listener.subscribe(ParseCfg.topic_listener, ParseCfg.qos_listener);
		
		//Open connection to the MQTT Publisher Broker
		mqtt_publisher.connectBroker(ParseCfg.broker_addr_publisher, ParseCfg.client_id_publisher, ParseCfg.broker_port_publisher);
		
		while(true){
			
		}
		
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message){
		// TODO Auto-generated method stub
		System.out.println("Info: MQTT message received topic "+topic);
		/*
		 * Build time-stamp to be used for createdAt and updatedAt
		 */
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String time = Long.toString(timestamp.getTime());
		/*
		 * De-strinize the MQTT payload
		 */
		JSONObject payload=null;
//*******************IF RX MODEL IS XML FILE***********************		
//		SyntheticClientPayload payHnd = new SyntheticClientPayload();
//		payload = payHnd.destrinize(message.toString());
//		System.out.println("Missatge XML rebut " +payload.toString());
//*******************IF RX MODEL IS JSON FILE**********************
		JSONParser jparse= new JSONParser();
		try {
			payload = (JSONObject) jparse.parse(message.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Missatge JSON rebut " +payload.toString());
//*****************************************************************
		if(payload ==null){
			System.out.println("Error parsing MQTT payload");
			return;
		}
		String IsAlive = "true";
		/*
		 * Generate TimeStamp string from the received MQTT message
		 */
		if(payload.get("timeStamp")!="" && payload.get("timeStamp")!=null){
			time = (String) payload.get("timeStamp");
		}

//******************************************************************************************************
		//A LA ESPERA DE EL MODEL DE BBDD_MATCHER_LISTENER A ESCOLLIR(tema streams interns BBDD)
//******************************************************************************************************
		mqtt_publisher.publish(topic, ParseCfg.qos_publisher, payload.toString());
//		/*
//		 * Resolve Id from received information. In the event that elementId information could not be
//		 *  resolved, force an empty value to enter the loop below.
//		 */
//		String elementIdError = null,sourceIdError = null,readingTypeIdError = null;
//		List elementIdList = getElementIdList(dbConn, (String) payload.get("elementId"),elementIdError);
//		List sourceIdList = getSourceIdList(dbConn, (String) payload.get("sourceId"), sourceIdError);
//		List readingTypeIdList = getReadingTypeIdList(dbConn, (String) payload.get("readingType"), readingTypeIdError);
//		if(elementIdList.isEmpty()){
//			elementIdList.add("");
//		}
//
//		/*
//		 * The reading is inserted in the DB as many times as elementId are resolved above
//		 * In the event that there are inconsistencies in sourceId and/or readingTypeId, the incorrect
//		 *  field is left blank and an error in the log table is inserted.
//		 */
//		for(Object obj: elementIdList){
//			String elementId=obj.toString(),sourceId="",readingTypeId="";
//			List logErrorTextList = new ArrayList();
//			if(elementIdList.get(0)==null || elementIdList.size()!=1)
//				logErrorTextList.add(elementIdError);
//			if(sourceIdList.isEmpty()||sourceIdList.size()!=1)
//				logErrorTextList.add(sourceIdError);
//			else
//				sourceId = sourceIdList.get(0).toString();
//			if(readingTypeIdList.isEmpty()||readingTypeIdList.size()!=1)
//				logErrorTextList.add(readingTypeIdError);
//			else
//				readingTypeId = readingTypeIdList.get(0).toString();
//			/*
//			 * Build the HASH value
//			 */
//			MessageDigest md;
//			String md5str;
//			try {
//				md = MessageDigest.getInstance("MD5");
//				md.update(time.getBytes());
//				md.update(((String) payload.get("trigger")).getBytes());
//				md.update(((String) payload.get("payload")).getBytes());
//				md.update(((String) payload.get("trigger")).getBytes());
//				md.update(IsAlive.getBytes());
//				md.update(((String) payload.get("notes")).getBytes());
//				md.update(elementId.getBytes());
//				md.update(readingTypeId.getBytes());
//				md.update(sourceId.getBytes());
//				byte[] md5bytes = md.digest();
//				md5str = new String(md5bytes);
//			} catch (NoSuchAlgorithmException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			/*
//			 * Build the SQL sQuery
//			 */
//			
//
//
//
//		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	public List getElementIdList(Connection con, String name,String pErrorText){
		String query = "SELECT \"id\" FROM \"tsElements\" WHERE \"isAlive\"='true' AND  \"serialNumber\"='"
				+StringEscapeUtils.escapeJava(name)+"';";
		ResultSet rs = db.select(con, query);
		List retList = new ArrayList();
		try {
			if(!rs.next()){
				if(pErrorText!=null){
					pErrorText = "Warning: Error in the SQL SELECT query of elementId for '"+name+"'";
				}
			}
			else{
				retList.add(rs.getObject(rs.getRow()));
				while(rs.next()){
					retList.add(rs.getObject(rs.getRow()));
				}
				if(pErrorText!=null){
					rs.last();
					if(rs.getRow()==0){
						pErrorText = "Warning: Unknown elementId for "+name;
					}
					else if(rs.getRow()>1){
						String idsStr="";
						Iterator it = retList.iterator();
						while(it.hasNext()){
							idsStr = idsStr+", "+it;
						}
						pErrorText = "Warning: Multiple elementId for '"+name+"': "+idsStr;
					}
				}	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return retList;
	}

	public List getSourceIdList(Connection con, String name, String pErrorText){
		String query = "SELECT \"id\" FROM \"tsElements\" WHERE \"isAlive\"='true' AND \"name\"='" + 
				StringEscapeUtils.escapeJava(name)+"';";
		ResultSet rs = db.select(con, query);
		List retList = new ArrayList();
		try {
			if(!rs.next()){
				if(pErrorText!=null){
					pErrorText = "Warning: Error in the SQL SELECT query of sourceId for '"+name+"'";
				}
			}
			else{
				retList.add(rs.getObject(rs.getRow()));
				while(rs.next()){
					retList.add(rs.getObject(rs.getRow()));
				}
				if(pErrorText!=null){
					rs.last();
					if(rs.getRow()==0){
						pErrorText = "Warning: Unknown sourceId for "+name;
					}
					else if(rs.getRow()>1){
						String idsStr="";
						Iterator it = retList.iterator();
						while(it.hasNext()){
							idsStr = idsStr+", "+it;
						}
						pErrorText = "Warning: Multiple sourceId for '"+name+"': "+idsStr;
					}
				}	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return retList;
	}
	
	public List getReadingTypeIdList(Connection con, String name, String pErrorText){
		String query = "SELECT \"id\" FROM \"tsReadingTypes\" WHERE \"isAlive\"='true' AND \"name\"='" + 
				StringEscapeUtils.escapeJava(name)+"';";
		ResultSet rs = db.select(con, query);
		List retList = new ArrayList();
		try {
			if(!rs.next()){
				if(pErrorText!=null){
					pErrorText = "Warning: Error in the SQL SELECT query of readingTypeId for '"+name+"'";
				}
			}
			else{
				retList.add(rs.getObject(rs.getRow()));
				while(rs.next()){
					retList.add(rs.getObject(rs.getRow()));
				}
				if(pErrorText!=null){
					rs.last();
					if(rs.getRow()==0){
						pErrorText = "Warning: Unknown readingTypeId for "+name;
					}
					else if(rs.getRow()>1){
						String idsStr="";
						Iterator it = retList.iterator();
						while(it.hasNext()){
							idsStr = idsStr+", "+it;
						}
						pErrorText = "Warning: Multiple readingTypeId for '"+name+"': "+idsStr;
					}
				}	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return retList;
	}
	
}