// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.View;
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
import mobi.parchment.harness.VisibleSurface;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The inherited {@code android.widget.AdapterView} surface on a real framework: what the three
 * views report after real gestures, across the circular wrap point, and while an empty view is
 * attached.
 *
 * <p>No expectation here is a pixel the engine is supposed to produce: each is derived from where
 * the children actually landed in the same layout pass, so the numbers hold on any density and on
 * either emulator. The one literal, {@code PADDING}, is the padding the layout asks for in {@code
 * px} and is what the viewport box is measured from.
 */
@RunWith(AndroidJUnit4.class)
public final class AdapterViewSurfaceInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = 200;
    private static final int ITEM_COUNT = 20;
    private static final int GRID_ITEM_COUNT = 30;
    private static final int VIEWS_PER_ROW = 3;
    private static final int GRID_ITEM_WIDTH = VIEWPORT_WIDTH / VIEWS_PER_ROW;
    private static final int ROW_HEIGHT = 200;
    private static final int PATTERN_ITEM_COUNT = 30;
    private static final int PATTERN_UNITS_ACROSS = 3;
    private static final int PATTERN_VIEWPORT_WIDTH = 600;
    private static final int CIRCULAR_ITEM_COUNT = 5;
    private static final int PADDING = 50;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int NO_ITEMS = 0;
    private static final int INVALID_POSITION = android.widget.AdapterView.INVALID_POSITION;
    private static final int ONE_POSITION_PER_CELL = 1;

    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int PATTERN_GESTURE_X = PATTERN_VIEWPORT_WIDTH / 2;
    private static final int DRAG_FROM_Y = 500;
    private static final int DRAG_TO_Y = 200;
    private static final int DRAG_STEPS = 8;
    private static final int FLING_FROM_Y = 500;
    private static final int FLING_TO_Y = 100;
    private static final int FLING_STEPS = 4;
    private static final int WRAPS_TO_CROSS = 3;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void aListView_onTheFirstLayout_reportsTheCountAndTheCellsOnScreen() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();

        assertEquals("the count should be the adapter's: " + surface, ITEM_COUNT, surface.count());
        assertEquals(surface + " against " + children, 0, surface.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(surface, children, ONE_POSITION_PER_CELL);
    }

    @Test
    public void aListView_afterADragTowardTheEnd_reportsTheCellsThatCameOnScreen() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final VisibleSurface before = harness.surface();

        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
        harness.settle();

        final VisibleSurface after = harness.surface();
        final LaidOutChildren children = harness.children();
        assertTrue(
                "the drag should have moved the range on: " + before + " then " + after,
                after.firstVisiblePosition() > before.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(after, children, ONE_POSITION_PER_CELL);
    }

    @Test
    public void aListView_afterAFlingTowardTheEnd_reportsTheCellsThatCameOnScreen() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final VisibleSurface before = harness.surface();

        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        harness.settle();

        final VisibleSurface after = harness.surface();
        final LaidOutChildren children = harness.children();
        assertTrue(
                "the fling should have moved the range on: " + before + " then " + after,
                after.firstVisiblePosition() > before.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(after, children, ONE_POSITION_PER_CELL);
    }

    @Test
    public void aListView_draggedBackToTheStart_reportsTheFirstPositionAgain() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        harness.settle();

        harness.fling(GESTURE_X, FLING_TO_Y, GESTURE_X, FLING_FROM_Y, FLING_STEPS);
        harness.settle();
        harness.fling(GESTURE_X, FLING_TO_Y, GESTURE_X, FLING_FROM_Y, FLING_STEPS);
        harness.settle();

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();
        assertEquals("back at the start: " + surface, 0, surface.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(surface, children, ONE_POSITION_PER_CELL);
    }

    @Test
    public void aListView_withPadding_leavesOutTheCellsBehindThePadding() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_surface_padded_list, ITEM_COUNT);

        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
        harness.settle();

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();
        assertVisibleRangeInsideThePadding(surface, children, ONE_POSITION_PER_CELL);
    }

    @Test
    public void aListView_withAnEmptyAdapter_reportsNoCountAndNoVisiblePositions() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(NO_ITEMS);

        final VisibleSurface surface = harness.surface();

        assertEquals(surface.toString(), NO_ITEMS, surface.count());
        assertEquals(surface.toString(), 0, surface.firstVisiblePosition());
        assertEquals(surface.toString(), INVALID_POSITION, surface.lastVisiblePosition());
    }

    @Test
    public void aGridView_reportsThePositionsInsideTheVisibleRowsNotTheChildren() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness = attachGrid();

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();

        assertEquals(surface.toString(), GRID_ITEM_COUNT, surface.count());
        assertEquals(surface.toString(), 0, surface.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(surface, children, VIEWS_PER_ROW);
    }

    @Test
    public void aGridView_afterAFling_reportsThePositionsInsideTheVisibleRows() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness = attachGrid();
        final VisibleSurface before = harness.surface();

        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        harness.settle();

        final VisibleSurface after = harness.surface();
        final LaidOutChildren children = harness.children();
        assertTrue(
                "the fling should have moved the range on: " + before + " then " + after,
                after.firstVisiblePosition() > before.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(after, children, VIEWS_PER_ROW);
    }

    @Test
    public void aGridPatternView_reportsThePositionsInsideTheVisibleGroups() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachPattern();

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();

        assertEquals(surface.toString(), PATTERN_ITEM_COUNT, surface.count());
        assertEquals(surface.toString(), 0, surface.firstVisiblePosition());
        assertVisibleRangeCoversTheChildrenOnScreen(surface, children);
    }

    @Test
    public void aGridPatternView_afterAFling_reportsThePositionsInsideTheVisibleGroups() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachPattern();
        final VisibleSurface before = harness.surface();

        harness.fling(PATTERN_GESTURE_X, FLING_FROM_Y, PATTERN_GESTURE_X, FLING_TO_Y, FLING_STEPS);
        harness.settle();

        final VisibleSurface after = harness.surface();
        final LaidOutChildren children = harness.children();
        assertTrue(
                "the fling should have moved the range on: " + before + " then " + after,
                after.firstVisiblePosition() > before.firstVisiblePosition());
        assertVisibleRangeCoversTheChildrenOnScreen(after, children);
    }

    @Test
    public void circularScroll_selectedOntoTheLastCell_reportsRealPositionsAcrossTheWrap() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachCircular();

        harness.setSelection(CIRCULAR_ITEM_COUNT - 1);

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();
        assertEquals(
                "the selected cell should be at the start edge: " + children,
                0,
                children.topOfAdapterPosition(CIRCULAR_ITEM_COUNT - 1));
        assertEquals("the wrap should follow it: " + children, 0, children.adapterPosition(1));
        assertPositionsAreInsideTheAdapter(surface);
        assertTrue(
                "across the wrap the first position is past the last: " + surface,
                surface.firstVisiblePosition() > surface.lastVisiblePosition());
        assertEquals(surface.toString(), CIRCULAR_ITEM_COUNT - 1, surface.firstVisiblePosition());
    }

    @Test
    public void circularScroll_flungOnAndOn_neverReportsAPositionOutsideTheAdapter() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachCircular();

        for (int crossing = 0; crossing < WRAPS_TO_CROSS; crossing++) {
            harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
            harness.settle();

            assertPositionsAreInsideTheAdapter(harness.surface());
        }
    }

    @Test
    public void circularScroll_scrolledBack_stillReportsRealAdapterPositions() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachCircular();

        for (int crossing = 0; crossing < WRAPS_TO_CROSS; crossing++) {
            harness.fling(GESTURE_X, FLING_TO_Y, GESTURE_X, FLING_FROM_Y, FLING_STEPS);
            harness.settle();

            assertPositionsAreInsideTheAdapter(harness.surface());
        }
    }

    @Test
    public void anEmptyAdapter_showsTheEmptyViewAndHidesTheAdapterView() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachWithAnEmptyView();

        harness.setAdapter(resizableAdapter(NO_ITEMS));

        assertEquals(View.GONE, harness.surface().visibility());
        assertEquals(View.VISIBLE, harness.visibilityOf(R.id.harness_empty_view));
    }

    @Test
    public void anAdapterWithItems_hidesTheEmptyViewAndShowsTheAdapterView() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachWithAnEmptyView();

        harness.setAdapter(resizableAdapter(ITEM_COUNT));

        assertEquals(View.VISIBLE, harness.surface().visibility());
        assertEquals(View.GONE, harness.visibilityOf(R.id.harness_empty_view));
    }

    @Test
    public void aDataSetChangeThatEmptiesTheAdapter_showsTheEmptyViewOnARealFramework() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachWithAnEmptyView();
        final ResizableAdapter adapter = resizableAdapter(ITEM_COUNT);
        harness.setAdapter(adapter);

        harness.apply(new ChangeTheCount<ListView<BaseAdapter>>(adapter, NO_ITEMS));

        assertEquals(View.GONE, harness.surface().visibility());
        assertEquals(View.VISIBLE, harness.visibilityOf(R.id.harness_empty_view));
        assertEquals(NO_ITEMS, harness.surface().count());
    }

    @Test
    public void aDataSetChangeThatRefillsTheAdapter_hidesTheEmptyViewOnARealFramework() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachWithAnEmptyView();
        final ResizableAdapter adapter = resizableAdapter(NO_ITEMS);
        harness.setAdapter(adapter);
        assertEquals(View.GONE, harness.surface().visibility());

        harness.apply(new ChangeTheCount<ListView<BaseAdapter>>(adapter, ITEM_COUNT));

        assertEquals(View.VISIBLE, harness.surface().visibility());
        assertEquals(View.GONE, harness.visibilityOf(R.id.harness_empty_view));
        assertEquals(ITEM_COUNT, harness.surface().count());
    }

    @Test
    public void aDataSetChangeThatRefills_bringsTheCellsAndTheReportedRangeBack() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachWithAnEmptyView();
        final ResizableAdapter adapter = resizableAdapter(NO_ITEMS);
        harness.setAdapter(adapter);

        harness.apply(new ChangeTheCount<ListView<BaseAdapter>>(adapter, ITEM_COUNT));

        final VisibleSurface surface = harness.surface();
        final LaidOutChildren children = harness.children();
        assertTrue("the refill should draw cells: " + children, children.count() > 0);
        assertEquals(surface.toString(), 0, surface.firstVisiblePosition());
        assertVisibleRangeMatchesTheChildren(surface, children, ONE_POSITION_PER_CELL);
    }

    private static void assertPositionsAreInsideTheAdapter(final VisibleSurface surface) {
        final int lastPosition = surface.count() - 1;
        assertTrue(
                "the first visible position should be a real one: " + surface,
                surface.firstVisiblePosition() >= 0
                        && surface.firstVisiblePosition() <= lastPosition);
        assertTrue(
                "the last visible position should be a real one: " + surface,
                surface.lastVisiblePosition() >= 0
                        && surface.lastVisiblePosition() <= lastPosition);
    }

    /**
     * For a {@code ListView} and a {@code GridView} every child of a cell shares that cell's span
     * along the scroll axis, so the children that overlap the viewport are exactly the children of
     * the visible cells and the reported pair is their lowest and highest adapter position.
     */
    private static void assertVisibleRangeMatchesTheChildren(
            final VisibleSurface surface,
            final LaidOutChildren children,
            final int positionsPerCell) {
        final int lowest = lowestPositionOnScreen(children, 0, children.viewHeight());
        final int highest = highestPositionOnScreen(children, 0, children.viewHeight());
        assertEquals(
                "the first visible position should be the lowest one on screen: "
                        + surface
                        + " against "
                        + children,
                lowest,
                surface.firstVisiblePosition());
        assertEquals(
                "the last visible position should be the highest one on screen: "
                        + surface
                        + " against "
                        + children,
                highest,
                surface.lastVisiblePosition());
        assertEquals(
                "the range should be a whole number of cells of "
                        + positionsPerCell
                        + ": "
                        + surface,
                0,
                (surface.lastVisiblePosition() - surface.firstVisiblePosition() + 1)
                        % positionsPerCell);
    }

    private static void assertVisibleRangeInsideThePadding(
            final VisibleSurface surface,
            final LaidOutChildren children,
            final int positionsPerCell) {
        final int lowest =
                lowestPositionOnScreen(children, PADDING, children.viewHeight() - PADDING);
        final int highest =
                highestPositionOnScreen(children, PADDING, children.viewHeight() - PADDING);
        assertEquals(
                "the padding box decides what is visible: " + surface + " against " + children,
                lowest,
                surface.firstVisiblePosition());
        assertEquals(
                "the padding box decides what is visible: " + surface + " against " + children,
                highest,
                surface.lastVisiblePosition());
        assertEquals(
                "the range should be a whole number of cells of "
                        + positionsPerCell
                        + ": "
                        + surface,
                0,
                (surface.lastVisiblePosition() - surface.firstVisiblePosition() + 1)
                        % positionsPerCell);
    }

    /**
     * A {@code GridPatternView} group's items do not all share the group's span, so a group can be
     * visible while one of its items is not. The pair therefore covers every child on screen rather
     * than equalling its ends, and both of its own positions are laid out.
     */
    private static void assertVisibleRangeCoversTheChildrenOnScreen(
            final VisibleSurface surface, final LaidOutChildren children) {
        final int lowest = lowestPositionOnScreen(children, 0, children.viewHeight());
        final int highest = highestPositionOnScreen(children, 0, children.viewHeight());
        assertTrue(
                "the pair should cover every child on screen: " + surface + " against " + children,
                surface.firstVisiblePosition() <= lowest
                        && surface.lastVisiblePosition() >= highest);
        assertTrue(
                "the first visible position should be laid out: " + surface + " " + children,
                children.indexOfAdapterPosition(surface.firstVisiblePosition()) >= 0);
        assertTrue(
                "the last visible position should be laid out: " + surface + " " + children,
                children.indexOfAdapterPosition(surface.lastVisiblePosition()) >= 0);
    }

    private static int lowestPositionOnScreen(
            final LaidOutChildren children, final int viewportTop, final int viewportBottom) {
        int lowest = Integer.MAX_VALUE;
        for (int index = 0; index < children.count(); index++) {
            final boolean isOnScreen =
                    children.bottom(index) > viewportTop && children.top(index) < viewportBottom;
            if (isOnScreen) {
                lowest = Math.min(lowest, children.adapterPosition(index));
            }
        }
        if (lowest == Integer.MAX_VALUE) {
            throw new AssertionError("no child is on screen: " + children);
        }
        return lowest;
    }

    private static int highestPositionOnScreen(
            final LaidOutChildren children, final int viewportTop, final int viewportBottom) {
        int highest = INVALID_POSITION;
        for (int index = 0; index < children.count(); index++) {
            final boolean isOnScreen =
                    children.bottom(index) > viewportTop && children.top(index) < viewportBottom;
            if (isOnScreen) {
                highest = Math.max(highest, children.adapterPosition(index));
            }
        }
        if (highest == INVALID_POSITION) {
            throw new AssertionError("no child is on screen: " + children);
        }
        return highest;
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(final int itemCount) {
        return attachList(R.layout.instrumented_surface_list, itemCount);
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(
            final int layoutResource, final int itemCount) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.setAdapter(new FixedSizeAdapter(context, itemCount, MATCH_PARENT, ITEM_HEIGHT));
        return harness;
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachCircular() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_surface_circular,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.setAdapter(
                new FixedSizeAdapter(context, CIRCULAR_ITEM_COUNT, MATCH_PARENT, ITEM_HEIGHT));
        return harness;
    }

    private ParchmentViewHarness<GridView<BaseAdapter>> attachGrid() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_surface_grid,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.setAdapter(
                new FixedSizeAdapter(context, GRID_ITEM_COUNT, GRID_ITEM_WIDTH, ROW_HEIGHT));
        return harness;
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachPattern() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_surface_pattern,
                        PATTERN_VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AddARowOfUnitSquares());
        harness.setAdapter(
                new FixedSizeAdapter(context, PATTERN_ITEM_COUNT, MATCH_PARENT, MATCH_PARENT));
        return harness;
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachWithAnEmptyView() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attachInsideAParent(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_surface_empty_view,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AttachTheEmptyView());
        return harness;
    }

    private static ResizableAdapter resizableAdapter(final int count) {
        final Context context = ApplicationProvider.getApplicationContext();
        return new ResizableAdapter(context, count);
    }

    /** A pattern group of one row of unit squares: the group is one unit tall. */
    private static final class AddARowOfUnitSquares
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {

        private static final int ONE_UNIT = 1;

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final List<GridPatternItemDefinition> definitions =
                    new ArrayList<GridPatternItemDefinition>();
            for (int column = 0; column < PATTERN_UNITS_ACROSS; column++) {
                definitions.add(new GridPatternItemDefinition(0, column, ONE_UNIT, ONE_UNIT));
            }
            view.addGridPatternGroupDefinition(definitions);
        }
    }

    private static final class AttachTheEmptyView
            implements ParchmentViewHarness.ViewSetup<ListView<BaseAdapter>> {

        @Override
        public void setUp(final ListView<BaseAdapter> view) {
            final View emptyView = view.getRootView().findViewById(R.id.harness_empty_view);
            view.setEmptyView(emptyView);
        }
    }

    private static final class ChangeTheCount<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements ParchmentViewHarness.ViewSetup<VIEW> {

        private final ResizableAdapter mAdapter;
        private final int mCount;

        private ChangeTheCount(final ResizableAdapter adapter, final int count) {
            mAdapter = adapter;
            mCount = count;
        }

        @Override
        public void setUp(final VIEW view) {
            mAdapter.setCount(mCount);
        }
    }

    private static final class ResizableAdapter extends BaseAdapter {
        private final FixedSizeAdapter mItems;
        private int mCount;

        private ResizableAdapter(final Context context, final int count) {
            mItems = new FixedSizeAdapter(context, ITEM_COUNT, MATCH_PARENT, ITEM_HEIGHT);
            mCount = count;
        }

        private void setCount(final int count) {
            mCount = count;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(final int position) {
            return mItems.getItem(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            return mItems.getView(position, convertView, parent);
        }
    }
}
