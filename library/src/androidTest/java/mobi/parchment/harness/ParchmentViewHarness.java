// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.AbstractAdapterView;

/**
 * Inflates a Parchment view from a layout with the real {@link LayoutInflater}, attaches it to a
 * real Activity at an exact pixel size, drives real measure and layout passes, and hands a test an
 * immutable snapshot of where the children landed.
 */
public final class ParchmentViewHarness<VIEW extends AbstractAdapterView<BaseAdapter, ?>> {

    private static final int LAYOUT_ATTEMPTS = 100;
    private static final int SETTLE_ATTEMPTS = 400;
    private static final int STABLE_READS_REQUIRED = 20;
    private static final int MINIMUM_SETTLE_MILLISECONDS = 250;
    private static final int SLOP_CROSSINGS_IN_THE_FIRST_MOVE = 2;
    private static final int FIRST_STEP = 1;
    private static final int ONE_STEP = 1;
    private static final float WHOLE_GESTURE = 1f;
    private static final int FRAME_MILLISECONDS = 16;
    private static final int NO_META_STATE = 0;
    private static final int NO_HOLD = 0;
    private static final int RELEASE_HOLD_STEPS = 10;

    private final VIEW mView;

    private ParchmentViewHarness(final VIEW view) {
        mView = view;
    }

    /**
     * Inflates the layout, attaches it to the harness Activity at exactly the requested pixel size,
     * and returns once the framework has laid it out at that size.
     */
    public static <VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            ParchmentViewHarness<VIEW> attach(
                    final ActivityScenario<HarnessActivity> scenario,
                    final int layoutResource,
                    final int width,
                    final int height) {
        final InflateAndAttach<VIEW> inflateAndAttach =
                new InflateAndAttach<>(layoutResource, width, height);
        scenario.onActivity(inflateAndAttach);
        final VIEW view = inflateAndAttach.view();
        final ParchmentViewHarness<VIEW> harness = new ParchmentViewHarness<>(view);
        harness.waitForLayout();
        harness.requireViewport(width, height);
        return harness;
    }

    public VIEW view() {
        return mView;
    }

    /** Runs the setup against the view on the main thread and waits for the layout it asks for. */
    public void apply(final ViewSetup<VIEW> setup) {
        final PerformSetup<VIEW> performSetup = new PerformSetup<>(mView, setup);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(performSetup);
        waitForLayout();
    }

    /** Sets the adapter on the main thread and waits for the layout pass it triggers. */
    public void setAdapter(final BaseAdapter adapter) {
        final SetAdapter<VIEW> setAdapter = new SetAdapter<>(mView, adapter);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(setAdapter);
        waitForLayout();
    }

    /** Calls setSelection on the main thread and waits for the layout it asks for. */
    public void setSelection(final int position) {
        final SetSelection<VIEW> setSelection = new SetSelection<>(mView, position);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(setSelection);
        waitForLayout();
    }

    /** Reads getSelectedItemPosition on the main thread. */
    public int selectedItemPosition() {
        final ReadSelectedPosition<VIEW> readSelectedPosition = new ReadSelectedPosition<>(mView);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(readSelectedPosition);
        return readSelectedPosition.selectedPosition();
    }

    /** Reads every child's bounds and adapter position on the main thread. */
    public LaidOutChildren children() {
        final ReadChildren<VIEW> readChildren = new ReadChildren<>(mView);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(readChildren);
        return readChildren.children();
    }

    /**
     * Dispatches a real down-move-up gesture to the view, fast enough for the platform {@link
     * android.view.GestureDetector} to call it a fling.
     */
    public void fling(
            final int fromX, final int fromY, final int toX, final int toY, final int steps) {
        performGesture(fromX, fromY, toX, toY, steps, NO_HOLD);
    }

    /**
     * Dispatches a real down-move-up gesture that ends held still, so the platform {@link
     * android.view.GestureDetector} reports a scroll and no fling.
     */
    public void dragAndRelease(
            final int fromX, final int fromY, final int toX, final int toY, final int steps) {
        performGesture(fromX, fromY, toX, toY, steps, RELEASE_HOLD_STEPS);
    }

