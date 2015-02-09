package com.akash.getlyrics;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * Created by akash on 1/19/2015.
 */
public class GetLyrics  extends AsyncTask<Void, Void, Void> {
    String lyrics="";
    String search_item;
    String artist;
    String track;
    String album;
    Intent intent;
    Context context;
    show myShow;
    String id;
    boolean mNoConnection;
    SharedPreferences mLyricsDb;
    SharedPreferences.Editor mLyricsEditor;


    GetLyrics(Context context, Intent intent){
        this.intent = intent;
        this.context = context;
        this.myShow = (show) context;
        mNoConnection = false;

    }


    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        mLyricsDb = context.getSharedPreferences("LYRICS_DB", Context.MODE_PRIVATE);
        mLyricsEditor = mLyricsDb.edit();

        String cachedArtist = intent.getStringExtra("cachedArtist");
        String cachedTrack= intent.getStringExtra("cachedTrack");


        artist="";
        track="";
        Bundle extras = intent.getExtras();
        if (extras != null) {
            artist = extras.getString("artist");
            track = extras.getString("track");
            album = extras.getString("album");
        } else {
            this.cancel(true);
        }
//
//        if(cachedArtist != null && cachedTrack != null){
//            if(cachedArtist.equals(artist) && cachedTrack.equals(track)){
//                this.cancel(true);
//            }
//        }

        intent.putExtra("cachedArtist",artist);
        intent.putExtra("cachedTrack",track);

        myShow.mLyrics.setVisibility(View.GONE);
        myShow.fail.setVisibility(View.GONE);
        myShow.progress.setVisibility(View.VISIBLE);

