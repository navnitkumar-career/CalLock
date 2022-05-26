package com.example.gallerylock.audio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.Flaes;
import com.example.gallerylock.R;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.FileUtils;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes2.dex */
public class AudiosImportActivity extends BaseActivity {
    private static String AUDIO_LOCALTION = "/audio/";
    public ProgressBar Progress;
    ListView album_import_ListView;
    AppCompatImageView btnSelectAll;
    ImageView document_empty_icon;
    private AudioSystemFileAdapter filesAdapter;
    int folderId;
    private AudioFoldersImportAdapter folderadapter;
    GridView imagegrid;
    TextView lbl_import_photo_album_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_photo_video_empty;
    int selectCount;
    String selectedCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    List<ImportAlbumEnt> importAlbumEnts = new ArrayList();
    ArrayList<String> spinnerValues = new ArrayList<>();
    boolean isAlbumClick = false;
    private boolean IsExceptionInImportPhotos = false;
    String folderPath = "";
    String folderName = "";
    private ArrayList<AudioEnt> audioImportEntList = new ArrayList<>();
    private ArrayList<AudioEnt> audioImportEntListShow = new ArrayList<>();
    List<List<AudioEnt>> audioEntListShowList = new ArrayList();
    private ArrayList<String> selectPath = new ArrayList<>();
    ProgressDialog myProgressDialog = null;
    Context context = this;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                AudiosImportActivity.this.hideProgress();
                AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                AudiosImportActivity audiosImportActivity2 = AudiosImportActivity.this;
                audiosImportActivity.filesAdapter = new AudioSystemFileAdapter(audiosImportActivity2, 1, audiosImportActivity2.audioImportEntListShow);
                AudiosImportActivity.this.imagegrid.setAdapter((ListAdapter) AudiosImportActivity.this.filesAdapter);
                AudiosImportActivity.this.filesAdapter.notifyDataSetChanged();
                if (AudiosImportActivity.this.audioImportEntListShow.size() <= 0) {
                    AudiosImportActivity.this.album_import_ListView.setVisibility(4);
                    AudiosImportActivity.this.imagegrid.setVisibility(4);
                    AudiosImportActivity.this.btnSelectAll.setVisibility(4);
                    AudiosImportActivity.this.ll_photo_video_empty.setVisibility(0);
                }
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    Common.IsImporting = false;
                    Toast.makeText(AudiosImportActivity.this, AudiosImportActivity.this.selectCount + " File(s) imported successfully", 0).show();
                    AudiosImportActivity.this.hideProgress();
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (Common.isFolderImport) {
                        Common.isFolderImport = false;
                        intent = new Intent(AudiosImportActivity.this, AudioActivity.class);
                    } else {
                        intent = new Intent(AudiosImportActivity.this, AudioActivity.class);
                    }
                    intent.addFlags(67108864);
                    AudiosImportActivity.this.startActivity(intent);
                    AudiosImportActivity.this.finish();
                }
                if (Common.InterstitialAdCount <= 1) {
                    Common.InterstitialAdCount++;
                }
            } else if (message.what == 2) {
                AudiosImportActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being copied... this may take a few moments... ", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        Back();
        invalidateOptionsMenu();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.import_album_list_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.lbl_import_photo_album_topbaar = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);*/
        this.lbl_import_photo_album_topbaar.setText("Select Audio");
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        StorageOptionsCommon.STORAGEPATH = StorageOptionSharedPreferences.GetObject(this).GetStoragePath();
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.document_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.folderId = Common.FolderId;
        this.folderName = null;
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenWrite();
        AudioPlayListEnt GetPlayListById = audioPlayListDAL.GetPlayListById(Common.FolderId);
        this.folderId = GetPlayListById.getId();
        this.folderName = GetPlayListById.getPlayListName();
        this.folderPath = GetPlayListById.getPlayListLocation();
        this.btnSelectAll.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudiosImportActivity.this.SelectAllAudios();
                AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                AudiosImportActivity audiosImportActivity2 = AudiosImportActivity.this;
                audiosImportActivity.filesAdapter = new AudioSystemFileAdapter(audiosImportActivity2, 1, audiosImportActivity2.audioImportEntListShow);
                AudiosImportActivity.this.imagegrid.setAdapter((ListAdapter) AudiosImportActivity.this.filesAdapter);
                AudiosImportActivity.this.filesAdapter.notifyDataSetChanged();
            }
        });
        this.ll_Import_bottom_baar.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AudiosImportActivity.this.OnImportClick();
            }
        });
        Iterator<AudioEnt> it = this.audioImportEntList.iterator();
        while (it.hasNext()) {
            AudioEnt next = it.next();
            if (this.spinnerValues.get(0).contains(new File(next.getOriginalAudioLocation()).getParent())) {
                this.audioImportEntListShow.add(next);
            }
        }
        this.album_import_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                AudiosImportActivity.this.isAlbumClick = true;
                AudiosImportActivity.this.album_import_ListView.setVisibility(4);
                AudiosImportActivity.this.imagegrid.setVisibility(0);
                AudiosImportActivity.this.btnSelectAll.setVisibility(0);
                AudiosImportActivity.this.folderadapter = new AudioFoldersImportAdapter(AudiosImportActivity.this.context, 17367043, AudiosImportActivity.this.importAlbumEnts, false);
                AudiosImportActivity.this.album_import_ListView.setAdapter((ListAdapter) AudiosImportActivity.this.folderadapter);
                AudiosImportActivity.this.audioImportEntListShow.clear();
                Iterator it2 = AudiosImportActivity.this.audioImportEntList.iterator();
                while (it2.hasNext()) {
                    AudioEnt audioEnt = (AudioEnt) it2.next();
                    if (AudiosImportActivity.this.spinnerValues.get(i).equals(new File(audioEnt.getOriginalAudioLocation()).getParent())) {
                        audioEnt.GetFileCheck();
                        AudiosImportActivity.this.audioImportEntListShow.add(audioEnt);
                    }
                }
                AudiosImportActivity audiosImportActivity = AudiosImportActivity.this;
                AudiosImportActivity audiosImportActivity2 = AudiosImportActivity.this;
                audiosImportActivity.filesAdapter = new AudioSystemFileAdapter(audiosImportActivity2, 1, audiosImportActivity2.audioImportEntListShow);
                AudiosImportActivity.this.imagegrid.setAdapter((ListAdapter) AudiosImportActivity.this.filesAdapter);
                AudiosImportActivity.this.filesAdapter.notifyDataSetChanged();
                if (AudiosImportActivity.this.audioImportEntListShow.size() <= 0) {
                    AudiosImportActivity.this.album_import_ListView.setVisibility(4);
                    AudiosImportActivity.this.imagegrid.setVisibility(4);
                    AudiosImportActivity.this.ll_photo_video_empty.setVisibility(0);
                    AudiosImportActivity.this.document_empty_icon.setBackgroundResource(R.drawable.ic_audio_empty_icon);
                    AudiosImportActivity.this.lbl_photo_video_empty.setText(R.string.no_audio);
                }
                AudiosImportActivity.this.invalidateOptionsMenu();
            }
        });
        AudioSystemFileAdapter audioSystemFileAdapter = new AudioSystemFileAdapter(this, 1, this.audioImportEntListShow);
        this.filesAdapter = audioSystemFileAdapter;
        this.imagegrid.setAdapter((ListAdapter) audioSystemFileAdapter);
        AudioFileBind();
        GetFolders();
        this.Progress.setVisibility(8);
    }

    private void AudioFileBind() {
        Iterator<File> it = new FileUtils().FindFiles(new String[]{"mp3", "wav", "m4a"}).iterator();
        while (it.hasNext()) {
            File next = it.next();
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.SetFile(next);
            audioEnt.setAudioName(next.getName());
            audioEnt.setOriginalAudioLocation(next.getAbsolutePath());
            audioEnt.setPlayListId(Common.PlayListId);
            audioEnt.SetFileCheck(false);
            audioEnt.SetFileImage(null);
            this.audioImportEntList.add(audioEnt);
            ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
            if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(next.getParent())) {
                importAlbumEnt.SetAlbumName(next.getParent());
                importAlbumEnt.Set_Activity_type(Common.ActivityType.Music.toString());
                this.importAlbumEnts.add(importAlbumEnt);
                this.spinnerValues.add(next.getParent());
            }
        }
    }

    public void GetFolders() {
        AudioFoldersImportAdapter audioFoldersImportAdapter = new AudioFoldersImportAdapter(this.context, 17367043, this.importAlbumEnts, false);
        this.folderadapter = audioFoldersImportAdapter;
        this.album_import_ListView.setAdapter((ListAdapter) audioFoldersImportAdapter);
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(4);
            this.imagegrid.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
            this.document_empty_icon.setBackgroundResource(R.drawable.ic_audio_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.no_audio);
        }
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
            Toast.makeText(this, (int) R.string.toast_unselectaudiomsg_import, 0).show();
        } else if (Common.GetFileSize(this.selectPath) < Common.GetTotalFree()) {
            int albumCheckCount = albumCheckCount();
            if (albumCheckCount >= 2) {
                final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.confirmation_message_box);
                dialog.setCancelable(true);
                Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
                TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
                textView.setTypeface(createFromAsset);
                textView.setText("Are you sure you want to import " + albumCheckCount + " folders? Importing may take time according to the size of your data.");
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        for (int i = 0; i < AudiosImportActivity.this.importAlbumEnts.size(); i++) {
                            AudiosImportActivity.this.importAlbumEnts.get(i).SetAlbumFileCheck(false);
                        }
                        AudiosImportActivity.this.folderadapter = new AudioFoldersImportAdapter(AudiosImportActivity.this.context, 17367043, AudiosImportActivity.this.importAlbumEnts, false);
                        AudiosImportActivity.this.album_import_ListView.setAdapter((ListAdapter) AudiosImportActivity.this.folderadapter);
                        AudiosImportActivity.this.folderadapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                            AudiosImportActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            AudiosImportActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            AudiosImportActivity.this.LollipopSdCardPermissionDialog();
                        } else {
                            AudiosImportActivity.this.Import();
                        }
                    }
                });
                dialog.show();
            } else if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                Import();
            } else if (GetObject.GetSDCardUri().length() > 0) {
                Import();
            } else if (!GetObject.GetISDAlertshow()) {
                LollipopSdCardPermissionDialog();
            } else {
                Import();
            }
        }
    }

    void LollipopSdCardPermissionDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.sdcard_permission_alert_msgbox);
        dialog.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        ((CheckBox) dialog.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                StorageOptionSharedPreferences.GetObject(AudiosImportActivity.this).SetISDAlertshow(Boolean.valueOf(z));
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
                AudiosImportActivity.this.Import();
            }
        });
        dialog.show();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity$10] */
    void Import() {
        SelectedCount();
        ShowImportProgress();
        Common.IsWorkInProgress = true;
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudiosImportActivity.10
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    AudiosImportActivity.this.ImportAudio();
                    Message message = new Message();
                    message.what = 3;
                    AudiosImportActivity.this.handle.sendMessage(message);
                    Common.IsWorkInProgress = false;
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 3;
                    AudiosImportActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void ImportAudio() {
        if (this.isAlbumClick) {
            ImportOnlyAudioSDCard();
        } else {
            importFolder();
        }
    }

    void importFolder() {
        if (this.importAlbumEnts.size() > 0) {
            int i = 0;
            for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                if (this.importAlbumEnts.get(i2).GetAlbumFileCheck()) {
                    File file = new File(this.importAlbumEnts.get(i2).GetAlbumName());
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + file.getName());
                    this.folderName = file.getName();
                    if (file2.exists()) {
                        int i3 = 1;
                        while (i3 < 100) {
                            this.folderName = file.getName() + "(" + i3 + ")";
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.AUDIOS);
                            sb.append(this.folderName);
                            file2 = new File(sb.toString());
                            if (!file2.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                    }
                    AddFolderToDatabase(this.folderName);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
                    audioPlayListDAL.OpenRead();
                    int GetLastPlayListId = audioPlayListDAL.GetLastPlayListId();
                    this.folderId = GetLastPlayListId;
                    Common.FolderId = GetLastPlayListId;
                    audioPlayListDAL.close();
                    ImportAlbumsAudioSDCard(i);
                    i++;
                }
            }
        }
    }

    void ImportAlbumsAudioSDCard(int i) {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List<AudioEnt> list = this.audioEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (list.get(i2).GetFileCheck()) {
                File file = new File(list.get(i2).getOriginalAudioLocation());
                File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + this.folderName);
                try {
                    String str = "";
                    File file3 = new File(file2.getAbsolutePath() + "/" + Utilities.ChangeFileExtention(file.getName()));
                    if (file.exists()) {
                        File parentFile = file3.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        file3.createNewFile();
                        Flaes.encryptUsingCipherStream_AES128(file, file3);
                        str = file3.getAbsolutePath();
                    }
                    if (file.exists() && file3.exists()) {
                        file.delete();
                    }
                    if (str.length() > 0) {
                        AddAudioToDatabase(FileName(list.get(i2).getOriginalAudioLocation()), list.get(i2).getOriginalAudioLocation(), str);
                    }
                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
            }
        }
    }

    void ImportOnlyAudioSDCard() {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.audioImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (this.audioImportEntListShow.get(i).GetFileCheck()) {
                File file = new File(this.audioImportEntListShow.get(i).getOriginalAudioLocation());
                File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + this.folderName);
                String str = "";
                try {
                    File file3 = new File(file2.getAbsolutePath() + "/" + Utilities.ChangeFileExtention(file.getName()));
                    if (file.exists()) {
                        File parentFile = file3.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        file3.createNewFile();
                        Flaes.encryptUsingCipherStream_AES128(file, file3);
                        str = file3.getAbsolutePath();
                        if (file.exists() && file3.exists()) {
                            file.delete();
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
                if (str.length() > 0) {
                    String[] split = str.split("/");
                    AddAudioToDatabase(Utilities.ChangeFileExtentionToOrignal(split[split.length - 1]), this.audioImportEntListShow.get(i).getOriginalAudioLocation(), str);
                }
            }
        }
        Common.SelectedCount = 0;
        Common.IsSelectAll = false;
    }

    void AddAudioToDatabase(String str, String str2, String str3) {
        AudioEnt audioEnt = new AudioEnt();
        audioEnt.setAudioName(str);
        audioEnt.setFolderLockAudioLocation(str3);
        audioEnt.setOriginalAudioLocation(str2);
        audioEnt.setPlayListId(this.folderId);
        AudioDAL audioDAL = new AudioDAL(this.context);
        try {
            try {
                audioDAL.OpenWrite();
                audioDAL.AddAudio(audioEnt, str3);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioDAL.close();
        }
    }

    public void AddFolderToDatabase(String str) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setPlayListName(str);
        audioPlayListEnt.setPlayListLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + str);
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this);
        try {
            try {
                audioPlayListDAL.OpenWrite();
                audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioPlayListDAL.close();
        }
    }

    private byte[] getRawKey(byte[] bArr) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(bArr);
        keyGenerator.init(128, secureRandom);
        return keyGenerator.generateKey().getEncoded();
    }

    private byte[] encrypt(byte[] bArr, byte[] bArr2) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, secretKeySpec);
        return cipher.doFinal(bArr2);
    }

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (this.importAlbumEnts.get(i).GetAlbumFileCheck()) {
                this.audioImportEntListShow = new ArrayList<>();
                Iterator<AudioEnt> it = this.audioImportEntList.iterator();
                while (it.hasNext()) {
                    AudioEnt next = it.next();
                    if (this.spinnerValues.get(i).equals(new File(next.getOriginalAudioLocation()).getParent())) {
                        this.audioImportEntListShow.add(next);
                    }
                    for (int i2 = 0; i2 < this.audioImportEntListShow.size(); i2++) {
                        this.audioImportEntListShow.get(i2).SetFileCheck(true);
                    }
                }
                this.audioEntListShowList.add(this.audioImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.audioImportEntListShow.size(); i3++) {
            if (this.audioImportEntListShow.get(i3).GetFileCheck()) {
                this.selectPath.add(this.audioImportEntListShow.get(i3).getOriginalAudioLocation());
                return true;
            }
        }
        return false;
    }

    private void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.audioImportEntListShow.size(); i++) {
            if (this.audioImportEntListShow.get(i).GetFileCheck()) {
                this.selectCount++;
            }
        }
    }

    public void btnBackonClick(View view) {
        Back();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectAllAudios() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.audioImportEntListShow.size(); i++) {
                this.audioImportEntListShow.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            Common.IsSelectAll = false;
            SelectedItemsCount(0);
            Common.SelectedCount = 0;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.audioImportEntListShow.size(); i2++) {
            this.audioImportEntListShow.get(i2).SetFileCheck(true);
        }
        Common.SelectedCount = this.audioImportEntListShow.size();
        this.IsSelectAll = true;
        Common.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
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
        Common.SelectedCount = 0;
        if (this.isAlbumClick) {
            this.isAlbumClick = false;
            this.album_import_ListView.setVisibility(0);
            this.imagegrid.setVisibility(4);
            this.btnSelectAll.setVisibility(4);
            for (int i = 0; i < this.audioImportEntListShow.size(); i++) {
                this.audioImportEntListShow.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            Common.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, AudioActivity.class));
        finish();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Common.SelectedCount = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (SecurityLocksCommon.IsAppDeactive && !Common.IsWorkInProgress) {
            finish();
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Common.SelectedCount = 0;
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.album_import_ListView.setVisibility(0);
                this.imagegrid.setVisibility(4);
                this.btnSelectAll.setVisibility(4);
                for (int i2 = 0; i2 < this.audioImportEntListShow.size(); i2++) {
                    this.audioImportEntListShow.get(i2).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                Common.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, AudioPlayListActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void SelectedItemsCount(int i) {
        this.selectedCount = Integer.toString(i);
        invalidateOptionsMenu();
    }
}
