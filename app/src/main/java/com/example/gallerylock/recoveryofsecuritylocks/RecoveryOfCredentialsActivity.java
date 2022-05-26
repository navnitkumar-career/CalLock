package com.example.gallerylock.recoveryofsecuritylocks;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.storageoption.SettingActivity;

/* loaded from: classes2.dex */
public class RecoveryOfCredentialsActivity extends BaseActivity {
    String LoginOption = "";
    TextView lblCancel;
    TextView lblRecoveryEMailTop;
    TextView lblSave;
    TextView lblrecovery_email_description;
    LinearLayout ll_Cancel;
    LinearLayout ll_Save;
    LinearLayout ll_SetRecoveryEMailTopBaar;
    LinearLayout ll_background;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    EditText txtrecovery_email;

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
        setContentView(R.layout.recovery_of_credentials_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.sensorManager = (SensorManager) getSystemService("sensor");
        getWindow().setSoftInputMode(5);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recovery Of Security Locks");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.recoveryofsecuritylocks.RecoveryOfCredentialsActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RecoveryOfCredentialsActivity.this.btnBackonClick();
            }
        });
        this.txtrecovery_email = (EditText) findViewById(R.id.txtrecovery_email);
        this.lblrecovery_email_description = (TextView) findViewById(R.id.lblrecovery_email_description);
        this.lblCancel = (TextView) findViewById(R.id.lblCancel);
        this.lblSave = (TextView) findViewById(R.id.lblSave);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_Cancel = (LinearLayout) findViewById(R.id.ll_Cancel);
        this.ll_Save = (LinearLayout) findViewById(R.id.ll_Save);
        this.ll_SetRecoveryEMailTopBaar = (LinearLayout) findViewById(R.id.ll_SetRecoveryEMailTopBaar);
        TextView textView = (TextView) findViewById(R.id.lblRecoveryEMailTop);
        this.lblRecoveryEMailTop = textView;
        textView.setTypeface(createFromAsset);
        this.lblRecoveryEMailTop.setText(R.string.lblsetting_RecoveryofCredentials);
        this.lblrecovery_email_description.setTypeface(createFromAsset);
        this.lblrecovery_email_description.setText(R.string.lblsetting_SecurityCredentials_recoery_email_activity_description);
        this.txtrecovery_email.setTypeface(createFromAsset);
        this.txtrecovery_email.setTextColor(getResources().getColor(R.color.Color_Secondary_Font));
        this.lblCancel.setTypeface(createFromAsset);
        this.lblSave.setTypeface(createFromAsset);
        this.txtrecovery_email.setText(SecurityLocksSharedPreferences.GetObject(this).GetEmail());
        this.txtrecovery_email.setFocusable(false);
        this.txtrecovery_email.setFocusableInTouchMode(false);
        this.txtrecovery_email.setClickable(false);
        this.txtrecovery_email.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.recoveryofsecuritylocks.RecoveryOfCredentialsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RecoveryOfCredentialsActivity.this.txtrecovery_email.setFocusable(true);
                RecoveryOfCredentialsActivity.this.txtrecovery_email.setFocusableInTouchMode(true);
                RecoveryOfCredentialsActivity.this.txtrecovery_email.setClickable(true);
            }
        });
        this.ll_Cancel.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.recoveryofsecuritylocks.RecoveryOfCredentialsActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RecoveryOfCredentialsActivity.this.Cancel();
            }
        });
        this.ll_Save.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.recoveryofsecuritylocks.RecoveryOfCredentialsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RecoveryOfCredentialsActivity.this.Save();
            }
        });
    }

    public void btnBackonClick() {
        SecurityLocksCommon.isBackupPasswordPin = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, SettingActivity.class));
        finish();
    }

    public void Cancel() {
        SecurityLocksCommon.isBackupPasswordPin = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, SettingActivity.class));
        finish();
    }

    public void Save() {
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this);
        if (this.txtrecovery_email.getText().toString().length() > 0) {
            if (new RecoveryOfCredentialsMethods().isEmailValid(this.txtrecovery_email.getText().toString())) {
                GetObject.SetEmail(this.txtrecovery_email.getText().toString());
                Toast.makeText(this, (int) R.string.toast_setting_SecurityCredentials_Recoveryemailsetsuccesfully, 0).show();
                SecurityLocksCommon.isBackupPasswordPin = false;
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, SettingActivity.class));
                finish();
                return;
            }
            Toast.makeText(this, (int) R.string.toast_setting_SecurityCredentials_Recoveryemail_invalid, 0).show();
            return;
        }
        Toast.makeText(this, (int) R.string.toast_setting_SecurityCredentials_Recoveryemail_please_enter, 0).show();
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
            SecurityLocksCommon.isBackupPasswordPin = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
