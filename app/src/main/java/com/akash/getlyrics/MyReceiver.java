package com.akash.getlyrics;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;


public class MyReceiver extends BroadcastReceiver {
    SharedPreferences mypref;
    SharedPreferences.Editor mEditor;

    public MyReceiver() {

    }

    public void updateNotification(Context context,boolean mPlaying,
                                   String mArtist, String mTrack, String mAlbum  ){


        mEditor = mypref.edit();

        if(mPlaying) {

            Intent intent = new Intent(context, show.class);
            intent.putExtra("artist", mArtist);
            intent.putExtra("track", mTrack);
            intent.putExtra("album", mAlbum);


            mEditor.putBoolean("playing",true);
            mEditor.putString("artist",mArtist);
            mEditor.putString("track",mTrack);
            mEditor.putString("album",mAlbum);
            mEditor.commit();

            intent.putExtra("searched",false);
            intent.putExtra("store", true);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT );

            Notification notification = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.music)
                    .setContentText(mTrack)
                    .setContentTitle(mArtist).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int noteId = 1232;

            notificationManager.notify(noteId, notification);

            Intent inte = new Intent();
            inte.setAction("com.akash.lyrics.update");
            inte.putExtra("artist",mArtist);
            inte.putExtra("track", mTrack);
            inte.putExtra("album", mAlbum);
            inte.putExtra("store", true);
            context.sendBroadcast(inte);

        }
        else {
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

            mEditor.putBoolean("playing",false);
            mEditor.putString("artist","");
            mEditor.putString("track","");
            mEditor.commit();
        }
    }



    @Override
    public void onReceive(Context context, Intent in) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        String music_state ="com.android.music.playstatechanged" ;
        String music_meta = "com.android.music.metachanged";
        String music_comp ="com.android.music.playbackcomplete" ;


        mypref = context.getSharedPreferences(show.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());



        if(!settings.getBoolean("notifications_new_message",false))
            return;

        if(in.getAction().equals(music_state)){
            updateNotification(context, in.getBooleanExtra("playing",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals(music_meta)){
            updateNotification(context, in.getBooleanExtra("playing",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals(music_comp)){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

        ////////////////////////////  htc  //////////////////////////
        }  else if(in.getAction().equals("com.htc.music.playstatechanged")){
            updateNotification(context, in.getBooleanExtra("isplaying",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals("com.htc.music.metachanged")){
            updateNotification(context, in.getBooleanExtra("isplaying",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals("com.htc.music.playbackcomplete")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

        } else if(in.getAction().equals("fm.last.android.metachanged")){
            updateNotification(context, true,
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals("fm.last.android.playbackpaused")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);
        } else if(in.getAction().equals("fm.last.music.playbackcomplete")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

        } else if(in.getAction().equals("com.adam.aslfms.notify.playstatechanged") ||
                  in.getAction().equals("com.adam.aslfms.notify.metachanged")){


            int state = in.getIntExtra("state",3);
            int var8 =0;
            if(state == 0)
                var8 =1;
            else if(state == 1)
                var8 =0;

            if(var8 == 0){
                updateNotification(context, in.getBooleanExtra("playing",false),
                        in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
            }else {
                int noteId = 1232;
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(noteId);
            }


        } else if(in.getAction().equals("com.spotify.mobile.android.playbackcomplete")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

        } else if(in.getAction().equals("com.real.RealPlayer.playstatechanged")){
            updateNotification(context, in.getBooleanExtra("isplaying",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals("com.real.RealPlayer.metachanged")){
            updateNotification(context, in.getBooleanExtra("isplaying",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals("com.real.RealPlayer.playbackcomplete")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

        } else if(in.getAction().equals("com.tbig.playerprotrial.playstatechanged") ||
                in.getAction().equals("com.tbig.playerprotrial.metachanged") ||
                in.getAction().equals("com.tbig.playerpro.playstatechanged") ||
                in.getAction().equals("com.tbig.playerpro.metachanged")  ){
            updateNotification(context, in.getBooleanExtra("playing",false),
                    in.getStringExtra("artist"), in.getStringExtra("track"), in.getStringExtra("album"));
        } else if(in.getAction().equals("com.tbig.playerprotrial.playbackcomplete") ||
                  in.getAction().equals("com.tbig.playerpro.playbackcomplete")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);

        }else if(in.getAction().equals("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED")){
            updateNotification(context, true,
                    in.getStringExtra("ARTIST_NAME"), in.getStringExtra("TRACK_NAME"), in.getStringExtra("ALBUM_NAME"));
        } else if(in.getAction().equals("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED") ||
                  in.getAction().equals("com.sonyericsson.music.TRACK_COMPLETED")){
            int noteId = 1232;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(noteId);
        }

    }
}

