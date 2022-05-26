package com.example.gallerylock.todolist;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerylock.R;
import com.example.gallerylock.adapter.ExpandableListAdapter1;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.wallet.CommonSharedPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes2.dex */
public class ToDoActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    ToDoListAdapter adapter;
    CommonSharedPreferences commonSharedPreferences;
    Constants constants;
    FloatingActionButton fab_AddToDoTask;
    GridLayoutManager glm;
    LinearLayout ll_NotesFolderEdit;
    LinearLayout ll_anchor;
    LinearLayout ll_emptyToDo;
    RecyclerView recList;
    SecurityLocksSharedPreferences securityLocksSharedPreferences;
    private SensorManager sensorManager;
    int sortBy;
    public ArrayList<ToDoDB_Pojo> toDoList;
    ToDoListeners toDoListeners;
    ToDoDAL todoDAL;
    private Toolbar toolbar;
    TextView toolbar_title;
    int viewBy = 0;
    boolean IsSortingDropdown = false;
    private String GA_ToDoLists = "To Do Lists";

    /* loaded from: classes2.dex */
    public enum SortBy {
        Name,
        CreatedTime,
        ModifiedTime
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
        setContentView(R.layout.activity_to_do);
        getWindow().addFlags(128);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        this.fab_AddToDoTask = (FloatingActionButton) findViewById(R.id.fab_AddToDoTask);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        this.recList = (RecyclerView) findViewById(R.id.toDoCardList);
        this.ll_emptyToDo = (LinearLayout) findViewById(R.id.ll_emptyToDo);
        this.constants = new Constants();
        this.toDoListeners = new ToDoListeners();
        this.todoDAL = new ToDoDAL(this);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.securityLocksSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        this.commonSharedPreferences = CommonSharedPreferences.GetObject(this);
        SecurityLocksCommon.IsAppDeactive = true;
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            this.toolbar = toolbar;
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("To do Lists");
            this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.sortBy = this.commonSharedPreferences.get_sortByToDoFile();
        this.viewBy = this.commonSharedPreferences.get_viewByToDoFile();
        FloatingActionButton floatingActionButton = this.fab_AddToDoTask;
        ToDoListeners toDoListeners = this.toDoListeners;
        toDoListeners.getClass();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                ToDoActivity.this.startActivity(new Intent(ToDoActivity.this, AddToDoActivity.class));
                ToDoActivity.this.finish();
                ToDoActivity.this.overridePendingTransition(17432576, 17432577);
            }
        });
        this.recList.setHasFixedSize(false);
        setRecyclerView();
    }

    public void setRecyclerView() {
        getData();
        if (this.viewBy == ViewBy.List.ordinal()) {
            this.glm = new GridLayoutManager(this, Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            this.glm = new GridLayoutManager(this, Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), true));
        } else {
            this.glm = new GridLayoutManager(this, Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), false));
        }
        this.recList.setLayoutManager(this.glm);
        if (this.toDoList.size() > 0) {
            ToDoListAdapter toDoListAdapter = new ToDoListAdapter(this, this.toDoList, this);
            this.adapter = toDoListAdapter;
            toDoListAdapter.setViewBy(this.viewBy);
            this.recList.setAdapter(this.adapter);
            this.ll_emptyToDo.setVisibility(View.GONE);
            return;
        }
        this.ll_emptyToDo.setVisibility(View.VISIBLE);
    }

    public void getData() {
        if (this.sortBy == SortBy.CreatedTime.ordinal()) {
            ToDoDAL toDoDAL = this.todoDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb.append("ToDoIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("ToDoCreatedDate");
            sb.append(" DESC");
            this.toDoList = toDoDAL.getAllToDoInfoFromDatabase(sb.toString());
        } else if (this.sortBy == SortBy.ModifiedTime.ordinal()) {
            ToDoDAL toDoDAL2 = this.todoDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb2.append("ToDoIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("ToDoModifiedDate");
            sb2.append(" DESC");
            this.toDoList = toDoDAL2.getAllToDoInfoFromDatabase(sb2.toString());
        } else if (this.sortBy == SortBy.Name.ordinal()) {
            ToDoDAL toDoDAL3 = this.todoDAL;
            StringBuilder sb3 = new StringBuilder();
            this.constants.getClass();
            sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb3.append("ToDoIsDecoy");
            sb3.append(" = ");
            sb3.append(SecurityLocksCommon.IsFakeAccount);
            sb3.append(" ORDER BY ");
            this.constants.getClass();
            sb3.append("ToDoName");
            sb3.append(" COLLATE NOCASE ASC");
            this.toDoList = toDoDAL3.getAllToDoInfoFromDatabase(sb3.toString());
        } else {
            ToDoDAL toDoDAL4 = this.todoDAL;
            StringBuilder sb4 = new StringBuilder();
            this.constants.getClass();
            sb4.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ");
            this.constants.getClass();
            sb4.append("ToDoIsDecoy");
            sb4.append(" = ");
            sb4.append(SecurityLocksCommon.IsFakeAccount);
            sb4.append(" ORDER BY ");
            this.constants.getClass();
            sb4.append("ToDoName");
            sb4.append(" COLLATE NOCASE ASC");
            this.toDoList = toDoDAL4.getAllToDoInfoFromDatabase(sb4.toString());
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
        arrayList3.add(getResources().getString(R.string.Created_time));
        arrayList3.add(getResources().getString(R.string.modified_time));
        hashMap.put((String) arrayList.get(1), arrayList3);
        expandableListView.setAdapter(new ExpandableListAdapter1(this, arrayList, hashMap));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoActivity.1
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals("Detail")) {
                    ToDoActivity.this.viewBy = 0;
                    ToDoActivity.this.commonSharedPreferences.set_viewByToDoFile(ToDoActivity.this.viewBy);
                }
                if (str.equals("List")) {
                    ToDoActivity.this.viewBy = 1;
                    ToDoActivity.this.commonSharedPreferences.set_viewByToDoFile(ToDoActivity.this.viewBy);
                }
                if (str.equals("Tile")) {
                    ToDoActivity.this.viewBy = 2;
                    ToDoActivity.this.commonSharedPreferences.set_viewByToDoFile(ToDoActivity.this.viewBy);
                }
                if (str.equals("Created Time")) {
                    ToDoActivity.this.sortBy = 1;
                    ToDoActivity.this.commonSharedPreferences.set_sortByToDoFile(ToDoActivity.this.sortBy);
                } else if (str.equals("Modified Time")) {
                    ToDoActivity.this.sortBy = 2;
                    ToDoActivity.this.commonSharedPreferences.set_sortByToDoFile(ToDoActivity.this.sortBy);
                } else {
                    ToDoActivity.this.sortBy = 0;
                    ToDoActivity.this.commonSharedPreferences.set_sortByToDoFile(ToDoActivity.this.sortBy);
                }
                ToDoActivity.this.setRecyclerView();
                popupWindow.dismiss();
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

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view_sort, menu);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoActivity.2
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                ArrayList<ToDoDB_Pojo> arrayList = new ArrayList<>();
                if (str.length() > 0) {
                    Iterator<ToDoDB_Pojo> it = ToDoActivity.this.toDoList.iterator();
                    while (it.hasNext()) {
                        ToDoDB_Pojo next = it.next();
                        if (next.getToDoFileName().toLowerCase(Locale.getDefault()).contains(str)) {
                            arrayList.add(next);
                        }
                    }
                } else {
                    arrayList = ToDoActivity.this.toDoList;
                }
                ToDoActivity.this.adapter.setAdapterData(arrayList);
                ToDoActivity.this.adapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        } else if (itemId == R.id.action_viewSort) {
            this.IsSortingDropdown = false;
            showPopupWindow();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
        overridePendingTransition(17432576, 17432577);
    }

    /* loaded from: classes2.dex */
    private class ToDoListeners {
        private ToDoListeners() {
        }

        /* loaded from: classes2.dex */
        public class MyOnClickListeners implements View.OnClickListener {
            public MyOnClickListeners() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                ToDoActivity.this.startActivity(new Intent(ToDoActivity.this, AddToDoActivity.class));
                ToDoActivity.this.finish();
                ToDoActivity.this.overridePendingTransition(17432576, 17432577);
            }
        }
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
