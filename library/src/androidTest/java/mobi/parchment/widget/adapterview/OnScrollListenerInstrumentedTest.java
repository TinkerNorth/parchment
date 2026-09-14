// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mobi.parchment.harness.AttachScrollListener;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.harness.RecordingScrollListener;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves setOnScrollListener on a real framework: real gestures through the platform
 * GestureDetector, real animation frames, and the callbacks compared against where the children
 * actually landed.
 *
 * <p>The content's movement is read off the children rather than trusted: every cell here is the
 * same size with no spacing, so any laid-out child gives the content's offset as its top minus its
 * cell index times the cell size, and the movement is the change in that offset. That survives the
 * recycling a fling does, which a tracked child would not.
 *
 * <p>A drag is 300px on 200px cells, so the snap it leaves behind is about 100px and outlives
 * several frames: `settling` is reported at the end of the first frame after the release, and a
 * snap of a few pixels can finish before that frame comes on a busy emulator.
 */
@RunWith(AndroidJUnit4.class)
public final class OnScrollListenerInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = 200;
    private static final int ITEM_COUNT = 20;
    private static final int SHORT_ITEM_COUNT = 8;
    private static final int ONE_POSITION_PER_CELL = 1;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int NO_MOVEMENT = 0;
    private static final int NOTHING_REPORTED = 0;

    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int DRAG_FROM_Y = 500;
    private static final int DRAG_DISTANCE = 300;
    private static final int DRAG_TO_Y = DRAG_FROM_Y - DRAG_DISTANCE;
    private static final int DRAG_STEPS = 8;
    private static final int FLING_FROM_Y = 500;
    private static final int FLING_TO_Y = 100;
    private static final int FLING_STEPS = 4;
    private static final int UNDER_THE_SLOP_STEPS = 2;
    private static final int TAPPED_CELL = 2;
    private static final int TAP_Y = TAPPED_CELL * ITEM_HEIGHT + ITEM_HEIGHT / 2;
    private static final int SNAPPED_CELL_Y = ITEM_HEIGHT / 2;
    private static final int SELECTED_CELL = 5;
    private static final int CELL_TO_DRAG_BACK_FROM = 10;
    private static final int SNAP_POSITION_TOP = 0;
    private static final int LAST_SHORT_CELL = SHORT_ITEM_COUNT - 1;
    private static final int DISTANCE_TO_THE_LAST_SHORT_CELL = LAST_SHORT_CELL * ITEM_HEIGHT;
    private static final int TRACKED_CIRCULAR_CELL = 2;

    private static final int GRID_ITEM_COUNT = 30;
    private static final int VIEWS_PER_ROW = 3;
    private static final int GRID_ITEM_WIDTH = VIEWPORT_WIDTH / VIEWS_PER_ROW;
    private static final int ROW_HEIGHT = 200;

    private static final int PATTERN_VIEWPORT_WIDTH = 600;
    private static final int UNITS_ACROSS = 3;
    private static final int UNIT_SIZE = PATTERN_VIEWPORT_WIDTH / UNITS_ACROSS;
    private static final int PATTERN_ITEM_COUNT = 30;

    private static final List<ScrollState> DRAG_SETTLE_REST =
            Arrays.asList(ScrollState.dragging, ScrollState.settling, ScrollState.idle);
    private static final List<ScrollState> SETTLE_REST =
            Arrays.asList(ScrollState.settling, ScrollState.idle);
    private static final List<ScrollState> DRAG_REST =
            Arrays.asList(ScrollState.dragging, ScrollState.idle);
    private static final List<ScrollState> DRAG_ONLY =
            Collections.singletonList(ScrollState.dragging);

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void aDrag_reportsDraggingThenSettlingThenIdleOnceEachInOrder() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);

        drag(harness);
        harness.settle();

        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
        assertEquals(
                "every callback should arrive on the main thread: " + listener,
                0,
                listener.callsOffTheMainThread());
    }

    @Test
    public void aDragTowardTheEnd_reportsANegativeSumEqualToTheDistanceTheContentMoved() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        drag(harness);
        final LaidOutChildren after = harness.settle();

        final int movement = listMovement(before, after);
        assertTrue("the content should have moved toward the end: " + after, movement < 0);
        assertEquals(listener.toString(), movement, listener.displacementSum());
    }

    @Test
    public void aDragBackTowardTheStart_reportsAPositiveSumEqualToTheDistanceTheContentMoved() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        harness.setSelection(CELL_TO_DRAG_BACK_FROM);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        harness.dragAndRelease(GESTURE_X, DRAG_TO_Y, GESTURE_X, DRAG_FROM_Y, DRAG_STEPS);
        final LaidOutChildren after = harness.settle();

        final int movement = listMovement(before, after);
        assertTrue("the content should have moved toward the start: " + after, movement > 0);
        assertEquals(listener.toString(), movement, listener.displacementSum());
    }

    @Test
    public void aFling_reportsDraggingThenSettlingThenIdleAndASumEqualToTheMovement() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        fling(harness);
        final LaidOutChildren after = harness.settle();

        final int movement = listMovement(before, after);
        assertTrue("the fling should have moved the content: " + after, movement < 0);
        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
        assertEquals(listener.toString(), movement, listener.displacementSum());
    }

    @Test
    public void aTapOnACellOffTheSnapPosition_reportsSettlingThenIdleAndNeverDragging() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        harness.tap(GESTURE_X, TAP_Y);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the tapped cell should have been snapped to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(TAPPED_CELL));
        assertEquals(listener.toString(), SETTLE_REST, listener.states());
        assertEquals(listener.toString(), listMovement(before, after), listener.displacementSum());
    }

    @Test
    public void setSelectionFromRest_movesTheContentAndReportsNothing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);

        harness.setSelection(SELECTED_CELL);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the selected cell should have been jumped to the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(SELECTED_CELL));
        assertEquals(listener.toString(), NOTHING_REPORTED, listener.calls().size());
    }

    @Test
    public void aGestureUnderTheTouchSlopOnTheSnappedCell_reportsNothing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();
        final int underTheSlop = harness.touchSlop() / 2;

        harness.dragAndRelease(
                GESTURE_X,
                SNAPPED_CELL_Y,
                GESTURE_X,
                SNAPPED_CELL_Y + underTheSlop,
                UNDER_THE_SLOP_STEPS);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "nothing should have moved: " + after, NO_MOVEMENT, listMovement(before, after));
        assertEquals(listener.toString(), NOTHING_REPORTED, listener.calls().size());
    }

    @Test
    public void aFlingRefusedAtTheStart_movesNothingAndReportsNothing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        harness.fling(GESTURE_X, FLING_TO_Y, GESTURE_X, FLING_FROM_Y, FLING_STEPS);
        final LaidOutChildren after = harness.settle();

        assertEquals("the start should hold: " + after, NO_MOVEMENT, listMovement(before, after));
        assertEquals(listener.toString(), NOTHING_REPORTED, listener.calls().size());
    }

    @Test
    public void aFlingClampedAtTheEnd_reportsOnlyTheDistanceThatLanded() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(SHORT_ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        fling(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the fling should be held with the last cell at the top: " + after,
                SNAP_POSITION_TOP,
                after.topOfAdapterPosition(LAST_SHORT_CELL));
        assertEquals(
                "the content can move no further than the last cell: " + after,
                -DISTANCE_TO_THE_LAST_SHORT_CELL,
                listMovement(before, after));
        assertEquals(
                listener.toString(), -DISTANCE_TO_THE_LAST_SHORT_CELL, listener.displacementSum());
        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
    }

    @Test
    public void aListenerAttachedMidDrag_isToldDraggingThenTheRestOfTheGesture() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = new RecordingScrollListener();

        harness.dragAndRelease(
                GESTURE_X,
                DRAG_FROM_Y,
                GESTURE_X,
                DRAG_TO_Y,
                DRAG_STEPS,
                new AttachScrollListener<ListView<BaseAdapter>>(listener));
        harness.settle();

        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
    }

    @Test
    public void aListenerReplacedMidDrag_theReplacementIsToldOnlyWhatFollows() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener first = listenTo(harness);
        final RecordingScrollListener replacement = new RecordingScrollListener();

        harness.dragAndRelease(
                GESTURE_X,
                DRAG_FROM_Y,
                GESTURE_X,
                DRAG_TO_Y,
                DRAG_STEPS,
                new AttachScrollListener<ListView<BaseAdapter>>(replacement));
        harness.settle();

        assertEquals(first.toString(), DRAG_ONLY, first.states());
        assertEquals(replacement.toString(), SETTLE_REST, replacement.states());
    }

    @Test
    public void aListenerAttachedMidFling_isToldSettlingThenIdle() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = new RecordingScrollListener();

        fling(harness);
        harness.apply(new AttachScrollListener<ListView<BaseAdapter>>(listener));
        harness.settle();

        assertEquals(listener.toString(), SETTLE_REST, listener.states());
    }

    @Test
    public void aListenerRemovedMidFling_hearsNothingMore() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness = attachList(ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);

        fling(harness);
        harness.apply(new AttachScrollListener<ListView<BaseAdapter>>(null));
        final List<String> callsAtRemoval = listener.calls();
        harness.settle();

        assertTrue(
                "the fling should have been reported before the removal: " + listener,
                callsAtRemoval.size() > 0);
        assertEquals(listener.toString(), callsAtRemoval, listener.calls());
    }

    @Test
    public void aGridView_reportsTheGestureAndTheDistanceTheRowsMoved() {
        final ParchmentViewHarness<GridView<BaseAdapter>> harness = attachGrid();
        final RecordingScrollListener listener = new RecordingScrollListener();
        harness.apply(new AttachScrollListener<GridView<BaseAdapter>>(listener));
        final LaidOutChildren before = harness.children();

        drag(harness);
        final LaidOutChildren after = harness.settle();

        final int movement = movement(before, after, VIEWS_PER_ROW, ROW_HEIGHT);
        assertTrue("the rows should have moved toward the end: " + after, movement < 0);
        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
        assertEquals(listener.toString(), movement, listener.displacementSum());
    }

    @Test
    public void aGridPatternView_reportsTheGestureAndTheDistanceTheGroupsMoved() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachPattern();
        final RecordingScrollListener listener = new RecordingScrollListener();
        harness.apply(new AttachScrollListener<GridPatternView<BaseAdapter>>(listener));
        final LaidOutChildren before = harness.children();

        drag(harness);
        final LaidOutChildren after = harness.settle();

        final int movement = movement(before, after, UNITS_ACROSS, UNIT_SIZE);
        assertTrue("the groups should have moved toward the end: " + after, movement < 0);
        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
        assertEquals(listener.toString(), movement, listener.displacementSum());
    }

    @Test
    public void circularScroll_aDrag_reportsTheDistanceATrackedCellMoved() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_circular, SHORT_ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);
        final LaidOutChildren before = harness.children();

        drag(harness);
        final LaidOutChildren after = harness.settle();

        final int movement =
                after.topOfAdapterPosition(TRACKED_CIRCULAR_CELL)
                        - before.topOfAdapterPosition(TRACKED_CIRCULAR_CELL);
        assertTrue("the content should have moved toward the end: " + after, movement < 0);
        assertEquals(listener.toString(), movement, listener.displacementSum());
        assertEquals(listener.toString(), DRAG_REST, listener.states());
    }

    @Test
    public void circularScroll_aFling_reportsMovementPastWhereAListWouldStop() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_circular, SHORT_ITEM_COUNT);
        final RecordingScrollListener listener = listenTo(harness);

        fling(harness);
        harness.settle();

        assertTrue(
                "a circular fling should carry past the last cell: " + listener,
                listener.displacementSum() < -DISTANCE_TO_THE_LAST_SHORT_CELL);
        assertEquals(listener.toString(), DRAG_SETTLE_REST, listener.states());
    }

    private static void drag(final ParchmentViewHarness<?> harness) {
        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
    }

    private static void fling(final ParchmentViewHarness<?> harness) {
        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
    }

    private static int listMovement(final LaidOutChildren before, final LaidOutChildren after) {
        return movement(before, after, ONE_POSITION_PER_CELL, ITEM_HEIGHT);
    }

    private static int movement(
            final LaidOutChildren before,
            final LaidOutChildren after,
            final int positionsPerCell,
            final int cellSize) {
        final int offsetBefore = contentOffset(before, positionsPerCell, cellSize);
        final int offsetAfter = contentOffset(after, positionsPerCell, cellSize);
        return offsetAfter - offsetBefore;
    }

    private static int contentOffset(
            final LaidOutChildren children, final int positionsPerCell, final int cellSize) {
        final int firstChild = 0;
        final int cellIndex = children.adapterPosition(firstChild) / positionsPerCell;
        return children.top(firstChild) - cellIndex * cellSize;
    }

    private static RecordingScrollListener listenTo(
            final ParchmentViewHarness<ListView<BaseAdapter>> harness) {
        final RecordingScrollListener listener = new RecordingScrollListener();
        harness.apply(new AttachScrollListener<ListView<BaseAdapter>>(listener));
        return listener;
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(final int itemCount) {
        return attach(R.layout.instrumented_scroll_listener, itemCount);
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attach(
            final int layoutResource, final int itemCount) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, itemCount, MATCH_PARENT, ITEM_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }

    private ParchmentViewHarness<GridView<BaseAdapter>> attachGrid() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_scroll_listener_grid,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, GRID_ITEM_COUNT, GRID_ITEM_WIDTH, ROW_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachPattern() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_scroll_listener_pattern,
                        PATTERN_VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AddARowOfUnitSquares());
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, PATTERN_ITEM_COUNT, MATCH_PARENT, MATCH_PARENT);
        harness.setAdapter(adapter);
        return harness;
    }

    /**
     * A pattern group of one row of unit squares: the group is one unit tall, three fill the view.
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
