package com.app.musicplayer.UI;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActionBarDrawerToggle;

import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

import android.media.MediaPlayer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import com.app.musicplayer.Custom.NavigationDrawer.NavigationDrawerCallbacks;
import com.app.musicplayer.Custom.NavigationDrawer.NavigationDrawerFragment;
import com.app.musicplayer.Custom.TypeFaceSpan;
import com.app.musicplayer.R;
import com.app.musicplayer.Util.MusicNotificationActivity;
import com.app.musicplayer.Util.SongSuggestionProvider;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class MyActivity extends ActionBarActivity implements MediaController.MediaPlayerControl,NavigationDrawerCallbacks {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    int numPlaylists;
    private ActionBarDrawerToggle mDrawerToggle;
    private final String PLAYLIST = "PLAYLIST";
    private final String PLAYLIST_NAMES = "PLAYLIST_NAMES";
    private final int notifId = 1;
    public static MediaPlayer mediaPlayer = null;

    private MusicMediaController controller;

    SharedPreferences mPrefs;
    static boolean playing = false;
    HashSet<String> playlistNames;

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*tint status bar*/
        setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(R.color.teal));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);



        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (mPrefs.contains(PLAYLIST)) {

            numPlaylists = mPrefs.getInt(PLAYLIST, 0);

        } else {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(PLAYLIST, 0);
            editor.apply();
        }
        if (mPrefs.contains(PLAYLIST_NAMES)) {
            playlistNames = (HashSet<String>) mPrefs.getStringSet(PLAYLIST_NAMES, new HashSet<String>());
        } else {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putStringSet(PLAYLIST_NAMES, new HashSet<String>());
            editor.apply();
        }
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        FragmentManager fragmentManager = getFragmentManager();

        controller = new MusicMediaController(this);
        controller.setAnchorView(findViewById(R.id.main_linearlayout));
        controller.setMediaPlayer(this);
        controller.setEnabled(true);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Save the query.
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SongSuggestionProvider.AUTHORITY, SongSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            Fragment fragment = new VideoListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_linearlayout, fragment)
                    .commit();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    controller.show(0);
                }
            },1000);
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_linearlayout, new VideoListFragment())
                    .commit();
        }


        String dir = "/data/data/com.app.musicplayer/files";
        addToPlaylistTest();


    }


    public void updateNotification(String title) {
        Intent resultIntent = new Intent(this, MyActivity.class);

// This ensures that the back button follows the recommended
// convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MyActivity.class);

// Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

// Create remote view and set bigContentView.
        RemoteViews expandedView = new RemoteViews(this.getPackageName(),
                R.layout.notification);
        expandedView.setTextViewText(R.id.notif_textView, title);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("Now Playing")
                .setContentInfo(title).build();

        notification.bigContentView = expandedView;
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent switchIntent = new Intent("com.example.app.ACTION_PLAY");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, switchIntent,0);
//
//
//        expandedView.setOnClickPendingIntent(R.id.notif_playButton, pendingIntent);
        mNotificationManager.notify(1, notification);
    }

    // Call this to clear the search history
    public void clearHistory() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SongSuggestionProvider.AUTHORITY, SongSuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show(0);
        return super.onTouchEvent(event);
    }

    public MediaController getController() {
        return controller;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }
    @Override
    public void onStop() {
        controller.actualHide();
        super.onStop();
    }
    @Override
    public void onResume() {
        controller.show();
        super.onResume();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        //Do this so that the user can type.
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    controller.actualHide();
                }
                else controller.show(0);
            }
        });

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        theTextArea.setTextColor(Color.WHITE);//or any color that you want
        theTextArea.setHintTextColor(Color.WHITE);

        ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.mag_glass);
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        if (item.getItemId() == R.id.clear_history) {
            clearHistory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addToPlaylistTest() {
        int numPlaylists = 1;
        for (int i = 0; i < 1; i++) {
            String FILENAME = "playlist0";

            try {
                PrintWriter writer = new PrintWriter("/data/data/com.app.musicplayer/files/playlist0.txt", "UTF-8");
                writer.println("EDM Playlist");
                playlistNames.add("EDM Playlist");
                writer.println("Best Electronic Dance Music Mix 2014 [EDM] ");
                writer.println(" ");
                writer.println("https://i.ytimg.com/vi/dlg66JU2-QU/default.jpg");
                writer.println("G-WjN61kfBw");
                writer.println("Frontier");
                writer.println("Thisnameisafail");
                writer.println("https://i.ytimg.com/vi/dlg66JU2-QU/default.jpg");
                writer.println("Ph26aycXpPQ");
                writer.println("Don't Leave (Ft. Ellie Goulding)");
                writer.println("Seven Lions");
                writer.println("https://i.ytimg.com/vi/dlg66JU2-QU/default.jpg");
                writer.println("SKlbCjNCDn4");
                writer.println("Live For The Night");
                writer.println("Krewella");
                writer.println("https://i.ytimg.com/vi/dlg66JU2-QU/default.jpg");
                writer.println("TFdDZOoQrUE");
                writer.println("Prototype");
                writer.println("Thisnameisafail");
                writer.println("https://i.ytimg.com/vi/dlg66JU2-QU/default.jpg");
                writer.println("jM4EZOnNKHc");


                writer.close();

//                FileOutputStream fos = openFileOutput("playlist.txt", Context.MODE_PRIVATE);
//                OutputStreamWriter outputWriter=new OutputStreamWriter(fos);
//                outputWriter.write("Play"+i+"\n");
//
//                for (int j=0; j<2; j++){
//                    String songName= "Song"+(j+1)+"\n";
//                    outputWriter.write(songName);
//                }
//                outputWriter.close();

                Log.v("info has been stored", "true");
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
            //writer.close();
        }
        mPrefs.edit().putInt(PLAYLIST, 1).commit();
    }

    public void addToPlaylist(View view) {

        int numPlaylists = mPrefs.getInt(PLAYLIST, 0);
        numPlaylists++;
        String FILENAME = "Playlist" + numPlaylists;
        mPrefs.edit().putInt(PLAYLIST, numPlaylists).apply();
        try {

            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            //fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

    }

    public void showAddPopup(View v,String title, String id,String thumbnail) {
        Log.v("HashSet is currently at",playlistNames.size()+"");
        boolean flag = false;
        final String names[] = new String[playlistNames.size()];
        int counter = 0;
        for (String s : playlistNames) {
            names[counter] = s;
            counter++;
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(MyActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_view, null);
        alertDialog.setView(convertView);
        //alertDialog.setTitle("Choose Playlist");
        ListView lv = (ListView) convertView.findViewById(R.id.dialog_listview);
        final String videoTitle = title, videoId = id;
        final String videoThumbnail = thumbnail;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            PrintWriter writer;
            FileReader reader;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView) view).getText().toString();
                int index = -1;
                for (int i = 0; i < names.length; i++) {
                    if (text.equals(names[i])) {
                        index = i;
                        break;
                    }
                }

                if (index!= -1) {
                    renameSong(index, videoTitle, videoId, videoThumbnail);
                    alertDialog.dismiss();
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dialog_list_item, names);
        lv.setAdapter(adapter);
        alertDialog.show();
    }

    public void renameSong(int playlistIndex, String curTitle, String videoId, String videoThumbnail){
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.rename_layout, null);
        final EditText titleText = (EditText) convertView.findViewById(R.id.title_textbox);
        final EditText artistText = (EditText) convertView.findViewById(R.id.artist_textbox);
        titleText.setText(curTitle);
        final int index = playlistIndex;
        final String id = videoId,  thumbnail = videoThumbnail;
        builder.setView(convertView)
                .setPositiveButton(R.string.name_confirm, new DialogInterface.OnClickListener() {
                    PrintWriter writer;
                    FileReader reader;

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String filename = "playlist" + index + ".txt";
                        try {
                            reader = new FileReader("/data/data/com.app.musicplayer/files/" + filename);
                            Scanner scanner = new Scanner(reader);
                            String playlistTitle = scanner.nextLine();
                            String songTitle = "", songId = "", artist = "", songThumbnail = "";
                            writer = new PrintWriter("/data/data/com.app.musicplayer/files/" + filename, "UTF-8");
                            writer.println(playlistTitle);
                            while (scanner.hasNextLine()) {
                                songTitle = scanner.nextLine();
                                artist = scanner.nextLine();
                                songThumbnail = scanner.nextLine();
                                songId = scanner.nextLine();
                                writer.println(songTitle);
                                writer.println(artist);
                                writer.println(songThumbnail);
                                writer.println(songId);
                            }

                            writer.println(titleText.getText().toString());
                            writer.println(artistText.getText().toString());
                            writer.println(thumbnail);
                            writer.println(id);
                            writer.close();
                        } catch (IOException e) {
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateNotification(controller.getTitle());
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        if (mediaPlayer != null) return mediaPlayer.getDuration();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public void seekTo(int i) {
        if (mediaPlayer != null) mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null) return mediaPlayer.isPlaying();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            selectItem(position);
            mDrawerList.setItemChecked(position, true);
            //getActionBar().setDisplayHomeAsUpEnabled(true);
            //getActionBar().setHomeButtonEnabled(true);
        }

        public void selectItem(int position) {
            FragmentManager fragmentManager = getFragmentManager();
            try {


                switch (position) {

                    case 0:
                        Log.v("SEARCH WAS CALLED", "yes");

                        mDrawerLayout.closeDrawer(mDrawerList);

                        fragmentManager.beginTransaction()
                                .replace(R.id.main_linearlayout, new VideoListFragment())
                                .commit();
                        break;
                    case 1:
                        Log.v("PLAYLISTS clicked", "yes");


                        // Insert the fragment by replacing any existing fragment
                        PlaylistFragment fragment = new PlaylistFragment();
                        Bundle args = new Bundle();
                        int numlists = mPrefs.getInt(PLAYLIST, 0);
                        args.putInt("playlists", numlists);
                        fragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_linearlayout, fragment)
                                .commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 2:
                        Log.v("SETTINGS was clicked", "yes");
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 3:
                        Log.v("ABOUT was clicked", "yes");
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    default:
                }
            } catch (NullPointerException e) {
            }


        }
    }

    public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("ACTION_PLAY")) {
                ((MyActivity) getParent()).pause();
            }
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }
    public void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            //status bar height
            int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Log.v("actionbar, statusbar",actionBarHeight + " "+statusBarHeight);
            //action bar height
            statusBar.getLayoutParams().height = statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
