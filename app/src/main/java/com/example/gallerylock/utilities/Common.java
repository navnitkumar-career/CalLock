package com.example.gallerylock.utilities;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.gallerylock.notes.SystemBarTintManager;
import com.example.gallerylock.notes.UIElementsHelper;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.http.HttpStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class Common {
    public static final String FL_FB_INTERSTITIAL_AD_ID = "753853102006386_753854172006279";
    public static final int HackAttemptedTotal = 3;
    public static int ToDoListId = 0;
    public static String ToDoListName = null;
    public static String UnhideKitkatAlbumName = "/Unlocked Files/";
    public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlc4y68tzu9ZRK958nn765Eqk8LnzskHIQgGG5mlIHNjEXGxp+edrSS0KFk/jGz5GZLaqwAifmB96Ufc3g2Gybkw9ue1m+T1kFPKHmMRc9UiERVkF6MrZvW1CtJqbEqZkw+H7Pay9qGtxRWiyv4xUVrEuWv6OOIyzmlJ8C4fJxsBieu5LmZrcFGSf3Go5s5duHT2PyqaD3BFPADVp8NbVGX2x+eBrAKZRnuZ1PDnOXbke1BO/vIGHwVdCmnaUVFJJfR7G/X+9LB6r56HclaHtXERznD9rpm56sd/eFmEW9EcnUOtpxg2Q5sKWxzRdaMp6/KhFOslswHihXnyZPRZddwIDAQAB";
    static final String keyPart1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlc4y68tzu9ZRK958nn765Eqk8LnzskHIQgGG5mlIHNjEXGxp+edrSS0KFk/jGz5GZLaqwAifmB96Ufc3g2Gybkw9ue1m";
    static final String keyPart2 = "+T1kFPKHmMRc9UiERVkF6MrZvW1CtJqbEqZkw+H7Pay9qGtxRWiyv4xUVrEuWv6OOIyzmlJ8C4fJxsBieu5LmZrcFGSf3Go5s5duHT2PyqaD3BFPADVp8NbVGX2x";
    static final String keyPart3 = "+eBrAKZRnuZ1PDnOXbke1BO/vIGHwVdCmnaUVFJJfR7G/X+9LB6r56HclaHtXERznD9rpm56sd/eFmEW9EcnUOtpxg2Q5sKWxzRdaMp6/KhFOslswHihXnyZPRZddwIDAQAB";
    public static final String sku_premium_pack = "ns_vault_premium_pack";
    public static String PhotoFolderName = StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM;
    public static String VideoFolderName = StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM;
    public static String DocumentFolderName = StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM;
    public static String AudioFolderName = StorageOptionsCommon.AUDIOS_DEFAULT_ALBUM;
    public static Activity CurrentWebBrowserActivity = null;
    public static boolean IsWebBrowserActive = false;
    public static String LastWebBrowserUrl = "http://www.newsoftwares.net";
    public static boolean isSignOutSuccessfully = false;
    public static int CurrentTrackIndex = 0;
    public static boolean isFolderImport = false;
    public static int loginCount = 0;
    public static int loginCountForRateAndReview = 0;
    public static int PlayListId = 0;
    public static boolean IsCameFromFeatureActivity = false;
    public static int HackAttemptCount = 0;
    public static int sortType = 0;
    public static boolean IsStart = false;
    public static boolean WhatsNew = false;
    public static final MediaPlayer mediaplayer = new MediaPlayer();
    public static final MediaPlayer voiceplayer = new MediaPlayer();
    public static boolean isOpenCameraorGalleryFromApp = false;
    public static int FolderId = 0;
    public static Activity CurrentWebServerActivity = null;
    public static int CurrentTrackId = 0;
    public static int CurrentTrackNextIndex = 0;
    public static boolean IsSelectAll = false;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static int InterstitialAdCount = 0;
    public static int EncryptBytesSize = 121;
    public static int MaxFileSizeInMB = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    public static boolean IsImporting = false;
    public static boolean IsWorkInProgress = false;
    public static boolean IsCameFromPhotoAlbum = false;
    public static int SelectedCount = 0;
    public static boolean isMove = false;
    public static boolean isUnHide = false;
    public static boolean isDelete = false;
    public static boolean isShared = false;
    public static int GalleryThumbnailCurrentPosition = 0;
    public static int PhotoThumbnailCurrentPosition = 0;
    public static int VideoThumbnailCurrentPosition = 0;
    public static int MemoriesThumbnailCurrentPosition = 0;
    public static boolean IsCameFromGalleryFeature = false;
    public static boolean IsCameFromAppGallery = false;
    public static boolean IsChangeVideoExtentionInProcess = false;
    public static String[] ColorsArray = {"#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372", "#049372"};
    public static boolean isPurchased = false;
    public static boolean IsOpenFile = false;
    public static Activity CurrentActivity = null;
    public static boolean ToDoListEdit = false;

    /* loaded from: classes2.dex */
    public enum ActivityType {
        Music,
        Document,
        Miscellaneous
    }

    /* loaded from: classes2.dex */
    public enum BrowserMenuType {
        Bookmark,
        History,
        Download
    }

    /* loaded from: classes2.dex */
    public enum DownloadStatus {
        Completed,
        InProgress,
        Failed
    }

    /* loaded from: classes2.dex */
    public enum DownloadType {
        Photo,
        Video,
        Music,
        Document,
        Miscellaneous
    }

    public static boolean IsAirplaneModeOn(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), "airplane_mode_on") == 1;
        } catch (Settings.SettingNotFoundException unused) {
            return false;
        }
    }

    public static boolean IsWiFiModeOn(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).isWifiEnabled();
    }

    public static boolean IsWiFiConnect(Context context) {
        return ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected();
    }

    public static void applyKitKatTranslucency(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setTintDrawable(UIElementsHelper.getGeneralActionBarBackground(activity));
        }
    }

    private static void setTranslucentStatus(Activity activity, boolean z) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (z) {
            attributes.flags |= 67108864;
        } else {
            attributes.flags &= -67108865;
        }
        window.setAttributes(attributes);
    }

    public static void initImageLoader(Context context) {
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(context).threadPriority(3).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.FIFO).build());
    }

    public static float GetTotalMemory() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        return ((statFs.getBlockCount() * statFs.getBlockSize()) / 1.07374182E9f) * 1024.0f;
    }

    public static float GetTotalFree() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        return ((statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1.07374182E9f) * 1024.0f;
    }

    public static long GetTotalFreeSpaceSDCard() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = (statFs.getBlockSize() * statFs.getAvailableBlocks()) / 1048576;
        Log.e("", "Available MB : " + blockSize);
        return blockSize;
    }

    public static float GetTotalUsed() {
        return (GetTotalMemory() - GetTotalFree()) * 1024.0f;
    }

    public static float GetFileSize(ArrayList<String> arrayList) {
        Iterator<String> it = arrayList.iterator();
        float f = 0.0f;
        while (it.hasNext()) {
            f += ((float) (new File(it.next()).length() / 1024)) / 1024.0f;
        }
        return f;
    }

    public static boolean isTablet7Inch(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) == 3;
    }

    public static boolean isTablet10Inch(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) == 4;
    }

    public static boolean isTablet(Context context) {
        return ((context.getResources().getConfiguration().screenLayout & 15) == 4) || ((context.getResources().getConfiguration().screenLayout & 15) == 3);
    }

    public static int getProgressPercentage(long j, long j2) {
        Double.valueOf(0.0d);
        Double.isNaN(j);
        Double.isNaN(j2);
        return Double.valueOf((j / j2) * 100.0d).intValue();
    }

    public static String milliSecondsToTimer(long j) {
        String str;
        String str2;
        int i = (int) (j / 3600000);
        long j2 = j % 3600000;
        int i2 = ((int) j2) / 60000;
        int i3 = (int) ((j2 % 60000) / 1000);
        if (i > 0) {
            str = i + ":";
        } else {
            str = "";
        }
        if (i3 < 10) {
            str2 = "0" + i3;
        } else {
            str2 = "" + i3;
        }
        return str + i2 + ":" + str2;
    }

    public static int progressToTimer(int i, int i2) {
        double d = i;
        Double.isNaN(d);
        double d2 = i2 / 1000;
        Double.isNaN(d2);
        return ((int) ((d / 100.0d) * d2)) * 1000;
    }
}
