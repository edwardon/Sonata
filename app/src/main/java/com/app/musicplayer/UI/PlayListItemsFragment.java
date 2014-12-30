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
public class PlayListItemsFragment extends Fragment {

    private Context context;
    public PlayListItemsFragment(){
        context = getActivity();
    }
    HashMap<String,String> songsHashMap= new HashMap<String,String>();
    final ArrayList<Song> songNames = new ArrayList<Song>();
    MusicArrayAdapter mArrayAdapter;
    ListView listView;
    private class DataLoader extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int num = params[0];
            if (num!=0){
                try {
                    //InputStream inputStream = mContext.openFileInput("playlist.txt");
                    for (int i=0; i<num; i++){
                        Log.v("num is", "" + num);
                        FileReader reader = new FileReader ("/data/data/com.app.musicplayer/files/playlist"+i+".txt");
                        Scanner scanner = new Scanner (reader);


                        String line = scanner.nextLine();
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
                            Song s = new Song (songTitle,artist,image);
                            songNames.add(s);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArrayAdapter.notifyDataSetChanged();
                        mArrayAdapter = new MusicArrayAdapter(getActivity(),R.id.playlist_name_listview,songNames);
                        mArrayAdapter.notifyDataSetChanged();
                        listView.setAdapter(mArrayAdapter);
                    }
                });

            }
            return null;
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        String name = getArguments().getString("name");

        View rootView = inflater.inflate(R.layout.fragment_playlist_items, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.playlist_name_textview);
        textView.setText(name);




        int num = getArguments().getInt("playlists",0);
        new DataLoader().execute(num);

        mArrayAdapter = new MusicArrayAdapter(getActivity(), R.id.playlist_name_listview, songNames);
        listView= (ListView) rootView.findViewById(R.id.playlist_name_listview);
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
                ((MusicMediaController) ((MyActivity) context).getController()).setSongTitle(name);
                ((MyActivity) context).getService().updateNotification(name);
                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.main_linearlayout, fragment).commit();
            }
        });
        return rootView;
    }
}
