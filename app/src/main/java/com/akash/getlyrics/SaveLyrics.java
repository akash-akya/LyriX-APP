package com.akash.getlyrics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by akash on 1/20/2015.
 */
public class SaveLyrics extends AlertDialog.Builder {

    String lyrics;
//    DBHelper mydb;
    String artist,track;
    String id;
    SharedPreferences mySharedPref,mLyricsDb;
    SharedPreferences.Editor mLyricsDbEditor;

    SaveLyrics(Context context,String artist,String track,String lyrics,String id){//,DBHelper mydb){
        super(context);
        this.lyrics = lyrics;
//        this.mydb = mydb;
        this.artist = artist;
        this.track = track;
        this.id = id;


        this.setTitle("Save lyrics...");

        // Setting Dialog Message
        this.setMessage("Do you want to save this lyrics to currently playing music?");

        // Setting Icon to Dialog
        this.setIcon(R.drawable.ic_action_save);


        mySharedPref = context.getSharedPreferences("MYPREF", Context.MODE_PRIVATE);
        mLyricsDb = context.getSharedPreferences("LYRICS_DB", Context.MODE_PRIVATE);
        mLyricsDbEditor = mLyricsDb.edit();

    }

    public AlertDialog.Builder setPositiveButton(CharSequence text) {
        DialogInterface.OnClickListener list = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed YES button. Write Logic Here
                Log.w("Names",artist+" "+track+" "+lyrics);
                artist = mySharedPref.getString("artist", "");
                track =  mySharedPref.getString("track", "");
                if(artist.length()!= 0 || track.length()!=0){
//                    mydb.insertContact(artist, track, lyrics);
                    mLyricsDbEditor.putString(show.getHash(artist,track),lyrics);
                    mLyricsDbEditor.commit();
                }else {
                    Log.w("lyricsApp","Saving dialog error");
                }

            }
        };
        return super.setPositiveButton(text, list);
    }


    public AlertDialog.Builder setNegativeButton(CharSequence text) {
        DialogInterface.OnClickListener list = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed YES button. Write Logic Here

//                mydb.insertContact(artist, track,lyrics);
                mLyricsDbEditor.putString(show.getHash(artist,track),lyrics);
                mLyricsDbEditor.commit();

            }
        };
        return super.setNegativeButton(text, list);
    }
//
//    @NonNull
//    @Override
//    public AlertDialog.Builder setNeutralButton(CharSequence text) {
//        DialogInterface.OnClickListener list = new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // User pressed YES button. Write Logic Here
//
//                return;
//           }
//        };
//        return super.setNeutralButton(text, list);
//    }
}
