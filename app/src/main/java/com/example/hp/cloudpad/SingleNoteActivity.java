package com.example.hp.cloudpad;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    String nTitle,nDesc;
    MenuItem action_cal;

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

                nTitle = (String) dataSnapshot.child("title").getValue();
                nDesc = (String) dataSnapshot.child("desc").getValue();

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
                AlertDialog.Builder adb = new AlertDialog.Builder(SingleNoteActivity.this);
                adb.setIcon(R.drawable.warning);
                adb.setTitle("Delete Note");
                adb.setMessage("Are you sure?");
                adb.setNegativeButton("No",null);
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child(mNoteKey).removeValue();

                        Intent mainIntent = new Intent(SingleNoteActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                }).create().show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cal, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.call_cal){
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, ""+nTitle)
                    .putExtra(CalendarContract.Events.DESCRIPTION, ""+nDesc)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            SingleNoteActivity.this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
