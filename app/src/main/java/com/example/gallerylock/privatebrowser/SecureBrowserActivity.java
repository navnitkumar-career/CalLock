package com.example.gallerylock.privatebrowser;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.rey.material.app.Dialog;

import org.apache.http.protocol.HTTP;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/* loaded from: classes2.dex */
public class SecureBrowserActivity extends BaseActivity {
    private static final int Download = 1;
    static final int FILECHOOSER_RESULTCODE = 1;
    static final int INPUT_FILE_REQUEST_CODE = 2;
    static List<String> autocompleted;
    private BrowserHistoryDAL browserHistoryDAL;
    private ClipboardManager clipboard;
    public Context con;
    private Dialog dialogDownload;
    private Dialog dialogUrl;
    private Dialog dialogUrlRecent;
    private long downloadReference;
    private boolean isClearChache;
    private boolean isClearCookies;
    private boolean isClearHistory;
    private boolean isSaveFormData;
    private ImageView iv_settings;
    private LinearLayout ll_Bottom;
    LinearLayout.LayoutParams ll_Hide_Params;
    LinearLayout.LayoutParams ll_Show_Params;
    LinearLayout ll_background;
    private String mCameraPhotoPath;
    private ValueCallback<Uri[]> mFilePathCallback;
    MenuItem mItemClrCacheOnExit;
    MenuItem mItemClrCookiesOnExit;
    MenuItem mItemClrHistoryOnExit;
    MenuItem mItemSaveFormData;
    private AnimatedProgressBar mProgressBar;
    private SearchAdapter mSearchAdapter;
    private ValueCallback<Uri> mUploadMessage;
    SearchClass search;
    private WebView secureBrowser;
    SecureBrowserSharedPreferences secureBrowserSharedPreferences;
    private AutoCompleteTextView txturl;
    private Boolean isLoading = false;
    private Boolean isvisible = true;
    private List<String> urlList = null;
    private int isDownloadedFileItem = 0;
    private boolean isDownloadedFile = false;
    private Uri mCapturedImageURI = null;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_secure_browser);
        getWindow().addFlags(128);
        this.con = this;
        SecurityLocksCommon.IsAppDeactive = true;
        Common.CurrentActivity = this;
        this.ll_Bottom = (LinearLayout) findViewById(R.id.ll_Bottom);
        this.secureBrowser = (WebView) findViewById(R.id.webviewsecurebrowser);
        this.iv_settings = (ImageView) findViewById(R.id.iv_settings);
        this.txturl = (AutoCompleteTextView) findViewById(R.id.txturl);
        this.mProgressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);
        this.clipboard = (ClipboardManager) getSystemService("clipboard");
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-1, 0);
        this.dialogUrl = new Dialog(this, R.style.FullHeightDialog);
        this.dialogUrlRecent = new Dialog(this, R.style.FullHeightDialog);
        this.dialogDownload = new Dialog(this, R.style.FullHeightDialog);
        this.browserHistoryDAL = new BrowserHistoryDAL(this);
        SecureBrowserSharedPreferences GetObject = SecureBrowserSharedPreferences.GetObject(this);
        this.secureBrowserSharedPreferences = GetObject;
        this.isClearChache = GetObject.getClearCache().booleanValue();
        this.isClearHistory = this.secureBrowserSharedPreferences.getClearHistory().booleanValue();
        this.isClearCookies = this.secureBrowserSharedPreferences.getClearCookies().booleanValue();
        this.isSaveFormData = this.secureBrowserSharedPreferences.getSaveFormData().booleanValue();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367043, autocompleted);
        this.dialogUrl.setContentView(R.layout.customurldialog);
        this.dialogDownload.setContentView(R.layout.customurldialog);
        this.dialogUrlRecent.setContentView(R.layout.customurldialog);
        this.txturl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_refresh, 0);
        registerForContextMenu(this.secureBrowser);
        WebSettings settings = this.secureBrowser.getSettings();
        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        if (this.isSaveFormData) {
            if (Build.VERSION.SDK_INT < 18) {
                settings.setSavePassword(true);
            }
            settings.setSaveFormData(true);
        } else {
            if (Build.VERSION.SDK_INT < 18) {
                settings.setSavePassword(false);
            }
            settings.setSaveFormData(false);
        }
        this.secureBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        this.secureBrowser.setClickable(true);
        this.secureBrowser.setInitialScale(1);
        this.secureBrowser.setLongClickable(true);
        this.secureBrowser.requestFocus(163);
        this.secureBrowser.setWebViewClient(new MyBrowser());
        this.secureBrowser.setWebChromeClient(new MyWebChromeClient());
        registerForContextMenu(this.secureBrowser);
        if (Build.VERSION.SDK_INT >= 5) {
            try {
                WebSettings.class.getMethod("setDomStorageEnabled", Boolean.TYPE).invoke(settings, Boolean.TRUE);
                WebSettings.class.getMethod("setDatabaseEnabled", Boolean.TYPE).invoke(settings, Boolean.TRUE);
                Method method = WebSettings.class.getMethod("setDatabasePath", String.class);
                method.invoke(settings, "/data/data/" + getPackageName() + "/databases/");
                WebSettings.class.getMethod("setAppCacheMaxSize", Long.TYPE).invoke(settings, 8388608);
                Method method2 = WebSettings.class.getMethod("setAppCachePath", String.class);
                method2.invoke(settings, "/data/data/" + getPackageName() + "/cache/");
                WebSettings.class.getMethod("setAppCacheEnabled", Boolean.TYPE).invoke(settings, Boolean.TRUE);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            }
        }
        this.browserHistoryDAL.OpenRead();
        autocompleted = this.browserHistoryDAL.GetBrowserAutoCompletedHistories();
        this.browserHistoryDAL.close();
        this.search = new SearchClass();
        this.txturl.setAdapter(arrayAdapter);
        this.txturl.setDropDownBackgroundResource(R.color.White);
        AutoCompleteTextView autoCompleteTextView = this.txturl;
        SearchClass searchClass = this.search;
        searchClass.getClass();
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!(motionEvent.getX() > ((float) ((SecureBrowserActivity.this.txturl.getWidth() - SecureBrowserActivity.this.txturl.getPaddingRight()) - SecureBrowserActivity.this.txturl.getCompoundDrawables()[2].getIntrinsicWidth())))) {
                    return false;
                }
                if (motionEvent.getAction() == 1) {
                    SecureBrowserActivity.this.refreshOrStop();
                }
                return true;
            }
        });
        AutoCompleteTextView autoCompleteTextView2 = this.txturl;
        SearchClass searchClass2 = this.search;
        searchClass2.getClass();
        autoCompleteTextView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (((keyEvent == null || keyEvent.getKeyCode() != 66) && i != 6) || SecureBrowserActivity.this.txturl.getText().toString().length() <= 0) {
                    return false;
                }
                SecureBrowserActivity.this.browserHistoryDAL.OpenWrite();
                if (SecureBrowserActivity.this.txturl.getText().toString().contains(Constants.HTTP) || SecureBrowserActivity.this.txturl.getText().toString().contains(Constants.HTTPS)) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl(SecureBrowserActivity.this.txturl.getText().toString());
                    SecureBrowserActivity.this.txturl.setText(SecureBrowserActivity.this.txturl.getText().toString());
                    SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(SecureBrowserActivity.this.txturl.getText().toString());
                } else {
                    String str = Constants.HTTP + SecureBrowserActivity.this.txturl.getText().toString();
                    SecureBrowserActivity.this.secureBrowser.loadUrl(str);
                    SecureBrowserActivity.this.txturl.setText(str);
                    SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(str);
                }
                SecureBrowserActivity.this.browserHistoryDAL.close();
                return false;
            }
        });
        new Thread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.1
            @Override // java.lang.Runnable
            public void run() {
                SecureBrowserActivity secureBrowserActivity = SecureBrowserActivity.this;
                secureBrowserActivity.initializeSearchSuggestions(secureBrowserActivity.txturl);
            }
        }).run();
        this.secureBrowser.loadUrl(Common.LastWebBrowserUrl);
    }

    /* loaded from: classes2.dex */
    private class SearchClass {
        private SearchClass() {
        }

        /* loaded from: classes2.dex */
        public class EditorActionListener implements TextView.OnEditorActionListener {
            public EditorActionListener() {
            }

            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (((keyEvent == null || keyEvent.getKeyCode() != 66) && i != 6) || SecureBrowserActivity.this.txturl.getText().toString().length() <= 0) {
                    return false;
                }
                SecureBrowserActivity.this.browserHistoryDAL.OpenWrite();
                if (SecureBrowserActivity.this.txturl.getText().toString().contains(Constants.HTTP) || SecureBrowserActivity.this.txturl.getText().toString().contains(Constants.HTTPS)) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl(SecureBrowserActivity.this.txturl.getText().toString());
                    SecureBrowserActivity.this.txturl.setText(SecureBrowserActivity.this.txturl.getText().toString());
                    SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(SecureBrowserActivity.this.txturl.getText().toString());
                } else {
                    String str = Constants.HTTP + SecureBrowserActivity.this.txturl.getText().toString();
                    SecureBrowserActivity.this.secureBrowser.loadUrl(str);
                    SecureBrowserActivity.this.txturl.setText(str);
                    SecureBrowserActivity.this.browserHistoryDAL.AddBrowserHistory(str);
                }
                SecureBrowserActivity.this.browserHistoryDAL.close();
                return false;
            }
        }

        /* loaded from: classes2.dex */
        public class TouchListener implements View.OnTouchListener {
            public TouchListener() {
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!(motionEvent.getX() > ((float) ((SecureBrowserActivity.this.txturl.getWidth() - SecureBrowserActivity.this.txturl.getPaddingRight()) - SecureBrowserActivity.this.txturl.getCompoundDrawables()[2].getIntrinsicWidth())))) {
                    return false;
                }
                if (motionEvent.getAction() == 1) {
                    SecureBrowserActivity.this.refreshOrStop();
                }
                return true;
            }
        }

        /* loaded from: classes2.dex */
        public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            public MenuItemClickListener() {
            }

            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId != R.id.save_forms_data) {
                    switch (itemId) {
                        case R.id.clear_chache /* 2131296437 */:
                            SecureBrowserActivity.this.clearCache();
                            Toast.makeText(SecureBrowserActivity.this, "Cache cleared!", 0).show();
                            break;
                        case R.id.clear_chache_on_exit /* 2131296438 */:
                            SecureBrowserActivity.this.mItemClrCacheOnExit.setChecked(!SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked());
                            SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearCache(Boolean.valueOf(SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked()));
                            SecureBrowserActivity.this.isClearChache = SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked();
                            break;
                        case R.id.clear_cookies /* 2131296439 */:
                            SecureBrowserActivity.this.clearCookies();
                            Toast.makeText(SecureBrowserActivity.this, "Cookies cleared!", 0).show();
                            break;
                        case R.id.clear_cookies_on_exit /* 2131296440 */:
                            SecureBrowserActivity.this.mItemClrCookiesOnExit.setChecked(!SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked());
                            SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearCookies(Boolean.valueOf(SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked()));
                            SecureBrowserActivity.this.isClearCookies = SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked();
                            break;
                        case R.id.clear_history /* 2131296441 */:
                            SecureBrowserActivity.this.clearHistory();
                            Toast.makeText(SecureBrowserActivity.this, "History cleared!", 0).show();
                            break;
                        case R.id.clear_history_on_exit /* 2131296442 */:
                            SecureBrowserActivity.this.mItemClrHistoryOnExit.setChecked(!SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked());
                            SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearHistory(Boolean.valueOf(SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked()));
                            SecureBrowserActivity.this.isClearHistory = SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked();
                            break;
                    }
                } else {
                    SecureBrowserActivity.this.mItemSaveFormData.setChecked(!SecureBrowserActivity.this.mItemSaveFormData.isChecked());
                    SecureBrowserActivity.this.secureBrowserSharedPreferences.setSaveFormData(Boolean.valueOf(SecureBrowserActivity.this.mItemSaveFormData.isChecked()));
                    SecureBrowserActivity.this.isSaveFormData = SecureBrowserActivity.this.mItemSaveFormData.isChecked();
                }
                return false;
            }
        }
    }

    /* loaded from: classes2.dex */
    private class MyBrowser extends WebViewClient implements DownloadListener {
        private MyBrowser() {
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
            SecureBrowserActivity.this.txturl.setText(str);
            SecureBrowserActivity.this.setIsLoading();
        }

        /* JADX WARN: Removed duplicated region for block: B:37:0x00cf A[Catch: Exception -> 0x00dc, TRY_LEAVE, TryCatch #0 {Exception -> 0x00dc, blocks: (B:3:0x0001, B:5:0x0007, B:7:0x0011, B:9:0x001d, B:11:0x0029, B:13:0x0035, B:15:0x0041, B:17:0x004d, B:19:0x0059, B:22:0x0066, B:24:0x0072, B:27:0x007f, B:29:0x008b, B:31:0x0097, B:33:0x00a3, B:34:0x00ad, B:37:0x00cf), top: B:42:0x0001 }] */
        @Override // android.webkit.WebViewClient
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean shouldOverrideUrlLoading(WebView r6, String r7) {
            /*
                r5 = this;
                r0 = 1
                java.lang.String r1 = android.webkit.MimeTypeMap.getFileExtensionFromUrl(r7)     // Catch: java.lang.Exception -> Ldc
                if (r1 == 0) goto Le0
                android.webkit.MimeTypeMap r2 = android.webkit.MimeTypeMap.getSingleton()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r2 = r2.getMimeTypeFromExtension(r1)     // Catch: java.lang.Exception -> Ldc
                if (r2 == 0) goto Lcc
                java.lang.String r2 = r2.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "video"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lad
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "mov"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lad
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "avi"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lad
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "flv"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lad
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "mkv"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lad
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "wmv"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lad
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "mp4"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 == 0) goto L66
                goto Lad
            L66:
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "mp3"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lcc
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "wav"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 == 0) goto L7f
                goto Lcc
            L7f:
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = ".jpg"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lcc
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = ".png"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lcc
                java.lang.String r2 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = ".gif"
                boolean r2 = r2.contains(r3)     // Catch: java.lang.Exception -> Ldc
                if (r2 != 0) goto Lcc
                java.lang.String r1 = r1.toLowerCase()     // Catch: java.lang.Exception -> Ldc
                java.lang.String r2 = ".bmp"
                r1.contains(r2)     // Catch: java.lang.Exception -> Ldc
                goto Lcc
            Lad:
                r1 = 0
                java.lang.String r1 = android.webkit.URLUtil.guessFileName(r7, r1, r1)     // Catch: java.lang.Exception -> Ldc
                net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity r2 = net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.this     // Catch: java.lang.Exception -> Ldc
                net.newsoftwares.hidepicturesvideos.utilities.Common$DownloadType r3 = net.newsoftwares.hidepicturesvideos.utilities.Common.DownloadType.Video     // Catch: java.lang.Exception -> Ldc
                int r3 = r3.ordinal()     // Catch: java.lang.Exception -> Ldc
                net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.access$1102(r2, r3)     // Catch: java.lang.Exception -> Ldc
                net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity r2 = net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.this     // Catch: java.lang.Exception -> Ldc
                java.lang.String r3 = "Video"
                net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity r4 = net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.this     // Catch: java.lang.Exception -> Ldc
                int r4 = net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.access$1100(r4)     // Catch: java.lang.Exception -> Ldc
                r2.downloadFile(r3, r7, r1, r4)     // Catch: java.lang.Exception -> Ldc
                r1 = 0
                goto Lcd
            Lcc:
                r1 = 1
            Lcd:
                if (r1 == 0) goto Le0
                r6.loadUrl(r7)     // Catch: java.lang.Exception -> Ldc
                net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity r1 = net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.this     // Catch: java.lang.Exception -> Ldc
                net.newsoftwares.hidepicturesvideos.privatebrowser.BrowserHistoryDAL r1 = net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.access$500(r1)     // Catch: java.lang.Exception -> Ldc
                r1.AddBrowserHistory(r7)     // Catch: java.lang.Exception -> Ldc
                goto Le0
            Ldc:
                r7 = move-exception
                r7.printStackTrace()
            Le0:
                r6.setDownloadListener(r5)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.MyBrowser.shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String):boolean");
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            Common.LastWebBrowserUrl = str;
            SecureBrowserActivity.this.setIsFinishedLoading();
        }

        @Override // android.webkit.DownloadListener
        public void onDownloadStart(final String str, final String str2, final String str3, final String str4, long j) {
            String guessFileName = URLUtil.guessFileName(str, str3, str4);
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.MyBrowser.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        DownloadHandler.onDownloadStart(SecureBrowserActivity.this, str, str2, str3, str4, false);
                    }
                }
            };
            new AlertDialog.Builder(SecureBrowserActivity.this).setTitle(guessFileName).setMessage(SecureBrowserActivity.this.getResources().getString(R.string.dialog_download)).setPositiveButton(SecureBrowserActivity.this.getResources().getString(R.string.action_download), onClickListener).setNegativeButton(SecureBrowserActivity.this.getResources().getString(R.string.action_cancel), onClickListener).show();
        }
    }

    /* loaded from: classes2.dex */
    private class MyWebChromeClient extends WebChromeClient {
        private MyWebChromeClient() {
        }

        @Override // android.webkit.WebChromeClient
        public void onProgressChanged(WebView webView, int i) {
            SecureBrowserActivity.this.updateProgress(i);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            SecureBrowserActivity.this.mUploadMessage = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("image/*");
            SecurityLocksCommon.IsAppDeactive = false;
            SecureBrowserActivity.this.startActivityForResult(Intent.createChooser(intent, "File Chooser"), 1);
        }

        public void openFileChooser(ValueCallback valueCallback, String str) {
            SecureBrowserActivity.this.mUploadMessage = valueCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("*/*");
            SecurityLocksCommon.IsAppDeactive = false;
            SecureBrowserActivity.this.startActivityForResult(Intent.createChooser(intent, "File Browser"), 1);
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String str, String str2) {
            SecureBrowserActivity.this.mUploadMessage = valueCallback;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                SecureBrowserActivity.this.mCapturedImageURI = Uri.fromFile(new File(file + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("output", SecureBrowserActivity.this.mCapturedImageURI);
                Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
                intent2.addCategory("android.intent.category.OPENABLE");
                intent2.setType("image/*");
                Intent createChooser = Intent.createChooser(intent2, "File Chooser");
                createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", new Parcelable[]{intent});
                SecurityLocksCommon.IsAppDeactive = false;
                SecureBrowserActivity.this.startActivityForResult(createChooser, 1);
            } catch (Exception unused) {
            }
        }

        @Override // android.webkit.WebChromeClient
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            SecureBrowserActivity.this.showFileChooser(valueCallback);
            return true;
        }
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        if (view instanceof WebView) {
            WebView.HitTestResult hitTestResult = ((WebView) view).getHitTestResult();
            String extra = hitTestResult.getExtra();
            String guessFileName = URLUtil.guessFileName(extra, null, null);
            if (hitTestResult != null) {
                try {
                    int type = hitTestResult.getType();
                    if (!(type == 5 || type == 8)) {
                        if (!hitTestResult.getExtra().endsWith(".mp4") && !hitTestResult.getExtra().endsWith(".3gp") && !hitTestResult.getExtra().endsWith(".avi") && !hitTestResult.getExtra().endsWith(".flv") && !hitTestResult.getExtra().endsWith(".mkv")) {
                            if (!hitTestResult.getExtra().endsWith(".mp3") && !hitTestResult.getExtra().endsWith(".wav")) {
                                if (!hitTestResult.getExtra().endsWith(".pdf") && !hitTestResult.getExtra().endsWith(".doc") && !hitTestResult.getExtra().endsWith(".docx") && !hitTestResult.getExtra().endsWith(".ppt") && !hitTestResult.getExtra().endsWith(".pptx") && !hitTestResult.getExtra().endsWith(".xls") && !hitTestResult.getExtra().endsWith(".xlsx") && !hitTestResult.getExtra().endsWith(".csv") && !hitTestResult.getExtra().endsWith(".dbk") && !hitTestResult.getExtra().endsWith(".dot") && !hitTestResult.getExtra().endsWith(".dotx") && !hitTestResult.getExtra().endsWith(".gdoc") && !hitTestResult.getExtra().endsWith(".pdax") && !hitTestResult.getExtra().endsWith(".pda") && !hitTestResult.getExtra().endsWith(".rtf") && !hitTestResult.getExtra().endsWith(".rpt") && !hitTestResult.getExtra().endsWith(".stw") && !hitTestResult.getExtra().endsWith(".txt") && !hitTestResult.getExtra().endsWith(".uof") && !hitTestResult.getExtra().endsWith(".uoml") && !hitTestResult.getExtra().endsWith(".wps") && !hitTestResult.getExtra().endsWith(".wpt") && !hitTestResult.getExtra().endsWith(".wrd") && !hitTestResult.getExtra().endsWith(".xps") && !hitTestResult.getExtra().endsWith(".epub") && !hitTestResult.getExtra().endsWith(".xml")) {
                                    if (!hitTestResult.getExtra().endsWith(".7z") && !hitTestResult.getExtra().endsWith(".ace") && !hitTestResult.getExtra().endsWith(".bik") && !hitTestResult.getExtra().endsWith(".bin") && !hitTestResult.getExtra().endsWith(".bkf") && !hitTestResult.getExtra().endsWith(".bzip2") && !hitTestResult.getExtra().endsWith(".cab") && !hitTestResult.getExtra().endsWith(".daa") && !hitTestResult.getExtra().endsWith(".gzip") && !hitTestResult.getExtra().endsWith(".jar") && !hitTestResult.getExtra().endsWith(".apk") && !hitTestResult.getExtra().endsWith(".xap") && !hitTestResult.getExtra().endsWith(".lzip") && !hitTestResult.getExtra().endsWith(".rar") && !hitTestResult.getExtra().endsWith(".tgz") && !hitTestResult.getExtra().endsWith(".iso") && !hitTestResult.getExtra().endsWith(".img") && !hitTestResult.getExtra().endsWith(".mdx") && !hitTestResult.getExtra().endsWith(".dmg") && !hitTestResult.getExtra().endsWith(".acp") && !hitTestResult.getExtra().endsWith(".amf") && !hitTestResult.getExtra().endsWith(".4db") && !hitTestResult.getExtra().endsWith(".4dr") && !hitTestResult.getExtra().endsWith(".ave") && !hitTestResult.getExtra().endsWith(".fm") && !hitTestResult.getExtra().endsWith(".acl") && !hitTestResult.getExtra().endsWith(".ans") && !hitTestResult.getExtra().endsWith(".ots") && !hitTestResult.getExtra().endsWith(".egt") && !hitTestResult.getExtra().endsWith(".ftx") && !hitTestResult.getExtra().endsWith(".lwp") && !hitTestResult.getExtra().endsWith(".nb") && !hitTestResult.getExtra().endsWith(".nbp") && !hitTestResult.getExtra().endsWith(".odm") && !hitTestResult.getExtra().endsWith(".odt") && !hitTestResult.getExtra().endsWith(".ott") && !hitTestResult.getExtra().endsWith(".via") && !hitTestResult.getExtra().endsWith(".wps") && !hitTestResult.getExtra().endsWith(".wrf") && !hitTestResult.getExtra().endsWith(".wri") && !hitTestResult.getExtra().endsWith(".org") && !hitTestResult.getExtra().endsWith(".ahk") && !hitTestResult.getExtra().endsWith(".as") && !hitTestResult.getExtra().endsWith(".bat") && !hitTestResult.getExtra().endsWith(".bas") && !hitTestResult.getExtra().endsWith(".hta") && !hitTestResult.getExtra().endsWith(".ijs") && !hitTestResult.getExtra().endsWith(".js") && !hitTestResult.getExtra().endsWith(".ncf") && !hitTestResult.getExtra().endsWith(".nut") && !hitTestResult.getExtra().endsWith(".sdl") && !hitTestResult.getExtra().endsWith(".au") && !hitTestResult.getExtra().endsWith(".raw") && !hitTestResult.getExtra().endsWith(".pac") && !hitTestResult.getExtra().endsWith(".m4a") && !hitTestResult.getExtra().endsWith(".ab2") && !hitTestResult.getExtra().endsWith(".via") && !hitTestResult.getExtra().endsWith(".wps") && !hitTestResult.getExtra().endsWith(".wrf") && !hitTestResult.getExtra().endsWith(".wri") && !hitTestResult.getExtra().endsWith(".ab3") && !hitTestResult.getExtra().endsWith(".aws") && !hitTestResult.getExtra().endsWith(".clf") && !hitTestResult.getExtra().endsWith(".ods") && !hitTestResult.getExtra().endsWith(".vc") && !hitTestResult.getExtra().endsWith(".bak") && !hitTestResult.getExtra().endsWith(".bdf") && !hitTestResult.getExtra().endsWith(".tos") && !hitTestResult.getExtra().endsWith(".exe") && !hitTestResult.getExtra().endsWith(".msg") && !hitTestResult.getExtra().endsWith(".dtp") && !hitTestResult.getExtra().endsWith(".pub") && !hitTestResult.getExtra().endsWith(".zip")) {
                                        return;
                                    }
                                    int ordinal = Common.DownloadType.Miscellaneous.ordinal();
                                    this.isDownloadedFileItem = ordinal;
                                    downloadFile("Miscellaneous", extra, guessFileName, ordinal);
                                    return;
                                }
                                int ordinal2 = Common.DownloadType.Document.ordinal();
                                this.isDownloadedFileItem = ordinal2;
                                downloadFile("Document", extra, guessFileName, ordinal2);
                                return;
                            }
                            int ordinal3 = Common.DownloadType.Music.ordinal();
                            this.isDownloadedFileItem = ordinal3;
                            downloadFile("Music", extra, guessFileName, ordinal3);
                            return;
                        }
                        int ordinal4 = Common.DownloadType.Video.ordinal();
                        this.isDownloadedFileItem = ordinal4;
                        downloadFile("Video", extra, guessFileName, ordinal4);
                        return;
                    }
                    this.isDownloadedFileItem = Common.DownloadType.Photo.ordinal();
                    String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    downloadFile("Image", extra, "Download_" + format + ".jpg", this.isDownloadedFileItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downloadFile(String str, final String str2, final String str3, final int i) {
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), str3);
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.download_dialog);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_save_media);
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_copy_url);
        ((TextView) dialog.findViewById(R.id.tv_save_media)).setText("Save " + str);
        ((TextView) dialog.findViewById(R.id.tv_copy_url)).setText("Copy Url of " + str);
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String str4;
                Uri parse = Uri.parse(str2);
                parse.getLastPathSegment();
                try {
                    DownloadManager.Request request = new DownloadManager.Request(parse);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(1);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, str3);
                    SecureBrowserActivity.this.downloadReference = ((DownloadManager) SecureBrowserActivity.this.getSystemService("download")).enqueue(request);
                    DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
                    downloadFileEnt.SetFileDownloadPath(file.getAbsolutePath());
                    downloadFileEnt.SetFileName(str3);
                    downloadFileEnt.SetReferenceId(String.valueOf(SecureBrowserActivity.this.downloadReference));
                    downloadFileEnt.SetStatus(Common.DownloadStatus.InProgress.ordinal());
                    downloadFileEnt.SetDownloadFileUrl(str2);
                    downloadFileEnt.SetDownloadType(i);
                    DownloadFileDAL downloadFileDAL = new DownloadFileDAL(SecureBrowserActivity.this);
                    downloadFileDAL.OpenWrite();
                    downloadFileDAL.AddDownloadFile(downloadFileEnt);
                    downloadFileDAL.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (str2.contains("image")) {
                        String str5 = str2;
                        str4 = SecureBrowserActivity.this.storeImage(BitmapFactory.decodeStream(new ByteArrayInputStream(Base64.decode(str5.substring(str5.indexOf(",") + 1).getBytes(), 0))));
                    } else {
                        str4 = "";
                    }
                    dialog.dismiss();
                    DownloadFileEnt downloadFileEnt2 = new DownloadFileEnt();
                    downloadFileEnt2.SetFileDownloadPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + str4);
                    downloadFileEnt2.SetFileName(str4);
                    downloadFileEnt2.SetReferenceId(String.valueOf(SecureBrowserActivity.this.downloadReference));
                    downloadFileEnt2.SetStatus(Common.DownloadStatus.Completed.ordinal());
                    downloadFileEnt2.SetDownloadFileUrl(str2);
                    downloadFileEnt2.SetDownloadType(i);
                    DownloadFileDAL downloadFileDAL2 = new DownloadFileDAL(SecureBrowserActivity.this);
                    downloadFileDAL2.OpenWrite();
                    downloadFileDAL2.AddDownloadFile(downloadFileEnt2);
                    downloadFileDAL2.close();
                    try {
                        downloadFileDAL2.OpenRead();
                        String MovePhotoFile = downloadFileDAL2.MovePhotoFile(downloadFileEnt2.GetFileDownloadPath(), downloadFileEnt2.GetFileName());
                        if (MovePhotoFile.length() > 0) {
                            downloadFileDAL2.AddPhotoToDatabase(downloadFileEnt2.GetFileName(), MovePhotoFile);
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecureBrowserActivity.this.clipboard.setPrimaryClip(ClipData.newPlainText("url", str2));
                Toast.makeText(SecureBrowserActivity.this, "Url Copied!", 0).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public String storeImage(Bitmap bitmap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()).mkdirs();
        String str = "Download_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file.toString() + File.separator + str));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return str;
        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return "";
        } catch (IOException e2) {
            Log.w("TAG", "Error saving image file: " + e2.getMessage());
            return "";
        }
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return File.createTempFile("JPEG_" + format + "_", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void showFileChooser(ValueCallback<Uri[]> r5) {
        /*
            r4 = this;
            android.webkit.ValueCallback<android.net.Uri[]> r0 = r4.mFilePathCallback
            r1 = 0
            if (r0 == 0) goto L8
            r0.onReceiveValue(r1)
        L8:
            r4.mFilePathCallback = r5
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r0 = "android.media.action.IMAGE_CAPTURE"
            r5.<init>(r0)
            android.content.pm.PackageManager r0 = r4.getPackageManager()
            android.content.ComponentName r0 = r5.resolveActivity(r0)
            if (r0 == 0) goto L4c
            java.io.File r0 = r4.createImageFile()     // Catch: java.io.IOException -> L29
            java.lang.String r2 = "PhotoPath"
            java.lang.String r3 = r4.mCameraPhotoPath     // Catch: java.io.IOException -> L27
            r5.putExtra(r2, r3)     // Catch: java.io.IOException -> L27
            goto L2a
        L27:
            goto L2a
        L29:
            r0 = r1
        L2a:
            if (r0 == 0) goto L4d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "file:"
            r1.append(r2)
            java.lang.String r2 = r0.getAbsolutePath()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r4.mCameraPhotoPath = r1
            android.net.Uri r0 = android.net.Uri.fromFile(r0)
            java.lang.String r1 = "output"
            r5.putExtra(r1, r0)
        L4c:
            r1 = r5
        L4d:
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r0 = "android.intent.action.GET_CONTENT"
            r5.<init>(r0)
            java.lang.String r0 = "android.intent.category.OPENABLE"
            r5.addCategory(r0)
            java.lang.String r0 = "image/*"
            r5.setType(r0)
            r0 = 0
            if (r1 == 0) goto L67
            r2 = 1
            android.content.Intent[] r2 = new android.content.Intent[r2]
            r2[r0] = r1
            goto L69
        L67:
            android.content.Intent[] r2 = new android.content.Intent[r0]
        L69:
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r3 = "android.intent.action.CHOOSER"
            r1.<init>(r3)
            java.lang.String r3 = "android.intent.extra.INTENT"
            r1.putExtra(r3, r5)
            java.lang.String r5 = "android.intent.extra.TITLE"
            java.lang.String r3 = "File Chooser"
            r1.putExtra(r5, r3)
            java.lang.String r5 = "android.intent.extra.INITIAL_INTENTS"
            r1.putExtra(r5, r2)
            net.newsoftwares.hidepicturesvideos.securitylocks.SecurityLocksCommon.IsAppDeactive = r0
            r5 = 2
            r4.startActivityForResult(r1, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.showFileChooser(android.webkit.ValueCallback):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri[] uriArr;
        Uri data = null;
        SecurityLocksCommon.IsAppDeactive = true;
        if (i == 1) {
            if (this.mUploadMessage != null) {
                if (i2 == -1) {
                    try {
                        data = intent == null ? this.mCapturedImageURI : intent.getData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.mUploadMessage.onReceiveValue(data);
                    this.mUploadMessage = null;
                }
                data = null;
                this.mUploadMessage.onReceiveValue(data);
                this.mUploadMessage = null;
            }
        } else if (i != 2 || this.mFilePathCallback == null) {
            super.onActivityResult(i, i2, intent);
        } else {
            if (i2 == -1) {
                if (intent == null) {
                    String str = this.mCameraPhotoPath;
                    if (str != null) {
                        uriArr = new Uri[]{Uri.parse(str)};
                        this.mFilePathCallback.onReceiveValue(uriArr);
                        this.mFilePathCallback = null;
                    }
                } else {
                    String dataString = intent.getDataString();
                    if (dataString != null) {
                        uriArr = new Uri[]{Uri.parse(dataString)};
                        this.mFilePathCallback.onReceiveValue(uriArr);
                        this.mFilePathCallback = null;
                    }
                }
            }
            uriArr = null;
            this.mFilePathCallback.onReceiveValue(uriArr);
            this.mFilePathCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeSearchSuggestions(final AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setDropDownWidth(-1);
        autoCompleteTextView.setDropDownAnchor(R.id.ll_sb_top_baar);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                try {
                    String charSequence = ((TextView) view.findViewById(R.id.url)).getText().toString();
                    if (charSequence.startsWith(SecureBrowserActivity.this.getString(R.string.suggestion))) {
                        charSequence = ((TextView) view.findViewById(R.id.title)).getText().toString();
                    } else {
                        autoCompleteTextView.setText(charSequence);
                    }
                    SecureBrowserActivity.this.searchTheWeb(charSequence);
                    ((InputMethodManager) SecureBrowserActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                    if (SecureBrowserActivity.this.secureBrowser != null) {
                        SecureBrowserActivity.this.secureBrowser.requestFocus();
                    }
                } catch (NullPointerException unused) {
                    Log.e("Browser Error: ", "NullPointerException on item click");
                }
            }
        });
        autoCompleteTextView.setSelectAllOnFocus(true);
        SearchAdapter searchAdapter = new SearchAdapter(this);
        this.mSearchAdapter = searchAdapter;
        autoCompleteTextView.setAdapter(searchAdapter);
    }

    void searchTheWeb(String str) {
        if (!str.equals("")) {
            String trim = str.trim();
            this.secureBrowser.stopLoading();
            if (trim.startsWith("www.")) {
                trim = Constants.HTTP + trim;
            } else if (trim.startsWith("ftp.")) {
                trim = "ftp://" + trim;
            }
            boolean contains = trim.contains(".");
            boolean z = true;
            boolean z2 = TextUtils.isDigitsOnly(trim.replace(".", "")) && trim.replace(".", "").length() >= 4 && trim.contains(".");
            boolean contains2 = trim.contains("about:");
            boolean z3 = trim.startsWith("ftp://") || trim.startsWith(Constants.HTTP) || trim.startsWith(Constants.FILE) || trim.startsWith(Constants.HTTPS) || z2;
            if ((!trim.contains(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR) && contains) || contains2) {
                z = false;
            }
            if (z2 && (!trim.startsWith(Constants.HTTP) || !trim.startsWith(Constants.HTTPS))) {
                trim = Constants.HTTP + trim;
            }
            if (z) {
                try {
                    trim = URLEncoder.encode(trim, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                this.secureBrowser.loadUrl("http://www.google.com/search?q=" + trim);
            } else if (!z3) {
                this.secureBrowser.loadUrl(Constants.HTTP + trim);
            } else {
                this.secureBrowser.loadUrl(trim);
            }
        }
    }

    public void setIsFinishedLoading() {
        this.txturl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_refresh, 0);
    }

    public void setIsLoading() {
        this.txturl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_delete, 0);
    }

    public void refreshOrStop() {
        WebView webView = this.secureBrowser;
        if (webView == null) {
            return;
        }
        if (webView.getProgress() < 100) {
            this.secureBrowser.stopLoading();
        } else {
            this.secureBrowser.reload();
        }
    }

    public void updateProgress(int i) {
        if (i >= 100) {
            setIsFinishedLoading();
        } else {
            setIsLoading();
        }
        this.mProgressBar.setProgress(i);
    }

    public void btnRefreshStop(View view) {
        if (this.isLoading.booleanValue()) {
            this.secureBrowser.stopLoading();
        } else {
            this.secureBrowser.reload();
        }
    }

    public void btnDropDown(View view) {
        if (this.isvisible.booleanValue()) {
            this.isvisible = false;
            this.ll_Bottom.setLayoutParams(this.ll_Hide_Params);
            this.ll_Bottom.setVisibility(4);
            return;
        }
        this.isvisible = true;
        this.ll_Bottom.setLayoutParams(this.ll_Show_Params);
        this.ll_Bottom.setVisibility(0);
    }

    public void BtnBack(View view) {
        if (this.secureBrowser.canGoBack()) {
            this.secureBrowser.goBack();
        }
    }

    public void BtnForward(View view) {
        if (this.secureBrowser.canGoForward()) {
            this.secureBrowser.goForward();
        }
    }

    public void BtnAddBookMark(View view) {
        BookmarkDAL bookmarkDAL = new BookmarkDAL(this);
        bookmarkDAL.OpenWrite();
        try {
            if (bookmarkDAL.AddBookmark(this.txturl.getText().toString()).booleanValue()) {
                Toast.makeText(this, "Added to bookmark list", 0).show();
            } else {
                Toast.makeText(this, "Already exist this bookmark", 0).show();
            }
            bookmarkDAL.close();
        } catch (Exception unused) {
            bookmarkDAL.close();
        }
    }

    public void BtnBookMarkHistory(View view) {
        ListView listView = (ListView) this.dialogUrl.findViewById(R.id.listViewhistory);
        BookmarkDAL bookmarkDAL = new BookmarkDAL(this);
        bookmarkDAL.OpenRead();
        this.urlList = bookmarkDAL.GetUrlBookmarks();
        bookmarkDAL.close();
        listView.setAdapter((ListAdapter) new UrlAdapter(this, 17367043, this.urlList, Common.BrowserMenuType.Bookmark.ordinal()));
        ((TextView) this.dialogUrl.findViewById(R.id.lblurlhistoty)).setText("Bookmark List");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                if (SecureBrowserActivity.this.urlList != null) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl((String) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.txturl.setText((CharSequence) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.dialogUrl.dismiss();
                }
            }
        });
        ((Button) this.dialogUrl.findViewById(R.id.btnclearbrowser)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final Dialog dialog = new Dialog(SecureBrowserActivity.this, R.style.FullHeightDialog);
                dialog.setCancelable(true);
                dialog.setTitle(R.string.del_bookmarks);
                dialog.positiveAction("Yes").negativeAction("No");
                dialog.positiveActionClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.6.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                        new BookmarkDAL(SecureBrowserActivity.this).DeleteBookmarks();
                        SecureBrowserActivity.this.dialogUrl.dismiss();
                        Toast.makeText(SecureBrowserActivity.this, "Bookmark(s) cleared", 0).show();
                    }
                });
                dialog.negativeActionClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.6.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        List<String> list = this.urlList;
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "No Bookmark(s)", 0).show();
        } else {
            this.dialogUrl.show();
        }
    }

    public void BtnHistoryClean(View view) {
        ListView listView = (ListView) this.dialogUrlRecent.findViewById(R.id.listViewhistory);
        BrowserHistoryDAL browserHistoryDAL = new BrowserHistoryDAL(this);
        browserHistoryDAL.OpenRead();
        this.urlList = browserHistoryDAL.GetBrowserUrlHistories();
        browserHistoryDAL.close();
        listView.setAdapter((ListAdapter) new UrlAdapter(this, 17367043, this.urlList, Common.BrowserMenuType.History.ordinal()));
        ((TextView) this.dialogUrlRecent.findViewById(R.id.lblurlhistoty)).setText("History List");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.7
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                if (SecureBrowserActivity.this.urlList != null) {
                    SecureBrowserActivity.this.secureBrowser.loadUrl((String) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.txturl.setText((CharSequence) SecureBrowserActivity.this.urlList.get(i));
                    SecureBrowserActivity.this.dialogUrlRecent.dismiss();
                }
            }
        });
        ((Button) this.dialogUrlRecent.findViewById(R.id.btnclearbrowser)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final Dialog dialog = new Dialog(SecureBrowserActivity.this, R.style.FullHeightDialog);
                dialog.setCancelable(true);
                dialog.setTitle(R.string.del_history);
                dialog.positiveAction("Yes").negativeAction("No");
                dialog.positiveActionClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.8.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                        SecureBrowserActivity.this.browserHistoryDAL.OpenWrite();
                        SecureBrowserActivity.this.browserHistoryDAL.DeleteHistories();
                        SecureBrowserActivity.this.browserHistoryDAL.close();
                        SecureBrowserActivity.this.dialogUrlRecent.dismiss();
                        Toast.makeText(SecureBrowserActivity.this, "History deleted", 0).show();
                    }
                });
                dialog.negativeActionClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.8.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        List<String> list = this.urlList;
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "No History", 0).show();
        } else {
            this.dialogUrlRecent.show();
        }
    }

    public void BtnDownloadHistory(View view) {
        ListView listView = (ListView) this.dialogDownload.findViewById(R.id.listViewhistory);
        DownloadFileDAL downloadFileDAL = new DownloadFileDAL(this);
        downloadFileDAL.OpenRead();
        this.urlList = downloadFileDAL.GetDownloadFileName();
        downloadFileDAL.close();
        listView.setAdapter((ListAdapter) new UrlAdapter(this, 17367043, this.urlList, Common.BrowserMenuType.Download.ordinal()));
        ((TextView) this.dialogDownload.findViewById(R.id.lblurlhistoty)).setText("Download List");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.9
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
            }
        });
        ((Button) this.dialogDownload.findViewById(R.id.btnclearbrowser)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final Dialog dialog = new Dialog(SecureBrowserActivity.this, R.style.FullHeightDialog);
                dialog.setCancelable(true);
                dialog.setTitle(R.string.del_download_history);
                dialog.positiveAction("Yes").negativeAction("No");
                dialog.positiveActionClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.10.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                        new DownloadFileDAL(SecureBrowserActivity.this).DeleteDownloadFile();
                        SecureBrowserActivity.this.dialogDownload.dismiss();
                        Toast.makeText(SecureBrowserActivity.this, "Download history cleared", 0).show();
                    }
                });
                dialog.negativeActionClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.SecureBrowserActivity.10.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                    }
                });
            }
        });
        List<String> list = this.urlList;
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "No Downloads", 0).show();
        } else {
            this.dialogDownload.show();
        }
    }

    public void btnBrowserExit(View view) {
        BrowserHistoryDAL browserHistoryDAL = this.browserHistoryDAL;
        if (browserHistoryDAL != null) {
            browserHistoryDAL.close();
        }
        Common.IsWebBrowserActive = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(getApplicationContext(), Common.CurrentWebBrowserActivity.getClass()));
        finish();
        overridePendingTransition(17432576, 17432577);
    }

    public void settingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, this.iv_settings);
        SearchClass searchClass = this.search;
        searchClass.getClass();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId != R.id.save_forms_data) {
                    switch (itemId) {
                        case R.id.clear_chache /* 2131296437 */:
                            SecureBrowserActivity.this.clearCache();
                            Toast.makeText(SecureBrowserActivity.this, "Cache cleared!", 0).show();
                            break;
                        case R.id.clear_chache_on_exit /* 2131296438 */:
                            SecureBrowserActivity.this.mItemClrCacheOnExit.setChecked(!SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked());
                            SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearCache(Boolean.valueOf(SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked()));
                            SecureBrowserActivity.this.isClearChache = SecureBrowserActivity.this.mItemClrCacheOnExit.isChecked();
                            break;
                        case R.id.clear_cookies /* 2131296439 */:
                            SecureBrowserActivity.this.clearCookies();
                            Toast.makeText(SecureBrowserActivity.this, "Cookies cleared!", 0).show();
                            break;
                        case R.id.clear_cookies_on_exit /* 2131296440 */:
                            SecureBrowserActivity.this.mItemClrCookiesOnExit.setChecked(!SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked());
                            SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearCookies(Boolean.valueOf(SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked()));
                            SecureBrowserActivity.this.isClearCookies = SecureBrowserActivity.this.mItemClrCookiesOnExit.isChecked();
                            break;
                        case R.id.clear_history /* 2131296441 */:
                            SecureBrowserActivity.this.clearHistory();
                            Toast.makeText(SecureBrowserActivity.this, "History cleared!", 0).show();
                            break;
                        case R.id.clear_history_on_exit /* 2131296442 */:
                            SecureBrowserActivity.this.mItemClrHistoryOnExit.setChecked(!SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked());
                            SecureBrowserActivity.this.secureBrowserSharedPreferences.setClearHistory(Boolean.valueOf(SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked()));
                            SecureBrowserActivity.this.isClearHistory = SecureBrowserActivity.this.mItemClrHistoryOnExit.isChecked();
                            break;
                    }
                } else {
                    SecureBrowserActivity.this.mItemSaveFormData.setChecked(!SecureBrowserActivity.this.mItemSaveFormData.isChecked());
                    SecureBrowserActivity.this.secureBrowserSharedPreferences.setSaveFormData(Boolean.valueOf(SecureBrowserActivity.this.mItemSaveFormData.isChecked()));
                    SecureBrowserActivity.this.isSaveFormData = SecureBrowserActivity.this.mItemSaveFormData.isChecked();
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.popup_menu);
        this.mItemClrCacheOnExit = popupMenu.getMenu().getItem(0);
        this.mItemClrHistoryOnExit = popupMenu.getMenu().getItem(1);
        this.mItemClrCookiesOnExit = popupMenu.getMenu().getItem(2);
        this.mItemSaveFormData = popupMenu.getMenu().getItem(3);
        this.mItemClrCacheOnExit.setChecked(this.isClearChache);
        this.mItemClrHistoryOnExit.setChecked(this.isClearHistory);
        this.mItemClrCookiesOnExit.setChecked(this.isClearCookies);
        this.mItemSaveFormData.setChecked(this.isSaveFormData);
        popupMenu.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        Common.LastWebBrowserUrl = this.txturl.getText().toString();
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        if (this.secureBrowser.canGoBack()) {
            this.secureBrowser.goBack();
            WebBackForwardList copyBackForwardList = this.secureBrowser.copyBackForwardList();
            if (copyBackForwardList.getSize() > 0 && copyBackForwardList.getCurrentIndex() != 0) {
                String url = copyBackForwardList.getItemAtIndex(copyBackForwardList.getCurrentIndex() - 1).getUrl();
                this.txturl.setText(url);
                this.browserHistoryDAL.OpenWrite();
                this.browserHistoryDAL.AddBrowserHistory(url);
                this.browserHistoryDAL.close();
            }
        } else {
            BrowserHistoryDAL browserHistoryDAL = this.browserHistoryDAL;
            if (browserHistoryDAL != null) {
                browserHistoryDAL.close();
            }
            Common.IsWebBrowserActive = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(getApplicationContext(), Common.CurrentWebBrowserActivity.getClass()));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.isClearHistory) {
            clearHistory();
        }
        if (this.isClearCookies) {
            clearCookies();
        }
        if (this.isClearChache) {
            clearCache();
        }
        this.secureBrowser.destroy();
    }

    public void clearCache() {
        this.secureBrowser.clearCache(true);
    }

    public void clearCookies() {
        WebStorage.getInstance().deleteAllData();
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.removeAllCookies(null);
            return;
        }
        CookieSyncManager.createInstance(this);
        cookieManager.removeAllCookie();
    }

    public void clearHistory() {
        WebViewDatabase webViewDatabase = WebViewDatabase.getInstance(this);
        webViewDatabase.clearFormData();
        this.secureBrowser.clearHistory();
        webViewDatabase.clearHttpAuthUsernamePassword();
        if (Build.VERSION.SDK_INT < 18) {
            webViewDatabase.clearUsernamePassword();
            WebIconDatabase.getInstance().removeAllIcons();
        }
        Utilities.trimCache(this);
        this.browserHistoryDAL.OpenWrite();
        this.browserHistoryDAL.DeleteHistories();
        this.browserHistoryDAL.close();
        this.dialogUrlRecent.dismiss();
    }
}
