package com.example.gallerylock.securitylocks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.example.gallerylock.R;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.storageoption.SettingActivity;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class ConfirmPatternActivity extends Activity implements AccelerometerListener, SensorEventListener {
    public static final String BUNDLE_GRID_LENGTH = "grid_length";
    public static final String BUNDLE_HIGHLIGHT = "highlight";
    public static final String BUNDLE_PATTERN = "pattern";
    public static final String BUNDLE_PATTERN_MAX = "pattern_max";
    public static final String BUNDLE_PATTERN_MIN = "pattern_min";
    public static final int DIALOG_EXITED_HARD = 1;
    public static final int DIALOG_SEPARATION_WARNING = 0;
    public static TextView lblConfirmpattern;
    protected Button PrintPattern;
    LinearLayout ll_background;
    LinearLayout ll_confirm_pattern_topbaar_bg;
    protected Button mGenerateButton;
    protected int mGridLength;
    protected String mHighlightMode;
    protected int mPatternMax;
    protected int mPatternMin;
    protected ConfirmLockPatternView mPatternView;
    protected ToggleButton mPracticeToggle;
    protected Button mSecuritySettingsButton;
    protected boolean mTactileFeedback;
    private SensorManager sensorManager;

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        SecurityLocksCommon.PatternPassword = SecurityLocksSharedPreferences.GetObject(this).GetSecurityCredential();
        setContentView(R.layout.confirm_pattern_activity);
        this.ll_confirm_pattern_topbaar_bg = (LinearLayout) findViewById(R.id.ll_confirm_pattern_topbaar_bg);
        this.mPatternView = (ConfirmLockPatternView) findViewById(R.id.pattern_view);
        TextView textView = (TextView) findViewById(R.id.txtdrawpattern);
        lblConfirmpattern = textView;
        textView.setTypeface(createFromAsset);
        lblConfirmpattern.setTextColor(getResources().getColor(R.color.ColorAppTheme));
        lblConfirmpattern.setText(R.string.lblsetting_SecurityCredentials_ConfirmYourPattern);
        this.mPatternView.setPracticeMode(true);
        this.mPatternView.invalidate();
        if (bundle != null) {
            this.mPatternView.setPattern(bundle.getParcelableArrayList("pattern"));
        }
    }

    public void btnBackonClick(View view) {
        SecurityLocksCommon.IsConfirmPatternActivity = false;
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, SettingActivity.class));
        finish();
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        SecurityLocksCommon.IsConfirmPatternActivity = false;
        SecurityLocksCommon.isBackupPasswordPin = false;
        SecurityLocksCommon.isBackupPattern = false;
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("grid_length", this.mGridLength);
        bundle.putInt("pattern_max", this.mPatternMax);
        bundle.putInt("pattern_min", this.mPatternMin);
        bundle.putString("highlight", this.mHighlightMode);
        bundle.putParcelableArrayList("pattern", new ArrayList<>(this.mPatternView.getPattern()));
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsConfirmPatternActivity = false;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
