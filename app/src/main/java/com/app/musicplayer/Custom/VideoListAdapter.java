package com.app.musicplayer.Custom;

import android.app.FragmentManager;
import android.content.Context;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.musicplayer.UI.MusicMediaController;
import com.app.musicplayer.Util.MediaFragment;
import com.app.musicplayer.R;
import com.app.musicplayer.UI.MyActivity;
import com.app.musicplayer.Custom.Objects.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Onochie on 15/11/14.
 */
public class VideoListAdapter extends ArrayAdapter<Video> {
    private ArrayList<Video> videoList;
    private Context context;
    private Typeface openFont;
    private MyActivity myActivity;
    public VideoListAdapter(Context context, int resource, List<Video> objects) {
        super(context, resource, objects);
        videoList = (ArrayList) objects;
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Video item = getItem(position);
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.search_view, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.search_title_textview);
        textView.setText(item.videoTitle);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        imageView.setImageBitmap(item.thumbNail);
        //imageView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
//        openFont = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.otf");
//
//        textView.setTypeface(openFont);
//        textView.setGravity(Gravity.CENTER);
//        int dps = 40;
//        final float scale = getContext().getResources().getDisplayMetrics().density;
//        int pixels = (int) (dps * scale + 0.5f);
//        textView.setHeight(pixels);
//        textView.setPadding(0,0,0,0);
        convertView.setTag(position);
        RelativeLayout cardRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.search_linearlayout);
        cardRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSong(item);
            }
        });

        ImageView cardImageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        myActivity = (MyActivity) getContext();
        cardImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                loadSong(item);
            }
        });
        cardImageView.setOnLongClickListener(new View.OnLongClickListener() {

            //MyActivity myActivity = (MyActivity) getContext();
            @Override
            public boolean onLongClick(View v) {
                myActivity.showAddPopup(v,item.videoTitle, item.videoID,item.thumbnailLink);
                return true;
            }
        });
        cardRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {

            //MyActivity myActivity = (MyActivity) getContext();
            @Override
            public boolean onLongClick(View v) {
                myActivity.showAddPopup(v,item.videoTitle, item.videoID,item.thumbnailLink);
                return true;
            }
        });
        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.search_more_button);
        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                myActivity.showAddPopup(v,item.videoTitle, item.videoID,item.thumbnailLink);
            }
        });

        return convertView;
    }

    private void loadSong(Video item) {
        Log.i("Video", "Loading...");
        FragmentManager fragmentManager = ((MyActivity) context).getFragmentManager();
        Bundle bundle = new Bundle();
        MediaFragment fragment = new MediaFragment();
        ((MusicMediaController) ((MyActivity) context).getController()).setSongTitle(item.videoTitle);
        ((MyActivity) context).getService().updateNotification(item.videoTitle);
        bundle.putString("video_id",item.videoID);
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction().add(fragment,fragment.getTag()).commit();
    }
}
