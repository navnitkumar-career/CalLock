package com.example.gallerylock.audio;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.gallerylock.Flaes;
import com.example.gallerylock.utilities.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes2.dex */
public class PlayAudioAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    File fileIn;
    FileInputStream fileInputStream;
    File fileOut;
    ProgressDialog progressDialog;
    long time = 0;

    public PlayAudioAsyncTask(Context context, File file, File file2) {
        this.context = context;
        this.fileIn = file;
        this.fileOut = file2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Boolean doInBackground(Void... voidArr) {
        long nanoTime = System.nanoTime();
        boolean decryptUsingCipherStream_AES128 = Flaes.decryptUsingCipherStream_AES128(this.fileIn, this.fileOut);
        this.time = (System.nanoTime() - nanoTime) / 1000000;
        if (decryptUsingCipherStream_AES128) {
            return true;
        }
        return false;
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog progressDialog = new ProgressDialog(this.context);
        this.progressDialog = progressDialog;
        progressDialog.setTitle("Please Wait");
        this.progressDialog.setMessage("Decrypting audio...");
        this.progressDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        this.progressDialog.dismiss();
        try {
            this.fileInputStream = new FileInputStream(this.fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Common.mediaplayer.stop();
        Common.mediaplayer.reset();
        try {
            Common.mediaplayer.setDataSource(this.fileInputStream.getFD());
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalStateException e4) {
            e4.printStackTrace();
        }
        try {
            Common.mediaplayer.prepare();
        } catch (IOException e5) {
            e5.printStackTrace();
        } catch (IllegalStateException e6) {
            e6.printStackTrace();
        }
        Common.mediaplayer.start();
    }
}
