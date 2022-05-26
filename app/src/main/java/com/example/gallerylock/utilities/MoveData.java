package com.example.gallerylock.utilities;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoAlbum;
import com.example.gallerylock.photo.PhotoAlbumDAL;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoAlbum;
import com.example.gallerylock.video.VideoAlbumDAL;
import com.example.gallerylock.video.VideoDAL;
import com.example.gallerylock.wallet.WalletCategoriesDAL;
import com.example.gallerylock.wallet.WalletCategoriesFileDB_Pojo;
import com.example.gallerylock.wallet.WalletEntriesDAL;
import com.example.gallerylock.wallet.WalletEntryFileDB_Pojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class MoveData {
    static SharedPreferences DataTransferPrefs;
    static SharedPreferences.Editor DataTransferprefsEditor;
    private static MoveData instance;
    Context context;
    DocumentDAL documentDAL;
    DocumentFolderDAL documentFolderDAL;
    NotesFilesDAL notesFilesDAL;
    NotesFolderDAL notesFolderDAL;
    PhotoAlbumDAL photoAlbumDAL;
    PhotoDAL photoDAL;
    VideoAlbumDAL videoAlbumDAL;
    VideoDAL videoDAL;
    WalletCategoriesDAL walletCategoriesDAL;
    WalletEntriesDAL walletEntriesDAL;
    private List<Photo> photos = new ArrayList();
    private List<Video> videos = new ArrayList();
    private List<DocumentsEnt> documentsEnts = new ArrayList();
    private List<NotesFileDB_Pojo> notesDbList = new ArrayList();
    private List<WalletEntryFileDB_Pojo> walletEntriesList = new ArrayList();
    private ArrayList<String> allFiles = new ArrayList<>();
    Constants constants = new Constants();

    private MoveData(Context context) {
        this.context = context;
    }

    public static MoveData getInstance(Context context) {
        if (instance == null) {
            instance = new MoveData(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences("DataTransferStatus", 0);
            DataTransferPrefs = sharedPreferences;
            DataTransferprefsEditor = sharedPreferences.edit();
        }
        return instance;
    }

    public void MoveDataToORFromCard() {
        DataMove();
    }

    public void MoveDataToORFromCardFromSetting() {
        DataMoveFromSetting();
    }

    public void GetDataFromDataBase() {
        PhotoDAL photoDAL = new PhotoDAL(this.context);
        this.photoDAL = photoDAL;
        photoDAL.OpenRead();
        this.photos = this.photoDAL.GetPhotos();
        this.photoDAL.close();
        VideoDAL videoDAL = new VideoDAL(this.context);
        this.videoDAL = videoDAL;
        videoDAL.OpenRead();
        this.videos = this.videoDAL.GetVideos();
        this.videoDAL.close();
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        this.documentDAL = documentDAL;
        documentDAL.OpenRead();
        this.documentsEnts = this.documentDAL.GetAllDocuments();
        this.documentDAL.close();
        this.notesFilesDAL = new NotesFilesDAL(this.context);
        this.walletEntriesDAL = new WalletEntriesDAL(this.context);
        NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        this.constants.getClass();
        sb.append("NotesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        this.notesDbList = notesFilesDAL.getAllNotesFileInfoFromDatabase(sb.toString());
        WalletEntriesDAL walletEntriesDAL = this.walletEntriesDAL;
        StringBuilder sb2 = new StringBuilder();
        this.constants.getClass();
        sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
        this.constants.getClass();
        sb2.append("WalletEntryFileIsDecoy");
        sb2.append(" = ");
        sb2.append(SecurityLocksCommon.IsFakeAccount);
        this.walletEntriesList = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb2.toString());
    }

    public ArrayList<String> GetAllFilesPath() {
        this.allFiles.clear();
        for (Photo photo : this.photos) {
            this.allFiles.add(photo.getFolderLockPhotoLocation());
        }
        for (Video video : this.videos) {
            this.allFiles.add(video.getFolderLockVideoLocation());
        }
        for (DocumentsEnt documentsEnt : this.documentsEnts) {
            this.allFiles.add(documentsEnt.getFolderLockDocumentLocation());
        }
        for (NotesFileDB_Pojo notesFileDB_Pojo : this.notesDbList) {
            this.allFiles.add(notesFileDB_Pojo.getNotesFileLocation());
        }
        for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : this.walletEntriesList) {
            this.allFiles.add(walletEntryFileDB_Pojo.getEntryFileLocation());
        }
        return this.allFiles;
    }

    private void DataMove() {
        try {
            if (!DataTransferPrefs.getBoolean("isPhotoTransferComplete", false)) {
                PhotoMove();
            }
        } catch (Exception e) {
            Log.e("Photo Move Exception", e.getMessage(), e);
            e.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isVideoTransferComplete", false)) {
                VideoMove();
            }
        } catch (Exception e2) {
            Log.e("Video Move Exception", e2.getMessage(), e2);
            e2.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isDocumentTransferComplete", false)) {
                VideoMove();
            }
        } catch (Exception e3) {
            Log.e("Document Move Exception", e3.getMessage(), e3);
            e3.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isNotesTransferComplete", false)) {
                NotesMove();
            }
        } catch (Exception e4) {
            Log.e("Document Move Exception", e4.getMessage(), e4);
            e4.printStackTrace();
        }
        try {
            if (!DataTransferPrefs.getBoolean("isWalletTransferComplete", false)) {
                WalletMove();
            }
        } catch (Exception e5) {
            Log.e("Document Move Exception", e5.getMessage(), e5);
            e5.printStackTrace();
        }
    }

    private void DataMoveFromSetting() {
        PhotoMove();
        VideoMove();
        DocumentMove();
        NotesMove();
        WalletMove();
    }

    private void PhotoMove() {
        String str;
        boolean z = true;
        for (Photo photo : this.photos) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
            this.photoAlbumDAL = photoAlbumDAL;
            photoAlbumDAL.OpenWrite();
            this.photoDAL.OpenWrite();
            PhotoAlbum GetAlbumById = this.photoAlbumDAL.GetAlbumById(Integer.toString(photo.getAlbumId()));
            File file = new File(photo.getFolderLockPhotoLocation());
            if (!photo.getFolderLockPhotoLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.PHOTOS + GetAlbumById.getAlbumName();
                try {
                    Utilities.RecoveryHideFileSDCard(this.context, file, new File(str2));
                    if (!photo.getPhotoName().contains("#")) {
                        str = Utilities.ChangeFileExtention(photo.getPhotoName());
                    } else {
                        str = photo.getPhotoName();
                    }
                    if (str2.length() > 0) {
                        this.photoAlbumDAL.UpdateAlbumLocation(photo.getAlbumId(), str2);
                        this.photoDAL.UpdatePhotosLocation(photo.getId(), str2 + "/" + str);
                    }
                } catch (IOException e) {
                    Log.e("Photo Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        PhotoAlbumDAL photoAlbumDAL2 = this.photoAlbumDAL;
        if (!(photoAlbumDAL2 == null || this.photoDAL == null)) {
            photoAlbumDAL2.close();
            this.photoDAL.close();
        }
        DataTransferprefsEditor.putBoolean("isPhotoTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    private void VideoMove() {
        String str;
        boolean z = true;
        for (Video video : this.videos) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
            this.videoAlbumDAL = videoAlbumDAL;
            videoAlbumDAL.OpenWrite();
            this.videoDAL.OpenWrite();
            VideoAlbum GetAlbumById = this.videoAlbumDAL.GetAlbumById(video.getAlbumId());
            File file = new File(video.getFolderLockVideoLocation());
            String str2 = video.getthumbnail_video_location();
            String FileName = Utilities.FileName(str2);
            if (!video.getFolderLockVideoLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                String str3 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + GetAlbumById.getAlbumName();
                try {
                    Utilities.RecoveryHideFileSDCard(this.context, file, new File(str3));
                    String str4 = FileName.contains("#") ? StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + GetAlbumById.getAlbumName() + "/VideoThumnails/" + FileName : StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + GetAlbumById.getAlbumName() + "/VideoThumnails/" + FileName.substring(0, FileName.lastIndexOf(".")) + "#jpg";
                    try {
                        Utilities.UnHideThumbnail(this.context, str2, str4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!video.getVideoName().contains("#")) {
                        str = Utilities.ChangeFileExtention(video.getVideoName());
                    } else {
                        str = video.getVideoName();
                    }
                    if (str3.length() > 0) {
                        this.videoAlbumDAL.UpdateAlbumLocation(video.getAlbumId(), str3);
                        this.videoDAL.UpdateVideosLocation(video.getId(), str3 + "/" + str, str4);
                    }
                } catch (IOException e2) {
                    Log.e("Video Move Exception", e2.getMessage(), e2);
                    e2.printStackTrace();
                    z = false;
                }
            }
        }
        VideoAlbumDAL videoAlbumDAL2 = this.videoAlbumDAL;
        if (videoAlbumDAL2 != null) {
            videoAlbumDAL2.close();
            VideoDAL videoDAL = this.videoDAL;
            if (videoDAL != null) {
                videoDAL.close();
            }
        }
        DataTransferprefsEditor.putBoolean("isVideoTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    private void DocumentMove() {
        String str;
        boolean z = true;
        for (DocumentsEnt documentsEnt : this.documentsEnts) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
            this.documentFolderDAL = documentFolderDAL;
            documentFolderDAL.OpenWrite();
            this.documentDAL.OpenWrite();
            DocumentFolder GetFolderById = this.documentFolderDAL.GetFolderById(Integer.toString(documentsEnt.getFolderId()));
            File file = new File(documentsEnt.getFolderLockDocumentLocation());
            if (!documentsEnt.getFolderLockDocumentLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + GetFolderById.getFolderName();
                try {
                    Utilities.RecoveryHideFileSDCard(this.context, file, new File(str2));
                    if (!documentsEnt.getDocumentName().contains("#")) {
                        str = Utilities.ChangeFileExtention(documentsEnt.getDocumentName());
                    } else {
                        str = documentsEnt.getDocumentName();
                    }
                    if (str2.length() > 0) {
                        this.documentFolderDAL.UpdateFolderLocation(documentsEnt.getFolderId(), str2);
                        this.documentDAL.UpdateDocumentsLocation(documentsEnt.getId(), str2 + "/" + str);
                    }
                } catch (IOException e) {
                    Log.e("Document Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        DocumentFolderDAL documentFolderDAL2 = this.documentFolderDAL;
        if (!(documentFolderDAL2 == null || this.photoDAL == null)) {
            documentFolderDAL2.close();
            this.photoDAL.close();
        }
        DataTransferprefsEditor.putBoolean("isDocumentTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    public void NotesMove() {
        this.notesFolderDAL = new NotesFolderDAL(this.context);
        boolean z = true;
        for (NotesFileDB_Pojo notesFileDB_Pojo : this.notesDbList) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb.append("NotesFolderId");
            sb.append(" = ");
            sb.append(notesFileDB_Pojo.getNotesFileFolderId());
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("NotesFolderIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(sb.toString());
            File file = new File(notesFileDB_Pojo.getNotesFileLocation());
            if (!notesFileDB_Pojo.getNotesFileLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                String str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES + notesFolderInfoFromDatabase.getNotesFolderName();
                File file2 = new File(str);
                File file3 = new File(str + File.separator + notesFileDB_Pojo.getNotesFileName() + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                try {
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String RecoveryEntryFile = Utilities.RecoveryEntryFile(this.context, file, file3);
                    if (RecoveryEntryFile.length() > 0) {
                        notesFileDB_Pojo.setNotesFileLocation(RecoveryEntryFile);
                        notesFolderInfoFromDatabase.setNotesFolderLocation(str);
                        NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
                        this.constants.getClass();
                        notesFolderDAL2.updateNotesFolderLocationInDatabase(notesFolderInfoFromDatabase, "NotesFolderId", String.valueOf(notesFolderInfoFromDatabase.getNotesFolderId()));
                        NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
                        this.constants.getClass();
                        notesFilesDAL.updateNotesFileLocationInDatabase(notesFileDB_Pojo, "NotesFileId", String.valueOf(notesFileDB_Pojo.getNotesFileId()));
                    }
                } catch (IOException e) {
                    Log.e("Note Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        DataTransferprefsEditor.putBoolean("isNotesTransferComplete", z);
        DataTransferprefsEditor.commit();
    }

    public void WalletMove() {
        this.walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        boolean z = true;
        for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : this.walletEntriesList) {
            StorageOptionsCommon.IsApphasDataforTransfer = true;
            WalletCategoriesDAL walletCategoriesDAL = this.walletCategoriesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileId");
            sb.append(" = ");
            sb.append(walletEntryFileDB_Pojo.getCategoryId());
            WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString());
            File file = new File(walletEntryFileDB_Pojo.getEntryFileLocation());
            if (!walletEntryFileDB_Pojo.getEntryFileLocation().contains(StorageOptionsCommon.STORAGEPATH)) {
                String str = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.WALLET + categoryInfoFromDatabase.getCategoryFileName();
                File file2 = new File(str);
                File file3 = new File(str + File.separator + StorageOptionsCommon.ENTRY + walletEntryFileDB_Pojo.getEntryFileName() + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                try {
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String RecoveryEntryFile = Utilities.RecoveryEntryFile(this.context, file, file3);
                    if (RecoveryEntryFile.length() > 0) {
                        walletEntryFileDB_Pojo.setEntryFileLocation(RecoveryEntryFile);
                        WalletEntriesDAL walletEntriesDAL = this.walletEntriesDAL;
                        this.constants.getClass();
                        walletEntriesDAL.updateEntryLocationInDatabase(walletEntryFileDB_Pojo, "WalletEntryFileId", String.valueOf(walletEntryFileDB_Pojo.getEntryFileId()));
                    }
                } catch (IOException e) {
                    Log.e("Note Move Exception", e.getMessage(), e);
                    e.printStackTrace();
                    z = false;
                }
            }
        }
        DataTransferprefsEditor.putBoolean("isNotesTransferComplete", z);
        DataTransferprefsEditor.commit();
    }
}
