package com.app.musicplayer.UI;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActionBarDrawerToggle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;


import com.app.musicplayer.Custom.TypeFaceSpan;
import com.app.musicplayer.R;
import com.app.musicplayer.Util.SongSuggestionProvider;


public class MyActivity extends ActionBarActivity implements MediaController.MediaPlayerControl {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    int numPlaylists;
    private ActionBarDrawerToggle mDrawerToggle;
    private final String PLAYLIST = "PLAYLIST";
    private final String PLAYLIST_NAMES = "PLAYLIST_NAMES";
    public static MediaPlayer mediaPlayer = null;

    private MediaController controller;

    SharedPreferences mPrefs;
    static boolean playing = false;
    HashSet<String> playlistNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpannableString s = new SpannableString("Sonata");
        s.setSpan(new TypeFaceSpan(this, "SourceSansPro-ExtraLight.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setContentView(R.layout.activity_my);
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
        if (savedInstanceState== null){

        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String [] drawerTitles = {"Search", "Playlists", "Settings", "About"};
        final Context context = this;
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerTitles));



        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer_white,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_open/* "close drawer" description */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Sonata");
                invalidateOptionsMenu();
                SpannableString s = new SpannableString("Sonata");
                s.setSpan(new TypeFaceSpan(getParent(), "SourceSansPro-ExtraLight.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                android.app.ActionBar actionBar = getActionBar();
                actionBar.setTitle(s);// creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Sonata");
                invalidateOptionsMenu();
                SpannableString s = new SpannableString("Sonata");
                s.setSpan(new TypeFaceSpan(getParent(), "SourceSansPro-ExtraLight.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                android.app.ActionBar actionBar = getActionBar();
                actionBar.setTitle(s);// creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,Gravity.START);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (mPrefs.contains(PLAYLIST)){

            numPlaylists = mPrefs.getInt(PLAYLIST, 0);

        }
        else{
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(PLAYLIST, 0);
            editor.apply();
        }
        if (mPrefs.contains(PLAYLIST_NAMES)){
            playlistNames =(HashSet<String>) mPrefs.getStringSet(PLAYLIST_NAMES, new HashSet<String>());
        }
        else{
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putStringSet(PLAYLIST_NAMES,new HashSet<String>());
            editor.apply();
        }
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        FragmentManager fragmentManager = getFragmentManager();

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
            bundle.putString("query",query);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_linearlayout,fragment)
                    .commit();
        }
        else {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_linearlayout,new VideoListFragment())
                    .commit();
        }



        String dir = "/data/data/com.app.musicplayer/files";
        addToPlaylistTest();

        controller = new MediaController(this);
        controller.setAnchorView(findViewById(R.id.main_linearlayout));
        controller.setMediaPlayer(this);
        controller.setEnabled(true);
    }

    // Call this to clear the search history, TODO: add a button in menu for this.
    public void clearHistory() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SongSuggestionProvider.AUTHORITY, SongSuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show(0);
        return true;
    }

    public MediaController getController() { return controller; }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public void addToPlaylistTest(){
        int numPlaylists = 1;
        for (int i=0; i<1; i++){
            String FILENAME = "playlist0";

            try{
                PrintWriter writer = new PrintWriter("/data/data/com.app.musicplayer/files/playlist0.txt","UTF-8");
                writer.println("EDM Playlist");
                playlistNames.add("EDM Playlist");
                writer.println("Best Electronic Dance Music Mix 2014 [EDM] ");
                writer.println(" ");
                writer.println("G-WjN61kfBw");
                writer.println("Frontier");
                writer.println("Thisnameisafail");
                writer.println("Ph26aycXpPQ");
                writer.println("Don't Leave (Ft. Ellie Goulding)");
                writer.println("Seven Lions");
                writer.println("SKlbCjNCDn4");
                writer.println("Live For The Night");
                writer.println("Krewella");
                writer.println("TFdDZOoQrUE");
                writer.println("Prototype");
                writer.println("Thisnameisafail");
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
            }
            catch (FileNotFoundException e){}
            catch (IOException e){}
            //writer.close();
        }
        mPrefs.edit().putInt(PLAYLIST,1).commit();
    }
    public void addToPlaylist(View view){

        int numPlaylists = mPrefs.getInt(PLAYLIST,0);
        numPlaylists++;
        String FILENAME = "Playlist"+numPlaylists;
        mPrefs.edit().putInt(PLAYLIST,numPlaylists).apply();
        try{

            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            //fos.write(string.getBytes());
            fos.close();
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}

    }
    public void showAddPopup(View v,String title, String id) {
        Log.v("HashSet is currently at",playlistNames.size()+"");
        boolean flag = false;
        final String names[] = new String [playlistNames.size()];
        int counter=0;
        for (String s: playlistNames){
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            PrintWriter writer;
            FileReader reader;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView) view).getText().toString();
                int index=-1;
                for (int i=0; i<names.length; i++) {
                    if (text.equals(names[i])){
                        index = i;
                        break;
                    }
                }
                if (index!= -1) {
                    renameSong(index, videoTitle, videoId);
                    alertDialog.dismiss();
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.dialog_list_item,names);
        lv.setAdapter(adapter);
        alertDialog.show();
    }
    public void renameSong(int playlistIndex, String curTitle,String videoId){
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.rename_layout,null);
        final EditText titleText = (EditText) convertView.findViewById(R.id.title_textbox);
        final EditText artistText = (EditText) convertView.findViewById(R.id.artist_textbox);
        titleText.setText(curTitle);
        final int index = playlistIndex;
        final String id = videoId;
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
                            String songTitle = "", songId = "", artist = "";
                            writer = new PrintWriter("/data/data/com.app.musicplayer/files/" + filename, "UTF-8");
                            writer.println(playlistTitle);
                            while (scanner.hasNextLine()) {
                                songTitle = scanner.nextLine();
                                artist = scanner.nextLine();
                                songId = scanner.nextLine();
                                writer.println(songTitle);
                                writer.println(artist);
                                writer.println(songId);
                            }

                            writer.println(titleText.getText().toString());
                            writer.println(artistText.getText().toString());
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
        if (mediaPlayer != null) mediaPlayer.start();
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

    public class DrawerItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            selectItem(position);
            mDrawerList.setItemChecked(position, true);
            //getActionBar().setDisplayHomeAsUpEnabled(true);
            //getActionBar().setHomeButtonEnabled(true);
        }
        public void selectItem (int position){
            FragmentManager fragmentManager = getFragmentManager();
            try{


                switch(position) {

                    case 0:
                        Log.v("SEARCH WAS CALLED","yes");

                        mDrawerLayout.closeDrawer(mDrawerList);

                        fragmentManager.beginTransaction()
                                .replace(R.id.main_linearlayout, new VideoListFragment())
                                .commit();
                        break;
                    case 1:
                        Log.v("PLAYLISTS clicked","yes");


                        // Insert the fragment by replacing any existing fragment
                        PlaylistFragment fragment = new PlaylistFragment();
                        Bundle args = new Bundle();
                        int numlists = mPrefs.getInt(PLAYLIST,0);
                        args.putInt("playlists",numlists);
                        fragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_linearlayout, fragment)
                                .commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 2:
                        Log.v("SETTINGS was clicked","yes");
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 3:
                        Log.v("ABOUT was clicked","yes");
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    default:
                }
            }
            catch (NullPointerException e){}


        }
    }

}
