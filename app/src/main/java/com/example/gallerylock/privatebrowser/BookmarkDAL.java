package com.example.gallerylock.privatebrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.gallerylock.dbhelper.DatabaseHelper;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* loaded from: classes2.dex */
public class BookmarkDAL {
    SQLiteDatabase database;
    DatabaseHelper helper;

    public BookmarkDAL(Context context) {
        this.helper = new DatabaseHelper(context);
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

    public Boolean AddBookmark(String str) {
        OpenWrite();
        if (!IsUrlAlreadyExist(str).booleanValue()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Url", str);
            contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
            this.database.insert("tbl_Bookmark", null, contentValues);
            return true;
        }
        UpdateBookmark(str);
        return false;
    }

    public Boolean IsUrlAlreadyExist(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Bookmark Where Url ='" + str + "' AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }

    public List<BookmarkEnt> GetBookmarks() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Bookmark Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY CreateDate", null);
        while (rawQuery.moveToNext()) {
            BookmarkEnt bookmarkEnt = new BookmarkEnt();
            bookmarkEnt.SetId(rawQuery.getInt(0));
            bookmarkEnt.SetURL(rawQuery.getString(1));
            bookmarkEnt.SetCreateDate(rawQuery.getString(2));
            arrayList.add(bookmarkEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public void UpdateBookmark(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Url", str);
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("CreateDate", new Date(Calendar.getInstance().getTime().getTime()).toString());
        this.database.update("tbl_Bookmark", contentValues, "Url = ? AND IsFakeAccount", new String[]{String.valueOf(str), String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }

    public List<String> GetUrlBookmarks() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_Bookmark Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY CreateDate desc", null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(1));
        }
        rawQuery.close();
        return arrayList;
    }

    public void DeleteBookmarks() {
        OpenWrite();
        this.database.delete("tbl_Bookmark", "IsFakeAccount = ?", new String[]{String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }
}
