package com.example.hp.cloudpad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mlog,mpass;
    private Button mlogbt,mregisterbt;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsr;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mlog =(EditText)findViewById(R.id.logInEmail);
        mpass = (EditText)findViewById(R.id.logInPass);
        mlogbt = (Button)findViewById(R.id.logbtn);
        mregisterbt = (Button)findViewById(R.id.newacc);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsr = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsr.keepSynced(true);

        mlogbt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                checkLogin();
            }
        });

        mregisterbt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mpass.setText("");
                mlog.setText("");
                Intent mainIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });
    }

    private void checkLogin() {
        String email = mlog.getText().toString().trim();
        String pass = mpass.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            mProgress.setMessage("Checking Login");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        mProgress.dismiss();
                        checkUserExist();

                        mpass.setText("");
                        mlog.setText("");

                    }else{
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this,"Error Logging in",Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }

    private void checkUserExist() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }


}
