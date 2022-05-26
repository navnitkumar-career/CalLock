package com.example.gallerylock.utilities;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.documentfile.provider.DocumentFile;

import com.example.gallerylock.R;
import com.example.gallerylock.dropbox.CloudService;
import com.example.gallerylock.dropbox.DropBoxDownloadActivity;
import com.example.gallerylock.dropbox.DropboxLoginActivity;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes2.dex */
public class Utilities {
    public static void StartCloudActivity(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Cloud", 0);
        boolean z = sharedPreferences.getBoolean("isAppRegisterd", false);
        CloudCommon.CloudType = sharedPreferences.getInt("CloudType", 0);
        Intent intent = null;
        String string = context.getSharedPreferences("DropboxPerf", 0).getString("access-token", null);
        if (!z || string == null) {
            if (!CloudCommon.IsCloudServiceStarted) {
                context.startService(new Intent(context, CloudService.class));
            }
            SecurityLocksCommon.IsAppDeactive = false;
            if (CloudCommon.CloudType == CloudCommon.CloudType1.DropBox.ordinal()) {
                intent = new Intent(context, DropboxLoginActivity.class);
            }
            context.startActivity(intent);
            ((Activity) context).finish();
            return;
        }
        if (!CloudCommon.IsCloudServiceStarted) {
            context.startService(new Intent(context, CloudService.class));
        }
        SecurityLocksCommon.IsAppDeactive = false;
        if (CloudCommon.CloudType == CloudCommon.CloudType1.DropBox.ordinal()) {
            intent = new Intent(context, DropBoxDownloadActivity.class);
        }
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void DeleteFile(String str) throws IOException {
        new File(str).delete();
    }

    public static void hideKeyboard(View view, Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean DeleteAlbum(File file, Context context) throws IOException {
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        file.delete();
        return true;
    }

    public static String convertDateToGMT(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        TimeZone timeZone = calendar.getTimeZone();
        int rawOffset = timeZone.getRawOffset();
        if (timeZone.inDaylightTime(new Date())) {
            rawOffset += timeZone.getDSTSavings();
        }
        int i = (rawOffset / 1000) / 60;
        calendar.add(11, -(i / 60));
        calendar.add(12, -(i % 60));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime()) + " +0000";
    }

    public static String getCurrentDateTime2() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public static TextView strikeTextview(TextView textView, String str, boolean z) {
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        if (z) {
            textView.setText(str, TextView.BufferType.SPANNABLE);
            ((Spannable) textView.getText()).setSpan(strikethroughSpan, 0, str.length(), 33);
        } else {
            textView.setText(str);
        }
        return textView;
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

    public static String FileSize(String str) {
        FileChannel fileChannel;
        try {
            fileChannel = new FileInputStream(str).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileChannel = null;
        }
        long j = 0;
        try {
            j = fileChannel.size();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        long j2 = j / 1024;
        if (j2 > 1000) {
            return String.valueOf(j / 1048576) + "mb";
        }
        return String.valueOf(j2) + "kb";
    }

    public static String getFileNameWithoutExtention(String str) {
        String name = new File(str).getName();
        for (int length = name.length() - 1; length > 0; length--) {
            if (name.charAt(length) == "_".charAt(0)) {
                int lastIndexOf = name.lastIndexOf(".");
                return lastIndexOf > 0 ? name.substring(length + 1, lastIndexOf) : name;
            }
        }
        return "";
    }

    public static int getNoOfColumns(Context context, int i, boolean z) {
        if (i == 1) {
            return Common.isTablet10Inch(context) ? z ? 4 : 1 : Common.isTablet7Inch(context) ? z ? 3 : 1 : z ? 2 : 1;
        }
        if (i == 2) {
            return Common.isTablet10Inch(context) ? z ? 5 : 1 : Common.isTablet7Inch(context) ? z ? 4 : 1 : z ? 3 : 1;
        }
        return 2;
    }

    public static String NSHideFile(Context context, File file, File file2) throws IOException {
        FileChannel fileChannel;
        if (!file.exists()) {
            return "";
        }
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File(file2.getAbsolutePath() + "/" + ChangeFileExtention(file.getName()));
        if (file3.exists()) {
            file3 = GetFileName(ChangeFileExtention(file.getName()), file3, file2.getAbsolutePath());
        }
        file3.createNewFile();
        FileChannel fileChannel2 = null;
        try {
            FileChannel channel = new FileInputStream(file).getChannel();
            try {
                fileChannel2 = new FileOutputStream(file3, true).getChannel();
                if (file.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH)) {
                    file.renameTo(file3);
                } else {
                    long size = channel.size();
                    long abs = Math.abs(size / 1048576);
                    if (abs > Common.MaxFileSizeInMB) {
                        int CalculateChunkCounts = CalculateChunkCounts(abs);
                        double d = size;
                        double d2 = CalculateChunkCounts;
                        Double.isNaN(d);
                        Double.isNaN(d2);
                        int abs2 = Math.abs((int) Math.ceil(d / d2));
                        for (int i = 0; i < CalculateChunkCounts; i++) {
                            channel.transferTo(abs2 * i, abs2, fileChannel2);
                        }
                    } else {
                        channel.transferTo(0L, size, fileChannel2);
                    }
                    if (channel != null) {
                        channel.close();
                    }
                    if (fileChannel2 != null) {
                        fileChannel2.close();
                    }
                    if (file.exists() && file3.exists()) {
                        file.delete();
                    }
                }
                return file3.getAbsolutePath();
            } catch (Exception unused) {
                fileChannel = fileChannel2;
                fileChannel2 = channel;
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (fileChannel == null) {
                    return "";
                }
                fileChannel.close();
                return "";
            }
        } catch (Exception unused2) {
            fileChannel = null;
        }
        return null;
    }

    public static boolean NSUnHideFile(Context context, String str, String str2) throws IOException {
        if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
            str2 = Environment.getExternalStorageDirectory().getPath() + Common.UnhideKitkatAlbumName + FileName(str2);
        }
        File file = new File(str);
        File file2 = new File(str2);
        if (file2.exists()) {
            file2 = GetDesFileNameForUnHide(file2.getAbsolutePath(), file2.getName(), file2);
        }
        File file3 = new File(file2.getParent());
        if (!file3.exists() && !file3.mkdirs()) {
            File file4 = new File(Environment.getExternalStorageDirectory().getPath() + Common.UnhideKitkatAlbumName + FileName(str2));
            file2 = file4.exists() ? GetDesFileNameForUnHide(file4.getAbsolutePath(), file4.getName(), file4) : file4;
            if (!file3.exists() && !file3.mkdirs()) {
                return false;
            }
        }
        if (file2.createNewFile() && file3.exists()) {
            FileChannel fileChannel = null;
            try {
                if (!(file2.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH) ? file.renameTo(file2) : false)) {
                    try {
                        long size = fileChannel.size();
                        long abs = Math.abs(size / 1048576);
                        if (abs > Common.MaxFileSizeInMB) {
                            int CalculateChunkCounts = CalculateChunkCounts(abs);
                            double d = size;
                            double d2 = CalculateChunkCounts;
                            Double.isNaN(d);
                            Double.isNaN(d2);
                            int abs2 = Math.abs((int) Math.ceil(d / d2));
                            for (int i = 0; i < CalculateChunkCounts; i++) {
                                FileChannel fileChannel2 = null;
                                fileChannel2.transferTo((abs2 * i) + 0, abs2, null);
                            }
                        } else {
                            FileChannel fileChannel3 = null;
                            fileChannel3.transferTo(0L, size, null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(file2.getAbsolutePath())));
                    context.sendBroadcast(intent);
                }
                if (!file.exists() || !file2.exists()) {
                    NSDecryption(file2);
                    return true;
                }
                NSDecryption(file2);
                file.delete();
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    public static File GetDesFileNameForUnHide(String str, String str2, File file) {
        int lastIndexOf = 0;
        String str3;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append(str.substring(0, str.lastIndexOf(47)));
        sb.append("/");
        String sb2 = sb.toString();
        String substring = str2.substring(0, str2.lastIndexOf(46));
        if (str2.lastIndexOf(46) > Math.max(str2.lastIndexOf(47), str2.lastIndexOf(92))) {
            str3 = "." + str2.substring(lastIndexOf + 1);
        } else {
            str3 = "";
        }
        while (i < 100) {
            File file2 = new File(sb2 + "/" + (substring + "(" + i + ")" + str3));
            if (!file2.exists()) {
                return file2;
            }
            i++;
            file = file2;
        }
        return file;
    }

    public static boolean MoveFileWithinDirectories(String str, String str2) throws IOException {
        File file = new File(str);
        File file2 = new File(str2);
        File file3 = new File(file2.getParent());
        if (!file3.exists()) {
            file3.mkdirs();
        }
        return file2.exists() ? file.renameTo(GetFileName(file.getName(), file2, file2.getParent())) : file.renameTo(file2);
    }

    public static String CopyTemporaryFile(Context context, String str, String str2) throws IOException {
        FileChannel fileChannel;
        FileChannel channel = null;
        FileChannel fileChannel2 = null;
        File file = new File(str);
        String FileName = FileName(str);
        File file2 = new File(str2 + ChangeFileExtentionToOrignal(FileName));
        File file3 = new File(file2.getParent());
        if (!file3.exists() && !file3.mkdirs()) {
            file2 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + FileName(str2));
            if (!file3.exists()) {
                file3.mkdirs();
            }
        }
        file2.createNewFile();
        if (file3.exists()) {
            FileChannel fileChannel3 = null;
            try {
                channel = new FileInputStream(file).getChannel();
            } catch (Exception unused) {
                fileChannel = null;
            }
            try {
                fileChannel3 = new FileOutputStream(file2).getChannel();
                long size = channel.size();
                long abs = Math.abs(size / 1048576);
                if (abs > Common.MaxFileSizeInMB) {
                    int CalculateChunkCounts = CalculateChunkCounts(abs);
                    double d = size;
                    double d2 = CalculateChunkCounts;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    int abs2 = Math.abs((int) Math.ceil(d / d2));
                    for (int i = 0; i < CalculateChunkCounts; i++) {
                        channel.transferTo((abs2 * i) + 0, abs2, fileChannel3);
                    }
                } else {
                    channel.transferTo(0L, size, fileChannel3);
                }
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel3 != null) {
                    fileChannel3.close();
                }
                NSDecryption(file2);
                return file2.getAbsolutePath();
            } catch (Exception unused2) {
                fileChannel = fileChannel3;
                fileChannel3 = fileChannel2;
                if (fileChannel3 != null) {
                    fileChannel3.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                return file2.getAbsolutePath();
            }
        }
        return file2.getAbsolutePath();
    }

    public static boolean UnHideThumbnail(Context context, String str, String str2) throws IOException {
        FileChannel fileChannel;
        FileChannel channel = null;
        File file = new File(str);
        File file2 = new File(str2);
        if (file2.exists()) {
            file2 = GetDesFileNameForUnHide(file2.getAbsolutePath(), file2.getName(), file2);
        }
        File file3 = new File(file2.getParent());
        if ((file3.exists() || file3.mkdirs() || file2.createNewFile()) && file3.exists()) {
            FileChannel fileChannel2 = null;
            try {
                channel = new FileInputStream(file).getChannel();
            } catch (Exception unused) {
                fileChannel = null;
            }
            try {
                fileChannel2 = new FileOutputStream(file2).getChannel();
                channel.transferTo(0L, channel.size(), fileChannel2);
                if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(file2.getAbsolutePath())));
                    context.sendBroadcast(intent);
                }
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (!file.exists() || !file2.exists()) {
                    return true;
                }
                file.delete();
                return true;
            } catch (Exception unused2) {
                fileChannel = fileChannel2;
                fileChannel2 = channel;
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                return false;
            }
        }
        return false;
    }

