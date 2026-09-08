// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import mobi.parchment.R;

public class Attributes {

    private static class DefaultValues {
        private static final int CELL_SPACING = 0;
        private static final int VIEW_PAGER_INTERVAL = 0;
        private static final Orientation ORIENTATION = Orientation.vertical;
        private static final boolean IS_CIRCULAR_SCROLL = false;
        private static final boolean SNAP_TO_POSITION = false;
        private static final boolean IS_VIEW_PAGER = false;
        private static final boolean SELECT_ON_SNAP = false;
        private static final SnapPosition SNAP_POSITION = SnapPosition.center;
        private static final boolean SELECT_WHILE_SCROLLING = false;
    }

    private Orientation mOrientation;
    private boolean mIsCircularScroll;
    private boolean mSnapToPosition;
    private SnapPosition mSnapPosition;
    private float mCellSpacing;
    private boolean mSelectOnSnap;
    private boolean mIsVertical;
    private boolean mSelectWhileScrolling;

    private boolean mIsViewPager;
    private int mViewPagerInterval;

    public Attributes(final Context context, final AttributeSet attributeSet) {
        initialize(context, attributeSet);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            final TypedArray typedArray =
                    context.getTheme()
                            .obtainStyledAttributes(attributeSet, R.styleable.ListView, 0, 0);

            try {
                final int snapPositionOrdinal =
                        typedArray.getInteger(
                                R.styleable.ListView_snapPosition, SnapPosition.onScreen.ordinal());
                final SnapPosition[] snapPositionValues = SnapPosition.values();
                if (snapPositionValues.length > snapPositionOrdinal && snapPositionOrdinal >= 0)
                    mSnapPosition = SnapPosition.values()[snapPositionOrdinal];
                else mSnapPosition = DefaultValues.SNAP_POSITION;

                mIsViewPager =
                        typedArray.getBoolean(
                                R.styleable.ListView_isViewPager, DefaultValues.IS_VIEW_PAGER);
                mIsCircularScroll =
                        typedArray.getBoolean(
                                R.styleable.ListView_isCircularScroll,
                                DefaultValues.IS_CIRCULAR_SCROLL);
                mSnapToPosition =
                        typedArray.getBoolean(
                                R.styleable.ListView_snapToPosition,
                                DefaultValues.SNAP_TO_POSITION);
                mCellSpacing =
                        typedArray.getDimensionPixelSize(
                                R.styleable.ListView_cellSpacing, DefaultValues.CELL_SPACING);
                mSelectOnSnap =
                        typedArray.getBoolean(
                                R.styleable.ListView_selectOnSnap, DefaultValues.SELECT_ON_SNAP);
                mSelectWhileScrolling =
                        typedArray.getBoolean(
                                R.styleable.ListView_selectWhileScrolling,
                                DefaultValues.SELECT_WHILE_SCROLLING);

                final int orientationOrdinal =
                        typedArray.getInteger(
                                R.styleable.ListView_orientation,
                                DefaultValues.ORIENTATION.ordinal());
                final Orientation[] orientationValues = Orientation.values();
                if (orientationValues.length > orientationOrdinal && orientationOrdinal >= 0) {
                    mOrientation = Orientation.values()[orientationOrdinal];
                } else {
                    mOrientation = DefaultValues.ORIENTATION;
                }

            } finally {
                typedArray.recycle();
            }
        } else {
            mViewPagerInterval = DefaultValues.VIEW_PAGER_INTERVAL;
            mIsViewPager = DefaultValues.IS_VIEW_PAGER;
            mSnapPosition = DefaultValues.SNAP_POSITION;
            mIsCircularScroll = DefaultValues.IS_CIRCULAR_SCROLL;
            mSnapToPosition = DefaultValues.SNAP_TO_POSITION;

            mCellSpacing = DefaultValues.CELL_SPACING;
            mSelectOnSnap = DefaultValues.SELECT_ON_SNAP;
            mSelectWhileScrolling = DefaultValues.SELECT_WHILE_SCROLLING;
            mOrientation = DefaultValues.ORIENTATION;
        }

        final boolean isVertical = mOrientation == Orientation.vertical;
        mIsVertical = isVertical;
    }

    public boolean isCircularScroll() {
        return mIsCircularScroll;
    }

    public boolean isViewPager() {
        return mIsViewPager;
    }

    public boolean isSnapToPosition() {
        return mSnapToPosition;
    }

    public SnapPosition getSnapPosition() {
        return mSnapPosition;
    }

    public float getCellSpacing() {
        return mCellSpacing;
    }

    public int getViewPagerInterval() {
        return mViewPagerInterval;
    }

    public boolean selectOnSnap() {
        return mSelectOnSnap;
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    public boolean selectWhileScrolling() {
        return mSelectWhileScrolling;
    }

    protected void setIsVertical(final boolean isVertical) {
        mIsVertical = isVertical;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }
}
