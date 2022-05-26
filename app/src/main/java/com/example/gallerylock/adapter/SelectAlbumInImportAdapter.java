package com.example.gallerylock.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.photo.PhotoAlbum;
import com.example.gallerylock.video.VideoAlbum;

import java.util.List;

/* loaded from: classes2.dex */
public class SelectAlbumInImportAdapter extends ArrayAdapter {
    boolean Isvideo;
    private Context con;
    LayoutInflater layoutInflater;
    RelativeLayout ll_thumimage;
    List<PhotoAlbum> photoAlbums;
    Resources res;
    List<VideoAlbum> videoAlbums;

    public SelectAlbumInImportAdapter(Context context, int i, List<PhotoAlbum> list) {
        super(context, i, list);
        this.Isvideo = false;
        this.con = context;
        this.photoAlbums = list;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public SelectAlbumInImportAdapter(Context context, int i, List<VideoAlbum> list, boolean z) {
        super(context, i, list);
        this.Isvideo = false;
        this.con = context;
        this.videoAlbums = list;
        this.Isvideo = z;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.move_customlistview_item, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.lblmoveitem);
        if (!this.Isvideo) {
            textView.setText(this.photoAlbums.get(i).getAlbumName());
        } else {
            textView.setText(this.videoAlbums.get(i).getAlbumName());
        }
        return inflate;
    }
}
