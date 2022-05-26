package com.example.gallerylock.securitylocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.gallerylock.R;
import com.example.gallerylock.features.FeaturesActivity;
import com.example.gallerylock.recoveryofsecuritylocks.RecoveryOfCredentialsActivity;
import com.example.gallerylock.utilities.Common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes2.dex */
public class ConfirmLockPatternView extends View {
    public static final int BACKGROUND_COLOR = -16760187;
    public static final long BUILD_TIMEOUT_MILLIS = 1000;
    public static final float CELL_NODE_RATIO = 0.9f;
    public static final int DEATH_COLOR = -65536;
    public static final int DEFAULT_LENGTH_NODES = 3;
    public static final int DEFAULT_LENGTH_PX = 100;
    public static final int EDGE_COLOR = -4008213;
    public static final int LINE_COLOR = -10027060;
    public static final float NODE_EDGE_RATIO = 0.33f;
    public static final int PRACTICE_RESULT_DISPLAY_MILLIS = 1000;
    public static final int TACTILE_FEEDBACK_DURATION = 35;
    protected static Paint mLinePaint;
    String DecoyPattern;
    public Context con;
    protected int mCellLength;
    protected Paint mEdgePaint;
    protected boolean mPracticeMode;
    protected List<Point> mPracticePattern;
    protected Set<Point> mPracticePool;
    protected boolean mTactileFeedback;
    protected int mTouchThreshold;
    public static final int DEFAULT_PATTERN_GREEN_LINE_COLOR = NodeDrawable1.green;
    public static final int DEFAULT_PATTERN_RED_LINE_COLOR = NodeDrawable1.red;
    public static boolean IsUninstallProtectionActivity = false;
    public static boolean IsUninstallFolderLcokActivity = false;
    protected int mLengthPx = 100;
    protected int mLengthNodes = 3;
    protected NodeDrawable1[][] mNodeDrawables = (NodeDrawable1[][]) Array.newInstance(NodeDrawable1.class, 0, 0);
    protected List<Point> mCurrentPattern = Collections.emptyList();
    protected HighlightMode mHighlightMode = new NoHighlight();
    protected Point mTouchPoint = new Point(-1, -1);
    protected Point mTouchCell = new Point(-1, -1);
    protected boolean mDrawTouchExtension = false;
    protected boolean mDisplayingPracticeResult = false;
    protected HighlightMode mPracticeFailureMode = new FailureHighlight();
    protected HighlightMode mPracticeSuccessMode = new SuccessHighlight();
    protected Handler mHandler = new Handler();
    protected Vibrator mVibrator = (Vibrator) getContext().getSystemService("vibrator");

