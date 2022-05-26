package com.example.gallerylock.securebackupcloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.example.gallerylock.audio.AudioDAL;
import com.example.gallerylock.audio.AudioEnt;
import com.example.gallerylock.audio.AudioPlayListDAL;
import com.example.gallerylock.audio.AudioPlayListEnt;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.documents.DocumentDAL;
import com.example.gallerylock.documents.DocumentFolder;
import com.example.gallerylock.documents.DocumentFolderDAL;
import com.example.gallerylock.documents.DocumentsEnt;
import com.example.gallerylock.dropbox.CloudService;
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
import com.example.gallerylock.todolist.ToDoDAL;
import com.example.gallerylock.todolist.ToDoDB_Pojo;
import com.example.gallerylock.todolist.ToDoPojo;
import com.example.gallerylock.todolist.ToDoReadFromXML;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoAlbum;
import com.example.gallerylock.video.VideoAlbumDAL;
import com.example.gallerylock.video.VideoDAL;
import com.example.gallerylock.wallet.WalletCategoriesDAL;
import com.example.gallerylock.wallet.WalletCategoriesFileDB_Pojo;
import com.example.gallerylock.wallet.WalletCommon;
import com.example.gallerylock.wallet.WalletEntriesDAL;
import com.example.gallerylock.wallet.WalletEntryFileDB_Pojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

/* loaded from: classes2.dex */
public class DropBoxDownloadFile extends AsyncTask<Void, Long, Boolean> {
    private BackupCloudEnt backupCloudEnt;
    private Context context;
    private String downloadFilePath;
    private int downloadType;
    private String localFilePath;
    private DbxClientV2 mApi;
    private String mErrorMsg;
    private FileOutputStream mFos;

    /* JADX INFO: Access modifiers changed from: protected */
    public void onProgressUpdate(Long... lArr) {
    }

