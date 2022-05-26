package com.example.gallerylock.notes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.wallet.CommonSharedPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class NotesFilesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    CommonSharedPreferences commonSharedPreference;
    Constants constants;
    NotesFolderDB_Pojo currentFolderDBInfo;
    Dialog dialog;
    List<Integer> focusedPosition;
    GridView gv_NotesFiles;
    AppCompatImageView iv_NotesFileDelete;
    AppCompatImageView iv_NotesFileMove;
    LinearLayout ll_NotesFileEdit;
    LinearLayout ll_noNotes;
    FloatingActionButton mFab;
    NotesCommon notesCommon;
    List<NotesFileDB_Pojo> notesFileDB_PojoList;
    NotesFilesDAL notesFilesDAL;
    NotesFilesGridViewAdapter notesFilesGridViewAdapter;
    NotesFilesListeners notesFilesListeners;
    NotesFolderDAL notesFolderDAL;
    List<NotesFolderDB_Pojo> notesFolderDB_PojoList;
    private SensorManager sensorManager;
    int sortBy;
    Toolbar toolbar;
    TextView toolbar_title;
    int viewBy = 0;
    boolean isEdittable = false;
    boolean IsSortingDropdown = false;

    /* loaded from: classes2.dex */
    public enum SortBy {
        Name,
        Time,
        Size
    }

    /* loaded from: classes2.dex */
    public enum ViewBy {
        Detail,
        List,
        Tile
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
        setContentView(R.layout.activity_notes_files);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_NotesFiles = (GridView) findViewById(R.id.gv_NotesFiles);
        this.mFab = (FloatingActionButton) findViewById(R.id.fabbutton_notesFiles);
        this.ll_NotesFileEdit = (LinearLayout) findViewById(R.id.ll_NotesFileEdit);
        this.ll_noNotes = (LinearLayout) findViewById(R.id.ll_noNotes);
        this.iv_NotesFileMove = (AppCompatImageView) findViewById(R.id.iv_NotesFileMove);
        this.iv_NotesFileDelete = (AppCompatImageView) findViewById(R.id.iv_NotesFileDelete);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.notesFileDB_PojoList = new ArrayList();
        this.notesCommon = new NotesCommon();
        this.notesFilesDAL = new NotesFilesDAL(this);
        this.notesFolderDAL = new NotesFolderDAL(this);
        this.notesFilesListeners = new NotesFilesListeners();
        this.notesFolderDB_PojoList = new ArrayList();
        this.focusedPosition = new ArrayList();
        this.constants = new Constants();
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
        sb.append("NotesFolderModifiedDate");
        sb.append(" DESC");
        this.notesFolderDB_PojoList = notesFolderDAL.getAllNotesFolderInfoFromDatabase(sb.toString());
        this.currentFolderDBInfo = new NotesFolderDB_Pojo();
        SecurityLocksCommon.IsAppDeactive = true;
        getCurrentFolder();
        this.commonSharedPreference = CommonSharedPreferences.GetObject(this);
        this.sortBy = this.currentFolderDBInfo.getNotesFolderFilesSortBy();
        this.viewBy = this.commonSharedPreference.get_viewByNotesFile();
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle(NotesCommon.CurrentNotesFolder);
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        setGridview();
        GridView gridView = this.gv_NotesFiles;
        NotesFilesListeners notesFilesListeners = this.notesFilesListeners;
        notesFilesListeners.getClass();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (NotesFilesActivity.this.isEdittable) {
                    if (NotesFilesActivity.this.focusedPosition.contains(Integer.valueOf(i))) {
                        NotesFilesActivity.this.focusedPosition.remove(NotesFilesActivity.this.focusedPosition.indexOf(Integer.valueOf(i)));
                        NotesFilesActivity.this.notesFilesGridViewAdapter.removeFocusedPosition(i);
                        if (NotesFilesActivity.this.focusedPosition.size() == 0) {
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                            NotesFilesActivity.this.focusedPosition.clear();
                        }
                    } else {
                        NotesFilesActivity.this.focusedPosition.add(Integer.valueOf(i));
                        NotesFilesActivity.this.notesFilesGridViewAdapter.setFocusedPosition(i);
                    }
                    NotesFilesActivity.this.notesFilesGridViewAdapter.setIsEdit(NotesFilesActivity.this.isEdittable);
                    if (NotesFilesActivity.this.viewBy == 2) {
                        NotesFilesActivity.this.gv_NotesFiles.setNumColumns(2);
                    } else {
                        NotesFilesActivity.this.gv_NotesFiles.setNumColumns(1);
                    }
                    NotesFilesActivity.this.gv_NotesFiles.setAdapter((ListAdapter) NotesFilesActivity.this.notesFilesGridViewAdapter);
                    NotesFilesActivity.this.notesFilesGridViewAdapter.notifyDataSetChanged();
                    return;
                }
                NotesCommon.isEdittingNote = true;
                SecurityLocksCommon.IsAppDeactive = false;
                NotesCommon.CurrentNotesFile = NotesFilesActivity.this.notesFileDB_PojoList.get(i).getNotesFileName();
                NotesFilesActivity.this.startActivity(new Intent(NotesFilesActivity.this, MyNoteActivity.class));
                NotesFilesActivity.this.finish();
            }
        });
        GridView gridView2 = this.gv_NotesFiles;
        NotesFilesListeners notesFilesListeners2 = this.notesFilesListeners;
        notesFilesListeners2.getClass();
        gridView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (NotesFilesActivity.this.focusedPosition.contains(Integer.valueOf(i))) {
                    NotesFilesActivity.this.focusedPosition.remove(NotesFilesActivity.this.focusedPosition.indexOf(Integer.valueOf(i)));
                    NotesFilesActivity.this.notesFilesGridViewAdapter.removeFocusedPosition(i);
                } else {
                    NotesFilesActivity.this.focusedPosition.add(Integer.valueOf(i));
                    NotesFilesActivity.this.notesFilesGridViewAdapter.setFocusedPosition(i);
                }
                if (NotesFilesActivity.this.focusedPosition.size() > 0) {
                    NotesFilesActivity.this.isEdittable = true;
                    NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(0);
                } else {
                    NotesFilesActivity.this.isEdittable = false;
                    NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                }
                NotesFilesActivity.this.notesFilesGridViewAdapter.setIsEdit(NotesFilesActivity.this.isEdittable);
                if (NotesFilesActivity.this.viewBy == 2) {
                    NotesFilesActivity.this.gv_NotesFiles.setNumColumns(2);
                } else {
                    NotesFilesActivity.this.gv_NotesFiles.setNumColumns(1);
                }
                NotesFilesActivity.this.gv_NotesFiles.setAdapter((ListAdapter) NotesFilesActivity.this.notesFilesGridViewAdapter);
                NotesFilesActivity.this.notesFilesGridViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
        AppCompatImageView appCompatImageView = this.iv_NotesFileMove;
        NotesFilesListeners notesFilesListeners3 = this.notesFilesListeners;
        notesFilesListeners3.getClass();
        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NotesFilesActivity.this.isEdittable && NotesFilesActivity.this.focusedPosition.size() > 0) {
                    switch (view.getId()) {
                        case R.id.iv_NotesFileDelete /* 2131296566 */:
                            NotesFilesActivity.this.noteDeleteDialog();
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.setGridview();
                            return;
                        case R.id.iv_NotesFileMove /* 2131296567 */:
                            NotesFilesActivity.this.NoteMoveDialog();
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.setGridview();
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        AppCompatImageView appCompatImageView2 = this.iv_NotesFileDelete;
        NotesFilesListeners notesFilesListeners4 = this.notesFilesListeners;
        notesFilesListeners4.getClass();
        appCompatImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NotesFilesActivity.this.isEdittable && NotesFilesActivity.this.focusedPosition.size() > 0) {
                    switch (view.getId()) {
                        case R.id.iv_NotesFileDelete /* 2131296566 */:
                            NotesFilesActivity.this.noteDeleteDialog();
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.setGridview();
                            return;
                        case R.id.iv_NotesFileMove /* 2131296567 */:
                            NotesFilesActivity.this.NoteMoveDialog();
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.setGridview();
                            return;
                        default:
                            return;
                    }
                }
            }
        });
    }

    public void getCurrentFolder() {
        NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
        StringBuilder sb = new StringBuilder();
        this.constants.getClass();
        sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFolder WHERE ");
        this.constants.getClass();
        sb.append("NotesFolderId");
        sb.append(" = ");
        sb.append(NotesCommon.CurrentNotesFolderId);
        sb.append(" AND ");
        this.constants.getClass();
        sb.append("NotesFolderIsDecoy");
        sb.append(" = ");
        sb.append(SecurityLocksCommon.IsFakeAccount);
        this.currentFolderDBInfo = notesFolderDAL.getNotesFolderInfoFromDatabase(sb.toString());
    }

    public void updateCurrentFolderSortBy() {
        this.currentFolderDBInfo.setNotesFolderFilesSortBy(this.sortBy);
        NotesFolderDAL notesFolderDAL = this.notesFolderDAL;
        NotesFolderDB_Pojo notesFolderDB_Pojo = this.currentFolderDBInfo;
        this.constants.getClass();
        notesFolderDAL.updateNotesFolderFromDatabase(notesFolderDB_Pojo, "NotesFolderId", String.valueOf(this.currentFolderDBInfo.getNotesFolderId()));
    }

    public void updateCurrentFolderViewBy() {
        this.commonSharedPreference.set_viewByNotesFiles(this.viewBy);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_cloud_view_sort, menu);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFilesActivity.1
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                List<NotesFileDB_Pojo> arrayList = new ArrayList<>();
                if (str.length() > 0) {
                    for (NotesFileDB_Pojo notesFileDB_Pojo : NotesFilesActivity.this.notesFileDB_PojoList) {
                        if (notesFileDB_Pojo.getNotesFileName().toLowerCase().contains(str)) {
                            arrayList.add(notesFileDB_Pojo);
                        }
                    }
                } else {
                    arrayList = NotesFilesActivity.this.notesFileDB_PojoList;
                }
                NotesFilesActivity.this.bindSearchResult(arrayList);
                return true;
            }
        });
        return true;
    }

    public void bindSearchResult(List<NotesFileDB_Pojo> list) {
        this.notesFilesGridViewAdapter = new NotesFilesGridViewAdapter(this, list);
        if (this.viewBy == ViewBy.List.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), true));
        } else {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        }
        this.notesFilesGridViewAdapter.setViewBy(this.viewBy);
        this.gv_NotesFiles.setAdapter((ListAdapter) this.notesFilesGridViewAdapter);
        this.notesFilesGridViewAdapter.notifyDataSetChanged();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, NotesFoldersActivity.class));
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
            /*InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.NotesFolder.ordinal();
            startActivity(new Intent(this, InAppPurchaseActivity.class));
            finish();*/
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("NotesFolderId");
            sb2.append(" = ");
            sb2.append(NotesCommon.CurrentNotesFolderId);
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb2.toString()));
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("NotesFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("NotesFileName");
            sb.append(" COLLATE NOCASE ASC");
            this.notesFileDB_PojoList = notesFilesDAL.getAllNotesFileInfoFromDatabase(sb.toString());
        } else if (this.sortBy == SortBy.Size.ordinal()) {
            NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
            StringBuilder sb3 = new StringBuilder();
            this.constants.getClass();
            StringBuilder sb4 = new StringBuilder();
            this.constants.getClass();
            sb4.append("NotesFolderId");
            sb4.append(" = ");
            sb4.append(NotesCommon.CurrentNotesFolderId);
            sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb4.toString()));
            sb3.append(" AND ");
            this.constants.getClass();
            sb3.append("NotesFileIsDecoy");
            sb3.append(" = ");
            sb3.append(SecurityLocksCommon.IsFakeAccount);
            sb3.append(" ORDER BY ");
            this.constants.getClass();
            sb3.append("NotesFileSize");
            sb3.append(" DESC");
            this.notesFileDB_PojoList = notesFilesDAL2.getAllNotesFileInfoFromDatabase(sb3.toString());
        } else {
            NotesFilesDAL notesFilesDAL3 = this.notesFilesDAL;
            StringBuilder sb5 = new StringBuilder();
            this.constants.getClass();
            StringBuilder sb6 = new StringBuilder();
            this.constants.getClass();
            sb6.append("NotesFolderId");
            sb6.append(" = ");
            sb6.append(NotesCommon.CurrentNotesFolderId);
            sb5.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ".concat(sb6.toString()));
            sb5.append(" AND ");
            this.constants.getClass();
            sb5.append("NotesFileIsDecoy");
            sb5.append(" = ");
            sb5.append(SecurityLocksCommon.IsFakeAccount);
            sb5.append(" ORDER BY ");
            this.constants.getClass();
            sb5.append("NotesFileModifiedDate");
            sb5.append(" DESC");
            this.notesFileDB_PojoList = notesFilesDAL3.getAllNotesFileInfoFromDatabase(sb5.toString());
        }
        if (this.viewBy == ViewBy.List.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), true));
        } else {
            this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        }
        if (this.notesFileDB_PojoList.size() > 0) {
            NotesFilesGridViewAdapter notesFilesGridViewAdapter = new NotesFilesGridViewAdapter(this, this.notesFileDB_PojoList);
            this.notesFilesGridViewAdapter = notesFilesGridViewAdapter;
            notesFilesGridViewAdapter.setIsEdit(false);
            this.notesFilesGridViewAdapter.setViewBy(this.viewBy);
            this.gv_NotesFiles.setAdapter((ListAdapter) this.notesFilesGridViewAdapter);
            this.notesFilesGridViewAdapter.notifyDataSetChanged();
            this.ll_noNotes.setVisibility(8);
            return;
        }
        this.ll_noNotes.setVisibility(0);
    }

    public void fabClicked(View view) {
        NotesCommon.isEdittingNote = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MyNoteActivity.class));
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.isEdittable) {
            this.notesFilesGridViewAdapter.removeAllFocusedPosition();
            this.focusedPosition.clear();
            this.isEdittable = false;
            this.ll_NotesFileEdit.setVisibility(8);
            setGridview();
            return;
        }
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, NotesFoldersActivity.class));
        finish();
    }

    public boolean deleteNote(int i, String str) {
        File file = new File(str);
        try {
            NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
            this.constants.getClass();
            notesFilesDAL.deleteNotesFileFromDatabase("NotesFileId", String.valueOf(i));
            if (file.exists()) {
                return file.delete();
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public void moveNote(int i, String str) {
        for (int i2 = 0; i2 < this.focusedPosition.size(); i2++) {
            int intValue = this.focusedPosition.get(i2).intValue();
            new NotesFileDB_Pojo();
            NotesFileDB_Pojo notesFileDB_Pojo = this.notesFileDB_PojoList.get(intValue);
            File file = new File(notesFileDB_Pojo.getNotesFileLocation());
            File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.NOTES, str);
            String str2 = file2.getAbsolutePath() + File.separator + notesFileDB_Pojo.getNotesFileName() + StorageOptionsCommon.NOTES_FILE_EXTENSION;
            if (file.exists()) {
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                try {
                    FileUtils.moveToDirectory(file, file2, true);
                    notesFileDB_Pojo.setNotesFileFolderId(i);
                    notesFileDB_Pojo.setNotesFileName(notesFileDB_Pojo.getNotesFileName());
                    notesFileDB_Pojo.setNotesFileModifiedDate(this.notesCommon.getCurrentDate());
                    notesFileDB_Pojo.setNotesFileLocation(str2);
                    notesFileDB_Pojo.setNotesFileIsDecoy(SecurityLocksCommon.IsFakeAccount);
                    NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
                    this.constants.getClass();
                    notesFilesDAL.updateNotesFileInfoInDatabase(notesFileDB_Pojo, "NotesFileId", String.valueOf(this.notesFileDB_PojoList.get(intValue).getNotesFileId()));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, (int) R.string.toast_exists, 0).show();
                }
            } else {
                Toast.makeText(this, (int) R.string.toast_not_exists, 0).show();
            }
        }
        setGridview();
        Toast.makeText(this, (int) R.string.toast_move, 0).show();
    }

    public void NoteMoveDialog() {
        final ArrayList arrayList = new ArrayList();
        for (NotesFolderDB_Pojo notesFolderDB_Pojo : this.notesFolderDB_PojoList) {
            if (!notesFolderDB_Pojo.getNotesFolderName().equals(NotesCommon.CurrentNotesFolder)) {
                arrayList.add(notesFolderDB_Pojo);
            }
        }
        if (arrayList.size() > 0) {
            final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.move_customlistview);
            ListView listView = (ListView) dialog.findViewById(R.id.ListViewfolderslist);
            listView.setAdapter((ListAdapter) new MoveNoteAdapter(this, arrayList));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFilesActivity.2
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    NotesFilesActivity.this.moveNote(((NotesFolderDB_Pojo) arrayList.get(i)).getNotesFolderId(), ((NotesFolderDB_Pojo) arrayList.get(i)).getNotesFolderName());
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        Toast.makeText(this, (int) R.string.no_other_folder_exists, 0).show();
        this.notesFilesGridViewAdapter.removeAllFocusedPosition();
        this.focusedPosition.clear();
    }

    public void noteDeleteDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(false);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(getResources().getString(R.string.delete_note));
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFilesActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                for (int i = 0; i < NotesFilesActivity.this.focusedPosition.size(); i++) {
                    int intValue = NotesFilesActivity.this.focusedPosition.get(i).intValue();
                    NotesFilesActivity notesFilesActivity = NotesFilesActivity.this;
                    if (!notesFilesActivity.deleteNote(notesFilesActivity.notesFileDB_PojoList.get(intValue).getNotesFileId(), NotesFilesActivity.this.notesFileDB_PojoList.get(intValue).getNotesFileLocation())) {
                        NotesFilesActivity notesFilesActivity2 = NotesFilesActivity.this;
                        Toast.makeText(notesFilesActivity2, NotesFilesActivity.this.notesFileDB_PojoList.get(intValue).getNotesFileName() + " could not be deleted", 0).show();
                    }
                }
                NotesFilesActivity.this.setGridview();
                dialog.dismiss();
                NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                NotesFilesActivity.this.focusedPosition.clear();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFilesActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
                NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                NotesFilesActivity.this.focusedPosition.clear();
            }
        });
        dialog.show();
    }

    /* loaded from: classes2.dex */
    private class NotesFilesListeners {
        private NotesFilesListeners() {
        }

        /* loaded from: classes2.dex */
        public class NotesFilesOnClickListener implements View.OnClickListener {
            public NotesFilesOnClickListener() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (NotesFilesActivity.this.isEdittable && NotesFilesActivity.this.focusedPosition.size() > 0) {
                    switch (view.getId()) {
                        case R.id.iv_NotesFileDelete /* 2131296566 */:
                            NotesFilesActivity.this.noteDeleteDialog();
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.setGridview();
                            return;
                        case R.id.iv_NotesFileMove /* 2131296567 */:
                            NotesFilesActivity.this.NoteMoveDialog();
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.setGridview();
                            return;
                        default:
                            return;
                    }
                }
            }
        }

        /* loaded from: classes2.dex */
        public class NotesFilesOnItemClickListener implements AdapterView.OnItemClickListener {
            public NotesFilesOnItemClickListener() {
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (NotesFilesActivity.this.isEdittable) {
                    if (NotesFilesActivity.this.focusedPosition.contains(Integer.valueOf(i))) {
                        NotesFilesActivity.this.focusedPosition.remove(NotesFilesActivity.this.focusedPosition.indexOf(Integer.valueOf(i)));
                        NotesFilesActivity.this.notesFilesGridViewAdapter.removeFocusedPosition(i);
                        if (NotesFilesActivity.this.focusedPosition.size() == 0) {
                            NotesFilesActivity.this.isEdittable = false;
                            NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                            NotesFilesActivity.this.notesFilesGridViewAdapter.removeAllFocusedPosition();
                            NotesFilesActivity.this.focusedPosition.clear();
                        }
                    } else {
                        NotesFilesActivity.this.focusedPosition.add(Integer.valueOf(i));
                        NotesFilesActivity.this.notesFilesGridViewAdapter.setFocusedPosition(i);
                    }
                    NotesFilesActivity.this.notesFilesGridViewAdapter.setIsEdit(NotesFilesActivity.this.isEdittable);
                    if (NotesFilesActivity.this.viewBy == 2) {
                        NotesFilesActivity.this.gv_NotesFiles.setNumColumns(2);
                    } else {
                        NotesFilesActivity.this.gv_NotesFiles.setNumColumns(1);
                    }
                    NotesFilesActivity.this.gv_NotesFiles.setAdapter((ListAdapter) NotesFilesActivity.this.notesFilesGridViewAdapter);
                    NotesFilesActivity.this.notesFilesGridViewAdapter.notifyDataSetChanged();
                    return;
                }
                NotesCommon.isEdittingNote = true;
                SecurityLocksCommon.IsAppDeactive = false;
                NotesCommon.CurrentNotesFile = NotesFilesActivity.this.notesFileDB_PojoList.get(i).getNotesFileName();
                NotesFilesActivity.this.startActivity(new Intent(NotesFilesActivity.this, MyNoteActivity.class));
                NotesFilesActivity.this.finish();
            }
        }

        /* loaded from: classes2.dex */
        public class NotesFilesOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
            public NotesFilesOnItemLongClickListener() {
            }

            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (NotesFilesActivity.this.focusedPosition.contains(Integer.valueOf(i))) {
                    NotesFilesActivity.this.focusedPosition.remove(NotesFilesActivity.this.focusedPosition.indexOf(Integer.valueOf(i)));
                    NotesFilesActivity.this.notesFilesGridViewAdapter.removeFocusedPosition(i);
                } else {
                    NotesFilesActivity.this.focusedPosition.add(Integer.valueOf(i));
                    NotesFilesActivity.this.notesFilesGridViewAdapter.setFocusedPosition(i);
                }
                if (NotesFilesActivity.this.focusedPosition.size() > 0) {
                    NotesFilesActivity.this.isEdittable = true;
                    NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(0);
                } else {
                    NotesFilesActivity.this.isEdittable = false;
                    NotesFilesActivity.this.ll_NotesFileEdit.setVisibility(8);
                }
                NotesFilesActivity.this.notesFilesGridViewAdapter.setIsEdit(NotesFilesActivity.this.isEdittable);
                if (NotesFilesActivity.this.viewBy == 2) {
                    NotesFilesActivity.this.gv_NotesFiles.setNumColumns(2);
                } else {
                    NotesFilesActivity.this.gv_NotesFiles.setNumColumns(1);
                }
                NotesFilesActivity.this.gv_NotesFiles.setAdapter((ListAdapter) NotesFilesActivity.this.notesFilesGridViewAdapter);
                NotesFilesActivity.this.notesFilesGridViewAdapter.notifyDataSetChanged();
                return true;
            }
        }
    }

    public void showPopupWindow() {
        View inflate = ((LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_window_expandable, (ViewGroup) null);
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
        arrayList2.add(getResources().getString(R.string.detail));
        arrayList2.add(getResources().getString(R.string.tile));
        hashMap.put((String) arrayList.get(0), arrayList2);
        arrayList.add(getResources().getString(R.string.sort_by));
        arrayList3.add(getResources().getString(R.string.name));
        arrayList3.add(getResources().getString(R.string.time));
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.notes.NotesFilesActivity.5
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals("Detail")) {
                    NotesFilesActivity.this.viewBy = 0;
                    NotesFilesActivity.this.updateCurrentFolderViewBy();
                }
                if (str.equals("List")) {
                    NotesFilesActivity.this.viewBy = 1;
                    NotesFilesActivity.this.updateCurrentFolderViewBy();
                }
                if (str.equals("Tile")) {
                    NotesFilesActivity.this.viewBy = 2;
                    NotesFilesActivity.this.updateCurrentFolderViewBy();
                }
                if (str.equals("Created Time")) {
                    NotesFilesActivity.this.sortBy = 1;
                    NotesFilesActivity.this.updateCurrentFolderSortBy();
                } else if (str.equals("Modified Time")) {
                    NotesFilesActivity.this.sortBy = 2;
                    NotesFilesActivity.this.updateCurrentFolderSortBy();
                } else {
                    NotesFilesActivity.this.sortBy = 0;
                    NotesFilesActivity.this.updateCurrentFolderSortBy();
                }
                NotesFilesActivity.this.setGridview();
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
        this.gv_NotesFiles.setNumColumns(Utilities.getNoOfColumns(this, configuration.orientation, true));
        setGridview();
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
