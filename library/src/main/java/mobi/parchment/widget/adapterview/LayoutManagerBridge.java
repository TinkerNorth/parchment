// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.view.View;
import android.view.ViewGroup;

public class LayoutManagerBridge {

    private final LayoutManager<?> mLayoutManager;

    public LayoutManagerBridge(final LayoutManager<?> layoutManager) {
        mLayoutManager = layoutManager;
    }

    public int onSingleTapUp(final ViewGroup viewGroup, final View view) {
        if (mLayoutManager == null) return 0;

        final boolean requestLayout = mLayoutManager.setSelected(view);
        if (requestLayout) viewGroup.requestLayout();

        if (!mLayoutManager.isSnapToPosition()) return 0;

        final int size = mLayoutManager.getViewGroupSize(viewGroup);
        final int distance = mLayoutManager.getSnapToPixelDistance(size, view);
        return distance;
    }

    public int snapTo(final ViewGroup viewGroup) {
        if (mLayoutManager == null) return 0;

        if (!mLayoutManager.isSnapToPosition()) return 0;

        return mLayoutManager.snapTo(viewGroup);
    }

    public int getViewPagerScrollDistance(final float velocityX, final float velocityY) {
        if (mLayoutManager == null) return 0;
        final Move move = getMove(mLayoutManager.isVerticalScroll() ? velocityY : velocityX);
        final int viewPagerScrollDistance = mLayoutManager.getViewPagerScrollDistance(move);
        return viewPagerScrollDistance;
    }

    private Move getMove(float velocityX) {
        if (velocityX < 0) return Move.forward;
        else if (velocityX > 0) return Move.back;

        return Move.none;
    }

    public void setAnimationStoppedListener(
            final AnimationStoppedListener animationStoppedListener) {
        mLayoutManager.setAnimationStoppedListener(animationStoppedListener);
    }

    public float getScrollDisplacement(final float distanceX, final float distanceY) {
        if (mLayoutManager == null) return 0;

        if (mLayoutManager.isVerticalScroll()) return -distanceY;
        return -distanceX;
    }
}
