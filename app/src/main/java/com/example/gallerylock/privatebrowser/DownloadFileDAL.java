package com.example.gallerylock.privatebrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.widget.Toast;

import com.example.gallerylock.Flaes;
import com.example.gallerylock.R;
import com.example.gallerylock.audio.AudioDAL;
import com.example.gallerylock.audio.AudioEnt;
import com.example.gallerylock.dbhelper.DatabaseHelper;
import com.example.gallerylock.documents.DocumentDAL;
import com.example.gallerylock.documents.DocumentsEnt;
import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoDAL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class DownloadFileDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public DownloadFileDAL(Context context) {
        this.helper = new DatabaseHelper(context);
        this.con = context;
    }

    public void OpenRead() throws SQLException {
        this.database = this.helper.getReadableDatabase();
    }

    public void OpenWrite() throws SQLException {
        this.database = this.helper.getWritableDatabase();
    }

    public void close() {
        this.database.close();
    }

    public void AddDownloadFile(DownloadFileEnt downloadFileEnt) {
        if (downloadFileEnt.GetFileName().contains(".")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("FileDownloadPath", downloadFileEnt.GetFileDownloadPath());
            contentValues.put("FileName", downloadFileEnt.GetFileName());
            contentValues.put("ReferenceId", downloadFileEnt.GetReferenceId());
            contentValues.put("Status", Integer.valueOf(downloadFileEnt.GetStatus()));
            contentValues.put("DownloadFileUrl", downloadFileEnt.GetDownloadFileUrl());
            contentValues.put("DownloadType", Integer.valueOf(downloadFileEnt.GetDownloadType()));
            contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
            this.database.insert("tbl_DownloadFile", null, contentValues);
            return;
        }
        Toast.makeText(this.con, (int) R.string.toast_browser_filenotdownload, 1).show();
        new File(downloadFileEnt.GetFileDownloadPath()).delete();
    }

    public List<DownloadFileEnt> GetDownloadFiles() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_DownloadFile WHERE Status = " + Common.DownloadStatus.InProgress.ordinal() + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
            downloadFileEnt.SetId(rawQuery.getInt(0));
            downloadFileEnt.SetFileDownloadPath(rawQuery.getString(1));
            downloadFileEnt.SetFileName(rawQuery.getString(2));
            downloadFileEnt.SetReferenceId(rawQuery.getString(3));
            downloadFileEnt.SetStatus(rawQuery.getInt(4));
            downloadFileEnt.SetDownloadFileUrl(rawQuery.getString(5));
            downloadFileEnt.SetDownloadType(rawQuery.getInt(6));
            arrayList.add(downloadFileEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public DownloadFileEnt GetDownloadFile(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_DownloadFile Where Id = " + str + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        DownloadFileEnt downloadFileEnt = new DownloadFileEnt();
        while (rawQuery.moveToNext()) {
            downloadFileEnt.SetId(rawQuery.getInt(0));
            downloadFileEnt.SetFileDownloadPath(rawQuery.getString(1));
            downloadFileEnt.SetFileName(rawQuery.getString(2));
            downloadFileEnt.SetReferenceId(rawQuery.getString(3));
            downloadFileEnt.SetStatus(rawQuery.getInt(4));
            downloadFileEnt.SetDownloadFileUrl(rawQuery.getString(5));
            downloadFileEnt.SetDownloadType(rawQuery.getInt(6));
        }
        rawQuery.close();
        return downloadFileEnt;
    }

    public void DeleteDownloadFile(DownloadFileEnt downloadFileEnt) {
        OpenWrite();
        this.database.delete("tbl_DownloadFile", "Id = ?", new String[]{String.valueOf(downloadFileEnt.GetId())});
        close();
    }

    public void DeleteDownloadFile() {
        OpenWrite();
        this.database.delete("tbl_DownloadFile", "Status = ?", new String[]{String.valueOf(Common.DownloadStatus.Completed.ordinal())});
        close();
    }

    public List<String> GetDownloadFileName() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_DownloadFile Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(2));
        }
        rawQuery.close();
        return arrayList;
    }

    public void DeleteDownloadFileAll() {
        OpenWrite();
        this.database.delete("tbl_DownloadFile", "IsFakeAccount = ?", new String[]{String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }

    public void UpdateDownloadFile(DownloadFileEnt downloadFileEnt) throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Status", Integer.valueOf(Common.DownloadStatus.Completed.ordinal()));
        this.database.update("tbl_DownloadFile", contentValues, "Id = ?", new String[]{String.valueOf(downloadFileEnt.GetId())});
        close();
        String GetFileDownloadPath = downloadFileEnt.GetFileDownloadPath();
        if (!downloadFileEnt.GetFileDownloadPath().contains(".")) {
            return;
        }
        if (downloadFileEnt.GetDownloadType() == Common.DownloadType.Photo.ordinal()) {
            String MovePhotoFile = MovePhotoFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MovePhotoFile.length() > 0) {
                AddPhotoToDatabase(downloadFileEnt.GetFileName(), MovePhotoFile);
            }
        } else if (downloadFileEnt.GetDownloadType() == Common.DownloadType.Video.ordinal()) {
            String GetThumnil = GetThumnil(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            String MoveVideoFile = MoveVideoFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MoveVideoFile.length() > 0) {
                AddVideoToDatabase(downloadFileEnt.GetFileName(), MoveVideoFile, GetThumnil);
            }
        } else if (downloadFileEnt.GetDownloadType() == Common.DownloadType.Music.ordinal()) {
            String MoveMusicFile = MoveMusicFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MoveMusicFile.length() > 0) {
                AddAudioToDatabase(downloadFileEnt.GetFileName(), MoveMusicFile);
            }
        } else if (downloadFileEnt.GetDownloadType() == Common.DownloadType.Document.ordinal()) {
            String MoveDocumentFile = MoveDocumentFile(downloadFileEnt.GetFileDownloadPath(), downloadFileEnt.GetFileName());
            if (MoveDocumentFile.length() > 0) {
                AddDocumentToDatabase(downloadFileEnt.GetFileName(), MoveDocumentFile);
            }
        } else {
            Toast.makeText(this.con, (int) R.string.toast_browser_filenotdownload, 1).show();
            new File(GetFileDownloadPath).delete();
        }
    }

    public String MovePhotoFile(String str, String str2) throws IOException {
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + "My Photos");
        String NSHideFile = Utilities.NSHideFile(this.con, new File(str), file);
        Utilities.NSEncryption(new File(NSHideFile));
        return NSHideFile;
    }

    private String MoveVideoFile(String str, String str2) throws IOException {
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + "My Videos");
        return Utilities.NSHideFile(this.con, new File(str), file);
    }

    public String MoveMusicFile(String str, String str2) throws IOException {
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, Utilities.ChangeFileExtention(str2));
        File file3 = new File(str);
        Flaes.encryptUsingCipherStream_AES128(file3, file2);
        if (file3.exists()) {
            file3.delete();
        }
        return file2.getAbsolutePath();
    }

    public String MoveDocumentFile(String str, String str2) throws IOException {
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + "My Documents");
        String NSHideFile = Utilities.NSHideFile(this.con, new File(str), file);
        Utilities.NSEncryption(new File(NSHideFile));
        return NSHideFile;
    }

    public void AddPhotoToDatabase(String str, String str2) {
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str2);
        photo.setOriginalPhotoLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + str);
        photo.setAlbumId(Common.FolderId);
        PhotoDAL photoDAL = new PhotoDAL(this.con);
        try {
            try {
                photoDAL.OpenWrite();
                photoDAL.AddPhotos(photo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            photoDAL.close();
        }
    }

    private String GetThumnil(String str, String str2) {
        Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(str, 1);
        new File(this.con.getFilesDir().getAbsoluteFile() + "/videos_gallery/VideoThumnails/").mkdirs();
        String str3 = this.con.getFilesDir().getAbsoluteFile() + "/videos_gallery/VideoThumnails/thumbnil-" + str2.substring(0, str2.lastIndexOf(".")) + "#jpg";
        File file = new File(str3);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        try {
            createVideoThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return str3;
    }

    private void AddVideoToDatabase(String str, String str2, String str3) {
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str2);
        String str4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + str;
        video.setthumbnail_video_location(str3);
        try {
            Utilities.NSEncryption(new File(str2));
            Utilities.NSEncryption(new File(str3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        video.setOriginalVideoLocation(str4);
        video.setAlbumId(Common.FolderId);
        VideoDAL videoDAL = new VideoDAL(this.con);
        try {
            try {
                videoDAL.OpenWrite();
                videoDAL.AddVideos(video);
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
            }
        } finally {
            videoDAL.close();
        }
    }

    public void AddAudioToDatabase(String str, String str2) {
        AudioEnt audioEnt = new AudioEnt();
        audioEnt.setAudioName(str);
        audioEnt.setOriginalAudioLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + str);
        audioEnt.setFolderLockAudioLocation(str2);
        audioEnt.setPlayListId(Common.FolderId);
        AudioDAL audioDAL = new AudioDAL(this.con);
        try {
            try {
                audioDAL.OpenWrite();
                audioDAL.AddAudio(audioEnt, str2);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioDAL.close();
        }
    }

    public void AddDocumentToDatabase(String str, String str2) {
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setOriginalDocumentLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + str);
        documentsEnt.setFolderId(Common.FolderId);
        documentsEnt.setFolderLockDocumentLocation(str2);
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        try {
            try {
                documentDAL.OpenWrite();
                documentDAL.AddDocuments(documentsEnt, str2);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            documentDAL.close();
        }
    }
}
