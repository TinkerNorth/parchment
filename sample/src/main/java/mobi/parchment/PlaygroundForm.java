// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import mobi.parchment.playground.CellSpacingOption;
import mobi.parchment.playground.GravityOption;
import mobi.parchment.playground.OrientationOption;
import mobi.parchment.playground.PatternOption;
import mobi.parchment.playground.PlaygroundOptions;
import mobi.parchment.playground.RadioOption;
import mobi.parchment.playground.RatioOption;
import mobi.parchment.playground.SnapPositionOption;
import mobi.parchment.playground.ViewKind;
import mobi.parchment.playground.ViewPagerIntervalOption;
import mobi.parchment.playground.ViewsPerCellOption;
import mobi.parchment.sample.R;

final class PlaygroundForm {

    private final RadioGroup mViewKind;
    private final RadioGroup mOrientation;
    private final RadioGroup mCellSpacing;
    private final CheckBox mDivider;
    private final CheckBox mSnapToPosition;
    private final RadioGroup mSnapPosition;
    private final CheckBox mCircularScroll;
    private final CheckBox mViewPager;
    private final RadioGroup mViewPagerInterval;
    private final CheckBox mSelectOnSnap;
    private final CheckBox mSelectWhileScrolling;
    private final View mGridViewSection;
    private final RadioGroup mViewsPerCell;
    private final RadioGroup mGravity;
    private final View mGridPatternViewSection;
    private final RadioGroup mRatio;
    private final RadioGroup mPattern;

    PlaygroundForm(final Activity activity) {
        mViewKind = activity.findViewById(R.id.playground_view_kind);
        mOrientation = activity.findViewById(R.id.playground_orientation);
        mCellSpacing = activity.findViewById(R.id.playground_cell_spacing);
        mDivider = activity.findViewById(R.id.playground_divider);
        mSnapToPosition = activity.findViewById(R.id.playground_snap_to_position);
        mSnapPosition = activity.findViewById(R.id.playground_snap_position);
        mCircularScroll = activity.findViewById(R.id.playground_circular_scroll);
        mViewPager = activity.findViewById(R.id.playground_view_pager);
        mViewPagerInterval = activity.findViewById(R.id.playground_view_pager_interval);
        mSelectOnSnap = activity.findViewById(R.id.playground_select_on_snap);
        mSelectWhileScrolling = activity.findViewById(R.id.playground_select_while_scrolling);
        mGridViewSection = activity.findViewById(R.id.playground_grid_view_section);
        mViewsPerCell = activity.findViewById(R.id.playground_views_per_cell);
        mGravity = activity.findViewById(R.id.playground_gravity);
        mGridPatternViewSection = activity.findViewById(R.id.playground_grid_pattern_view_section);
        mRatio = activity.findViewById(R.id.playground_ratio);
        mPattern = activity.findViewById(R.id.playground_pattern);
        mViewKind.setOnCheckedChangeListener(new ShowSectionsForViewKind(this));
    }

    void show(final PlaygroundOptions options) {
        check(mViewKind, options.getViewKind());
        check(mOrientation, options.getOrientation());
        check(mCellSpacing, options.getCellSpacing());
        mDivider.setChecked(options.hasDivider());
        mSnapToPosition.setChecked(options.isSnapToPosition());
        check(mSnapPosition, options.getSnapPosition());
        mCircularScroll.setChecked(options.isCircularScroll());
        mViewPager.setChecked(options.isViewPager());
        check(mViewPagerInterval, options.getViewPagerInterval());
        mSelectOnSnap.setChecked(options.selectOnSnap());
        mSelectWhileScrolling.setChecked(options.selectWhileScrolling());
        check(mViewsPerCell, options.getViewsPerCell());
        check(mGravity, options.getGravity());
        check(mRatio, options.getRatio());
        check(mPattern, options.getPattern());
    }

    PlaygroundOptions read() {
        return new PlaygroundOptions.Builder()
                .viewKind(readViewKind())
                .orientation(checked(mOrientation, OrientationOption.values()))
                .cellSpacing(checked(mCellSpacing, CellSpacingOption.values()))
                .hasDivider(mDivider.isChecked())
                .snapToPosition(mSnapToPosition.isChecked())
                .snapPosition(checked(mSnapPosition, SnapPositionOption.values()))
                .isCircularScroll(mCircularScroll.isChecked())
                .isViewPager(mViewPager.isChecked())
                .viewPagerInterval(checked(mViewPagerInterval, ViewPagerIntervalOption.values()))
                .selectOnSnap(mSelectOnSnap.isChecked())
                .selectWhileScrolling(mSelectWhileScrolling.isChecked())
                .viewsPerCell(checked(mViewsPerCell, ViewsPerCellOption.values()))
                .gravity(checked(mGravity, GravityOption.values()))
                .ratio(checked(mRatio, RatioOption.values()))
                .pattern(checked(mPattern, PatternOption.values()))
                .build();
    }

    void showSections() {
        final ViewKind viewKind = readViewKind();
        final boolean isGridView = viewKind == ViewKind.gridView;
        final boolean isGridPatternView = viewKind == ViewKind.gridPatternView;
        setVisible(mGridViewSection, isGridView);
        setVisible(mGridPatternViewSection, isGridPatternView);
    }

    private ViewKind readViewKind() {
        return checked(mViewKind, ViewKind.values());
    }

    private static void check(final RadioGroup group, final RadioOption option) {
        group.check(option.getRadioButtonId());
    }

    private static <OPTION extends RadioOption> OPTION checked(
            final RadioGroup group, final OPTION[] options) {
        final int checkedId = group.getCheckedRadioButtonId();
        for (final OPTION option : options) {
            if (option.getRadioButtonId() == checkedId) {
                return option;
            }
        }
        throw new IllegalStateException("Nothing checked in " + group.getId());
    }

    private static void setVisible(final View view, final boolean isVisible) {
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private static final class ShowSectionsForViewKind
            implements RadioGroup.OnCheckedChangeListener {

        private final PlaygroundForm mForm;

        private ShowSectionsForViewKind(final PlaygroundForm form) {
            mForm = form;
        }

        @Override
        public void onCheckedChanged(final RadioGroup group, final int checkedId) {
            mForm.showSections();
        }
    }
}
