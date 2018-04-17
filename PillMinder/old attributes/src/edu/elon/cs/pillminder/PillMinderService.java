/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * PillMinderService
 * 
 * @author Clyde Zuber
 *
 */
public class PillMinderService extends Service implements Schema {
	private Context context;
	
	private NotificationManager pillMinderServiceNM;
	private Notification n;
	private Resources res;
	
	private RxDatabaseHelper db;
	private Scheduler sched;
	
	private final IBinder myBinder = new LocalBinder();
	
	
	/**
     * Class for clients to access.  Because we know this service
     * always runs in the same process as its clients, we don't need
     * to deal with IPC.
     */
    public class LocalBinder extends Binder {
        PillMinderService getService() {
            return PillMinderService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		
		/**
		 * Display a notification about service starting.
		 * It also puts an icon in the status bar.
		 */
		Intent notificationIntent = new Intent(context,
				PillMinderService.class);
		PendingIntent contentIntent =
				PendingIntent.getActivity(context, 0,
		        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		pillMinderServiceNM = (NotificationManager)
				getSystemService(NOTIFICATION_SERVICE);

		res = getResources();       
        Notification.Builder builder =
        		new Notification.Builder(context);
        
        builder.setContentIntent(contentIntent)
        	.setSmallIcon(R.drawable.pill_minder)
        	.setTicker(res.getString(R.string.pill_service_started))
        	.setWhen(System.currentTimeMillis())
        	.setAutoCancel(false)
        	.setContentTitle(res.getString(R.string.app_name))
        	.setContentText(res.getString(
        			R.string.pill_service_started));
        
        n = builder.build();
        pillMinderServiceNM.notify(NOTIFICATION, n);
        
        /**
         * Database/Scheduler initialization.
         */
        db = new RxDatabaseHelper(context);
        sched = new Scheduler(context, db);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(res.getString(R.string.app_name), "Received start id " +
        	startId + ": " + intent);
       
        // continue running service until explicitly stopped 
        return START_STICKY;
    }
	
	@Override
    public void onDestroy() {
        // Cancel the persistent notification.
		pillMinderServiceNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(context, R.string.pill_service_stopped, 
        		Toast.LENGTH_LONG).show();
    }

	/**
	 * Create a new schedule data structure.
	 */
	public void renewSchedule() {
		sched.clearPI();
		sched = new Scheduler(context, db);
	}

	/**
	 * @return the db handle to the database helper.
	 */
	public RxDatabaseHelper getDB() {
		return db;
	}

	/**
	 * @return the sched handle to the Scheduler.
	 */
	public Scheduler getSched() {
		return sched;
	}
}
