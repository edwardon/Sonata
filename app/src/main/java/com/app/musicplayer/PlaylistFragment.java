package com.app.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.musicplayer.Custom.PlaylistArrayAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by Yuwei on 2014-11-14.
 */
public class PlaylistFragment extends Fragment {
    Context mContext;
    public PlaylistFragment(){
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("PlaylistFragment", "is being created");
        View rootView = inflater.inflate(R.layout.fragment_playlist,container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.playlist_listview);

        ArrayList<String> playlistNames = new ArrayList<String>();


        //sample bullshit
        playlistNames.add("Top Hits");
        playlistNames.add("Hip-Hop");
        playlistNames.add("Study Music");

        PlaylistArrayAdapter mArrayAdapter = new PlaylistArrayAdapter(getActivity(), R.id.playlist_listview, playlistNames);

        listView.setAdapter(mArrayAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
