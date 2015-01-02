package com.app.musicplayer.Custom.Objects;

import android.graphics.Bitmap;

/**
 * Created by Yuwei on 2014-12-11.
 */
public class Song {
    public String title, artist, videoId,playlistName;
    public Bitmap thumbnail;
    public int playlist;

    public Song (String title, String artist,Bitmap thumbnail,String id,int playlist,String playlistName){
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
        this.videoId = id;
        this.playlist=playlist;
    }
}
