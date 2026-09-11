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
import mobi.parchment.harness.AlternatingHeightAdapter;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.harness.PositionRecordingAdapter;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves on a real framework that a ViewPager gesture with a positive parchment_viewPagerInterval
 * advances exactly that many cells, whatever else is on screen. Every cell here is a third of the
 * viewport, so three of them are visible at once: that is the arrangement issue #26 reported, where
 * one swipe jumped three cells because the page was the whole run of visible cells rather than one
 * of them. An interval of one is what that reporter wants and every layout here declares it, since
 * the attribute left unset still means viewport paging.
 *
 * <p>This class also pins which attribute values select which mode. The paging geometry of viewport
 * mode is covered by ViewPagerViewportInstrumentedTest.
 *
 * <p>The distance is measured, not counted, so these tests assert where a named adapter position
 * came to rest in pixels rather than how many children are on screen.
 */
@RunWith(AndroidJUnit4.class)
public final class ViewPagerIntervalInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int CELL_HEIGHT = 200;
    private static final int CELLS_ON_SCREEN = VIEWPORT_HEIGHT / CELL_HEIGHT;
    private static final int TALL_CELL_HEIGHT = CELL_HEIGHT;
    private static final int SHORT_CELL_HEIGHT = 100;
    private static final int OVERSIZED_CELL_HEIGHT = 900;
    private static final int ITEM_COUNT = 10;
    private static final int ITEMS_TO_THE_END = 4;
    private static final int LAST_ITEM_TO_THE_END = ITEMS_TO_THE_END - 1;
    private static final int CIRCULAR_ITEM_COUNT = 4;
    private static final int LAST_CIRCULAR_ITEM = CIRCULAR_ITEM_COUNT - 1;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int SNAP_POSITION_TOP = 0;

    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int GESTURE_START_Y = 500;
    private static final int GESTURE_END_Y = 100;
    private static final int GESTURE_TRAVEL = GESTURE_START_Y - GESTURE_END_Y;
    private static final int GESTURE_STEPS = 4;
    private static final int PART_WAY_TRAVEL = 60;
    private static final int PART_WAY_END_Y = GESTURE_START_Y - PART_WAY_TRAVEL;
    private static final int PART_WAY_STEPS = 2;

    private static final int FIRST_ITEM = 0;
    private static final int SECOND_ITEM = 1;
    private static final int THIRD_ITEM = 2;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    /**
     * The arrangement from issue #26: three cells fit the viewport, and one fling moves the content
     * by one cell rather than by the three that are visible.
     */
    @Test
    public void aFlingWithThreeCellsVisible_advancesExactlyOneCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, CELL_HEIGHT);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), SNAP_POSITION_TOP, before.topOfAdapterPosition(FIRST_ITEM));
        assertEquals(before.toString(), CELL_HEIGHT, before.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "the cell one short of the viewport should be the third: " + before,
                VIEWPORT_HEIGHT - CELL_HEIGHT,
                before.topOfAdapterPosition(CELLS_ON_SCREEN - 1));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the second cell to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "the content should have moved by exactly one cell: " + after,
                -CELL_HEIGHT,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    @Test
    public void aFlingBackWithThreeCellsVisible_advancesExactlyOneCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, CELL_HEIGHT);
        flingForward(harness);
        harness.settle();
        flingForward(harness);
        final LaidOutChildren atTheThirdCell = harness.settle();
        assertEquals(
                "two flings should have reached the third cell: " + atTheThirdCell,
                SNAP_POSITION_TOP,
                atTheThirdCell.topOfAdapterPosition(THIRD_ITEM));

        flingBack(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling back should return exactly one cell: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "the third cell should sit one cell below it: " + after,
                CELL_HEIGHT,
                after.topOfAdapterPosition(THIRD_ITEM));
    }

    /**
     * What the issue reporter actually complained about: the same swipe advanced a different number
     * of cells depending on how it was made. A drag held still before it is released reports no
     * fling and is stopped by the page clamp; a quick one reports a fling and is animated to the
     * page. Both land on the second cell.
     *
     * <p>The drag half lands exactly on the cell because the clamp refuses a whole frame rather
     * than the part of it that would overshoot, and this gesture's travel per frame divides the
     * cell. A drag whose frames do not divide it stops at the last frame that fits and stays there,
     * since these layouts do not ask for parchment_snapToPosition.
     */
    @Test
    public void aSlowDragAndAFastFlingOfTheSameLength_bothAdvanceExactlyOneCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> dragged =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, CELL_HEIGHT);
        dragged.dragAndRelease(GESTURE_X, GESTURE_START_Y, GESTURE_X, GESTURE_END_Y, GESTURE_STEPS);
        final LaidOutChildren afterTheDrag = dragged.settle();

        final ParchmentViewHarness<ListView<BaseAdapter>> flung =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, CELL_HEIGHT);
        flingForward(flung);
        final LaidOutChildren afterTheFling = flung.settle();

        assertEquals(
                "a slow drag of " + GESTURE_TRAVEL + "px should advance one cell: " + afterTheDrag,
                SNAP_POSITION_TOP,
                afterTheDrag.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "a fast fling of the same length should advance one cell too: " + afterTheFling,
                SNAP_POSITION_TOP,
                afterTheFling.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "the drag and the fling should leave the first cell in the same place",
                afterTheDrag.topOfAdapterPosition(FIRST_ITEM),
                afterTheFling.topOfAdapterPosition(FIRST_ITEM));
    }

    @Test
    public void anIntervalOfTwoInXml_advancesExactlyTwoCells() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager_interval_two, ITEM_COUNT, CELL_HEIGHT);

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "an interval of two should bring the third cell to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(THIRD_ITEM));
        assertEquals(
                "the content should have moved by exactly two cells: " + after,
                -CELL_HEIGHT,
                after.topOfAdapterPosition(SECOND_ITEM));
    }

    @Test
    public void anIntervalOfTwoInXml_advancesExactlyTwoCellsBack() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager_interval_two, ITEM_COUNT, CELL_HEIGHT);
        flingForward(harness);
        final LaidOutChildren forward = harness.settle();
        assertEquals(
                "the forward fling should have reached the third cell: " + forward,
                SNAP_POSITION_TOP,
                forward.topOfAdapterPosition(THIRD_ITEM));

        flingBack(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling back should return exactly two cells: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    /**
     * Zero is the value an absent attribute already yields, so "unset" and "viewport" are one
     * state: both page by the whole run of cells that fit. This is the value arriving through a
     * real TypedArray rather than through the attribute getters.
     */
    @Test
    public void anIntervalOfZeroInXml_pagesByTheWholeViewport() {
        assertPagesByTheWholeViewport(R.layout.instrumented_view_pager_interval_zero);
    }

    @Test
    public void noIntervalInXml_pagesByTheWholeViewport() {
        assertPagesByTheWholeViewport(R.layout.instrumented_view_pager_no_interval);
    }

    @Test
    public void theViewportConstantInXml_pagesByTheWholeViewport() {
        assertPagesByTheWholeViewport(R.layout.instrumented_view_pager_viewport);
    }

    @Test
    public void aNegativeIntervalInXml_pagesByTheWholeViewport() {
        assertPagesByTheWholeViewport(R.layout.instrumented_view_pager_interval_negative);
    }

    private void assertPagesByTheWholeViewport(final int layoutResource) {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(layoutResource, ITEM_COUNT, CELL_HEIGHT);

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the page should be the whole run of cells that fit: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(CELLS_ON_SCREEN));
    }

    /**
     * Cells of different sizes page by their own size. The first page is a 200px cell and the
     * second a 100px one, so no single cell size multiplied by an interval describes both.
     */
    @Test
    public void cellsOfUnequalSize_pageByTheDistanceToTheNextCellRatherThanAFixedOne() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager);
        final AlternatingHeightAdapter adapter =
                new AlternatingHeightAdapter(
                        context, ITEM_COUNT, MATCH_PARENT, TALL_CELL_HEIGHT, SHORT_CELL_HEIGHT);
        harness.setAdapter(adapter);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "the second cell should start one tall cell down: " + before,
                TALL_CELL_HEIGHT,
                before.topOfAdapterPosition(SECOND_ITEM));

        flingForward(harness);
        final LaidOutChildren afterTheFirstPage = harness.settle();

        assertEquals(
                "the first page should be the tall cell's own height: " + afterTheFirstPage,
                -TALL_CELL_HEIGHT,
                afterTheFirstPage.topOfAdapterPosition(FIRST_ITEM));
        assertEquals(
                "the second cell should be at the top: " + afterTheFirstPage,
                SNAP_POSITION_TOP,
                afterTheFirstPage.topOfAdapterPosition(SECOND_ITEM));

        flingForward(harness);
        final LaidOutChildren afterTheSecondPage = harness.settle();

        assertEquals(
                "the second page should be the short cell's own height: " + afterTheSecondPage,
                -SHORT_CELL_HEIGHT,
                afterTheSecondPage.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "the third cell should be at the top: " + afterTheSecondPage,
                SNAP_POSITION_TOP,
                afterTheSecondPage.topOfAdapterPosition(THIRD_ITEM));
    }

    /**
     * A cell taller than the viewport is one page on its own, which is what 1.6.6 intended and what
     * the summing this replaced also happened to do for a cell resting at the snap position.
     */
    @Test
    public void aCellTallerThanTheViewport_advancesExactlyOneCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, OVERSIZED_CELL_HEIGHT);
        final LaidOutChildren before = harness.children();
        final int firstIndex = before.indexOfAdapterPosition(FIRST_ITEM);
        assertTrue(
                "the framework should have measured a cell taller than the viewport: " + before,
                before.height(firstIndex) > before.viewHeight());

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should move by the oversized cell's own height: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(SECOND_ITEM));
    }

    /**
     * A gesture that starts with the content resting part-way through a cell lands on a cell
     * boundary rather than carrying the offset along with it. The page is measured from where the
     * cell nearest the snap position would sit once snapped, so it is 140px here, not the 200px a
     * cell is wide.
     */
    @Test
    public void aGestureFromARestingPositionPartWayThroughACell_landsOnACellBoundary() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, CELL_HEIGHT);
        assertTrue(
                "a nudge of "
                        + PART_WAY_TRAVEL
                        + "px has to clear this device's touch slop of "
                        + harness.touchSlop()
                        + "px to move anything at all",
                PART_WAY_TRAVEL > harness.touchSlop());

        harness.dragAndRelease(
                GESTURE_X, GESTURE_START_Y, GESTURE_X, PART_WAY_END_Y, PART_WAY_STEPS);
        final LaidOutChildren partWay = harness.settle();
        assertEquals(
                "the nudge should leave the content part-way through the first cell: " + partWay,
                -PART_WAY_TRAVEL,
                partWay.topOfAdapterPosition(FIRST_ITEM));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the page should land the second cell exactly on the snap position: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(SECOND_ITEM));
        assertEquals(
                "the first cell should sit one whole cell above it: " + after,
                -CELL_HEIGHT,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    @Test
    public void pagingBackAtTheFirstCell_holdsTheFirstCellOnTheSnapPosition() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager, ITEM_COUNT, CELL_HEIGHT);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), SNAP_POSITION_TOP, before.topOfAdapterPosition(FIRST_ITEM));

        flingBack(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "there is nothing before the first cell to page to: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM));
        assertEquals(
                "and nothing behind it should have been drawn: " + after,
                FIRST_ITEM,
                after.lowestAdapterPosition());
    }

    @Test
    public void pagingForwardAtTheLastCell_holdsTheLastCellOnTheSnapPosition() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager, ITEMS_TO_THE_END, CELL_HEIGHT);
        for (int page = 0; page < LAST_ITEM_TO_THE_END; page++) {
            flingForward(harness);
            harness.settle();
        }
        final LaidOutChildren atTheEnd = harness.settle();
        assertEquals(
                "paging to the end should rest the last cell on the snap position: " + atTheEnd,
                SNAP_POSITION_TOP,
                atTheEnd.topOfAdapterPosition(LAST_ITEM_TO_THE_END));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the last cell should still be on the snap position: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(LAST_ITEM_TO_THE_END));
    }

    /**
     * An interval past the end of the adapter is capped at the adapter's last cell, the same way
     * the fling snap extrapolation is capped, so a gesture cannot ask for a cell that does not
     * exist.
     */
    @Test
    public void anIntervalLargerThanTheAdapter_stopsAtTheLastCellInsteadOfRunningOff() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_interval_beyond_adapter,
                        ITEMS_TO_THE_END,
                        CELL_HEIGHT);

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the page should stop on the last cell: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(LAST_ITEM_TO_THE_END));
    }

    /**
     * With circular scrolling the cap comes off, so paging forward from the last cell comes back
     * round to the first instead of holding still.
     */
    @Test
    public void circularPagingForwardPastTheLastCell_wrapsRoundToTheFirstCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_circular,
                        CIRCULAR_ITEM_COUNT,
                        CELL_HEIGHT);
        for (int page = 0; page < LAST_CIRCULAR_ITEM; page++) {
            flingForward(harness);
            harness.settle();
        }
        final LaidOutChildren atTheLastCell = harness.settle();
        assertEquals(
                "paging should have reached the last cell: " + atTheLastCell,
                SNAP_POSITION_TOP,
                atTheLastCell.topOfAdapterPosition(LAST_CIRCULAR_ITEM));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one more page should come back round to the first cell: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    @Test
    public void circularPagingBackFromTheFirstCell_wrapsRoundToTheLastCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_circular,
                        CIRCULAR_ITEM_COUNT,
                        CELL_HEIGHT);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), SNAP_POSITION_TOP, before.topOfAdapterPosition(FIRST_ITEM));

        flingBack(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "paging back from the first cell should wrap to the last: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(LAST_CIRCULAR_ITEM));
    }

    /** A wrapped position is a layout-engine index and must never reach the adapter. */
    @Test
    public void circularPaging_neverAsksTheAdapterForAWrappedPosition() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager_circular);
        final PositionRecordingAdapter adapter =
                new PositionRecordingAdapter(
                        context, CIRCULAR_ITEM_COUNT, MATCH_PARENT, CELL_HEIGHT);
        harness.setAdapter(adapter);

        for (int page = 0; page <= CIRCULAR_ITEM_COUNT; page++) {
            flingForward(harness);
            harness.settle();
        }

        assertTrue(
                "the adapter was asked for " + adapter.requestedRange(),
                adapter.lowestRequestedPosition() >= FIRST_ITEM);
        assertTrue(
                "the adapter was asked for " + adapter.requestedRange(),
                adapter.highestRequestedPosition() <= LAST_CIRCULAR_ITEM);
    }

    private void flingForward(final ParchmentViewHarness<ListView<BaseAdapter>> harness) {
        harness.fling(GESTURE_X, GESTURE_START_Y, GESTURE_X, GESTURE_END_Y, GESTURE_STEPS);
    }

    private void flingBack(final ParchmentViewHarness<ListView<BaseAdapter>> harness) {
        harness.fling(GESTURE_X, GESTURE_END_Y, GESTURE_X, GESTURE_START_Y, GESTURE_STEPS);
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(
            final int layoutResource, final int itemCount, final int itemHeight) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attach(layoutResource);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, itemCount, MATCH_PARENT, itemHeight);
        harness.setAdapter(adapter);
        return harness;
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attach(final int layoutResource) {
        return ParchmentViewHarness.attach(
                mActivityRule.getScenario(), layoutResource, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }
}
