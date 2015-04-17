package com.akash.getlyrics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by akash on 1/20/2015.
 */
public class SaveLyrics extends AlertDialog.Builder {

    String lyrics,artist,track,id;
    SharedPreferences mySharedPref,mLyricsDb;
    SharedPreferences.Editor mLyricsDbEditor;

    SaveLyrics(Context context,String artist,String track,String lyrics,String id){//,DBHelper mydb){
        super(context);
        this.lyrics = lyrics;
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
                artist = mySharedPref.getString("artist", "");
                track =  mySharedPref.getString("track", "");
                if(artist.length()!= 0 || track.length()!=0){
                    String mHash = show.getHash(artist,track);
                    mLyricsDbEditor.putString(mHash,lyrics);
                    mLyricsDbEditor.putString(mHash+".ArtistNameCorrected",artist);
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

                mLyricsDbEditor.putString(show.SONGHASH,lyrics);
                mLyricsDbEditor.commit();

            }
        };
        return super.setNegativeButton(text, list);
    }

    public AlertDialog.Builder setNeutralButton(CharSequence text) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        return super.setNeutralButton(text, listener);
    }
}
