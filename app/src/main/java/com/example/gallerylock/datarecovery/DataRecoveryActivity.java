package com.example.gallerylock.datarecovery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.SettingActivity;
import com.example.gallerylock.storageoption.StorageOptionsCommon;

/* loaded from: classes2.dex */
public class DataRecoveryActivity extends BaseActivity {
    public static ProgressDialog myProgressDialog;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.datarecovery.DataRecoveryActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 3) {
                if (StorageOptionsCommon.IsAllDataRecoveryInProg) {
                    DataRecoveryActivity.this.hideProgress();
                    StorageOptionsCommon.IsAllDataRecoveryInProg = false;
                    if (StorageOptionsCommon.IsUserHasDataToRecover) {
                        StorageOptionsCommon.IsUserHasDataToRecover = false;
                        Toast.makeText(DataRecoveryActivity.this, (int) R.string.toast_dataRecovery_Success, 1).show();
                    } else {
                        Toast.makeText(DataRecoveryActivity.this, (int) R.string.toast_dataRecovery_have_no_data, 1).show();
                    }
                }
            } else if (message.what == 2) {
                DataRecoveryActivity.this.hideProgress();
            }
            super.handleMessage(message);
        }
    };
    LinearLayout ll_DataRecover_Recover;
    LinearLayout ll_background;
    LinearLayout ll_storage_option_topbaar_bg;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDataRecoveryProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Your data is being recovered\nWarning: Please be patient and do not close this app otherwise you may lose your data.", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        try {
            if (myProgressDialog != null && myProgressDialog.isShowing()) {
                myProgressDialog.dismiss();
            }
        } catch (Exception unused) {
        } catch (Throwable th) {
            myProgressDialog = null;
            throw th;
        }
        myProgressDialog = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.data_recovery_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        StorageOptionsCommon.IsUserHasDataToRecover = false;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Data Recovery");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.datarecovery.DataRecoveryActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DataRecoveryActivity.this.btnBackonClick();
            }
        });
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_DataRecover_Recover);
        this.ll_DataRecover_Recover = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.datarecovery.DataRecoveryActivity.3
            /* JADX WARN: Type inference failed for: r1v3, types: [net.newsoftwares.hidepicturesvideos.datarecovery.DataRecoveryActivity$3$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DataRecoveryActivity.this.showDataRecoveryProgress();
                StorageOptionsCommon.IsAllDataRecoveryInProg = true;
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.datarecovery.DataRecoveryActivity.3.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            new DataRecover().RecoverALLData(DataRecoveryActivity.this);
                            Message message = new Message();
                            message.what = 3;
                            DataRecoveryActivity.this.handle.sendMessage(message);
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 2;
                            DataRecoveryActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
            }
        });
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, SettingActivity.class));
        finish();
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
        if (SecurityLocksCommon.IsAppDeactive && !StorageOptionsCommon.IsAllDataRecoveryInProg) {
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
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
