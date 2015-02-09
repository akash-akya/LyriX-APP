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
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


public class show extends ActionBarActivity {

    String artist, track, lyrics;
    int noteId=1232;

    TextView mLyrics;
    EditText editBox;
    TextView fail,fetch;
    Button mButtonDone;
    String id;
    private ActionBar mActionBar;
    ScrollView mScrollerEdit;
    public static String PREF_NAME = "MYPREF";
    SharedPreferences mySharedPref;
    Intent starterIntent;
    UpdateView mReceiver;
    ProgressBar progress;
    public static boolean mSearched;
    SharedPreferences mLyricsDb;
    SharedPreferences.Editor mLyricsEditor;
    static String SONGHASH;
    static String mArtistCorrected;
    static boolean mAsyncTaskRunning;
    GetLyrics mTask;
    private static boolean isNewIntent;

     ScaleGestureDetector scaleGD;


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
        setContentView(R.layout.activity_main);

        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        mSearched = getIntent().getBooleanExtra("searched", false);
        mArtistCorrected = "";
        starterIntent = getIntent();
        mLyricsDb = getSharedPreferences("LYRICS_DB", Context.MODE_PRIVATE);
        mLyricsEditor = mLyricsDb.edit();

        mButtonDone = (Button) findViewById(R.id.button);
        fail = (TextView) findViewById(R.id.textView2);
        mLyrics = (TextView) findViewById(R.id.textView);
        editBox = (EditText) findViewById(R.id.editText3);
        progress = (ProgressBar) findViewById(R.id.progressBar2);
        fetch = (TextView) findViewById(R.id.textView9);
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
        new int[] { android.R.attr.actionBarSize });
        final float mActionBarHeight = styledAttributes.getDimension(0, 0)/4;


//        progress.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            artist = extras.getString("artist");
            track = extras.getString("track");
        }
        setHash(artist,track);




        mButtonDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                lockUnlock(true,editBox.getText().toString());
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                lyrics = editBox.getText().toString();

                mLyrics.setText(lyrics);
                mLyricsDb.getString(SONGHASH,"");

                mLyricsEditor.putString(SONGHASH,lyrics);

                mLyricsEditor.commit();

                mLyrics.setVisibility(View.VISIBLE);
                mScrollerEdit.setVisibility(View.GONE);
                mButtonDone.setVisibility(View.GONE);

                getSupportActionBar().show();

            }
        });

        editBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mButtonDone.getVisibility() == View.VISIBLE){
                    editBox.clearFocus();
                    editBox.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(editBox, InputMethodManager.SHOW_FORCED);
                }

            }
        });


        styledAttributes.recycle();
        mActionBar = getSupportActionBar();

        final ScrollView mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            float dy = 0;
            float mPrevDy = 0;

            @Override
            public void onScrollChanged() {
                dy = findViewById(R.id.scrollView).getScrollY();

                if (dy > mPrevDy+mActionBarHeight && mActionBar.isShowing()) {
                    mActionBar.hide();
                } else if ((dy < mPrevDy-mActionBarHeight ) && !mActionBar.isShowing()) {
                    mActionBar.show();
                }
                mPrevDy = dy;
            }
        });

        mScrollerEdit = (ScrollView) findViewById(R.id.scrollView2);
        mScrollerEdit.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            float dy = 0;
            float mPrevDy = 0;

            @Override
            public void onScrollChanged() {
                dy = findViewById(R.id.scrollView2).getScrollY();

                if (dy > mPrevDy+mActionBarHeight && mActionBar.isShowing()) {
                    mActionBar.hide();
                } else if ((dy < mPrevDy-mActionBarHeight ) && !mActionBar.isShowing()) {
                    mActionBar.show();
                }
                mPrevDy = dy;
            }
        });



        scaleGD = new ScaleGestureDetector(this, new simpleOnScaleGestureListener());
        mLyrics.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getPointerCount() == 1){
                    //stuff for 1 pointer
                }else{ //when 2 pointers are present
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            scaleGD.onTouchEvent(event);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            scaleGD.onTouchEvent(event);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return true;
            }
        });


        mLyrics.setText("");


        mTask  = new GetLyrics(this,getIntent());
        mTask.execute();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mReceiver = new UpdateView(this,mTask);
