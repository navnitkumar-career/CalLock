package com.example.gallerylock.gallery;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.gallerylock.photo.Photo;
import com.example.gallerylock.photo.PhotoDAL;
import com.example.gallerylock.video.Video;
import com.example.gallerylock.video.VideoDAL;

import java.util.List;


/* loaded from: classes2.dex */
public class ImageScanner {
    List<Photo> PhotosList;
    List<Video> VideoList;
    private Context mContext;

    /* loaded from: classes2.dex */
    public interface ScanCompleteCallBack {
        void scanComplete(List<Photo> list, List<Video> list2);
    }

    public ImageScanner(Context context) {
        this.mContext = context;
    }

    public void scanImages(final ScanCompleteCallBack scanCompleteCallBack) {
        final Handler handler = new Handler() { // from class: net.newsoftwares.hidepicturesvideos.gallery.ImageScanner.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                scanCompleteCallBack.scanComplete(ImageScanner.this.PhotosList, ImageScanner.this.VideoList);
            }
        };
        new Thread(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.gallery.ImageScanner.2
            @Override // java.lang.Runnable
            public void run() {
                PhotoDAL photoDAL = new PhotoDAL(ImageScanner.this.mContext);
                photoDAL.OpenRead();
                ImageScanner.this.PhotosList = photoDAL.GetPhotos();
                photoDAL.close();
                VideoDAL videoDAL = new VideoDAL(ImageScanner.this.mContext);
                videoDAL.OpenRead();
                ImageScanner.this.VideoList = videoDAL.GetVideos();
                videoDAL.close();
                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
    }
}
