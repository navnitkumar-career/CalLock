package com.example.gallerylock.video;

import android.content.Context;

import com.example.gallerylock.storageoption.StorageOptionsCommon;

/* loaded from: classes2.dex */
public class AlbumsGalleryVideosMethods {
    public void AddAlbumToDatabase(Context context, String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setAlbumName(str);
        videoAlbum.setAlbumLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + str);
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(context);
        try {
            try {
                videoAlbumDAL.OpenWrite();
                videoAlbumDAL.AddVideoAlbum(videoAlbum);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoAlbumDAL.close();
        }
    }

    public void UpdateAlbumInDatabase(Context context, int i, String str) {
        VideoAlbum videoAlbum = new VideoAlbum();
        videoAlbum.setId(i);
        videoAlbum.setAlbumName(str);
        videoAlbum.setAlbumLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.VIDEOS + str);
        VideoAlbumDAL videoAlbumDAL = new VideoAlbumDAL(context);
        try {
            try {
                videoAlbumDAL.OpenWrite();
                videoAlbumDAL.UpdateAlbumName(videoAlbum);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            videoAlbumDAL.close();
        }
    }
}
