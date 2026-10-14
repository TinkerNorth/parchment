// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import static mobi.parchment.harness.PatternState.assertSameState;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.widget.BaseAdapter;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;

public final class PatternRun {

    public static final int ITEM_COUNT = 30;
    public static final int NO_ITEMS = 0;
    private static final int GROWN_ITEM_COUNT = 40;
    private static final int SHRUNK_ITEM_COUNT = 3;
    private static final int LAST_SHRUNK_POSITION = SHRUNK_ITEM_COUNT - 1;
    private static final int OFF_SCREEN_SELECTION = 27;
    private static final int SCROLL_BACK_DISTANCE = 4;
    private static final int NOT_LAID_OUT = -1;
    private static final int DRAG_STEPS = 8;
    private static final int REST_ATTEMPTS = 400;
    private static final long ONE_FRAME_MILLISECONDS = 16;
    private static final long START_TIME_LIMIT_MILLISECONDS = 60000;
    private static final String STARTING =
            "starting the pattern, from attaching it to its first layout coming to rest";
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final int VIEWPORT_BREADTH = 600;
    private static final int VIEWPORT_SIZE = 900;
    private static final int GESTURE_ACROSS = VIEWPORT_BREADTH / 2;
    private static final int GESTURE_NEAR_THE_START = 200;
    private static final int GESTURE_NEAR_THE_END = 700;
    public static final Viewport VERTICAL_VIEWPORT =
            new Viewport(
                    VERTICAL,
                    VIEWPORT_BREADTH,
                    VIEWPORT_SIZE,
                    GESTURE_ACROSS,
                    GESTURE_NEAR_THE_END,
                    GESTURE_ACROSS,
                    GESTURE_NEAR_THE_START);
    public static final Viewport HORIZONTAL_VIEWPORT =
            new Viewport(
                    HORIZONTAL,
                    VIEWPORT_SIZE,
                    VIEWPORT_BREADTH,
                    GESTURE_NEAR_THE_END,
                    GESTURE_ACROSS,
                    GESTURE_NEAR_THE_START,
                    GESTURE_ACROSS);

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

    public static PatternRun start(
            final ActivityScenario<HarnessActivity> scenario,
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final int itemCount) {
        final int width = viewport.width();
        final int height = viewport.height();
        final boolean isVertical = viewport.isVertical();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(scenario, layoutResource, width, height);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        final Context context = ApplicationProvider.getApplicationContext();
        final ResizableAdapter adapter = new ResizableAdapter(context, itemCount);
        harness.apply(new Define(definitions, isVertical));
        harness.apply(new ListenForSelections<>(selections));
        harness.setAdapter(adapter);
        waitForRest(harness);
        return new PatternRun(harness, adapter, selections, viewport);
    }

    public static PatternRun startInTime(
            final ActivityScenario<HarnessActivity> scenario,
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions) {
        final Watchdog watchdog = Watchdog.watchFor(STARTING, START_TIME_LIMIT_MILLISECONDS);
        try {
            return start(scenario, layoutResource, viewport, definitions, ITEM_COUNT);
        } finally {
            watchdog.standDown();
        }
    }

    public static void assertRunsLikeTheOracle(
            final ActivityScenario<HarnessActivity> scenario,
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternRun oracleRun =
                start(scenario, layoutResource, viewport, oracleDefinitions, ITEM_COUNT);
        final PatternState oracleLayout = oracleRun.state();
        final Journey oracle = oracleRun.travelOn(oracleLayout);
        final PatternRun patternRun =
                start(scenario, layoutResource, viewport, definitions, ITEM_COUNT);
        final PatternState patternLayout = patternRun.state();
        final Journey pattern = patternRun.travelOn(patternLayout);

        assertTravelsLikeTheOracle(pattern, oracle);
    }

