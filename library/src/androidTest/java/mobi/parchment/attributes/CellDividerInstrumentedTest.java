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
import java.util.List;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.PaintedPixels;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.harness.PixelRun;
import mobi.parchment.harness.SolidColourAdapter;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that parchment_divider and parchment_dividerSize, parsed by a real LayoutInflater, put
 * real pixels on a real framework canvas between the cells of a ListView. Every expected band is
 * written out as literal pixels rather than recomputed from the production formula, so a change to
 * that formula fails here.
 */
@RunWith(AndroidJUnit4.class)
public final class CellDividerInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_WIDTH = 200;
    private static final int ITEM_HEIGHT = 100;
    private static final int ITEM_COUNT = 30;
    private static final int SHORTER_THAN_VIEWPORT_COUNT = 3;
    private static final int ONE_ITEM = 1;
    private static final int NO_ITEMS = 0;
    private static final int WRAPPING_ITEM_COUNT = 8;
    private static final int LAST_WRAPPING_POSITION = WRAPPING_ITEM_COUNT - 1;
    private static final int FIRST_POSITION = 0;
    private static final int CELL_SPACING = 24;
    private static final int NO_CELL_SPACING = 0;
    private static final int START_BREADTH_PADDING = 40;
    private static final int START_SIZE_PADDING = 30;
    private static final int END_BREADTH_PADDING = 60;
    private static final int CELL_COLOUR = 0xff0000ff;
    private static final int DIVIDER_COLOUR = 0xff00ff00;
    private static final int NOTHING_PAINTED = 0;
    private static final int ONE_RUN = 1;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int A_COLUMN_INSIDE_EVERY_CELL = 100;
    private static final int A_ROW_INSIDE_EVERY_CELL = 50;
    private static final int A_COLUMN_INSIDE_THE_BREADTH_PADDING = 10;
    private static final int INSIDE_THE_FIRST_CELL = 50;
    private static final int UNDER_A_THICK_DIVIDER = 95;

    private static final int[] VERTICAL_BAND_STARTS = {108, 232, 356, 480};
    private static final int[] VERTICAL_BAND_ENDS = {116, 240, 364, 488};
    private static final int[] HORIZONTAL_BAND_STARTS = {208, 432, 656, 880};
    private static final int[] HORIZONTAL_BAND_ENDS = {216, 440, 664, 888};
    private static final int[] INTRINSIC_BAND_STARTS = {107, 231, 355, 479};
    private static final int[] INTRINSIC_BAND_ENDS = {117, 241, 365, 489};
    private static final int[] HORIZONTAL_INTRINSIC_BAND_STARTS = {197, 421, 645, 869};
    private static final int[] HORIZONTAL_INTRINSIC_BAND_ENDS = {227, 451, 675, 899};
    private static final int[] THICK_BAND_STARTS = {92, 216, 340, 464};
    private static final int[] THICK_BAND_ENDS = {132, 256, 380, 504};
    private static final int[] NO_SPACING_BAND_STARTS = {96, 196, 296, 396, 496, 596};
    private static final int[] NO_SPACING_BAND_ENDS = {104, 204, 304, 404, 504, 600};
    private static final int[] PADDED_SHORT_BAND_STARTS = {234, 358};
    private static final int[] PADDED_SHORT_BAND_ENDS = {242, 366};

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void dividerInXml_isPaintedInEveryGapBetweenAdjacentCells() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_vertical, ITEM_COUNT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertTrue(children.toString(), children.count() > 1);
        assertEquals(children.toString(), CELL_SPACING, children.top(1) - children.bottom(0));
        final List<PixelRun> painted = dividersDownTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, VERTICAL_BAND_STARTS, VERTICAL_BAND_ENDS);
    }

    @Test
    public void dividerInXml_inAHorizontalList_isPaintedAcrossTheGapAlongTheXAxis() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_divider_horizontal,
                        ITEM_COUNT,
                        ITEM_WIDTH,
                        MATCH_PARENT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertTrue(children.toString(), children.count() > 1);
        assertEquals(children.toString(), CELL_SPACING, children.left(1) - children.right(0));
        final List<PixelRun> painted = dividersAcrossTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, HORIZONTAL_BAND_STARTS, HORIZONTAL_BAND_ENDS);
    }

    @Test
    public void divider_paintsNothingBeforeTheFirstCellOrAfterTheLast() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(
                        R.layout.instrumented_divider_padded, SHORTER_THAN_VIEWPORT_COUNT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertEquals(children.toString(), SHORTER_THAN_VIEWPORT_COUNT, children.count());
        final int lastIndex = children.count() - 1;
        assertTrue(
                "there has to be room above the first cell to paint into: " + children,
                children.top(0) > 0);
        assertTrue(
                "there has to be room below the last cell to paint into: " + children,
                children.bottom(lastIndex) < VIEWPORT_HEIGHT);
        final List<PixelRun> painted = dividersDownTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, PADDED_SHORT_BAND_STARTS, PADDED_SHORT_BAND_ENDS);
        assertTrue(
                "no divider may be painted above the first cell: " + painted + " for " + children,
                painted.get(0).start() >= children.bottom(0));
        assertTrue(
                "no divider may be painted below the last cell: " + painted + " for " + children,
                painted.get(painted.size() - 1).end() <= children.top(lastIndex));
    }

    @Test
    public void divider_spansTheBreadthInsideThePaddingAndNoFurther() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_padded, ITEM_COUNT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertEquals(children.toString(), START_SIZE_PADDING, children.top(0));
        final List<PixelRun> down = dividersDownTheList(pixels);
        assertTrue("some divider has to be painted: " + children, down.size() > 0);
        final int insideADivider = down.get(0).start();
        final List<PixelRun> across = pixels.runsAcrossRow(insideADivider, DIVIDER_COLOUR);
        assertEquals("one unbroken divider across the breadth: " + across, ONE_RUN, across.size());
        assertEquals(
                "the divider starts at the start breadth padding: " + across,
                START_BREADTH_PADDING,
                across.get(0).start());
        assertEquals(
                "the divider ends at the end breadth padding: " + across,
                VIEWPORT_WIDTH - END_BREADTH_PADDING,
                across.get(0).end());
        assertEquals(
                "nothing may be painted in the breadth padding",
                NOTHING_PAINTED,
                pixels.colourAt(A_COLUMN_INSIDE_THE_BREADTH_PADDING, insideADivider));
    }

    @Test
    public void noDividerInXml_paintsNothingBetweenTheCells() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_absent, ITEM_COUNT);
        final PaintedPixels pixels = harness.paint();

        assertEquals(
                "no pixel may carry the divider colour",
                NOTHING_PAINTED,
                pixels.countOfColour(DIVIDER_COLOUR));
        assertEquals(
                "the cells still paint",
                CELL_COLOUR,
                pixels.colourAt(A_COLUMN_INSIDE_EVERY_CELL, INSIDE_THE_FIRST_CELL));
    }

    @Test
    public void aColourDividerWithNoDividerSizeInXml_paintsNothing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_colour_no_size, ITEM_COUNT);
        final PaintedPixels pixels = harness.paint();

        assertEquals(
                "a colour has no intrinsic size, so nothing may be painted",
                NOTHING_PAINTED,
                pixels.countOfColour(DIVIDER_COLOUR));
    }

    @Test
    public void dividerWithNoSize_scrollingVertically_takesTheDrawablesIntrinsicHeight() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_intrinsic, ITEM_COUNT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        final List<PixelRun> painted = dividersDownTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, INTRINSIC_BAND_STARTS, INTRINSIC_BAND_ENDS);
    }

    @Test
    public void dividerWithNoSize_scrollingHorizontally_takesTheDrawablesIntrinsicWidth() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_divider_intrinsic_horizontal,
                        ITEM_COUNT,
                        ITEM_WIDTH,
                        MATCH_PARENT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        final List<PixelRun> painted = dividersAcrossTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, HORIZONTAL_INTRINSIC_BAND_STARTS, HORIZONTAL_INTRINSIC_BAND_ENDS);
    }

    @Test
    public void aDividerThickerThanTheCellSpacing_isPaintedOverTheCellsItOverlaps() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_thick, ITEM_COUNT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        final List<PixelRun> painted = dividersDownTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, THICK_BAND_STARTS, THICK_BAND_ENDS);
        assertEquals(
                "the thick divider must cover the bottom of the first cell",
                DIVIDER_COLOUR,
                pixels.colourAt(A_COLUMN_INSIDE_EVERY_CELL, UNDER_A_THICK_DIVIDER));
    }

    @Test
    public void aDividerWithNoCellSpacing_isPaintedOnTheBoundaryBetweenTheCells() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_no_spacing, ITEM_COUNT);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertTrue(children.toString(), children.count() > 1);
        assertEquals(children.toString(), NO_CELL_SPACING, children.top(1) - children.bottom(0));
        final List<PixelRun> painted = dividersDownTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, NO_SPACING_BAND_STARTS, NO_SPACING_BAND_ENDS);
    }

    @Test
    public void dividerWithCircularScroll_isPaintedAcrossTheWrapLikeAnyOtherGap() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_circular, WRAPPING_ITEM_COUNT);
        harness.setSelection(LAST_WRAPPING_POSITION);
        harness.settle();
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        final int lastItemIndex = children.indexOfAdapterPosition(LAST_WRAPPING_POSITION);
        assertTrue("the last item should be laid out: " + children, lastItemIndex >= 0);
        assertTrue(
                "the wrap should lay out cells after the last item: " + children,
                lastItemIndex < children.count() - 1);
        assertEquals(
                "item 0 should follow the last item: " + children,
                FIRST_POSITION,
                children.adapterPosition(lastItemIndex + 1));
        final List<PixelRun> painted = dividersDownTheList(pixels);
        assertOneFewerDividerThanCells(children, painted);
        assertBands(painted, VERTICAL_BAND_STARTS, VERTICAL_BAND_ENDS);
    }

    @Test
    public void aSingleCellAdapter_paintsNoDivider() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_vertical, ONE_ITEM);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertEquals(children.toString(), ONE_ITEM, children.count());
        assertEquals(
                "one cell has no gap to divide",
                NOTHING_PAINTED,
                pixels.countOfColour(DIVIDER_COLOUR));
    }

    @Test
    public void anEmptyAdapter_paintsNoDivider() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachVerticalList(R.layout.instrumented_divider_vertical, NO_ITEMS);
        final LaidOutChildren children = harness.children();
        final PaintedPixels pixels = harness.paint();

        assertEquals(children.toString(), NO_ITEMS, children.count());
        assertEquals(
                "no cells means no dividers",
                NOTHING_PAINTED,
                pixels.countOfColour(DIVIDER_COLOUR));
    }

    private static List<PixelRun> dividersDownTheList(final PaintedPixels pixels) {
        return pixels.runsDownColumn(A_COLUMN_INSIDE_EVERY_CELL, DIVIDER_COLOUR);
    }

    private static List<PixelRun> dividersAcrossTheList(final PaintedPixels pixels) {
        return pixels.runsAcrossRow(A_ROW_INSIDE_EVERY_CELL, DIVIDER_COLOUR);
    }

    private static void assertOneFewerDividerThanCells(
            final LaidOutChildren children, final List<PixelRun> painted) {
        final int gapsBetweenCells = children.count() - 1;
        assertEquals(
                "one divider fewer than there are drawn cells, painted "
                        + painted
                        + " for "
                        + children,
                gapsBetweenCells,
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

    private ParchmentViewHarness<ListView<BaseAdapter>> attachVerticalList(
            final int layoutResource, final int itemCount) {
        return attachList(layoutResource, itemCount, MATCH_PARENT, ITEM_HEIGHT);
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(
            final int layoutResource,
            final int itemCount,
            final int itemWidth,
            final int itemHeight) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final SolidColourAdapter adapter =
                new SolidColourAdapter(context, itemCount, itemWidth, itemHeight, CELL_COLOUR);
        harness.setAdapter(adapter);
        return harness;
    }
}
