package com.app.musicplayer;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.musicplayer.Custom.PlaylistArrayAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

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

        int num = getArguments().getInt("playlists",0);
        if (num!=0){
            try {
                //InputStream inputStream = mContext.openFileInput("playlist.txt");
                for (int i=0; i<num; i++){
                    Log.v("num is",""+num);
                    FileReader reader = new FileReader ("/data/data/com.app.musicplayer/files/playlist"+i+".txt");
                    Scanner scanner = new Scanner (reader);


                    String line = scanner.nextLine();
                    playlistNames.add(line);
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String receiveString = "";
//                    StringBuilder stringBuilder = new StringBuilder();
//
//                    while ( (receiveString = bufferedReader.readLine()) != null ) {
//                        stringBuilder.append(receiveString);
//                    }
//
//                    inputStream.close();
//                    String ret = stringBuilder.toString();


                }

            }
            catch (IOException e){

            }

        }

        //sample bullshit



        PlaylistArrayAdapter mArrayAdapter = new PlaylistArrayAdapter(getActivity(), R.id.playlist_listview, playlistNames);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            FragmentManager fragmentManager;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentManager = getFragmentManager();
                PlayListItemsFragment fragment = new PlayListItemsFragment();
                Bundle args = new Bundle();

                String name = ((TextView) view.findViewById(R.id.playlist_item_textview)).getText().toString();
                args.putString("name", name);

                int num = getArguments().getInt("playlists",0);
                args.putInt("playlists",num);
                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.main_linearlayout, fragment).commit();
            }
        });

        listView.setAdapter(mArrayAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
