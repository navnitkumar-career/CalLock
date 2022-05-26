package com.example.gallerylock.calculator;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;

import com.example.gallerylock.R;

/* loaded from: classes2.dex */
public class CalculatorSetting extends AppCompatActivity {
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        char c = 65535;
        if (defaultSharedPreferences.getBoolean("pref_dark", false)) {
            String string = defaultSharedPreferences.getString("pref_theme", "0");
            switch (string.hashCode()) {
                case 48:
                    if (string.equals("0")) {
                        c = 0;
                        break;
                    }
                    break;
                case 49:
                    if (string.equals("1")) {
                        c = 1;
                        break;
                    }
                    break;
                case 50:
                    if (string.equals("2")) {
                        c = 2;
                        break;
                    }
                    break;
                case 51:
                    if (string.equals("3")) {
                        c = 3;
                        break;
                    }
                    break;
                case 52:
                    if (string.equals("4")) {
                        c = 4;
                        break;
                    }
                    break;
                case 53:
                    if (string.equals("5")) {
                        c = 5;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                setTheme(R.style.AppTheme_Dark_Blue);
            } else if (c == 1) {
                setTheme(R.style.AppTheme_Dark_Cyan);
            } else if (c == 2) {
                setTheme(R.style.AppTheme_Dark_Gray);
            } else if (c == 3) {
                setTheme(R.style.AppTheme_Dark_Green);
            } else if (c == 4) {
                setTheme(R.style.AppTheme_Dark_Purple);
            } else if (c == 5) {
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
                    break;
                case 49:
                    if (string2.equals("1")) {
                        c = 1;
                        break;
                    }
                    break;
                case 50:
                    if (string2.equals("2")) {
                        c = 2;
                        break;
                    }
                    break;
                case 51:
                    if (string2.equals("3")) {
                        c = 3;
                        break;
                    }
                    break;
                case 52:
                    if (string2.equals("4")) {
                        c = 4;
                        break;
                    }
                    break;
                case 53:
                    if (string2.equals("5")) {
                        c = 5;
                        break;
                    }
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
        setContentView(R.layout.activity_calculator_setting);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment()).commit();
    }

    /* loaded from: classes2.dex */
    public static class SettingsFragment extends PreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.preferences);
            findPreference("pref_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorSetting.SettingsFragment.1
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) SettingsFragment.this.findPreference("pref_theme");
                    String packageName = SettingsFragment.this.getActivity().getPackageName();
                    String str = packageName + ".MainActivity-" + ((Object) listPreference.getEntry());
                    SettingsFragment.this.getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(packageName, str), 2, 1);
                    SettingsFragment.this.getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(packageName, packageName + ".MainActivity-" + ((Object) listPreference.getEntries()[Integer.parseInt(obj.toString())])), 1, 1);
                    SettingsFragment.this.getActivity().finish();
                    TaskStackBuilder.create(SettingsFragment.this.getActivity()).addNextIntent(new Intent(SettingsFragment.this.getActivity(), MyCalculatorActivity.class)).addNextIntent(SettingsFragment.this.getActivity().getIntent()).startActivities();
                    return true;
                }
            });
            findPreference("pref_dark").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorSetting.SettingsFragment.2
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    SettingsFragment.this.getActivity().finish();
                    TaskStackBuilder.create(SettingsFragment.this.getActivity()).addNextIntent(new Intent(SettingsFragment.this.getActivity(), MyCalculatorActivity.class)).addNextIntent(SettingsFragment.this.getActivity().getIntent()).startActivities();
                    return true;
                }
            });
            findPreference("pref_rate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: net.newsoftwares.hidepicturesvideos.calculator.CalculatorSetting.SettingsFragment.3
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    RateDialog.show(SettingsFragment.this.getActivity());
                    return false;
                }
            });
        }
    }
}
