package com.example.gallerylock.features;

import android.content.Context;

import com.example.gallerylock.R;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class FeatureActivityMethods {
    public ArrayList<FeatureActivityEnt> GetFeatures(Context context) {
        ArrayList<FeatureActivityEnt> arrayList = new ArrayList<>();
        FeatureActivityEnt featureActivityEnt = new FeatureActivityEnt();
        featureActivityEnt.set_featureName(context.getResources().getString(R.string.lblFeature1));
        featureActivityEnt.set_feature_Icon(R.drawable.ic_photo_empty_icon);
        arrayList.add(featureActivityEnt);
        FeatureActivityEnt featureActivityEnt2 = new FeatureActivityEnt();
        featureActivityEnt2.set_featureName(context.getResources().getString(R.string.lblFeature2));
        featureActivityEnt2.set_feature_Icon(R.drawable.ic_menu_video_icon);
        arrayList.add(featureActivityEnt2);
        FeatureActivityEnt featureActivityEnt3 = new FeatureActivityEnt();
        featureActivityEnt3.set_featureName(context.getResources().getString(R.string.lblFeature9));
        featureActivityEnt3.set_feature_Icon(R.drawable.ic_menu_audio_icon);
        arrayList.add(featureActivityEnt3);
        FeatureActivityEnt featureActivityEnt5 = new FeatureActivityEnt();
        featureActivityEnt5.set_featureName(context.getResources().getString(R.string.lblFeature4));
        featureActivityEnt5.set_feature_Icon(R.drawable.ic_documents_thumb_icon);
        arrayList.add(featureActivityEnt5);
        return arrayList;
    }
}