//        mTask.execute();

    }

    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            /*float size = mLyrics.getTextSize();
            float factor = detector.getScaleFactor();
            int increase = 0;
            if(factor > 1.0f)
                increase = 2;
            else if(factor < 1.0f)
                increase = -2;

            size += increase;

            mLyrics.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//            mLyrics.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //remembering the favourable text size

            return true;*/
            float size = mLyrics.getTextSize();
            Log.d("TextSizeStart", String.valueOf(size));

            float factor = detector.getScaleFactor();
            Log.d("Factor", String.valueOf(factor));


            float product = size*factor;
            Log.d("TextSize", String.valueOf(product));
            mLyrics.setTextSize(TypedValue.COMPLEX_UNIT_PX, product);

            size = mLyrics.getTextSize();
            Log.d("TextSizeEnd", String.valueOf(size));
            return true;

        }
    }

    public void mAfterFetch(String lyrics,String artist,String track,String id){

        this.artist = artist;
        this.track = track;
        this.id = id;

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
        }else if(lyrics.isEmpty() || lyrics.length() == 0){

            AlertDialog.Builder mWarning = new AlertDialog.Builder(this);
            mWarning.setTitle("Cannot find lyrics");
            mWarning.setMessage("Cannot find the lyrics.\nPlease fill track and artist name manually.");
            mWarning.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
//                finish();

                }
            });

            mWarning.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            mWarning.show();

            fail.setText("\n\n\nLyrics not found.\ntry searching manually.");

        }
        else {

            invalidateOptionsMenu();
            mLyrics.setText(lyrics);
            this.lyrics = lyrics;
        }

        mButtonDone.setVisibility(View.GONE);

    }


    public static String getHash(String artist, String track){
        return Integer.toString((artist+track).hashCode());
    }

    public static void setHash(String artist, String track){
        SONGHASH = getHash(artist,track);
    }


    @Override
    protected void onResume() {
        super.onResume();


            if (mTask.getStatus() != AsyncTask.Status.RUNNING) {
                mTask = new GetLyrics(this, getIntent());
                mTask.execute();
            }

//        isNewIntent = false;

//        registerReceiver( mReceiver ,
//                new IntentFilter("com.akash.lyrics.update"));
    }




    @Override
    protected void onPause() {

        super.onPause();


        if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }

//        isNewIntent=false;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

//        isNewIntent = true;

//        setHash(intent.getStringExtra("artist"),intent.getStringExtra("track"));
//        mSearched = getIntent().getBooleanExtra("searched", false);
//        new GetLyrics(this,intent).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mReceiver);
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

//        if(mySharedPref.getBoolean("hide_notification",false)) {
//            menu.findItem(R.id.item6).setTitle("Show Notification");
//        }else {
//            menu.findItem(R.id.item6).setTitle("Hide Notification");
//        }

        if(mLyrics.getText().toString().length() == 0) {
            menu.findItem(R.id.item2).setEnabled(false);
        }
        else {
            menu.findItem(R.id.item2).setEnabled(true);
        }

//        if(mySharedPref.getString("theme","light").equals("dark")) {
//            menu.findItem(R.id.item4).setTitle("Light Theme");
//        }

        if(mTask.getStatus() == AsyncTask.Status.RUNNING){
            menu.findItem(R.id.item1).setVisible(false);
            menu.findItem(R.id.item2).setVisible(false);
//            menu.findItem(R.id.item4).setVisible(false);

        }

//        if(mySharedPref.getBoolean("playing", false)){
            menu.findItem(R.id.item9).setVisible(true);
//        }

        return true;
    }

    void mSetEditorVisible(){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(editBox, InputMethodManager.SHOW_FORCED);

        editBox.setText(lyrics);
        mLyrics.setVisibility(View.GONE);
        mScrollerEdit.setVisibility(View.VISIBLE);
        mButtonDone.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.item1:
//                lockUnlock(false,mLyrics.getText().toString());
                mSetEditorVisible();
                return true;

            case R.id.menu_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
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
//
//            case R.id.item4:
//                SharedPreferences.Editor ed = mySharedPref.edit();
//                if(mySharedPref.getString("theme","light").equals("dark")) {
//                     ed.putString("theme", "light");
//                }
//                else {
//                    ed.putString("theme", "dark");
//                }
//                ed.apply();
//                invalidateOptionsMenu();
//                Bundle bundle = getIntent().getExtras();
//                starterIntent.putExtras(bundle);
//                finish();
//                startActivity(starterIntent);
//                return true;

//            case R.id.item5:
//                Intent in = new Intent(this, AboutApp.class);
//                startActivity(in);
//                return true;

//            case R.id.item6:
//                ed = mySharedPref.edit();
//                if(mySharedPref.getBoolean("hide_notification",false)) {
//                    ed.putBoolean("hide_notification", false);
//                }
//                else {
//                    ed.putBoolean("hide_notification", true);
//                    notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.cancel(noteId);
//                }
//                ed.apply();
//
//                return true;

            case R.id.item7:
                Intent in;
                in = new Intent(this, ArtistInfo.class);
                startActivity(in);
                return true;

            case R.id.item8:
                in = new Intent(this, MainActivity.class);
//                in.putExtra("show_")
                startActivity(in);
//                finish();
                return true;

            case R.id.item9:
                String lyrics = mLyrics.getText().toString();
                if(mySharedPref.getBoolean("playing",false)){

                    SaveLyrics mDialog = new SaveLyrics(this,artist,track,lyrics,id);

                    mDialog.setPositiveButton("OK");
                    mDialog.setNegativeButton("NO");
                    mDialog.setNeutralButton("CANCEL");

                    mDialog.show();

                } else {


                    mLyricsEditor.putString(SONGHASH,lyrics);
//                    Log.w("artist - track ",artist+track);
                    mLyricsEditor.commit();

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}