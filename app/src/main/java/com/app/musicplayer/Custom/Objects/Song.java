package com.app.musicplayer.Custom.Objects;

import android.graphics.Bitmap;

/**
 * Created by Yuwei on 2014-12-11.
 */
public class Song {
    public String title, artist;
    public Bitmap thumbnail;
    public Song (String title, String artist,Bitmap thumbnail){
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
    }
}
