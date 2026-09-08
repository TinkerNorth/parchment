// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class ScrollAnimator {
    private static final float SNAP_MILLISECONDS_PER_INCH = 100f;
    // RecyclerView's LinearSmoothScroller stretches a snap's time by this ratio so a
    // DecelerateInterpolator starts at the linear scrolling speed instead of faster.
    private static final float DECELERATION_TIME_RATIO = 0.3356f;
    private static final int MAX_SNAP_DURATION_MILLISECONDS = 500;

    private final boolean mIsVertical;
    private final Scroller mScroller;
    private final float mSnapMillisecondsPerPixel;

    public ScrollAnimator(final Context context, final boolean isVertical) {
        mIsVertical = isVertical;
        mScroller = new Scroller(context, new DecelerateInterpolator());
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mSnapMillisecondsPerPixel = SNAP_MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }

    public void snapTo(final int scrollDistance) {
        final int duration = getSnapDuration(scrollDistance);
        if (mIsVertical) mScroller.startScroll(0, 0, 0, scrollDistance, duration);
        else mScroller.startScroll(0, 0, scrollDistance, 0, duration);
    }

    private int getSnapDuration(final int scrollDistance) {
        final float scrollTime = Math.abs(scrollDistance) * mSnapMillisecondsPerPixel;
        final int duration = (int) Math.ceil(scrollTime / DECELERATION_TIME_RATIO);
        return Math.min(duration, MAX_SNAP_DURATION_MILLISECONDS);
    }

    public int getDuration() {
        return mScroller.getDuration();
    }

    public boolean isFinished() {
        return mScroller.isFinished();
    }

    public void forceFinished(final boolean finished) {
        mScroller.forceFinished(finished);
    }

    public boolean computeScrollOffset() {
        return mScroller.computeScrollOffset();
    }

    public int getCurrrentOffset() {
        if (mIsVertical) return mScroller.getCurrY();
        else return mScroller.getCurrX();
    }

    public void flingBy(final float velocityX, final float velocityY) {
        if (mIsVertical)
            mScroller.fling(0, 0, 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        else mScroller.fling(0, 0, (int) velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
    }
}
