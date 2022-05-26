package com.example.gallerylock.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;

import java.util.List;

/* loaded from: classes2.dex */
public class MoveNoteAdapter extends BaseAdapter {
    Context con;
    LayoutInflater layoutInflater;
    List<NotesFolderDB_Pojo> noteFolderlist;

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public MoveNoteAdapter(Context context, List<NotesFolderDB_Pojo> list) {
        this.noteFolderlist = list;
        this.con = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.move_customlistview_item, viewGroup, false);
            viewHolder.lblmoveitem = (TextView) view.findViewById(R.id.lblmoveitem);
            viewHolder.imgurlitem = (ImageView) view.findViewById(R.id.imgurlitem);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.lblmoveitem.setText(this.noteFolderlist.get(i).getNotesFolderName());
        viewHolder.imgurlitem.setVisibility(8);
        return view;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        ImageView imgurlitem;
        TextView lblmoveitem;

        public ViewHolder() {
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.noteFolderlist.size();
    }
}
