package com.example.gallerylock.documents;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Utilities;

import java.util.List;

/* loaded from: classes2.dex */
public class AppDocumentsAdapter extends ArrayAdapter {
    private int _ViewBy;
    private final Context con;
    private boolean isEdit;
    LayoutInflater layoutInflater;
    private List<DocumentsEnt> listItems;
    Resources res;

    public AppDocumentsAdapter(Context context, int i, List<DocumentsEnt> list, boolean z, int i2) {
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
            DocumentsEnt documentsEnt = this.listItems.get(i);
            viewHolder = new ViewHolder();
            if (this._ViewBy == 0) {
                view2 = this.layoutInflater.inflate(R.layout.documents_item_list, (ViewGroup) null);
                viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.textAlbumName);
                viewHolder.rl_thumimage = (RelativeLayout) view2.findViewById(R.id.rl_thumimage);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.textAlbumName.setText(documentsEnt.getDocumentName());
            } else {
                view2 = this.layoutInflater.inflate(R.layout.documents_item_detail, (ViewGroup) null);
                viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.textAlbumName);
                viewHolder.rl_thumimage = (RelativeLayout) view2.findViewById(R.id.rl_thumimage);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                viewHolder.lbl_Date = (TextView) view2.findViewById(R.id.lbl_Date);
                viewHolder.lbl_Time = (TextView) view2.findViewById(R.id.lbl_Time);
                viewHolder.lbl_Size = (TextView) view2.findViewById(R.id.lbl_Size);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
            }
            if (documentsEnt.GetFileCheck()) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            } else {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            }
            if (this.isEdit) {
                viewHolder.rl_thumimage.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.AppDocumentsAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        int intValue = ((Integer) view3.getTag()).intValue();
                        if (((DocumentsEnt) AppDocumentsAdapter.this.listItems.get(intValue)).GetFileCheck()) {
                            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
                            ((DocumentsEnt) AppDocumentsAdapter.this.listItems.get(intValue)).SetFileCheck(false);
                            return;
                        }
                        viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                        ((DocumentsEnt) AppDocumentsAdapter.this.listItems.get(intValue)).SetFileCheck(true);
                    }
                });
            }
            view2.setTag(viewHolder);
            view2.setTag(R.id.thumbImage, viewHolder.imageview);
            view2.setTag(R.id.rl_thumimage, viewHolder.rl_thumimage);
            view = view2;
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.rl_thumimage.setTag(Integer.valueOf(i));
        if (this._ViewBy == 0) {
            viewHolder.textAlbumName.setText(this.listItems.get(i).getDocumentName());
        } else {
            viewHolder.textAlbumName.setText(this.listItems.get(i).getDocumentName());
            String str = this.listItems.get(i).get_modifiedDateTime().split(",")[0];
            String str2 = this.listItems.get(i).get_modifiedDateTime().split(", ")[1];
            viewHolder.lbl_Date.setText(str);
            viewHolder.lbl_Time.setText(str2);
            viewHolder.lbl_Size.setText(Utilities.FileSize(this.listItems.get(i).getFolderLockDocumentLocation()));
        }
        if (this.listItems.get(i).GetFileCheck()) {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
        } else {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        }
        viewHolder.id = i;
        return view;
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
