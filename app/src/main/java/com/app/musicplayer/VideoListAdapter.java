package com.app.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Onochie on 15/11/14.
 */
public class VideoListAdapter extends ArrayAdapter<Video> {
    private ArrayList<Video> videoList;
    private Context context;

    public VideoListAdapter(Context context, int resource, List<Video> objects) {
        super(context, resource, objects);
        videoList = (ArrayList) objects;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Video item = getItem(position);
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.package_row, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.example_row_tv_title);
        textView.setText(item.videoTitle);
        return convertView;
    }
}
