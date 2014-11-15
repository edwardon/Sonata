package com.app.musicplayer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.musicplayer.Custom.MusicArrayAdapter;
import com.app.musicplayer.Custom.PlaylistArrayAdapter;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Yuwei on 2014-11-15.
 */
public class PlayListItemsFragment extends Fragment {

    private Context mContext;
    HashMap<String,String> songsHashMap;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        String name = getArguments().getString("name");

        View rootView = inflater.inflate(R.layout.fragment_playlist_items, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.playlist_name_textview);
        textView.setText(name);

        final ArrayList<String> songNames = new ArrayList<String>();
        songsHashMap = new HashMap<String,String>();

        int num = getArguments().getInt("playlists",0);
        if (num!=0){
            try {
                //InputStream inputStream = mContext.openFileInput("playlist.txt");
                for (int i=0; i<num; i++){
                    Log.v("num is", "" + num);
                    FileReader reader = new FileReader ("/data/data/com.app.musicplayer/files/playlist"+i+".txt");
                    Scanner scanner = new Scanner (reader);


                    String line = scanner.nextLine();
                    String songTitle ="",songId = "";
                    while (scanner.hasNextLine()){
                        songTitle = scanner.nextLine();
                        songId = scanner.nextLine();
                        songNames.add(songTitle);
                        songsHashMap.put(songTitle, songId);
                    }

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

        MusicArrayAdapter mArrayAdapter = new MusicArrayAdapter(getActivity(), R.id.playlist_name_listview, songNames);
        ListView listView = (ListView) rootView.findViewById(R.id.playlist_name_listview);
        listView.setAdapter(mArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            FragmentManager fragmentManager;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentManager = getFragmentManager();
                MediaFragment fragment = new MediaFragment();
                String name = ((TextView) view.findViewById(R.id.music_list_item)).getText().toString();
                String songId = songsHashMap.get(name);
                Bundle args = new Bundle();
                args.putString("video_id", songId);


                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.main_linearlayout, fragment).commit();
            }
        });
        return rootView;
    }
}
