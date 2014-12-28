package com.app.musicplayer.Custom.NavigationDrawer;

import android.graphics.drawable.Drawable;

public class NavigationItem {
    private String mText;
    private Drawable mDrawable;

    public NavigationItem(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
}
