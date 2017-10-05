package com.example.hp.cloudpad;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mNotesList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseStar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    String mUserId;

    private boolean processstar = false;

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            //Go to login
            Intent setupIntent = new Intent(MainActivity.this,LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
        }
            mUserId = mFirebaseUser.getUid();
        if(mUserId == null){
            //Go to login
            Intent setupIntent = new Intent(MainActivity.this,LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
        }


        //mDatabase = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Notes");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Notes");
        mDatabaseStar = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Star");

        mDatabase.keepSynced(true);
        mDatabaseStar.keepSynced(true);






        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mNotesList = (RecyclerView) findViewById(R.id.NotesList);
        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(new LinearLayoutManager(this));



        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                startActivity(new Intent(MainActivity.this,PostActivity.class));

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Vibrate for 400 milliseconds
                v.vibrate(400);

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Note,NotesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Note, NotesViewHolder>(
                Note.class,
                R.layout.notes_row,
                NotesViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(NotesViewHolder viewHolder, Note model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setStarbutton(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleIntent = new Intent(MainActivity.this,SingleNoteActivity.class);
                        singleIntent.putExtra("Note_id",post_key);
                        startActivity(singleIntent);
                    }
                });

                viewHolder.mStarbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processstar = true;
                        mDatabaseStar.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(processstar){
                                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                                        mDatabaseStar.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        processstar = false;
                                    }else{
                                        mDatabaseStar.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                        processstar = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                }

                });


            }
        };

        mNotesList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder{

        View mView;

        String mUserId;

        ImageButton mStarbutton ;

        DatabaseReference mDatabaseStar;
        private FirebaseAuth mAuth;

        public NotesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mStarbutton = (ImageButton) mView.findViewById(R.id.starbuttn);

            mAuth = FirebaseAuth.getInstance();

            mUserId = mAuth.getCurrentUser().getUid();

            mDatabaseStar = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Star");

            mDatabaseStar.keepSynced(true);
        }

        public void setStarbutton(final String post_key){

            mDatabaseStar.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mUserId)){
                        mStarbutton.setImageResource(R.mipmap.ic_star_black_24dp);
                    }else{
                        mStarbutton.setImageResource(R.mipmap.ic_star_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.posttitle);
            post_title.setText(title);
        }


        public void setDesc(String desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.postdesc);
            post_desc.setText(desc);
        }

    }

    private void checkUserExist() {
        final String userId  ;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
            Intent setupIntent = new Intent(MainActivity.this,LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
        }
        else{
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        /*mDatabaseUsr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userId)){



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
