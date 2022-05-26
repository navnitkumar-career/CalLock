package com.example.gallerylock.datarecovery;

import android.content.Context;
import android.util.Log;

import com.example.gallerylock.common.Constants;
import com.example.gallerylock.documents.DocumentDAL;
import com.example.gallerylock.documents.DocumentFolder;
import com.example.gallerylock.documents.DocumentFolderDAL;
import com.example.gallerylock.documents.DocumentsEnt;
import com.example.gallerylock.notes.NotesFileDB_Pojo;
import com.example.gallerylock.notes.NotesFilesDAL;
import com.example.gallerylock.notes.NotesFolderDAL;
import com.example.gallerylock.notes.NotesFolderDB_Pojo;
import com.example.gallerylock.notes.ReadNoteFromXML;
import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoAlbum;
import com.example.gallerylock.photo.PhotoAlbumDAL;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoAlbum;
import com.example.gallerylock.video.VideoAlbumDAL;
import com.example.gallerylock.video.VideoDAL;
import com.example.gallerylock.wallet.WalletCategoriesDAL;
import com.example.gallerylock.wallet.WalletCategoriesFileDB_Pojo;
import com.example.gallerylock.wallet.WalletCategoriesPojo;
import com.example.gallerylock.wallet.WalletCommon;
import com.example.gallerylock.wallet.WalletEntriesDAL;
import com.example.gallerylock.wallet.WalletEntryFileDB_Pojo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class DataRecover {
    Context context;

    public void RecoverALLData(Context context) {
        this.context = context;
        if (!StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage) {
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH, true);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH, true);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH, true);
        } else if (!StorageOptionsCommon.STORAGEPATH.equals(StorageOptionsCommon.STORAGEPATH_1)) {
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH, true);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH, true);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH_1, false);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH, true);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH_1, false);
        } else if (!StorageOptionsCommon.STORAGEPATH.equals(StorageOptionsCommon.STORAGEPATH_2)) {
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverPhotos(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH, true);
            RecoverVideos(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH, true);
            RecoverDocuments(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH, true);
            RecoverNotes(StorageOptionsCommon.STORAGEPATH_2, false);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH, true);
            RecoverWalletEntries(StorageOptionsCommon.STORAGEPATH_2, false);
        }
    }

    private void RecoverPhotos(String str, boolean z) {
        File[] listFiles;
        boolean z2;
        String str2;
        boolean z3;
        File file = new File(str + StorageOptionsCommon.PHOTOS + "/");
        if (file.exists()) {
            File[] listFiles2 = file.listFiles();
            boolean z4 = false;
            for (File file2 : listFiles2) {
                if (file2.isDirectory()) {
                    for (File file3 : file2.listFiles()) {
                        if (file3.isFile()) {
                            String absolutePath = file3.getAbsolutePath();
                            if (!z) {
                                try {
                                    absolutePath = Utilities.RecoveryHideFileSDCard(this.context, file3, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + "/" + file2.getName()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    z4 = true;
                                }
                            }
                            if (!z4) {
                                if (file3.getName().contains("#")) {
                                    str2 = Utilities.ChangeFileExtentionToOrignal(file3.getName());
                                } else {
                                    str2 = file3.getName();
                                }
                                String str3 = StorageOptionsCommon.STORAGEPATH + "NS Vault Unhide Data/" + str2;
                                PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
                                photoAlbumDAL.OpenRead();
                                int IfAlbumNameExistReturnId = photoAlbumDAL.IfAlbumNameExistReturnId(file2.getName());
                                if (IfAlbumNameExistReturnId == 0) {
                                    AddPhotoAlbumToDatabase(file2.getName());
                                    IfAlbumNameExistReturnId = photoAlbumDAL.GetLastAlbumId();
                                }
                                photoAlbumDAL.close();
                                PhotoDAL photoDAL = new PhotoDAL(this.context);
                                photoDAL.OpenRead();
                                if (absolutePath.contains("'")) {
                                    z2 = z4;
                                    z3 = photoDAL.IsFileAlreadyExist(absolutePath.replaceAll("'", "''"));
                                } else {
                                    z2 = z4;
                                    z3 = photoDAL.IsFileAlreadyExist(absolutePath);
                                }
                                photoDAL.close();
                                if (!z3) {
                                    StorageOptionsCommon.IsUserHasDataToRecover = true;
                                    AddPhotoToDatabase(file3.getName(), absolutePath, str3, IfAlbumNameExistReturnId);
                                }
                            } else {
                                z2 = z4;
                            }
                            z4 = z2;
                        }
                    }
                }
            }
        }
    }

    private void RecoverVideos(String str, boolean z) {
        int i;
        int i2;
        String str2;
        boolean z2;
        String str3;
        int i3;
        boolean z3;
        String str4;
        String str5;
        DataRecover dataRecover = this;
        String str6 = str;
        File file = new File(str6 + StorageOptionsCommon.VIDEOS);
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            int length = listFiles.length;
            boolean z4 = false;
            int i4 = 0;
            while (i4 < length) {
                File file2 = listFiles[i4];
                if (file2.isDirectory() && !file2.getName().equals("VideoThumnails")) {
                    File[] listFiles2 = file2.listFiles();
                    int length2 = listFiles2.length;
                    int i5 = 0;
                    while (i5 < length2) {
                        File file3 = listFiles2[i5];
                        if (file3.isFile()) {
                            String absolutePath = file3.getAbsolutePath();
                            if (!z) {
                                StringBuilder sb = new StringBuilder();
                                boolean z5 = z4;
                                sb.append(StorageOptionsCommon.STORAGEPATH);
                                sb.append(StorageOptionsCommon.VIDEOS);
                                sb.append(file2.getName());
                                sb.append("/");
                                String sb2 = sb.toString();
                                try {
                                    str5 = Utilities.RecoveryHideFileSDCard(dataRecover.context, file3, new File(sb2));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    str5 = absolutePath;
                                    z5 = true;
                                }
                                str4 = str5;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(sb2);
                                i2 = i5;
                                sb3.append("VideoThumnails/");
                                File file4 = new File(sb3.toString());
                                if (!file4.exists()) {
                                    file4.mkdirs();
                                }
                                String name = file3.getName();
                                try {
                                    Utilities.UnHideThumbnail(dataRecover.context, str6 + StorageOptionsCommon.VIDEOS + file2.getName() + "/VideoThumnails/thumbnil-" + name.substring(0, name.lastIndexOf("#")) + "#jpg", sb2 + "VideoThumnails/thumbnil-" + file3.getName().substring(0, file3.getName().lastIndexOf("#")) + "#jpg");
                                    z4 = z5;
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                    str2 = str4;
                                    z4 = true;
                                }
                            } else {
                                str4 = absolutePath;
                                i2 = i5;
                            }
                            str2 = str4;
                            if (!z4) {
                                if (file3.getName().contains("#")) {
                                    str3 = Utilities.ChangeFileExtentionToOrignal(file3.getName());
                                } else {
                                    str3 = file3.getName();
                                }
                                String str7 = StorageOptionsCommon.STORAGEPATH + "NS Vault Unhide Data/" + str3;
                                VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(dataRecover.context);
                                videoAlbumDAL.OpenRead();
                                int IfAlbumNameExistReturnId = videoAlbumDAL.IfAlbumNameExistReturnId(file2.getName());
                                if (IfAlbumNameExistReturnId == 0) {
                                    dataRecover.AddVideoAlbumToDatabase(file2.getName());
                                    z2 = z4;
                                    if (!new File(file2.getAbsolutePath()).exists()) {
                                        file2.mkdirs();
                                    }
                                    i3 = videoAlbumDAL.GetLastAlbumId();
                                } else {
                                    z2 = z4;
                                    i3 = IfAlbumNameExistReturnId;
                                }
                                videoAlbumDAL.close();
                                VideoDAL videoDAL = new VideoDAL(dataRecover.context);
                                videoDAL.OpenRead();
                                if (str2.contains("'")) {
                                    z3 = videoDAL.IsFileAlreadyExist(str2.replaceAll("'", "''"));
                                } else {
                                    z3 = videoDAL.IsFileAlreadyExist(str2);
                                }
                                videoDAL.close();
                                if (!z3) {
                                    String str8 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + file2.getName() + "/VideoThumnails/thumbnil-" + file3.getName().substring(0, file3.getName().lastIndexOf("#")) + "#jpg";
                                    StorageOptionsCommon.IsUserHasDataToRecover = true;
                                    if (new File(str8).exists()) {
                                        i = i2;
                                        AddVideoToDatabase(file3.getName(), str2, str7, str8, i3);
                                    } else {
                                        i = i2;
                                    }
                                    z4 = z2;
                                }
                            } else {
                                z2 = z4;
                            }
                            i = i2;
                            z4 = z2;
                        } else {
                            i = i5;
                        }
                        i5 = i + 1;
                        dataRecover = this;
                        str6 = str;
                    }
                }
                i4++;
                dataRecover = this;
                str6 = str;
            }
        }
    }

    private void RecoverDocuments(String str, boolean z) {
        File[] listFiles;
        boolean z2;
        String str2;
        boolean z3;
        File file = new File(str + StorageOptionsCommon.DOCUMENTS + "/");
        if (file.exists()) {
            File[] listFiles2 = file.listFiles();
            boolean z4 = false;
            for (File file2 : listFiles2) {
                if (file2.isDirectory()) {
                    for (File file3 : file2.listFiles()) {
                        if (file3.isFile()) {
                            String absolutePath = file3.getAbsolutePath();
                            if (!z) {
                                try {
                                    absolutePath = Utilities.RecoveryHideFileSDCard(this.context, file3, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + "/" + file2.getName()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    z4 = true;
                                }
                            }
                            if (!z4) {
                                if (file3.getName().contains("#")) {
                                    str2 = Utilities.ChangeFileExtentionToOrignal(file3.getName());
                                } else {
                                    str2 = file3.getName();
                                }
                                String str3 = StorageOptionsCommon.STORAGEPATH + Common.UnhideKitkatAlbumName + str2;
                                DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
                                documentFolderDAL.OpenRead();
                                int IfFolderNameExistReturnId = documentFolderDAL.IfFolderNameExistReturnId(file2.getName());
                                if (IfFolderNameExistReturnId == 0) {
                                    AddDocumentFolderToDatabase(file2.getName());
                                    IfFolderNameExistReturnId = documentFolderDAL.GetLastFolderId();
                                }
                                documentFolderDAL.close();
                                DocumentDAL documentDAL = new DocumentDAL(this.context);
                                documentDAL.OpenRead();
                                if (absolutePath.contains("'")) {
                                    z2 = z4;
                                    z3 = documentDAL.IsFileAlreadyExist(absolutePath.replaceAll("'", "''"));
                                } else {
                                    z2 = z4;
                                    z3 = documentDAL.IsFileAlreadyExist(absolutePath);
                                }
                                documentDAL.close();
                                if (!z3) {
                                    StorageOptionsCommon.IsUserHasDataToRecover = true;
                                    AddDocumentToDatabase(file3.getName(), absolutePath, str3, IfFolderNameExistReturnId);
                                }
                            } else {
                                z2 = z4;
                            }
                            z4 = z2;
                        }
                    }
                }
            }
        }
    }

    private void RecoverNotes(String str, boolean z) {
        int i;
        int i2;
        String str2;
        File[] fileArr;
        int i3;
        File[] fileArr2;
        boolean z2;
        DataRecover dataRecover = this;
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(dataRecover.context);
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(dataRecover.context);
        new Constants();
        File file = new File(str + StorageOptionsCommon.NOTES + "/");
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            int length = listFiles.length;
            int i4 = 0;
            boolean z3 = false;
            int i5 = 0;
            while (i5 < length) {
                File file2 = listFiles[i5];
                if (file2.isDirectory()) {
                    String name = file2.getName();
                    File[] listFiles2 = file2.listFiles();
                    int length2 = listFiles2.length;
                    int i6 = 0;
                    while (i6 < length2) {
                        File file3 = listFiles2[i6];
                        if (file3.isFile()) {
                            String name2 = file3.getName();
                            String substring = name2.substring(i4, name2.lastIndexOf("."));
                            String absolutePath = file3.getAbsolutePath();
                            boolean z4 = z3;
                            StringBuilder sb = new StringBuilder();
                            fileArr2 = listFiles;
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.NOTES);
                            sb.append(name);
                            File file4 = new File(sb.toString());
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(substring);
                            i3 = length;
                            sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file5 = new File(file4, sb2.toString());
                            if (!z) {
                                try {
                                    if (!file4.exists()) {
                                        file4.mkdirs();
                                    }
                                    absolutePath = Utilities.RecoveryEntryFile(dataRecover.context, file3, file5);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    z4 = true;
                                }
                            }
                            if (!z4) {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                sb3.append("NotesFileName");
                                sb3.append(" = '");
                                sb3.append(substring);
                                sb3.append("' AND ");
                                sb3.append("NotesFileIsDecoy");
                                sb3.append(" = ");
                                z2 = z4;
                                sb3.append(SecurityLocksCommon.IsFakeAccount);
                                String sb4 = sb3.toString();
                                String str3 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderName = '" + name + "' AND NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount;
                                double length3 = absolutePath.length();
                                new HashMap();
                                NotesFileDB_Pojo notesFileDB_Pojo = new NotesFileDB_Pojo();
                                NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
                                fileArr = listFiles2;
                                HashMap<String, String> ReadNote = new ReadNoteFromXML().ReadNote(absolutePath);
                                if (ReadNote != null) {
                                    i2 = length2;
                                    i = i5;
                                    if (!notesFolderDAL.IsFolderAlreadyExist(str3)) {
                                        notesFolderDB_Pojo.setNotesFolderName(name);
                                        str2 = name;
                                        notesFolderDB_Pojo.setNotesFolderLocation(file2.getAbsolutePath());
                                        notesFolderDB_Pojo.setNotesFolderCreatedDate(ReadNote.get("note_datetime_c"));
                                        notesFolderDB_Pojo.setNotesFolderModifiedDate(ReadNote.get("note_datetime_m"));
                                        notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
                                        notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                        notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
                                    } else {
                                        str2 = name;
                                    }
                                    new NotesFolderDB_Pojo();
                                    notesFileDB_Pojo.setNotesFileFolderId(notesFolderDAL.getNotesFolderInfoFromDatabase(str3).getNotesFolderId());
                                    notesFileDB_Pojo.setNotesFileName(substring);
                                    notesFileDB_Pojo.setNotesFileText(ReadNote.get("Text"));
                                    notesFileDB_Pojo.setNotesFileCreatedDate(ReadNote.get("note_datetime_c"));
                                    notesFileDB_Pojo.setNotesFileModifiedDate(ReadNote.get("note_datetime_m"));
                                    notesFileDB_Pojo.setNotesFileLocation(absolutePath);
                                    notesFileDB_Pojo.setNotesFileFromCloud(0);
                                    notesFileDB_Pojo.setNotesFileSize(length3);
                                    notesFileDB_Pojo.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                    if (notesFilesDAL.IsFileAlreadyExist(sb4)) {
                                        notesFilesDAL.updateNotesFileInfoInDatabase(notesFileDB_Pojo, "NotesFileId", String.valueOf(notesFileDB_Pojo.getNotesFileId()));
                                    } else {
                                        notesFilesDAL.addNotesFilesInfoInDatabase(notesFileDB_Pojo);
                                    }
                                    z3 = z2;
                                } else {
                                    i = i5;
                                    str2 = name;
                                }
                            } else {
                                z2 = z4;
                                i = i5;
                                str2 = name;
                                fileArr = listFiles2;
                            }
                            i2 = length2;
                            z3 = z2;
                        } else {
                            fileArr2 = listFiles;
                            i3 = length;
                            i = i5;
                            str2 = name;
                            fileArr = listFiles2;
                            i2 = length2;
                        }
                        i6++;
                        dataRecover = this;
                        listFiles = fileArr2;
                        length = i3;
                        listFiles2 = fileArr;
                        name = str2;
                        length2 = i2;
                        i5 = i;
                        i4 = 0;
                    }
                }
                listFiles = listFiles;
                length = length;
                i5++;
                dataRecover = this;
                i4 = 0;
            }
        }
    }

    private void RecoverWalletEntries(String str, boolean z) {
        int i;
        int i2;
        File[] fileArr;
        int i3;
        File[] fileArr2;
        String str2;
        boolean z2;
        DataRecover dataRecover = this;
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(dataRecover.context);
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(dataRecover.context);
        new Constants();
        WalletCommon walletCommon = new WalletCommon();
        File file = new File(str + StorageOptionsCommon.WALLET + "/");
        walletCommon.createDefaultCategories(dataRecover.context);
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            int length = listFiles.length;
            boolean z3 = false;
            int i4 = 0;
            while (i4 < length) {
                File file2 = listFiles[i4];
                if (file2.isDirectory()) {
                    String name = file2.getName();
                    File[] listFiles2 = file2.listFiles();
                    int length2 = listFiles2.length;
                    int i5 = 0;
                    while (i5 < length2) {
                        File file3 = listFiles2[i5];
                        if (file3.isFile()) {
                            String fileNameWithoutExtention = Utilities.getFileNameWithoutExtention(file3.getName());
                            String absolutePath = file3.getAbsolutePath();
                            boolean z4 = z3;
                            StringBuilder sb = new StringBuilder();
                            fileArr2 = listFiles;
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.WALLET);
                            sb.append(name);
                            File file4 = new File(sb.toString());
                            StringBuilder sb2 = new StringBuilder();
                            i3 = length;
                            sb2.append(StorageOptionsCommon.ENTRY);
                            sb2.append(fileNameWithoutExtention);
                            sb2.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file5 = new File(file4, sb2.toString());
                            if (!z) {
                                try {
                                    if (!file4.exists()) {
                                        file4.mkdirs();
                                    }
                                    absolutePath = Utilities.RecoveryEntryFile(dataRecover.context, file3, file5);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    str2 = absolutePath;
                                    z4 = true;
                                }
                            }
                            str2 = absolutePath;
                            if (!z4) {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
                                sb3.append("WalletEntryFileIsDecoy");
                                sb3.append(" = ");
                                sb3.append(SecurityLocksCommon.IsFakeAccount);
                                sb3.append(" AND ");
                                z2 = z4;
                                sb3.append("WalletEntryFileName");
                                sb3.append(" = '");
                                sb3.append(fileNameWithoutExtention);
                                fileArr = listFiles2;
                                sb3.append("'");
                                String sb4 = sb3.toString();
                                i2 = length2;
                                StringBuilder sb5 = new StringBuilder();
                                i = i4;
                                sb5.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
                                sb5.append("WalletCategoriesFileIsDecoy");
                                sb5.append(" = ");
                                sb5.append(SecurityLocksCommon.IsFakeAccount);
                                sb5.append(" AND ");
                                sb5.append("WalletCategoriesFileName");
                                sb5.append(" = '");
                                sb5.append(name);
                                sb5.append("'");
                                String sb6 = sb5.toString();
                                WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
                                WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
                                WalletCategoriesPojo currentCategoryData = walletCommon.getCurrentCategoryData(dataRecover.context, name);
                                String currentDate = walletCommon.getCurrentDate();
                                if (!walletCategoriesDAL.IsWalletCategoryAlreadyExist(sb6)) {
                                    walletCategoriesFileDB_Pojo.setCategoryFileName(name);
                                    walletCategoriesFileDB_Pojo.setCategoryFileLocation(file2.getAbsolutePath());
                                    walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(currentDate);
                                    walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(currentDate);
                                    walletCategoriesFileDB_Pojo.setCategoryFileSortBy(1);
                                    walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                    walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
                                } else {
                                    WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb6);
                                    categoryInfoFromDatabase.setCategoryFileModifiedDate(currentDate);
                                    categoryInfoFromDatabase.setCategoryFileLocation(file2.getAbsolutePath());
                                    categoryInfoFromDatabase.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                    walletCategoriesDAL.updateCategoryFromDatabase(categoryInfoFromDatabase, "WalletCategoriesFileId", String.valueOf(categoryInfoFromDatabase.getCategoryFileId()));
                                }
                                walletEntryFileDB_Pojo.setCategoryId(walletCategoriesDAL.getCategoryInfoFromDatabase(sb6).getCategoryFileId());
                                walletEntryFileDB_Pojo.setEntryFileName(fileNameWithoutExtention);
                                walletEntryFileDB_Pojo.setEntryFileCreatedDate(currentDate);
                                walletEntryFileDB_Pojo.setEntryFileModifiedDate(currentDate);
                                walletEntryFileDB_Pojo.setEntryFileLocation(str2);
                                walletEntryFileDB_Pojo.setCategoryFileIconIndex(currentCategoryData.getCategoryIconIndex());
                                walletEntryFileDB_Pojo.setEntryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                                if (walletEntriesDAL.IsWalletEntryAlreadyExist(sb4)) {
                                    walletEntriesDAL.updateEntryInDatabase(walletEntryFileDB_Pojo, "WalletEntryFileId", String.valueOf(walletEntryFileDB_Pojo.getEntryFileId()));
                                } else {
                                    walletEntriesDAL.addWalletEntriesInfoInDatabase(walletEntryFileDB_Pojo);
                                }
                            } else {
                                z2 = z4;
                                i = i4;
                                fileArr = listFiles2;
                                i2 = length2;
                            }
                            z3 = z2;
                        } else {
                            fileArr2 = listFiles;
                            i3 = length;
                            i = i4;
                            fileArr = listFiles2;
                            i2 = length2;
                        }
                        i5++;
                        dataRecover = this;
                        listFiles = fileArr2;
                        length = i3;
                        listFiles2 = fileArr;
                        length2 = i2;
                        i4 = i;
                    }
                }
                listFiles = listFiles;
                length = length;
                i4++;
                dataRecover = this;
            }
        }
    }

    private void AddPhotoAlbumToDatabase(String str) {
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setAlbumName(str);
        photoAlbum.setAlbumLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + str);
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        try {
            try {
                photoAlbumDAL.OpenWrite();
                photoAlbumDAL.AddPhotoAlbum(photoAlbum);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            photoAlbumDAL.close();
        }
    }

    private void AddVideoAlbumToDatabase(String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(str);
        videoAlbum.setAlbumLocation(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.VIDEOS + str);
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        try {
            try {
                videoAlbumDAL.OpenWrite();
                videoAlbumDAL.AddVideoAlbum(videoAlbum);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoAlbumDAL.close();
        }
    }

    private void AddPhotoToDatabase(String str, String str2, String str3, int i) {
        if (str.contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(str);
        }
        Log.d("Path", str2);
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str2);
        photo.setOriginalPhotoLocation(str3);
        photo.setAlbumId(i);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
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

    private void AddVideoToDatabase(String str, String str2, String str3, String str4, int i) {
        if (str.contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(str);
        }
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str2);
        video.setOriginalVideoLocation(str3);
        video.setthumbnail_video_location(str4);
        video.setAlbumId(i);
        VideoDAL videoDAL = new VideoDAL(this.context);
        try {
            try {
                videoDAL.OpenWrite();
                videoDAL.AddVideos(video);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoDAL.close();
        }
    }

    private void AddDocumentFolderToDatabase(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(str);
        documentFolder.setFolderLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + str);
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        try {
            try {
                documentFolderDAL.OpenWrite();
                documentFolderDAL.AddDocumentFolder(documentFolder);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            documentFolderDAL.close();
        }
    }

    private void AddDocumentToDatabase(String str, String str2, String str3, int i) {
        if (str.contains("#")) {
            str = Utilities.ChangeFileExtentionToOrignal(str);
        }
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setFolderLockDocumentLocation(str2);
        documentsEnt.setOriginalDocumentLocation(str3);
        documentsEnt.setFolderId(i);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
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
