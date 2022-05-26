package com.example.gallerylock.documents;

import android.content.Context;
import android.content.res.Resources;
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

import java.io.File;
import java.util.List;

/* loaded from: classes2.dex */
public class FoldersImportAdapter extends ArrayAdapter {
    boolean IsAlbumSelect;
    Context con;
    List<ImportAlbumEnt> importAlbumEnts;
    LayoutInflater layoutInflater;
    Resources res;

    public FoldersImportAdapter(Context context, int i, List<ImportAlbumEnt> list, boolean z) {
        super(context, i, list);
        this.IsAlbumSelect = false;
        this.res = context.getResources();
        this.importAlbumEnts = list;
        this.con = context;
        this.IsAlbumSelect = z;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.import_folder_list_item_activity, (ViewGroup) null);
            viewHolder.checkbox = (CheckBox) view2.findViewById(R.id.cb_import_album_item);
            viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.lbl_import_album_item);
            viewHolder.imageAlbum = (ImageView) view2.findViewById(R.id.thumbnil_import_album_titem);
            viewHolder.imageAlbum.setBackgroundColor(0);
            viewHolder.imageAlbum.setBackgroundResource(R.drawable.ic_document_list_empty_icon);
            viewHolder.checkbox.setId(i);
            viewHolder.imageAlbum.setId(i);
            if (!this.IsAlbumSelect) {
                for (int i2 = 0; i2 < this.importAlbumEnts.size(); i2++) {
                    this.importAlbumEnts.get(i2).SetAlbumFileCheck(false);
                }
            }
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.FoldersImportAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    FoldersImportAdapter.this.importAlbumEnts.get(((Integer) compoundButton.getTag()).intValue()).SetAlbumFileCheck(compoundButton.isChecked());
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
        return view2;
    }

    /* loaded from: classes2.dex */
    class ViewHolder {
        CheckBox checkbox;
        ImageView imageAlbum;
        TextView lbl_import_album_photo_count_item;
        TextView textAlbumName;

        ViewHolder() {
        }
    }
}
