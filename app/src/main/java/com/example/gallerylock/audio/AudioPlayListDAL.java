package com.example.gallerylock.audio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.example.gallerylock.dbhelper.DatabaseHelper;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class AudioPlayListDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public AudioPlayListDAL(Context context) {
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

    public void AddAudioPlayList(AudioPlayListEnt audioPlayListEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PlayListName", audioPlayListEnt.getPlayListName());
        contentValues.put("FlPlayListLocation", audioPlayListEnt.getPlayListLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("SortBy", (Integer) 0);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tblAudioPlayList", null, contentValues);
    }

    public int GetSortByPlaylistId(int i) {
        Cursor rawQuery = this.database.rawQuery("SELECT SortBy FROM tblAudioPlayList where Id = " + i, null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2 = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i2;
    }

    public void AddSortByInAudioPlaylist(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SortBy", Integer.valueOf(i));
        this.database.update("tblAudioPlayList", contentValues, "Id = ?", new String[]{String.valueOf(Common.FolderId)});
        close();
    }

    public List<AudioPlayListEnt> GetPlayLists() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY Id", null);
        while (rawQuery.moveToNext()) {
            AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
            audioPlayListEnt.setId(rawQuery.getInt(0));
            audioPlayListEnt.setPlayListName(rawQuery.getString(1));
            audioPlayListEnt.setPlayListLocation(rawQuery.getString(2));
            arrayList.add(audioPlayListEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<AudioPlayListEnt> GetAllPlayLists() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList", null);
        while (rawQuery.moveToNext()) {
            AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
            audioPlayListEnt.setId(rawQuery.getInt(0));
            audioPlayListEnt.setPlayListName(rawQuery.getString(1));
            audioPlayListEnt.setPlayListLocation(rawQuery.getString(2));
            arrayList.add(audioPlayListEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<AudioPlayListEnt> GetPlayLists(int i) {
        ArrayList arrayList = new ArrayList();
        AudioDAL audioDAL = new AudioDAL(this.con);
        audioDAL.OpenRead();
        String str = "SELECT * FROM tblAudioPlayList Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY Id";
        if (AudioPlayListActivity.SortBy.Time.ordinal() == i) {
            str = "SELECT * FROM tblAudioPlayList Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY ModifiedDateTime DESC";
        } else if (AudioPlayListActivity.SortBy.Name.ordinal() == i) {
            str = "SELECT * FROM tblAudioPlayList Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY PlayListName COLLATE NOCASE ASC";
        }
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
            audioPlayListEnt.setId(rawQuery.getInt(0));
            audioPlayListEnt.setPlayListName(rawQuery.getString(1));
            audioPlayListEnt.setPlayListLocation(rawQuery.getString(2));
            audioPlayListEnt.set_modifiedDateTime(rawQuery.getString(6));
            audioPlayListEnt.set_fileCount(audioDAL.GetAudiosCountByFolderId(rawQuery.getInt(0)));
            arrayList.add(audioPlayListEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public AudioPlayListEnt GetPlayList(String str) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where PlayListName = '" + str + "' AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            audioPlayListEnt.setId(rawQuery.getInt(0));
            audioPlayListEnt.setPlayListName(rawQuery.getString(1));
            audioPlayListEnt.setPlayListLocation(rawQuery.getString(2));
        }
        rawQuery.close();
        return audioPlayListEnt;
    }

    public AudioPlayListEnt GetPlayListById(int i) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where Id = " + i + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            audioPlayListEnt.setId(rawQuery.getInt(0));
            audioPlayListEnt.setPlayListName(rawQuery.getString(1));
            audioPlayListEnt.setPlayListLocation(rawQuery.getString(2));
        }
        rawQuery.close();
        return audioPlayListEnt;
    }

    public void UpdatePlayListLocationOnly(AudioPlayListEnt audioPlayListEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlPlayListLocation", audioPlayListEnt.getPlayListLocation());
        this.database.update("tblAudioPlayList", contentValues, "Id = ?", new String[]{String.valueOf(audioPlayListEnt.getId())});
        close();
        AudioDAL audioDAL = new AudioDAL(this.con);
        audioDAL.OpenWrite();
        audioDAL.UpdateAudioPlayListLocation(audioPlayListEnt.getId(), audioPlayListEnt.getPlayListLocation());
        audioDAL.close();
    }

    public void UpdatePlayListName(AudioPlayListEnt audioPlayListEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PlayListName", audioPlayListEnt.getPlayListName());
        contentValues.put("FlPlayListLocation", audioPlayListEnt.getPlayListLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        this.database.update("tblAudioPlayList", contentValues, "Id = ?", new String[]{String.valueOf(audioPlayListEnt.getId())});
        close();
        AudioDAL audioDAL = new AudioDAL(this.con);
        audioDAL.OpenWrite();
        audioDAL.UpdateAudioPlayListLocation(audioPlayListEnt.getId(), audioPlayListEnt.getPlayListLocation());
        audioDAL.close();
    }

    public String GetPlayListName(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where Id =" + str + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        String str2 = "";
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(1);
        }
        rawQuery.close();
        return str2;
    }

    public boolean IsPlayListNameExist(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where PlayListName ='" + str + "'", null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }

    public int IfPlayListExistReturnId(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where PlayListName ='" + str + "'", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public void DeletePlayListById(int i) {
        this.database.delete("tblAudioPlayList", "Id = ?", new String[]{String.valueOf(i)});
        close();
        AudioDAL audioDAL = new AudioDAL(this.con);
        audioDAL.OpenWrite();
        audioDAL.DeleteAudios(i);
        audioDAL.close();
    }

    public int GetLastPlayListId() {
        Cursor rawQuery = this.database.rawQuery("SELECT Id FROM tblAudioPlayList WHERE Id = (SELECT MAX(Id)  FROM tblAudioPlayList)", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public void UpdatePlayListLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlPlayListLocation", str);
        this.database.update("tblAudioPlayList", contentValues, "Id = ?", new String[]{String.valueOf(i)});
        close();
    }
}
