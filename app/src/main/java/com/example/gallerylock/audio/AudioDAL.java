package com.example.gallerylock.audio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.gallerylock.dbhelper.DatabaseHelper;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class AudioDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public AudioDAL(Context context) {
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

    public void AddAudio(AudioEnt audioEnt, String str) {
        File file = new File(audioEnt.getFolderLockAudioLocation());
        ContentValues contentValues = new ContentValues();
        contentValues.put("AudioName", audioEnt.getAudioName());
        contentValues.put("FlAudioLocation", str);
        contentValues.put("OriginalAudioLocation", audioEnt.getFolderLockAudioLocation());
        contentValues.put("PlayListId", Integer.valueOf(audioEnt.getPlayListId()));
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("FileSize", Long.valueOf(file.length()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tbl_Audio", null, contentValues);
    }

    public List<AudioEnt> GetAllAudios() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio ORDER BY Id", null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetTotalCount() {
        OpenRead();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i++;
        }
        rawQuery.close();
        close();
        return i;
    }

    public List<AudioEnt> GetAudiosByAlbumId(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        String str = "SELECT * FROM tbl_Audio where PlayListId = " + i;
        if (AudioActivity.SortBy.Time.ordinal() == i2) {
            str = "SELECT * FROM tbl_Audio where PlayListId = " + i + " ORDER BY ModifiedDateTime DESC";
        } else if (AudioActivity.SortBy.Name.ordinal() == i2) {
            str = "SELECT * FROM tbl_Audio where PlayListId = " + i + " ORDER BY AudioName COLLATE NOCASE ASC";
        } else if (AudioActivity.SortBy.Size.ordinal() == i2) {
            str = "SELECT * FROM tbl_Audio where PlayListId = " + i + " ORDER BY FileSize ASC";
        }
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            audioEnt.set_modifiedDateTime(rawQuery.getString(8));
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<AudioEnt> GetAudios(int i) {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio Where PlayListId =" + i + " AND IsFakeAccount =" + SecurityLocksCommon.IsFakeAccount + " ORDER BY Id", null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public String[] GetPlayListNames(int i) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where Id != " + i + " AND IsFakeAccount =" + SecurityLocksCommon.IsFakeAccount, null);
        String[] strArr = new String[rawQuery.getCount()];
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            strArr[i2] = rawQuery.getString(1);
            i2++;
        }
        rawQuery.close();
        return strArr;
    }

    public List<String> GetMovePlayListNames(int i) {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tblAudioPlayList where Id != " + i + " AND IsFakeAccount =" + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(1));
        }
        rawQuery.close();
        return arrayList;
    }

    public AudioEnt GetAudio(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio Where Id = " + str + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        AudioEnt audioEnt = new AudioEnt();
        while (rawQuery.moveToNext()) {
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
        }
        rawQuery.close();
        return audioEnt;
    }

    public void DeleteAllAudios() {
        OpenWrite();
        this.database.execSQL("delete from tbl_Audio");
        close();
    }

    public void DeleteAudio(AudioEnt audioEnt) {
        OpenWrite();
        this.database.delete("tbl_Audio", "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public void DeleteAudioById(int i) {
        OpenWrite();
        this.database.delete("tbl_Audio", "Id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void DeleteAudios(int i) {
        for (AudioEnt audioEnt : GetAudiosByPlayListId(i)) {
            this.database.delete("tbl_Audio", "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
            File file = new File(audioEnt.getFolderLockAudioLocation());
            if (file.exists()) {
                file.delete();
            }
        }
        close();
    }

    public List<AudioEnt> GetAudiosByPlayListId(int i) {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where PlayListId = " + i + " ORDER BY Id", null);
        while (rawQuery.moveToNext()) {
            AudioEnt audioEnt = new AudioEnt();
            audioEnt.setId(rawQuery.getInt(0));
            audioEnt.setAudioName(rawQuery.getString(1));
            audioEnt.setFolderLockAudioLocation(rawQuery.getString(2));
            audioEnt.setOriginalAudioLocation(rawQuery.getString(3));
            audioEnt.setPlayListId(rawQuery.getInt(6));
            audioEnt.SetFileCheck(false);
            arrayList.add(audioEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetAudiosCountByFolderId(int i) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where PlayListId = " + i, null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2++;
        }
        rawQuery.close();
        return i2;
    }

    public void UpdateAudioPlayListLocation(int i, String str) {
        String str2;
        for (AudioEnt audioEnt : GetAudiosByPlayListId(i)) {
            ContentValues contentValues = new ContentValues();
            if (audioEnt.getAudioName().contains("#")) {
                str2 = audioEnt.getAudioName();
            } else {
                str2 = Utilities.ChangeFileExtention(audioEnt.getAudioName());
            }
            contentValues.put("FlAudioLocation", str + "/" + str2);
            this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        }
        close();
    }

    public void UpdateAudioLocationById(AudioEnt audioEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", audioEnt.getFolderLockAudioLocation());
        contentValues.put("PlayListId", Integer.valueOf(audioEnt.getPlayListId()));
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public void UpdateAudiosLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", str);
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void UpdateAudiosLocationById(AudioEnt audioEnt) {
        String str;
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", audioEnt.getFolderLockAudioLocation());
        if (audioEnt.getAudioName().contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(audioEnt.getAudioName());
        } else {
            str = audioEnt.getAudioName();
        }
        contentValues.put("AudioName", str);
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public void UpdateAudiosLocation(AudioEnt audioEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("FlAudioLocation", audioEnt.getFolderLockAudioLocation());
        contentValues.put("PlayListId", Integer.valueOf(audioEnt.getPlayListId()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_Audio", contentValues, "Id = ?", new String[]{String.valueOf(audioEnt.getId())});
        close();
    }

    public boolean CheckAllFilesIsInSDCard() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where IsSDCard == 0", null);
        if (rawQuery.moveToNext()) {
            return false;
        }
        rawQuery.close();
        return true;
    }

    public boolean CheckAllFilesIsInPhone() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where IsSDCard == 1", null);
        if (rawQuery.moveToNext()) {
            return true;
        }
        rawQuery.close();
        return false;
    }

    public boolean IsFileAlreadyExist(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Audio where FlAudioLocation ='" + str + "'", null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }
}
