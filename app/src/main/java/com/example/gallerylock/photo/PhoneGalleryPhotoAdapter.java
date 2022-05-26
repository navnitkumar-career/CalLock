package com.example.gallerylock.photo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

/* loaded from: classes2.dex */
public class PhoneGalleryPhotoAdapter extends ArrayAdapter {
    private int _ViewBy;
    private final Context con;
    private boolean isEdit;
    LayoutInflater layoutInflater;
    private List<Photo> listItems;
    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_photo_empty_icon).showImageForEmptyUri(R.drawable.ic_photo_empty_icon).showImageOnFail(R.drawable.ic_photo_empty_icon).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    Resources res;

    public PhoneGalleryPhotoAdapter(Context context, int i, List<Photo> list, boolean z, int i2) {
        super(context, i, list);
        this._ViewBy = 0;
        this.con = context;
        this.listItems = list;
        this.isEdit = z;
        this._ViewBy = i2;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View view2;
        if (view == null) {
            Photo photo = this.listItems.get(i);
            viewHolder = new ViewHolder();
            int i2 = this._ViewBy;
            if (i2 == 0) {
                view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item, (ViewGroup) null);
                viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
                viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
                viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.iv_tick.setId(i);
            } else if (i2 == 1) {
                view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item_tiles, (ViewGroup) null);
                viewHolder.ll_custom_gallery = (RelativeLayout) view2.findViewById(R.id.ll_custom_gallery);
                viewHolder.ll_dark_on_click = (LinearLayout) view2.findViewById(R.id.ll_dark_on_click);
                viewHolder.iv_tick = (ImageView) view2.findViewById(R.id.iv_tick);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.iv_tick.setId(i);
            } else if (i2 == 2) {
                view2 = this.layoutInflater.inflate(R.layout.custom_gallery_item_list, (ViewGroup) null);
                viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.textAlbumName);
                viewHolder.rl_thumimage = (RelativeLayout) view2.findViewById(R.id.rl_thumimage);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                viewHolder.lbl_Date = (TextView) view2.findViewById(R.id.lbl_Date);
                viewHolder.lbl_Time = (TextView) view2.findViewById(R.id.lbl_Time);
                viewHolder.lbl_Size = (TextView) view2.findViewById(R.id.lbl_Size);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
            } else {
                view2 = view;
            }
            viewHolder.imageview.setId(i);
            if (photo.GetFileCheck()) {
                if (this._ViewBy == 2) {
                    viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                } else {
                    viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                    viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                    viewHolder.iv_tick.setVisibility(0);
                }
            } else if (this._ViewBy == 2) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            } else {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                viewHolder.iv_tick.setVisibility(4);
            }
            if (this.isEdit) {
                viewHolder.imageview.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhoneGalleryPhotoAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        if (PhoneGalleryPhotoAdapter.this._ViewBy != 2) {
                            int intValue = ((Integer) view3.getTag()).intValue();
                            if (((Photo) PhoneGalleryPhotoAdapter.this.listItems.get(intValue)).GetFileCheck()) {
                                viewHolder.ll_custom_gallery.setBackgroundResource(R.color.fulltransparent_color);
                                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.fulltransparent_color);
                                ((Photo) PhoneGalleryPhotoAdapter.this.listItems.get(intValue)).SetFileCheck(false);
                                viewHolder.iv_tick.setVisibility(4);
                                return;
                            }
                            viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                            viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                            ((Photo) PhoneGalleryPhotoAdapter.this.listItems.get(intValue)).SetFileCheck(true);
                            viewHolder.iv_tick.setVisibility(0);
                        }
                    }
                });
                if (this._ViewBy == 2) {
                    viewHolder.rl_thumimage.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.PhoneGalleryPhotoAdapter.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view3) {
                            int intValue = ((Integer) view3.getTag()).intValue();
                            if (((Photo) PhoneGalleryPhotoAdapter.this.listItems.get(intValue)).GetFileCheck()) {
                                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
                                ((Photo) PhoneGalleryPhotoAdapter.this.listItems.get(intValue)).SetFileCheck(false);
                                return;
                            }
                            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                            ((Photo) PhoneGalleryPhotoAdapter.this.listItems.get(intValue)).SetFileCheck(true);
                        }
                    });
                }
            }
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageview);
            if (this._ViewBy != 2) {
                view2.setTag(R.id.iv_tick, viewHolder.iv_tick);
            } else {
                view2.setTag(R.id.rl_thumimage, viewHolder.rl_thumimage);
            }
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.imageview.setTag(Integer.valueOf(i));
        if (this._ViewBy != 2) {
            viewHolder.iv_tick.setTag(Integer.valueOf(i));
        } else {
            viewHolder.rl_thumimage.setTag(Integer.valueOf(i));
        }
        if (this._ViewBy == 2) {
            viewHolder.textAlbumName.setText(this.listItems.get(i).getPhotoName());
            String str = this.listItems.get(i).get_modifiedDateTime().split(",")[0];
            String str2 = this.listItems.get(i).get_modifiedDateTime().split(", ")[1];
            viewHolder.lbl_Date.setText(str);
            viewHolder.lbl_Time.setText(str2);
            viewHolder.lbl_Size.setText(Utilities.FileSize(this.listItems.get(i).getFolderLockPhotoLocation()));
        }
        try {
            /*ImageLoader imageLoader = Common.imageLoader;
            imageLoader.displayImage("file:///" + this.listItems.get(i).getFolderLockPhotoLocation().toString(), viewHolder.imageview, this.options);*/
            Uri photoUri = Uri.fromFile( new File(this.listItems.get(i).getFolderLockPhotoLocation()));
            Glide.with(con).load(photoUri).into(viewHolder.imageview);
            Log.e("TAG", "getView: " +  this.listItems.get(i).getFolderLockPhotoLocation().toString());
        } catch (Exception unused) {
            Log.e("TAG", "getView: " + unused.getMessage());
        }
        if (this.listItems.get(i).GetFileCheck()) {
            if (this._ViewBy == 2) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            } else {
                viewHolder.ll_custom_gallery.setBackgroundResource(R.drawable.photo_grid_item_click);
                viewHolder.ll_dark_on_click.setBackgroundResource(R.color.transparent_black_color);
                viewHolder.iv_tick.setVisibility(0);
            }
        } else if (this._ViewBy == 2) {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
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
        TextView lbl_Date;
        TextView lbl_Size;
        TextView lbl_Time;
        RelativeLayout ll_custom_gallery;
        LinearLayout ll_dark_on_click;
        LinearLayout ll_selection;
        RelativeLayout rl_thumimage;
        TextView textAlbumName;

        ViewHolder() {
        }
    }
}
