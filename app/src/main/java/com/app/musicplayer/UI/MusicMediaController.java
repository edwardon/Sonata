package com.app.musicplayer.UI;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Edward Onochie on 17/12/14.
 */

// Same as the regular one, but doesn't hide.
public class MusicMediaController extends MediaController {
    public MusicMediaController(Context context) {
        super(context);
    }

    @Override
    public void hide() {

    }

    public void actuallyHide() {
        super.hide();
    }
}