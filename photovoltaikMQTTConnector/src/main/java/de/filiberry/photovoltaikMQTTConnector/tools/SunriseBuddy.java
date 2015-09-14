package de.filiberry.photovoltaikMQTTConnector.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class SunriseBuddy {

	public static Logger log = Logger.getLogger(SunriseBuddy.class);
	private Location location = null;
	private SunriseSunsetCalculator calculator = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private DateFormat simpleTime = DateFormat.getTimeInstance(DateFormat.SHORT);

	/**
	 * 
	 * @param config
	 */
	public SunriseBuddy(Properties config) {
		location = new Location(config.getProperty("SUNRISE.LONGITUDE"), config.getProperty("SUNRISE.LATITUDE"));
		calculator = new SunriseSunsetCalculator(location, config.getProperty("SUNRISE.TIMEZONE"));
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isSunrise() {
		log.debug("is Sunrise is called ");
		//--
		Calendar sunrise = calculator.getCivilSunriseCalendarForDate(new GregorianCalendar());
		log.debug("Sunrise at " + dateFormat.format(sunrise.getTime()));
		Calendar sunset = calculator.getCivilSunsetCalendarForDate(new GregorianCalendar());
		log.debug("Sunset at " + dateFormat.format(sunset.getTime()));
		GregorianCalendar now = new GregorianCalendar();
		String nowText = simpleTime.format(now.getTime());
		if (now.after(sunrise) && now.before(sunset)) {
			log.info(nowText + " ist nach Sonnenaufg. und bevor SUnterg.");
			return true;
		}
		log.info(nowText + " ist vor SAufgang oder nach SUntergang");
		return false;
	}

}
