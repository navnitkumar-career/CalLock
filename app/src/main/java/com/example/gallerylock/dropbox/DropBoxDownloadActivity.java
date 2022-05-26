package com.example.gallerylock.dropbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.AudioPlayListActivity;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.documents.DocumentsFolderActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.notes.NotesFoldersActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.photo.PhotosAlbumActivty;
import com.example.gallerylock.securebackupcloud.BackupCloudEnt;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securebackupcloud.DropboxCloud;
import com.example.gallerylock.securebackupcloud.ISecureBackupCloud;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.SettingActivity;
import com.example.gallerylock.todolist.ToDoActivity;
import com.example.gallerylock.video.VideosAlbumActivty;
import com.example.gallerylock.wallet.WalletCategoriesActivity;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class DropBoxDownloadActivity extends BaseActivity {
    public static ProgressDialog myProgressDialog;
    ArrayList<BackupCloudEnt> backupCloudEntList;
    DropboxAdapter dropboxAdapter;
    ListView dropboxdownloadListView;
    ISecureBackupCloud iSecureBackupCloud;
    LinearLayout ll_background;
    LinearLayout ll_topbaar;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    final Handler myHandler = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            DropBoxDownloadActivity.this.UpDataBind(0, 0);
            DropBoxDownloadActivity.this.UpDatedListView();
        }
    };
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 3) {
                DropBoxDownloadActivity.this.hideProgress();
            } else if (message.what == 1) {
                DropBoxDownloadActivity.this.hideProgress();
                DropBoxDownloadActivity.this.dropboxAdapter = new DropboxAdapter(DropBoxDownloadActivity.this.getApplicationContext(), 17367043, DropBoxDownloadActivity.this.backupCloudEntList);
                DropBoxDownloadActivity.this.dropboxdownloadListView.setAdapter((ListAdapter) DropBoxDownloadActivity.this.dropboxAdapter);
                DropBoxDownloadActivity.this.dropboxAdapter.notifyDataSetChanged();
                DropBoxDownloadActivity.this.UpDatedListView();
            } else if (message.what == 2) {
                DropBoxDownloadActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX WARN: Type inference failed for: r2v23, types: [net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity$4] */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dropbox_download_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.dropboxdownloadListView = (ListView) findViewById(R.id.dropboxdownloadListView);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DropBoxDownloadActivity.this.btnBackonClick();
            }
        });
        if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.lblFeature1);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.lblFeature2);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.lblFeature4);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.lblFeature6);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.lblFeature7);
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.todoList);
        } else if (CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
            getSupportActionBar().setTitle(R.string.dropbox_Audios);
        }
        showProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity.4
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    DropBoxDownloadActivity.this.DataBind(CloudCommon.ModuleType);
                    Message message = new Message();
                    message.what = 1;
                    DropBoxDownloadActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 2;
                    DropBoxDownloadActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
        this.dropboxdownloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                BackupCloudEnt backupCloudEnt = DropBoxDownloadActivity.this.backupCloudEntList.get(i);
                if (!backupCloudEnt.GetIsInProgress() && backupCloudEnt.GetSyncVisibility() != 4 && backupCloudEnt.GetStatus() != CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal()) {
                    CloudService.AddBackupCloudEntList.add(backupCloudEnt);
                    DropBoxDownloadActivity.this.backupCloudEntList.get(i).SetIsInProgress(true);
                    int firstVisiblePosition = DropBoxDownloadActivity.this.dropboxdownloadListView.getFirstVisiblePosition();
                    int i2 = 0;
                    View childAt = DropBoxDownloadActivity.this.dropboxdownloadListView.getChildAt(0);
                    if (childAt != null) {
                        i2 = childAt.getTop();
                    }
                    DropBoxDownloadActivity.this.UpDataBind(firstVisiblePosition, i2);
                    if (!CloudCommon.IsCloudServiceStarted) {
                        DropBoxDownloadActivity.this.startService(new Intent(DropBoxDownloadActivity.this, CloudService.class));
                    }
                }
            }
        });
    }

    private void showProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... cloud data loading...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void btnBackonClick() {
        Intent intent;
        SecurityLocksCommon.IsAppDeactive = false;
        if (CloudCommon.IsCameFromSettings) {
            intent = new Intent(getApplicationContext(), SettingActivity.class);
            CloudCommon.IsCameFromSettings = false;
        } else if (CloudCommon.IsCameFromCloudMenu) {
            intent = new Intent(getApplicationContext(), FeaturesActivity.class);
            CloudCommon.IsCameFromCloudMenu = false;
        } else if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), PhotosAlbumActivty.class);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), VideosAlbumActivty.class);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), DocumentsFolderActivity.class);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), NotesFoldersActivity.class);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), WalletCategoriesActivity.class);
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), ToDoActivity.class);
        } else {
            intent = CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), AudioPlayListActivity.class) : null;
        }
        startActivity(intent);
        finish();
    }

    public void SyncAll(View view) {
        Iterator<BackupCloudEnt> it = this.backupCloudEntList.iterator();
        boolean z = false;
        while (it.hasNext()) {
            BackupCloudEnt next = it.next();
            if (!(next.GetIsInProgress() || next.GetSyncVisibility() == 4 || next.GetStatus() == CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal())) {
                CloudService.AddBackupCloudEntList.add(next);
                next.SetIsInProgress(true);
                z = true;
            }
        }
        int firstVisiblePosition = this.dropboxdownloadListView.getFirstVisiblePosition();
        View childAt = this.dropboxdownloadListView.getChildAt(0);
        int top = childAt == null ? 0 : childAt.getTop();
        if (z) {
            UpDataBind(firstVisiblePosition, top);
            Toast.makeText(getApplicationContext(), "Sync All", 0).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already sync", 0).show();
        }
        if (!CloudCommon.IsCloudServiceStarted) {
            startService(new Intent(this, CloudService.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void DataBind(int i) {
        if (CloudCommon.DropboxType.Photos.ordinal() == i) {
            DropboxCloud dropboxCloud = new DropboxCloud(this, CloudCommon.DropboxType.Photos.ordinal());
            this.iSecureBackupCloud = dropboxCloud;
            this.backupCloudEntList = dropboxCloud.GetFolders(CloudCommon.PhotoFolder);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == i) {
            DropboxCloud dropboxCloud2 = new DropboxCloud(this, CloudCommon.DropboxType.Videos.ordinal());
            this.iSecureBackupCloud = dropboxCloud2;
            this.backupCloudEntList = dropboxCloud2.GetFolders(CloudCommon.VideoFolder);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == i) {
            DropboxCloud dropboxCloud3 = new DropboxCloud(this, CloudCommon.DropboxType.Documents.ordinal());
            this.iSecureBackupCloud = dropboxCloud3;
            this.backupCloudEntList = dropboxCloud3.GetFolders(CloudCommon.DocumentFolder);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == i) {
            DropboxCloud dropboxCloud4 = new DropboxCloud(this, CloudCommon.DropboxType.Notes.ordinal());
            this.iSecureBackupCloud = dropboxCloud4;
            this.backupCloudEntList = dropboxCloud4.GetFolders(CloudCommon.NotesFolder);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == i) {
            DropboxCloud dropboxCloud5 = new DropboxCloud(this, CloudCommon.DropboxType.Wallet.ordinal());
            this.iSecureBackupCloud = dropboxCloud5;
            this.backupCloudEntList = dropboxCloud5.GetFolders(CloudCommon.WalletFolder);
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == i) {
            DropboxCloud dropboxCloud6 = new DropboxCloud(this, CloudCommon.DropboxType.ToDo.ordinal());
            this.iSecureBackupCloud = dropboxCloud6;
            this.backupCloudEntList = dropboxCloud6.GetFolders(CloudCommon.ToDoListFolder);
        } else if (CloudCommon.DropboxType.Audio.ordinal() == i) {
            DropboxCloud dropboxCloud7 = new DropboxCloud(this, CloudCommon.DropboxType.Audio.ordinal());
            this.iSecureBackupCloud = dropboxCloud7;
            this.backupCloudEntList = dropboxCloud7.GetFolders(CloudCommon.AudioFolder);
        }
        Iterator<BackupCloudEnt> it = this.backupCloudEntList.iterator();
        while (it.hasNext()) {
            BackupCloudEnt next = it.next();
            if (next.GetStatus() == CloudCommon.CloudFolderStatus.OnlyPhone.ordinal()) {
                next.SetImageStatus(R.drawable.up_status);
            } else if (next.GetStatus() == CloudCommon.CloudFolderStatus.OnlyCloud.ordinal()) {
                next.SetImageStatus(R.drawable.down_status);
            } else if (next.GetStatus() == CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal()) {
                next.SetImageStatus(R.drawable.synced_status);
                next.SetSyncVisibility(4);
            } else if (next.GetStatus() == CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal()) {
                next.SetImageStatus(R.drawable.up_down_status);
            }
            Iterator<BackupCloudEnt> it2 = CloudService.UpdateBackupCloudEntList.iterator();
            while (it2.hasNext()) {
                if (next.GetFolderName().equals(it2.next().GetFolderName())) {
                    next.SetIsInProgress(true);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void UpDataBind(int i, int i2) {
        DropboxAdapter dropboxAdapter = new DropboxAdapter(getApplicationContext(), 17367043, this.backupCloudEntList);
        this.dropboxAdapter = dropboxAdapter;
        this.dropboxdownloadListView.setAdapter((ListAdapter) dropboxAdapter);
        this.dropboxAdapter.notifyDataSetChanged();
        this.dropboxdownloadListView.setSelectionFromTop(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void UpDatedListView() {
        new Thread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropBoxDownloadActivity.6
            @Override // java.lang.Runnable
            public void run() {
                boolean z = true;
                int i = 0;
                while (z) {
                    try {
                        Iterator<BackupCloudEnt> it = DropBoxDownloadActivity.this.backupCloudEntList.iterator();
                        while (it.hasNext()) {
                            BackupCloudEnt next = it.next();
                            int i2 = -1;
                            Iterator<BackupCloudEnt> it2 = CloudService.UpdateBackupCloudEntList.iterator();
                            while (true) {
                                if (it2.hasNext()) {
                                    BackupCloudEnt next2 = it2.next();
                                    i2++;
                                    if (next.GetFolderName().equals(next2.GetFolderName()) && !next2.GetIsInProgress()) {
                                        next.SetDownloadCount(0);
                                        next.SetUploadCount(0);
                                        next.SetImageStatus(R.drawable.synced_status);
                                        next.SetSyncVisibility(4);
                                        next.SetStatus(CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal());
                                        i = i2;
                                        z = false;
                                        continue;
                                    }
                                }
                            }
                        }
                        Thread.sleep(2000L);
                    } catch (Exception unused) {
                        return;
                    }
                }
                if (CloudService.UpdateBackupCloudEntList.size() > 0 && !CloudService.IsRemovingIndex && !CloudService.RemoveUpdateBackupIndexs.contains(Integer.valueOf(i))) {
                    CloudService.RemoveUpdateBackupIndexs.add(Integer.valueOf(i));
                }
                DropBoxDownloadActivity.this.myHandler.sendMessage(DropBoxDownloadActivity.this.myHandler.obtainMessage());
            }
        }).start();
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

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
        }
        super.onPause();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            Intent intent = null;
            if (CloudCommon.IsCameFromSettings) {
                intent = new Intent(this, SettingActivity.class);
                CloudCommon.IsCameFromSettings = false;
            } else if (CloudCommon.IsCameFromCloudMenu) {
                intent = new Intent(this, FeaturesActivity.class);
                CloudCommon.IsCameFromCloudMenu = false;
            } else if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), PhotosAlbumActivty.class);
            } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), VideosAlbumActivty.class);
            } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), DocumentsFolderActivity.class);
            } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), NotesFoldersActivity.class);
            } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), WalletCategoriesActivity.class);
            } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), ToDoActivity.class);
            } else if (CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), AudioPlayListActivity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
