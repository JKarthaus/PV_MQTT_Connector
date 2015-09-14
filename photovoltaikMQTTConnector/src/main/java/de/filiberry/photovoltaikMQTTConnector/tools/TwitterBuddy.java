package de.filiberry.photovoltaikMQTTConnector.tools;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import de.filiberry.photovoltaikMQTTConnector.App;
import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterBuddy {

	private Properties config;
	private Twitter sender;
	public static Logger log = Logger.getLogger(App.class);

	/**
	 * 
	 * @param config
	 */
	public TwitterBuddy(Properties config) {
		this.config = config;

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			log.error(e);
		}
		// --
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(config.getProperty("TWITTER.OAUTH.CONSUMERKEY"))
				.setOAuthConsumerSecret(config.getProperty("TWITTER.OAUTH.CONSUMERSECRET"))
				.setOAuthAccessToken(config.getProperty("TWITTER.OAUTH.ACCESSTOKEN"))
				.setOAuthAccessTokenSecret(config.getProperty("TWITTER.OAUTH.ACCESSTOKENSECRET"));
		TwitterFactory tf = new TwitterFactory(cb.build());
		sender = tf.getInstance();
	}

	/**
	 * 
	 * @param messageText
	 * @throws TwitterException
	 */
	public void sendMessage(String messageText) throws TwitterException {
		DirectMessage message = sender.sendDirectMessage(config.getProperty("TWITTER.RECEIVER"), messageText);
		log.info("Message id:" + message.getId() + " send to Twitter Account:" + config.getProperty("TWITTER.RECEIVER"));
	}

}
