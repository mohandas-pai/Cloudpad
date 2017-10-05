package com.example.hp.cloudpad;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleNoteActivity extends AppCompatActivity {

    String mNoteKey = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;

    private TextView tittext,desctext;
    private Button del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_note);

        tittext = (TextView) findViewById(R.id.singleTitle);
        desctext = (TextView) findViewById(R.id.singleDesc);
        del = (Button) findViewById(R.id.singleDelete);

        mNoteKey = getIntent().getExtras().getString("Note_id");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Notes");

        mDatabase.child(mNoteKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nTitle = (String) dataSnapshot.child("title").getValue();
                String nDesc = (String) dataSnapshot.child("desc").getValue();

                tittext.setText(nTitle);
                desctext.setText(nDesc);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mNoteKey).removeValue();

                Intent mainIntent = new Intent(SingleNoteActivity.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });

    }
}
