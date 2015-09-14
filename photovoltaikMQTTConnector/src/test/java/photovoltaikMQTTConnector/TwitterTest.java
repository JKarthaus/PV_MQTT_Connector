package photovoltaikMQTTConnector;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTest {

	public static void main(String[] args) throws TwitterException {
		
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		    ;
		}
		
		
		
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("Consumer Key")
		  .setOAuthConsumerSecret("Consumer Secret")
		  .setOAuthAccessToken("Access Token")
		  .setOAuthAccessTokenSecret("Token Sekret");
		TwitterFactory tf = new TwitterFactory(cb.build());
		// The factory instance is re-useable and thread safe.
	    Twitter sender = tf.getInstance();
	    DirectMessage message = sender.sendDirectMessage("@BillGates", "Fehler in Heizungssystem -bitte hilf mir");
	    System.out.println("Sent: " + message.getText() + " to @" + message.getRecipientScreenName());
	}

}
