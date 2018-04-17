/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


/**
 * MinderActivity
 * 
 * @author Clyde Zuber
 * 
 */
public class MinderActivity extends Activity implements Schema {

	private Context context;
	private SharedPreferences sharedPref;
	
	protected static PillMinderService minderService;
	protected static boolean serviceBound = false;
	protected static boolean alarms;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		/* get default */
		sharedPref = context.getSharedPreferences(PREFS_DSN,
				Context.MODE_PRIVATE);
		alarms = sharedPref.getBoolean(INFO_ALARMS, true);
		
		if (alarms) {
			setTheme(R.style.AppTheme);
		} else {
			setTheme(R.style.OffTheme);
		}
		setContentView(R.layout.activity_minder);
		bindService(new Intent(context, PillMinderService.class),
				myConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (serviceBound) {
			unbindService(myConnection);
			serviceBound = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; adds items to the action bar if present.
		getMenuInflater().inflate(R.menu.minder, menu);
		return true;
	}

	/******************************************************************
	 * Settings/Option Menu Selections
	 */
	public boolean intentInfoProfile(MenuItem menuItem) {
		Intent i = new Intent(context, InfoProfileActivity.class);
		startActivity(i);
		return true;
	}

	public boolean intentRxLabel(MenuItem menuItem) {
		Intent i = new Intent(context, RxLabelActivity.class);
		startActivity(i);
		return true;
	}

	public boolean intentCurrentRx(MenuItem menuItem) {
		Intent i = new Intent(context, CurrentRxActivity.class);
		startActivity(i);
		return true;
	}

	public boolean intentDisplaySchedule(MenuItem menuItem) {
		Intent i = new Intent(context, DisplayDailyActivity.class);
		i.setAction(PACKAGE + INTENT_SCH);
		startActivity(i);
		return true;
	}

	public boolean intentDisplayMeds(MenuItem menuItem) {
		Intent i = new Intent(context, DisplayDailyActivity.class);
		i.setAction(PACKAGE + INTENT_MED);
		startActivity(i);
		return true;
	}
	
	public boolean intentHistoryRx(MenuItem menuItem) {
		Intent i = new Intent(context, HistoryRxActivity.class);
		startActivity(i);
		return true;
	}

	/**
	 * END Settings/Option Menu Selections
	 *****************************************************************/

	/******************************************************************
	 * Button listener Action
	 */

	public boolean buttonInfoProfile(View view) {
		Intent i = new Intent(context, InfoProfileActivity.class);
		startActivity(i);
		return true;
	}

	public boolean buttonRxLabel(View view) {
		Intent i = new Intent(context, RxLabelActivity.class);
		startActivity(i);
		return true;
	}

	public boolean buttonCurrentRx(View view) {
		Intent i = new Intent(context, CurrentRxActivity.class);
		startActivity(i);
		return true;
	}

	public boolean buttonDisplaySchedule(View view) {
		Intent i = new Intent(context, DisplayDailyActivity.class);
		i.setAction(PACKAGE + INTENT_SCH);
		startActivity(i);
		return true;
	}
	
	public boolean buttonDisplayMeds(View view) {
		Intent i = new Intent(context, DisplayDailyActivity.class);
		i.setAction(PACKAGE + INTENT_MED);
		startActivity(i);
		return true;
	}
	
	public boolean buttonHistoryRx(View view) {
		Intent i = new Intent(context, HistoryRxActivity.class);
		startActivity(i);
		return true;
	}

	
	/******************************************************************
	 * PRIVATE methods/routines
	 */
	private ServiceConnection myConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			/**
	    	 * This is called when the connection with the service has
	    	 * been established, giving us the service object we can
	    	 * use to interact with the service.  Because we have bound
	    	 * to an explicit service that we know is running in our
	    	 * own process, we can cast its IBinder to a concrete class
	    	 * and directly access it.
	    	 */
			minderService = ((PillMinderService.LocalBinder) service)
					.getService();
			serviceBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			/**
	    	 * This is called when the connection with the service has
	    	 * been unexpectedly disconnected -- that is, its process
	    	 * crashed.  Because it is running in our same process,
	    	 * we should never see this happen.
	    	 */
			minderService = null;
			serviceBound = false;
		}
	};
}
