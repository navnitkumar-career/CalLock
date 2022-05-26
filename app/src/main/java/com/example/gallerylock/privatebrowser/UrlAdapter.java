package com.example.gallerylock.privatebrowser;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gallerylock.R;
import com.example.gallerylock.utilities.Common;

import java.util.List;

/* loaded from: classes2.dex */
public class UrlAdapter extends ArrayAdapter {
    Context con;
    LayoutInflater layoutInflater;
    private int listIcon;
    Resources res;
    List<String> urlList;

    public UrlAdapter(Context context, int i, List<String> list, int i2) {
        super(context, i, list);
        this.listIcon = 0;
        this.res = context.getResources();
        this.urlList = list;
        this.con = context;
        this.listIcon = i2;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.layoutInflater.inflate(R.layout.custom_url_item, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.lblurlitem);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.imgurlitem);
        if (this.listIcon == Common.BrowserMenuType.Bookmark.ordinal()) {
            imageView.setImageResource(R.drawable.download_bokmrk_histry_list_icon);
        } else if (this.listIcon == Common.BrowserMenuType.Download.ordinal()) {
            imageView.setImageResource(R.drawable.download_dwnlod_histry_list_icon);
        }
        textView.setText(this.urlList.get(i));
        return inflate;
    }
}
