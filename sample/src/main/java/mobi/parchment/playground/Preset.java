// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

/**
 * The high-level items on the menu. Each is the playground pre-filled to show one thing off: a view
 * at its most typical, or a behaviour switched on in the view that shows it best.
 */
public enum Preset {
    listView(
            Group.views,
            R.string.preset_list_view,
            new PlaygroundOptions.Builder()
                    .snapToPosition(true)
                    .snapPosition(SnapPositionOption.start)),
    gridView(
            Group.views,
            R.string.preset_grid_view,
            new PlaygroundOptions.Builder()
                    .viewKind(ViewKind.gridView)
                    .orientation(OrientationOption.vertical)
                    .hasDivider(true)),
    gridPatternView(
            Group.views,
            R.string.preset_grid_pattern_view,
            new PlaygroundOptions.Builder().viewKind(ViewKind.gridPatternView).hasDivider(true)),
    viewPager(
            Group.features,
            R.string.preset_view_pager,
            new PlaygroundOptions.Builder()
                    .orientation(OrientationOption.vertical)
                    .snapToPosition(true)
                    .snapPosition(SnapPositionOption.start)
                    .isViewPager(true)
                    .isCircularScroll(true)),
    snapping(
            Group.features,
            R.string.preset_snapping,
            new PlaygroundOptions.Builder()
                    .snapToPosition(true)
                    .snapPosition(SnapPositionOption.center)),
    circularScroll(
            Group.features,
            R.string.preset_circular_scroll,
            new PlaygroundOptions.Builder().isCircularScroll(true)),
    dividers(
            Group.features,
            R.string.preset_dividers,
            new PlaygroundOptions.Builder()
                    .viewKind(ViewKind.gridPatternView)
                    .orientation(OrientationOption.vertical)
                    .cellSpacing(CellSpacingOption.small)
                    .hasDivider(true)
                    .ratio(RatioOption.square)
                    .pattern(PatternOption.hero)),
    selection(
            Group.features,
            R.string.preset_selection,
            new PlaygroundOptions.Builder()
                    .snapToPosition(true)
                    .snapPosition(SnapPositionOption.center)
                    .selectOnSnap(true)
                    .selectWhileScrolling(true)),
    scrollListener(
            Group.features, R.string.preset_scroll_listener, new PlaygroundOptions.Builder());

    /** The menu's sections, in order. */
    public enum Group {
        views(R.string.menu_views),
        features(R.string.menu_features);

        private final int mTitleResourceId;

        Group(final int titleResourceId) {
            mTitleResourceId = titleResourceId;
        }

        public int getTitleResourceId() {
            return mTitleResourceId;
        }
    }

    private final Group mGroup;
    private final int mTitleResourceId;
    private final PlaygroundOptions mOptions;

    Preset(final Group group, final int titleResourceId, final PlaygroundOptions.Builder options) {
        mGroup = group;
        mTitleResourceId = titleResourceId;
        mOptions = options.build();
    }

    public Group getGroup() {
        return mGroup;
    }

    public int getTitleResourceId() {
        return mTitleResourceId;
    }

    public PlaygroundOptions getOptions() {
        return mOptions;
    }
}