    /**
     * Dispatches one touch event per main-thread pass, waiting a frame between them. The waiting is
     * the point: Parchment applies a scroll on the animation frame that follows it, so a gesture
     * delivered without frames in between never reaches a layout pass.
     *
     * <p>The moves stop one step short of the target and the event that ends the gesture carries
     * the last of the travel, so the final stretch of the gesture is still moving when the finger
     * leaves. A move that already sat on the target followed by a lift at the same place gives the
     * platform detector a stationary frame to end on, and the velocity it derives from that is too
     * near zero to have a reliable sign: the same upward gesture was read as a fling one way from a
     * cell boundary and the other way from a resting position part-way through a cell.
     */
    private void performGesture(
            final int fromX,
            final int fromY,
            final int toX,
            final int toY,
            final int steps,
            final int holdSteps) {
        final float firstProgress = firstMoveProgress(fromX, fromY, toX, toY, steps);
        final int movesBeforeTheLastEvent = Math.max(FIRST_STEP, steps - ONE_STEP);
        final long downTime = SystemClock.uptimeMillis();
        long eventTime = downTime;
        dispatch(downTime, eventTime, MotionEvent.ACTION_DOWN, fromX, fromY);
        for (int step = FIRST_STEP; step <= movesBeforeTheLastEvent; step++) {
            sleepOneFrame();
            eventTime = eventTime + FRAME_MILLISECONDS;
            final float progress = progressAt(step, steps, firstProgress);
            final float x = fromX + (toX - fromX) * progress;
            final float y = fromY + (toY - fromY) * progress;
            dispatch(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y);
        }
        for (int hold = 0; hold < holdSteps; hold++) {
            sleepOneFrame();
            eventTime = eventTime + FRAME_MILLISECONDS;
            dispatch(downTime, eventTime, MotionEvent.ACTION_MOVE, toX, toY);
        }
        sleepOneFrame();
        eventTime = eventTime + FRAME_MILLISECONDS;
        dispatch(downTime, eventTime, MotionEvent.ACTION_UP, toX, toY);
    }

    /**
     * How far along the gesture the first move lands. It clears the touch slop in one event so that
     * the number of events needed to start scrolling never depends on how quickly the machine can
     * dispatch them, and so that the platform long-press timeout — which runs on the wall clock —
     * is cancelled before it can fire and swallow the rest of the gesture.
     */
    private float firstMoveProgress(
            final int fromX, final int fromY, final int toX, final int toY, final int steps) {
        final float travelX = toX - fromX;
        final float travelY = toY - fromY;
        final float travel = (float) Math.hypot(travelX, travelY);
        if (travel <= 0f) {
            return WHOLE_GESTURE;
        }
        final int slopToClear = SLOP_CROSSINGS_IN_THE_FIRST_MOVE * touchSlop() + 1;
        final float slopProgress = slopToClear / travel;
        final float evenProgress = WHOLE_GESTURE / steps;
        return Math.min(WHOLE_GESTURE, Math.max(evenProgress, slopProgress));
    }

    private static float progressAt(final int step, final int steps, final float firstProgress) {
        if (step >= steps) {
            return WHOLE_GESTURE;
        }
        if (step == FIRST_STEP) {
            return firstProgress;
        }
        final float stepsAfterTheFirst = steps - FIRST_STEP;
        final float stepsTaken = step - FIRST_STEP;
        final float remaining = WHOLE_GESTURE - firstProgress;
        return firstProgress + remaining * (stepsTaken / stepsAfterTheFirst);
    }

    /**
     * The platform touch slop for this view, so a test that depends on a gesture shorter than one
     * cell can state the premise its numbers rest on rather than failing obscurely on a device with
     * a different density.
     */
    public int touchSlop() {
        final ReadTouchSlop<VIEW> readTouchSlop = new ReadTouchSlop<>(mView);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(readTouchSlop);
        return readTouchSlop.touchSlop();
    }

    /**
     * Event times are stepped by hand rather than read off the clock: the velocity the platform
     * detector derives from them has to come out the same on every run, and the real time between
     * two dispatches on a busy emulator does not.
     */
    private void dispatch(
            final long downTime,
            final long eventTime,
            final int action,
            final float x,
            final float y) {
        final DispatchTouch<VIEW> dispatchTouch =
                new DispatchTouch<>(mView, downTime, eventTime, action, x, y);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(dispatchTouch);
    }

    /**
     * Waits until the children stop moving, so an assertion sees where the content came to rest.
     */
    public LaidOutChildren settle() {
        // Nothing public reports whether the animator is still running, so rest has to be inferred
        // from the children not moving. The fixed wait first covers an animation that has been
        // asked for but has not produced its first frame yet, which would otherwise read as rest.
        SystemClock.sleep(MINIMUM_SETTLE_MILLISECONDS);
        LaidOutChildren previous = children();
        int stableReads = 0;
        for (int attempt = 0; attempt < SETTLE_ATTEMPTS; attempt++) {
            sleepOneFrame();
            final LaidOutChildren current = children();
            if (current.sameGeometryAs(previous)) {
                stableReads++;
                if (stableReads >= STABLE_READS_REQUIRED) {
                    return current;
                }
            } else {
                stableReads = 0;
            }
            previous = current;
        }
        throw new AssertionError("the content never came to rest: " + previous);
    }

    private void waitForLayout() {
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final IsLaidOut<VIEW> isLaidOut = new IsLaidOut<>(mView);
        for (int attempt = 0; attempt < LAYOUT_ATTEMPTS; attempt++) {
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(isLaidOut);
            if (isLaidOut.isLaidOut()) {
                return;
            }
            sleepOneFrame();
        }
        throw new AssertionError("the view never finished a layout pass");
    }

    private void requireViewport(final int width, final int height) {
        final LaidOutChildren children = children();
        final boolean widthMatches = children.viewWidth() == width;
        final boolean heightMatches = children.viewHeight() == height;
        if (!widthMatches || !heightMatches) {
            throw new AssertionError(
                    "the harness asked for a "
                            + width
                            + "x"
                            + height
                            + " viewport but the framework gave it "
                            + children.viewWidth()
                            + "x"
                            + children.viewHeight());
        }
    }

