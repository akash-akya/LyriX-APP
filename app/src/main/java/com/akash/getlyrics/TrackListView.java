package com.akash.getlyrics;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TrackListView extends ActionBarActivity {

    Cursor musiccursor;
    SharedPreferences mySharedPref;
    public static String PREF_NAME = "MYPREF";
    MusicListAdapter adapter;
//    ArrayAdapter<String> adapterFilter;
//    List<String> mDisplayName;
//    List<String> mFilter;
//    List<String> mArtist;
//    List<String> mTrack;
    ArrayList<MusicModel> musicDB = new ArrayList<MusicModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if(settings.contains("example_list")) {
            if (settings.getString("example_list", "0").equals("0")) {
                setTheme(R.style.AppTheme2);
            } else {
                setTheme(R.style.AppTheme);
            }
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list_view);

        final TextView mSearchFilter = (TextView) findViewById(R.id.search_filter);

//         mDisplayName = new ArrayList<String>();
//         mFilter = new ArrayList<String>();
//         mArtist = new ArrayList<String>();
//         mTrack = new ArrayList<String>();



        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE
        };

        musiccursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE);



        while (musiccursor.moveToNext()) {

            MusicModel musicItem = new MusicModel(musiccursor.getString(0),
                                                    musiccursor.getString(1),
                                                    musiccursor.getString(2));

            musicDB.add(musicItem);
        }


        ListView listView1 = (ListView) findViewById(R.id.PhoneMusicList);


        adapter = new MusicListAdapter(this, musicDB);


        listView1.setAdapter(adapter);




        mSearchFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = mSearchFilter.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.listview_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int noteId=1232;
        switch (item.getItemId()) {

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

            case R.id.search:
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
