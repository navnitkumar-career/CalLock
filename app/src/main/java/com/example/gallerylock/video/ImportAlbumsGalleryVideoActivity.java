package com.example.gallerylock.video;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.gallery.GalleryActivity;
import com.example.gallerylock.gallery.SelectAlbumInImportAdapter;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class ImportAlbumsGalleryVideoActivity extends Activity implements AccelerometerListener, SensorEventListener {
    public ProgressBar Progress;
    private AlbumsImportAdapter adapter;
    ListView album_import_ListView;
    AppCompatImageView btnSelectAll;
    private int count;
    String dbFolderPath;
    int folderId;
    String folderName;
    String folderPath;
    private GalleryVideoAdapter galleryImagesAdapter;
    int image_column_index;
    GridView imagegrid;
    TextView lbl_import_photo_album_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_background;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_topbaar;
    ImageView photo_video_empty_icon;
    int selectCount;
    private SensorManager sensorManager;
    Cursor videoCursor;
    int video_column_index;
    ArrayList<ImportAlbumEnt> importAlbumEnts = new ArrayList<>();
    private ArrayList<ImportEnt> videoImportEntList = new ArrayList<>();
    ArrayList<String> spinnerValues = new ArrayList<>();
    private ArrayList<ImportEnt> videoImportEntListShow = new ArrayList<>();
    boolean isAlbumClick = false;
    List<List<ImportEnt>> videoImportEntListShowList = new ArrayList();
    private ArrayList<String> selectPath = new ArrayList<>();
    ProgressDialog myProgressDialog = null;
    Context context = this;
    private boolean IsExceptionInImportVideos = false;
    Handler Progresshowhandler = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                ImportAlbumsGalleryVideoActivity.this.Progress.setVisibility(8);
                ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter = new GalleryVideoAdapter(ImportAlbumsGalleryVideoActivity.this.context, 1, ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow);
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setAdapter((ListAdapter) ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter);
                ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter.notifyDataSetChanged();
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(0);
                if (ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.size() <= 0) {
                    ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setVisibility(4);
                    ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(4);
                    ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setVisibility(4);
                    ImportAlbumsGalleryVideoActivity.this.ll_photo_video_empty.setVisibility(0);
                    ImportAlbumsGalleryVideoActivity.this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
                    ImportAlbumsGalleryVideoActivity.this.lbl_photo_video_empty.setText(R.string.no_videos);
                }
            }
            super.handleMessage(message);
        }
    };
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                ImportAlbumsGalleryVideoActivity.this.hideProgress();
                if (ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.size() > 0) {
                    ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity = ImportAlbumsGalleryVideoActivity.this;
                    ImportAlbumsGalleryVideoActivity importAlbumsGalleryVideoActivity2 = ImportAlbumsGalleryVideoActivity.this;
                    importAlbumsGalleryVideoActivity.galleryImagesAdapter = new GalleryVideoAdapter(importAlbumsGalleryVideoActivity2, 1, importAlbumsGalleryVideoActivity2.videoImportEntListShow);
                    ImportAlbumsGalleryVideoActivity.this.imagegrid.setAdapter((ListAdapter) ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter);
                } else {
                    ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setEnabled(false);
                    ImportAlbumsGalleryVideoActivity.this.ll_Import_bottom_baar.setEnabled(false);
                }
            } else if (message.what == 4) {
                ImportAlbumsGalleryVideoActivity.this.Progress.setVisibility(8);
                ImportAlbumsGalleryVideoActivity.this.GetAlbumsFromGallery();
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        ImportAlbumsGalleryVideoActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        ImportAlbumsGalleryVideoActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    if (ImportAlbumsGalleryVideoActivity.this.IsExceptionInImportVideos) {
                        ImportAlbumsGalleryVideoActivity.this.IsExceptionInImportVideos = false;
                    } else {
                        Toast.makeText(ImportAlbumsGalleryVideoActivity.this, ImportAlbumsGalleryVideoActivity.this.selectCount + " video(s) imported successfully", 0).show();
                    }
                    ImportAlbumsGalleryVideoActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        if (Common.IsCameFromPhotoAlbum) {
                            if (ImportAlbumsGalleryVideoActivity.this.isAlbumClick) {
                                intent = new Intent(ImportAlbumsGalleryVideoActivity.this, Videos_Gallery_Actitvity.class);
                            } else {
                                intent = new Intent(ImportAlbumsGalleryVideoActivity.this, VideosAlbumActivty.class);
                            }
                        } else if (Common.IsCameFromGalleryFeature) {
                            Common.IsCameFromGalleryFeature = false;
                            intent = new Intent(ImportAlbumsGalleryVideoActivity.this, GalleryActivity.class);
                        } else if (ImportAlbumsGalleryVideoActivity.this.isAlbumClick) {
                            intent = new Intent(ImportAlbumsGalleryVideoActivity.this, Videos_Gallery_Actitvity.class);
                        } else {
                            intent = new Intent(ImportAlbumsGalleryVideoActivity.this, VideosAlbumActivty.class);
                        }
                        intent.addFlags(67108864);
                        ImportAlbumsGalleryVideoActivity.this.startActivity(intent);
                        ImportAlbumsGalleryVideoActivity.this.finish();
                    }
                }
                if (Common.InterstitialAdCount <= 1) {
                    Common.InterstitialAdCount++;
                }
            } else if (message.what == 2) {
                ImportAlbumsGalleryVideoActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RefershGalleryforKitkat() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.3
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being encrypted... this may take a few moments...", true);
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
        setContentView(R.layout.import_album_list_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        getWindow().addFlags(128);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        TextView textView = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
        this.lbl_import_photo_album_topbaar = textView;
        textView.setText(R.string.lbl_import_photo_album_select_album_topbaar);
        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.folderId = Common.FolderId;
        this.folderName = null;
        if (0 == 0) {
            this.folderId = Common.FolderId;
            VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
            videoAlbumDAL.OpenRead();
            VideoAlbum GetAlbumById = videoAlbumDAL.GetAlbumById(Common.FolderId);
            videoAlbumDAL.close();
            this.folderName = GetAlbumById.getAlbumName();
        }
        this.dbFolderPath = (String) getIntent().getSerializableExtra("FOLDER_PATH");
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(3);
            } else {
                this.imagegrid.setNumColumns(2);
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else {
                this.imagegrid.setNumColumns(3);
            }
        }
        LoadData();
        this.ll_Import_bottom_baar.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImportAlbumsGalleryVideoActivity.this.OnImportClick();
            }
        });
        this.album_import_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(0);
                ImportAlbumsGalleryVideoActivity.this.isAlbumClick = true;
                ImportAlbumsGalleryVideoActivity.this.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_video_album_select_video_topbaar);
                ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setVisibility(4);
                ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setVisibility(0);
                ImportAlbumsGalleryVideoActivity.this.adapter = new AlbumsImportAdapter(ImportAlbumsGalleryVideoActivity.this.context, 17367043, ImportAlbumsGalleryVideoActivity.this.importAlbumEnts, false, true);
                ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setAdapter((ListAdapter) ImportAlbumsGalleryVideoActivity.this.adapter);
                ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.clear();
                Iterator it = ImportAlbumsGalleryVideoActivity.this.videoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt importEnt = (ImportEnt) it.next();
                    if (ImportAlbumsGalleryVideoActivity.this.spinnerValues.get(i).equals(new File(importEnt.GetPath()).getParent())) {
                        importEnt.GetThumbnailSelection().booleanValue();
                        ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.add(importEnt);
                    }
                }
                ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter = new GalleryVideoAdapter(ImportAlbumsGalleryVideoActivity.this.context, 1, ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow);
                ImportAlbumsGalleryVideoActivity.this.imagegrid.setAdapter((ListAdapter) ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter);
                ImportAlbumsGalleryVideoActivity.this.galleryImagesAdapter.notifyDataSetChanged();
                if (ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.size() <= 0) {
                    ImportAlbumsGalleryVideoActivity.this.album_import_ListView.setVisibility(4);
                    ImportAlbumsGalleryVideoActivity.this.imagegrid.setVisibility(4);
                    ImportAlbumsGalleryVideoActivity.this.btnSelectAll.setVisibility(4);
                    ImportAlbumsGalleryVideoActivity.this.ll_photo_video_empty.setVisibility(0);
                    ImportAlbumsGalleryVideoActivity.this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
                    ImportAlbumsGalleryVideoActivity.this.lbl_photo_video_empty.setText(R.string.no_videos);
                }
            }
        });
    }

    int albumCheckCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
            if (this.importAlbumEnts.get(i2).GetAlbumFileCheck()) {
                i++;
            }
        }
        return i;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity$6] */
    void LoadData() {
        this.Progress.setVisibility(0);
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.6
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    try {
                        ImportAlbumsGalleryVideoActivity.this.loadGallery();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Iterator it = ImportAlbumsGalleryVideoActivity.this.videoImportEntList.iterator();
                    while (it.hasNext()) {
                        ImportEnt importEnt = (ImportEnt) it.next();
                        if (ImportAlbumsGalleryVideoActivity.this.spinnerValues.get(0).contains(new File(importEnt.GetPath()).getParent())) {
                            ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.add(importEnt);
                        }
                    }
                    Message message = new Message();
                    message.what = 4;
                    ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity$7] */
    void ShowAlbumData(final int i) {
        this.Progress.setVisibility(0);
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.7
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.clear();
                    Iterator it = ImportAlbumsGalleryVideoActivity.this.videoImportEntList.iterator();
                    while (it.hasNext()) {
                        ImportEnt importEnt = (ImportEnt) it.next();
                        if (ImportAlbumsGalleryVideoActivity.this.spinnerValues.get(i).equals(new File(importEnt.GetPath()).getParent())) {
                            importEnt.GetThumbnailSelection().booleanValue();
                            ImportAlbumsGalleryVideoActivity.this.videoImportEntListShow.add(importEnt);
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    ImportAlbumsGalleryVideoActivity.this.Progresshowhandler.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 1;
                    ImportAlbumsGalleryVideoActivity.this.Progresshowhandler.sendMessage(message2);
                }
            }
        }.start();
    }

    public void OnImportClick() {
        final StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectvideomsg_import, 1).show();
        } else if (Common.GetFileSize(this.selectPath) < Common.GetTotalFree()) {
            int albumCheckCount = albumCheckCount();
            if (albumCheckCount >= 2) {
                final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.confirmation_message_box);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
                TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
                textView.setTypeface(createFromAsset);
                textView.setText("Are you sure you want to import " + albumCheckCount + " albums? Importing may take time according to the size of your data.");
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.8
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                            ImportAlbumsGalleryVideoActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            ImportAlbumsGalleryVideoActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            final Dialog dialog2 = new Dialog(ImportAlbumsGalleryVideoActivity.this, R.style.FullHeightDialog);
                            dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                            dialog2.setCancelable(false);
                            dialog2.setCanceledOnTouchOutside(false);
                            ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgVideo);
                            ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.8.1
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    GetObject.SetISDAlertshow(Boolean.valueOf(z));
                                }
                            });
                            ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.8.2
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view2) {
                                    dialog2.dismiss();
                                    ImportAlbumsGalleryVideoActivity.this.Import();
                                }
                            });
                            dialog2.show();
                        } else {
                            ImportAlbumsGalleryVideoActivity.this.Import();
                        }
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.9
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        for (int i = 0; i < ImportAlbumsGalleryVideoActivity.this.importAlbumEnts.size(); i++) {
                            ImportAlbumsGalleryVideoActivity.this.importAlbumEnts.get(i).SetAlbumFileCheck(false);
                        }
                        ImportAlbumsGalleryVideoActivity.this.GetAlbumsFromGallery();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                Import();
            } else if (GetObject.GetSDCardUri().length() > 0) {
                Import();
            } else if (!GetObject.GetISDAlertshow()) {
                final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
                dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                dialog2.setCancelable(false);
                dialog2.setCanceledOnTouchOutside(false);
                ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgVideo);
                ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.10
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        GetObject.SetISDAlertshow(Boolean.valueOf(z));
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.11
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        dialog2.dismiss();
                        ImportAlbumsGalleryVideoActivity.this.Import();
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.12
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
            } else {
                Import();
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity$14] */
    void Import() {
        if (!Common.IsCameFromGalleryFeature || !this.isAlbumClick) {
            SelectedCount();
            ShowImportProgress();
            Common.IsWorkInProgress = true;
            new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.14
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        ImportAlbumsGalleryVideoActivity.this.ImportVideo();
                        Message message = new Message();
                        message.what = 3;
                        ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message);
                        Common.IsWorkInProgress = false;
                    } catch (Exception unused) {
                        Message message2 = new Message();
                        message2.what = 3;
                        ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message2);
                    }
                }
            }.start();
            return;
        }
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
        videoAlbumDAL.OpenRead();
        final List<VideoAlbum> GetAlbums = videoAlbumDAL.GetAlbums(0);
        videoAlbumDAL.close();
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.share_from_gallery);
        ((LinearLayout) dialog.findViewById(R.id.ll_bottom_bar)).setVisibility(8);
        ((TextView) dialog.findViewById(R.id.lbl_import_title)).setText("Select Album to Import Video(s)");
        GridView gridView = (GridView) dialog.findViewById(R.id.fileListgrid);
        gridView.setNumColumns(1);
        SelectAlbumInImportAdapter selectAlbumInImportAdapter = new SelectAlbumInImportAdapter(this, 1, GetAlbums, true);
        gridView.setAdapter((ListAdapter) selectAlbumInImportAdapter);
        selectAlbumInImportAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.13
            /* JADX WARN: Type inference failed for: r1v6, types: [net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity$13$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryVideoActivity.this.folderId = ((VideoAlbum) GetAlbums.get(i)).getId();
                dialog.dismiss();
                ImportAlbumsGalleryVideoActivity.this.SelectedCount();
                ImportAlbumsGalleryVideoActivity.this.ShowImportProgress();
                Common.IsWorkInProgress = true;
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.13.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            ImportAlbumsGalleryVideoActivity.this.ImportOnlyVideosSDCard();
                            Message message = new Message();
                            message.what = 3;
                            ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            ImportAlbumsGalleryVideoActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    public void ImportVideo() throws IOException {
        if (this.isAlbumClick) {
            ImportOnlyVideosSDCard();
        } else {
            importAlbum();
        }
    }

    void importAlbum() {
        if (this.importAlbumEnts.size() > 0) {
            int i = 0;
            for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                if (this.importAlbumEnts.get(i2).GetAlbumFileCheck()) {
                    File file = new File(this.importAlbumEnts.get(i2).GetAlbumName());
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + file.getName());
                    this.folderName = file.getName();
                    if (file2.exists()) {
                        int i3 = 1;
                        while (i3 < 100) {
                            this.folderName = file.getName() + "(" + i3 + ")";
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.VIDEOS);
                            sb.append(this.folderName);
                            file2 = new File(sb.toString());
                            if (!file2.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                    }
                    AddAlbumToDatabase(this.folderName, file2.getAbsolutePath());
                    VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(getApplicationContext());
                    videoAlbumDAL.OpenRead();
                    this.folderId = videoAlbumDAL.GetLastAlbumId();
                    videoAlbumDAL.close();
                    try {
                        ImportAlbumsVideosSDCard(i);
                        i++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    void ImportAlbumsVideosSDCard(int i) throws IOException {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List<ImportEnt> list = this.videoImportEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (list.get(i2).GetThumbnailSelection().booleanValue()) {
                File file = new File(list.get(i2).GetPath());
                String str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.folderName + "/";
                File file2 = new File(str);
                new File(str + "VideoThumnails/").mkdirs();
                String FileName = FileName(list.get(i2).GetPath());
                String str2 = str + "VideoThumnails/thumbnil-" + FileName.substring(0, FileName.lastIndexOf(".")) + "#jpg";
                File file3 = new File(str2);
                file3.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file3);
                list.get(i2).SetThumbnail(MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), list.get(i2).GetId(), 3, null));
                list.get(i2).GetThumbnail().compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Utilities.NSEncryption(file3);
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, file2);
                    try {
                        Utilities.NSEncryption(new File(NSHideFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (NSHideFile.length() > 1) {
                        AddVideoToDatabase(FileName(list.get(i2).GetPath()), list.get(i2).GetPath(), str2, NSHideFile);
                        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                            Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                        }
                        try {
                            getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "_data='" + file.getPath() + "'", null);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        this.IsExceptionInImportVideos = true;
                    }
                } catch (Exception e3) {
                    this.IsExceptionInImportVideos = true;
                    e3.printStackTrace();
                }
            }
        }
    }

    public void ImportOnlyVideosSDCard() throws IOException {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.videoImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (this.videoImportEntListShow.get(i).GetThumbnailSelection().booleanValue()) {
                String str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.folderName;
                new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.folderName + "/VideoThumnails/").mkdirs();
                String FileName = FileName(this.videoImportEntListShow.get(i).GetPath());
                String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.folderName + "/VideoThumnails/thumbnil-" + FileName.substring(0, FileName.lastIndexOf(".")) + "#jpg";
                File file = new File(str2);
                FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                this.videoImportEntListShow.get(i).GetThumbnail().compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Utilities.NSEncryption(file);
                try {
                    File file2 = new File(this.videoImportEntListShow.get(i).GetPath());
                    String NSHideFile = Utilities.NSHideFile(this, file2, new File(str));
                    try {
                        Utilities.NSEncryption(new File(NSHideFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (NSHideFile.length() > 1) {
                        AddVideoToDatabase(FileName(this.videoImportEntListShow.get(i).GetPath()), this.videoImportEntListShow.get(i).GetPath(), str2, NSHideFile);
                        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                            Utilities.DeleteSDcardImageLollipop(this, file2.getAbsolutePath());
                        }
                        try {
                            getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "_data='" + file2.getPath() + "'", null);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        this.IsExceptionInImportVideos = true;
                    }
                } catch (Exception e3) {
                    this.IsExceptionInImportVideos = true;
                    e3.printStackTrace();
                }
            }
        }
    }

    void AddVideoToDatabase(String str, String str2, String str3, String str4) {
        Log.d("Path", str4);
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str4);
        video.setOriginalVideoLocation(str2);
        video.setthumbnail_video_location(str3);
        video.setAlbumId(this.folderId);
        VideoDAL videoDAL = new VideoDAL(this.context);
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

    public void AddAlbumToDatabase(String str, String str2) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(str);
        videoAlbum.setAlbumLocation(str2);
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        try {
            try {
                videoAlbumDAL.OpenWrite();
                videoAlbumDAL.AddVideoAlbum(videoAlbum);
                Common.FolderId = videoAlbumDAL.GetLastAlbumId();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoAlbumDAL.close();
        }
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (this.importAlbumEnts.get(i).GetAlbumFileCheck()) {
                this.videoImportEntListShow = new ArrayList<>();
                Iterator<ImportEnt> it = this.videoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt next = it.next();
                    if (this.spinnerValues.get(i).equals(new File(next.GetPath()).getParent())) {
                        this.videoImportEntListShow.add(next);
                    }
                    for (int i2 = 0; i2 < this.videoImportEntListShow.size(); i2++) {
                        this.videoImportEntListShow.get(i2).SetThumbnailSelection(true);
                    }
                }
                this.videoImportEntListShowList.add(this.videoImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.videoImportEntListShow.size(); i3++) {
            if (this.videoImportEntListShow.get(i3).GetThumbnailSelection().booleanValue()) {
                this.selectPath.add(this.videoImportEntListShow.get(i3).GetPath());
                return true;
            }
        }
        return false;
    }

    public void GetAlbumsFromGallery() {
        AlbumsImportAdapter albumsImportAdapter = new AlbumsImportAdapter(this.context, 17367043, this.importAlbumEnts, false, true);
        this.adapter = albumsImportAdapter;
        this.album_import_ListView.setAdapter((ListAdapter) albumsImportAdapter);
        this.adapter.notifyDataSetChanged();
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(4);
            this.imagegrid.setVisibility(4);
            this.btnSelectAll.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.no_videos);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.videoImportEntListShow.size(); i++) {
            if (this.videoImportEntListShow.get(i).GetThumbnailSelection().booleanValue()) {
                this.selectCount++;
            }
        }
    }

    public void btnSelectAllonClick(View view) {
        SelectAllPhotos();
        GalleryVideoAdapter galleryVideoAdapter = new GalleryVideoAdapter(this.context, 1, this.videoImportEntListShow);
        this.galleryImagesAdapter = galleryVideoAdapter;
        this.imagegrid.setAdapter((ListAdapter) galleryVideoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    private void SelectAllPhotos() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.videoImportEntListShow.size(); i++) {
                this.videoImportEntListShow.get(i).SetThumbnailSelection(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.videoImportEntListShow.size(); i2++) {
            this.videoImportEntListShow.get(i2).SetThumbnailSelection(true);
        }
        this.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
    }

    public void loadGallery() throws IOException {
        if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
            sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
        } else {
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.video.ImportAlbumsGalleryVideoActivity.15
                @Override // android.media.MediaScannerConnection.OnScanCompletedListener
                public void onScanCompleted(String str, Uri uri) {
                }
            });
        }
        Cursor managedQuery = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, null, null, "_id");
        this.videoCursor = managedQuery;
        this.video_column_index = managedQuery.getColumnIndex("_id");
        this.count = this.videoCursor.getCount();
        for (int i = 0; i < this.count; i++) {
            this.videoCursor.moveToPosition(i);
            int i2 = this.videoCursor.getInt(this.video_column_index);
            int columnIndex = this.videoCursor.getColumnIndex("_data");
            if (new File(this.videoCursor.getString(columnIndex)).exists()) {
                String string = this.videoCursor.getString(columnIndex);
                if (string.endsWith(".3gp") || string.endsWith(".mp4") || string.endsWith(".ts") || string.endsWith(".webm") || string.endsWith(".mkv") || string.endsWith(".wmv") || string.endsWith(".mov") || string.endsWith(".avi") || string.endsWith(".flv")) {
                    ImportEnt importEnt = new ImportEnt();
                    importEnt.SetId(i2);
                    importEnt.SetPath(this.videoCursor.getString(columnIndex));
                    importEnt.SetThumbnail(null);
                    importEnt.SetThumbnailSelection(false);
                    this.videoImportEntList.add(importEnt);
                    ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
                    File file = new File(importEnt.GetPath());
                    if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(file.getParent())) {
                        importAlbumEnt.SetId(i2);
                        importAlbumEnt.SetAlbumName(file.getParent());
                        importAlbumEnt.SetPath(this.videoCursor.getString(columnIndex));
                        this.importAlbumEnts.add(importAlbumEnt);
                        this.spinnerValues.add(file.getParent());
                    }
                }
            }
        }
        if (this.videoImportEntList.size() <= 0) {
            this.btnSelectAll.setEnabled(false);
            this.ll_Import_bottom_baar.setEnabled(false);
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

    public void Back() {
        Intent intent;
        if (this.isAlbumClick) {
            this.isAlbumClick = false;
            this.lbl_import_photo_album_topbaar.setText("Import Albums");
            this.album_import_ListView.setVisibility(0);
            this.imagegrid.setVisibility(4);
            this.btnSelectAll.setVisibility(4);
            for (int i = 0; i < this.videoImportEntListShow.size(); i++) {
                this.videoImportEntListShow.get(i).SetThumbnailSelection(false);
            }
            this.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        if (Common.IsCameFromPhotoAlbum) {
            Common.IsCameFromPhotoAlbum = false;
            intent = new Intent(this, VideosAlbumActivty.class);
        } else if (Common.IsCameFromGalleryFeature) {
            Common.IsCameFromGalleryFeature = false;
            intent = new Intent(this, GalleryActivity.class);
        } else {
            intent = new Intent(this, Videos_Gallery_Actitvity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(5);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else {
                this.imagegrid.setNumColumns(3);
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(4);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.imagegrid.setNumColumns(3);
            } else {
                this.imagegrid.setNumColumns(2);
            }
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive && !Common.IsWorkInProgress) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Intent intent;
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.lbl_import_photo_album_topbaar.setText("Import Albums");
                this.album_import_ListView.setVisibility(0);
                this.imagegrid.setVisibility(4);
                this.btnSelectAll.setVisibility(4);
                for (int i2 = 0; i2 < this.videoImportEntListShow.size(); i2++) {
                    this.videoImportEntListShow.get(i2).SetThumbnailSelection(false);
                }
                this.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            if (Common.IsCameFromPhotoAlbum) {
                Common.IsCameFromPhotoAlbum = false;
                intent = new Intent(this, VideosAlbumActivty.class);
            } else if (Common.IsCameFromGalleryFeature) {
                Common.IsCameFromGalleryFeature = false;
                intent = new Intent(this, GalleryActivity.class);
            } else {
                intent = new Intent(this, Videos_Gallery_Actitvity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
