// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that a ViewPager page is one cell on the two views whose cell is a whole group of items: a
 * GridView cell is a row of parchment_numberOfViewsPerCell views, and a GridPatternView cell is one
 * repeat of the pattern. Both are laid out three groups to a viewport here, so a page that counted
 * what was visible would move three groups instead of one.
 */
@RunWith(AndroidJUnit4.class)
public final class ViewPagerGroupCellInstrumentedTest {

    private static final int GRID_VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ROW_HEIGHT = 200;
    private static final int VIEWS_PER_ROW = 3;
    private static final int GRID_ITEM_WIDTH = GRID_VIEWPORT_WIDTH / VIEWS_PER_ROW;
    private static final int GRID_ITEM_COUNT = 30;
    private static final int FIRST_ITEM_OF_THE_SECOND_ROW = VIEWS_PER_ROW;
    private static final int FIRST_ITEM_OF_THE_THIRD_ROW = VIEWS_PER_ROW * 2;
    private static final int FIRST_ITEM_OF_THE_FOURTH_ROW = VIEWS_PER_ROW * 3;

    private static final int PATTERN_VIEWPORT_WIDTH = 600;
    private static final int UNITS_ACROSS = 3;
    private static final int UNIT_SIZE = PATTERN_VIEWPORT_WIDTH / UNITS_ACROSS;
    private static final int PATTERN_ITEM_COUNT = 30;
    private static final int FIRST_ITEM_OF_THE_SECOND_GROUP = UNITS_ACROSS;
    private static final int FIRST_ITEM_OF_THE_THIRD_GROUP = UNITS_ACROSS * 2;
    private static final int FIRST_ITEM_OF_THE_FOURTH_GROUP = UNITS_ACROSS * 3;

    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int SNAP_POSITION_TOP = 0;
    private static final int FIRST_ITEM = 0;

    private static final int GESTURE_START_Y = 500;
    private static final int GESTURE_END_Y = 100;
    private static final int GESTURE_STEPS = 4;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void aGridFling_advancesExactlyOneRowRatherThanTheRowsOnScreen() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                attachGrid(R.layout.instrumented_view_pager_grid);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "the second row should start one row down: " + before,
                ROW_HEIGHT,
                before.topOfAdapterPosition(FIRST_ITEM_OF_THE_SECOND_ROW));
        assertEquals(
                "three rows should fill the viewport: " + before,
                VIEWPORT_HEIGHT - ROW_HEIGHT,
                before.topOfAdapterPosition(FIRST_ITEM_OF_THE_THIRD_ROW));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the second row to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_SECOND_ROW));
        assertEquals(
                "the content should have moved by exactly one row: " + after,
                -ROW_HEIGHT,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    @Test
    public void aGridWithAnIntervalOfTwo_advancesExactlyTwoRows() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                attachGrid(R.layout.instrumented_view_pager_grid_interval_two);

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "an interval of two should bring the third row to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_THIRD_ROW));
        assertEquals(
                "the second row should sit one row above it: " + after,
                -ROW_HEIGHT,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_SECOND_ROW));
    }

    /**
     * With the interval left at viewport a grid pages by the rows that fit whole, which is the run
     * of rows on screen: three 200px rows in a 600px viewport.
     */
    @Test
    public void aGridViewportFling_advancesEveryWholeRowThatFits() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                attachGrid(R.layout.instrumented_view_pager_grid_viewport);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "three rows should fill the viewport: " + before,
                VIEWPORT_HEIGHT - ROW_HEIGHT,
                before.topOfAdapterPosition(FIRST_ITEM_OF_THE_THIRD_ROW));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the fourth row to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_FOURTH_ROW));
        assertEquals(
                "the content should have moved by three whole rows: " + after,
                -ROW_HEIGHT,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_THIRD_ROW));
    }

    /**
     * The same on a pattern view, where a cell is a whole pattern group: three one-unit groups fit
     * the viewport, so one fling advances all three.
     */
    @Test
    public void aGridPatternViewportFling_advancesEveryWholeGroupThatFits() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                attachPattern(R.layout.instrumented_view_pager_pattern_viewport);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "three groups should fill the viewport: " + before,
                VIEWPORT_HEIGHT - UNIT_SIZE,
                before.topOfAdapterPosition(FIRST_ITEM_OF_THE_THIRD_GROUP));

        harness.fling(
                PATTERN_VIEWPORT_WIDTH / 2,
                GESTURE_START_Y,
                PATTERN_VIEWPORT_WIDTH / 2,
                GESTURE_END_Y,
                GESTURE_STEPS);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the fourth group to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_FOURTH_GROUP));
    }

    @Test
    public void aGridPatternFling_advancesExactlyOneGroupRatherThanTheGroupsOnScreen() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachPattern();
        final LaidOutChildren before = harness.children();
        assertEquals(
                "the second group should start one group down: " + before,
                UNIT_SIZE,
                before.topOfAdapterPosition(FIRST_ITEM_OF_THE_SECOND_GROUP));

        harness.fling(
                PATTERN_VIEWPORT_WIDTH / 2,
                GESTURE_START_Y,
                PATTERN_VIEWPORT_WIDTH / 2,
                GESTURE_END_Y,
                GESTURE_STEPS);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the second group to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM_OF_THE_SECOND_GROUP));
        assertEquals(
                "the content should have moved by exactly one group: " + after,
                -UNIT_SIZE,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    private void flingForward(final ParchmentViewHarness<GridView<BaseAdapter>> harness) {
        harness.fling(
                GRID_VIEWPORT_WIDTH / 2,
                GESTURE_START_Y,
                GRID_VIEWPORT_WIDTH / 2,
                GESTURE_END_Y,
                GESTURE_STEPS);
    }

    private ParchmentViewHarness<GridView<BaseAdapter>> attachGrid(final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        GRID_VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, GRID_ITEM_COUNT, GRID_ITEM_WIDTH, ROW_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachPattern() {
        return attachPattern(R.layout.instrumented_view_pager_pattern);
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachPattern(
            final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        PATTERN_VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AddARowOfUnitSquares());
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, PATTERN_ITEM_COUNT, MATCH_PARENT, MATCH_PARENT);
        harness.setAdapter(adapter);
        return harness;
    }

    /**
     * A pattern group of one row of one-by-one grid units, so the group is exactly one unit tall
     * and three of them fill the viewport. The definition takes its arguments as top, left, height,
     * width.
     */
    private static final class AddARowOfUnitSquares
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {

        private static final int ONE_UNIT = 1;

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final List<GridPatternItemDefinition> definitions = new ArrayList<>();
            for (int column = 0; column < UNITS_ACROSS; column++) {
                definitions.add(new GridPatternItemDefinition(0, column, ONE_UNIT, ONE_UNIT));
            }
            view.addGridPatternGroupDefinition(definitions);
        }
    }
}
