package com.example.gallerylock.documents;

import android.content.Context;

import com.example.gallerylock.storageoption.StorageOptionsCommon;

/* loaded from: classes2.dex */
public class DocumentsFolderGalleryMethods {
    public void AddFolderToDatabase(Context context, String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setFolderName(str);
        documentFolder.setFolderLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + str);
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(context);
        try {
            try {
                documentFolderDAL.OpenWrite();
                documentFolderDAL.AddDocumentFolder(documentFolder);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            documentFolderDAL.close();
        }
    }

    public void UpdateAlbumInDatabase(Context context, int i, String str) {
        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setId(i);
        documentFolder.setFolderName(str);
        documentFolder.setFolderLocation(StorageOptionsCommon.STORAGEPATH + StorageOptionsCommon.DOCUMENTS + str);
        DocumentFolderDAL documentFolderDAL = new DocumentFolderDAL(context);
        try {
            try {
                documentFolderDAL.OpenWrite();
                documentFolderDAL.UpdateFolderName(documentFolder);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } finally {
            documentFolderDAL.close();
        }
    }
}
