package com.example.gallerylock.securebackupcloud;

import android.content.Context;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.gallerylock.audio.AudioDAL;
import com.example.gallerylock.audio.AudioEnt;
import com.example.gallerylock.audio.AudioPlayListDAL;
import com.example.gallerylock.audio.AudioPlayListEnt;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.documents.DocumentDAL;
import com.example.gallerylock.documents.DocumentFolder;
import com.example.gallerylock.documents.DocumentFolderDAL;
import com.example.gallerylock.documents.DocumentsEnt;
import com.example.gallerylock.notes.NotesCommon;
import com.example.gallerylock.notes.NotesFileDB_Pojo;
import com.example.gallerylock.notes.NotesFilesDAL;
import com.example.gallerylock.notes.NotesFolderDAL;
import com.example.gallerylock.notes.NotesFolderDB_Pojo;
import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoAlbum;
import com.example.gallerylock.photo.PhotoAlbumDAL;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.todolist.ToDoDAL;
import com.example.gallerylock.todolist.ToDoDB_Pojo;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoAlbum;
import com.example.gallerylock.video.VideoAlbumDAL;
import com.example.gallerylock.video.VideoDAL;
import com.example.gallerylock.wallet.WalletCategoriesDAL;
import com.example.gallerylock.wallet.WalletCategoriesFileDB_Pojo;
import com.example.gallerylock.wallet.WalletEntriesDAL;
import com.example.gallerylock.wallet.WalletEntryFileDB_Pojo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes2.dex */
public class DropboxCloud implements ISecureBackupCloud {
    private int DownloadType;
    Context context;
    public DropboxCloudApi dropboxCloudApi;
    ListFolderResult result = null;

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void GetFiles(String str) {
    }

