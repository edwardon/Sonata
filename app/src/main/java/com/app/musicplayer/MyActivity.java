package com.app.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        /*MediaPlayer player = new MediaPlayer();
       // new YouTubePlayTask("CHCwXc4DBaA", player).execute();
        try {
            player.setDataSource("rtsp://r6---sn-jc47eu7l.c.youtube.com/CiILENy73wIaGQkreq3yN6pNFRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp");
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.start();*/

    }
}
