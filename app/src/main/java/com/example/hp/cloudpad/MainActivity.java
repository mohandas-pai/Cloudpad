package com.example.hp.cloudpad;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.provider.CalendarContract;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Task;
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
    private long backPressedTime = 0;
    FloatingActionButton fab;

    private boolean processstar = false;

    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout) findViewById(R.id.progressbar_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
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
            Log.i("Came","Here 1");
            Intent setupIntent = new Intent(MainActivity.this,LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
            finish();
        }
        else {
            mUserId = mFirebaseUser.getUid();
            if (mUserId == null) {
                //Go to login
                Log.i("Came", "Here 2");
                Intent setupIntent = new Intent(MainActivity.this, LoginActivity.class);
                setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setupIntent);
                finish();
            }
            else {
                Log.i("Main Activity", "mAuth:" + mAuth + " mFirebaseUser:" + mFirebaseUser + " mUserId:" + mUserId);

                mDatabase = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Notes");
                mDatabaseStar = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(mUserId).child("Star");

                mDatabase.keepSynced(true);
                mDatabaseStar.keepSynced(true);


                mAuthListener = new FirebaseAuth.AuthStateListener() {

                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() == null) {
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                            finish();
                        }
                    }
                };

                mNotesList = (RecyclerView) findViewById(R.id.NotesList);
                mNotesList.setHasFixedSize(true);
                mNotesList.setLayoutManager(new LinearLayoutManager(this));
                new Task().execute();
            }
        }


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
                viewHolder.setDatetime(model.getDatetime());
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

        public void setDatetime(String datetime) {
            TextView post_time = (TextView) mView.findViewById(R.id.posttime);
            post_time.setText(datetime);
        }

    }


    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            layout.setVisibility(View.VISIBLE);
            mNotesList.setVisibility(View.GONE);
            fab.setVisibility(View.INVISIBLE);
            //lla.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            layout.setVisibility(View.GONE);
            mNotesList.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            //lla.setVisibility(View.VISIBLE);
            //adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.call_log){
            mAuth.signOut();
            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to quit",
                    Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            super.onBackPressed();       // bye
        }

    }

}
