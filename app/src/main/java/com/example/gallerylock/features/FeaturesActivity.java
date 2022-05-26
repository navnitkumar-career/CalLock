package com.example.gallerylock.features;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.R;
import com.example.gallerylock.adapter.GridAdapter;
import com.example.gallerylock.audio.AudioPlayListActivity;
import com.example.gallerylock.documents.DocumentsFolderActivity;
import com.example.gallerylock.gallery.GalleryActivity;
import com.example.gallerylock.more.MoreActivity;
import com.example.gallerylock.notes.NotesFoldersActivity;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.photo.PhotosAlbumActivty;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.SettingActivity;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.todolist.ToDoActivity;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.VideosAlbumActivty;
import com.example.gallerylock.wallet.WalletCategoriesActivity;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/* loaded from: classes2.dex */
public class FeaturesActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener, EasyPermissions.PermissionCallbacks {
    private GridAdapter featureActivityListAdapter;
    private ArrayList<FeatureActivityEnt> featureEntList;
    private GridView gv_featureGrid;
    int pos;
    SecurityLocksSharedPreferences securityLocksSharedPreferences;
    private SensorManager sensorManager;
    StorageOptionSharedPreferences storageOptionSharedPreferences;
    private Toolbar toolbar;
    TextView toolbar_title;
    boolean isSDCard = false;
    boolean isSelectedStoraged = false;
    String LoginOption = "";
    String[] PERMISSIONS = {"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    String[] perms = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

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
        setContentView(R.layout.activity_main);
        invalidateOptionsMenu();
        this.gv_featureGrid = (GridView) findViewById(R.id.gridview);
        askForPermissions();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.securityLocksSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        Common.initImageLoader(this);
        this.gv_featureGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.features.FeaturesActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                switch (i) {
                    case 0:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity = FeaturesActivity.this;
                        featuresActivity.requestPermission(featuresActivity.PERMISSIONS);
                        return;
                    case 1:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity2 = FeaturesActivity.this;
                        featuresActivity2.requestPermission(featuresActivity2.PERMISSIONS);
                        return;
                    case 2:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity3 = FeaturesActivity.this;
                        featuresActivity3.requestPermission(featuresActivity3.PERMISSIONS);
                        return;
                    case 3:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity4 = FeaturesActivity.this;
                        featuresActivity4.requestPermission(featuresActivity4.PERMISSIONS);
                        return;
                    case 4:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity5 = FeaturesActivity.this;
                        featuresActivity5.requestPermission(featuresActivity5.PERMISSIONS);
                        return;
                    case 5:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity6 = FeaturesActivity.this;
                        featuresActivity6.requestPermission(featuresActivity6.PERMISSIONS);
                        return;
                    case 6:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity7 = FeaturesActivity.this;
                        featuresActivity7.requestPermission(featuresActivity7.PERMISSIONS);
                        return;
                    case 7:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity8 = FeaturesActivity.this;
                        featuresActivity8.requestPermission(featuresActivity8.PERMISSIONS);
                        return;
                    case 8:
                    default:
                        return;
                    case 9:
                        FeaturesActivity.this.pos = i;
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity featuresActivity9 = FeaturesActivity.this;
                        featuresActivity9.requestPermission(featuresActivity9.PERMISSIONS);
                        return;
                    case 10:
                        FeaturesActivity.this.securityLocksSharedPreferences.isGetCalModeEnable();
                        return;
                    case 11:
                        SecurityLocksCommon.IsAppDeactive = false;
                        return;
                }
            }
        });
        SetFeatureinGridView();
        Rate();
    }

    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }
    }

    public void SetFeatureinGridView() {
        this.featureEntList = new FeatureActivityMethods().GetFeatures(this);
        GridAdapter gridAdapter = new GridAdapter(this, 1, this.featureEntList);
        this.featureActivityListAdapter = gridAdapter;
        this.gv_featureGrid.setAdapter((ListAdapter) gridAdapter);
        this.featureActivityListAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @AfterPermissionGranted(123)
    public void requestPermission(String[] strArr) {
        if (EasyPermissions.hasPermissions(this, strArr)) {
            int i = this.pos;
            if (i == 0) {
                startActivity(new Intent(this, PhotosAlbumActivty.class));
                finish();
            } else if (i == 1) {
                startActivity(new Intent(this, VideosAlbumActivty.class));
                finish();
            } /*else if (i == 2) {
                startActivity(new Intent(this, GalleryActivity.class));
                finish();
            }*/ else if (i == 2) {
                startActivity(new Intent(this, AudioPlayListActivity.class));
                finish();
            } else if (i == 3) {
                startActivity(new Intent(this, DocumentsFolderActivity.class));
                finish();
            } /*else if (i == 5) {
                startActivity(new Intent(this, WalletCategoriesActivity.class));
                finish();
            } else if (i == 6) {
                startActivity(new Intent(this, NotesFoldersActivity.class));
                finish();
            } else if (i == 7) {
                startActivity(new Intent(this, ToDoActivity.class));
                finish();
            } else if (i != 8) {
            }*/
        } else {
            EasyPermissions.requestPermissions(this, "For the best NS Vault experience, please Allow Permission", 123, strArr);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length > 0 && iArr[0] == 0) {
            int i2 = this.pos;
            if (i2 == 0) {
                startActivity(new Intent(this, PhotosAlbumActivty.class));
                finish();
            } else if (i2 == 1) {
                startActivity(new Intent(this, VideosAlbumActivty.class));
                finish();
            } /*else if (i2 == 2) {
                startActivity(new Intent(this, GalleryActivity.class));
                finish();
            }*/ else if (i2 == 2) {
                startActivity(new Intent(this, AudioPlayListActivity.class));
                finish();
            } else if (i2 == 3) {
                startActivity(new Intent(this, DocumentsFolderActivity.class));
                finish();
            } /*else if (i2 == 5) {
                startActivity(new Intent(this, WalletCategoriesActivity.class));
                finish();
            } else if (i2 == 6) {
                startActivity(new Intent(this, NotesFoldersActivity.class));
                finish();
            } else if (i2 == 7) {
                startActivity(new Intent(this, ToDoActivity.class));
                finish();
            } else if (i2 == 8) {
                startActivity(new Intent(this, ToDoActivity.class));
                finish();
            }*/
            Toast.makeText(getApplicationContext(), "Permission is granted ", 0).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.PROCESS_OUTGOING_CALLS")) {
            if (!EasyPermissions.hasPermissions(this, this.perms)) {
                EasyPermissions.requestPermissions(this, "For the best NS Vault experience, please Allow Permission", 123, this.perms);
            }
            Toast.makeText(this, "Permission denied", 0).show();
        } else {
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override // pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
    public void onPermissionsDenied(int i, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    public static boolean hasPermissions(Context context, String... strArr) {
        if (context == null || strArr == null) {
            return true;
        }
        for (String str : strArr) {
            if (ActivityCompat.checkSelfPermission(context, str) != 0) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
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
            if (!this.isSelectedStoraged) {
                SecurityLocksCommon.IsAppDeactive = false;
                StorageOptionSharedPreferences GetObject = StorageOptionSharedPreferences.GetObject(this);
                this.storageOptionSharedPreferences = GetObject;
                StorageOptionsCommon.STORAGEPATH = GetObject.GetStoragePath();
                if (StorageOptionsCommon.STORAGEPATH.length() <= 0) {
                    StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_1;
                    this.storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                }
                this.isSelectedStoraged = true;
                return false;
            } else if (Build.VERSION.SDK_INT >= 16) {
                finishAffinity();
            } else {
                finish();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
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

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_settings) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_more) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MoreActivity.class));
            finish();
            return true;
        } else {
            if (itemId == R.id.action_buy) {
                SecurityLocksCommon.IsAppDeactive = false;
              /*  InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.Feature.ordinal();
                startActivity(new Intent(this, InAppPurchaseActivity.class));
                finish();*/
            }
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        Utilities.hideMenuItems(menu);
        super.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    public void Rate() {
        if (!this.securityLocksSharedPreferences.GetIsAppRated() && Common.loginCountForRateAndReview == 3 && Utilities.isNetworkOnline(this)) {
            Common.loginCountForRateAndReview = 0;
            this.securityLocksSharedPreferences.SetRateCountForRateAndReview(0);
            final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.ratedialog);
            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            ((LinearLayout) dialog.findViewById(R.id.ll_ratePopup)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.features.FeaturesActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    FeaturesActivity.this.securityLocksSharedPreferences.SetIsAppRated(true);
                    FeaturesActivity featuresActivity = FeaturesActivity.this;
                    featuresActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + AppPackageCommon.AppPackageName)));
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_rate_five_star)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.features.FeaturesActivity.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    FeaturesActivity.this.securityLocksSharedPreferences.SetIsAppRated(true);
                    FeaturesActivity featuresActivity = FeaturesActivity.this;
                    featuresActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + AppPackageCommon.AppPackageName)));
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_dislike)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.features.FeaturesActivity.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    FeaturesActivity.this.securityLocksSharedPreferences.SetIsAppRated(true);
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(HTTP.PLAIN_TEXT_TYPE);
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{"support@newsoftwares.net"});
                    intent.putExtra("android.intent.extra.SUBJECT", "I dislike NS Vault Pro because");
                    intent.putExtra("android.intent.extra.TEXT", "");
                    try {
                        SecurityLocksCommon.IsAppDeactive = false;
                        FeaturesActivity.this.startActivity(Intent.createChooser(intent, "Support via email..."));
                    } catch (ActivityNotFoundException unused) {
                    }
                    dialog.dismiss();
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_later)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.features.FeaturesActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }
}
