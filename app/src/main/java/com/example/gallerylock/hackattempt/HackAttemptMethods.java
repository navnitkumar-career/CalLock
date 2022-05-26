package com.example.gallerylock.hackattempt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/* loaded from: classes2.dex */
public class HackAttemptMethods {
    static ArrayList<HackAttemptEntity> HackAttemptEntitys;

    public void AddHackAttempToSharedPreference(Context context, String str, String str2) {
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(context);
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:edit_share_btn");
        Date date = new Date(currentTimeMillis);
        System.out.println(simpleDateFormat.format(date));
        HackAttemptEntity hackAttemptEntity = new HackAttemptEntity();
        hackAttemptEntity.SetLoginOption(GetObject.GetLoginType());
        hackAttemptEntity.SetWrongPassword(str);
        hackAttemptEntity.SetImagePath(str2);
        hackAttemptEntity.SetHackAttemptTime(date.toString());
        hackAttemptEntity.SetIsCheck(false);
        HackAttemptEntitys = new ArrayList<>();
        HackAttemptsSharedPreferences GetObject2 = HackAttemptsSharedPreferences.GetObject(context);
        ArrayList<HackAttemptEntity> GetHackAttemptObject = GetObject2.GetHackAttemptObject();
        HackAttemptEntitys = GetHackAttemptObject;
        if (GetHackAttemptObject == null) {
            ArrayList<HackAttemptEntity> arrayList = new ArrayList<>();
            HackAttemptEntitys = arrayList;
            arrayList.add(hackAttemptEntity);
        } else {
            GetHackAttemptObject.add(hackAttemptEntity);
        }
        GetObject2.SetHackAttemptObject(HackAttemptEntitys);
    }

    public static Bitmap DecodeFile(File file) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            while ((options.outWidth / i) / 2 >= 70 && (options.outHeight / i) / 2 >= 70) {
                i *= 2;
            }
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = i;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static byte[] GetBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
