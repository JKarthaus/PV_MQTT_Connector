package de.filiberry.photovoltaikMQTTConnector.tools;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.filiberry.photovoltaikMQTTConnector.model.PhotovoltaikModel;

/**
 * Solar Log Format
 * Datum ; Uhrzeit ; WR ; Pac ; DaySum ; Status ; Error ; Pdc1 ; Pdc2 ; Udc1 ; Udc2 ; Temp ; Uac
 * 19.02.15 ; 16:40:00 ; 1 ; 570 ; 5800 ; 1 ; 0 ; 291 ; 287 ; 380 ; 374 ; 33 ; 229
 * 
 * @author joern
 *
 */

public class SolarLoggerParser {

	static Logger log = Logger.getLogger(SolarLoggerParser.class);
	static Float SOLAR_AREA = new Float(12);
	private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public ArrayList<PhotovoltaikModel> parseSolarDataSince(InputStream data, Date sinceDate) throws IOException {
		// Parse the File
		ArrayList<PhotovoltaikModel> pmAL = new ArrayList<PhotovoltaikModel>();
		// --
		log.info("Parse SolarLogData ");
		List<String> content = IOUtils.readLines(data);
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (checkLine(line, sinceDate)) {
				pmAL.add(parseLine(line));
			}
		}
		return pmAL;
	}

	/**
	 * 
	 */
	private boolean checkLine(String line, Date compareDate) {
		// Valid Line ?
		if (line == null || line.startsWith("#")) {
			return false;
		}
		// Check the Age of the Data
		String parts[] = StringUtils.split(line, ';');
		if (parts.length == 13) {
			Date dateTime;
			try {
				dateTime = getTimeStamp(parts[0], parts[1]);
				if (dateTime.getTime() > compareDate.getTime()) {
					log.debug("Solar Data from :" + formatter.format(dateTime) + "  ist after lastRun: " + formatter.format(compareDate) + " -> OK");
					return true;
				} else {
					log.debug("Solar Data from :" + formatter.format(dateTime) + " ist before lastRun: " + formatter.format(compareDate)
							+ " I dont use this Data.");
				}
			} catch (ParseException e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private PhotovoltaikModel parseLine(String line) {
		PhotovoltaikModel result = new PhotovoltaikModel();
		String parts[] = StringUtils.split(line, ';');
		if (parts.length == 13) {
			try {
				result.setTimeStamp(getTimeStamp(parts[0], parts[1]));
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
			result.setWr(new Integer(parts[2]));
			result.setPac(new Integer(parts[3]));
			result.setDaySum(new Integer(parts[4]));
			result.setStatus(new Integer(parts[5]));
			result.setError(new Integer(parts[6]));
			result.setPdc1(new Integer(parts[7]));
			result.setPdc2(new Integer(parts[8]));
			result.setUdc1(new Integer(parts[9]));
			result.setUdc2(new Integer(parts[10]));
			result.setTemp(new Integer(parts[11]));
			result.setUac(new Integer(parts[12]));
			result.setPvMQTTConnStatus(PhotovoltaikModel.pvMQTTConnStatus_SUNRISE);
		}
		return result;
	}

	/**
	 * 
	 * @param date
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	private Date getTimeStamp(String date, String time) throws ParseException {

		return formatter.parse(date + " " + time);
	}

}
