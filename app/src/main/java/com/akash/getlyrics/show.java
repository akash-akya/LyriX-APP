package com.akash.getlyrics;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


public class show extends ActionBarActivity implements ViewTreeObserver.OnScrollChangedListener {

    String artist, track, lyrics;
    int noteId=1232;
    ProgressDialog mProgressDialog;
    TextView mLyrics;
    EditText editBox;
    TextView fail,fetch;
    Button edit,google;
    String id;
    private float mActionBarHeight;
    private ActionBar mActionBar;
    ScrollView mScrollerEdit;
    public static String PREF_NAME = "MYPREF";
    SharedPreferences mySharedPref;
    Intent starterIntent;
    public GetLyrics mTask;
    UpdateView mReceiver;
    ProgressBar progress;
    public static String mArtist;
    public static boolean mSearched;
    float mPrevious_y;
    SharedPreferences mLyricsDb;
    SharedPreferences.Editor mLyricsEditor;



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
        mPrevious_y = 0;
        if(getIntent().getBooleanExtra("searched",false)){
            mSearched = true;
        }

        setContentView(R.layout.activity_main);

//        mydb = new DBHelper(this);

        starterIntent = getIntent();
        mArtist = "";

        mLyricsDb = getSharedPreferences("LYRICS_DB", Context.MODE_PRIVATE);
        mLyricsEditor = mLyricsDb.edit();

        edit = (Button) findViewById(R.id.button);
        google = (Button) findViewById(R.id.button4);
        fail = (TextView) findViewById(R.id.textView2);
        mLyrics = (TextView) findViewById(R.id.textView);
        editBox = (EditText) findViewById(R.id.editText3);
        mScrollerEdit = (ScrollView) findViewById(R.id.scrollView2);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        fetch = (TextView) findViewById(R.id.textView8);


