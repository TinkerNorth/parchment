// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import mobi.parchment.widget.adapterview.utilities.ViewGroupUtilities;

public class ScrollDirectionManager {

    private final boolean mIsVerticalScroll;

    public ScrollDirectionManager(final boolean isVerticalScroll) {
        mIsVerticalScroll = isVerticalScroll;
    }

    public ScrollDirectionManager(final LayoutManagerAttributes attributes) {
        mIsVerticalScroll = attributes.isVertical();
    }

    public boolean isVerticalScroll() {
        return mIsVerticalScroll;
    }

    public int getViewStart(final View view) {
        if (isVerticalScroll()) return view.getTop();
        return view.getLeft();
    }

    public int getViewEnd(final View view) {
        if (isVerticalScroll()) return view.getBottom();
        return view.getRight();
    }

    public int getViewSize(final View view) {
        if (isVerticalScroll()) return view.getMeasuredHeight();
        return view.getMeasuredWidth();
    }

    public int getViewBreadth(final View view) {
        if (isVerticalScroll()) return view.getMeasuredWidth();
        return view.getMeasuredHeight();
    }

    public int getViewGroupSize(final ViewGroup viewGroup) {
        if (isVerticalScroll()) return ViewGroupUtilities.getViewGroupMeasuredHeight(viewGroup);
        return ViewGroupUtilities.getViewGroupMeasuredWidth(viewGroup);
    }

    public int getDrawSize(int left, int top, int right, int bottom) {
        if (isVerticalScroll()) return bottom - top;
        return right - left;
    }

    public int getDrawBreadth(int left, int top, int right, int bottom) {
        if (isVerticalScroll()) return right - left;
        return bottom - top;
    }

    public int getDrawableSize(final Drawable drawable) {
        if (isVerticalScroll()) return drawable.getIntrinsicHeight();
        return drawable.getIntrinsicWidth();
    }

    public void setDrawBounds(
            final Rect bounds,
            final int start,
            final int end,
            final int breadthStart,
            final int breadthEnd) {
        if (isVerticalScroll()) {
            setVerticalDrawBounds(bounds, start, end, breadthStart, breadthEnd);
        } else {
            setHorizontalDrawBounds(bounds, start, end, breadthStart, breadthEnd);
        }
    }

    private static void setVerticalDrawBounds(
            final Rect bounds,
            final int start,
            final int end,
            final int breadthStart,
            final int breadthEnd) {
        bounds.set(breadthStart, start, breadthEnd, end);
    }

    private static void setHorizontalDrawBounds(
            final Rect bounds,
            final int start,
            final int end,
            final int breadthStart,
            final int breadthEnd) {
        bounds.set(start, breadthStart, end, breadthEnd);
    }
}