    public DropBoxDownloadFile(Context context, DbxClientV2 dbxClientV2, String str, String str2, int i, BackupCloudEnt backupCloudEnt) {
        this.context = context;
        this.mApi = dbxClientV2;
        this.localFilePath = str2;
        this.downloadFilePath = str;
        this.downloadType = i;
        this.backupCloudEnt = backupCloudEnt;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Boolean doInBackground(Void... voidArr) {
        try {
            File file = new File(this.localFilePath);
            try {
                if (!file.exists()) {
                    file.mkdirs();
                }
                String name = new File(this.downloadFilePath).getName();
                if (!(this.downloadType == CloudCommon.DropboxType.Notes.ordinal() || this.downloadType == CloudCommon.DropboxType.Wallet.ordinal())) {
                    name = Utilities.ChangeFileExtention(name);
                }
                this.localFilePath += "/" + name;
                new File(this.localFilePath).createNewFile();
                try {
                    this.mFos = new FileOutputStream(this.localFilePath);
                    this.mApi.files().download(this.downloadFilePath).download(this.mFos);
                } catch (DbxException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return true;
            } catch (IOException e3) {
                e3.printStackTrace();
                return false;
            }
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostExecute(Boolean bool) {
        try {
            if (bool.booleanValue()) {
                if (this.downloadType == CloudCommon.DropboxType.Photos.ordinal()) {
                    Utilities.NSEncryption(new File(this.localFilePath));
                    AddPhotoToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == CloudCommon.DropboxType.Videos.ordinal()) {
                    AddVideoToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == CloudCommon.DropboxType.Documents.ordinal()) {
                    Utilities.NSEncryption(new File(this.localFilePath));
                    AddDocumentToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == CloudCommon.DropboxType.Notes.ordinal()) {
                    AddNoteToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == CloudCommon.DropboxType.Wallet.ordinal()) {
                    AddWalletToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                } else if (this.downloadType == CloudCommon.DropboxType.ToDo.ordinal()) {
                    Utilities.NSEncryption(new File(this.localFilePath));
                    AddToDoInDatabase(new File(this.downloadFilePath).getName(), this.localFilePath);
                } else if (this.downloadType == CloudCommon.DropboxType.Audio.ordinal()) {
                    AddMusicToDatabase(new File(this.downloadFilePath).getName(), this.localFilePath, new File(this.localFilePath).getParentFile().getName());
                }
                int indexOf = CloudService.CloudBackupCloudEntList.indexOf(this.backupCloudEnt);
                Hashtable<String, Boolean> GetDownloadPath = this.backupCloudEnt.GetDownloadPath();
                if (GetDownloadPath.containsKey(this.downloadFilePath)) {
                    GetDownloadPath.put(this.downloadFilePath, true);
                    this.backupCloudEnt.SetDownloadPath(GetDownloadPath);
                    CloudService.CloudBackupCloudEntList.set(indexOf, this.backupCloudEnt);
                    return;
                }
                return;
            }
            showToast(this.mErrorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String str) {
        Toast.makeText(this.context, str, 1).show();
    }

    private void AddPhotoToDatabase(String str, String str2, String str3) {
        PhotoAlbumDAL photoAlbumDAL = new PhotoAlbumDAL(this.context);
        photoAlbumDAL.OpenWrite();
        PhotoAlbum GetAlbum = photoAlbumDAL.GetAlbum(str3);
        int id = GetAlbum.getId();
        if (GetAlbum.getId() == 0) {
            PhotoAlbum photoAlbum = new PhotoAlbum();
            photoAlbum.setAlbumName(str3);
            photoAlbum.setAlbumLocation(new File(str2).getParent());
            photoAlbumDAL.AddPhotoAlbum(photoAlbum);
            id = photoAlbumDAL.GetLastAlbumId();
        }
        Photo photo = new Photo();
        photo.setPhotoName(str);
        photo.setFolderLockPhotoLocation(str2);
        photo.setOriginalPhotoLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + Common.UnhideKitkatAlbumName + str);
        photo.setAlbumId(id);
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
            photoAlbumDAL.close();
        }
    }

    private void AddVideoToDatabase(String str, String str2, String str3) {
        String str4;
        FileOutputStream fileOutputStream;
        new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + str3 + "/VideoThumnails/").mkdir();
        if (str.contains("#")) {
            str4 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + str3 + "/VideoThumnails/thumbnil-" + str.substring(0, str.lastIndexOf("#")) + "#jpg";
        } else {
            str4 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + str3 + "/VideoThumnails/thumbnil-" + str.substring(0, str.lastIndexOf(".")) + "#jpg";
        }
        File file = new File(str4);
        Bitmap bitmap = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileOutputStream = null;
        }
        File file2 = new File(str2);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(file2.getAbsolutePath());
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000L, 3);
        } catch (Exception unused) {
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (Exception unused2) {
        }
        try {
            fileOutputStream.flush();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            Utilities.NSEncryption(file);
            Utilities.NSEncryption(file2);
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(this.context);
        videoAlbumDAL.OpenWrite();
        VideoAlbum GetAlbum = videoAlbumDAL.GetAlbum(str3);
        int id = GetAlbum.getId();
        if (GetAlbum.getId() == 0) {
            VideoAlbum videoAlbum = new VideoAlbum();
            videoAlbum.setAlbumName(str3);
            videoAlbum.setAlbumLocation(new File(str2).getParent());
            videoAlbumDAL.AddVideoAlbum(videoAlbum);
            id = videoAlbumDAL.GetLastAlbumId();
        }
        Video video = new Video();
        video.setVideoName(str);
        video.setFolderLockVideoLocation(str2);
        video.setOriginalVideoLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + Common.UnhideKitkatAlbumName + str);
        video.setthumbnail_video_location(str4);
        video.setAlbumId(id);
        VideoDAL videoDAL = new VideoDAL(this.context);
        try {
            try {
                videoDAL.OpenWrite();
                videoDAL.AddVideos(video);
            } finally {
                videoDAL.close();
            }
        } catch (Exception e5) {
            System.out.println(e5.getMessage());
        }
    }

    private void AddDocumentToDatabase(String str, String str2, String str3) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
        documentFolderDAL.OpenWrite();
        DocumentFolder GetFolder = documentFolderDAL.GetFolder(str3);
        int id = GetFolder.getId();
        if (GetFolder.getId() == 0) {
            DocumentFolder documentFolder = new DocumentFolder();
            documentFolder.setFolderName(str3);
            documentFolder.setFolderLocation(new File(str2).getParent());
            documentFolderDAL.AddDocumentFolder(documentFolder);
            id = documentFolderDAL.GetLastFolderId();
        }
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setFolderLockDocumentLocation(str2);
        documentsEnt.setOriginalDocumentLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + Common.UnhideKitkatAlbumName + str);
        documentsEnt.setFolderId(id);
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
            documentFolderDAL.close();
        }
    }

    private void AddNoteToDatabase(String str, String str2, String str3) {
        NotesFolderDAL notesFolderDAL = new NotesFolderDAL(this.context);
        NotesFilesDAL notesFilesDAL = new NotesFilesDAL(this.context);
        new Constants();
        new HashMap();
        ReadNoteFromXML readNoteFromXML = new ReadNoteFromXML();
        NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
        long length = new File(str2).length();
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES + str3);
        String substring = str.substring(0, str.lastIndexOf("."));
        String str4 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE NotesFolderName = '" + str3 + "' AND NotesFolderIsDecoy = " + SecurityLocksCommon.IsFakeAccount;
        if (!file.exists()) {
            file.mkdirs();
        }
        HashMap<String, String> ReadNote = readNoteFromXML.ReadNote(str2);
        if (ReadNote != null) {
            if (!notesFolderDAL.IsFolderAlreadyExist(str4)) {
                notesFolderDB_Pojo.setNotesFolderName(str3);
                notesFolderDB_Pojo.setNotesFolderLocation(file.getAbsolutePath());
                notesFolderDB_Pojo.setNotesFolderCreatedDate(ReadNote.get("note_datetime_c"));
                notesFolderDB_Pojo.setNotesFolderModifiedDate(ReadNote.get("note_datetime_m"));
                notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
                notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
            }
            new NotesFolderDB_Pojo();
            NotesFolderDB_Pojo notesFolderInfoFromDatabase = notesFolderDAL.getNotesFolderInfoFromDatabase(str4);
            NotesFileDB_Pojo notesFileDB_Pojo = new NotesFileDB_Pojo();
            notesFileDB_Pojo.setNotesFileFolderId(notesFolderInfoFromDatabase.getNotesFolderId());
            notesFileDB_Pojo.setNotesFileName(substring);
            notesFileDB_Pojo.setNotesFileCreatedDate(ReadNote.get("note_datetime_c"));
            notesFileDB_Pojo.setNotesFileModifiedDate(ReadNote.get("note_datetime_m"));
            notesFileDB_Pojo.setNotesFileLocation(str2);
            notesFileDB_Pojo.setNotesFileFromCloud(1);
            notesFileDB_Pojo.setNotesFileSize(length);
            notesFileDB_Pojo.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            notesFilesDAL.addNotesFilesInfoInDatabase(notesFileDB_Pojo);
        }
    }

    private void AddWalletToDatabase(String str, String str2, String str3) {
        WalletEntriesDAL walletEntriesDAL = new WalletEntriesDAL(this.context);
        WalletCategoriesDAL walletCategoriesDAL = new WalletCategoriesDAL(this.context);
        new Constants();
        WalletCommon walletCommon = new WalletCommon();
        WalletEntryFileDB_Pojo walletEntryFileDB_Pojo = new WalletEntryFileDB_Pojo();
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        String fileNameWithoutExtention = Utilities.getFileNameWithoutExtention(str);
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.WALLET + str3);
        String str4 = "SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE WalletCategoriesFileIsDecoy = " + SecurityLocksCommon.IsFakeAccount + " AND WalletCategoriesFileName = '" + str3 + "'";
        if (!file.exists()) {
            file.mkdirs();
        }
        String currentDate = walletCommon.getCurrentDate();
        if (!walletCategoriesDAL.IsWalletCategoryAlreadyExist(str4)) {
            walletCategoriesFileDB_Pojo.setCategoryFileName(str3);
            walletCategoriesFileDB_Pojo.setCategoryFileLocation(file.getAbsolutePath());
            walletCategoriesFileDB_Pojo.setCategoryFileCreatedDate(currentDate);
            walletCategoriesFileDB_Pojo.setCategoryFileModifiedDate(currentDate);
            walletCategoriesFileDB_Pojo.setCategoryFileSortBy(1);
            walletCategoriesFileDB_Pojo.setCategoryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
            walletCategoriesDAL.addWalletCategoriesInfoInDatabase(walletCategoriesFileDB_Pojo);
        }
        new WalletCategoriesFileDB_Pojo();
        WalletCategoriesFileDB_Pojo categoryInfoFromDatabase = walletCategoriesDAL.getCategoryInfoFromDatabase(str4);
        walletEntryFileDB_Pojo.setCategoryId(categoryInfoFromDatabase.getCategoryFileId());
        walletEntryFileDB_Pojo.setEntryFileName(fileNameWithoutExtention);
        walletEntryFileDB_Pojo.setEntryFileCreatedDate(currentDate);
        walletEntryFileDB_Pojo.setEntryFileModifiedDate(currentDate);
        walletEntryFileDB_Pojo.setEntryFileLocation(str2);
        walletEntryFileDB_Pojo.setCategoryFileIconIndex(categoryInfoFromDatabase.getCategoryFileIconIndex());
        walletEntryFileDB_Pojo.setEntryFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        walletEntriesDAL.addWalletEntriesInfoInDatabase(walletEntryFileDB_Pojo);
    }

    private void AddToDoInDatabase(String str, String str2) {
        new SimpleDateFormat("EEE, dd MMM yyyy hh:edit_share_btn:ss +0000", Locale.getDefault());
        String str3 = str.split(StorageOptionsCommon.NOTES_FILE_EXTENSION)[0];
        ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST);
        if (!file.exists()) {
            file.mkdirs();
        }
        String str4 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + str3 + StorageOptionsCommon.NOTES_FILE_EXTENSION;
        ToDoPojo ReadToDoList = toDoReadFromXML.ReadToDoList(str4);
        ToDoDB_Pojo toDoDB_Pojo = new ToDoDB_Pojo();
        toDoDB_Pojo.setToDoFileName(str3);
        toDoDB_Pojo.setToDoFileLocation(str4);
        toDoDB_Pojo.setToDoFileColor(ReadToDoList.getTodoColor());
        toDoDB_Pojo.setToDoFileCreatedDate(ReadToDoList.getDateCreated());
        toDoDB_Pojo.setToDoFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
        toDoDB_Pojo.setToDoFileTask1(ReadToDoList.getToDoList().get(0).getToDo());
        toDoDB_Pojo.setToDoFileTask1IsChecked(ReadToDoList.getToDoList().get(0).isChecked());
        if (ReadToDoList.getToDoList().size() >= 2) {
            toDoDB_Pojo.setToDoFileTask2(ReadToDoList.getToDoList().get(1).getToDo());
            toDoDB_Pojo.setToDoFileTask2IsChecked(ReadToDoList.getToDoList().get(1).isChecked());
        }
        new ToDoDAL(this.context).addToDoInfoInDatabase(toDoDB_Pojo);
    }

    private void AddMusicToDatabase(String str, String str2, String str3) {
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(this.context);
        audioPlayListDAL.OpenWrite();
        AudioPlayListEnt GetPlayList = audioPlayListDAL.GetPlayList(str3);
        int id = GetPlayList.getId();
        if (GetPlayList.getId() == 0) {
            AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
            audioPlayListEnt.setPlayListName(str3);
            audioPlayListEnt.setPlayListLocation(new File(str2).getParent());
            audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
            id = audioPlayListDAL.GetLastPlayListId();
        }
        AudioEnt audioEnt = new AudioEnt();
        audioEnt.setAudioName(str);
        audioEnt.setFolderLockAudioLocation(str2);
        audioEnt.setOriginalAudioLocation(Environment.getExternalStorageDirectory().getAbsolutePath() + Common.UnhideKitkatAlbumName + str);
        audioEnt.setPlayListId(id);
        AudioDAL audioDAL = new AudioDAL(this.context);
        try {
            try {
                audioDAL.OpenWrite();
                audioDAL.AddAudio(audioEnt, str2);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioDAL.close();
            audioPlayListDAL.close();
        }
    }
}
