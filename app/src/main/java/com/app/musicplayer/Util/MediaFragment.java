package com.app.musicplayer.Util;

import android.app.Fragment;

/**
 * Created by edward on 14/11/14.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.app.musicplayer.Custom.Objects.VideoStream;
import com.app.musicplayer.UI.MusicMediaController;
import com.app.musicplayer.UI.MyActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.axet.vget.vhs.YouTubeMPGParser;
import com.github.axet.vget.vhs.YouTubeParser;
import com.github.axet.vget.vhs.YouTubeParser.VideoDownload;
import com.github.axet.vget.vhs.YoutubeInfo.StreamVideo;
import com.github.axet.vget.vhs.YoutubeInfo;

/**
 * Created by Yuwei on 2014-11-13.
 */
public class MediaFragment extends Fragment {
    Context mContext;
    private String videoId;
    private int pos = 0;
    private Bundle bundle;
    public MediaFragment(){
        mContext = getActivity();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        if (getArguments() == null) {
            videoId = "";
        }
        else {
            videoId = getArguments().getString("video_id");
        }
        new YouTubePageStreamUriGetter().execute("https://www.youtube.com/watch?v=" + videoId);
    }


    private class YouTubePageStreamUriGetter extends
            AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "",
                    "Connecting to YouTube...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
                try {
                    YoutubeInfo info = new YoutubeInfo(new URL(url));
                    YouTubeMPGParser parser = new YouTubeMPGParser();

                    List<VideoDownload> list = parser.extractLinks(info);
                    for (VideoDownload d : list) {
                        System.out.println(d.stream);
                    }
                    if (pos >= list.size()) return null;
                    return list.get(pos).url.toString();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();

                }

                return null;
        }

        @Override
        protected void onPostExecute(String streamingUrl) {
            super.onPostExecute(streamingUrl);
            progressDialog.dismiss();
            if (streamingUrl != null) {
                ((MyActivity) getActivity()).getService().restartMediaPlayer();
                //((MyActivity) getActivity()).getService().onCreate();
                MediaPlayer player = ((MyActivity) getActivity()).getService().getMediaPlayer();
                player.reset();
                //player.stop();

                //MyActivity.mediaPlayer = new MediaPlayer();

                //MyActivity.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                System.out.println("WHY IS THIS: " + streamingUrl);

                try{
                    player.setDataSource(getActivity(), Uri.parse(streamingUrl));
                    player.prepare();
                }
                catch (IOException e){};
                player.start();

                ((MyActivity) getActivity()).getController().show(0);
                if (player.getDuration() >= 1440000000) {
                    pos++;
                    // Not every URL works, keep trying until we get one that does.
                    // The recursion is messy, but I can't think of a better way.
                    //TODO: clean it up.
                    onCreate(bundle);
                }

            }
        }
    }
}










