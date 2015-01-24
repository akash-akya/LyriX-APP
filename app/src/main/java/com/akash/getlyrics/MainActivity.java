package com.akash.getlyrics;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {


    EditText artist;
    EditText track;
    Button search;
    SharedPreferences mySharedPref;
    public static String PREF_NAME = "MYPREF";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(mySharedPref.contains("theme")) {
            if (mySharedPref.getString("theme", "light").equals("dark")) {
                setTheme(R.style.AppTheme2);
            } else {
                setTheme(R.style.AppTheme);
            }
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show);

        artist = (EditText) findViewById(R.id.editText);
        track = (EditText) findViewById(R.id.editText2);
        search = (Button) findViewById(R.id.button2);



        final AlertDialog.Builder mWarning = new AlertDialog.Builder(this);
        mWarning.setTitle("Empty field");
//                        mWarning.setIcon()
        mWarning.setMessage("Track name field is empty!\nPlease fill the field.");
        mWarning.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(track.getText().toString().length() != 0){

                        Intent intent = new Intent(MainActivity.this, show.class);

                        if(artist.getText().toString().length() != 0) {
                            intent.putExtra("artist", artist.getText().toString());
                        } else {
                            intent.putExtra("artist", "");
                        }


                        intent.putExtra("track", track.getText().toString());
                        intent.putExtra("album", "");
                        intent.putExtra("store",false);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("searched",true);
                        startActivity(intent);
                    }  else {
                        mWarning.show();
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                }


            }
        });


//        getSupportActionBar().hide();
        setTitle("Lyrics Finder");

    }


    //////////////////// menu /////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.




        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.launch, menu);//Menu Resource, Menu

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(mySharedPref.getBoolean("hide_notification",false)) {
            menu.findItem(R.id.item2).setTitle("Show Notification");
        }else {
            menu.findItem(R.id.item2).setTitle("Hide Notification");
        }
//        menu.findItem(R.id.item1).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int noteId=1232;
        switch (item.getItemId()) {
            case R.id.item1:
                Intent in = new Intent(this, AboutApp.class);
                startActivity(in);
                return true;

            case R.id.item2:
                SharedPreferences.Editor ed = mySharedPref.edit();
                if(mySharedPref.getBoolean("hide_notification",false)) {
                    ed.putBoolean("hide_notification", false);
                }
                else {
                    ed.putBoolean("hide_notification", true);
                     NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(noteId);
                }
                ed.apply();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}