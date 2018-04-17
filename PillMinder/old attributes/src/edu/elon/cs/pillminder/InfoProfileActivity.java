/**
 * PillMinder (c) 2013 by Clyde Thomas Zuber
 */
package edu.elon.cs.pillminder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * InfoProfileActivity
 * 
 * @author Clyde Zuber
 *
 */
public class InfoProfileActivity extends Activity
		implements OnItemSelectedListener, Schema  { 

	private Activity context;
	private Vibrator vibrator; 
	
	private EditText etPharmacy;
	private EditText etPharmNumber;
	private EditText etDoctor;
	private EditText etLastName;
	private EditText etFirstName;
	private EditText etDelay;

	private Spinner awakeSpinner;
	private ArrayAdapter<CharSequence> awakeSelect;
	private Spinner sleepSpinner;
	private ArrayAdapter<CharSequence> sleepSelect;
	
	private Button finishedButton;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	
	private String pharmacy;
	private String pharmPhone;
	private String doctor;
	private String lastName;
	private String firstName;
	private int delay;
	private int awakePos;
	private int sleepPos;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		if (!MinderActivity.serviceBound) {
			// update now done by renewSchedule() 
			Toast.makeText(context, R.string.disconnected_pill_service,
		        Toast.LENGTH_LONG).show();
			finish();
		}
		
		/* get defaults */
		sharedPref = context.getSharedPreferences(PREFS_DSN,
				Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		pharmacy = sharedPref.getString(RX_PHARM, "");
		pharmPhone = sharedPref.getString(RX_PHONE, "");
		doctor = sharedPref.getString(RX_DR, "");
		lastName = sharedPref.getString(INFO_LASTNAME, "");
		firstName = sharedPref.getString(INFO_FIRSTNAME, "");
		delay = sharedPref.getInt(INFO_DELAY, 0);
		awakePos = sharedPref.getInt(INFO_WAKE, 8);
		sleepPos = sharedPref.getInt(INFO_SLEEP, 23);
		
		if (MinderActivity.alarms) {
			setTheme(R.style.AppTheme);
		} else {
			setTheme(R.style.OffTheme);
		}
		setContentView(R.layout.activity_info_profile);

		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		
		/* show defaults */
		etPharmacy = (EditText) findViewById(R.id.etPharmacy);
		if (!pharmacy.equals("")) {
			etPharmacy.setText(pharmacy);
		}
		etPharmNumber = (EditText) findViewById(R.id.etPharmNumber);
		if (!pharmPhone.equals("")) {
			etPharmNumber.setText(pharmPhone);
		}
		etDoctor = (EditText) findViewById(R.id.etDoctor);
		if (!doctor.equals("")) {
			etDoctor.setText(doctor);
		}
		etLastName = (EditText) findViewById(R.id.etLastName);
		if (!lastName.equals("")) {
			etLastName.setText(lastName);
		}
		etFirstName = (EditText) findViewById(R.id.etFirstName);
		if (!firstName.equals("")) {
			etFirstName.setText(firstName);
		}
		etDelay = (EditText) findViewById(R.id.etDelay);
		if (delay != 0) {
			etDelay.setText(Integer.toString(delay));
		}
		
		finishedButton = (Button) findViewById(R.id.finishedButton);
		finishedButton.setOnClickListener(finishedClicked);
			
		awakeSpinner = (Spinner) findViewById(R.id.awakeSpinner);
		awakeSelect = ArrayAdapter.createFromResource(context,
		    R.array.time_select, android.R.layout.simple_spinner_item);
		awakeSelect.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// Tie drop down list to the spinner, set the call back methods
		awakeSpinner.setAdapter(awakeSelect);
		awakeSpinner.setOnItemSelectedListener(this);
		awakeSpinner.setSelection(awakePos);
		
		sleepSpinner = (Spinner) findViewById(R.id.sleepSpinner);
		sleepSelect = ArrayAdapter.createFromResource(context,
		    R.array.time_select, android.R.layout.simple_spinner_item);
		sleepSelect.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// Tie drop down list to the spinner, set the call back methods
		sleepSpinner.setAdapter(sleepSelect);
		sleepSpinner.setOnItemSelectedListener(this);
		sleepSpinner.setSelection(sleepPos);
			
		setNextFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; adds items to the action bar if present.
		if (MinderActivity.alarms) {
			getMenuInflater().inflate(R.menu.alarms_on, menu);
		} else {
			getMenuInflater().inflate(R.menu.alarms_off, menu);
		}
		return true;
	}
	
	/****************************************************************** 
	 * Settings/Option Menu Selections
	 */
	public boolean alarmSwitch(MenuItem menuItem) {
		
		MinderActivity.alarms = !MinderActivity.alarms;
		if (!MinderActivity.alarms) {
			vibrator.vibrate(1000);
		} 
		editor.putBoolean(INFO_ALARMS, MinderActivity.alarms);
		editor.commit();
		invalidateOptionsMenu();
		MinderActivity.minderService.renewSchedule();
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
		if (parent == awakeSpinner) {
			awakePos = pos;
		} if (parent == sleepSpinner) {
			sleepPos = pos;
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
		etPharmNumber.setNextFocusDownId(R.id.etDoctor);
		etDoctor.setNextFocusDownId(R.id.etLastName);
		etLastName.setNextFocusDownId(R.id.etFirstName);
		etFirstName.setImeOptions(EditorInfo.IME_ACTION_DONE);
	}
	
	/******************************************************************
	 * Button listener Action
	 */
	private OnClickListener finishedClicked = new OnClickListener() {

		@Override
		public void onClick(View view) {
			pharmacy = etPharmacy.getText().toString();
			pharmPhone = etPharmNumber.getText().toString();
			doctor = etDoctor.getText().toString();
			lastName = etLastName.getText().toString();
			firstName = etFirstName.getText().toString();
			String parseDelay = etDelay.getText().toString();
			if (parseDelay.equals("")) {
				delay = 0;
			} else {
				delay = Integer.parseInt(parseDelay);
				delay = delay % 60;
			}
			
			/**
			 * Save defaults.
			 */
			editor.putString(RX_PHARM, pharmacy);
			editor.putString(RX_PHONE, pharmPhone);
			editor.putString(RX_DR, doctor);
			editor.putString(INFO_LASTNAME, lastName);
			editor.putString(INFO_FIRSTNAME, firstName);
			editor.putInt(INFO_DELAY, delay);
			editor.putInt(INFO_WAKE, awakePos);
			editor.putInt(INFO_SLEEP, sleepPos);
			editor.commit();
			/**
			 * Update the Scheduler with the new times.
			 */
			MinderActivity.minderService.renewSchedule();
			// return to last screen
			finish();
		}
	};
}
