package com.app.musicplayer.Util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.app.musicplayer.R;
import com.app.musicplayer.UI.MusicMediaController;
import com.app.musicplayer.UI.MyActivity;

/**
 * Created by Edward Onochie on 29/12/14.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;
    private WifiManager.WifiLock wifiLock;
    private IBinder musicBinder;
    private int lastDur;
    private int lastPos;

    //Binder class
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        if (musicBinder == null) musicBinder = new MusicBinder();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        doWifiLock();

    }
    public MediaPlayer getMediaPlayer() {return mediaPlayer;}
    // This function sets up the WIFI lock. (So the music streams in low power states)
    public void doWifiLock() {
       final String WIFI_LOCK_NAME = "songlock";
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_NAME);

        wifiLock.acquire();
    }
    public void updateNotification(String title) {
        final int NOTIFICATION_ID = 1;

        Intent intent = new Intent(getApplicationContext(), MyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.play)
                .setTicker(title)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(title);

        Notification notification = builder.build();
        //notification.tickerText = text;
        /*notification.icon = R.drawable.ic_launcher;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "Sonata",
                "Playing: " + title, pendingIntent);*/
        startForeground(NOTIFICATION_ID, notification);
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        updateNotification(MusicMediaController.activeTitle);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }
    @Override
    public boolean onUnbind(Intent intent){
        //mediaPlayer.stop();
        //mediaPlayer.release();
        //wifiLock.release();
        //stopForeground(false);
        return false;
    }
    public void restartMediaPlayer() {
        mediaPlayer.stop();
    }

    //playback methods
    public int getPosn() {
        return mediaPlayer.getCurrentPosition();
    }
    public int getDur() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying()  {
        return mediaPlayer.isPlaying();
    }

    public void pause(){
        lastDur = mediaPlayer.getDuration();
        lastPos = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }
    public int getLastDur() {return lastDur;}
    public int getLastPos() {return lastPos;}
    public void seekTo(int posn){
        mediaPlayer.seekTo(posn);
    }

    public void go(){
        mediaPlayer.start();
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
