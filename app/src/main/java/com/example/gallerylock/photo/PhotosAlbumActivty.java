package com.example.gallerylock.photo;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.gallerylock.LibCommonAppClass;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.getbase.floatingactionbutton.BuildConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: classes2.dex */
public class PhotosAlbumActivty extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    public static int albumPosition = 0;
    public static boolean isEditPhotoAlbum = false;
    public static boolean isGridView = true;
    public ProgressBar Progress;
    private PhotosAlbumsAdapter adapter;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private FloatingActionButton btn_Add_Album;
    File cacheDir;
    private GridView gridView;
    private ImageButton ib_more;
    LinearLayout ll_EditAlbum;
    LinearLayout.LayoutParams ll_EditAlbum_Hide_Params;
    LinearLayout.LayoutParams ll_EditAlbum_Show_Params;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_import_from_camera_btn;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_rename_btn;
    private Uri outputFileUri;
    private PhotoAlbumDAL photoAlbumDAL;
    private ArrayList<PhotoAlbum> photoAlbums;
    int position;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    private TextView toolbar_title;
    String albumName = "";
    int AlbumId = 0;
    int _SortBy = 0;
    boolean IsMoreDropdown = false;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                PhotosAlbumActivty.this.Progress.setVisibility(8);
            }
            super.handleMessage(message);
        }
    };

    /* loaded from: classes2.dex */
    public enum SortBy {
        Name,
        Time
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_photos_videos_albums);
        Log.d("TAG", "PhotoAlumActivity");
      /*  this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle(R.string.lblFeature1);*/
