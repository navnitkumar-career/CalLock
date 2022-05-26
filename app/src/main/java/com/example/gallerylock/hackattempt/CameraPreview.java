package com.example.gallerylock.hackattempt;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/* loaded from: classes2.dex */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        SurfaceHolder holder = getHolder();
        this.mSurfaceHolder = holder;
        holder.addCallback(this);
        this.mSurfaceHolder.setType(3);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            this.mCamera.setPreviewDisplay(surfaceHolder);
            this.mCamera.startPreview();
        } catch (IOException unused) {
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            if (this.mCamera != null) {
                getHolder().removeCallback(this);
                this.mCamera.stopPreview();
                this.mCamera.release();
            }
        } catch (Exception e) {
            Log.v("The Exception is:", e.toString());
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        try {
            this.mCamera.setPreviewDisplay(surfaceHolder);
            this.mCamera.startPreview();
        } catch (Exception unused) {
        }
    }
}
