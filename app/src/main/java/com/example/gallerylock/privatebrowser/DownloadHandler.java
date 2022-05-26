package com.example.gallerylock.privatebrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Common;

import org.apache.commons.fileupload.FileUploadBase;

import java.io.File;

/* loaded from: classes2.dex */
public class DownloadHandler {
    private static final String LOGTAG = "DLHandler";

    public static void onDownloadStart(Activity activity, String str, String str2, String str3, String str4, boolean z) {
        if (str3 == null || !str3.regionMatches(true, 0, FileUploadBase.ATTACHMENT, 0, 10)) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(str), str4);
//            intent.addFlags(CrashUtils.ErrorDialogData.BINDER_CRASH);
            ResolveInfo resolveActivity = activity.getPackageManager().resolveActivity(intent, 65536);
            if (resolveActivity != null) {
                ComponentName componentName = activity.getComponentName();
                if (!componentName.getPackageName().equals(resolveActivity.activityInfo.packageName) || !componentName.getClassName().equals(resolveActivity.activityInfo.name)) {
                    try {
                        activity.startActivity(intent);
                        return;
                    } catch (ActivityNotFoundException unused) {
                    }
                }
            }
        }
        onDownloadStartNoStream(activity, str, str2, str3, str4, z);
    }

    private static String encodePath(String str) {
        boolean z;
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (c == '[' || c == ']' || c == '|') {
                z = true;
                break;
            }
        }
        z = false;
        if (!z) {
            return str;
        }
        StringBuilder sb = new StringBuilder("");
        for (char c2 : charArray) {
            if (c2 == '[' || c2 == ']' || c2 == '|') {
                sb.append('%');
                sb.append(Integer.toHexString(c2));
            } else {
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    /* JADX WARN: Type inference failed for: r10v1, types: [net.newsoftwares.hidepicturesvideos.privatebrowser.DownloadHandler$1] */
    private static void onDownloadStartNoStream(final Activity activity, final String str, String str2, String str3, String str4, boolean z) {
        int i;
        String str5;
        final String guessFileName = URLUtil.guessFileName(str, str3, str4);
        String externalStorageState = Environment.getExternalStorageState();
        if (!externalStorageState.equals("mounted")) {
            if (externalStorageState.equals("shared")) {
                str5 = activity.getString(R.string.download_sdcard_busy_dlg_msg);
                i = R.string.download_sdcard_busy_dlg_title;
            } else {
                str5 = activity.getString(R.string.download_no_sdcard_dlg_msg);
                i = R.string.download_no_sdcard_dlg_title;
            }
            new AlertDialog.Builder(activity).setTitle(i).setIcon(17301543).setMessage(str5).setPositiveButton(R.string.action_ok, (DialogInterface.OnClickListener) null).show();
            return;
        }
        try {
            WebAddress webAddress = new WebAddress(str);
            webAddress.setPath(encodePath(webAddress.getPath()));
            String webAddress2 = webAddress.toString();
            try {
                final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(webAddress2));
                request.setMimeType(str4);
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, guessFileName);
                request.allowScanningByMediaScanner();
                request.setDescription(webAddress.getHost());
                String cookie = CookieManager.getInstance().getCookie(str);
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(1);
                if (str4 != null) {
                    final DownloadManager downloadManager = (DownloadManager) activity.getSystemService("download");
                    new Thread("Browser download") { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.DownloadHandler.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            long enqueue = downloadManager.enqueue(request);
                            String str6 = str;
                            if (str6 != null && str6.length() > 0) {
                                DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
                                if (str6.contains(".jpg") || str6.contains(".png") || str6.contains(".gif") || str6.contains(".bmp")) {
                                    downloadFileEnt.SetDownloadType(Common.DownloadType.Photo.ordinal());
                                } else if (str6.contains(".mp4") || str6.contains(".3gp") || str6.contains(".avi") || str6.contains(".flv") || str6.contains(".mkv") || str6.contains(".wmv")) {
                                    downloadFileEnt.SetDownloadType(Common.DownloadType.Video.ordinal());
                                } else if (str6.contains(".pdf") || str6.contains(".doc") || str6.contains(".docx") || str6.contains(".ppt") || str6.contains(".pptx") || str6.contains(".xls") || str6.contains(".xlsx") || str6.contains(".csv") || str6.contains(".dbk") || str6.contains(".dot") || str6.contains(".dotx") || str6.contains(".gdoc") || str6.contains(".pdax") || str6.contains(".pda") || str6.contains(".rtf") || str6.contains(".rpt") || str6.contains(".stw") || str6.contains(".txt") || str6.contains(".uof") || str6.contains(".uoml") || str6.contains(".wps") || str6.contains(".wpt") || str6.contains(".wrd") || str6.contains(".xps") || str6.contains(".epub") || str6.contains(".xml")) {
                                    downloadFileEnt.SetDownloadType(Common.DownloadType.Document.ordinal());
                                } else if (str6.contains(".7z") || str6.contains(".ace") || str6.contains(".bik") || str6.contains(".bin") || str6.contains(".bkf") || str6.contains(".bzip2") || str6.contains(".cab") || str6.contains(".daa") || str6.contains(".gzip") || str6.contains(".jar") || str6.contains(".apk") || str6.contains(".xap") || str6.contains(".lzip") || str6.contains(".rar") || str6.contains(".tgz") || str6.contains(".iso") || str6.contains(".img") || str6.contains(".mdx") || str6.contains(".dmg") || str6.contains(".acp") || str6.contains(".amf") || str6.contains(".4db") || str6.contains(".4dr") || str6.contains(".ave") || str6.contains(".fm") || str6.contains(".acl") || str6.contains(".ans") || str6.contains(".ots") || str6.contains(".egt") || str6.contains(".ftx") || str6.contains(".lwp") || str6.contains(".nb") || str6.contains(".nbp") || str6.contains(".odm") || str6.contains(".odt") || str6.contains(".ott") || str6.contains(".via") || str6.contains(".wps") || str6.contains(".wrf") || str6.contains(".wri") || str6.contains(".org") || str6.contains(".ahk") || str6.contains(".as") || str6.contains(".bat") || str6.contains(".bas") || str6.contains(".hta") || str6.contains(".ijs") || str6.contains(".js") || str6.contains(".ncf") || str6.contains(".nut") || str6.contains(".sdl") || str6.contains(".au") || str6.contains(".raw") || str6.contains(".pac") || str6.contains(".m4a") || str6.contains(".ab2") || str6.contains(".via") || str6.contains(".wps") || str6.contains(".wrf") || str6.contains(".wri") || str6.contains(".ab3") || str6.contains(".aws") || str6.contains(".clf") || str6.contains(".ods") || str6.contains(".vc") || str6.contains(".bak") || str6.contains(".bdf") || str6.contains(".tos") || str6.contains(".exe") || str6.contains(".msg") || str6.contains(".dtp") || str6.contains(".pub") || str6.contains(".zip")) {
                                    downloadFileEnt.SetDownloadType(Common.DownloadType.Miscellaneous.ordinal());
                                } else if (str6.contains(".mp3") || str6.contains(".wav")) {
                                    downloadFileEnt.SetDownloadType(Common.DownloadType.Music.ordinal());
                                } else {
                                    downloadFileEnt.SetDownloadType(Common.DownloadType.Miscellaneous.ordinal());
                                }
                                downloadFileEnt.SetFileDownloadPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + guessFileName);
                                downloadFileEnt.SetFileName(guessFileName);
                                downloadFileEnt.SetReferenceId(String.valueOf(enqueue));
                                downloadFileEnt.SetStatus(Common.DownloadStatus.InProgress.ordinal());
                                downloadFileEnt.SetDownloadFileUrl(str);
                                DownloadFileDAL downloadFileDAL = new DownloadFileDAL(activity);
                                downloadFileDAL.OpenWrite();
                                downloadFileDAL.AddDownloadFile(downloadFileEnt);
                                downloadFileDAL.close();
                            }
                        }
                    }.start();
                    Toast.makeText(activity, (int) R.string.download_pending, 0).show();
                } else if (!TextUtils.isEmpty(webAddress2)) {
                    new FetchUrlMimeType(activity, request, webAddress2, cookie, str2).start();
                }
            } catch (IllegalArgumentException unused) {
                Toast.makeText(activity, (int) R.string.cannot_download, 0).show();
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Exception while trying to parse url '" + str + '\'', e);
        }
    }
}
