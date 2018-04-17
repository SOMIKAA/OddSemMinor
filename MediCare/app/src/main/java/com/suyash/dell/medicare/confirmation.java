package com.suyash.dell.medicare;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class confirmation extends AppCompatActivity {

    EditText num,sub;
    Button msgbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        num=(EditText)findViewById(R.id.emailText);
        msgbutton=(Button)findViewById(R.id.confirmButton);
        reqAskPermission(Manifest.permission.SEND_SMS,1001);
        reqAskPermission(Manifest.permission.RECEIVE_SMS,1002);
        msgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobilenumber=num.getText().toString();
                // String smscontent=sub.getText().toString();
                if(mobilenumber.length() > 0)
                {
                    sentSMS(mobilenumber,"appointment confirmed!");
                }else {
                    Toast.makeText(confirmation.this,"Enter mobile number & subject",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sentSMS(String mob,String msg)
    {
        String sentSMS="SENT_SMS";
        String delSMS="SMS_DEL";
        PendingIntent smsSEND=PendingIntent.getBroadcast(getBaseContext(),0,new Intent(sentSMS),0);
        PendingIntent smsDEL=PendingIntent.getBroadcast(getBaseContext(),0,new Intent(delSMS),0);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"MSG SEND",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(),"Generic Failure",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(),"NULL PDU",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(),"RADIO OFF",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(),"NO NW SERVICE",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        },new IntentFilter(sentSMS));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"SMS DEL",Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(),"SMS NOT DEL",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        },new IntentFilter(delSMS));
        SmsManager manager=SmsManager.getDefault();
        manager.sendTextMessage(mob,null,msg,smsSEND,smsDEL);
    }
    private void reqAskPermission(String permission,int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
        }else
        {
            Toast.makeText(this,"PERMISSION_GRANTED",Toast.LENGTH_LONG).show();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1001:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                }else
                {

                }
                break;
            case 1002:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                }else
                {

                }
                break;
            default:
        }
        return;
    }

}
