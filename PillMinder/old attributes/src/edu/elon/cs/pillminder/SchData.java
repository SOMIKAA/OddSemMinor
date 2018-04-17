/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.io.Serializable;
import java.util.Comparator;

import android.app.PendingIntent;


/**
 * SchData
 * 
 * @author Clyde Zuber
 *
 */
public class SchData implements Serializable, Comparable<SchData> {
	
	private static final long serialVersionUID = 1L;
	protected long rxId;
	protected PendingIntent pendingIntent;
	protected int baseHour;  			// without adjustment applied
	protected int offset;    			// iterates doses per day
	protected int hourInt;   			// hour scheduled in int form
	protected String hourScheduled;
	protected String medication;
	protected String mg;
	protected String numPills;
	protected Freq freq;
	protected String photoURI;
	

	/**
	 * Creates an instance of the Scheduler Data Object.
	 */
	public SchData() {
		super();
		// Assume values initialized by friends
	}
	
	/**
	 * Creates an instance of the Schedule Data Object.
	 * 
	 * @param rxId
	 * @param pendingIntent
	 * @param baseHour
	 * @param offset
	 * @param hourInt
	 * @param hourScheduled
	 * @param medication
	 * @param mg
	 * @param numPills
	 * @param freq
	 * @param photoURI
	 */
	public SchData(long rxId, PendingIntent pendingIntent,
			int baseHour, int offset, int hourInt,
			String hourScheduled, String medication, String mg,
			String numPills, Freq freq,	String photoURI) {
		super();
		this.rxId = rxId;
		this.pendingIntent = pendingIntent;
		this.baseHour = baseHour;
		this.offset = offset;
		this.hourInt = hourInt;
		this.hourScheduled = hourScheduled;
		this.medication = medication;
		this.mg = mg;
		this.numPills = numPills;
		this.freq = freq;
		this.photoURI = photoURI;
	}
	
	/**
	 * Sort by hour, then medication
	 */
	@Override
	public int compareTo(SchData that) {
		if (this.hourInt == that.hourInt) {
			return this.medication.compareTo(that.medication);
		} else {
			return this.hourInt - that.hourInt;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Scheduled: [At " +
				hourScheduled + " take " + 
				numPills + " of " + 
				medication + " " +
				mg + "MG]";
	}

	
	/**
	 * Comparator for sorting in schedule order.
	 */
	public static Comparator<SchData> SchDataComparator =
			new Comparator<SchData>() {
		public int compare(SchData obj1, SchData obj2) {
			return obj1.compareTo(obj2);
		}
	};
}
