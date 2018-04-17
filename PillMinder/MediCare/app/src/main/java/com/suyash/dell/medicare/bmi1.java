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

public class bmi1 extends AppCompatActivity {

    EditText height;
    EditText weight;
    RadioButton kc;
    RadioButton li;
    Button back;
    Button next;
    String height1,result,category;
    String weight1;
    Boolean kgButton = false;
    Boolean lbButton = false;
    SharedPreferences sp;
    double resultNum1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi1);

        height = (EditText) findViewById(R.id.editText3);
        weight = (EditText) findViewById(R.id.editText4);
        kc = (RadioButton) findViewById(R.id.radioButton);
        li = (RadioButton) findViewById(R.id.radioButton2);
        back = (Button)findViewById(R.id.button);
        next = (Button)findViewById(R.id.button2);
        sp=getSharedPreferences("MYPREF",MODE_PRIVATE);
        final SharedPreferences.Editor editor=sp.edit();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                height1 = height.getText().toString();
                weight1 = weight.getText().toString();
                if (kgButton) {
                    editor.putBoolean("choice",true);
                    resultNum1 = Double.parseDouble(weight1) / ((Double.parseDouble(height1) * 0.01) * (Double.parseDouble(height1) * 0.01));
                } else if (lbButton) {
                    editor.putBoolean("choice",false);
                    Double up = Double.parseDouble(weight1);
                    Double down = Double.parseDouble(height1) * Double.parseDouble(height1);
                    resultNum1 = up / down * 703;
                }
                result=Double.toString(resultNum1);
                if(resultNum1<18.5)
                {
                    category="Underweight";
                }
                else if(resultNum1>=18.5 && resultNum1<=25)
                {
                    category="Normal Weight";
                }
                else if(resultNum1>25)
                {
                    category="OverWeight";
                }
                editor.putString("Category",category);
                editor.putString("height1",height.getText().toString());
                editor.putString("weight1",weight.getText().toString());
                editor.putString("result",result);
               /* editor.putBoolean("choice",)*/
                editor.putBoolean("isTrue",true);
                editor.commit();

                Intent intent=new Intent(bmi1.this,bmi2.class);
                intent.putExtra(result,false);
                startActivity(intent);
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

    }
}
