// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.os.Bundle;

/** One complete configuration of a Parchment view: the view, and every attribute it reads. */
public final class PlaygroundOptions {

    private static final String VIEW_KIND = "viewKind";
    private static final String ORIENTATION = "orientation";
    private static final String CELL_SPACING = "cellSpacing";
    private static final String SNAP_TO_POSITION = "snapToPosition";
    private static final String SNAP_POSITION = "snapPosition";
    private static final String IS_CIRCULAR_SCROLL = "isCircularScroll";
    private static final String IS_VIEW_PAGER = "isViewPager";
    private static final String VIEW_PAGER_INTERVAL = "viewPagerInterval";
    private static final String SELECT_ON_SNAP = "selectOnSnap";
    private static final String SELECT_WHILE_SCROLLING = "selectWhileScrolling";
    private static final String HAS_DIVIDER = "hasDivider";
    private static final String VIEWS_PER_CELL = "viewsPerCell";
    private static final String GRAVITY = "gravity";
    private static final String RATIO = "ratio";
    private static final String PATTERN = "pattern";

    private final ViewKind mViewKind;
    private final OrientationOption mOrientation;
    private final CellSpacingOption mCellSpacing;
    private final boolean mSnapToPosition;
    private final SnapPositionOption mSnapPosition;
    private final boolean mIsCircularScroll;
    private final boolean mIsViewPager;
    private final ViewPagerIntervalOption mViewPagerInterval;
    private final boolean mSelectOnSnap;
    private final boolean mSelectWhileScrolling;
    private final boolean mHasDivider;
    private final ViewsPerCellOption mViewsPerCell;
    private final GravityOption mGravity;
    private final RatioOption mRatio;
    private final PatternOption mPattern;

    private PlaygroundOptions(final Builder builder) {
        mViewKind = builder.mViewKind;
        mOrientation = builder.mOrientation;
        mCellSpacing = builder.mCellSpacing;
        mSnapToPosition = builder.mSnapToPosition;
        mSnapPosition = builder.mSnapPosition;
        mIsCircularScroll = builder.mIsCircularScroll;
        mIsViewPager = builder.mIsViewPager;
        mViewPagerInterval = builder.mViewPagerInterval;
        mSelectOnSnap = builder.mSelectOnSnap;
        mSelectWhileScrolling = builder.mSelectWhileScrolling;
        mHasDivider = builder.mHasDivider;
        mViewsPerCell = builder.mViewsPerCell;
        mGravity = builder.mGravity;
        mRatio = builder.mRatio;
        mPattern = builder.mPattern;
    }

    public ViewKind getViewKind() {
        return mViewKind;
    }

    public OrientationOption getOrientation() {
        return mOrientation;
    }

    public CellSpacingOption getCellSpacing() {
        return mCellSpacing;
    }

    public boolean isSnapToPosition() {
        return mSnapToPosition;
    }

    public SnapPositionOption getSnapPosition() {
        return mSnapPosition;
    }

    public boolean isCircularScroll() {
        return mIsCircularScroll;
    }

    public boolean isViewPager() {
        return mIsViewPager;
    }

    public ViewPagerIntervalOption getViewPagerInterval() {
        return mViewPagerInterval;
    }

    public boolean selectOnSnap() {
        return mSelectOnSnap;
    }

    public boolean selectWhileScrolling() {
        return mSelectWhileScrolling;
    }

    public boolean hasDivider() {
        return mHasDivider;
    }

    public ViewsPerCellOption getViewsPerCell() {
        return mViewsPerCell;
    }

    public GravityOption getGravity() {
        return mGravity;
    }

    public RatioOption getRatio() {
        return mRatio;
    }

