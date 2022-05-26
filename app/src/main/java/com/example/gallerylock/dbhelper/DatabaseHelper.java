package com.example.gallerylock.dbhelper;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gallerylock.common.Constants;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.io.IOException;

/* loaded from: classes2.dex */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_AUDIO = "CREATE TABLE [tbl_Audio] ([Id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[AudioName] TEXT  NULL,[FlAudioLocation] TEXT  NOT NULL,[OriginalAudioLocation] TEXT  NULL,[PlayListId] INTEGER NOT NULL,[IsFakeAccount] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[FileSize] INTEGER NULL ,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_AUDIO_PLAYLIST = "CREATE TABLE [tblAudioPlayList] ([Id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[PlayListName] TEXT  NOT NULL,[FlPlayListLocation] TEXT  NOT NULL,[IsFakeAccount] INTEGER NULL,[SortBy] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_BOOKMARK = "CREATE TABLE [tbl_Bookmark] ([Id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[Url] TEXT  NULL,[CreateDate] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL, [IsFakeAccount] INTEGER  NULL);";
    public static final String CREATE_TABLE_BROWSERHISTORY = "CREATE TABLE [tbl_BrowserHistory] ([Id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[Url] TEXT  NULL,[CreateDate] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL, [IsFakeAccount] INTEGER  NULL);";
    public static final String CREATE_TABLE_DOCUMENTS = "CREATE TABLE [tbl_documents] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[document_name] TEXT  NULL,[fl_document_location] TEXT  NOT NULL,[original_document_location] TEXT  NULL,[folder_id] INTEGER NOT NULL,[IsFakeAccount] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[FileSize] INTEGER NULL ,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_DOCUMENTS_FOLDERS = "CREATE TABLE [tbl_document_folders] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[folder_name] TEXT  NOT NULL,[fl_folder_location] TEXT  NOT NULL,[IsFakeAccount] INTEGER NULL,[SortBy] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_DOWNLOADFILE = "CREATE TABLE [tbl_DownloadFile] ([Id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[FileDownloadPath] TEXT  NULL,[FileName] TEXT  NULL,[ReferenceId] TEXT  NULL,[Status] INTEGER  NULL,[DownloadFileUrl] TEXT  NULL,[DownloadType] INTEGER  NULL,[CreateDate] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL, [IsFakeAccount] INTEGER  NULL);";
    public static final String CREATE_TABLE_LOCK_APPS = "CREATE TABLE [tbl_lock_apps] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[app_name] TEXT NULL,[app_package_name] TEXT  NOT NULL,[lock_type] INTEGER NOT NULL,[IsFakeAccount] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL);";
    public static final String CREATE_TABLE_PHOTOS = "CREATE TABLE [tbl_photos] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[photo_name] TEXT NULL,[fl_photo_location] TEXT NOT NULL,[original_photo_location] TEXT NOT NULL,[album_id] INTEGER NOT NULL,[IsFakeAccount] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[FileSize] INTEGER NULL ,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_PHOTOS_ALBUMS = "CREATE TABLE [tbl_photo_albums] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[album_name] TEXT NULL,[fl_album_location] TEXT NULL,[IsFakeAccount] INTEGER NULL,[SortBy] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_VIDEOS = "CREATE TABLE [tbl_videos] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[video_name] TEXT NULL,[fl_video_location] TEXT  NOT NULL,[original_video_location] TEXT  NOT NULL,[thumbnail_video_location] TEXT  NOT NULL,[album_id] INTEGER NOT NULL,[IsFakeAccount] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[FileSize] INTEGER NULL ,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String CREATE_TABLE_VIDEOS_ALBUMS = "CREATE TABLE [tbl_video_albums] ([_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,[album_name] TEXT  NOT NULL,[fl_album_location] TEXT NULL,[IsFakeAccount] INTEGER NULL,[SortBy] INTEGER NULL,[CreatedTime] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,[ModifiedDateTime] TIMESTAMP NULL );";
    public static final String DATABASE_NAME = "calculator_sharp.db";
    public static final int DATABASE_VERSION = 2;
    public static final String INSERT_AUDIO_PLAYLIST;
    Constants constants;
    Context context;
    Resources res;
    public static final String INSERT_PHOTO_ALBUM = "INSERT INTO tbl_photo_albums ( album_name, fl_album_location, IsFakeAccount, SortBy, ModifiedDateTime) VALUES ( '" + StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM + "', '" + StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.PHOTOS + StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM + "', 0, 0,'" + Utilities.getCurrentDateTime() + "')";
    public static final String INSERT_VIDEO_ALBUM = "INSERT INTO tbl_video_albums ( album_name, fl_album_location, IsFakeAccount ,SortBy, ModifiedDateTime ) VALUES ( '" + StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM + "', '" + StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.VIDEOS + StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM + "', 0, 0,'" + Utilities.getCurrentDateTime() + "')";
    public static final String INSERT_DOCUMENT_FOLDER = "INSERT INTO tbl_document_folders ( folder_name, fl_folder_location, IsFakeAccount ,SortBy, ModifiedDateTime ) VALUES ( '" + StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM + "', '" + StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.DOCUMENTS + StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM + "', 0, 0,'" + Utilities.getCurrentDateTime() + "')";

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO tblAudioPlayList ( PlayListName, FlPlayListLocation, IsFakeAccount , SortBy, ModifiedDateTime  ) VALUES ( 'My Playlist', '");
        sb.append(StorageOptionsCommon.STORAGEPATH_1);
        sb.append(StorageOptionsCommon.AUDIOS);
        sb.append("My Playlist', 0,0,'");
        sb.append(Utilities.getCurrentDateTime());
        sb.append("')");
        INSERT_AUDIO_PLAYLIST = sb.toString();
    }

    public DatabaseHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
        this.context = context;
        this.constants = new Constants();
    }

    public DatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, 2);
        this.context = context;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(CREATE_TABLE_PHOTOS_ALBUMS);
        sQLiteDatabase.execSQL(CREATE_TABLE_PHOTOS);
        sQLiteDatabase.execSQL(CREATE_TABLE_VIDEOS_ALBUMS);
        sQLiteDatabase.execSQL(CREATE_TABLE_VIDEOS);
        sQLiteDatabase.execSQL(CREATE_TABLE_DOCUMENTS_FOLDERS);
        sQLiteDatabase.execSQL(CREATE_TABLE_DOCUMENTS);
        sQLiteDatabase.execSQL(CREATE_TABLE_AUDIO);
        sQLiteDatabase.execSQL(CREATE_TABLE_AUDIO_PLAYLIST);
        this.constants.getClass();
        sQLiteDatabase.execSQL("CREATE TABLE [TableWalletCategories]( [WalletCategoriesFileId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [WalletCategoriesFileName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [WalletCategoriesFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [WalletCategoriesFileCreatedDate] \t\t\tTIMESTAMP NOT NULL  \t\t\t\t\t\t ,  [WalletCategoriesFileModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [WalletCategoriesFileIconIndex] \t\t\tINTEGER  NOT  NULL  \t\t\t\t\t\t ,  [WalletCategoriesFileSortBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [WalletCategoriesFileIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t )");
        this.constants.getClass();
        sQLiteDatabase.execSQL("CREATE TABLE [TableWalletEntries]( [WalletEntryFileId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [WalletCategoriesFileId] \t\t\tINTEGER   NOT NULL\t\t\t\t\t\t\t ,  [WalletEntryFileName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [WalletEntryFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [WalletEntryFileCreatedDate] \t\t\tTIMESTAMP NOT NULL  \t\t\t\t\t\t ,  [WalletEntryFileModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [WalletCategoriesFileIconIndex] \t\t\tINTEGER  NOT  NULL  \t\t\t\t\t\t ,  [WalletEntryFilesSortBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [WalletEntryFileIsDecoy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  FOREIGN KEY(WalletCategoriesFileId) \t\t\tREFERENCES TableWalletCategories(WalletCategoriesFileId))");
        this.constants.getClass();
        sQLiteDatabase.execSQL("CREATE TABLE [TableToDo]( [ToDoId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [ToDoName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [ToDoFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [ToDoTask1] \t\t\tTEXT\t  \t  NULL\t\t\t\t\t\t\t ,  [ToDoTask1IsChecked] \t\t\tBOOLEAN \t  NULL \t\t\t\t\t\t\t ,  [ToDoTask2] \t\t\tTEXT \t  \t  NULL \t\t\t\t\t\t\t ,  [ToDoTask2IsChecked] \t\t\tBOOLEAN\t\t  NULL\t\t\t\t\t\t\t ,  [ToDoCreatedDate] \t\t\tTIMESTAMP NOT NULL \t\t\t\t\t\t\t ,  [ToDoModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [ToDoColor] \t\t\tTEXT \t  NOT NULL \t\t\t\t\t\t\t ,  [ToDoIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [ToDoFinished] \t\t\tBOOLEAN \t  NULL \t\t\t\t     \t \t)");
        this.constants.getClass();
        sQLiteDatabase.execSQL("CREATE TABLE [TableNotesFolder]( [NotesFolderId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [NotesFolderName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [NotesFolderLocation] \t\t\tTEXT \t  NOT NULL \t\t\t\t\t\t\t ,  [NotesFolderCreatedDate] \t\t\tTIMESTAMP NOT NULL  \t\t\t\t\t\t ,  [NotesFolderModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [NotesFolderSortBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [NotesFolderIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [NotesFolderViewBy] \t\t\tINTEGER \t  NULL  \t\t\t\t\t\t ,  [NotesFolderColor] \t\t\tTEXT \t\t  NULL  \t\t\t\t\t\t )");
        this.constants.getClass();
        sQLiteDatabase.execSQL("CREATE TABLE [TableNotesFile]( [NotesFileId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [NotesFolderId] \t\t\tINTEGER   NOT NULL                           ,  [NotesFileName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [NotesFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [NotesFileCreatedDate] \t\t\tTIMESTAMP NOT NULL \t\t\t\t\t\t\t ,  [NotesFileModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [NotesFileFromCloud] \t\t\tINTEGER\t  NOT NULL\t\t\t\t\t\t\t ,  [NotesFileSize] \t\t\tREAL \t      NULL\t\t\t\t\t\t     ,  [NotesFileText] \t\t\tTEXT \t      NULL\t\t\t\t\t\t     ,  [NotesFileIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [NotesFileColor] \t\t\tTEXT \t      NULL\t\t\t\t\t\t     ,  FOREIGN KEY(NotesFolderId) \t\t\tREFERENCES TableNotesFolder(NotesFolderId))");
        sQLiteDatabase.execSQL(CREATE_TABLE_DOWNLOADFILE);
        sQLiteDatabase.execSQL(CREATE_TABLE_BROWSERHISTORY);
        sQLiteDatabase.execSQL(CREATE_TABLE_BOOKMARK);
        sQLiteDatabase.execSQL(CREATE_TABLE_LOCK_APPS);
        Utilities.CheckDeviceStoragePaths(this.context);
        File file = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.PHOTOS + StorageOptionsCommon.PHOTOS_DEFAULT_ALBUM);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.VIDEOS + StorageOptionsCommon.VIDEOS_DEFAULT_ALBUM);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.DOCUMENTS + StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        File file4 = new File(StorageOptionsCommon.STORAGEPATH_1 + StorageOptionsCommon.AUDIOS + StorageOptionsCommon.AUDIOS_DEFAULT_ALBUM);
        if (!file4.exists()) {
            file4.mkdirs();
        }
        sQLiteDatabase.execSQL(INSERT_PHOTO_ALBUM);
        sQLiteDatabase.execSQL(INSERT_VIDEO_ALBUM);
        sQLiteDatabase.execSQL(INSERT_DOCUMENT_FOLDER);
        sQLiteDatabase.execSQL(INSERT_AUDIO_PLAYLIST);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        File[] listFiles;
        if (i2 == 2) {
            this.constants.getClass();
            sQLiteDatabase.execSQL("CREATE TABLE [TableToDo]( [ToDoId] \t\t\tINTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT ,  [ToDoName] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t     ,  [ToDoFileLocation] \t\t\tTEXT \t  NOT NULL\t\t\t\t\t\t\t ,  [ToDoTask1] \t\t\tTEXT\t  \t  NULL\t\t\t\t\t\t\t ,  [ToDoTask1IsChecked] \t\t\tBOOLEAN \t  NULL \t\t\t\t\t\t\t ,  [ToDoTask2] \t\t\tTEXT \t  \t  NULL \t\t\t\t\t\t\t ,  [ToDoTask2IsChecked] \t\t\tBOOLEAN\t\t  NULL\t\t\t\t\t\t\t ,  [ToDoCreatedDate] \t\t\tTIMESTAMP NOT NULL \t\t\t\t\t\t\t ,  [ToDoModifiedDate] \t\t\tTIMESTAMP \t  NULL\t\t\t\t\t\t\t ,  [ToDoColor] \t\t\tTEXT \t  NOT NULL \t\t\t\t\t\t\t ,  [ToDoIsDecoy] \t\t\tINTEGER\t\t  NULL\t\t\t\t\t\t\t ,  [ToDoFinished] \t\t\tBOOLEAN \t  NULL \t\t\t\t     \t \t)");
            sQLiteDatabase.execSQL("ALTER TABLE [TableNotesFolder] ADD COLUMN NotesFolderColor TEXT null;");
            sQLiteDatabase.execSQL("ALTER TABLE [TableNotesFile] ADD COLUMN NotesFileColor TEXT null;");
            sQLiteDatabase.execSQL("ALTER TABLE [TableNotesFolder] ADD COLUMN NotesFolderViewBy INTEGER null;");
            sQLiteDatabase.execSQL("ALTER TABLE [tbl_Audio] ADD COLUMN PlayListId INTEGER NULL;");
            sQLiteDatabase.execSQL("UPDATE [tbl_Audio] SET PlayListId = 1;");
            File file = new File(this.context.getFilesDir() + "/audio/My Playlist");
            if (!file.exists()) {
                file.mkdirs();
            }
            for (File file2 : new File(this.context.getFilesDir() + "/audio/").listFiles()) {
                if (file2.isFile()) {
                    try {
                        Utilities.MoveFileWithinDirectories(file2.getAbsolutePath(), file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("onUpgrade DB", "upgraded from " + i + " to " + i2);
        }
    }
}
