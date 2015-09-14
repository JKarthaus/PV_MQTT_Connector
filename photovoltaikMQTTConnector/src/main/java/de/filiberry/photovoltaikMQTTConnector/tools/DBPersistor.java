package de.filiberry.photovoltaikMQTTConnector.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

import de.filiberry.photovoltaikMQTTConnector.model.PhotovoltaikModel;

public class DBPersistor {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yy__HH_mm_ss");
	private ObjectContainer objectContainer = null;

	/**
	 * 
	 */
	public DBPersistor() {
		String filename = sdf.format(new Date()) + ".db";
		objectContainer = Db4o.openFile(filename);

	}

	/**
	 * 
	 * @param pmAL
	 */
	public void PersistNewData(ArrayList<PhotovoltaikModel> pmAL) {
		for (int i = 0; i < pmAL.size(); i++) {
			objectContainer.store(pmAL.get(i));
		}
	}

	/**
	 * 
	 */
	public void closeDB() {
		objectContainer.close();
	}
}
