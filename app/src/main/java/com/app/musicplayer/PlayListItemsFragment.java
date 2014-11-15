package com.app.musicplayer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.app.musicplayer.Custom.PlaylistArrayAdapter;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Yuwei on 2014-11-15.
 */
public class PlayListItemsFragment extends Fragment {

    private Context mContext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        String name = getArguments().getString("name");

        View rootView = inflater.inflate(R.layout.fragment_playlist_items, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.playlist_name_textview);
        textView.setText(name);

        ArrayList<String> songNames = new ArrayList<String>();
        ArrayList<String> songIDs = new ArrayList<String>();

        int num = getArguments().getInt("playlists",0);
        if (num!=0){
            try {
                //InputStream inputStream = mContext.openFileInput("playlist.txt");
                for (int i=0; i<num; i++){
                    Log.v("num is", "" + num);
                    FileReader reader = new FileReader ("/data/data/com.app.musicplayer/files/playlist"+i+".txt");
                    Scanner scanner = new Scanner (reader);


                    String line = scanner.nextLine();
                    while (scanner.hasNextLine()){
                        songNames.add(scanner.nextLine());
                        songIDs.add(scanner.nextLine());
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

        PlaylistArrayAdapter mArrayAdapter = new PlaylistArrayAdapter(getActivity(), R.id.playlist_name_listview, songNames);
        ListView listView = (ListView) rootView.findViewById(R.id.playlist_name_listview);
        listView.setAdapter(mArrayAdapter);
        return rootView;
    }
}