        progress.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        mLyrics.setFocusable(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            artist = extras.getString("artist");
            track = extras.getString("track");
        }


        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lockUnlock(true,editBox.getText().toString());

            }
        });

        google.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        editBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edit.getVisibility() == View.VISIBLE){
                    editBox.clearFocus();
                    editBox.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(editBox, InputMethodManager.SHOW_FORCED);
                }

            }
        });

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
        new int[] { android.R.attr.actionBarSize });
        mActionBarHeight = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        mActionBar = getSupportActionBar();
        (findViewById(R.id.scrollView)).getViewTreeObserver().addOnScrollChangedListener(this);
        (findViewById(R.id.scrollView2)).getViewTreeObserver().addOnScrollChangedListener(this);

        google.setVisibility(View.INVISIBLE);
        mLyrics.setText("");

        mTask =  new GetLyrics(this,getApplicationContext(),getIntent());
        mReceiver = new UpdateView(this,mTask);

        mTask.execute();

    }



    @Override
    public void onScrollChanged() {
        float y = (findViewById(R.id.scrollView)).getScrollY();

        if ((y >= mActionBarHeight) && mActionBar.isShowing()) {
            mActionBar.hide();
        } else if ( (y==0  ) && !mActionBar.isShowing() ) {
            mActionBar.show();
        }
    }

    public void mAfterFetch(String lyrics,String artist,String track,String id){

        this.artist = artist;
        this.track = track;
        this.id = id;

        mActionBar = getSupportActionBar();
        mActionBar.setTitle(artist);
        mActionBar.setSubtitle(track);

        if(lyrics.equals("No Internet connection")) {
            AlertDialog.Builder mNoConnectionError = new AlertDialog.Builder(this);
            mNoConnectionError.setTitle("No Internet connection");
            mNoConnectionError.setMessage("Unable to connect to Internet!\nPlease check your connection.");
            mNoConnectionError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            mNoConnectionError.show();
        }

        if(lyrics.isEmpty() || lyrics.length() == 0){

            AlertDialog.Builder mWarning = new AlertDialog.Builder(this);
            mWarning.setTitle("Cannot find lyrics");
            mWarning.setMessage("Cannot find the lyrics.\nPlease fill track and artist name manually.");
            mWarning.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            mWarning.show();

            google.setVisibility(View.VISIBLE);
//            Log.w("LyricsApp","inside empty");
            fail.setText("\n\n\nLyrics not found.\ntry searching manually.");
            textv(false);

        }
        else {
            google.setVisibility(View.GONE);
//            menu.findItem(R.id.item2).setEnabled(true);
            invalidateOptionsMenu();
            mLyrics.setText(lyrics);
            this.lyrics = lyrics;
            textv(true);
        }

//        mLyrics.setFocusable(false);
        edit.setVisibility(View.GONE);

    }


    public void textv(boolean val){
        if(val) {
            fail.setVisibility(View.GONE);
            mLyrics.setVisibility(View.VISIBLE);

        }
        else {
            fail.setVisibility(View.VISIBLE);
            mLyrics.setVisibility(View.GONE);
        }
    }


    public static String getHash(String artist, String track){
        return Integer.toString((artist+track).hashCode());
    }


    /**
     * Method which locks and unlocks editText component
     * @param value our boolean value which using in or if operator
     */
    public void lockUnlock(boolean value,String lyrics) {
        if (value) {

            if(getCurrentFocus()!=null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }


            if(lyrics.length() == 0){
                google.setVisibility(View.VISIBLE);
                textv(false);
                Log.w("LyricsApp", "inside empty");
            } else {

                mLyrics.setText(lyrics);
//                Cursor rs = mydb.getData(artist, track);
                mLyricsDb.getString(getHash(artist,track),"");

                mLyricsEditor.putString(getHash(artist,track),mLyrics.getText().toString());
                mLyricsEditor.commit();

            }

            mLyrics.setVisibility(View.VISIBLE);
            editBox.setVisibility(View.GONE);
            mScrollerEdit.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);


        } else {

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(editBox, InputMethodManager.SHOW_FORCED);
            if(lyrics.length() == 0){
                google.setVisibility(View.INVISIBLE);
                textv(true);
            }
            editBox.setText(lyrics);
            editBox.setVisibility(View.VISIBLE);
            mScrollerEdit.setVisibility(View.VISIBLE);
            mLyrics.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        }
    }


    /////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver( mReceiver ,
                new IntentFilter("com.akash.lyrics.update"));
    }

    @Override
    protected void onPause() {

        super.onPause();
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

//////////////////// menu /////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);//Menu Resource, Menu

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(mySharedPref.getBoolean("hide_notification",false)) {
            menu.findItem(R.id.item6).setTitle("Show Notification");
        }else {
            menu.findItem(R.id.item6).setTitle("Hide Notification");
        }



//        menu.findItem(R.id.item6).setEnabled(true);

        if(mLyrics.getText().toString().length() == 0) {
            menu.findItem(R.id.item2).setEnabled(false);
        }
        else {
            menu.findItem(R.id.item2).setEnabled(true);
        }

        if(mySharedPref.getString("theme","light").equals("dark")) {
            menu.findItem(R.id.item4).setTitle("Light Theme");
        }

        if(mTask.getStatus() == AsyncTask.Status.RUNNING){
            menu.findItem(R.id.item1).setVisible(false);
            menu.findItem(R.id.item2).setVisible(false);
            menu.findItem(R.id.item4).setVisible(false);

        }

//        if(mySharedPref.getBoolean("playing", false)){
            menu.findItem(R.id.item9).setVisible(true);
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                lockUnlock(false,mLyrics.getText().toString());
                return true;

            case R.id.item2:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody;
                if(mLyrics.getVisibility() == View.VISIBLE ){

                    shareBody = mLyrics.getText().toString();
                } else {
                    shareBody = editBox.getText().toString();
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,getTitle() );
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;

            case R.id.item3:
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(noteId);

                finish();
                System.exit(0);
                return true;

            case R.id.item4:
                SharedPreferences.Editor ed = mySharedPref.edit();
                if(mySharedPref.getString("theme","light").equals("dark")) {
                     ed.putString("theme", "light");
                }
                else {
                    ed.putString("theme", "dark");
                }

                ed.apply();
                invalidateOptionsMenu();
                finish();
                startActivity(starterIntent);
                return true;

            case R.id.item5:
                Intent in = new Intent(this, AboutApp.class);
                startActivity(in);
                return true;

            case R.id.item6:
                ed = mySharedPref.edit();
                if(mySharedPref.getBoolean("hide_notification",false)) {
                    ed.putBoolean("hide_notification", false);
                }
                else {
                    ed.putBoolean("hide_notification", true);
                    notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(noteId);
                }
                ed.apply();

                return true;

            case R.id.item7:
                in = new Intent(this, ArtistInfo.class);
                startActivity(in);
                return true;

            case R.id.item8:
                in = new Intent(this, MainActivity.class);
//                in.putExtra("show_")
                startActivity(in);
                finish();
                return true;

            case R.id.item9:
                String lyrics = mLyrics.getText().toString();
                if(mySharedPref.getBoolean("playing",false)){
                    Intent intent = getIntent();
                    SaveLyrics mDialog = new SaveLyrics(this,intent.getStringExtra("artist"),
                            intent.getStringExtra("track"),lyrics,id);

                    mDialog.setPositiveButton("OK");
                    mDialog.setNegativeButton("NO");
//                    mDialog.setNeutralButton("CANCEL");
                    mDialog.show();
                } else {
//                    mydb.insertContact(artist, track, lyrics);
                    mLyricsEditor.putString(getHash(artist,track),lyrics);
                    mLyricsEditor.commit();

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}