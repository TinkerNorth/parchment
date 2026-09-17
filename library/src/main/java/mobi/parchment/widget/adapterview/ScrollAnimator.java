// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class ScrollAnimator {
    private static final float SNAP_MILLISECONDS_PER_INCH = 100f;
    private static final float SCROLL_TO_POSITION_MILLISECONDS_PER_INCH = 25f;
    // RecyclerView's LinearSmoothScroller stretches a snap's time by this ratio so a
    // DecelerateInterpolator starts at the linear scrolling speed instead of faster.
    private static final float DECELERATION_TIME_RATIO = 0.3356f;
    // A DecelerateInterpolator's curve, 1 - (1 - t)^2, starts twice as steep as a straight line
    // through the same distance, so a landing that is to begin at the seek's speed takes twice
    // the seek's time.
    private static final float LANDING_TIME_RATIO = 0.5f;
    private static final int MAX_SNAP_DURATION_MILLISECONDS = 500;

    private final boolean mIsVertical;
    private final Scroller mDeceleratingScroller;
    private final Scroller mLinearScroller;
    private final float mSnapMillisecondsPerPixel;
    private final float mScrollToPositionMillisecondsPerPixel;
    // The scroller the last animation was started on; every accessor reads it, so a seek and a
    // snap never have to agree on one curve.
    private Scroller mActiveScroller;

    public ScrollAnimator(final Context context, final boolean isVertical) {
        mIsVertical = isVertical;
        mDeceleratingScroller = new Scroller(context, new DecelerateInterpolator());
        mLinearScroller = new Scroller(context, new LinearInterpolator());
        mActiveScroller = mDeceleratingScroller;
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mSnapMillisecondsPerPixel = SNAP_MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
        mScrollToPositionMillisecondsPerPixel =
                SCROLL_TO_POSITION_MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }

    public void snapTo(final int scrollDistance) {
        final int duration = getSnapDuration(scrollDistance);
        mActiveScroller = mDeceleratingScroller;
        startScroll(scrollDistance, duration);
    }

    public void seekBy(final int scrollDistance) {
        final int duration = getSeekDuration(scrollDistance);
        mActiveScroller = mLinearScroller;
        startScroll(scrollDistance, duration);
    }

    public void landBy(final int scrollDistance) {
        final int duration = getLandingDuration(scrollDistance);
        mActiveScroller = mDeceleratingScroller;
        startScroll(scrollDistance, duration);
    }

    private void startScroll(final int scrollDistance, final int duration) {
        if (mIsVertical) mActiveScroller.startScroll(0, 0, 0, scrollDistance, duration);
        else mActiveScroller.startScroll(0, 0, scrollDistance, 0, duration);
    }

    private int getSnapDuration(final int scrollDistance) {
        final float scrollTime = Math.abs(scrollDistance) * mSnapMillisecondsPerPixel;
        final int duration = (int) Math.ceil(scrollTime / DECELERATION_TIME_RATIO);
        return Math.min(duration, MAX_SNAP_DURATION_MILLISECONDS);
    }

    private int getSeekDuration(final int scrollDistance) {
        final float scrollTime = getScrollToPositionTime(scrollDistance);
        return (int) Math.ceil(scrollTime);
    }

    private int getLandingDuration(final int scrollDistance) {
        final float scrollTime = getScrollToPositionTime(scrollDistance);
        return (int) Math.ceil(scrollTime / LANDING_TIME_RATIO);
    }

    private float getScrollToPositionTime(final int scrollDistance) {
        final int distance = Math.abs(scrollDistance);
        return distance * mScrollToPositionMillisecondsPerPixel;
    }

    public int getDuration() {
        return mActiveScroller.getDuration();
    }

    public boolean isFinished() {
        return mActiveScroller.isFinished();
    }

    public void forceFinished(final boolean finished) {
        mActiveScroller.forceFinished(finished);
    }

    public boolean computeScrollOffset() {
        return mActiveScroller.computeScrollOffset();
    }

    public int getCurrrentOffset() {
        if (mIsVertical) return mActiveScroller.getCurrY();
        else return mActiveScroller.getCurrX();
    }

    public int getFinalOffset() {
        if (mIsVertical) return mActiveScroller.getFinalY();
        else return mActiveScroller.getFinalX();
    }

    public int getRemainingDistance() {
        final int finalOffset = getFinalOffset();
        final int currentOffset = getCurrrentOffset();
        final int remaining = finalOffset - currentOffset;
        return Math.abs(remaining);
    }

    public void setFinalOffset(final int finalOffset) {
        if (mIsVertical) mActiveScroller.setFinalY(finalOffset);
        else mActiveScroller.setFinalX(finalOffset);
    }

    public void flingBy(final float velocityX, final float velocityY) {
        mActiveScroller = mDeceleratingScroller;
        if (mIsVertical)
            mActiveScroller.fling(
                    0, 0, 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        else
            mActiveScroller.fling(
                    0, 0, (int) velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
    }
}
