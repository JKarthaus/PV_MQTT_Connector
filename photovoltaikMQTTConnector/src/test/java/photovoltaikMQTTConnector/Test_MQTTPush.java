package photovoltaikMQTTConnector;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.filiberry.photovoltaikMQTTConnector.model.PhotovoltaikModel;
import de.filiberry.photovoltaikMQTTConnector.tools.MQTT_Push;

public class Test_MQTTPush {

	private MQTT_Push mqtt_push;
	private Properties config;
	private final static String TEST_CONFIG_FILE = "/home/joern/_Repo_PMQTTC/photovoltaikMQTTConnector/_INSTALL_/config.properties";

	@Before
	public void setUp() throws Exception {
		config = new Properties();
		config.load(new FileInputStream(new File(TEST_CONFIG_FILE)));
		mqtt_push = new MQTT_Push(config);
	}

	/**
	 * 
	 * @return
	 */
	private ArrayList<PhotovoltaikModel> getMOCKData() {
		ArrayList<PhotovoltaikModel> result = new ArrayList<PhotovoltaikModel>();
		// --
		PhotovoltaikModel mock1 = new PhotovoltaikModel();
		mock1.setDaySum(3000);
		mock1.setError(1);
		mock1.setPac(2);
		mock1.setPdc1(220);
		mock1.setPdc2(200);
		mock1.setStatus(1);
		mock1.setTemp(35);
		mock1.setTimeStamp(new Date());
		mock1.setUac(100);
		mock1.setUdc1(200);
		mock1.setUdc2(201);
		mock1.setWr(0);
		// --
		result.add(mock1);
		return result;
	}

	@Test
	public void test() {
		System.out.println("Send Mock Data to " + config.getProperty("MQTT_BROKER"));
		mqtt_push.PushNewData(getMOCKData());
		System.out.println("OK");

	}

}
