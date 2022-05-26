package com.example.gallerylock.video;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class VideosAlbumActivty extends BaseActivity {
    private static final int ACTION_TAKE_VIDEO = 2;
    private static final int RESULT_LOAD_IMAGE = 1;
    public static int albumPosition = 0;
    public static boolean isEditMode = false;
    public static boolean isGridView = true;
    public ProgressBar Progress;
    private VideosAlbumsAdapter adapter;
    String albumName;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private FloatingActionButton btn_Add_Album;
    GridView gridView;
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
    private Uri mVideoUri;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private ArrayList<VideoAlbum> videoAlbum;
    private VideoAlbumDAL videoAlbumDAL;
    int Position = 0;
    int AlbumId = 0;
    int _SortBy = 0;
    boolean IsMoreDropdown = false;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                VideosAlbumActivty.this.Progress.setVisibility(View.GONE);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_photos_videos_albums);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        /*setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle(R.string.lblFeature2);
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideosAlbumActivty.this.btnBackonClick();
            }
        });*/
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getWindow().addFlags(128);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
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
        this.ll_background.setOnTouchListener(new View.OnTouchListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (VideosAlbumActivty.this.IsMoreDropdown) {
                    VideosAlbumActivty.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(0);
        new Handler().postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.4
            @Override // java.lang.Runnable
            public void run() {
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
                Message message = new Message();
                message.what = 1;
                VideosAlbumActivty.this.handle.sendMessage(message);
            }
        }, 300L);
        this.btn_Add_Album.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!VideosAlbumActivty.isEditMode) {
                    VideosAlbumActivty.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.6
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                VideosAlbumActivty.albumPosition = VideosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (VideosAlbumActivty.isEditMode) {
                    VideosAlbumActivty.isEditMode = false;
                    VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                    videosAlbumActivty.adapter = new VideosAlbumsAdapter(videosAlbumActivty2, 17367043, videosAlbumActivty2.videoAlbum, i, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                    VideosAlbumActivty.this.gridView.setAdapter((ListAdapter) VideosAlbumActivty.this.adapter);
                    VideosAlbumActivty.this.adapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(i)).getId();
                VideosAlbumActivty.this.startActivity(new Intent(VideosAlbumActivty.this, Videos_Gallery_Actitvity.class));
                VideosAlbumActivty.this.finish();
            }
        });
        this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.7
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                VideosAlbumActivty.albumPosition = VideosAlbumActivty.this.gridView.getFirstVisiblePosition();
                if (VideosAlbumActivty.isEditMode) {
                    VideosAlbumActivty.isEditMode = false;
                    VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                    videosAlbumActivty.adapter = new VideosAlbumsAdapter(videosAlbumActivty2, 17367043, videosAlbumActivty2.videoAlbum, i, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                    VideosAlbumActivty.this.gridView.setAdapter((ListAdapter) VideosAlbumActivty.this.adapter);
                    VideosAlbumActivty.this.adapter.notifyDataSetChanged();
                } else {
                    VideosAlbumActivty.isEditMode = true;
                    VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Show_Params);
                    VideosAlbumActivty.this.Position = i;
                    VideosAlbumActivty.this.AlbumId = Common.FolderId;
                    VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                    videosAlbumActivty3.albumName = ((VideoAlbum) videosAlbumActivty3.videoAlbum.get(i)).getAlbumName();
                    VideosAlbumActivty videosAlbumActivty4 = VideosAlbumActivty.this;
                    VideosAlbumActivty videosAlbumActivty5 = VideosAlbumActivty.this;
                    videosAlbumActivty4.adapter = new VideosAlbumsAdapter(videosAlbumActivty5, 17367043, videosAlbumActivty5.videoAlbum, i, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                    VideosAlbumActivty.this.gridView.setAdapter((ListAdapter) VideosAlbumActivty.this.adapter);
                    VideosAlbumActivty.this.adapter.notifyDataSetChanged();
                }
                if (VideosAlbumActivty.albumPosition != 0) {
                    VideosAlbumActivty.this.gridView.setSelection(VideosAlbumActivty.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getId() != 1) {
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    videosAlbumActivty.EditAlbumPopup(((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getId(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(VideosAlbumActivty.this, (int) R.string.lbl_default_album_notrenamed, 0).show();
                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                videosAlbumActivty2.adapter = new VideosAlbumsAdapter(videosAlbumActivty3, 17367043, videosAlbumActivty3.videoAlbum, 0, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                VideosAlbumActivty.this.gridView.setAdapter((ListAdapter) VideosAlbumActivty.this.adapter);
                VideosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getId() != 1) {
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    videosAlbumActivty.DeleteALertDialog(((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getId(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName(), ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumLocation());
                    return;
                }
                Toast.makeText(VideosAlbumActivty.this, (int) R.string.lbl_default_album_notdeleted, 0).show();
                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                videosAlbumActivty2.adapter = new VideosAlbumsAdapter(videosAlbumActivty3, 17367043, videosAlbumActivty3.videoAlbum, 0, VideosAlbumActivty.isEditMode, VideosAlbumActivty.isGridView);
                VideosAlbumActivty.this.gridView.setAdapter((ListAdapter) VideosAlbumActivty.this.adapter);
                VideosAlbumActivty.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = true;
                Common.FolderId = ((VideoAlbum) VideosAlbumActivty.this.videoAlbum.get(VideosAlbumActivty.this.Position)).getId();
                VideosAlbumActivty.this.AlbumId = Common.FolderId;
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.albumName = ((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName();
                VideosAlbumActivty.this.startActivity(new Intent(VideosAlbumActivty.this, ImportAlbumsGalleryVideoActivity.class));
                VideosAlbumActivty.this.finish();
            }
        });
        this.ll_import_from_camera_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.AlbumId = ((VideoAlbum) videosAlbumActivty.videoAlbum.get(VideosAlbumActivty.this.Position)).getId();
                VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                videosAlbumActivty2.albumName = ((VideoAlbum) videosAlbumActivty2.videoAlbum.get(VideosAlbumActivty.this.Position)).getAlbumName();
                VideosAlbumActivty.this.dispatchTakeVideoIntent();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
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
        arrayList2.add("Tile");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.12
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.13
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        VideosAlbumActivty.isGridView = false;
                        VideosAlbumActivty.this.ViewBy();
                        popupWindow.dismiss();
                        VideosAlbumActivty.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        VideosAlbumActivty.isGridView = true;
                        VideosAlbumActivty.this.ViewBy();
                        popupWindow.dismiss();
                        VideosAlbumActivty.this.IsMoreDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        VideosAlbumActivty.this._SortBy = SortBy.Name.ordinal();
                        VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                        videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
                        VideosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(VideosAlbumActivty.this._SortBy);
                        popupWindow.dismiss();
                        VideosAlbumActivty.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        VideosAlbumActivty.this._SortBy = SortBy.Time.ordinal();
                        VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                        videosAlbumActivty2.GetAlbumsFromDatabase(videosAlbumActivty2._SortBy);
                        VideosAlbumActivty.this.appSettingsSharedPreferences.SetPhotosAlbumsSortBy(VideosAlbumActivty.this._SortBy);
                        popupWindow.dismiss();
                        VideosAlbumActivty.this.IsMoreDropdown = false;
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
        VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
        this.adapter = videosAlbumsAdapter;
        this.gridView.setAdapter((ListAdapter) videosAlbumsAdapter);
        this.adapter.notifyDataSetChanged();
    }

    public void btnBackonClick() {
        if (isEditMode) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEditMode = false;
            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
            this.adapter = videosAlbumsAdapter;
            this.gridView.setAdapter((ListAdapter) videosAlbumsAdapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
    }

    public void GetAlbumsFromDatabase(int i) {
        isEditMode = false;
        VideoAlbumDAL videoAlbumDAL2 = new VideoAlbumDAL(this);
        this.videoAlbumDAL = videoAlbumDAL2;
        try {
            videoAlbumDAL2.OpenRead();
            ArrayList<VideoAlbum> arrayList = (ArrayList) this.videoAlbumDAL.GetAlbums(i);
            this.videoAlbum = arrayList;
            Iterator<VideoAlbum> it = arrayList.iterator();
            while (it.hasNext()) {
                VideoAlbum next = it.next();
                next.setAlbumCoverLocation(GetCoverPhotoLocation(next.getId()));
            }
            VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
            this.adapter = videosAlbumsAdapter;
            this.gridView.setAdapter(videosAlbumsAdapter);
            this.adapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            VideoAlbumDAL videoAlbumDAL3 = this.videoAlbumDAL;
            if (videoAlbumDAL3 != null) {
                videoAlbumDAL3.close();
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
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(VideosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_please_enter, 0).show();
                    return;
                }
                VideosAlbumActivty.this.albumName = editText.getEditableText().toString();
                File file = new File(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.VIDEOS + VideosAlbumActivty.this.albumName);
                if (file.exists()) {
                    VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                    Toast.makeText(videosAlbumActivty, "\"" + VideosAlbumActivty.this.albumName + "\" already exist", Toast.LENGTH_SHORT).show();
                } else if (file.mkdirs()) {
                    AlbumsGalleryVideosMethods albumsGalleryVideosMethods = new AlbumsGalleryVideosMethods();
                    VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                    albumsGalleryVideosMethods.AddAlbumToDatabase(videosAlbumActivty2, videosAlbumActivty2.albumName);
                    Toast.makeText(VideosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_Success, 0).show();
                    VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                    videosAlbumActivty3.GetAlbumsFromDatabase(videosAlbumActivty3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(VideosAlbumActivty.this, "ERROR! Some Error in creating album", 0).show();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.15
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
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(VideosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_please_enter, 0).show();
                    return;
                }
                VideosAlbumActivty.this.albumName = editText.getEditableText().toString();
                if (new File(str2).exists()) {
                    File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + VideosAlbumActivty.this.albumName);
                    if (file.exists()) {
                        VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                        Toast.makeText(videosAlbumActivty, "\"" + VideosAlbumActivty.this.albumName + "\" already exist", 0).show();
                        return;
                    }
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + str);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        AlbumsGalleryVideosMethods albumsGalleryVideosMethods = new AlbumsGalleryVideosMethods();
                        VideosAlbumActivty videosAlbumActivty2 = VideosAlbumActivty.this;
                        albumsGalleryVideosMethods.UpdateAlbumInDatabase(videosAlbumActivty2, i, videosAlbumActivty2.albumName);
                        Toast.makeText(VideosAlbumActivty.this, (int) R.string.lbl_Photos_Album_Create_Album_Success_renamed, 0).show();
                        VideosAlbumActivty videosAlbumActivty3 = VideosAlbumActivty.this;
                        videosAlbumActivty3.GetAlbumsFromDatabase(videosAlbumActivty3._SortBy);
                        dialog.dismiss();
                        VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
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
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        if (str.length() > 9) {
            textView.setText("Are you sure you want to delete " + str.substring(0, 8) + ".. including its data?");
        } else {
            textView.setText("Are you sure you want to delete " + str + " including its data?");
        }
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.18
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideosAlbumActivty.this.DeleteAlbum(i, str, str2);
                dialog.dismiss();
                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.VideosAlbumActivty.19
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideosAlbumActivty.this.ll_EditAlbum.setLayoutParams(VideosAlbumActivty.this.ll_EditAlbum_Hide_Params);
                VideosAlbumActivty.isEditMode = false;
                VideosAlbumActivty videosAlbumActivty = VideosAlbumActivty.this;
                videosAlbumActivty.GetAlbumsFromDatabase(videosAlbumActivty._SortBy);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeleteAlbumFromDatabase(i);
        File file2 = new File(str2 + File.separator + "VideoThumnails");
        if (file2.exists()) {
            file2.delete();
        }
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void DeleteAlbumFromDatabase(int i) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this);
        try {
            try {
                videoAlbumDAL.OpenWrite();
                videoAlbumDAL.DeleteAlbumById(i);
                Toast.makeText(this, (int) R.string.lbl_Photos_Album_delete_success, 0).show();
                GetAlbumsFromDatabase(this._SortBy);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoAlbumDAL.close();
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
            GetAlbumsFromDatabase(this._SortBy);
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
        video.setAlbumId(this.AlbumId);
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

    /* JADX WARN: Finally extract failed */
    public String GetCoverPhotoLocation(int i) {
        new Video();
        VideoDAL videoDAL = new VideoDAL(this);
        try {
            try {
                videoDAL.OpenRead();
                String str = videoDAL.GetCoverVideo(i).getthumbnail_video_location();
                videoDAL.close();
                return str;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                videoDAL.close();
                return null;
            }
        } catch (Throwable th) {
            videoDAL.close();
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
        Common.imageLoader.clearMemoryCache();
        Common.imageLoader.clearDiskCache();
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
            if (isEditMode) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEditMode = false;
                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                VideosAlbumsAdapter videosAlbumsAdapter = new VideosAlbumsAdapter(this, 17367043, this.videoAlbum, 0, isEditMode, isGridView);
                this.adapter = videosAlbumsAdapter;
                this.gridView.setAdapter((ListAdapter) videosAlbumsAdapter);
                this.adapter.notifyDataSetChanged();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
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
            CloudCommon.ModuleType = CloudCommon.DropboxType.Videos.ordinal();
            Utilities.StartCloudActivity(this);
            return true;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
           /* InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.VideoAlbum.ordinal();
            startActivity(new Intent(this, InAppPurchaseActivity.class));
            finish();*/
            return true;
        }
    }
}
