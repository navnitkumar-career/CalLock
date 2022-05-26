package com.example.gallerylock.documents;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gallerylock.R;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class DocumentSystemFileAdapter extends ArrayAdapter {
    boolean _isAllCheck = false;
    private Context con;
    ArrayList<DocumentsEnt> documentEntlist;
    LayoutInflater layoutInflater;
    Resources res;

    public DocumentSystemFileAdapter(Context context, int i, ArrayList<DocumentsEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.documentEntlist = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.documents_item_list, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.lbldocumenttitle = (TextView) view.findViewById(R.id.textAlbumName);
            viewHolder.rl_thumimage = (RelativeLayout) view.findViewById(R.id.rl_thumimage);
            viewHolder.ll_selection = (LinearLayout) view.findViewById(R.id.ll_selection);
            DocumentsEnt documentsEnt = this.documentEntlist.get(i);
            viewHolder.lbldocumenttitle.setText(documentsEnt.getDocumentName());
            if (documentsEnt.GetFileCheck()) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            } else {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            }
            viewHolder.rl_thumimage.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.documents.DocumentSystemFileAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    int intValue = ((Integer) view2.getTag()).intValue();
                    if (DocumentSystemFileAdapter.this.documentEntlist.get(intValue).GetFileCheck()) {
                        viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
                        DocumentSystemFileAdapter.this.documentEntlist.get(intValue).SetFileCheck(false);
                        return;
                    }
                    viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                    DocumentSystemFileAdapter.this.documentEntlist.get(intValue).SetFileCheck(true);
                }
            });
            view.setTag(viewHolder);
            view.setTag(R.id.textAlbumName, viewHolder.lbldocumenttitle);
            view.setTag(R.id.rl_thumimage, viewHolder.rl_thumimage);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.rl_thumimage.setTag(Integer.valueOf(i));
        viewHolder.lbldocumenttitle.setText(this.documentEntlist.get(i).getDocumentName());
        if (this.documentEntlist.get(i).GetFileCheck()) {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
        } else {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        }
        viewHolder.id = i;
        return view;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        int id;
        TextView lbldocumenttitle;
        LinearLayout ll_selection;
        RelativeLayout rl_thumimage;

        public ViewHolder() {
        }
    }
}
