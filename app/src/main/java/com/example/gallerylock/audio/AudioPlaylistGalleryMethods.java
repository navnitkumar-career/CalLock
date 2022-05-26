package com.example.gallerylock.audio;

import android.content.Context;

import com.example.gallerylock.storageoption.StorageOptionsCommon;

/* loaded from: classes2.dex */
public class AudioPlaylistGalleryMethods {
    public void AddPlaylistToDatabase(Context context, String str) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setPlayListName(str);
        audioPlayListEnt.setPlayListLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + str);
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(context);
        try {
            try {
                audioPlayListDAL.OpenWrite();
                audioPlayListDAL.AddAudioPlayList(audioPlayListEnt);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioPlayListDAL.close();
        }
    }

    public void UpdatePlaylistInDatabase(Context context, int i, String str) {
        AudioPlayListEnt audioPlayListEnt = new AudioPlayListEnt();
        audioPlayListEnt.setId(i);
        audioPlayListEnt.setPlayListName(str);
        audioPlayListEnt.setPlayListLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + str);
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(context);
        try {
            try {
                audioPlayListDAL.OpenWrite();
                audioPlayListDAL.UpdatePlayListName(audioPlayListEnt);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioPlayListDAL.close();
        }
    }

    public void UpdatePlaylistLocation(AudioPlayListEnt audioPlayListEnt, Context context, String str) {
        audioPlayListEnt.setPlayListLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.AUDIOS + str);
        AudioPlayListDAL audioPlayListDAL = new AudioPlayListDAL(context);
        try {
            try {
                audioPlayListDAL.OpenWrite();
                audioPlayListDAL.UpdatePlayListLocationOnly(audioPlayListEnt);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            audioPlayListDAL.close();
        }
    }
}
