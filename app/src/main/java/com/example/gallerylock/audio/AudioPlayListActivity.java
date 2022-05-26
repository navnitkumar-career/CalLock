package com.example.gallerylock.audio;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.gallerylock.LibCommonAppClass;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class AudioPlayListActivity extends BaseActivity {
    private static int RESULT_LOAD_CAMERA = 1;
    public static int albumPosition = 0;
    public static boolean isEditAudioAlbum = false;
    public static boolean isGridView = true;
    public ProgressBar Progress;
    private AudioPlayListAdapter adapter;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private ArrayList<AudioPlayListEnt> audioAlbums;
    private AudioPlayListDAL audioPlayListDAL;
    private FloatingActionButton btn_Add_Album;
    private GridView gridView;
    private ImageButton ib_more;
    LinearLayout ll_EditAlbum;
    LinearLayout.LayoutParams ll_EditAlbum_Hide_Params;
    LinearLayout.LayoutParams ll_EditAlbum_Show_Params;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_rename_btn;
    private Uri outputFileUri;
    int position;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    String albumName = "";
    int AlbumId = 0;
    int _SortBy = 0;
    boolean IsMoreDropdown = false;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                AudioPlayListActivity.this.Progress.setVisibility(8);
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
        setContentView(R.layout.activity_audios_test);
        Log.d("TAG", "Audio");
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
//        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
      /*  setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle(R.string.lblFeature9);
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudioPlayListActivity.this.btnBackonClick();
            }
        });*/
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.ll_rename_btn = (LinearLayout) findViewById(R.id.ll_rename_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_EditAlbum_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, 0);
        this.ll_EditAlbum_Hide_Params = layoutParams;
        this.ll_EditAlbum.setLayoutParams(layoutParams);
        ImageButton imageButton = (ImageButton) findViewById(R.id.ib_more);
        this.ib_more = imageButton;
        imageButton.setVisibility(0);
        ((TextView) findViewById(R.id.tvActivityTopBaar_Title)).setText(R.string.lblFeature9);
        AppSettingsSharedPreferences GetObject = AppSettingsSharedPreferences.GetObject(this);
        this.appSettingsSharedPreferences = GetObject;
        this._SortBy = GetObject.GetAudioViewBy();
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
        this.ll_background.setOnTouchListener(new View.OnTouchListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (AudioPlayListActivity.this.IsMoreDropdown) {
                    AudioPlayListActivity.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(0);
        new Handler().postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.4
            @Override // java.lang.Runnable
            public void run() {
                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                audioPlayListActivity.GetAlbumsFromDatabase(audioPlayListActivity._SortBy);
                Message message = new Message();
                message.what = 1;
                AudioPlayListActivity.this.handle.sendMessage(message);
            }
        }, 300L);
        this.btn_Add_Album.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!AudioPlayListActivity.isEditAudioAlbum) {
                    AudioPlayListActivity.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.6
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                AudioPlayListActivity.albumPosition = AudioPlayListActivity.this.gridView.getFirstVisiblePosition();
                if (AudioPlayListActivity.isEditAudioAlbum) {
                    AudioPlayListActivity.isEditAudioAlbum = false;
                    AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                    audioPlayListActivity.adapter = new AudioPlayListAdapter(audioPlayListActivity2, 17367043, audioPlayListActivity2.audioAlbums, i, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                    AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                    AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.FolderId = ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(i)).getId();
                AudioPlayListActivity.this.startActivity(new Intent(AudioPlayListActivity.this, AudioActivity.class));
                AudioPlayListActivity.this.finish();
            }
        });
        this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.7
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                AudioPlayListActivity.albumPosition = AudioPlayListActivity.this.gridView.getFirstVisiblePosition();
                if (AudioPlayListActivity.isEditAudioAlbum) {
                    AudioPlayListActivity.isEditAudioAlbum = false;
                    AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                    audioPlayListActivity.adapter = new AudioPlayListAdapter(audioPlayListActivity2, 17367043, audioPlayListActivity2.audioAlbums, i, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                    AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                    AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                } else {
                    AudioPlayListActivity.isEditAudioAlbum = true;
                    AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Show_Params);
                    AudioPlayListActivity.this.position = i;
                    AudioPlayListActivity.this.AlbumId = Common.FolderId;
                    AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                    audioPlayListActivity3.albumName = ((AudioPlayListEnt) audioPlayListActivity3.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListName();
                    AudioPlayListActivity audioPlayListActivity4 = AudioPlayListActivity.this;
                    AudioPlayListActivity audioPlayListActivity5 = AudioPlayListActivity.this;
                    audioPlayListActivity4.adapter = new AudioPlayListAdapter(audioPlayListActivity5, 17367043, audioPlayListActivity5.audioAlbums, i, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                    AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                    AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                }
                if (AudioPlayListActivity.albumPosition != 0) {
                    AudioPlayListActivity.this.gridView.setSelection(AudioPlayListActivity.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getId() != 1) {
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    audioPlayListActivity.EditAlbumPopup(((AudioPlayListEnt) audioPlayListActivity.audioAlbums.get(AudioPlayListActivity.this.position)).getId(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListName(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListLocation());
                    return;
                }
                Toast.makeText(AudioPlayListActivity.this, (int) R.string.lbl_default_album_notrenamed, 0).show();
                AudioPlayListActivity.isEditAudioAlbum = false;
                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                audioPlayListActivity2.adapter = new AudioPlayListAdapter(audioPlayListActivity3, 17367043, audioPlayListActivity3.audioAlbums, 0, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getId() != 1) {
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    audioPlayListActivity.DeleteALertDialog(((AudioPlayListEnt) audioPlayListActivity.audioAlbums.get(AudioPlayListActivity.this.position)).getId(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListName(), ((AudioPlayListEnt) AudioPlayListActivity.this.audioAlbums.get(AudioPlayListActivity.this.position)).getPlayListLocation());
                    return;
                }
                Toast.makeText(AudioPlayListActivity.this, (int) R.string.lbl_default_album_notdeleted, 0).show();
                AudioPlayListActivity.isEditAudioAlbum = false;
                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                audioPlayListActivity2.adapter = new AudioPlayListAdapter(audioPlayListActivity3, 17367043, audioPlayListActivity3.audioAlbums, 0, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
    }

    public void btnOnCloudClick(View view) {
        SecurityLocksCommon.IsAppDeactive = false;
        CloudCommon.ModuleType = CloudCommon.DropboxType.Audio.ordinal();
        Utilities.StartCloudActivity(this);
    }

    public void btnOnMoreClick(View view) {
        this.IsMoreDropdown = false;
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
        arrayList2.add("Tile");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.10
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.11
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        AudioPlayListActivity.isGridView = false;
                        AudioPlayListActivity.this.ViewBy();
                        popupWindow.dismiss();
                        AudioPlayListActivity.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        AudioPlayListActivity.isGridView = true;
                        AudioPlayListActivity.this.ViewBy();
                        popupWindow.dismiss();
                        AudioPlayListActivity.this.IsMoreDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        AudioPlayListActivity.this._SortBy = SortBy.Name.ordinal();
                        AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                        audioPlayListActivity.GetAlbumsFromDatabase(audioPlayListActivity._SortBy);
                        AudioPlayListActivity.this.appSettingsSharedPreferences.SetAudioViewBy(AudioPlayListActivity.this._SortBy);
                        popupWindow.dismiss();
                        AudioPlayListActivity.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        AudioPlayListActivity.this._SortBy = SortBy.Time.ordinal();
                        AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                        audioPlayListActivity2.GetAlbumsFromDatabase(audioPlayListActivity2._SortBy);
                        AudioPlayListActivity.this.appSettingsSharedPreferences.SetAudioViewBy(AudioPlayListActivity.this._SortBy);
                        popupWindow.dismiss();
                        AudioPlayListActivity.this.IsMoreDropdown = false;
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
        AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
        this.adapter = audioPlayListAdapter;
        this.gridView.setAdapter((ListAdapter) audioPlayListAdapter);
        this.adapter.notifyDataSetChanged();
    }

    public void btnBackonClick() {
        if (isEditAudioAlbum) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEditAudioAlbum = false;
            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
            this.adapter = audioPlayListAdapter;
            this.gridView.setAdapter((ListAdapter) audioPlayListAdapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
    }

    public void GetAlbumsFromDatabase(int i) {
        isEditAudioAlbum = false;
        AudioPlayListDAL audioPlayListDAL2 = new AudioPlayListDAL(this);
        this.audioPlayListDAL = audioPlayListDAL2;
        try {
            audioPlayListDAL2.OpenRead();
            this.audioAlbums = (ArrayList) this.audioPlayListDAL.GetPlayLists(i);
            AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
            this.adapter = audioPlayListAdapter;
            this.gridView.setAdapter(audioPlayListAdapter);
            this.adapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            AudioPlayListDAL audioPlayListDAL3 = this.audioPlayListDAL;
            if (audioPlayListDAL3 != null) {
                audioPlayListDAL3.close();
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
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AudioPlayListActivity.this, (int) R.string.lbl_Photos_Album_Create_Album_please_enter, 0).show();
                    return;
                }
                AudioPlayListActivity.this.albumName = editText.getEditableText().toString();
                File file = new File(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.AUDIOS + AudioPlayListActivity.this.albumName);
                if (file.exists()) {
                    AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                    Toast.makeText(audioPlayListActivity, "\"" + AudioPlayListActivity.this.albumName + "\" already exist", 0).show();
                } else if (file.mkdirs()) {
                    AudioPlaylistGalleryMethods audioPlaylistGalleryMethods = new AudioPlaylistGalleryMethods();
                    AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                    audioPlaylistGalleryMethods.AddPlaylistToDatabase(audioPlayListActivity2, audioPlayListActivity2.albumName);
                    Toast.makeText(AudioPlayListActivity.this, (int) R.string.lbl_Photos_Album_Create_Album_Success, 0).show();
                    AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                    audioPlayListActivity3.GetAlbumsFromDatabase(audioPlayListActivity3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(AudioPlayListActivity.this, "ERROR! Some Error in creating album", 0).show();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.13
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
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AudioPlayListActivity.this, (int) R.string.lbl_Photos_Album_Create_Album_please_enter, 0).show();
                    return;
                }
                AudioPlayListActivity.this.albumName = editText.getEditableText().toString();
                if (new File(str2).exists()) {
                    File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + AudioPlayListActivity.this.albumName);
                    if (file.exists()) {
                        AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                        Toast.makeText(audioPlayListActivity, "\"" + AudioPlayListActivity.this.albumName + "\" already exist", 0).show();
                        return;
                    }
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + str);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        AudioPlaylistGalleryMethods audioPlaylistGalleryMethods = new AudioPlaylistGalleryMethods();
                        AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                        audioPlaylistGalleryMethods.UpdatePlaylistInDatabase(audioPlayListActivity2, i, audioPlayListActivity2.albumName);
                        Toast.makeText(AudioPlayListActivity.this, (int) R.string.lbl_Photos_Album_Create_Album_Success_renamed, 0).show();
                        AudioPlayListActivity audioPlayListActivity3 = AudioPlayListActivity.this;
                        audioPlayListActivity3.GetAlbumsFromDatabase(audioPlayListActivity3._SortBy);
                        dialog.dismiss();
                        AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity.isEditAudioAlbum = false;
                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                audioPlayListActivity.adapter = new AudioPlayListAdapter(audioPlayListActivity2, 17367043, audioPlayListActivity2.audioAlbums, AudioPlayListActivity.this.position, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
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
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudioPlayListActivity.this.DeleteAlbum(i, str, str2);
                dialog.dismiss();
                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioPlayListActivity.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudioPlayListActivity.isEditAudioAlbum = false;
                AudioPlayListActivity.this.ll_EditAlbum.setLayoutParams(AudioPlayListActivity.this.ll_EditAlbum_Hide_Params);
                AudioPlayListActivity audioPlayListActivity = AudioPlayListActivity.this;
                AudioPlayListActivity audioPlayListActivity2 = AudioPlayListActivity.this;
                audioPlayListActivity.adapter = new AudioPlayListAdapter(audioPlayListActivity2, 17367043, audioPlayListActivity2.audioAlbums, AudioPlayListActivity.this.position, AudioPlayListActivity.isEditAudioAlbum, AudioPlayListActivity.isGridView);
                AudioPlayListActivity.this.gridView.setAdapter((ListAdapter) AudioPlayListActivity.this.adapter);
                AudioPlayListActivity.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeletePlayListFromDatabase(i);
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void DeletePlayListFromDatabase(int i) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        try {
            try {
                audioPlayListDAL.OpenWrite();
                audioPlayListDAL.DeletePlayListById(i);
                Toast.makeText(this, (int) R.string.lbl_delete_success, 0).show();
                GetAlbumsFromDatabase(this._SortBy);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioPlayListDAL.close();
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
            if (isEditAudioAlbum) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEditAudioAlbum = false;
                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                AudioPlayListAdapter audioPlayListAdapter = new AudioPlayListAdapter(this, 17367043, this.audioAlbums, 0, isEditAudioAlbum, isGridView);
                this.adapter = audioPlayListAdapter;
                this.gridView.setAdapter((ListAdapter) audioPlayListAdapter);
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
            CloudCommon.ModuleType = CloudCommon.DropboxType.Audio.ordinal();
            Utilities.StartCloudActivity(this);
            return true;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            /*InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.AudioFolder.ordinal();
            startActivity(new Intent(this, InAppPurchaseActivity.class));
            finish();*/
            return true;
        }
    }
}
