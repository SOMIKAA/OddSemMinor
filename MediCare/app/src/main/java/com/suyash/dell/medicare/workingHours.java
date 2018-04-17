package com.suyash.dell.medicare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class workingHours extends AppCompatActivity {

    RadioButton rb1,rb2,rb3,rb4;
    Button b1;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);
        rb1=(RadioButton)findViewById(R.id.radioButton5);
        rb2=(RadioButton)findViewById(R.id.radioButton6);
        rb3=(RadioButton)findViewById(R.id.radioButton7);
        rb4=(RadioButton)findViewById(R.id.radioButton8);
        tv1=(TextView)findViewById(R.id.textView16);
        b1=(Button)findViewById(R.id.button6);

    }
}
