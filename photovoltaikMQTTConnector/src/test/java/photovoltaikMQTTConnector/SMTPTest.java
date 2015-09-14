package photovoltaikMQTTConnector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

public class SMTPTest {

	public static void main(String[] args) {
		String sender, recipient, subject, filename, server, cc;
		List<String> ccList = new ArrayList<String>();
		BufferedReader stdin;
		FileReader fileReader = null;
		Writer writer;
		SimpleSMTPHeader header;
		SMTPClient client;


		server = "mail.masterlogin.de";


		try {
	
			sender = "test@home";

			recipient = "home@joern-karthaus.de";

			subject = "testmail";

			header = new SimpleSMTPHeader(sender, recipient, subject);


			client = new SMTPClient();
			client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

			client.connect(server);

			if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				System.err.println("SMTP server refused connection.");
				System.exit(1);
			}

			client.login();

			client.setSender(sender);
			client.addRecipient(recipient);

			for (String recpt : ccList) {
				client.addRecipient(recpt);
			}

			writer = client.sendMessageData();

			if (writer != null) {
				writer.write(header.toString());
				Util.copyReader(fileReader, writer);
				writer.close();
				client.completePendingCommand();
			}

			if (fileReader != null) {
				fileReader.close();
			}

			client.logout();

			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