    private static void sleepOneFrame() {
        SystemClock.sleep(FRAME_MILLISECONDS);
    }

    /** What a test does to a view on the main thread before the content is measured. */
    public interface ViewSetup<VIEW> {
        void setUp(VIEW view);
    }

    private static final class InflateAndAttach<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements ActivityScenario.ActivityAction<HarnessActivity> {

        private final int mLayoutResource;
        private final int mWidth;
        private final int mHeight;
        private VIEW mView;

        InflateAndAttach(final int layoutResource, final int width, final int height) {
            mLayoutResource = layoutResource;
            mWidth = width;
            mHeight = height;
        }

        VIEW view() {
            return mView;
        }

        @Override
        public void perform(final HarnessActivity activity) {
            final FrameLayout content = activity.content();
            content.removeAllViews();
            final LayoutInflater layoutInflater = LayoutInflater.from(activity);
            final View inflated = layoutInflater.inflate(mLayoutResource, content, false);
            final ViewGroup.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(mWidth, mHeight);
            inflated.setLayoutParams(layoutParams);
            content.addView(inflated);
            mView = inflated.findViewById(R.id.parchment_view);
        }
    }

    private static final class SetAdapter<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private final BaseAdapter mAdapter;

        SetAdapter(final VIEW view, final BaseAdapter adapter) {
            mView = view;
            mAdapter = adapter;
        }

        @Override
        public void run() {
            mView.setAdapter(mAdapter);
        }
    }

    private static final class SetSelection<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private final int mPosition;

        SetSelection(final VIEW view, final int position) {
            mView = view;
            mPosition = position;
        }

        @Override
        public void run() {
            mView.setSelection(mPosition);
        }
    }

    private static final class ReadSelectedPosition<
                    VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private int mSelectedPosition;

        ReadSelectedPosition(final VIEW view) {
            mView = view;
        }

        int selectedPosition() {
            return mSelectedPosition;
        }

        @Override
        public void run() {
            mSelectedPosition = mView.getSelectedItemPosition();
        }
    }

    private static final class PerformSetup<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private final ViewSetup<VIEW> mSetup;

        PerformSetup(final VIEW view, final ViewSetup<VIEW> setup) {
            mView = view;
            mSetup = setup;
        }

        @Override
        public void run() {
            mSetup.setUp(mView);
        }
    }

    private static final class IsLaidOut<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private boolean mIsLaidOut;

        IsLaidOut(final VIEW view) {
            mView = view;
        }

        boolean isLaidOut() {
            return mIsLaidOut;
        }

        @Override
        public void run() {
            final boolean hasSize = mView.getWidth() > 0 && mView.getHeight() > 0;
            final boolean layoutIsPending = mView.isLayoutRequested();
            mIsLaidOut = hasSize && !layoutIsPending;
        }
    }

    private static final class ReadTouchSlop<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private int mTouchSlop;

        ReadTouchSlop(final VIEW view) {
            mView = view;
        }

        int touchSlop() {
            return mTouchSlop;
        }

        @Override
        public void run() {
            final ViewConfiguration viewConfiguration = ViewConfiguration.get(mView.getContext());
            mTouchSlop = viewConfiguration.getScaledTouchSlop();
        }
    }

    private static final class ReadChildren<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private LaidOutChildren mChildren;

        ReadChildren(final VIEW view) {
            mView = view;
        }

        LaidOutChildren children() {
            return mChildren;
        }

        @Override
        public void run() {
            final int childCount = mView.getChildCount();
            final int[] lefts = new int[childCount];
            final int[] tops = new int[childCount];
            final int[] rights = new int[childCount];
            final int[] bottoms = new int[childCount];
            final int[] adapterPositions = new int[childCount];
            for (int index = 0; index < childCount; index++) {
                final View child = mView.getChildAt(index);
                lefts[index] = child.getLeft();
                tops[index] = child.getTop();
                rights[index] = child.getRight();
                bottoms[index] = child.getBottom();
                adapterPositions[index] = mView.getPositionForView(child);
            }
            mChildren =
                    new LaidOutChildren(
                            lefts,
                            tops,
                            rights,
                            bottoms,
                            adapterPositions,
                            mView.getWidth(),
                            mView.getHeight());
        }
    }

    private static final class DispatchTouch<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
            implements Runnable {

        private final VIEW mView;
        private final long mDownTime;
        private final long mEventTime;
        private final int mAction;
        private final float mX;
        private final float mY;

        DispatchTouch(
                final VIEW view,
                final long downTime,
                final long eventTime,
                final int action,
                final float x,
                final float y) {
            mView = view;
            mDownTime = downTime;
            mEventTime = eventTime;
            mAction = action;
            mX = x;
            mY = y;
        }

        @Override
        public void run() {
            final MotionEvent event =
                    MotionEvent.obtain(mDownTime, mEventTime, mAction, mX, mY, NO_META_STATE);
            try {
                mView.dispatchTouchEvent(event);
            } finally {
                event.recycle();
            }
        }
    }
}
