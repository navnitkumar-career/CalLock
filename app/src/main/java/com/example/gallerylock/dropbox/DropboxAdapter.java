package com.example.gallerylock.dropbox;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.securebackupcloud.BackupCloudEnt;
import com.example.gallerylock.securebackupcloud.CloudCommon;

import java.util.ArrayList;

/* loaded from: classes2.dex */
public class DropboxAdapter extends ArrayAdapter {
    ArrayList<BackupCloudEnt> backupCloudEntlist;
    private Context con;
    LayoutInflater layoutInflater;
    Resources res;

    public DropboxAdapter(Context context, int i, ArrayList<BackupCloudEnt> arrayList) {
        super(context, i, arrayList);
        this.con = context;
        this.backupCloudEntlist = arrayList;
        this.res = context.getResources();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view2;
        String str;
        String str2;
        if (view == null) {
            view2 = this.layoutInflater.inflate(R.layout.dropbox_download_items, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.lblFolderName = (TextView) view2.findViewById(R.id.lblFolderName);
            viewHolder.imagestatusitem = (ImageView) view2.findViewById(R.id.imagestatusitem);
            viewHolder.imagesyncitem = (ImageView) view2.findViewById(R.id.imagesyncitem);
            viewHolder.lblUploadSubject = (TextView) view2.findViewById(R.id.lblUploadSubject);
            viewHolder.lblDownloadSubject = (TextView) view2.findViewById(R.id.lblDownloadSubject);
            viewHolder.ivfile = (ImageView) view2.findViewById(R.id.ivfile);
            BackupCloudEnt backupCloudEnt = this.backupCloudEntlist.get(i);
            TextView textView = viewHolder.lblFolderName;
            if (backupCloudEnt.GetFolderName().length() > 16) {
                str2 = backupCloudEnt.GetFolderName().substring(0, 15) + "...";
            } else {
                str2 = backupCloudEnt.GetFolderName();
            }
            textView.setText(str2);
            viewHolder.lblUploadSubject.setText("Subject Upload = " + Integer.toString(backupCloudEnt.GetUploadCount()));
            viewHolder.lblDownloadSubject.setText("Subject Download = " + Integer.toString(backupCloudEnt.GetDownloadCount()));
            if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_photo_icon);
            } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_video_icon);
            } else if (CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_audio_icon);
            } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_documents_icon);
            } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_notes_icon);
            } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_password_icon);
            } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
                viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_todos_icon);
                viewHolder.lblUploadSubject.setVisibility(8);
                viewHolder.lblDownloadSubject.setVisibility(8);
            }
            viewHolder.imagestatusitem.setBackgroundResource(backupCloudEnt.GetImageStatus());
            viewHolder.imagesyncitem.setVisibility(View.VISIBLE);
            if (backupCloudEnt.GetIsInProgress()) {
                viewHolder.imagesyncitem.startAnimation(AnimationUtils.loadAnimation(this.con, R.anim.speaker_plate));
            }
            view2.setTag(viewHolder);
            view2.setTag(R.id.lblFolderName, viewHolder.lblFolderName);
            view2.setTag(R.id.lblUploadSubject, viewHolder.lblUploadSubject);
            view2.setTag(R.id.lblDownloadSubject, viewHolder.lblDownloadSubject);
            view2.setTag(R.id.imagestatusitem, viewHolder.imagestatusitem);
            view2.setTag(R.id.imagesyncitem, viewHolder.imagesyncitem);
            view2.setTag(R.id.ivfile, viewHolder.ivfile);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.imagestatusitem.setTag(Integer.valueOf(i));
        viewHolder.imagesyncitem.setTag(Integer.valueOf(i));
        viewHolder.lblFolderName.setTag(Integer.valueOf(i));
        viewHolder.lblUploadSubject.setTag(Integer.valueOf(i));
        viewHolder.lblDownloadSubject.setTag(Integer.valueOf(i));
        viewHolder.ivfile.setTag(Integer.valueOf(i));
        viewHolder.imagestatusitem.setBackgroundResource(this.backupCloudEntlist.get(i).GetImageStatus());
        viewHolder.imagesyncitem.setVisibility(View.VISIBLE);
        TextView textView2 = viewHolder.lblFolderName;
        if (this.backupCloudEntlist.get(i).GetFolderName().length() > 16) {
            str = this.backupCloudEntlist.get(i).GetFolderName().substring(0, 15) + "...";
        } else {
            str = this.backupCloudEntlist.get(i).GetFolderName();
        }
        textView2.setText(str);
        viewHolder.lblUploadSubject.setText("Subject Upload = " + Integer.toString(this.backupCloudEntlist.get(i).GetUploadCount()));
        viewHolder.lblDownloadSubject.setText("Subject Download = " + Integer.toString(this.backupCloudEntlist.get(i).GetDownloadCount()));
        if (CloudCommon.DropboxType.Photos.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_photo_icon);
        } else if (CloudCommon.DropboxType.Videos.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_video_icon);
        } else if (CloudCommon.DropboxType.Audio.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_audio_icon);
        } else if (CloudCommon.DropboxType.Documents.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_documents_icon);
        } else if (CloudCommon.DropboxType.Notes.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_notes_icon);
        } else if (CloudCommon.DropboxType.Wallet.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_password_icon);
        } else if (CloudCommon.DropboxType.ToDo.ordinal() == CloudCommon.ModuleType) {
            viewHolder.ivfile.setImageResource(R.drawable.ic_menu_cloud_todos_icon);
        }
        return view2;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        public ImageView imagestatusitem;
        public ImageView imagesyncitem;
        public ImageView ivfile;
        public TextView lblDownloadSubject;
        public TextView lblFolderName;
        public TextView lblUploadSubject;

        public ViewHolder() {
        }
    }
}
