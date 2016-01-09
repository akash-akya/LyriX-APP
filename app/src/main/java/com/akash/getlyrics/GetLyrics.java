package com.akash.getlyrics;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 1/19/2015.
 */
public class GetLyrics  extends AsyncTask<Void, Void, Void> {
    String lyrics;
    String search_item;
    String artist;
    String track;
    String album;
    Intent intent;
    Context context;
    show myShow;
    String id;
    final String mUserAgent = "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.122 Mobile Safari/537.36";
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

        intent.putExtra("cachedArtist",artist);
        intent.putExtra("cachedTrack",track);


        myShow.mLyrics.setVisibility(View.GONE);
        myShow.fail.setVisibility(View.GONE);
        myShow.progress.setVisibility(View.VISIBLE);

        show.mAsyncTaskRunning = true;
    }

    public JSONArray getFile(String fileName){
        JSONArray jsonArray=null;
        File yourFile = new File(Environment.getExternalStorageDirectory(), fileName);//timetable.ctt");
        FileInputStream stream = null;
        String jsonStr = null;

        if(yourFile.exists()) {
            try {
                stream = new FileInputStream(yourFile);
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
                JSONObject obj = new JSONObject(jsonStr);
                jsonArray = obj.getJSONArray("sites");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                Log.w("LyriX","No file!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String mURLprefix = " -youtube.com ";

        lyrics = mLyricsDb.getString(show.SONGHASH,"");
        show.mArtistCorrected = mLyricsDb.getString(show.SONGHASH+".ArtistNameCorrected",artist);

        if (lyrics.length()==0) {
            try {
                artist = correctTag(artist);
                track = correctTag(track);
                album = correctTag(album);
                show.mArtistCorrected = artist;

                JSONArray sitesList = getFile("sites.json");
                String item = mURLprefix +track  + " " + artist + " lyrics";

                for(int i=0; i<2 && lyrics.length()== 0; i++) {

                    if(i==1 ){
                        if (album.trim().length() == 0)
                            break;
                        item = mURLprefix +track  + " " + artist + " " + album + " lyrics";
                    }

                    Log.w("LyricsApp", item );

                    try {
                        search_item = URLEncoder.encode(item, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    List<String> nurls = searchv2(search_item);

                    if (nurls == null) {
                        return null;
                    }

                    for (String iUrl : nurls) {
                        for (int j = 0; j < sitesList.length(); j++ ){
                            try {
                                JSONObject site = sitesList.getJSONObject(j);
                                String name = site.getString("name");
                                String url  =  site.getString("url");
                                JSONArray xpaths = site.getJSONArray("xpath");

                                for (int k=0; k<xpaths.length() && lyrics.length() == 0; k++)
                                {
                                    if (iUrl.contains(url)) {
                                        lyrics = fetchLyrics(iUrl, xpaths.getString(k));
                                        if(site.getBoolean("pre")){
                                            lyrics = lyrics.replaceAll("(\\r|\\n|\\r\\n)","<br />");
                                        }
                                        Log.w("LyriX", name);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (lyrics.length() != 0)
                            break;
                    }

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
        tag = tag.replaceAll("-", " ");
        tag = tag.replaceAll("_", " ");

        tag=tag.toLowerCase();
        for(String str : remove){
            tag = tag.replaceAll(str,"");
        }

        tag = tag.replaceAll("\\.", " ");

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

    private List<String> searchv2(String sitem) throws IOException{
        String lurl = "https://www.google.com/search?q=" + sitem; //+ mSufix;

        Log.w("LyricsApp", lurl);
        List<String> urls = new ArrayList<String>();
        Document document;
        Elements links;

        try {
            document = Jsoup.connect(lurl).userAgent(mUserAgent).get();
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
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36")
                    .referrer("https://www.google.com")
                    .get();

            document.select("script, style, img, .hidden").remove();

            Elements elements = document.select(element);

            if(!elements.isEmpty()){
                document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
                document.select("a").unwrap();
                document.select("a").unwrap();
                lyrics = elements.html().trim(); //.replace("b2nl\n", "");

            }
        } catch (SocketTimeoutException e){
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }

        return lyrics;

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }

    @Override
    protected void onPostExecute(Void result) {

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

        show.mAsyncTaskRunning = false;
    }
}
