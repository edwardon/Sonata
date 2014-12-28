package com.app.musicplayer.Custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.musicplayer.R;
import com.app.musicplayer.Custom.Objects.Song;

import java.io.IOException;
import java.net.URL;
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
        Bitmap thumbnail = song.thumbnail;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_item_view,parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.song_thumbnail);
        imageView.setImageBitmap(thumbnail);
        imageView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
        // Lookup view for data population
        TextView songTextView = (TextView) convertView.findViewById(R.id.song_title_textview);
        // Populate the data into the template view using the data object
        songTextView.setText(name);
        TextView artistTextView = (TextView) convertView.findViewById(R.id.artist_title_textview);
        // Populate the data into the template view using the data object
        artistTextView.setText(artist);
        // Return the completed view to render on screen
        return convertView;
    }
}
