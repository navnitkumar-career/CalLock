package com.example.gallerylock.video;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.gallerylock.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class GalleryVideoAdapter extends ArrayAdapter {
    private final Context con;
    private ArrayList<ImportEnt> importEntList;
    LayoutInflater layoutInflater;
    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_video_empty_icon).showImageForEmptyUri(R.drawable.ic_video_empty_icon).showImageOnFail(R.drawable.ic_video_empty_icon).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    Resources res;

    public GalleryVideoAdapter(Context context, int i, ArrayList<ImportEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.importEntList = arrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    /* JADX WARN: Type inference failed for: r9v0, types: [net.newsoftwares.hidepicturesvideos.video.GalleryVideoAdapter$1] */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View view2;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item, (ViewGroup) null);
            viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
            viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
            viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
            viewHolder.playimageAlbum = (ImageView) view2.findViewById(R.id.playthumbImage);
            viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
            final ImportEnt importEnt = this.importEntList.get(i);
            new Thread() { // from class: net.newsoftwares.hidepicturesvideos.video.GalleryVideoAdapter.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        if (importEnt.GetThumbnail() == null) {
                            importEnt.SetThumbnail(MediaStore.Video.Thumbnails.getThumbnail(((Activity) GalleryVideoAdapter.this.getContext()).getContentResolver(), importEnt.GetId(), 3, null));
                        }
                        viewHolder.imageview.setImageBitmap(importEnt.GetThumbnail());
                    } catch (Exception unused) {
                    }
                }
            }.start();
            viewHolder.playimageAlbum.setBackgroundResource(R.drawable.play_video_btn);
            if (importEnt.GetThumbnailSelection().booleanValue()) {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                viewHolder.iv_tick.setVisibility(0);
            } else {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.iv_tick.setVisibility(4);
            }
            viewHolder.imageview.setBackgroundColor(0);
            viewHolder.ll_custom_gallery.setId(i);
            viewHolder.imageview.setId(i);
            viewHolder.iv_tick.setId(i);
            viewHolder.imageview.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.video.GalleryVideoAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    int intValue = ((Integer) view3.getTag()).intValue();
                    if (((ImportEnt) GalleryVideoAdapter.this.importEntList.get(intValue)).GetThumbnailSelection().booleanValue()) {
                        viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                        ((ImportEnt) GalleryVideoAdapter.this.importEntList.get(intValue)).SetThumbnailSelection(false);
                        viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                        viewHolder.iv_tick.setVisibility(4);
                        return;
                    }
                    viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                    ((ImportEnt) GalleryVideoAdapter.this.importEntList.get(intValue)).SetThumbnailSelection(true);
                    viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                    viewHolder.iv_tick.setVisibility(0);
                }
            });
            view2.setTag(viewHolder);
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageview);
            view2.setTag(R.id.iv_tick, viewHolder.iv_tick);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.imageview.setTag(Integer.valueOf(i));
        viewHolder.iv_tick.setTag(Integer.valueOf(i));
        if (this.importEntList.get(i).GetThumbnail() == null) {
            this.importEntList.get(i).SetThumbnail(MediaStore.Video.Thumbnails.getThumbnail(((Activity) getContext()).getContentResolver(), this.importEntList.get(i).GetId(), 3, null));
            viewHolder.imageview.setImageBitmap(this.importEntList.get(i).GetThumbnail());
        } else {
            viewHolder.imageview.setImageBitmap(this.importEntList.get(i).GetThumbnail());
        }
        if (this.importEntList.get(i).GetThumbnailSelection().booleanValue()) {
            viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
            viewHolder.iv_tick.setVisibility(0);
        } else {
            viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
            viewHolder.iv_tick.setVisibility(4);
        }
        viewHolder.id = i;
        return view2;
    }

    /* loaded from: classes2.dex */
    class ViewHolder {
        int id;
        ImageView imageview;
        ImageView iv_tick;
        RelativeLayout ll_custom_gallery;
        LinearLayout ll_dark_on_click;
        ImageView playimageAlbum;

        ViewHolder() {
        }
    }
}
