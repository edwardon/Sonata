package com.app.musicplayer.Custom;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.musicplayer.R;
import com.app.musicplayer.Custom.Objects.Song;
import com.app.musicplayer.UI.MusicMediaController;
import com.app.musicplayer.UI.MyActivity;
import com.app.musicplayer.Util.MediaFragment;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Yuwei on 2014-11-15.
 */
public class MusicArrayAdapter extends ArrayAdapter<Song> {
    private MyActivity myActivity;
    private MusicArrayAdapter adapter;
    public MusicArrayAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource,objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Song song = getItem(position);
        final String name = song.title;
        final String artist = song.artist;
        Bitmap thumbnail = song.thumbnail;
        adapter = this;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_item_view,parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.song_thumbnail);
        imageView.setImageBitmap(thumbnail);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadSong(song);
            }
        });
        //imageView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
        // Lookup view for data population
        TextView songTextView = (TextView) convertView.findViewById(R.id.song_title_textview);
        // Populate the data into the template view using the data object
        songTextView.setText(name);
        TextView artistTextView = (TextView) convertView.findViewById(R.id.song_artist_textview);
        // Populate the data into the template view using the data object
        artistTextView.setText(artist);
        // Return the completed view to render on screen
        myActivity = (MyActivity)getContext();
       final ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.song_more_button);

       imageButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
              myActivity.showSongPopup(v,song.playlist,song.playlistName, name,artist,adapter);
           }
       });
        return convertView;
    }
    public void loadSong(Song s){
        FragmentManager fragmentManager = myActivity.getFragmentManager();
        MediaFragment fragment = new MediaFragment();
        Bundle bundle = new Bundle();
        ((MusicMediaController) myActivity.getController()).setSongTitle(s.title);
        myActivity.getService().updateNotification(s.title);
        bundle.putString("video_id",s.videoId);
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction().add(fragment,fragment.getTag()).commit();
    }
}
