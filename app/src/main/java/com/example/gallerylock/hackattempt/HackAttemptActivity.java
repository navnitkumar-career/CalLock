package com.example.gallerylock.hackattempt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.more.MoreActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class HackAttemptActivity extends BaseActivity {
    public static ProgressDialog myProgressDialog;
    ListView HackAttemptListView;
    ArrayList<HackAttemptEntity> hackAttemptEntitys;
    HackAttemptListAdapter hackAttemptListAdapter;
    ImageView iv_hacker_image;
    LinearLayout ll_Delete;
    LinearLayout ll_DeleteAndSelectAll;
    LinearLayout.LayoutParams ll_DeleteAndSelectAll_Params;
    LinearLayout.LayoutParams ll_DeleteAndSelectAll_Params_hidden;
    LinearLayout ll_HackAttemptTopBaar;
    LinearLayout ll_No_hackattempts;
    LinearLayout ll_SelectAll;
    LinearLayout ll_background;
    LinearLayout ll_hackattempts;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    boolean selectAll = false;
    boolean isEditMode = false;
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                HackAttemptActivity.this.hideProgress();
            } else if (message.what == 3) {
                HackAttemptActivity.this.hideProgress();
                HackAttemptActivity.this.ChangeCheckboxVisibility(false);
                Toast.makeText(HackAttemptActivity.this, (int) R.string.toast_more_hack_attempts_deleted, 0).show();
                SecurityLocksCommon.IsAppDeactive = false;
                HackAttemptActivity.this.startActivity(new Intent(HackAttemptActivity.this, HackAttemptActivity.class));
                HackAttemptActivity.this.overridePendingTransition(17432576, 17432577);
                HackAttemptActivity.this.finish();
            }
            super.handleMessage(message);
        }
    };

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDeleteProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Please be patient... this may take a few moments...", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.hack_attempt_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        this.sensorManager = (SensorManager) getSystemService("sensor");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hack Attempts");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HackAttemptActivity.this.btnBackonClick();
            }
        });
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        this.ll_DeleteAndSelectAll_Params = new LinearLayout.LayoutParams(-1, -2);
        this.ll_DeleteAndSelectAll_Params_hidden = new LinearLayout.LayoutParams(-2, 0);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_DeleteAndSelectAll);
        this.ll_DeleteAndSelectAll = linearLayout;
        linearLayout.setVisibility(4);
        this.ll_DeleteAndSelectAll.setLayoutParams(this.ll_DeleteAndSelectAll_Params_hidden);
        this.ll_HackAttemptTopBaar = (LinearLayout) findViewById(R.id.ll_HackAttemptTopBaar);
        this.ll_Delete = (LinearLayout) findViewById(R.id.ll_Delete);
        this.ll_SelectAll = (LinearLayout) findViewById(R.id.ll_SelectAll);
        this.ll_No_hackattempts = (LinearLayout) findViewById(R.id.ll_No_hackattempts);
        this.ll_hackattempts = (LinearLayout) findViewById(R.id.ll_hackattempts);
        this.ll_No_hackattempts.setVisibility(0);
        this.ll_hackattempts.setVisibility(4);
        this.iv_hacker_image = (ImageView) findViewById(R.id.iv_hackattempt_item);
        this.HackAttemptListView = (ListView) findViewById(R.id.HackAttemptListView);
        ((TextView) findViewById(R.id.HackAttemptTopBaar_Title)).setTypeface(createFromAsset);
        ((TextView) findViewById(R.id.lblDelete)).setTypeface(createFromAsset);
        ((TextView) findViewById(R.id.lblSelectAll)).setTypeface(createFromAsset);
        this.HackAttemptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (!HackAttemptActivity.this.isEditMode) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent = new Intent(HackAttemptActivity.this, HackAttemptDetailActivity.class);
                    intent.putExtra("HackerImagePath", HackAttemptActivity.this.hackAttemptEntitys.get(i).GetImagePath());
                    intent.putExtra("WrongPass", HackAttemptActivity.this.hackAttemptEntitys.get(i).GetWrongPassword());
                    intent.putExtra("DateTime", HackAttemptActivity.this.hackAttemptEntitys.get(i).GetHackAttemptTime());
                    intent.putExtra("Position", i);
                    HackAttemptActivity.this.startActivity(intent);
                    HackAttemptActivity.this.finish();
                }
            }
        });
        this.HackAttemptListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.4
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                HackAttemptActivity.this.hackAttemptEntitys.get(i).SetIsCheck(true);
                HackAttemptActivity.this.ll_DeleteAndSelectAll.setVisibility(0);
                HackAttemptActivity.this.ll_DeleteAndSelectAll.setLayoutParams(HackAttemptActivity.this.ll_DeleteAndSelectAll_Params);
                HackAttemptActivity hackAttemptActivity = HackAttemptActivity.this;
                HackAttemptActivity hackAttemptActivity2 = HackAttemptActivity.this;
                hackAttemptActivity.hackAttemptListAdapter = new HackAttemptListAdapter(hackAttemptActivity2, 17367043, hackAttemptActivity2.hackAttemptEntitys, true, false);
                HackAttemptActivity.this.HackAttemptListView.setAdapter((ListAdapter) HackAttemptActivity.this.hackAttemptListAdapter);
                HackAttemptActivity.this.hackAttemptListAdapter.notifyDataSetChanged();
                return true;
            }
        });
        this.ll_Delete.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (HackAttemptActivity.this.hackAttemptEntitys == null) {
                    return;
                }
                if (HackAttemptActivity.this.IsFileCheck()) {
                    HackAttemptActivity.this.DeleteHackAttept();
                } else {
                    Toast.makeText(HackAttemptActivity.this, (int) R.string.toast_unselectphotomsg_Hackattempts, 0).show();
                }
            }
        });
        this.ll_SelectAll.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (HackAttemptActivity.this.selectAll) {
                    HackAttemptActivity.this.selectAll = false;
                } else {
                    HackAttemptActivity.this.selectAll = true;
                }
                Iterator<HackAttemptEntity> it = HackAttemptActivity.this.hackAttemptEntitys.iterator();
                while (it.hasNext()) {
                    it.next().SetIsCheck(Boolean.valueOf(HackAttemptActivity.this.selectAll));
                }
                HackAttemptActivity hackAttemptActivity = HackAttemptActivity.this;
                HackAttemptActivity hackAttemptActivity2 = HackAttemptActivity.this;
                hackAttemptActivity.hackAttemptListAdapter = new HackAttemptListAdapter(hackAttemptActivity2, 17367043, hackAttemptActivity2.hackAttemptEntitys, true, Boolean.valueOf(HackAttemptActivity.this.selectAll));
                HackAttemptActivity.this.HackAttemptListView.setAdapter((ListAdapter) HackAttemptActivity.this.hackAttemptListAdapter);
                HackAttemptActivity.this.hackAttemptListAdapter.notifyDataSetChanged();
            }
        });
        BindHackAttempsList();
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, MoreActivity.class));
        finish();
    }

    void DeleteHackAttept() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(true);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        textView.setText("Are you sure you want to delete selected Hack Attempts?");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.7
            /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity$7$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HackAttemptActivity.this.showDeleteProgress();
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.7.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            dialog.dismiss();
                            HackAttemptActivity.this.Delete();
                            Message message = new Message();
                            message.what = 3;
                            HackAttemptActivity.this.handle.sendMessage(message);
                        } catch (Exception unused) {
                            Message message2 = new Message();
                            message2.what = 2;
                            HackAttemptActivity.this.handle.sendMessage(message2);
                        }
                    }
                }.start();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void Delete() {
        Iterator it = new ArrayList(this.hackAttemptEntitys).iterator();
        while (it.hasNext()) {
            HackAttemptEntity hackAttemptEntity = (HackAttemptEntity) it.next();
            if (hackAttemptEntity.GetIsCheck()) {
                this.hackAttemptEntitys.remove(hackAttemptEntity);
            }
        }
        HackAttemptsSharedPreferences.GetObject(this).SetHackAttemptObject(this.hackAttemptEntitys);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean IsFileCheck() {
        Iterator it = new ArrayList(this.hackAttemptEntitys).iterator();
        while (it.hasNext()) {
            if (((HackAttemptEntity) it.next()).GetIsCheck()) {
                return true;
            }
        }
        return false;
    }

    private void BindHackAttempsList() {
        ArrayList<HackAttemptEntity> GetHackAttemptObject = HackAttemptsSharedPreferences.GetObject(getApplicationContext()).GetHackAttemptObject();
        this.hackAttemptEntitys = GetHackAttemptObject;
        if (GetHackAttemptObject != null) {
            if (GetHackAttemptObject.size() > 0) {
                this.ll_No_hackattempts.setVisibility(4);
                this.ll_hackattempts.setVisibility(0);
            }
            HackAttemptListAdapter hackAttemptListAdapter = new HackAttemptListAdapter(this, 17367043, this.hackAttemptEntitys, false, false);
            this.hackAttemptListAdapter = hackAttemptListAdapter;
            this.HackAttemptListView.setAdapter((ListAdapter) hackAttemptListAdapter);
            this.hackAttemptListAdapter.notifyDataSetChanged();
            return;
        }
        this.ll_No_hackattempts.setVisibility(0);
        this.ll_hackattempts.setVisibility(4);
    }

    void ChangeCheckboxVisibility(boolean z) {
        if (z) {
            this.ll_DeleteAndSelectAll.setVisibility(0);
            this.ll_DeleteAndSelectAll.setLayoutParams(this.ll_DeleteAndSelectAll_Params);
        } else {
            this.ll_DeleteAndSelectAll.setVisibility(4);
            this.ll_DeleteAndSelectAll.setLayoutParams(this.ll_DeleteAndSelectAll_Params_hidden);
        }
        Iterator<HackAttemptEntity> it = this.hackAttemptEntitys.iterator();
        while (it.hasNext()) {
            it.next().SetIsCheck(Boolean.valueOf(z));
        }
        HackAttemptListAdapter hackAttemptListAdapter = new HackAttemptListAdapter(this, 17367043, this.hackAttemptEntitys, z, false);
        this.hackAttemptListAdapter = hackAttemptListAdapter;
        this.HackAttemptListView.setAdapter((ListAdapter) hackAttemptListAdapter);
        this.hackAttemptListAdapter.notifyDataSetChanged();
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
            if (this.isEditMode) {
                this.isEditMode = false;
                ChangeCheckboxVisibility(false);
            }
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MoreActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
