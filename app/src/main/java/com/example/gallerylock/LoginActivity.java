package com.example.gallerylock;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gallerylock.calculator.CalculatorPinSetting;
import com.example.gallerylock.calculator.MyCalculatorActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.hackattempt.CameraPreview;
import com.example.gallerylock.hackattempt.HackAttemptEntity;
import com.example.gallerylock.hackattempt.HackAttemptsSharedPreferences;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.panicswitch.PanicSwitchSharedPreferences;
import com.example.gallerylock.securitylocks.ConfirmLockPatternViewLogin;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.storageoption.AppSettingsSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionSharedPreferences;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/* loaded from: classes2.dex */
public class LoginActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener {
    static int hackAttemptCount = 0;
    public static Camera mCamera = null;
    public static CameraPreview mCameraPreview = null;
    private static long startTime = 0;
    public static TextView txt_wrong_pttern = null;
    public static String wrongPassword = "";
    private ImageButton btnLogin;
    HackAttemptLoginClass hackAttemptLoginClass;
    private RelativeLayout imgKey;
    private RelativeLayout imgKeyfail;
    String mypass;
    PanicSwitchSharedPreferences panicSwitchSharedPreferences;
    private SecurityLocksSharedPreferences securityLocksSharedPreferences;
    private SensorManager sensorManager;
    private TextView tv_forgot;
    private EditText txtPassword;
    private TextView txtTimer;
    private TextView txt_wrong_password_pin;
    private TextView txtforgotpassword;
    private TextView txtforgotpattern;
    private String LoginOption = "";
    long timeInMilliseconds = 0;
    long timeSwapBuff = 0;
    long updatedTime = 0;
    private Handler customHandler = new Handler();
    private Runnable updateTimerThread = new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.6
        @Override // java.lang.Runnable
        public void run() {
            Calendar calendar = Calendar.getInstance();
            LoginActivity.this.timeInMilliseconds = LoginActivity.startTime - calendar.getTimeInMillis();
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.updatedTime = loginActivity.timeSwapBuff + LoginActivity.this.timeInMilliseconds;
            int i = (int) (LoginActivity.this.updatedTime / 1000);
            int i2 = (i / 60) / 60;
            int i3 = i % 60;
            if (LoginActivity.startTime > calendar.getTimeInMillis()) {
                LoginActivity.this.customHandler.postDelayed(this, 0L);
                return;
            }
            Common.HackAttemptCount = 0;
            LoginActivity.hackAttemptCount = 0;
            LoginActivity.this.txtPassword.setEnabled(true);
        }
    };

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        HackAttemptLoginClass hackAttemptLoginClass = new HackAttemptLoginClass();
        this.hackAttemptLoginClass = hackAttemptLoginClass;
        hackAttemptLoginClass.initCamera(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r1v14, types: [net.newsoftwares.hidepicturesvideos.LoginActivity$1] */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        SecurityLocksCommon.IsAppDeactive = true;
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this);
        this.securityLocksSharedPreferences = GetObject;
        this.LoginOption = GetObject.GetLoginType();
        Common.loginCountForRateAndReview = this.securityLocksSharedPreferences.GetRateCountForRateAndReview();
        Common.loginCountForRateAndReview++;
        this.securityLocksSharedPreferences.SetRateCountForRateAndReview(Common.loginCountForRateAndReview);
        this.mypass = this.securityLocksSharedPreferences.GetSecurityCredential();
        StorageOptionsCommon.STORAGEPATH = StorageOptionSharedPreferences.GetObject(this).GetStoragePath();
        SecurityLocksCommon.PatternPassword = this.securityLocksSharedPreferences.GetSecurityCredential();
        Utilities.CheckDeviceStoragePaths(this);
        Common.initImageLoader(this);
        new Thread() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Utilities.changeFileExtention(StorageOptionsCommon.VIDEOS);
                    Utilities.changeFileExtention(StorageOptionsCommon.DOCUMENTS);
                } catch (Exception unused) {
                    Log.v("Login ", "error in changeVideosExtention method");
                }
            }
        }.start();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        PanicSwitchSharedPreferences GetObject2 = PanicSwitchSharedPreferences.GetObject(this);
        this.panicSwitchSharedPreferences = GetObject2;
        PanicSwitchCommon.IsFlickOn = GetObject2.GetIsFlickOn();
        PanicSwitchCommon.IsShakeOn = this.panicSwitchSharedPreferences.GetIsShakeOn();
        PanicSwitchCommon.IsPalmOnFaceOn = this.panicSwitchSharedPreferences.GetIsPalmOnScreenOn();
        PanicSwitchCommon.SwitchingApp = this.panicSwitchSharedPreferences.GetSwitchApp();
        if (this.securityLocksSharedPreferences.GetIsFirstLogin()) {
            SecurityLocksCommon.IsFirstLogin = true;
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, CalculatorPinSetting.class));
            finish();
        } else if (SecurityLocksCommon.LoginOptions.Calculator.toString().equals(this.LoginOption)) {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, MyCalculatorActivity.class));
            finish();
        } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(this.LoginOption)) {
            setContentView(R.layout.pattern_login_activity);
            TextView textView = (TextView) findViewById(R.id.txt_wrong_pttern);
            txt_wrong_pttern = textView;
            textView.setVisibility(4);
            ConfirmLockPatternViewLogin confirmLockPatternViewLogin = (ConfirmLockPatternViewLogin) findViewById(R.id.pattern_view);
            TextView textView2 = (TextView) findViewById(R.id.lblforgotpattern);
            this.txtforgotpattern = textView2;
            textView2.setVisibility(0);
            confirmLockPatternViewLogin.setPracticeMode(true);
            confirmLockPatternViewLogin.invalidate();
            this.txtforgotpattern.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (!Utilities.isNetworkOnline(LoginActivity.this)) {
                        LoginActivity.txt_wrong_pttern.setVisibility(0);
                        LoginActivity.txt_wrong_pttern.setText(R.string.toast_connection_error);
                    } else if (LoginActivity.this.securityLocksSharedPreferences.GetSecurityCredential().length() <= 0 || LoginActivity.this.securityLocksSharedPreferences.GetEmail().length() <= 0) {
                        LoginActivity.txt_wrong_pttern.setVisibility(0);
                        LoginActivity.txt_wrong_pttern.setText(R.string.toast_forgot_recovery_fail_Pattern);
                    } else {
                        new MyAsyncTask().execute(LoginActivity.this.mypass, LoginActivity.this.securityLocksSharedPreferences.GetEmail(), LoginActivity.this.LoginOption);
                        Toast.makeText(LoginActivity.this, (int) R.string.toast_forgot_recovery_Success_Pattern, 0).show();
                    }
                }
            });
        } else {
            setContentView(R.layout.activity_login);
            getWindow().setSoftInputMode(5);
            EditText editText = (EditText) findViewById(R.id.txtPassword);
            this.txtPassword = editText;
            editText.setTextColor(getResources().getColor(R.color.ColorWhite));
            this.txtforgotpassword = (TextView) findViewById(R.id.txtforgotpassword);
            TextView textView3 = (TextView) findViewById(R.id.txt_wrong_password_pin);
            this.txt_wrong_password_pin = textView3;
            textView3.setVisibility(4);
            TextView textView4 = (TextView) findViewById(R.id.tv_forgot);
            this.tv_forgot = textView4;
            textView4.setVisibility(4);
            this.txtforgotpassword.setVisibility(0);
            this.txtPassword.addTextChangedListener(new TextWatcher() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.3
                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    LoginActivity.this.txt_wrong_password_pin.setText("Enter Password");
                    if (LoginActivity.this.txtPassword.length() >= 4 && LoginActivity.this.securityLocksSharedPreferences.GetSecurityCredential().equals(LoginActivity.this.txtPassword.getText().toString())) {
                        LoginActivity.this.SignIn();
                    }
                }
            });
            this.txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.4
                @Override // android.widget.TextView.OnEditorActionListener
                public boolean onEditorAction(TextView textView5, int i, KeyEvent keyEvent) {
                    if (i != 6) {
                        return true;
                    }
                    LoginActivity.this.SignIn();
                    return true;
                }
            });
            this.txtforgotpassword.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (!Utilities.isNetworkOnline(LoginActivity.this)) {
                        ((InputMethodManager) LoginActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
                        LoginActivity.this.tv_forgot.setVisibility(0);
                        LoginActivity.this.tv_forgot.setText(R.string.toast_connection_error);
                    } else if (LoginActivity.this.securityLocksSharedPreferences.GetSecurityCredential().length() <= 0 || LoginActivity.this.securityLocksSharedPreferences.GetEmail().length() <= 0) {
                        ((InputMethodManager) LoginActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
                        if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(LoginActivity.this.LoginOption)) {
                            LoginActivity.this.tv_forgot.setVisibility(0);
                            LoginActivity.this.tv_forgot.setText(R.string.toast_forgot_recovery_fail_Pin);
                            return;
                        }
                        LoginActivity.this.tv_forgot.setVisibility(0);
                        LoginActivity.this.tv_forgot.setText(R.string.toast_forgot_recovery_fail_Password);
                    } else {
                        new MyAsyncTask().execute(LoginActivity.this.mypass, LoginActivity.this.securityLocksSharedPreferences.GetEmail(), LoginActivity.this.LoginOption);
                        if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(LoginActivity.this.LoginOption)) {
                            Toast.makeText(LoginActivity.this, (int) R.string.toast_forgot_recovery_Success_Pin, 0).show();
                        } else {
                            Toast.makeText(LoginActivity.this, (int) R.string.toast_forgot_recovery_Success_Password, 0).show();
                        }
                    }
                }
            });
            if (this.securityLocksSharedPreferences.GetSecurityCredential().length() == 0) {
                if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(this.LoginOption)) {
                    this.txtPassword.setHint(R.string.lblsetting_SecurityCredentials_SetyourPin);
                } else {
                    this.txtPassword.setHint(R.string.lblsetting_SecurityCredentials_SetyourPassword);
                }
            }
            if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(this.LoginOption)) {
                this.txtforgotpassword.setText(R.string.lbl_Forgot_pin);
                this.txtPassword.setHint(R.string.lbl_Enter_pin);
                this.txtPassword.setInputType(2);
                this.txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        hackAttemptCount = Common.HackAttemptCount;
        CheckHackAttemptCount(false);
        String string = getSharedPreferences("whatsnew", 0).getString("AppVersion", "");
        if (!this.securityLocksSharedPreferences.GetIconChanged()) {
            string.equals("");
        }
    }

    public void btnLoginonClick(View view) {
        SignIn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SignIn() {
        if (this.txtPassword.getText().toString().length() > 0) {
            this.securityLocksSharedPreferences.GetEmail();
            if (this.securityLocksSharedPreferences.GetSecurityCredential().equals(this.txtPassword.getText().toString())) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.HackAttemptCount = 0;
                SecurityLocksCommon.IsFakeAccount = 0;
                if (!SecurityLocksCommon.IsAppDeactive || SecurityLocksCommon.CurrentActivity == null) {
                    Common.loginCount = this.securityLocksSharedPreferences.GetRateCount();
                    Common.loginCount++;
                    this.securityLocksSharedPreferences.SetRateCount(Common.loginCount);
                    SecurityLocksCommon.IsnewloginforAd = true;
                    SecurityLocksCommon.IsFakeAccount = 0;
                    Intent intent = new Intent(this, FeaturesActivity.class);
                    overridePendingTransition(17432576, 17432577);
                    startActivity(intent);
                    finish();
                    return;
                }
                Common.loginCount = this.securityLocksSharedPreferences.GetRateCount();
                Common.loginCount++;
                this.securityLocksSharedPreferences.SetRateCount(Common.loginCount);
                SecurityLocksCommon.IsnewloginforAd = true;
                SecurityLocksCommon.IsFakeAccount = 0;
                Intent intent2 = new Intent(this, SecurityLocksCommon.CurrentActivity.getClass());
                overridePendingTransition(17432576, 17432577);
                startActivity(intent2);
                finish();
            } else if (this.securityLocksSharedPreferences.GetDecoySecurityCredential().equals(this.txtPassword.getText().toString())) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.HackAttemptCount = 0;
                Common.loginCount = this.securityLocksSharedPreferences.GetRateCount();
                Common.loginCount++;
                this.securityLocksSharedPreferences.SetRateCount(Common.loginCount);
                SecurityLocksCommon.IsFakeAccount = 1;
                Intent intent3 = new Intent(this, FeaturesActivity.class);
                overridePendingTransition(17432576, 17432577);
                startActivity(intent3);
                finish();
            } else {
                int i = hackAttemptCount + 1;
                hackAttemptCount = i;
                Common.HackAttemptCount = i;
                this.hackAttemptLoginClass.HackAttempt(this);
                wrongPassword = this.txtPassword.getText().toString();
                this.txtPassword.setText("");
                this.txt_wrong_password_pin.setVisibility(0);
                if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(this.LoginOption)) {
                    this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpin_Tryagain);
                } else {
                    this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpasword_Tryagain);
                }
                CheckHackAttemptCount(true);
            }
        } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(this.LoginOption)) {
            this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpin_Tryagain);
        } else {
            this.txt_wrong_password_pin.setText(R.string.lblsetting_SecurityCredentials_Setpasword_Tryagain);
        }
    }

    private void CheckHackAttemptCount(boolean z) {
        if (z && hackAttemptCount == 3) {
            Common.IsStart = true;
        }
        if (hackAttemptCount == 3) {
            TimerStart();
        }
    }

    private void TimerStart() {
        if (Common.IsStart) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(13, 30);
            calendar.getTimeInMillis();
            startTime = calendar.getTimeInMillis();
            Common.IsStart = false;
        }
        this.customHandler.postDelayed(this.updateTimerThread, 0L);
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        if (!SecurityLocksCommon.LoginOptions.Pattern.toString().equals(this.LoginOption)) {
            this.txtPassword.setText("");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        Camera camera = mCamera;
        if (camera != null) {
            camera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.release();
            mCameraPreview = null;
            mCamera = null;
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            Log.i("app close", "appdeac true");
            finish();
            System.exit(0);
        }
        Log.i("app close", "appdeac false");
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
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
            if (Build.VERSION.SDK_INT >= 16) {
                finishAffinity();
            } else {
                finish();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    /* loaded from: classes2.dex */
    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        /* JADX INFO: Access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
        }

        private MyAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Double doInBackground(String... strArr) {
            postData(strArr[0], strArr[1], strArr[2]);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(Double d) {
            if (SecurityLocksCommon.LoginOptions.Password.toString().equals(LoginActivity.this.LoginOption)) {
                Toast.makeText(LoginActivity.this, (int) R.string.toast_forgot_recovery_Success_Password_sent, 1).show();
            } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(LoginActivity.this.LoginOption)) {
                Toast.makeText(LoginActivity.this, (int) R.string.toast_forgot_recovery_Success_Pin_sent, 1).show();
            } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(LoginActivity.this.LoginOption)) {
                Toast.makeText(LoginActivity.this, (int) R.string.toast_forgot_recovery_Success_Pattern_sent, 1).show();
            }
        }

        public void postData(String str, String str2, String str3) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SecurityLocksCommon.ServerAddress);
            try {
                ArrayList arrayList = new ArrayList(3);
                arrayList.add(new BasicNameValuePair("AppType", "Android"));
                arrayList.add(new BasicNameValuePair("Email", str2));
                arrayList.add(new BasicNameValuePair("Pass", str));
                arrayList.add(new BasicNameValuePair("PassType", str3));
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList));
                inputStreamToString(defaultHttpClient.execute(httpPost).getEntity().getContent()).toString().toString().equalsIgnoreCase("Successfully");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        private StringBuilder inputStreamToString(InputStream inputStream) {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    sb.append(readLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb;
        }
    }

    /* loaded from: classes2.dex */
    public class HackAttemptLoginClass {
        Context context;
        ArrayList<HackAttemptEntity> HackAttemptEntitys = null;
        String hackAttemptPath = "";
        Camera.PictureCallback mPicture = new Camera.PictureCallback() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.HackAttemptLoginClass.2
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                File file = new File(SecurityLocksCommon.StoragePath + SecurityLocksCommon.HackAttempts);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String uuid = UUID.randomUUID().toString();
                File file2 = new File(SecurityLocksCommon.StoragePath + SecurityLocksCommon.HackAttempts + uuid + "#jpg");
                HackAttemptLoginClass hackAttemptLoginClass = HackAttemptLoginClass.this;
                hackAttemptLoginClass.hackAttemptPath = SecurityLocksCommon.StoragePath + SecurityLocksCommon.HackAttempts + uuid + "#jpg";
                if (!file2.exists()) {
                    try {
                        file2.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    fileOutputStream.write(bArr);
                    fileOutputStream.close();
                    new HackAttemptLoginClass().AddHackAttempToSharedPreference(HackAttemptLoginClass.this.context, LoginActivity.wrongPassword, HackAttemptLoginClass.this.hackAttemptPath);
                } catch (FileNotFoundException unused) {
                    Toast.makeText(LoginActivity.this, "File not found exception", 0).show();
                } catch (IOException unused2) {
                    Toast.makeText(LoginActivity.this, "IO Exception", 0).show();
                }
                camera.startPreview();
            }
        };

        public HackAttemptLoginClass() {
        }

        public void AddHackAttempToSharedPreference(Context context, String str, String str2) {
            SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(context);
            long currentTimeMillis = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date date = new Date(currentTimeMillis);
            System.out.println(simpleDateFormat.format(date));
            HackAttemptEntity hackAttemptEntity = new HackAttemptEntity();
            hackAttemptEntity.SetLoginOption(GetObject.GetLoginType());
            hackAttemptEntity.SetWrongPassword(str);
            hackAttemptEntity.SetImagePath(str2);
            hackAttemptEntity.SetHackAttemptTime(date.toString());
            hackAttemptEntity.SetIsCheck(false);
            this.HackAttemptEntitys = new ArrayList<>();
            HackAttemptsSharedPreferences GetObject2 = HackAttemptsSharedPreferences.GetObject(context);
            ArrayList<HackAttemptEntity> GetHackAttemptObject = GetObject2.GetHackAttemptObject();
            this.HackAttemptEntitys = GetHackAttemptObject;
            if (GetHackAttemptObject == null) {
                ArrayList<HackAttemptEntity> arrayList = new ArrayList<>();
                this.HackAttemptEntitys = arrayList;
                arrayList.add(hackAttemptEntity);
            } else {
                GetHackAttemptObject.add(hackAttemptEntity);
            }
            GetObject2.SetHackAttemptObject(this.HackAttemptEntitys);
        }

        /* JADX WARN: Type inference failed for: r1v2, types: [net.newsoftwares.hidepicturesvideos.LoginActivity$HackAttemptLoginClass$1] */
        public void HackAttempt(Context context) {
            this.context = context;
            if (LoginActivity.mCamera != null) {
                new Thread() { // from class: net.newsoftwares.hidepicturesvideos.LoginActivity.HackAttemptLoginClass.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Boolean bool = true;
                            while (bool.booleanValue()) {
                                if (SecurityLocksCommon.IsPreviewStarted) {
                                    LoginActivity.mCamera.takePicture(null, null, HackAttemptLoginClass.this.mPicture);
                                    bool = false;
                                }
                            }
                        } catch (Exception e) {
                            Log.v("TakePicture Exception", e.toString());
                        }
                    }
                }.start();
            }
        }

        public void initCamera(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                if (Build.VERSION.SDK_INT >= 9 && packageManager.hasSystemFeature("android.hardware.camera")) {
                    if (Camera.getNumberOfCameras() == 2) {
                        LoginActivity.mCamera = Camera.open(1);
                    } else if (Camera.getNumberOfCameras() == 1) {
                        LoginActivity.mCamera = Camera.open(0);
                    }
                    if (LoginActivity.mCamera != null) {
                        LoginActivity.mCameraPreview = new CameraPreview(context, LoginActivity.mCamera);
                        ((FrameLayout) LoginActivity.this.findViewById(R.id.camera_preview)).addView(LoginActivity.mCameraPreview);
                        SecurityLocksCommon.IsPreviewStarted = true;
                    }
                }
            } catch (Exception unused) {
                SecurityLocksCommon.IsPreviewStarted = false;
            }
        }
    }
}
