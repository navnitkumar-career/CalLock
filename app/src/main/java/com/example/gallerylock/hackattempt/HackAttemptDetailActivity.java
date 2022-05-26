package com.example.gallerylock.hackattempt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class HackAttemptDetailActivity extends Activity implements AccelerometerListener, SensorEventListener, SimpleGestureFilter.SimpleGestureListener {
    private SimpleGestureFilter detector;
    ArrayList<HackAttemptEntity> hackAttemptEntitys;
    ImageView imghackattempt;
    LinearLayout ll_HackAttemptDetailTopBaar;
    LinearLayout ll_background;
    private SensorManager sensorManager;
    TextView txtattempttime;
    TextView txtwrongpass;
    String HackerImagePath = "";
    String WrongPass = "";
    String DateTime = "";
    int Position = 0;

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.hackattempt.SimpleGestureFilter.SimpleGestureListener
    public void onDoubleTap() {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.hackattempt_detail_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.detector = new SimpleGestureFilter(this, this);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.imghackattempt = (ImageView) findViewById(R.id.imghackattempt);
        this.txtwrongpass = (TextView) findViewById(R.id.txtwrongpass);
        this.txtattempttime = (TextView) findViewById(R.id.txtattempttime);
        this.hackAttemptEntitys = HackAttemptsSharedPreferences.GetObject(getApplicationContext()).GetHackAttemptObject();
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("HackerImagePath");
        this.HackerImagePath = stringExtra;
        SetHackerImageToImageView(stringExtra);
        this.WrongPass = intent.getStringExtra("WrongPass");
        this.DateTime = intent.getStringExtra("DateTime");
        this.Position = intent.getIntExtra("Position", 0);
        if (SecurityLocksCommon.LoginOptions.Password.toString().equals(this.hackAttemptEntitys.get(this.Position).GetLoginOption().toString())) {
            TextView textView = this.txtwrongpass;
            textView.setText("Wrong Password: " + this.hackAttemptEntitys.get(this.Position).GetWrongPassword().toString());
        } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(this.hackAttemptEntitys.get(this.Position).GetLoginOption().toString())) {
            TextView textView2 = this.txtwrongpass;
            textView2.setText("Wrong PIN: " + this.hackAttemptEntitys.get(this.Position).GetWrongPassword().toString());
        } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(this.hackAttemptEntitys.get(this.Position).GetLoginOption().toString())) {
            TextView textView3 = this.txtwrongpass;
            textView3.setText("Wrong Pattern: " + this.hackAttemptEntitys.get(this.Position).GetWrongPassword().toString());
        }
        String replace = this.DateTime.replace("GMT+05:00", "");
        this.DateTime = replace;
        if (replace.length() > 0) {
            this.txtattempttime.setText(this.DateTime);
        }
    }

    public void btnBackonClick(View view) {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, HackAttemptActivity.class));
        finish();
    }

    public void SetHackerImageToImageView(String str) {
        try {
            this.imghackattempt.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(new File(str)), null, null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.detector.onTouchEvent(motionEvent);
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // net.newsoftwares.hidepicturesvideos.hackattempt.SimpleGestureFilter.SimpleGestureListener
    public void onSwipe(int i) {
        if (i == 3) {
            if (this.Position == 0) {
                this.Position = this.hackAttemptEntitys.size();
            }
            int i2 = this.Position;
            if (i2 > 0) {
                int i3 = i2 - 1;
                this.Position = i3;
                this.HackerImagePath = this.hackAttemptEntitys.get(i3).GetImagePath();
                this.WrongPass = this.hackAttemptEntitys.get(this.Position).GetWrongPassword();
                String GetHackAttemptTime = this.hackAttemptEntitys.get(this.Position).GetHackAttemptTime();
                this.DateTime = GetHackAttemptTime;
                this.DateTime = GetHackAttemptTime.replace("GMT+05:00", "");
                runOnUiThread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptDetailActivity.2
                    @Override // java.lang.Runnable
                    public void run() {
                        HackAttemptDetailActivity hackAttemptDetailActivity = HackAttemptDetailActivity.this;
                        hackAttemptDetailActivity.SetHackerImageToImageView(hackAttemptDetailActivity.HackerImagePath);
                        if (HackAttemptDetailActivity.this.WrongPass.length() > 0) {
                            if (SecurityLocksCommon.LoginOptions.Password.toString().equals(HackAttemptDetailActivity.this.hackAttemptEntitys.get(HackAttemptDetailActivity.this.Position).GetLoginOption().toString())) {
                                TextView textView = HackAttemptDetailActivity.this.txtwrongpass;
                                textView.setText("Wrong Password: " + HackAttemptDetailActivity.this.hackAttemptEntitys.get(HackAttemptDetailActivity.this.Position).GetWrongPassword().toString());
                            } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(HackAttemptDetailActivity.this.hackAttemptEntitys.get(HackAttemptDetailActivity.this.Position).GetLoginOption().toString())) {
                                TextView textView2 = HackAttemptDetailActivity.this.txtwrongpass;
                                textView2.setText("Wrong PIN: " + HackAttemptDetailActivity.this.hackAttemptEntitys.get(HackAttemptDetailActivity.this.Position).GetWrongPassword().toString());
                            } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(HackAttemptDetailActivity.this.hackAttemptEntitys.get(HackAttemptDetailActivity.this.Position).GetLoginOption().toString())) {
                                TextView textView3 = HackAttemptDetailActivity.this.txtwrongpass;
                                textView3.setText("Wrong Pattern: " + HackAttemptDetailActivity.this.hackAttemptEntitys.get(HackAttemptDetailActivity.this.Position).GetWrongPassword().toString());
                            }
                        }
                        if (HackAttemptDetailActivity.this.DateTime.length() > 0) {
                            HackAttemptDetailActivity.this.txtattempttime.setText(HackAttemptDetailActivity.this.DateTime);
                        }
                    }
                });
            }
        } else if (i == 4) {
            if (this.Position == this.hackAttemptEntitys.size()) {
                this.Position = 0;
            }
            int i4 = this.Position;
            if (i4 >= 0 && i4 < this.hackAttemptEntitys.size()) {
                this.HackerImagePath = this.hackAttemptEntitys.get(this.Position).GetImagePath();
                this.WrongPass = this.hackAttemptEntitys.get(this.Position).GetWrongPassword();
                String GetHackAttemptTime2 = this.hackAttemptEntitys.get(this.Position).GetHackAttemptTime();
                this.DateTime = GetHackAttemptTime2;
                this.DateTime = GetHackAttemptTime2.replace("GMT+05:00", "");
                runOnUiThread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptDetailActivity.1
                    @Override // java.lang.Runnable
                    public void run() {
                        HackAttemptDetailActivity hackAttemptDetailActivity = HackAttemptDetailActivity.this;
                        hackAttemptDetailActivity.SetHackerImageToImageView(hackAttemptDetailActivity.HackerImagePath);
                        if (HackAttemptDetailActivity.this.WrongPass.length() > 0) {
                            HackAttemptDetailActivity.this.txtwrongpass.setText(HackAttemptDetailActivity.this.WrongPass);
                        }
                        if (HackAttemptDetailActivity.this.DateTime.length() > 0) {
                            HackAttemptDetailActivity.this.txtattempttime.setText(HackAttemptDetailActivity.this.DateTime);
                        }
                    }
                });
                this.Position++;
            }
        }
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
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
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

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(getApplicationContext(), HackAttemptActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
