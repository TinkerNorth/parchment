// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.harness.PatternState.assertSameState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import mobi.parchment.harness.ChangeTheCount;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ListenForSelections;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.harness.PatternState;
import mobi.parchment.harness.RecordingItemSelectedListener;
import mobi.parchment.harness.ResizableAdapter;
import mobi.parchment.harness.Viewport;
import mobi.parchment.harness.VisibleSurface;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class GridPatternEmptyGroupInstrumentedTest {

    private static final int ITEM_COUNT = 30;
    private static final int GROWN_ITEM_COUNT = 40;
    private static final int SHRUNK_ITEM_COUNT = 3;
    private static final int LAST_SHRUNK_POSITION = SHRUNK_ITEM_COUNT - 1;
    private static final int NO_ITEMS = 0;
    private static final int OFF_SCREEN_SELECTION = 27;
    private static final int SCROLL_BACK_DISTANCE = 4;
    private static final int NOT_LAID_OUT = -1;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final int DRAG_STEPS = 8;
    private static final int REST_ATTEMPTS = 400;
    private static final long ONE_FRAME_MILLISECONDS = 16;
    private static final long START_TIME_LIMIT_MILLISECONDS = 60000;
    private static final List<GridPatternGroupDefinition> NULL_LIST = null;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final int VIEWPORT_BREADTH = 600;
    private static final int VIEWPORT_SIZE = 900;
    private static final int GESTURE_ACROSS = VIEWPORT_BREADTH / 2;
    private static final int GESTURE_NEAR_THE_START = 200;
    private static final int GESTURE_NEAR_THE_END = 700;
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
    public void anEmptyGroupAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheView(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheView(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewAfterAClear_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewAfterAClear(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewAfterAClear_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewAfterAClear(),
                new NoGroup());
    }

    @Test
    public void
            anEmptyGroupAddedToTheViewBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewBetweenTwoGroups(),
                new AddTwoGroupsToTheView());
    }

    @Test
    public void
            anEmptyGroupAddedToTheViewBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewBetweenTwoGroups(),
                new AddTwoGroupsToTheView());
    }

    @Test
    public void anEmptyGroupAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_verticalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_verticalStartSnapFromAnEmptyAdapter_runsLikeNoDefinition() {
        assertRunsLikeTheOracleFromAnEmptyAdapter(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalStartSnapFromAnEmptyAdapter_runsLikeNoDefinition() {
        assertRunsLikeTheOracleFromAnEmptyAdapter(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedAfterAClear_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAClear(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedAfterAClear_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAClear(),
                new NoGroup());
    }

    @Test
    public void aListOfAnEmptyGroupSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfAnEmptyGroupSet(),
                new NoGroup());
    }

    @Test
    public void aListOfAnEmptyGroupSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfAnEmptyGroupSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyListSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyListSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSetAfterAGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSetAfterAGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void aNullListSetAfterAGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void aNullListSetAfterAGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void aNullListSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullListSet(),
                new NoGroup());
    }

    @Test
    public void aNullListSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullListSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_verticalCircular_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_horizontalCircular_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_verticalCircular_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_horizontalCircular_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_verticalCircularWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_horizontalCircularWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_verticalCenterSnapWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_vertical_center_no_spacing,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_horizontalCenterSnapWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_horizontal_center_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupAfterAGroup_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupAfterAGroup_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void aGroupAddedAfterAClear_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new OneGroupAddedAfterAClear(),
                new OneGroupAdded());
    }

    @Test
    public void aGroupAddedAfterAClear_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new OneGroupAddedAfterAClear(),
                new OneGroupAdded());
    }

    @Test
    public void aListOfOneGroupSetAfterAnotherGroup_verticalStartSnap_runsLikeTheListAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfOneGroupSetAfterAnotherGroup(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfOneGroupSetAfterAnotherGroup_horizontalStartSnap_runsLikeTheListAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfOneGroupSetAfterAnotherGroup(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfAnEmptyGroupAndAGroupSet_verticalStartSnap_runsLikeAListOfTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfAnEmptyGroupAndAGroupSet(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfAnEmptyGroupAndAGroupSet_horizontalStartSnap_runsLikeAListOfTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfAnEmptyGroupAndAGroupSet(),
                new AListOfOneGroupSet());
    }

    private void assertRunsLikeTheOracle(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternRun oracleRun = start(layoutResource, viewport, oracleDefinitions, ITEM_COUNT);
        final PatternState oracleLayout = oracleRun.state();
        final Journey oracle = oracleRun.travelOn(oracleLayout);
        final PatternRun patternRun = start(layoutResource, viewport, definitions, ITEM_COUNT);
        final PatternState patternLayout = patternRun.state();
        final Journey pattern = patternRun.travelOn(patternLayout);

        assertTravelsLikeTheOracle(pattern, oracle);
    }

    private void assertRunsLikeTheOracleOnceLaidOutInTime(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternRun oracleRun = start(layoutResource, viewport, oracleDefinitions, ITEM_COUNT);
        final PatternState oracleLayout = oracleRun.state();
        final Journey oracle = oracleRun.travelOn(oracleLayout);
        final PatternRun patternRun = startInTime(layoutResource, viewport, definitions);
        final PatternState patternLayout = patternRun.state();
        final Journey pattern = patternRun.travelOn(patternLayout);

        assertTravelsLikeTheOracle(pattern, oracle);
    }

    private void assertRunsLikeTheOracleFromAnEmptyAdapter(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternRun oracleRun = start(layoutResource, viewport, oracleDefinitions, NO_ITEMS);
        final PatternState emptyOracleLayout = oracleRun.state();
        final PatternState filledOracleLayout = oracleRun.changeTheCount(ITEM_COUNT);
        final Journey oracle = oracleRun.travelOn(filledOracleLayout);
        final PatternRun patternRun = start(layoutResource, viewport, definitions, NO_ITEMS);
        final PatternState emptyPatternLayout = patternRun.state();
        final PatternState filledPatternLayout = patternRun.changeTheCount(ITEM_COUNT);
        final Journey pattern = patternRun.travelOn(filledPatternLayout);

        final LaidOutChildren emptyOracleChildren = emptyOracleLayout.children();
        final int emptyChildCount = emptyOracleChildren.count();
        assertEquals(emptyOracleLayout.toString(), NO_ITEMS, emptyChildCount);
        assertSameState(emptyPatternLayout, emptyOracleLayout);
        assertTravelsLikeTheOracle(pattern, oracle);
    }

    private static void assertTravelsLikeTheOracle(final Journey pattern, final Journey oracle) {
        assertLaysOutLikeTheOracle(pattern, oracle);
        assertSelectsOffScreenLikeTheOracle(pattern, oracle);
        assertScrollsLikeTheOracle(pattern, oracle);
        assertDragsLikeTheOracle(pattern, oracle);
        assertShrinksLikeTheOracle(pattern, oracle);
        assertGrowsLikeTheOracle(pattern, oracle);
    }

    private static void assertLaysOutLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleLayout = oracle.mLaidOut;
        final LaidOutChildren oracleChildren = oracleLayout.children();
        assertTrue(oracleLayout.toString(), oracleChildren.count() > NO_ITEMS);
        assertSameState(pattern.mLaidOut, oracleLayout);
    }

    private static void assertSelectsOffScreenLikeTheOracle(
            final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.mLaidOut;
        final PatternState oracleLayout = oracle.mSelected;
        assertFalse(oracleBefore.toString(), isLaidOut(oracleBefore, OFF_SCREEN_SELECTION));
        assertTrue(oracleLayout.toString(), isLaidOut(oracleLayout, OFF_SCREEN_SELECTION));
        assertSameState(pattern.mSelected, oracleLayout);
    }

    private static void assertScrollsLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.mSelected;
        final PatternState oracleLayout = oracle.mScrolled;
        final int scrollBackTarget = oracle.mScrollBackTarget;
        assertFalse(oracleBefore.toString(), isLaidOut(oracleBefore, scrollBackTarget));
        assertTrue(oracleLayout.toString(), isLaidOut(oracleLayout, scrollBackTarget));
        assertSameState(pattern.mScrolled, oracleLayout);
    }

    private static void assertDragsLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.mScrolled;
        final PatternState oracleLayout = oracle.mDragged;
        final LaidOutChildren oracleChildrenBefore = oracleBefore.children();
        final LaidOutChildren oracleChildren = oracleLayout.children();
        final boolean theDragMovedTheContent = !oracleChildren.sameGeometryAs(oracleChildrenBefore);
        assertTrue(oracleLayout.toString(), theDragMovedTheContent);
        assertSameState(pattern.mDragged, oracleLayout);
    }

    private static void assertShrinksLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleLayout = oracle.mShrunk;
        assertTrue(oracleLayout.toString(), isLaidOut(oracleLayout, LAST_SHRUNK_POSITION));
        assertSameState(pattern.mShrunk, oracleLayout);
    }

    private static void assertGrowsLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.mShrunk;
        final PatternState oracleLayout = oracle.mGrown;
        final LaidOutChildren oracleChildrenBefore = oracleBefore.children();
        final LaidOutChildren oracleChildren = oracleLayout.children();
        final boolean growingChangedTheLayout =
                !oracleChildren.sameGeometryAs(oracleChildrenBefore);
        assertTrue(oracleLayout.toString(), growingChangedTheLayout);
        assertSameState(pattern.mGrown, oracleLayout);
    }

    private static boolean isLaidOut(final PatternState state, final int position) {
        final LaidOutChildren children = state.children();
        final int index = children.indexOfAdapterPosition(position);
        return index != NOT_LAID_OUT;
    }

    private static int scrollBackTargetFrom(final PatternState selected) {
        final VisibleSurface surface = selected.surface();
        final int firstVisiblePosition = surface.firstVisiblePosition();
        return firstVisiblePosition - SCROLL_BACK_DISTANCE;
    }

    private PatternRun startInTime(
            final int layoutResource, final Viewport viewport, final Definitions definitions) {
        final StartWatchdog watchdog = StartWatchdog.watchFor(START_TIME_LIMIT_MILLISECONDS);
        try {
            return start(layoutResource, viewport, definitions, ITEM_COUNT);
        } finally {
            watchdog.standDown();
        }
    }

    private PatternRun start(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final int itemCount) {
        final int width = viewport.width();
        final int height = viewport.height();
        final boolean isVertical = viewport.isVertical();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(), layoutResource, width, height);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        final Context context = ApplicationProvider.getApplicationContext();
        final ResizableAdapter adapter = new ResizableAdapter(context, itemCount);
        harness.apply(new Define(definitions, isVertical));
        harness.apply(new ListenForSelections<>(selections));
        harness.setAdapter(adapter);
        waitForRest(harness);
        return new PatternRun(harness, adapter, selections, viewport);
    }

    private static void waitForRest(
            final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness) {
        final GridPatternView<BaseAdapter> view = harness.view();
        final ReachableGridPatternView reachableView = (ReachableGridPatternView) view;
        final ReadRest readRest = new ReadRest(reachableView);
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        for (int attempt = 0; attempt < REST_ATTEMPTS; attempt++) {
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(readRest);
            if (readRest.isAtRest()) return;
            SystemClock.sleep(ONE_FRAME_MILLISECONDS);
        }
        throw new AssertionError("the pattern never came to rest: " + harness.children());
    }

    private static List<GridPatternItemDefinition> noItems() {
        return new ArrayList<GridPatternItemDefinition>();
    }

    private static List<GridPatternItemDefinition> twoOneByOnes(final boolean isVertical) {
        final GridPatternItemDefinition first =
                new GridPatternItemDefinition(
                        FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        final GridPatternItemDefinition second = secondOneByOneAcrossTheBreadth(isVertical);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(first);
        items.add(second);
        return items;
    }

    private static GridPatternItemDefinition secondOneByOneAcrossTheBreadth(
            final boolean isVertical) {
        if (isVertical) {
            return new GridPatternItemDefinition(
                    FIRST_ROW, SECOND_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        }
        return new GridPatternItemDefinition(
                SECOND_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    }

    private static List<GridPatternItemDefinition> oneWideItem(final boolean isVertical) {
        final GridPatternItemDefinition wide = oneItemAcrossTheBreadth(isVertical);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(wide);
        return items;
    }

    private static GridPatternItemDefinition oneItemAcrossTheBreadth(final boolean isVertical) {
        if (isVertical) {
            return new GridPatternItemDefinition(
                    FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, TWO_GRID_UNITS);
        }
        return new GridPatternItemDefinition(
                FIRST_ROW, FIRST_COLUMN, TWO_GRID_UNITS, ONE_GRID_UNIT);
    }

    private static GridPatternGroupDefinition group(
            final boolean isVertical, final List<GridPatternItemDefinition> items) {
        return new GridPatternGroupDefinition(isVertical, items);
    }

    private static GridPatternLayoutManager layoutManagerOf(
            final GridPatternView<BaseAdapter> view) {
        final ReachableGridPatternView reachableView = (ReachableGridPatternView) view;
        return reachableView.gridPatternLayoutManager();
    }

    private static final class PatternRun {
        private final ParchmentViewHarness<GridPatternView<BaseAdapter>> mHarness;
        private final ResizableAdapter mAdapter;
        private final RecordingItemSelectedListener mSelections;
        private final Viewport mViewport;

        private PatternRun(
                final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness,
                final ResizableAdapter adapter,
                final RecordingItemSelectedListener selections,
                final Viewport viewport) {
            mHarness = harness;
            mAdapter = adapter;
            mSelections = selections;
            mViewport = viewport;
        }

        private Journey travelOn(final PatternState laidOut) {
            final PatternState selected = select(OFF_SCREEN_SELECTION);
            final int scrollBackTarget = scrollBackTargetFrom(selected);
            final PatternState scrolled = scrollTo(scrollBackTarget);
            final PatternState dragged = dragBack();
            final PatternState shrunk = changeTheCount(SHRUNK_ITEM_COUNT);
            final PatternState grown = changeTheCount(GROWN_ITEM_COUNT);
            return new Journey(
                    laidOut, selected, scrollBackTarget, scrolled, dragged, shrunk, grown);
        }

        private PatternState select(final int position) {
            mHarness.setSelection(position);
            return stateAtRest();
        }

        private PatternState scrollTo(final int position) {
            mHarness.smoothScrollToPosition(position);
            return stateAtRest();
        }

        private PatternState dragBack() {
            mViewport.dragBack(mHarness, DRAG_STEPS);
            return stateAtRest();
        }

        private PatternState changeTheCount(final int itemCount) {
            mHarness.apply(new ChangeTheCount<>(mAdapter, itemCount));
            return stateAtRest();
        }

        private PatternState stateAtRest() {
            waitForRest(mHarness);
            return state();
        }

        private PatternState state() {
            return PatternState.of(mHarness, mSelections);
        }
    }

    private static final class StartWatchdog implements Runnable {
        private final long mTimeLimitMilliseconds;
        private final AtomicBoolean mIsStoodDown = new AtomicBoolean();

        private StartWatchdog(final long timeLimitMilliseconds) {
            mTimeLimitMilliseconds = timeLimitMilliseconds;
        }

        private static StartWatchdog watchFor(final long timeLimitMilliseconds) {
            final StartWatchdog watchdog = new StartWatchdog(timeLimitMilliseconds);
            final Thread watching = new Thread(watchdog);
            watching.setDaemon(true);
            watching.start();
            return watchdog;
        }

        private void standDown() {
            mIsStoodDown.set(true);
        }

        @Override
        public void run() {
            SystemClock.sleep(mTimeLimitMilliseconds);
            final boolean isStoodDown = mIsStoodDown.get();
            if (isStoodDown) return;
            final Looper mainLooper = Looper.getMainLooper();
            final Thread mainThread = mainLooper.getThread();
            final AssertionError stillStarting =
                    new AssertionError(
                            "starting the pattern, from attaching it to its first layout coming to"
                                    + " rest, was still running after "
                                    + mTimeLimitMilliseconds
                                    + " ms");
            stillStarting.setStackTrace(mainThread.getStackTrace());
            // A hung main thread blocks the scenario's teardown; only an uncaught throw reports.
            throw stillStarting;
        }
    }

    private static final class Journey {
        private final PatternState mLaidOut;
        private final PatternState mSelected;
        private final int mScrollBackTarget;
        private final PatternState mScrolled;
        private final PatternState mDragged;
        private final PatternState mShrunk;
        private final PatternState mGrown;

        private Journey(
                final PatternState laidOut,
                final PatternState selected,
                final int scrollBackTarget,
                final PatternState scrolled,
                final PatternState dragged,
                final PatternState shrunk,
                final PatternState grown) {
            mLaidOut = laidOut;
            mSelected = selected;
            mScrollBackTarget = scrollBackTarget;
            mScrolled = scrolled;
            mDragged = dragged;
            mShrunk = shrunk;
            mGrown = grown;
        }
    }

    private interface Definitions {
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical);
    }

    private static final class Define
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {
        private final Definitions mDefinitions;
        private final boolean mIsVertical;

        private Define(final Definitions definitions, final boolean isVertical) {
            mDefinitions = definitions;
            mIsVertical = isVertical;
        }

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            mDefinitions.defineOn(view, mIsVertical);
        }
    }

    private static final class NoGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {}
    }

    private static final class AddTwoGroupsToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddAnEmptyGroupToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> noItems = noItems();
            view.addGridPatternGroupDefinition(noItems);
        }
    }

    private static final class AddAnEmptyGroupToTheViewWithAdd implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> noItems = noItems();
            view.add(noItems);
        }
    }

    private static final class AddAnEmptyGroupToTheViewAfterAClear implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.clear();
            view.addGridPatternGroupDefinition(noItems);
        }
    }

    private static final class AddAnEmptyGroupToTheViewBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(noItems);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class OneGroupAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
        }
    }

    private static final class TwoGroupsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
        }
    }

    private static final class AListOfOneGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class OneGroupAddedAfterAClear implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
            layoutManager.clearGridPatternGroupDefinition();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
        }
    }

    private static final class AListOfOneGroupSetAfterAnotherGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AnEmptyGroupAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
        }
    }

    private static final class AnEmptyGroupAddedAfterAClear implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.clearGridPatternGroupDefinition();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
        }
    }

    private static final class AListOfAnEmptyGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, noItems));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AnEmptyListSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            layoutManager.setGridPatternGroupDefinitions(noGroups);
        }
    }

    private static final class AnEmptyListSetAfterAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(noGroups);
        }
    }

    private static final class ANullListSetAfterAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(NULL_LIST);
        }
    }

    private static final class ANullListSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            layoutManager.setGridPatternGroupDefinitions(NULL_LIST);
        }
    }

    private static final class AnEmptyGroupAddedBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
        }
    }

    private static final class AnEmptyGroupAddedBeforeAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
        }
    }

    private static final class AnEmptyGroupAddedAfterAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
        }
    }

    private static final class AListOfAnEmptyGroupAndAGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, noItems));
            groups.add(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class ReadRest implements Runnable {
        private final ReachableGridPatternView mView;
        private boolean mIsAtRest;

        private ReadRest(final ReachableGridPatternView view) {
            mView = view;
        }

        @Override
        public void run() {
            mIsAtRest = mView.isAtRest();
        }

        private boolean isAtRest() {
            return mIsAtRest;
        }
    }

    public static final class ReachableGridPatternView extends GridPatternView<BaseAdapter> {
        private GridPatternLayoutManager mGridPatternLayoutManager;
        private boolean mIsWaitingForAFrame;

        public ReachableGridPatternView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        protected AdapterViewInitializer<GridPatternGroup> createAdapterViewInitializer(
                final Context context,
                final boolean isViewPager,
                final AdapterViewManager adapterViewManager,
                final LayoutManager<GridPatternGroup> layoutManager,
                final boolean isVerticalScroll,
                final Drawable divider,
                final int dividerSize) {
            mGridPatternLayoutManager = (GridPatternLayoutManager) layoutManager;
            return super.createAdapterViewInitializer(
                    context,
                    isViewPager,
                    adapterViewManager,
                    layoutManager,
                    isVerticalScroll,
                    divider,
                    dividerSize);
        }

        @Override
        public void requestAnimationFrame() {
            mIsWaitingForAFrame = true;
            super.requestAnimationFrame();
        }

        @Override
        protected void onAnimationFrame() {
            mIsWaitingForAFrame = false;
            super.onAnimationFrame();
        }

        private GridPatternLayoutManager gridPatternLayoutManager() {
            return mGridPatternLayoutManager;
        }

        private boolean isAtRest() {
            final boolean isLayoutRequested = isLayoutRequested();
            return !mIsWaitingForAFrame && !isLayoutRequested;
        }
    }
}
