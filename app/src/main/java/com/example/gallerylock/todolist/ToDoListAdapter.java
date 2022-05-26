package com.example.gallerylock.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerylock.R;
import com.example.gallerylock.common.Constants;
import com.example.gallerylock.common.ValidationCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.storageoption.StorageOptionsCommon;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import java.io.File;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    int ToDofilesCount;
    Constants constants;
    ToDoActivity fragment;
    LayoutInflater inflater;
    Activity mContext;
    ArrayList<ToDoDB_Pojo> toDoList;
    int viewBy = 0;

    /* loaded from: classes2.dex */
    public enum ViewBy {
        Detail,
        List,
        Tile
    }

    /* loaded from: classes2.dex */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView colorBorder;
        ImageView iv_toDoOptions;
        TextView tv_ToDoDate;
        TextView tv_ToDoName;
        TextView tv_ToDoTime;
        TextView tv_task1;
        TextView tv_task1c;
        TextView tv_task2;
        TextView tv_task2c;

        public ViewHolder(View view) {
            super(view);
            this.tv_ToDoName = (TextView) view.findViewById(R.id.tv_ToDoName);
            this.tv_ToDoDate = (TextView) view.findViewById(R.id.tv_ToDoDate);
            this.tv_ToDoTime = (TextView) view.findViewById(R.id.tv_ToDoTime);
            this.tv_task1 = (TextView) view.findViewById(R.id.tv_task1);
            this.tv_task2 = (TextView) view.findViewById(R.id.tv_task2);
            this.tv_task1c = (TextView) view.findViewById(R.id.tv_task1c);
            this.tv_task2c = (TextView) view.findViewById(R.id.tv_task2c);
            this.colorBorder = (ImageView) view.findViewById(R.id.colorBorder);
            this.iv_toDoOptions = (ImageView) view.findViewById(R.id.iv_toDoOptions);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            view.setSelected(true);
            int adapterPosition = getAdapterPosition();
            SecurityLocksCommon.IsAppDeactive = false;
            Common.ToDoListEdit = false;
            Common.ToDoListName = ToDoListAdapter.this.toDoList.get(adapterPosition).getToDoFileName();
            Common.ToDoListId = ToDoListAdapter.this.toDoList.get(adapterPosition).getToDoId();
            ToDoListAdapter.this.mContext.startActivity(new Intent(ToDoListAdapter.this.mContext, ViewToDoActivity.class));
            ToDoListAdapter.this.mContext.finish();
            ToDoListAdapter.this.mContext.overridePendingTransition(17432576, 17432577);
        }
    }

    public ToDoListAdapter(Activity activity, ArrayList<ToDoDB_Pojo> arrayList, ToDoActivity toDoActivity) {
        this.ToDofilesCount = 0;
        this.mContext = activity;
        this.fragment = toDoActivity;
        this.toDoList = arrayList;
        this.ToDofilesCount = arrayList.size();
        this.inflater = (LayoutInflater) activity.getSystemService("layout_inflater");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.toDoList.size();
    }

    public void setViewBy(int i) {
        this.viewBy = i;
    }

    public void setAdapterData(ArrayList<ToDoDB_Pojo> arrayList) {
        this.toDoList = arrayList;
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (this.ToDofilesCount > 0) {
            String str = this.toDoList.get(i).getToDoFileModifiedDate().split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR)[0];
            String str2 = this.toDoList.get(i).getToDoFileModifiedDate().split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR)[1];
            boolean isToDoFileTask1IsChecked = this.toDoList.get(i).isToDoFileTask1IsChecked();
            boolean isToDoFileTask2IsChecked = this.toDoList.get(i).isToDoFileTask2IsChecked();
            boolean isToDoFinished = this.toDoList.get(i).isToDoFinished();
            Utilities.strikeTextview(viewHolder.tv_ToDoName, this.toDoList.get(i).getToDoFileName().toString(), isToDoFinished);
            if (isToDoFinished) {
                viewHolder.tv_ToDoName.append("  ✓");
            }
            TextView textView = viewHolder.tv_ToDoDate;
            textView.setText("Date: " + str);
            TextView textView2 = viewHolder.tv_ToDoTime;
            textView2.setText("Time: " + str2);
            viewHolder.tv_ToDoDate.setSelected(true);
            viewHolder.tv_ToDoTime.setSelected(true);
            String trim = this.toDoList.get(i).getToDoFileTask1().trim();
            String trim2 = this.toDoList.get(i).getToDoFileTask2().trim();
            TextView textView3 = viewHolder.tv_task1;
            Utilities.strikeTextview(textView3, "• " + trim, isToDoFileTask1IsChecked);
            if (!trim2.equals("")) {
                TextView textView4 = viewHolder.tv_task2;
                Utilities.strikeTextview(textView4, "• " + trim2, isToDoFileTask2IsChecked);
                viewHolder.tv_task2.setVisibility(0);
                viewHolder.tv_task2c.setVisibility(0);
            } else {
                viewHolder.tv_task2c.setVisibility(8);
            }
            int i2 = -16711936;
            viewHolder.tv_task1c.setTextColor(isToDoFileTask1IsChecked ? -16711936 : this.mContext.getResources().getColor(R.color.Color_Secondary_Font));
            TextView textView5 = viewHolder.tv_task2c;
            if (!isToDoFileTask2IsChecked) {
                i2 = this.mContext.getResources().getColor(R.color.Color_Secondary_Font);
            }
            textView5.setTextColor(i2);
            try {
                String substring = this.toDoList.get(i).getToDoFileColor().substring(3);
                ImageView imageView = viewHolder.colorBorder;
                imageView.setBackgroundColor(Color.parseColor("#E6" + substring));
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewHolder.iv_toDoOptions.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ToDoListAdapter.this.showPopup(view, i);
                }
            });
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (this.viewBy == ViewBy.List.ordinal()) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_list_item, viewGroup, false);
        } else if (this.viewBy == ViewBy.Detail.ordinal()) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_detail_item, viewGroup, false);
        } else if (this.viewBy == ViewBy.Tile.ordinal()) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_grid_item, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_todo_detail_item, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPopup(View view, final int i) {
        View inflate = this.inflater.inflate(R.layout.popup_delete_rename, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(this.mContext);
        popupWindow.setContentView(inflate);
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);
        ((TextView) inflate.findViewById(R.id.tv_NotesFolderRename)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final String toDoFileName = ToDoListAdapter.this.toDoList.get(i).getToDoFileName();
                final Dialog dialog = new Dialog(ToDoListAdapter.this.mContext, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.album_add_edit_popup);
                final EditText editText = (EditText) dialog.findViewById(R.id.txt_AlbumName);
                editText.setText(toDoFileName);
                ((TextView) dialog.findViewById(R.id.lbl_Create_Edit_Album)).setText(R.string.rename_todo);
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.2.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        String trim = editText.getText().toString().trim();
                        if (trim.trim().equals("")) {
                            Toast.makeText(ToDoListAdapter.this.mContext, "Todo name can't be empty", 0).show();
                        } else if (ValidationCommon.isNoSpecialCharsInName(trim)) {
                            ToDoDAL toDoDAL = new ToDoDAL(ToDoListAdapter.this.mContext);
                            new Constants();
                            String currentDateTime2 = Utilities.getCurrentDateTime2();
                            File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + trim + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            File file2 = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + toDoFileName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            boolean IsFileAlreadyExist = toDoDAL.IsFileAlreadyExist("SELECT \t     * \t\t\t\t\t\t   FROM  TableToDo WHERE ToDoName = '" + trim + "'");
                            if (!file2.exists()) {
                                Toast.makeText(ToDoListAdapter.this.mContext, "Todo does not exists", 0).show();
                            } else if (!IsFileAlreadyExist) {
                                if (file.exists()) {
                                    file.delete();
                                }
                                ToDoReadFromXML toDoReadFromXML = new ToDoReadFromXML();
                                ToDoWriteInXML toDoWriteInXML = new ToDoWriteInXML();
                                ToDoPojo ReadToDoList = toDoReadFromXML.ReadToDoList(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + toDoFileName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                                ReadToDoList.setTitle(trim);
                                ReadToDoList.setDateModified(currentDateTime2);
                                if (toDoWriteInXML.saveToDoList(ToDoListAdapter.this.mContext, ReadToDoList, toDoFileName, true)) {
                                    ToDoListAdapter.this.toDoList.get(i).setToDoFileName(trim);
                                    ToDoListAdapter.this.toDoList.get(i).setToDoFileLocation(file.getAbsolutePath());
                                    ToDoListAdapter.this.toDoList.get(i).setToDoFileModifiedDate(currentDateTime2);
                                    toDoDAL.updateToDoFileInfoInDatabase(ToDoListAdapter.this.toDoList.get(i), "ToDoId", String.valueOf(ToDoListAdapter.this.toDoList.get(i).getToDoId()));
                                    ToDoListAdapter.this.notifyItemChanged(i);
                                    Toast.makeText(ToDoListAdapter.this.mContext, "Todo renamed successfully", 0).show();
                                } else {
                                    Toast.makeText(ToDoListAdapter.this.mContext, "Todo can't be renamed", 0).show();
                                }
                                dialog.cancel();
                            } else {
                                Toast.makeText(ToDoListAdapter.this.mContext, "Todo already exists", 0).show();
                            }
                        } else {
                            Toast.makeText(ToDoListAdapter.this.mContext, "Todo name can't have special characters", 0).show();
                        }
                        dialog.dismiss();
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.2.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                popupWindow.dismiss();
            }
        });
        ((TextView) inflate.findViewById(R.id.tv_NotesFolderDelete)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final Dialog dialog = new Dialog(ToDoListAdapter.this.mContext, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.confirmation_message_box);
                ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(ToDoListAdapter.this.mContext.getResources().getString(R.string.delete_todo));
                ((LinearLayout) dialog.findViewById(R.id.ll_Ok)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.3.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        try {
                            dialog.dismiss();
                            String toDoFileName = ToDoListAdapter.this.toDoList.get(i).getToDoFileName();
                            File file = new File(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.TODOLIST + toDoFileName + StorageOptionsCommon.NOTES_FILE_EXTENSION);
                            if (file.exists() && file.delete()) {
                                new ToDoDAL(ToDoListAdapter.this.mContext).deleteToDoFileFromDatabase("ToDoId", String.valueOf(ToDoListAdapter.this.toDoList.get(i).getToDoId()));
                                ToDoListAdapter.this.toDoList.remove(i);
                                ToDoListAdapter.this.notifyItemRemoved(i);
                                ToDoListAdapter.this.fragment.getData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                ((LinearLayout) dialog.findViewById(R.id.ll_Cancel)).setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.todolist.ToDoListAdapter.3.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                popupWindow.dismiss();
            }
        });
    }
}
