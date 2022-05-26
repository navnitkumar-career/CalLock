package com.example.gallerylock.audio;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Common;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class AudioSystemFileAdapter extends ArrayAdapter {
    boolean _isAllCheck = false;
    ArrayList<AudioEnt> audioEntlist;
    private AudiosImportActivity audioImportActivity;
    private Context con;
    LayoutInflater layoutInflater;
    Resources res;

    private Bitmap ShrinkBitmap(Bitmap bitmap, int i, int i2) {
        return null;
    }

    public AudioSystemFileAdapter(AudiosImportActivity audiosImportActivity, int i, ArrayList<AudioEnt> arrayList) {
        super(audiosImportActivity, i, arrayList);
        this.audioImportActivity = audiosImportActivity;
        this.con = audiosImportActivity;
        this.audioEntlist = arrayList;
        this.res = audiosImportActivity.getResources();
        this.layoutInflater = (LayoutInflater) audiosImportActivity.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.activity_addmiscellaneous_items, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.lblaudioname = (TextView) view.findViewById(R.id.lbladdmiscellaneoustitleitem);
            viewHolder.cbaddaudioitem = (CheckBox) view.findViewById(R.id.cbaddmiscellaneousitem);
            AppCompatImageView appCompatImageView = (AppCompatImageView) view.findViewById(R.id.imageaddmiscellaneousitem);
            AudioEnt audioEnt = this.audioEntlist.get(i);
            viewHolder.lblaudioname.setText(audioEnt.getAudioName());
            if (audioEnt.GetFileImage() != null) {
                appCompatImageView.setImageBitmap(audioEnt.GetFileImage());
            }
            if (this._isAllCheck) {
                viewHolder.cbaddaudioitem.setChecked(true);
            }
            if (Common.IsSelectAll) {
                this.audioImportActivity.SelectedItemsCount(Common.SelectedCount);
            }
            viewHolder.cbaddaudioitem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioSystemFileAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AudioSystemFileAdapter.this.audioEntlist.get(((Integer) compoundButton.getTag()).intValue()).SetFileCheck(compoundButton.isChecked());
                    if (Common.IsSelectAll) {
                        return;
                    }
                    if (compoundButton.isChecked()) {
                        Common.SelectedCount++;
                        AudioSystemFileAdapter.this.audioImportActivity.SelectedItemsCount(Common.SelectedCount);
                        return;
                    }
                    Common.SelectedCount--;
                    AudioSystemFileAdapter.this.audioImportActivity.SelectedItemsCount(Common.SelectedCount);
                    Common.IsSelectAll = false;
                }
            });
            view.setTag(viewHolder);
            view.setTag(R.id.lbladdmiscellaneoustitleitem, viewHolder.lblaudioname);
            view.setTag(R.id.cbaddmiscellaneousitem, viewHolder.cbaddaudioitem);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.cbaddaudioitem.setTag(Integer.valueOf(i));
        viewHolder.lblaudioname.setText(this.audioEntlist.get(i).getAudioName());
        viewHolder.cbaddaudioitem.setChecked(this.audioEntlist.get(i).GetFileCheck());
        return view;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        public CheckBox cbaddaudioitem;
        public TextView lblaudioname;

        public ViewHolder() {
        }
    }
}
