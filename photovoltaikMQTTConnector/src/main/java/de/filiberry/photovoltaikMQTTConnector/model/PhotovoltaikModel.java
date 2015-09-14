package de.filiberry.photovoltaikMQTTConnector.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PhotovoltaikModel {
	private Date timeStamp;
	private int wr;
	private int pac;
	private int daySum;
	private int status;
	private int error;
	private int pdc1;
	private int pdc2;
	private int udc1;
	private int udc2;
	private int temp;
	private int uac;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getWr() {
		return wr;
	}

	public void setWr(int wr) {
		this.wr = wr;
	}

	public int getPac() {
		return pac;
	}

	public void setPac(int pac) {
		this.pac = pac;
	}

	public int getDaySum() {
		return daySum;
	}

	public void setDaySum(int daySum) {
		this.daySum = daySum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int getPdc1() {
		return pdc1;
	}

	public void setPdc1(int pdc1) {
		this.pdc1 = pdc1;
	}

	public int getPdc2() {
		return pdc2;
	}

	public void setPdc2(int pdc2) {
		this.pdc2 = pdc2;
	}

	public int getUdc1() {
		return udc1;
	}

	public void setUdc1(int udc1) {
		this.udc1 = udc1;
	}

	public int getUdc2() {
		return udc2;
	}

	public void setUdc2(int udc2) {
		this.udc2 = udc2;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public int getUac() {
		return uac;
	}

	public void setUac(int uac) {
		this.uac = uac;
	}

}
