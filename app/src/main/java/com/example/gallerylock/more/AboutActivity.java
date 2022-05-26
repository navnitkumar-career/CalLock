package com.example.gallerylock.more;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import org.apache.http.protocol.HTTP;

/* loaded from: classes2.dex */
public class AboutActivity extends BaseActivity {
    ImageView imgGooglePlus;
    ImageView imgfacebook;
    ImageView imgtweet;
    TextView lbl_App_Name;
    TextView lbl_App_Version;
    TextView lbl_More_top;
    TextView lbl_Support_title;
    TextView lbl_Support_value;
    TextView lbl_copy_write;
    TextView lbl_vist_title;
    TextView lbl_vist_value;
    LinearLayout ll_More_TopBaar;
    LinearLayout ll_background;
    LinearLayout ll_support;
    LinearLayout ll_visit;
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
        setContentView(R.layout.about_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        getWindow().addFlags(128);
        Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.AboutActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AboutActivity.this.btnBackonClick();
            }
        });
        this.ll_visit = (LinearLayout) findViewById(R.id.ll_visit);
        this.ll_support = (LinearLayout) findViewById(R.id.ll_support);
        this.imgfacebook = (ImageView) findViewById(R.id.imgfacebook);
        this.imgtweet = (ImageView) findViewById(R.id.imgtweet);
        this.imgGooglePlus = (ImageView) findViewById(R.id.imgGooglePlus);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_visit.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.AboutActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.newsoftwares.net/ns-vault")));
            }
        });
        this.ll_support.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.AboutActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(HTTP.PLAIN_TEXT_TYPE);
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"support@newsoftwares.net"});
                intent.putExtra("android.intent.extra.SUBJECT", "NS Vault Android");
                intent.putExtra("android.intent.extra.TEXT", "");
                try {
                    SecurityLocksCommon.IsAppDeactive = false;
                    AboutActivity.this.startActivity(Intent.createChooser(intent, "Support via email..."));
                } catch (ActivityNotFoundException unused) {
                }
            }
        });
        this.imgfacebook.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.AboutActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/newsoftwares.net")));
            }
        });
        this.imgtweet.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.AboutActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/NewSoftwaresInc")));
            }
        });
        this.imgGooglePlus.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.more.AboutActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                AboutActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.instagram.com/newsoftwaresinc/")));
            }
        });
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

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(getApplicationContext(), MoreActivity.class));
        finish();
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
        SecurityLocksCommon.IsAppDeactive = true;
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
            startActivity(new Intent(getApplicationContext(), MoreActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
