package com.suyash.dell.medicare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DoctorSelection extends Activity implements View.OnClickListener {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    TextView textView;
    Button button;
    String day;
    Date dateObj = new Date();
    SimpleDateFormat sfDate = new SimpleDateFormat("E");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_selection);

        Intent intent = getIntent();
        dateObj.setTime(intent.getLongExtra("date", -1));
        day = sfDate.format(dateObj);

        spinnerMethod();
        button.setOnClickListener(this);
    }

    public void spinnerMethod() {
        spinner = (Spinner) findViewById(R.id.doctorSpinner);
        if (day.matches("Tue|Thu"))
            adapter = ArrayAdapter.createFromResource(this, R.array.doctors_list_TuThu, android.R.layout.simple_spinner_item);
        else
            adapter = ArrayAdapter.createFromResource(this, R.array.doctors_list_MWF,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        textView = (TextView) findViewById(R.id.descriptionText);
        button = (Button) findViewById(R.id.nextButton2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(19);
                displayDescription(spinner.getSelectedItem().toString());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void displayDescription(String text){
        switch (text) {
            case "Dr. Benjamin Spock":
                textView.setText(R.string.description_Benjamin);
                break;
            case "Dr. William Sears":
                textView.setText(R.string.description_Sears);
                break;
            case "Dr. Robert Mendelsohn":
                textView.setText(R.string.description_Mendelsohn);
                break;
            case "Dr. V.N. Korovin":
                textView.setText(R.string.description_Korovin);
                break;
            case "Dr. Florence Blake":
                textView.setText(R.string.description_Florence);
                break;

            default:
                textView.setText(R.string.description_Benjamin);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent2 = new Intent(DoctorSelection.this,confirmation.class);
        intent2.putExtra("date",dateObj.getTime());
        intent2.putExtra("doctor",spinner.getSelectedItem().toString());
        startActivity(intent2);
//        Toast.makeText(this,spinner.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

    }
}
