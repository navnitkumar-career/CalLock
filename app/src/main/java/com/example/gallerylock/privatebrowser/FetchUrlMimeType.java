package com.example.gallerylock.privatebrowser;

import android.app.DownloadManager;
import android.content.Context;
import android.widget.Toast;

import com.example.gallerylock.R;

/* loaded from: classes2.dex */
public class FetchUrlMimeType extends Thread {
    private final Context mContext;
    private final String mCookies;
    private final DownloadManager.Request mRequest;
    private final String mUri;
    private final String mUserAgent;

    public FetchUrlMimeType(Context context, DownloadManager.Request request, String str, String str2, String str3) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mRequest = request;
        this.mUri = str;
        this.mCookies = str2;
        this.mUserAgent = str3;
        Toast.makeText(applicationContext, (int) R.string.download_pending, 0).show();
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x007f  */
    @Override // java.lang.Thread, java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void run() {
        /*
            r6 = this;
            r0 = 0
            java.net.URL r1 = new java.net.URL     // Catch: java.lang.Throwable -> L62 java.lang.Throwable -> L67
            java.lang.String r2 = r6.mUri     // Catch: java.lang.Throwable -> L62 java.lang.Throwable -> L67
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L62 java.lang.Throwable -> L67
            java.net.URLConnection r1 = r1.openConnection()     // Catch: java.lang.Throwable -> L62 java.lang.Throwable -> L67
            java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch: java.lang.Throwable -> L62 java.lang.Throwable -> L67
            java.lang.String r2 = r6.mCookies     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            if (r2 == 0) goto L28
            java.lang.String r2 = r6.mCookies     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            int r2 = r2.length()     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            if (r2 <= 0) goto L28
            java.lang.String r2 = "Cookie"
            java.lang.String r3 = r6.mCookies     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            r1.addRequestProperty(r2, r3)     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            java.lang.String r2 = "User-Agent"
            java.lang.String r3 = r6.mUserAgent     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            r1.setRequestProperty(r2, r3)     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
        L28:
            r1.connect()     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            int r2 = r1.getResponseCode()     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            r3 = 200(0xc8, float:2.8E-43)
            if (r2 != r3) goto L59
            java.lang.String r2 = "Content-Type"
            java.lang.String r2 = r1.getHeaderField(r2)     // Catch: java.lang.Throwable -> L60 java.lang.Throwable -> L6f
            if (r2 == 0) goto L4a
            r3 = 59
            int r3 = r2.indexOf(r3)     // Catch: java.lang.Throwable -> L57 java.lang.Throwable -> L6f
            r4 = -1
            if (r3 == r4) goto L4b
            r4 = 0
            java.lang.String r2 = r2.substring(r4, r3)     // Catch: java.lang.Throwable -> L57 java.lang.Throwable -> L6f
            goto L4b
        L4a:
            r2 = r0
        L4b:
            java.lang.String r3 = "Content-Disposition"
            java.lang.String r3 = r1.getHeaderField(r3)     // Catch: java.lang.Throwable -> L57 java.lang.Throwable -> L6f
            if (r3 == 0) goto L54
            goto L55
        L54:
            r3 = r0
        L55:
            r0 = r2
            goto L5a
        L57:
            goto L69
        L59:
            r3 = r0
        L5a:
            if (r1 == 0) goto L7d
            r1.disconnect()
            goto L7d
        L60:
            r2 = r0
            goto L69
        L62:
            r1 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
            goto L70
        L67:
            r1 = r0
            r2 = r1
        L69:
            if (r1 == 0) goto L76
            r1.disconnect()     // Catch: java.lang.Throwable -> L6f
            goto L76
        L6f:
            r0 = move-exception
        L70:
            if (r1 == 0) goto L75
            r1.disconnect()
        L75:
            throw r0
        L76:
            if (r1 == 0) goto L7b
            r1.disconnect()
        L7b:
            r3 = r0
            r0 = r2
        L7d:
            if (r0 == 0) goto Lb1
            java.lang.String r1 = "text/plain"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 != 0) goto L8f
            java.lang.String r1 = "application/octet-stream"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto La4
        L8f:
            android.webkit.MimeTypeMap r1 = android.webkit.MimeTypeMap.getSingleton()
            java.lang.String r2 = r6.mUri
            java.lang.String r2 = android.webkit.MimeTypeMap.getFileExtensionFromUrl(r2)
            java.lang.String r1 = r1.getMimeTypeFromExtension(r2)
            if (r1 == 0) goto La4
            android.app.DownloadManager$Request r2 = r6.mRequest
            r2.setMimeType(r1)
        La4:
            java.lang.String r1 = r6.mUri
            java.lang.String r0 = android.webkit.URLUtil.guessFileName(r1, r3, r0)
            android.app.DownloadManager$Request r1 = r6.mRequest
            java.lang.String r2 = android.os.Environment.DIRECTORY_DOWNLOADS
            r1.setDestinationInExternalPublicDir(r2, r0)
        Lb1:
            android.content.Context r0 = r6.mContext
            java.lang.String r1 = "download"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.app.DownloadManager r0 = (android.app.DownloadManager) r0
            android.app.DownloadManager$Request r1 = r6.mRequest
            r0.enqueue(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.newsoftwares.hidepicturesvideos.privatebrowser.FetchUrlMimeType.run():void");
    }
}
