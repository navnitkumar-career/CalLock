package com.example.gallerylock.gallery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.photo.ImportAlbumsGalleryPhotoActivity;
import com.example.gallerylock.photo.NewFullScreenViewActivity;
import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.ImportAlbumsGalleryVideoActivity;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoDAL;
import com.getbase.floatingactionbutton.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class GalleryActivity extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    private static int _ViewBy = 1;
    public static ProgressDialog myProgressDialog;
    private ImageButton _btnSortingDropdown;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private ImageButton btnSelectAll;
    private FrameLayout fl_top_baar;
    GalleryFeatureAdapter galleryFeatureAdapter;
    private GridView galleryGrid;
    private TextView lbl_photo_video_empty;
    private LinearLayout ll_AddInGallery_Baar;
    private LinearLayout ll_Edit;
    private RelativeLayout.LayoutParams ll_GridviewParams;
    private LinearLayout.LayoutParams ll_Hide_Params;
    private LinearLayout.LayoutParams ll_Show_Params;
    private LinearLayout ll_anchor;
    private LinearLayout ll_background;
    private LinearLayout ll_delete_btn;
    private LinearLayout ll_import_Photos_from_gallery_btn;
    private LinearLayout ll_import_videos_from_gallery_btn;
    private LinearLayout ll_photo_video_empty;
    private LinearLayout ll_photo_video_grid;
    private LinearLayout ll_share_btn;
    private LinearLayout ll_topbaar;
    private LinearLayout ll_unhide_btn;
    private Uri outputFileUri;
    private ImageView photo_video_empty_icon;
    int selectCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    private List<GalleryEnt> mGalleryGirdList = new ArrayList();
    private ArrayList<String> files = new ArrayList<>();
    private ArrayList<String> mPhotosList = new ArrayList<>();
    private boolean isEditMode = false;
    private boolean isAddbarvisible = false;
    private boolean IsSortingDropdown = false;
    private int _SortBy = 1;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                GalleryActivity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(GalleryActivity.this, (int) R.string.Unhide_error, 0).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(GalleryActivity.this, (int) R.string.Delete_error, 0).show();
                }
            } else if (message.what == 4) {
                Toast.makeText(GalleryActivity.this, (int) R.string.toast_share, 1).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        GalleryActivity galleryActivity = GalleryActivity.this;
                        galleryActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        GalleryActivity.this.RefershGalleryforKitkat();
                    }
                    Common.isUnHide = false;
                    Toast.makeText(GalleryActivity.this, (int) R.string.toast_unhide, 1).show();
                    GalleryActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(GalleryActivity.this, GalleryActivity.class);
                        intent.addFlags(67108864);
                        GalleryActivity.this.startActivity(intent);
                        GalleryActivity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(GalleryActivity.this, (int) R.string.toast_delete, 0).show();
                    GalleryActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(GalleryActivity.this, GalleryActivity.class);
                        intent2.addFlags(67108864);
                        GalleryActivity.this.startActivity(intent2);
                        GalleryActivity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    /* loaded from: classes2.dex */
    private enum SortBy {
        Name,
        Time,
        Size
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum ViewBy {
        LargeTiles,
        Tiles,
        List
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUnhideProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDeleteProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    private void showMoveProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    private void showIsImportingProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    private void showCopyFilesProcessForShareProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RefershGalleryforKitkat() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.2
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.gallery_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
//        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
       /* setSupportActionBar(this.toolbar);
        getSupportActionBar();
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                GalleryActivity.this.Back();
            }
        });*/
        this.galleryGrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-2, 0);
        this.ll_GridviewParams = new RelativeLayout.LayoutParams(-1, -1);
        AppSettingsSharedPreferences GetObject = AppSettingsSharedPreferences.GetObject(this);
        this.appSettingsSharedPreferences = GetObject;
        this._SortBy = GetObject.GetGallerySortBy();
        _ViewBy = this.appSettingsSharedPreferences.GetGalleryViewBy();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_top_baar);
        this.fl_top_baar = frameLayout;
        frameLayout.setLayoutParams(this.ll_Hide_Params);
        this.ll_Edit = (LinearLayout) findViewById(R.id.ll_Edit);
        this.ll_AddInGallery_Baar = (LinearLayout) findViewById(R.id.ll_AddInGallery_Baar);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_photo_video_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_photo_video_grid.setVisibility(0);
        this.ll_photo_video_empty.setVisibility(4);
        this.ll_import_Photos_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_Photo_gallery_btn);
        this.ll_import_videos_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_video_gallery_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this._btnSortingDropdown = (ImageButton) findViewById(R.id.btnSort);
        this.galleryGrid.setOnItemClickListener(new galleryListners());
        this.galleryGrid.setOnItemLongClickListener(new galleryListners());
        this.ll_import_Photos_from_gallery_btn.setOnClickListener(new galleryListners());
        this.ll_import_videos_from_gallery_btn.setOnClickListener(new galleryListners());
        this.ll_delete_btn.setOnClickListener(new galleryListners());
        this.ll_unhide_btn.setOnClickListener(new galleryListners());
        this.ll_share_btn.setOnClickListener(new galleryListners());
        this.ll_background.setOnTouchListener(new galleryListners());
        this.btnSelectAll.setOnClickListener(new galleryListners());
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        LoadGalleryFilesFromDB(this._SortBy);
    }

    private void LoadGalleryFilesFromDB(int i) {
        new ArrayList();
        PhotoDAL photoDAL = new PhotoDAL(this);
        photoDAL.OpenRead();
        List<Photo> GetPhotos = photoDAL.GetPhotos();
        photoDAL.close();
        for (Photo photo : GetPhotos) {
            GalleryEnt galleryEnt = new GalleryEnt();
            galleryEnt.set_albumId(photo.getAlbumId());
            galleryEnt.set_folderLockgalleryfileLocation(photo.getFolderLockPhotoLocation());
            galleryEnt.set_galleryfileName(photo.getPhotoName());
            galleryEnt.set_id(photo.getId());
            galleryEnt.set_isCheck(false);
            galleryEnt.set_isVideo(false);
            galleryEnt.set_modifiedDateTime(photo.get_modifiedDateTime());
            galleryEnt.set_originalgalleryfileLocation(photo.getOriginalPhotoLocation());
            galleryEnt.set_thumbnail_video_location("");
            this.mGalleryGirdList.add(galleryEnt);
        }
        new ArrayList();
        VideoDAL videoDAL = new VideoDAL(this);
        videoDAL.OpenRead();
        List<Video> GetVideos = videoDAL.GetVideos();
        videoDAL.close();
        for (Video video : GetVideos) {
            GalleryEnt galleryEnt2 = new GalleryEnt();
            galleryEnt2.set_albumId(video.getAlbumId());
            galleryEnt2.set_folderLockgalleryfileLocation(video.getFolderLockVideoLocation());
            galleryEnt2.set_galleryfileName(video.getVideoName());
            galleryEnt2.set_id(video.getId());
            galleryEnt2.set_isCheck(false);
            galleryEnt2.set_isVideo(true);
            galleryEnt2.set_modifiedDateTime(video.get_modifiedDateTime());
            galleryEnt2.set_originalgalleryfileLocation(video.getOriginalVideoLocation());
            galleryEnt2.set_thumbnail_video_location(video.getthumbnail_video_location());
            this.mGalleryGirdList.add(galleryEnt2);
        }
        Collections.sort(this.mGalleryGirdList, new GalleryFileSort(i));
        Collections.reverse(this.mGalleryGirdList);
        for (GalleryEnt galleryEnt3 : this.mGalleryGirdList) {
            if (!galleryEnt3.get_isVideo().booleanValue()) {
                this.mPhotosList.add(galleryEnt3.get_folderLockgalleryfileLocation());
            }
        }
        GalleryFeatureAdapter galleryFeatureAdapter = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, false, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter;
        this.galleryGrid.setAdapter((ListAdapter) galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
        if (this.mGalleryGirdList.size() < 1) {
            this.ll_photo_video_grid.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_gallery_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.lbl_No_Gallery_File);
            return;
        }
        this.ll_photo_video_grid.setVisibility(0);
        this.ll_photo_video_empty.setVisibility(4);
    }

    public void UnhideGalleryFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_unhide, 0).show();
            return;
        }
        SelectedCount();
        if (Common.GetTotalFree() > Common.GetFileSize(this.files)) {
            final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.confirmation_message_box);
            dialog.setCancelable(true);
            Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(createFromAsset);
            textView.setText("Are you sure you want to restore (" + this.selectCount + ") photo(s) or video(s)?");
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.4
                /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity$4$1] */
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    GalleryActivity.this.showUnhideProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.4.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                GalleryActivity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                GalleryActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                GalleryActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    void Unhide() throws IOException {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (this.mGalleryGirdList.get(i).get_isCheck().booleanValue()) {
                if (!Utilities.NSUnHideFile(this, this.mGalleryGirdList.get(i).get_folderLockgalleryfileLocation(), this.mGalleryGirdList.get(i).get_originalgalleryfileLocation())) {
                    Toast.makeText(this, (int) R.string.Unhide_error, 0).show();
                } else if (this.mGalleryGirdList.get(i).get_isVideo().booleanValue()) {
                    DeleteVideosFromDatabase(this.mGalleryGirdList.get(i).get_id());
                } else {
                    DeletePhotosFromDatabase(this.mGalleryGirdList.get(i).get_id());
                }
            }
        }
    }

    public void DeleteGalleryFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_delete, 0).show();
            return;
        }
        SelectedCount();
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        textView.setText("Are you sure you want to delete (" + this.selectCount + ") photo(s) or video(s)?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.6
            /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity$6$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                GalleryActivity.this.showDeleteProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.6.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            GalleryActivity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            GalleryActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            GalleryActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (this.mGalleryGirdList.get(i).get_isCheck().booleanValue()) {
                this.files.add(this.mGalleryGirdList.get(i).get_folderLockgalleryfileLocation());
                this.selectCount++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean IsFileCheck() {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (this.mGalleryGirdList.get(i).get_isCheck().booleanValue()) {
                return true;
            }
        }
        return false;
    }

    void Delete() {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            if (this.mGalleryGirdList.get(i).get_isCheck().booleanValue()) {
                new File(this.mGalleryGirdList.get(i).get_folderLockgalleryfileLocation()).delete();
                new File(this.mGalleryGirdList.get(i).get_thumbnail_video_location()).delete();
                if (this.mGalleryGirdList.get(i).get_isVideo().booleanValue()) {
                    DeleteVideosFromDatabase(this.mGalleryGirdList.get(i).get_id());
                } else {
                    DeletePhotosFromDatabase(this.mGalleryGirdList.get(i).get_id());
                }
            }
        }
    }

    public void DeletePhotosFromDatabase(int i) {
        PhotoDAL photoDAL = new PhotoDAL(this);
        try {
            try {
                photoDAL.OpenWrite();
                photoDAL.DeletePhotoById(i);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            photoDAL.close();
        }
    }

    public void DeleteVideosFromDatabase(int i) {
        VideoDAL videoDAL = new VideoDAL(this);
        try {
            try {
                videoDAL.OpenWrite();
                videoDAL.DeleteVideoById(i);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoDAL.close();
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity$8] */
    public void ShareGalleryFiles() {
        showCopyFilesProcessForShareProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.8
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : GalleryActivity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageCommon.AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TEMPFILES;
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList<Uri> arrayList3 = new ArrayList<>();
                            for (int i = 0; i < GalleryActivity.this.mGalleryGirdList.size(); i++) {
                                if (((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_isCheck().booleanValue()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(GalleryActivity.this, ((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(GalleryActivity.this, BuildConfig.APPLICATION_ID, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    GalleryActivity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    GalleryActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    GalleryActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void DeleteTemporaryGalleryFiles() {
        File[] listFiles;
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TEMPFILES + "/");
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isFile()) {
                    file2.delete();
                }
            }
        }
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
            this.mGalleryGirdList.get(i).set_isCheck(false);
        }
        GalleryFeatureAdapter galleryFeatureAdapter = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, false, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter;
        this.galleryGrid.setAdapter((ListAdapter) galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
        if (Common.GalleryThumbnailCurrentPosition != 0) {
            this.galleryGrid.setSelection(Common.GalleryThumbnailCurrentPosition);
            Common.GalleryThumbnailCurrentPosition = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
                this.mGalleryGirdList.get(i).set_isCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.mGalleryGirdList.size(); i2++) {
                this.mGalleryGirdList.get(i2).set_isCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        GalleryFeatureAdapter galleryFeatureAdapter = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, true, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter;
        this.galleryGrid.setAdapter((ListAdapter) galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
    }

    public void setUIforlistView() {
        this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 1));
        this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 0));
        this.ll_GridviewParams.setMargins(0, 0, 0, 0);
        this.ll_photo_video_grid.setLayoutParams(this.ll_GridviewParams);
        this.galleryGrid.setNumColumns(1);
    }

    public void btnSortonClick(View view) {
        this.IsSortingDropdown = false;
        showPopupWindow();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService("layout_inflater")).inflate(R.layout.popup_window_expandable, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.expListview);
        arrayList.add("View by");
        arrayList2.add("List");
        arrayList2.add("Tiles");
        arrayList2.add("Large tiles");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        arrayList3.add("Size");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.gallery.GalleryActivity.9
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        int unused = GalleryActivity._ViewBy = ViewBy.List.ordinal();
                        GalleryActivity.this.ViewBy();
                        popupWindow.dismiss();
                        GalleryActivity.this.IsSortingDropdown = false;
                        GalleryActivity.this.appSettingsSharedPreferences.SetGalleryViewBy(GalleryActivity._ViewBy);
                    } else if (i2 == 1) {
                        int unused2 = GalleryActivity._ViewBy = ViewBy.Tiles.ordinal();
                        GalleryActivity.this.ViewBy();
                        popupWindow.dismiss();
                        GalleryActivity.this.IsSortingDropdown = false;
                        GalleryActivity.this.appSettingsSharedPreferences.SetGalleryViewBy(GalleryActivity._ViewBy);
                    } else if (i2 == 2) {
                        int unused3 = GalleryActivity._ViewBy = ViewBy.LargeTiles.ordinal();
                        GalleryActivity.this.ViewBy();
                        popupWindow.dismiss();
                        GalleryActivity.this.IsSortingDropdown = false;
                        GalleryActivity.this.appSettingsSharedPreferences.SetGalleryViewBy(GalleryActivity._ViewBy);
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        GalleryActivity.this._SortBy = SortBy.Name.ordinal();
                        GalleryActivity.this.SortGalleryFiles();
                        GalleryActivity.this.AddSortInSharedPerference();
                        popupWindow.dismiss();
                        GalleryActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        GalleryActivity.this._SortBy = SortBy.Time.ordinal();
                        GalleryActivity.this.SortGalleryFiles();
                        GalleryActivity.this.AddSortInSharedPerference();
                        popupWindow.dismiss();
                        GalleryActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        GalleryActivity.this._SortBy = SortBy.Size.ordinal();
                        GalleryActivity.this.SortGalleryFiles();
                        GalleryActivity.this.AddSortInSharedPerference();
                        popupWindow.dismiss();
                        GalleryActivity.this.IsSortingDropdown = false;
                    }
                }
                return false;
            }
        });
        if (!this.IsSortingDropdown) {
            LinearLayout linearLayout = this.ll_anchor;
            popupWindow.showAsDropDown(linearLayout, linearLayout.getWidth(), 0);
            this.IsSortingDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsSortingDropdown = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SortGalleryFiles() {
        Collections.sort(this.mGalleryGirdList, new GalleryFileSort(this._SortBy));
        Collections.reverse(this.mGalleryGirdList);
        this.galleryFeatureAdapter.SetGalleryFilesList(this.mGalleryGirdList);
        this.galleryFeatureAdapter.notifyDataSetChanged();
        for (GalleryEnt galleryEnt : this.mGalleryGirdList) {
            if (!galleryEnt.get_isVideo().booleanValue()) {
                this.mPhotosList.add(galleryEnt.get_folderLockgalleryfileLocation());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AddSortInSharedPerference() {
        this.appSettingsSharedPreferences.SetGallerySortBy(this._SortBy);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        }
    }

    public void ViewBy() {
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(3);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                    this.galleryGrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.galleryGrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.galleryGrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.galleryGrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        GalleryFeatureAdapter galleryFeatureAdapter = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, false, _ViewBy);
        this.galleryFeatureAdapter = galleryFeatureAdapter;
        this.galleryGrid.setAdapter((ListAdapter) galleryFeatureAdapter);
        this.galleryFeatureAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    public void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddInGallery_Baar.setVisibility(4);
            this.ll_Edit.setVisibility(4);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(4);
            this._btnSortingDropdown.setVisibility(0);
            invalidateOptionsMenu();
        } else if (this.isAddbarvisible) {
            this.isAddbarvisible = false;
            this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddInGallery_Baar.setVisibility(4);
            this.ll_Edit.setVisibility(4);
            this.IsSortingDropdown = false;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.PhotoFolderName = "My Photos";
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        }
        DeleteTemporaryGalleryFiles();
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        Common.IsWorkInProgress = true;
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
        this.handle.removeCallbacksAndMessages(null);
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
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
        SetcheckFlase();
        Utilities.changeFileExtention(StorageOptionsCommon.VIDEOS);
        this.isEditMode = false;
        this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddInGallery_Baar.setVisibility(4);
        this.ll_Edit.setVisibility(4);
        this.IsSelectAll = false;
        this.btnSelectAll.setVisibility(4);
        invalidateOptionsMenu();
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        Common.imageLoader.clearMemoryCache();
        Common.imageLoader.clearDiskCache();
        super.onStop();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Common.isOpenCameraorGalleryFromApp = false;
            if (this.isEditMode) {
                SetcheckFlase();
                this.IsSortingDropdown = false;
                this.isEditMode = false;
                this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddInGallery_Baar.setVisibility(4);
                this.ll_Edit.setVisibility(4);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(4);
                this._btnSortingDropdown.setVisibility(0);
                invalidateOptionsMenu();
                return true;
            } else if (this.isAddbarvisible) {
                this.isAddbarvisible = false;
                this.fl_top_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddInGallery_Baar.setVisibility(4);
                this.ll_Edit.setVisibility(4);
                this.IsSortingDropdown = false;
                return true;
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = 0;
                Common.PhotoFolderName = "My Photos";
                startActivity(new Intent(this, FeaturesActivity.class));
                finish();
                DeleteTemporaryGalleryFiles();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* loaded from: classes2.dex */
    private class galleryListners implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnTouchListener {
        private galleryListners() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnAdd /* 2131296366 */:
                    GalleryActivity.this.isAddbarvisible = true;
                    GalleryActivity.this.fl_top_baar.setLayoutParams(GalleryActivity.this.ll_Show_Params);
                    GalleryActivity.this.ll_AddInGallery_Baar.setVisibility(0);
                    GalleryActivity.this.ll_Edit.setVisibility(4);
                    GalleryActivity.this.IsSortingDropdown = true;
                    return;
                case R.id.btnSelectAll /* 2131296370 */:
                    GalleryActivity.this.SelectOrUnSelectAll();
                    return;
                case R.id.ll_delete_btn /* 2131296782 */:
                    GalleryActivity.this.DeleteGalleryFiles();
                    return;
                case R.id.ll_import_from_Photo_gallery_btn /* 2131296797 */:
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.IsCameFromPhotoAlbum = false;
                    Common.IsCameFromGalleryFeature = true;
                    GalleryActivity.this.startActivity(new Intent(GalleryActivity.this, ImportAlbumsGalleryPhotoActivity.class));
                    GalleryActivity.this.finish();
                    return;
                case R.id.ll_import_from_video_gallery_btn /* 2131296800 */:
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.IsCameFromPhotoAlbum = false;
                    Common.IsCameFromGalleryFeature = true;
                    GalleryActivity.this.startActivity(new Intent(GalleryActivity.this, ImportAlbumsGalleryVideoActivity.class));
                    GalleryActivity.this.finish();
                    return;
                case R.id.ll_share_btn /* 2131296840 */:
                    if (!GalleryActivity.this.IsFileCheck()) {
                        Toast.makeText(GalleryActivity.this, (int) R.string.toast_unselectphotovideomsg_share, 0).show();
                        return;
                    } else {
                        GalleryActivity.this.ShareGalleryFiles();
                        return;
                    }
                case R.id.ll_unhide_btn /* 2131296854 */:
                    GalleryActivity.this.UnhideGalleryFiles();
                    return;
                default:
                    return;
            }
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            Common.GalleryThumbnailCurrentPosition = GalleryActivity.this.galleryGrid.getFirstVisiblePosition();
            GalleryActivity.this.isEditMode = true;
            GalleryActivity.this.fl_top_baar.setLayoutParams(GalleryActivity.this.ll_Show_Params);
            GalleryActivity.this.ll_AddInGallery_Baar.setVisibility(4);
            GalleryActivity.this.ll_Edit.setVisibility(0);
            GalleryActivity.this._btnSortingDropdown.setVisibility(4);
            GalleryActivity.this.btnSelectAll.setVisibility(0);
            GalleryActivity.this.invalidateOptionsMenu();
            ((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).set_isCheck(true);
            GalleryActivity galleryActivity = GalleryActivity.this;
            GalleryActivity galleryActivity2 = GalleryActivity.this;
            galleryActivity.galleryFeatureAdapter = new GalleryFeatureAdapter(galleryActivity2, 1, galleryActivity2.mGalleryGirdList, true, GalleryActivity._ViewBy);
            GalleryActivity.this.galleryGrid.setAdapter((ListAdapter) GalleryActivity.this.galleryFeatureAdapter);
            GalleryActivity.this.galleryFeatureAdapter.notifyDataSetChanged();
            if (Common.GalleryThumbnailCurrentPosition != 0) {
                GalleryActivity.this.galleryGrid.setSelection(Common.GalleryThumbnailCurrentPosition);
            }
            return true;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Common.GalleryThumbnailCurrentPosition = GalleryActivity.this.galleryGrid.getFirstVisiblePosition();
            if (GalleryActivity.this.isEditMode) {
                return;
            }
            if (((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_isVideo().booleanValue()) {
                GalleryActivity galleryActivity = GalleryActivity.this;
                galleryActivity.PlayVideo(((GalleryEnt) galleryActivity.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation());
                return;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= GalleryActivity.this.mPhotosList.size()) {
                    i2 = 0;
                    break;
                } else if (((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation().endsWith((String) GalleryActivity.this.mPhotosList.get(i2))) {
                    break;
                } else {
                    i2++;
                }
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.IsCameFromGalleryFeature = true;
            Intent intent = new Intent(GalleryActivity.this, NewFullScreenViewActivity.class);
            intent.putExtra("IMAGE_URL", ((GalleryEnt) GalleryActivity.this.mGalleryGirdList.get(i)).get_folderLockgalleryfileLocation());
            intent.putExtra("IMAGE_POSITION", i2);
            intent.putStringArrayListExtra("mPhotosList", GalleryActivity.this.mPhotosList);
            GalleryActivity.this.startActivity(intent);
            GalleryActivity.this.finish();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (view.getId() == R.id.ll_background && GalleryActivity.this.IsSortingDropdown) {
                GalleryActivity.this.IsSortingDropdown = false;
            }
            return false;
        }
    }

    public void PlayVideo(String str) {
        String str2;
        File file = new File(str);
        SecurityLocksCommon.IsAppDeactive = false;
        if (file.exists()) {
            try {
                str2 = Utilities.NSVideoDecryptionDuringPlay(new File(str));
            } catch (IOException e) {
                e.printStackTrace();
                str2 = "";
            }
        } else {
            str2 = file.getParent() + File.separator + Utilities.ChangeFileExtentionToOrignal(Utilities.FileName(file.getAbsolutePath()));
        }
        String substring = str2.substring(str2.lastIndexOf(".") + 1);
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, new File(str2)), MimeTypeMap.getSingleton().getMimeTypeFromExtension(substring));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e2) {
            e2.getMessage();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        menu.findItem(R.id.action_more);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_more) {
            this.IsSortingDropdown = false;
            showPopupWindow();
            return true;
        } else if (itemId != R.id.action_select) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            if (this.IsSelectAll) {
                for (int i = 0; i < this.mGalleryGirdList.size(); i++) {
                    this.mGalleryGirdList.get(i).set_isCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.mGalleryGirdList.size(); i2++) {
                    this.mGalleryGirdList.get(i2).set_isCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            GalleryFeatureAdapter galleryFeatureAdapter = new GalleryFeatureAdapter(this, 1, this.mGalleryGirdList, true, _ViewBy);
            this.galleryFeatureAdapter = galleryFeatureAdapter;
            this.galleryGrid.setAdapter((ListAdapter) galleryFeatureAdapter);
            this.galleryFeatureAdapter.notifyDataSetChanged();
            return true;
        }
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.isEditMode) {
            menu.findItem(R.id.action_more).setVisible(false);
            getMenuInflater().inflate(R.menu.menu_selection, menu);
        } else {
            menu.findItem(R.id.action_more).setVisible(true);
            if (this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_unselectallicon);
            } else if (!this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_selectallicon);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
