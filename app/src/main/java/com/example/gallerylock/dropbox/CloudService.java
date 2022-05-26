package com.example.gallerylock.dropbox;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.gallerylock.R;
import com.example.gallerylock.securebackupcloud.BackupCloudEnt;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securebackupcloud.DropboxCloud;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class CloudService extends IntentService {
    Context context;
    public volatile boolean run = true;
    public static ArrayList<BackupCloudEnt> AddBackupCloudEntList = new ArrayList<>();
    public static ArrayList<Integer> RemoveUpdateBackupIndexs = new ArrayList<>();
    public static ArrayList<BackupCloudEnt> CloudBackupCloudEntList = new ArrayList<>();
    public static ArrayList<BackupCloudEnt> UpdateBackupCloudEntList = new ArrayList<>();
    public static boolean IsRemovingIndex = false;

    public CloudService() {
        super("CloudService");
    }

    public CloudService(String str) {
        super(str);
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        Log.i(NotificationCompat.CATEGORY_SERVICE, "service running");
        while (this.run) {
            synchronized (this) {
                try {
                    CloudCommon.IsCloudServiceStarted = true;
                    if (AddBackupCloudEntList.size() > 0) {
                        BackupCloudEnt remove = AddBackupCloudEntList.remove(0);
                        remove.SetIsInProgress(true);
                        CloudBackupCloudEntList.add(remove);
                        UpdateBackupCloudEntList.add(remove);
                        DropboxCloud dropboxCloud = new DropboxCloud(this, remove.GetDropboxType());
                        if (remove.GetDownloadCount() > 0) {
                            dropboxCloud.DownloadFile(remove);
                        }
                        if (remove.GetUploadCount() > 0) {
                            dropboxCloud.UploadFile(remove);
                        }
                        if (remove.GetDownloadCount() == 0 && remove.GetUploadCount() == 0) {
                            if (remove.GetStatus() == CloudCommon.CloudFolderStatus.OnlyCloud.ordinal()) {
                                dropboxCloud.CreateLocalFolder(remove);
                            }
                            if (remove.GetStatus() == CloudCommon.CloudFolderStatus.OnlyPhone.ordinal()) {
                                dropboxCloud.CreateFolder(remove);
                            }
                            for (int i = 0; i < UpdateBackupCloudEntList.size(); i++) {
                                if (UpdateBackupCloudEntList.get(i).GetFolderName().equals(remove.GetFolderName())) {
                                    UpdateBackupCloudEntList.get(i).SetIsInProgress(false);
                                    UpdateBackupCloudEntList.get(i).SetUploadCount(0);
                                    UpdateBackupCloudEntList.get(i).SetDownloadCount(0);
                                    UpdateBackupCloudEntList.get(i).SetStatus(CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal());
                                }
                            }
                            CloudBackupCloudEntList.remove(remove);
                        }
                    }
                    if (CloudBackupCloudEntList.size() > 0) {
                        Iterator<BackupCloudEnt> it = CloudBackupCloudEntList.iterator();
                        while (it.hasNext()) {
                            BackupCloudEnt next = it.next();
                            boolean contains = next.GetDownloadCount() > 0 ? next.GetDownloadPath().values().contains(false) : false;
                            boolean contains2 = next.GetUploadCount() > 0 ? next.GetUploadPath().values().contains(false) : false;
                            if (!contains && !contains2) {
                                for (int i2 = 0; i2 < UpdateBackupCloudEntList.size(); i2++) {
                                    if (UpdateBackupCloudEntList.get(i2).GetFolderName().equals(next.GetFolderName())) {
                                        UpdateBackupCloudEntList.get(i2).SetIsInProgress(false);
                                        UpdateBackupCloudEntList.get(i2).SetUploadCount(0);
                                        UpdateBackupCloudEntList.get(i2).SetDownloadCount(0);
                                        UpdateBackupCloudEntList.get(i2).SetImageStatus(R.drawable.synced_status);
                                        UpdateBackupCloudEntList.get(i2).SetSyncVisibility(4);
                                        UpdateBackupCloudEntList.get(i2).SetStatus(CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal());
                                    }
                                }
                                CloudBackupCloudEntList.remove(next);
                            }
                        }
                    }
                    if (RemoveUpdateBackupIndexs.size() > 0) {
                        IsRemovingIndex = true;
                        Iterator<Integer> it2 = RemoveUpdateBackupIndexs.iterator();
                        while (it2.hasNext()) {
                            UpdateBackupCloudEntList.remove(it2.next().intValue());
                        }
                        RemoveUpdateBackupIndexs.clear();
                        IsRemovingIndex = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (AddBackupCloudEntList.size() <= 0 && CloudBackupCloudEntList.size() <= 0 && RemoveUpdateBackupIndexs.size() <= 0) {
                this.run = false;
                CloudCommon.IsCloudServiceStarted = false;
            }
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
}
