package com.example.gallerylock.share;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.photo.PhotoAlbum;
import com.example.gallerylock.video.VideoAlbum;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class ShareFromGalleryAdapter extends ArrayAdapter {
    boolean Isvideo;
    int ListPosition;
    private Context con;
    LayoutInflater layoutInflater;
    RelativeLayout ll_thumimage;
    ArrayList<PhotoAlbum> photoAlbums;
    Resources res;
    ArrayList<VideoAlbum> videoAlbums;

    public ShareFromGalleryAdapter(Context context, int i, ArrayList<PhotoAlbum> arrayList, int i2) {
        super(context, i, arrayList);
        this.Isvideo = false;
        this.con = context;
        this.photoAlbums = arrayList;
        this.ListPosition = i2;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public ShareFromGalleryAdapter(Context context, int i, ArrayList<VideoAlbum> arrayList, int i2, boolean z) {
        super(context, i, arrayList);
        this.Isvideo = false;
        this.con = context;
        this.videoAlbums = arrayList;
        this.ListPosition = i2;
        this.Isvideo = z;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.share_from_gallery_item, (ViewGroup) null);
            this.ll_thumimage = (RelativeLayout) view.findViewById(R.id.ll_thumimage);
            viewHolder = new ViewHolder();
            viewHolder.ll_selection = (LinearLayout) view.findViewById(R.id.ll_selection);
            viewHolder.lblfiletitle = (TextView) view.findViewById(R.id.textAlbumName);
            viewHolder.imageAlbum = (ImageView) view.findViewById(R.id.thumbImage);
            viewHolder.ll_PhotoAlbums = (LinearLayout) view.findViewById(R.id.ll_PhotoAlbums);
            if (!this.Isvideo) {
                viewHolder.lblfiletitle.setText(this.photoAlbums.get(i).getAlbumName());
                viewHolder.imageAlbum.setBackgroundResource(R.drawable.ic_photo_empty_icon);
            } else {
                viewHolder.lblfiletitle.setText(this.videoAlbums.get(i).getAlbumName());
                viewHolder.imageAlbum.setBackgroundResource(R.drawable.ic_video_thumb_empty_icon);
            }
            viewHolder.lblfiletitle.setSelected(true);
            viewHolder.lblfiletitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            viewHolder.lblfiletitle.setSingleLine(true);
            if (this.ListPosition == i) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            } else {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            }
            view.setTag(viewHolder);
            view.setTag(R.id.textAlbumName, viewHolder.lblfiletitle);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (!this.Isvideo) {
            viewHolder.lblfiletitle.setText(this.photoAlbums.get(i).getAlbumName());
        } else {
            viewHolder.lblfiletitle.setText(this.videoAlbums.get(i).getAlbumName());
        }
        viewHolder.lblfiletitle.setSelected(true);
        viewHolder.lblfiletitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        viewHolder.lblfiletitle.setSingleLine(true);
        return view;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        public ImageView imageAlbum;
        public TextView lbl_Count;
        public TextView lblfiletitle;
        public LinearLayout ll_PhotoAlbums;
        public LinearLayout ll_selection;

        public ViewHolder() {
        }
    }
}
