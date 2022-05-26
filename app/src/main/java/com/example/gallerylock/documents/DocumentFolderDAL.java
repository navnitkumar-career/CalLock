package com.example.gallerylock.documents;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.gallerylock.dbhelper.DatabaseHelper;
import com.example.gallerylock.photo.PhotosAlbumActivty;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class DocumentFolderDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public DocumentFolderDAL(Context context) {
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

    public void AddDocumentFolder(DocumentFolder documentFolder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("folder_name", documentFolder.getFolderName());
        contentValues.put("fl_folder_location", documentFolder.getFolderLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("SortBy", (Integer) 0);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tbl_document_folders", null, contentValues);
    }

    public List<DocumentFolder> GetFolders(int i) {
        ArrayList arrayList = new ArrayList();
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        documentDAL.OpenRead();
        String str = "SELECT * FROM tbl_document_folders Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY _id";
        if (PhotosAlbumActivty.SortBy.Time.ordinal() == i) {
            str = "SELECT * FROM tbl_document_folders Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY ModifiedDateTime DESC";
        } else if (PhotosAlbumActivty.SortBy.Name.ordinal() == i) {
            str = "SELECT * FROM tbl_document_folders Where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY folder_name COLLATE NOCASE ASC";
        }
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            DocumentFolder documentFolder = new DocumentFolder();
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
            documentFolder.set_fileCount(documentDAL.GetDocumentCountByFolderId(rawQuery.getInt(0)));
            arrayList.add(documentFolder);
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetSortByFolderId(int i) {
        Cursor rawQuery = this.database.rawQuery("SELECT SortBy FROM tbl_document_folders where _id = " + i, null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2 = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i2;
    }

    public void AddSortByInDocumentFolder(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("SortBy", Integer.valueOf(i));
        this.database.update("tbl_document_folders", contentValues, "_id = ?", new String[]{String.valueOf(Common.FolderId)});
        close();
    }

    public DocumentFolder GetFolder(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where folder_name = '" + str + "' AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return documentFolder;
    }

    public DocumentFolder GetFolderById(int i) {
        DocumentFolder documentFolder = new DocumentFolder();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where _id = " + i + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return documentFolder;
    }

    public DocumentFolder GetFolderById(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where _id = '" + str + "' AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(6));
        }
        rawQuery.close();
        return documentFolder;
    }

    public void UpdateFolderName(DocumentFolder documentFolder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("folder_name", documentFolder.getFolderName());
        contentValues.put("fl_folder_location", documentFolder.getFolderLocation());
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_document_folders", contentValues, "_id = ?", new String[]{String.valueOf(documentFolder.getId())});
        close();
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        documentDAL.OpenWrite();
        documentDAL.UpdateFolderDocumentLocation(documentFolder.getId(), documentFolder.getFolderLocation());
        documentDAL.close();
    }

    public String GetFolderName(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where _id =" + str + " AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        String str2 = "";
        while (rawQuery.moveToNext()) {
            str2 = rawQuery.getString(1);
        }
        rawQuery.close();
        return str2;
    }

    public boolean IsFolderNameExist(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where folder_name ='" + str + "'", null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }

    public int IfFolderNameExistReturnId(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where folder_name ='" + str + "'", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public void DeleteFolderById(int i) {
        this.database.delete("tbl_document_folders", "_id = ?", new String[]{String.valueOf(i)});
        close();
        DocumentDAL documentDAL = new DocumentDAL(this.con);
        documentDAL.OpenWrite();
        documentDAL.DeleteDocuments(i);
        documentDAL.close();
    }

    public int GetLastFolderId() {
        Cursor rawQuery = this.database.rawQuery("SELECT _id FROM tbl_document_folders WHERE _id = (SELECT MAX(_id)  FROM tbl_document_folders)", null);
        int i = 0;
        while (rawQuery.moveToNext()) {
            i = rawQuery.getInt(0);
        }
        rawQuery.close();
        return i;
    }

    public void UpdateFolderLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_folder_location", str);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_document_folders", contentValues, "_id = ?", new String[]{String.valueOf(i)});
        close();
    }
}
