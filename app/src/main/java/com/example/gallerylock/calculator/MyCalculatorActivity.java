package com.example.gallerylock.calculator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gallerylock.R;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.securitylocks.SecurityLocksSharedPreferences;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class MyCalculatorActivity extends AppCompatActivity {
    String LoginOption;
    private Calculator calculator;
    private TextView displayPrimary;
    private TextView displaySecondary;
    private HorizontalScrollView hsv;
    SecurityLocksSharedPreferences securityCredentialsSharedPreferences;
    StringBuilder compringString = new StringBuilder();
    int counter = 0;
    StringBuilder builder = new StringBuilder();
    String mypass = "";
    String myDecoyPass = "";

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        char c;
        char c2;
        super.onCreate(bundle);
        SecurityLocksCommon.IsAppDeactive = true;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        this.securityCredentialsSharedPreferences = SecurityLocksSharedPreferences.GetObject(this);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.e("emailll", this.securityCredentialsSharedPreferences.GetEmail() + "");
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
        setContentView(R.layout.activity_my_calculator);
        String GetSecurityCredential = this.securityCredentialsSharedPreferences.GetSecurityCredential();
        this.mypass = GetSecurityCredential;
        Log.e("password123", GetSecurityCredential);
        this.myDecoyPass = this.securityCredentialsSharedPreferences.GetDecoySecurityCredential();
        this.LoginOption = this.securityCredentialsSharedPreferences.GetLoginType();
        Log.e("mypass", this.mypass);
        this.displayPrimary = (TextView) findViewById(R.id.display_primary);
        this.displaySecondary = (TextView) findViewById(R.id.display_secondary);
        this.hsv = (HorizontalScrollView) findViewById(R.id.display_hsv);
        final SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this);
        TextView[] textViewArr = {(TextView) findViewById(R.id.button_0), (TextView) findViewById(R.id.button_1), (TextView) findViewById(R.id.button_2), (TextView) findViewById(R.id.button_3), (TextView) findViewById(R.id.button_4), (TextView) findViewById(R.id.button_5), (TextView) findViewById(R.id.button_6), (TextView) findViewById(R.id.button_7), (TextView) findViewById(R.id.button_8), (TextView) findViewById(R.id.button_9)};
        int i = 0;
        for (int i2 = 10; i < i2; i2 = 10) {
            final String str = (String) textViewArr[i].getText();
            textViewArr[i].setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MyCalculatorActivity.this.calculator.digit(str.charAt(0));
                    MyCalculatorActivity myCalculatorActivity = MyCalculatorActivity.this;
                    StringBuilder sb = myCalculatorActivity.builder;
                    sb.append(str.charAt(0));
                    myCalculatorActivity.compringString = sb;
                    MyCalculatorActivity.this.counter = 0;
                    if (MyCalculatorActivity.this.mypass.trim().equals(MyCalculatorActivity.this.getText().trim())) {
                        SecurityLocksCommon.IsAppDeactive = false;
                        MyCalculatorActivity.this.builder.setLength(0);
                        Intent intent = new Intent(MyCalculatorActivity.this, FeaturesActivity.class);
                        Common.loginCount++;
                        GetObject.SetRateCount(Common.loginCount);
                        MyCalculatorActivity.this.startActivity(intent);
                        MyCalculatorActivity.this.calculator.setText("");
                        MyCalculatorActivity.this.finish();
                    }
                }
            });
            i++;
        }
        TextView[] textViewArr2 = {(TextView) findViewById(R.id.button_sin), (TextView) findViewById(R.id.button_cos), (TextView) findViewById(R.id.button_tan), (TextView) findViewById(R.id.button_ln), (TextView) findViewById(R.id.button_log), (TextView) findViewById(R.id.button_factorial), (TextView) findViewById(R.id.button_pi), (TextView) findViewById(R.id.button_e), (TextView) findViewById(R.id.button_exponent), (TextView) findViewById(R.id.button_start_parenthesis), (TextView) findViewById(R.id.button_end_parenthesis), (TextView) findViewById(R.id.button_square_root), (TextView) findViewById(R.id.button_add), (TextView) findViewById(R.id.button_subtract), (TextView) findViewById(R.id.button_multiply), (TextView) findViewById(R.id.button_divide), (TextView) findViewById(R.id.button_decimal), (TextView) findViewById(R.id.button_equals)};
        for (int i3 = 0; i3 < 18; i3++) {
            final String str2 = (String) textViewArr2[i3].getText();
            textViewArr2[i3].setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (str2.equals("sin")) {
                        MyCalculatorActivity.this.calculator.opNum('s');
                    }
                    if (str2.equals("cos")) {
                        MyCalculatorActivity.this.calculator.opNum('c');
                    }
                    if (str2.equals("tan")) {
                        MyCalculatorActivity.this.calculator.opNum('t');
                    }
                    if (str2.equals("ln")) {
                        MyCalculatorActivity.this.calculator.opNum('n');
                    }
                    if (str2.equals("log")) {
                        MyCalculatorActivity.this.calculator.opNum('l');
                    }
                    if (str2.equals("!")) {
                        MyCalculatorActivity.this.counter = 0;
                        MyCalculatorActivity.this.calculator.numOp('!');
                    }
                    if (str2.equals("π")) {
                        MyCalculatorActivity.this.calculator.num((char) 960);
                    }
                    if (str2.equals("e")) {
                        MyCalculatorActivity.this.calculator.num('e');
                    }
                    if (str2.equals("%")) {
                        MyCalculatorActivity.this.calculator.numOp('%');
                    }
                    if (str2.equals("(")) {
                        MyCalculatorActivity.this.calculator.parenthesisLeft();
                    }
                    if (str2.equals(")")) {
                        MyCalculatorActivity.this.calculator.parenthesisRight();
                    }
                    if (str2.equals("√")) {
                        MyCalculatorActivity.this.calculator.opNum((char) 8730);
                        MyCalculatorActivity.this.counter = 0;
                    }
                    if (str2.equals("÷")) {
                        MyCalculatorActivity.this.calculator.numOpNum(IOUtils.DIR_SEPARATOR_UNIX);
                        MyCalculatorActivity.this.counter = 0;
                    }
                    if (str2.equals("×")) {
                        MyCalculatorActivity.this.counter = 0;
                        MyCalculatorActivity.this.calculator.numOpNum('*');
                    }
                    if (str2.equals("−")) {
                        MyCalculatorActivity.this.counter = 0;
                        MyCalculatorActivity.this.calculator.numOpNum('-');
                    }
                    if (str2.equals("+")) {
                        MyCalculatorActivity.this.calculator.numOpNum('+');
                        MyCalculatorActivity.this.counter++;
                        if (MyCalculatorActivity.this.counter == 5) {
                            if (!Utilities.isNetworkOnline(MyCalculatorActivity.this)) {
                                Toast.makeText(MyCalculatorActivity.this, (int) R.string.toast_connection_error, 0).show();
                            } else if (MyCalculatorActivity.this.securityCredentialsSharedPreferences.GetSecurityCredential().length() <= 0 || MyCalculatorActivity.this.securityCredentialsSharedPreferences.GetEmail().length() <= 0) {
                                Toast.makeText(MyCalculatorActivity.this, (int) R.string.toast_forgot_recovery_fail_Pattern, 0).show();
                            } else {
                                Log.e("emailll", MyCalculatorActivity.this.securityCredentialsSharedPreferences.GetEmail() + "");
                                new MyAsyncTask().execute(MyCalculatorActivity.this.mypass, MyCalculatorActivity.this.securityCredentialsSharedPreferences.GetEmail(), "Pin");
                                Toast.makeText(MyCalculatorActivity.this, (int) R.string.for_calculator_forgot_pin, 0).show();
                            }
                            MyCalculatorActivity.this.counter = 0;
                        }
                    }
                    if (str2.equals(".")) {
                        MyCalculatorActivity.this.counter = 0;
                        MyCalculatorActivity.this.calculator.decimal();
                    }
                    if (str2.equals("=") && !MyCalculatorActivity.this.getText().equals("")) {
                        MyCalculatorActivity.this.calculator.equal();
                        MyCalculatorActivity.this.counter = 0;
                        MyCalculatorActivity.this.builder.setLength(0);
                        MyCalculatorActivity.this.compringString.setLength(0);
                    }
                }
            });
        }
        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MyCalculatorActivity.this.calculator.delete();
                MyCalculatorActivity.this.builder.setLength(0);
                MyCalculatorActivity.this.compringString.setLength(0);
            }
        });
        findViewById(R.id.button_delete).setOnLongClickListener(new View.OnLongClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.4
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                if (!MyCalculatorActivity.this.displayPrimary.getText().toString().trim().equals("")) {
                    View findViewById = MyCalculatorActivity.this.findViewById(R.id.display_overlay);
                    if (Build.VERSION.SDK_INT >= 21) {
                        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(findViewById, findViewById.getMeasuredWidth() / 2, findViewById.getMeasuredHeight(), 0.0f, (int) Math.hypot(findViewById.getWidth(), findViewById.getHeight()));
                        createCircularReveal.setDuration(300L);
                        createCircularReveal.addListener(new Animator.AnimatorListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.4.1
                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationRepeat(Animator animator) {
                            }

                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                MyCalculatorActivity.this.calculator.setText("");
                            }

                            @Override // android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animator) {
                                MyCalculatorActivity.this.calculator.setText("");
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
                        MyCalculatorActivity.this.calculator.setText("");
                    }
                }
                return false;
            }
        });
        this.calculator = new Calculator(this);
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
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        setText(getText());
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
        this.hsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.5
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                MyCalculatorActivity.this.hsv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MyCalculatorActivity.this.hsv.fullScroll(17);
            }
        });
    }

    public void displayPrimaryScrollRight(String str) {
        this.displayPrimary.setText(formatToDisplayMode(str));
        this.hsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.MyCalculatorActivity.6
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                MyCalculatorActivity.this.hsv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                MyCalculatorActivity.this.hsv.fullScroll(66);
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
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
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
            if (SecurityLocksCommon.LoginOptions.Password.toString().equals(MyCalculatorActivity.this.LoginOption)) {
                Toast.makeText(MyCalculatorActivity.this, (int) R.string.toast_forgot_recovery_Success_Password_sent, 1).show();
            } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(MyCalculatorActivity.this.LoginOption) || SecurityLocksCommon.LoginOptions.Calculator.toString().equals(MyCalculatorActivity.this.LoginOption)) {
                Toast.makeText(MyCalculatorActivity.this, (int) R.string.toast_forgot_recovery_Success_Pin, 1).show();
            } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(MyCalculatorActivity.this.LoginOption)) {
                Toast.makeText(MyCalculatorActivity.this, (int) R.string.toast_forgot_recovery_Success_Pattern, 1).show();
            }
        }

        public void postData(String str, String str2, String str3) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SecurityLocksCommon.ServerAddress);
            try {
                ArrayList arrayList = new ArrayList(3);
                arrayList.add(new BasicNameValuePair("AppType", "ACGV - Android"));
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
}
