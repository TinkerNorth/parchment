// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class GridPatternNoDefinitionInstrumentedTest {

    private static final int ITEM_COUNT = 30;
    private static final int GROWN_ITEM_COUNT = 40;
    private static final int SHRUNK_ITEM_COUNT = 3;
    private static final int NO_ITEMS = 0;
    private static final int FIRST_POSITION = 0;
    private static final int ON_SCREEN_POSITION = 1;
    private static final int OFF_SCREEN_POSITION = 15;
    private static final int LATE_POSITION = 25;
    private static final int LAST_POSITION = ITEM_COUNT - 1;
    private static final int NOTHING_SELECTED = AdapterView.INVALID_POSITION;
    private static final int NOT_LAID_OUT = -1;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int FIRST_ROW = 0;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int FLING_STEPS = 4;
    private static final int DRAG_STEPS = 8;
    private static final int START_EDGE = 0;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final int VIEWPORT_BREADTH = 600;
    private static final int VIEWPORT_SIZE = 900;
    private static final int GESTURE_ACROSS = VIEWPORT_BREADTH / 2;
    private static final int GESTURE_NEAR_THE_END = 700;
    private static final int GESTURE_NEAR_THE_START = 200;
    private static final Viewport VERTICAL_VIEWPORT =
            new Viewport(
                    VERTICAL,
                    VIEWPORT_BREADTH,
                    VIEWPORT_SIZE,
                    GESTURE_ACROSS,
                    GESTURE_NEAR_THE_END,
                    GESTURE_ACROSS,
                    GESTURE_NEAR_THE_START);
    private static final Viewport HORIZONTAL_VIEWPORT =
            new Viewport(
                    HORIZONTAL,
                    VIEWPORT_SIZE,
                    VIEWPORT_BREADTH,
                    GESTURE_NEAR_THE_END,
                    GESTURE_ACROSS,
                    GESTURE_NEAR_THE_START,
                    GESTURE_ACROSS);

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void noDefinition_vertical_firstLayout_isTheLayoutOfOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new NothingMore());
    }

    @Test
    public void noDefinition_horizontal_firstLayout_isTheLayoutOfOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new NothingMore());
    }

    @Test
    public void
            noDefinition_verticalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalCenterSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_center,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalEndSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_end,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalOnScreenSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_on_screen,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalCircular_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_circular,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalCenterSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_center,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalEndSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_end,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalOnScreenSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_on_screen,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalCircular_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            clearedDefinitions_verticalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertAClearedPatternRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            clearedDefinitions_horizontalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertAClearedPatternRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalDefault_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_default,
                VERTICAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_verticalCenterSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_center,
                VERTICAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_verticalStartSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_verticalEndSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_end,
                VERTICAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_verticalOnScreenSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_on_screen,
                VERTICAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_verticalCircular_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_circular,
                VERTICAL_VIEWPORT,
                new ShrinkACircleAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_horizontalDefault_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_default,
                HORIZONTAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_horizontalCenterSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_center,
                HORIZONTAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_horizontalEndSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_end,
                HORIZONTAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_horizontalOnScreenSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_on_screen,
                HORIZONTAL_VIEWPORT,
                new ShrinkAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_horizontalCircular_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new ShrinkACircleAfterScrollingOn(SHRUNK_ITEM_COUNT));
    }

    @Test
    public void noDefinition_verticalDefault_dataSetEmptied_clearsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_default,
                VERTICAL_VIEWPORT,
                new ShrinkAfterScrollingOn(NO_ITEMS));
    }

    @Test
    public void noDefinition_horizontalDefault_dataSetEmptied_clearsLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_default,
                HORIZONTAL_VIEWPORT,
                new ShrinkAfterScrollingOn(NO_ITEMS));
    }

    @Test
    public void noDefinition_verticalStartSnap_dataSetGrows_staysLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new GrowAfterScrollingOn(GROWN_ITEM_COUNT));
    }

    @Test
    public void noDefinition_horizontalStartSnap_dataSetGrows_staysLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new GrowAfterScrollingOn(GROWN_ITEM_COUNT));
    }

    @Test
    public void
            noDefinition_verticalStartSnap_restoredAfterSetSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertARestoredFallbackLandsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start, VERTICAL_VIEWPORT);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_restoredAfterSetSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertARestoredFallbackLandsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start, HORIZONTAL_VIEWPORT);
    }

    @Test
    public void
            noDefinition_verticalStartSnap_setSelectionOnScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(ON_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_setSelectionOnScreen_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(ON_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalDefault_setSelectionOffScreen_selectsNothingAndStaysLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_default,
                VERTICAL_VIEWPORT,
                new SelectOffScreenWithoutSnapping());
    }

    @Test
    public void
            noDefinition_horizontalDefault_setSelectionOffScreen_selectsNothingAndStaysLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_default,
                HORIZONTAL_VIEWPORT,
                new SelectOffScreenWithoutSnapping());
    }

    @Test
    public void
            noDefinition_verticalDefault_setSelectionOnScreen_selectsInPlaceLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_default,
                VERTICAL_VIEWPORT,
                new SetSelectionTo(ON_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalDefault_setSelectionOnScreen_selectsInPlaceLikeOneOneByOneDefinition() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_default,
                HORIZONTAL_VIEWPORT,
                new SetSelectionTo(ON_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalStartSnap_smoothScrollToPosition_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start,
                VERTICAL_VIEWPORT,
                new SmoothScrollTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_smoothScrollToPosition_landsWhereOneOneByOneDefinitionLands() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new SmoothScrollTo(OFF_SCREEN_POSITION));
    }

    @Test
    public void
            noDefinition_verticalStartSnap_fling_restsOnTheSnapPositionLikeOneOneByOneDefinition() {
        assertTheFallbackFlingsOntoTheSnapPositionLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_start, VERTICAL_VIEWPORT);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_fling_restsOnTheSnapPositionLikeOneOneByOneDefinition() {
        assertTheFallbackFlingsOntoTheSnapPositionLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_start, HORIZONTAL_VIEWPORT);
    }

    @Test
    public void
            noDefinition_verticalCircular_dragBackAcrossTheWrap_restsWhereOneOneByOneDefinitionRests() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_vertical_circular,
                VERTICAL_VIEWPORT,
                new DragBackAcrossTheWrap());
    }

    @Test
    public void
            noDefinition_horizontalCircular_dragBackAcrossTheWrap_restsWhereOneOneByOneDefinitionRests() {
        assertTheFallbackRunsLikeTheOracle(
                R.layout.instrumented_pattern_no_definition_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new DragBackAcrossTheWrap());
    }

    private void assertTheFallbackRunsLikeTheOracle(
            final int layoutResource, final Viewport viewport, final Scenario scenario) {
        final PatternState oracle =
                run(layoutResource, viewport, new AddOneOneByOneDefinition(), scenario);
        final PatternState fallback =
                run(layoutResource, viewport, new AddNoDefinition(), scenario);

        scenario.assertTheOracleShowsIt(oracle, viewport);
        assertSameState(fallback, oracle);
    }

    private void assertTheFallbackFlingsOntoTheSnapPositionLikeTheOracle(
            final int layoutResource, final Viewport viewport) {
        final Scenario fling = new FlingForward();
        final PatternState oracle =
                run(layoutResource, viewport, new AddOneOneByOneDefinition(), fling);
        final PatternState fallback = run(layoutResource, viewport, new AddNoDefinition(), fling);

        fling.assertTheOracleShowsIt(oracle, viewport);
        assertRestsForwardOnTheStartSnapPosition(fallback, viewport);
    }

    private void assertAClearedPatternRunsLikeTheOracle(
            final int layoutResource, final Viewport viewport, final Scenario scenario) {
        final PatternState oracle =
                run(layoutResource, viewport, new AddOneOneByOneDefinition(), scenario);
        final PatternState cleared =
                run(layoutResource, viewport, new AddADefinitionAndClearIt(), scenario);

        scenario.assertTheOracleShowsIt(oracle, viewport);
        assertSameState(cleared, oracle);
    }

    private void assertARestoredFallbackLandsLikeTheOracle(
            final int layoutResource, final Viewport viewport) {
        final PatternState oracle =
                restoreAfterSelectingOffScreen(
                        layoutResource, viewport, new AddOneOneByOneDefinition());
        final PatternState fallback =
                restoreAfterSelectingOffScreen(layoutResource, viewport, new AddNoDefinition());

        final int indexOfTheTarget = oracle.mChildren.indexOfAdapterPosition(OFF_SCREEN_POSITION);
        assertTrue(oracle.toString(), indexOfTheTarget != NOT_LAID_OUT);
        assertSameState(fallback, oracle);
    }

    private PatternState run(
            final int layoutResource,
            final Viewport viewport,
            final ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> definitions,
            final Scenario scenario) {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                attach(layoutResource, viewport);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        harness.apply(definitions);
        harness.apply(new ListenForSelections(selections));
        final ResizableAdapter adapter = resizableAdapter();
        harness.setAdapter(adapter);
        scenario.runOn(harness, adapter, viewport);
        harness.settle();
        return PatternState.of(harness, selections);
    }

    private PatternState restoreAfterSelectingOffScreen(
            final int layoutResource,
            final Viewport viewport,
            final ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> definitions) {
        final SparseArray<Parcelable> savedState = new SparseArray<Parcelable>();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> selected =
                attach(layoutResource, viewport);
        selected.apply(definitions);
        selected.setAdapter(resizableAdapter());
        selected.setSelection(OFF_SCREEN_POSITION);
        selected.settle();
        selected.apply(new SaveState(savedState));

        final ParchmentViewHarness<GridPatternView<BaseAdapter>> restored =
                attach(layoutResource, viewport);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        restored.apply(definitions);
        restored.apply(new RestoreState(savedState));
        restored.apply(new ListenForSelections(selections));
        restored.setAdapter(resizableAdapter());
        restored.settle();
        return PatternState.of(restored, selections);
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attach(
            final int layoutResource, final Viewport viewport) {
        return ParchmentViewHarness.attach(
                mActivityRule.getScenario(), layoutResource, viewport.mWidth, viewport.mHeight);
    }

    private static LaidOutChildren scrollToTheLatePosition(
            final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness) {
        harness.smoothScrollToPosition(LATE_POSITION);
        return harness.settle();
    }

    private static void shrinkAfterScrollingToTheLatePosition(
            final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
            final ResizableAdapter adapter,
            final int itemCount) {
        final LaidOutChildren scrolled = scrollToTheLatePosition(harness);
        final int lastPosition = itemCount - 1;
        final int indexOfTheNewLastPosition = scrolled.indexOfAdapterPosition(lastPosition);
        assertEquals(scrolled.toString(), NOT_LAID_OUT, indexOfTheNewLastPosition);
        harness.apply(new ChangeTheCount(adapter, itemCount));
    }

    private static ResizableAdapter resizableAdapter() {
        final Context context = ApplicationProvider.getApplicationContext();
        return new ResizableAdapter(context, ITEM_COUNT);
    }

    private static void assertRestsForwardOnTheStartSnapPosition(
            final PatternState state, final Viewport viewport) {
        final int firstVisiblePosition = state.mSurface.firstVisiblePosition();
        final int indexOfTheFirstVisible =
                state.mChildren.indexOfAdapterPosition(firstVisiblePosition);
        final int startOfTheFirstVisible =
                viewport.startOf(state.mChildren, indexOfTheFirstVisible);
        final boolean reportedTheFirstVisible = state.mSelections.contains(firstVisiblePosition);
        assertTrue(state.toString(), firstVisiblePosition > FIRST_POSITION);
        assertEquals(state.toString(), START_EDGE, startOfTheFirstVisible);
        assertEquals(state.toString(), firstVisiblePosition, state.mSelectedPosition);
        assertTrue(state.toString(), reportedTheFirstVisible);
    }

    private static void assertSameState(final PatternState actual, final PatternState oracle) {
        final String both = "\nwithout a definition: " + actual + "\nwith one 1x1: " + oracle;
        final boolean sameChildren = actual.mChildren.sameGeometryAs(oracle.mChildren);
        final VisibleSurface actualSurface = actual.mSurface;
        final VisibleSurface oracleSurface = oracle.mSurface;
        assertTrue(both, sameChildren);
        assertEquals(both, oracle.mSelectedPosition, actual.mSelectedPosition);
        assertEquals(
                both, oracleSurface.firstVisiblePosition(), actualSurface.firstVisiblePosition());
        assertEquals(
                both, oracleSurface.lastVisiblePosition(), actualSurface.lastVisiblePosition());
        assertEquals(both, oracle.mSelections, actual.mSelections);
    }

    private interface Scenario {
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport);

        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport);
    }

    private static final class NothingMore implements Scenario {
        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {}

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int firstVisiblePosition = oracle.mSurface.firstVisiblePosition();
            assertTrue(oracle.toString(), oracle.mChildren.count() > NO_ITEMS);
            assertEquals(oracle.toString(), FIRST_POSITION, firstVisiblePosition);
        }
    }

    private static final class SetSelectionTo implements Scenario {
        private final int mPosition;

        private SetSelectionTo(final int position) {
            mPosition = position;
        }

        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            harness.setSelection(mPosition);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int indexOfTheTarget = oracle.mChildren.indexOfAdapterPosition(mPosition);
            assertTrue(oracle.toString(), indexOfTheTarget != NOT_LAID_OUT);
            assertEquals(oracle.toString(), mPosition, oracle.mSelectedPosition);
        }
    }

    private static final class SelectOffScreenWithoutSnapping implements Scenario {
        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            harness.setSelection(OFF_SCREEN_POSITION);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int firstVisiblePosition = oracle.mSurface.firstVisiblePosition();
            assertEquals(oracle.toString(), NOTHING_SELECTED, oracle.mSelectedPosition);
            assertEquals(oracle.toString(), FIRST_POSITION, firstVisiblePosition);
        }
    }

    private static final class ShrinkAfterScrollingOn implements Scenario {
        private final int mItemCount;

        private ShrinkAfterScrollingOn(final int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            shrinkAfterScrollingToTheLatePosition(harness, adapter, mItemCount);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int lastPosition = mItemCount - 1;
            final int lastVisiblePosition = oracle.mSurface.lastVisiblePosition();
            assertEquals(oracle.toString(), lastPosition, lastVisiblePosition);
        }
    }

    private static final class ShrinkACircleAfterScrollingOn implements Scenario {
        private final int mItemCount;

        private ShrinkACircleAfterScrollingOn(final int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            shrinkAfterScrollingToTheLatePosition(harness, adapter, mItemCount);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int lastPosition = mItemCount - 1;
            final int indexOfTheLastPosition =
                    oracle.mChildren.indexOfAdapterPosition(lastPosition);
            assertTrue(oracle.toString(), indexOfTheLastPosition != NOT_LAID_OUT);
        }
    }

    private static final class GrowAfterScrollingOn implements Scenario {
        private final int mItemCount;

        private GrowAfterScrollingOn(final int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            scrollToTheLatePosition(harness);
            harness.apply(new ChangeTheCount(adapter, mItemCount));
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int indexOfTheLatePosition =
                    oracle.mChildren.indexOfAdapterPosition(LATE_POSITION);
            assertTrue(oracle.toString(), indexOfTheLatePosition != NOT_LAID_OUT);
        }
    }

    private static final class SmoothScrollTo implements Scenario {
        private final int mPosition;

        private SmoothScrollTo(final int position) {
            mPosition = position;
        }

        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            harness.smoothScrollToPosition(mPosition);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int indexOfTheTarget = oracle.mChildren.indexOfAdapterPosition(mPosition);
            assertTrue(oracle.toString(), indexOfTheTarget != NOT_LAID_OUT);
        }
    }

    private static final class FlingForward implements Scenario {
        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            harness.fling(
                    viewport.mForwardFromX,
                    viewport.mForwardFromY,
                    viewport.mForwardToX,
                    viewport.mForwardToY,
                    FLING_STEPS);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            assertRestsForwardOnTheStartSnapPosition(oracle, viewport);
        }
    }

    private static final class DragBackAcrossTheWrap implements Scenario {
        @Override
        public void runOn(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final Viewport viewport) {
            harness.dragAndRelease(
                    viewport.mForwardToX,
                    viewport.mForwardToY,
                    viewport.mForwardFromX,
                    viewport.mForwardFromY,
                    DRAG_STEPS);
        }

        @Override
        public void assertTheOracleShowsIt(final PatternState oracle, final Viewport viewport) {
            final int indexOfTheLast = oracle.mChildren.indexOfAdapterPosition(LAST_POSITION);
            assertTrue(oracle.toString(), indexOfTheLast != NOT_LAID_OUT);
        }
    }

    private static final class PatternState {
        private final LaidOutChildren mChildren;
        private final int mSelectedPosition;
        private final VisibleSurface mSurface;
        private final List<Integer> mSelections;

        private PatternState(
                final LaidOutChildren children,
                final int selectedPosition,
                final VisibleSurface surface,
                final List<Integer> selections) {
            mChildren = children;
            mSelectedPosition = selectedPosition;
            mSurface = surface;
            mSelections = selections;
        }

        private static PatternState of(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final RecordingItemSelectedListener selections) {
            final LaidOutChildren children = harness.children();
            final int selectedPosition = harness.selectedItemPosition();
            final VisibleSurface surface = harness.surface();
            final List<Integer> selectedPositions = selections.positions();
            return new PatternState(children, selectedPosition, surface, selectedPositions);
        }

        @Override
        public String toString() {
            return mChildren
                    + "\n  selected "
                    + mSelectedPosition
                    + ", "
                    + mSurface
                    + ", selections reported "
                    + mSelections;
        }
    }

    private static final class Viewport {
        private final boolean mIsVertical;
        private final int mWidth;
        private final int mHeight;
        private final int mForwardFromX;
        private final int mForwardFromY;
        private final int mForwardToX;
        private final int mForwardToY;

        private Viewport(
                final boolean isVertical,
                final int width,
                final int height,
                final int forwardFromX,
                final int forwardFromY,
                final int forwardToX,
                final int forwardToY) {
            mIsVertical = isVertical;
            mWidth = width;
            mHeight = height;
            mForwardFromX = forwardFromX;
            mForwardFromY = forwardFromY;
            mForwardToX = forwardToX;
            mForwardToY = forwardToY;
        }

        private int startOf(final LaidOutChildren children, final int index) {
            if (mIsVertical) return children.top(index);
            return children.left(index);
        }
    }

    private static final class AddNoDefinition
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {}
    }

    private static final class AddOneOneByOneDefinition
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final GridPatternItemDefinition oneByOne =
                    new GridPatternItemDefinition(
                            FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(oneByOne);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AddADefinitionAndClearIt
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final GridPatternItemDefinition first =
                    new GridPatternItemDefinition(
                            FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
            final GridPatternItemDefinition second =
                    new GridPatternItemDefinition(
                            FIRST_ROW, SECOND_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(first);
            items.add(second);
            view.addGridPatternGroupDefinition(items);
            view.clear();
        }
    }

    private static final class ListenForSelections
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        private final RecordingItemSelectedListener mSelections;

        private ListenForSelections(final RecordingItemSelectedListener selections) {
            mSelections = selections;
        }

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            view.setOnItemSelectedListener(mSelections);
        }
    }

    private static final class ChangeTheCount
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        private final ResizableAdapter mAdapter;
        private final int mItemCount;

        private ChangeTheCount(final ResizableAdapter adapter, final int itemCount) {
            mAdapter = adapter;
            mItemCount = itemCount;
        }

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            mAdapter.setCount(mItemCount);
        }
    }

    private static final class SaveState
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        private final SparseArray<Parcelable> mSavedState;

        private SaveState(final SparseArray<Parcelable> savedState) {
            mSavedState = savedState;
        }

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            view.saveHierarchyState(mSavedState);
        }
    }

    private static final class RestoreState
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        private final SparseArray<Parcelable> mSavedState;

        private RestoreState(final SparseArray<Parcelable> savedState) {
            mSavedState = savedState;
        }

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            view.restoreHierarchyState(mSavedState);
        }
    }

    private static final class RecordingItemSelectedListener
            implements AdapterView.OnItemSelectedListener {
        private final List<Integer> mPositions = new ArrayList<Integer>();

        @Override
        public synchronized void onItemSelected(
                final AdapterView<?> parent, final View view, final int position, final long id) {
            mPositions.add(position);
        }

        @Override
        public synchronized void onNothingSelected(final AdapterView<?> parent) {
            mPositions.add(NOTHING_SELECTED);
        }

        private synchronized List<Integer> positions() {
            return new ArrayList<Integer>(mPositions);
        }
    }

    private static final class ResizableAdapter extends BaseAdapter {
        private final FixedSizeAdapter mItems;
        private int mCount;

        private ResizableAdapter(final Context context, final int count) {
            mItems = new FixedSizeAdapter(context, GROWN_ITEM_COUNT, MATCH_PARENT, MATCH_PARENT);
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
