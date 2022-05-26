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
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class WalletCategoriesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    List<WalletCategoriesFileDB_Pojo> categoryFileDB_List;
    Constants constants;
    WalletCategoriesAdapter gvAdapter;
    GridView gv_wallet;
    private SensorManager sensorManager;
    int sortBy;
    private Toolbar toolbar;
    TextView toolbar_title;
    WalletCategoriesDAL walletCategoriesDAL;
    WalletCommon walletCommon;
    CommonSharedPreferences walletSharedPreferences;
    int categoryCount = 0;
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
        setContentView(R.layout.activity_wallet);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.gv_wallet = (GridView) findViewById(R.id.gv_wallet);
        this.toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        this.walletSharedPreferences = CommonSharedPreferences.GetObject(this);
        this.categoryFileDB_List = new ArrayList();
        this.walletCategoriesDAL = new WalletCategoriesDAL(this);
        this.walletCommon = new WalletCommon();
        this.constants = new Constants();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        boolean z = true;
        SecurityLocksCommon.IsAppDeactive = true;
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.wallet));
        this.toolbar.setNavigationIcon(R.drawable.back_top_bar_icon);
        if (this.walletSharedPreferences.get_ViewByWalletCategory() == 0) {
            z = false;
        }
        this.isGridview = z;
        this.sortBy = this.walletSharedPreferences.get_sortByWalletCategory();
        this.walletCommon.createDefaultCategories(this);
        setGridview();
        this.gv_wallet.setSelection(WalletCommon.walletCategoryScrollIndex);
        this.gv_wallet.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletCategoriesActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                WalletCommon.WalletCurrentCategoryId = WalletCategoriesActivity.this.categoryFileDB_List.get(i).getCategoryFileId();
                WalletCommon.walletCurrentCategoryName = WalletCategoriesActivity.this.categoryFileDB_List.get(i).getCategoryFileName();
                WalletCommon.walletCategoryScrollIndex = i;
                SecurityLocksCommon.IsAppDeactive = false;
                WalletCategoriesActivity.this.startActivity(new Intent(WalletCategoriesActivity.this, WalletEntriesActivity.class));
                WalletCategoriesActivity.this.finish();
            }
        });
    }

    public void setGridview() {
        if (this.sortBy == SortBy.Name.ordinal()) {
            WalletCategoriesDAL walletCategoriesDAL = this.walletCategoriesDAL;
            StringBuilder sb = new StringBuilder();
            this.constants.getClass();
            sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileIsDecoy");
            sb.append(" = ");
            sb.append(SecurityLocksCommon.IsFakeAccount);
            sb.append(" ORDER BY ");
            this.constants.getClass();
            sb.append("WalletCategoriesFileName");
            sb.append(" COLLATE NOCASE ASC");
            this.categoryFileDB_List = walletCategoriesDAL.getAllCategoriesInfoFromDatabase(sb.toString());
        } else {
            WalletCategoriesDAL walletCategoriesDAL2 = this.walletCategoriesDAL;
            StringBuilder sb2 = new StringBuilder();
            this.constants.getClass();
            sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableWalletCategories WHERE ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileIsDecoy");
            sb2.append(" = ");
            sb2.append(SecurityLocksCommon.IsFakeAccount);
            sb2.append(" ORDER BY ");
            this.constants.getClass();
            sb2.append("WalletCategoriesFileModifiedDate");
            sb2.append(" DESC");
            this.categoryFileDB_List = walletCategoriesDAL2.getAllCategoriesInfoFromDatabase(sb2.toString());
        }
        WalletCategoriesAdapter walletCategoriesAdapter = new WalletCategoriesAdapter(this, this.categoryFileDB_List);
        this.gvAdapter = walletCategoriesAdapter;
        walletCategoriesAdapter.setFocusedPosition(0);
        this.gvAdapter.setIsEdit(false);
        this.gvAdapter.setIsGridview(this.isGridview);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
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
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletCategoriesActivity.2
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                String str = ((String) ((List) hashMap.get(arrayList.get(i))).get(i2)).toString();
                if (str.equals(WalletCategoriesActivity.this.getResources().getString(R.string.tile))) {
                    WalletCategoriesActivity.this.isGridview = true;
                    WalletCategoriesActivity.this.walletSharedPreferences.set_ViewByWalletCategory(WalletCategoriesActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals(WalletCategoriesActivity.this.getResources().getString(R.string.list))) {
                    WalletCategoriesActivity.this.isGridview = false;
                    WalletCategoriesActivity.this.walletSharedPreferences.set_ViewByWalletCategory(WalletCategoriesActivity.this.isGridview ? 1 : 0);
                }
                if (str.equals(WalletCategoriesActivity.this.getResources().getString(R.string.name))) {
                    WalletCategoriesActivity.this.sortBy = 0;
                    WalletCategoriesActivity.this.walletSharedPreferences.set_sortByWalletCategory(WalletCategoriesActivity.this.sortBy);
                }
                if (str.equals(WalletCategoriesActivity.this.getResources().getString(R.string.time))) {
                    WalletCategoriesActivity.this.sortBy = 1;
                    WalletCategoriesActivity.this.walletSharedPreferences.set_sortByWalletCategory(WalletCategoriesActivity.this.sortBy);
                }
                WalletCategoriesActivity.this.setGridview();
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

    public void bindSearchResult(List<WalletCategoriesFileDB_Pojo> list) {
        this.gvAdapter = new WalletCategoriesAdapter(this, list);
        this.gv_wallet.setNumColumns(Utilities.getNoOfColumns(this, Utilities.getScreenOrientation(this), this.isGridview));
        this.gvAdapter.setIsGridview(this.isGridview);
        this.gv_wallet.setAdapter((ListAdapter) this.gvAdapter);
        this.gvAdapter.notifyDataSetChanged();
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
        ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search))).setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: net.newsoftwares.hidepicturesvideos.wallet.WalletCategoriesActivity.3
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                List<WalletCategoriesFileDB_Pojo> arrayList = new ArrayList<>();
                if (str.length() > 0) {
                    for (WalletCategoriesFileDB_Pojo walletCategoriesFileDB_Pojo : WalletCategoriesActivity.this.categoryFileDB_List) {
                        if (walletCategoriesFileDB_Pojo.getCategoryFileName().toLowerCase().contains(str)) {
                            arrayList.add(walletCategoriesFileDB_Pojo);
                        }
                    }
                } else {
                    arrayList = WalletCategoriesActivity.this.categoryFileDB_List;
                }
                WalletCategoriesActivity.this.bindSearchResult(arrayList);
                return true;
            }
        });
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            WalletCommon.walletCategoryScrollIndex = 0;
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
        WalletCommon.walletCategoryScrollIndex = 0;
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
