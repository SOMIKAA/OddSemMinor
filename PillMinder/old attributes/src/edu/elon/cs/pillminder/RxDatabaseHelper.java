/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * RxDatabaseHelper
 * 
 * @author Clyde Zuber
 * 
 */
public class RxDatabaseHelper extends SQLiteOpenHelper
		implements Schema {
	
	/**
	 * Default constructor for the RxDatabaseHelper class that
	 * substitutes our default values to the super.
	 * 
	 * @param context 
	 */
	public RxDatabaseHelper(Context service) {
		super(service, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " + RX_TABLE + " (" + ID +
			" INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RX_NUMBER + SQL_INT +
			RX_PHARM + SQL_TEXT +
			RX_PHONE + SQL_TEXT +
			RX_DR + SQL_TEXT +
			RX_DISP + SQL_TEXT +
			RX_NUM_PILLS + SQL_TEXT +
			RX_PILLS + SQL_INT +
			RX_FREQUENCY + SQL_TEXT +
			RX_FREQ + SQL_INT +
			RX_MED + SQL_TEXT +
			RX_MG + SQL_INT +
			RX_QTY + SQL_INT +
			RX_BRAND + SQL_TEXT +
			RX_REFILLS + SQL_INT +
			RX_CUTOFF + SQL_TEXT +
			RX_PHOTO + SQL_TEXT_LAST);
		
		db.execSQL("CREATE TABLE " + HIST_TABLE + " (" + ID +
			" INTEGER PRIMARY KEY AUTOINCREMENT, " +
			HIST_DATE_TIME + SQL_TEXT +
			HIST_COMP + SQL_INT +
			RX_ID + SQL_INT +
			"FOREIGN KEY(" + RX_ID + ") REFERENCES " + RX_TABLE +
			"(" + ID + "))");
		
		db.execSQL("CREATE TABLE " + CURR_TABLE + " (" + ID +
			" INTEGER PRIMARY KEY AUTOINCREMENT, " +
			CURR_PI0 + SQL_INT +
			CURR_PI1 + SQL_INT +
			CURR_PI2 + SQL_INT +
			CURR_PI3 + SQL_INT +
			CURR_PI4 + SQL_INT +
			CURR_PI5 + SQL_INT +
			CURR_ADJ0 + SQL_INT +
			CURR_ADJ1 + SQL_INT +
			CURR_ADJ2 + SQL_INT +
			CURR_ADJ3 + SQL_INT +
			CURR_ADJ4 + SQL_INT +
			CURR_ADJ5 + SQL_INT +
			RX_ID + SQL_INT +
			"FOREIGN KEY(" + RX_ID + ") REFERENCES " + RX_TABLE +
			"(" + ID + "))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
	}
	
	public long insertRx(String rxNumber, String pharmacy,
			String pharmPhone, String doctor, String dispensed,
			String numPills, int pills,	String frequency, int freq,
			String medication, String mg, String quantity,
			String brandName, String numRefills, String cutoffDate,
			Uri photoURI) {
		long rxRow;
		ContentValues cv = new ContentValues();
		
		cv.put(RX_NUMBER, rxNumber); 
		cv.put(RX_PHARM, pharmacy); 
		cv.put(RX_PHONE, pharmPhone);
		cv.put(RX_DR, doctor); 
		cv.put(RX_DISP, dispensed); 
		cv.put(RX_NUM_PILLS, numPills); 
		cv.put(RX_PILLS, pills); 
		cv.put(RX_FREQUENCY, frequency); 
		cv.put(RX_FREQ, freq); 
		cv.put(RX_MED, medication); 
		cv.put(RX_MG, mg); 
		cv.put(RX_QTY, quantity); 
		cv.put(RX_BRAND, brandName); 
		cv.put(RX_REFILLS, numRefills);
		cv.put(RX_CUTOFF, cutoffDate);
		cv.put(RX_PHOTO, photoURI.toString());
		
		rxRow = getWritableDatabase().insert(RX_TABLE, null, cv);
		insertCurrent(rxRow);
		return rxRow;
	}
	
	public long insertCurrent(long rxRow) {
		ContentValues cv = new ContentValues();
		
		cv.put(RX_ID, rxRow);
		cv.put(CURR_PI0, 0);
		cv.put(CURR_PI1, 0);
		cv.put(CURR_PI2, 0);
		cv.put(CURR_PI3, 0);
		cv.put(CURR_PI4, 0);
		cv.put(CURR_PI5, 0);
		cv.put(CURR_ADJ0, 0); 
		cv.put(CURR_ADJ1, 0); 
		cv.put(CURR_ADJ2, 0); 
		cv.put(CURR_ADJ3, 0);
		cv.put(CURR_ADJ4, 0);
		cv.put(CURR_ADJ5, 0); 
		return getWritableDatabase().insert(CURR_TABLE, null, cv);
	}
	
	public long insertCurrent(long rxRow, long piReq[], int adj[]) {
		ContentValues cv = new ContentValues();
		
		cv.put(RX_ID, rxRow); 
		cv.put(CURR_PI0, piReq[0]);
		cv.put(CURR_PI1, piReq[1]);
		cv.put(CURR_PI2, piReq[2]);
		cv.put(CURR_PI3, piReq[3]);
		cv.put(CURR_PI4, piReq[4]);
		cv.put(CURR_PI5, piReq[5]);
		cv.put(CURR_ADJ0, adj[0]); 
		cv.put(CURR_ADJ1, adj[1]); 
		cv.put(CURR_ADJ2, adj[2]); 
		cv.put(CURR_ADJ3, adj[3]);
		cv.put(CURR_ADJ4, adj[4]);
		cv.put(CURR_ADJ5, adj[5]);
		return getWritableDatabase().insert(CURR_TABLE, null, cv);
	}
	
	public long insertHistory(String dateTime, int compliance,
			long rxRow) {
		ContentValues cv = new ContentValues();
		
		cv.put(HIST_DATE_TIME, dateTime);
		cv.put(HIST_COMP, compliance);
		cv.put(RX_ID, rxRow); 
		return getWritableDatabase().insert(HIST_TABLE, null, cv);
	}
	
	public Cursor getCurrent() {
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + CURR_TABLE + ", " + RX_TABLE + 
						SQL_WHERE + RX_ID + "=" + RX_TABLE + "." + ID;
		return getReadableDatabase().rawQuery(sql, null);	
	}
	
	
	public Cursor getCurrentByRxid(long rxId) {
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + CURR_TABLE + 
						SQL_WHERE + RX_ID + "= " + rxId; 
		return getReadableDatabase().rawQuery(sql, null);	
	}
	
	public int delCurrentById(String id) {
		String where;
		
		where = ID + " = " + id;
		return getWritableDatabase().delete(CURR_TABLE, where, null); 
	}
	
	/**
	 * Sanity check the adjustment and proceed if allowed.
	 * 
	 * @param rxId - the RX_ID of the CURR record (not the ID)
	 * @param freq - how many times per day taken
	 * @param offset - which ADJ? is to be adjusted
	 * 					 (append value of offset for ?)
	 * @param adj - adjustment +/- 1 to be added to ADJ?
	 */
	public void adjustCurrent(long rxId, Freq freq, int offset,
			int adj) {
		int maxAdj;
		int currAdj[] = new int[6];
		ContentValues cv = new ContentValues();
		String sql = SQL_SELECT + "*" + 
				SQL_FROM + CURR_TABLE +
				SQL_WHERE + RX_ID + "=" + rxId;
		Cursor selection = getReadableDatabase().rawQuery(sql, null);
		selection.moveToFirst();
	    int currId = selection.getInt(selection.getColumnIndex(ID));
	    currAdj[0] = selection.getInt(
	    		selection.getColumnIndex(CURR_ADJ0));
	    currAdj[1] = selection.getInt(
	    		selection.getColumnIndex(CURR_ADJ1));
	    currAdj[2] = selection.getInt(
	    		selection.getColumnIndex(CURR_ADJ2));
	    currAdj[3] = selection.getInt(
	    		selection.getColumnIndex(CURR_ADJ3));
	    currAdj[4] = selection.getInt(
	    		selection.getColumnIndex(CURR_ADJ4));
	    currAdj[5] = selection.getInt(
	    		selection.getColumnIndex(CURR_ADJ5));
	       
	    /**
	     * Use return to prevent update to database
	     * whenever it's an invalid request.
	     * 
	     * These values actually allow the invalid situation where one
	     * pill's time is adjusted up and the next one down, thus
	     * taking two at the same time.
	     * 
	     *  Note that only one value is ever changed at a time.
	     */
	    switch (freq) {
		case ONCE_DAY:
			maxAdj = 18;
			currAdj[offset] += adj;
			if (currAdj[offset] > maxAdj || currAdj[offset] < -maxAdj) {
	    		return;	
			}
			break;
			
		case TWICE_DAY:
			maxAdj = 6;
			currAdj[offset] += adj;
			if (currAdj[offset] > maxAdj || currAdj[offset] < -maxAdj) {
	    		return;
			}
			break;
			
		case THREE_DAY:
			maxAdj = 4;
			currAdj[offset] += adj;
			if (currAdj[offset] > maxAdj || currAdj[offset] < -maxAdj) {
	    		return;	
			}
			break;
			
		case FOUR_DAY:
			maxAdj = 3;
			currAdj[offset] += adj;
			if (currAdj[offset] > maxAdj || currAdj[offset] < -maxAdj) {
	    		return;	
			}
			break;
			
		case EVERY_FOUR_HOURS:
			maxAdj = 2;
			currAdj[offset] += adj;
			if (currAdj[offset] > maxAdj || currAdj[offset] < -maxAdj) {
	    		return;	
			}
			break;
	    }
	    
	    /* put the row back together and update it */
		String where = ID + "=" + currId;
		cv.put(CURR_ADJ0, currAdj[0]);
		cv.put(CURR_ADJ1, currAdj[1]);
		cv.put(CURR_ADJ2, currAdj[2]);
		cv.put(CURR_ADJ3, currAdj[3]);
		cv.put(CURR_ADJ4, currAdj[4]);
		cv.put(CURR_ADJ5, currAdj[5]);

		getWritableDatabase().update(CURR_TABLE, cv, where, null);
	}
	
	
	/**
	 * Update the pending intent request code (unique id) used to set
	 * the alarm.
	 * 
	 * @param rxId - the RX_ID of the CURR record (not the ID)
	 * @param piReq - the unique identifier to update in the record
	 */
	public void updatePiCurrent(long rxId, long piReq[]) {
		ContentValues cv = new ContentValues();
		String sql = SQL_SELECT + "*" + 
				SQL_FROM + CURR_TABLE +
				SQL_WHERE + RX_ID + "=" + rxId;
		Cursor selection = getReadableDatabase().rawQuery(sql, null);
		selection.moveToFirst();
	    int currId = selection.getInt(selection.getColumnIndex(ID));
		
	    /* put the row back together and update it */
		String where = ID + "=" + currId;
		cv.put(CURR_PI0, piReq[0]);
		cv.put(CURR_PI1, piReq[1]);
		cv.put(CURR_PI2, piReq[2]);
		cv.put(CURR_PI3, piReq[3]);
		cv.put(CURR_PI4, piReq[4]);
		cv.put(CURR_PI5, piReq[5]);
		
		getWritableDatabase().update(CURR_TABLE, cv, where, null);
	}
	
	
	public Cursor getRxByCurrentId(String id) {
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + CURR_TABLE + ", " + RX_TABLE + 
						SQL_WHERE + CURR_TABLE + "." + ID + "=" + id +
						SQL_AND + RX_ID + "=" + RX_TABLE + "." + ID;
		return getReadableDatabase().rawQuery(sql, null);	
	}
	
	public Cursor getRxById(long id) {
//		Alternate code which also works:		
//		String where = ID + " = " + id;
//		return getReadableDatabase().query(RX_TABLE, null, where, null,
//			null, null, null);
		
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + RX_TABLE + 
						SQL_WHERE + ID + " = " + id;
		return getReadableDatabase().rawQuery(sql, null);
	}
	
	public Cursor getRxByNumber(String rxNumber) {
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + RX_TABLE + 
						SQL_WHERE + RX_NUMBER + " = " + rxNumber;
		return getReadableDatabase().rawQuery(sql, null);
	}
	
	public Cursor getHistory() {
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + HIST_TABLE + ", " + RX_TABLE + 
						SQL_WHERE + RX_ID + "=" + RX_TABLE + "." + ID;
		return getReadableDatabase().rawQuery(sql, null);	
	}
	
	public Cursor getHistoryById(String id) {
		String sql = SQL_SELECT + "*" + 
						SQL_FROM + HIST_TABLE + ", " + RX_TABLE + 
						SQL_WHERE + HIST_TABLE + "." + ID + "=" + id +
						SQL_AND + RX_ID + "=" + RX_TABLE + "." + ID;
		return getReadableDatabase().rawQuery(sql, null);	
	}
	
	public void delHistory() {
		getWritableDatabase().execSQL("delete from " + HIST_TABLE);	
	}
}
