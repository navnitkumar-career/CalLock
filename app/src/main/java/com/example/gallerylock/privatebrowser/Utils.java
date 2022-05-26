package com.example.gallerylock.privatebrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: classes2.dex */
public final class Utils {
    private static Bitmap mWebIconDark;
    private static Bitmap mWebIconLight;

    private Utils() {
    }

    public static void downloadFile(Activity activity, String str, String str2, String str3, boolean z) {
        String guessFileName = URLUtil.guessFileName(str, null, null);
        DownloadHandler.onDownloadStart(activity, str, str2, str3, null, z);
        Log.i(Constants.TAG, "Downloading" + guessFileName);
    }

    public static Intent newEmailIntent(Context context, String str, String str2, String str3, String str4) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.EMAIL", new String[]{str});
        intent.putExtra("android.intent.extra.TEXT", str3);
        intent.putExtra("android.intent.extra.SUBJECT", str2);
        intent.putExtra("android.intent.extra.CC", str4);
        intent.setType("message/rfc822");
        return intent;
    }

    public static void createInformativeDialog(Context context, String str, String str2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str);
        builder.setMessage(str2).setCancelable(true).setPositiveButton(context.getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.Utils.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }

    public static int convertDpToPixels(int i) {
        return (int) ((i * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static String getDomainName(String str) {
        boolean startsWith = str.startsWith(Constants.HTTPS);
        int indexOf = str.indexOf(47, 8);
        if (indexOf != -1) {
            str = str.substring(0, indexOf);
        }
        String str2 = null;
        try {
            str2 = new URI(str).getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (str2 == null || str2.isEmpty()) {
            return str;
        }
        if (!startsWith) {
            return str2.startsWith("www.") ? str2.substring(4) : str2;
        }
        return Constants.HTTPS + str2;
    }

    public static String getProtocol(String str) {
        return str.substring(0, str.indexOf(47) + 2);
    }

    public static String[] getArray(String str) {
        return str.split("\\|\\$\\|SEPARATOR\\|\\$\\|");
    }

    public static void trimCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception unused) {
        }
    }

    public static boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            for (String str : file.list()) {
                if (!deleteDir(new File(file, str))) {
                    return false;
                }
            }
        }
        return file != null && file.delete();
    }

    public static Bitmap padFavicon(Bitmap bitmap) {
        int convertDpToPixels = convertDpToPixels(4);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth() + convertDpToPixels, bitmap.getHeight() + convertDpToPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        float f = convertDpToPixels / 2;
        canvas.drawBitmap(bitmap, f, f, new Paint(2));
        return createBitmap;
    }

    public static File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return File.createTempFile("JPEG_" + format + "_", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }
}
