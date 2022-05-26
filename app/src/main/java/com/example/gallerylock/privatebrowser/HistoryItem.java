package com.example.gallerylock.privatebrowser;

import android.graphics.Bitmap;

/* loaded from: classes2.dex */
public class HistoryItem implements Comparable<HistoryItem> {
    private Bitmap mBitmap;
    private String mFolder;
    private int mId;
    private int mImageId;
    private int mOrder;
    private String mTitle;
    private String mUrl;

    public HistoryItem() {
        this.mId = 0;
        this.mUrl = "";
        this.mTitle = "";
        this.mFolder = "";
        this.mBitmap = null;
        this.mImageId = 0;
        this.mOrder = 0;
    }

    public HistoryItem(int i, String str, String str2) {
        this.mId = 0;
        this.mUrl = "";
        this.mTitle = "";
        this.mFolder = "";
        this.mBitmap = null;
        this.mImageId = 0;
        this.mOrder = 0;
        this.mId = i;
        this.mUrl = str;
        this.mTitle = str2;
        this.mBitmap = null;
    }

    public HistoryItem(String str, String str2) {
        this.mId = 0;
        this.mUrl = "";
        this.mTitle = "";
        this.mFolder = "";
        this.mBitmap = null;
        this.mImageId = 0;
        this.mOrder = 0;
        this.mUrl = str;
        this.mTitle = str2;
        this.mBitmap = null;
    }

    public HistoryItem(String str, String str2, int i) {
        this.mId = 0;
        this.mUrl = "";
        this.mTitle = "";
        this.mFolder = "";
        this.mBitmap = null;
        this.mImageId = 0;
        this.mOrder = 0;
        this.mUrl = str;
        this.mTitle = str2;
        this.mBitmap = null;
        this.mImageId = i;
    }

    public int getId() {
        return this.mId;
    }

    public int getImageId() {
        return this.mImageId;
    }

    public void setID(int i) {
        this.mId = i;
    }

    public void setImageId(int i) {
        this.mImageId = i;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setFolder(String str) {
        if (str == null) {
            str = "";
        }
        this.mFolder = str;
    }

    public void setOrder(int i) {
        this.mOrder = i;
    }

    public int getOrder() {
        return this.mOrder;
    }

    public String getFolder() {
        return this.mFolder;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String str) {
        if (str == null) {
            str = "";
        }
        this.mUrl = str;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String str) {
        if (str == null) {
            str = "";
        }
        this.mTitle = str;
    }

    public String toString() {
        return this.mTitle;
    }

    public int compareTo(HistoryItem historyItem) {
        return this.mTitle.compareTo(historyItem.mTitle);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HistoryItem historyItem = (HistoryItem) obj;
        if (this.mId != historyItem.mId || this.mImageId != historyItem.mImageId) {
            return false;
        }
        Bitmap bitmap = this.mBitmap;
        if (bitmap == null ? historyItem.mBitmap == null : bitmap.equals(historyItem.mBitmap)) {
            return this.mTitle.equals(historyItem.mTitle) && this.mUrl.equals(historyItem.mUrl);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = ((((this.mId * 31) + this.mUrl.hashCode()) * 31) + this.mTitle.hashCode()) * 31;
        Bitmap bitmap = this.mBitmap;
        return ((hashCode + (bitmap != null ? bitmap.hashCode() : 0)) * 31) + this.mImageId;
    }
}
