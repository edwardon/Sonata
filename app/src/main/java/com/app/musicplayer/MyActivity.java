package com.app.musicplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.app.musicplayer.Custom.TypeFaceSpan;


public class MyActivity extends ActionBarActivity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    int numPlaylists;
    private ActionBarDrawerToggle mDrawerToggle;
    private final String PLAYLIST = "PLAYLIST";
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    SharedPreferences mPrefs;
    static boolean playing = false;
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
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        addToPlaylistTest();
        FragmentManager fragmentManager = getFragmentManager();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
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




// Update the action bar title with the TypefaceSpan instance


    }
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

    public void playSong(View view) {
        playing = !playing;
        ImageButton playButton = (ImageButton) findViewById(R.id.play_button);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pause_button);
        if (playing){

            mediaPlayer.start();
            playButton.setEnabled(false);
            playButton.setVisibility(View.INVISIBLE);
            pauseButton.setEnabled(true);
            pauseButton.setVisibility(View.VISIBLE);

        }
        else{
            mediaPlayer.pause();
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.INVISIBLE);
        }

    }
    public void addToPlaylistTest(){
        int numPlaylists = 1;
        for (int i=0; i<1; i++){
            String FILENAME = "playlist0";
            try{
                PrintWriter writer = new PrintWriter("/data/data/com.app.musicplayer/files/playlist0.txt","UTF-8");
                writer.println("EDM Playlist");
                writer.println("Best Electronic Dance Music Mix 2014 [EDM] ");
                writer.println("G-WjN61kfBw");
                writer.println("Frontier - Thisnameisafail");
                writer.println("Ph26aycXpPQ");
                writer.println("Seven Lions - Don't Leave (Ft. Ellie Goulding)");
                writer.println("SKlbCjNCDn4");
                writer.println("Live For The Night - Krewella");
                writer.println("TFdDZOoQrUE");
                writer.println("Prototype");
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
    public void showAddPopup(View v) {

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

