package de.filiberry.photovoltaikMQTTConnector.tools;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;

public class LastRunParser {

	private static final String LAST_RUN_FILE = "LASTRUN.txt";
	private static final String PATTERN = "dd.MM.yyyy HH:mm:SS";

	/**
	 * 
	 * @param date
	 * @throws IOException
	 */
	public static void setLastRun(Date date) throws IOException {
		File timeStampFile = new File(LAST_RUN_FILE);
		FileUtils.writeStringToFile(timeStampFile, DateFormatUtils.format(date, PATTERN));
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static Date getLastRun() throws IOException, ParseException {
		File timeStampFile = new File(LAST_RUN_FILE);
		if (!timeStampFile.exists()) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(GregorianCalendar.HOUR_OF_DAY, -1);
			LastRunParser.setLastRun(cal.getTime());
		}
		timeStampFile = new File(LAST_RUN_FILE);
		String data = FileUtils.readFileToString(timeStampFile);
		SimpleDateFormat format = new SimpleDateFormat(PATTERN);
		return format.parse(data);
	}

}
