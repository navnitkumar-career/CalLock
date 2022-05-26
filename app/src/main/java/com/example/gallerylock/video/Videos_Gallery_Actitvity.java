package com.example.gallerylock.video;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.gallerylock.photo.MoveAlbumAdapter;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class Videos_Gallery_Actitvity extends BaseActivity {
    private static final int ACTION_TAKE_VIDEO = 2;
    private static final int RESULT_LOAD_IMAGE = 1;
    public static int _ViewBy;
    public static ProgressDialog myProgressDialog;
    private String[] _albumNameArray;
    ImageButton _btnSortingDropdown;
    int albumId;
    String albumName;
    private FloatingActionButton btnAdd;
    private ImageButton btnSelectAll;
    private FloatingActionButton fabImpBrowser;
    private FloatingActionButton fabImpCam;
    private FloatingActionButton fabImpGallery;
    private FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    FrameLayout fl_bottom_baar;
    protected String folderLocation;
    private AppGalleryVideoAdapter galleryVideosAdapter;
    GridView imagegrid;
    TextView lbl_album_name_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_AddPhotos_Bottom_Baar;
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
    private Uri mVideoUri;
    private VideoAlbum m_videoAlbum;
    private String moveToFolderLocation;
    ImageView photo_video_empty_icon;
    int selectCount;
    private SensorManager sensorManager;
    Toolbar toolbar;
    private VideoAlbumDAL videoAlbumDAL;
    private VideoDAL videoDAL;
    private List<Video> videos;
    private ArrayList<String> files = new ArrayList<>();
    private int videoCount = 0;
    boolean isEditMode = false;
    private List<String> _albumNameArrayForMove = null;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                Videos_Gallery_Actitvity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.Unhide_error, 0).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.Move_error, 0).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.Delete_error, 0).show();
                }
            } else if (message.what == 4) {
                Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.toast_share, 1).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                        videos_Gallery_Actitvity.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        Videos_Gallery_Actitvity.this.RefershGalleryforKitkat();
                    }
                    Common.isUnHide = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.toast_unhide, 1).show();
                    Videos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(Videos_Gallery_Actitvity.this, Videos_Gallery_Actitvity.class);
                        intent.addFlags(67108864);
                        Videos_Gallery_Actitvity.this.startActivity(intent);
                        Videos_Gallery_Actitvity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.toast_delete, 0).show();
                    Videos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(Videos_Gallery_Actitvity.this, Videos_Gallery_Actitvity.class);
                        intent2.addFlags(67108864);
                        Videos_Gallery_Actitvity.this.startActivity(intent2);
                        Videos_Gallery_Actitvity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(Videos_Gallery_Actitvity.this, (int) R.string.toast_move, 0).show();
                    Videos_Gallery_Actitvity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(Videos_Gallery_Actitvity.this, Videos_Gallery_Actitvity.class);
                        intent3.addFlags(67108864);
                        Videos_Gallery_Actitvity.this.startActivity(intent3);
                        Videos_Gallery_Actitvity.this.finish();
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
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.2
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    @Override
    public void onCreate(android.os.Bundle r7) {
        super.onCreate(r7);
        setContentView((int) R.layout.photos_videos_gallery_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      /*  Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        setSupportActionBar(toolbar2);
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.Back();
            }
        });*/
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-1, 0);
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
                Videos_Gallery_Actitvity.this.startActivity(new Intent(Videos_Gallery_Actitvity.this, ImportAlbumsGalleryVideoActivity.class));
                Videos_Gallery_Actitvity.this.finish();
            }
        });
        this.fabImpCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.dispatchTakeVideoIntent();
            }
        });
        this.fabImpBrowser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.CurrentWebBrowserActivity = Videos_Gallery_Actitvity.this;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(Videos_Gallery_Actitvity.this, SecureBrowserActivity.class));
                Videos_Gallery_Actitvity.this.finish();
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
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(this);
        videoAlbumDAL2.OpenRead();
        VideoAlbum GetAlbumById = videoAlbumDAL2.GetAlbumById(Integer.toString(Common.FolderId));
        this._SortBy = videoAlbumDAL2.GetSortByAlbumId(Common.FolderId);
        videoAlbumDAL2.close();
        String albumName2 = GetAlbumById.getAlbumName();
        this.albumName = albumName2;
        Common.VideoFolderName = albumName2;
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
                if (Videos_Gallery_Actitvity.this.IsSortingDropdown) {
                    Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                }
                if (Videos_Gallery_Actitvity.this.IsSortingDropdown) {
                    Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                String str;
                if (!Videos_Gallery_Actitvity.this.isEditMode) {
                    Common.VideoThumbnailCurrentPosition = Videos_Gallery_Actitvity.this.imagegrid.getFirstVisiblePosition();
                    File file = new File(((Video) Videos_Gallery_Actitvity.this.videos.get(i)).getFolderLockVideoLocation());
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (file.exists()) {
                        try {
                            str = Utilities.NSVideoDecryptionDuringPlay(new File(((Video) Videos_Gallery_Actitvity.this.videos.get(i)).getFolderLockVideoLocation()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            str = "";
                        }
                    } else {
                        str = file.getParent() + File.separator + Utilities.ChangeFileExtentionToOrignal(Utilities.FileName(file.getAbsolutePath()));
                    }
                    String substring = str.substring(str.lastIndexOf(".") + 1);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setDataAndType(FileProvider.getUriForFile(Videos_Gallery_Actitvity.this, BuildConfig.APPLICATION_ID, new File(str)), MimeTypeMap.getSingleton().getMimeTypeFromExtension(substring));
                        intent.addFlags(1);
                        intent.addFlags(2);
                        Videos_Gallery_Actitvity.this.startActivity(intent);
                    } catch (Exception e2) {
                        e2.getMessage();
                    }
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.VideoThumbnailCurrentPosition = Videos_Gallery_Actitvity.this.imagegrid.getFirstVisiblePosition();
                Videos_Gallery_Actitvity.this.isEditMode = true;
                Videos_Gallery_Actitvity.this.fl_bottom_baar.setLayoutParams(Videos_Gallery_Actitvity.this.ll_Show_Params);
                Videos_Gallery_Actitvity.this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
                Videos_Gallery_Actitvity.this.ll_EditPhotos.setVisibility(0);
                Videos_Gallery_Actitvity.this._btnSortingDropdown.setVisibility(4);
                Videos_Gallery_Actitvity.this.btnSelectAll.setVisibility(0);
                Videos_Gallery_Actitvity.this.invalidateOptionsMenu();
                ((Video) Videos_Gallery_Actitvity.this.videos.get(i)).SetFileCheck(true);
                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                Videos_Gallery_Actitvity videos_Gallery_Actitvity2 = Videos_Gallery_Actitvity.this;
                AppGalleryVideoAdapter unused = videos_Gallery_Actitvity.galleryVideosAdapter = new AppGalleryVideoAdapter(videos_Gallery_Actitvity2, 1, videos_Gallery_Actitvity2.videos, true, Videos_Gallery_Actitvity._ViewBy);
                Videos_Gallery_Actitvity.this.imagegrid.setAdapter(Videos_Gallery_Actitvity.this.galleryVideosAdapter);
                Videos_Gallery_Actitvity.this.galleryVideosAdapter.notifyDataSetChanged();
                if (Common.VideoThumbnailCurrentPosition != 0) {
                    Videos_Gallery_Actitvity.this.imagegrid.setSelection(Common.VideoThumbnailCurrentPosition);
                }
                return true;
            }
        });
        this.btnSelectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.SelectOrUnSelectAll();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(Videos_Gallery_Actitvity.this, ImportAlbumsGalleryVideoActivity.class));
                Videos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.dispatchTakeVideoIntent();
            }
        });
        this.ll_import_intenet_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Constants.CurrentWebBrowserActivity = Videos_Gallery_Actitvity.this;
                Videos_Gallery_Actitvity.this.startActivity(new Intent(Videos_Gallery_Actitvity.this, SecureBrowserActivity.class));
                Videos_Gallery_Actitvity.this.finish();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.DeleteVideos();
            }
        });
        this.ll_unhide_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.UnhideVideos();
            }
        });
        this.ll_move_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.MoveVideos();
            }
        });
        this.ll_share_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!Videos_Gallery_Actitvity.this.IsFileCheck()) {
                    Toast.makeText(Videos_Gallery_Actitvity.this, R.string.toast_unselectvideomsg_share, 0).show();
                } else {
                    Videos_Gallery_Actitvity.this.ShareVideos();
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
            LoadVideosFromDB(this._SortBy);
        }
        if (Common.VideoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.VideoThumbnailCurrentPosition);
            Common.VideoThumbnailCurrentPosition = 0;
        }
    }

    public void setUIforlistView() {
        this.imagegrid.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 1));
        this.imagegrid.setHorizontalSpacing(Utilities.convertDptoPix(getApplicationContext(), 0));
        this.ll_GridviewParams.setMargins(0, 0, 0, 0);
        this.ll_photo_video_grid.setLayoutParams(this.ll_GridviewParams);
        this.imagegrid.setNumColumns(1);
    }

    public void btnBackonClick(View view) {
        Back();
    }

    void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(View.INVISIBLE);
            this.ll_EditPhotos.setVisibility(View.INVISIBLE);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.VideoFolderName = StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM;
            startActivity(new Intent(this, VideosAlbumActivty.class));
            finish();
        }
        DeleteTemporaryVideos();
    }

    public void UnhideVideos() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectvideomsg_unhide, 0).show();
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
            textView.setText("Are you sure you want to restore (" + this.selectCount + ") video(s)?");
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.18
                /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity$18$1] */
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Videos_Gallery_Actitvity.this.showUnhideProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.18.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                Videos_Gallery_Actitvity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 2;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.19
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    void Unhide() throws IOException {
        for (int i = 0; i < this.videos.size(); i++) {
            if (this.videos.get(i).GetFileCheck()) {
                if (Utilities.NSUnHideFile(this, this.videos.get(i).getFolderLockVideoLocation(), this.videos.get(i).getOriginalVideoLocation())) {
                    new File(this.videos.get(i).getthumbnail_video_location()).delete();
                    DeleteFromDatabase(this.videos.get(i).getId());
                } else {
                    Toast.makeText(this, (int) R.string.Unhide_error, 0).show();
                }
            }
        }
    }

    public void DeleteVideos() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectvideomsg_delete, 0).show();
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
        textView.setText("Are you sure you want to delete (" + this.selectCount + ") video(s)?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.20
            /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity$20$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Videos_Gallery_Actitvity.this.showDeleteProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.20.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            Videos_Gallery_Actitvity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 2;
                            Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.21
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void Delete() {
        for (int i = 0; i < this.videos.size(); i++) {
            if (this.videos.get(i).GetFileCheck()) {
                new File(this.videos.get(i).getFolderLockVideoLocation()).delete();
                new File(this.videos.get(i).getthumbnail_video_location()).delete();
                DeleteFromDatabase(this.videos.get(i).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        VideoDAL videoDAL;
        VideoDAL videoDAL2 = new VideoDAL(this);
        this.videoDAL = videoDAL2;
        try {
            try {
                videoDAL2.OpenWrite();
                this.videoDAL.DeleteVideoById(i);
                videoDAL = this.videoDAL;
                if (videoDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                videoDAL = this.videoDAL;
                if (videoDAL == null) {
                    return;
                }
            }
            videoDAL.close();
        } catch (Throwable th) {
            VideoDAL videoDAL3 = this.videoDAL;
            if (videoDAL3 != null) {
                videoDAL3.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.videos.size(); i++) {
            if (this.videos.get(i).GetFileCheck()) {
                this.files.add(this.videos.get(i).getFolderLockVideoLocation());
                this.selectCount++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean IsFileCheck() {
        for (int i = 0; i < this.videos.size(); i++) {
            if (this.videos.get(i).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MoveVideos() {
        VideoDAL videoDAL = new VideoDAL(this);
        this.videoDAL = videoDAL;
        videoDAL.OpenWrite();
        this._albumNameArray = this.videoDAL.GetAlbumNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectvideomsg_move, 0).show();
        } else if (this._albumNameArray.length > 0) {
            GetAlbumNameFromDB();
        } else {
            Toast.makeText(this, (int) R.string.toast_OneAlbum, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Move(String str, String str2, String str3) throws IOException {
        String str4;
        VideoAlbum GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.videos.size(); i++) {
            if (this.videos.get(i).GetFileCheck()) {
                if (this.videos.get(i).getVideoName().contains("#")) {
                    str4 = this.videos.get(i).getVideoName();
                } else {
                    str4 = Utilities.ChangeFileExtention(this.videos.get(i).getVideoName());
                }
                String str5 = str2 + "/" + str4;
                if (Utilities.MoveFileWithinDirectories(this.videos.get(i).getFolderLockVideoLocation(), str5)) {
                    String str6 = this.videos.get(i).getthumbnail_video_location();
                    String FileName = Utilities.FileName(this.videos.get(i).getthumbnail_video_location());
                    if (!FileName.contains("#")) {
                        FileName = Utilities.ChangeFileExtention(FileName);
                    }
                    String str7 = str2 + "/VideoThumnails/" + FileName;
                    if (Utilities.MoveFileWithinDirectories(str6, str7)) {
                        UpdateVideoLocationInDatabase(this.videos.get(i), str5, GetAlbum.getId(), str7);
                        Common.FolderId = GetAlbum.getId();
                    }
                }
            }
        }
    }

    public void UpdateVideoLocationInDatabase(Video video, String str, int i, String str2) {
        VideoDAL videoDAL;
        video.setFolderLockVideoLocation(str);
        video.setthumbnail_video_location(str2);
        video.setAlbumId(i);
        try {
            try {
                this.videoDAL.OpenWrite();
                this.videoDAL.UpdateVideoLocationById(video);
                videoDAL = this.videoDAL;
                if (videoDAL == null) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                videoDAL = this.videoDAL;
                if (videoDAL == null) {
                    return;
                }
            }
            videoDAL.close();
        } catch (Throwable th) {
            VideoDAL videoDAL2 = this.videoDAL;
            if (videoDAL2 != null) {
                videoDAL2.close();
            }
            throw th;
        }
    }

    public VideoAlbum GetAlbum(String str) {
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(this);
        this.videoAlbumDAL = videoAlbumDAL2;
        try {
            videoAlbumDAL2.OpenRead();
            VideoAlbum GetAlbum = this.videoAlbumDAL.GetAlbum(str);
            this.m_videoAlbum = GetAlbum;
            return GetAlbum;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } catch (Throwable th) {
            VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
            if (videoAlbumDAL3 != null) {
                videoAlbumDAL3.close();
            }
            throw th;
        }
    }

    private void GetAlbumNameFromDB() {
        VideoDAL videoDAL;
        VideoDAL videoDAL2 = new VideoDAL(this);
        this.videoDAL = videoDAL2;
        try {
            try {
                videoDAL2.OpenWrite();
                this._albumNameArrayForMove = this.videoDAL.GetMoveAlbumNames(Common.FolderId);
                MoveVideoDialog();
                videoDAL = this.videoDAL;
                if (videoDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                videoDAL = this.videoDAL;
                if (videoDAL == null) {
                    return;
                }
            }
            videoDAL.close();
        } catch (Throwable th) {
            VideoDAL videoDAL3 = this.videoDAL;
            if (videoDAL3 != null) {
                videoDAL3.close();
            }
            throw th;
        }
    }

    void MoveVideoDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.move_customlistview);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        listView.setAdapter((ListAdapter) new MoveAlbumAdapter(this, 17367043, this._albumNameArrayForMove, R.drawable.empty_folder_video_icon));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.22
            /* JADX WARN: Type inference failed for: r1v5, types: [net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity$22$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (Videos_Gallery_Actitvity.this._albumNameArrayForMove != null) {
                    Videos_Gallery_Actitvity.this.SelectedCount();
                    Videos_Gallery_Actitvity.this.showMoveProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.22.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                                videos_Gallery_Actitvity.moveToFolderLocation = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + ((String) Videos_Gallery_Actitvity.this._albumNameArrayForMove.get(i));
                                Videos_Gallery_Actitvity.this.Move(Videos_Gallery_Actitvity.this.folderLocation, Videos_Gallery_Actitvity.this.moveToFolderLocation, (String) Videos_Gallery_Actitvity.this._albumNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 2;
                                Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity$23] */
    public void ShareVideos() {
        showCopyFilesProcessForShareProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.23
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : Videos_Gallery_Actitvity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageCommon.AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS;
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList<Uri> arrayList3 = new ArrayList<>();
                            for (int i = 0; i < Videos_Gallery_Actitvity.this.videos.size(); i++) {
                                if (((Video) Videos_Gallery_Actitvity.this.videos.get(i)).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(Videos_Gallery_Actitvity.this, ((Video) Videos_Gallery_Actitvity.this.videos.get(i)).getFolderLockVideoLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(Videos_Gallery_Actitvity.this, BuildConfig.APPLICATION_ID, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    Videos_Gallery_Actitvity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    Videos_Gallery_Actitvity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    Videos_Gallery_Actitvity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void DeleteTemporaryVideos() {
        File[] listFiles;
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + "/");
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isFile()) {
                    file2.delete();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchTakeVideoIntent() {
        SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(true);
        Common.isOpenCameraorGalleryFromApp = true;
        SecurityLocksCommon.IsAppDeactive = false;
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            startActivityForResult(intent, 2);
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", "VideoTitle");
        contentValues.put("mime_type", "video/mp4");
        contentValues.put("_data", Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ("Video_" + new SimpleDateFormat("yyyymmddhhmmss").format(new Date()) + ".mp4"));
        intent.putExtra("output", getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues));
        startActivityForResult(intent, 2);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        FileOutputStream fileOutputStream;
        super.onActivityResult(i, i2, intent);
        SecurityLocksCommon.IsAppDeactive = true;
        if (i != 1) {
            if (i != 2) {
                return;
            }
        } else if (i2 == -1) {
            return;
        }
        if (i2 == -1) {
            Uri lastPhotoOrVideo = Utilities.getLastPhotoOrVideo(this);
            this.mVideoUri = lastPhotoOrVideo;
            String encodedPath = lastPhotoOrVideo.getEncodedPath();
            new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
            String ChangeFileExtention = Utilities.ChangeFileExtention(Utilities.FileName(encodedPath));
            File file = new File(encodedPath);
            Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), 1);
            File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.albumName + "/VideoThumnails/");
            if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                file2 = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.VIDEOS + this.albumName + "/VideoThumnails/");
            }
            file2.mkdirs();
            Utilities.FileName(encodedPath);
            String str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.albumName + "/VideoThumnails/thumbnil-" + ChangeFileExtention.substring(0, ChangeFileExtention.lastIndexOf("#")) + "#jpg";
            if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                str = StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.VIDEOS + this.albumName + "/VideoThumnails/thumbnil-" + ChangeFileExtention.substring(0, ChangeFileExtention.lastIndexOf("#")) + "#jpg";
            }
            File file3 = new File(str);
            String str2 = null;
            try {
                fileOutputStream = new FileOutputStream(file3, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fileOutputStream = null;
            }
            createVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                Utilities.NSEncryption(file3);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            File file4 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + this.albumName + "/" + ChangeFileExtention);
            if (Build.VERSION.SDK_INT >= StorageOptionsCommon.Kitkat) {
                file4 = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.VIDEOS + this.albumName + "/" + ChangeFileExtention);
            }
            try {
                str2 = Utilities.NSHideFile(this, file, new File(file4.getParent()));
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            AddVideoToDatabase(Utilities.ChangeFileExtentionToOrignal(ChangeFileExtention), encodedPath, str, str2);
            file.delete();
            try {
                Utilities.NSEncryption(new File(str2));
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            LoadVideosFromDB(this._SortBy);
            Toast.makeText(this, (int) R.string.toast_saved, 1).show();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor managedQuery = managedQuery(uri, new String[]{"_data"}, null, null, null);
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }

    private void AddVideoToDatabase(String str, String str2, String str3, String str4) {
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str4);
        video.setOriginalVideoLocation(str2);
        video.setthumbnail_video_location(str3);
        video.setAlbumId(Common.FolderId);
        VideoDAL videoDAL = new VideoDAL(this);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.videos.size(); i++) {
                this.videos.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.videos.size(); i2++) {
                this.videos.get(i2).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, true, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter((ListAdapter) appGalleryVideoAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.videos.size(); i++) {
            this.videos.get(i).SetFileCheck(false);
        }
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, false, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter((ListAdapter) appGalleryVideoAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
        if (Common.VideoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.VideoThumbnailCurrentPosition);
            Common.VideoThumbnailCurrentPosition = 0;
        }
    }

    void LoadVideosFromDB(int i) {
        this.videos = new ArrayList();
        VideoDAL videoDAL = new VideoDAL(this);
        videoDAL.OpenRead();
        this.videoCount = videoDAL.GetVideoCountByAlbumId(Common.FolderId);
        this.videos = videoDAL.GetVideoByAlbumId(Common.FolderId, i);
        videoDAL.close();
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, false, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter((ListAdapter) appGalleryVideoAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
        if (this.videos.size() < 1) {
            this.ll_photo_video_grid.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.lbl_No_Video);
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
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.24
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.Videos_Gallery_Actitvity.25
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        Videos_Gallery_Actitvity._ViewBy = ViewBy.List.ordinal();
                        Videos_Gallery_Actitvity.this.ViewBy();
                        popupWindow.dismiss();
                        Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        Videos_Gallery_Actitvity._ViewBy = ViewBy.Tiles.ordinal();
                        Videos_Gallery_Actitvity.this.ViewBy();
                        popupWindow.dismiss();
                        Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        Videos_Gallery_Actitvity._ViewBy = ViewBy.LargeTiles.ordinal();
                        Videos_Gallery_Actitvity.this.ViewBy();
                        popupWindow.dismiss();
                        Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        Videos_Gallery_Actitvity.this._SortBy = SortBy.Name.ordinal();
                        Videos_Gallery_Actitvity videos_Gallery_Actitvity = Videos_Gallery_Actitvity.this;
                        videos_Gallery_Actitvity.LoadVideosFromDB(videos_Gallery_Actitvity._SortBy);
                        Videos_Gallery_Actitvity.this.AddSortInDB();
                        popupWindow.dismiss();
                        Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        Videos_Gallery_Actitvity.this._SortBy = SortBy.Time.ordinal();
                        Videos_Gallery_Actitvity videos_Gallery_Actitvity2 = Videos_Gallery_Actitvity.this;
                        videos_Gallery_Actitvity2.LoadVideosFromDB(videos_Gallery_Actitvity2._SortBy);
                        Videos_Gallery_Actitvity.this.AddSortInDB();
                        popupWindow.dismiss();
                        Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        Videos_Gallery_Actitvity.this._SortBy = SortBy.Size.ordinal();
                        Videos_Gallery_Actitvity videos_Gallery_Actitvity3 = Videos_Gallery_Actitvity.this;
                        videos_Gallery_Actitvity3.LoadVideosFromDB(videos_Gallery_Actitvity3._SortBy);
                        Videos_Gallery_Actitvity.this.AddSortInDB();
                        popupWindow.dismiss();
                        Videos_Gallery_Actitvity.this.IsSortingDropdown = false;
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
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this);
        videoAlbumDAL.OpenWrite();
        videoAlbumDAL.AddSortByInVideoAlbum(this._SortBy);
        videoAlbumDAL.close();
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
        AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, false, _ViewBy);
        this.galleryVideosAdapter = appGalleryVideoAdapter;
        this.imagegrid.setAdapter((ListAdapter) appGalleryVideoAdapter);
        this.galleryVideosAdapter.notifyDataSetChanged();
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
            SecurityLocksSharedPreferences.GetObject(this).SetIsCameraOpenFromInApp(false);
            Common.isOpenCameraorGalleryFromApp = false;
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
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
        this.ll_EditPhotos.setVisibility(4);
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
                this.isEditMode = false;
                this.IsSortingDropdown = false;
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(4);
                this.ll_EditPhotos.setVisibility(4);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(4);
                this._btnSortingDropdown.setVisibility(0);
                invalidateOptionsMenu();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.VideoFolderName = StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM;
            startActivity(new Intent(this, VideosAlbumActivty.class));
            finish();
            DeleteTemporaryVideos();
        }
        return super.onKeyDown(i, keyEvent);
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
                for (int i = 0; i < this.videos.size(); i++) {
                    this.videos.get(i).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.videos.size(); i2++) {
                    this.videos.get(i2).SetFileCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            AppGalleryVideoAdapter appGalleryVideoAdapter = new AppGalleryVideoAdapter(this, 1, this.videos, true, _ViewBy);
            this.galleryVideosAdapter = appGalleryVideoAdapter;
            this.imagegrid.setAdapter((ListAdapter) appGalleryVideoAdapter);
            this.galleryVideosAdapter.notifyDataSetChanged();
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
