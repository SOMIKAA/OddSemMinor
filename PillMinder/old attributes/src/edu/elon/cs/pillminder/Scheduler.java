/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

/**
 * Scheduler
 * 
 * @author Clyde Zuber
 *
 */
public class Scheduler implements Schema {
	private Context context;
	private RxDatabaseHelper db;
	private SharedPreferences sharedPref;
	private AlarmManager am;
	
	protected static boolean alarms;
	private int delay;
	private int awakeHour;
	private int sleepHour;
	private long piReq[] = new long[6];
	private int adj[] = new int[6];
	private ArrayList<SchData>[] hour;
	private ArrayList<SchData> hourArrayList;
	private ArrayList<SchData> schHourList;
	private ArrayList<MedData> medHourList;
 	private MedData rx;
	private PendingIntent pi;
	private Cursor cursor;
	
	
	/**
	 * Default constructor for the Scheduler class.
	 * @param context 
	 */
	public Scheduler(Context service, RxDatabaseHelper dbHelper) {
		context = service;
		db = dbHelper;
		am = (AlarmManager) 
				context.getSystemService(Context.ALARM_SERVICE);
		
		sharedPref = context.getSharedPreferences(PREFS_DSN,
				Context.MODE_PRIVATE);
		alarms = sharedPref.getBoolean(INFO_ALARMS, true);
		delay = sharedPref.getInt(INFO_DELAY, 0);
		awakeHour = sharedPref.getInt(INFO_WAKE, 8);
		sleepHour = sharedPref.getInt(INFO_SLEEP, 0);
		schHourList = null;
		medHourList = null;
		updateSchedule();
	}

	/**
	 * @return the hour Schedule data
	 */
	public ArrayList<SchData>[] getHour() {
		return hour;
	}

	/**
	 * @return the flatHourList
	 */
	public ArrayList<SchData> getSchHourList() {
		return schHourList;
	}

	/**
	 * @return the medHourList
	 */
	public ArrayList<MedData> getMedHourList() {
		return medHourList;
	}

	/**
	 * @param awakePos the awakePos to set
	 */
	public void setAwakeHour(int awakePos) {
		awakeHour = awakePos;
	}

	/**
	 * @param sleepPos the sleepPos to set
	 */
	public void setSleepHour(int sleepPos) {
		sleepHour = sleepPos;
	}
	

	/**
	 * Clear out pending intents if the SchData information exists
	 */
	public void clearPI() {
		if (schHourList != null) {
			for (SchData c : schHourList) {
				am.cancel(c.pendingIntent);
				c.pendingIntent.cancel();
			}
		}
	}
	
	/** 
     * Find any persistent old alarms (if the service got cancelled
     * the SchData was wiped out) that got by clearPI().
     */
	private void cancelPI(long piReq) {
		Intent i = new Intent(context, AlarmReceiver.class);
		i.setAction(PACKAGE + piReq);
		pi = PendingIntent.getBroadcast(context, (int) piReq, i,
				PendingIntent.FLAG_NO_CREATE);
		if (pi == null) {
			return;
		} else {
		    am.cancel(pi);
		}	
	}
	

	/**
	 * Builds or rebuilds the ArrayList data structures that represent
	 * the schedule.
	 */
	@SuppressWarnings("unchecked")
	public void updateSchedule() {
		/**
		 * Clear out the old alarms, if any,
		 * by canceling the pending intents.
		 */
		clearPI();
		
		hour = (ArrayList<SchData>[]) new ArrayList[24];
		for (int h = 0; h < hour.length; ++h) {
			hourArrayList = new ArrayList<SchData>();
			hour[h] = hourArrayList;
		}	
		schHourList = new ArrayList<SchData>();
		medHourList = new ArrayList<MedData>();
				
		cursor = db.getCurrent();	
		buildSchedule();
	}
	
	private void buildSchedule() {
		for (cursor.moveToFirst(); !cursor.isAfterLast();
				cursor.moveToNext()) {
			rx = new MedData();
			rx.rxId = cursor.getLong(cursor.getColumnIndex(RX_ID));
		    rx.medication = cursor.getString(cursor.getColumnIndex(RX_MED));
		    rx.mg = cursor.getString(cursor.getColumnIndex(RX_MG));
		    rx.numPills = cursor.getString(cursor.getColumnIndex(RX_NUM_PILLS));
		    rx.photoURI = cursor.getString(cursor.getColumnIndex(RX_PHOTO));
		    
		    switch (cursor.getInt(cursor.getColumnIndex(RX_FREQ))) {
		    case 0:
		    	rx.freq = Freq.ONCE_DAY;
		    	break;
		    case 1:
		    	rx.freq = Freq.TWICE_DAY;
		     	break;
		    case 2:
		    	rx.freq = Freq.THREE_DAY;
		     	break;
		    case 3:
		    	rx.freq = Freq.FOUR_DAY;
		     	break;
		    case 4:
		    	rx.freq = Freq.EVERY_FOUR_HOURS;
		     	break;
		    }
		    
		    piReq[0] = cursor.getLong(cursor.getColumnIndex(CURR_PI0));
		    piReq[1] = cursor.getLong(cursor.getColumnIndex(CURR_PI1));
		    piReq[2] = cursor.getLong(cursor.getColumnIndex(CURR_PI2));
		    piReq[3] = cursor.getLong(cursor.getColumnIndex(CURR_PI3));
		    piReq[4] = cursor.getLong(cursor.getColumnIndex(CURR_PI4));
		    piReq[5] = cursor.getLong(cursor.getColumnIndex(CURR_PI5));
		    
		    adj[0] = cursor.getInt(cursor.getColumnIndex(CURR_ADJ0));
		    adj[1] = cursor.getInt(cursor.getColumnIndex(CURR_ADJ1));
		    adj[2] = cursor.getInt(cursor.getColumnIndex(CURR_ADJ2));
		    adj[3] = cursor.getInt(cursor.getColumnIndex(CURR_ADJ3));
		    adj[4] = cursor.getInt(cursor.getColumnIndex(CURR_ADJ4));
		    adj[5] = cursor.getInt(cursor.getColumnIndex(CURR_ADJ5));
		    			
		    addTimes();
		}
	}

