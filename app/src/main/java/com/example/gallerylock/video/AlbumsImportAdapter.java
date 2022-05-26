package com.example.gallerylock.video;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class AlbumsImportAdapter extends ArrayAdapter {
    boolean IsAlbumSelect;
    boolean IsVideo;
    Context con;
    ArrayList<ImportAlbumEnt> importAlbumEnts;
    LayoutInflater layoutInflater;
    Resources res;

    public AlbumsImportAdapter(Context context, int i, ArrayList<ImportAlbumEnt> arrayList, boolean z, boolean z2) {
        super(context, i, arrayList);
        this.IsAlbumSelect = false;
        this.IsVideo = false;
        this.res = context.getResources();
        this.importAlbumEnts = arrayList;
        this.con = context;
        this.IsAlbumSelect = z;
        this.IsVideo = z2;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view2;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.import_album_list_item_activity, (ViewGroup) null);
            viewHolder.checkbox = (CheckBox) view2.findViewById(R.id.cb_import_album_item);
            viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.lbl_import_album_item);
            viewHolder.imageAlbum = (ImageView) view2.findViewById(R.id.thumbnil_import_album_titem);
            viewHolder.playimageAlbum = (ImageView) view2.findViewById(R.id.playimageAlbum);
            viewHolder.imageAlbum.setBackgroundColor(0);
            viewHolder.checkbox.setId(i);
            viewHolder.imageAlbum.setId(i);
            if (!this.IsAlbumSelect) {
                for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                    this.importAlbumEnts.get(i2).SetAlbumFileCheck(false);
                }
            }
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.video.AlbumsImportAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AlbumsImportAdapter.this.importAlbumEnts.get(((Integer) compoundButton.getTag()).intValue()).SetAlbumFileCheck(compoundButton.isChecked());
                }
            });
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageAlbum);
            view2.setTag(R.id.cb_import_album_item, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.imageAlbum.setTag(Integer.valueOf(i));
        viewHolder.checkbox.setTag(Integer.valueOf(i));
        viewHolder.textAlbumName.setText(new File(this.importAlbumEnts.get(i).GetAlbumName()).getName());
        viewHolder.textAlbumName.setSelected(true);
        viewHolder.textAlbumName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        viewHolder.textAlbumName.setSingleLine(true);
        if (this.IsVideo) {
            viewHolder.imageAlbum.setImageBitmap(MediaStore.Video.Thumbnails.getThumbnail(((Activity) getContext()).getContentResolver(), this.importAlbumEnts.get(i).GetId(), 3, null));
            viewHolder.playimageAlbum.setVisibility(0);
            viewHolder.playimageAlbum.setImageResource(R.drawable.play_video_btn);
        } else {
            try {
                viewHolder.playimageAlbum.setVisibility(4);
                viewHolder.imageAlbum.setScaleType(ImageView.ScaleType.FIT_XY);
                viewHolder.imageAlbum.setImageBitmap(Utilities.DecodeFile(new File(this.importAlbumEnts.get(i).GetPath())));
            } catch (Exception unused) {
            }
        }
        return view2;
    }

    /* loaded from: classes2.dex */
    class ViewHolder {
        CheckBox checkbox;
        ImageView imageAlbum;
        TextView lbl_import_album_photo_count_item;
        ImageView playimageAlbum;
        TextView textAlbumName;

        ViewHolder() {
        }
    }
}
