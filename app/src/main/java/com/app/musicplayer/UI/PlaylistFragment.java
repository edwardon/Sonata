package com.app.musicplayer.UI;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.musicplayer.Custom.PlaylistArrayAdapter;
import com.app.musicplayer.R;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        final HashMap<String, Integer> playlistMap = new HashMap<String, Integer>();
        int counter =0;
        try {
            FileReader reader = new FileReader ("/data/data/com.app.musicplayer/files/playlists.txt");
            Scanner scanner = new Scanner (reader);
            String line;
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                playlistNames.add(line);
                playlistMap.put(line,counter);
                counter++;
            }

        }
        catch (IOException e){

        }





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

                int num = playlistMap.get(name);
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
