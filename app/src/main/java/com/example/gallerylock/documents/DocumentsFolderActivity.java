package com.example.gallerylock.documents;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.gallerylock.LibCommonAppClass;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.notes.SystemBarTintManager;
import com.example.gallerylock.notes.UIElementsHelper;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class DocumentsFolderActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    public static int albumPosition = 0;
    public static boolean isEdit = false;
    public static boolean isGridView = true;
    private DocumentFolderDAL DocumentFolderDAL;
    public ProgressBar Progress;
    private DocumentsFolderAdapter adapter;
    AppSettingsSharedPreferences appSettingsSharedPreferences;
    private FloatingActionButton btn_Add_Album;
    private AppDocumentsAdapter docadapter;
    private ArrayList<DocumentFolder> documentFolders;
    private ArrayList<DocumentsEnt> documentList;
    private GridView gridView;
    private ImageButton ib_more;
    LinearLayout ll_EditAlbum;
    LinearLayout.LayoutParams ll_EditAlbum_Hide_Params;
    LinearLayout.LayoutParams ll_EditAlbum_Show_Params;
    LinearLayout ll_background;
    LinearLayout ll_delete_btn;
    LinearLayout ll_import_from_camera_btn;
    LinearLayout ll_import_from_gallery_btn;
    LinearLayout ll_rename_btn;
    int position;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    TextView toolbar_title;
    String folderName = "";
    int AlbumId = 0;
    int _SortBy = 0;
    boolean IsMoreDropdown = false;
    private boolean isSearch = false;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                DocumentsFolderActivity.this.Progress.setVisibility(8);
            }
            super.handleMessage(message);
        }
    };

    /* loaded from: classes2.dex */
    public enum SortBy {
        Name,
        Time
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_docuement_folders);
        /*this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle("Documents");*/
