package com.app.musicplayer.UI;
import android.os.Bundle;
import android.widget.Toast;

import com.app.musicplayer.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
/**
 * Created by edward on 16/02/15.
 */
public class YouTubeTestActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyDD_lm7D1xXaAGE_2ZH0USKJJ7sC7kW9BQ";

    //http://youtu.be/<VIDEO_ID>
    public static final String VIDEO_ID = "awimSQD2Dyo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** attaching layout xml **/
        setContentView(R.layout.youtube_test);

        /** Initializing YouTube player view **/
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);

    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        /** Start buffering **/
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onPlaying() {

        }

        @Override
        public void onSeekTo(int arg0) {

        }

        @Override
        public void onStopped() {

        }

    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onError(ErrorReason arg0) {

        }

        @Override
        public void onLoaded(String arg0) {

        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onVideoStarted() {

        }
    };
}
