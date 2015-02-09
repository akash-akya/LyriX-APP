package com.akash.getlyrics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by akash on 1/31/2015.
 */
public class MusicListAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    List<MusicModel> musicModelList;
    ArrayList<MusicModel> musicFilterList;



    MusicListAdapter(Context mContext, List<MusicModel> musicModelList){

        this.mContext = mContext;
        this.musicModelList = musicModelList;
        this.musicFilterList = new ArrayList<MusicModel>();
        this.musicFilterList.addAll(musicModelList);


    }

    @Override
    public int getCount() {
        return musicModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        TextView mDisplayName;
        TextView mArtist;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.music_list_item, null);
            // Locate the TextViews in listview_item.xml
            holder.mDisplayName = (TextView) view.findViewById(R.id.display_name);
            holder.mArtist = (TextView) view.findViewById(R.id.artist_name);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
            if(settings.contains("example_list")) {
                if (settings.getString("example_list", "0").equals("0")) {
                    ((TextView) view.findViewById(R.id.display_name)).setTextColor(Color.parseColor("#ffffffff"));
                } else {
                    ((TextView) view.findViewById(R.id.display_name)).setTextColor(Color.parseColor("#000000"));
                }
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.mDisplayName.setText(musicModelList.get(position).getDisplayName());
        holder.mArtist.setText(musicModelList.get(position).getArtistName());
//        holder.population.setText(worldpopulationlist.get(position).getPopulation());

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, show.class);

                intent.putExtra("artist",musicModelList.get(position).getArtistName());
                intent.putExtra("track",musicModelList.get(position).getTrackName());
                intent.putExtra("album", "");
                intent.putExtra("store",false);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("searched",false);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        musicModelList.clear();
        if (charText.length() == 0) {
            musicModelList.addAll(musicFilterList);
        }
        else
        {
            for (MusicModel wp : musicFilterList)
            {
                if (wp.getTrackName().toLowerCase(Locale.getDefault()).contains(charText)
                        || wp.getArtistName().toLowerCase(Locale.getDefault()).contains(charText) )
                {
                    musicModelList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }



}
