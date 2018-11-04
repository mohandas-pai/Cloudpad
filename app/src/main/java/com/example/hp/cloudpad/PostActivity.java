package com.example.hp.cloudpad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mPostBtn;

    String mUserId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPostTitle = (EditText)findViewById(R.id.titletext);
        mPostDesc = (EditText)findViewById(R.id.desctext);
        mPostBtn = (Button)findViewById(R.id.postbtn);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUserId = mFirebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Notes");

        mPostBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                startPosting();

            }
        });

    }

    private void startPosting() {
        String title_val = mPostTitle.getText().toString().trim();
        String desc_val = mPostDesc.getText().toString().trim();
        String date_val = new SimpleDateFormat("dd-MM-yyy HH:mm").format(Calendar.getInstance().getTime());

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val)) {
            DatabaseReference newPost = mDatabase.push();
            newPost.child("title").setValue(title_val);
            newPost.child("desc").setValue(desc_val);
            newPost.child("datetime").setValue(date_val);


            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
        else
            Toast.makeText(PostActivity.this,"Title,Description cant be empty",Toast.LENGTH_LONG).show();
    }
}
