// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

public class LayoutManagerAttributes {

    private static class DefaultValues {
        private static final SnapPosition SNAP_POSITION = SnapPosition.center;
        private static final int VIEW_PAGER_INTERVAL = 0;
    }

    private final boolean mIsCircularScroll;
    private final boolean mIsViewPager;
    private final boolean mIsVertical;
    private final int mViewPagerInterval;
    private final boolean mSnapToPosition;
    private final SnapPosition mSnapPosition;
    private final int mCellSpacing;
    private final boolean mSelectOnSnap;
    private final boolean mSelectWhileScrolling;

    public LayoutManagerAttributes(
            final boolean isCircularScroll,
            final boolean snapToPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final SnapPosition snapPosition,
            final int cellSpacing,
            final boolean selectOnSnap,
            final boolean selectWhileScrolling,
            final boolean isVertical) {
        super();

        if (snapPosition != null) mSnapPosition = snapPosition;
        else mSnapPosition = DefaultValues.SNAP_POSITION;

        mViewPagerInterval = Math.max(viewPagerInterval, DefaultValues.VIEW_PAGER_INTERVAL);
        mIsViewPager = isViewPager;
        mIsCircularScroll = isCircularScroll;
        mSnapToPosition = snapToPosition;
        mCellSpacing = cellSpacing;
        mSelectOnSnap = selectOnSnap;
        mIsVertical = isVertical;
        mSelectWhileScrolling = selectWhileScrolling;
    }

    public boolean isCircularScroll() {
        return mIsCircularScroll;
    }

    public boolean isSnapToPosition() {
        return mSnapToPosition;
    }

    public SnapPosition getSnapPosition() {
        return mSnapPosition;
    }

    public boolean selectOnSnap() {
        return mSelectOnSnap;
    }

    public int getCellSpacing() {
        return mCellSpacing;
    }

    public boolean isViewPager() {
        return mIsViewPager;
    }

    public int getViewPagerInterval() {
        return mViewPagerInterval;
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    public boolean isSnapPositionOnScreen() {
        return mSnapPosition == SnapPosition.onScreen;
    }

    public boolean selectWhileScrolling() {
        return mSelectWhileScrolling;
    }
}