/*        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotosAlbumActivty.this.Back();
            }
        });*/
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.ll_rename_btn = (LinearLayout) findViewById(R.id.ll_rename_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_gallery_btn);
        this.ll_import_from_camera_btn = (LinearLayout) findViewById(R.id.ll_import_from_camera_btn);
        this.ll_EditAlbum_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, 0);
        this.ll_EditAlbum_Hide_Params = layoutParams;
        this.ll_EditAlbum.setLayoutParams(layoutParams);
        AppSettingsSharedPreferences GetObject = AppSettingsSharedPreferences.GetObject(this);
        this.appSettingsSharedPreferences = GetObject;
        this._SortBy = GetObject.GetPhotosAlbumsSortBy();
        if (isGridView) {
            this.gridView.setNumColumns(2);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        this.ll_background.setOnTouchListener(new View.OnTouchListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (PhotosAlbumActivty.this.IsMoreDropdown) {
                    PhotosAlbumActivty.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(0);
        new Handler().postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.4
            @Override // java.lang.Runnable
            public void run() {
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                photosAlbumActivty.GetAlbumsFromDatabase(photosAlbumActivty._SortBy);
                Message message = new Message();
                message.what = 1;
                PhotosAlbumActivty.this.handle.sendMessage(message);
            }
        }, 300L);
        this.btn_Add_Album.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!PhotosAlbumActivty.isEditPhotoAlbum) {
                    PhotosAlbumActivty.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.6
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                PhotosAlbumActivty.albumPosition = PhotosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (PhotosAlbumActivty.isEditPhotoAlbum) {
                    PhotosAlbumActivty.isEditPhotoAlbum = false;
                    PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                    photosAlbumActivty.adapter = new PhotosAlbumsAdapter(photosAlbumActivty2, 17367043, photosAlbumActivty2.photoAlbums, i, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                    PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                    PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(i)).getId();
                PhotosAlbumActivty.this.startActivity(new Intent(PhotosAlbumActivty.this, Photos_Gallery_Actitvity.class));
                PhotosAlbumActivty.this.finish();
            }
        });
        this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.7
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                PhotosAlbumActivty.albumPosition = PhotosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (PhotosAlbumActivty.isEditPhotoAlbum) {
                    PhotosAlbumActivty.isEditPhotoAlbum = false;
                    PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                    photosAlbumActivty.adapter = new PhotosAlbumsAdapter(photosAlbumActivty2, 17367043, photosAlbumActivty2.photoAlbums, i, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                    PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                    PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                } else {
                    PhotosAlbumActivty.isEditPhotoAlbum = true;
                    PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Show_Params);
                    PhotosAlbumActivty.this.position = i;
                    PhotosAlbumActivty.this.AlbumId = Common.FolderId;
                    PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                    photosAlbumActivty3.albumName = ((PhotoAlbum) photosAlbumActivty3.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName();
                    PhotosAlbumActivty photosAlbumActivty4 = PhotosAlbumActivty.this;
                    PhotosAlbumActivty photosAlbumActivty5 = PhotosAlbumActivty.this;
                    photosAlbumActivty4.adapter = new PhotosAlbumsAdapter(photosAlbumActivty5, 17367043, photosAlbumActivty5.photoAlbums, i, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                    PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                    PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                }
                if (PhotosAlbumActivty.albumPosition != 0) {
                    PhotosAlbumActivty.this.gridView.setSelection(PhotosAlbumActivty.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId() != 1) {
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    photosAlbumActivty.EditAlbumPopup(((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getId(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(PhotosAlbumActivty.this, (int) R.string.lbl_default_album_notrenamed, 0).show();
                PhotosAlbumActivty.isEditPhotoAlbum = false;
                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                photosAlbumActivty2.adapter = new PhotosAlbumsAdapter(photosAlbumActivty3, 17367043, photosAlbumActivty3.photoAlbums, 0, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId() != 1) {
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    photosAlbumActivty.DeleteALertDialog(((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getId(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName(), ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(PhotosAlbumActivty.this, (int) R.string.lbl_default_album_notdeleted, 0).show();
                PhotosAlbumActivty.isEditPhotoAlbum = false;
                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                photosAlbumActivty2.adapter = new PhotosAlbumsAdapter(photosAlbumActivty3, 17367043, photosAlbumActivty3.photoAlbums, 0, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = true;
                Common.FolderId = ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId();
                PhotosAlbumActivty.this.AlbumId = Common.FolderId;
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                photosAlbumActivty.albumName = ((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName();
                Intent intent = new Intent(PhotosAlbumActivty.this, ImportAlbumsGalleryPhotoActivity.class);
                intent.putExtra("ALBUM_ID", PhotosAlbumActivty.this.AlbumId);
                intent.putExtra("FOLDER_NAME", PhotosAlbumActivty.this.AlbumId);
                PhotosAlbumActivty.this.startActivity(intent);
                PhotosAlbumActivty.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Common.FolderId = ((PhotoAlbum) PhotosAlbumActivty.this.photoAlbums.get(PhotosAlbumActivty.this.position)).getId();
                PhotosAlbumActivty.this.AlbumId = Common.FolderId;
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                photosAlbumActivty.albumName = ((PhotoAlbum) photosAlbumActivty.photoAlbums.get(PhotosAlbumActivty.this.position)).getAlbumName();
                PhotosAlbumActivty.this.openCameraIntent();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
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
        arrayList2.add("Tile");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.12
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.13
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        PhotosAlbumActivty.isGridView = false;
                        PhotosAlbumActivty.this.ViewBy();
                        popupWindow.dismiss();
                        PhotosAlbumActivty.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        PhotosAlbumActivty.isGridView = true;
                        PhotosAlbumActivty.this.ViewBy();
                        popupWindow.dismiss();
                        PhotosAlbumActivty.this.IsMoreDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        PhotosAlbumActivty.this._SortBy = SortBy.Name.ordinal();
                        PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                        photosAlbumActivty.GetAlbumsFromDatabase(photosAlbumActivty._SortBy);
                        PhotosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(PhotosAlbumActivty.this._SortBy);
                        popupWindow.dismiss();
                        PhotosAlbumActivty.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        PhotosAlbumActivty.this._SortBy = SortBy.Time.ordinal();
                        PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                        photosAlbumActivty2.GetAlbumsFromDatabase(photosAlbumActivty2._SortBy);
                        PhotosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(PhotosAlbumActivty.this._SortBy);
                        popupWindow.dismiss();
                        PhotosAlbumActivty.this.IsMoreDropdown = false;
                    }
                }
                return false;
            }
        });
        if (!this.IsMoreDropdown) {
            LinearLayout linearLayout = this.ll_anchor;
            popupWindow.showAsDropDown(linearLayout, linearLayout.getWidth(), 0);
            this.IsMoreDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsMoreDropdown = false;
    }

    public void ViewBy() {
        if (isGridView) {
            this.gridView.setNumColumns(2);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
        this.adapter = photosAlbumsAdapter;
        this.gridView.setAdapter((ListAdapter) photosAlbumsAdapter);
        this.adapter.notifyDataSetChanged();
    }

    public void GetAlbumsFromDatabase(int i) {
        isEditPhotoAlbum = false;
        PhotoAlbumDAL photoAlbumDAL2 = new PhotoAlbumDAL(this);
        this.photoAlbumDAL = photoAlbumDAL2;
        try {
            photoAlbumDAL2.OpenRead();
            ArrayList<PhotoAlbum> arrayList = (ArrayList) this.photoAlbumDAL.GetAlbums(i);
            this.photoAlbums = arrayList;
            Iterator<PhotoAlbum> it = arrayList.iterator();
            while (it.hasNext()) {
                PhotoAlbum next = it.next();
                next.setAlbumCoverLocation(GetCoverPhotoLocation(next.getId()));
            }
            PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
            this.adapter = photosAlbumsAdapter;
            this.gridView.setAdapter(photosAlbumsAdapter);
            this.adapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            PhotoAlbumDAL photoAlbumDAL3 = this.photoAlbumDAL;
            if (photoAlbumDAL3 != null) {
                photoAlbumDAL3.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AddAlbumPopup() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        ((TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PhotosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_please_enter, 0).show();
                    return;
                }
                PhotosAlbumActivty.this.albumName = editText.getEditableText().toString();
                File file = new File(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.PHOTOS + PhotosAlbumActivty.this.albumName);
                if (file.exists()) {
                    PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                    Toast.makeText(photosAlbumActivty, "\"" + PhotosAlbumActivty.this.albumName + "\" already exist", 0).show();
                } else if (file.mkdirs()) {
                    AlbumsGalleryPhotosMethods albumsGalleryPhotosMethods = new AlbumsGalleryPhotosMethods();
                    PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                    albumsGalleryPhotosMethods.AddAlbumToDatabase(photosAlbumActivty2, photosAlbumActivty2.albumName);
                    Toast.makeText(PhotosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_Success, 0).show();
                    PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                    photosAlbumActivty3.GetAlbumsFromDatabase(photosAlbumActivty3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(PhotosAlbumActivty.this, "ERROR! Some Error in creating album", 0).show();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void EditAlbumPopup(final int i, final String str, final String str2) {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_Photos_Album_Rename_Album);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        if (str.length() > 0) {
            editText.setText(str);
        }
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PhotosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_please_enter, 0).show();
                    return;
                }
                PhotosAlbumActivty.this.albumName = editText.getEditableText().toString();
                if (new File(str2).exists()) {
                    File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + PhotosAlbumActivty.this.albumName);
                    if (file.exists()) {
                        PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                        Toast.makeText(photosAlbumActivty, "\"" + PhotosAlbumActivty.this.albumName + "\" already exist", 0).show();
                        return;
                    }
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + str);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        AlbumsGalleryPhotosMethods albumsGalleryPhotosMethods = new AlbumsGalleryPhotosMethods();
                        PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                        albumsGalleryPhotosMethods.UpdateAlbumInDatabase(photosAlbumActivty2, i, photosAlbumActivty2.albumName);
                        Toast.makeText(PhotosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_Success_renamed, 0).show();
                        PhotosAlbumActivty photosAlbumActivty3 = PhotosAlbumActivty.this;
                        photosAlbumActivty3.GetAlbumsFromDatabase(photosAlbumActivty3._SortBy);
                        dialog.dismiss();
                        PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty.isEditPhotoAlbum = false;
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                photosAlbumActivty.adapter = new PhotosAlbumsAdapter(photosAlbumActivty2, 17367043, photosAlbumActivty2.photoAlbums, PhotosAlbumActivty.this.position, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void DeleteALertDialog(final int i, final String str, final String str2) {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        if (str.length() > 9) {
            textView.setText("Are you sure you want to delete " + str.substring(0, 8) + ".. including its data?");
        } else {
            textView.setText("Are you sure you want to delete " + str + " including its data?");
        }
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.18
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotosAlbumActivty.this.DeleteAlbum(i, str, str2);
                dialog.dismiss();
                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhotosAlbumActivty.19
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotosAlbumActivty.isEditPhotoAlbum = false;
                PhotosAlbumActivty.this.ll_EditAlbum.setLayoutParams(PhotosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                PhotosAlbumActivty photosAlbumActivty = PhotosAlbumActivty.this;
                PhotosAlbumActivty photosAlbumActivty2 = PhotosAlbumActivty.this;
                photosAlbumActivty.adapter = new PhotosAlbumsAdapter(photosAlbumActivty2, 17367043, photosAlbumActivty2.photoAlbums, PhotosAlbumActivty.this.position, PhotosAlbumActivty.isEditPhotoAlbum, PhotosAlbumActivty.isGridView);
                PhotosAlbumActivty.this.gridView.setAdapter((ListAdapter) PhotosAlbumActivty.this.adapter);
                PhotosAlbumActivty.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeleteAlbumFromDatabase(i);
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void DeleteAlbumFromDatabase(int i) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this);
        try {
            try {
                photoAlbumDAL.OpenWrite();
                photoAlbumDAL.DeleteAlbumById(i);
                Toast.makeText(this, (int) R.string.lbl_Photos_Album_delete_success, 0).show();
                GetAlbumsFromDatabase(this._SortBy);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            photoAlbumDAL.close();
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
                GetAlbumsFromDatabase(this._SortBy);
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    public Bitmap GetCoverPhoto(int i) {
        String str;
        new Photo();
        PhotoDAL photoDAL = new PhotoDAL(this);
        try {
            try {
                photoDAL.OpenRead();
                str = photoDAL.GetCoverPhoto(i).getFolderLockPhotoLocation();
                photoDAL.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                photoDAL.close();
                str = null;
            }
            if (str != null) {
                File file = new File(str);
                if (file.exists()) {
                    return Utilities.DecodeFile(file);
                }
            }
            return null;
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
    }

    /* JADX WARN: Finally extract failed */
    public String GetCoverPhotoLocation(int i) {
        String str;
        new Photo();
        PhotoDAL photoDAL = new PhotoDAL(this);
        try {
            try {
                photoDAL.OpenRead();
                str = photoDAL.GetCoverPhoto(i).getFolderLockPhotoLocation();
                photoDAL.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                photoDAL.close();
                str = null;
            }
            if (str != null) {
                return str;
            }
            return null;
        } catch (Throwable th) {
            photoDAL.close();
            throw th;
        }
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
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(3);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
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

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (isEditPhotoAlbum) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEditPhotoAlbum = false;
                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
                this.adapter = photosAlbumsAdapter;
                this.gridView.setAdapter((ListAdapter) photosAlbumsAdapter);
                this.adapter.notifyDataSetChanged();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
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
                intent.putExtra("output", FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, this.cacheDir));
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

    public void Back() {
        if (isEditPhotoAlbum) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEditPhotoAlbum = false;
            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            PhotosAlbumsAdapter photosAlbumsAdapter = new PhotosAlbumsAdapter(this, 17367043, this.photoAlbums, 0, isEditPhotoAlbum, isGridView);
            this.adapter = photosAlbumsAdapter;
            this.gridView.setAdapter((ListAdapter) photosAlbumsAdapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more_cloud, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != R.id.action_cloud) {
            if (itemId != R.id.action_more) {
                return super.onOptionsItemSelected(menuItem);
            }
            this.IsMoreDropdown = false;
            showPopupWindow();
            return true;
        } else if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = CloudCommon.DropboxType.Photos.ordinal();
            Utilities.StartCloudActivity(this);
            return true;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
           /* InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.PhotoAlbum.ordinal();
            startActivity(new Intent(this, InAppPurchaseActivity.class));
            finish();*/
            return true;
        }
    }
}
