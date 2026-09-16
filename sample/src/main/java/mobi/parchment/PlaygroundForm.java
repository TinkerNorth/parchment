// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import mobi.parchment.playground.CellSpacingOption;
import mobi.parchment.playground.GravityOption;
import mobi.parchment.playground.OrientationOption;
import mobi.parchment.playground.PatternOption;
import mobi.parchment.playground.PlaygroundOptions;
import mobi.parchment.playground.PlaygroundRules;
import mobi.parchment.playground.RadioOption;
import mobi.parchment.playground.RatioOption;
import mobi.parchment.playground.SnapPositionOption;
import mobi.parchment.playground.ViewKind;
import mobi.parchment.playground.ViewPagerIntervalOption;
import mobi.parchment.playground.ViewsPerCellOption;
import mobi.parchment.sample.R;

final class PlaygroundForm {

    private final int mItemCount;
    private final RadioGroup mViewKind;
    private final RadioGroup mOrientation;
    private final RadioGroup mCellSpacing;
    private final CheckBox mDivider;
    private final CheckBox mSnapToPosition;
    private final RadioGroup mSnapPosition;
    private final TextView mSnapPositionHint;
    private final CheckBox mCircularScroll;
    private final CheckBox mViewPager;
    private final TextView mViewPagerHint;
    private final RadioGroup mViewPagerInterval;
    private final CheckBox mSelectOnSnap;
    private final CheckBox mSelectWhileScrolling;
    private final View mGridViewSection;
    private final RadioGroup mViewsPerCell;
    private final RadioGroup mGravity;
    private final TextView mGravityHint;
    private final View mGridPatternViewSection;
    private final RadioGroup mRatio;
    private final RadioGroup mPattern;

    PlaygroundForm(final Activity activity) {
        mItemCount = ProductsAdapter.getPictures().size();
        mViewKind = activity.findViewById(R.id.playground_view_kind);
        mOrientation = activity.findViewById(R.id.playground_orientation);
        mCellSpacing = activity.findViewById(R.id.playground_cell_spacing);
        mDivider = activity.findViewById(R.id.playground_divider);
        mSnapToPosition = activity.findViewById(R.id.playground_snap_to_position);
        mSnapPosition = activity.findViewById(R.id.playground_snap_position);
        mSnapPositionHint = activity.findViewById(R.id.playground_snap_position_hint);
        mCircularScroll = activity.findViewById(R.id.playground_circular_scroll);
        mViewPager = activity.findViewById(R.id.playground_view_pager);
        mViewPagerHint = activity.findViewById(R.id.playground_view_pager_hint);
        mViewPagerInterval = activity.findViewById(R.id.playground_view_pager_interval);
        mSelectOnSnap = activity.findViewById(R.id.playground_select_on_snap);
        mSelectWhileScrolling = activity.findViewById(R.id.playground_select_while_scrolling);
        mGridViewSection = activity.findViewById(R.id.playground_grid_view_section);
        mViewsPerCell = activity.findViewById(R.id.playground_views_per_cell);
        mGravity = activity.findViewById(R.id.playground_gravity);
        mGravityHint = activity.findViewById(R.id.playground_gravity_hint);
        mGridPatternViewSection = activity.findViewById(R.id.playground_grid_pattern_view_section);
        mRatio = activity.findViewById(R.id.playground_ratio);
        mPattern = activity.findViewById(R.id.playground_pattern);
        refreshOnEveryChange();
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
        refresh();
    }

