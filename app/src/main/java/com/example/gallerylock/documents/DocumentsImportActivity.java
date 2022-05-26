package com.example.gallerylock.documents;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.FileUtils;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class DocumentsImportActivity extends Activity implements AccelerometerListener, SensorEventListener {
    public ProgressBar Progress;
    private FoldersImportAdapter adapter;
    ListView album_import_ListView;
    AppCompatImageView btnSelectAll;
    private int count;
    private DocumentSystemFileAdapter filesAdapter;
    int folderId;
    String folderName;
    String folderPath;
    GridView imagegrid;
    TextView lbl_import_photo_album_topbaar;
    TextView lbl_photo_video_empty;
    LinearLayout ll_Import_bottom_baar;
    LinearLayout ll_background;
    LinearLayout ll_photo_video_empty;
    LinearLayout ll_topbaar;
    ImageView photo_video_empty_icon;
    int selectCount;
    private SensorManager sensorManager;
    List<ImportAlbumEnt> importAlbumEnts = new ArrayList();
    private ArrayList<DocumentsEnt> fileImportEntList = new ArrayList<>();
    ArrayList<String> spinnerValues = new ArrayList<>();
    private ArrayList<DocumentsEnt> fileImportEntListShow = new ArrayList<>();
    boolean isAlbumClick = false;
    private boolean IsExceptionInImportPhotos = false;
    List<List<DocumentsEnt>> fileImportEntListShowList = new ArrayList();
    private ArrayList<String> selectPath = new ArrayList<>();
    ProgressDialog myProgressDialog = null;
    Context context = this;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Intent intent;
            if (message.what == 1) {
                DocumentsImportActivity.this.hideProgress();
                if (DocumentsImportActivity.this.fileImportEntListShow.size() > 0) {
                    DocumentsImportActivity documentsImportActivity = DocumentsImportActivity.this;
                    DocumentsImportActivity documentsImportActivity2 = DocumentsImportActivity.this;
                    documentsImportActivity.filesAdapter = new DocumentSystemFileAdapter(documentsImportActivity2, 1, documentsImportActivity2.fileImportEntListShow);
                    DocumentsImportActivity.this.imagegrid.setAdapter((ListAdapter) DocumentsImportActivity.this.filesAdapter);
                } else {
                    DocumentsImportActivity.this.btnSelectAll.setEnabled(false);
                    DocumentsImportActivity.this.ll_Import_bottom_baar.setEnabled(false);
                }
            } else if (message.what == 3) {
                if (Common.IsImporting) {
                    if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
                        DocumentsImportActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(Constants.FILE + Environment.getExternalStorageDirectory())));
                    } else {
                        DocumentsImportActivity.this.RefershGalleryforKitkat();
                    }
                    Common.IsImporting = false;
                    if (DocumentsImportActivity.this.IsExceptionInImportPhotos) {
                        DocumentsImportActivity.this.IsExceptionInImportPhotos = false;
                    } else {
                        Toast.makeText(DocumentsImportActivity.this, DocumentsImportActivity.this.selectCount + " document(s) imported successfully", 0).show();
                    }
                    DocumentsImportActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        if (DocumentsImportActivity.this.isAlbumClick) {
                            intent = new Intent(DocumentsImportActivity.this, DocumentsActivity.class);
                        } else {
                            intent = new Intent(DocumentsImportActivity.this, DocumentsFolderActivity.class);
                        }
                        intent.addFlags(67108864);
                        DocumentsImportActivity.this.startActivity(intent);
                        DocumentsImportActivity.this.finish();
                    }
                }
                if (Common.InterstitialAdCount <= 1) {
                    Common.InterstitialAdCount++;
                }
            } else if (message.what == 2) {
                DocumentsImportActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RefershGalleryforKitkat() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.2
            @Override // android.media.MediaScannerConnection.OnScanCompletedListener
            public void onScanCompleted(String str, Uri uri) {
            }
        });
    }

    private void ShowImportProgress() {
        this.myProgressDialog = ProgressDialog.show(this, null, "Your data is being encrypted... this may take a few moments... ", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.myProgressDialog.dismiss();
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.import_album_list_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        Common.IsWorkInProgress = false;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.album_import_ListView = (ListView) findViewById(R.id.album_import_ListView);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        TextView textView = (TextView) findViewById(R.id.lbl_import_photo_album_topbaar);
        this.lbl_import_photo_album_topbaar = textView;
        textView.setText(R.string.lbl_import_photo_album_select_folder_topbaar);
        this.btnSelectAll = (AppCompatImageView) findViewById(R.id.btnSelectAll);
        this.ll_Import_bottom_baar = (LinearLayout) findViewById(R.id.ll_Import_bottom_baar);
        this.ll_photo_video_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.photo_video_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_photo_video_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.folderId = Common.FolderId;
        this.folderName = null;
        if (0 == 0) {
            this.folderId = Common.FolderId;
            DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this.context);
            documentFolderDAL.OpenRead();
            DocumentFolder GetFolderById = documentFolderDAL.GetFolderById(Integer.toString(Common.FolderId));
            documentFolderDAL.close();
            this.folderName = GetFolderById.getFolderName();
        }
        DocumentFileBind();
        GetFolders();
        this.Progress.setVisibility(8);
        Iterator<DocumentsEnt> it = this.fileImportEntList.iterator();
        while (it.hasNext()) {
            DocumentsEnt next = it.next();
            if (this.spinnerValues.get(0).contains(new File(next.getOriginalDocumentLocation()).getParent())) {
                this.fileImportEntListShow.add(next);
            }
        }
        this.ll_Import_bottom_baar.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DocumentsImportActivity.this.OnImportClick();
            }
        });
        this.album_import_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DocumentsImportActivity.this.isAlbumClick = true;
                DocumentsImportActivity.this.lbl_import_photo_album_topbaar.setText(R.string.lbl_import_doc_album_select_doc_topbaar);
                DocumentsImportActivity.this.album_import_ListView.setVisibility(4);
                DocumentsImportActivity.this.imagegrid.setVisibility(0);
                DocumentsImportActivity.this.btnSelectAll.setVisibility(0);
                DocumentsImportActivity.this.adapter = new FoldersImportAdapter(DocumentsImportActivity.this.context, 17367043, DocumentsImportActivity.this.importAlbumEnts, false);
                DocumentsImportActivity.this.album_import_ListView.setAdapter((ListAdapter) DocumentsImportActivity.this.adapter);
                DocumentsImportActivity.this.fileImportEntListShow.clear();
                Iterator it2 = DocumentsImportActivity.this.fileImportEntList.iterator();
                while (it2.hasNext()) {
                    DocumentsEnt documentsEnt = (DocumentsEnt) it2.next();
                    if (DocumentsImportActivity.this.spinnerValues.get(i).equals(new File(documentsEnt.getOriginalDocumentLocation()).getParent())) {
                        documentsEnt.GetFileCheck();
                        DocumentsImportActivity.this.fileImportEntListShow.add(documentsEnt);
                    }
                }
                DocumentsImportActivity.this.filesAdapter = new DocumentSystemFileAdapter(DocumentsImportActivity.this.context, 1, DocumentsImportActivity.this.fileImportEntListShow);
                DocumentsImportActivity.this.imagegrid.setAdapter((ListAdapter) DocumentsImportActivity.this.filesAdapter);
                DocumentsImportActivity.this.filesAdapter.notifyDataSetChanged();
                if (DocumentsImportActivity.this.fileImportEntListShow.size() <= 0) {
                    DocumentsImportActivity.this.album_import_ListView.setVisibility(4);
                    DocumentsImportActivity.this.imagegrid.setVisibility(4);
                    DocumentsImportActivity.this.btnSelectAll.setVisibility(4);
                    DocumentsImportActivity.this.ll_photo_video_empty.setVisibility(0);
                    DocumentsImportActivity.this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
                    DocumentsImportActivity.this.lbl_photo_video_empty.setText(R.string.no_docs);
                }
            }
        });
        DocumentSystemFileAdapter documentSystemFileAdapter = new DocumentSystemFileAdapter(this.context, 1, this.fileImportEntListShow);
        this.filesAdapter = documentSystemFileAdapter;
        this.imagegrid.setAdapter((ListAdapter) documentSystemFileAdapter);
    }

    private void DocumentFileBind() {
        Iterator<File> it = new FileUtils().FindFiles(new String[]{"doc", "pdf", "txt", "xlsx", "docx", "ppt", "pptx", "xls", "csv", "dbk", "dot", "dotx", "gdoc", "pdax", "pda", "rtf", "rpt", "uoml", "uof", "stw", "xps", "wrd", "wpt", "wps", "epub"}).iterator();
        while (it.hasNext()) {
            File next = it.next();
            DocumentsEnt documentsEnt = new DocumentsEnt();
            documentsEnt.SetFile(next);
            documentsEnt.setDocumentName(next.getName());
            documentsEnt.setOriginalDocumentLocation(next.getAbsolutePath());
            documentsEnt.SetFileCheck(false);
            this.fileImportEntList.add(documentsEnt);
            ImportAlbumEnt importAlbumEnt = new ImportAlbumEnt();
            if (this.spinnerValues.size() <= 0 || !this.spinnerValues.contains(next.getParent())) {
                importAlbumEnt.SetAlbumName(next.getParent());
                this.importAlbumEnts.add(importAlbumEnt);
                this.spinnerValues.add(next.getParent());
            }
        }
        if (this.fileImportEntList.size() <= 0) {
            this.btnSelectAll.setEnabled(false);
            this.ll_Import_bottom_baar.setEnabled(false);
        }
    }

    public void GetFolders() {
        FoldersImportAdapter foldersImportAdapter = new FoldersImportAdapter(this.context, 17367043, this.importAlbumEnts, false);
        this.adapter = foldersImportAdapter;
        this.album_import_ListView.setAdapter((ListAdapter) foldersImportAdapter);
        if (this.importAlbumEnts.size() <= 0) {
            this.album_import_ListView.setVisibility(4);
            this.imagegrid.setVisibility(4);
            this.btnSelectAll.setVisibility(4);
            this.ll_photo_video_empty.setVisibility(0);
            this.photo_video_empty_icon.setBackgroundResource(R.drawable.ic_video_empty_icon);
            this.lbl_photo_video_empty.setText(R.string.no_docs);
        }
    }

    int albumCheckCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
            if (this.importAlbumEnts.get(i2).GetAlbumFileCheck()) {
                i++;
            }
        }
        return i;
    }

    public void OnImportClick() {
        final StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectdocmsg_import, 0).show();
        } else if (Common.GetFileSize(this.selectPath) < Common.GetTotalFree()) {
            int albumCheckCount = albumCheckCount();
            if (albumCheckCount >= 2) {
                final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.confirmation_message_box);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
                TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
                textView.setTypeface(createFromAsset);
                textView.setText("Are you sure you want to import " + albumCheckCount + " folders? Importing may take time according to the size of your data.");
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                            DocumentsImportActivity.this.Import();
                        } else if (GetObject.GetSDCardUri().length() > 0) {
                            DocumentsImportActivity.this.Import();
                        } else if (!GetObject.GetISDAlertshow()) {
                            final Dialog dialog2 = new Dialog(DocumentsImportActivity.this, R.style.FullHeightDialog);
                            dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                            dialog2.setCancelable(false);
                            dialog2.setCanceledOnTouchOutside(false);
                            ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgDocs);
                            ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.5.1
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    GetObject.SetISDAlertshow(Boolean.valueOf(z));
                                }
                            });
                            ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.5.2
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view2) {
                                    dialog2.dismiss();
                                    DocumentsImportActivity.this.Import();
                                }
                            });
                            dialog2.show();
                        } else {
                            DocumentsImportActivity.this.Import();
                        }
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        for (int i = 0; i < DocumentsImportActivity.this.importAlbumEnts.size(); i++) {
                            DocumentsImportActivity.this.importAlbumEnts.get(i).SetAlbumFileCheck(false);
                        }
                        DocumentsImportActivity.this.adapter = new FoldersImportAdapter(DocumentsImportActivity.this.context, 17367043, DocumentsImportActivity.this.importAlbumEnts, false);
                        DocumentsImportActivity.this.album_import_ListView.setAdapter((ListAdapter) DocumentsImportActivity.this.adapter);
                        DocumentsImportActivity.this.adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 23) {
                Import();
            } else if (GetObject.GetSDCardUri().length() > 0) {
                Import();
            } else if (!GetObject.GetISDAlertshow()) {
                final Dialog dialog2 = new Dialog(this, R.style.FullHeightDialog);
                dialog2.setContentView(R.layout.sdcard_permission_alert_msgbox);
                dialog2.setCancelable(false);
                dialog2.setCanceledOnTouchOutside(false);
                ((TextView) dialog2.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.lblSdCardAlertMsgDocs);
                ((CheckBox) dialog2.findViewById(R.id.cbalertdialog)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.7
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        GetObject.SetISDAlertshow(Boolean.valueOf(z));
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.8
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        dialog2.dismiss();
                        DocumentsImportActivity.this.Import();
                    }
                });
                ((LinearLayout) dialog2.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.9
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
            } else {
                Import();
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity$10] */
    void Import() {
        SelectedCount();
        ShowImportProgress();
        Common.IsWorkInProgress = true;
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsImportActivity.10
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    DocumentsImportActivity.this.ImportDocuments();
                    Message message = new Message();
                    message.what = 3;
                    DocumentsImportActivity.this.handle.sendMessage(message);
                    Common.IsWorkInProgress = false;
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 3;
                    DocumentsImportActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    public void ImportDocuments() {
        if (this.isAlbumClick) {
            ImportOnlyDocumentsSDCard();
        } else {
            importFolder();
        }
    }

    void importFolder() {
        if (this.importAlbumEnts.size() > 0) {
            int i = 0;
            for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                if (this.importAlbumEnts.get(i2).GetAlbumFileCheck()) {
                    File file = new File(this.importAlbumEnts.get(i2).GetAlbumName());
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + file.getName());
                    this.folderName = file.getName();
                    if (file2.exists()) {
                        int i3 = 1;
                        while (i3 < 100) {
                            this.folderName = file.getName() + "(" + i3 + ")";
                            StringBuilder sb = new StringBuilder();
                            sb.append(StorageOptionsCommon.STORAGEPATH);
                            sb.append(StorageOptionsCommon.DOCUMENTS);
                            sb.append(this.folderName);
                            file2 = new File(sb.toString());
                            if (!file2.exists()) {
                                i3 = 100;
                            }
                            i3++;
                        }
                    }
                    AddFolderToDatabase(this.folderName);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
                    documentFolderDAL.OpenRead();
                    int GetLastFolderId = documentFolderDAL.GetLastFolderId();
                    this.folderId = GetLastFolderId;
                    Common.FolderId = GetLastFolderId;
                    documentFolderDAL.close();
                    ImportAlbumsDocumentsSDCard(i);
                    i++;
                }
            }
        }
    }

    void ImportAlbumsDocumentsSDCard(int i) {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        List<DocumentsEnt> list = this.fileImportEntListShowList.get(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (list.get(i2).GetFileCheck()) {
                File file = new File(list.get(i2).getOriginalDocumentLocation());
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + this.folderName + "/"));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (NSHideFile.length() > 0) {
                        AddDocumentToDatabase(FileName(list.get(i2).getOriginalDocumentLocation()), list.get(i2).getOriginalDocumentLocation(), NSHideFile);
                    }
                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
            }
        }
    }

    void ImportOnlyDocumentsSDCard() {
        Common.IsImporting = true;
        SecurityLocksCommon.IsAppDeactive = true;
        int size = this.fileImportEntListShow.size();
        for (int i = 0; i < size; i++) {
            if (this.fileImportEntListShow.get(i).GetFileCheck()) {
                File file = new File(this.fileImportEntListShow.get(i).getOriginalDocumentLocation());
                try {
                    String NSHideFile = Utilities.NSHideFile(this, file, new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + this.folderName + "/"));
                    Utilities.NSEncryption(new File(NSHideFile));
                    if (NSHideFile.length() > 0) {
                        AddDocumentToDatabase(FileName(this.fileImportEntListShow.get(i).getOriginalDocumentLocation()), this.fileImportEntListShow.get(i).getOriginalDocumentLocation(), NSHideFile);
                    }
                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23 && StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                        Utilities.DeleteSDcardImageLollipop(this, file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.IsExceptionInImportPhotos = true;
                    e.printStackTrace();
                }
            }
        }
    }

    void AddDocumentToDatabase(String str, String str2, String str3) {
        DocumentsEnt documentsEnt = new DocumentsEnt();
        documentsEnt.setDocumentName(str);
        documentsEnt.setFolderLockDocumentLocation(str3);
        documentsEnt.setOriginalDocumentLocation(str2);
        documentsEnt.setFolderId(this.folderId);
        DocumentDAL documentDAL = new DocumentDAL(this.context);
        try {
            try {
                documentDAL.OpenWrite();
                documentDAL.AddDocuments(documentsEnt, str3);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            documentDAL.close();
        }
    }

    public void AddFolderToDatabase(String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(str);
        documentFolder.setFolderLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + str);
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
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

    private boolean IsFileCheck() {
        for (int i = 0; i < this.importAlbumEnts.size(); i++) {
            if (this.importAlbumEnts.get(i).GetAlbumFileCheck()) {
                this.fileImportEntListShow = new ArrayList<>();
                Iterator<DocumentsEnt> it = this.fileImportEntList.iterator();
                while (it.hasNext()) {
                    DocumentsEnt next = it.next();
                    if (this.spinnerValues.get(i).equals(new File(next.getOriginalDocumentLocation()).getParent())) {
                        this.fileImportEntListShow.add(next);
                    }
                    for (int i2 = 0; i2 < this.fileImportEntListShow.size(); i2++) {
                        this.fileImportEntListShow.get(i2).SetFileCheck(true);
                    }
                }
                this.fileImportEntListShowList.add(this.fileImportEntListShow);
            }
        }
        this.selectPath.clear();
        for (int i3 = 0; i3 < this.fileImportEntListShow.size(); i3++) {
            if (this.fileImportEntListShow.get(i3).GetFileCheck()) {
                this.selectPath.add(this.fileImportEntListShow.get(i3).getOriginalDocumentLocation());
                return true;
            }
        }
        return false;
    }

    private void SelectedCount() {
        this.selectCount = 0;
        for (int i = 0; i < this.fileImportEntListShow.size(); i++) {
            if (this.fileImportEntListShow.get(i).GetFileCheck()) {
                this.selectCount++;
            }
        }
    }

    public void btnSelectAllonClick(View view) {
        SelectAllPhotos();
        DocumentSystemFileAdapter documentSystemFileAdapter = new DocumentSystemFileAdapter(this.context, 1, this.fileImportEntListShow);
        this.filesAdapter = documentSystemFileAdapter;
        this.imagegrid.setAdapter((ListAdapter) documentSystemFileAdapter);
        this.filesAdapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        Back();
    }

    private void SelectAllPhotos() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.fileImportEntListShow.size(); i++) {
                this.fileImportEntListShow.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setImageResource(R.drawable.ic_unselectallicon);
            return;
        }
        for (int i2 = 0; i2 < this.fileImportEntListShow.size(); i2++) {
            this.fileImportEntListShow.get(i2).SetFileCheck(true);
        }
        this.IsSelectAll = true;
        this.btnSelectAll.setImageResource(R.drawable.ic_selectallicon);
    }

    public String FileName(String str) {
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == " /".charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    public void Back() {
        if (this.isAlbumClick) {
            this.isAlbumClick = false;
            this.lbl_import_photo_album_topbaar.setText("Import Folders");
            this.album_import_ListView.setVisibility(0);
            this.imagegrid.setVisibility(4);
            this.btnSelectAll.setVisibility(4);
            for (int i = 0; i < this.fileImportEntListShow.size(); i++) {
                this.fileImportEntListShow.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, DocumentsActivity.class));
        finish();
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive && !Common.IsWorkInProgress) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (this.isAlbumClick) {
                this.isAlbumClick = false;
                this.lbl_import_photo_album_topbaar.setText("Import Albums");
                this.album_import_ListView.setVisibility(0);
                this.imagegrid.setVisibility(4);
                this.btnSelectAll.setVisibility(4);
                for (int i2 = 0; i2 < this.fileImportEntListShow.size(); i2++) {
                    this.fileImportEntListShow.get(i2).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, DocumentsActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
