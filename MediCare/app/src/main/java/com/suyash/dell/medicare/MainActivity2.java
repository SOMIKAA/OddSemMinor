package com.suyash.dell.medicare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity2 extends Activity implements View.OnClickListener {

    private int year, month, day;
    private EditText dateText;
    private EditText timeText;
    private Intent currentIntent;
    private final Calendar chosenDateTime = Calendar.getInstance();
    private Calendar calToday = Calendar.getInstance();
    private Calendar calFuture;
    private boolean dateBool, timeBool;
    private Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        initializeView();

        button = (Button) findViewById(R.id.nextButton);
        button.setOnClickListener(this);
    }



    private void initializeView() {
        dateText = (EditText)findViewById( R.id.dateText );
        timeText = (EditText)findViewById( R.id.timeTextConfirm);
        currentIntent = getIntent();
        Bundle extras = currentIntent.getExtras();
        setCurrentDateOnView();

    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            chosenDateTime.set(Calendar.YEAR, year);
            chosenDateTime.set(Calendar.MONTH, monthOfYear);
            chosenDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calFuture = Calendar.getInstance();
            calFuture.add(Calendar.DAY_OF_MONTH,30);
            dateBool = false;
            if (calToday.after(chosenDateTime))
                Toast.makeText(MainActivity2.this, "Please pick a date in the future!", Toast.LENGTH_SHORT).show();
            else if (chosenDateTime.after(calFuture))
                Toast.makeText(MainActivity2.this, "Appointments cannot be set for more than 30 days in the future!", Toast.LENGTH_SHORT).show();
            else if (chosenDateTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || chosenDateTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                Toast.makeText(MainActivity2.this, "We are closed on weekends!", Toast.LENGTH_SHORT).show();
            else
                dateBool = true;

            setCurrentDateOnView();
        }
    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute ) {
            chosenDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            chosenDateTime.set(Calendar.MINUTE, minute);
            int hour = chosenDateTime.get(Calendar.HOUR);
            if ((hour < 9 && (chosenDateTime.get(Calendar.AM_PM) == Calendar.SUNDAY))||(hour > 4 && (chosenDateTime.get(Calendar.AM_PM) == Calendar.SATURDAY))){
                String timeMsg = "Please pick a time between the hours of 9 am and 5 pm!";
                Toast.makeText(MainActivity2.this, timeMsg, Toast.LENGTH_SHORT).show();
                timeBool = false;
            }
            else{
                timeBool = true;
            }

            setCurrentDateOnView();
        }
    };

    public void dateOnClick(View view) {
        new DatePickerDialog(MainActivity2.this, date,
                chosenDateTime.get(Calendar.YEAR), chosenDateTime.get(Calendar.MONTH), chosenDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void timeOnClick(View view) {
        new TimePickerDialog( MainActivity2.this, time,
                chosenDateTime.get(Calendar.HOUR), chosenDateTime.get(Calendar.MINUTE), false).show();
    }

    public void setCurrentDateOnView() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat( dateFormat, Locale.US );
        dateText.setText( sdf.format(chosenDateTime.getTime()) );

        String timeFormat = "hh:mm a";
        SimpleDateFormat stf = new SimpleDateFormat( timeFormat, Locale.US );
        timeText.setText( stf.format( chosenDateTime.getTime() ) );
    }

    @Override
    public void onClick(View v) {



        if (dateBool && timeBool) {
            Intent intent = new Intent("com.example.adityas.DoctorsAppointment.DoctorSelection");
            SimpleDateFormat sfDate = new SimpleDateFormat("E MMM dd yyyy");
            SimpleDateFormat sfTime = new SimpleDateFormat("h:MM a zzz");
//            intent.putExtra("time", sfTime.format(chosenDateTime.getTime()));
            intent.putExtra("date",chosenDateTime.getTimeInMillis());
            startActivity(intent);
        }
        else {
            String timeMsg = "Please fix time and date errors!";
            Toast.makeText(MainActivity2.this, timeMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
