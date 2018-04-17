/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.provider.BaseColumns;

/**
 * Schema
 * 
 * @author Clyde Zuber
 *
 */
public interface Schema {
	public static final int NOTIFICATION = 5757;
	public static final String PACKAGE = "edu.elon.cs.pillminder.";
	
	public static final String DB_NAME = "pillMinder.sqlite";
	public static final int DB_VERSION = 1;
	public static final String ID = BaseColumns._ID;
	public static final String SQL_DELETE = "delete ";
	public static final String SQL_SELECT = "select ";
	public static final String SQL_FROM = " from ";
	public static final String SQL_WHERE = " where ";
	public static final String SQL_AND = " and ";
	public static final String SQL_INT = " integer, ";
	public static final String SQL_INT_LAST = " integer)";
	public static final String SQL_TEXT = " text, ";
	public static final String SQL_TEXT_LAST = " text)";
	
	public static final String RX_TABLE = "Rx";
	public static final String RX_NUMBER = "RxNumber"; 
	public static final String RX_PHARM = "pharmacy"; 
	public static final String RX_PHONE = "pharmPhone";
	public static final String RX_DR = "doctor"; 
	public static final String RX_DISP = "dispensed"; 
	public static final String RX_NUM_PILLS = "numPills"; 
	public static final String RX_PILLS = "pills"; 
	public static final String RX_FREQUENCY = "frequency"; 
	public static final String RX_FREQ = "freq";
	public static final String RX_MED = "medication"; 
	public static final String RX_MG = "mg"; 
	public static final String RX_QTY = "quantity"; 
	public static final String RX_BRAND = "brandName"; 
	public static final String RX_REFILLS = "numRefills";
	public static final String RX_CUTOFF = "cutoffDate";
	public static final String RX_PHOTO = "photoURI";
	
	public static final String RX_ID = "Rx_id";
	
	public static final String HIST_TABLE = "history";
	public static final String HIST_DATE_TIME = "dateTime";
	public static final String HIST_COMP = "compliance";
	public static final int YES = 1;
	public static final int NO = 0;
	
	public static final String CURR_TABLE = "current";
	public static final String CURR_PI0 = "pi0";
	public static final String CURR_PI1 = "pi1";
	public static final String CURR_PI2 = "pi2";
	public static final String CURR_PI3 = "pi3";
	public static final String CURR_PI4 = "pi4";
	public static final String CURR_PI5 = "pi5";
	public static final String CURR_ADJ0 = "adj0";
	public static final String CURR_ADJ1 = "adj1";
	public static final String CURR_ADJ2 = "adj2";
	public static final String CURR_ADJ3 = "adj3";
	public static final String CURR_ADJ4 = "adj4";
	public static final String CURR_ADJ5 = "adj5";


	public static final String PREFS_DSN = "pillMinder.prefs";
	public static final String INFO_ALARMS = "true";
	public static final String INFO_WAKE = "wake";
	public static final String INFO_SLEEP = "sleep";
	public static final String INFO_LASTNAME = "lastName";
	public static final String INFO_FIRSTNAME = "firstName";
	public static final String INFO_DELAY = "delay";
	
	public static final String INTENT_SCH = "sch";
	public static final String INTENT_MED = "med";

}
