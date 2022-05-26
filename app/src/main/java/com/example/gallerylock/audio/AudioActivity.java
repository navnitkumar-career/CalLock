package com.example.gallerylock.audio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.Flaes;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.photo.MoveAlbumAdapter;
import com.example.gallerylock.privatebrowser.SecureBrowserActivity;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class AudioActivity extends BaseActivity implements View.OnClickListener {
    public static int _ViewBy;
    public static ProgressDialog myProgressDialog;
    private String[] _folderNameArray;
    int albumId;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private AudioDAL audioDAL;
    List<AudioEnt> audioEntList;
    private AudioFileAdapter audioFileAdapter;
    private AudioPlayListEnt audioFolder;
    private AudioPlayListDAL audioFolderDAL;
    FloatingActionButton fabImpBrowser;
    FloatingActionButton fabImpGallery;
    FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    ImageView file_empty_icon;
    FrameLayout fl_bottom_baar;
    protected String folderLocation;
    String folderName;
    GridView imagegrid;
    TextView lbl_file_empty;
    LinearLayout ll_EditAlbum;
    LinearLayout.LayoutParams ll_EditAlbum_Hide_Params;
    LinearLayout.LayoutParams ll_EditAlbum_Show_Params;
    LinearLayout.LayoutParams ll_Hide_Params;
    LinearLayout.LayoutParams ll_Show_Params;
    LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_file_empty;
    LinearLayout ll_file_grid;
    LinearLayout ll_move_btn;
    LinearLayout ll_share_btn;
    LinearLayout ll_unhide_btn;
    private String moveToFolderLocation;
    int selectCount;
    String selectedCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    private ArrayList<String> files = new ArrayList<>();
    private int fileCount = 0;
    boolean isEditMode = false;
    boolean isAudioShared = false;
    private List<String> _folderNameArrayForMove = null;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    private String GA_Audio = "Audio Play List";
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                AudioActivity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(AudioActivity.this, (int) R.string.Unhide_error, 0).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                }
            } else if (message.what == 4) {
                Toast.makeText(AudioActivity.this, (int) R.string.toast_share, 1).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(AudioActivity.this, (int) R.string.toast_unhide, 1).show();
                    AudioActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(AudioActivity.this, AudioActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AudioActivity.this.startActivity(intent);
                        AudioActivity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(AudioActivity.this, (int) R.string.toast_delete, 0).show();
                    AudioActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(AudioActivity.this, AudioActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AudioActivity.this.startActivity(intent2);
                        AudioActivity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(AudioActivity.this, (int) R.string.toast_move, 0).show();
                    AudioActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(AudioActivity.this, AudioActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AudioActivity.this.startActivity(intent3);
                        AudioActivity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    final Context context = this;
    private boolean IsSelectAll = false;

    /* loaded from: classes2.dex */
    public enum SortBy {
        Time,
        Name,
        Size
    }

    /* loaded from: classes2.dex */
    public enum ViewBy {
        List,
        Detail
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_delete_btn /* 2131296782 */:
                DeleteFiles();
                return;
            case R.id.ll_move_btn /* 2131296814 */:
                MoveFiles();
                return;
            case R.id.ll_share_btn /* 2131296840 */:
                if (!IsFileCheck()) {
                    Toast.makeText(this, (int) R.string.toast_unselectaudiomsg_share, 0).show();
                    return;
                } else {
                    ShareAudio();
                    return;
                }
            case R.id.ll_unhide_btn /* 2131296854 */:
                UnhideFiles();
                return;
            default:
                return;
        }
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

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(android.os.Bundle r4) {
        super.onCreate(r4);
        setContentView((int) R.layout.activity_audio);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       /* Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        setSupportActionBar(toolbar2);
        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);*/
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-2, 0);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_bottom_baar);
        this.fl_bottom_baar = frameLayout;
        frameLayout.setLayoutParams(this.ll_Hide_Params);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_move_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.ll_file_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_file_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.file_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_file_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_file_grid.setVisibility(View.VISIBLE);
        this.ll_file_empty.setVisibility(View.INVISIBLE);
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayListById = audioPlayListDAL.GetPlayListById(Common.FolderId);
        this._SortBy = audioPlayListDAL.GetSortByPlaylistId(Common.FolderId);
        audioPlayListDAL.close();
        this.folderName = GetPlayListById.getPlayListName();
        getSupportActionBar().setTitle((CharSequence) this.folderName);
        this.folderLocation = GetPlayListById.getPlayListLocation();
        AppSettingsSharedPreferences GetObject = AppSettingsSharedPreferences.GetObject(this);
        this.appSettingsSharedPreferences = GetObject;
        _ViewBy = GetObject.GetAudioViewBy();
        this.ll_delete_btn.setOnClickListener(this);
        this.ll_unhide_btn.setOnClickListener(this);
        this.ll_move_btn.setOnClickListener(this);
        this.ll_share_btn.setOnClickListener(this);
        this.ll_background.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (AudioActivity.this.IsSortingDropdown) {
                    AudioActivity.this.IsSortingDropdown = false;
                }
                if (AudioActivity.this.IsSortingDropdown) {
                    AudioActivity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.fabImpGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                AudioActivity.this.startActivity(new Intent(AudioActivity.this, AudiosImportActivity.class));
                AudioActivity.this.finish();
            }
        });
        this.fabImpBrowser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.CurrentWebBrowserActivity = AudioActivity.this;
                AudioActivity.this.startActivity(new Intent(AudioActivity.this, SecureBrowserActivity.class));
                AudioActivity.this.finish();
            }
        });
        this.fabImpPcMac.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Common.IsAirplaneModeOn(AudioActivity.this.context) || !Common.IsWiFiModeOn(AudioActivity.this.context) || !Common.IsWiFiConnect(AudioActivity.this.context)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.CurrentWebServerActivity = AudioActivity.this;
                    AudioActivity.this.finish();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                Common.CurrentWebServerActivity = AudioActivity.this;
                AudioActivity.this.finish();
            }
        });
        this.imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!AudioActivity.this.isEditMode) {
                    int id = AudioActivity.this.audioEntList.get(i).getId();
                    AudioDAL audioDAL = new AudioDAL(AudioActivity.this);
                    audioDAL.OpenRead();
                    String folderLockAudioLocation = audioDAL.GetAudio(Integer.toString(id)).getFolderLockAudioLocation();
                    audioDAL.close();
                    String FileName = Utilities.FileName(folderLockAudioLocation);
                    if (FileName.contains("#")) {
                        FileName = Utilities.ChangeFileExtentionToOrignal(FileName);
                    }
                    File file = new File(folderLockAudioLocation);
                    File file2 = new File(file.getParent() + "/" + FileName);
                    file.renameTo(file2);
                    AudioActivity.this.CopyTempFile(file2.getAbsolutePath());
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.PhotoThumbnailCurrentPosition = AudioActivity.this.imagegrid.getFirstVisiblePosition();
                AudioActivity.this.isEditMode = true;
                AudioActivity.this.audioEntList.get(i).SetFileCheck(true);
                AudioActivity.this.fl_bottom_baar.setLayoutParams(AudioActivity.this.ll_Show_Params);
                AudioActivity.this.ll_EditAlbum.setVisibility(0);
                AudioActivity.this.invalidateOptionsMenu();
                AudioActivity audioActivity = AudioActivity.this;
                AudioActivity audioActivity2 = AudioActivity.this;
                AudioFileAdapter unused = audioActivity.audioFileAdapter = new AudioFileAdapter(audioActivity2, audioActivity2, 1, audioActivity2.audioEntList, true, AudioActivity._ViewBy);
                AudioActivity.this.imagegrid.setAdapter(AudioActivity.this.audioFileAdapter);
                AudioActivity.this.audioFileAdapter.notifyDataSetChanged();
                if (Common.PhotoThumbnailCurrentPosition != 0) {
                    AudioActivity.this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
                }
                return true;
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
            LoadFilesFromDB(this._SortBy);
        }
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
        this.imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (new File(AudioActivity.this.audioEntList.get(i).getFolderLockAudioLocation()).exists()) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.CurrentTrackId = AudioActivity.this.audioEntList.get(i).getId();
                    Intent intent = new Intent(AudioActivity.this, AudioPlayerActivity.class);
                    Common.CurrentTrackNextIndex = i;
                    AudioActivity.this.startActivity(intent);
                    AudioActivity.this.finish();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                AudioActivity audioActivity = AudioActivity.this;
                audioActivity.startActivity(audioActivity.getIntent());
                AudioActivity.this.finish();
            }
        });
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.audioEntList.size(); i++) {
            this.audioEntList.get(i).SetFileCheck(false);
        }
        AudioFileAdapter audioFileAdapter = new AudioFileAdapter(this, this, 1, this.audioEntList, false, _ViewBy);
        this.audioFileAdapter = audioFileAdapter;
        this.imagegrid.setAdapter((ListAdapter) audioFileAdapter);
        this.audioFileAdapter.notifyDataSetChanged();
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

    public void Back() {
        Common.SelectedCount = 0;
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.IsSelectAll = false;
            Common.IsSelectAll = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_EditAlbum.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
        } else if (this.fabMenu.isExpanded()) {
            this.fabMenu.collapse();
            this.IsSortingDropdown = false;
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();
        }
    }

    public void UnhideFiles() {
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
            textView.setText("Are you sure you want to restore (" + this.selectCount + ") audio(s)?");
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.9
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.10
                /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.audio.AudioActivity$10$1] */
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    AudioActivity.this.showUnhideProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.10.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                AudioActivity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                AudioActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                AudioActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Unhide() throws IOException {
        AudioDAL audioDAL = new AudioDAL(this);
        audioDAL.OpenWrite();
        for (AudioEnt audioEnt : this.audioEntList) {
            if (audioEnt.GetFileCheck()) {
                File file = new File(audioEnt.getFolderLockAudioLocation());
                File file2 = new File(Environment.getExternalStorageDirectory().getPath() + Common.UnhideKitkatAlbumName + audioEnt.getAudioName());
                File file3 = new File(file2.getParent());
                if (!file3.exists() && !file3.mkdirs() && file2.exists()) {
                    file2 = Utilities.GetDesFileNameForUnHide(file2.getAbsolutePath(), file2.getName(), file2);
                }
                if (file.exists()) {
                    file2.createNewFile();
                    Flaes.decryptUsingCipherStream_AES128(file, file2);
                    if (file.exists() && file2.exists()) {
                        file.delete();
                        audioDAL.DeleteAudio(audioEnt);
                    }
                }
            }
        }
        audioDAL.close();
    }

    public void DeleteFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_delete, Toast.LENGTH_SHORT).show();
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
        textView.setText("Are you sure you want to delete (" + this.selectCount + ") audio(s)?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.11
            /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.audio.AudioActivity$11$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudioActivity.this.showDeleteProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.11.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            AudioActivity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            AudioActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            AudioActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void Delete() {
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (this.audioEntList.get(i).GetFileCheck()) {
                new File(this.audioEntList.get(i).getFolderLockAudioLocation()).delete();
                DeleteFromDatabase(this.audioEntList.get(i).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        AudioDAL audioDAL;
        AudioDAL audioDAL2 = new AudioDAL(this);
        this.audioDAL = audioDAL2;
        try {
            try {
                audioDAL2.OpenWrite();
                this.audioDAL.DeleteAudioById(i);
                audioDAL = this.audioDAL;
                if (audioDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                audioDAL = this.audioDAL;
                if (audioDAL == null) {
                    return;
                }
            }
            audioDAL.close();
        } catch (Throwable th) {
            AudioDAL audioDAL3 = this.audioDAL;
            if (audioDAL3 != null) {
                audioDAL3.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (this.audioEntList.get(i).GetFileCheck()) {
                this.files.add(this.audioEntList.get(i).getFolderLockAudioLocation());
                this.selectCount++;
            }
        }
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (this.audioEntList.get(i).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MoveFiles() {
        AudioDAL audioDAL = new AudioDAL(this);
        this.audioDAL = audioDAL;
        audioDAL.OpenWrite();
        this._folderNameArray = this.audioDAL.GetPlayListNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectdocumentmsg_move, 0).show();
        } else if (this._folderNameArray.length > 0) {
            GetFolderNameFromDB();
        } else {
            Toast.makeText(this, (int) R.string.toast_OneFolder, 0).show();
        }
    }

    void Move(String str, String str2, String str3) {
        String str4;
        AudioPlayListEnt GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.audioEntList.size(); i++) {
            if (this.audioEntList.get(i).GetFileCheck()) {
                if (this.audioEntList.get(i).getAudioName().contains("#")) {
                    str4 = this.audioEntList.get(i).getAudioName();
                } else {
                    str4 = Utilities.ChangeFileExtention(this.audioEntList.get(i).getAudioName());
                }
                String str5 = str2 + "/" + str4;
                try {
                    if (Utilities.MoveFileWithinDirectories(this.audioEntList.get(i).getFolderLockAudioLocation(), str5)) {
                        UpdateFileLocationInDatabase(this.audioEntList.get(i), str5, GetAlbum.getId());
                        Common.FolderId = GetAlbum.getId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public AudioPlayListEnt GetAlbum(String str) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        this.audioFolderDAL = audioPlayListDAL;
        try {
            audioPlayListDAL.OpenRead();
            AudioPlayListEnt GetPlayList = this.audioFolderDAL.GetPlayList(str);
            this.audioFolder = GetPlayList;
            return GetPlayList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } catch (Throwable th) {
            AudioPlayListDAL audioPlayListDAL2 = this.audioFolderDAL;
            if (audioPlayListDAL2 != null) {
                audioPlayListDAL2.close();
            }
            throw th;
        }
    }


    public void UpdateFileLocationInDatabase(AudioEnt audioEnt, String str, int i) {
        AudioDAL audioDAL;
        audioEnt.setFolderLockAudioLocation(str);
        audioEnt.setPlayListId(i);
        try {
            try {
                AudioDAL audioDAL2 = new AudioDAL(this);
                audioDAL2.OpenWrite();
                audioDAL2.UpdateAudiosLocation(audioEnt);
                audioDAL = this.audioDAL;
                if (audioDAL == null) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                audioDAL = this.audioDAL;
                if (audioDAL == null) {
                    return;
                }
            }
            audioDAL.close();
        } catch (Throwable th) {
            AudioDAL audioDAL3 = this.audioDAL;
            if (audioDAL3 != null) {
                audioDAL3.close();
            }
            throw th;
        }
    }

    private void GetFolderNameFromDB() {
        AudioDAL audioDAL;
        AudioDAL audioDAL2 = new AudioDAL(this);
        this.audioDAL = audioDAL2;
        try {
            try {
                audioDAL2.OpenWrite();
                this._folderNameArrayForMove = this.audioDAL.GetMovePlayListNames(Common.FolderId);
                MoveDocumentDialog();
                audioDAL = this.audioDAL;
                if (audioDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                audioDAL = this.audioDAL;
                if (audioDAL == null) {
                    return;
                }
            }
            audioDAL.close();
        } catch (Throwable th) {
            AudioDAL audioDAL3 = this.audioDAL;
            if (audioDAL3 != null) {
                audioDAL3.close();
            }
            throw th;
        }
    }

    void MoveDocumentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.move_customlistview);
        dialog.setTitle(R.string.lbl_Moveto);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        listView.setAdapter((ListAdapter) new MoveAlbumAdapter(this, 17367043, this._folderNameArrayForMove, R.drawable.audio_list_icon));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.13
            /* JADX WARN: Type inference failed for: r1v5, types: [net.newsoftwares.hidepicturesvideos.audio.AudioActivity$13$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (AudioActivity.this._folderNameArrayForMove != null) {
                    AudioActivity.this.SelectedCount();
                    AudioActivity.this.showMoveProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.13.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                AudioActivity audioActivity = AudioActivity.this;
                                audioActivity.moveToFolderLocation = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + ((String) AudioActivity.this._folderNameArrayForMove.get(i));
                                AudioActivity.this.Move(AudioActivity.this.folderLocation, AudioActivity.this.moveToFolderLocation, (String) AudioActivity.this._folderNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                AudioActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                AudioActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.audio.AudioActivity$14] */
    public void ShareAudio() {
        showCopyFilesProcessForShareProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.14
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : AudioActivity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageCommon.AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS;
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList<Uri> arrayList3 = new ArrayList<>();
                            for (int i = 0; i < AudioActivity.this.audioEntList.size(); i++) {
                                if (AudioActivity.this.audioEntList.get(i).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(AudioActivity.this, AudioActivity.this.audioEntList.get(i).getFolderLockAudioLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(AudioActivity.this, AppPackageCommon.AppPackageName, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    createChooser.addFlags(1);
                    createChooser.addFlags(2);
                    AudioActivity.this.startActivity(createChooser);
                    AudioActivity.this.isAudioShared = true;
                    Message message = new Message();
                    message.what = 4;
                    AudioActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    AudioActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    void LoadFilesFromDB(int i) {
        this.audioEntList = new ArrayList();
        AudioDAL audioDAL = new AudioDAL(this);
        audioDAL.OpenRead();
        this.fileCount = audioDAL.GetAudiosCountByFolderId(Common.FolderId);
        this.audioEntList = audioDAL.GetAudiosByAlbumId(Common.FolderId, i);
        Common.sortType = i;
        audioDAL.close();
        AudioFileAdapter audioFileAdapter = new AudioFileAdapter(this, this, 1, this.audioEntList, false, _ViewBy);
        this.audioFileAdapter = audioFileAdapter;
        this.imagegrid.setAdapter((ListAdapter) audioFileAdapter);
        this.audioFileAdapter.notifyDataSetChanged();
        if (this.audioEntList.size() < 1) {
            this.ll_file_grid.setVisibility(View.INVISIBLE);
            this.ll_file_empty.setVisibility(View.VISIBLE);
            this.file_empty_icon.setBackgroundResource(R.drawable.ic_audio_empty_icon);
            this.lbl_file_empty.setText(R.string.lbl_No_audio);
            return;
        }
        this.ll_file_grid.setVisibility(View.VISIBLE);
        this.ll_file_empty.setVisibility(View.INVISIBLE);
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
        arrayList2.add("Detail");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        arrayList3.add("Size");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.15
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioActivity.16
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        AudioActivity._ViewBy = ViewBy.List.ordinal();
                        AudioActivity.this.ViewBy();
                        popupWindow.dismiss();
                        AudioActivity.this.IsSortingDropdown = false;
                        AudioActivity.this.appSettingsSharedPreferences.SetAudioViewBy(AudioActivity._ViewBy);
                    } else if (i2 == 1) {
                        AudioActivity._ViewBy = ViewBy.Detail.ordinal();
                        AudioActivity.this.ViewBy();
                        popupWindow.dismiss();
                        AudioActivity.this.IsSortingDropdown = false;
                        AudioActivity.this.appSettingsSharedPreferences.SetAudioViewBy(AudioActivity._ViewBy);
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        AudioActivity.this._SortBy = SortBy.Name.ordinal();
                        AudioActivity audioActivity = AudioActivity.this;
                        audioActivity.LoadFilesFromDB(audioActivity._SortBy);
                        AudioActivity.this.AddSortInDB();
                        popupWindow.dismiss();
                        AudioActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        AudioActivity.this._SortBy = SortBy.Time.ordinal();
                        AudioActivity audioActivity2 = AudioActivity.this;
                        audioActivity2.LoadFilesFromDB(audioActivity2._SortBy);
                        AudioActivity.this.AddSortInDB();
                        popupWindow.dismiss();
                        AudioActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        AudioActivity.this._SortBy = SortBy.Size.ordinal();
                        AudioActivity audioActivity3 = AudioActivity.this;
                        audioActivity3.LoadFilesFromDB(audioActivity3._SortBy);
                        AudioActivity.this.AddSortInDB();
                        popupWindow.dismiss();
                        AudioActivity.this.IsSortingDropdown = false;
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
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        audioPlayListDAL.OpenWrite();
        audioPlayListDAL.AddSortByInAudioPlaylist(this._SortBy);
        audioPlayListDAL.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CopyTempFile(String str) {
        File file = new File(str);
        try {
            String guessContentTypeFromName = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(Constants.FILE + file.getAbsolutePath()), guessContentTypeFromName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                Back();
                return true;
            case R.id.action_delete /* 2131296319 */:
                DeleteFiles();
                return true;
            case R.id.action_more /* 2131296331 */:
                this.IsSortingDropdown = false;
                showPopupWindow();
                return true;
            case R.id.action_move /* 2131296332 */:
                MoveFiles();
                return true;
            case R.id.action_select /* 2131296335 */:
                if (this.IsSelectAll) {
                    for (int i = 0; i < this.audioEntList.size(); i++) {
                        this.audioEntList.get(i).SetFileCheck(false);
                    }
                    this.IsSelectAll = false;
                    menuItem.setIcon(R.drawable.ic_unselectallicon);
                    Common.IsSelectAll = false;
                    SelectedItemsCount(0);
                    Common.SelectedCount = 0;
                    invalidateOptionsMenu();
                } else {
                    for (int i2 = 0; i2 < this.audioEntList.size(); i2++) {
                        this.audioEntList.get(i2).SetFileCheck(true);
                    }
                    Common.SelectedCount = this.audioEntList.size();
                    this.IsSelectAll = true;
                    menuItem.setIcon(R.drawable.ic_selectallicon);
                    Common.IsSelectAll = true;
                }
                AudioFileAdapter audioFileAdapter = new AudioFileAdapter(this, this, 1, this.audioEntList, true, _ViewBy);
                this.audioFileAdapter = audioFileAdapter;
                this.imagegrid.setAdapter((ListAdapter) audioFileAdapter);
                this.audioFileAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_share /* 2131296337 */:
                if (!IsFileCheck()) {
                    Toast.makeText(this, (int) R.string.toast_unselectaudiomsg_share, 0).show();
                } else {
                    ShareAudio();
                }
                return true;
            case R.id.action_unlock /* 2131296339 */:
                UnhideFiles();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
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

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Common.SelectedCount = 0;
    }

    public void ViewBy() {
        AudioFileAdapter audioFileAdapter = new AudioFileAdapter(this, this, 1, this.audioEntList, false, _ViewBy);
        this.audioFileAdapter = audioFileAdapter;
        this.imagegrid.setAdapter((ListAdapter) audioFileAdapter);
        this.audioFileAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        Common.IsWorkInProgress = true;
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
        this.handle.removeCallbacksAndMessages(null);
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        SetcheckFlase();
        this.IsSortingDropdown = false;
        this.isEditMode = false;
        this.IsSelectAll = false;
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_EditAlbum.setVisibility(View.INVISIBLE);
        invalidateOptionsMenu();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Common.SelectedCount = 0;
        if (i == 4) {
            if (this.isEditMode) {
                SetcheckFlase();
                this.IsSortingDropdown = false;
                this.isEditMode = false;
                this.IsSelectAll = false;
                Common.IsSelectAll = false;
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_EditAlbum.setVisibility(View.INVISIBLE);
                invalidateOptionsMenu();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void SelectedItemsCount(int i) {
        this.selectedCount = Integer.toString(i);
    }
}
