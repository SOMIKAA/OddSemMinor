package com.suyash.dell.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class help extends AppCompatActivity {
    ImageButton info,contact,check,chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        info=(ImageButton) findViewById(R.id.imageButton13);
        contact=(ImageButton) findViewById(R.id.imageButton15);
        check=(ImageButton) findViewById(R.id.imageButton16);
        chat=(ImageButton) findViewById(R.id.imageButton17);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infopage=new Intent(help.this,ScrollingActivity.class);
                startActivity(infopage);
            }
        });
        contact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent contactpage=new Intent(help.this,contactp.class);
                startActivity(contactpage);
            }
        });
    }
}
