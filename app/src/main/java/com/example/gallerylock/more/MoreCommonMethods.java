package com.example.gallerylock.more;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.R;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import org.apache.http.protocol.HTTP;

/* loaded from: classes2.dex */
public class MoreCommonMethods {
    public static void TellaFriendDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.tell_a_friend_dialog);
        dialog.setCancelable(true);
        ((ImageView) dialog.findViewById(R.id.ivemail)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreCommonMethods.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(HTTP.PLAIN_TEXT_TYPE);
                intent.putExtra("android.intent.extra.EMAIL", new String[]{""});
                intent.putExtra("android.intent.extra.SUBJECT", "Download this amazing app, NS Vault.");
                intent.putExtra("android.intent.extra.TEXT", "Hey, there's a securer and easier way to hide secret photos, videos, documents and every other sort of data on your Android Phone. Download this amazing app NS Vault, and see for yourself. Download NS Vault by clicking here:  Play Store Link \n https://play.google.com/store/apps/details?id=" + AppPackageCommon.AppPackageName);
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    context.startActivity(Intent.createChooser(intent, "Tell A Friend via email..."));
                } catch (ActivityNotFoundException unused) {
                }
            }
        });
        ((ImageView) dialog.findViewById(R.id.ivfacebook)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreCommonMethods.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
                SecurityLocksCommon.IsAppDeactive = false;
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.facebook.com/sharer/sharer.php?u=https://play.google.com/store/apps/details?id=" + AppPackageCommon.AppPackageName)));
            }
        });
        ((ImageView) dialog.findViewById(R.id.ivtwitter)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreCommonMethods.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
                SecurityLocksCommon.IsAppDeactive = false;
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/intent/tweet?text=Protect photos, videos, audio files, documents and other sort of data on your phone with NS Vault! https://play.google.com/store/apps/details?id=" + AppPackageCommon.AppPackageName)));
            }
        });
        ((ImageView) dialog.findViewById(R.id.ivinsta)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreCommonMethods.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
                SecurityLocksCommon.IsAppDeactive = false;
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.instagram.com/?hl=en=https://play.google.com/store/apps/details?id=" + AppPackageCommon.AppPackageName)));
            }
        });
        ((Button) dialog.findViewById(R.id.btnDialogCancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreCommonMethods.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