    /* loaded from: classes2.dex */
    public static class FirstHighlight implements HighlightMode {
        @Override // net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.HighlightMode
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            return i == 0 ? 2 : 1;
        }
    }

    /* loaded from: classes2.dex */
    public interface HighlightMode {
        int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5);
    }

    /* loaded from: classes2.dex */
    public static class NoHighlight implements HighlightMode {
        @Override // net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.HighlightMode
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            return 1;
        }
    }

    public ConfirmLockPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.con = context;
        Paint paint = new Paint();
        this.mEdgePaint = paint;
        paint.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
        this.mEdgePaint.setStrokeCap(Paint.Cap.ROUND);
        this.mEdgePaint.setFlags(1);
        Paint paint2 = new Paint();
        mLinePaint = paint2;
        paint2.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAlpha(100);
        mLinePaint.setFlags(1);
        this.DecoyPattern = SecurityLocksSharedPreferences.GetObject(this.con).GetDecoySecurityCredential();
    }

    private void buildDrawables() {
        int i = this.mLengthNodes;
        this.mNodeDrawables = (NodeDrawable1[][]) Array.newInstance(NodeDrawable1.class, i, i);
        int i2 = this.mLengthPx / this.mLengthNodes;
        this.mCellLength = i2;
        float f = i2 * 0.9f;
        this.mEdgePaint.setStrokeWidth(0.33f * f);
        this.mTouchThreshold = (int) (f / 2.0f);
        int i3 = this.mCellLength / 2;
        long currentTimeMillis = System.currentTimeMillis();
        for (int i4 = 0; i4 < this.mLengthNodes; i4++) {
            for (int i5 = 0; i5 < this.mLengthNodes; i5++) {
                if (System.currentTimeMillis() - currentTimeMillis >= 1000) {
                    PatternActivityMethods.clearAndBail(getContext());
                }
                int i6 = this.mCellLength;
                this.mNodeDrawables[i5][i4] = new NodeDrawable1(f, new Point((i5 * i6) + i3, (i6 * i4) + i3), this.con);
            }
        }
        if (!this.mPracticeMode) {
            loadPattern(this.mCurrentPattern, this.mHighlightMode);
        }
    }

    private void clearPattern(List<Point> list) {
        for (Point point : list) {
            this.mNodeDrawables[point.x][point.y].setNodeState(0);
            mLinePaint.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
            mLinePaint.setAlpha(100);
        }
    }

    private void loadPattern(List<Point> list, HighlightMode highlightMode) {
        for (int i = 0; i < list.size(); i++) {
            Point point = list.get(i);
            NodeDrawable1 nodeDrawable1 = this.mNodeDrawables[point.x][point.y];
            nodeDrawable1.setNodeState(highlightMode.select(nodeDrawable1, i, list.size(), point.x, point.y, this.mLengthNodes));
            if (i < list.size() - 1) {
                Point point2 = list.get(i + 1);
                Point center = this.mNodeDrawables[point.x][point.y].getCenter();
                Point center2 = this.mNodeDrawables[point2.x][point2.y].getCenter();
                this.mNodeDrawables[point.x][point.y].setExitAngle((float) Math.atan2(center.y - center2.y, center.x - center2.x));
            }
        }
    }

    private void appendPattern(List<Point> list, Point point) {
        NodeDrawable1 nodeDrawable1 = this.mNodeDrawables[point.x][point.y];
        nodeDrawable1.setNodeState(1);
        if (list.size() > 0) {
            Point point2 = list.get(list.size() - 1);
            NodeDrawable1 nodeDrawable12 = this.mNodeDrawables[point2.x][point2.y];
            Point center = nodeDrawable12.getCenter();
            Point center2 = nodeDrawable1.getCenter();
            nodeDrawable12.setExitAngle((float) Math.atan2(center.y - center2.y, center.x - center2.x));
        }
        list.add(point);
    }

    private void testPracticePattern() {
        this.mDisplayingPracticeResult = true;
        HighlightMode highlightMode = this.mPracticeFailureMode;
        if (PatternActivityMethods.ConvertPatternToNo(this.mPracticePattern).equals(SecurityLocksCommon.PatternPassword)) {
            highlightMode = this.mPracticeSuccessMode;
            Log.v(" Success", "Great ");
        } else if (PatternActivityMethods.ConvertPatternToNo(this.mPracticePattern).equals(this.DecoyPattern) && !SecurityLocksCommon.IsConfirmPatternActivity) {
            highlightMode = this.mPracticeSuccessMode;
            Log.v(" Success", "Great ");
        }
        loadPattern(this.mPracticePattern, highlightMode);
        this.mHandler.postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.1
            @Override // java.lang.Runnable
            public void run() {
                if (ConfirmLockPatternView.this.mDisplayingPracticeResult) {
                    ConfirmLockPatternView confirmLockPatternView = ConfirmLockPatternView.this;
                    confirmLockPatternView.testPasswordPattern(PatternActivityMethods.ConvertPatternToNo(confirmLockPatternView.mPracticePattern));
                    ConfirmLockPatternView.this.resetPractice();
                    ConfirmLockPatternView.this.invalidate();
                }
            }
        }, 1000L);
    }

    public void testPattern(List<Point> list, List<Point> list2) {
        if (list.equals(list2)) {
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                ((Activity) getContext()).finish();
            }
            Log.v(" Success", "Great ");
        } else {
            Log.v(" Fail", "Fail ");
        }
        this.mHandler.postDelayed(new Runnable() { // from class: net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.2
            @Override // java.lang.Runnable
            public void run() {
                ConfirmLockPatternView.this.resetPractice();
                ConfirmLockPatternView.this.invalidate();
            }
        }, 1000L);
    }

    public void testPasswordPattern(String str) {
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this.con);
        if (str.equals(SecurityLocksCommon.PatternPassword)) {
            SecurityLocksCommon.IsFakeAccount = 0;
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                SecurityLocksCommon.IsAppDeactive = false;
                SecurityLocksCommon.isBackupPattern = false;
                SecurityLocksCommon.isSettingDecoy = false;
                SecurityLocksCommon.IsConfirmPatternActivity = false;
                Intent intent = new Intent(this.con, SecurityLocksActivity.class);
                intent.putExtra("isSettingDecoy", false);
                this.con.startActivity(intent);
                ((Activity) getContext()).finish();
            } else if (SecurityLocksCommon.isSettingDecoy) {
                SecurityLocksCommon.IsAppDeactive = false;
                SecurityLocksCommon.isSettingDecoy = false;
                SecurityLocksCommon.IsConfirmPatternActivity = false;
                SecurityLocksCommon.isBackupPattern = false;
                Intent intent2 = new Intent(this.con, SetPatternActivity.class);
                intent2.putExtra("isSettingDecoy", true);
                this.con.startActivity(intent2);
                ((Activity) getContext()).finish();
            } else if (SecurityLocksCommon.isBackupPattern) {
                SecurityLocksCommon.IsAppDeactive = false;
                SecurityLocksCommon.IsConfirmPatternActivity = false;
                SecurityLocksCommon.isSettingDecoy = false;
                SecurityLocksCommon.isBackupPattern = false;
                this.con.startActivity(new Intent(this.con, RecoveryOfCredentialsActivity.class));
                ((Activity) getContext()).finish();
            } else {
                Common.loginCount = GetObject.GetRateCount();
                Common.loginCount++;
                GetObject.SetRateCount(Common.loginCount);
                SecurityLocksCommon.IsFakeAccount = 0;
                SecurityLocksCommon.IsnewloginforAd = true;
                SecurityLocksCommon.IsAppDeactive = true;
                if (!SecurityLocksCommon.IsAppDeactive || SecurityLocksCommon.CurrentActivity == null) {
                    Common.loginCount = GetObject.GetRateCount();
                    Common.loginCount++;
                    GetObject.SetRateCount(Common.loginCount);
                    this.con.startActivity(new Intent(this.con, FeaturesActivity.class));
                    ((Activity) getContext()).finish();
                } else {
                    this.con.startActivity(new Intent(this.con, SecurityLocksCommon.CurrentActivity.getClass()));
                    ((Activity) getContext()).finish();
                }
            }
            Log.v(" Success", "Great ");
        } else if (!str.equals(this.DecoyPattern)) {
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
                ConfirmPatternActivity.lblConfirmpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Tryagain);
            } else {
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
            }
            Log.v(" Fail", "Fail ");
        } else if (SecurityLocksCommon.IsConfirmPatternActivity && SecurityLocksCommon.IsConfirmPatternActivity) {
            loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
            ConfirmPatternActivity.lblConfirmpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Tryagain);
        }
    }

    public void resetPractice() {
        clearPattern(this.mPracticePattern);
        this.mPracticePattern.clear();
        this.mPracticePool.clear();
        this.mDisplayingPracticeResult = false;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        List<Point> list = this.mCurrentPattern;
        if (this.mPracticeMode) {
            list = this.mPracticePattern;
        }
        CenterIterator centerIterator = new CenterIterator(list.iterator());
        if (centerIterator.hasNext()) {
            mLinePaint.setStrokeWidth(9.0f);
            mLinePaint.setAlpha(100);
            Point next = centerIterator.next();
            while (centerIterator.hasNext()) {
                Point next2 = centerIterator.next();
                canvas.drawLine(next.x, next.y, next2.x, next2.y, mLinePaint);
                next = next2;
            }
            if (this.mDrawTouchExtension) {
                canvas.drawLine(next.x, next.y, this.mTouchPoint.x, this.mTouchPoint.y, mLinePaint);
            }
        }
        for (int i = 0; i < this.mLengthNodes; i++) {
            for (int i2 = 0; i2 < this.mLengthNodes; i2++) {
                this.mNodeDrawables[i2][i].draw(canvas);
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mPracticeMode) {
            return super.onTouchEvent(motionEvent);
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            if (this.mDisplayingPracticeResult) {
                resetPractice();
            }
            this.mDrawTouchExtension = true;
        } else if (action == 1) {
            this.mDrawTouchExtension = false;
            testPracticePattern();
            invalidate();
            return true;
        } else if (action != 2) {
            return super.onTouchEvent(motionEvent);
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int i = (int) x;
        this.mTouchPoint.x = i;
        int i2 = (int) y;
        this.mTouchPoint.y = i2;
        this.mTouchCell.x = i / this.mCellLength;
        this.mTouchCell.y = i2 / this.mCellLength;
        if (this.mTouchCell.x >= 0 && this.mTouchCell.x < this.mLengthNodes && this.mTouchCell.y >= 0 && this.mTouchCell.y < this.mLengthNodes) {
            Point center = this.mNodeDrawables[this.mTouchCell.x][this.mTouchCell.y].getCenter();
            if (((int) Math.sqrt(Math.pow(x - center.x, 2.0d) + Math.pow(y - center.y, 2.0d))) < this.mTouchThreshold && !this.mPracticePool.contains(this.mTouchCell)) {
                if (this.mTactileFeedback) {
                    this.mVibrator.vibrate(35L);
                }
                Point point = new Point(this.mTouchCell);
                appendPattern(this.mPracticePattern, point);
                this.mPracticePool.add(point);
            }
        }
        invalidate();
        return true;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        int size2 = MeasureSpec.getSize(i2);
        int mode2 = MeasureSpec.getMode(i2);
        if (mode == 0 && mode2 == 0) {
            size = 100;
            setMeasuredDimension(100, 100);
        } else if (mode == 0) {
            size = size2;
        } else if (mode2 != 0) {
            size = Math.min(size, size2);
        }
        setMeasuredDimension(size, size);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mLengthPx = Math.min(i, i2);
        buildDrawables();
        if (!this.mPracticeMode) {
            loadPattern(this.mCurrentPattern, this.mHighlightMode);
        }
    }

    public void setPattern(List<Point> list) {
        clearPattern(this.mCurrentPattern);
        loadPattern(list, this.mHighlightMode);
        this.mCurrentPattern = list;
    }

    public List<Point> getPattern() {
        return this.mCurrentPattern;
    }

    public void setGridLength(int i) {
        this.mLengthNodes = i;
        this.mCurrentPattern = Collections.emptyList();
        buildDrawables();
    }

    public int getGridLength() {
        return this.mLengthNodes;
    }

    public void setHighlightMode(HighlightMode highlightMode) {
        setHighlightMode(highlightMode, this.mPracticeMode);
    }

    public void setHighlightMode(HighlightMode highlightMode, boolean z) {
        this.mHighlightMode = highlightMode;
        if (!z) {
            loadPattern(this.mCurrentPattern, highlightMode);
        }
    }

    public HighlightMode getHighlightMode() {
        return this.mHighlightMode;
    }

    public void setPracticeMode(boolean z) {
        this.mDisplayingPracticeResult = false;
        this.mPracticeMode = z;
        if (z) {
            this.mPracticePattern = new ArrayList();
            this.mPracticePool = new HashSet();
            clearPattern(this.mCurrentPattern);
            return;
        }
        clearPattern(this.mPracticePattern);
        loadPattern(this.mCurrentPattern, this.mHighlightMode);
    }

    public boolean getPracticeMode() {
        return this.mPracticeMode;
    }

    public void setTactileFeedbackEnabled(boolean z) {
        this.mTactileFeedback = z;
    }

    public boolean getTactileFeedbackEnabled() {
        return this.mTactileFeedback;
    }

    /* loaded from: classes2.dex */
    private class CenterIterator implements Iterator<Point> {
        private Iterator<Point> nodeIterator;

        public CenterIterator(Iterator<Point> it) {
            this.nodeIterator = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.nodeIterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Point next() {
            Point next = this.nodeIterator.next();
            return ConfirmLockPatternView.this.mNodeDrawables[next.x][next.y].getCenter();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: classes2.dex */
    public static class RainbowHighlight implements HighlightMode {
        @Override // net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.HighlightMode
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            nodeDrawable1.setCustomColor(Color.HSVToColor(new float[]{(i / i2) * 360.0f, 1.0f, 1.0f}));
            return 5;
        }
    }

    /* loaded from: classes2.dex */
    public static class SuccessHighlight implements HighlightMode {
        @Override // net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.HighlightMode
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            ConfirmLockPatternView.mLinePaint.setColor(ConfirmLockPatternView.DEFAULT_PATTERN_GREEN_LINE_COLOR);
            ConfirmLockPatternView.mLinePaint.setAlpha(100);
            return 3;
        }
    }

    /* loaded from: classes2.dex */
    public static class FailureHighlight implements HighlightMode {
        @Override // net.newsoftwares.hidepicturesvideos.securitylocks.ConfirmLockPatternView.HighlightMode
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            ConfirmLockPatternView.mLinePaint.setColor(ConfirmLockPatternView.DEFAULT_PATTERN_RED_LINE_COLOR);
            ConfirmLockPatternView.mLinePaint.setAlpha(100);
            return 4;
        }
    }
}
