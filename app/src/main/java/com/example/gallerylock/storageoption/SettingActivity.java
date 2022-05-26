package com.example.gallerylock.storageoption;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.calculator.CalculatorPinSetting;
import com.example.gallerylock.datarecovery.DataRecoveryActivity;
import com.example.gallerylock.dropbox.DropboxLoginActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivity;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securebackupcloud.CloudCommon;
import com.example.gallerylock.securitylocks.ConfirmPasswordPinActivity;
import com.example.gallerylock.securitylocks.ConfirmPatternActivity;
import com.example.gallerylock.securitylocks.SecurityLocksActivity;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.MoveData;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/* loaded from: classes2.dex */
public class SettingActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    static SharedPreferences DataTransferPrefs;
    static SharedPreferences.Editor DataTransferprefsEditor;
    public static ProgressDialog myProgressDialog;
    CheckBox cb_stealth_mode;
    Dialog dialog;
    private LinearLayout ll_anchor;
    LinearLayout ll_background;
    LinearLayout ll_cloud_backup;
    LinearLayout ll_data_recovery;
    LinearLayout ll_decoy_security_lock;
    LinearLayout ll_lollipop_sd_permission;
    LinearLayout ll_panic_switch;
    LinearLayout ll_recovery_credentials;
    LinearLayout ll_security_credentials;
    LinearLayout ll_stealth_mode;
    LinearLayout ll_storage_options;
    LinearLayout ll_tick;
    List<UriPermission> permissions;
    SecurityLocksSharedPreferences securityCredentialsSharedPreferences;
    private SensorManager sensorManager;
    StorageOptionSharedPreferences storageOptionSharedPreferences;
    private Toolbar toolbar;
    String LoginOption = "";
    boolean IsStealthModeOn = false;
    boolean isSDCard = false;
    boolean isStealthModeAlreadyCheck = false;
    int number = 0;
    String[] PERMISSIONS_ = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    Handler handle = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 3) {
                if (StorageOptionsCommon.IsAllDataMoveingInProg) {
                    StorageOptionsCommon.IsAllDataMoveingInProg = false;
                    SettingActivity.this.hideProgress();
                    if (StorageOptionsCommon.IsApphasDataforTransfer) {
                        StorageOptionsCommon.IsApphasDataforTransfer = false;
                        Toast.makeText(SettingActivity.this, "Data Moved successfully.", 1).show();
                    }
                }
            } else if (message.what == 2 && StorageOptionsCommon.IsAllDataMoveingInProg) {
                StorageOptionsCommon.IsAllDataMoveingInProg = false;
                SettingActivity.this.hideProgress();
                if (StorageOptionsCommon.IsApphasDataforTransfer) {
                    StorageOptionsCommon.IsApphasDataforTransfer = false;
                    Toast.makeText(SettingActivity.this, "Data transferred successfully.", 1).show();
                }
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

    @Override // pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
    public void onPermissionsGranted(int i, List<String> list) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMoveDataToOrFromSDCardProgress() {
        myProgressDialog = ProgressDialog.show(this, null, "Data transferring \nWarning: Please be patient and do not close this app otherwise you may lose your data.", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgress() {
        ProgressDialog progressDialog = myProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            myProgressDialog.dismiss();
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.ll_anchor = (LinearLayout) findViewById(R.id.ll_anchor);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle("Settings");
        this.toolbar.setNavigationIcon(R.drawable.ic_top_back_icon);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingActivity.this.btnBackonClick();
            }
        });
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this);
        this.securityCredentialsSharedPreferences = GetObject;
        this.LoginOption = GetObject.GetLoginType();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.ll_lollipop_sd_permission = (LinearLayout) findViewById(R.id.ll_lollipop_sd_permission);
        this.ll_security_credentials = (LinearLayout) findViewById(R.id.ll_security_credentials);
        this.ll_cloud_backup = (LinearLayout) findViewById(R.id.ll_cloud_backup);
        this.ll_data_recovery = (LinearLayout) findViewById(R.id.ll_data_recovery);
        this.ll_recovery_credentials = (LinearLayout) findViewById(R.id.ll_recovery_credentials);
        this.ll_decoy_security_lock = (LinearLayout) findViewById(R.id.ll_decoy_security_lock);
        this.ll_storage_options = (LinearLayout) findViewById(R.id.ll_storage_options);
        this.ll_panic_switch = (LinearLayout) findViewById(R.id.ll_panic_switch);
        this.ll_stealth_mode = (LinearLayout) findViewById(R.id.ll_stealth_mode);
        this.cb_stealth_mode = (CheckBox) findViewById(R.id.cb_stealth_mode);
        this.ll_tick = (LinearLayout) findViewById(R.id.ll_tick);
        if (SecurityLocksCommon.IsFakeAccount == 1) {
            this.ll_cloud_backup.setVisibility(8);
            this.ll_data_recovery.setVisibility(8);
            this.ll_recovery_credentials.setVisibility(8);
            this.ll_decoy_security_lock.setVisibility(8);
            this.ll_storage_options.setVisibility(8);
            this.ll_panic_switch.setVisibility(8);
            this.ll_stealth_mode.setVisibility(8);
        }
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            this.ll_lollipop_sd_permission.setVisibility(0);
            if (StorageOptionSharedPreferences.GetObject(this).GetSDCardUri().length() > 0) {
                this.ll_tick.setVisibility(0);
            } else {
                this.ll_tick.setVisibility(4);
            }
        }
        this.ll_lollipop_sd_permission.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                final Dialog dialog = new Dialog(SettingActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.sdcard_permission_select_hint_msgbox);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.3.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        SettingActivity.this.startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), 42);
                        dialog.dismiss();
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.3.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        this.ll_security_credentials.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(SettingActivity.this.LoginOption)) {
                    SecurityLocksCommon.IsConfirmPatternActivity = true;
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, ConfirmPatternActivity.class));
                    SettingActivity.this.finish();
                } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(SettingActivity.this.LoginOption) || SecurityLocksCommon.LoginOptions.Password.toString().equals(SettingActivity.this.LoginOption)) {
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, ConfirmPasswordPinActivity.class));
                    SettingActivity.this.finish();
                } else if (SecurityLocksCommon.LoginOptions.Calculator.toString().equals(SettingActivity.this.LoginOption)) {
                    Intent intent = new Intent(SettingActivity.this, CalculatorPinSetting.class);
                    intent.putExtra("from", "SettingFragment");
                    SettingActivity.this.startActivity(intent);
                    SettingActivity.this.finish();
                } else {
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, SecurityLocksActivity.class));
                    SettingActivity.this.finish();
                }
            }
        });
        this.ll_cloud_backup.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (Common.isPurchased) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    CloudCommon.IsCameFromSettings = true;
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, DropboxLoginActivity.class));
                    SettingActivity.this.finish();
                    return;
                }
                SecurityLocksCommon.IsAppDeactive = false;
               /* InAppPurchaseActivity._cameFrom = InAppPurchaseActivity.CameFrom.Feature.ordinal();
                SettingActivity.this.startActivity(new Intent(SettingActivity.this, InAppPurchaseActivity.class));
                SettingActivity.this.finish();*/
            }
        });
        this.ll_data_recovery.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingActivity.this.number = 1;
                SecurityLocksCommon.IsAppDeactive = false;
                SettingActivity settingActivity = SettingActivity.this;
                settingActivity.requestPermission(settingActivity.PERMISSIONS_);
            }
        });
        this.ll_recovery_credentials.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(SettingActivity.this.LoginOption)) {
                    SecurityLocksCommon.isBackupPattern = true;
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, ConfirmPatternActivity.class));
                    SettingActivity.this.finish();
                } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(SettingActivity.this.LoginOption) || SecurityLocksCommon.LoginOptions.Password.toString().equals(SettingActivity.this.LoginOption)) {
                    SecurityLocksCommon.isBackupPasswordPin = true;
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, ConfirmPasswordPinActivity.class));
                    SettingActivity.this.finish();
                } else if (SecurityLocksCommon.LoginOptions.Calculator.toString().equals(SettingActivity.this.LoginOption)) {
                    SecurityLocksCommon.isBackupPasswordPin = true;
                    Intent intent = new Intent(SettingActivity.this, CalculatorPinSetting.class);
                    intent.putExtra("from", "SettingFragment");
                    SettingActivity.this.startActivity(intent);
                    SettingActivity.this.finish();
                }
            }
        });
        this.ll_decoy_security_lock.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(SettingActivity.this.LoginOption)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    SecurityLocksCommon.isSettingDecoy = true;
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, ConfirmPatternActivity.class));
                    SettingActivity.this.finish();
                } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(SettingActivity.this.LoginOption) || SecurityLocksCommon.LoginOptions.Password.toString().equals(SettingActivity.this.LoginOption)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    SecurityLocksCommon.isSettingDecoy = true;
                    SettingActivity.this.startActivity(new Intent(SettingActivity.this, ConfirmPasswordPinActivity.class));
                    SettingActivity.this.finish();
                } else {
                    Toast.makeText(SettingActivity.this, (int) R.string.decoy_mode_toast_disguise, 1).show();
                }
            }
        });
        this.ll_storage_options.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingActivity.this.SetStorageOption();
            }
        });
        this.ll_panic_switch.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                SettingActivity.this.startActivity(new Intent(SettingActivity.this, PanicSwitchActivity.class));
                SettingActivity.this.finish();
            }
        });
        this.ll_stealth_mode.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                if (SettingActivity.this.securityCredentialsSharedPreferences.isGetCalModeEnable()) {
                    SettingActivity.this.showDisguiseModeSelected();
                }
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 42 && Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            Uri data = intent.getData();
            String uri = data.toString();
            getContentResolver().takePersistableUriPermission(data, 3);
            List<UriPermission> persistedUriPermissions = getContentResolver().getPersistedUriPermissions();
            this.permissions = persistedUriPermissions;
            String uri2 = persistedUriPermissions.get(0).getUri().toString();
            if (uri2.contains("primary") || !uri2.contains("-")) {
                Toast.makeText(this, (int) R.string.lblwrong_sd_card_permssion, 0).show();
                this.ll_tick.setVisibility(4);
            } else if (uri2.substring(uri2.length() - 3).contains("%3A")) {
                StorageOptionSharedPreferences.GetObject(this).SetSDCardUri(uri);
                this.ll_tick.setVisibility(0);
            } else {
                Toast.makeText(this, (int) R.string.lblwrong_sd_card_permssion, 0).show();
                this.ll_tick.setVisibility(4);
            }
        }
    }

    public void btnBackonClick() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, FeaturesActivity.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SetStorageOption() {
        this.storageOptionSharedPreferences = StorageOptionSharedPreferences.GetObject(this);
        if (Build.VERSION.SDK_INT < StorageOptionsCommon.Kitkat) {
            this.isSDCard = false;
            StorageOptionsCommon.IsStorageSDCard = this.storageOptionSharedPreferences.GetIsStorageSDCard();
            this.isSDCard = StorageOptionsCommon.IsStorageSDCard;
            final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.set_storage_option_popup);
            dialog.setCancelable(true);
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
            LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_PhoneMemory);
            LinearLayout linearLayout3 = (LinearLayout) dialog.findViewById(R.id.ll_SDCard);
            final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.cbPhoneMemory);
            final CheckBox checkBox2 = (CheckBox) dialog.findViewById(R.id.cbSDCard);
            TextView textView = (TextView) dialog.findViewById(R.id.lblPhoneMemory);
            TextView textView2 = (TextView) dialog.findViewById(R.id.lblSDCard);
            checkBox.setEnabled(false);
            checkBox2.setEnabled(false);
            if (!StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage) {
                textView.setText(StorageOptionsCommon.STORAGEPATH_1);
                linearLayout3.setVisibility(4);
                linearLayout3.setEnabled(false);
            } else {
                textView.setText(StorageOptionsCommon.STORAGEPATH_1);
                textView2.setText(StorageOptionsCommon.STORAGEPATH_2);
            }
            if (StorageOptionsCommon.IsStorageSDCard) {
                checkBox2.setChecked(true);
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
                checkBox2.setChecked(false);
            }
            linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.12
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    checkBox.setChecked(true);
                    checkBox2.setChecked(false);
                }
            });
            linearLayout3.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.13
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (StorageOptionsCommon.IsDeviceHaveMoreThenOneStorage) {
                        checkBox2.setChecked(true);
                        checkBox.setChecked(false);
                    }
                }
            });
            ((LinearLayout) dialog.findViewById(R.id.ll_Save)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.14
                /* JADX WARN: Type inference failed for: r0v5, types: [net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity$14$1] */
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                    if (SettingActivity.this.isSDCard && checkBox2.isChecked()) {
                        SettingActivity settingActivity = SettingActivity.this;
                        Toast.makeText(settingActivity, "You are already using" + StorageOptionsCommon.STORAGEPATH_2, 0).show();
                        StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_2;
                        SettingActivity.this.storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                    } else if (SettingActivity.this.isSDCard || !checkBox.isChecked()) {
                        final MoveData moveData = MoveData.getInstance(SettingActivity.this);
                        if (StorageOptionsCommon.IsStorageSDCard) {
                            if (((float) Common.GetTotalFreeSpaceSDCard()) < Common.GetFileSize(moveData.GetAllFilesPath())) {
                                Toast.makeText(SettingActivity.this, "You dont have enough space in SD Card", 0).show();
                                return;
                            }
                        } else if (Common.GetTotalFree() < Common.GetFileSize(moveData.GetAllFilesPath())) {
                            Toast.makeText(SettingActivity.this, "You dont have enough space in Phone Memory", 0).show();
                            return;
                        }
                        if (checkBox2.isChecked()) {
                            StorageOptionsCommon.IsStorageSDCard = true;
                            SettingActivity.this.storageOptionSharedPreferences.SetIsStorageSDCard(true);
                            StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_2;
                            SettingActivity.this.storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                        } else {
                            StorageOptionsCommon.IsStorageSDCard = false;
                            SettingActivity.this.storageOptionSharedPreferences.SetIsStorageSDCard(false);
                            StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_1;
                            SettingActivity.this.storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                        }
                        SettingActivity.this.showMoveDataToOrFromSDCardProgress();
                        StorageOptionsCommon.IsAllDataMoveingInProg = true;
                        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.14.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                try {
                                    SettingActivity.DataTransferPrefs = SettingActivity.this.getSharedPreferences("DataTransferStatus", 0);
                                    SettingActivity.DataTransferprefsEditor = SettingActivity.DataTransferPrefs.edit();
                                    SettingActivity.DataTransferprefsEditor.putBoolean("isPhotoTransferComplete", false);
                                    SettingActivity.DataTransferprefsEditor.commit();
                                    SettingActivity.DataTransferprefsEditor.putBoolean("isVideoTransferComplete", false);
                                    SettingActivity.DataTransferprefsEditor.commit();
                                    SettingActivity.DataTransferprefsEditor.putBoolean("isDocumentTransferComplete", false);
                                    SettingActivity.DataTransferprefsEditor.commit();
                                    moveData.GetDataFromDataBase();
                                    moveData.MoveDataToORFromCardFromSetting();
                                    Message message = new Message();
                                    message.what = 3;
                                    SettingActivity.this.handle.sendMessage(message);
                                    Toast.makeText(SettingActivity.this, "Storage setting changed successfully.", 1).show();
                                } catch (Exception unused) {
                                    Message message2 = new Message();
                                    message2.what = 2;
                                    SettingActivity.this.handle.sendMessage(message2);
                                }
                            }
                        }.start();
                        dialog.dismiss();
                    } else {
                        SettingActivity settingActivity2 = SettingActivity.this;
                        Toast.makeText(settingActivity2, "You are already using" + StorageOptionsCommon.STORAGEPATH_1, 0).show();
                        StorageOptionsCommon.STORAGEPATH = StorageOptionsCommon.STORAGEPATH_1;
                        SettingActivity.this.storageOptionSharedPreferences.SetStoragePath(StorageOptionsCommon.STORAGEPATH);
                    }
                }
            });
            dialog.show();
        } else if (Build.VERSION.SDK_INT == 19) {
            Toast.makeText(this, (int) R.string.lbl_KitKat_StorageOption_Alert, 1).show();
        } else if (Build.VERSION.SDK_INT >= 21) {
            Toast.makeText(this, (int) R.string.lbl_Lollipop_StorageOption_Alert, 1).show();
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
        if (SecurityLocksCommon.IsAppDeactive && !StorageOptionsCommon.IsAllDataMoveingInProg) {
            finish();
            System.exit(0);
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
        if (i == 4) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, FeaturesActivity.class));
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @AfterPermissionGranted(123)
    public void requestPermission(String[] strArr) {
        SecurityLocksCommon.IsAppDeactive = false;
        if (!EasyPermissions.hasPermissions(this, strArr)) {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 123, strArr).setRationale("For the best NS Vault experience, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
        } else if (this.number == 1) {
            startActivity(new Intent(this, DataRecoveryActivity.class));
            finish();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length <= 0 || iArr[0] != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.PROCESS_OUTGOING_CALLS")) {
                EasyPermissions.hasPermissions(this, this.number == 11 ? new String[]{"android.permission.PROCESS_OUTGOING_CALLS"} : new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"});
                Toast.makeText(this, "Permission is denied", 0).show();
                return;
            }
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        } else if (hasPermissions(this, strArr)) {
            if (this.number == 1) {
                startActivity(new Intent(this, DataRecoveryActivity.class));
                finish();
            }
            Toast.makeText(this, "Permission is granted ", 0).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            String[] strArr2 = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (EasyPermissions.hasPermissions(this, strArr2)) {
                Toast.makeText(this, "Permission  again...", 0).show();
            } else {
                EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 123, strArr2).setRationale("For the best NS Vault experience, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
            }
            Toast.makeText(this, "Permission denied", 0).show();
        } else {
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        }
    }

    @Override // pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
    public void onPermissionsDenied(int i, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    public static boolean hasPermissions(Context context, String... strArr) {
        if (context == null || strArr == null) {
            return true;
        }
        for (String str : strArr) {
            if (ActivityCompat.checkSelfPermission(context, str) != 0) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDisguiseModeSelected() {
        new AlertDialog.Builder(this).setTitle("Calculator Disguise Mode").setCancelable(false).setMessage("You have already selected Calculator Disguise Mode").setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.storageoption.SettingActivity.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}
