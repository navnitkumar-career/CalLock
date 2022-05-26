package com.example.gallerylock.photo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.LibCommonAppClass;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.privatebrowser.SecureBrowserActivity;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.getbase.floatingactionbutton.BuildConfig;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/* loaded from: classes2.dex */
public class Photos_Gallery_Actitvity extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    public static int _ViewBy = 1;
    public static ProgressDialog myProgressDialog;
    private String[] _albumNameArray;
    ImageButton _btnSortingDropdown;
    int albumId;
    String albumName;
    private FloatingActionButton btnAdd;
    ImageButton btnSelectAll;
    File cacheDir;
    private FloatingActionButton fabImpBrowser;
    private FloatingActionButton fabImpCam;
    private FloatingActionButton fabImpGallery;
    private FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    FrameLayout fl_bottom_baar;
    protected String folderLocation;
    private PhoneGalleryPhotoAdapter galleryImagesAdapter;
    GridView imagegrid;
    TextView lbl_album_name_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_AddPhotos_Bottom_Baar;
    LinearLayout.LayoutParams ll_EditAlbum_Hide_Params;
    LinearLayout.LayoutParams ll_EditAlbum_Show_Params;
    LinearLayout ll_EditPhotos;
    RelativeLayout.LayoutParams ll_GridviewParams;
    LinearLayout.LayoutParams ll_Hide_Params;
    LinearLayout.LayoutParams ll_Show_Params;
    LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_import_from_camera_btn;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_import_intenet_btn;
    LinearLayout ll_move_btn;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_photo_video_grid;
    LinearLayout ll_share_btn;
    LinearLayout ll_topbaar;
    LinearLayout ll_unhide_btn;
    private PhotoAlbum m_photoAlbum;
    private String moveToFolderLocation;
    private Uri outputFileUri;
    private PhotoAlbumDAL photoAlbumDAL;
    private PhotoDAL photoDAL;
    ImageView photo_video_empty_icon;
    private List<Photo> photos;
    int selectCount;
    private SensorManager sensorManager;
    Toolbar toolbar;
    private ArrayList<ImportEnt> photoImportEntListShow = new ArrayList<>();
    private ArrayList<String> files = new ArrayList<>();
    private int photoCount = 0;
    boolean isEditMode = false;
    boolean isAddbarvisible = false;
    private List<String> _albumNameArrayForMove = null;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                Photos_Gallery_Actitvity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.Unhide_error, 0).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.Move_error, 0).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.Delete_error, 0).show();
                }
            } else if (message.what == 4) {
                Photos_Gallery_Actitvity.this.hideProgress();
                Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.toast_share, 1).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                        photos_Gallery_Actitvity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        Photos_Gallery_Actitvity.this.RefershGalleryforKitkat();
                    }
                    Common.isUnHide = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.toast_unhide, 1).show();
                    Photos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class);
                        intent.addFlags(67108864);
                        Photos_Gallery_Actitvity.this.startActivity(intent);
                        Photos_Gallery_Actitvity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.toast_delete, 0).show();
                    Photos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class);
                        intent2.addFlags(67108864);
                        Photos_Gallery_Actitvity.this.startActivity(intent2);
                        Photos_Gallery_Actitvity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Photos_Gallery_Actitvity.this, (int) R.string.toast_move, 0).show();
                    Photos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(Photos_Gallery_Actitvity.this, Photos_Gallery_Actitvity.class);
                        intent3.addFlags(67108864);
                        Photos_Gallery_Actitvity.this.startActivity(intent3);
                        Photos_Gallery_Actitvity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    /* loaded from: classes2.dex */
    public enum SortBy {
        Time,
        Name,
        Size
    }

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

    /* JADX INFO: Access modifiers changed from: private */
    public void showMoveProgress() {
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
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.2
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
     public void onCreate(android.os.Bundle r7) {
        super.onCreate(r7);
        setContentView((int) R.layout.photos_videos_gallery_activity);
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        Log.d("TAG", "PhotoGalleryActivity");
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
       /* Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        setSupportActionBar(toolbar2);
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Photos_Gallery_Actitvity.this.Back();
            }
        });*/
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-2, 0);
        this.ll_GridviewParams = new RelativeLayout.LayoutParams(-1, -1);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_bottom_baar);
        this.fl_bottom_baar = frameLayout;
        frameLayout.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar = (LinearLayout) findViewById(R.id.ll_AddPhotos_Bottom_Baar);
        this.ll_EditPhotos = (LinearLayout) findViewById(R.id.ll_EditPhotos);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpCam = (FloatingActionButton) findViewById(R.id.btn_impCam);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.fabImpGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Photos_Gallery_Actitvity.this.startActivity(new Intent(Photos_Gallery_Actitvity.this, ImportAlbumsGalleryPhotoActivity.class));
                Photos_Gallery_Actitvity.this.finish();
            }
        });
        this.fabImpCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Common.isOpenCameraorGalleryFromApp = true;
                Photos_Gallery_Actitvity.this.openCameraIntent();
            }
        });
        this.fabImpBrowser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.CurrentWebBrowserActivity = Photos_Gallery_Actitvity.this;
                Photos_Gallery_Actitvity.this.startActivity(new Intent(Photos_Gallery_Actitvity.this, SecureBrowserActivity.class));
                Photos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_gallery_btn);
        this.ll_import_from_camera_btn = (LinearLayout) findViewById(R.id.ll_import_from_camera_btn);
        this.ll_import_intenet_btn = (LinearLayout) findViewById(R.id.ll_import_intenet_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_move_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this._btnSortingDropdown = (ImageButton) findViewById(R.id.btnSort);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_photo_video_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_photo_video_grid.setVisibility(0);
        this.ll_photo_video_empty.setVisibility(4);
        this.btnSelectAll.setVisibility(4);
        this.lbl_album_name_topbaar = (TextView) findViewById(R.id.lbl_album_name_topbaar);
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(this);
        photoAlbumDAL2.OpenRead();
        PhotoAlbum GetAlbumById = photoAlbumDAL2.GetAlbumById(Integer.toString(Common.FolderId));
        this._SortBy = photoAlbumDAL2.GetSortByAlbumId(Common.FolderId);
        photoAlbumDAL2.close();
        String albumName2 = GetAlbumById.getAlbumName();
        this.albumName = albumName2;
        Common.PhotoFolderName = albumName2;
        this.folderLocation = GetAlbumById.getAlbumLocation();
        this.lbl_album_name_topbaar.setText(this.albumName);
        getSupportActionBar().setTitle((CharSequence) this.albumName);
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        this.ll_background.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Photos_Gallery_Actitvity.this.IsSortingDropdown) {
                    Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                }
                if (Photos_Gallery_Actitvity.this.IsSortingDropdown) {
                    Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!Photos_Gallery_Actitvity.this.isEditMode) {
                    Common.PhotoThumbnailCurrentPosition = Photos_Gallery_Actitvity.this.imagegrid.getFirstVisiblePosition();
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.IsCameFromAppGallery = true;
                    Intent intent = new Intent(Photos_Gallery_Actitvity.this, NewFullScreenViewActivity.class);
                    intent.putExtra("IMAGE_URL", ((Photo) Photos_Gallery_Actitvity.this.photos.get(i)).getFolderLockPhotoLocation());
                    intent.putExtra("IMAGE_POSITION", i);
                    intent.putExtra("ALBUM_ID", ((Photo) Photos_Gallery_Actitvity.this.photos.get(i)).getAlbumId());
                    intent.putExtra("ALBUM_NAME", Photos_Gallery_Actitvity.this.albumName);
                    intent.putExtra("ALBUM_LOCATION", Photos_Gallery_Actitvity.this.folderLocation);
                    intent.putExtra("_SortBy", Photos_Gallery_Actitvity.this._SortBy);
                    Photos_Gallery_Actitvity.this.startActivity(intent);
                    Photos_Gallery_Actitvity.this.finish();
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.PhotoThumbnailCurrentPosition = Photos_Gallery_Actitvity.this.imagegrid.getFirstVisiblePosition();
                Photos_Gallery_Actitvity.this.isEditMode = true;
                Photos_Gallery_Actitvity.this.fl_bottom_baar.setLayoutParams(Photos_Gallery_Actitvity.this.ll_Show_Params);
                Photos_Gallery_Actitvity.this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
                Photos_Gallery_Actitvity.this.ll_EditPhotos.setVisibility(0);
                Photos_Gallery_Actitvity.this._btnSortingDropdown.setVisibility(4);
                Photos_Gallery_Actitvity.this.btnSelectAll.setVisibility(0);
                Photos_Gallery_Actitvity.this.invalidateOptionsMenu();
                ((Photo) Photos_Gallery_Actitvity.this.photos.get(i)).SetFileCheck(true);
                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                Photos_Gallery_Actitvity photos_Gallery_Actitvity2 = Photos_Gallery_Actitvity.this;
                PhoneGalleryPhotoAdapter unused = photos_Gallery_Actitvity.galleryImagesAdapter = new PhoneGalleryPhotoAdapter(photos_Gallery_Actitvity2, 1, photos_Gallery_Actitvity2.photos, true, Photos_Gallery_Actitvity._ViewBy);
                Photos_Gallery_Actitvity.this.imagegrid.setAdapter(Photos_Gallery_Actitvity.this.galleryImagesAdapter);
                Photos_Gallery_Actitvity.this.galleryImagesAdapter.notifyDataSetChanged();
                if (Common.PhotoThumbnailCurrentPosition != 0) {
                    Photos_Gallery_Actitvity.this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
                }
                return true;
            }
        });
        this.btnSelectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Photos_Gallery_Actitvity.this.SelectOrUnSelectAll();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Photos_Gallery_Actitvity.this.startActivity(new Intent(Photos_Gallery_Actitvity.this, ImportAlbumsGalleryPhotoActivity.class));
                Photos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Common.isOpenCameraorGalleryFromApp = true;
                Photos_Gallery_Actitvity.this.openCameraIntent();
            }
        });
        this.ll_import_intenet_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Constants.CurrentWebBrowserActivity = Photos_Gallery_Actitvity.this;
                Photos_Gallery_Actitvity.this.startActivity(new Intent(Photos_Gallery_Actitvity.this, SecureBrowserActivity.class));
                Photos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Photos_Gallery_Actitvity.this.DeletePhotos();
            }
        });
        this.ll_unhide_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Photos_Gallery_Actitvity.this.UnhidePhotos();
            }
        });
        this.ll_move_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Photos_Gallery_Actitvity.this.MovePhotos();
            }
        });
        this.ll_share_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!Photos_Gallery_Actitvity.this.IsFileCheck()) {
                    Toast.makeText(Photos_Gallery_Actitvity.this, R.string.toast_unselectphotomsg_share, 0).show();
                } else {
                    Photos_Gallery_Actitvity.this.SharePhotos();
                }
            }
        });
        if (Common.IsImporting) {
            try {
                showIsImportingProgress();
            } catch (Exception unused) {
            }
        } else if (Common.isUnHide) {
            showUnhideProgress();
        } else if (Common.isDelete) {
            showDeleteProgress();
        } else if (Common.isMove) {
            showMoveProgress();
        } else {
            LoadPhotosFromDB(this._SortBy);
        }
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

   /* *//* access modifiers changed from: private *//*
    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.photos.size(); i++) {
                this.photos.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.photos.size(); i2++) {
                this.photos.get(i2).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, true, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter(phoneGalleryPhotoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();    }

    *//* JADX INFO: Access modifiers changed from: private *//*
    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.photos.size(); i++) {
                this.photos.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.photos.size(); i2++) {
                this.photos.get(i2).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, true, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter((ListAdapter) phoneGalleryPhotoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }*/

    public void setUIforlistView() {
        this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 1));
        this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 0));
        this.ll_GridviewParams.setMargins(0, 0, 0, 0);
        this.ll_photo_video_grid.setLayoutParams(this.ll_GridviewParams);
        this.imagegrid.setNumColumns(1);
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.photos.size(); i++) {
            this.photos.get(i).SetFileCheck(false);
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, false, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter((ListAdapter) phoneGalleryPhotoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

    public void btnBackonClick(View view) {
        Back();
    }

    public void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
            this.ll_EditPhotos.setVisibility(4);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(4);
            this._btnSortingDropdown.setVisibility(0);
            invalidateOptionsMenu();
        } else if (this.isAddbarvisible) {
            this.isAddbarvisible = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
            this.ll_EditPhotos.setVisibility(4);
            this.IsSortingDropdown = false;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.PhotoFolderName = "My Photos";
            startActivity(new Intent(this, PhotosAlbumActivty.class));
            finish();
        }
        DeleteTemporaryPhotos();
    }

    public void UnhidePhotos() {
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
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(createFromAsset);
            textView.setText("Are you sure you want to restore (" + this.selectCount + ") photo(s)?");
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.18
                /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity$18$1] */
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Photos_Gallery_Actitvity.this.showUnhideProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.18.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                Photos_Gallery_Actitvity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                Photos_Gallery_Actitvity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                Photos_Gallery_Actitvity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.19
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    void Unhide() throws IOException {
        for (int i = 0; i < this.photos.size(); i++) {
            if (this.photos.get(i).GetFileCheck()) {
                if (Utilities.NSUnHideFile(this, this.photos.get(i).getFolderLockPhotoLocation(), this.photos.get(i).getOriginalPhotoLocation())) {
                    DeleteFromDatabase(this.photos.get(i).getId());
                } else {
                    Toast.makeText(this, (int) R.string.Unhide_error, 0).show();
                }
            }
        }
    }

    public void DeletePhotos() {
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
        textView.setText("Are you sure you want to delete (" + this.selectCount + ") photo(s)?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.20
            /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity$20$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Photos_Gallery_Actitvity.this.showDeleteProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.20.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            Photos_Gallery_Actitvity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            Photos_Gallery_Actitvity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            Photos_Gallery_Actitvity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.21
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void Delete() {
        for (int i = 0; i < this.photos.size(); i++) {
            if (this.photos.get(i).GetFileCheck()) {
                new File(this.photos.get(i).getFolderLockPhotoLocation()).delete();
                DeleteFromDatabase(this.photos.get(i).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        PhotoDAL photoDAL;
        PhotoDAL photoDAL2 = new PhotoDAL(this);
        this.photoDAL = photoDAL2;
        try {
            try {
                photoDAL2.OpenWrite();
                this.photoDAL.DeletePhotoById(i);
                photoDAL = this.photoDAL;
                if (photoDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                photoDAL = this.photoDAL;
                if (photoDAL == null) {
                    return;
                }
            }
            photoDAL.close();
        } catch (Throwable th) {
            PhotoDAL photoDAL3 = this.photoDAL;
            if (photoDAL3 != null) {
                photoDAL3.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.photos.size(); i++) {
            if (this.photos.get(i).GetFileCheck()) {
                this.files.add(this.photos.get(i).getFolderLockPhotoLocation());
                this.selectCount++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean IsFileCheck() {
        for (int i = 0; i < this.photos.size(); i++) {
            if (this.photos.get(i).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MovePhotos() {
        PhotoDAL photoDAL = new PhotoDAL(this);
        this.photoDAL = photoDAL;
        photoDAL.OpenWrite();
        this._albumNameArray = this.photoDAL.GetAlbumNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_move, 0).show();
        } else if (this._albumNameArray.length > 0) {
            GetAlbumNameFromDB();
        } else {
            Toast.makeText(this, (int) R.string.toast_OneAlbum, 0).show();
        }
    }

    void Move(String str, String str2, String str3) {
        String str4;
        PhotoAlbum GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.photos.size(); i++) {
            if (this.photos.get(i).GetFileCheck()) {
                if (this.photos.get(i).getPhotoName().contains("#")) {
                    str4 = this.photos.get(i).getPhotoName();
                } else {
                    str4 = Utilities.ChangeFileExtention(this.photos.get(i).getPhotoName());
                }
                String str5 = str2 + "/" + str4;
                try {
                    if (Utilities.MoveFileWithinDirectories(this.photos.get(i).getFolderLockPhotoLocation(), str5)) {
                        UpdatePhotoLocationInDatabase(this.photos.get(i), str5, GetAlbum.getId());
                        Common.FolderId = GetAlbum.getId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UpdatePhotoLocationInDatabase(Photo photo, String str, int i) {
        PhotoDAL photoDAL;
        photo.setFolderLockPhotoLocation(str);
        photo.setAlbumId(i);
        try {
            try {
                PhotoDAL photoDAL2 = new PhotoDAL(this);
                photoDAL2.OpenWrite();
                photoDAL2.UpdatePhotoLocation(photo);
                photoDAL = this.photoDAL;
                if (photoDAL == null) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                photoDAL = this.photoDAL;
                if (photoDAL == null) {
                    return;
                }
            }
            photoDAL.close();
        } catch (Throwable th) {
            PhotoDAL photoDAL3 = this.photoDAL;
            if (photoDAL3 != null) {
                photoDAL3.close();
            }
            throw th;
        }
    }

    public PhotoAlbum GetAlbum(String str) {
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(this);
        this.photoAlbumDAL = photoAlbumDAL2;
        try {
            photoAlbumDAL2.OpenRead();
            PhotoAlbum GetAlbum = this.photoAlbumDAL.GetAlbum(str);
            this.m_photoAlbum = GetAlbum;
            Log.e("TAG", "GetAlbum: "+ GetAlbum);
            return GetAlbum;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Log.e("TAG", "Exception: "+ e.getMessage());
            return null;
        } catch (Throwable th) {
            PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
            if (photoAlbumDAL3 != null) {
                photoAlbumDAL3.close();
            }
            Log.e("TAG", "Exception: "+ th.getMessage());
            throw th;
        }
    }

    private void GetAlbumNameFromDB() {
        PhotoDAL photoDAL;
        PhotoDAL photoDAL2 = new PhotoDAL(this);
        this.photoDAL = photoDAL2;
        try {
            try {
                photoDAL2.OpenWrite();
                this._albumNameArrayForMove = this.photoDAL.GetMoveAlbumNames(Common.FolderId);
                MovePhotoDialog();
                photoDAL = this.photoDAL;
                if (photoDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                photoDAL = this.photoDAL;
                if (photoDAL == null) {
                    return;
                }
            }
            photoDAL.close();
        } catch (Throwable th) {
            PhotoDAL photoDAL3 = this.photoDAL;
            if (photoDAL3 != null) {
                photoDAL3.close();
            }
            throw th;
        }
    }

    void MovePhotoDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.move_customlistview);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        listView.setAdapter((ListAdapter) new MoveAlbumAdapter(this, 17367043, this._albumNameArrayForMove, R.drawable.empty_folder_album_icon));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.22
            /* JADX WARN: Type inference failed for: r1v5, types: [net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity$22$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (Photos_Gallery_Actitvity.this._albumNameArrayForMove != null) {
                    Photos_Gallery_Actitvity.this.SelectedCount();
                    Photos_Gallery_Actitvity.this.showMoveProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.22.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                                photos_Gallery_Actitvity.moveToFolderLocation = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + ((String) Photos_Gallery_Actitvity.this._albumNameArrayForMove.get(i));
                                Photos_Gallery_Actitvity.this.Move(Photos_Gallery_Actitvity.this.folderLocation, Photos_Gallery_Actitvity.this.moveToFolderLocation, (String) Photos_Gallery_Actitvity.this._albumNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                Photos_Gallery_Actitvity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                Photos_Gallery_Actitvity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity$23] */
    public void SharePhotos() {
        showCopyFilesProcessForShareProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.23
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : Photos_Gallery_Actitvity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageCommon.AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS;
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList<Uri> arrayList3 = new ArrayList<>();
                            for (int i = 0; i < Photos_Gallery_Actitvity.this.photos.size(); i++) {
                                if (((Photo) Photos_Gallery_Actitvity.this.photos.get(i)).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(Photos_Gallery_Actitvity.this, ((Photo) Photos_Gallery_Actitvity.this.photos.get(i)).getFolderLockPhotoLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(Photos_Gallery_Actitvity.this, BuildConfig.APPLICATION_ID, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    Photos_Gallery_Actitvity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    Photos_Gallery_Actitvity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    Photos_Gallery_Actitvity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void DeleteTemporaryPhotos() {
        File[] listFiles;
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + "/");
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isFile()) {
                    file2.delete();
                }
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        SecurityLocksCommon.IsAppDeactive = true;
        if (i == RESULT_LOAD_CAMERA && i2 == -1 && this.cacheDir != null) {
            String str = null;
            try {
                str = Utilities.NSHideFile(this, this.cacheDir, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + this.albumName));
                Utilities.NSEncryption(new File(str));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String str2 = str;
            if (!str2.equals("")) {
                new AlbumsGalleryPhotosMethods().AddPhotoToDatabase(this, Common.FolderId, this.cacheDir.getName(), str2, this.cacheDir.getAbsolutePath());
                Toast.makeText(this, (int) R.string.toast_saved, 0).show();
                LoadPhotosFromDB(this._SortBy);
            }
        }
    }

    void LoadPhotosFromDB(int i) {
        this.photos = new ArrayList();
        PhotoDAL photoDAL = new PhotoDAL(this);
        photoDAL.OpenRead();
        this.photoCount = photoDAL.GetPhotoCountByAlbumId(Common.FolderId);
        this.photos = photoDAL.GetPhotoByAlbumId(Common.FolderId, i);
        Log.e("TAG", "LoadPhotosFromDB: " + photos.size());
        Log.e("TAG", "LoadPhotosFromDB: " + photoCount);
        photoDAL.close();
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, false, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter((ListAdapter) phoneGalleryPhotoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
        if (this.photos.size() < 1) {
            this.ll_photo_video_grid.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_photo_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.lbl_No_Photos);
            return;
        }
        this.ll_photo_video_grid.setVisibility(0);
        this.ll_photo_video_empty.setVisibility(4);
    }

    public void btnSortonClick(View view) {
        this.IsSortingDropdown = false;
        showPopupWindow();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_expandable, (ViewGroup) null);
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
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.24
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.Photos_Gallery_Actitvity.25
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        Photos_Gallery_Actitvity._ViewBy = ViewBy.List.ordinal();
                        Photos_Gallery_Actitvity.this.ViewBy();
                        popupWindow.dismiss();
                        Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        Photos_Gallery_Actitvity._ViewBy = ViewBy.Tiles.ordinal();
                        Photos_Gallery_Actitvity.this.ViewBy();
                        popupWindow.dismiss();
                        Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        Photos_Gallery_Actitvity._ViewBy = ViewBy.LargeTiles.ordinal();
                        Photos_Gallery_Actitvity.this.ViewBy();
                        popupWindow.dismiss();
                        Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        Photos_Gallery_Actitvity.this._SortBy = SortBy.Name.ordinal();
                        Photos_Gallery_Actitvity photos_Gallery_Actitvity = Photos_Gallery_Actitvity.this;
                        photos_Gallery_Actitvity.LoadPhotosFromDB(photos_Gallery_Actitvity._SortBy);
                        Photos_Gallery_Actitvity.this.AddSortInDB();
                        popupWindow.dismiss();
                        Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        Photos_Gallery_Actitvity.this._SortBy = SortBy.Time.ordinal();
                        Photos_Gallery_Actitvity photos_Gallery_Actitvity2 = Photos_Gallery_Actitvity.this;
                        photos_Gallery_Actitvity2.LoadPhotosFromDB(photos_Gallery_Actitvity2._SortBy);
                        Photos_Gallery_Actitvity.this.AddSortInDB();
                        popupWindow.dismiss();
                        Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        Photos_Gallery_Actitvity.this._SortBy = SortBy.Size.ordinal();
                        Photos_Gallery_Actitvity photos_Gallery_Actitvity3 = Photos_Gallery_Actitvity.this;
                        photos_Gallery_Actitvity3.LoadPhotosFromDB(photos_Gallery_Actitvity3._SortBy);
                        Photos_Gallery_Actitvity.this.AddSortInDB();
                        popupWindow.dismiss();
                        Photos_Gallery_Actitvity.this.IsSortingDropdown = false;
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
    public void AddSortInDB() {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this);
        photoAlbumDAL.OpenWrite();
        photoAlbumDAL.AddSortByInPhotoAlbum(this._SortBy);
        photoAlbumDAL.close();
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
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        }
    }

    public void ViewBy() {
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(3);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                    this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                    this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 5));
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(2);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(4);
            } else {
                setUIforlistView();
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(5);
                } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                    this.imagegrid.setNumColumns(7);
                } else {
                    setUIforlistView();
                }
            } else if (ViewBy.LargeTiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(3);
            } else if (ViewBy.Tiles.ordinal() == _ViewBy) {
                this.imagegrid.setNumColumns(7);
            } else {
                setUIforlistView();
            }
        }
        PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, false, _ViewBy);
        this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
        this.imagegrid.setAdapter((ListAdapter) phoneGalleryPhotoAdapter);
        this.galleryImagesAdapter.notifyDataSetChanged();
    }

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
            SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(false);
            Common.isOpenCameraorGalleryFromApp = false;
            finish();
            System.exit(0);
        }
        super.onPause();
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        SetcheckFlase();
        this.IsSortingDropdown = false;
        this.isEditMode = false;
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
        this.ll_EditPhotos.setVisibility(4);
        this.IsSelectAll = false;
        this.btnSelectAll.setVisibility(4);
        this._btnSortingDropdown.setVisibility(0);
        invalidateOptionsMenu();
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
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
                this.ll_EditPhotos.setVisibility(4);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(4);
                this._btnSortingDropdown.setVisibility(0);
                invalidateOptionsMenu();
                return true;
            } else if (this.isAddbarvisible) {
                this.isAddbarvisible = false;
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
                this.ll_EditPhotos.setVisibility(4);
                this.IsSortingDropdown = false;
                return true;
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = 0;
                Common.PhotoFolderName = "My Photos";
                startActivity(new Intent(this, PhotosAlbumActivty.class));
                finish();
                DeleteTemporaryPhotos();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCameraIntent() {
        SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(true);
        Common.isOpenCameraorGalleryFromApp = true;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getApplication().getPackageManager()) != null) {
            try {
                this.cacheDir = createImageFile();
                Log.e("photoFile", this.cacheDir + "");
                intent.putExtra("output", FileProvider.getUriForFile(getApplicationContext(), "com.example.gallerylock", this.cacheDir));
                SecurityLocksCommon.IsAppDeactive = false;
                startActivityForResult(intent, RESULT_LOAD_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File createTempFile = File.createTempFile("IMG_" + format + "_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        createTempFile.getAbsolutePath();
        return createTempFile;
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
                for (int i = 0; i < this.photos.size(); i++) {
                    this.photos.get(i).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.photos.size(); i2++) {
                    this.photos.get(i2).SetFileCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            PhoneGalleryPhotoAdapter phoneGalleryPhotoAdapter = new PhoneGalleryPhotoAdapter(this, 1, this.photos, true, _ViewBy);
            this.galleryImagesAdapter = phoneGalleryPhotoAdapter;
            this.imagegrid.setAdapter((ListAdapter) phoneGalleryPhotoAdapter);
            this.galleryImagesAdapter.notifyDataSetChanged();
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
