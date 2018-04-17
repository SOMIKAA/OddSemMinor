/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.io.FileNotFoundException;
import java.io.IOException;

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
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * CurrentRxActivity
 * 
 * @author Clyde Zuber
 * 
 */
public class CurrentRxActivity extends Activity implements Schema {

	private Context context;
	private RxDatabaseHelper db;
	private Scheduler sched;

	private ListView listView;
	private SimpleCursorAdapter cursorAdapter;
	
	private Cursor cursor;
	private String[] fromColumns = {CURR_TABLE + "." + ID, RX_NUMBER, 
			RX_NUM_PILLS, RX_FREQUENCY, RX_MED, RX_MG};
	private int[] toViews = {R.id.idGone, R.id.rxShow, R.id.pillsShow, 
			R.id.freqShow, R.id.medShow, R.id.mgShow};
	
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
			sched = MinderActivity.minderService.getSched();
		} else {
			Toast.makeText(context, R.string.disconnected_pill_service,
		        Toast.LENGTH_LONG).show();
			finish();
		}
		
		listView = (ListView) findViewById(R.id.displayListView);  
		cursor = db.getCurrent();
		cursorAdapter = new SimpleCursorAdapter(context, 
				R.layout.listitem_currentrx, cursor, fromColumns,
				toViews, 0);
		listView.setAdapter(cursorAdapter);
		listView.setOnItemClickListener(selectRx);
		listView.setOnItemLongClickListener(deleteRx);
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
		// get rid of the old view
		finish();
		return true;
	}

	public boolean intentRxLabel(MenuItem menuItem) {
		Intent i;
		// need to refresh our view on return
		i = new Intent(context, CurrentRxActivity.class);
		startActivity(i);
		
		i = new Intent(context, RxLabelActivity.class);
		startActivity(i);
		// get rid of the old view
		finish();
		return true;
	}
	
	public boolean intentCurrentRx(MenuItem menuItem) {
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
		Intent i = new Intent(context, HistoryRxActivity.class);
		startActivity(i);
		finish();
		return true; 
	}
	/**
	 * END Settings/Option Menu Selections
	 *****************************************************************/
	
	/******************************************************************
	 * PRIVATE methods/routines
	 */
	private OnItemClickListener selectRx = new OnItemClickListener() {
		BitmapDrawable bitmapDrawable;
		SchData rx = new SchData();
		String frequency;
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int pos, long id) {
			
			Bitmap bitmap = null;
			Cursor selection = (Cursor)listView.getItemAtPosition(pos);
			String rowId = selection.getString(0);
			selection = db.getRxByCurrentId(rowId);
			selection.moveToFirst();
			
		    rx.medication = selection.getString(
		    		selection.getColumnIndex(RX_MED));
		    rx.mg = selection.getString(
		    		selection.getColumnIndex(RX_MG));
		    rx.numPills = selection.getString(
		    		selection.getColumnIndex(RX_NUM_PILLS));
		    frequency = selection.getString(
		    		selection.getColumnIndex(RX_FREQUENCY));
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
			adb.setTitle(getText(R.string.rx_reminder));
			adb.setIcon(bitmapDrawable);
			adb.setMessage(getText(R.string.you_take) + rx.numPills +
					getText(R.string.of) + rx.medication + " " + rx.mg +
					getText(R.string.mg_suffix) + frequency);
			adb.setPositiveButton(getText(R.string.okay_dismiss),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			adb.show();
		}
	};

	private OnItemLongClickListener deleteRx =
			new OnItemLongClickListener() {
		BitmapDrawable bitmapDrawable;
		SchData rx = new SchData();
		String frequency;

		@Override
		public boolean onItemLongClick(AdapterView<?> parent,
				View view, int pos, long id) {
			Bitmap bitmap = null;
			// keep cursor (to be deleted) separate from selection
			Cursor selection;
			
			cursor = (Cursor) listView.getItemAtPosition(pos);
			selection = (Cursor) listView.getItemAtPosition(pos);
			selection = db.getRxByCurrentId(selection.getString(0));
			selection.moveToFirst();
			
		    rx.medication = selection.getString(
		    		selection.getColumnIndex(RX_MED));
		    rx.mg = selection.getString(
		    		selection.getColumnIndex(RX_MG));
		    rx.numPills = selection.getString(
		    		selection.getColumnIndex(RX_NUM_PILLS));
		    frequency = selection.getString(
		    		selection.getColumnIndex(RX_FREQUENCY));
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
			deleteDialog();	
			return true;
		}
		
		private void deleteDialog() {
			msg = new AlertDialog.Builder(context);
			msg.setTitle(getText(R.string.delete_rx));
			msg.setIcon(bitmapDrawable);
			msg.setMessage(getText(R.string.you_sure) + rx.numPills +
					getText(R.string.of) + rx.medication + " " + 
					rx.mg +	getText(R.string.mg_suffix) + frequency);
			msg.setPositiveButton(getText(R.string.yes), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					db.delCurrentById(cursor.getString(0));
					sched.updateSchedule();
					Intent i = new Intent(context,
							CurrentRxActivity.class);
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
