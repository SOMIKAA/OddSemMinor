package com.suyash.dell.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class body extends AppCompatActivity {
    TextView tv1,tv2,tv3,tv4,tv5;

    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        tv1=(TextView)findViewById(R.id.textView11);
        tv2=(TextView)findViewById(R.id.textView12);
        tv3=(TextView)findViewById(R.id.textView13);
        tv4=(TextView)findViewById(R.id.textView14);
        tv5=(TextView)findViewById(R.id.textView15);
        b1=(Button)findViewById(R.id.button5);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(body.this,workingHours.class);
                startActivity(i);
            }
        });




    }
}
