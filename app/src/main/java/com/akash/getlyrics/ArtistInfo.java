package com.akash.getlyrics;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class ArtistInfo extends ActionBarActivity {


    String artist;
    String mHtmlStart,mHtmlEnd;
    TextView mResult;
    ProgressBar mProgressBar;
    SharedPreferences mySharedPref;
    public static String PREF_NAME = "MYPREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mySharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(mySharedPref.contains("theme")) {
            if (mySharedPref.getString("theme", "light").equals("dark")) {
                setTheme(R.style.AppTheme2);
            } else {
                setTheme(R.style.AppTheme);
            }
        }

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_artist_info);
        mResult = (TextView) findViewById(R.id.textView_artist_info);
        mResult.setClickable(true);
        mResult.setMovementMethod(LinkMovementMethod.getInstance());
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);

        setTitle("Artist Information");

//        Log.w("Artist","main");
        artist = show.mArtist;
        mHtmlStart = "<a href='";
        mHtmlEnd = "</a>";
        new GetArtistInfo().execute();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    ///////////////////////////// GetArtistInfo AsyncTask . ////////////////////////////
    public class GetArtistInfo extends AsyncTask<Void, Void, Void> {
        String lyrics = "";
        String search_item;
        List<String> nurls;
        Document document;


        @Override
        protected Void doInBackground(Void... params) {

            try {
                artist = correctTag(artist);
            } catch (NullPointerException e){
                lyrics = "Artist field is empty.";
                e.printStackTrace();
                return null;
            }

            try {
                search_item = URLEncoder.encode(artist, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                lyrics = "Failed Encoded.";
                return  null;
            }

            try {
                nurls = searchv2(search_item);
            } catch (IOException e) {
                e.printStackTrace();
                lyrics = "Failed to search.";
                return  null;
            }

            String azlyric = "azlyrics";

            for (String iUrl : nurls) {
                if (!isCancelled()) {

                    Log.w("LyricsApp", iUrl);
                    if (iUrl.contains(azlyric)) {

                        lyrics = mfetchInfo(iUrl, "div[id=listAlbum]");
                        if (lyrics.length() != 0)
                            break;
                    }
                }
            }
            return null;
        }

        ///////////////////// search v2   /////////////////////////////////

        private List<String> searchv2(String sitem) throws IOException {

            String lurl = "https://www.google.com/search?q=" + sitem + "+azlyrics";
            Log.w("LyricsApp", lurl);

            List<String> urls = new ArrayList<String>();

            Document document = Jsoup.connect(lurl).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("https://www.google.com").get();
            Elements links = document.select("h3[class=r] a");

            for (Element link : links) {
                urls.add(link.attr("abs:href"));
            }

            return urls;
        }


        private String mfetchInfo(String iUrl, String element) {
            String lyrics="";
            String googl_url = "google.com";
            String mQueryURL = "https://www.google.com/search?q=";

            try {

                if (iUrl.contains(googl_url)) {
                    int start_index = iUrl.indexOf("http", 8);
                    int end_index = iUrl.indexOf("&sa=");
                    iUrl = iUrl.substring(start_index, end_index);
                }

                document = Jsoup.connect(iUrl).get();


                Elements description = document
                        .select(element);

                for( Element elemt : description.select("script") )
                    elemt.remove();

                for( Element elemt : description.select("img") )
                    elemt.remove();

                for( Element elemt : description.select("a") )
                    elemt.attr("href", mQueryURL + URLEncoder.encode(elemt.text(), "utf-8")) ;

                lyrics = description.toString();

            } catch (IOException e) {
                e.printStackTrace();
                lyrics = "Failed to fetch";
            }
            return lyrics;
        }

        private String correctTag(String tag){

            String[] remove = {"128","160","192","96","320","mp3","\\.com","kbps","<unknown>","\\sft\\.", "\\sfeat\\.", "\\sfeat\\s", "\\sft\\s", "www[^\\s]+",
                    "[^a-z_0-9\\s]", "^[0-9\\.\\s]+", "remix", "original mix", " mix ", " radio edit "};

            tag = tag.replaceAll("_"," ");
            tag = tag.replaceAll("-"," ");

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

        @Override
        protected void onPostExecute(Void result) {

            setTitle(show.mArtist);
            mProgressBar.setVisibility(View.GONE);
            mResult.setVisibility(View.VISIBLE);
            if(lyrics.length()==0){
                lyrics = "Can not find the information";
            }
            mResult.setText(Html.fromHtml(lyrics));

        }
    }

    ////////////////////////// End //////////////////////////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_artist_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
