// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import static org.assertj.core.api.Assertions.assertThat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import mobi.parchment.playground.Preset;
import mobi.parchment.playground.ViewsPerCellOption;
import mobi.parchment.sample.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

// SampleApplication installs a Picasso singleton, which a JVM accepts once.
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class)
public class PlaygroundFormTest {

    private static final int NOTHING_LEFT_OVER = 0;

    @Test
    public void theIntervalButtons_withoutViewPager_areDisabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_viewport)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_one)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_two)).isFalse();
    }

    @Test
    public void theIntervalButtons_withViewPager_areEnabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        setChecked(activity, R.id.playground_view_pager, true);

        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_viewport)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_one)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_two)).isTrue();
    }

    @Test
    public void theIntervalButtons_withTheViewPagerPreset_areEnabled() {
        final PlaygroundActivity activity = playground(Preset.viewPager);

        assertThat(isEnabled(activity, R.id.playground_view_pager_interval_viewport)).isTrue();
    }

    @Test
    public void selectOnSnap_withSnapToPosition_isEnabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        assertThat(isEnabled(activity, R.id.playground_select_on_snap)).isTrue();
    }

    @Test
    public void selectOnSnap_withoutSnapToPosition_isDisabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        setChecked(activity, R.id.playground_snap_to_position, false);

        assertThat(isEnabled(activity, R.id.playground_select_on_snap)).isFalse();
    }

    @Test
    public void selectOnSnap_snappingOnScreenWithAnEndToPushAgainst_isEnabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_on_screen);
        setChecked(activity, R.id.playground_circular_scroll, false);

        assertThat(isEnabled(activity, R.id.playground_select_on_snap)).isTrue();
    }

    @Test
    public void selectOnSnap_snappingOnScreenUnderCircularScroll_isDisabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_on_screen);
        setChecked(activity, R.id.playground_circular_scroll, true);

        assertThat(isEnabled(activity, R.id.playground_select_on_snap)).isFalse();
    }

    @Test
    public void scrollWithinContent_withAnotherPositionAndEnds_isEnabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_start);
        setChecked(activity, R.id.playground_circular_scroll, false);

        assertThat(isEnabled(activity, R.id.playground_scroll_within_content)).isTrue();
    }

    @Test
    public void scrollWithinContent_withOnScreen_isDisabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_on_screen);

        assertThat(isEnabled(activity, R.id.playground_scroll_within_content)).isFalse();
    }

    @Test
    public void scrollWithinContent_underCircularScroll_isDisabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_start);
        setChecked(activity, R.id.playground_circular_scroll, true);

        assertThat(isEnabled(activity, R.id.playground_scroll_within_content)).isFalse();
    }

    @Test
    public void selectWhileScrolling_withOnScreen_isDisabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_on_screen);

        assertThat(isEnabled(activity, R.id.playground_select_while_scrolling)).isFalse();
    }

    @Test
    public void selectWhileScrolling_withAnotherPositionAndNoSnapToPosition_isEnabled() {
        final PlaygroundActivity activity = playground(Preset.listView);

        setChecked(activity, R.id.playground_snap_to_position, false);
        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_center);

        assertThat(isEnabled(activity, R.id.playground_select_while_scrolling)).isTrue();
    }

    @Test
    public void everyGravityButton_inAVerticalGridWithAShortRow_isEnabled() {
        final PlaygroundActivity activity = playground(Preset.gridView);

        check(activity, R.id.playground_views_per_cell, leavingAShortLine().getRadioButtonId());

        assertThat(isEnabled(activity, R.id.playground_gravity_top)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_bottom)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_left)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_right)).isTrue();
    }

    @Test
    public void leftAndRight_inAVerticalGridWithFullRows_areDisabled() {
        final PlaygroundActivity activity = playground(Preset.gridView);

        check(activity, R.id.playground_views_per_cell, fillingEveryLine().getRadioButtonId());

        assertThat(isEnabled(activity, R.id.playground_gravity_top)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_bottom)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_left)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_gravity_right)).isFalse();
    }

    @Test
    public void leftAndRight_inAHorizontalGridWithAShortColumn_areDisabled() {
        final PlaygroundActivity activity = playground(Preset.gridView);

        check(activity, R.id.playground_views_per_cell, leavingAShortLine().getRadioButtonId());
        check(activity, R.id.playground_orientation, R.id.playground_orientation_horizontal);

        assertThat(isEnabled(activity, R.id.playground_gravity_top)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_bottom)).isTrue();
        assertThat(isEnabled(activity, R.id.playground_gravity_left)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_gravity_right)).isFalse();
    }

    @Test
    public void everyGravityButton_inAHorizontalGridWithFullColumns_isDisabled() {
        final PlaygroundActivity activity = playground(Preset.gridView);

        check(activity, R.id.playground_views_per_cell, fillingEveryLine().getRadioButtonId());
        check(activity, R.id.playground_orientation, R.id.playground_orientation_horizontal);

        assertThat(isEnabled(activity, R.id.playground_gravity_top)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_gravity_bottom)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_gravity_left)).isFalse();
        assertThat(isEnabled(activity, R.id.playground_gravity_right)).isFalse();
    }

    @Test
    public void aCheckedGravity_thatLosesItsEffect_movesToTheFirstWithAnEffect() {
        final PlaygroundActivity activity = playground(Preset.gridView);
        check(activity, R.id.playground_views_per_cell, leavingAShortLine().getRadioButtonId());
        check(activity, R.id.playground_gravity, R.id.playground_gravity_left);

        check(activity, R.id.playground_orientation, R.id.playground_orientation_horizontal);

        assertThat(checkedId(activity, R.id.playground_gravity))
                .isEqualTo(R.id.playground_gravity_top);
    }

    @Test
    public void aCheckedGravity_thatKeepsItsEffect_staysChecked() {
        final PlaygroundActivity activity = playground(Preset.gridView);
        check(activity, R.id.playground_views_per_cell, leavingAShortLine().getRadioButtonId());
        check(activity, R.id.playground_gravity, R.id.playground_gravity_bottom);

        check(activity, R.id.playground_views_per_cell, fillingEveryLine().getRadioButtonId());

        assertThat(checkedId(activity, R.id.playground_gravity))
                .isEqualTo(R.id.playground_gravity_bottom);
    }

    @Test
    public void aCheckedGravity_whenNoneHasAnEffect_staysChecked() {
        final PlaygroundActivity activity = playground(Preset.gridView);
        check(activity, R.id.playground_gravity, R.id.playground_gravity_bottom);

        check(activity, R.id.playground_views_per_cell, fillingEveryLine().getRadioButtonId());
        check(activity, R.id.playground_orientation, R.id.playground_orientation_horizontal);

        assertThat(checkedId(activity, R.id.playground_gravity))
                .isEqualTo(R.id.playground_gravity_bottom);
    }

    @Test
    public void theSnapPositionHint_withoutCircularScroll_describesThePositions() {
        final PlaygroundActivity activity = playground(Preset.listView);

        assertThat(text(activity, R.id.playground_snap_position_hint))
                .isEqualTo(activity.getString(R.string.playground_snap_position_hint));
    }

    @Test
    public void theSnapPositionHint_underCircularScroll_saysThePositionsAreForcedOnScreen() {
        final PlaygroundActivity activity = playground(Preset.listView);

        setChecked(activity, R.id.playground_circular_scroll, true);

        assertThat(text(activity, R.id.playground_snap_position_hint))
                .isEqualTo(activity.getString(R.string.playground_snap_position_hint_circular));
    }

    @Test
    public void theViewPagerHint_snappingToAPosition_saysADragSettles() {
        final PlaygroundActivity activity = playground(Preset.listView);

        assertThat(text(activity, R.id.playground_view_pager_hint))
                .isEqualTo(activity.getString(R.string.playground_view_pager_hint_settles));
    }

    @Test
    public void theViewPagerHint_withoutSnapToPosition_saysWhatAPagerNeeds() {
        final PlaygroundActivity activity = playground(Preset.listView);

        setChecked(activity, R.id.playground_snap_to_position, false);

        assertThat(text(activity, R.id.playground_view_pager_hint))
                .isEqualTo(activity.getString(R.string.playground_view_pager_hint_needs_snapping));
    }

    @Test
    public void theViewPagerHint_snappingOnScreen_saysWhatAPagerNeeds() {
        final PlaygroundActivity activity = playground(Preset.listView);

        check(activity, R.id.playground_snap_position, R.id.playground_snap_position_on_screen);

        assertThat(text(activity, R.id.playground_view_pager_hint))
                .isEqualTo(activity.getString(R.string.playground_view_pager_hint_needs_snapping));
    }

    @Test
    public void theGravityHint_inAVerticalGridWithAShortRow_describesBothPairs() {
        final PlaygroundActivity activity = playground(Preset.gridView);

        check(activity, R.id.playground_views_per_cell, leavingAShortLine().getRadioButtonId());

        assertThat(text(activity, R.id.playground_gravity_hint))
                .isEqualTo(activity.getString(R.string.playground_gravity_hint_vertical));
    }

    @Test
    public void theGravityHint_inAVerticalGridWithFullRows_countsThePhotosOverTheRows() {
        final PlaygroundActivity activity = playground(Preset.gridView);
        final ViewsPerCellOption viewsPerCell = fillingEveryLine();

        check(activity, R.id.playground_views_per_cell, viewsPerCell.getRadioButtonId());

        assertThat(text(activity, R.id.playground_gravity_hint))
                .isEqualTo(
                        activity.getString(
                                R.string.playground_gravity_hint_vertical_full_rows,
                                itemCount(),
                                viewsPerCell.getCount()));
    }

    @Test
    public void theGravityHint_inAHorizontalGridWithAShortColumn_describesBothPairs() {
        final PlaygroundActivity activity = playground(Preset.gridView);

        check(activity, R.id.playground_views_per_cell, leavingAShortLine().getRadioButtonId());
        check(activity, R.id.playground_orientation, R.id.playground_orientation_horizontal);

        assertThat(text(activity, R.id.playground_gravity_hint))
                .isEqualTo(activity.getString(R.string.playground_gravity_hint_horizontal));
    }

    @Test
    public void theGravityHint_inAHorizontalGridWithFullColumns_countsThePhotosOverTheColumns() {
        final PlaygroundActivity activity = playground(Preset.gridView);
        final ViewsPerCellOption viewsPerCell = fillingEveryLine();

        check(activity, R.id.playground_views_per_cell, viewsPerCell.getRadioButtonId());
        check(activity, R.id.playground_orientation, R.id.playground_orientation_horizontal);

        assertThat(text(activity, R.id.playground_gravity_hint))
                .isEqualTo(
                        activity.getString(
                                R.string.playground_gravity_hint_horizontal_full_columns,
                                itemCount(),
                                viewsPerCell.getCount()));
    }

    @Test
    public void theGridViewSection_isShownOnlyForAGridView() {
        final PlaygroundActivity activity = playground(Preset.listView);

        assertThat(visibility(activity, R.id.playground_grid_view_section)).isEqualTo(View.GONE);

        check(activity, R.id.playground_view_kind, R.id.playground_view_kind_grid_view);

        assertThat(visibility(activity, R.id.playground_grid_view_section)).isEqualTo(View.VISIBLE);
    }

    @Test
    public void aRecreatedPlayground_atEveryDefault_stillDisablesWhatHasNoEffect() {
        final ActivityController<PlaygroundActivity> controller = controller(Preset.scrollListener);

        controller.recreate();

        assertThat(isEnabled(controller.get(), R.id.playground_view_pager_interval_viewport))
                .isFalse();
    }

    @Test
    public void aRecreatedPlayground_atEveryDefault_stillShowsTheRefreshedHint() {
        final ActivityController<PlaygroundActivity> controller = controller(Preset.scrollListener);
        final String hintBefore = text(controller.get(), R.id.playground_gravity_hint);

        controller.recreate();

        assertThat(text(controller.get(), R.id.playground_gravity_hint)).isEqualTo(hintBefore);
    }

    @Test
    public void aRecreatedPlayground_atEveryDefault_stillHidesTheOtherViewsSections() {
        final ActivityController<PlaygroundActivity> controller = controller(Preset.scrollListener);

        controller.recreate();

        assertThat(visibility(controller.get(), R.id.playground_grid_view_section))
                .isEqualTo(View.GONE);
    }

    @Test
    public void aRecreatedPlayground_keepsWhatWasChanged() {
        final ActivityController<PlaygroundActivity> controller = controller(Preset.listView);
        setChecked(controller.get(), R.id.playground_circular_scroll, true);

        controller.recreate();

        final PlaygroundActivity recreated = controller.get();
        assertThat(text(recreated, R.id.playground_snap_position_hint))
                .isEqualTo(recreated.getString(R.string.playground_snap_position_hint_circular));
    }

    private static PlaygroundActivity playground(final Preset preset) {
        return controller(preset).get();
    }

    private static ActivityController<PlaygroundActivity> controller(final Preset preset) {
        final Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = PlaygroundActivity.intentFor(context, preset);
        return Robolectric.buildActivity(PlaygroundActivity.class, intent).setup();
    }

    private static boolean isEnabled(final PlaygroundActivity activity, final int viewId) {
        final View view = activity.findViewById(viewId);
        return view.isEnabled();
    }

    private static int visibility(final PlaygroundActivity activity, final int viewId) {
        final View view = activity.findViewById(viewId);
        return view.getVisibility();
    }

    private static String text(final PlaygroundActivity activity, final int viewId) {
        final TextView view = activity.findViewById(viewId);
        return view.getText().toString();
    }

    private static int checkedId(final PlaygroundActivity activity, final int groupId) {
        final RadioGroup group = activity.findViewById(groupId);
        return group.getCheckedRadioButtonId();
    }

    private static void check(
            final PlaygroundActivity activity, final int groupId, final int radioButtonId) {
        final RadioGroup group = activity.findViewById(groupId);
        group.check(radioButtonId);
    }

    private static void setChecked(
            final PlaygroundActivity activity, final int checkBoxId, final boolean isChecked) {
        final CheckBox checkBox = activity.findViewById(checkBoxId);
        checkBox.setChecked(isChecked);
    }

    private static int itemCount() {
        return ProductsAdapter.getPictures().size();
    }

    private static ViewsPerCellOption leavingAShortLine() {
        for (final ViewsPerCellOption option : ViewsPerCellOption.values()) {
            final int leftOver = itemCount() % option.getCount();
            final boolean leavesAShortLine = leftOver != NOTHING_LEFT_OVER;
            if (leavesAShortLine) {
                return option;
            }
        }
        throw new IllegalStateException("every option fills every line");
    }

    private static ViewsPerCellOption fillingEveryLine() {
        for (final ViewsPerCellOption option : ViewsPerCellOption.values()) {
            final int leftOver = itemCount() % option.getCount();
            final boolean fillsEveryLine = leftOver == NOTHING_LEFT_OVER;
            if (fillsEveryLine) {
                return option;
            }
        }
        throw new IllegalStateException("every option leaves a short line");
    }
}
