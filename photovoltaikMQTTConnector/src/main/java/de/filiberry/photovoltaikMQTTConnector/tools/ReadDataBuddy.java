package de.filiberry.photovoltaikMQTTConnector.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class ReadDataBuddy {
	private static final int MAX_LOOP_COUNT = 3;
	public static Logger log = Logger.getLogger(ReadDataBuddy.class);

	/**
	 * 
	 * @param url
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public InputStream getDataFromServer(String url) throws IOException {
		int errorCount = 0;
		String lastError = "";
		InputStream result = null;
		URL serverURL;
		URLConnection connection;
		while (errorCount < MAX_LOOP_COUNT) {
			try {
				lastError = "";
				serverURL = new URL(url);
				connection = serverURL.openConnection();
				log.info("Try to open connection to : " + connection.getURL().toString());
				connection.setReadTimeout((120 * 1000));
				result = IOUtils.toBufferedInputStream(connection.getInputStream());
				log.info("Sucess getting Data from SunnyBoy.");
				return result;
			} catch (IOException e) {
				log.error(e);
				lastError = e.getMessage();
				errorCount++;
				try {
					log.info("Get Data failed try number: " + errorCount + " from " + MAX_LOOP_COUNT);
					Thread.sleep((60 * 1000));
				} catch (InterruptedException e1) {
					log.error(e1);
					return null;
				}
			}
		}
		throw new IOException("Sorry, I try " + MAX_LOOP_COUNT + " Times to get Data from : " + url + " -> Error:" + lastError);
	}

}
