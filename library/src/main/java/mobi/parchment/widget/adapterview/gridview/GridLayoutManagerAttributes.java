// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridview;

import mobi.parchment.widget.adapterview.LayoutManagerAttributes;
import mobi.parchment.widget.adapterview.Orientation;
import mobi.parchment.widget.adapterview.SnapPosition;

public class GridLayoutManagerAttributes extends LayoutManagerAttributes {
    private final int mNumberOfViewsPerCell;
    private final Orientation mOrientation;
    private final boolean mIsLeft;
    private final boolean mIsRight;
    private final boolean mIsTop;
    private final boolean mIsBottom;

    public GridLayoutManagerAttributes(
            final int numberOfViewsPerCell,
            final boolean isCircularScroll,
            final boolean snapToPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final SnapPosition snapPosition,
            final int cellSpacing,
            final boolean selectOnSnap,
            final boolean selectWhileScrolling,
            final boolean isVertical,
            final boolean isTop,
            final boolean isBottom,
            final boolean isLeft,
            final boolean isRight) {
        super(
                isCircularScroll,
                snapToPosition,
                isViewPager,
                viewPagerInterval,
                snapPosition,
                cellSpacing,
                selectOnSnap,
                selectWhileScrolling,
                isVertical);
        mNumberOfViewsPerCell = numberOfViewsPerCell;
        if (isVertical) {
            mOrientation = Orientation.vertical;
        } else {
            mOrientation = Orientation.horizontal;
        }
        mIsTop = isTop;
        mIsBottom = isBottom;
        mIsLeft = isLeft;
        mIsRight = isRight;
    }

    public int getNumberOfViewsPerCell() {
        return mNumberOfViewsPerCell;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public boolean isTop() {
        return mIsTop;
    }

    public boolean isBottom() {
        return mIsBottom;
    }

    public boolean isLeft() {
        return mIsLeft;
    }

    public boolean isRight() {
        return mIsRight;
    }
}