    public static String RecoveryHideFileSDCard(Context context, File file, File file2) throws IOException {
        File file3;
        FileChannel fileChannel;
        FileChannel channel = null;
        if (!file.exists()) {
            return "";
        }
        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (file.getName().contains("#")) {
            file3 = new File(file2.getAbsolutePath() + "/" + file.getName());
        } else {
            file3 = new File(file2.getAbsolutePath() + "/" + ChangeFileExtention(file.getName()));
        }
        if (file3.exists()) {
            file3 = GetFileName(file.getName(), file3, file2.getAbsolutePath());
        }
        file3.createNewFile();
        FileChannel fileChannel2 = null;
        try {
            channel = new FileInputStream(file).getChannel();
        } catch (Exception unused) {
            fileChannel = null;
        }
        try {
            fileChannel2 = new FileOutputStream(file3).getChannel();
            if (file.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH)) {
                file.renameTo(file3);
            } else {
                long size = channel.size();
                long abs = Math.abs(size / 1048576);
                if (abs > Common.MaxFileSizeInMB) {
                    int CalculateChunkCounts = CalculateChunkCounts(abs);
                    double d = size;
                    double d2 = CalculateChunkCounts;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    int abs2 = Math.abs((int) Math.ceil(d / d2));
                    for (int i = 0; i < CalculateChunkCounts; i++) {
                        channel.transferTo((abs2 * i) + 0, abs2, fileChannel2);
                    }
                } else {
                    channel.transferTo(0L, size, fileChannel2);
                }
            }
            if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(Uri.fromFile(new File(file.getAbsolutePath())));
                context.sendBroadcast(intent);
            }
            if (channel != null) {
                channel.close();
            }
            if (fileChannel2 != null) {
                fileChannel2.close();
            }
            if (file.exists() && file3.exists()) {
                file.delete();
            }
            return file3.getAbsolutePath();
        } catch (Exception unused2) {
            fileChannel = fileChannel2;
            fileChannel2 = channel;
            if (fileChannel2 != null) {
                fileChannel2.close();
            }
            if (fileChannel == null) {
                return "";
            }
            fileChannel.close();
            return "";
        }
    }

    public static String RecoveryEntryFile(Context context, File file, File file2) throws IOException {
        FileChannel fileChannel;
        FileChannel channel = null;
        if (file.exists()) {
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileChannel fileChannel2 = null;
            try {
                channel = new FileInputStream(file).getChannel();
            } catch (Exception unused) {
                fileChannel = null;
            }
            try {
                fileChannel2 = new FileOutputStream(file2).getChannel();
                if (file.getAbsolutePath().contains(StorageOptionsCommon.STORAGEPATH)) {
                    file.renameTo(file2);
                } else {
                    long size = channel.size();
                    long abs = Math.abs(size / 1048576);
                    if (abs > Common.MaxFileSizeInMB) {
                        int CalculateChunkCounts = CalculateChunkCounts(abs);
                        double d = size;
                        double d2 = CalculateChunkCounts;
                        Double.isNaN(d);
                        Double.isNaN(d2);
                        int abs2 = Math.abs((int) Math.ceil(d / d2));
                        for (int i = 0; i < CalculateChunkCounts; i++) {
                            channel.transferTo((abs2 * i) + 0, abs2, fileChannel2);
                        }
                    } else {
                        channel.transferTo(0L, size, fileChannel2);
                    }
                }
                if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(file.getAbsolutePath())));
                    context.sendBroadcast(intent);
                }
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (file.exists() && file2.exists()) {
                    file.delete();
                }
                return file2.getAbsolutePath();
            } catch (Exception unused2) {
                fileChannel = fileChannel2;
                fileChannel2 = channel;
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                return file2.getAbsolutePath();
            }
        }
        return file2.getAbsolutePath();
    }

    private static int CalculateChunkCounts(long j) {
        int i = 2;
        do {
            i++;
        } while (j / i > Common.MaxFileSizeInMB);
        return i;
    }

    public static void NSEncryption(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0L);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
    }

    public static void NSDecryption(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0L);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
    }

    public static String NSVideoDecryptionDuringPlay(File file) throws IOException {
        File file2 = new File(file.getParent() + File.separator + ChangeFileExtentionToOrignal(FileName(file.getAbsolutePath())));
        file.renameTo(file2);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0L);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
        return file2.getAbsolutePath();
    }

    public static String NSVideoEncryptionAfterPlay(File file) throws IOException {
        File file2 = new File(file.getParent() + File.separator + ChangeFileExtention(FileName(file.getAbsolutePath())));
        file.renameTo(file2);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        byte[] bArr = new byte[Common.EncryptBytesSize];
        randomAccessFile.read(bArr, 0, Common.EncryptBytesSize);
        randomAccessFile.seek(0L);
        randomAccessFile.write(ReverseBytes(bArr));
        randomAccessFile.close();
        return file2.getAbsolutePath();
    }

    public static byte[] ReverseBytes(byte[] bArr) {
        if (bArr == null) {
            return bArr;
        }
        int length = bArr.length - 1;
        for (int i = 0; length > i; i++) {
            byte b = bArr[length];
            bArr[length] = bArr[i];
            bArr[i] = b;
            length--;
        }
        return bArr;
    }

    public static String ChangeFileExtentionToOrignal(String str) {
        String substring = str.substring(0, str.lastIndexOf(35));
        int lastIndexOf = str.lastIndexOf(35);
        return substring + ("." + str.substring(lastIndexOf + 1));
    }

    public static String FileName(String str) {
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == " /".charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    private static File GetFileName(String str, File file, String str2) {
        int lastIndexOf = 0;
        int lastIndexOf2 = 0;
        String str3 = "";
        int i = 0;
        if (str.contains("#")) {
            String substring = str.substring(0, str.lastIndexOf(35));
            if (str.lastIndexOf(35) > Math.max(str.lastIndexOf(47), str.lastIndexOf(92))) {
                str3 = "#" + str.substring(lastIndexOf2 + 1);
            }
            while (i < 100) {
                file = new File(str2 + "/" + (substring + "(" + i + ")" + str3));
                if (!file.exists()) {
                    return file;
                }
                i++;
            }
        } else {
            String substring2 = str.substring(0, str.lastIndexOf(46));
            if (str.lastIndexOf(46) > Math.max(str.lastIndexOf(47), str.lastIndexOf(92))) {
                str3 = "." + str.substring(lastIndexOf + 1);
            }
            while (i < 100) {
                file = new File(str2 + "/" + (substring2 + "(" + i + ")" + str3));
                if (!file.exists()) {
                    return file;
                }
                i++;
            }
        }
        return file;
    }

    public static String ChangeFileExtention(String str) {
        String substring = str.substring(0, str.lastIndexOf(46));
        int lastIndexOf = str.lastIndexOf(46);
        return substring + ("#" + str.substring(lastIndexOf + 1));
    }

    public static void CheckDeviceStoragePaths(Context context) {
        new ArrayList();
        if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            StorageOptionsCommon.IsStorageSDCard = context.getSharedPreferences("StorageOption", 0).getBoolean("IsStorageSDCard", false);
            ArrayList<String> externalMountss = getExternalMountss();
            if (externalMountss.size() > 0) {
                StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage = true;
                for (int i = 0; i < externalMountss.size(); i++) {
                    String str = externalMountss.get(i);
                    String[] split = str.split("/");
                    String str2 = split[2];
                    if (!str2.equals("sdcard") && !str2.equals("sdcard0") && new File(str).exists()) {
                        StorageOptionsCommon.STORAGEPATH_2 = str + "/";
                    } else if (str2.equals("media_rw")) {
                        StorageOptionsCommon.STORAGEPATH_2 = "/" + split[1] + "/" + split[3] + "/";
                    }
                }
            } else {
                StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage = false;
            }
        } else {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(externalStoragePublicDirectory.getParent());
            if (new File("/storage/sdcard/").exists()) {
                StorageOptionsCommon.STORAGEPATH = externalStoragePublicDirectory.getParent() + File.separator;
            }
            if (new File("/storage/sdcard0/").exists()) {
                StorageOptionsCommon.STORAGEPATH = externalStoragePublicDirectory.getParent() + File.separator;
            } else if (file.exists()) {
                StorageOptionsCommon.STORAGEPATH = externalStoragePublicDirectory.getParent() + File.separator;
            }
            File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM);
            if (!file2.exists()) {
                file2.mkdirs();
                file2.exists();
            }
            File file3 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.STORAGE, "don't delete this folder.txt");
            if (!file3.exists()) {
                try {
                    file3.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file3);
                    fileOutputStream.write("Warning! \nDo Not Delete this folder; it contains NS Vault Encrypted data.".getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SharedPreferences.Editor edit = context.getSharedPreferences("StorageOption", 0).edit();
            edit.putString("STORAGEPATH", StorageOptionsCommon.STORAGEPATH);
            edit.commit();
        }
        File externalStoragePublicDirectory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file4 = new File(externalStoragePublicDirectory2.getParent());
        File file5 = new File(StorageOptionsCommon.STORAGEPATH_2);
        if (file4.exists()) {
            StorageOptionsCommon.STORAGEPATH_1 = externalStoragePublicDirectory2.getParent() + File.separator;
        }
        File file6 = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.PHOTOS + StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM);
        if (!file6.exists()) {
            file6.mkdirs();
            file6.exists();
        }
        File file7 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.STORAGE, "don't delete this folder.txt");
        if (!file7.exists()) {
            try {
                file7.createNewFile();
                FileOutputStream fileOutputStream2 = new FileOutputStream(file7);
                fileOutputStream2.write("Warning! \nDo Not Delete this folder, it contains NS Vault data.".getBytes());
                fileOutputStream2.flush();
                fileOutputStream2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (!file5.exists()) {
            StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage = false;
        }
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

    public static ArrayList<String> getExternalMountss() {
        String[] split;
        String[] split2;
        byte[] bArr = new byte[0];
        ArrayList<String> arrayList = new ArrayList<>();
        String str = "";
        try {
            Process start = new ProcessBuilder(new String[0]).command("mount").redirectErrorStream(true).start();
            start.waitFor();
            InputStream inputStream = start.getInputStream();
            while (inputStream.read(new byte[1024]) != -1) {
                str = str + new String(bArr);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String str2 : str.split(IOUtils.LINE_SEPARATOR_UNIX)) {
            if (!str2.toLowerCase(Locale.US).contains("asec") && str2.matches("(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*")) {
                for (String str3 : str2.split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR)) {
                    if (str3.startsWith("/") && !str3.toLowerCase(Locale.US).contains("vold")) {
                        arrayList.add(str3);
                    }
                }
            }
        }
        return arrayList;
    }

    public static int getScreenOrientation(Context context) {
        Activity activity = (Activity) context;
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        defaultDisplay.getOrientation();
        int i = activity.getResources().getConfiguration().orientation;
        if (i != 0) {
            return i;
        }
        if (defaultDisplay.getWidth() == defaultDisplay.getHeight()) {
            return 3;
        }
        return defaultDisplay.getWidth() < defaultDisplay.getHeight() ? 1 : 2;
    }

    public static boolean isNetworkOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            if (networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
                NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(1);
                if (networkInfo2 == null) {
                    return false;
                }
                if (networkInfo2.getState() != NetworkInfo.State.CONNECTED) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("EEE dd MMM yyyy, HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static int convertDptoPix(Context context, int i) {
        return (int) TypedValue.applyDimension(1, i, context.getResources().getDisplayMetrics());
    }

    public static Uri getLastPhotoOrVideo(Context context) {
        Cursor query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "date_added"}, null, null, "date_added DESC");
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return Uri.fromFile(new File(string));
    }

    public static void changeFileExtention(String str) {
        File[] listFiles;
        File[] listFiles2;
        File file = null;
        try {
            if (str.equals(StorageOptionsCommon.VIDEOS)) {
                file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + "/");
            }
            if (str.equals(StorageOptionsCommon.DOCUMENTS)) {
                file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + "/");
            }
            if (file.exists()) {
                for (File file2 : file.listFiles()) {
                    if (file2.isDirectory()) {
                        for (File file3 : file2.listFiles()) {
                            if (file3.isFile()) {
                                String absolutePath = file3.getAbsolutePath();
                                if (!FileName(absolutePath).contains("#")) {
                                    NSVideoEncryptionAfterPlay(new File(absolutePath));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception unused) {
            Log.v("changeVideosExtention", "error in changeVideosExtention method");
        }
    }

    public static void DeleteSDcardImageLollipop(Context context, String str) {
        DeleteFile(context, str, DocumentFile.fromTreeUri(context, Uri.parse(StorageOptionSharedPreferences.GetObject(context).GetSDCardUri())));
    }

    public static void DeleteFile(Context context, String str, DocumentFile documentFile) {
        File file = new File(str);
        traverseDoc(context, documentFile, new ArrayList(Arrays.asList(file.getParent().split("/"))), file.getName());
    }

    public static void traverseDoc(Context context, DocumentFile documentFile, ArrayList<String> arrayList, String str) {
        DocumentFile[] listFiles;
        for (DocumentFile documentFile2 : documentFile.listFiles()) {
            if (documentFile2.isDirectory()) {
                if (arrayList.contains(documentFile2.getName())) {
                    traverseDoc(context, documentFile2, arrayList, str);
                    return;
                }
            } else if (documentFile2.isFile() && documentFile2.getName().equals(str)) {
                try {
                    if (DocumentsContract.deleteDocument(context.getContentResolver(), documentFile2.getUri())) {
                        return;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean needPermissionForBlocking(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            return ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        }
    }

    public static void hideMenuItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() != R.id.action_buy) {
                menu.getItem(i).setVisible(true);
            } else if (Common.isPurchased) {
                menu.getItem(i).setVisible(false);
            } else {
                menu.getItem(i).setVisible(true);
            }
        }
    }
}
