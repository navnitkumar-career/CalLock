package com.example.gallerylock.hackattempt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;

import java.io.File;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class HackAttemptListAdapter extends ArrayAdapter {
    boolean _isAllCheck;
    boolean _isEdit;
    private final Context con;
    private final ArrayList<HackAttemptEntity> hackAttemptEntitys;
    LayoutInflater layoutInflater;
    Resources res;

    public HackAttemptListAdapter(Context context, int i, ArrayList<HackAttemptEntity> arrayList, boolean z, Boolean bool) {
        super(context, i, arrayList);
        this._isEdit = false;
        this._isAllCheck = false;
        this.con = context;
        this.hackAttemptEntitys = arrayList;
        this.res = context.getResources();
        this._isEdit = z;
        this._isAllCheck = bool.booleanValue();
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view2;
        if (view == null) {
            view2 = this.layoutInflater.inflate(R.layout.hack_attempt_activity_item, (ViewGroup) null);
            viewHolder = new ViewHolder();
            Typeface createFromAsset = Typeface.createFromAsset(this.con.getAssets(), "ebrima.ttf");
            viewHolder.lbl_hackattempt_pass_item = (TextView) view2.findViewById(R.id.lbl_hackattempt_pass_item);
            viewHolder.lbl_hackattempt_description_item = (TextView) view2.findViewById(R.id.lbl_hackattempt_description_item);
            viewHolder.lbl_hackattempt_pass_item.setTypeface(createFromAsset);
            viewHolder.iv_hackattempt_item = (ImageView) view2.findViewById(R.id.iv_hackattempt_item);
            viewHolder.cb_hackattempt_item = (CheckBox) view2.findViewById(R.id.cb_hackattempt_item);
            LinearLayout linearLayout = (LinearLayout) view2.findViewById(R.id.ll_hackattemptitem);
            HackAttemptEntity hackAttemptEntity = this.hackAttemptEntitys.get(i);
            if (SecurityLocksCommon.LoginOptions.Password.toString().equals(hackAttemptEntity.GetLoginOption())) {
                TextView textView = viewHolder.lbl_hackattempt_pass_item;
                textView.setText("Wrong Password: " + hackAttemptEntity.GetWrongPassword());
            } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(hackAttemptEntity.GetLoginOption())) {
                TextView textView2 = viewHolder.lbl_hackattempt_pass_item;
                textView2.setText("Wrong PIN: " + hackAttemptEntity.GetWrongPassword());
            } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(hackAttemptEntity.GetLoginOption())) {
                TextView textView3 = viewHolder.lbl_hackattempt_pass_item;
                textView3.setText("Wrong Pattern: " + hackAttemptEntity.GetWrongPassword());
            }
            viewHolder.lbl_hackattempt_description_item.setText(hackAttemptEntity.GetHackAttemptTime().replace("GMT+05:00", ""));
            viewHolder.cb_hackattempt_item.setChecked(hackAttemptEntity.GetIsCheck());
            if (hackAttemptEntity.GetImagePath().length() > 0) {
                viewHolder.iv_hackattempt_item.setImageBitmap(HackAttemptMethods.DecodeFile(new File(hackAttemptEntity.GetImagePath())));
            }
            if (this._isEdit) {
                viewHolder.cb_hackattempt_item.setVisibility(0);
            } else {
                viewHolder.cb_hackattempt_item.setVisibility(4);
            }
            if (this._isAllCheck && hackAttemptEntity.GetIsCheck()) {
                viewHolder.cb_hackattempt_item.setChecked(true);
            }
            viewHolder.cb_hackattempt_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: net.newsoftwares.hidepicturesvideos.hackattempt.HackAttemptListAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ((HackAttemptEntity) HackAttemptListAdapter.this.hackAttemptEntitys.get(((Integer) compoundButton.getTag()).intValue())).SetIsCheck(Boolean.valueOf(compoundButton.isChecked()));
                }
            });
            view2.setTag(viewHolder);
            view2.setTag(R.id.lbl_hackattempt_pass_item, viewHolder.lbl_hackattempt_pass_item);
            view2.setTag(R.id.lbl_hackattempt_description_item, viewHolder.lbl_hackattempt_description_item);
            view2.setTag(R.id.iv_hackattempt_item, viewHolder.iv_hackattempt_item);
            view2.setTag(R.id.cb_hackattempt_item, viewHolder.cb_hackattempt_item);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view2 = view;
        }
        viewHolder.cb_hackattempt_item.setTag(Integer.valueOf(i));
        if (this._isEdit) {
            viewHolder.cb_hackattempt_item.setVisibility(0);
        } else {
            viewHolder.cb_hackattempt_item.setVisibility(4);
        }
        if (SecurityLocksCommon.LoginOptions.Password.toString().equals(this.hackAttemptEntitys.get(i).GetLoginOption())) {
            TextView textView4 = viewHolder.lbl_hackattempt_pass_item;
            textView4.setText("Wrong Password: " + this.hackAttemptEntitys.get(i).GetWrongPassword());
        } else if (SecurityLocksCommon.LoginOptions.Pin.toString().equals(this.hackAttemptEntitys.get(i).GetLoginOption())) {
            TextView textView5 = viewHolder.lbl_hackattempt_pass_item;
            textView5.setText("Wrong PIN: " + this.hackAttemptEntitys.get(i).GetWrongPassword());
        } else if (SecurityLocksCommon.LoginOptions.Pattern.toString().equals(this.hackAttemptEntitys.get(i).GetLoginOption())) {
            TextView textView6 = viewHolder.lbl_hackattempt_pass_item;
            textView6.setText("Wrong Pattern: " + this.hackAttemptEntitys.get(i).GetWrongPassword());
        }
        viewHolder.lbl_hackattempt_description_item.setText(this.hackAttemptEntitys.get(i).GetHackAttemptTime().replace("GMT+05:00", ""));
        viewHolder.cb_hackattempt_item.setChecked(this.hackAttemptEntitys.get(i).GetIsCheck());
        view2.startAnimation(AnimationUtils.loadAnimation(this.con, 17432578));
        return view2;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        public CheckBox cb_hackattempt_item;
        public ImageView iv_hackattempt_item;
        public TextView lbl_hackattempt_description_item;
        public TextView lbl_hackattempt_pass_item;

        public ViewHolder() {
        }
    }
}
