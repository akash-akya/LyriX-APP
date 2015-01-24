package com.akash.getlyrics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by akash on 1/19/2015.
 */
public class UpdateView extends BroadcastReceiver {
    show myActivity;
    GetLyrics mTask;


    UpdateView(show myActivity, GetLyrics mTask){
        this.myActivity = myActivity;
        this.mTask = mTask;

    }

    @Override
    public void onReceive(Context context, Intent in) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if(mTask.getStatus() != AsyncTask.Status.RUNNING){
            mTask = new GetLyrics(myActivity,context,in);
            mTask.execute();
        }
    }
}
