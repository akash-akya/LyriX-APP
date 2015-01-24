package com.akash.getlyrics;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(mySharedPref.contains("theme")) {
            if (mySharedPref.getString("theme", "light").equals("dark")) {
                setTheme(R.style.AppTheme2);
            } else {
                setTheme(R.style.AppTheme);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        getSupportActionBar().hide();
        setTitle("Lyrics Finder");

    }


}