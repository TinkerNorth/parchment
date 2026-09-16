// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.os.Bundle;

public final class PlaygroundOptions {

    private static final String VIEW_KIND = "viewKind";
    private static final String ORIENTATION = "orientation";
    private static final String CELL_SPACING = "cellSpacing";
    private static final String SNAP_TO_POSITION = "snapToPosition";
    private static final String SNAP_POSITION = "snapPosition";
    private static final String SCROLL_WITHIN_CONTENT = "scrollWithinContent";
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
    private final boolean mScrollWithinContent;
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
        mScrollWithinContent = builder.mScrollWithinContent;
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

    public boolean scrollWithinContent() {
        return mScrollWithinContent;
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
        bundle.putBoolean(SCROLL_WITHIN_CONTENT, mScrollWithinContent);
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
                .viewKind(read(bundle, VIEW_KIND, ViewKind.class))
                .orientation(read(bundle, ORIENTATION, OrientationOption.class))
                .cellSpacing(read(bundle, CELL_SPACING, CellSpacingOption.class))
                .snapToPosition(bundle.getBoolean(SNAP_TO_POSITION))
                .snapPosition(read(bundle, SNAP_POSITION, SnapPositionOption.class))
                .scrollWithinContent(bundle.getBoolean(SCROLL_WITHIN_CONTENT))
                .isCircularScroll(bundle.getBoolean(IS_CIRCULAR_SCROLL))
                .isViewPager(bundle.getBoolean(IS_VIEW_PAGER))
                .viewPagerInterval(read(bundle, VIEW_PAGER_INTERVAL, ViewPagerIntervalOption.class))
                .selectOnSnap(bundle.getBoolean(SELECT_ON_SNAP))
                .selectWhileScrolling(bundle.getBoolean(SELECT_WHILE_SCROLLING))
                .hasDivider(bundle.getBoolean(HAS_DIVIDER))
                .viewsPerCell(read(bundle, VIEWS_PER_CELL, ViewsPerCellOption.class))
                .gravity(read(bundle, GRAVITY, GravityOption.class))
                .ratio(read(bundle, RATIO, RatioOption.class))
                .pattern(read(bundle, PATTERN, PatternOption.class))
                .build();
    }

    private static <OPTION extends Enum<OPTION>> OPTION read(
            final Bundle bundle, final String key, final Class<OPTION> type) {
        final String name = bundle.getString(key);
        return Enum.valueOf(type, name);
    }

    public static final class Builder {

        private ViewKind mViewKind = ViewKind.listView;
        private OrientationOption mOrientation = OrientationOption.horizontal;
        private CellSpacingOption mCellSpacing = CellSpacingOption.large;
        private boolean mSnapToPosition;
        private SnapPositionOption mSnapPosition = SnapPositionOption.onScreen;
        private boolean mScrollWithinContent;
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

        public Builder scrollWithinContent(final boolean scrollWithinContent) {
            mScrollWithinContent = scrollWithinContent;
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
