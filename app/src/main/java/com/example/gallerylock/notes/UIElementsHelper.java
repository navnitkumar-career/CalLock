package com.example.gallerylock.notes;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

/* loaded from: classes2.dex */
public class UIElementsHelper {
    private static final String BLACK = "BLACK";
    private static final String BLUE = "BLUE";
    private static final String GRAY = "GRAY";
    private static final String GREEN = "GREEN";
    private static final String MAGENTA = "MAGENTA";
    private static final String NOW_PLAYING_COLOR = "NOW_PLAYING_COLOR";
    private static final String ORANGE = "ORANGE";
    private static final String PURPLE = "PURPLE";
    private static final String RED = "RED";
    private static final String WHITE = "WHITE";

    public static Drawable getGeneralActionBarBackground(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context);
        return new ColorDrawable(-14142061);
    }
}
