// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridview;

import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class Group {

    private static final int NO_PIXEL = 0;
    private static final int FIRST_VIEW_INDEX = 0;
    private static final int SECOND_VIEW_INDEX = 1;

    private final List<View> mViews = new ArrayList<View>();

    public List<View> getViews() {
        return new ArrayList<View>(mViews);
    }

    public View getView(final int index) {
        return mViews.get(index);
    }

    private final boolean mIsVerticalScroll;

    public Group(final boolean isVerticalScroll) {
        mIsVerticalScroll = isVerticalScroll;
    }

    public int getTop() {
        if (mViews.isEmpty()) return NO_PIXEL;

        int lowestPixel = mViews.get(FIRST_VIEW_INDEX).getTop();
        for (int viewIndex = SECOND_VIEW_INDEX; viewIndex < mViews.size(); viewIndex++) {
            final int top = mViews.get(viewIndex).getTop();
            if (top < lowestPixel) lowestPixel = top;
        }
        return lowestPixel;
    }

    public int getLeft() {
        if (mViews.isEmpty()) return NO_PIXEL;

        int lowestPixel = mViews.get(FIRST_VIEW_INDEX).getLeft();
        for (int viewIndex = SECOND_VIEW_INDEX; viewIndex < mViews.size(); viewIndex++) {
            final int left = mViews.get(viewIndex).getLeft();
            if (left < lowestPixel) lowestPixel = left;
        }
        return lowestPixel;
    }

    public int getBottom() {
        if (mViews.isEmpty()) return NO_PIXEL;

        int highestPixel = mViews.get(FIRST_VIEW_INDEX).getBottom();
        for (int viewIndex = SECOND_VIEW_INDEX; viewIndex < mViews.size(); viewIndex++) {
            final int bottom = mViews.get(viewIndex).getBottom();
            if (bottom > highestPixel) highestPixel = bottom;
        }
        return highestPixel;
    }

    public int getRight() {
        if (mViews.isEmpty()) return NO_PIXEL;

        int highestPixel = mViews.get(FIRST_VIEW_INDEX).getRight();
        for (int viewIndex = SECOND_VIEW_INDEX; viewIndex < mViews.size(); viewIndex++) {
            final int right = mViews.get(viewIndex).getRight();
            if (right > highestPixel) highestPixel = right;
        }
        return highestPixel;
    }

    public int getMeasuredHeight() {
        int groupHeight = 0;
        for (final View view : mViews) {
            final int height = view.getMeasuredHeight();

            if (mIsVerticalScroll) {
                if (height > groupHeight) {
                    groupHeight = height;
                }
            } else {
                groupHeight += height;
            }
        }
        return groupHeight;
    }

    public int getMeasuredWidth() {
        int groupWidth = 0;
        for (final View view : mViews) {
            final int width = view.getMeasuredWidth();

            if (!mIsVerticalScroll) {
                if (width > groupWidth) {
                    groupWidth = width;
                }
            } else {
                groupWidth += width;
            }
        }
        return groupWidth;
    }

    public View getHorizontalScrollRepresentative() {
        return getWidestView();
    }

    private View getWidestView() {
        int fattestWidth = 0;
        View fattestView = null;

        for (final View view : mViews) {
            final int width = view.getWidth();

            if (width > fattestWidth) {
                fattestWidth = width;
                fattestView = view;
            }
        }

        return fattestView;
    }

    public View getVerticalScrollRepresentative() {
        return getTallestView();
    }

    private View getTallestView() {
        int tallestHeight = 0;
        View tallestView = null;

        for (final View view : mViews) {
            final int height = view.getHeight();

            if (height > tallestHeight) {
                tallestHeight = height;
                tallestView = view;
            }
        }

        return tallestView;
    }

    public View getLastView() {
        if (mViews.isEmpty()) {
            return null;
        }
        final int lastIndex = mViews.size() - 1;
        return mViews.get(lastIndex);
    }

    public View getFirstView() {
        if (mViews.isEmpty()) {
            return null;
        }
        return mViews.get(FIRST_VIEW_INDEX);
    }

    public void clear() {
        mViews.clear();
    }

    public int getNumberOfItems() {
        return mViews.size();
    }

    public void addView(final View view) {
        mViews.add(view);
    }

    public int getBreadth(final int cellSpacing) {
        int breadth = 0;

        for (final View view : mViews) {
            if (!mIsVerticalScroll) {
                final int height = view.getMeasuredHeight();
                breadth += height;
            } else {
                final int width = view.getMeasuredWidth();
                breadth += width;
            }
        }
        final int numberOfCellSpacings = Math.max(0, mViews.size() - 1);
        final int cellSpacingBreadth = numberOfCellSpacings * cellSpacing;

        return breadth + cellSpacingBreadth;
    }

    public boolean isEmpty() {
        return mViews.isEmpty();
    }
}
