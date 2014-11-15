package com.app.musicplayer;

import android.app.Fragment;

/**
 * Created by edward on 14/11/14.
 */
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.keyes.youtube.Format;
import com.keyes.youtube.VideoStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yuwei on 2014-11-13.
 */
public class MediaFragment extends Fragment {
    Context mContext;
    static String actualString = "";
    //private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    public MediaFragment(){
        mContext = getActivity();
    }

    private class YoutubeScrape extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String actualUrlString= "";
            String lVideoIdStr = Uri.parse("ytv://TFdDZOoQrUE").getEncodedSchemeSpecificPart();
            Log.v("STR =", lVideoIdStr);
            if(lVideoIdStr.startsWith("//")){
                if(lVideoIdStr.length() > 2){
                    lVideoIdStr = lVideoIdStr.substring(2);
                }
            }
            try {
                actualUrlString = calculateYouTubeUrl("18", true, lVideoIdStr);
            }
            catch (IOException e){}
            Log.v("URL",actualUrlString);

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            System.out.println("WHY IS THIS: " + actualUrlString);

            try{
                mediaPlayer.setDataSource(mContext, Uri.parse(actualUrlString));
                mediaPlayer.prepare();
            }
            catch (IOException e){};
            mediaPlayer.start();
            return actualUrlString;
        }
        protected void onPostExecute(String result) {

            //mProgress.setEnabled(false);

            //actualString = "http%3A%2F%2Fr4---sn-5aanugx5h-tuvl.googlevideo.com%2Fvideoplayback%3Fupn%3D75KJYmnRjdQ%26mm%3D31%26ipbits%3D0%26mt%3D1415997589%26mv%3Dm%26ms%3Dau%26fexp%3D904732%252C907259%252C914020%252C916640%252C922243%252C927622%252C932404%252C936111%252C943909%252C945084%252C947209%252C947215%252C948124%252C952302%252C952605%252C952901%252C953603%252C953912%252C955100%252C957103%252C957105%252C957201%26cwbhb%3Dyes%26sver%3D3%26expire%3D1416019302%26key%3Dyt5%26ip%3D129.97.125.93%26initcwndbps%3D6975000%26source%3Dyoutube%26ratebypass%3Dyes%26sparams%3Dcwbhb%252Cid%252Cinitcwndbps%252Cip%252Cipbits%252Citag%252Cmm%252Cms%252Cmv%252Cratebypass%252Csource%252Cupn%252Cexpire%26itag%3D18%26id%3Do-ACqtHTb3iAXmMa2p48xf5yMOU4RZJxTx6k47Yp087FAX%26signature%3DB79D5715B736731E0CAE587BF49D37EBE8DE0FB4.E8CA59F99F2CCBE845684B9873890D868DF03446";


        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        String url="";
        new YoutubeScrape().execute();
        return rootView;

        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static int getSupportedFallbackId(int pOldId){
        final int lSupportedFormatIds[] = {13,  //3GPP (MPEG-4 encoded) Low quality
                17,  //3GPP (MPEG-4 encoded) Medium quality
                18,  //MP4  (H.264 encoded) Normal quality
                22,  //MP4  (H.264 encoded) High quality
                37   //MP4  (H.264 encoded) High quality
        };
        int lFallbackId = pOldId;
        for(int i = lSupportedFormatIds.length - 1; i >= 0; i--){
            if(pOldId == lSupportedFormatIds[i] && i > 0){
                lFallbackId = lSupportedFormatIds[i-1];
            }
        }
        return lFallbackId;
    }

    public String calculateYouTubeUrl(String pYouTubeFmtQuality, boolean pFallback,
                                      String pYouTubeVideoId) throws IOException,
            ClientProtocolException, UnsupportedEncodingException {

        String lUriStr = null;
        HttpClient lClient = new DefaultHttpClient();

        HttpGet lGetMethod = new HttpGet("http://www.youtube.com/get_video_info?&video_id=" +
                pYouTubeVideoId);

        HttpResponse lResp = null;

        lResp = lClient.execute(lGetMethod);

        ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
        String lInfoStr = null;

        lResp.getEntity().writeTo(lBOS);
        lInfoStr = new String(lBOS.toString("UTF-8"));

        String[] lArgs=lInfoStr.split("&");
        Map<String,String> lArgMap = new HashMap<String, String>();
        for(int i=0; i<lArgs.length; i++){
            String[] lArgValStrArr = lArgs[i].split("=");
            if(lArgValStrArr != null){
                if(lArgValStrArr.length >= 2){
                    lArgMap.put(lArgValStrArr[0], URLDecoder.decode(lArgValStrArr[1]));
                }
            }
        }

        //Find out the URI string from the parameters

        //Populate the list of formats for the video
        String lFmtList = URLDecoder.decode(lArgMap.get("fmt_list"));
        ArrayList<Format> lFormats = new ArrayList<Format>();
        if(null != lFmtList){
            String lFormatStrs[] = lFmtList.split(",");

            for(String lFormatStr : lFormatStrs){
                Format lFormat = new Format(lFormatStr);
                lFormats.add(lFormat);
            }
        }

        //Populate the list of streams for the video
        String lStreamList = lArgMap.get("url_encoded_fmt_stream_map");
        if(null != lStreamList){
            String lStreamStrs[] = lStreamList.split(",");
            ArrayList<VideoStream> lStreams = new ArrayList<VideoStream>();
            for(String lStreamStr : lStreamStrs){
                VideoStream lStream = new VideoStream(lStreamStr);
                lStreams.add(lStream);
            }

            //Search for the given format in the list of video formats
            // if it is there, select the corresponding stream
            // otherwise if fallback is requested, check for next lower format
            int lFormatId = Integer.parseInt(pYouTubeFmtQuality);

            Format lSearchFormat = new Format(lFormatId);
            while(!lFormats.contains(lSearchFormat) && pFallback ){
                int lOldId = lSearchFormat.getId();
                int lNewId = getSupportedFallbackId(lOldId);

                if(lOldId == lNewId){
                    break;
                }
                lSearchFormat = new Format(lNewId);
            }

            int lIndex = lFormats.indexOf(lSearchFormat);
            if(lIndex >= 0){
                VideoStream lSearchStream = lStreams.get(lIndex);
                lUriStr = lSearchStream.getUrl();
            }

        }
        //Return the URI string. It may be null if the format (or a fallback format if enabled)
        // is not found in the list of formats for the video
        return lUriStr;
    }
}
