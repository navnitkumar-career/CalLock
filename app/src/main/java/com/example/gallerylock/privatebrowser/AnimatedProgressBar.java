package com.example.gallerylock.privatebrowser;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.example.gallerylock.R;

/* loaded from: classes2.dex */
public class AnimatedProgressBar extends LinearLayout {
    private int mProgressColor;
    private int mProgress = 0;
    private boolean mBidirectionalAnimate = true;
    private int mDrawWidth = 0;
    private final Paint mPaint = new Paint();
    private final Rect mRect = new Rect();

    public AnimatedProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public AnimatedProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    /* JADX WARN: Finally extract failed */
    private void init(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.AnimatedProgressBar, 0, 0);
        try {
            int color = obtainStyledAttributes.getColor(0, 4342338);
            this.mProgressColor = obtainStyledAttributes.getColor(2, 2201331);
            this.mBidirectionalAnimate = obtainStyledAttributes.getBoolean(1, false);
            obtainStyledAttributes.recycle();
            ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.animated_progress_bar, (ViewGroup) this, true);
            setBackgroundColor(color);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public int getProgress() {
        return this.mProgress;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        this.mPaint.setColor(this.mProgressColor);
        this.mPaint.setStrokeWidth(10.0f);
        Rect rect = this.mRect;
        rect.right = rect.left + this.mDrawWidth;
        canvas.drawRect(this.mRect, this.mPaint);
    }

    public void setProgress(int i) {
        if (i > 100) {
            i = 100;
        } else if (i < 0) {
            i = 0;
        }
        if (getAlpha() < 1.0f) {
            fadeIn();
        }
        int measuredWidth = getMeasuredWidth();
        this.mRect.left = 0;
        this.mRect.top = 0;
        this.mRect.bottom = getBottom() - getTop();
        if (i < this.mProgress && !this.mBidirectionalAnimate) {
            this.mDrawWidth = 0;
        } else if (i == this.mProgress) {
            if (i == 100) {
                fadeOut();
                return;
            }
            return;
        }
        this.mProgress = i;
        int i2 = this.mDrawWidth;
        animateView(i2, measuredWidth, ((i * measuredWidth) / 100) - i2);
    }

    private void animateView(final int i, final int i2, final int i3) {
        Animation animation = new Animation() { // from class: net.newsoftwares.hidepicturesvideos.privatebrowser.AnimatedProgressBar.1
            @Override // android.view.animation.Animation
            public boolean willChangeBounds() {
                return false;
            }

            @Override // android.view.animation.Animation
            protected void applyTransformation(float f, Transformation transformation) {
                int i4 = i + ((int) (i3 * f));
                if (i4 <= i2) {
                    AnimatedProgressBar.this.mDrawWidth = i4;
                    AnimatedProgressBar.this.invalidate();
                }
                if (1.0f - f < 5.0E-4d && AnimatedProgressBar.this.mProgress >= 100) {
                    AnimatedProgressBar.this.fadeOut();
                }
            }
        };
        animation.setDuration(500L);
        animation.setInterpolator(new DecelerateInterpolator());
        startAnimation(animation);
    }

    private void fadeIn() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "alpha", 1.0f);
        ofFloat.setDuration(200L);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ofFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fadeOut() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "alpha", 0.0f);
        ofFloat.setDuration(200L);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ofFloat.start();
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            this.mProgress = bundle.getInt("progressState");
            parcelable = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(parcelable);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("progressState", this.mProgress);
        return bundle;
    }
}
