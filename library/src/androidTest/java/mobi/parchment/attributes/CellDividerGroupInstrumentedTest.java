// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.PaintedPixels;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.harness.PixelRun;
import mobi.parchment.harness.SolidColourAdapter;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that in the two views whose cell is a whole group, parchment_divider separates the groups
 * and never the items inside one: a row of a GridView and one repeat of a GridPatternView pattern
 * each get a single divider after them, painted on a real framework canvas. Expected bands are
 * literal pixels, not a recomputation of the production formula.
 */
@RunWith(AndroidJUnit4.class)
public final class CellDividerGroupInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_COUNT = 30;
    private static final int ITEM_HEIGHT = 100;
    private static final int VIEWS_PER_CELL = 3;
    private static final int UNITS_ACROSS = 2;
    private static final int CELL_SPACING = 24;
    private static final int CELL_COLOUR = 0xff0000ff;
    private static final int DIVIDER_COLOUR = 0xff00ff00;
    private static final int NO_RUNS = 0;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int A_COLUMN_INSIDE_THE_FIRST_ITEM = 20;
    private static final int INSIDE_THE_FIRST_ROW = 50;
    private static final int INSIDE_THE_FIRST_PATTERN_GROUP = 50;

    private static final int[] GRID_BAND_STARTS = {108, 232, 356, 480};
    private static final int[] GRID_BAND_ENDS = {116, 240, 364, 488};
    private static final int[] PATTERN_BAND_STARTS = {227, 470};
    private static final int[] PATTERN_BAND_ENDS = {235, 478};

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void dividerInXmlOnAGridView_isPaintedOnceAfterEachRow() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness = attachGrid();
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        final List<Integer> rowTops = distinctTops(children);
        assertTrue(children.toString(), rowTops.size() > 1);
        assertEquals(
                "every row should hold " + VIEWS_PER_CELL + " views: " + children,
                rowTops.size() * VIEWS_PER_CELL,
                children.count());
        assertEquals(
                "the rows should be a cell spacing apart: " + children,
                CELL_SPACING,
                rowTops.get(1).intValue() - (rowTops.get(0).intValue() + ITEM_HEIGHT));
        final List<PixelRun> painted = dividersDownTheView(pixels);
        assertOneFewerDividerThanGroups(rowTops, painted);
        assertBands(painted, GRID_BAND_STARTS, GRID_BAND_ENDS);
    }

    @Test
    public void dividerInXmlOnAGridView_isNotPaintedBetweenTheItemsOfARow() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness = attachGrid();
        final PaintedPixels pixels = harness.paint();

        final List<PixelRun> acrossTheRow =
                pixels.runsAcrossRow(INSIDE_THE_FIRST_ROW, DIVIDER_COLOUR);
        assertEquals(
                "a row of items must carry no divider between them: " + acrossTheRow,
                NO_RUNS,
                acrossTheRow.size());
    }

    @Test
    public void dividerInXmlOnAGridPatternView_isPaintedOnceAfterEachPatternGroup() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachGridPattern();
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        final List<Integer> groupTops = distinctTops(children);
        assertTrue(children.toString(), groupTops.size() > 1);
        assertEquals(
                "every group should hold " + UNITS_ACROSS + " views: " + children,
                groupTops.size() * UNITS_ACROSS,
                children.count());
        final List<PixelRun> painted = dividersDownTheView(pixels);
        assertOneFewerDividerThanGroups(groupTops, painted);
        assertBands(painted, PATTERN_BAND_STARTS, PATTERN_BAND_ENDS);
    }

    @Test
    public void dividerInXmlOnAGridPatternView_isNotPaintedBetweenTheItemsOfAGroup() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachGridPattern();
        final PaintedPixels pixels = harness.paint();

        final List<PixelRun> acrossTheGroup =
                pixels.runsAcrossRow(INSIDE_THE_FIRST_PATTERN_GROUP, DIVIDER_COLOUR);
        assertEquals(
                "a pattern group must carry no divider inside it: " + acrossTheGroup,
                NO_RUNS,
                acrossTheGroup.size());
    }

    private static List<PixelRun> dividersDownTheView(final PaintedPixels pixels) {
        return pixels.runsDownColumn(A_COLUMN_INSIDE_THE_FIRST_ITEM, DIVIDER_COLOUR);
    }

    private static void assertOneFewerDividerThanGroups(
            final List<Integer> groupTops, final List<PixelRun> painted) {
        final int gapsBetweenGroups = groupTops.size() - 1;
        assertEquals(
                "one divider fewer than there are drawn groups, painted "
                        + painted
                        + " for group tops "
                        + groupTops,
                gapsBetweenGroups,
                painted.size());
    }

    private static void assertBands(
            final List<PixelRun> painted, final int[] starts, final int[] ends) {
        assertEquals("dividers painted: " + painted, starts.length, painted.size());
        for (int index = 0; index < starts.length; index++) {
            assertEquals(
                    "start of divider " + index + " of " + painted,
                    starts[index],
                    painted.get(index).start());
            assertEquals(
                    "end of divider " + index + " of " + painted,
                    ends[index],
                    painted.get(index).end());
        }
    }

    /** The top of every distinct row or group of the laid-out children, in ascending order. */
    private static List<Integer> distinctTops(final LaidOutChildren children) {
        final List<Integer> tops = new ArrayList<Integer>();
        for (int index = 0; index < children.count(); index++) {
            final Integer top = Integer.valueOf(children.top(index));
            if (!tops.contains(top)) {
                tops.add(top);
            }
        }
        sortAscending(tops);
        return tops;
    }

    private static void sortAscending(final List<Integer> values) {
        for (int outer = 0; outer < values.size(); outer++) {
            for (int inner = outer + 1; inner < values.size(); inner++) {
                final int left = values.get(outer).intValue();
                final int right = values.get(inner).intValue();
                if (right < left) {
                    values.set(outer, Integer.valueOf(right));
                    values.set(inner, Integer.valueOf(left));
                }
            }
        }
    }

    private ParchmentViewHarness<GridView<BaseAdapter>> attachGrid() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_divider_grid,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.setAdapter(
                new SolidColourAdapter(
                        context, ITEM_COUNT, MATCH_PARENT, ITEM_HEIGHT, CELL_COLOUR));
        return harness;
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachGridPattern() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_divider_pattern,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AddTwoUnitSquares());
        harness.setAdapter(
                new SolidColourAdapter(
                        context, ITEM_COUNT, MATCH_PARENT, MATCH_PARENT, CELL_COLOUR));
        return harness;
    }

    /**
     * Adds a pattern of two one-by-one grid units side by side, so one repeat of the pattern is one
     * row of two items and a divider that separates groups has to fall between the rows. The
     * definition takes its arguments as top, left, height, width.
     */
    private static final class AddTwoUnitSquares
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {

        private static final int ONE_UNIT = 1;
        private static final int FIRST_ROW = 0;
        private static final int FIRST_COLUMN = 0;
        private static final int SECOND_COLUMN = 1;

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final List<GridPatternItemDefinition> definitions =
                    new ArrayList<GridPatternItemDefinition>();
            definitions.add(
                    new GridPatternItemDefinition(FIRST_ROW, FIRST_COLUMN, ONE_UNIT, ONE_UNIT));
            definitions.add(
                    new GridPatternItemDefinition(FIRST_ROW, SECOND_COLUMN, ONE_UNIT, ONE_UNIT));
            view.addGridPatternGroupDefinition(definitions);
        }
    }
}
