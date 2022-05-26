package com.example.gallerylock.dropbox;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.utilities.Utilities;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class CloudMenuActivity extends BaseActivity {
    private CloudMenuAdapter adapter;
    private ArrayList<CloudMenuEnt> cloudEntList;
    private ListView cloudListView;
    LinearLayout ll_background;
    LinearLayout ll_topbaar;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.cloud_menu_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cloud");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.CloudMenuActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CloudMenuActivity.this.btnBackonClick();
            }
        });
        ListView listView = (ListView) findViewById(R.id.cloudListView);
        this.cloudListView = listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.CloudMenuActivity.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                switch (i) {
                    case 0:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.Photos.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 1:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.Videos.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 2:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.Documents.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 3:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.Notes.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 4:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.Wallet.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 5:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.ToDo.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    case 6:
                        SecurityLocksCommon.IsAppDeactive = false;
                        CloudCommon.IsCameFromSettings = false;
                        CloudCommon.IsCameFromCloudMenu = true;
                        CloudCommon.ModuleType = CloudCommon.DropboxType.Audio.ordinal();
                        Utilities.StartCloudActivity(CloudMenuActivity.this);
                        return;
                    default:
                        return;
                }
            }
        });
        BindCloudMenu();
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
    }

    private void BindCloudMenu() {
        this.cloudEntList = GetCloudDetail();
        CloudMenuAdapter cloudMenuAdapter = new CloudMenuAdapter(this, 17367043, this.cloudEntList);
        this.adapter = cloudMenuAdapter;
        this.cloudListView.setAdapter((ListAdapter) cloudMenuAdapter);
    }

    private ArrayList<CloudMenuEnt> GetCloudDetail() {
        ArrayList<CloudMenuEnt> arrayList = new ArrayList<>();
        if (SecurityLocksCommon.IsFakeAccount != 1) {
            CloudMenuEnt cloudMenuEnt = new CloudMenuEnt();
            cloudMenuEnt.SetCloudHeading(R.string.lblFeature1);
            cloudMenuEnt.SetDrawable(R.drawable.ic_menu_cloud_photo_icon);
            arrayList.add(cloudMenuEnt);
            CloudMenuEnt cloudMenuEnt2 = new CloudMenuEnt();
            cloudMenuEnt2.SetCloudHeading(R.string.lblFeature2);
            cloudMenuEnt2.SetDrawable(R.drawable.ic_menu_cloud_video_icon);
            arrayList.add(cloudMenuEnt2);
            CloudMenuEnt cloudMenuEnt3 = new CloudMenuEnt();
            cloudMenuEnt3.SetCloudHeading(R.string.lblFeature9);
            cloudMenuEnt3.SetDrawable(R.drawable.ic_menu_cloud_audio_icon);
            arrayList.add(cloudMenuEnt3);
            CloudMenuEnt cloudMenuEnt4 = new CloudMenuEnt();
            cloudMenuEnt4.SetCloudHeading(R.string.lblFeature4);
            cloudMenuEnt4.SetDrawable(R.drawable.ic_menu_cloud_documents_icon);
            arrayList.add(cloudMenuEnt4);
            CloudMenuEnt cloudMenuEnt5 = new CloudMenuEnt();
            cloudMenuEnt5.SetCloudHeading(R.string.lblFeature6);
            cloudMenuEnt5.SetDrawable(R.drawable.ic_menu_cloud_notes_icon);
            arrayList.add(cloudMenuEnt5);
            CloudMenuEnt cloudMenuEnt6 = new CloudMenuEnt();
            cloudMenuEnt6.SetCloudHeading(R.string.lblFeature7);
            cloudMenuEnt6.SetDrawable(R.drawable.ic_menu_cloud_password_icon);
            arrayList.add(cloudMenuEnt6);
            CloudMenuEnt cloudMenuEnt7 = new CloudMenuEnt();
            cloudMenuEnt7.SetCloudHeading(R.string.todoList);
            cloudMenuEnt7.SetDrawable(R.drawable.ic_menu_cloud_todos_icon);
            arrayList.add(cloudMenuEnt7);
        }
        return arrayList;
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
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
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
