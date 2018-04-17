/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * DisplayDailyActivity
 * 
 * @author Clyde Zuber
 * 
 */
public class DisplayDailyActivity extends Activity implements Schema {
	private Context context;
	private RxDatabaseHelper db;
	private Scheduler sched;
	private Boolean dailyMeds;
	private ArrayList<SchData> schHour;
	private ArrayList<MedData> medHour;
	private ListView listView;
	private ArrayAdapter<SchData> schAdapter;
	private ArrayAdapter<MedData> medAdapter;
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
		
		if (getIntent().getAction().equals(PACKAGE + INTENT_MED)) {
			dailyMeds = true;
		} else {
			dailyMeds = false;
		}
		
		if (dailyMeds) {
			medHour = sched.getMedHourList();
			medAdapter = new ArrayAdapter<MedData>(context,
					R.layout.listitem_adapter, medHour);
			medAdapter.sort(MedData.MedDataComparator);
			listView.setAdapter(medAdapter);
		} else {
			schHour = sched.getSchHourList();
			schAdapter = new ArrayAdapter<SchData>(context,
					R.layout.listitem_adapter, schHour);
			schAdapter.sort(SchData.SchDataComparator);
			listView.setAdapter(schAdapter);
		}
		
		listView.setOnItemClickListener(selectDetail);
		listView.setOnItemLongClickListener(adjustSched);
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
		i = new Intent(context, DisplayDailyActivity.class);
		if (dailyMeds) {
			i.setAction(PACKAGE + INTENT_MED);
		} else {
			i.setAction(PACKAGE + INTENT_SCH);
		}
		startActivity(i);
		
		i = new Intent(context, RxLabelActivity.class);
		startActivity(i);
		// get rid of the old view
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
	private OnItemClickListener selectDetail = 
			new OnItemClickListener() {
		BitmapDrawable bitmapDrawable;
		SchData rx;
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int pos, long id) {
			Bitmap bitmap = null;
			
			if (dailyMeds) {
				rx = medAdapter.getItem(pos);
			} else {
				rx = schAdapter.getItem(pos);
			}
				
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
			adb.setTitle(getText(R.string.detail_reminder));
			adb.setIcon(bitmapDrawable);
			adb.setMessage(getText(R.string.at) + rx.hourScheduled +
					getText(R.string.taking) + rx.numPills +
					getText(R.string.of) + rx.medication + " " +
					rx.mg + getText(R.string.mg_period));
			adb.setPositiveButton(getText(R.string.okay_dismiss),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			adb.show();
		}
	};
	

	private OnItemLongClickListener adjustSched =
			new OnItemLongClickListener() {
		BitmapDrawable bitmapDrawable;
		SchData rx;

		@Override
		public boolean onItemLongClick(AdapterView<?> parent,
				View view, int pos, long id) {
			Bitmap bitmap = null;
			
			if (dailyMeds) {
				rx = medAdapter.getItem(pos);
			} else {
				rx = schAdapter.getItem(pos);
			}
				
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
			adjustDialog();			
			return true;
		}
		
		private void adjustDialog() {
			msg = new AlertDialog.Builder(context);
			msg.setTitle(getText(R.string.adj_sched));
			msg.setIcon(bitmapDrawable);
			msg.setMessage(getText(R.string.at) + rx.hourScheduled +
					getText(R.string.taking) + rx.numPills +
					getText(R.string.of) + rx.medication + " " +
					rx.mg + getText(R.string.adj_question));
			msg.setPositiveButton(getText(R.string.adj_later),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					db.adjustCurrent(rx.rxId, rx.freq, rx.offset, 1);
					sched.updateSchedule();
					dialog.dismiss();
					Intent i = new Intent(context,
							DisplayDailyActivity.class);
					if (dailyMeds) {
						i.setAction(PACKAGE + INTENT_MED);
					} else {
						i.setAction(PACKAGE + INTENT_SCH);
					}
					startActivity(i);
					finish();
				}
			});	
			msg.setNeutralButton(getText(R.string.adj_nevermind),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			msg.setNegativeButton(getText(R.string.adj_earlier),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					db.adjustCurrent(rx.rxId, rx.freq, rx.offset, -1);
					sched.updateSchedule();
					dialog.dismiss();
					Intent i = new Intent(context,
							DisplayDailyActivity.class);
					if (dailyMeds) {
						i.setAction(PACKAGE + INTENT_MED);
					} else {
						i.setAction(PACKAGE + INTENT_SCH);
					}
					startActivity(i);
					finish();
				}
			});
			msg.show();
		}
	};
}