    public DropboxCloud(Context context, int i) {
        this.context = context;
        this.DownloadType = i;
        this.dropboxCloudApi = new DropboxCloudApi(context);
        CreateFolderStructure();
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void UploadFile(BackupCloudEnt backupCloudEnt) {
        String str;
        if (CloudCommon.DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.PhotoFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.PhotoFolder + "/" + backupCloudEnt.GetFolderName());
        } else if (CloudCommon.DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.VideoFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.VideoFolder + "/" + backupCloudEnt.GetFolderName());
        } else if (CloudCommon.DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.DocumentFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.DocumentFolder + "/" + backupCloudEnt.GetFolderName());
        } else if (CloudCommon.DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.NotesFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.NotesFolder + "/" + backupCloudEnt.GetFolderName());
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.WalletFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.WalletFolder + "/" + backupCloudEnt.GetFolderName());
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.ToDoListFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.ToDoListFolder + "/" + backupCloudEnt.GetFolderName());
        } else if (CloudCommon.DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = CloudCommon.AudioFolder + "/" + backupCloudEnt.GetFolderName();
            CreateFolder(CloudCommon.AudioFolder + "/" + backupCloudEnt.GetFolderName());
        } else {
            str = "";
        }
        if (backupCloudEnt.GetUploadCount() > 0) {
            Enumeration<String> keys = backupCloudEnt.GetUploadPath().keys();
            while (keys.hasMoreElements()) {
                new DropBoxUploadFile(this.context, this.dropboxCloudApi.client, str, keys.nextElement(), this.DownloadType, backupCloudEnt).execute(new Void[0]);
            }
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void DownloadFile(BackupCloudEnt backupCloudEnt) {
        String str;
        String str2;
        if (CloudCommon.DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS;
        } else if (CloudCommon.DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS;
        } else if (CloudCommon.DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS;
        } else if (CloudCommon.DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES;
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.WALLET;
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST;
        } else if (CloudCommon.DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS;
        } else {
            str = "";
        }
        if (backupCloudEnt.GetDownloadCount() > 0) {
            Enumeration<String> keys = backupCloudEnt.GetDownloadPath().keys();
            while (keys.hasMoreElements()) {
                String nextElement = keys.nextElement();
                if (CloudCommon.DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
                    str2 = str;
                } else {
                    str2 = str + new File(nextElement).getParentFile().getName();
                }
                new DropBoxDownloadFile(this.context, this.dropboxCloudApi.client, nextElement, str2, this.DownloadType, backupCloudEnt).execute(new Void[0]);
            }
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void CreateLocalFolder(BackupCloudEnt backupCloudEnt) {
        String str;
        if (CloudCommon.DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS;
        } else if (CloudCommon.DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS;
        } else if (CloudCommon.DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS;
        } else if (CloudCommon.DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES;
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.WALLET;
        } else if (CloudCommon.DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS;
        } else {
            str = "";
        }
        if (CloudCommon.DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST;
        }
        AddFolderInLocal(backupCloudEnt.GetDropboxType(), str + backupCloudEnt.GetFolderName());
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public ArrayList<BackupCloudEnt> GetFolders(String str) {
        ListFolderResult listFolderResult;
        ArrayList<BackupCloudEnt> arrayList = new ArrayList<>();
        ArrayList<String> arrayList2 = new ArrayList<>();
        try {
            listFolderResult = this.dropboxCloudApi.client.files().listFolder(str);
        } catch (DbxException e) {
            e.printStackTrace();
            listFolderResult = null;
        }
        if (listFolderResult == null) {
            return arrayList;
        }
        for (Metadata metadata : listFolderResult.getEntries()) {
            BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
            if (metadata instanceof FolderMetadata) {
                FolderMetadata folderMetadata = (FolderMetadata) metadata;
                backupCloudEnt.SetMetadata(folderMetadata);
                backupCloudEnt.SetFolderName(folderMetadata.getName());
                backupCloudEnt.SetMimeType(backupCloudEnt.GetMimeType());
                backupCloudEnt.SetPath(metadata.getPathDisplay());
                backupCloudEnt.SetRoot(backupCloudEnt.GetRoot());
                backupCloudEnt.SetSize(backupCloudEnt.GetSize());
                backupCloudEnt.SetFileNames(GetCloudFolderFiles(backupCloudEnt.GetPath()));
                backupCloudEnt.SetUploadPath(GetUploadPaths(backupCloudEnt.GetFileNames(), metadata.getName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadPath(GetDownloadPaths(backupCloudEnt.GetFileNames(), metadata.getName()));
                backupCloudEnt.SetDownloadCount(backupCloudEnt.GetDownloadPath().size());
                backupCloudEnt.SetStatus(GetStatus(metadata.getName(), backupCloudEnt.GetDownloadCount(), backupCloudEnt.GetUploadCount()));
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetDownloadCompleteStatus(false);
                backupCloudEnt.SetUploadCompleteStatus(false);
                arrayList.add(backupCloudEnt);
                arrayList2.add(metadata.getName());
            }
        }
        return GetPhoneFolders(arrayList2, arrayList);
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void CreateFolder(String str) {
        try {
            this.dropboxCloudApi.client.files().createFolder(str);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void CreateFolder(BackupCloudEnt backupCloudEnt) {
        if (CloudCommon.DropboxType.Photos.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.PhotoFolder + backupCloudEnt.GetFolderName() + "/");
        } else if (CloudCommon.DropboxType.Videos.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.VideoFolder + backupCloudEnt.GetFolderName() + "/");
        } else if (CloudCommon.DropboxType.Documents.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.DocumentFolder + backupCloudEnt.GetFolderName() + "/");
        } else if (CloudCommon.DropboxType.Notes.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.NotesFolder + backupCloudEnt.GetFolderName() + "/");
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.WalletFolder + backupCloudEnt.GetFolderName() + "/");
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.ToDoListFolder + backupCloudEnt.GetFolderName() + "/");
        } else if (CloudCommon.DropboxType.Audio.ordinal() == backupCloudEnt.GetDropboxType()) {
            CreateFolder(CloudCommon.AudioFolder + backupCloudEnt.GetFolderName() + "/");
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.securebackupcloud.ISecureBackupCloud
    public void CreateFolderStructure() {
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.PhotoFolder);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.VideoFolder);
        } catch (DbxException e2) {
            e2.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.DocumentFolder);
        } catch (DbxException e3) {
            e3.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.NotesFolder);
        } catch (DbxException e4) {
            e4.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.WalletFolder);
        } catch (DbxException e5) {
            e5.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.ToDoListFolder);
        } catch (DbxException e6) {
            e6.printStackTrace();
        }
        try {
            this.dropboxCloudApi.client.files().createFolder(CloudCommon.AudioFolder);
        } catch (DbxException e7) {
            e7.printStackTrace();
        }
    }

    private ArrayList<String> GetCloudFolderFiles(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            this.result = this.dropboxCloudApi.client.files().listFolder(str);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        for (Metadata metadata : this.result.getEntries()) {
            arrayList.add(metadata.getPathDisplay());
        }
        return arrayList;
    }

    private Hashtable<String, Boolean> GetDownloadPaths(ArrayList<String> arrayList, String str) {
        if (CloudCommon.DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetDownloadPhotosPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetDownloadVideosPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDownloadDocumentsPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetDownloadNotesPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetDownloadWalletPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetDownloadMusicPath(arrayList, str);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetDownloadPathForToDo(String str, String str2) {
        if (CloudCommon.DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetDownloadToDoPath(str, str2);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetUploadPathForToDo(String str, String str2) {
        if (CloudCommon.DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetUploadToDoPath(str, str2);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetUploadPaths(ArrayList<String> arrayList, String str) {
        if (CloudCommon.DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetUploadPhotosPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetUploadVideosPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetUploadDocumentsPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetUploadNotesPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetUploadWalletPath(arrayList, str);
        }
        if (CloudCommon.DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetUploadMusicPath(arrayList, str);
        }
        return null;
    }

    private int GetStatus(String str, int i, int i2) {
        if (CloudCommon.DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetPhotoStatus(str, i, i2);
        }
        if (CloudCommon.DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetVideoStatus(str, i, i2);
        }
        if (CloudCommon.DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDocumentStatus(str, i, i2);
        }
        if (CloudCommon.DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetNoteStatus(str, i, i2);
        }
        if (CloudCommon.DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetWalletStatus(str, i, i2);
        }
        if (CloudCommon.DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetToDoStatus(str, i, i2);
        }
        if (CloudCommon.DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetMusicStatus(str, i, i2);
        }
        return 0;
    }

    private int GetToDoStatus(String str, int i, int i2) {
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        new Constants();
        if (toDoDAL.getToDoInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ToDoName = '" + str + "' AND ToDoIsDecoy = " + SecurityLocksCommon.IsFakeAccount).getToDoFileName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i != 0) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i2 != 0) {
            return CloudCommon.CloudFolderStatus.OnlyPhone.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        if (CloudCommon.DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetPhotoPhoneFolders(arrayList, arrayList2);
        }
        if (CloudCommon.DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetVideoPhoneFolders(arrayList, arrayList2);
        }
        if (CloudCommon.DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDocumentPhoneFolders(arrayList, arrayList2);
        }
        if (CloudCommon.DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetNotesPhoneFolders(arrayList, arrayList2);
        }
        if (CloudCommon.DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetWalletPhoneFolders(arrayList, arrayList2);
        }
        if (CloudCommon.DropboxType.ToDo.ordinal() == this.DownloadType) {
            return GetToDoPhoneFiles(arrayList, arrayList2);
        }
        if (CloudCommon.DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetMusicPhoneFolders(arrayList, arrayList2);
        }
        return null;
    }

    private Hashtable<String, Boolean> GetPhoneFolderFiles(String str) {
        if (CloudCommon.DropboxType.Photos.ordinal() == this.DownloadType) {
            return GetPhotoPhoneFolderFiles(str);
        }
        if (CloudCommon.DropboxType.Videos.ordinal() == this.DownloadType) {
            return GetVideoPhoneFolderFiles(str);
        }
        if (CloudCommon.DropboxType.Documents.ordinal() == this.DownloadType) {
            return GetDocumentPhoneFolderFiles(str);
        }
        if (CloudCommon.DropboxType.Notes.ordinal() == this.DownloadType) {
            return GetNotesPhoneFolderFiles(str);
        }
        if (CloudCommon.DropboxType.Wallet.ordinal() == this.DownloadType) {
            return GetWalletPhoneFolderFiles(str);
        }
        if (CloudCommon.DropboxType.Audio.ordinal() == this.DownloadType) {
            return GetMusicPhoneFolderFiles(str);
        }
        return null;
    }

    public String FileName(String str) {
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == " /".charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    private Hashtable<String, Boolean> GetDownloadToDoPath(String str, String str2) {
        new SimpleDateFormat("EEE, dd MMM yyyy HH:edit_share_btn:ss");
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        new Constants();
        String str3 = new File(str2).getName().split(StorageOptionsCommon.NOTES_FILE_EXTENSION)[0];
        try {
            if (!new File(str2).getName().contentEquals(new File(toDoDAL.getToDoInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ToDoName = '" + str3 + "' AND ToDoIsDecoy = " + SecurityLocksCommon.IsFakeAccount).getToDoFileLocation()).getName())) {
                hashtable.put(str2, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadTodo123tPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        new Constants();
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + str + "'");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        sb.append("WalletEntryFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        sb.append("WalletCategoriesFileId");
        sb.append(" = ");
        sb.append(categoryInfoFromDatabase.getCategoryFileId());
        List<WalletEntryFileDB_Pojo> allEntriesInfoFromDatabase = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb.toString());
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<WalletEntryFileDB_Pojo> it2 = allEntriesInfoFromDatabase.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(new File(it2.next().getEntryFileLocation()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadToDoPath(String str, String str2) {
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        new Constants();
        String str3 = new File(str2).getName().split(StorageOptionsCommon.NOTES_FILE_EXTENSION)[0];
        ToDoDB_Pojo toDoInfoFromDatabase = toDoDAL.getToDoInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ToDoName = '" + str3 + "' AND ToDoIsDecoy = " + SecurityLocksCommon.IsFakeAccount);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:edit_share_btn:ss", Locale.getDefault());
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE, dd MMM yyyy hh:edit_share_btn:ss +0000", Locale.getDefault());
            Date parse = simpleDateFormat.parse(toDoInfoFromDatabase.getToDoFileModifiedDate());
            Date parse2 = simpleDateFormat2.parse(str);
            Date parse3 = simpleDateFormat2.parse(Utilities.convertDateToGMT(parse));
            if (!toDoInfoFromDatabase.getToDoFileName().equals("") && new File(toDoInfoFromDatabase.getToDoFileLocation()).getName().contentEquals(new File(str2).getName()) && parse3.after(parse2)) {
                hashtable.put(toDoInfoFromDatabase.getToDoFileLocation(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashtable;
    }

    private ArrayList<BackupCloudEnt> GetToDoPhoneFiles(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        ToDoDAL toDoDAL = new ToDoDAL(this.context);
        new Constants();
        for (ToDoDB_Pojo toDoDB_Pojo : toDoDAL.getAllToDoInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ToDoIsDecoy = " + SecurityLocksCommon.IsFakeAccount)) {
            if (!arrayList.contains(toDoDB_Pojo.getToDoFileName().concat(StorageOptionsCommon.NOTES_FILE_EXTENSION))) {
                Hashtable<String, Boolean> hashtable = new Hashtable<>();
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(toDoDB_Pojo.getToDoFileName());
                backupCloudEnt.SetPath(CloudCommon.ToDoListFolder);
                backupCloudEnt.SetDropboxType(this.DownloadType);
                hashtable.put(toDoDB_Pojo.getToDoFileLocation(), false);
                backupCloudEnt.SetUploadPath(hashtable);
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetDownloadVideosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        VideoDAL videoDAL = new VideoDAL(this.context);
        videoDAL.OpenRead();
        List<Video> GetVideosById = videoDAL.GetVideosById(GetAlbum.getId());
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<Video> it2 = GetVideosById.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(it2.next().getVideoName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        videoAlbumDAL.close();
        videoDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadVideosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        VideoDAL videoDAL = new VideoDAL(this.context);
        videoDAL.OpenRead();
        List<Video> GetVideosById = videoDAL.GetVideosById(GetAlbum.getId());
        if (GetVideosById.size() > 0) {
            for (Video video : GetVideosById) {
                Iterator<String> it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (video.getVideoName().contentEquals(new File(it.next()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(video.getFolderLockVideoLocation(), false);
                }
            }
        }
        videoAlbumDAL.close();
        videoDAL.close();
        return hashtable;
    }

    private int GetVideoStatus(String str, int i, int i2) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        videoAlbumDAL.close();
        if (GetAlbum.getAlbumName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetVideoPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        for (VideoAlbum videoAlbum : videoAlbumDAL.GetAlbums(0)) {
            if (!arrayList.contains(videoAlbum.getAlbumName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(videoAlbum.getAlbumName());
                backupCloudEnt.SetPath(videoAlbum.getAlbumLocation());
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(videoAlbum.getAlbumName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetVideoPhoneFolderFiles(String str) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenRead();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str);
        VideoDAL videoDAL = new VideoDAL(this.context);
        videoDAL.OpenRead();
        List<Video> GetVideoByAlbumId = videoDAL.GetVideoByAlbumId(GetAlbum.getId(), 4);
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (Video video : GetVideoByAlbumId) {
            hashtable.put(video.getFolderLockVideoLocation(), false);
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadPhotosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        photoDAL.OpenRead();
        List<Photo> GetPhotoByAlbumId = photoDAL.GetPhotoByAlbumId(GetAlbum.getId(), 4);
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<Photo> it2 = GetPhotoByAlbumId.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(it2.next().getPhotoName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        photoAlbumDAL.close();
        photoDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadPhotosPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        photoDAL.OpenRead();
        List<Photo> GetPhotoByAlbumId = photoDAL.GetPhotoByAlbumId(GetAlbum.getId(), 4);
        if (GetPhotoByAlbumId.size() > 0) {
            for (Photo photo : GetPhotoByAlbumId) {
                Iterator<String> it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (photo.getPhotoName().contentEquals(new File(it.next()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(photo.getFolderLockPhotoLocation(), false);
                }
            }
        }
        photoAlbumDAL.close();
        photoDAL.close();
        return hashtable;
    }

    private int GetPhotoStatus(String str, int i, int i2) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        photoAlbumDAL.close();
        if (GetAlbum.getAlbumName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetPhotoPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        for (PhotoAlbum photoAlbum : photoAlbumDAL.GetAlbums(0)) {
            if (!arrayList.contains(photoAlbum.getAlbumName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(photoAlbum.getAlbumName());
                backupCloudEnt.SetPath(photoAlbum.getAlbumLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(photoAlbum.getAlbumName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetPhotoPhoneFolderFiles(String str) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenRead();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str);
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        photoDAL.OpenRead();
        List<Photo> GetPhotoByAlbumId = photoDAL.GetPhotoByAlbumId(GetAlbum.getId(), 4);
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (Photo photo : GetPhotoByAlbumId) {
            hashtable.put(photo.getFolderLockPhotoLocation(), false);
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadMusicPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        AudioDAL audioDAL = new AudioDAL(this.context);
        audioDAL.OpenRead();
        List<AudioEnt> GetAudios = audioDAL.GetAudios(GetPlayList.getId());
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<AudioEnt> it2 = GetAudios.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(it2.next().getAudioName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        audioPlayListDAL.close();
        audioDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadMusicPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        AudioDAL audioDAL = new AudioDAL(this.context);
        audioDAL.OpenRead();
        List<AudioEnt> GetAudios = audioDAL.GetAudios(GetPlayList.getId());
        if (GetAudios.size() > 0) {
            for (AudioEnt audioEnt : GetAudios) {
                Iterator<String> it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (audioEnt.getAudioName().contentEquals(new File(it.next()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(audioEnt.getFolderLockAudioLocation(), false);
                }
            }
        }
        audioPlayListDAL.close();
        audioDAL.close();
        return hashtable;
    }

    private int GetMusicStatus(String str, int i, int i2) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        audioPlayListDAL.close();
        if (GetPlayList.getPlayListName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetMusicPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        for (AudioPlayListEnt audioPlayListEnt : audioPlayListDAL.GetPlayLists()) {
            if (!arrayList.contains(audioPlayListEnt.getPlayListName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(audioPlayListEnt.getPlayListName());
                backupCloudEnt.SetPath(audioPlayListEnt.getPlayListLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(audioPlayListEnt.getPlayListName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetMusicPhoneFolderFiles(String str) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenRead();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str);
        AudioDAL audioDAL = new AudioDAL(this.context);
        audioDAL.OpenRead();
        List<AudioEnt> GetAudios = audioDAL.GetAudios(GetPlayList.getId());
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (AudioEnt audioEnt : GetAudios) {
            hashtable.put(audioEnt.getFolderLockAudioLocation(), false);
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadNotesPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        new Constants();
        NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderName = '" + str + "' AND NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount);
        List<NotesFileDB_Pojo> allNotesFileInfoFromDatabase = notesFilesDAL.getAllNotesFileInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE NotesFolderId = " + notesFolderInfoFromDatabase.getNotesFolderId() + " AND NotesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount);
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<NotesFileDB_Pojo> it2 = allNotesFileInfoFromDatabase.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(new File(it2.next().getNotesFileLocation()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadNotesPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        new Constants();
        NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderName = '" + str + "' AND NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount);
        List<NotesFileDB_Pojo> allNotesFileInfoFromDatabase = notesFilesDAL.getAllNotesFileInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE NotesFolderId = " + notesFolderInfoFromDatabase.getNotesFolderId() + " AND NotesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount);
        if (allNotesFileInfoFromDatabase.size() > 0) {
            for (NotesFileDB_Pojo notesFileDB_Pojo : allNotesFileInfoFromDatabase) {
                Iterator<String> it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (new File(notesFileDB_Pojo.getNotesFileLocation()).getName().contentEquals(new File(it.next()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(notesFileDB_Pojo.getNotesFileLocation(), false);
                }
            }
        }
        return hashtable;
    }

    private int GetNoteStatus(String str, int i, int i2) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        new Constants();
        if (notesFolderDAL.getNotesFolderInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderName = '" + str + "' AND NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount).getNotesFolderName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetNotesPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        new Constants();
        for (NotesFolderDB_Pojo notesFolderDB_Pojo : notesFolderDAL.getAllNotesFolderInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount)) {
            if (!arrayList.contains(notesFolderDB_Pojo.getNotesFolderName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(notesFolderDB_Pojo.getNotesFolderName());
                backupCloudEnt.SetPath(notesFolderDB_Pojo.getNotesFolderLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(notesFolderDB_Pojo.getNotesFolderName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetNotesPhoneFolderFiles(String str) {
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        new Constants();
        NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderName = '" + str + "' AND NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount);
        for (NotesFileDB_Pojo notesFileDB_Pojo : notesFilesDAL.getAllNotesFileInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE NotesFolderId = " + notesFolderInfoFromDatabase.getNotesFolderId() + " AND NotesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount)) {
            hashtable.put(notesFileDB_Pojo.getNotesFileLocation(), false);
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadWalletPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        new Constants();
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + str + "'");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        sb.append("WalletEntryFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        sb.append("WalletCategoriesFileId");
        sb.append(" = ");
        sb.append(categoryInfoFromDatabase.getCategoryFileId());
        List<WalletEntryFileDB_Pojo> allEntriesInfoFromDatabase = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb.toString());
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<WalletEntryFileDB_Pojo> it2 = allEntriesInfoFromDatabase.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(new File(it2.next().getEntryFileLocation()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadWalletPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        new Constants();
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + str + "'");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        sb.append("WalletEntryFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        sb.append("WalletCategoriesFileId");
        sb.append(" = ");
        sb.append(categoryInfoFromDatabase.getCategoryFileId());
        List<WalletEntryFileDB_Pojo> allEntriesInfoFromDatabase = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb.toString());
        if (allEntriesInfoFromDatabase.size() > 0) {
            for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : allEntriesInfoFromDatabase) {
                Iterator<String> it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (new File(walletEntryFileDB_Pojo.getEntryFileLocation()).getName().contentEquals(new File(it.next()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(walletEntryFileDB_Pojo.getEntryFileLocation(), false);
                }
            }
        }
        return hashtable;
    }

    private int GetWalletStatus(String str, int i, int i2) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        new Constants();
        if (walletCategoriesDAL.getCategoryInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + str + "'").getCategoryFileName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetWalletPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        new Constants();
        for (WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo : walletCategoriesDAL.getAllCategoriesInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount)) {
            if (!arrayList.contains(walletCategoriesFileDB_Pojo.getCategoryFileName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(walletCategoriesFileDB_Pojo.getCategoryFileName());
                backupCloudEnt.SetPath(walletCategoriesFileDB_Pojo.getCategoryFileLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(walletCategoriesFileDB_Pojo.getCategoryFileName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetWalletPhoneFolderFiles(String str) {
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        new Constants();
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + str + "'");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        sb.append("WalletEntryFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        sb.append(" AND ");
        sb.append("WalletCategoriesFileId");
        sb.append(" = ");
        sb.append(categoryInfoFromDatabase.getCategoryFileId());
        for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : walletEntriesDAL.getAllEntriesInfoFromDatabase(sb.toString())) {
            hashtable.put(walletEntryFileDB_Pojo.getEntryFileLocation(), false);
        }
        return hashtable;
    }

    private Hashtable<String, Boolean> GetDownloadDocumentsPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        documentDAL.OpenRead();
        List<DocumentsEnt> GetDocuments = documentDAL.GetDocuments(GetFolder.getId(), 0);
        if (arrayList.size() > 0) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Iterator<DocumentsEnt> it2 = GetDocuments.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (new File(next).getName().contentEquals(it2.next().getDocumentName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(next, false);
                }
            }
        }
        documentFolderDAL.close();
        documentDAL.close();
        return hashtable;
    }

    private Hashtable<String, Boolean> GetUploadDocumentsPath(ArrayList<String> arrayList, String str) {
        boolean z;
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        documentDAL.OpenRead();
        List<DocumentsEnt> GetDocuments = documentDAL.GetDocuments(GetFolder.getId(), 0);
        if (GetDocuments.size() > 0) {
            for (DocumentsEnt documentsEnt : GetDocuments) {
                Iterator<String> it = arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (documentsEnt.getDocumentName().contentEquals(new File(it.next()).getName())) {
                            z = true;
                            break;
                        }
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    hashtable.put(documentsEnt.getFolderLockDocumentLocation(), false);
                }
            }
        }
        documentFolderDAL.close();
        documentDAL.close();
        return hashtable;
    }

    private int GetDocumentStatus(String str, int i, int i2) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        documentFolderDAL.close();
        if (GetFolder.getFolderName() == null) {
            return CloudCommon.CloudFolderStatus.OnlyCloud.ordinal();
        }
        if (i == 0 && i2 == 0) {
            return CloudCommon.CloudFolderStatus.CloudAndPhoneCompleteSync.ordinal();
        }
        return CloudCommon.CloudFolderStatus.CloudAndPhoneNotSync.ordinal();
    }

    private ArrayList<BackupCloudEnt> GetDocumentPhoneFolders(ArrayList<String> arrayList, ArrayList<BackupCloudEnt> arrayList2) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        for (DocumentFolder documentFolder : documentFolderDAL.GetFolders(0)) {
            if (!arrayList.contains(documentFolder.getFolderName())) {
                BackupCloudEnt backupCloudEnt = new BackupCloudEnt();
                backupCloudEnt.SetFolderName(documentFolder.getFolderName());
                backupCloudEnt.SetPath(documentFolder.getFolderLocation());
                backupCloudEnt.SetDropboxType(this.DownloadType);
                backupCloudEnt.SetUploadPath(GetPhoneFolderFiles(documentFolder.getFolderName()));
                backupCloudEnt.SetUploadCount(backupCloudEnt.GetUploadPath().size());
                backupCloudEnt.SetDownloadCount(0);
                backupCloudEnt.SetStatus(CloudCommon.CloudFolderStatus.OnlyPhone.ordinal());
                arrayList2.add(backupCloudEnt);
            }
        }
        return arrayList2;
    }

    private Hashtable<String, Boolean> GetDocumentPhoneFolderFiles(String str) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenRead();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        documentDAL.OpenRead();
        List<DocumentsEnt> GetDocuments = documentDAL.GetDocuments(GetFolder.getId(), 0);
        Hashtable<String, Boolean> hashtable = new Hashtable<>();
        for (DocumentsEnt documentsEnt : GetDocuments) {
            hashtable.put(documentsEnt.getFolderLockDocumentLocation(), false);
        }
        return hashtable;
    }

    private void AddFolderInLocal(int i, String str) {
        if (CloudCommon.DropboxType.Photos.ordinal() == i) {
            AddPhotoToDatabase(str);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == i) {
            AddVideoToDatabase(str);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == i) {
            AddDocumentToDatabase(str);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == i) {
            AddNoteToDatabase(str);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == i) {
            AddWalletToDatabase(str);
        } else if (CloudCommon.DropboxType.Audio.ordinal() == i) {
            AddMusicToDatabase(str);
        }
    }

    private void AddPhotoToDatabase(String str) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenWrite();
        PhotoAlbum photoAlbum = new PhotoAlbum();
        photoAlbum.setAlbumName(new File(str).getName());
        photoAlbum.setAlbumLocation(new File(str).getAbsolutePath());
        photoAlbumDAL.AddPhotoAlbum(photoAlbum);
        photoAlbumDAL.close();
    }

    private void AddVideoToDatabase(String str) {
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenWrite();
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(new File(str).getName());
        videoAlbum.setAlbumLocation(new File(str).getParent());
        videoAlbumDAL.AddVideoAlbum(videoAlbum);
        videoAlbumDAL.close();
    }

    private void AddDocumentToDatabase(String str) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenWrite();
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(new File(str).getName());
        documentFolder.setFolderLocation(new File(str).getParent());
        documentFolderDAL.AddDocumentFolder(documentFolder);
        documentFolderDAL.close();
    }

    private void AddNoteToDatabase(String str) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
        String currentDate = new NotesCommon().getCurrentDate();
        notesFolderDB_Pojo.setNotesFolderName(new File(str).getName());
        notesFolderDB_Pojo.setNotesFolderLocation(new File(str).getParent());
        notesFolderDB_Pojo.setNotesFolderFilesSortBy(0);
        notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
        notesFolderDB_Pojo.setNotesFolderCreatedDate(currentDate);
        notesFolderDB_Pojo.setNotesFolderModifiedDate(currentDate);
        notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
    }

    private void AddWalletToDatabase(String str) {
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        String currentDate = new NotesCommon().getCurrentDate();
        walletCategoriesFileDB_Pojo.setCategoryFileName(new File(str).getName());
        walletCategoriesFileDB_Pojo.setCategoryFileLocation(new File(str).getParent());
        walletCategoriesFileDB_Pojo.setCategoryFileSortBy(0);
        walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(currentDate);
        walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(currentDate);
        walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
    }

    private void AddMusicToDatabase(String str) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenWrite();
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setPlayListName(new File(str).getName());
        audioPlayListEnt.setPlayListLocation(new File(str).getParent());
        audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
        audioPlayListDAL.close();
    }
}
