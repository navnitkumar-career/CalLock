package com.example.gallerylock.more;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.AppPackageCommon;
import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.hackattempt.HackAttemptActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

/* loaded from: classes2.dex */
public class MoreActivity extends BaseActivity {
    LinearLayout ll_More_TopBaar;
    LinearLayout ll_about;
    LinearLayout ll_dextopproduct;
    LinearLayout ll_hack_attempt;
    LinearLayout ll_license_agrement;
    LinearLayout ll_more_list;
    LinearLayout ll_moreproduct;
    LinearLayout ll_rate_and_review;
    LinearLayout ll_tell_a_friend;
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
        setContentView(R.layout.activity_more);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("More");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MoreActivity.this.btnBackonClick();
            }
        });
        this.ll_more_list = (LinearLayout) findViewById(R.id.ll_more_list);
        this.ll_More_TopBaar = (LinearLayout) findViewById(R.id.ll_More_TopBaar);
        this.ll_hack_attempt = (LinearLayout) findViewById(R.id.ll_hack_attempt);
        this.ll_rate_and_review = (LinearLayout) findViewById(R.id.ll_rate_and_review);
        this.ll_tell_a_friend = (LinearLayout) findViewById(R.id.ll_tell_friend_icon);
        this.ll_license_agrement = (LinearLayout) findViewById(R.id.ll_license_agrement);
        this.ll_about = (LinearLayout) findViewById(R.id.ll_about);
        this.ll_hack_attempt.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity.this.startActivity(new Intent(MoreActivity.this, HackAttemptActivity.class));
                MoreActivity.this.finish();
            }
        });
        this.ll_rate_and_review.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsRateReview = true;
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity moreActivity = MoreActivity.this;
                moreActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + AppPackageCommon.AppPackageName)));
            }
        });
        this.ll_tell_a_friend.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MoreCommonMethods.TellaFriendDialog(MoreActivity.this);
            }
        });
        this.ll_license_agrement.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.newsoftwares.net/ns-vault/license/")));
            }
        });
        this.ll_about.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.MoreActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MoreActivity.this.startActivity(new Intent(MoreActivity.this, AboutActivity.class));
                MoreActivity.this.finish();
            }
        });
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
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
