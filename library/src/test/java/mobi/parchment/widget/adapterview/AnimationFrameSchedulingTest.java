// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import mobi.parchment.widget.adapterview.gridview.Group;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystemClock;

@RunWith(RobolectricTestRunner.class)
public class AnimationFrameSchedulingTest {

    private static final int VIEW_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int ADAPTER_SIZE = 10;
    private static final float FLING_VELOCITY = -1000f;

    private FrameLayout mContent;
    private CountingListView mListView;

    @Before
    public void setup() {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        mContent = new FrameLayout(activity);
        activity.setContentView(mContent);
        mListView = (CountingListView) View.inflate(activity, R.layout.counting_list_view, null);
        mListView.setAdapter(new FixedSizeAdapter(activity));
        mContent.addView(mListView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        assertThat(mListView.isAttachedToWindow()).isTrue();
        measureAndLayout();
        mListView.reset();
    }

    private void measureAndLayout() {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY);
        mListView.forceLayout();
        mListView.measure(measureSpec, measureSpec);
        mListView.layout(0, 0, VIEW_SIZE, VIEW_SIZE);
    }

    private void idleMainLooper() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    private View firstChild() {
        return mListView.getChildAt(0);
    }

    @Test
    public void firstLayout_centersTheFirstCell() {
        assertThat(mListView.getChildCount()).isGreaterThan(0);
        assertThat(firstChild().getLeft()).isEqualTo(100);
    }

    @Test
    public void requestAnimationFrame_runsTheFrameOnTheNextLooperPassNotSynchronously() {
        mListView.requestAnimationFrame();

        assertThat(mListView.mFramesRun).isEqualTo(0);

        idleMainLooper();

        assertThat(mListView.mFramesRun).isEqualTo(1);
        assertThat(mListView.mLayoutRequests).isEqualTo(0);
        assertThat(mListView.mLayoutPasses).isEqualTo(0);
    }