    public PatternOption getPattern() {
        return mPattern;
    }

    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString(VIEW_KIND, mViewKind.name());
        bundle.putString(ORIENTATION, mOrientation.name());
        bundle.putString(CELL_SPACING, mCellSpacing.name());
        bundle.putBoolean(SNAP_TO_POSITION, mSnapToPosition);
        bundle.putString(SNAP_POSITION, mSnapPosition.name());
        bundle.putBoolean(IS_CIRCULAR_SCROLL, mIsCircularScroll);
        bundle.putBoolean(IS_VIEW_PAGER, mIsViewPager);
        bundle.putString(VIEW_PAGER_INTERVAL, mViewPagerInterval.name());
        bundle.putBoolean(SELECT_ON_SNAP, mSelectOnSnap);
        bundle.putBoolean(SELECT_WHILE_SCROLLING, mSelectWhileScrolling);
        bundle.putBoolean(HAS_DIVIDER, mHasDivider);
        bundle.putString(VIEWS_PER_CELL, mViewsPerCell.name());
        bundle.putString(GRAVITY, mGravity.name());
        bundle.putString(RATIO, mRatio.name());
        bundle.putString(PATTERN, mPattern.name());
        return bundle;
    }

    public static PlaygroundOptions fromBundle(final Bundle bundle) {
        return new Builder()
                .viewKind(ViewKind.valueOf(bundle.getString(VIEW_KIND)))
                .orientation(OrientationOption.valueOf(bundle.getString(ORIENTATION)))
                .cellSpacing(CellSpacingOption.valueOf(bundle.getString(CELL_SPACING)))
                .snapToPosition(bundle.getBoolean(SNAP_TO_POSITION))
                .snapPosition(SnapPositionOption.valueOf(bundle.getString(SNAP_POSITION)))
                .isCircularScroll(bundle.getBoolean(IS_CIRCULAR_SCROLL))
                .isViewPager(bundle.getBoolean(IS_VIEW_PAGER))
                .viewPagerInterval(
                        ViewPagerIntervalOption.valueOf(bundle.getString(VIEW_PAGER_INTERVAL)))
                .selectOnSnap(bundle.getBoolean(SELECT_ON_SNAP))
                .selectWhileScrolling(bundle.getBoolean(SELECT_WHILE_SCROLLING))
                .hasDivider(bundle.getBoolean(HAS_DIVIDER))
                .viewsPerCell(ViewsPerCellOption.valueOf(bundle.getString(VIEWS_PER_CELL)))
                .gravity(GravityOption.valueOf(bundle.getString(GRAVITY)))
                .ratio(RatioOption.valueOf(bundle.getString(RATIO)))
                .pattern(PatternOption.valueOf(bundle.getString(PATTERN)))
                .build();
    }

    /** Starts from a horizontal ListView with spaced cells and every behaviour switched off. */
    public static final class Builder {

        private ViewKind mViewKind = ViewKind.listView;
        private OrientationOption mOrientation = OrientationOption.horizontal;
        private CellSpacingOption mCellSpacing = CellSpacingOption.large;
        private boolean mSnapToPosition;
        private SnapPositionOption mSnapPosition = SnapPositionOption.onScreen;
        private boolean mIsCircularScroll;
        private boolean mIsViewPager;
        private ViewPagerIntervalOption mViewPagerInterval = ViewPagerIntervalOption.viewport;
        private boolean mSelectOnSnap;
        private boolean mSelectWhileScrolling;
        private boolean mHasDivider;
        private ViewsPerCellOption mViewsPerCell = ViewsPerCellOption.two;
        private GravityOption mGravity = GravityOption.top;
        private RatioOption mRatio = RatioOption.golden;
        private PatternOption mPattern = PatternOption.magazine;

        public Builder viewKind(final ViewKind viewKind) {
            mViewKind = viewKind;
            return this;
        }

        public Builder orientation(final OrientationOption orientation) {
            mOrientation = orientation;
            return this;
        }

        public Builder cellSpacing(final CellSpacingOption cellSpacing) {
            mCellSpacing = cellSpacing;
            return this;
        }

        public Builder snapToPosition(final boolean snapToPosition) {
            mSnapToPosition = snapToPosition;
            return this;
        }

        public Builder snapPosition(final SnapPositionOption snapPosition) {
            mSnapPosition = snapPosition;
            return this;
        }

        public Builder isCircularScroll(final boolean isCircularScroll) {
            mIsCircularScroll = isCircularScroll;
            return this;
        }

        public Builder isViewPager(final boolean isViewPager) {
            mIsViewPager = isViewPager;
            return this;
        }

        public Builder viewPagerInterval(final ViewPagerIntervalOption viewPagerInterval) {
            mViewPagerInterval = viewPagerInterval;
            return this;
        }

        public Builder selectOnSnap(final boolean selectOnSnap) {
            mSelectOnSnap = selectOnSnap;
            return this;
        }

        public Builder selectWhileScrolling(final boolean selectWhileScrolling) {
            mSelectWhileScrolling = selectWhileScrolling;
            return this;
        }

        public Builder hasDivider(final boolean hasDivider) {
            mHasDivider = hasDivider;
            return this;
        }

        public Builder viewsPerCell(final ViewsPerCellOption viewsPerCell) {
            mViewsPerCell = viewsPerCell;
            return this;
        }

        public Builder gravity(final GravityOption gravity) {
            mGravity = gravity;
            return this;
        }

        public Builder ratio(final RatioOption ratio) {
            mRatio = ratio;
            return this;
        }

        public Builder pattern(final PatternOption pattern) {
            mPattern = pattern;
            return this;
        }

        public PlaygroundOptions build() {
            return new PlaygroundOptions(this);
        }
    }
}
