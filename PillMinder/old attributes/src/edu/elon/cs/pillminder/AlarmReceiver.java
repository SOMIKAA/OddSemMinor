/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * AlarmReceiver
 * 
 * @author Clyde Zuber
 *
 */
public class AlarmReceiver extends BroadcastReceiver implements Schema {

	@Override
	public void onReceive(Context context, Intent i) {
		long rxId = i.getLongExtra(ID, 0);
		
		PmWakeLock.acquireWakeLock(context);
		
		Intent intent = new Intent(context,
				RecordHistoryActivity.class);
		intent.putExtra(ID, rxId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(PACKAGE + System.currentTimeMillis());
		context.startActivity(intent);
	}
}
