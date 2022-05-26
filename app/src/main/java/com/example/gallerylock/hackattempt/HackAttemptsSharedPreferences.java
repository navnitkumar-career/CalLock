package com.example.gallerylock.hackattempt;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class HackAttemptsSharedPreferences {
    private static String _fileName = "HackAttempts";
    private static String _hackAttemptObject = "HackAttemptObject";
    static Context context;
    private static HackAttemptsSharedPreferences hackAttemptsSharedPreferences;
    static SharedPreferences myPrefs;

    private HackAttemptsSharedPreferences() {
    }

    public static HackAttemptsSharedPreferences GetObject(Context context2) {
        if (hackAttemptsSharedPreferences == null) {
            hackAttemptsSharedPreferences = new HackAttemptsSharedPreferences();
        }
        context = context2;
        myPrefs = context2.getSharedPreferences(_fileName, 0);
        return hackAttemptsSharedPreferences;
    }

    public void SetHackAttemptObject(ArrayList<HackAttemptEntity> arrayList) {
        SharedPreferences.Editor edit = myPrefs.edit();
        edit.putString("HackAttemptObject", new Gson().toJson(arrayList));
        edit.commit();
    }

    public ArrayList<HackAttemptEntity> GetHackAttemptObject() {
        new Gson();
        return (ArrayList) new Gson().fromJson(myPrefs.getString("HackAttemptObject", "").toString(), new TypeToken<ArrayList<HackAttemptEntity>>() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptsSharedPreferences.1
        }.getType());
    }
}
