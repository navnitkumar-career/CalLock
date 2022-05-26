package com.example.gallerylock.documents;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;


import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.photo.MoveAlbumAdapter;
import com.example.gallerylock.privatebrowser.SecureBrowserActivity;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.getbase.floatingactionbutton.BuildConfig;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class DocumentsActivity extends BaseActivity {
    public static int _ViewBy;
    public static ProgressDialog myProgressDialog;
    ImageButton _btnSortingDropdown;
    private String[] _folderNameArray;
    int albumId;
    private AppDocumentsAdapter appDocumentsAdapter;
    ImageButton btnSelectAll;
    private DocumentDAL documentDAL;
    List<DocumentsEnt> documentEntList;
    private DocumentFolder documentFolder;
    private DocumentFolderDAL documentFolderDAL;
    FloatingActionButton fabImpBrowser;
    FloatingActionButton fabImpGallery;
    FloatingActionButton fabImpPcMac;
    FloatingActionsMenu fabMenu;
    ImageView file_empty_icon;
    FrameLayout fl_bottom_baar;
    protected String folderLocation;
    String folderName;
    GridView imagegrid;
    TextView lbl_album_name_topbaar;
    TextView lbl_file_empty;
    LinearLayout ll_AddPhotos_Bottom_Baar;
    LinearLayout ll_EditFiles;
    LinearLayout.LayoutParams ll_Hide_Params;
    LinearLayout.LayoutParams ll_Show_Params;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_file_empty;
    LinearLayout ll_file_grid;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_import_intenet_btn;
    LinearLayout ll_import_wifi_btn;
    LinearLayout ll_move_btn;
    LinearLayout ll_share_btn;
    LinearLayout ll_topbaar;
    LinearLayout ll_unhide_btn;
    private String moveToFolderLocation;
    int selectCount;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    private ArrayList<String> files = new ArrayList<>();
    private int fileCount = 0;
    boolean isEditMode = false;
    private List<String> _folderNameArrayForMove = null;
    boolean IsSortingDropdown = false;
    int _SortBy = 1;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                DocumentsActivity.this.hideProgress();
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(DocumentsActivity.this, (int) R.string.Unhide_error, 0).show();
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(DocumentsActivity.this, (int) R.string.Move_error, 0).show();
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(DocumentsActivity.this, (int) R.string.Delete_error, 0).show();
                }
            } else if (message.what == 4) {
                Toast.makeText(DocumentsActivity.this, (int) R.string.toast_share, 1).show();
            } else if (message.what == 3) {
                if (Common.isUnHide) {
                    Common.isUnHide = false;
                    Toast.makeText(DocumentsActivity.this, (int) R.string.toast_unhide, 1).show();
                    DocumentsActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent = new Intent(DocumentsActivity.this, DocumentsActivity.class);
                        intent.addFlags(67108864);
                        DocumentsActivity.this.startActivity(intent);
                        DocumentsActivity.this.finish();
                    }
                } else if (Common.isDelete) {
                    Common.isDelete = false;
                    Toast.makeText(DocumentsActivity.this, (int) R.string.toast_delete, 0).show();
                    DocumentsActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent2 = new Intent(DocumentsActivity.this, DocumentsActivity.class);
                        intent2.addFlags(67108864);
                        DocumentsActivity.this.startActivity(intent2);
                        DocumentsActivity.this.finish();
                    }
                } else if (Common.isMove) {
                    Common.isMove = false;
                    Toast.makeText(DocumentsActivity.this, (int) R.string.toast_move, 0).show();
                    DocumentsActivity.this.hideProgress();
                    if (!Common.IsWorkInProgress) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        Intent intent3 = new Intent(DocumentsActivity.this, DocumentsActivity.class);
                        intent3.addFlags(67108864);
                        DocumentsActivity.this.startActivity(intent3);
                        DocumentsActivity.this.finish();
                    }
                }
            }
            super.handleMessage(message);
        }
    };
    private boolean IsSelectAll = false;

    /* loaded from: classes2.dex */
    public enum SortBy {
        Time,
        Name,
        Size
    }

    /* loaded from: classes2.dex */
    public enum ViewBy {
        List,
        Detail
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUnhideProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDeleteProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMoveProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    private void showIsImportingProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    private void showCopyFilesProcessForShareProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    public void onCreate(android.os.Bundle r4) {
        super.onCreate(r4);
        setContentView((int) R.layout.documents_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
       /* setSupportActionBar(this.toolbar);
       *//* this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.Back();
            }
        });
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);*/
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_Hide_Params = new LinearLayout.LayoutParams(-2, 0);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_bottom_baar);
        this.fl_bottom_baar = frameLayout;
        frameLayout.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar = (LinearLayout) findViewById(R.id.ll_AddPhotos_Bottom_Baar);
        this.ll_EditFiles = (LinearLayout) findViewById(R.id.ll_EditPhotos);
        this.imagegrid = (GridView) findViewById(R.id.customGalleryGrid);
        this.btnSelectAll = (ImageButton) findViewById(R.id.btnSelectAll);
        this.fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        this.fabImpGallery = (FloatingActionButton) findViewById(R.id.btn_impGallery);
        this.fabImpBrowser = (FloatingActionButton) findViewById(R.id.btn_impBrowser);
        this.fabImpPcMac = (FloatingActionButton) findViewById(R.id.btn_impPcMac);
        this.fabImpGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                DocumentsActivity.this.startActivity(new Intent(DocumentsActivity.this, DocumentsImportActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.fabImpBrowser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.CurrentWebBrowserActivity = DocumentsActivity.this;
                DocumentsActivity.this.startActivity(new Intent(DocumentsActivity.this, SecureBrowserActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_import_from_gallery_btn);
        this.ll_import_intenet_btn = (LinearLayout) findViewById(R.id.ll_import_intenet_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_unhide_btn = (LinearLayout) findViewById(R.id.ll_unhide_btn);
        this.ll_move_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_share_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this._btnSortingDropdown = (ImageButton) findViewById(R.id.btnSort);
        this.ll_file_empty = (LinearLayout) findViewById(R.id.ll_photo_video_empty);
        this.ll_file_grid = (LinearLayout) findViewById(R.id.ll_photo_video_grid);
        this.file_empty_icon = (ImageView) findViewById(R.id.photo_video_empty_icon);
        this.lbl_file_empty = (TextView) findViewById(R.id.lbl_photo_video_empty);
        this.ll_file_grid.setVisibility(0);
        this.ll_file_empty.setVisibility(4);
        this.btnSelectAll.setVisibility(4);
        this.lbl_album_name_topbaar = (TextView) findViewById(R.id.lbl_album_name_topbaar);
        DocumentFolderDAL documentFolderDAL2 = new DocumentFolderDAL(this);
        documentFolderDAL2.OpenRead();
        DocumentFolder GetFolderById = documentFolderDAL2.GetFolderById(Integer.toString(Common.FolderId));
        this._SortBy = documentFolderDAL2.GetSortByFolderId(Common.FolderId);
        documentFolderDAL2.close();
        String folderName2 = GetFolderById.getFolderName();
        this.folderName = folderName2;
        Common.DocumentFolderName = folderName2;
        this.folderLocation = GetFolderById.getFolderLocation();
        getSupportActionBar().setTitle((CharSequence) this.folderName);
        this.ll_background.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (DocumentsActivity.this.IsSortingDropdown) {
                    DocumentsActivity.this.IsSortingDropdown = false;
                }
                if (DocumentsActivity.this.IsSortingDropdown) {
                    DocumentsActivity.this.IsSortingDropdown = false;
                }
                return false;
            }
        });
        this.imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!DocumentsActivity.this.isEditMode) {
                    int id = DocumentsActivity.this.documentEntList.get(i).getId();
                    DocumentDAL documentDAL = new DocumentDAL(DocumentsActivity.this);
                    documentDAL.OpenRead();
                    String folderLockDocumentLocation = documentDAL.GetDocumentById(Integer.toString(id)).getFolderLockDocumentLocation();
                    documentDAL.close();
                    String FileName = Utilities.FileName(folderLockDocumentLocation);
                    if (FileName.contains("#")) {
                        FileName = Utilities.ChangeFileExtentionToOrignal(FileName);
                    }
                    File file = new File(folderLockDocumentLocation);
                    File file2 = new File(file.getParent() + "/" + FileName);
                    file.renameTo(file2);
                    DocumentsActivity.this.CopyTempFile(file2.getAbsolutePath());
                }
            }
        });
        this.imagegrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                Common.PhotoThumbnailCurrentPosition = DocumentsActivity.this.imagegrid.getFirstVisiblePosition();
                DocumentsActivity.this.isEditMode = true;
                DocumentsActivity.this.fl_bottom_baar.setLayoutParams(DocumentsActivity.this.ll_Show_Params);
                DocumentsActivity.this.ll_AddPhotos_Bottom_Baar.setVisibility(8);
                DocumentsActivity.this.ll_EditFiles.setVisibility(0);
                DocumentsActivity.this._btnSortingDropdown.setVisibility(4);
                DocumentsActivity.this.btnSelectAll.setVisibility(0);
                DocumentsActivity.this.invalidateOptionsMenu();
                DocumentsActivity.this.documentEntList.get(i).SetFileCheck(true);
                DocumentsActivity documentsActivity = DocumentsActivity.this;
                DocumentsActivity documentsActivity2 = DocumentsActivity.this;
                AppDocumentsAdapter unused = documentsActivity.appDocumentsAdapter = new AppDocumentsAdapter(documentsActivity2, 1, documentsActivity2.documentEntList, true, DocumentsActivity._ViewBy);
                DocumentsActivity.this.imagegrid.setAdapter(DocumentsActivity.this.appDocumentsAdapter);
                DocumentsActivity.this.appDocumentsAdapter.notifyDataSetChanged();
                if (Common.PhotoThumbnailCurrentPosition != 0) {
                    DocumentsActivity.this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
                }
                return true;
            }
        });
        this.btnSelectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.SelectOrUnSelectAll();
            }
        });
        this.ll_import_from_gallery_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromPhotoAlbum = false;
                DocumentsActivity.this.startActivity(new Intent(DocumentsActivity.this, DocumentsImportActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.ll_import_intenet_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Constants.CurrentWebBrowserActivity = DocumentsActivity.this;
                DocumentsActivity.this.startActivity(new Intent(DocumentsActivity.this, SecureBrowserActivity.class));
                DocumentsActivity.this.finish();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.DeleteFiles();
            }
        });
        this.ll_unhide_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.UnhideFiles();
            }
        });
        this.ll_move_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DocumentsActivity.this.MoveFiles();
            }
        });
        this.ll_share_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!DocumentsActivity.this.IsFileCheck()) {
                    Toast.makeText(DocumentsActivity.this, R.string.toast_unselectphotomsg_share, 0).show();
                } else {
                    DocumentsActivity.this.ShareDocuments();
                }
            }
        });
        if (Common.IsImporting) {
            try {
                showIsImportingProgress();
            } catch (Exception unused) {
            }
        } else if (Common.isUnHide) {
            showUnhideProgress();
        } else if (Common.isDelete) {
            showDeleteProgress();
        } else if (Common.isMove) {
            showMoveProgress();
        } else {
            LoadFilesFromDB(this._SortBy);
        }
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectOrUnSelectAll() {
        if (this.IsSelectAll) {
            for (int i = 0; i < this.documentEntList.size(); i++) {
                this.documentEntList.get(i).SetFileCheck(false);
            }
            this.IsSelectAll = false;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_unselectallicon);
        } else {
            for (int i2 = 0; i2 < this.documentEntList.size(); i2++) {
                this.documentEntList.get(i2).SetFileCheck(true);
            }
            this.IsSelectAll = true;
            this.btnSelectAll.setBackgroundResource(R.drawable.ic_selectallicon);
        }
        AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(this, 1, this.documentEntList, true, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter;
        this.imagegrid.setAdapter((ListAdapter) appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
    }

    private void SetcheckFlase() {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            this.documentEntList.get(i).SetFileCheck(false);
        }
        AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(this, 1, this.documentEntList, false, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter;
        this.imagegrid.setAdapter((ListAdapter) appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
        if (Common.PhotoThumbnailCurrentPosition != 0) {
            this.imagegrid.setSelection(Common.PhotoThumbnailCurrentPosition);
            Common.PhotoThumbnailCurrentPosition = 0;
        }
    }

    public void btnBackonClick(View view) {
        Back();
    }

    public void Back() {
        if (this.isEditMode) {
            SetcheckFlase();
            this.isEditMode = false;
            this.IsSortingDropdown = false;
            this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
            this.ll_AddPhotos_Bottom_Baar.setVisibility(8);
            this.ll_EditFiles.setVisibility(4);
            this.IsSelectAll = false;
            this.btnSelectAll.setVisibility(4);
            this._btnSortingDropdown.setVisibility(0);
            invalidateOptionsMenu();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        Common.FolderId = 0;
        Common.DocumentFolderName = StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM;
        startActivity(new Intent(this, DocumentsFolderActivity.class));
        finish();
    }

    public void UnhideFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_unhide, 0).show();
            return;
        }
        SelectedCount();
        if (Common.GetTotalFree() > Common.GetFileSize(this.files)) {
            final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.confirmation_message_box);
            dialog.setCancelable(true);
            Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
            TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
            textView.setTypeface(createFromAsset);
            textView.setText("Are you sure you want to restore (" + this.selectCount + ") document(s)?");
            ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.15
                /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity$15$1] */
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    DocumentsActivity.this.showUnhideProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.15.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                dialog.dismiss();
                                Common.isUnHide = true;
                                DocumentsActivity.this.Unhide();
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.16
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    void Unhide() throws IOException {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (this.documentEntList.get(i).GetFileCheck()) {
                if (Utilities.NSUnHideFile(this, this.documentEntList.get(i).getFolderLockDocumentLocation(), this.documentEntList.get(i).getOriginalDocumentLocation())) {
                    DeleteFromDatabase(this.documentEntList.get(i).getId());
                } else {
                    Toast.makeText(this, (int) R.string.Unhide_error, 0).show();
                }
            }
        }
    }

    public void DeleteFiles() {
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectphotomsg_delete, 0).show();
            return;
        }
        SelectedCount();
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        textView.setText("Are you sure you want to delete (" + this.selectCount + ") document(s)?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.17
            /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity$17$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DocumentsActivity.this.showDeleteProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.17.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Common.isDelete = true;
                            dialog.dismiss();
                            DocumentsActivity.this.Delete();
                            Common.IsWorkInProgress = true;
                            Message message = new Message();
                            message.what = 3;
                            DocumentsActivity.this.handle.sendMessage(message);
                            Common.IsWorkInProgress = false;
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 3;
                            DocumentsActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.18
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void Delete() {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (this.documentEntList.get(i).GetFileCheck()) {
                new File(this.documentEntList.get(i).getFolderLockDocumentLocation()).delete();
                DeleteFromDatabase(this.documentEntList.get(i).getId());
            }
        }
    }

    public void DeleteFromDatabase(int i) {
        DocumentDAL documentDAL;
        DocumentDAL documentDAL2 = new DocumentDAL(this);
        this.documentDAL = documentDAL2;
        try {
            try {
                documentDAL2.OpenWrite();
                this.documentDAL.DeleteDocumentById(i);
                documentDAL = this.documentDAL;
                if (documentDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                documentDAL = this.documentDAL;
                if (documentDAL == null) {
                    return;
                }
            }
            documentDAL.close();
        } catch (Throwable th) {
            DocumentDAL documentDAL3 = this.documentDAL;
            if (documentDAL3 != null) {
                documentDAL3.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectedCount() {
        this.files.clear();
        this.selectCount = 0;
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (this.documentEntList.get(i).GetFileCheck()) {
                this.files.add(this.documentEntList.get(i).getFolderLockDocumentLocation());
                this.selectCount++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean IsFileCheck() {
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (this.documentEntList.get(i).GetFileCheck()) {
                return true;
            }
        }
        return false;
    }

    public void MoveFiles() {
        DocumentDAL documentDAL = new DocumentDAL(this);
        this.documentDAL = documentDAL;
        documentDAL.OpenWrite();
        this._folderNameArray = this.documentDAL.GetFolderNames(Common.FolderId);
        if (!IsFileCheck()) {
            Toast.makeText(this, (int) R.string.toast_unselectdocumentmsg_move, 0).show();
        } else if (this._folderNameArray.length > 0) {
            GetFolderNameFromDB();
        } else {
            Toast.makeText(this, (int) R.string.toast_OneFolder, 0).show();
        }
    }

    void Move(String str, String str2, String str3) {
        String str4;
        DocumentFolder GetAlbum = GetAlbum(str3);
        for (int i = 0; i < this.documentEntList.size(); i++) {
            if (this.documentEntList.get(i).GetFileCheck()) {
                if (this.documentEntList.get(i).getDocumentName().contains("#")) {
                    str4 = this.documentEntList.get(i).getDocumentName();
                } else {
                    str4 = Utilities.ChangeFileExtention(this.documentEntList.get(i).getDocumentName());
                }
                String str5 = str2 + "/" + str4;
                try {
                    if (Utilities.MoveFileWithinDirectories(this.documentEntList.get(i).getFolderLockDocumentLocation(), str5)) {
                        UpdateFileLocationInDatabase(this.documentEntList.get(i), str5, GetAlbum.getId());
                        Common.FolderId = GetAlbum.getId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UpdateFileLocationInDatabase(DocumentsEnt documentsEnt, String str, int i) {
        DocumentDAL documentDAL;
        documentsEnt.setFolderLockDocumentLocation(str);
        documentsEnt.setFolderId(i);
        try {
            try {
                DocumentDAL documentDAL2 = new DocumentDAL(this);
                documentDAL2.OpenWrite();
                documentDAL2.UpdateDocumentLocation(documentsEnt);
                documentDAL = this.documentDAL;
                if (documentDAL == null) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                documentDAL = this.documentDAL;
                if (documentDAL == null) {
                    return;
                }
            }
            documentDAL.close();
        } catch (Throwable th) {
            DocumentDAL documentDAL3 = this.documentDAL;
            if (documentDAL3 != null) {
                documentDAL3.close();
            }
            throw th;
        }
    }

    public DocumentFolder GetAlbum(String str) {
        DocumentFolderDAL documentFolderDAL2 = new DocumentFolderDAL(this);
        this.documentFolderDAL = documentFolderDAL2;
        try {
            documentFolderDAL2.OpenRead();
            DocumentFolder GetFolder = this.documentFolderDAL.GetFolder(str);
            this.documentFolder = GetFolder;
            return GetFolder;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } catch (Throwable th) {
            DocumentFolderDAL documentFolderDAL3 = this.documentFolderDAL;
            if (documentFolderDAL3 != null) {
                documentFolderDAL3.close();
            }
            throw th;
        }
    }

    private void GetFolderNameFromDB() {
        DocumentDAL documentDAL;
        DocumentDAL documentDAL2 = new DocumentDAL(this);
        this.documentDAL = documentDAL2;
        try {
            try {
                documentDAL2.OpenWrite();
                this._folderNameArrayForMove = this.documentDAL.GetMoveFolderNames(Common.FolderId);
                MovePhotoDialog();
                documentDAL = this.documentDAL;
                if (documentDAL == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                documentDAL = this.documentDAL;
                if (documentDAL == null) {
                    return;
                }
            }
            documentDAL.close();
        } catch (Throwable th) {
            DocumentDAL documentDAL3 = this.documentDAL;
            if (documentDAL3 != null) {
                documentDAL3.close();
            }
            throw th;
        }
    }

    void MovePhotoDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.move_customlistview);
        ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
        listView.setAdapter((ListAdapter) new MoveAlbumAdapter(this, 17367043, this._folderNameArrayForMove, R.drawable.ic_notesfolder_thumb_icon));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.19
            /* JADX WARN: Type inference failed for: r1v5, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity$19$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
                if (DocumentsActivity.this._folderNameArrayForMove != null) {
                    DocumentsActivity.this.SelectedCount();
                    DocumentsActivity.this.showMoveProgress();
                    new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.19.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            try {
                                Common.isMove = true;
                                dialog.dismiss();
                                DocumentsActivity documentsActivity = DocumentsActivity.this;
                                documentsActivity.moveToFolderLocation = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + ((String) DocumentsActivity.this._folderNameArrayForMove.get(i));
                                DocumentsActivity.this.Move(DocumentsActivity.this.folderLocation, DocumentsActivity.this.moveToFolderLocation, (String) DocumentsActivity.this._folderNameArrayForMove.get(i));
                                Common.IsWorkInProgress = true;
                                Message message = new Message();
                                message.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message);
                                Common.IsWorkInProgress = false;
                            } catch (Exception unused) {
                                Message message2 = new Message();
                                message2.what = 3;
                                DocumentsActivity.this.handle.sendMessage(message2);
                            }
                        }
                    }.start();
                }
            }
        });
        dialog.show();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity$20] */
    public void ShareDocuments() {
        showCopyFilesProcessForShareProgress();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.20
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    ArrayList arrayList = new ArrayList();
                    Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                    intent.setType("image/*");
                    for (ResolveInfo resolveInfo : DocumentsActivity.this.getPackageManager().queryIntentActivities(intent, 0)) {
                        String str = resolveInfo.activityInfo.packageName;
                        if (!str.equals(AppPackageCommon.AppPackageName) && !str.equals("com.dropbox.android") && !str.equals("com.facebook.katana")) {
                            Intent intent2 = new Intent("android.intent.action.SEND_MULTIPLE");
                            intent2.setType("image/*");
                            intent2.setPackage(str);
                            arrayList.add(intent2);
                            String str2 = StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS;
                            ArrayList arrayList2 = new ArrayList();
                            ArrayList<Uri> arrayList3 = new ArrayList<>();
                            for (int i = 0; i < DocumentsActivity.this.documentEntList.size(); i++) {
                                if (DocumentsActivity.this.documentEntList.get(i).GetFileCheck()) {
                                    try {
                                        str2 = Utilities.CopyTemporaryFile(DocumentsActivity.this, DocumentsActivity.this.documentEntList.get(i).getFolderLockDocumentLocation(), str2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    arrayList2.add(str2);
                                    arrayList3.add(FileProvider.getUriForFile(DocumentsActivity.this, BuildConfig.APPLICATION_ID, new File(str2)));
                                }
                            }
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList3);
                        }
                    }
                    Intent createChooser = Intent.createChooser((Intent) arrayList.remove(0), "Share Via");
                    createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
                    DocumentsActivity.this.startActivity(createChooser);
                    Message message = new Message();
                    message.what = 4;
                    DocumentsActivity.this.handle.sendMessage(message);
                } catch (Exception unused) {
                    Message message2 = new Message();
                    message2.what = 4;
                    DocumentsActivity.this.handle.sendMessage(message2);
                }
            }
        }.start();
    }

    void LoadFilesFromDB(int i) {
        this.documentEntList = new ArrayList();
        DocumentDAL documentDAL = new DocumentDAL(this);
        documentDAL.OpenRead();
        this.fileCount = documentDAL.GetDocumentCountByFolderId(Common.FolderId);
        this.documentEntList = documentDAL.GetDocuments(Common.FolderId, i);
        documentDAL.close();
        AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(this, 1, this.documentEntList, false, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter;
        this.imagegrid.setAdapter((ListAdapter) appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
        if (this.documentEntList.size() < 1) {
            this.ll_file_grid.setVisibility(4);
            this.ll_file_empty.setVisibility(0);
            this.file_empty_icon.setBackgroundResource(R.drawable.ic_documents_empty_icon);
            this.lbl_file_empty.setText(R.string.lbl_No_Documents);
            return;
        }
        this.ll_file_grid.setVisibility(0);
        this.ll_file_empty.setVisibility(4);
    }

    public void btnSortonClick(View view) {
        this.IsSortingDropdown = false;
        showPopupWindow();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService("layout_inflater")).inflate(R.layout.popup_window_expandable, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.expListview);
        arrayList.add("View by");
        arrayList2.add("List");
        arrayList2.add("Detail");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        arrayList3.add("Size");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.21
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.22
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        DocumentsActivity._ViewBy = ViewBy.List.ordinal();
                        DocumentsActivity.this.ViewBy();
                        popupWindow.dismiss();
                        DocumentsActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        DocumentsActivity._ViewBy = ViewBy.Detail.ordinal();
                        DocumentsActivity.this.ViewBy();
                        popupWindow.dismiss();
                        DocumentsActivity.this.IsSortingDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        DocumentsActivity.this._SortBy = SortBy.Name.ordinal();
                        DocumentsActivity documentsActivity = DocumentsActivity.this;
                        documentsActivity.LoadFilesFromDB(documentsActivity._SortBy);
                        DocumentsActivity.this.AddSortInDB();
                        popupWindow.dismiss();
                        DocumentsActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 1) {
                        DocumentsActivity.this._SortBy = SortBy.Time.ordinal();
                        DocumentsActivity documentsActivity2 = DocumentsActivity.this;
                        documentsActivity2.LoadFilesFromDB(documentsActivity2._SortBy);
                        DocumentsActivity.this.AddSortInDB();
                        popupWindow.dismiss();
                        DocumentsActivity.this.IsSortingDropdown = false;
                    } else if (i2 == 2) {
                        DocumentsActivity.this._SortBy = SortBy.Size.ordinal();
                        DocumentsActivity documentsActivity3 = DocumentsActivity.this;
                        documentsActivity3.LoadFilesFromDB(documentsActivity3._SortBy);
                        DocumentsActivity.this.AddSortInDB();
                        popupWindow.dismiss();
                        DocumentsActivity.this.IsSortingDropdown = false;
                    }
                }
                return false;
            }
        });
        if (!this.IsSortingDropdown) {
            LinearLayout linearLayout = this.ll_anchor;
            popupWindow.showAsDropDown(linearLayout, linearLayout.getWidth(), 0);
            this.IsSortingDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsSortingDropdown = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AddSortInDB() {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
        documentFolderDAL.OpenWrite();
        documentFolderDAL.AddSortByInDocumentFolder(this._SortBy);
        documentFolderDAL.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CopyTempFile(String str) {
        File file = new File(str);
        try {
            Utilities.NSDecryption(file);
            SecurityLocksCommon.IsAppDeactive = false;
            String guessContentTypeFromName = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file), guessContentTypeFromName);
            intent.addFlags(1);
            intent.addFlags(2);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", e.toString());
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void ViewBy() {
        AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(this, 1, this.documentEntList, false, _ViewBy);
        this.appDocumentsAdapter = appDocumentsAdapter;
        this.imagegrid.setAdapter((ListAdapter) appDocumentsAdapter);
        this.appDocumentsAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        Common.IsWorkInProgress = true;
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
        this.handle.removeCallbacksAndMessages(null);
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity$23] */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.23
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Utilities.changeFileExtention(StorageOptionsCommon.DOCUMENTS);
                } catch (Exception unused) {
                    Log.v("Login Activity", "error in changeVideosExtention method");
                }
            }
        }.start();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        SetcheckFlase();
        this.IsSortingDropdown = false;
        this.isEditMode = false;
        this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
        this.ll_AddPhotos_Bottom_Baar.setVisibility(8);
        this.ll_EditFiles.setVisibility(4);
        this.IsSelectAll = false;
        this.btnSelectAll.setVisibility(4);
        this._btnSortingDropdown.setVisibility(0);
        invalidateOptionsMenu();
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r0v0, types: [net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity$24] */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsActivity.24
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Utilities.changeFileExtention(StorageOptionsCommon.DOCUMENTS);
                } catch (Exception unused) {
                    Log.v("Login Activity", "error in changeVideosExtention method");
                }
            }
        }.start();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Common.isOpenCameraorGalleryFromApp = false;
            if (this.isEditMode) {
                SetcheckFlase();
                this.IsSortingDropdown = false;
                this.isEditMode = false;
                this.fl_bottom_baar.setLayoutParams(this.ll_Hide_Params);
                this.ll_AddPhotos_Bottom_Baar.setVisibility(8);
                this.ll_EditFiles.setVisibility(4);
                this.IsSelectAll = false;
                this.btnSelectAll.setVisibility(4);
                this._btnSortingDropdown.setVisibility(0);
                invalidateOptionsMenu();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            Common.FolderId = 0;
            Common.DocumentFolderName = StorageOptionsCommon.DOCUMENTS_DEFAULT_ALBUM;
            startActivity(new Intent(this, DocumentsFolderActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_more) {
            this.IsSortingDropdown = false;
            showPopupWindow();
            return true;
        } else if (itemId != R.id.action_select) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            if (this.IsSelectAll) {
                for (int i = 0; i < this.documentEntList.size(); i++) {
                    this.documentEntList.get(i).SetFileCheck(false);
                }
                this.IsSelectAll = false;
                menuItem.setIcon(R.drawable.ic_unselectallicon);
                invalidateOptionsMenu();
            } else {
                for (int i2 = 0; i2 < this.documentEntList.size(); i2++) {
                    this.documentEntList.get(i2).SetFileCheck(true);
                }
                this.IsSelectAll = true;
                menuItem.setIcon(R.drawable.ic_selectallicon);
            }
            AppDocumentsAdapter appDocumentsAdapter = new AppDocumentsAdapter(this, 1, this.documentEntList, true, _ViewBy);
            this.appDocumentsAdapter = appDocumentsAdapter;
            this.imagegrid.setAdapter((ListAdapter) appDocumentsAdapter);
            this.appDocumentsAdapter.notifyDataSetChanged();
            return true;
        }
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.isEditMode) {
            menu.findItem(R.id.action_more).setVisible(false);
            getMenuInflater().inflate(R.menu.menu_selection, menu);
        } else {
            menu.findItem(R.id.action_more).setVisible(true);
            if (this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_unselectallicon);
            } else if (!this.IsSelectAll && this.isEditMode) {
                menu.findItem(R.id.action_select).setIcon(R.drawable.ic_selectallicon);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
