package com.example.gallerylock.dropbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.dropbox.core.android.Auth;
import com.example.gallerylock.R;
import com.example.gallerylock.audio.AudioPlayListActivity;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.documents.DocumentsFolderActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.notes.NotesFoldersActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.photo.PhotosAlbumActivty;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securebackupcloud.DropboxCloudApi;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.SettingActivity;
import com.example.gallerylock.todolist.ToDoActivity;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.example.gallerylock.video.VideosAlbumActivty;
import com.example.gallerylock.wallet.WalletCategoriesActivity;

/* loaded from: classes2.dex */
public class DropboxLoginActivity extends BaseActivity {
    private static final String ACCOUNT_PREFS_NAME = "DropboxPerf";
    static SharedPreferences CloudPrefs;
    static SharedPreferences.Editor CloudprefsEditor;
    private String APP_KEY = "u041g5ffkpo64o5";
    Handler SignOuthandler = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropboxLoginActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                Common.isSignOutSuccessfully = true;
            }
            super.handleMessage(message);
        }
    };
    private DropboxCloudApi dropboxCloudApi;
    LinearLayout ll_background;
    LinearLayout ll_btnDropboxSignIn;
    LinearLayout ll_btnDropboxSignOut;
    LinearLayout ll_topbaar;
    private boolean mLoggedIn;
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
        setContentView(R.layout.dropboxsignin_activity);
        SecurityLocksCommon.IsAppDeactive = true;
        this.dropboxCloudApi = new DropboxCloudApi(this);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_btnDropboxSignIn = (LinearLayout) findViewById(R.id.ll_btnDropboxSignIn);
        this.ll_btnDropboxSignOut = (LinearLayout) findViewById(R.id.ll_btnDropboxSignOut);
        this.ll_topbaar = (LinearLayout) findViewById(R.id.ll_topbaar);
        this.ll_background = (LinearLayout) findViewById(R.id.ll_background);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cloud");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.dropbox.DropboxLoginActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DropboxLoginActivity.this.btnBackonClick();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("Cloud", 0);
        CloudPrefs = sharedPreferences;
        CloudCommon.CloudType = sharedPreferences.getInt("CloudType", 0);
        boolean z = CloudPrefs.getBoolean("isAppRegisterd", false);
        String string = getSharedPreferences(ACCOUNT_PREFS_NAME, 0).getString("access-token", null);
        if (z && string != null) {
            this.ll_btnDropboxSignIn.setVisibility(4);
            this.ll_btnDropboxSignOut.setVisibility(0);
        }
        SharedPreferences.Editor edit = CloudPrefs.edit();
        CloudprefsEditor = edit;
        edit.putInt("CloudType", 0);
        CloudprefsEditor.commit();
        CloudCommon.CloudType = 0;
    }

    public void btnBackonClick() {
        Intent intent;
        SecurityLocksCommon.IsAppDeactive = false;
        if (CloudCommon.IsCameFromSettings) {
            intent = new Intent(getApplicationContext(), SettingActivity.class);
            CloudCommon.IsCameFromSettings = false;
        } else if (CloudCommon.IsCameFromCloudMenu) {
            intent = new Intent(getApplicationContext(), FeaturesActivity.class);
            CloudCommon.IsCameFromCloudMenu = false;
        } else if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), PhotosAlbumActivty.class);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), VideosAlbumActivty.class);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), DocumentsFolderActivity.class);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), NotesFoldersActivity.class);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), WalletCategoriesActivity.class);
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            intent = new Intent(getApplicationContext(), ToDoActivity.class);
        } else {
            intent = CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType ? new Intent(getApplicationContext(), AudioPlayListActivity.class) : null;
        }
        startActivity(intent);
        finish();
    }

    public void DropboxSignIn(View view) {
        SecurityLocksCommon.IsAppDeactive = false;
        Common.isSignOutSuccessfully = false;
        Auth.startOAuth2Authentication(this, this.APP_KEY);
    }

    public void DropboxSignOut(View view) {
        DropboxSignOut();
    }

    public void DropboxSignOut() {
        if (CloudPrefs.getBoolean("isAppRegisterd", false)) {
            SharedPreferences.Editor edit = CloudPrefs.edit();
            CloudprefsEditor = edit;
            edit.putBoolean("isAppRegisterd", false);
            CloudprefsEditor.commit();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        if (sharedPreferences.getString("access-token", null) != null) {
            sharedPreferences.edit().remove("access-token").commit();
        }
        if (Auth.getOAuth2Token() != null) {
            this.dropboxCloudApi.client = null;
            Message message = new Message();
            message.what = 1;
            this.SignOuthandler.sendMessage(message);
        }
        setLoggedIn(false);
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(getApplicationContext(), DropboxLoginActivity.class));
        finish();
    }

    private void setLoggedIn(boolean z) {
        this.mLoggedIn = z;
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

    public void StartCloudDownloadActiviy() {
        SecurityLocksCommon.IsAppDeactive = false;
        if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.Photos.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.Videos.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.Documents.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.Notes.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.Wallet.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.ToDo.ordinal();
            Utilities.StartCloudActivity(this);
        } else if (CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
            CloudCommon.ModuleType = CloudCommon.DropboxType.Audio.ordinal();
            Utilities.StartCloudActivity(this);
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        String oAuth2Token;
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        if (sharedPreferences.getString("access-token", null) == null && (oAuth2Token = Auth.getOAuth2Token()) != null && !Common.isSignOutSuccessfully) {
            sharedPreferences.edit().putString("access-token", oAuth2Token).apply();
            if (!CloudPrefs.getBoolean("isAppRegisterd", false)) {
                SharedPreferences.Editor edit = CloudPrefs.edit();
                CloudprefsEditor = edit;
                edit.putBoolean("isAppRegisterd", true);
                CloudprefsEditor.commit();
            }
            if (!CloudCommon.IsCameFromSettings) {
                StartCloudDownloadActiviy();
                return;
            }
            this.ll_btnDropboxSignIn.setVisibility(4);
            this.ll_btnDropboxSignOut.setVisibility(0);
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

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            Intent intent = null;
            if (CloudCommon.IsCameFromSettings) {
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                CloudCommon.IsCameFromSettings = false;
            } else if (CloudCommon.IsCameFromCloudMenu) {
                intent = new Intent(getApplicationContext(), FeaturesActivity.class);
                CloudCommon.IsCameFromCloudMenu = false;
            } else if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), PhotosAlbumActivty.class);
            } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), VideosAlbumActivty.class);
            } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), DocumentsFolderActivity.class);
            } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), NotesFoldersActivity.class);
            } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), WalletCategoriesActivity.class);
            } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), ToDoActivity.class);
            } else if (CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
                intent = new Intent(getApplicationContext(), AudioPlayListActivity.class);
            }
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
