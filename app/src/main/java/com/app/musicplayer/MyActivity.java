package com.app.musicplayer;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MyActivity extends ActionBarActivity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    int numPlaylists;
    private ActionBarDrawerToggle mDrawerToggle;
    private final String PLAYLIST = "PLAYLIST";
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
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
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Sonata");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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


        fragmentManager.beginTransaction()
                .replace(R.id.main_linearlayout,new VideoListFragment())
                .commit();

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
        FragmentManager fragmentManager = getFragmentManager();

    }
    public void addToPlaylistTest(){
        int numPlaylists = 3;
        for (int i=0; i<3; i++){
            String FILENAME = "Playlist"+numPlaylists;
            try{

                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                for (int j=0; j<2; j++){
                    String songName= "Song"+(j+1);
                    fos.write(songName.getBytes());
                }

                fos.close();
            }
            catch (FileNotFoundException e){}
            catch (IOException e){}
        }
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

                    fragmentManager.beginTransaction()
                            .replace(R.id.main_linearlayout, new PlaylistFragment())
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
    }

}

