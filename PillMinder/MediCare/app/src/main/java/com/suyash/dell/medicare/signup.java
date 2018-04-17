package com.suyash.dell.medicare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity implements View.OnClickListener {
    EditText etemail,etpass,etcheckpass,etuser,etphone;
    Button submit,save;
    TextView text1;
    String email,password,phone,user,checkpass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    //defining a database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();

     //   firebaseAuth = FirebaseAuth.getInstance();
         etemail=(EditText) findViewById(R.id.editText7);
       etuser=(EditText) findViewById(R.id.editText8);
       etpass=(EditText) findViewById(R.id.editText4);
       etcheckpass=(EditText) findViewById(R.id.editText5);
         etphone=(EditText) findViewById(R.id.editText6);
        submit=(Button) findViewById(R.id.button2);
             save=(Button)findViewById(R.id.button7);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);

        submit.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
         email = etemail.getText().toString().trim();
         password  = etpass.getText().toString().trim();
         phone=etphone.getText().toString().trim();

        checkpass=etcheckpass.toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(signup.this,"Successfully registered",Toast.LENGTH_LONG).show();
                        }else{
                            //display some message here
                            Toast.makeText(signup.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
  /* private void saveUserInformation() {
        //Getting values from database
        user=etuser.getText().toString().trim();
        phone=etphone.getText().toString().trim();


        //creating a userinformation object
        UserInformation userInformation = new UserInformation(user,phone);

        //getting the current logged in user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //saving data to firebase database
        /*
        * first we are creating a new child in firebase with the
        * unique id of logged in user
        * and then for that user under the unique id we are saving data
        * for saving data we are using setvalue method this method takes a normal java object
        *
        databaseReference.child(user.getUid()).setValue(userInformation);

        //displaying a success toast
        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
    }*/

    @Override
    public void onClick(View view) {
        if(view == submit){
            registerUser();

        }
            if(view == text1){
                //open login activity when user taps on the already registered textview
                startActivity(new Intent(this, login.class));
            }
      /*  if(view==save)
        {
            saveUserInformation();
        }*/
    }
}
