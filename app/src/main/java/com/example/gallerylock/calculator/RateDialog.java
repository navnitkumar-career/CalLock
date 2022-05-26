package com.example.gallerylock.calculator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class RateDialog {
    public static void show(final Context context) {
        new AlertDialog.Builder(context).setTitle("Rate NS Vault").setMessage("Would you like to rate NS Vault on the Google Play store?").setPositiveButton("Sure", new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.RateDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Context context2 = context;
                    context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName())));
                } catch (ActivityNotFoundException unused) {
                    Context context3 = context;
                    context3.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
            }
        }).setNegativeButton("No thanks", new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.RateDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }
}
