package com.example.gallerylock.audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Common;
import com.example.gallerylock.utilities.Utilities;

import java.util.List;

/* loaded from: classes2.dex */
public class AudioFileAdapter extends ArrayAdapter {
    private int _ViewBy;
    private AudioActivity audioActivity;
    private final Context con;
    private boolean isEdit;
    LayoutInflater layoutInflater;
    private List<AudioEnt> listItems;

    public AudioFileAdapter(AudioActivity audioActivity, Context context, int i, List<AudioEnt> list, Boolean bool, int i2) {
        super(context, i, list);
        this._ViewBy = 0;
        this.audioActivity = audioActivity;
        this.con = context;
        this.listItems = list;
        this.isEdit = bool.booleanValue();
        this._ViewBy = i2;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        View view2;
        if (view == null) {
            AudioEnt audioEnt = this.listItems.get(i);
            viewHolder = new ViewHolder();
            if (this._ViewBy == 0) {
                view2 = this.layoutInflater.inflate(R.layout.card_view_audio_item_list, (ViewGroup) null);
                viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.textAlbumName);
                viewHolder.rl_thumimage = (RelativeLayout) view2.findViewById(R.id.rl_thumimage);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.ll_thumimage = (LinearLayout) view2.findViewById(R.id.ll_thumimage);
                viewHolder.textAlbumName.setText(audioEnt.getAudioName());
            } else {
                view2 = this.layoutInflater.inflate(R.layout.card_view_audio_item_detail, (ViewGroup) null);
                viewHolder.textAlbumName = (TextView) view2.findViewById(R.id.textAlbumName);
                viewHolder.rl_thumimage = (RelativeLayout) view2.findViewById(R.id.rl_thumimage);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                viewHolder.lbl_Date = (TextView) view2.findViewById(R.id.lbl_Date);
                viewHolder.lbl_Time = (TextView) view2.findViewById(R.id.lbl_Time);
                viewHolder.lbl_Size = (TextView) view2.findViewById(R.id.lbl_Size);
                viewHolder.imageview = (ImageView) view2.findViewById(R.id.thumbImage);
                viewHolder.ll_thumimage = (LinearLayout) view2.findViewById(R.id.ll_thumimage);
            }
            if (audioEnt.GetFileCheck()) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                if (!Common.IsSelectAll) {
                    Common.SelectedCount++;
                    this.audioActivity.SelectedItemsCount(Common.SelectedCount);
                }
            } else {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            }
            if (this.isEdit) {
                viewHolder.rl_thumimage.setOnClickListener(new View.OnClickListener() { // from class: net.newsoftwares.hidepicturesvideos.audio.AudioFileAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view3) {
                        int intValue = ((Integer) view3.getTag()).intValue();
                        if (((AudioEnt) AudioFileAdapter.this.listItems.get(intValue)).GetFileCheck()) {
                            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
                            ((AudioEnt) AudioFileAdapter.this.listItems.get(intValue)).SetFileCheck(false);
                            Common.SelectedCount--;
                            AudioFileAdapter.this.audioActivity.SelectedItemsCount(Common.SelectedCount);
                            Common.IsSelectAll = false;
                            return;
                        }
                        viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
                        ((AudioEnt) AudioFileAdapter.this.listItems.get(intValue)).SetFileCheck(true);
                        if (!Common.IsSelectAll) {
                            Common.SelectedCount++;
                            AudioFileAdapter.this.audioActivity.SelectedItemsCount(Common.SelectedCount);
                        }
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
            viewHolder.textAlbumName.setText(this.listItems.get(i).getAudioName());
        } else {
            viewHolder.textAlbumName.setText(this.listItems.get(i).getAudioName());
            String str = this.listItems.get(i).get_modifiedDateTime().split(",")[0];
            String str2 = this.listItems.get(i).get_modifiedDateTime().split(", ")[1];
            viewHolder.lbl_Date.setText(str);
            viewHolder.lbl_Time.setText(str2);
            viewHolder.lbl_Size.setText(Utilities.FileSize(this.listItems.get(i).getFolderLockAudioLocation()));
        }
        if (this.listItems.get(i).GetFileCheck()) {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            this.audioActivity.SelectedItemsCount(Common.SelectedCount);
        } else {
            viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
        }
        viewHolder.id = i;
        return view;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        int id;
        ImageView imageview;
        TextView lbl_Date;
        TextView lbl_Size;
        TextView lbl_Time;
        LinearLayout ll_selection;
        LinearLayout ll_thumimage;
        RelativeLayout rl_thumimage;
        TextView textAlbumName;

        public ViewHolder() {
        }
    }
}
