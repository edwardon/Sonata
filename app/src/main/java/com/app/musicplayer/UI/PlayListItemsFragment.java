package com.app.musicplayer.UI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.musicplayer.Custom.MusicArrayAdapter;
import com.app.musicplayer.Custom.VideoListAdapter;
import com.app.musicplayer.Util.MediaFragment;
import com.app.musicplayer.R;
import com.app.musicplayer.Custom.Objects.Song;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Yuwei on 2014-11-15.
 */
public class PlayListItemsFragment extends Fragment{

    private Context context;
    public PlayListItemsFragment(){
        context = getActivity();
    }
    final ArrayList<Song> songNames = new ArrayList<Song>();
    MusicArrayAdapter musicArrayAdapter;
    GridView gridView;
    String playlistName;
    private class DataLoader extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int num = params[0];
            try {
            //InputStream inputStream = mContext.openFileInput("playlist.txt");
                Log.v("num is", "" + num);
                FileReader reader = new FileReader ("/data/data/com.app.musicplayer/files/playlist"+num+".txt");
                Scanner scanner = new Scanner (reader);


                String line = scanner.nextLine();
                playlistName=line;
                String songTitle ="",songId = "",artist ="", thumbnail = "";
                while (scanner.hasNextLine()){
                    songTitle = scanner.nextLine();
                    artist = scanner.nextLine();
                    thumbnail = scanner.nextLine();
                    Bitmap image = null;
                    // Actually load the image.
                    try {
                        URL imageUrl = new URL(thumbnail);
                        image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    songId = scanner.nextLine();
                    Song s = new Song (songTitle,artist,image,songId,num,line);
                    songNames.add(s);

                }

            }
            catch (IOException e){

            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    musicArrayAdapter.notifyDataSetChanged();
                    musicArrayAdapter= new MusicArrayAdapter(getActivity(),R.layout.song_item_view,songNames);
                    musicArrayAdapter.notifyDataSetChanged();
                    gridView.setAdapter(musicArrayAdapter);
                }
            });

            return null;
        }

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        String name = getArguments().getString("name");

        View rootView = inflater.inflate(R.layout.song_layout, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.playlist_name_textview);
        textView.setText(name);

        int num = getArguments().getInt("playlists",0);
        new DataLoader().execute(num);

        musicArrayAdapter = new MusicArrayAdapter(getActivity(),R.layout.song_item_view, songNames);
        gridView = (GridView)rootView.findViewById(R.id.song_gridview);
        gridView.setAdapter(musicArrayAdapter);
        return rootView;
    }
}
