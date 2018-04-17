package com.suyash.dell.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class InitialPage extends AppCompatActivity {
    Button blogin,bsignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

          blogin=(Button) findViewById(R.id.button3);
         bsignup=(Button) findViewById(R.id.button4);

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent=new Intent(InitialPage.this,login.class);
                startActivity(LoginIntent);
            }
        });
        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignupIntent=new Intent(InitialPage.this,signup.class);
                startActivity(SignupIntent);
            }
        });

    }
}
