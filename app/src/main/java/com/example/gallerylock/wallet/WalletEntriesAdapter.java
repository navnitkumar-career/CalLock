package com.example.gallerylock.wallet;

import android.content.Context;
import android.content.res.TypedArray;
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

import java.util.List;

/* loaded from: classes2.dex */
public class WalletEntriesAdapter extends BaseAdapter {
    int categoriesCount;
    private Context context;
    int count;
    int[] entryCount;
    LayoutInflater inflater;
    List<WalletEntryFileDB_Pojo> walletEntryDB_Pojo;
    int focusedPosition = 0;
    boolean isEdit = false;
    boolean isGridView = true;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public WalletEntriesAdapter(Context context, List<WalletEntryFileDB_Pojo> list) {
        this.context = context;
        this.walletEntryDB_Pojo = list;
        this.categoriesCount = list.size();
        this.inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        this.entryCount = new int[list.size()];
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
        return this.walletEntryDB_Pojo.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.walletEntryDB_Pojo.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (this.categoriesCount > 0) {
            if (view == null) {
                viewHolder = new ViewHolder();
                if (this.isGridView) {
                    view2 = this.inflater.inflate(R.layout.wallet_categories_item_gridview, viewGroup, false);
                } else {
                    view2 = this.inflater.inflate(R.layout.wallet_categories_item_listview, viewGroup, false);
                }
                viewHolder.tv_WalletTitle = (TextView) view2.findViewById(R.id.tv_WalletTitle);
                viewHolder.ll_WalletCategoriesEntryCount = (RelativeLayout) view2.findViewById(R.id.ll_WalletCategoriesEntryCount);
                viewHolder.iv_walletCategoriesIcon = (ImageView) view2.findViewById(R.id.iv_walletCategoriesIcon);
                viewHolder.ll_selection = (LinearLayout) view2.findViewById(R.id.ll_selection);
                view2.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                view2 = view;
            }
            TypedArray obtainTypedArray = this.context.getResources().obtainTypedArray(R.array.wallet_drawables_list);
            int resourceId = obtainTypedArray.getResourceId(this.walletEntryDB_Pojo.get(i).getCategoryFileIconIndex(), -1);
            viewHolder.tv_WalletTitle.setText(this.walletEntryDB_Pojo.get(i).getEntryFileName());
            viewHolder.ll_WalletCategoriesEntryCount.setVisibility(4);
            viewHolder.iv_walletCategoriesIcon.setBackgroundResource(resourceId);
            obtainTypedArray.recycle();
            if (this.focusedPosition != i || !this.isEdit) {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_unselect);
            } else {
                viewHolder.ll_selection.setBackgroundResource(R.drawable.album_grid_item_boarder_select);
            }
            view = view2;
        } else {
            view.setVisibility(8);
        }
        view.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.anim_fade_in));
        return view;
    }

    /* loaded from: classes2.dex */
    public class ViewHolder {
        ImageView iv_walletCategoriesIcon;
        RelativeLayout ll_WalletCategoriesEntryCount;
        LinearLayout ll_selection;
        TextView tv_WalletTitle;

        public ViewHolder() {
        }
    }
}
