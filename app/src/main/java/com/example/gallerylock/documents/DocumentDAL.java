package com.example.gallerylock.documents;

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
public class DocumentDAL {
    Context con;
    SQLiteDatabase database;
    DatabaseHelper helper;

    public DocumentDAL(Context context) {
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

    public void AddDocuments(DocumentsEnt documentsEnt, String str) {
        File file = new File(str);
        ContentValues contentValues = new ContentValues();
        contentValues.put("document_name", documentsEnt.getDocumentName());
        contentValues.put("fl_document_location", str);
        contentValues.put("original_document_location", documentsEnt.getOriginalDocumentLocation());
        contentValues.put("folder_id", Integer.valueOf(documentsEnt.getFolderId()));
        contentValues.put("IsFakeAccount", Integer.valueOf(SecurityLocksCommon.IsFakeAccount));
        contentValues.put("FileSize", Long.valueOf(file.length()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.insert("tbl_documents", null, contentValues);
    }

    public List<DocumentsEnt> GetAllDocuments() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_documents where IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount + " ORDER BY ModifiedDateTime DESC", null);
        while (rawQuery.moveToNext()) {
            DocumentsEnt documentsEnt = new DocumentsEnt();
            documentsEnt.setId(rawQuery.getInt(0));
            documentsEnt.setDocumentName(rawQuery.getString(1));
            documentsEnt.setFolderLockDocumentLocation(rawQuery.getString(2));
            documentsEnt.setOriginalDocumentLocation(rawQuery.getString(3));
            documentsEnt.setFolderId(rawQuery.getInt(4));
            documentsEnt.set_modifiedDateTime(rawQuery.getString(8));
            documentsEnt.SetFileCheck(false);
            arrayList.add(documentsEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public List<DocumentsEnt> GetDocuments(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        String str = "SELECT * FROM tbl_documents where folder_id = " + i + " ORDER BY _id";
        if (DocumentsActivity.SortBy.Time.ordinal() == i2) {
            str = "SELECT * FROM tbl_documents where folder_id = " + i + " ORDER BY ModifiedDateTime DESC";
        } else if (DocumentsActivity.SortBy.Name.ordinal() == i2) {
            str = "SELECT * FROM tbl_documents where folder_id = " + i + " ORDER BY document_name COLLATE NOCASE ASC";
        } else if (DocumentsActivity.SortBy.Size.ordinal() == i2) {
            str = "SELECT * FROM tbl_documents where folder_id = " + i + " ORDER BY FileSize ASC";
        }
        Cursor rawQuery = this.database.rawQuery(str, null);
        while (rawQuery.moveToNext()) {
            DocumentsEnt documentsEnt = new DocumentsEnt();
            documentsEnt.setId(rawQuery.getInt(0));
            documentsEnt.setDocumentName(rawQuery.getString(1));
            documentsEnt.setFolderLockDocumentLocation(rawQuery.getString(2));
            documentsEnt.setOriginalDocumentLocation(rawQuery.getString(3));
            documentsEnt.setFolderId(rawQuery.getInt(4));
            documentsEnt.set_modifiedDateTime(rawQuery.getString(8));
            documentsEnt.SetFileCheck(false);
            arrayList.add(documentsEnt);
        }
        rawQuery.close();
        return arrayList;
    }

    public DocumentsEnt GetDocumentById(String str) {
        DocumentsEnt documentsEnt = new DocumentsEnt();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_documents where _id = " + str, null);
        while (rawQuery.moveToNext()) {
            documentsEnt.setId(rawQuery.getInt(0));
            documentsEnt.setDocumentName(rawQuery.getString(1));
            documentsEnt.setFolderLockDocumentLocation(rawQuery.getString(2));
            documentsEnt.setOriginalDocumentLocation(rawQuery.getString(3));
            documentsEnt.setFolderId(rawQuery.getInt(4));
            documentsEnt.set_modifiedDateTime(rawQuery.getString(8));
        }
        rawQuery.close();
        return documentsEnt;
    }

    public void DeleteDocument(DocumentsEnt documentsEnt) {
        OpenWrite();
        this.database.delete("tbl_documents", "_id = ?", new String[]{String.valueOf(documentsEnt.getId())});
        close();
    }

    public void DeleteAllDocument() {
        OpenWrite();
        this.database.execSQL("delete from tbl_documents");
        close();
    }

    public void DeleteDocumentById(int i) {
        OpenWrite();
        this.database.delete("tbl_documents", "_id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public void DeleteDocuments(int i) {
        for (DocumentsEnt documentsEnt : GetDocuments(i, 0)) {
            this.database.delete("tbl_documents", "_id = ?", new String[]{String.valueOf(documentsEnt.getId())});
            File file = new File(documentsEnt.getFolderLockDocumentLocation());
            if (file.exists()) {
                file.delete();
            }
        }
        close();
    }

    public String[] GetFolderNames(int i) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where _id != " + i + " AND IsFakeAccount =" + SecurityLocksCommon.IsFakeAccount, null);
        String[] strArr = new String[rawQuery.getCount()];
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            strArr[i2] = rawQuery.getString(1);
            i2++;
        }
        rawQuery.close();
        return strArr;
    }

    public List<String> GetMoveFolderNames(int i) {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where _id != " + i + " AND IsFakeAccount =" + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            arrayList.add(rawQuery.getString(1));
        }
        rawQuery.close();
        return arrayList;
    }

    public int GetDocumentCountByFolderId(int i) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_documents where folder_id = " + i, null);
        int i2 = 0;
        while (rawQuery.moveToNext()) {
            i2++;
        }
        rawQuery.close();
        return i2;
    }

    public DocumentFolder GetFolders(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_document_folders where folder_name = '" + str + "' AND IsFakeAccount = " + SecurityLocksCommon.IsFakeAccount, null);
        while (rawQuery.moveToNext()) {
            documentFolder.setId(rawQuery.getInt(0));
            documentFolder.setFolderName(rawQuery.getString(1));
            documentFolder.setFolderLocation(rawQuery.getString(2));
            documentFolder.set_modifiedDateTime(rawQuery.getString(8));
        }
        rawQuery.close();
        return documentFolder;
    }

    public void UpdateDocumentLocation(DocumentsEnt documentsEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_document_location", documentsEnt.getFolderLockDocumentLocation());
        contentValues.put("folder_id", Integer.valueOf(documentsEnt.getFolderId()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_documents", contentValues, "document_name = ?", new String[]{String.valueOf(documentsEnt.getDocumentName())});
        close();
    }

    public void UpdateDocumentLocationById(DocumentsEnt documentsEnt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_document_location", documentsEnt.getFolderLockDocumentLocation());
        contentValues.put("folder_id", Integer.valueOf(documentsEnt.getFolderId()));
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_documents", contentValues, "_id = ?", new String[]{String.valueOf(documentsEnt.getId())});
        close();
    }

    public void UpdateFolderDocumentLocation(int i, String str) {
        String str2;
        for (DocumentsEnt documentsEnt : GetDocuments(i, 0)) {
            ContentValues contentValues = new ContentValues();
            if (documentsEnt.getDocumentName().contains("#")) {
                str2 = documentsEnt.getDocumentName();
            } else {
                str2 = Utilities.ChangeFileExtention(documentsEnt.getDocumentName());
            }
            contentValues.put("fl_document_location", str + "/" + str2);
            contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
            this.database.update("tbl_documents", contentValues, "_id = ?", new String[]{String.valueOf(documentsEnt.getId())});
        }
        close();
    }

    public void UpdateDocumentsLocation(int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fl_document_location", str);
        contentValues.put("ModifiedDateTime", Utilities.getCurrentDateTime());
        this.database.update("tbl_documents", contentValues, "_id = ?", new String[]{String.valueOf(i)});
        close();
    }

    public boolean CheckAllFilesIsInSDCard() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_documents where IsSDCard == 0", null);
        if (rawQuery.moveToNext()) {
            return false;
        }
        rawQuery.close();
        return true;
    }

    public boolean CheckAllFilesIsInPhone() {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_documents where IsSDCard == 1", null);
        if (rawQuery.moveToNext()) {
            return true;
        }
        rawQuery.close();
        return false;
    }

    public boolean IsFileAlreadyExist(String str) {
        Cursor rawQuery = this.database.rawQuery("SELECT * FROM tbl_documents where fl_document_location ='" + str + "'", null);
        boolean z = false;
        while (rawQuery.moveToNext()) {
            z = true;
        }
        rawQuery.close();
        return z;
    }
}
