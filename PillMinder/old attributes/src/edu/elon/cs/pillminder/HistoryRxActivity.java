/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * HistoryRxActivity
 * 
 * @author Clyde Zuber
 * 
 */
public class HistoryRxActivity extends Activity implements Schema {

	private Context context;
	private RxDatabaseHelper db;
	private ListView listView;
	private SimpleCursorAdapter cursorAdapter;
	
	private Cursor cursor;
	private String[] fromColumns = {HIST_TABLE + "." + ID,
			HIST_DATE_TIME, HIST_COMP, RX_NUMBER, RX_MED, RX_MG};
	private int[] toViews = {R.id.idGone, R.id.dateTimeShow,
			R.id.compShow, R.id.rxShow, R.id.medShow, R.id.mgShow};
	
	private AlertDialog.Builder msg;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (MinderActivity.alarms) {
			setTheme(R.style.AppTheme);
		} else {
			setTheme(R.style.OffTheme);
		}
		setContentView(R.layout.display_activities);
		context = this;
		if (MinderActivity.serviceBound) {
			db = MinderActivity.minderService.getDB();
		} else {
			Toast.makeText(context, R.string.disconnected_pill_service,
		        Toast.LENGTH_LONG).show();
			finish();
		}
		
		listView = (ListView) findViewById(R.id.displayListView);
        
		cursor = db.getHistory();
		cursorAdapter = new SimpleCursorAdapter(context, 
				R.layout.listitem_historyrx, cursor, fromColumns,
				toViews, 0);
		/**
		 * Change the compliance column from 1 to taken, 0 to missed.
		 */
		cursorAdapter.setViewBinder(new ViewBinder() {
		    public boolean setViewValue(View aView, Cursor aCursor,
		    		int aColumnIndex) {

		        if (aColumnIndex == 2) {
		        	String compliance = aCursor.getString(aColumnIndex);
		            TextView textView = (TextView) aView;
		            if (compliance.equals("1")) {
		            	textView.setText(R.string.taken);
		            } else {
		            	textView.setText(R.string.missed);
		            }
		            return true;
		         }
		         return false;
		    }
		});
		
		listView.setAdapter(cursorAdapter);
		
		listView.setOnItemClickListener(selectHist);
		listView.setOnItemLongClickListener(deleteAllHist);
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
		finish();
		return true;
	}

	public boolean intentRxLabel(MenuItem menuItem) {
		Intent i = new Intent(context, RxLabelActivity.class);
		startActivity(i);
		finish();
		return true;
	}
	
	public boolean intentCurrentRx(MenuItem menuItem) {
		Intent i = new Intent(context, CurrentRxActivity.class);
		startActivity(i);
		finish();
		return true; 
	}
	
	public boolean intentDisplaySchedule(MenuItem menuItem) {
		Intent i = new Intent(context, DisplayDailyActivity.class);
		i.setAction(PACKAGE + INTENT_SCH);
		startActivity(i);
		finish();
		return true; 
	}
	
	public boolean intentDisplayMeds(MenuItem menuItem) {
		Intent i = new Intent(context, DisplayDailyActivity.class);
		i.setAction(PACKAGE + INTENT_MED);
		startActivity(i);
		finish();
		return true; 
	}
	
	public boolean intentHistoryRx(MenuItem menuItem) {
		return true; 
	}
	/**
	 * END Settings/Option Menu Selections
	 *****************************************************************/
	
	/******************************************************************
	 * 	 PRIVATE methods/routines
	 */
	
	private boolean historyArchive() {
		File file;
		PrintWriter out;
		String str;

		String state = Environment.getExternalStorageState();
		// creates the correct filename and path for saving
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			File path = getExternalFilesDir("HistArch");

			// Create filename from date and time
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd-HH_mm_ss", Locale.US);
			Date now = new Date();
			String saveName = formatter.format(now) + ".txt";

			file = new File(path, saveName);
		} else {
			return false;
		}

		try {
			out = new PrintWriter(file);

			cursor = db.getHistory();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); 
					cursor.moveToNext()) {
				str = cursor.getString(
						cursor.getColumnIndex(HIST_DATE_TIME)) + " ";
				if (cursor.getInt(
						cursor.getColumnIndex(HIST_COMP)) == 1) {
	            	str += getString(R.string.taken);
	            } else {
	            	str += getString(R.string.missed);
	            }
				str += " " + cursor.getString(
						cursor.getColumnIndex(RX_NUM_PILLS));
			    str += " " + cursor.getString(
			    		cursor.getColumnIndex(RX_MED));
			    str += " " + cursor.getString(
			    		cursor.getColumnIndex(RX_MG)) +
			    		getString(R.string.mg_suffix);
			    long rxNumber = cursor.getLong(
			    		cursor.getColumnIndex(RX_NUMBER));
			    if (rxNumber != 0) {
			    	str += "Rx# " + rxNumber;
			    }     
				out.println(str);
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private OnItemClickListener selectHist =
			new OnItemClickListener() {
		BitmapDrawable bitmapDrawable;
		SchData rx = new SchData();
		String dateTime;
		String compliance;
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int pos, long id) {

			Bitmap bitmap = null;
			Cursor selection = (Cursor) listView.getItemAtPosition(pos);
			selection = db.getHistoryById(selection.getString(0));
			selection.moveToFirst();

			dateTime = selection.getString(
					selection.getColumnIndex(HIST_DATE_TIME));
			
			if (selection.getInt(
					selection.getColumnIndex(HIST_COMP)) == 1) {
				compliance = getString(R.string.took);
			} else {
				compliance = getString(R.string.skipped);
			}
		    rx.medication = selection.getString(
		    		selection.getColumnIndex(RX_MED));
		    rx.mg = selection.getString(
		    		selection.getColumnIndex(RX_MG));
		    rx.numPills = selection.getString(
		    		selection.getColumnIndex(RX_NUM_PILLS));
		    rx.photoURI = selection.getString(
		    		selection.getColumnIndex(RX_PHOTO));
		    
		    /* Get drawable bitmap from URI */
			Uri photoUri = Uri.parse(rx.photoURI);
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
			bitmap = Bitmap.createScaledBitmap(bitmap, 400, 420, false);		
			bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
			selectionDialog();
		}
		
		private void selectionDialog() {
			AlertDialog.Builder adb = new AlertDialog.Builder(context);
			adb.setTitle(getText(R.string.hist_reminder));
			adb.setIcon(bitmapDrawable);
			adb.setMessage(getText(R.string.you) + compliance +
					rx.numPills + getText(R.string.of) + rx.medication +
					" " + rx.mg + getText(R.string.mg_on) + dateTime);
			adb.setPositiveButton(getText(R.string.okay_dismiss),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			adb.show();
		}
	};

	private OnItemLongClickListener deleteAllHist =
			new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent,
				View view, int pos, long id) {
			deleteDialog();			
			return true;
		}
		
		private void deleteDialog() {
			msg = new AlertDialog.Builder(context);
			msg.setTitle(getText(R.string.del_hist));
			msg.setMessage(getText(R.string.you_sure_hist));
			msg.setPositiveButton(getText(R.string.yes),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if (historyArchive()) {
						db.delHistory();
					} 
					
					Intent i = new Intent(context,
							HistoryRxActivity.class);
					startActivity(i);
					finish();
					dialog.dismiss();
				}
			});
			msg.setNegativeButton(getText(R.string.no),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			msg.show();
		}
	};
}
