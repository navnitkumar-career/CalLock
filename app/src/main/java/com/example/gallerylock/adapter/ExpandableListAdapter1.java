package com.example.gallerylock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.gallerylock.R;

import java.util.HashMap;
import java.util.List;

/* loaded from: classes2.dex */
public class ExpandableListAdapter1 extends BaseExpandableListAdapter {
    private Context _context;
    private HashMap<String, List<String>> _listDataChild;
    private List<String> _listDataHeader;

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int i) {
        return i;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public ExpandableListAdapter1(Context context, List<String> list, HashMap<String, List<String>> hashMap) {
        this._context = context;
        this._listDataHeader = list;
        this._listDataChild = hashMap;
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int i, int i2) {
        return this._listDataChild.get(this._listDataHeader.get(i)).get(i2);
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        String str = (String) getChild(i, i2);
        if (view == null) {
            view = ((LayoutInflater) this._context.getSystemService("layout_inflater")).inflate(R.layout.popup_window_list_item, (ViewGroup) null);
        }
        ((TextView) view.findViewById(R.id.lblListItem)).setText(str);
        return view;
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int i) {
        return this._listDataChild.get(this._listDataHeader.get(i)).size();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int i) {
        return this._listDataHeader.get(i);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        String str = (String) getGroup(i);
        if (view == null) {
            view = ((LayoutInflater) this._context.getSystemService("layout_inflater")).inflate(R.layout.popup_window_list_group, (ViewGroup) null);
        }
        TextView textView = (TextView) view.findViewById(R.id.lblListHeader);
        textView.setTypeface(null, 1);
        textView.setText(str);
        return view;
    }
}
