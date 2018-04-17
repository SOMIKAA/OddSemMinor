package com.suyash.dell.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tv11, tv21, tv31, tv41, tv51, tv61, tv71;
    ImageView iv1, iv2;
    ImageButton ib1, ib2, ib3, ib4, ib5, ib6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv11 = (TextView) findViewById(R.id.tv1);
        tv21 = (TextView) findViewById(R.id.tv2);
        tv31 = (TextView) findViewById(R.id.tv3);
        tv41 = (TextView) findViewById(R.id.tv4);
        tv51 = (TextView) findViewById(R.id.tv5);
        tv61 = (TextView) findViewById(R.id.tv6);
        tv71=(TextView) findViewById(R.id.text8);
        iv1 = (ImageView) findViewById(R.id.image);
        iv2 = (ImageView) findViewById(R.id.image1);
        ib1 = (ImageButton) findViewById(R.id.img1);
        ib2 = (ImageButton) findViewById(R.id.img2);
        ib3 = (ImageButton) findViewById(R.id.img3);
        ib4 = (ImageButton) findViewById(R.id.img4);
        ib5 = (ImageButton) findViewById(R.id.img5);
        ib6 = (ImageButton) findViewById(R.id.img6);

    /*    ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,pillminder.class);
                startActivity(i);
            }
        });*/

        ib4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, bmi1.class);
                startActivity(i);
            }
        });

      /*  ib5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,bmi1.class);
                startActivity(i);
            }
        });*/

      /*  ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,bmi1.class);
                startActivity(i);

            }
        });*/
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, InitialPage.class);
                startActivity(i);
            }

        });

        ib6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ip = new Intent(MainActivity.this, help.class);
                startActivity(ip);
            }

        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about) {
             Intent aboutc=new Intent(MainActivity.this,ScrollingActivity.class);
            startActivity(aboutc);

        } else if (id == R.id.bmi) {
            Intent bmic=new Intent(MainActivity.this,bmi1.class);
            startActivity(bmic);


        } else if (id == R.id.contact) {
            Intent cic=new Intent(MainActivity.this,contactp.class);
            startActivity(cic);

        } else if (id == R.id.pres) {
            //Intent presi=new Intent(MainActivity.this,)

        } else if (id == R.id.prof) {
            Intent profi=new Intent(MainActivity.this,login.class);
            startActivity(profi);

        } else if (id == R.id.chat) {

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
