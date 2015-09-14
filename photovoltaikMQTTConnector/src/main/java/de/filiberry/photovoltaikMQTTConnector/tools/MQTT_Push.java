package de.filiberry.photovoltaikMQTTConnector.tools;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.filiberry.photovoltaikMQTTConnector.model.PhotovoltaikModel;

public class MQTT_Push {

	public static Logger log = Logger.getLogger(MQTT_Push.class);
	private Properties config = new Properties();
	private MemoryPersistence persistence = new MemoryPersistence();
	private int qos = 2;
	private String baseTopic;
	private MqttMessage message;

	private Marshaller jaxbMarshaller;

	/**
	 * 
	 * @param config
	 * @throws JAXBException
	 */
	public MQTT_Push(Properties config) throws JAXBException {
		this.config = config;
		this.baseTopic = config.getProperty("MQTT_TOPIC");
		JAXBContext jaxbContext = JAXBContext.newInstance(PhotovoltaikModel.class);
		jaxbMarshaller = jaxbContext.createMarshaller();
	}

	/**
	 * 
	 * @param pmAL
	 */
	public void PushNewData(ArrayList<PhotovoltaikModel> pmAL) {
		try {

			MqttClient sampleClient = new MqttClient(config.getProperty("MQTT_BROKER"), "pvMQTTConnector", persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			log.info("Connecting to broker: " + config.getProperty("MQTT_BROKER"));
			sampleClient.connect(connOpts);
			log.info("Connected");
			for (int i = 0; i < pmAL.size(); i++) {
				StringWriter stringWriter = new StringWriter();
				jaxbMarshaller.marshal(pmAL.get(i), stringWriter);
				message = new MqttMessage(stringWriter.toString().getBytes());
				message.setQos(qos);
				sampleClient.publish(baseTopic, message);
				log.info("Message : " + i +" von : " + pmAL.size() + "->" + stringWriter.toString() + " published");
			}
			// --
			sampleClient.disconnect();
			log.info("Disconnected");
		} catch (MqttException me) {
			log.error(me);
		} catch (JAXBException e) {
			log.error(e);
		}
	}
}
