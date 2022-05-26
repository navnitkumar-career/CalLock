package com.example.gallerylock.dropbox;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class CloudMenuAdapter extends ArrayAdapter {
    boolean IsStealthModeOn = false;
    private final ArrayList<CloudMenuEnt> cloudEntList;
    private final Context con;
    Dialog dialog;
    LayoutInflater layoutInflater;
    SharedPreferences myPrefs;
    final SharedPreferences.Editor prefsEditor;
    Resources res;

    public CloudMenuAdapter(Context context, int i, ArrayList<CloudMenuEnt> arrayList) {
        super(context, i, arrayList);
        SharedPreferences sharedPreferences = context.getSharedPreferences("Login", 0);
        this.myPrefs = sharedPreferences;
        this.prefsEditor = sharedPreferences.edit();
        this.con = context;
        this.cloudEntList = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.cloud_menu_activity_item, (ViewGroup) null);
        CloudMenuEnt cloudMenuEnt = this.cloudEntList.get(i);
        ((TextView) inflate.findViewById(R.id.lblcloudheadingitem)).setText(cloudMenuEnt.GetCloudHeading());
        ((ImageView) inflate.findViewById(R.id.imgclouditem)).setImageResource(cloudMenuEnt.GetDrawable());
        return inflate;
    }
}
