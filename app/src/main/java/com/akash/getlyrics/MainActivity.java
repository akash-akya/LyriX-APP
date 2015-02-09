package com.akash.getlyrics;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {


    EditText artist;
    EditText track;
    Button search;
    SharedPreferences mySharedPref;
    public static String PREF_NAME = "MYPREF";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if(settings.contains("example_list")) {
            if (settings.getString("example_list", "0").equals("0")) {
                setTheme(R.style.AppTheme2);
            } else {
                setTheme(R.style.AppTheme);
            }
        }

          super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show);

        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        artist = (EditText) findViewById(R.id.editText);
        track = (EditText) findViewById(R.id.editText2);
        search = (Button) findViewById(R.id.button2);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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



        artist.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try{
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

                return true;
                }
                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().hide();
        setTitle(R.string.app_name);

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

//        menu.findItem(R.id.item1).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int noteId=1232;
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.exit:
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(noteId);

                finish();
                System.exit(0);
                return true;

            case R.id.menu_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }



}