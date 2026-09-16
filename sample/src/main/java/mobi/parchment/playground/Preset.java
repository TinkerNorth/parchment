// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mobi.parchment.sample.R;

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

    public static List<Preset> in(final Group group) {
        final List<Preset> presets = new ArrayList<>();
        for (final Preset preset : values()) {
            final boolean isInGroup = preset.mGroup == group;
            if (isInGroup) {
                presets.add(preset);
            }
        }
        return Collections.unmodifiableList(presets);
    }
}