//        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.btn_Add_Album = (FloatingActionButton) findViewById(R.id.btn_Add_Album);
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        this.sensorManager = (SensorManager) getSystemService("sensor");
        getWindow().addFlags(128);
        this.Progress = (ProgressBar) findViewById(R.id.prbLoading);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_EditAlbum = (LinearLayout) findViewById(R.id.ll_EditAlbum);
        this.gridView = (GridView) findViewById(R.id.AlbumsGalleryGrid);
        this.ll_rename_btn = (LinearLayout) findViewById(R.id.ll_rename_btn);
        this.ll_delete_btn = (LinearLayout) findViewById(R.id.ll_delete_btn);
        this.ll_import_from_gallery_btn = (LinearLayout) findViewById(R.id.ll_move_btn);
        this.ll_import_from_camera_btn = (LinearLayout) findViewById(R.id.ll_share_btn);
        this.ll_EditAlbum_Show_Params = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, 0);
        this.ll_EditAlbum_Hide_Params = layoutParams;
        this.ll_EditAlbum.setLayoutParams(layoutParams);
        ImageButton imageButton = (ImageButton) findViewById(R.id.ib_more);
        this.ib_more = imageButton;
        imageButton.setVisibility(0);
        AppSettingsSharedPreferences GetObject = AppSettingsSharedPreferences.GetObject(this);
        this.appSettingsSharedPreferences = GetObject;
        this._SortBy = GetObject.GetDocumentFoldersSortBy();
        if (isGridView) {
            this.gridView.setNumColumns(2);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        this.ll_background.setOnTouchListener(new View.OnTouchListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (DocumentsFolderActivity.this.IsMoreDropdown) {
                    DocumentsFolderActivity.this.IsMoreDropdown = false;
                }
                return false;
            }
        });
        this.Progress.setVisibility(0);
        new Handler().postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.3
            @Override // java.lang.Runnable
            public void run() {
                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                documentsFolderActivity.GetFodlersFromDatabase(documentsFolderActivity._SortBy);
                Message message = new Message();
                message.what = 1;
                DocumentsFolderActivity.this.handle.sendMessage(message);
            }
        }, 300L);
        this.btn_Add_Album.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!DocumentsFolderActivity.isEdit) {
                    DocumentsFolderActivity.this.AddAlbumPopup();
                }
            }
        });
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                DocumentsFolderActivity.albumPosition = DocumentsFolderActivity.this.gridView.getFirstVisiblePosition();
                if (DocumentsFolderActivity.isEdit) {
                    DocumentsFolderActivity.isEdit = false;
                    DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                    documentsFolderActivity.adapter = new DocumentsFolderAdapter(documentsFolderActivity2, 17367043, documentsFolderActivity2.documentFolders, i, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                    DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                    DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                } else if (DocumentsFolderActivity.this.isSearch) {
                    DocumentsFolderActivity.this.isSearch = false;
                    int id = ((DocumentsEnt) DocumentsFolderActivity.this.documentList.get(i)).getId();
                    DocumentDAL documentDAL = new DocumentDAL(DocumentsFolderActivity.this);
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
                    DocumentsFolderActivity.this.CopyTempFile(file2.getAbsolutePath());
                } else {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Common.FolderId = ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(i)).getId();
                    DocumentsFolderActivity.this.startActivity(new Intent(DocumentsFolderActivity.this, DocumentsActivity.class));
                    DocumentsFolderActivity.this.finish();
                }
            }
        });
        this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.6
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                DocumentsFolderActivity.albumPosition = DocumentsFolderActivity.this.gridView.getFirstVisiblePosition();
                if (DocumentsFolderActivity.isEdit) {
                    DocumentsFolderActivity.isEdit = false;
                    DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                    documentsFolderActivity.adapter = new DocumentsFolderAdapter(documentsFolderActivity2, 17367043, documentsFolderActivity2.documentFolders, i, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                    DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                    DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                } else {
                    DocumentsFolderActivity.isEdit = true;
                    DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Show_Params);
                    DocumentsFolderActivity.this.position = i;
                    DocumentsFolderActivity.this.AlbumId = Common.FolderId;
                    DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                    documentsFolderActivity3.folderName = ((DocumentFolder) documentsFolderActivity3.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderName();
                    DocumentsFolderActivity documentsFolderActivity4 = DocumentsFolderActivity.this;
                    DocumentsFolderActivity documentsFolderActivity5 = DocumentsFolderActivity.this;
                    documentsFolderActivity4.adapter = new DocumentsFolderAdapter(documentsFolderActivity5, 17367043, documentsFolderActivity5.documentFolders, i, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                    DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                    DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                }
                if (DocumentsFolderActivity.albumPosition != 0) {
                    DocumentsFolderActivity.this.gridView.setSelection(DocumentsFolderActivity.albumPosition);
                }
                return true;
            }
        });
        this.ll_rename_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getId() != 1) {
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    documentsFolderActivity.EditAlbumPopup(((DocumentFolder) documentsFolderActivity.documentFolders.get(DocumentsFolderActivity.this.position)).getId(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderName(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderLocation());
                    return;
                }
                Toast.makeText(DocumentsFolderActivity.this, (int) R.string.lbl_default_folder_notrenamed, 0).show();
                DocumentsFolderActivity.isEdit = false;
                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                documentsFolderActivity2.adapter = new DocumentsFolderAdapter(documentsFolderActivity3, 17367043, documentsFolderActivity3.documentFolders, 0, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
            }
        });
        this.ll_delete_btn.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getId() != 1) {
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    documentsFolderActivity.DeleteALertDialog(((DocumentFolder) documentsFolderActivity.documentFolders.get(DocumentsFolderActivity.this.position)).getId(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderName(), ((DocumentFolder) DocumentsFolderActivity.this.documentFolders.get(DocumentsFolderActivity.this.position)).getFolderLocation());
                    return;
                }
                Toast.makeText(DocumentsFolderActivity.this, (int) R.string.lbl_default_folder_notdeleted, 0).show();
                DocumentsFolderActivity.isEdit = false;
                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                documentsFolderActivity2.adapter = new DocumentsFolderAdapter(documentsFolderActivity3, 17367043, documentsFolderActivity3.documentFolders, 0, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
            }
        });
        int i = albumPosition;
        if (i != 0) {
            this.gridView.setSelection(i);
            albumPosition = 0;
        }
        documentBind();
    }

    public void btnOnCloudClick() {
        if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = CloudCommon.DropboxType.Documents.ordinal();
            Utilities.StartCloudActivity(this);
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        /*InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.DocuementFolder.ordinal();
        startActivity(new Intent(this, InAppPurchaseActivity.class));
        finish();*/
    }

    private void documentBind() {
        DocumentDAL documentDAL = new DocumentDAL(this);
        documentDAL.OpenRead();
        this.documentList = (ArrayList) documentDAL.GetAllDocuments();
        this.docadapter = new AppDocumentsAdapter(this, 17367043, this.documentList, false, 1);
        this.gridView.setAdapter((ListAdapter) this.adapter);
        documentDAL.close();
    }

    public void btnOnMoreClick() {
        this.IsMoreDropdown = false;
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
        arrayList2.add("Tile");
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add("Sort by");
        arrayList3.add("Name");
        arrayList3.add("Date");
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.9
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                Log.v("", "yes");
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.10
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (i == 0) {
                    if (i2 == 0) {
                        DocumentsFolderActivity.isGridView = false;
                        DocumentsFolderActivity.this.ViewBy();
                        popupWindow.dismiss();
                        DocumentsFolderActivity.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        DocumentsFolderActivity.isGridView = true;
                        DocumentsFolderActivity.this.ViewBy();
                        popupWindow.dismiss();
                        DocumentsFolderActivity.this.IsMoreDropdown = false;
                    }
                } else if (i == 1) {
                    if (i2 == 0) {
                        DocumentsFolderActivity.this._SortBy = SortBy.Name.ordinal();
                        DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                        documentsFolderActivity.GetFodlersFromDatabase(documentsFolderActivity._SortBy);
                        DocumentsFolderActivity.this.appSettingsSharedPreferences.SetDocumentFoldersSortBy(DocumentsFolderActivity.this._SortBy);
                        popupWindow.dismiss();
                        DocumentsFolderActivity.this.IsMoreDropdown = false;
                    } else if (i2 == 1) {
                        DocumentsFolderActivity.this._SortBy = SortBy.Time.ordinal();
                        DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                        documentsFolderActivity2.GetFodlersFromDatabase(documentsFolderActivity2._SortBy);
                        DocumentsFolderActivity.this.appSettingsSharedPreferences.SetDocumentFoldersSortBy(DocumentsFolderActivity.this._SortBy);
                        popupWindow.dismiss();
                        DocumentsFolderActivity.this.IsMoreDropdown = false;
                    }
                }
                return false;
            }
        });
        if (!this.IsMoreDropdown) {
            Toolbar toolbar = this.toolbar;
            popupWindow.showAsDropDown(toolbar, toolbar.getWidth(), 0);
            this.IsMoreDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsMoreDropdown = false;
    }

    public void ViewBy() {
        if (isGridView) {
            this.gridView.setNumColumns(2);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
        } else {
            this.gridView.setNumColumns(1);
            this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
        }
        if (Utilities.getScreenOrientation(this) == 1) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 2));
            }
        } else if (Utilities.getScreenOrientation(this) == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                } else {
                    this.gridView.setNumColumns(1);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
                }
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
        DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
        this.adapter = documentsFolderAdapter;
        this.gridView.setAdapter((ListAdapter) documentsFolderAdapter);
        this.adapter.notifyDataSetChanged();
    }

    public void btnBackonClick(View view) {
        if (isEdit) {
            SecurityLocksCommon.IsAppDeactive = false;
            isEdit = false;
            this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
            DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
            this.adapter = documentsFolderAdapter;
            this.gridView.setAdapter((ListAdapter) documentsFolderAdapter);
            this.adapter.notifyDataSetChanged();
        } else if (this.isSearch) {
            this.isSearch = false;
            GetFodlersFromDatabase(this._SortBy);
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        }
    }

    public void GetFodlersFromDatabase(int i) {
        isEdit = false;
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
        this.DocumentFolderDAL = documentFolderDAL;
        try {
            documentFolderDAL.OpenRead();
            this.documentFolders = (ArrayList) this.DocumentFolderDAL.GetFolders(i);
            DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
            this.adapter = documentsFolderAdapter;
            this.gridView.setAdapter(documentsFolderAdapter);
            this.adapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } catch (Throwable th) {
            DocumentFolderDAL documentFolderDAL2 = this.DocumentFolderDAL;
            if (documentFolderDAL2 != null) {
                documentFolderDAL2.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AddAlbumPopup() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_Document_folder_Create_Album);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        editText.setHint(R.string.lbl_Document_folder_Create_Folder_enter);
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(DocumentsFolderActivity.this, (int) R.string.lbl_Document_folder_Create_Folder_please_enter, 0).show();
                    return;
                }
                DocumentsFolderActivity.this.folderName = editText.getEditableText().toString();
                File file = new File(StorageOptionsCommon.STORAGEPATH + "/" + StorageOptionsCommon.DOCUMENTS + DocumentsFolderActivity.this.folderName);
                if (file.exists()) {
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    Toast.makeText(documentsFolderActivity, "\"" + DocumentsFolderActivity.this.folderName + "\" already exist", 0).show();
                } else if (file.mkdirs()) {
                    DocumentsFolderGalleryMethods documentsFolderGalleryMethods = new DocumentsFolderGalleryMethods();
                    DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                    documentsFolderGalleryMethods.AddFolderToDatabase(documentsFolderActivity2, documentsFolderActivity2.folderName);
                    Toast.makeText(DocumentsFolderActivity.this, (int) R.string.lbl_Document_folder_Create_Album_Success, 0).show();
                    DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                    documentsFolderActivity3.GetFodlersFromDatabase(documentsFolderActivity3._SortBy);
                    dialog.dismiss();
                } else {
                    Toast.makeText(DocumentsFolderActivity.this, "ERROR! Some Error in creating folder", 0).show();
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void EditAlbumPopup(final int i, final String str, final String str2) {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.album_add_edit_popup);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_Document_folder_Rename_Album);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setTypeface(createFromAsset);
        final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
        editText.setHint(R.string.lbl_Document_folder_Create_Folder_enter);
        if (str.length() > 0) {
            editText.setText(str);
        }
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getEditableText().toString().length() <= 0 || editText.getEditableText().toString().trim().isEmpty()) {
                    Toast.makeText(DocumentsFolderActivity.this, (int) R.string.lbl_Document_folder_Create_Folder_please_enter, 0).show();
                    return;
                }
                DocumentsFolderActivity.this.folderName = editText.getEditableText().toString();
                if (new File(str2).exists()) {
                    File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + DocumentsFolderActivity.this.folderName);
                    if (file.exists()) {
                        DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                        Toast.makeText(documentsFolderActivity, "\"" + DocumentsFolderActivity.this.folderName + "\" already exist", 0).show();
                        return;
                    }
                    File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + str);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    if (file2.renameTo(file)) {
                        DocumentsFolderGalleryMethods documentsFolderGalleryMethods = new DocumentsFolderGalleryMethods();
                        DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                        documentsFolderGalleryMethods.UpdateAlbumInDatabase(documentsFolderActivity2, i, documentsFolderActivity2.folderName);
                        Toast.makeText(DocumentsFolderActivity.this, (int) R.string.lbl_Photos_Album_Create_Album_Success_renamed, 0).show();
                        DocumentsFolderActivity documentsFolderActivity3 = DocumentsFolderActivity.this;
                        documentsFolderActivity3.GetFodlersFromDatabase(documentsFolderActivity3._SortBy);
                        dialog.dismiss();
                        DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                    }
                }
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity.isEdit = false;
                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                documentsFolderActivity.adapter = new DocumentsFolderAdapter(documentsFolderActivity2, 17367043, documentsFolderActivity2.documentFolders, DocumentsFolderActivity.this.position, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void DeleteALertDialog(final int i, final String str, final String str2) {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        if (str.length() > 9) {
            textView.setText("Are you sure you want to delete " + str.substring(0, 8) + "... including its data?");
        } else {
            textView.setText("Are you sure you want to delete " + str + " including its data?");
        }
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DocumentsFolderActivity.this.DeleteAlbum(i, str, str2);
                dialog.dismiss();
                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DocumentsFolderActivity.isEdit = false;
                DocumentsFolderActivity.this.ll_EditAlbum.setLayoutParams(DocumentsFolderActivity.this.ll_EditAlbum_Hide_Params);
                DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                DocumentsFolderActivity documentsFolderActivity2 = DocumentsFolderActivity.this;
                documentsFolderActivity.adapter = new DocumentsFolderAdapter(documentsFolderActivity2, 17367043, documentsFolderActivity2.documentFolders, DocumentsFolderActivity.this.position, DocumentsFolderActivity.isEdit, DocumentsFolderActivity.isGridView);
                DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.adapter);
                DocumentsFolderActivity.this.adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void DeleteAlbum(int i, String str, String str2) {
        File file = new File(str2);
        DeleteAlbumFromDatabase(i);
        try {
            Utilities.DeleteAlbum(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void DeleteAlbumFromDatabase(int i) {
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(this);
        try {
            try {
                documentFolderDAL.OpenWrite();
                documentFolderDAL.DeleteFolderById(i);
                Toast.makeText(this, (int) R.string.lbl_Photos_Album_delete_success, 0).show();
                GetFodlersFromDatabase(this._SortBy);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            documentFolderDAL.close();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(5);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(3);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        } else if (configuration.orientation != 1) {
        } else {
            if (Common.isTablet10Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(4);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (Common.isTablet7Inch(this)) {
                if (isGridView) {
                    this.gridView.setNumColumns(3);
                    this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
                    return;
                }
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            } else if (isGridView) {
                this.gridView.setNumColumns(2);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 10));
            } else {
                this.gridView.setNumColumns(1);
                this.gridView.setVerticalSpacing(Utilities.convertDptoPix(getApplicationContext(), 4));
            }
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.document_folder_menu, menu);
        menu.findItem(R.id.action_search).setIcon(R.drawable.top_srch_icon);
        menu.findItem(R.id.action_view).setIcon(R.drawable.ic_more_top_bar_icon);
        menu.findItem(R.id.action_cloud).setIcon(R.drawable.ic_topcloudicon);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentsFolderActivity.17
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                if (str.length() > 0) {
                    DocumentsFolderActivity.this.isSearch = true;
                    ArrayList arrayList = new ArrayList();
                    Iterator it = DocumentsFolderActivity.this.documentList.iterator();
                    while (it.hasNext()) {
                        DocumentsEnt documentsEnt = (DocumentsEnt) it.next();
                        if (documentsEnt.getDocumentName().toLowerCase().contains(str)) {
                            arrayList.add(documentsEnt);
                        }
                    }
                    DocumentsFolderActivity.this.gridView.setNumColumns(1);
                    DocumentsFolderActivity.this.gridView.setVerticalSpacing(Utilities.convertDptoPix(DocumentsFolderActivity.this.getApplicationContext(), 4));
                    DocumentsFolderActivity.this.docadapter = new AppDocumentsAdapter(DocumentsFolderActivity.this, 17367043, arrayList, false, 1);
                    DocumentsFolderActivity.this.gridView.setAdapter((ListAdapter) DocumentsFolderActivity.this.docadapter);
                } else {
                    DocumentsFolderActivity.this.isSearch = false;
                    DocumentsFolderActivity.this.ViewBy();
                    DocumentsFolderActivity documentsFolderActivity = DocumentsFolderActivity.this;
                    documentsFolderActivity.GetFodlersFromDatabase(documentsFolderActivity._SortBy);
                }
                return true;
            }
        });
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(getApplicationContext(), FeaturesActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_cloud) {
            btnOnCloudClick();
            return true;
        } else if (itemId != R.id.action_view) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            btnOnMoreClick();
            return true;
        }
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    private void applyKitKatTranslucency() {
        if (Build.VERSION.SDK_INT >= 19) {
            setTranslucentStatus(true);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setTintDrawable(UIElementsHelper.getGeneralActionBarBackground(this));
            this.toolbar.setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(this));
        }
    }

    private void setTranslucentStatus(boolean z) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (z) {
            attributes.flags |= 67108864;
        } else {
            attributes.flags &= -67108865;
        }
        window.setAttributes(attributes);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CopyTempFile(String str) {
        File file = new File(str);
        try {
            Utilities.NSDecryption(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String guessContentTypeFromName = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse(Constants.FILE + file.getAbsolutePath()), guessContentTypeFromName);
        startActivity(intent);
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

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
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

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (isEdit) {
                SecurityLocksCommon.IsAppDeactive = false;
                isEdit = false;
                this.ll_EditAlbum.setLayoutParams(this.ll_EditAlbum_Hide_Params);
                DocumentsFolderAdapter documentsFolderAdapter = new DocumentsFolderAdapter(this, 17367043, this.documentFolders, 0, isEdit, isGridView);
                this.adapter = documentsFolderAdapter;
                this.gridView.setAdapter((ListAdapter) documentsFolderAdapter);
                this.adapter.notifyDataSetChanged();
                return true;
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
