/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * OnBootCompleted
 * 
 * @author Clyde Zuber
 *
 */
public class OnBootCompleted extends BroadcastReceiver {

	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		// create an intent for the PillMinder service
		Intent i = new Intent(context, PillMinderService.class);
		this.context.startService(i);
	}
}
