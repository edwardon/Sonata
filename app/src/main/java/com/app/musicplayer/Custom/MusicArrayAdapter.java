package com.app.musicplayer.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.musicplayer.R;

import java.util.List;

/**
 * Created by Yuwei on 2014-11-15.
 */
public class MusicArrayAdapter extends ArrayAdapter<String> {

    public MusicArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.music_list_item,parent, false);
        }
        // Lookup view for data population
        TextView playlistTextView = (TextView) convertView.findViewById(R.id.music_list_item);
        // Populate the data into the template view using the data object
        playlistTextView.setText(name);
        // Return the completed view to render on screen
        return convertView;
    }
}
