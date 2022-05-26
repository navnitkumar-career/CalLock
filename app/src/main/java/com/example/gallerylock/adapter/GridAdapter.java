package com.example.gallerylock.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.features.FeatureActivityEnt;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class GridAdapter extends ArrayAdapter {
    boolean IsStealthModeOn = false;
    private final Context con;
    Dialog dialog;
    private final ArrayList<FeatureActivityEnt> featureEntList;
    LayoutInflater layoutInflater;
    Resources res;

    public GridAdapter(Context context, int i, ArrayList<FeatureActivityEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.featureEntList = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.grid_layout, (ViewGroup) null);
        FeatureActivityEnt featureActivityEnt = this.featureEntList.get(i);
        ((TextView) inflate.findViewById(R.id.tv_text)).setText(featureActivityEnt.get_featureName());
        ((ImageView) inflate.findViewById(R.id.img_grid)).setBackgroundResource(featureActivityEnt.get_feature_Icon());
        return inflate;
    }
}
