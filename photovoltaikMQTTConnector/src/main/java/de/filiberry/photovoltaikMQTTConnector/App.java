package de.filiberry.photovoltaikMQTTConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.log4j.Logger;

import twitter4j.TwitterException;
import de.filiberry.photovoltaikMQTTConnector.model.PhotovoltaikModel;
import de.filiberry.photovoltaikMQTTConnector.tools.DBPersistor;
import de.filiberry.photovoltaikMQTTConnector.tools.LastRunParser;
import de.filiberry.photovoltaikMQTTConnector.tools.MQTT_Push;
import de.filiberry.photovoltaikMQTTConnector.tools.ReadDataBuddy;
import de.filiberry.photovoltaikMQTTConnector.tools.SolarLoggerParser;
import de.filiberry.photovoltaikMQTTConnector.tools.SunriseBuddy;
import de.filiberry.photovoltaikMQTTConnector.tools.TwitterBuddy;

@SuppressWarnings("restriction")
public class App implements Daemon {

	public static Logger log = Logger.getLogger(App.class);
	private TwitterBuddy twitterBuddy;
	private String[] startPara;

	/**
	 * 
	 * @param config
	 * @throws IOException
	 * @throws ParseException
	 * @throws JAXBException
	 */
	private void run(Properties config) throws IOException, ParseException, JAXBException {
		log.info("Application : " + this.getClass().getName() + " startet ...");
		// Init Phase
		SunriseBuddy sunriseBuddy = new SunriseBuddy(config);
		ReadDataBuddy readDataBuddy = new ReadDataBuddy();
		SolarLoggerParser solarLoggerParser = new SolarLoggerParser(config);
		DBPersistor dbPersistor = new DBPersistor();
		MQTT_Push mqtt_Push = new MQTT_Push(config);
		int interval = new Integer(config.getProperty("RUN_INTERVAL"));
		int intervalCount = 0;
		// Main Loop
		log.info("Going to Main Loop run every " + interval + " Seconds...");
		while (true) {
			// --
			ArrayList<PhotovoltaikModel> pmAL = null;
			if (intervalCount >= interval) {
				log.debug("Application -> RUN");
				if (sunriseBuddy.isSunrise()) {
					Date lastRun = LastRunParser.getLastRun();
					InputStream dataInputStream = readDataBuddy.getDataFromServer(config.getProperty("PHOTOVOLTAIK_DATA_PROVIDER"));
					pmAL = solarLoggerParser.parseSolarDataSince(dataInputStream, lastRun);
					LastRunParser.setLastRun(new Date());
				} else {
					// Push that we are in Sunset
					mqtt_Push.PushSunsetData();
					log.debug("We are in Sunset - NO new Data Sunset Info Push to MQTT Connector");
				}

				// There is some Data to Push
				if (pmAL != null && pmAL.size() > 0) {
					mqtt_Push.PushNewData(pmAL);
					dbPersistor.PersistNewData(pmAL);
					log.info("Send " + pmAL.size() + " new Solar Data to MQTT Connector");
				}
				// Disconnected no Sun Power
				if (pmAL != null && pmAL.size() == 0) {
					mqtt_Push.PushNoData();
					log.debug("NO Data from PV Server maybe Cloudy ?");
				}
				// --
				intervalCount = 0;
				log.info("Next run in " + interval + " Seconds...");
			}
			// --
			intervalCount++;
			// --
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				dbPersistor.closeDB();
				log.error(e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			log.error("No Config Parameter giver -> Exit");
			System.out.println("Please give me a config File as Parameter1");
			System.exit(1);
		}
		// -------------------------------------------------------- Main Loop
		while (true) {
			App app = null;
			try {
				app = new App();
				Properties config = new Properties();
				config.load(new FileInputStream(new File(args[0])));
				app.twitterBuddy = new TwitterBuddy(config);
				app.run(config);
			} catch (Exception e) {
				log.error(e);
				try {
					app.twitterBuddy.sendMessage("ERROR:" + e.getMessage());
				} catch (TwitterException e2) {
					e2.printStackTrace();
					log.error(e2);
				}
				try {
					log.info("Try to Restart in 30 Minutes");
					Thread.sleep((30 * 60 * 1000));
				} catch (InterruptedException e1) {
					log.info("Application Shut Down");
					e1.printStackTrace();
					System.exit(1);
				}
			}
		}
		// --------------------------------------------------------------------
	}

	public void destroy() {
		System.exit(0);
	}

	/**
	 * JSVC init Implementation
	 */
	public void init(DaemonContext daemonContext) throws Exception {
		if (daemonContext == null || daemonContext.getArguments().length == 0) {
			throw new Exception("No config File given by jsvc deamonContext");
		}
		startPara = daemonContext.getArguments();
	}

	/**
	 * JSVC Start implementation
	 */
	public void start() throws Exception {
		main(startPara);
	}

	/**
	 * JSVC stop Implementation
	 */
	public void stop() throws Exception {
		System.exit(0);
	}

}