	private void addTimes() {
		int nextTime = awakeHour;
		
		// everyone goes once, so do it once
		rx.baseHour = nextTime;
		rx.offset = 0;
		cancelPI(piReq[rx.offset]);
		addData(nextTime + adj[rx.offset]);
		
		switch (rx.freq) {
		case ONCE_DAY:
			break;
		case TWICE_DAY:
			nextTime = (nextTime + 12) % 24;
			rx.baseHour = nextTime;
			rx.offset = 1;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			break;
		case THREE_DAY:
			nextTime = (nextTime + 8) % 24;
			rx.baseHour = nextTime;
			rx.offset = 1;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 8) % 24;
			rx.baseHour = nextTime;
			rx.offset = 2;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			break;
		case FOUR_DAY:
			nextTime = (nextTime + 6) % 24;
			rx.baseHour = nextTime;
			rx.offset = 1;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 6) % 24;
			rx.baseHour = nextTime;
			rx.offset = 2;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 6) % 24;
			rx.baseHour = nextTime;
			rx.offset = 3;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			break;
		case EVERY_FOUR_HOURS:
			nextTime = (nextTime + 4) % 24;
			rx.baseHour = nextTime;
			rx.offset = 1;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 4) % 24;
			rx.baseHour = nextTime;
			rx.offset = 2;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 4) % 24;
			rx.baseHour = nextTime;
			rx.offset = 3;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 4) % 24;
			rx.baseHour = nextTime;
			rx.offset = 4;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			nextTime = (nextTime + 4) % 24;
			rx.baseHour = nextTime;
			rx.offset = 5;
			cancelPI(piReq[rx.offset]);
			addData(nextTime + adj[rx.offset]);
			break;
		}
	}

	private void addData(int nextTime) {
		if (nextTime < 0) {
			nextTime += 24;
		} else {
			nextTime = nextTime % 24;
		}
		// allow for bed time
		if (nextTime == (sleepHour + 1) % 24) {
			--nextTime;
			if (nextTime < 0) {
				nextTime += 24;
			}
		}
		rx.pendingIntent = setAlarm(nextTime);
		rx.hourInt = nextTime;
		rx.hourScheduled = friendlyHour(nextTime);
		hour[nextTime].add(rx);
		medHourList.add(rx);
		schHourList.add(rx.schCopy());
		rx = rx.copy();
	}

	private String friendlyHour(int schedHour) {
		String friendly = null;
		switch(schedHour) {
		case 0:
			friendly = "12 AM Midnight";
			break;
		case 1:
			friendly = "1 AM";
			break;
		case 2:
			friendly = "2 AM";
			break;
		case 3:
			friendly = "3 AM";
			break;
		case 4:
			friendly = "4 AM";
			break;
		case 5:
			friendly = "5 AM";
			break;
		case 6:
			friendly = "6 AM";
			break;
		case 7:
			friendly = "7 AM";
			break;
		case 8:
			friendly = "8 AM";
			break;
		case 9:
			friendly = "9 AM";
			break;
		case 10:
			friendly = "10 AM";
			break;
		case 11:
			friendly = "11 AM";
			break;
		case 12:
			friendly = "12 PM Noon";
			break;
		case 13:
			friendly = "1 PM";
			break;
		case 14:
			friendly = "2 PM";
			break;
		case 15:
			friendly = "3 PM";
			break;
		case 16:
			friendly = "4 PM";
			break;
		case 17:
			friendly = "5 PM";
			break;
		case 18:
			friendly = "6 PM";
			break;
		case 19:
			friendly = "7 PM";
			break;
		case 20:
			friendly = "8 PM";
			break;
		case 21:
			friendly = "9 PM";
			break;
		case 22:
			friendly = "10 PM";
			break;
		case 23:
			friendly = "11 PM";
			break;
		}
		return friendly;
	}
	
	private PendingIntent setAlarm(int nextTime) {
		
		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, nextTime);
		time.set(Calendar.MINUTE, delay);
		time.set(Calendar.SECOND, 0);
		long aTime = time.getTimeInMillis();
		long cTime = System.currentTimeMillis();
		if (aTime < cTime) {
			aTime += AlarmManager.INTERVAL_DAY;
		}
		
		Intent i = new Intent(context, AlarmReceiver.class);
		i.putExtra(ID, rx.rxId);
		i.setAction(PACKAGE + cTime);
		pi = PendingIntent.getBroadcast(context, (int) cTime, i, 0);
		if (alarms) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, aTime,
					AlarmManager.INTERVAL_DAY, pi);
		}
		piReq[rx.offset] = cTime;
		db.updatePiCurrent(rx.rxId, piReq);
		return(pi);
	}
}
