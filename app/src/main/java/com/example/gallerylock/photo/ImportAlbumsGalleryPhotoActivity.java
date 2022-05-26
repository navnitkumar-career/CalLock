package com.example.gallerylock.photo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.gallery.GalleryActivity;
import com.example.gallerylock.gallery.SelectAlbumInImportAdapter;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class ImportAlbumsGalleryPhotoActivity extends BaseActivity {
    public ProgressBar Progress;
    private AlbumsImportAdapter adapter;
    ListView album_import_ListView;
    AppCompatImageView btnSelectAll;
    private int count;
    String dbFolderPath;
    int folderId;
    String folderName;
    String folderPath;
    public static boolean IsPhoneGalleryLoad = true;
    private GalleryPhotoAdapter galleryImagesAdapter;
    int image_column_index;
    Cursor imagecursor;
    GridView imagegrid;
    TextView lbl_import_photo_album_topbaar;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_topbaar;
    int selectCount;
    private SensorManager sensorManager;
    Toolbar toolbar;
    ArrayList<ImportAlbumEnt> importAlbumEnts = new ArrayList<>();
    private ArrayList<ImportEnt> photoImportEntList = new ArrayList<>();
    ArrayList<String> spinnerValues = new ArrayList<>();
    private ArrayList<ImportEnt> photoImportEntListShow = new ArrayList<>();
    boolean isAlbumClick = false;
    private boolean IsExceptionInImportPhotos = false;
    List<List<ImportEnt>> photoImportEntListShowList = new ArrayList();
    private ArrayList<String> selectPath = new ArrayList<>();
    ProgressDialog myProgressDialog = null;
    Context context = this;
    Handler Progresshowhandler = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(8);
                ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter = new GalleryPhotoAdapter(ImportAlbumsGalleryPhotoActivity.this.context, 1, ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow);
                ImportAlbumsGalleryPhotoActivity.this.imagegrid.setAdapter((ListAdapter) ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter);
                ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter.notifyDataSetChanged();
                if (ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.size() <= 0) {
                    ImportAlbumsGalleryPhotoActivity.this.album_import_ListView.setVisibility(4);
                    ImportAlbumsGalleryPhotoActivity.this.imagegrid.setVisibility(4);
                    ImportAlbumsGalleryPhotoActivity.this.btnSelectAll.setVisibility(4);
                    ImportAlbumsGalleryPhotoActivity.this.ll_photo_video_empty.setVisibility(0);
                }
            }
            super.handleMessage(message);
        }
    };
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                ImportAlbumsGalleryPhotoActivity.this.hideProgress();
                if (ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.size() > 0) {
                    ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity = ImportAlbumsGalleryPhotoActivity.this;
                    ImportAlbumsGalleryPhotoActivity importAlbumsGalleryPhotoActivity2 = ImportAlbumsGalleryPhotoActivity.this;
                    importAlbumsGalleryPhotoActivity.galleryImagesAdapter = new GalleryPhotoAdapter(importAlbumsGalleryPhotoActivity2, 1, importAlbumsGalleryPhotoActivity2.photoImportEntListShow);
                    ImportAlbumsGalleryPhotoActivity.this.imagegrid.setAdapter((ListAdapter) ImportAlbumsGalleryPhotoActivity.this.galleryImagesAdapter);
                } else {
                    ImportAlbumsGalleryPhotoActivity.this.btnSelectAll.setEnabled(false);
                    ImportAlbumsGalleryPhotoActivity.this.ll_Import_bottom_baar.setEnabled(false);
                }
            } else if (message.what == 4) {
                ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(8);
                ImportAlbumsGalleryPhotoActivity.this.GetAlbumsFromGallery();
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        ImportAlbumsGalleryPhotoActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        ImportAlbumsGalleryPhotoActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    if (ImportAlbumsGalleryPhotoActivity.this.IsExceptionInImportPhotos) {
                        ImportAlbumsGalleryPhotoActivity.this.IsExceptionInImportPhotos = false;
                    } else {
                        Toast.makeText(ImportAlbumsGalleryPhotoActivity.this, ImportAlbumsGalleryPhotoActivity.this.selectCount + " photo(s) imported successfully", 0).show();
                    }
                    ImportAlbumsGalleryPhotoActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        IsPhoneGalleryLoad = true;
                        if (Common.IsCameFromPhotoAlbum) {
                            if (ImportAlbumsGalleryPhotoActivity.this.isAlbumClick) {
                                intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, Photos_Gallery_Actitvity.class);
                            } else {
                                intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, PhotosAlbumActivty.class);
                            }
                        } else if (Common.IsCameFromGalleryFeature) {
                            Common.IsCameFromGalleryFeature = false;
                            intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, GalleryActivity.class);
                        } else if (ImportAlbumsGalleryPhotoActivity.this.isAlbumClick) {
                            intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, Photos_Gallery_Actitvity.class);
                        } else {
                            intent = new Intent(ImportAlbumsGalleryPhotoActivity.this, PhotosAlbumActivty.class);
                        }
                        intent.addFlags(67108864);
                        ImportAlbumsGalleryPhotoActivity.this.startActivity(intent);
                        ImportAlbumsGalleryPhotoActivity.this.finish();
                    }
                }
                if (Common.InterstitialAdCount <= 1) {
                    Common.InterstitialAdCount++;
                }
            } else if (message.what == 2) {
                ImportAlbumsGalleryPhotoActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RefershGalleryforKitkat() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.3
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being encrypted... this may take a few moments... ", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.import_album_list_activity);
        Log.d("TAG", "ImportAlbumsGalleryPhotoActivity");
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        IsPhoneGalleryLoad = false;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
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
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.folderId = Common.FolderId;
        this.folderName = null;
        if (0 == 0) {
            this.folderId = Common.FolderId;
            PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
            photoAlbumDAL.OpenRead();
            PhotoAlbum GetAlbumById = photoAlbumDAL.GetAlbumById(Integer.toString(Common.FolderId));
            photoAlbumDAL.close();
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
        new DataLoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        this.ll_Import_bottom_baar.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImportAlbumsGalleryPhotoActivity.this.OnImportClick();
            }
        });
        this.album_import_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(0);
                ImportAlbumsGalleryPhotoActivity.this.isAlbumClick = true;
                ImportAlbumsGalleryPhotoActivity.this.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_photo_album_select_photo_topbaar);
                ImportAlbumsGalleryPhotoActivity.this.adapter = new AlbumsImportAdapter(ImportAlbumsGalleryPhotoActivity.this.context, 17367043, ImportAlbumsGalleryPhotoActivity.this.importAlbumEnts, false, false);
                ImportAlbumsGalleryPhotoActivity.this.album_import_ListView.setAdapter((ListAdapter) ImportAlbumsGalleryPhotoActivity.this.adapter);
                ImportAlbumsGalleryPhotoActivity.this.ShowAlbumData(i);
                ImportAlbumsGalleryPhotoActivity.this.imagegrid.setVisibility(0);
                ImportAlbumsGalleryPhotoActivity.this.btnSelectAll.setVisibility(0);
                ImportAlbumsGalleryPhotoActivity.this.album_import_ListView.setVisibility(4);
            }
        });
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity$6] */
    void LoadData() {
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.6
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    ImportAlbumsGalleryPhotoActivity.this.loadGallery();
                    Message message = new Message();
                    message.what = 4;
                    ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
        Iterator<ImportEnt> it = this.photoImportEntList.iterator();
        while (it.hasNext()) {
            ImportEnt next = it.next();
            if (this.spinnerValues.get(0).contains(new File(next.GetPath()).getParent())) {
                this.photoImportEntListShow.add(next);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity$7] */
    void ShowAlbumData(final int i) {
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.7
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.clear();
                    Iterator it = ImportAlbumsGalleryPhotoActivity.this.photoImportEntList.iterator();
                    while (it.hasNext()) {
                        ImportEnt importEnt = (ImportEnt) it.next();
                        if (ImportAlbumsGalleryPhotoActivity.this.spinnerValues.get(i).equals(new File(importEnt.GetPath()).getParent())) {
                            importEnt.GetThumbnailSelection().booleanValue();
                            ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.add(importEnt);
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    ImportAlbumsGalleryPhotoActivity.this.Progresshowhandler.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 1;
                    ImportAlbumsGalleryPhotoActivity.this.Progresshowhandler.sendMessage(message2);
                }
            }
        }.start();
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

    public void OnImportClick() {
        final StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_import, 0).show();
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
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.8
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                            ImportAlbumsGalleryPhotoActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            ImportAlbumsGalleryPhotoActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            final Dialog dialog2 = new Dialog(ImportAlbumsGalleryPhotoActivity.this, R.style.FullHeightDialog);
                            dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                            dialog2.setCancelable(false);
                            dialog2.setCanceledOnTouchOutside(false);
                            ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.8.1
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    GetObject.SetISDAlertshow(Boolean.valueOf(z));
                                }
                            });
                            ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.8.2
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view2) {
                                    dialog2.dismiss();
                                    ImportAlbumsGalleryPhotoActivity.this.Import();
                                }
                            });
                            dialog2.show();
                        } else {
                            ImportAlbumsGalleryPhotoActivity.this.Import();
                        }
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.9
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        for (int i = 0; i < ImportAlbumsGalleryPhotoActivity.this.importAlbumEnts.size(); i++) {
                            ImportAlbumsGalleryPhotoActivity.this.importAlbumEnts.get(i).SetAlbumFileCheck(false);
                        }
                        ImportAlbumsGalleryPhotoActivity.this.GetAlbumsFromGallery();
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
                ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.10
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        GetObject.SetISDAlertshow(Boolean.valueOf(z));
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.11
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        dialog2.dismiss();
                        ImportAlbumsGalleryPhotoActivity.this.Import();
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.12
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

    /* JADX WARN: Type inference failed for: r0v1, types: [net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity$14] */
    void Import() {
        if (!Common.IsCameFromGalleryFeature || !this.isAlbumClick) {
            SelectedCount();
            ShowImportProgress();
            Common.IsWorkInProgress = true;
            new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.14
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        ImportAlbumsGalleryPhotoActivity.this.ImportPhotos();
                        Message message = new Message();
                        message.what = 3;
                        ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message);
                        Common.IsWorkInProgress = false;
                    } catch (Exception unused) {
                        Message message2 = new Message();
                        message2.what = 3;
                        ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message2);
                    }
                }
            }.start();
            return;
        }
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        final List<PhotoAlbum> GetAlbums = photoAlbumDAL.GetAlbums(0);
        photoAlbumDAL.close();
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.share_from_gallery);
        ((LinearLayout) dialog.findViewById(R.id.ll_bottom_bar)).setVisibility(8);
        ((TextView) dialog.findViewById(R.id.lbl_import_title)).setText("Select Album to Import Photo(s)");
        GridView gridView = (GridView) dialog.findViewById(R.id.fileListgrid);
        gridView.setNumColumns(1);
        SelectAlbumInImportAdapter selectAlbumInImportAdapter = new SelectAlbumInImportAdapter(this, 1, GetAlbums);
        gridView.setAdapter((ListAdapter) selectAlbumInImportAdapter);
        selectAlbumInImportAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.13
            /* JADX WARN: Type inference failed for: r1v6, types: [net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity$13$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ImportAlbumsGalleryPhotoActivity.this.folderId = ((PhotoAlbum) GetAlbums.get(i)).getId();
                dialog.dismiss();
                ImportAlbumsGalleryPhotoActivity.this.SelectedCount();
                ImportAlbumsGalleryPhotoActivity.this.ShowImportProgress();
                Common.IsWorkInProgress = true;
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.ImportAlbumsGalleryPhotoActivity.13.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            ImportAlbumsGalleryPhotoActivity.this.ImportOnlyPhotosSDCard();
                            Message message = new Message();
                            message.what = 3;
                            ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            ImportAlbumsGalleryPhotoActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    public void ImportPhotos() {
        if (this.isAlbumClick) {
            ImportOnlyPhotosSDCard();
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
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + file.getName());
                    this.folderName = file.getName();
                    if (file2.exists()) {
                        int i3 = 1;
                        while (i3 < 100) {
                            this.folderName = file.getName() + "(" + i3 + ")";
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.PHOTOS);
                            sb.append(this.folderName);
                            file2 = new File(sb.toString());
                            if (!file2.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                    }
                    AddAlbumToDatabase(this.folderName);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this);
                    photoAlbumDAL.OpenRead();
                    int GetLastAlbumId = photoAlbumDAL.GetLastAlbumId();
                    this.folderId = GetLastAlbumId;
                    Common.FolderId = GetLastAlbumId;
                    photoAlbumDAL.close();
                    ImportAlbumsPhotosSDCard(i);
                    i++;
                }
            }
        }
    }

    void ImportAlbumsPhotosSDCard(int i) {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List<ImportEnt> list = this.photoImportEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (list.get(i2).GetThumbnailSelection().booleanValue()) {
                File file = new File(list.get(i2).GetPath());
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + this.folderName + "/"));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (NSHideFile.length() > 0) {
                        AddPhotoToDatabase(FileName(list.get(i2).GetPath()), list.get(i2).GetPath(), NSHideFile);
                    }
                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                    try {
                        ContentResolver contentResolver = getContentResolver();
                        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        contentResolver.delete(uri, "_data='" + file.getPath() + "'", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e2) {
                    this.IsExceptionInImportPhotos = true;
                    e2.printStackTrace();
                }
            }
        }
    }

    void ImportOnlyPhotosSDCard() {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.photoImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (this.photoImportEntListShow.get(i).GetThumbnailSelection().booleanValue()) {
                File file = new File(this.photoImportEntListShow.get(i).GetPath());
                Log.e("TAG", "ImportOnlyPhotosSDCard: " + this.photoImportEntListShow.get(i).GetPath());
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + this.folderName + "/"));
                    Log.e("TAG", "NSHideFile: " + NSHideFile);
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                    try {
                        ContentResolver contentResolver = getContentResolver();
                        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        contentResolver.delete(uri, "_data='" + file.getPath() + "'", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (NSHideFile.length() > 0) {
                        AddPhotoToDatabase(FileName(this.photoImportEntListShow.get(i).GetPath()), this.photoImportEntListShow.get(i).GetPath(), NSHideFile);
                    }
                } catch (IOException e2) {
                    this.IsExceptionInImportPhotos = true;
                    e2.printStackTrace();
                    Log.e("TAG", "ImportOnlyPhotosSDCard: " + e2.getMessage());
                }
            }
        }
    }

    void AddPhotoToDatabase(String str, String str2, String str3) {
        Log.d("Path", str3);
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str3);
        photo.setOriginalPhotoLocation(str2);
        photo.setAlbumId(this.folderId);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
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

    public void AddAlbumToDatabase(String str) {
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setAlbumName(str);
        photoAlbum.setAlbumLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + str);
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this);
        try {
            try {
                photoAlbumDAL.OpenWrite();
                photoAlbumDAL.AddPhotoAlbum(photoAlbum);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            photoAlbumDAL.close();
        }
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (this.importAlbumEnts.get(i).GetAlbumFileCheck()) {
                this.photoImportEntListShow = new ArrayList<>();
                Iterator<ImportEnt> it = this.photoImportEntList.iterator();
                while (it.hasNext()) {
                    ImportEnt next = it.next();
                    if (this.spinnerValues.get(i).equals(new File(next.GetPath()).getParent())) {
                        this.photoImportEntListShow.add(next);
                    }
                    for (int i2 = 0; i2 < this.photoImportEntListShow.size(); i2++) {
                        this.photoImportEntListShow.get(i2).SetThumbnailSelection(true);
                    }
                }
                this.photoImportEntListShowList.add(this.photoImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.photoImportEntListShow.size(); i3++) {
            if (this.photoImportEntListShow.get(i3).GetThumbnailSelection().booleanValue()) {
                this.selectPath.add(this.photoImportEntListShow.get(i3).GetPath());
                return true;
            }
        }
        return false;
    }

    public void GetAlbumsFromGallery() {
        AlbumsImportAdapter albumsImportAdapter = new AlbumsImportAdapter(this.context, 17367043, this.importAlbumEnts, false, false);
        this.adapter = albumsImportAdapter;
        this.album_import_ListView.setAdapter((ListAdapter) albumsImportAdapter);
        this.adapter.notifyDataSetChanged();
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(4);
            this.imagegrid.setVisibility(4);
            this.btnSelectAll.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.photoImportEntListShow.size(); i++) {
            if (this.photoImportEntListShow.get(i).GetThumbnailSelection().booleanValue()) {
                this.selectCount++;
            }
        }
    }

    public void btnSelectAllonClick(View view) {
        SelectAllPhotos();
        GalleryPhotoAdapter galleryPhotoAdapter = new GalleryPhotoAdapter(this.context, 1, this.photoImportEntListShow);
        this.galleryImagesAdapter = galleryPhotoAdapter;
        this.imagegrid.setAdapter((ListAdapter) galleryPhotoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    private void SelectAllPhotos() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.photoImportEntListShow.size(); i++) {
                this.photoImportEntListShow.get(i).SetThumbnailSelection(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.photoImportEntListShow.size(); i2++) {
            this.photoImportEntListShow.get(i2).SetThumbnailSelection(true);
        }
        this.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
    }

    public void loadGallery() {
        try {
            Cursor managedQuery = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, null, null, "_id");
            this.imagecursor = managedQuery;
            this.image_column_index = managedQuery.getColumnIndex("_id");
            this.count = this.imagecursor.getCount();
            for (int i = 0; i < this.count; i++) {
                this.imagecursor.moveToPosition(i);
                int columnIndex = this.imagecursor.getColumnIndex("_data");
                if (new File(this.imagecursor.getString(columnIndex)).exists()) {
                    ImportEnt importEnt = new ImportEnt();
                    importEnt.SetPath(this.imagecursor.getString(columnIndex));
                    importEnt.SetThumbnailSelection(false);
                    importEnt.SetThumbnail(null);
                    this.photoImportEntList.add(importEnt);
                    ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
                    File file = new File(importEnt.GetPath());
                    if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(file.getParent())) {
                        importAlbumEnt.SetAlbumName(file.getParent());
                        importAlbumEnt.SetPath(this.imagecursor.getString(columnIndex));
                        this.importAlbumEnts.add(importAlbumEnt);
                        this.spinnerValues.add(file.getParent());
                    }
                }
            }
        } catch (Exception unused) {
        }
        if (this.photoImportEntList.size() <= 0) {
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
            for (int i = 0; i < this.photoImportEntListShow.size(); i++) {
                this.photoImportEntListShow.get(i).SetThumbnailSelection(false);
            }
            this.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        if (Common.IsCameFromPhotoAlbum) {
            Common.IsCameFromPhotoAlbum = false;
            intent = new Intent(this, PhotosAlbumActivty.class);
        } else if (Common.IsCameFromGalleryFeature) {
            Common.IsCameFromGalleryFeature = false;
            intent = new Intent(this, GalleryActivity.class);
        } else {
            intent = new Intent(this, Photos_Gallery_Actitvity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Intent intent;
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.lbl_import_photo_album_topbaar.setText("Import Albums");
                this.album_import_ListView.setVisibility(0);
                this.imagegrid.setVisibility(4);
                this.btnSelectAll.setVisibility(4);
                for (int i2 = 0; i2 < this.photoImportEntListShow.size(); i2++) {
                    this.photoImportEntListShow.get(i2).SetThumbnailSelection(false);
                }
                this.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            if (Common.IsCameFromPhotoAlbum) {
                intent = new Intent(this, PhotosAlbumActivty.class);
            } else if (Common.IsCameFromGalleryFeature) {
                Common.IsCameFromGalleryFeature = false;
                intent = new Intent(this, GalleryActivity.class);
            } else {
                intent = new Intent(this, Photos_Gallery_Actitvity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* loaded from: classes2.dex */
    private class DataLoadTask extends AsyncTask<Void, Void, Void> {
        private DataLoadTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            ImportAlbumsGalleryPhotoActivity.this.loadGallery();
            Iterator it = ImportAlbumsGalleryPhotoActivity.this.photoImportEntList.iterator();
            while (it.hasNext()) {
                ImportEnt importEnt = (ImportEnt) it.next();
                if (ImportAlbumsGalleryPhotoActivity.this.spinnerValues.get(0).contains(new File(importEnt.GetPath()).getParent())) {
                    ImportAlbumsGalleryPhotoActivity.this.photoImportEntListShow.add(importEnt);
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(Void r2) {
            ImportAlbumsGalleryPhotoActivity.this.Progress.setVisibility(8);
            ImportAlbumsGalleryPhotoActivity.this.GetAlbumsFromGallery();
        }
    }
}
