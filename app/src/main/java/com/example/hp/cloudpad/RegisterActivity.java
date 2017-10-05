package com.example.hp.cloudpad;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField,mEmailField,mPasswordField;
    private Button mSign;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("MyUsers");

        mNameField = (EditText) findViewById(R.id.nameField);
        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passField);

        mSign = (Button) findViewById(R.id.reg);

        mSign.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startRegister();
            }
        });

    }

    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String passw = mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(passw)){
            mProgress.setMessage("Signing Up....");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        /*String user_id = mAuth.getCurrentUser().getUid();
                        /*DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);*/


                        mProgress.dismiss();

                        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });
        }
    }
}
