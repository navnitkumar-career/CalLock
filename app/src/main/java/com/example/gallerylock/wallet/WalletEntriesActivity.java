package com.example.gallerylock.wallet;

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
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class WalletEntriesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    WalletCategoriesFileDB_Pojo categoriesFileDB_Pojo;
    Constants constants;
    List<WalletEntryFileDB_Pojo> entryFileDB_Pojo;
    WalletEntriesAdapter gvAdapter;
    GridView gv_wallet;
    LinearLayout ll_noWallet;
    FloatingActionButton mFab;
    private SensorManager sensorManager;
    int sortBy;
    private Toolbar toolbar;
    WalletCategoriesDAL walletCategoriesDAL;
    WalletCommon walletCommon;
    WalletEntriesDAL walletEntriesDAL;
    CommonSharedPreferences walletSharedPreferences;
    boolean isEdittable = false;
    boolean IsSortingDropdown = false;
    public boolean isGridview = false;

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
        setContentView(R.layout.activity_wallet);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_wallet = (GridView) findViewById(R.id.gv_wallet);
        this.mFab = (FloatingActionButton) findViewById(R.id.fabbutton);
        this.ll_noWallet = (LinearLayout) findViewById(R.id.ll_noWallet);
        this.walletSharedPreferences = CommonSharedPreferences.GetObject(this);
        this.entryFileDB_Pojo = new ArrayList();
        this.walletEntriesDAL = new WalletEntriesDAL(this);
        this.walletCategoriesDAL = new WalletCategoriesDAL(this);
        this.categoriesFileDB_Pojo = new WalletCategoriesFileDB_Pojo();
        this.walletCommon = new WalletCommon();
        this.constants = new Constants();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.isGridview = this.walletSharedPreferences.get_ViewByWalletEntry() != 0;
        setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        getSupportActionBar().setTitle(WalletCommon.walletCurrentCategoryName);
        Common.applyKitKatTranslucency(this);
        SecurityLocksCommon.IsAppDeactive = true;
        this.mFab.setVisibility(0);
        getCurrentCategory();
        this.sortBy = this.categoriesFileDB_Pojo.getCategoryFileSortBy();
        setGridview();
        this.gv_wallet.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletEntriesActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                SecurityLocksCommon.IsAppDeactive = false;
                WalletCommon.walletCurrentEntryName = WalletEntriesActivity.this.entryFileDB_Pojo.get(i).getEntryFileName();
                WalletEntriesActivity.this.startActivity(new Intent(WalletEntriesActivity.this, WalletEntryActivity.class));
                WalletEntriesActivity.this.finish();
            }
        });
        this.mFab.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletEntriesActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                WalletCommon.walletCurrentEntryName = "";
                WalletEntriesActivity.this.startActivity(new Intent(WalletEntriesActivity.this, WalletEntryActivity.class));
                WalletEntriesActivity.this.finish();
            }
        });
    }

    public void getCurrentCategory() {
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
        sb.append(WalletCommon.WalletCurrentCategoryId);
        this.categoriesFileDB_Pojo = walletCategoriesDAL.getCategoryInfoFromDatabase(sb.toString());
    }

    public void updateCurrentCategorySortBy() {
        this.categoriesFileDB_Pojo.setCategoryFileSortBy(this.sortBy);
        WalletCategoriesDAL walletCategoriesDAL = this.walletCategoriesDAL;
        WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo = this.categoriesFileDB_Pojo;
        this.constants.getClass();
        walletCategoriesDAL.updateCategoryFromDatabase(walletCategoriesFileDB_Pojo, "WalletCategoriesFileId", String.valueOf(this.categoriesFileDB_Pojo.getCategoryFileId()));
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            WalletEntriesDAL walletEntriesDAL = this.walletEntriesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
            this.constants.getClass();
            sb.append("WalletEntryFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" AND ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileId");
            sb.append(" = ");
            sb.append(WalletCommon.WalletCurrentCategoryId);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("WalletEntryFileName");
            sb.append(" COLLATE NOCASE ASC");
            this.entryFileDB_Pojo = walletEntriesDAL.getAllEntriesInfoFromDatabase(sb.toString());
        } else {
            WalletEntriesDAL walletEntriesDAL2 = this.walletEntriesDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletEntries WHERE ");
            this.constants.getClass();
            sb2.append("WalletEntryFileIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" AND ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileId");
            sb2.append(" = ");
            sb2.append(WalletCommon.WalletCurrentCategoryId);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("WalletEntryFileModifiedDate");
            sb2.append(" DESC");
            this.entryFileDB_Pojo = walletEntriesDAL2.getAllEntriesInfoFromDatabase(sb2.toString());
        }
        if (this.entryFileDB_Pojo.size() > 0) {
            WalletEntriesAdapter walletEntriesAdapter = new WalletEntriesAdapter(this, this.entryFileDB_Pojo);
            this.gvAdapter = walletEntriesAdapter;
            walletEntriesAdapter.setFocusedPosition(0);
            this.gvAdapter.setIsEdit(false);
            this.gvAdapter.setIsGridview(this.isGridview);
            this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
            this.gv_wallet.setAdapter((ListAdapter) this.gvAdapter);
            this.gvAdapter.notifyDataSetChanged();
            this.ll_noWallet.setVisibility(8);
            return;
        }
        this.ll_noWallet.setVisibility(0);
    }

    public void bindSearchResult(List<WalletEntryFileDB_Pojo> list) {
        this.gvAdapter = new WalletEntriesAdapter(this, list);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gvAdapter.setIsGridview(this.isGridview);
        this.gv_wallet.setAdapter((ListAdapter) this.gvAdapter);
        this.gvAdapter.notifyDataSetChanged();
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
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletEntriesActivity.3
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals(WalletEntriesActivity.this.getResources().getString(R.string.tile))) {
                    WalletEntriesActivity.this.isGridview = true;
                    WalletEntriesActivity.this.walletSharedPreferences.set_viewByWalletEntry(WalletEntriesActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals(WalletEntriesActivity.this.getResources().getString(R.string.list))) {
                    WalletEntriesActivity.this.isGridview = false;
                    WalletEntriesActivity.this.walletSharedPreferences.set_viewByWalletEntry(WalletEntriesActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals(WalletEntriesActivity.this.getResources().getString(R.string.name))) {
                    WalletEntriesActivity.this.sortBy = 0;
                    WalletEntriesActivity.this.updateCurrentCategorySortBy();
                }
                if (str.equals(WalletEntriesActivity.this.getResources().getString(R.string.time))) {
                    WalletEntriesActivity.this.sortBy = 1;
                    WalletEntriesActivity.this.updateCurrentCategorySortBy();
                }
                WalletEntriesActivity.this.setGridview();
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
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, configuration.orientation, true));
        setGridview();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_cloud_view_sort, menu);
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletEntriesActivity.4
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                List<WalletEntryFileDB_Pojo> arrayList = new ArrayList<>();
                if (str.length() > 0) {
                    for (WalletEntryFileDB_Pojo walletEntryFileDB_Pojo : WalletEntriesActivity.this.entryFileDB_Pojo) {
                        if (walletEntryFileDB_Pojo.getEntryFileName().toLowerCase().contains(str)) {
                            arrayList.add(walletEntryFileDB_Pojo);
                        }
                    }
                } else {
                    arrayList = WalletEntriesActivity.this.entryFileDB_Pojo;
                }
                WalletEntriesActivity.this.bindSearchResult(arrayList);
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
            startActivity(new Intent(this, WalletCategoriesActivity.class));
            finish();
        } else if (itemId != R.id.action_cloud) {
            if (itemId == R.id.action_viewSort) {
                this.IsSortingDropdown = false;
                showPopupWindow();
            }
        } else if (Common.isPurchased) {
            SecurityLocksCommon.IsAppDeactive = false;
            CloudCommon.ModuleType = CloudCommon.DropboxType.Wallet.ordinal();
            Utilities.StartCloudActivity(this);
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
           /* InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.WalletCategory.ordinal();
            startActivity(new Intent(this, InAppPurchaseActivity.class));
            finish();*/
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, WalletCategoriesActivity.class));
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
