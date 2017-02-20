package mqtt;

import java.io.IOException;
import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import links.SyntheticClientPayload;
import listener.MqttListener;

public class MosquittoWrapper{
	public static final int MQTT_DEFAULT_PORT = 1883;
	public MqttClient broker;
	public MqttListener listener;
	public MosquittoWrapper(MqttListener mqtt){
		listener = mqtt;
	}
	public MosquittoWrapper() {
		// TODO Auto-generated constructor stub
	}
	public void connectBroker(String serverURI, String broker_id, int port){
		if (port==-1){
			port = MQTT_DEFAULT_PORT;
		}
		serverURI = "tcp://"+serverURI+":"+Integer.toString(port);
		System.out.println("Connecting to MQTT Broker at "+serverURI);
		try {
			broker = new MqttClient(serverURI, broker_id);
			broker.connect();
			broker.setCallback(listener);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void subscribe(String topic, int qos){
		try {
			broker.subscribe(topic,qos);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void publish(String topic, int qos, String message){
		MqttMessage mes = new MqttMessage();
		mes.setPayload(message.getBytes());
		try {
			broker.publish(topic, mes);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
