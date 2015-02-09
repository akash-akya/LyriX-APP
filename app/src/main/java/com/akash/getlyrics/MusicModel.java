package com.akash.getlyrics;

import java.util.List;

/**
 * Created by akash on 1/31/2015.
 */
public class MusicModel {
    String mDisplayName;
    String mTrack;
    String mArtist;

    public  MusicModel(String mDisplayName, String mArtist, String mTrack){
        this.mDisplayName = mDisplayName;
        this.mArtist = mArtist;
        this.mTrack = mTrack;
    }

    public String getDisplayName(){
        return mDisplayName;
    }

    public String getTrackName(){
        return mTrack;
    }

    public String getArtistName(){
        return mArtist;
    }



}
