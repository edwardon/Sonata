package com.app.musicplayer.Util;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Edward Onochie on 14/12/14.
 */

/* This class controls search history, enabling recent queries to be shown to the user while
    they search.
 */

public class SongSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.app.musicplayer.Util.SongSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SongSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
