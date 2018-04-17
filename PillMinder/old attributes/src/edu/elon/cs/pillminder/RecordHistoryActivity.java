/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;

/**
 * RecordHistoryActivity - intented from AlarmReceiver
 * 
 * @author Clyde Zuber
 * 
 */
public class RecordHistoryActivity extends Activity
	implements OnDismissListener, OnCancelListener, Schema {
	
	private Context context;
	private PillMinderService minderService;
	private boolean serviceBound = false;
	private RxDatabaseHelper db;
	private Cursor cursor;
	
	private String dateTime;
	private long rxId;
	private BitmapDrawable bitmapDrawable;
	private MediaPlayer alarmSound;
	private Vibrator vibrator; 
	private AlertDialog.Builder msg;
	private boolean cancelled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rec_history);
		context = this;
		
		Intent myI = getIntent();
		rxId = myI.getLongExtra(ID, 0);
		
		dateTime = Calendar.getInstance().getTime().toString();
		if (serviceBound) {
			queryDB();
		} else {
			Intent i = new Intent(context, PillMinderService.class);
			i.setAction(PACKAGE + dateTime);
			bindService(i, myConnection, Context.BIND_AUTO_CREATE);
		}    
		alarmSound = MediaPlayer.create(context,
				Settings.System.DEFAULT_ALARM_ALERT_URI);
		vibrator = (Vibrator) 
				context.getSystemService(Context.VIBRATOR_SERVICE);
	    
	    alarmSound.start();
	    vibrator.vibrate(300);
	}
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    alarmSound.stop();
	    if (serviceBound) {
	        unbindService(myConnection);
	        serviceBound = false;
	    }
	}
	
	private void queryDB() {
		final SchData rx = new SchData();
		db = minderService.getDB();
        cursor = db.getRxById(rxId);
        cursor.moveToFirst();

	    rx.medication = cursor.getString(
	    		cursor.getColumnIndex(RX_MED));
	    rx.mg = cursor.getString(
	    		cursor.getColumnIndex(RX_MG));
	    rx.numPills = cursor.getString(
	    		cursor.getColumnIndex(RX_NUM_PILLS));
	    rx.photoURI = cursor.getString(
	    		cursor.getColumnIndex(RX_PHOTO));
	    
	    /* Get drawable bitmap from URI */
		Uri photoUri = Uri.parse(rx.photoURI);
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), photoUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
		} catch (IOException e) {
			e.printStackTrace();
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
		}
		bitmap = Bitmap.createScaledBitmap(bitmap, 400, 450, false);		
		bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
	    
	    dateTime = Calendar.getInstance().getTime().toString();
	    takePillDialog(rx);
	}
	
	private void takePillDialog(SchData rx) {
		msg = new AlertDialog.Builder(context);
		msg.setTitle(getText(R.string.rx_reminder));
		msg.setIcon(bitmapDrawable);
		msg.setMessage(getText(R.string.take) + rx.numPills + 
				getText(R.string.of) + rx.medication + " " + rx.mg +
				getText(R.string.respond));
		
		msg.setPositiveButton(getText(R.string.done),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				alarmSound.stop();
				db.insertHistory(dateTime, YES, rxId);
				PmWakeLock.releaseWakeLock();
				dialog.dismiss();
				finish();
			}
		});

		msg.setNeutralButton(getText(R.string.wait),
				new DialogInterface.OnClickListener() {
			/**
			 * When alarm is delayed, it is not cancellable.
			 * No updates area made to db or schData. 
			 */
			public void onClick(DialogInterface dialog, int id) {
				long time = System.currentTimeMillis() +
						AlarmManager.INTERVAL_HOUR;
				alarmSound.stop();
				AlarmManager am = (AlarmManager)
						context.getSystemService(Context.ALARM_SERVICE);
				Intent i = new Intent(context, AlarmReceiver.class);
				i.putExtra(ID, rxId);
				i.setAction(PACKAGE + time);
				PendingIntent pi = PendingIntent.getBroadcast(context, 
						(int) time, i, 0);
				am.set(AlarmManager.RTC_WAKEUP, time, pi);
				PmWakeLock.releaseWakeLock();
				dialog.dismiss();		
				finish();
			}
		});
		
		msg.setNegativeButton(getText(R.string.skip),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				alarmSound.stop();
				db.insertHistory(dateTime, NO, rxId);
				PmWakeLock.releaseWakeLock();
				dialog.dismiss();
				finish();
			}
		});
		msg.setOnDismissListener(this);
		msg.setOnCancelListener(this);
		msgShow();
	}
	
	public void msgShow() {
		cancelled = false;
		msg.show();
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		alarmSound.stop();
		cancelled = true;
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		// You have to respond to the dialog..
		if (cancelled) {
			msgShow();
		}
	}
	
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
	        queryDB();
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