    @Test
    public void aFrame_movesTheCellsWithoutALayoutPass() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);

        idleMainLooper();

        assertThat(firstChild().getLeft()).isEqualTo(55);
        assertThat(mListView.mFramesRun).isEqualTo(1);
        assertThat(mListView.mLayoutPasses).isEqualTo(0);
        assertThat(mListView.mLayoutRequests).isEqualTo(0);
        assertThat(mListView.isLayoutRequested()).isFalse();
    }

    @Test
    public void aFling_runsToRestOnAnimationFramesWithoutLayoutPasses() {
        fling();

        idleMainLooper();

        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mListView.mFramesRun).isGreaterThan(1);
        assertThat(mListView.mLayoutPasses).isEqualTo(0);
        assertThat(mListView.mLayoutRequests).isEqualTo(0);
        assertThat(mListView.getChildAt(2).getLeft()).isEqualTo(100);
    }

    @Test
    public void aFrame_whileALayoutIsPending_leavesTheWorkToTheLayoutPass() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        mListView.requestLayout();
        assertThat(mListView.mLayoutRequests).isEqualTo(1);

        idleMainLooper();

        assertThat(firstChild().getLeft()).isEqualTo(55);
        assertThat(mListView.mFramesRun).isEqualTo(1);
        assertThat(mListView.mLayoutPasses).isEqualTo(1);
        assertThat(mListView.mLayoutRequests).isEqualTo(1);
    }

    @Test
    public void requestAnimationFrame_twiceBeforeTheFrame_runsOneFrame() {
        mListView.requestAnimationFrame();
        mListView.requestAnimationFrame();

        idleMainLooper();

        assertThat(mListView.mFramesRun).isEqualTo(1);
    }

    @Test
    public void requestAnimationFrame_afterTheFrameRan_runsAnotherFrame() {
        mListView.requestAnimationFrame();
        idleMainLooper();

        mListView.requestAnimationFrame();
        idleMainLooper();

        assertThat(mListView.mFramesRun).isEqualTo(2);
    }

    @Test
    public void detachingTheView_dropsThePendingFrameAndAcceptsANewOne() {
        mListView.requestAnimationFrame();

        mContent.removeView(mListView);
        assertThat(mListView.isAttachedToWindow()).isFalse();
        mContent.addView(mListView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        mListView.reset();

        mListView.requestAnimationFrame();
        idleMainLooper();

        assertThat(mListView.mFramesRun).isEqualTo(1);
    }

    @Test
    public void aDrag_movesTheCellsOnTheNextFrameWithoutRequestingLayoutDuringLayout() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);

        assertThat(mListView.mFrameRequests).isEqualTo(1);
        assertThat(mListView.mLayoutRequests).isEqualTo(0);
        idleMainLooper();
        assertThat(mListView.mFramesRun).isEqualTo(1);

        measureAndLayout();

        assertThat(firstChild().getLeft()).isEqualTo(55);
        assertThat(mListView.mLayoutRequestsDuringLayout).isEqualTo(0);
    }

    @Test
    public void onLayout_whileAnimating_requestsTheNextFrameThroughTheScheduler() {
        fling();
        mListView.reset();

        measureAndLayout();

        assertThat(mListView.mFrameRequests).isEqualTo(1);
        assertThat(mListView.mLayoutRequestsDuringLayout).isEqualTo(0);
    }

    @Test
    public void aFlingThatEndsInsideOnLayout_comesToRestOnTheSnapPosition() {
        fling();
        ShadowSystemClock.advanceBy(Duration.ofSeconds(5));
        mListView.reset();

        measureAndLayout();

        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mListView.getChildAt(2).getLeft()).isEqualTo(100);
        assertThat(mListView.mLayoutRequestsDuringLayout).isEqualTo(0);
        assertThat(mListView.mLayoutRequests).isEqualTo(0);
    }

    @Test
    public void anAnimationThatEndsInsideOnLayoutOffTheSnapPosition_handsOffToItsSnapInThatFrame() {
        mListView.mGestureListener.setAnimateToDistance(-45);
        ShadowSystemClock.advanceBy(Duration.ofSeconds(5));
        mListView.reset();

        measureAndLayout();

        assertThat(firstChild().getLeft()).isEqualTo(55);
        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.snapingTo);
        assertThat(mListView.mFramesRun).isEqualTo(0);
        assertThat(mListView.mLayoutRequestsDuringLayout).isEqualTo(0);
        assertThat(mListView.mLayoutRequests).isEqualTo(0);

        idleMainLooper();

        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mListView.mFramesRun).isGreaterThan(1);
        assertThat(mListView.mLayoutRequestsDuringLayout).isEqualTo(0);
        assertThat(firstChild().getLeft()).isEqualTo(100);
    }

    @Test
    public void aDragReleaseSnapAndRest_reportsDraggingThenSettlingThenIdleEachExactlyOnce() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        mListView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(firstChild().getLeft()).isEqualTo(100);
        assertThat(listener.mScrollStates)
                .containsExactly(ScrollState.dragging, ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void aFling_reportsDraggingThenSettlingThenIdle() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        mListView.mGestureListener.onFling(down(), moveTo(100f), FLING_VELOCITY, 0f);
        mListView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(listener.mScrollStates)
                .containsExactly(ScrollState.dragging, ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void aTapToSnapFromRest_reportsSettlingThenIdleWithoutADragging() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onSingleTapUp(up());
        mListView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(listener.mScrollStates).containsExactly(ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void aProgrammaticMoveFromRest_reportsSettlingThenIdleWithoutADragging() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.setAnimateToDistance(-CELL_SIZE);
        idleMainLooper();

        assertThat(listener.mScrollStates).containsExactly(ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void aGestureUnderTheTouchSlop_reportsNothing() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(199f), 1f, 0f);
        mListView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(mListView.mFrameRequests).isEqualTo(0);
        assertThat(listener.mCalls).isEmpty();
    }

    @Test
    public void aDragThatEndsOnTheSnapPosition_stillReportsIdle() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(100f), 100f, 0f);
        idleMainLooper();
        assertThat(firstChild().getLeft()).isEqualTo(0);
        assertThat(listener.mScrollStates).containsExactly(ScrollState.dragging);
        mListView.reset();

        mListView.mGestureListener.onUp();

        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mListView.mFrameRequests).isEqualTo(1);

        idleMainLooper();

        assertThat(listener.mScrollStates).containsExactly(ScrollState.dragging, ScrollState.idle);
    }

    @Test
    public void aFrameThatMovesNothing_reportsNoDisplacement() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.requestAnimationFrame();
        idleMainLooper();

        assertThat(mListView.mFramesRun).isEqualTo(1);
        assertThat(listener.mCalls).isEmpty();
    }

    @Test
    public void theSummedDisplacementOfAFling_isTheDistanceTheContentMoved() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        final int before = mListView.mLayoutManager.getViewForPosition(0).getLeft();

        fling();
        idleMainLooper();

        final int after = mListView.mLayoutManager.getViewForPosition(0).getLeft();
        assertThat(after - before).isLessThan(0);
        assertThat(listener.mDisplacementSum).isEqualTo(after - before);
    }

    @Test
    public void aFlingBack_reportsPositiveDisplacementSummingToTheDistanceTheContentMoved() {
        dragForwardAndSettle();
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        final int trackedPosition = mListView.getPositionForView(firstChild());
        final int before = firstChild().getLeft();

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onFling(down(), moveTo(300f), -FLING_VELOCITY, 0f);
        mListView.mGestureListener.onUp();
        idleMainLooper();

        final int after = mListView.mLayoutManager.getViewForPosition(trackedPosition).getLeft();
        assertThat(after - before).isGreaterThan(0);
        assertThat(listener.mDisplacementSum).isEqualTo(after - before);
    }

    @Test
    public void theCallbacks_runAfterTheFramesLayout_soAListenerSeesTheCellsWhereTheyLanded() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        assertThat(firstChild().getLeft()).isEqualTo(100);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();

        assertThat(firstChild().getLeft()).isEqualTo(55);
        assertThat(listener.mFirstChildLeftAtFirstCallback).isEqualTo(55);
    }

    @Test
    public void aFrameWithMovementAndAStateChange_reportsTheDisplacementBeforeTheState() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();

        assertThat(listener.mCalls).containsExactly("scrolled:-45", "state:dragging");
    }

    @Test
    public void aListenerRemovedMidFling_getsNoFurtherCallbacks() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        fling();
        runOneFrame();
        final int callsBeforeRemoval = listener.mCalls.size();
        assertThat(callsBeforeRemoval).isGreaterThan(0);

        mListView.setOnScrollListener(null);
        idleMainLooper();

        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(listener.mCalls).hasSize(callsBeforeRemoval);
    }

    @Test
    public void aListenerRemovedFromInsideOnScrolled_getsNoStateChangeInThatFrame() {
        final SelfRemovingScrollListener listener = new SelfRemovingScrollListener();
        mListView.setOnScrollListener(listener);

        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();

        assertThat(listener.mScrolledCalls).isEqualTo(1);
        assertThat(listener.mStateChanges).isEqualTo(0);
    }

    @Test
    public void detachingTheViewMidFling_stopsTheCallbacks() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        fling();
        runOneFrame();
        final int callsBeforeDetach = listener.mCalls.size();
        assertThat(callsBeforeDetach).isGreaterThan(0);

        mContent.removeView(mListView);
        assertThat(mListView.isAttachedToWindow()).isFalse();
        idleMainLooper();

        assertThat(listener.mCalls).hasSize(callsBeforeDetach);
    }

    @Test
    public void aListenerAttachedMidFling_isToldTheCurrentStateOnTheNextFrame() {
        fling();
        runOneFrame();
        final RecordingScrollListener listener = new RecordingScrollListener();

        mListView.setOnScrollListener(listener);
        idleMainLooper();

        assertThat(listener.mScrollStates).containsExactly(ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void circularScroll_reportsDraggingThenSettlingThenIdleWithoutEverReachingAnEnd() {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        final FrameLayout content = new FrameLayout(activity);
        activity.setContentView(content);
        final CountingListView listView =
                (CountingListView)
                        View.inflate(activity, R.layout.counting_circular_list_view, null);
        listView.setAdapter(new FixedSizeAdapter(activity));
        content.addView(listView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        layOut(listView);
        final RecordingScrollListener listener = new RecordingScrollListener();
        listView.setOnScrollListener(listener);
        final View trackedCell = listView.getChildAt(listView.getChildCount() - 1);
        final int trackedPosition = listView.getPositionForView(trackedCell);
        final int before = trackedCell.getLeft();

        listView.mGestureListener.onDown(down());
        listView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        listView.mGestureListener.onFling(down(), moveTo(100f), FLING_VELOCITY, 0f);
        listView.mGestureListener.onUp();
        idleMainLooper();

        final int after = listView.mLayoutManager.getViewForPosition(trackedPosition).getLeft();
        assertThat(listener.mScrollStates)
                .containsExactly(ScrollState.dragging, ScrollState.settling, ScrollState.idle);
        assertThat(listener.mDisplacementSum).isEqualTo(after - before);
    }

    @Test
    public void aGridView_reportsTheDragAndTheRestToItsListener() {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        final FrameLayout content = new FrameLayout(activity);
        activity.setContentView(content);
        final CountingGridView gridView =
                (CountingGridView) View.inflate(activity, R.layout.counting_grid_view, null);
        gridView.setAdapter(new FixedSizeAdapter(activity));
        content.addView(gridView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        layOut(gridView);
        final RecordingScrollListener listener = new RecordingScrollListener();
        gridView.setOnScrollListener(listener);

        final int before = gridView.getChildAt(0).getLeft();

        gridView.mGestureListener.onDown(down());
        gridView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();

        assertThat(gridView.getChildAt(0).getLeft() - before).isEqualTo(-45);
        assertThat(listener.mCalls).containsExactly("scrolled:-45", "state:dragging");

        gridView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(gridView.getChildAt(0).getLeft()).isEqualTo(before);
        assertThat(listener.mScrollStates)
                .containsExactly(ScrollState.dragging, ScrollState.settling, ScrollState.idle);
        assertThat(listener.mDisplacementSum).isEqualTo(0);
    }

    @Test
    public void aGridPatternView_withoutSnapping_reportsTheDragAndTheRestToItsListener() {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        final FrameLayout content = new FrameLayout(activity);
        activity.setContentView(content);
        final CountingGridPatternView patternView =
                (CountingGridPatternView)
                        View.inflate(activity, R.layout.counting_grid_pattern_view, null);
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
        itemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
        patternView.addGridPatternGroupDefinition(itemDefinitions);
        patternView.setAdapter(new FixedSizeAdapter(activity));
        content.addView(patternView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        layOut(patternView);
        final RecordingScrollListener listener = new RecordingScrollListener();
        patternView.setOnScrollListener(listener);
        final int before = patternView.getChildAt(0).getLeft();

        patternView.mGestureListener.onDown(down());
        patternView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        patternView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(patternView.getChildAt(0).getLeft() - before).isEqualTo(-45);
        assertThat(listener.mScrollStates).containsExactly(ScrollState.dragging, ScrollState.idle);
        assertThat(listener.mDisplacementSum).isEqualTo(-45);
    }

    @Test
    public void aListenerAttachedMidDrag_isToldTheDraggingStateOnTheNextFrame() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        assertThat(mListView.mGestureListener.getState())
                .isEqualTo(AdapterAnimator.State.scrolling);
        final RecordingScrollListener listener = new RecordingScrollListener();

        mListView.setOnScrollListener(listener);
        idleMainLooper();

        assertThat(listener.mScrollStates).containsExactly(ScrollState.dragging);
    }

    @Test
    public void aListenerAttachedMidDragAndThenReleased_isToldTheRestOfTheGesture() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        idleMainLooper();

        mListView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(listener.mScrollStates)
                .containsExactly(ScrollState.dragging, ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void reAttachingTheSameListenerMidDrag_doesNotReportDraggingTwice() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        assertThat(listener.mScrollStates).containsExactly(ScrollState.dragging);

        mListView.setOnScrollListener(listener);
        mListView.mGestureListener.onScroll(down(), moveTo(145f), 10f, 0f);
        idleMainLooper();

        assertThat(listener.mScrollStates).containsExactly(ScrollState.dragging);
    }

    @Test
    public void aListenerReplacedFromInsideACallback_isNotToldTheStateItsPredecessorGot() {
        final RecordingScrollListener replacement = new RecordingScrollListener();
        mListView.setOnScrollListener(new ReplacingScrollListener(replacement));
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();

        mListView.mGestureListener.onScroll(down(), moveTo(145f), 10f, 0f);
        idleMainLooper();

        assertThat(replacement.mCalls).containsExactly("scrolled:-10");
    }

    @Test
    public void aDragReleaseSnapAndRest_withAListener_runsTheSameFramesAsWithout() {
        dragReleaseAndSettle();
        final int framesWithoutAListener = mListView.mFramesRun;
        assertThat(framesWithoutAListener).isGreaterThan(1);

        setup();
        mListView.setOnScrollListener(new RecordingScrollListener());
        dragReleaseAndSettle();

        assertThat(mListView.mFramesRun).isEqualTo(framesWithoutAListener);
    }

    @Test
    public void setSelection_isAJumpAndNotAScroll_soItReportsNoDisplacement() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        mListView.setOnScrollListener(listener);
        final int before = mListView.getPositionForView(firstChild());

        mListView.setSelection(6);
        measureAndLayout();
        idleMainLooper();

        assertThat(mListView.getPositionForView(firstChild())).isNotEqualTo(before);
        assertThat(listener.mCalls).isEmpty();
    }

    @Test
    public void aDataSetChange_isAJumpAndNotAScroll_soItReportsNoDisplacement() {
        final RecordingScrollListener listener = new RecordingScrollListener();
        final ResizableAdapter adapter = new ResizableAdapter(mListView.getContext());
        mListView.setAdapter(adapter);
        measureAndLayout();
        mListView.setOnScrollListener(listener);

        adapter.setCount(2);
        measureAndLayout();
        idleMainLooper();

        assertThat(mListView.getChildCount()).isEqualTo(2);
        assertThat(listener.mCalls).isEmpty();
    }

    @Test
    public void aVerticalDrag_reportsTheDistanceTheCellsMovedDown() {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        final FrameLayout content = new FrameLayout(activity);
        activity.setContentView(content);
        final CountingListView listView =
                (CountingListView)
                        View.inflate(activity, R.layout.counting_vertical_list_view, null);
        listView.setAdapter(new FixedSizeAdapter(activity));
        content.addView(listView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        layOut(listView);
        final RecordingScrollListener listener = new RecordingScrollListener();
        listView.setOnScrollListener(listener);
        final int before = listView.getChildAt(0).getTop();

        listView.mGestureListener.onDown(down());
        listView.mGestureListener.onScroll(down(), moveDownTo(105f), 0f, 45f);
        idleMainLooper();

        assertThat(listView.getChildAt(0).getTop() - before).isEqualTo(-45);
        assertThat(listener.mCalls).containsExactly("scrolled:-45", "state:dragging");
    }

    @Test
    public void aViewPagerGestureHeldAtItsPage_reportsOnlyTheDistanceTheCellsMoved() {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        final FrameLayout content = new FrameLayout(activity);
        activity.setContentView(content);
        final CountingListView listView =
                (CountingListView)
                        View.inflate(activity, R.layout.counting_view_pager_list_view, null);
        listView.setAdapter(new FixedSizeAdapter(activity));
        content.addView(listView, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
        layOut(listView);
        layOut(listView);
        final RecordingScrollListener listener = new RecordingScrollListener();
        listView.setOnScrollListener(listener);
        final int before = listView.getChildAt(0).getLeft();

        listView.mGestureListener.onDown(down());
        listView.mGestureListener.onFling(down(), moveTo(100f), FLING_VELOCITY, 0f);
        listView.mGestureListener.onUp();
        idleMainLooper();

        assertThat(listView.mLayoutManager.getViewForPosition(1).getLeft()).isEqualTo(0);
        assertThat(listener.mScrollStates).containsExactly(ScrollState.settling, ScrollState.idle);
        assertThat(listener.mDisplacementSum).isEqualTo(before - CELL_SIZE);
    }

    private static void layOut(final View view) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY);
        view.forceLayout();
        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, VIEW_SIZE, VIEW_SIZE);
    }

    private void runOneFrame() {
        shadowOf(Looper.getMainLooper()).runOneTask();
    }

    private void dragReleaseAndSettle() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(155f), 45f, 0f);
        idleMainLooper();
        mListView.mGestureListener.onUp();
        idleMainLooper();
    }

    private void dragForwardAndSettle() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onScroll(down(), moveTo(-100f), 300f, 0f);
        idleMainLooper();
        mListView.mGestureListener.onUp();
        idleMainLooper();
    }

    private void fling() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onFling(down(), moveTo(100f), FLING_VELOCITY, 0f);
        mListView.mGestureListener.onUp();
        assertThat(mListView.mGestureListener.getState()).isEqualTo(AdapterAnimator.State.flinging);
    }

    private static MotionEvent up() {
        return MotionEvent.obtain(0, 50, MotionEvent.ACTION_UP, 200f, 150f, 0);
    }

    private static MotionEvent moveDownTo(final float y) {
        return MotionEvent.obtain(0, 10, MotionEvent.ACTION_MOVE, 200f, y, 0);
    }

    private static MotionEvent down() {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 200f, 150f, 0);
    }

    private static MotionEvent moveTo(final float x) {
        return MotionEvent.obtain(0, 10, MotionEvent.ACTION_MOVE, x, 150f, 0);
    }

    public static final class CountingListView extends ListView<BaseAdapter> {
        int mLayoutRequests;
        int mLayoutRequestsDuringLayout;
        int mFrameRequests;
        int mFramesRun;
        int mLayoutPasses;
        ChildTouchGestureListener mGestureListener;
        LayoutManager<View> mLayoutManager;
        private boolean mInLayout;

        public CountingListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        void reset() {
            mLayoutRequests = 0;
            mLayoutRequestsDuringLayout = 0;
            mFrameRequests = 0;
            mFramesRun = 0;
            mLayoutPasses = 0;
        }

        @Override
        protected AdapterViewInitializer<View> createAdapterViewInitializer(
                final Context context,
                final boolean isViewPager,
                final AdapterViewManager adapterViewManager,
                final LayoutManager<View> layoutManager,
                final boolean isVerticalScroll,
                final Drawable divider,
                final int dividerSize) {
            final AdapterViewInitializer<View> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll,
                            divider,
                            dividerSize);
            mGestureListener = initializer.getChildTouchListener();
            mLayoutManager = layoutManager;
            return initializer;
        }

        @Override
        public void requestLayout() {
            mLayoutRequests++;
            if (mInLayout) mLayoutRequestsDuringLayout++;
            super.requestLayout();
        }

        @Override
        public void requestAnimationFrame() {
            mFrameRequests++;
            super.requestAnimationFrame();
        }

        @Override
        protected void onAnimationFrame() {
            mFramesRun++;
            super.onAnimationFrame();
        }

        @Override
        protected void onLayout(
                final boolean changed,
                final int left,
                final int top,
                final int right,
                final int bottom) {
            mLayoutPasses++;
            mInLayout = true;
            super.onLayout(changed, left, top, right, bottom);
            mInLayout = false;
        }
    }

    public static final class CountingGridView extends GridView<BaseAdapter> {
        ChildTouchGestureListener mGestureListener;
        LayoutManager<Group> mLayoutManager;

        public CountingGridView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        protected AdapterViewInitializer<Group> createAdapterViewInitializer(
                final Context context,
                final boolean isViewPager,
                final AdapterViewManager adapterViewManager,
                final LayoutManager<Group> layoutManager,
                final boolean isVerticalScroll,
                final Drawable divider,
                final int dividerSize) {
            final AdapterViewInitializer<Group> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll,
                            divider,
                            dividerSize);
            mGestureListener = initializer.getChildTouchListener();
            mLayoutManager = layoutManager;
            return initializer;
        }
    }

    public static final class CountingGridPatternView extends GridPatternView<BaseAdapter> {
        ChildTouchGestureListener mGestureListener;
        LayoutManager<GridPatternGroup> mLayoutManager;

        public CountingGridPatternView(final Context context, final AttributeSet attributeSet) {
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
            final AdapterViewInitializer<GridPatternGroup> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll,
                            divider,
                            dividerSize);
            mGestureListener = initializer.getChildTouchListener();
            mLayoutManager = layoutManager;
            return initializer;
        }
    }

    private static final class RecordingScrollListener implements OnScrollListener {
        static final int NOT_RECORDED = Integer.MIN_VALUE;

        final List<ScrollState> mScrollStates = new ArrayList<ScrollState>();
        final List<String> mCalls = new ArrayList<String>();
        int mDisplacementSum;
        int mFirstChildLeftAtFirstCallback = NOT_RECORDED;

        @Override
        public void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement) {
            mDisplacementSum += displacement;
            mCalls.add("scrolled:" + displacement);
            recordFirstChildLeft(view);
        }

        @Override
        public void onScrollStateChanged(
                final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
            mScrollStates.add(scrollState);
            mCalls.add("state:" + scrollState);
            recordFirstChildLeft(view);
        }

        private void recordFirstChildLeft(final AbstractAdapterView<?, ?> view) {
            if (mFirstChildLeftAtFirstCallback != NOT_RECORDED) return;
            if (view.getChildCount() == 0) return;
            mFirstChildLeftAtFirstCallback = view.getChildAt(0).getLeft();
        }
    }

    private static final class SelfRemovingScrollListener implements OnScrollListener {
        int mScrolledCalls;
        int mStateChanges;

        @Override
        public void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement) {
            mScrolledCalls++;
            view.setOnScrollListener(null);
        }

        @Override
        public void onScrollStateChanged(
                final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
            mStateChanges++;
        }
    }

    private static final class ReplacingScrollListener implements OnScrollListener {
        private final OnScrollListener mReplacement;

        ReplacingScrollListener(final OnScrollListener replacement) {
            mReplacement = replacement;
        }

        @Override
        public void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement) {}

        @Override
        public void onScrollStateChanged(
                final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
            view.setOnScrollListener(mReplacement);
        }
    }

    private static final class ResizableAdapter extends BaseAdapter {
        private final Context mContext;
        private int mCount = ADAPTER_SIZE;

        ResizableAdapter(final Context context) {
            mContext = context;
        }

        void setCount(final int count) {
            mCount = count;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(final int position) {
            return position;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            if (convertView != null) return convertView;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(CELL_SIZE, CELL_SIZE));
            return view;
        }
    }

    private static final class FixedSizeAdapter extends BaseAdapter {
        private final Context mContext;

        FixedSizeAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return ADAPTER_SIZE;
        }

        @Override
        public Object getItem(final int position) {
            return position;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            if (convertView != null) return convertView;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(CELL_SIZE, CELL_SIZE));
            return view;
        }
    }
}
