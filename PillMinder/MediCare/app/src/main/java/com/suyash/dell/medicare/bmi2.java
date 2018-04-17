package com.suyash.dell.medicare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class bmi2 extends AppCompatActivity {

    TextView gender;
    TextView age;
    EditText ageentry;
    Button calc;
    RadioButton ma, fe ,kc,li;
    Boolean maButton = false;
    Boolean feButton = false;
    Boolean kgButton = false;
    Boolean lbButton = false;
    SharedPreferences sp;
    String height2,weight2,age1;
    Double resultNum2;
    String resultc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi2);

        ma = (RadioButton) findViewById(R.id.male);
        fe = (RadioButton) findViewById(R.id.female);
        gender = (TextView) findViewById(R.id.gender);
        kc = (RadioButton) findViewById(R.id.radioButton3);
        li = (RadioButton) findViewById(R.id.radioButton4);
        age = (TextView) findViewById(R.id.age);
        ageentry = (EditText) findViewById(R.id.editText);
        calc = (Button) findViewById(R.id.calculate);
        sp = getSharedPreferences("MYPREF", MODE_PRIVATE);

        height2 = sp.getString("height1", null);
        weight2 = sp.getString("weight1", null);
        age1 = ageentry.getText().toString();
         Toast.makeText(getApplicationContext(),weight2,Toast.LENGTH_SHORT).show();

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           /*  if (kgButton) {

                    if (maButton) {
                        resultNum2 = 66.4730 + (13.7516 * Double.parseDouble(weight2)) + (5.0033 * Double.parseDouble(height2)) - (6.7550 * Integer.parseInt(age1));
                    } else if (feButton) {
                        resultNum2 = 655.0955 + (9.5634 * Double.parseDouble(weight2) + (1.8496 * Double.parseDouble(height2)) - (4.6756 * Integer.parseInt(age1)));
                    }
                } else if (lbButton) {

                    if (maButton) {
                        resultNum2 = 66 + (6.23 * Double.parseDouble(weight2)) + (12.7 * Double.parseDouble(height2)) - (6.8 * Integer.parseInt(age1));
                    } else if (feButton) {
                        resultNum2 = 655 + (4.35 * Double.parseDouble(weight2)) + (4.7 * Double.parseDouble(height2)) - (4.7 * Integer.parseInt(age1));
                    }

                }
                resultc=Double.toString(resultNum2);
               SharedPreferences.Editor edit=sp.edit();
                edit.putString("result1",resultc);
                Toast.makeText(getApplicationContext(),resultc,Toast.LENGTH_LONG).show();
         //     editor.putBoolean("isTrue",true);
                edit.commit();
*/                Intent i=new Intent(bmi2.this,bmi3.class);
               // i.putExtra(resultc,false);
                startActivity(i);
            }
        });

        li.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                //handle the boolean flag here.
                if (arg1 == true) {
                }
                {         //Do something
                    kc.setChecked(false);
                }
                //do something else
                lbButton = true;

            }
        });

        kc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                //handle the boolean flag here.
                if (arg1 == true) {
                }
                {         //Do something
                    li.setChecked(false);
                }
                //do something else
                kgButton = true;
            }
        });


        ma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                }
                {         //Do something
                    fe.setChecked(false);
                }
                //do something else
                maButton = true;
            }
        });


        fe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                }
                {         //Do something
                    ma.setChecked(false);
                }
                //do something else
                feButton = true;
            }
        });
    }

    }

