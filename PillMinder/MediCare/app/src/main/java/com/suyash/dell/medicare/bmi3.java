package com.suyash.dell.medicare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class bmi3 extends AppCompatActivity {

    TextView bmivalue;
    TextView bmicategory;
    TextView  bmrvalue;
    TextView calorie;
    TextView  bmiva;
    TextView  bmicat;
    TextView   bmrval;
    TextView   bmrcal;
    Button calculate;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi3);
        bmivalue=(TextView)findViewById(R.id.textView2);
        bmicategory=(TextView)findViewById(R.id.textView4);
        bmrvalue=(TextView)findViewById(R.id.textView5);
        calorie=(TextView)findViewById(R.id.textView6);
        bmiva=(TextView)findViewById(R.id.textView7);
        bmicat=(TextView)findViewById(R.id.textView8);
        bmrval=(TextView)findViewById(R.id.textView9);
        bmrcal=(TextView)findViewById(R.id.textView10);
        calculate=(Button)findViewById(R.id.button4);
        sp = getSharedPreferences("MYPREF", MODE_PRIVATE);
        Toast.makeText(getApplicationContext(),sp.getString("result",null),Toast.LENGTH_SHORT).show();
        bmiva.setText(sp.getString("result",null));

        bmrval.setText(sp.getString("result1",null));
       // ed.putBoolean("isTrue",false);
       bmicat.setText(sp.getString("Category",null));

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(bmi3.this,body.class);
                startActivity(intent);
            }
        });

    }
}
