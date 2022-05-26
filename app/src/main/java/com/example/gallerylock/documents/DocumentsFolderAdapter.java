package com.example.gallerylock.documents;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class DocumentsFolderAdapter extends ArrayAdapter {
    boolean _isEdit;
    boolean _isGridView;
    Context con;
    int focusedPosition;
    LayoutInflater layoutInflater;
    ArrayList<DocumentFolder> list;
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    Resources res;

    public DocumentsFolderAdapter(Context context, int i, ArrayList<DocumentFolder> arrayList, int i2, boolean z, boolean z2) {
        super(context, i, arrayList);
        this._isEdit = false;
        this._isGridView = false;
        this.res = context.getResources();
        this.list = arrayList;
        this.con = context;
        this.focusedPosition = i2;
        this._isEdit = z;
        this._isGridView = z2;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        if (this._isGridView) {
            view2 = this.layoutInflater.inflate(R.layout.albums_gallery_item_grid, (ViewGroup) null);
        } else {
            view2 = this.layoutInflater.inflate(R.layout.albums_gallery_item_list, (ViewGroup) null);
        }
        TextView textView = (TextView) view2.findViewById(R.id.textAlbumName);
        TextView textView2 = (TextView) view2.findViewById(R.id.lbl_Count);
        ImageView imageView = (ImageView) view2.findViewById(R.id.thumbImage);
        ImageView imageView2 = (ImageView) view2.findViewById(R.id.iv_album_thumbnil);
        LinearLayout linearLayout = (LinearLayout) view2.findViewById(R.id.ll_selection);
        TextView textView3 = (TextView) view2.findViewById(R.id.lbl_Date);
        DocumentFolder documentFolder = this.list.get(i);
        String str = documentFolder.get_modifiedDateTime().split(",")[0];
        String str2 = documentFolder.get_modifiedDateTime().split(", ")[1];
        textView3.setSelected(true);
        textView3.setText("Date: " + str);
        ((TextView) view2.findViewById(R.id.lbl_Time)).setText("Time: " + str2);
        textView.setText(documentFolder.getFolderName());
        textView.setSelected(true);
        if (this.focusedPosition != i || !this._isEdit) {
            linearLayout.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        } else {
            linearLayout.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
        }
        int i2 = documentFolder.get_fileCount();
        textView2.setText(Integer.toString(i2));
        if (this._isGridView) {
            if (i2 > 0) {
                imageView.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
            } else {
                imageView.setBackgroundResource(R.drawable.ic_documentsfolder_empty_thumb_icon);
            }
        } else if (i2 > 0) {
            imageView.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
        } else {
            imageView.setBackgroundResource(R.drawable.ic_documentsfolder_empty_thumb_icon);
        }
        return view2;
    }
}
