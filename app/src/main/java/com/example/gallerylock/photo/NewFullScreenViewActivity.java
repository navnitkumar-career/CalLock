package com.example.gallerylock.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.gallerylock.R;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.gallery.GalleryActivity;
import com.example.gallerylock.panicswitch.AccelerometerListener;
import com.example.gallerylock.panicswitch.AccelerometerManager;
import com.example.gallerylock.panicswitch.PanicSwitchActivityMethods;
import com.example.gallerylock.panicswitch.PanicSwitchCommon;
import com.example.gallerylock.securitylocks.SecurityLocksCommon;
import com.example.gallerylock.utilities.Common;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class NewFullScreenViewActivity extends Activity implements AccelerometerListener, SensorEventListener {
    public static final int INDEX = 2;
    int albumId;
    int m_imagePosition;
    DisplayImageOptions options;
    List<Photo> photo;
    private SensorManager sensorManager;
    private ViewPager viewPager;
    final Context context = this;
    List<ImageData> imList = new ArrayList();
    int index = 2;
    int Backindex = 0;
    private ArrayList<String> mPhotosList = new ArrayList<>();
    int _SortType = 0;
    public static boolean IsPhoneGalleryLoad = true;

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_fullscreen_view);
        Log.d("TAG", "NewFullScreenViewActivity");
        IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_photo_empty_icon).showImageOnFail(R.drawable.ic_photo_empty_icon).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(HttpStatus.SC_MULTIPLE_CHOICES)).build();
        int i = 0;
        this.m_imagePosition = getIntent().getIntExtra("IMAGE_POSITION", 0);
        this._SortType = getIntent().getIntExtra("_SortBy", 0);
        this.albumId = getIntent().getIntExtra("ALBUM_ID", 0);
        this.mPhotosList = getIntent().getStringArrayListExtra("mPhotosList");
        if (Common.IsCameFromAppGallery || this.mPhotosList == null) {
            PhotoDAL photoDAL = new PhotoDAL(this);
            photoDAL.OpenRead();
            this.photo = photoDAL.GetPhotoByAlbumId(this.albumId, this._SortType);
            photoDAL.close();
            while (i < this.photo.size()) {
                ImageData imageData = new ImageData();
                imageData.setImgPath(this.photo.get(i).getFolderLockPhotoLocation());
                this.imList.add(imageData);
                i++;
            }
        } else {
            while (i < this.mPhotosList.size()) {
                ImageData imageData2 = new ImageData();
                imageData2.setImgPath(this.mPhotosList.get(i));
                this.imList.add(imageData2);
                i++;
            }
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        this.viewPager = viewPager;
        viewPager.setAdapter(new ImageAdapter());
        this.viewPager.setCurrentItem(this.m_imagePosition);
    }

    /* loaded from: classes2.dex */
    private class ImageAdapter extends PagerAdapter {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private LayoutInflater inflater;

        @Override // androidx.viewpager.widget.PagerAdapter
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        ImageAdapter() {
            this.inflater = LayoutInflater.from(NewFullScreenViewActivity.this.getApplicationContext());
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return NewFullScreenViewActivity.this.imList.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View inflate = this.inflater.inflate(R.layout.item_pager_image, viewGroup, false);
            TouchImageView touchImageView = (TouchImageView) inflate.findViewById(R.id.image);
            final ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.loading);
            touchImageView.setMaxZoom(6.0f);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("file:///" + NewFullScreenViewActivity.this.imList.get(i).getImgPath().toString(), touchImageView, NewFullScreenViewActivity.this.options, new SimpleImageLoadingListener() { // from class: net.newsoftwares.hidepicturesvideos.photo.NewFullScreenViewActivity.ImageAdapter.1
                @Override // com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener, com.nostra13.universalimageloader.core.listener.ImageLoadingListener
                public void onLoadingStarted(String str, View view) {
                    progressBar.setVisibility(0);
                }

                @Override // com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener, com.nostra13.universalimageloader.core.listener.ImageLoadingListener
                public void onLoadingFailed(String str, View view, FailReason failReason) {
                    int i2 = AnonymousClass1.$SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType[failReason.getType().ordinal()];
                    progressBar.setVisibility(8);
                }

                @Override // com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener, com.nostra13.universalimageloader.core.listener.ImageLoadingListener
                public void onLoadingComplete(String str, View view, Bitmap bitmap) {
                    progressBar.setVisibility(8);
                }
            });
            viewGroup.addView(inflate, 0);
            return inflate;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }

    /* renamed from: net.newsoftwares.hidepicturesvideos.photo.NewFullScreenViewActivity$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType;

        static {
            int[] iArr = new int[FailReason.FailType.values().length];
            $SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType = iArr;
            try {
                iArr[FailReason.FailType.IO_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType[FailReason.FailType.DECODING_ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType[FailReason.FailType.NETWORK_DENIED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType[FailReason.FailType.OUT_OF_MEMORY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$nostra13$universalimageloader$core$assist$FailReason$FailType[FailReason.FailType.UNKNOWN.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    @Override // net.newsoftwares.hidepicturesvideos.panicswitch.AccelerometerListener
    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager = this.sensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(8), 3);
        super.onResume();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (Common.IsCameFromAppGallery) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromAppGallery = false;
                startActivity(new Intent(this, Photos_Gallery_Actitvity.class));
                finish();
            } else if (Common.IsCameFromGalleryFeature) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromGalleryFeature = false;
                startActivity(new Intent(this, GalleryActivity.class));
                finish();
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, FeaturesActivity.class));
                finish();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }
}
