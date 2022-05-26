package com.example.gallerylock.securebackupcloud;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.example.gallerylock.dropbox.CloudService;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

/* loaded from: classes2.dex */
public class DropBoxUploadFile extends AsyncTask<Void, Long, Boolean> {
    private BackupCloudEnt backupCloudEnt;
    private int downloadType;
    private String dropboxUploadPath;
    private DbxClientV2 mApi;
    private Context mContext;
    private String mErrorMsg;
    private File uploadfile;

    /* JADX INFO: Access modifiers changed from: protected */
    public void onProgressUpdate(Long... lArr) {
    }

    public DropBoxUploadFile(Context context, DbxClientV2 dbxClientV2, String str, String str2, int i, BackupCloudEnt backupCloudEnt) {
        this.mContext = context.getApplicationContext();
        this.mApi = dbxClientV2;
        this.dropboxUploadPath = str;
        this.uploadfile = new File(str2);
        this.downloadType = i;
        this.backupCloudEnt = backupCloudEnt;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Boolean doInBackground(Void... voidArr) {
        FileInputStream fileInputStream;
        String str;
        try {
            fileInputStream = new FileInputStream(this.uploadfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileInputStream = null;
        }
        try {
            if (!(this.downloadType == CloudCommon.DropboxType.Notes.ordinal() || this.downloadType == CloudCommon.DropboxType.ToDo.ordinal() || this.downloadType == CloudCommon.DropboxType.Wallet.ordinal())) {
                Utilities.NSDecryption(this.uploadfile);
            }
            if (this.uploadfile.getName().contains("#")) {
                str = Utilities.ChangeFileExtentionToOrignal(this.uploadfile.getName());
            } else {
                str = this.uploadfile.getName();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            str = "";
        }
        try {
            this.mApi.files().uploadBuilder(this.dropboxUploadPath + "/" + str).uploadAndFinish(fileInputStream);
            return true;
        } catch (DbxException e3) {
            e3.printStackTrace();
            return false;
        } catch (IOException e4) {
            e4.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostExecute(Boolean bool) {
        try {
            if (bool.booleanValue()) {
                int indexOf = CloudService.CloudBackupCloudEntList.indexOf(this.backupCloudEnt);
                Hashtable<String, Boolean> GetUploadPath = this.backupCloudEnt.GetUploadPath();
                if (GetUploadPath.containsKey(this.uploadfile.getAbsolutePath())) {
                    GetUploadPath.put(this.uploadfile.getAbsolutePath().toString(), true);
                    this.backupCloudEnt.SetUploadPath(GetUploadPath);
                    CloudService.CloudBackupCloudEntList.set(indexOf, this.backupCloudEnt);
                }
                if (this.downloadType != CloudCommon.DropboxType.Notes.ordinal() && this.downloadType != CloudCommon.DropboxType.Wallet.ordinal() && this.downloadType != CloudCommon.DropboxType.ToDo.ordinal()) {
                    try {
                        Utilities.NSEncryption(this.uploadfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                showToast(this.mErrorMsg);
                if (this.downloadType != CloudCommon.DropboxType.Notes.ordinal() && this.downloadType != CloudCommon.DropboxType.Wallet.ordinal() && this.downloadType != CloudCommon.DropboxType.ToDo.ordinal()) {
                    try {
                        Utilities.NSEncryption(this.uploadfile);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } catch (Exception unused) {
        }
    }

    private void showToast(String str) {
        Toast.makeText(this.mContext, str, 1).show();
    }
}
