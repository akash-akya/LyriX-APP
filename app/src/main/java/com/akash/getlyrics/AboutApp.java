package com.akash.getlyrics;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;


public class AboutApp extends ActionBarActivity {
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
        setContentView(R.layout.activity_about_app);

        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        setTitle("About");

    }


}