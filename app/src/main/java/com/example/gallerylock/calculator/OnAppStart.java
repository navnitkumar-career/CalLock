package com.example.gallerylock.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/* loaded from: classes.dex */
public class OnAppStart extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (defaultSharedPreferences.getInt("launch_count", 5) > 0) {
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            edit.putInt("launch_count", defaultSharedPreferences.getInt("launch_count", 5) - 1);
            edit.apply();
        }
    }
}
