// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves on a real framework that a ViewPager gesture with parchment_viewPagerInterval left at
 * viewport advances by the run of cells that fit the viewport whole, so pages do not overlap and
 * each cell is shown once per pass. This is the paging Parchment has always done and it is still
 * the default; an interval of one or more is the carousel that ViewPagerIntervalInstrumentedTest
 * covers.
 */
@RunWith(AndroidJUnit4.class)
public final class ViewPagerViewportInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int CELL_HEIGHT = 200;
    private static final int CELLS_ON_SCREEN = VIEWPORT_HEIGHT / CELL_HEIGHT;
    private static final int UNEVEN_CELL_HEIGHT = 250;
    private static final int UNEVEN_CELLS_ON_SCREEN = VIEWPORT_HEIGHT / UNEVEN_CELL_HEIGHT;
    private static final int TALL_CELL_HEIGHT = 200;
    private static final int SHORT_CELL_HEIGHT = 100;
    private static final int ALTERNATING_CELLS_ON_SCREEN = 4;
    private static final int OVERSIZED_CELL_HEIGHT = 900;
    private static final int CELL_WIDTH = 300;
    private static final int CELLS_ACROSS = VIEWPORT_WIDTH / CELL_WIDTH;
    private static final int VIEWPORT_PADDING = 50;
    private static final int PADDED_VIEWPORT_HEIGHT = VIEWPORT_HEIGHT - 2 * VIEWPORT_PADDING;
    private static final int PADDED_CELLS_ON_SCREEN = PADDED_VIEWPORT_HEIGHT / CELL_HEIGHT;

    private static final int ITEM_COUNT = 10;
    private static final int ITEMS_TO_A_SHORT_LAST_PAGE = 5;
    private static final int LAST_OF_THE_SHORT_LAST_PAGE = ITEMS_TO_A_SHORT_LAST_PAGE - 1;
    private static final int CIRCULAR_ITEM_COUNT = 4;
    private static final int LAST_CIRCULAR_ITEM = CIRCULAR_ITEM_COUNT - 1;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int SNAP_POSITION_TOP = 0;
    private static final int SNAP_POSITION_START = 0;

    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int GESTURE_START_Y = 500;
    private static final int GESTURE_END_Y = 100;
    private static final int GESTURE_Y = VIEWPORT_HEIGHT / 2;
    private static final int GESTURE_START_X = 800;
    private static final int GESTURE_END_X = 200;
    private static final int GESTURE_STEPS = 4;

    private static final int FIRST_ITEM = 0;
    private static final int SECOND_ITEM = 1;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void aFlingWithThreeCellsVisible_advancesAllThreeOfThem() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager_viewport, ITEM_COUNT, CELL_HEIGHT);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "three cells should fill the viewport: " + before,
                VIEWPORT_HEIGHT - CELL_HEIGHT,
                before.topOfAdapterPosition(CELLS_ON_SCREEN - 1));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the fourth cell to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(CELLS_ON_SCREEN));
        assertEquals(
                "the content should have moved by a whole viewport: " + after,
                -CELL_HEIGHT,
                after.topOfAdapterPosition(CELLS_ON_SCREEN - 1));
    }

    @Test
    public void aFlingBack_returnsTheWholeViewportItCameOver() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager_viewport, ITEM_COUNT, CELL_HEIGHT);
        flingForward(harness);
        final LaidOutChildren forward = harness.settle();
        assertEquals(
                "the forward fling should have paged a viewport: " + forward,
                SNAP_POSITION_TOP,
                forward.topOfAdapterPosition(CELLS_ON_SCREEN));

        flingBack(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling back should return the whole viewport: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(FIRST_ITEM));
    }

    /**
     * Cells that do not divide the viewport leave a partial cell behind rather than counting it:
     * two 250px cells fit a 600px viewport whole and the third does not, so the page is 500px.
     */
    @Test
    public void cellsThatDoNotDivideTheViewport_leaveThePartialCellForTheNextPage() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_viewport, ITEM_COUNT, UNEVEN_CELL_HEIGHT);

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the third cell should come to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(UNEVEN_CELLS_ON_SCREEN));
        assertEquals(
                "the page should be the two whole cells only: " + after,
                -UNEVEN_CELL_HEIGHT,
                after.topOfAdapterPosition(UNEVEN_CELLS_ON_SCREEN - 1));
    }

    /**
     * Cells of unequal size are measured, not counted: 200, 100, 200 and 100 fill exactly 600, so
     * four of them make one page where a fixed cell size would give three or six.
     */
    @Test
    public void cellsOfUnequalSize_advanceByTheCellsThatFitWhole() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager_viewport);
        final AlternatingHeightAdapter adapter =
                new AlternatingHeightAdapter(
                        context, ITEM_COUNT, MATCH_PARENT, TALL_CELL_HEIGHT, SHORT_CELL_HEIGHT);
        harness.setAdapter(adapter);

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the fifth cell should come to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(ALTERNATING_CELLS_ON_SCREEN));
        assertEquals(
                "the page should be the four cells that fit whole: " + after,
                -SHORT_CELL_HEIGHT,
                after.topOfAdapterPosition(ALTERNATING_CELLS_ON_SCREEN - 1));
    }

    /**
     * A cell taller than the viewport is the first cell that does not fit, so the page falls back
     * to the one cell it always advances at least.
     */
    @Test
    public void aCellTallerThanTheViewport_advancesExactlyThatOneCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_viewport,
                        ITEM_COUNT,
                        OVERSIZED_CELL_HEIGHT);
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
     * The last page is short when the cells left do not fill a viewport: five 200px cells page
     * three, then the last two, rather than running off the end.
     */
    @Test
    public void aPartialPageAtTheEnd_advancesOnlyAsFarAsTheLastCell() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_viewport,
                        ITEMS_TO_A_SHORT_LAST_PAGE,
                        CELL_HEIGHT);
        flingForward(harness);
        final LaidOutChildren firstPage = harness.settle();
        assertEquals(
                "the first page should be the three cells that fit: " + firstPage,
                SNAP_POSITION_TOP,
                firstPage.topOfAdapterPosition(CELLS_ON_SCREEN));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the last page should stop on the last cell: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(LAST_OF_THE_SHORT_LAST_PAGE));
    }

    @Test
    public void pagingForwardAtTheLastCell_holdsTheLastCellOnTheSnapPosition() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_viewport,
                        ITEMS_TO_A_SHORT_LAST_PAGE,
                        CELL_HEIGHT);
        flingForward(harness);
        harness.settle();
        flingForward(harness);
        final LaidOutChildren atTheEnd = harness.settle();
        assertEquals(
                "paging to the end should rest the last cell on the snap position: " + atTheEnd,
                SNAP_POSITION_TOP,
                atTheEnd.topOfAdapterPosition(LAST_OF_THE_SHORT_LAST_PAGE));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the last cell should still be on the snap position: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(LAST_OF_THE_SHORT_LAST_PAGE));
    }

    @Test
    public void pagingBackAtTheFirstCell_holdsTheFirstCellOnTheSnapPosition() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_view_pager_viewport, ITEM_COUNT, CELL_HEIGHT);

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

    /**
     * The fit is measured against the size inside the padding, so a padded viewport pages fewer
     * cells than its full height would allow: 500px of content fits two 200px cells, not three.
     */
    @Test
    public void viewportPagingWithPadding_fitsTheCellsInsideThePaddingOnly() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager_viewport);
        harness.apply(new SetViewportPadding<ListView<BaseAdapter>>(VIEWPORT_PADDING));
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, MATCH_PARENT, CELL_HEIGHT);
        harness.setAdapter(adapter);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "the first cell should rest inside the padding: " + before,
                VIEWPORT_PADDING,
                before.topOfAdapterPosition(FIRST_ITEM));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "only two cells fit inside the padding, so the third comes to the top: " + after,
                VIEWPORT_PADDING,
                after.topOfAdapterPosition(PADDED_CELLS_ON_SCREEN));
    }

    @Test
    public void viewportPagingInAHorizontalList_advancesAWholeViewport() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager_viewport_horizontal);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, CELL_WIDTH, MATCH_PARENT);
        harness.setAdapter(adapter);
        final LaidOutChildren before = harness.children();
        assertEquals(
                "three cells should fill the viewport across: " + before,
                VIEWPORT_WIDTH - CELL_WIDTH,
                leftOfAdapterPosition(before, CELLS_ACROSS - 1));

        harness.fling(GESTURE_START_X, GESTURE_Y, GESTURE_END_X, GESTURE_Y, GESTURE_STEPS);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should bring the fourth cell to the start: " + after,
                SNAP_POSITION_START,
                leftOfAdapterPosition(after, CELLS_ACROSS));
    }

    /**
     * Circular scrolling lifts the cap at the adapter's end, so a viewport page keeps advancing
     * past the last cell instead of holding still the way the bounded list does.
     */
    @Test
    public void circularViewportPaging_pastTheLastCell_keepsAdvancing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(
                        R.layout.instrumented_view_pager_viewport_circular,
                        CIRCULAR_ITEM_COUNT,
                        CELL_HEIGHT);
        flingForward(harness);
        final LaidOutChildren firstPage = harness.settle();
        assertEquals(
                "the first page should reach the last cell: " + firstPage,
                SNAP_POSITION_TOP,
                firstPage.topOfAdapterPosition(LAST_CIRCULAR_ITEM));

        flingForward(harness);
        final LaidOutChildren after = harness.settle();

        assertFalse(
                "a second page should have carried on past the last cell: " + after,
                after.isAdapterPositionAtTop(LAST_CIRCULAR_ITEM, SNAP_POSITION_TOP));
        assertTrue(
                "and it should have wrapped round rather than stopped: " + after,
                after.hasChildWithTop(SNAP_POSITION_TOP));
    }

    private static int leftOfAdapterPosition(
            final LaidOutChildren children, final int adapterPosition) {
        final int index = children.indexOfAdapterPosition(adapterPosition);
        if (index < 0) {
            throw new AssertionError(
                    "adapter position " + adapterPosition + " is not laid out: " + children);
        }
        return children.left(index);
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

    private static final class SetViewportPadding<VIEW extends android.view.View>
            implements ParchmentViewHarness.ViewSetup<VIEW> {

        private final int mPadding;

        private SetViewportPadding(final int padding) {
            mPadding = padding;
        }

        @Override
        public void setUp(final VIEW view) {
            view.setPadding(mPadding, mPadding, mPadding, mPadding);
        }
    }
}