    public static void assertTravelsLikeTheOracle(final Journey pattern, final Journey oracle) {
        assertLaysOutLikeTheOracle(pattern, oracle);
        assertSelectsOffScreenLikeTheOracle(pattern, oracle);
        assertScrollsLikeTheOracle(pattern, oracle);
        assertDragsLikeTheOracle(pattern, oracle);
        assertShrinksLikeTheOracle(pattern, oracle);
        assertGrowsLikeTheOracle(pattern, oracle);
    }

    private static void assertLaysOutLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleLayout = oracle.laidOut();
        final LaidOutChildren oracleChildren = oracleLayout.children();
        assertTrue(oracleLayout.toString(), oracleChildren.count() > NO_ITEMS);
        assertSameState(pattern.laidOut(), oracleLayout);
    }

    private static void assertSelectsOffScreenLikeTheOracle(
            final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.laidOut();
        final PatternState oracleLayout = oracle.selected();
        assertFalse(oracleBefore.toString(), isLaidOut(oracleBefore, OFF_SCREEN_SELECTION));
        assertTrue(oracleLayout.toString(), isLaidOut(oracleLayout, OFF_SCREEN_SELECTION));
        assertSameState(pattern.selected(), oracleLayout);
    }

    private static void assertScrollsLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.selected();
        final PatternState oracleLayout = oracle.scrolled();
        final int scrollBackTarget = oracle.scrollBackTarget();
        assertFalse(oracleBefore.toString(), isLaidOut(oracleBefore, scrollBackTarget));
        assertTrue(oracleLayout.toString(), isLaidOut(oracleLayout, scrollBackTarget));
        assertSameState(pattern.scrolled(), oracleLayout);
    }

    private static void assertDragsLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.scrolled();
        final PatternState oracleLayout = oracle.dragged();
        final LaidOutChildren oracleChildrenBefore = oracleBefore.children();
        final LaidOutChildren oracleChildren = oracleLayout.children();
        final boolean theDragMovedTheContent = !oracleChildren.sameGeometryAs(oracleChildrenBefore);
        assertTrue(oracleLayout.toString(), theDragMovedTheContent);
        assertSameState(pattern.dragged(), oracleLayout);
    }

    private static void assertShrinksLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleLayout = oracle.shrunk();
        assertTrue(oracleLayout.toString(), isLaidOut(oracleLayout, LAST_SHRUNK_POSITION));
        assertSameState(pattern.shrunk(), oracleLayout);
    }

    private static void assertGrowsLikeTheOracle(final Journey pattern, final Journey oracle) {
        final PatternState oracleBefore = oracle.shrunk();
        final PatternState oracleLayout = oracle.grown();
        final LaidOutChildren oracleChildrenBefore = oracleBefore.children();
        final LaidOutChildren oracleChildren = oracleLayout.children();
        final boolean growingChangedTheLayout =
                !oracleChildren.sameGeometryAs(oracleChildrenBefore);
        assertTrue(oracleLayout.toString(), growingChangedTheLayout);
        assertSameState(pattern.grown(), oracleLayout);
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

    public Journey travelOn(final PatternState laidOut) {
        final PatternState selected = select(OFF_SCREEN_SELECTION);
        final int scrollBackTarget = scrollBackTargetFrom(selected);
        final PatternState scrolled = scrollTo(scrollBackTarget);
        final PatternState dragged = dragBack();
        final PatternState shrunk = changeTheCount(SHRUNK_ITEM_COUNT);
        final PatternState grown = changeTheCount(GROWN_ITEM_COUNT);
        return new Journey(laidOut, selected, scrollBackTarget, scrolled, dragged, shrunk, grown);
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

    public PatternState changeTheCount(final int itemCount) {
        mHarness.apply(new ChangeTheCount<>(mAdapter, itemCount));
        return stateAtRest();
    }

    private PatternState stateAtRest() {
        waitForRest(mHarness);
        return state();
    }

    public PatternState state() {
        return PatternState.of(mHarness, mSelections);
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
}
