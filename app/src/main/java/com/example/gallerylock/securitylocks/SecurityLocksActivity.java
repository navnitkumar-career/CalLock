package com.example.gallerylock.securitylocks;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.calculator.CalculatorPinSetting;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.storageoption.SettingActivity;
import com.example.gallerylock.utilities.Utilities;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class SecurityLocksActivity extends BaseActivity {
    TextView SecurityCredentialsToBaar_Title;
    private SecurityLocksListAdapter adapter;
    public LinearLayout btnCancel;
    public LinearLayout btnOk;
    public CheckBox checkboxCal;
    boolean isSettingDecoy = false;
    public boolean ischeckbox = false;
    String isconfirmdialog = "";
    TextView lblloginoptionitem;
    LinearLayout ll_SecurityCredentials_TopBaar;
    LinearLayout ll_background;
    LinearLayout rootViewGroup;
    private ArrayList<SecurityLocksEnt> securityLocksEntEntList;
    private ListView securityLocksListView;
    private SensorManager sensorManager;
    private Toolbar toolbar;

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.security_lock_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        if (getIntent().getStringExtra("isconfirmdialog") != null) {
            this.isconfirmdialog = getIntent().getStringExtra("isconfirmdialog");
        }
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   Select Security Locks");*/
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.securityLocksListView = (ListView) findViewById(R.id.SecurityCredentialsListView);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_SecurityCredentials_TopBaar);
        this.ll_SecurityCredentials_TopBaar = linearLayout;
        linearLayout.setBackgroundColor(getResources().getColor(R.color.ColorAppTheme));
        TextView textView = (TextView) findViewById(R.id.SecurityCredentialsToBaar_Title);
        this.SecurityCredentialsToBaar_Title = textView;
        textView.setTextColor(getResources().getColor(R.color.ColorWhite));
        this.rootViewGroup = (LinearLayout) findViewById(R.id.rootViewGroup);
        this.isSettingDecoy = SecurityLocksCommon.isSettingDecoy;
        if (Utilities.getScreenOrientation(this) == 1) {
            this.rootViewGroup.setVisibility(4);
        } else if (Utilities.getScreenOrientation(this) == 2) {
            this.rootViewGroup.setVisibility(8);
        }
        this.securityLocksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.securitylocks.SecurityLocksActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (i == 0) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    SecurityLocksActivity.this.agreeDisguiseModeDialog();
                } else if (i == 1) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent = new Intent(SecurityLocksActivity.this, SetPasswordActivity.class);
                    intent.putExtra("LoginOption", "Pin");
                    intent.putExtra("isSettingDecoy", SecurityLocksActivity.this.isSettingDecoy);
                    SecurityLocksActivity.this.startActivity(intent);
                    SecurityLocksActivity.this.finish();
                } else if (i == 2) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent2 = new Intent(SecurityLocksActivity.this, SetPatternActivity.class);
                    intent2.putExtra("isSettingDecoy", SecurityLocksActivity.this.isSettingDecoy);
                    SecurityLocksActivity.this.startActivity(intent2);
                    SecurityLocksActivity.this.finish();
                } else if (i == 3) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent3 = new Intent(SecurityLocksActivity.this, SetPasswordActivity.class);
                    intent3.putExtra("LoginOption", "Password");
                    intent3.putExtra("isSettingDecoy", SecurityLocksActivity.this.isSettingDecoy);
                    SecurityLocksActivity.this.startActivity(intent3);
                    SecurityLocksActivity.this.finish();
                }
            }
        });
        BindSecurityLocks();
    }

    private void BindSecurityLocks() {
        this.securityLocksEntEntList = new SecurityLocksActivityMethods().GetSecurityCredentialsDetail(this);
        SecurityLocksListAdapter securityLocksListAdapter = new SecurityLocksListAdapter(this, 17367043, this.securityLocksEntEntList);
        this.adapter = securityLocksListAdapter;
        this.securityLocksListView.setAdapter((ListAdapter) securityLocksListAdapter);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == 2) {
            this.rootViewGroup.setVisibility(8);
        } else if (configuration.orientation == 1) {
            this.rootViewGroup.setVisibility(4);
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

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
        }
        super.onPause();
    }

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
        if (i == 4 && !SecurityLocksCommon.IsFirstLogin) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void agreeDisguiseModeDialog() {
        SecurityLocksCommon.IsAppDeactive = false;
        Intent intent = new Intent(this, CalculatorPinSetting.class);
        intent.putExtra("from", "Cal");
        intent.putExtra("isconfirmdialog", this.isconfirmdialog);
        intent.putExtra("from", "isSettingDecoy");
        startActivity(intent);
        finish();
    }
}
