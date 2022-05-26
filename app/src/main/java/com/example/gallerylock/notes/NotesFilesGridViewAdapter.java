package com.example.gallerylock.notes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class NotesFilesGridViewAdapter extends BaseAdapter {
    List<Integer> focusedPosition;
    LayoutInflater inflater;
    boolean isEdit;
    private Context mContext;
    List<NotesFileDB_Pojo> notesFileDB_PojoList;
    int notesfilesCount;
    int viewBy;

    /* loaded from: classes2.dex */
    public enum ViewBy {
        Detail,
        List,
        Tile
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public NotesFilesGridViewAdapter(Context context) {
        this.notesfilesCount = 0;
        this.viewBy = 0;
        this.isEdit = false;
        this.mContext = context;
    }

    public NotesFilesGridViewAdapter(Context context, List<NotesFileDB_Pojo> list) {
        this.notesfilesCount = 0;
        this.viewBy = 0;
        this.isEdit = false;
        this.mContext = context;
        this.notesFileDB_PojoList = list;
        this.notesfilesCount = list.size();
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.focusedPosition = new ArrayList();
    }

    public void setFocusedPosition(int i) {
        if (!this.focusedPosition.contains(Integer.valueOf(i))) {
            this.focusedPosition.add(Integer.valueOf(i));
        }
    }

    public void removeFocusedPosition(int i) {
        if (this.focusedPosition.contains(Integer.valueOf(i))) {
            this.focusedPosition.remove(this.focusedPosition.indexOf(Integer.valueOf(i)));
        }
    }

    public void removeAllFocusedPosition() {
        this.focusedPosition.clear();
    }

    public void setIsEdit(boolean z) {
        this.isEdit = z;
    }

    public void setViewBy(int i) {
        this.viewBy = i;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.notesFileDB_PojoList.size();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (this.notesfilesCount > 0) {
            String str = this.notesFileDB_PojoList.get(i).getNotesFileModifiedDate().split(",")[0];
            String str2 = this.notesFileDB_PojoList.get(i).getNotesFileModifiedDate().split(", ")[1];
            Holder holder = new Holder();
            if (view == null) {
                if (this.viewBy == ViewBy.List.ordinal()) {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_listview, viewGroup, false);
                } else if (this.viewBy == ViewBy.Tile.ordinal()) {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_listview, viewGroup, false);
                } else {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_detailview, viewGroup, false);
                }
                holder.tv_FileName = (TextView) view.findViewById(R.id.tv_FolderName);
                holder.tv_NoteFileTime = (TextView) view.findViewById(R.id.tv_NoteFolderTime);
                holder.tv_NoteFileDate = (TextView) view.findViewById(R.id.tv_NoteFolderDate);
                holder.tv_noteFolder_size = (TextView) view.findViewById(R.id.tv_noteFolder_size);
                holder.iv_notesFile = (ImageView) view.findViewById(R.id.iv_notesFolder);
                holder.ll_selection = (LinearLayout) view.findViewById(R.id.ll_selection);
                holder.rl_noteFolder_count = (RelativeLayout) view.findViewById(R.id.rl_noteFolder_count);
                holder.colorBorder = (ImageView) view.findViewById(R.id.colorBorder);
                holder.tv_NoteContent = (TextView) view.findViewById(R.id.tv_NoteContent);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            double notesFileSize = this.notesFileDB_PojoList.get(i).getNotesFileSize();
            holder.tv_FileName.setText(this.notesFileDB_PojoList.get(i).getNotesFileName());
            TextView textView = holder.tv_NoteFileDate;
            textView.setText("Date: " + str);
            TextView textView2 = holder.tv_NoteFileTime;
            textView2.setText("Time: " + str2);
            holder.tv_NoteFileDate.setSelected(true);
            holder.tv_NoteFileTime.setSelected(true);
            holder.rl_noteFolder_count.setVisibility(4);
            TextView textView3 = holder.tv_noteFolder_size;
            textView3.setText("Size: " + readableFileSize(notesFileSize));
            try {
                holder.tv_NoteContent.setText(this.notesFileDB_PojoList.get(i).getNotesFileText().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.viewBy == ViewBy.Tile.ordinal()) {
                holder.iv_notesFile.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
            } else {
                holder.iv_notesFile.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
            }
            try {
                holder.colorBorder.setBackgroundColor(Color.parseColor(this.notesFileDB_PojoList.get(i).getNotesfileColor()));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            if (!this.focusedPosition.contains(Integer.valueOf(i)) || !this.isEdit) {
                holder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            } else {
                holder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            }
        } else {
            view.setVisibility(8);
        }
        if (!this.isEdit) {
            view.startAnimation(AnimationUtils.loadAnimation(this.mContext, R.anim.anim_fade_in));
        }
        return view;
    }

    public String readableFileSize(double d) {
        if (d <= 0.0d) {
            return "0";
        }
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        return new DecimalFormat("#,##0.#").format(d / Math.pow(1024.0d, log10)) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + new String[]{"B", "kB", "MB", "GB", "TB"}[log10];
    }

    /* loaded from: classes2.dex */
    public class Holder {
        ImageView colorBorder;
        ImageView iv_notesFile;
        LinearLayout ll_selection;
        RelativeLayout rl_noteFolder_count;
        TextView tv_FileName;
        TextView tv_NoteContent;
        TextView tv_NoteFileDate;
        TextView tv_NoteFileTime;
        TextView tv_noteFolder_size;

        public Holder() {
        }
    }
}
