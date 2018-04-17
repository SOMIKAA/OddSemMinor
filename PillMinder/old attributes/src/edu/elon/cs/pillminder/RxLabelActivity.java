/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * RxLabelActivity
 * 
 * 
 *  @author Clyde Zuber
 *  
 */
public class RxLabelActivity extends Activity 
		implements OnItemSelectedListener, Schema {

	private Activity context;
	private RxDatabaseHelper db;
	private Scheduler sched;
	
	private EditText etPharmacy;
	private EditText etPharmNumber;
	private EditText etRxNumber;
	private EditText etDoctor;
	private EditText etDispensed;
	
	private TextView patient;
	private Spinner pillsSpinner;
	private ArrayAdapter<CharSequence> pillsSelect;
	private int pills = 1;
	private Spinner freqSpinner;
	private ArrayAdapter<CharSequence> freqSelect;
	private int freq = 0;
	private Button finishedButton;
	
	private EditText etMedication;
	private EditText etMG;
	private EditText etQuantity;
	private EditText etBrandName;
	private EditText etRefills;
	private EditText etCutoff;
	
	private SharedPreferences sharedPref;
	private String lastName;
	private String firstName;
	
	private String pharmacy;
	private String pharmPhone;
	private String doctor;
	private String rxNumber;
	private String dispensed;
	private String numPills;
	private String frequency;
	private String medication;
	private String mg;
	private String quantity;
	private String brandName;
	private String numRefills;
	private String cutoffDate;
	private Uri photoURI;
	
	private File file;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (MinderActivity.alarms) {
			setTheme(R.style.AppTheme);
		} else {
			setTheme(R.style.OffTheme);
		}
		setContentView(R.layout.activity_rxlabel);
		context = this;
		if (MinderActivity.serviceBound) {
			db = MinderActivity.minderService.getDB();
			sched = MinderActivity.minderService.getSched();
		} else {
			Toast.makeText(context, R.string.disconnected_pill_service,
		        Toast.LENGTH_LONG).show();
			finish();
		}

		sharedPref = context.getSharedPreferences(PREFS_DSN,
				Context.MODE_PRIVATE);
		pharmacy = sharedPref.getString(RX_PHARM, "");
		pharmPhone = sharedPref.getString(RX_PHONE, "");
		doctor = sharedPref.getString(RX_DR, "");
		lastName = sharedPref.getString(INFO_LASTNAME, "");
		firstName = sharedPref.getString(INFO_FIRSTNAME, "");
		etPharmacy = (EditText) findViewById(R.id.etPharmacy);
		if (!pharmacy.equals("")) {
			etPharmacy.setText(pharmacy);
		}
		etPharmNumber = (EditText) findViewById(R.id.etPharmNumber);
		if (!pharmPhone.equals("")) {
			etPharmNumber.setText(pharmPhone);
		}
		
		/* 
		 *  Check for existence of Rx and allow editing.
		 */
		etRxNumber = (EditText) findViewById(R.id.etRxNumber);
		etRxNumber.addTextChangedListener(rxEdit);
		
		etDoctor = (EditText) findViewById(R.id.etDoctor);
		if (!doctor.equals("")) {
			etDoctor.setText(doctor);
		}
		etDispensed = (EditText) findViewById(R.id.etDispensed);
		
		patient = (TextView) findViewById(R.id.patientText);
		if (!lastName.equals("")) {
			patient.setText(lastName + ", " + firstName);
		} else {
			patient.setText(R.string.temp_name);
		}
		
		pillsSpinner = (Spinner) findViewById(R.id.pillsSpinner);
		// Create ArrayAdapter using #pills to take with each dose
		pillsSelect = ArrayAdapter.createFromResource(context,
		        R.array.pills_select,
		        android.R.layout.simple_spinner_item);
		pillsSelect.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// Tie drop down list to the spinner, set the call back methods
		pillsSpinner.setAdapter(pillsSelect);
		pillsSpinner.setOnItemSelectedListener(this);
		// One pill per period is the default
		pillsSpinner.setSelection(pills);
		
		freqSpinner = (Spinner) findViewById(R.id.freqSpinner);
		// Create ArrayAdapter using doctor prescribed frequencies
		freqSelect = ArrayAdapter.createFromResource(context,
		        R.array.freq_select,
		        android.R.layout.simple_spinner_item);
		freqSelect.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// Tie drop down list to the spinner, set the call back methods
		freqSpinner.setAdapter(freqSelect);
		freqSpinner.setOnItemSelectedListener(this);
		freqSpinner.setSelection(freq);
		
		finishedButton = (Button) findViewById(R.id.finishedButton);
		finishedButton.setOnClickListener(finishedClicked);
		
		etMedication = (EditText) findViewById(R.id.etMedication);
		etMG = (EditText) findViewById(R.id.etMG);
		etQuantity = (EditText) findViewById(R.id.etQuantity);
		etBrandName = (EditText) findViewById(R.id.etBrandName);
		etRefills = (EditText) findViewById(R.id.etRefills);
		etCutoff = (EditText) findViewById(R.id.etCutoff);
		
		setNextFocus();
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
	 * OnItemSelectedListener elements
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int pos, long id) {
		if (parent == pillsSpinner) {
			pills = pos;
			numPills = parent.getItemAtPosition(pills).toString();
		} if (parent == freqSpinner) {
			freq = pos;
			frequency = parent.getItemAtPosition(freq).toString();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	
	/******************************************************************
	 * PRIVATE methods/routines
	 */
	private void setNextFocus() { 
		etPharmacy.setNextFocusDownId(R.id.etPharmNumber);
		etPharmNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etRxNumber.setNextFocusDownId(R.id.etDispensed);
		etDoctor.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etDispensed.setNextFocusDownId(R.id.etMedication);
		etMedication.setNextFocusDownId(R.id.etMG);
		etMG.setNextFocusDownId(R.id.etQuantity);
		etQuantity.setNextFocusDownId(R.id.etRefills);
		etBrandName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etRefills.setNextFocusDownId(R.id.etCutoff);
		etCutoff.setImeOptions(EditorInfo.IME_ACTION_DONE);
	}
	
	
	private void photoFileStorage() {
		String state = Environment.getExternalStorageState();
		// creates the correct filename and path for saving
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			File path = getExternalFilesDir("RxPhotos");

			// Create filename from date and time
			SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy_MM_dd-HH_mm_ss", Locale.US);
			Date now = new Date();
			String saveName = formatter.format(now) + ".jpg";

			file = new File(path, saveName);
		}
	}
	
	/******************************************************************
	 * Watch the Rx number field to allow for editing an existing
	 * prescription.
	 */	
	private TextWatcher rxEdit = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			String str = s.toString();
			if (str.compareTo(" ") < 1) {
				return;
			}
			Cursor cursor = db.getRxByNumber(str);
			if (cursor.moveToFirst()) {
				etPharmacy.setText(cursor.getString(
						cursor.getColumnIndex(RX_PHARM)));
				etPharmNumber.setText(cursor.getString(
						cursor.getColumnIndex(RX_PHONE)));
				etDoctor.setText(cursor.getString(
						cursor.getColumnIndex(RX_DR)));
				etDispensed.setText(cursor.getString(
						cursor.getColumnIndex(RX_DISP)));
				
				pills = cursor.getInt(cursor.getColumnIndex(RX_PILLS));
				pillsSpinner.setSelection(pills);
				freq = cursor.getInt(cursor.getColumnIndex(RX_FREQ));
				freqSpinner.setSelection(freq);
				
				etMedication.setText(cursor.getString(
						cursor.getColumnIndex(RX_MED)));
				etMG.setText(cursor.getString(
						cursor.getColumnIndex(RX_MG)));
				etQuantity.setText(cursor.getString(
						cursor.getColumnIndex(RX_QTY)));
				etBrandName.setText(cursor.getString(
						cursor.getColumnIndex(RX_BRAND)));
				etRefills.setText(cursor.getString(
						cursor.getColumnIndex(RX_REFILLS)));
				etCutoff.setText(cursor.getString(
						cursor.getColumnIndex(RX_CUTOFF)));
			}	
		}

		@Override
		public void beforeTextChanged(CharSequence str, int start,
				int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence str, int start,
				int before,	int count) {
		}
	};
	
	/******************************************************************
	 * Button listener Action
	 */
	private OnClickListener finishedClicked = new OnClickListener() {

		@Override
		public void onClick(View view) {
			pharmacy = etPharmacy.getText().toString();
			pharmPhone = etPharmNumber.getText().toString();
			doctor = etDoctor.getText().toString();
			rxNumber = etRxNumber.getText().toString();
			dispensed = etDispensed.getText().toString();
			medication = etMedication.getText().toString();
			mg = etMG.getText().toString();
			quantity = etQuantity.getText().toString();
			brandName = etBrandName.getText().toString();
			numRefills = etRefills.getText().toString();
			cutoffDate = etCutoff.getText().toString();
			photoFileStorage();
			photoURI = Uri.fromFile(file);
			
			// Intent to camera for picture
			Intent cameraIntent = 
					new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
		    startActivityForResult(cameraIntent, NOTIFICATION); 
			
			db.insertRx(rxNumber, pharmacy, pharmPhone, doctor,
					dispensed, numPills, pills, frequency, freq,
					medication, mg, quantity, brandName, numRefills,
					cutoffDate, photoURI);
			sched.updateSchedule();
			finish();
		}
	};
}
