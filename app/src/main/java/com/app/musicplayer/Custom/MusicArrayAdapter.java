package com.app.musicplayer.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.musicplayer.R;
import com.app.musicplayer.Song;

import java.util.List;

/**
 * Created by Yuwei on 2014-11-15.
 */
public class MusicArrayAdapter extends ArrayAdapter<Song> {

    public MusicArrayAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource,objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = getItem(position);
        String name = song.title;
        String artist = song.artist;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.music_list_item,parent, false);
        }
        // Lookup view for data population
        TextView songTextView = (TextView) convertView.findViewById(R.id.music_list_item);
        // Populate the data into the template view using the data object
        songTextView.setText(name);
        TextView artistTextView = (TextView) convertView.findViewById(R.id.artist_list_item);
        // Populate the data into the template view using the data object
        artistTextView.setText(artist);
        // Return the completed view to render on screen
        return convertView;
    }
}
