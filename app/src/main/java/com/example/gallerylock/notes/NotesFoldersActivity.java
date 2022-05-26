package com.example.gallerylock.notes;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.FileUtils;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.wallet.CommonSharedPreferences;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class NotesFoldersActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    CommonSharedPreferences commonSharedPreferences;
    Constants constants;
    Dialog dialog;
    GridView gv_NotesFolder;
    AppCompatImageView iv_NotesFolderDelete;
    AppCompatImageView iv_NotesFolderRename;
    LinearLayout ll_NotesFolderEdit;
    FloatingActionButton mFab;
    NotesCommon notesCommon;
    NotesFilesDAL notesFilesDAL;
    NotesFolderDAL notesFolderDAL;
    NotesFolderListeners notesFolderListeners;
    List<NotesFolderDB_Pojo> notesFolderPojoList;
    NotesFolderGridViewAdapter notesFolderadapter;
    private SensorManager sensorManager;
    int sortBy;
    private Toolbar toolbar;
    TextView toolbar_title;
    int folderPosition = -1;
    boolean isEdittable = false;
    boolean IsSortingDropdown = false;
    public boolean isGridview = true;

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
        setContentView(R.layout.activity_notes_folders);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_NotesFolder = (GridView) findViewById(R.id.gv_NotesFolder);
        this.ll_NotesFolderEdit = (LinearLayout) findViewById(R.id.ll_NotesFolderEdit);
        this.iv_NotesFolderRename = (AppCompatImageView) findViewById(R.id.iv_NotesFolderRename);
        this.iv_NotesFolderDelete = (AppCompatImageView) findViewById(R.id.iv_NotesFolderDelete);
        this.mFab = (FloatingActionButton) findViewById(R.id.fabbutton);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        this.notesCommon = new NotesCommon();
        this.notesFolderPojoList = new ArrayList();
        this.notesFolderDAL = new NotesFolderDAL(this);
        this.notesFilesDAL = new NotesFilesDAL(this);
        this.notesFolderListeners = new NotesFolderListeners();
        this.constants = new Constants();
        SecurityLocksCommon.IsAppDeactive = true;
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle("Notes");
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        CommonSharedPreferences GetObject = CommonSharedPreferences.GetObject(this);
        this.commonSharedPreferences = GetObject;
        if (GetObject.get_viewByNotesFolder() == 0) {
            this.isGridview = false;
        } else {
            this.isGridview = true;
        }
        this.sortBy = this.commonSharedPreferences.get_sortByNotesFolder();
        checkIsDefaultFolderCreated();
        setGridview();
        GridView gridView = this.gv_NotesFolder;
        NotesFolderListeners notesFolderListeners = this.notesFolderListeners;
        notesFolderListeners.getClass();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (NotesFoldersActivity.this.isEdittable) {
                    NotesFoldersActivity.this.isEdittable = false;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                    NotesFoldersActivity.this.notesFolderadapter.setFocusedPosition(i);
                    NotesFoldersActivity.this.notesFolderadapter.setIsEdit(NotesFoldersActivity.this.isEdittable);
                    if (!NotesFoldersActivity.this.isGridview) {
                        NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                    } else {
                        NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(2);
                    }
                    NotesFoldersActivity.this.gv_NotesFolder.setAdapter((ListAdapter) NotesFoldersActivity.this.notesFolderadapter);
                    NotesFoldersActivity.this.notesFolderadapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                NotesCommon.CurrentNotesFolder = NotesFoldersActivity.this.notesFolderPojoList.get(i).getNotesFolderName();
                NotesCommon.CurrentNotesFolderId = NotesFoldersActivity.this.notesFolderPojoList.get(i).getNotesFolderId();
                NotesFoldersActivity.this.startActivity(new Intent(NotesFoldersActivity.this, NotesFilesActivity.class));
                NotesFoldersActivity.this.finish();
            }
        });
        AppCompatImageView appCompatImageView = this.iv_NotesFolderRename;
        NotesFolderListeners notesFolderListeners2 = this.notesFolderListeners;
        notesFolderListeners2.getClass();
        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NotesFoldersActivity.this.isEdittable && NotesFoldersActivity.this.folderPosition >= 0) {
                    switch (view.getId()) {
                        case R.id.iv_NotesFolderDelete /* 2131296568 */:
                            if (NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition).getNotesFolderName().equals("My Notes")) {
                                Toast.makeText(NotesFoldersActivity.this, "Default folder can not be deleted", 0).show();
                                return;
                            }
                            NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                            NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                            NotesFoldersActivity.this.isEdittable = false;
                            NotesFoldersActivity.this.setGridview();
                            return;
                        case R.id.iv_NotesFolderRename /* 2131296569 */:
                            if (NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition).getNotesFolderName().equals("My Notes")) {
                                Toast.makeText(NotesFoldersActivity.this, "Default folder can not be renamed", 0).show();
                                return;
                            }
                            NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                            NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                            NotesFoldersActivity.this.isEdittable = false;
                            NotesFoldersActivity.this.setGridview();
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        AppCompatImageView appCompatImageView2 = this.iv_NotesFolderDelete;
        NotesFolderListeners notesFolderListeners3 = this.notesFolderListeners;
        notesFolderListeners3.getClass();
        appCompatImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NotesFoldersActivity.this.isEdittable && NotesFoldersActivity.this.folderPosition >= 0) {
                    switch (view.getId()) {
                        case R.id.iv_NotesFolderDelete /* 2131296568 */:
                            if (NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition).getNotesFolderName().equals("My Notes")) {
                                Toast.makeText(NotesFoldersActivity.this, "Default folder can not be deleted", 0).show();
                                return;
                            }
                            NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                            NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                            NotesFoldersActivity.this.isEdittable = false;
                            NotesFoldersActivity.this.setGridview();
                            return;
                        case R.id.iv_NotesFolderRename /* 2131296569 */:
                            if (NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition).getNotesFolderName().equals("My Notes")) {
                                Toast.makeText(NotesFoldersActivity.this, "Default folder can not be renamed", 0).show();
                                return;
                            }
                            NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                            NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                            NotesFoldersActivity.this.isEdittable = false;
                            NotesFoldersActivity.this.setGridview();
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        GridView gridView2 = this.gv_NotesFolder;
        NotesFolderListeners notesFolderListeners4 = this.notesFolderListeners;
        notesFolderListeners4.getClass();
        gridView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (NotesFoldersActivity.this.isEdittable) {
                    NotesFoldersActivity.this.isEdittable = false;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                } else {
                    NotesFoldersActivity.this.isEdittable = true;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(0);
                }
                NotesFoldersActivity.this.folderPosition = i;
                NotesFoldersActivity.this.notesFolderadapter.setFocusedPosition(i);
                NotesFoldersActivity.this.notesFolderadapter.setIsEdit(NotesFoldersActivity.this.isEdittable);
                if (!NotesFoldersActivity.this.isGridview) {
                    NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                } else {
                    NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(2);
                }
                NotesFoldersActivity.this.gv_NotesFolder.setAdapter((ListAdapter) NotesFoldersActivity.this.notesFolderadapter);
                NotesFoldersActivity.this.notesFolderadapter.notifyDataSetChanged();
                return true;
            }
        });
        try {
            File file = new File(getFilesDir(), "Recordings");
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_cloud_view_sort, menu);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.1
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                List<NotesFolderDB_Pojo> arrayList = new ArrayList<>();
                if (str.length() > 0) {
                    for (NotesFolderDB_Pojo notesFolderDB_Pojo : NotesFoldersActivity.this.notesFolderPojoList) {
                        if (notesFolderDB_Pojo.getNotesFolderName().toLowerCase().contains(str)) {
                            arrayList.add(notesFolderDB_Pojo);
                        }
                    }
                } else {
                    arrayList = NotesFoldersActivity.this.notesFolderPojoList;
                }
                NotesFoldersActivity.this.bindSearchResult(arrayList);
                return true;
            }
        });
        return true;
    }

    public void bindSearchResult(List<NotesFolderDB_Pojo> list) {
        this.notesFolderadapter = new NotesFolderGridViewAdapter(this, list);
        this.gv_NotesFolder.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.notesFolderadapter.setIsGridview(this.isGridview);
        this.gv_NotesFolder.setAdapter((ListAdapter) this.notesFolderadapter);
        this.notesFolderadapter.notifyDataSetChanged();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        } else if (itemId != R.id.action_cloud) {
            if (itemId == R.id.action_viewSort) {
                this.IsSortingDropdown = false;
                showPopupWindow();
            }
        } else if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = CloudCommon.DropboxType.Notes.ordinal();
            Utilities.StartCloudActivity(this);
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
          /*  InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.NotesFolder.ordinal();
            startActivity(new Intent(this, InAppPurchaseActivity.class));
            finish();*/
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void checkIsDefaultFolderCreated() {
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES, getResources().getString(R.string.my_notes));
        if (!file.exists()) {
            file.mkdirs();
        }
        NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderName");
        sb.append(" = '");
        sb.append(getResources().getString(R.string.my_notes));
        sb.append("' AND ");
        this.constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        if (!notesFolderDAL.IsFolderAlreadyExist(sb.toString())) {
            String currentDate = this.notesCommon.getCurrentDate();
            NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
            notesFolderDB_Pojo.setNotesFolderName(getResources().getString(R.string.my_notes));
            notesFolderDB_Pojo.setNotesFolderLocation(file.getAbsolutePath());
            notesFolderDB_Pojo.setNotesFolderCreatedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderModifiedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
            notesFolderDB_Pojo.setNotesFolderColor(String.valueOf(getResources().getColor(R.color.ColorLightBlue)));
            notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
            this.notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
        }
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb.append("NotesFolderIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("NotesFolderName");
            sb.append(" COLLATE NOCASE ASC");
            this.notesFolderPojoList = notesFolderDAL.getAllNotesFolderInfoFromDatabase(sb.toString());
        } else {
            NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb2.append("NotesFolderIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("NotesFolderModifiedDate");
            sb2.append(" DESC");
            this.notesFolderPojoList = notesFolderDAL2.getAllNotesFolderInfoFromDatabase(sb2.toString());
        }
        NotesFolderGridViewAdapter notesFolderGridViewAdapter = new NotesFolderGridViewAdapter(this, this.notesFolderPojoList);
        this.notesFolderadapter = notesFolderGridViewAdapter;
        notesFolderGridViewAdapter.setFocusedPosition(0);
        this.notesFolderadapter.setIsEdit(false);
        this.notesFolderadapter.setIsGridview(this.isGridview);
        this.gv_NotesFolder.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gv_NotesFolder.setAdapter((ListAdapter) this.notesFolderadapter);
        this.notesFolderadapter.notifyDataSetChanged();
    }

    public void fabClicked(View view) {
        addFolderDialog();
    }

    public void addFolderDialog() {
        Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        this.dialog = dialog;
        dialog.setContentView(R.layout.dialog_add_notes_folder);
        this.dialog.setCancelable(true);
        final EditText editText = (EditText) this.dialog.findViewById(R.id.et_folderName);
        final ImageView imageView = (ImageView) this.dialog.findViewById(R.id.iv_selectedColor);
        final ColorPicker colorPicker = (ColorPicker) this.dialog.findViewById(R.id.folder_colorPicker);
        colorPicker.addSVBar((SVBar) this.dialog.findViewById(R.id.svbar));
        colorPicker.addOpacityBar((OpacityBar) this.dialog.findViewById(R.id.opacitybar));
        colorPicker.requestFocus();
        colorPicker.setColor(getResources().getColor(R.color.ColorLightBlue));
        colorPicker.setOldCenterColor(getResources().getColor(R.color.ColorLightBlue));
        imageView.setBackgroundColor(getResources().getColor(R.color.ColorLightBlue));
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.2
            @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
            public void onColorChanged(int i) {
                imageView.setBackgroundColor(i);
            }
        });
        ((TextView) this.dialog.findViewById(R.id.lbl_Create_Edit_Album)).setText(getResources().getString(R.string.add_folder));
        editText.setHint(R.string.add_folder_hint);
        ((LinearLayout) this.dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String trim = editText.getText().toString().trim();
                if (trim.equals("") || trim.isEmpty()) {
                    Toast.makeText(NotesFoldersActivity.this, "Enter folder Name!", 0).show();
                    return;
                }
                NotesFoldersActivity.this.createFolder(trim, String.valueOf(colorPicker.getColor()));
                Log.i("color", String.valueOf(colorPicker.getColor()));
            }
        });
        ((LinearLayout) this.dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NotesFoldersActivity.this.dialog.dismiss();
            }
        });
        this.dialog.show();
    }

    public void createFolder(String str, String str2) {
        File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES, str);
        this.constants.getClass();
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("NotesFolderName");
        sb.append(" = '");
        sb.append(str);
        sb.append("' AND ");
        this.constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        String concat = "SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ".concat(sb.toString());
        String currentDate = this.notesCommon.getCurrentDate();
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            NotesFolderDB_Pojo notesFolderDB_Pojo = new NotesFolderDB_Pojo();
            notesFolderDB_Pojo.setNotesFolderName(str);
            notesFolderDB_Pojo.setNotesFolderLocation(file.getAbsolutePath());
            notesFolderDB_Pojo.setNotesFolderCreatedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderModifiedDate(currentDate);
            notesFolderDB_Pojo.setNotesFolderFilesSortBy(1);
            notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
            notesFolderDB_Pojo.setNotesFolderColor(str2);
            if (!this.notesFolderDAL.IsFolderAlreadyExist(concat)) {
                this.notesFolderDAL.addNotesFolderInfoInDatabase(notesFolderDB_Pojo);
                Toast.makeText(this, getResources().getString(R.string.note_folder_created_successfully), 0).show();
                setGridview();
                this.dialog.dismiss();
                return;
            }
            Toast.makeText(this, getResources().getString(R.string.note_folder_exists), 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renameFolder(NotesFolderDB_Pojo notesFolderDB_Pojo, String str, String str2) {
        File file = new File(notesFolderDB_Pojo.getNotesFolderLocation());
        File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES, str);
        if (str.equals(notesFolderDB_Pojo.getNotesFolderName())) {
            notesFolderDB_Pojo.setNotesFolderName(str);
            notesFolderDB_Pojo.setNotesFolderLocation(file2.getAbsolutePath());
            notesFolderDB_Pojo.setNotesFolderModifiedDate(this.notesCommon.getCurrentDate());
            notesFolderDB_Pojo.setNotesFolderColor(str2);
            notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
            NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
            this.constants.getClass();
            notesFolderDAL.updateNotesFolderFromDatabase(notesFolderDB_Pojo, "NotesFolderId", String.valueOf(notesFolderDB_Pojo.getNotesFolderId()));
            updateNotesFolderPath(notesFolderDB_Pojo.getNotesFolderId(), str, file2.getAbsolutePath());
            setGridview();
            Toast.makeText(this, getResources().getString(R.string.note_folder_renamed_successfully), 0).show();
            return;
        }
        if (file.exists()) {
            NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
            this.constants.getClass();
            sb.append("NotesFolderId");
            sb.append(" = ");
            sb.append(notesFolderDB_Pojo.getNotesFolderId());
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("NotesFolderIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            if (notesFolderDAL2.IsFolderAlreadyExist(sb.toString())) {
                NotesFolderDAL notesFolderDAL3 = this.notesFolderDAL;
                StringBuilder sb2 = new StringBuilder();
                this.constants.getClass();
                sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
                this.constants.getClass();
                sb2.append("NotesFolderName");
                sb2.append(" = '");
                sb2.append(str);
                sb2.append("' AND ");
                this.constants.getClass();
                sb2.append("NotesFolderIsDecoy");
                sb2.append(" = ");
                sb2.append(SecurityLocksCommon.IsFakeAccount);
                if (notesFolderDAL3.IsFolderAlreadyExist(sb2.toString())) {
                    Toast.makeText(this, getResources().getString(R.string.note_folder_exists), 0).show();
                } else if (file.renameTo(file2)) {
                    notesFolderDB_Pojo.setNotesFolderName(str);
                    notesFolderDB_Pojo.setNotesFolderLocation(file2.getAbsolutePath());
                    notesFolderDB_Pojo.setNotesFolderModifiedDate(this.notesCommon.getCurrentDate());
                    notesFolderDB_Pojo.setNotesFolderIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    notesFolderDB_Pojo.setNotesFolderColor(str2);
                    NotesFolderDAL notesFolderDAL4 = this.notesFolderDAL;
                    this.constants.getClass();
                    notesFolderDAL4.updateNotesFolderFromDatabase(notesFolderDB_Pojo, "NotesFolderId", String.valueOf(notesFolderDB_Pojo.getNotesFolderId()));
                    updateNotesFolderPath(notesFolderDB_Pojo.getNotesFolderId(), str, file2.getAbsolutePath());
                    setGridview();
                }
                Toast.makeText(this, getResources().getString(R.string.note_folder_renamed_successfully), 0).show();
                return;
            }
        }
        Toast.makeText(this, getResources().getString(R.string.note_folder_exists), 0).show();
    }

    public boolean deleteFolder(int i, String str) {
        File[] listFiles;
        File file = new File(str);
        NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderId");
        sb.append(" = ");
        sb.append(i);
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        if (notesFolderDAL.IsFolderAlreadyExist(sb.toString())) {
            NotesFolderDAL notesFolderDAL2 = this.notesFolderDAL;
            this.constants.getClass();
            notesFolderDAL2.deleteNotesFolderFromDatabase("NotesFolderId", String.valueOf(i));
        }
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                if (file2.exists()) {
                    file2.delete();
                    NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
                    this.constants.getClass();
                    notesFilesDAL.deleteNotesFileFromDatabase("NotesFolderId", String.valueOf(i));
                }
            }
        }
        return file.delete();
    }

    public void updateNotesFolderPath(int i, String str, String str2) {
        new ArrayList();
        NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderId");
        sb.append(" = ");
        sb.append(String.valueOf(i));
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("NotesFileIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        for (NotesFileDB_Pojo notesFileDB_Pojo : notesFilesDAL.getAllNotesFileInfoFromDatabase(sb.toString())) {
            notesFileDB_Pojo.setNotesFileLocation(str2 + File.separator + notesFileDB_Pojo.getNotesFileName() + StorageOptionsCommon.NOTES_FILE_EXTENSION);
            NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
            this.constants.getClass();
            notesFilesDAL2.updateNotesFileLocationInDatabase(notesFileDB_Pojo, "NotesFolderId", String.valueOf(i));
        }
    }

    /* loaded from: classes2.dex */
    private class NotesFolderListeners {
        private NotesFolderListeners() {
        }

        /* loaded from: classes2.dex */
        public class ClickListener implements View.OnClickListener {
            public ClickListener() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (NotesFoldersActivity.this.isEdittable && NotesFoldersActivity.this.folderPosition >= 0) {
                    switch (view.getId()) {
                        case R.id.iv_NotesFolderDelete /* 2131296568 */:
                            if (NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition).getNotesFolderName().equals("My Notes")) {
                                Toast.makeText(NotesFoldersActivity.this, "Default folder can not be deleted", 0).show();
                                return;
                            }
                            NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                            NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                            NotesFoldersActivity.this.isEdittable = false;
                            NotesFoldersActivity.this.setGridview();
                            return;
                        case R.id.iv_NotesFolderRename /* 2131296569 */:
                            if (NotesFoldersActivity.this.notesFolderPojoList.get(NotesFoldersActivity.this.folderPosition).getNotesFolderName().equals("My Notes")) {
                                Toast.makeText(NotesFoldersActivity.this, "Default folder can not be renamed", 0).show();
                                return;
                            }
                            NotesFoldersActivity.this.show_Dialog(view, NotesFoldersActivity.this.folderPosition);
                            NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                            NotesFoldersActivity.this.isEdittable = false;
                            NotesFoldersActivity.this.setGridview();
                            return;
                        default:
                            return;
                    }
                }
            }
        }

        /* loaded from: classes2.dex */
        public class ItemClickListener implements AdapterView.OnItemClickListener {
            public ItemClickListener() {
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (NotesFoldersActivity.this.isEdittable) {
                    NotesFoldersActivity.this.isEdittable = false;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                    NotesFoldersActivity.this.notesFolderadapter.setFocusedPosition(i);
                    NotesFoldersActivity.this.notesFolderadapter.setIsEdit(NotesFoldersActivity.this.isEdittable);
                    if (!NotesFoldersActivity.this.isGridview) {
                        NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                    } else {
                        NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(2);
                    }
                    NotesFoldersActivity.this.gv_NotesFolder.setAdapter((ListAdapter) NotesFoldersActivity.this.notesFolderadapter);
                    NotesFoldersActivity.this.notesFolderadapter.notifyDataSetChanged();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
                NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                NotesCommon.CurrentNotesFolder = NotesFoldersActivity.this.notesFolderPojoList.get(i).getNotesFolderName();
                NotesCommon.CurrentNotesFolderId = NotesFoldersActivity.this.notesFolderPojoList.get(i).getNotesFolderId();
                NotesFoldersActivity.this.startActivity(new Intent(NotesFoldersActivity.this, NotesFilesActivity.class));
                NotesFoldersActivity.this.finish();
            }
        }

        /* loaded from: classes2.dex */
        public class ItemLongClickListeners implements AdapterView.OnItemLongClickListener {
            public ItemLongClickListeners() {
            }

            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (NotesFoldersActivity.this.isEdittable) {
                    NotesFoldersActivity.this.isEdittable = false;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(8);
                } else {
                    NotesFoldersActivity.this.isEdittable = true;
                    NotesFoldersActivity.this.ll_NotesFolderEdit.setVisibility(0);
                }
                NotesFoldersActivity.this.folderPosition = i;
                NotesFoldersActivity.this.notesFolderadapter.setFocusedPosition(i);
                NotesFoldersActivity.this.notesFolderadapter.setIsEdit(NotesFoldersActivity.this.isEdittable);
                if (!NotesFoldersActivity.this.isGridview) {
                    NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(1);
                } else {
                    NotesFoldersActivity.this.gv_NotesFolder.setNumColumns(2);
                }
                NotesFoldersActivity.this.gv_NotesFolder.setAdapter((ListAdapter) NotesFoldersActivity.this.notesFolderadapter);
                NotesFoldersActivity.this.notesFolderadapter.notifyDataSetChanged();
                return true;
            }
        }
    }

    public void show_Dialog(View view, final int i) {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        switch (view.getId()) {
            case R.id.iv_NotesFolderDelete /* 2131296568 */:
                dialog.setContentView(R.layout.confirmation_message_box);
                ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(getResources().getString(R.string.delete_toast) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + this.notesFolderPojoList.get(i).getNotesFolderName() + " ?");
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.8
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        NotesFoldersActivity notesFoldersActivity = NotesFoldersActivity.this;
                        if (notesFoldersActivity.deleteFolder(notesFoldersActivity.notesFolderPojoList.get(i).getNotesFolderId(), NotesFoldersActivity.this.notesFolderPojoList.get(i).getNotesFolderLocation())) {
                            NotesFoldersActivity notesFoldersActivity2 = NotesFoldersActivity.this;
                            Toast.makeText(notesFoldersActivity2, notesFoldersActivity2.getResources().getString(R.string.note_folder_deleted_successfully), 0).show();
                            NotesFoldersActivity.this.setGridview();
                        } else {
                            NotesFoldersActivity notesFoldersActivity3 = NotesFoldersActivity.this;
                            Toast.makeText(notesFoldersActivity3, notesFoldersActivity3.getResources().getString(R.string.note_folder_not_deleted), 0).show();
                        }
                        dialog.dismiss();
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.9
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.iv_NotesFolderRename /* 2131296569 */:
                dialog.setContentView(R.layout.dialog_add_notes_folder);
                TextView textView = (TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
                LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
                final EditText editText = (EditText) dialog.findViewById(R.id.et_folderName);
                final ImageView imageView = (ImageView) dialog.findViewById(R.id.iv_selectedColor);
                final ColorPicker colorPicker = (ColorPicker) dialog.findViewById(R.id.folder_colorPicker);
                colorPicker.addSVBar((SVBar) dialog.findViewById(R.id.svbar));
                colorPicker.addOpacityBar((OpacityBar) dialog.findViewById(R.id.opacitybar));
                colorPicker.requestFocus();
                editText.setText(this.notesFolderPojoList.get(i).getNotesFolderName());
                try {
                    int parseInt = Integer.parseInt(this.notesFolderPojoList.get(i).getNotesFolderColor());
                    colorPicker.setColor(parseInt);
                    colorPicker.setOldCenterColor(parseInt);
                    imageView.setBackgroundColor(parseInt);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.5
                    @Override // com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener
                    public void onColorChanged(int i2) {
                        imageView.setBackgroundColor(i2);
                    }
                });
                textView.setText("Rename");
                editText.setHint(R.string.add_folder_hint);
                linearLayout.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        String trim = editText.getText().toString().trim();
                        if (trim.equals("") || trim.isEmpty()) {
                            Toast.makeText(NotesFoldersActivity.this, "Enter Folder name", 0).show();
                            return;
                        }
                        NotesFoldersActivity notesFoldersActivity = NotesFoldersActivity.this;
                        notesFoldersActivity.renameFolder(notesFoldersActivity.notesFolderPojoList.get(i), trim, String.valueOf(colorPicker.getColor()));
                        dialog.dismiss();
                    }
                });
                linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.7
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog.show();
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService("layout_inflater")).inflate(R.layout.popup_window_expandable, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(inflate, -2, -2, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        final ArrayList arrayList = new ArrayList();
        final HashMap hashMap = new HashMap();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.expListview);
        arrayList.add(getResources().getString(R.string.view_by));
        arrayList2.add(getResources().getString(R.string.list));
        arrayList2.add(getResources().getString(R.string.tile));
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add(getResources().getString(R.string.sort_by));
        arrayList3.add(getResources().getString(R.string.name));
        arrayList3.add(getResources().getString(R.string.time));
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFoldersActivity.10
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals("Tile")) {
                    NotesFoldersActivity.this.isGridview = true;
                    NotesFoldersActivity.this.commonSharedPreferences.set_viewByNotesFolder(NotesFoldersActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals("List")) {
                    NotesFoldersActivity.this.isGridview = false;
                    NotesFoldersActivity.this.commonSharedPreferences.set_viewByNotesFolder(NotesFoldersActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals("Name")) {
                    NotesFoldersActivity.this.sortBy = 0;
                    NotesFoldersActivity.this.commonSharedPreferences.set_sortByNotesFolder(NotesFoldersActivity.this.sortBy);
                }
                if (str.equals("Time")) {
                    NotesFoldersActivity.this.sortBy = 1;
                    NotesFoldersActivity.this.commonSharedPreferences.set_sortByNotesFolder(NotesFoldersActivity.this.sortBy);
                }
                NotesFoldersActivity.this.setGridview();
                popupWindow.dismiss();
                return false;
            }
        });
        if (!this.IsSortingDropdown) {
            Toolbar toolbar = this.toolbar;
            popupWindow.showAsDropDown(toolbar, toolbar.getWidth(), 0);
            this.IsSortingDropdown = true;
            return;
        }
        popupWindow.dismiss();
        this.IsSortingDropdown = false;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.gv_NotesFolder.setNumColumns(Utilities.getNoOfColumns(this, configuration.orientation, true));
        setGridview();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.isEdittable) {
            this.isEdittable = false;
            this.ll_NotesFolderEdit.setVisibility(8);
            setGridview();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }
}
