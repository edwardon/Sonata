package com.app.musicplayer;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        Button playButton = (Button) convertView.findViewById(R.id.example_row_b_action_1);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((MyActivity) context).getFragmentManager();
                Bundle bundle = new Bundle();
                MediaFragment fragment = new MediaFragment();
                bundle.putString("video_id",item.videoID);
                fragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.main_linearlayout,fragment).commit();
            }
        });
        return convertView;
    }
}
