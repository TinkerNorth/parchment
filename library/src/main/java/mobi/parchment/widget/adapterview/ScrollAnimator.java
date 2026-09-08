// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.widget.Scroller;

/** Created by Emir Hasanbegovic */
public class ScrollAnimator {
    private final boolean mIsVertical;
    private final Scroller mScroller;

    public ScrollAnimator(final Context context, final boolean isVertical) {
        mIsVertical = isVertical;
        mScroller = new Scroller(context);
    }

    public void startScroll(final int scrollDistance, final int animationDuration) {
        if (mIsVertical) mScroller.startScroll(0, 0, 0, scrollDistance, animationDuration);
        else mScroller.startScroll(0, 0, scrollDistance, 0, animationDuration);
    }

    public boolean isFinished() {
        return mScroller.isFinished();
    }

    public void forceFinished(boolean finished) {
        mScroller.forceFinished(finished);
    }

    public boolean computeScrollOffset() {
        return mScroller.computeScrollOffset();
    }

    public int getCurrrentOffset() {
        if (mIsVertical) return mScroller.getCurrY();
        else return mScroller.getCurrX();
    }

    public void flingBy(float velocityX, float velocityY) {
        if (mIsVertical)
            mScroller.fling(0, 0, 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        else mScroller.fling(0, 0, (int) velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
    }
}