    PlaygroundOptions read() {
        return new PlaygroundOptions.Builder()
                .viewKind(checked(mViewKind, ViewKind.values()))
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

    void refresh() {
        final PlaygroundOptions options = read();
        final boolean intervalHasAnEffect = PlaygroundRules.viewPagerIntervalHasAnEffect(options);
        final boolean selectOnSnapHasAnEffect = PlaygroundRules.selectOnSnapHasAnEffect(options);
        final boolean selectWhileScrollingHasAnEffect =
                PlaygroundRules.selectWhileScrollingHasAnEffect(options);
        showSections(options.getViewKind());
        enableEach(mViewPagerInterval, ViewPagerIntervalOption.values(), intervalHasAnEffect);
        mSelectOnSnap.setEnabled(selectOnSnapHasAnEffect);
        mSelectWhileScrolling.setEnabled(selectWhileScrollingHasAnEffect);
        showSnapPositionHint(options);
        showViewPagerHint(options);
        enableGravityButtonsWithAnEffect(options);
        keepGravityOnAButtonWithAnEffect(options);
        showGravityHint(options);
    }

    private void refreshOnEveryChange() {
        final RefreshOnCheck refreshOnCheck = new RefreshOnCheck(this);
        final RefreshOnGroupCheck refreshOnGroupCheck = new RefreshOnGroupCheck(this);
        mViewKind.setOnCheckedChangeListener(refreshOnGroupCheck);
        mOrientation.setOnCheckedChangeListener(refreshOnGroupCheck);
        mCellSpacing.setOnCheckedChangeListener(refreshOnGroupCheck);
        mDivider.setOnCheckedChangeListener(refreshOnCheck);
        mSnapToPosition.setOnCheckedChangeListener(refreshOnCheck);
        mSnapPosition.setOnCheckedChangeListener(refreshOnGroupCheck);
        mCircularScroll.setOnCheckedChangeListener(refreshOnCheck);
        mViewPager.setOnCheckedChangeListener(refreshOnCheck);
        mViewPagerInterval.setOnCheckedChangeListener(refreshOnGroupCheck);
        mSelectOnSnap.setOnCheckedChangeListener(refreshOnCheck);
        mSelectWhileScrolling.setOnCheckedChangeListener(refreshOnCheck);
        mViewsPerCell.setOnCheckedChangeListener(refreshOnGroupCheck);
        mGravity.setOnCheckedChangeListener(refreshOnGroupCheck);
        mRatio.setOnCheckedChangeListener(refreshOnGroupCheck);
        mPattern.setOnCheckedChangeListener(refreshOnGroupCheck);
    }

    private void showSections(final ViewKind viewKind) {
        final boolean isGridView = viewKind == ViewKind.gridView;
        final boolean isGridPatternView = viewKind == ViewKind.gridPatternView;
        setVisible(mGridViewSection, isGridView);
        setVisible(mGridPatternViewSection, isGridPatternView);
    }

    private void showSnapPositionHint(final PlaygroundOptions options) {
        final boolean isForcedOnScreen = PlaygroundRules.snapPositionsAreForcedOnScreen(options);
        if (isForcedOnScreen) {
            mSnapPositionHint.setText(R.string.playground_snap_position_hint_circular);
        } else {
            mSnapPositionHint.setText(R.string.playground_snap_position_hint);
        }
    }

    private void showViewPagerHint(final PlaygroundOptions options) {
        final boolean restsAfterADrag = PlaygroundRules.pagerRestsAfterADrag(options);
        if (restsAfterADrag) {
            mViewPagerHint.setText(R.string.playground_view_pager_hint_settles);
        } else {
            mViewPagerHint.setText(R.string.playground_view_pager_hint_needs_snapping);
        }
    }

    private void enableGravityButtonsWithAnEffect(final PlaygroundOptions options) {
        for (final GravityOption gravity : GravityOption.values()) {
            final boolean hasAnEffect =
                    PlaygroundRules.gravityHasAnEffect(options, gravity, mItemCount);
            button(mGravity, gravity).setEnabled(hasAnEffect);
        }
    }

    private void keepGravityOnAButtonWithAnEffect(final PlaygroundOptions options) {
        final GravityOption gravity = PlaygroundRules.gravityToDemo(options, mItemCount);
        check(mGravity, gravity);
    }

    private void showGravityHint(final PlaygroundOptions options) {
        final OrientationOption orientation = options.getOrientation();
        final boolean hasAShortLastLine = PlaygroundRules.hasAShortLastLine(options, mItemCount);
        if (hasAShortLastLine) {
            mGravityHint.setText(orientation.getGravityHintResourceId());
        } else {
            mGravityHint.setText(fullLinesGravityHint(options));
        }
    }

    private String fullLinesGravityHint(final PlaygroundOptions options) {
        final Resources resources = mGravityHint.getResources();
        final int hintResourceId = options.getOrientation().getFullLinesGravityHintResourceId();
        final int viewsPerLine = options.getViewsPerCell().getCount();
        return resources.getString(hintResourceId, mItemCount, viewsPerLine);
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

    private static void enableEach(
            final RadioGroup group, final RadioOption[] options, final boolean isEnabled) {
        for (final RadioOption option : options) {
            button(group, option).setEnabled(isEnabled);
        }
    }

    private static RadioButton button(final RadioGroup group, final RadioOption option) {
        return group.findViewById(option.getRadioButtonId());
    }

    private static void setVisible(final View view, final boolean isVisible) {
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private static final class RefreshOnCheck implements CompoundButton.OnCheckedChangeListener {

        private final PlaygroundForm mForm;

        private RefreshOnCheck(final PlaygroundForm form) {
            mForm = form;
        }

        @Override
        public void onCheckedChanged(final CompoundButton button, final boolean isChecked) {
            mForm.refresh();
        }
    }

    private static final class RefreshOnGroupCheck implements RadioGroup.OnCheckedChangeListener {

        private final PlaygroundForm mForm;

        private RefreshOnGroupCheck(final PlaygroundForm form) {
            mForm = form;
        }

        @Override
        public void onCheckedChanged(final RadioGroup group, final int checkedId) {
            mForm.refresh();
        }
    }
}