        show.mAsyncTaskRunning = true;

    }

    @Override
    protected Void doInBackground(Void... params) {

        String mURLprefix = " -youtube.com ";
        String wikia = "lyrics.wikia";
        String azlyric = "azlyrics";
        String metrolyrics = "metrolyrics";
        String musicXmatch = "musixmatch";
        String lyricsontop = "lyricsontop";
        String directlylyrics = "directlyrics";
        String lyricsfreak = "lyricsfreak";
        String lyricsmode = "lyricsmode";
        String lyricsastra = "lyrics.astraweb";
        String songlyrics = "songlyrics";

        lyrics = mLyricsDb.getString(show.SONGHASH,"");
        show.mArtistCorrected = mLyricsDb.getString(show.SONGHASH+".ArtistNameCorrected",artist);

        if (lyrics.length()==0) {
            try {
                artist = correctTag(artist);
                track = correctTag(track);
                album = correctTag(album);
                show.mArtistCorrected = artist;

                lyrics="";

                String item = mURLprefix + artist + " " + track + " lyrics";
//                        Log.w("LyricsApp", artist + " - " + track + " - " + album );

                for(int i=0; i<2; i++) {

                    if(i==1){
                        item = mURLprefix + artist + " " + track + " " + album + " lyrics";
                    }

                    Log.w("LyricsApp", item );

                    try {
                        search_item = URLEncoder.encode(item, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    List<String> nurls = searchv2(search_item);

                    if (nurls == null)
                        return null;

                    for (String iUrl : nurls) {

                        if (iUrl.contains(azlyric)) {
                            lyrics = fetchLyrics(iUrl, "div[style=margin-left:10px;margin-right:10px;]");
//                            Log.w("LyricsApp", "lyrics : "+lyrics);
                            Log.w("LyricsApp", "in azlyric");
                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(wikia)) {

                            lyrics = fetchLyrics(iUrl, "div[class=lyricbox]");
                            Log.w("LyricsApp", "in wikia");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("metrolyrics")) {

                            lyrics = fetchLyrics(iUrl, "div[id=lyrics-body-text]");
                            Log.w("LyricsApp", "in metrolyrics");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(musicXmatch)) {

                            lyrics = fetchLyrics(iUrl, "div[id=selectable-lyrics]");
                            Log.w("LyricsApp", "in musicXmatch");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(lyricsontop)) {

                            lyrics = fetchLyrics(iUrl, "div[class=content_inside_lyric]");
                            Log.w("LyricsApp", "in lyricsontop");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(directlylyrics)) {

                            lyrics = fetchLyrics(iUrl, "div[class=lyrics lyricsselect]");
                            Log.w("LyricsApp", "in directlylyrics");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("genius")) {

                            lyrics = fetchLyrics(iUrl, "div[class=lyrics_container]");
                            Log.w("LyricsApp", "in genius.com");

                            if (lyrics.length() != 0)
                                break;

                        }else if (iUrl.contains("lyricsbox")) {

                            lyrics = fetchLyrics(iUrl, "table");
                            Log.w("LyricsApp", "in lyricsbox.com");

                            if (lyrics.length() != 0)
                                break;

                        }else if (iUrl.contains("elyricsworld")) {

                            lyrics = fetchLyrics(iUrl, "div[id=lyric_itself]");
                            Log.w("LyricsApp", "in elyricsworld.com");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(lyricsfreak)) {

                            lyrics = fetchLyrics(iUrl, "div[class=lyrictxt]");
                            Log.w("LyricsApp", "in lyricsfreak");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(lyricsmode)) {

                            lyrics = fetchLyrics(iUrl, "div[id=lyrics_text]");
                            Log.w("LyricsApp", "in lyricsmode");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(lyricsastra)) {

                            lyrics = fetchLyrics(iUrl, "font[face=arial]");
                            Log.w("LyricsApp", "in lyricsastra");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains(songlyrics)) {

                            lyrics = fetchLyrics(iUrl, "p[id=songLyricsDiv]");
                            Log.w("LyricsApp", "in http://www.songlyrics.com/");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("hindilyrics")) {

                            lyrics = fetchLyrics(iUrl, "font[face=verdana]");
                            Log.w("LyricsApp", "in http://www.hindilyrics.net/");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("glamsham")) {

                            lyrics = fetchLyrics(iUrl, "tbody > tr > td[align=center] > table > tbody > tr > td[align=left] > font.general"); //td[width=480 align=left] font[class=general]");
                            Log.w("LyricsApp", "in http://www.glamsham.com/");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("smriti")) {

                            lyrics = fetchLyrics(iUrl, "div[class=songbody]");
                            Log.w("LyricsApp", "in http://www.smriti.com/");
                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("lyricsmint")) {

                            lyrics = fetchLyrics(iUrl, "div[style=float: left; font-size: 14px; line-height: 20px; padding: 5px; width: 300px;]");
                            Log.w("LyricsApp", "in http://www.lyricsmint.com/");

                            if (lyrics.length() == 0) {
                                lyrics = fetchLyrics(iUrl, "div[id=lyric]");
                                Log.w("LyricsApp", "2 time in http://www.lyricsmint.com/");
                            }

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("lyricsmasti")) {

                            lyrics = fetchLyrics(iUrl, "div[id=lcontent1]");
                            Log.w("LyricsApp", "in http://www.lyricsmasti.com/");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("hindigeetmala")) {

                            lyrics = fetchLyrics(iUrl, "div[class=song]");
                            Log.w("LyricsApp", "in hindigeetmala.com");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("giitaayan")) {

                            lyrics = fetchLyrics(iUrl, "div[id=ConvertedText]");
                            Log.w("LyricsApp", "in giitaayan.com");

                            if (lyrics.length() != 0)
                                break;

                        } else if (iUrl.contains("bollywoodhungama")) {

                            lyrics = fetchLyrics(iUrl, "div[id=ConvertedText]");
                            Log.w("LyricsApp", "in bollywoodhungama");

                            if (lyrics.length() != 0)
                                break;

                        }
                    }

                    if (lyrics.length() != 0)
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String correctTag(String tag){

        String[] remove = {"128","160","192","96","320","mp3","\\.com","kbps","<unknown>","\\sft\\.", "\\sfeat\\.", "\\sfeat\\s", "\\sft\\s", "www[^\\s]+",
                "[^a-z_0-9\\s]", "^[0-9\\.\\s]+", "remix", "original mix", " mix ", " radio edit "};

//        String art = tag.toLowerCase().replaceAll("ft.|feat.|[\\d]", "").replaceAll("www[^\\s]*","").replaceAll("_"," ").replaceAll("[^a-z_0-9\\s]","").trim();
//        art = art.replaceAll(" remix | original mix | mix | radio edit ","").replaceAll("www.* ","");
        tag = tag.replaceAll("_"," ");

        tag=tag.toLowerCase();
        for(String str : remove){
            tag = tag.replaceAll(str,"");
        }


        int startIndex = tag.indexOf("(");
        int endIndex = tag.indexOf(")");
        String replacement = "";
        String toBeReplaced;
        if(startIndex != -1 && endIndex != -1) {
            toBeReplaced = tag.substring(startIndex, endIndex + 1);
            tag = tag.replace(toBeReplaced, replacement);
        }

        return tag;

    }

    /////////////////////search v2/////////////////////////////////////

    private List<String> searchv2(String sitem) throws IOException{

//        String mSufix = "+site:lyrics.wikia.com+OR+site:azlyrics.com+OR+site:musixmatch.com+OR+site:lyricsontop.com+OR+site:directlyrics.com+OR+site:lyricsfreak.com+OR+site:lyricsmode.com+OR+site:lyrics.astraweb.com+OR+site:songlyrics.com+OR+site:hindilyrics.net+OR+site:glamsham.com+OR+site:giitaayan.com+OR+site:bollywoodhungama.com";

        String lurl = "https://www.google.com/search?q=" + sitem; //+ mSufix;

        Log.w("LyricsApp", lurl);

        List<String> urls = new ArrayList<String>();
        Document document;
        Elements links;
        try {
            document = Jsoup.connect(lurl)//.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    //.referrer("https://www.google.com")
                    .get();
            links = document.select("h3[class=r] a:not(google)");
        } catch (UnknownHostException e){
            mNoConnection = true;
            return null;
        }


        for ( Element link : links) {
            urls.add(link.attr("href"));
        }

        return urls;
    }

    private String fetchLyrics(String iUrl, String element) {
        Document document = null;
        String lyrics="";
        String lurl = "google.com";


        Log.w("LyricsApp", iUrl);
        try {

            if (iUrl.contains(lurl)) {

                int findex = iUrl.indexOf("http", 8);
                int eindex = iUrl.indexOf("&sa=");
                iUrl = iUrl.substring(findex, eindex);
                Log.w("LyricsApp", iUrl);
            }

            document = Jsoup.connect(iUrl)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("https://www.google.com")
                    .get();

            document.outputSettings(new Document.OutputSettings().prettyPrint(false));

//            document.html(document.html().replaceAll("(\\r\\n|\\n)", "<br />"));

            document.select("br").append("b2nl");
            document.select("p").prepend("\\n\\n");

            Elements elements = document.select(element);
//            elements.html(elements.html().replaceAll("(\r\n|\n)", "\\n"));

            if(elements.isEmpty()){
                lyrics="";
                return lyrics;
            }else {
                lyrics = elements.html().replace("b2nl\n", "");
                lyrics = lyrics.replace("b2nl", "\n");
                lyrics = lyrics.replaceAll("\\\\n", "\n");
//                lyrics = elements.html().replaceAll("\\n\\n\\n+", "\n\n");
                lyrics = Jsoup.clean(lyrics, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
            }
        } catch (SocketTimeoutException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lyrics = lyrics.replaceAll("&nbsp;","").trim();
        return lyrics;

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }

    @Override
    protected void onPostExecute(Void result) {
        // Set description into TextView

        if(mNoConnection) {
            lyrics = "No Internet connection";
        } else if (lyrics.length() != 0) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            if (settings.getBoolean("auto_save", true) || !intent.getBooleanExtra("searched", false)) {

                artist = intent.getStringExtra("artist");
                track = intent.getStringExtra("track");
                mLyricsEditor.putString(show.SONGHASH,lyrics);
                mLyricsEditor.putString(show.SONGHASH+".ArtistNameCorrected",show.mArtistCorrected);
                mLyricsEditor.commit();
            }
        }

        try {
            myShow.mAfterFetch(lyrics, artist, track, id);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        myShow.mLyrics.setVisibility(View.VISIBLE);
        myShow.progress.setVisibility(View.GONE);
        myShow.fetch.setVisibility(View.GONE);

        show.mAsyncTaskRunning = false;
    }

}
