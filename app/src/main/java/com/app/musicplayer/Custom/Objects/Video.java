package com.app.musicplayer.Custom.Objects;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.api.services.youtube.model.ThumbnailDetails;

/**
 * Created by Edward Onochie on 15/11/14.
 */
public class Video {
    public String videoID;
    public String videoTitle;
    public Bitmap thumbNail;

    public Video(String videoID, String videoTitle,Bitmap thumbNail) {
        this.videoID = videoID;
        this.videoTitle = videoTitle;
        this.thumbNail = thumbNail;
    }
}
