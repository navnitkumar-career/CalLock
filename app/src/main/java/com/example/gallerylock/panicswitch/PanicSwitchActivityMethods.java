package com.example.gallerylock.panicswitch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.gallerylock.securitylocks.SecurityLocksCommon;

/* loaded from: classes2.dex */
public class PanicSwitchActivityMethods {
    public static void SwitchApp(Context context) {
        Intent intent;
        SecurityLocksCommon.IsAppDeactive = true;
        if (PanicSwitchCommon.SwitchingApp.equals(PanicSwitchCommon.SwitchApp.Browser.toString())) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com"));
        } else {
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
        }
        intent.addFlags(67108864);
        context.startActivity(intent);
    }
}
