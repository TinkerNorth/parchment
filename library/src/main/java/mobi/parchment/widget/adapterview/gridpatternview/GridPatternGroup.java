// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridpatternview;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.utilities.ViewGroupUtilities;

/** Created by Emir Hasanbegovic on 2014-03-03. */
public class GridPatternGroup {

    private final GridPatternGroupDefinition mGridPatternGroupDefinition;
    private final ViewGroup mViewGroup;
    private final boolean mIsVerticalScroll;
    private final float mStretchRatio;
    private final int mCellSpacing;
    private int mStartOffset;

    private final List<View> mViews = new ArrayList<View>();

    public GridPatternGroup(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final ViewGroup viewGroup,
            final boolean isVerticalScroll,
            final float stretchRatio,
            final int cellSpacing) {
        mGridPatternGroupDefinition = gridPatternGroupDefinition;
        mViewGroup = viewGroup;
        mIsVerticalScroll = isVerticalScroll;
        mStretchRatio = stretchRatio;
        mCellSpacing = cellSpacing;
    }

    public void setStartOffset(final int startOffset) {
        mStartOffset = startOffset;
    }

    public int getTop() {
        if (mIsVerticalScroll) {
            return mStartOffset;
        }

        return mViewGroup.getPaddingTop();
    }

    public GridPatternGroupDefinition getGridPatternGroupDefinition() {
        return mGridPatternGroupDefinition;
    }

    public int getBottom() {
        if (mIsVerticalScroll) {
            final int top = getTop();
            final int height = getHeight();
            final int bottom = top + height;
            return bottom;
        }

        final int viewGroupHeight = ViewGroupUtilities.getViewGroupMeasuredHeight(mViewGroup);
        return viewGroupHeight - mViewGroup.getPaddingBottom();
    }

    public int getLeft() {
        final View firstView = getFirstView();
        final int left = firstView.getLeft();
        return left;
    }

    public int getRight() {
        final int left = getLeft();
        final int width = getWidth();
        final int right = left + width;
        return right;
    }

    public View getLastView() {
        final int numberOfItems = getNumberOfItems();
        final int lastGridGroupPosition =
                mGridPatternGroupDefinition.getLastGridGroupPosition(numberOfItems);
        final View lastView = mViews.get(lastGridGroupPosition);
        return lastView;
    }

    public View getFirstView() {
        final int numberOfItems = getNumberOfItems();
        final int firstGridGroupPosition =
                mGridPatternGroupDefinition.getFirstGridGroupPosition(numberOfItems);
        final View firstView = mViews.get(firstGridGroupPosition);
        return firstView;
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

    public int getBreadth() {
        if (!mIsVerticalScroll) {
            return mGridPatternGroupDefinition.getGroupHeight(
                    mViewGroup, mCellSpacing, mStretchRatio);
        }

        return mGridPatternGroupDefinition.getGroupWidth(mViewGroup, mCellSpacing, mStretchRatio);
    }

    public boolean isEmpty() {
        return mViews.isEmpty();
    }

    public List<View> getViews() {
        return mViews;
    }

    public int getWidth() {
        if (mViews.isEmpty()) {
            return 0;
        }
        int left = Integer.MAX_VALUE;
        int right = 0;

        for (int index = 0; index < mViews.size(); index++) {
            final int currentLeft =
                    mGridPatternGroupDefinition.getLeftOffset(
                            mViewGroup, mCellSpacing, mStretchRatio, index);
            final int width =
                    mGridPatternGroupDefinition.getItemWidth(
                            mViewGroup, mCellSpacing, mStretchRatio, index);
            final int currentRight = currentLeft + width;

            if (left > currentLeft) {
                left = currentLeft;
            }
            if (right < currentRight) {
                right = currentRight;
            }
        }

        final int width = right - left;
        return width;
    }

    public int getHeight() {
        if (mViews.isEmpty()) {
            return 0;
        }
        int top = Integer.MAX_VALUE;
        int bottom = 0;

        for (int index = 0; index < mViews.size(); index++) {
            final int currentTop =
                    mGridPatternGroupDefinition.getTopOffset(
                            mViewGroup, mCellSpacing, mStretchRatio, index);
            final int height =
                    mGridPatternGroupDefinition.getItemHeight(
                            mViewGroup, mCellSpacing, mStretchRatio, index);
            final int currentBottom = currentTop + height;

            if (top > currentTop) {
                top = currentTop;
            }
            if (bottom < currentBottom) {
                bottom = currentBottom;
            }
        }

        final int height = bottom - top;
        return height;
    }
}
