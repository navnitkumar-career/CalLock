package com.example.gallerylock.calculator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallerylock.R;
import com.example.gallerylock.audio.BaseActivity;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.recoveryofsecuritylocks.RecoveryOfCredentialsActivity;
import com.example.gallerylock.recoveryofsecuritylocks.RecoveryOfCredentialsMethods;
import com.example.gallerylock.securitylocks.SecurityLocksActivity;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.storageoption.SettingActivity;
import com.example.gallerylock.utilities.Common;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

/* loaded from: classes2.dex */
public class CalculatorPinSetting extends BaseActivity {
    Button btnDone;
    private CalculatorPin calculator;
    public TextView displayPrimary;
    private TextView displaySecondary;
    String from;
    private HorizontalScrollView hsv;
    SecurityLocksSharedPreferences securityCredentialsSharedPreferences;
    private SensorManager sensorManager;
    public TextView tvPin;
    StringBuilder compringString = new StringBuilder();
    StringBuilder builder = new StringBuilder();
    private String newCalPin = "";
    private String confirmCalPin = "";
    private String confCalPIN = "";
    private String decoyPIN = "";
    private String decoyConfirmPIN = "";
    boolean isSettingDecoy = false;
    boolean isPinNotMatch = false;
    private boolean isconfirmDisguiseMode = false;
    String LoginOption = "";
    String isconfirmdialog = "";

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        char c;
        char c2;
        super.onCreate(bundle);
        SecurityLocksCommon.IsAppDeactive = false;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.sensorManager = (SensorManager) getSystemService("sensor");
        if (defaultSharedPreferences.getBoolean("pref_dark", false)) {
            String string = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string.hashCode()) {
                case 48:
                    if (string.equals("0")) {
                        c2 = 0;
                        break;
                    }
                    c2 = 65535;
                    break;
                case 49:
                    if (string.equals("1")) {
                        c2 = 1;
                        break;
                    }
                    c2 = 65535;
                    break;
                case 50:
                    if (string.equals("2")) {
                        c2 = 2;
                        break;
                    }
                    c2 = 65535;
                    break;
                case 51:
                    if (string.equals("3")) {
                        c2 = 3;
                        break;
                    }
                    c2 = 65535;
                    break;
                case 52:
                    if (string.equals("4")) {
                        c2 = 4;
                        break;
                    }
                    c2 = 65535;
                    break;
                case 53:
                    if (string.equals("5")) {
                        c2 = 5;
                        break;
                    }
                    c2 = 65535;
                    break;
                default:
                    c2 = 65535;
                    break;
            }
            if (c2 == 0) {
                setTheme(R.style.AppTheme_Dark_Blue);
            } else if (c2 == 1) {
                setTheme(R.style.AppTheme_Dark_Cyan);
            } else if (c2 == 2) {
                setTheme(R.style.AppTheme_Dark_Gray);
            } else if (c2 == 3) {
                setTheme(R.style.AppTheme_Dark_Green);
            } else if (c2 == 4) {
                setTheme(R.style.AppTheme_Dark_Purple);
            } else if (c2 == 5) {
                setTheme(R.style.AppTheme_Dark_Red);
            }
        } else {
            String string2 = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string2.hashCode()) {
                case 48:
                    if (string2.equals("0")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 49:
                    if (string2.equals("1")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 50:
                    if (string2.equals("2")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 51:
                    if (string2.equals("3")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 52:
                    if (string2.equals("4")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 53:
                    if (string2.equals("5")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            if (c == 0) {
                setTheme(R.style.AppTheme_Light_Blue);
            } else if (c == 1) {
                setTheme(R.style.AppTheme_Light_Cyan);
            } else if (c == 2) {
                setTheme(R.style.AppTheme_Light_Gray);
            } else if (c == 3) {
                setTheme(R.style.AppTheme_Light_Green);
            } else if (c == 4) {
                setTheme(R.style.AppTheme_Light_Purple);
            } else if (c == 5) {
                setTheme(R.style.AppTheme_Light_Red);
            }
        }
        setContentView(R.layout.activity_calculator_pin_setting);
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this);
        this.securityCredentialsSharedPreferences = GetObject;
        this.LoginOption = GetObject.GetLoginType();
        this.tvPin = (TextView) findViewById(R.id.tv_pin);
        this.displayPrimary = (TextView) findViewById(R.id.display_primary);
        this.displaySecondary = (TextView) findViewById(R.id.display_secondary);
        this.hsv = (HorizontalScrollView) findViewById(R.id.display_hsv);
        Bundle extras = getIntent().getExtras();
        if (getIntent().getStringExtra("isconfirmdialog") != null) {
            this.isconfirmdialog = getIntent().getStringExtra("isconfirmdialog");
        }
        if (extras != null) {
            String string3 = extras.getString("from");
            this.from = string3;
            if (string3 == null || !string3.equals("SettingFragment")) {
                this.tvPin.setText(R.string.cal_pin_text);
            } else if (this.securityCredentialsSharedPreferences.isGetCalModeEnable()) {
                this.tvPin.setText(R.string.cal_pin_text_conf);
                this.isconfirmDisguiseMode = true;
            } else {
                this.tvPin.setText(R.string.cal_pin_text);
            }
        } else {
            this.tvPin.setText(R.string.cal_pin_text);
        }
        TextView[] textViewArr = {(TextView) findViewById(R.id.button_0), (TextView) findViewById(R.id.button_1), (TextView) findViewById(R.id.button_2), (TextView) findViewById(R.id.button_3), (TextView) findViewById(R.id.button_4), (TextView) findViewById(R.id.button_5), (TextView) findViewById(R.id.button_6), (TextView) findViewById(R.id.button_7), (TextView) findViewById(R.id.button_8), (TextView) findViewById(R.id.button_9)};
        int i = 0;
        for (int i2 = 10; i < i2; i2 = 10) {
            final String str = (String) textViewArr[i].getText();
            textViewArr[i].setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    CalculatorPinSetting.this.calculator.digit(str.charAt(0));
                    CalculatorPinSetting calculatorPinSetting = CalculatorPinSetting.this;
                    StringBuilder sb = calculatorPinSetting.builder;
                    sb.append(str.charAt(0));
                    calculatorPinSetting.compringString = sb;
                    if (CalculatorPinSetting.this.isPinNotMatch) {
                        CalculatorPinSetting.this.tvPin.setText(R.string.cal_pin_text_confirm);
                    }
                }
            });
            i++;
        }
        TextView[] textViewArr2 = {(TextView) findViewById(R.id.button_sin), (TextView) findViewById(R.id.button_cos), (TextView) findViewById(R.id.button_tan), (TextView) findViewById(R.id.button_ln), (TextView) findViewById(R.id.button_log), (TextView) findViewById(R.id.button_factorial), (TextView) findViewById(R.id.button_pi), (TextView) findViewById(R.id.button_e), (TextView) findViewById(R.id.button_exponent), (TextView) findViewById(R.id.button_start_parenthesis), (TextView) findViewById(R.id.button_end_parenthesis), (TextView) findViewById(R.id.button_square_root), (TextView) findViewById(R.id.button_add), (TextView) findViewById(R.id.button_subtract), (TextView) findViewById(R.id.button_multiply), (TextView) findViewById(R.id.button_divide), (TextView) findViewById(R.id.button_decimal), (TextView) findViewById(R.id.button_equals)};
        for (int i3 = 0; i3 < 18; i3++) {
            final String str2 = (String) textViewArr2[i3].getText();
            textViewArr2[i3].setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (str2.equals("sin")) {
                        CalculatorPinSetting.this.calculator.opNum('s');
                    }
                    if (str2.equals("cos")) {
                        CalculatorPinSetting.this.calculator.opNum('c');
                    }
                    if (str2.equals("tan")) {
                        CalculatorPinSetting.this.calculator.opNum('t');
                    }
                    if (str2.equals("ln")) {
                        CalculatorPinSetting.this.calculator.opNum('n');
                    }
                    if (str2.equals("log")) {
                        CalculatorPinSetting.this.calculator.opNum('l');
                    }
                    if (str2.equals("!")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals("π")) {
                        CalculatorPinSetting.this.calculator.num((char) 960);
                    }
                    if (str2.equals("e")) {
                        CalculatorPinSetting.this.calculator.num('e');
                    }
                    if (str2.equals("^")) {
                        CalculatorPinSetting.this.calculator.numOpNum('^');
                    }
                    if (str2.equals("%")) {
                        CalculatorPinSetting.this.ResetorSetCalPin();
                    }
                    if (str2.equals("(")) {
                        CalculatorPinSetting.this.calculator.parenthesisLeft();
                    }
                    if (str2.equals(")")) {
                        CalculatorPinSetting.this.calculator.parenthesisRight();
                    }
                    if (str2.equals("√")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals("÷")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals("×")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals("−")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals("+")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals(".")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                    }
                    if (str2.equals("=") && !CalculatorPinSetting.this.getText().equals("")) {
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.disable_operation_cal, 0).show();
                        CalculatorPinSetting.this.builder.setLength(0);
                        CalculatorPinSetting.this.compringString.setLength(0);
                    }
                }
            });
        }
        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CalculatorPinSetting.this.calculator.delete();
                CalculatorPinSetting.this.builder.setLength(0);
                CalculatorPinSetting.this.compringString.setLength(0);
            }
        });
        findViewById(R.id.button_delete).setOnLongClickListener(new View.OnLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.4
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                if (!CalculatorPinSetting.this.displayPrimary.getText().toString().trim().equals("")) {
                    View findViewById = CalculatorPinSetting.this.findViewById(R.id.display_overlay);
                    if (Build.VERSION.SDK_INT >= 21) {
                        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(findViewById, findViewById.getMeasuredWidth() / 2, findViewById.getMeasuredHeight(), 0.0f, (int) Math.hypot(findViewById.getWidth(), findViewById.getHeight()));
                        createCircularReveal.setDuration(300L);
                        createCircularReveal.addListener(new Animator.AnimatorListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.4.1
                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationRepeat(Animator animator) {
                            }

                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                CalculatorPinSetting.this.calculator.setText("");
                            }

                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animator) {
                                CalculatorPinSetting.this.calculator.setText("");
                            }
                        });
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(findViewById, "alpha", 0.0f);
                        ofFloat.setInterpolator(new DecelerateInterpolator());
                        ofFloat.setDuration(200L);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playSequentially(createCircularReveal, ofFloat);
                        findViewById.setAlpha(1.0f);
                        animatorSet.start();
                    } else {
                        CalculatorPinSetting.this.calculator.setText("");
                    }
                }
                return false;
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, CalculatorSetting.class));
            }
        });
        this.calculator = new CalculatorPin(this);
        if (bundle != null) {
            setText(bundle.getString("text"));
        }
        if (defaultSharedPreferences.getInt("launch_count", 5) == 0) {
            RateDialog.show(this);
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            edit.putInt("launch_count", -1);
            edit.apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        setText(getText());
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("text", getText());
    }

    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        setText(bundle.getString("text"));
    }

    public String getText() {
        return this.calculator.getText();
    }

    public void setText(String str) {
        this.calculator.setText(str);
    }

    public void displayPrimaryScrollLeft(String str) {
        this.displayPrimary.setText(formatToDisplayMode(str));
        this.hsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.6
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                CalculatorPinSetting.this.hsv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                CalculatorPinSetting.this.hsv.fullScroll(17);
            }
        });
    }

    public void displayPrimaryScrollRight(String str) {
        this.displayPrimary.setText(formatToDisplayMode(str));
        this.hsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.7
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                CalculatorPinSetting.this.hsv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                CalculatorPinSetting.this.hsv.fullScroll(66);
            }
        });
    }

    public void displaySecondary(String str) {
        this.displaySecondary.setText(formatToDisplayMode(str));
    }

    private String formatToDisplayMode(String str) {
        return str.replace("/", "÷").replace("*", "×").replace("-", "−").replace("n ", "ln(").replace("l ", "log(").replace("√ ", "√(").replace("s ", "sin(").replace("c ", "cos(").replace("t ", "tan(").replace(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, "").replace("∞", "Infinity").replace("NaN", "Undefined");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void ResetorSetCalPin() {
       /* if (!getText().trim().isEmpty()) {
            String str = this.from;
            if (str != null && str.equals("SettingFragment")) {
                this.confCalPIN = this.securityCredentialsSharedPreferences.GetSecurityCredential();
                if (getText().contentEquals(this.confCalPIN)) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    if (SecurityLocksCommon.isBackupPasswordPin) {
                        SecurityLocksCommon.isBackupPasswordPin = false;
                        startActivity(new Intent(this, RecoveryOfCredentialsActivity.class));
                        finish();
                        return;
                    }
                    Intent intent = new Intent(this, SecurityLocksActivity.class);
                    intent.putExtra("isconfirmdialog", "isconfirmdialog");
                    startActivity(intent);
                    finish();
                    return;
                }
                Toast.makeText(this, (int) R.string.lbl_Pin_doesnt_match, 0).show();
            } else if (getText().length() <= 4) {
                Toast.makeText(this, (int) R.string.lbl_Pin_Limit, 0).show();
            } else if (getText().length() > 9) {
                Toast.makeText(this, (int) R.string.lbl_Pin_not_greater_than_8, 0).show();
            } else if (!this.isSettingDecoy) {
                if (this.newCalPin.equals("")) {
                    this.tvPin.setText(R.string.cal_pin_text_confirm);
                    this.newCalPin = getText();
                    setText("");
                } else if (this.confirmCalPin.equals("")) {
                    String text = getText();
                    this.confirmCalPin = text;
                    if (text.equals(this.newCalPin)) {
                        this.securityCredentialsSharedPreferences.SetLoginType(SecurityLocksCommon.LoginOptions.Calculator.toString());
                        this.securityCredentialsSharedPreferences.SetSecurityCredential(this.confirmCalPin);
                        Toast.makeText(this, "Calculator PIN set successfully.....", 0).show();
                        this.securityCredentialsSharedPreferences.RemoveDecoySecurityCredential();
                        this.securityCredentialsSharedPreferences.isSetCalModeEnable(true);
                        if (SecurityLocksCommon.IsFirstLogin) {
                            SecurityLocksCommon.IsFirstLogin = false;
                            this.securityCredentialsSharedPreferences.SetIsFirstLogin(false);
                        }
                        if (this.isSettingDecoy) {
                            Intent intent2 = new Intent(this, FeaturesActivity.class);
                            SecurityLocksCommon.IsAppDeactive = false;
                            startActivity(intent2);
                            finish();
                        } else if (!this.isconfirmdialog.equals("isconfirmdialog")) {
                            if (this.securityCredentialsSharedPreferences.GetShowFirstTimeEmailPopup() && SecurityLocksCommon.LoginOptions.None.toString().equals(this.LoginOption)) {
                                FirstTimeEmailDialog();
                            } else if (this.securityCredentialsSharedPreferences.GetSecurityCredential().length() <= 0 || this.securityCredentialsSharedPreferences.GetEmail().length() <= 0) {
                                FirstTimeEmailDialog();
                            } else {
                                Log.d("Calculator Pin Setting", "Calculator dialog will appear");
                                SecurityLocksCommon.IsAppDeactive = false;
                                startActivity(new Intent(this, FeaturesActivity.class));
                                finish();
                            }
                        } else if (this.securityCredentialsSharedPreferences.GetEmail().trim().isEmpty()) {
                            FirstTimeEmailDialog();
                        } else {
                            SecurityLocksCommon.IsAppDeactive = false;
                            Intent intent3 = new Intent(this, FeaturesActivity.class);
                            if (SecurityLocksCommon.IsFirstLogin) {
                                SecurityLocksCommon.IsFirstLogin = false;
                                this.securityCredentialsSharedPreferences.SetIsFirstLogin(false);
                            }
                            startActivity(intent3);
                            overridePendingTransition(17432576, 17432577);
                            finish();
                        }
                    } else {
                        Toast.makeText(this, (int) R.string.lbl_Password_doesnt_match, 0).show();
                        this.confirmCalPin = "";
                        this.tvPin.setText(R.string.lbl_Pin_doesnt_match);
                        setText("");
                        this.isPinNotMatch = true;
                    }
                }
            }
        } else {
            Toast.makeText(this, "Enter your PIN", 0).show();
        }*/

//        SecurityLocksCommon.IsAppDeactive = false;
        Intent intent = new Intent(CalculatorPinSetting.this, FeaturesActivity.class);
        /*if (SecurityLocksCommon.IsFirstLogin) {
            SecurityLocksCommon.IsFirstLogin = false;
            CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetIsFirstLogin(false);
        }*/
        CalculatorPinSetting.this.startActivity(intent);
        CalculatorPinSetting.this.overridePendingTransition(17432576, 17432577);
        CalculatorPinSetting.this.finish();
    }

    public void DecoySetPopup() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.confirmation_message_box);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_background);
        TextView textView = (TextView) dialog.findViewById(R.id.tvmessagedialogtitle);
        textView.setTypeface(createFromAsset);
        textView.setText(R.string.lbl_msg_want_to_set_decoy_pin_ornot);
        ((TextView) dialog.findViewById(R.id.lbl_Ok)).setText("Yes");
        ((TextView) dialog.findViewById(R.id.lbl_Cancel)).setText("No");
        ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CalculatorPinSetting.this.tvPin.setText("Set Decoy PIN");
                CalculatorPinSetting.this.isSettingDecoy = true;
                CalculatorPinSetting.this.setText("");
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                Intent intent = new Intent(CalculatorPinSetting.this, FeaturesActivity.class);
                if (SecurityLocksCommon.IsFirstLogin) {
                    SecurityLocksCommon.IsFirstLogin = false;
                    CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetIsFirstLogin(false);
                }
                CalculatorPinSetting.this.startActivity(intent);
                CalculatorPinSetting.this.overridePendingTransition(17432576, 17432577);
                CalculatorPinSetting.this.finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DecoyModeEnable() {
        String str = this.from;
        if (str != null && str.equals("isSettingDecoy") && !getText().trim().isEmpty() && getText().length() > 4 && getText().length() < 9) {
            this.confCalPIN = this.securityCredentialsSharedPreferences.GetSecurityCredential();
            if (this.decoyPIN.equals("")) {
                String text = getText();
                this.decoyPIN = text;
                this.isSettingDecoy = true;
                if (text.equals(this.confCalPIN)) {
                    Toast.makeText(this, "Cant set same as master pin", 1).show();
                    setText("");
                    return;
                }
                setText("");
                this.isSettingDecoy = true;
                this.tvPin.setText("Please enter confirm decoy PIN");
            } else if (this.decoyConfirmPIN.equals("")) {
                this.decoyConfirmPIN = getText();
                setText("");
                if (this.decoyConfirmPIN.equals(this.decoyPIN)) {
                    this.securityCredentialsSharedPreferences.SetDecoySecurityCredential(this.decoyConfirmPIN);
                    Toast.makeText(this, "Decoy PIN set successfully ", 0).show();
                    SecurityLocksCommon.IsAppDeactive = false;
                    Intent intent = new Intent(this, FeaturesActivity.class);
                    if (SecurityLocksCommon.IsFirstLogin) {
                        SecurityLocksCommon.IsFirstLogin = false;
                        this.securityCredentialsSharedPreferences.SetIsFirstLogin(false);
                    }
                    startActivity(intent);
                    overridePendingTransition(17432576, 17432577);
                    finish();
                    return;
                }
                Toast.makeText(this, "Decoy PIN doest match...", 0).show();
                this.tvPin.setText("Decoy PIN doest match...");
                this.decoyConfirmPIN = "";
            } else {
                Toast.makeText(this, "Please enter minimum four digits", 0).show();
            }
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.audio.BaseActivity, net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        SecurityLocksCommon.IsAppDeactive = false;
        if (SecurityLocksCommon.isBackupPasswordPin) {
            Intent intent = new Intent(this, SettingActivity.class);
            overridePendingTransition(17432576, 17432577);
            startActivity(intent);
            SecurityLocksCommon.isBackupPasswordPin = false;
            finish();
        } else if (this.from.equals("SettingFragment")) {
            Intent intent2 = new Intent(this, SettingActivity.class);
            overridePendingTransition(17432576, 17432577);
            startActivity(intent2);
            finish();
        } else {
            startActivity(new Intent(this, SecurityLocksActivity.class));
            overridePendingTransition(17432576, 17432577);
            finish();
        }
    }

    public void FirstTimeEmailDialog() {
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "ebrima.ttf");
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.backup_email_popup);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final EditText editText = (EditText) dialog.findViewById(R.id.txtemailid);
        ((TextView) dialog.findViewById(R.id.lblEmail_popup_title)).setTypeface(createFromAsset);
        editText.setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_email_warning_title)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lbl_email_warning_desc)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lblSave)).setTypeface(createFromAsset);
        ((TextView) dialog.findViewById(R.id.lblSkip)).setTypeface(createFromAsset);
        ((LinearLayout) dialog.findViewById(R.id.ll_Save)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (editText.getText().length() > 0) {
                    String obj = editText.getText().toString();
                    if (new RecoveryOfCredentialsMethods().isEmailValid(obj)) {
                        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetEmail(obj);
                        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(false);
                        SecurityLocksCommon.IsAppDeactive = false;
                        Toast.makeText(CalculatorPinSetting.this, (int) R.string.toast_Email_Saved, 1).show();
                        if (!CalculatorPinSetting.this.isconfirmdialog.equals("isconfirmdialog")) {
                            Log.d("CalculaltorPinSetting", "dialog for calculator will appear");
                            SecurityLocksCommon.IsAppDeactive = false;
                            CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                            CalculatorPinSetting.this.finish();
                        } else {
                            SecurityLocksCommon.IsAppDeactive = false;
                            CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                            CalculatorPinSetting.this.finish();
                        }
                        dialog.dismiss();
                        Common.loginCount = 2;
                        CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetRateCount(Common.loginCount);
                        return;
                    }
                    Toast.makeText(CalculatorPinSetting.this, (int) R.string.toast_Invalid_Email, 0).show();
                    return;
                }
                Toast.makeText(CalculatorPinSetting.this, (int) R.string.toast_Enter_Email, 0).show();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.ll_Skip)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorPinSetting.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetShowFirstTimeEmailPopup(false);
                SecurityLocksCommon.IsAppDeactive = false;
                Toast.makeText(CalculatorPinSetting.this, (int) R.string.toast_Skip, 1).show();
                if (!CalculatorPinSetting.this.isconfirmdialog.equals("isconfirmdialog")) {
                    Log.d("Calculator Pin setting", "Calculator dialog will appear");
                    SecurityLocksCommon.IsAppDeactive = false;
                    CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                    CalculatorPinSetting.this.finish();
                } else {
                    SecurityLocksCommon.IsAppDeactive = false;
                    CalculatorPinSetting.this.startActivity(new Intent(CalculatorPinSetting.this, FeaturesActivity.class));
                    CalculatorPinSetting.this.finish();
                }
                dialog.dismiss();
                Common.loginCount = 1;
                CalculatorPinSetting.this.securityCredentialsSharedPreferences.SetRateCount(Common.loginCount);
            }
        });
        dialog.show();
    }
}
