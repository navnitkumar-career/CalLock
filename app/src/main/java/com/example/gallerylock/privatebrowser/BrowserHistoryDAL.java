package com.example.gallerylock.privatebrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.gallerylock.common.Constants;
import com.example.gallerylock.dbhelper.DatabaseHelper;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* loaded from: classes2.dex */
public class BrowserHistoryDAL {
    SQLiteDatabase database;
    DatabaseHelper helper;

    public BrowserHistoryDAL(Context context) {
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

    public Boolean AddBrowserHistory(String str) {
        OpenWrite();
        if (!IsUrlAlreadyExist(str).booleanValue()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Url", str);
            contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
            this.database.insert("tbl_BrowserHistory", null, contentValues);
            return true;
        }
        UpdateBrowserHistory(str);
        return false;
    }

    public Boolean IsUrlAlreadyExist(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_BrowserHistory Where Url ='" + str + "' AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }

    public List<BrowserHistoryEnt> GetBrowserHistories() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_BrowserHistory Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY CreateDate", null);
        while (rawQuery.moveToNext()) {
            BrowserHistoryEnt browserHistoryEnt = new BrowserHistoryEnt();
            browserHistoryEnt.SetId(rawQuery.getInt(0));
            browserHistoryEnt.SetURL(rawQuery.getString(1));
            browserHistoryEnt.SetCreateDate(rawQuery.getString(2));
            arrayList.add(browserHistoryEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public void UpdateBrowserHistory(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Url", str);
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("CreateDate", new Timestamp(Calendar.getInstance().getTime().getTime()).toGMTString());
        this.database.update("tbl_BrowserHistory", contentValues, "Url = ? AND IsFakeAccount = ?", new String[]{String.valueOf(str), String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }

    public List<String> GetBrowserUrlHistories() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_BrowserHistory Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY CreateDate desc", null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(1));
        }
        rawQuery.close();
        return arrayList;
    }

    public List<String> GetBrowserAutoCompletedHistories() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_BrowserHistory Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY CreateDate desc", null);
        while (rawQuery.moveToNext()) {
            String replace = rawQuery.getString(1).replace(Constants.HTTP, "").replace(Constants.HTTPS, "");
            if (replace.length() > 30) {
                arrayList.add(replace.substring(0, 29));
            } else {
                arrayList.add(replace);
            }
        }
        rawQuery.close();
        return arrayList;
    }

    public void DeleteHistories() {
        OpenWrite();
        this.database.delete("tbl_BrowserHistory", "IsFakeAccount = ?", new String[]{String.valueOf(SecurityLocksCommon.IsFakeAccount)});
        close();
    }
}
