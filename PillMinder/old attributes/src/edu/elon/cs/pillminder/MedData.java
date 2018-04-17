/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.util.Comparator;

import android.app.PendingIntent;

/**
 * MedData - SchData extended to display medications
 * (main difference is sorting and toString method).
 * 
 * @author Clyde Zuber
 *
 */
public class MedData extends SchData {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of the Medication Data Object.
	 */
	public MedData() {
		super();
	}
	
	/**
	 * Creates an instance of the Medication Data Object.
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
	public MedData(long rxId, PendingIntent pendingIntent,
			int baseHour, int offset, int hourInt,
			String hourScheduled, String medication, String mg,
			String numPills, Freq freq,	String photoURI) {
		super(rxId, pendingIntent, baseHour, offset, hourInt,
				hourScheduled, medication, mg, numPills, freq,
				photoURI);
	}
	
	/**
	 * Sort by medication, then hour
	 */
	@Override
	public int compareTo(SchData that) {
		if (this.medication.equals(that.medication)) {
			return this.hourInt - that.hourInt;
		} else { 
			return this.medication.compareTo(that.medication);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dosage: {" + numPills + " of " + medication + " " +
				mg + "MG at " +	hourScheduled + "}";
	}

	/**
	 * Make a copy of the current object.
	 * 
	 * @return MedData 
	 */
	public MedData copy() {
		 return new MedData(rxId, pendingIntent, baseHour, offset,
				hourInt, hourScheduled,	medication, mg,	numPills,
				freq, photoURI);
	}

	/**
	 * Make a SchData copy of the current object.
	 * 
	 * @return SchData 
	 */
	public SchData schCopy() {
		return new SchData(rxId, pendingIntent, baseHour, offset,
				hourInt, hourScheduled,	medication, mg,	numPills,
				freq, photoURI);
	}
	

	/**
	 * Comparator for sorting in medicine order.
	 */
	public static Comparator<MedData> MedDataComparator = 
			new Comparator<MedData>() {
		public int compare(MedData obj1, MedData obj2) {
			return obj1.compareTo(obj2);
		}
	};
}
