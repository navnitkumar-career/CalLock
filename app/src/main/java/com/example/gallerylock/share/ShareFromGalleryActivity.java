package com.example.gallerylock.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoAlbum;
import com.example.gallerylock.photo.PhotoAlbumDAL;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoAlbum;
import com.example.gallerylock.video.VideoAlbumDAL;
import com.example.gallerylock.video.VideoDAL;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class ShareFromGalleryActivity extends Activity {
    Intent ImageAndVideosMixintent;
    Intent MultipleFileintent;
    Intent MultipleVideoFileintent;
    Intent SingleFileintent;
    Intent SingleVideoFileintent;
    private ShareFromGalleryAdapter adapter;
    GridView albumGridView;
    String dbFolderPath;
    Uri imageUri;
    ArrayList<Uri> imageUris;
    LinearLayout ll_background;
    private PhotoAlbumDAL photoAlbumDAL;
    private ArrayList<PhotoAlbum> photoAlbums;
    TextView textfoldername;
    private VideoAlbumDAL videoAlbumDAL;
    private ArrayList<VideoAlbum> videoAlbums;
    Uri videoUri;
    ArrayList<Uri> videoUris;
    protected int AlbumId = 1;
    boolean IsSingleFile = false;
    boolean IsMultipleFile = false;
    boolean IsSingleVideoFile = false;
    boolean IsMultipVideoleFile = false;
    boolean IsImageAndVideosMix = false;
    boolean IsImageMix = false;
    boolean IsVideoMix = false;
    int ListPosition = 0;
    ProgressDialog myProgressDialog = null;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.share.ShareFromGalleryActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 3) {
                if (Common.IsImporting) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        ShareFromGalleryActivity shareFromGalleryActivity = ShareFromGalleryActivity.this;
                        shareFromGalleryActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        ShareFromGalleryActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    ShareFromGalleryActivity.this.hideProgress();
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (!ShareFromGalleryActivity.this.IsImageAndVideosMix) {
                        if (ShareFromGalleryActivity.this.IsSingleFile || ShareFromGalleryActivity.this.IsMultipleFile) {
                            Toast.makeText(ShareFromGalleryActivity.this, (int) R.string.toast_Share_from_gallery_sucess_photo, 0).show();
                        } else {
                            Toast.makeText(ShareFromGalleryActivity.this, (int) R.string.toast_Share_from_gallery_sucess_video, 0).show();
                        }
                        ShareFromGalleryActivity.this.finish();
                    } else {
                        Toast.makeText(ShareFromGalleryActivity.this, (int) R.string.toast_Share_from_gallery_sucess_photo, 0).show();
                        ShareFromGalleryActivity.this.ListPosition = 0;
                        ShareFromGalleryActivity.this.textfoldername.setText(R.string.lbl_Share_from_gallery_selectvideo);
                        ShareFromGalleryActivity.this.GetVideoAlbumsFromDatabase();
                        ShareFromGalleryActivity shareFromGalleryActivity2 = ShareFromGalleryActivity.this;
                        shareFromGalleryActivity2.AlbumId = ((VideoAlbum) shareFromGalleryActivity2.videoAlbums.get(0)).getId();
                    }
                }
            } else if (message.what == 2) {
                ShareFromGalleryActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };

    private void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.share_from_gallery);
        Common.applyKitKatTranslucency(this);
        this.albumGridView = (GridView) findViewById(R.id.fileListgrid);
        this.textfoldername = (TextView) findViewById(R.id.lbl_import_title);
        AppSettingsSharedPreferences.GetObject(this);
        Utilities.CheckDeviceStoragePaths(this);
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(3);
            } else {
                this.albumGridView.setNumColumns(2);
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else {
                this.albumGridView.setNumColumns(3);
            }
        }
        StorageOptionsCommon.STORAGEPATH = StorageOptionSharedPreferences.GetObject(this).GetStoragePath();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if ("android.intent.action.SEND".equals(action) && type != null) {
            if (HTTP.PLAIN_TEXT_TYPE.equals(type)) {
                handleSendText(intent);
            }
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
            if (type.startsWith("video/")) {
                handleSendVideo(intent);
            }
        }
        if ("android.intent.action.SEND_MULTIPLE".equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
            if (type.startsWith("video/")) {
                handleSendMultipleVideos(intent);
            }
            if (type.startsWith("*/*")) {
                handleSendImageAndVideosMix(intent);
            }
        }
        if ("android.intent.action.PICK".equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
            if (type.startsWith("video/")) {
                handleSendMultipleVideos(intent);
            }
        }
        this.albumGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.share.ShareFromGalleryActivity.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ShareFromGalleryActivity.this.ListPosition = i;
                if (ShareFromGalleryActivity.this.IsSingleFile || ShareFromGalleryActivity.this.IsMultipleFile) {
                    ShareFromGalleryActivity shareFromGalleryActivity = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity.AlbumId = ((PhotoAlbum) shareFromGalleryActivity.photoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity2 = ShareFromGalleryActivity.this;
                    ShareFromGalleryActivity shareFromGalleryActivity3 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity2.adapter = new ShareFromGalleryAdapter(shareFromGalleryActivity3, 17367043, shareFromGalleryActivity3.photoAlbums, ShareFromGalleryActivity.this.ListPosition);
                } else if (ShareFromGalleryActivity.this.IsSingleVideoFile || ShareFromGalleryActivity.this.IsMultipVideoleFile) {
                    ShareFromGalleryActivity shareFromGalleryActivity4 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity4.AlbumId = ((VideoAlbum) shareFromGalleryActivity4.videoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity5 = ShareFromGalleryActivity.this;
                    ShareFromGalleryActivity shareFromGalleryActivity6 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity5.adapter = new ShareFromGalleryAdapter(shareFromGalleryActivity6, 17367043, shareFromGalleryActivity6.videoAlbums, ShareFromGalleryActivity.this.ListPosition, true);
                } else if (ShareFromGalleryActivity.this.IsImageMix) {
                    ShareFromGalleryActivity shareFromGalleryActivity7 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity7.AlbumId = ((PhotoAlbum) shareFromGalleryActivity7.photoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity8 = ShareFromGalleryActivity.this;
                    ShareFromGalleryActivity shareFromGalleryActivity9 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity8.adapter = new ShareFromGalleryAdapter(shareFromGalleryActivity9, 17367043, shareFromGalleryActivity9.photoAlbums, ShareFromGalleryActivity.this.ListPosition);
                } else if (ShareFromGalleryActivity.this.IsVideoMix) {
                    ShareFromGalleryActivity shareFromGalleryActivity10 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity10.AlbumId = ((VideoAlbum) shareFromGalleryActivity10.videoAlbums.get(i)).getId();
                    ShareFromGalleryActivity shareFromGalleryActivity11 = ShareFromGalleryActivity.this;
                    ShareFromGalleryActivity shareFromGalleryActivity12 = ShareFromGalleryActivity.this;
                    shareFromGalleryActivity11.adapter = new ShareFromGalleryAdapter(shareFromGalleryActivity12, 17367043, shareFromGalleryActivity12.videoAlbums, ShareFromGalleryActivity.this.ListPosition, true);
                }
                ShareFromGalleryActivity.this.albumGridView.setAdapter((ListAdapter) ShareFromGalleryActivity.this.adapter);
            }
        });
        if (this.IsImageAndVideosMix) {
            GetPhotoAlbumsFromDatabase();
            this.textfoldername.setText(R.string.lbl_Share_from_gallery_selectphoto);
            return;
        }
        GetAlbumsFromDatabase();
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [net.newsoftwares.hidepicturesvideos.share.ShareFromGalleryActivity$3] */
    public void onFileMoveClick(View view) {
        ShowImportProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.share.ShareFromGalleryActivity.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    if (ShareFromGalleryActivity.this.IsImageAndVideosMix) {
                        ShareFromGalleryActivity.this.ImportMix();
                    } else {
                        ShareFromGalleryActivity.this.Import();
                    }
                    Message message = new Message();
                    message.what = 3;
                    ShareFromGalleryActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 3;
                    ShareFromGalleryActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(20:83|(3:126|84|85)|(2:132|86)|91|118|92|(1:94)|95|(1:97)|98|(1:100)|101|116|102|103|120|104|146|113|81) */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x04bb, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x04bd, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x04bf, code lost:
        r0.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x04c3, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x04c5, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x04c7, code lost:
        r0.printStackTrace();
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x0481 A[Catch: IOException -> 0x04c5, TryCatch #2 {IOException -> 0x04c5, blocks: (B:92:0x0431, B:94:0x0443, B:95:0x0462, B:97:0x0474, B:98:0x047b, B:100:0x0481, B:101:0x0495), top: B:118:0x0431 }] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0443 A[Catch: IOException -> 0x04c5, TryCatch #2 {IOException -> 0x04c5, blocks: (B:92:0x0431, B:94:0x0443, B:95:0x0462, B:97:0x0474, B:98:0x047b, B:100:0x0481, B:101:0x0495), top: B:118:0x0431 }] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0474 A[Catch: IOException -> 0x04c5, TryCatch #2 {IOException -> 0x04c5, blocks: (B:92:0x0431, B:94:0x0443, B:95:0x0462, B:97:0x0474, B:98:0x047b, B:100:0x0481, B:101:0x0495), top: B:118:0x0431 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void Import() {
        /*
            Method dump skipped, instructions count: 1235
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: net.newsoftwares.hidepicturesvideos.share.ShareFromGalleryActivity.Import():void");
    }

    public void ImportMix() {
        if (this.IsImageMix) {
            this.IsImageMix = false;
            Common.IsImporting = true;
            ArrayList<Uri> parcelableArrayListExtra = this.ImageAndVideosMixintent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            this.imageUris = parcelableArrayListExtra;
            if (parcelableArrayListExtra != null) {
                PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(getApplicationContext());
                photoAlbumDAL.OpenRead();
                PhotoAlbum GetAlbumById = photoAlbumDAL.GetAlbumById(Integer.toString(this.AlbumId));
                for (int i = 0; i < this.imageUris.size(); i++) {
                    if (this.imageUris.get(i).getPathSegments().get(1).equals("images")) {
                        this.dbFolderPath = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + GetAlbumById.getAlbumName();
                        try {
                            String NSHideFile = Utilities.NSHideFile(this, new File(getRealPathFromURI(this.imageUris.get(i))), new File(this.dbFolderPath));
                            Utilities.NSEncryption(new File(NSHideFile));
                            if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                                sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                            }
                            String realPathFromURI = getRealPathFromURI(this.imageUris.get(i));
                            if (NSHideFile.length() > 0) {
                                AddPhotoToDatabase(FileName(realPathFromURI), realPathFromURI, NSHideFile);
                            }
                            if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                intent.setData(Uri.fromFile(new File(realPathFromURI)));
                                sendBroadcast(intent);
                            }
                            File file = new File(realPathFromURI);
                            try {
                                getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_data='" + file.getPath() + "'", null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        }
        if (this.IsVideoMix) {
            this.IsVideoMix = false;
            this.IsImageAndVideosMix = false;
            Common.IsImporting = true;
            ArrayList<Uri> parcelableArrayListExtra2 = this.ImageAndVideosMixintent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            this.videoUris = parcelableArrayListExtra2;
            if (parcelableArrayListExtra2 != null) {
                VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
                videoAlbumDAL.OpenRead();
                VideoAlbum GetAlbumById2 = videoAlbumDAL.GetAlbumById(this.AlbumId);
                this.dbFolderPath = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + GetAlbumById2.getAlbumName();
                StringBuilder sb = new StringBuilder();
                sb.append(this.dbFolderPath);
                sb.append("/VideoThumnails/");
                File file2 = new File(sb.toString());
                if (!file2.exists()) {
                    file2.mkdir();
                }
                for (int i2 = 0; i2 < this.videoUris.size(); i2++) {
                    if (this.videoUris.get(i2).getPathSegments().get(1).equals("video")) {
                        String realPathFromURI2 = getRealPathFromURI(this.videoUris.get(i2));
                        String FileName = FileName(realPathFromURI2);
                        String str = this.dbFolderPath + "/VideoThumnails/thumbnil-" + FileName.substring(0, FileName.lastIndexOf(".")) + "#jpg";
                        File file3 = new File(str);
                        Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(realPathFromURI2, 3);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file3);
                            createVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Utilities.NSEncryption(file3);
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                        try {
                            String NSHideFile2 = Utilities.NSHideFile(this, new File(getRealPathFromURI(this.videoUris.get(i2))), new File(this.dbFolderPath));
                            Utilities.NSEncryption(new File(NSHideFile2));
                            if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                                sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                            }
                            String realPathFromURI3 = getRealPathFromURI(this.videoUris.get(i2));
                            if (NSHideFile2.length() > 0) {
                                AddVideoToDatabase(FileName(realPathFromURI3), realPathFromURI3, NSHideFile2, str);
                            }
                            if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                                Intent intent2 = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                intent2.setData(Uri.fromFile(new File(realPathFromURI3)));
                                sendBroadcast(intent2);
                            }
                            File file4 = new File(realPathFromURI3);
                            try {
                                getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "_data='" + file4.getPath() + "'", null);
                            } catch (Exception e4) {
                                e4.printStackTrace();
                            }
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    void handleSendText(Intent intent) {
        intent.getStringExtra("android.intent.extra.TEXT");
    }

    void handleSendImage(Intent intent) {
        this.IsSingleFile = true;
        this.SingleFileintent = intent;
    }

    void handleSendVideo(Intent intent) {
        this.IsSingleVideoFile = true;
        this.SingleVideoFileintent = intent;
    }

    void handleSendMultipleVideos(Intent intent) {
        this.IsMultipVideoleFile = true;
        this.MultipleVideoFileintent = intent;
    }

    void handleSendImageAndVideosMix(Intent intent) {
        this.IsImageAndVideosMix = true;
        this.ImageAndVideosMixintent = intent;
    }

    void handleSendMultipleImages(Intent intent) {
        this.IsMultipleFile = true;
        this.MultipleFileintent = intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RefershGalleryforKitkat() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.share.ShareFromGalleryActivity.4
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor query = getContentResolver().query(uri, null, null, null, null);
        query.moveToFirst();
        return query.getString(query.getColumnIndex("_data"));
    }

    void AddPhotoToDatabase(String str, String str2, String str3) {
        Log.d("Path", str3);
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str3);
        photo.setOriginalPhotoLocation(str2);
        photo.setAlbumId(this.AlbumId);
        PhotoDAL photoDAL = new PhotoDAL(getApplicationContext());
        try {
            try {
                photoDAL.OpenWrite();
                photoDAL.AddPhotos(photo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            photoDAL.close();
        }
    }

    void AddVideoToDatabase(String str, String str2, String str3, String str4) {
        Log.d("Path", str3);
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str3);
        video.setOriginalVideoLocation(str2);
        video.setthumbnail_video_location(str4);
        video.setAlbumId(this.AlbumId);
        VideoDAL videoDAL = new VideoDAL(getApplicationContext());
        try {
            try {
                videoDAL.OpenWrite();
                videoDAL.AddVideos(video);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoDAL.close();
        }
    }

    public String FileName(String str) {
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == " /".charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    public void GetAlbumsFromDatabase() {
        PhotoAlbumDAL photoAlbumDAL;
        VideoAlbumDAL videoAlbumDAL;
        if (this.IsSingleFile || this.IsMultipleFile) {
            this.textfoldername.setText("Select photo album");
            PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(getApplicationContext());
            this.photoAlbumDAL = photoAlbumDAL2;
            try {
                try {
                    photoAlbumDAL2.OpenRead();
                    this.photoAlbums = (ArrayList) this.photoAlbumDAL.GetAlbums(0);
                    ShareFromGalleryAdapter shareFromGalleryAdapter = new ShareFromGalleryAdapter(this, 17367043, this.photoAlbums, this.ListPosition);
                    this.adapter = shareFromGalleryAdapter;
                    this.albumGridView.setAdapter((ListAdapter) shareFromGalleryAdapter);
                    photoAlbumDAL = this.photoAlbumDAL;
                    if (photoAlbumDAL == null) {
                        return;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    photoAlbumDAL = this.photoAlbumDAL;
                    if (photoAlbumDAL == null) {
                        return;
                    }
                }
                photoAlbumDAL.close();
            } catch (Throwable th) {
                PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
                if (photoAlbumDAL3 != null) {
                    photoAlbumDAL3.close();
                }
                throw th;
            }
        } else if (this.IsSingleVideoFile || this.IsMultipVideoleFile) {
            this.textfoldername.setText("Select video album");
            VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(getApplicationContext());
            this.videoAlbumDAL = videoAlbumDAL2;
            try {
                try {
                    videoAlbumDAL2.OpenRead();
                    this.videoAlbums = (ArrayList) this.videoAlbumDAL.GetAlbums(0);
                    ShareFromGalleryAdapter shareFromGalleryAdapter2 = new ShareFromGalleryAdapter(this, 17367043, this.videoAlbums, this.ListPosition, true);
                    this.adapter = shareFromGalleryAdapter2;
                    this.albumGridView.setAdapter((ListAdapter) shareFromGalleryAdapter2);
                    videoAlbumDAL = this.videoAlbumDAL;
                    if (videoAlbumDAL == null) {
                        return;
                    }
                } catch (Exception e2) {
                    System.out.println(e2.getMessage());
                    videoAlbumDAL = this.videoAlbumDAL;
                    if (videoAlbumDAL == null) {
                        return;
                    }
                }
                videoAlbumDAL.close();
            } catch (Throwable th2) {
                VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
                if (videoAlbumDAL3 != null) {
                    videoAlbumDAL3.close();
                }
                throw th2;
            }
        }
    }

    public void btnCancelClick(View view) {
        finish();
    }

    public void GetPhotoAlbumsFromDatabase() {
        PhotoAlbumDAL photoAlbumDAL;
        this.IsImageMix = true;
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(getApplicationContext());
        this.photoAlbumDAL = photoAlbumDAL2;
        try {
            try {
                photoAlbumDAL2.OpenRead();
                this.photoAlbums = (ArrayList) this.photoAlbumDAL.GetAlbums(0);
                ShareFromGalleryAdapter shareFromGalleryAdapter = new ShareFromGalleryAdapter(this, 17367043, this.photoAlbums, this.ListPosition);
                this.adapter = shareFromGalleryAdapter;
                this.albumGridView.setAdapter((ListAdapter) shareFromGalleryAdapter);
                photoAlbumDAL = this.photoAlbumDAL;
                if (photoAlbumDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                photoAlbumDAL = this.photoAlbumDAL;
                if (photoAlbumDAL == null) {
                    return;
                }
            }
            photoAlbumDAL.close();
        } catch (Throwable th) {
            PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
            if (photoAlbumDAL3 != null) {
                photoAlbumDAL3.close();
            }
            throw th;
        }
    }

    public void GetVideoAlbumsFromDatabase() {
        VideoAlbumDAL videoAlbumDAL;
        this.IsVideoMix = true;
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(getApplicationContext());
        this.videoAlbumDAL = videoAlbumDAL2;
        try {
            try {
                videoAlbumDAL2.OpenRead();
                this.videoAlbums = (ArrayList) this.videoAlbumDAL.GetAlbums(0);
                ShareFromGalleryAdapter shareFromGalleryAdapter = new ShareFromGalleryAdapter(this, 17367043, this.videoAlbums, this.ListPosition, true);
                this.adapter = shareFromGalleryAdapter;
                this.albumGridView.setAdapter((ListAdapter) shareFromGalleryAdapter);
                videoAlbumDAL = this.videoAlbumDAL;
                if (videoAlbumDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                videoAlbumDAL = this.videoAlbumDAL;
                if (videoAlbumDAL == null) {
                    return;
                }
            }
            videoAlbumDAL.close();
        } catch (Throwable th) {
            VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
            if (videoAlbumDAL3 != null) {
                videoAlbumDAL3.close();
            }
            throw th;
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else {
                this.albumGridView.setNumColumns(3);
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.albumGridView.setNumColumns(3);
            } else {
                this.albumGridView.setNumColumns(2);
            }
        }
    }
}
