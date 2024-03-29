package com.app.musicplayer.UI;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.app.musicplayer.R;

/**
 * Created by Edward Onochie on 17/12/14.
 */

public class MusicMediaController extends MediaController {
    String title;
    public static String activeTitle = "";
    public MusicMediaController(Context context) {
        super(context);
        title = "";
        show(0);
    }
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        View customView = View.inflate(getContext(),R.layout.song_title, null);
        TextView tvSongTitle = (TextView) customView.findViewById(R.id.songTitleView);
        // Default text
        tvSongTitle.setText("");
        addView(customView);
    }
    @Override
    public void hide() {

    }
    public void actualHide() {
        super.hide();
    }
    public void setSongTitle(String title) {
        TextView tvSongTitle = (TextView) findViewById(R.id.songTitleView);
        tvSongTitle.setText(title);
        this.title = title;
        activeTitle = title;
    }
    public String getTitle(){
        return title;
    }
}