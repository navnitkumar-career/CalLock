package com.example.gallerylock.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import java.text.DecimalFormat;
import java.util.List;

/* loaded from: classes2.dex */
public class NotesFolderGridViewAdapter extends BaseAdapter {
    Constants constants;
    int focusedPosition;
    double[] folderSize;
    int foldersCount;
    LayoutInflater inflater;
    boolean isEdit;
    boolean isGridView;
    private Context mContext;
    int[] notesCount;
    NotesFilesDAL notesFilesDAL;
    List<NotesFolderDB_Pojo> notesFolderPojoList;

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    public NotesFolderGridViewAdapter(Context context) {
        this.focusedPosition = 0;
        this.isEdit = false;
        this.isGridView = true;
        this.mContext = context;
    }

    public NotesFolderGridViewAdapter(Context context, List<NotesFolderDB_Pojo> list) {
        int i = 0;
        this.focusedPosition = 0;
        this.isEdit = false;
        this.isGridView = true;
        this.mContext = context;
        this.notesFolderPojoList = list;
        this.foldersCount = list.size();
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.notesCount = new int[list.size()];
        this.folderSize = new double[list.size()];
        this.constants = new Constants();
        this.notesFilesDAL = new NotesFilesDAL(context);
        while (true) {
            int[] iArr = this.notesCount;
            if (i < iArr.length) {
                NotesFilesDAL notesFilesDAL = this.notesFilesDAL;
                this.constants.getClass();
                StringBuilder sb = new StringBuilder();
                this.constants.getClass();
                sb.append("NotesFolderId");
                sb.append(" = ");
                sb.append(list.get(i).getNotesFolderId());
                iArr[i] = notesFilesDAL.getNotesFilesCount("SELECT \t\tCOUNT(*) \t\t\t\t   FROM TableNotesFile WHERE ".concat(sb.toString()));
                double[] dArr = this.folderSize;
                NotesFilesDAL notesFilesDAL2 = this.notesFilesDAL;
                this.constants.getClass();
                StringBuilder sb2 = new StringBuilder();
                this.constants.getClass();
                sb2.append("NotesFolderId");
                sb2.append(" = ");
                sb2.append(list.get(i).getNotesFolderId());
                dArr[i] = notesFilesDAL2.GetNotesFileRealEntity("SELECT SUM (NotesFileSize) FROM  TableNotesFile WHERE ".concat(sb2.toString()));
                i++;
            } else {
                return;
            }
        }
    }

    public void setFocusedPosition(int i) {
        this.focusedPosition = i;
    }

    public void setIsEdit(boolean z) {
        this.isEdit = z;
    }

    public void setIsGridview(boolean z) {
        this.isGridView = z;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.notesFolderPojoList.size();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (this.foldersCount > 0) {
            String str = this.notesFolderPojoList.get(i).getNotesFolderModifiedDate().split(",")[0];
            String str2 = this.notesFolderPojoList.get(i).getNotesFolderModifiedDate().split(", ")[1];
            Holder holder = new Holder();
            if (view == null) {
                if (this.isGridView) {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_gridview, viewGroup, false);
                } else {
                    view = this.inflater.inflate(R.layout.list_item_notes_folder_listview, viewGroup, false);
                }
                holder.tv_FolderName = (TextView) view.findViewById(R.id.tv_FolderName);
                holder.tv_NoteFolderDate = (TextView) view.findViewById(R.id.tv_NoteFolderDate);
                holder.tv_NoteFolderTime = (TextView) view.findViewById(R.id.tv_NoteFolderTime);
                holder.tv_noteFolder_size = (TextView) view.findViewById(R.id.tv_noteFolder_size);
                holder.tv_NotesCount = (TextView) view.findViewById(R.id.tv_NotesCount);
                holder.iv_notesFolder = (ImageView) view.findViewById(R.id.iv_notesFolder);
                holder.ll_selection = (LinearLayout) view.findViewById(R.id.ll_selection);
                holder.colorBorder = (ImageView) view.findViewById(R.id.colorBorder);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.tv_FolderName.setText(this.notesFolderPojoList.get(i).getNotesFolderName().toString());
            TextView textView = holder.tv_NoteFolderDate;
            textView.setText("Date: " + str);
            TextView textView2 = holder.tv_NoteFolderTime;
            textView2.setText("Time: " + str2);
            holder.tv_NoteFolderDate.setSelected(true);
            holder.tv_NoteFolderTime.setSelected(true);
            holder.tv_NotesCount.setText(String.valueOf(this.notesCount[i]));
            if (this.isGridView) {
                if (this.notesCount[i] > 0) {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
                } else {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.ic_notesfolder_empty_thumb_icon);
                }
                holder.tv_noteFolder_size.setVisibility(8);
            } else {
                if (this.notesCount[i] > 0) {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.ic_notesfolder_thumb_icon);
                } else {
                    holder.iv_notesFolder.setBackgroundResource(R.drawable.ic_notesfolder_empty_thumb_icon);
                }
                TextView textView3 = holder.tv_noteFolder_size;
                textView3.setText("Size: " + readableFileSize(this.folderSize[i]));
            }
            try {
                if (this.notesFolderPojoList.get(i).getNotesFolderName().equals("My Notes")) {
                    holder.colorBorder.setBackgroundColor(this.mContext.getResources().getColor(R.color.ColorLightBlue));
                } else {
                    holder.colorBorder.setBackgroundColor(Integer.parseInt(this.notesFolderPojoList.get(i).getNotesFolderColor()));
                }
            } catch (Exception unused) {
            }
            if (this.focusedPosition != i || !this.isEdit) {
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
            return "0 B";
        }
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        return new DecimalFormat("#,##0.#").format(d / Math.pow(1024.0d, log10)) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + new String[]{"B", "KB", "MB", "GB", "TB"}[log10];
    }

    /* loaded from: classes2.dex */
    public class Holder {
        ImageView colorBorder;
        ImageView iv_notesFolder;
        LinearLayout ll_selection;
        TextView tv_FolderName;
        TextView tv_NoteFolderDate;
        TextView tv_NoteFolderTime;
        TextView tv_NotesCount;
        TextView tv_noteFolder_size;

        public Holder() {
        }
    }
}
