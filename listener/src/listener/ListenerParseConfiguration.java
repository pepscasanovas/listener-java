package listener;

import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ListenerParseConfiguration {
	//MQTT INFO LISTENER CONFIG
	public static String client_id_listener;
	public static String broker_addr_listener; 	
	public static int broker_port_listener;
	public int broker_keep_alive_listener;
	public String broker_bind_if_listener;
	public static String topic_listener;
	public static int qos_listener;
	
	//MQTT INFO PUBLISHER CONFIG
	public static String client_id_publisher;
	public static String broker_addr_publisher;
	public static int broker_port_publisher;
	public int broker_keep_alive_publisher;
	public String broker_bind_if_publisher;
	public static String topic_publisher;
	public static int qos_publisher;
	
	//DB INFO CONFIG
	public static String hostname;
	public static String hostaddr;
	public static int hostport;
	public static String dbname;
	public static String dbuser;
	public static String dbpassword;
	
	public boolean parse(Reader msgCfg){
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(msgCfg);
			parse_DBCfg(jsonObject);
			parse_MqttClientCfg(jsonObject);
		} catch (IOException | ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean parse_DBCfg(JSONObject jobj){
		JSONObject dbobj = (JSONObject) jobj.get("DB_information");
		hostname = (String) dbobj.get("hostname");
		hostaddr = (String) dbobj.get("hostaddr");
		hostport = Integer.parseInt((String) dbobj.get("hostport"));
		dbname = (String) dbobj.get("dbname");
		dbuser = (String) dbobj.get("dbuser");
		dbpassword = (String) dbobj.get("dbpassword");
		return true;
	}
	
	public boolean parse_MqttClientCfg(JSONObject jobj){
		JSONObject mqttobj = (JSONObject) jobj.get("mqtt_client_listener");
		client_id_listener = (String) mqttobj.get("client_id");
		broker_addr_listener = (String) mqttobj.get("broker_addr");
		broker_port_listener = Integer.parseInt((String) mqttobj.get("broker_port"));
		broker_keep_alive_listener = Integer.parseInt((String) mqttobj.get("broker_keep_alive"));
		broker_bind_if_listener = (String) mqttobj.get("broker_bind_if");
		JSONObject subobj = (JSONObject) mqttobj.get("subscription");
		topic_listener = (String) subobj.get("topic");
		qos_listener = Integer.parseInt((String) subobj.get("qos"));
		
		JSONObject mqttobj2 = (JSONObject) jobj.get("mqtt_client_publisher");
		client_id_publisher = (String) mqttobj2.get("client_id");
		broker_addr_publisher = (String) mqttobj2.get("broker_addr");
		broker_port_publisher = Integer.parseInt((String) mqttobj2.get("broker_port"));
		broker_keep_alive_publisher = Integer.parseInt((String) mqttobj2.get("broker_keep_alive"));
		broker_bind_if_publisher = (String) mqttobj2.get("broker_bind_if");
		JSONObject subobj2 = (JSONObject) mqttobj2.get("subscription");
		topic_publisher = (String) subobj2.get("topic");
		qos_publisher = Integer.parseInt((String) subobj2.get("qos"));
		return true;
	}
}
