package com.example.gallerylock.securitylocks;

import android.app.Activity;
import android.graphics.Point;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class SecurityLocksCommon {
    public static final String ServerAddress = "https://secure.newsoftwares.net/php/web/NSVPhoneUsersPwdRecovery.php";
    public static List<Point> mSiginPattern = new ArrayList();
    public static List<Point> mSiginPatternConfirm = new ArrayList();
    public static String PatternPassword = "";
    public static boolean IsFirstLogin = false;
    public static boolean Isfreshlogin = false;
    public static boolean IsCancel = false;
    public static boolean IsSiginPattern = false;
    public static boolean IsSiginPatternContinue = false;
    public static boolean IsSiginPatternConfirm = false;
    public static boolean IsConfirmPatternActivity = false;
    public static boolean isBackupPasswordPin = false;
    public static boolean IsAppDeactive = false;
    public static boolean isBackupPattern = false;
    public static boolean IsStealthModeOn = false;
    public static boolean IsRateReview = false;
    public static boolean IsPreviewStarted = false;
    public static boolean showDialogWhatsNew = false;
    public static boolean isSettingDecoy = false;
    public static String AppName = "/NS Vault Hack Attempts/";
    public static String StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data" + AppName;
    public static String HackAttempts = "/HackAttempts/";
    public static int IsFakeAccount = 0;
    public static Activity CurrentActivity = null;
    public static boolean IsnewloginforAd = false;

    /* loaded from: classes2.dex */
    public enum LoginOptions {
        None,
        Password,
        Pattern,
        Pin,
        Calculator
    }
}
