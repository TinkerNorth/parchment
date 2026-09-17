// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import androidx.test.core.app.ApplicationProvider;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystemClock;

@RunWith(RobolectricTestRunner.class)
public class AdapterAnimatorTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int VIEW_SIZE = 100;
    private static final int ADAPTER_SIZE = 10;
    private static final float FLING_VELOCITY = -1000f;
    private static final int FLING_DISTANCE = -194;
    private static final int VIEWPORT_PAGING = 0;
    private static final int ONE_CELL_PER_GESTURE = 1;
    private static final long FLING_DURATION = 555;
    private static final long MAX_SNAP_DURATION = 500;
    private static final int FIRST_CELL = 0;
    private static final int SECOND_CELL = 1;
    private static final int FOURTH_CELL = 3;
    private static final int SEVENTH_CELL = 6;
    private static final int LAST_CELL = ADAPTER_SIZE - 1;
    private static final int PAST_THE_END = 50;
    private static final int A_NEGATIVE_POSITION = -3;
    private static final int EMPTY_ADAPTER = 0;
    private static final int TWO_CELLS = 2;
    private static final int CENTRED_CELL_START = (VIEW_GROUP_SIZE - VIEW_SIZE) / 2;
    private static final int FAR_CELL_SIZE = 600;
    private static final int FIRST_FAR_CELL = 3;
    private static final int CENTRED_FAR_CELL_START = (VIEW_GROUP_SIZE - FAR_CELL_SIZE) / 2;
    private static final int SIXTH_CELL = 5;
    private static final int FRAMES_OF_THE_FIRST_SEEK = 9;
    private static final int A_FULL_SEEK_STEP = 102;
    private static final long MILLISECONDS_TO_SEEK_SIX_CELLS = 94;
    private static final boolean REFUSES_EVERY_FRAME = true;
    private static final boolean HONOURS_EVERY_FRAME = false;
    private static final long ONE_FRAME = 16;
    private static final long FRAMES_UNTIL_THE_SEVENTH_CELL_IS_DRAWN = 4 * ONE_FRAME;
    private static final long A_WHOLE_ANIMATION = 5000;
    private static final int REST_FRAME_LIMIT = 500;
    private static final boolean SELECT_ON_SNAP = true;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean SCROLL_WITHIN_CONTENT = true;
    private static final boolean SCROLL_PAST_CONTENT = false;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean A_GESTURE = true;
    private static final boolean NOT_A_GESTURE = false;
    private static final float A_DRAG = 45f;
    private static final int START_OF_THE_VIEW = 0;
    private static final int THE_END_ALIGNED_CELL_START = VIEW_GROUP_SIZE - VIEW_SIZE;

    private final Context mContext = ApplicationProvider.getApplicationContext();
    private final RecordingFrameScheduler mFrameScheduler = new RecordingFrameScheduler();
    private final MyViewGroup mViewGroup = new MyViewGroup(mContext);
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final Animation mAnimation = new Animation();
    private final ScrollListenerDispatcher mScrollListenerDispatcher =
            new ScrollListenerDispatcher();
    private ListLayoutManager mLayoutManager;
    private AdapterAnimator mAdapterAnimator;

    @Before
    public void setup() {
        setup(false, SnapPosition.onScreen);
    }

    private void setup(final boolean snapToPosition, final SnapPosition snapPosition) {
        setup(snapToPosition, snapPosition, false, VIEWPORT_PAGING);
    }

    private void setup(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval) {
        setup(
                snapToPosition,
                snapPosition,
                isViewPager,
                viewPagerInterval,
                NO_SELECT_ON_SNAP,
                SCROLL_PAST_CONTENT,
                NOT_CIRCULAR,
                null);
    }

    private void setup(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final boolean selectOnSnap,
            final boolean scrollWithinContent,
            final boolean isCircularScroll,
            final OnSelectedListener onSelectedListener) {
        setup(
                snapToPosition,
                snapPosition,
                isViewPager,
                viewPagerInterval,
                selectOnSnap,
                scrollWithinContent,
                isCircularScroll,
                onSelectedListener,
                HONOURS_EVERY_FRAME);
    }

    private void setup(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final boolean selectOnSnap,
            final boolean scrollWithinContent,
            final boolean isCircularScroll,
            final OnSelectedListener onSelectedListener,
            final boolean refusesEveryFrame) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        isCircularScroll,
                        snapToPosition,
                        isViewPager,
                        viewPagerInterval,
                        snapPosition,
                        scrollWithinContent,
                        0,
                        selectOnSnap,
                        false,
                        false);
        mLayoutManager =
                new ListLayoutManager(
                        mViewGroup, onSelectedListener, mAdapterViewManager, attributes);
        mAdapterAnimator =
                new AdapterAnimator(
                        mViewGroup,
                        mFrameScheduler,
                        isViewPager,
                        false,
                        bridgeFor(mLayoutManager, refusesEveryFrame),
                        ViewConfiguration.get(mContext),
                        mScrollListenerDispatcher);
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    private static LayoutManagerBridge bridgeFor(
            final LayoutManager<?> layoutManager, final boolean refusesEveryFrame) {
        if (refusesEveryFrame) return new RefusingBridge(layoutManager);
        return new LayoutManagerBridge(layoutManager);
    }

    private void layOutCenterSnappingList() {
        layOutList(true, SnapPosition.center, false, VIEWPORT_PAGING);
    }

    private void layOutCenterSnappingList(final BaseAdapter adapter) {
        layOutList(
                true,
                SnapPosition.center,
                false,
                VIEWPORT_PAGING,
                ADAPTER_SIZE,
                NO_SELECT_ON_SNAP,
                SCROLL_PAST_CONTENT,
                NOT_CIRCULAR,
                null,
                adapter);
    }

    private void layOutList(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval) {
        layOutList(snapToPosition, snapPosition, isViewPager, viewPagerInterval, ADAPTER_SIZE);
    }

    private void layOutList(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final int adapterSize) {
        layOutList(
                snapToPosition,
                snapPosition,
                isViewPager,
                viewPagerInterval,
                adapterSize,
                NO_SELECT_ON_SNAP,
                SCROLL_PAST_CONTENT,
                NOT_CIRCULAR,
                null,
                new TestAdapter());
    }

    private void layOutList(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final int adapterSize,
            final boolean selectOnSnap,
            final boolean scrollWithinContent,
            final boolean isCircularScroll,
            final OnSelectedListener onSelectedListener,
            final BaseAdapter adapter) {
        layOutList(
                snapToPosition,
                snapPosition,
                isViewPager,
                viewPagerInterval,
                adapterSize,
                selectOnSnap,
                scrollWithinContent,
                isCircularScroll,
                onSelectedListener,
                adapter,
                HONOURS_EVERY_FRAME);
    }

    private void layOutList(
            final boolean snapToPosition,
            final SnapPosition snapPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final int adapterSize,
            final boolean selectOnSnap,
            final boolean scrollWithinContent,
            final boolean isCircularScroll,
            final OnSelectedListener onSelectedListener,
            final BaseAdapter adapter,
            final boolean refusesEveryFrame) {
        setup(
                snapToPosition,
                snapPosition,
                isViewPager,
                viewPagerInterval,
                selectOnSnap,
                scrollWithinContent,
                isCircularScroll,
                onSelectedListener,
                refusesEveryFrame);
        mAdapterViewManager.setAdapter(adapter);
        setAdapterSize(adapter, adapterSize);
        layout();
    }

    private static void setAdapterSize(final BaseAdapter adapter, final int adapterSize) {
        final boolean isResizable = adapter instanceof TestAdapter;
        if (!isResizable) return;
        final TestAdapter resizableAdapter = (TestAdapter) adapter;
        resizableAdapter.setAdapterSize(adapterSize);
    }

    private void layout() {
        mAdapterAnimator.computeScrollOffset();
        final Animation animation = mAdapterAnimator.getAnimation();
        mLayoutManager.layout(mViewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void frame() {
        layout();
        mAdapterAnimator.onFrameLaidOut();
    }

    private void runToRest() {
        for (int frames = 0; frames < REST_FRAME_LIMIT; frames++) {
            ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
            frame();
            final boolean isAtRest = mAdapterAnimator.getState() == AdapterAnimator.State.notMoving;
            if (isAtRest) return;
        }
        throw new AssertionError("the animation never came to rest");
    }

    private List<Integer> runTheSeek() {
        final List<Integer> displacements = new ArrayList<Integer>();
        for (int frames = 0; frames < REST_FRAME_LIMIT; frames++) {
            final boolean isSeeking =
                    mAdapterAnimator.getState() == AdapterAnimator.State.seekingTo;
            if (!isSeeking) return displacements;
            ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
            frame();
            final int displacement = Math.abs(mLayoutManager.getFrameDisplacement());
            displacements.add(displacement);
        }
        throw new AssertionError("the seek never ended");
    }

    private List<Integer> cellStarts() {
        final List<Integer> starts = new ArrayList<Integer>();
        for (final View view : mViewGroup.mViews) {
            starts.add(view.getLeft());
        }
        return starts;
    }

    @Test
    public void getAnimation_beforeAnyScrollOffsetIsComputed_appliesNothingAndKeepsFlinging() {
        mAdapterAnimator.onFling(down(), up(), 1000f, 0f);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);

        final Animation animation = mAdapterAnimator.getAnimation();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);
        assertThat(animation.getDisplacement()).isEqualTo(0);
    }

    @Test
    public void anAnimationThatEndsThisFrameOffTheSnapPosition_handsOffToItsSnapInTheSameFrame() {
        layOutCenterSnappingList();
        mAdapterAnimator.setAnimateToDistance(-45);
        ShadowSystemClock.advanceBy(Duration.ofMillis(MAX_SNAP_DURATION + 1));
        mFrameScheduler.mRequests = 0;

        layout();
        assertThat(mLayoutManager.getViewForPosition(0).getLeft()).isEqualTo(55);
        mAdapterAnimator.onFrameLaidOut();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
        assertThat(mFrameScheduler.mRequests).isEqualTo(1);

        ShadowSystemClock.advanceBy(Duration.ofMillis(16));
        assertThat(nextFrameDisplacement()).isNotEqualTo(0);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
    }

    @Test
    public void onFling_withSnapping_endsTheFlingOnTheNearestSnapPosition() {
        layOutCenterSnappingList();
        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(FLING_DURATION + 1));
        mFrameScheduler.mRequests = 0;

        layout();
        mAdapterAnimator.onFrameLaidOut();

        assertThat(mLayoutManager.getViewForPosition(2).getLeft()).isEqualTo(100);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void onFling_withoutSnapping_keepsTheNaturalFlingEnd() {
        layOutList(false, SnapPosition.onScreen, false, VIEWPORT_PAGING);
        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(FLING_DURATION + 1));

        layout();

        assertThat(mLayoutManager.getViewForPosition(0)).isNull();
        assertThat(mLayoutManager.getViewForPosition(1).getLeft())
                .isEqualTo(VIEW_SIZE + FLING_DISTANCE);
    }

    @Test
    public void onFling_asAViewPager_movesExactlyOnePage() {
        layOutList(true, SnapPosition.start, true, ONE_CELL_PER_GESTURE);
        layout();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(MAX_SNAP_DURATION + 1));

        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(nextFrameDisplacement()).isEqualTo(-VIEW_SIZE);
    }

    @Test
    public void onFling_asAViewPagerInTheOtherDirection_movesExactlyOneCellBack() {
        layOutList(true, SnapPosition.start, true, ONE_CELL_PER_GESTURE);
        layout();

        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(MAX_SNAP_DURATION + 1));
        layout();
        mAdapterAnimator.onFrameLaidOut();

        assertThat(mLayoutManager.getViewForPosition(1).getLeft()).isEqualTo(0);

        layout();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.onFling(down(), up(), -FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(MAX_SNAP_DURATION + 1));

        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(nextFrameDisplacement()).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void anAnimationThatEndsOnTheSnapPosition_comesToRestWithoutASnap() {
        layOutCenterSnappingList();
        mAdapterAnimator.setAnimateToDistance(-VIEW_GROUP_SIZE);
        ShadowSystemClock.advanceBy(Duration.ofMillis(MAX_SNAP_DURATION + 1));
        mFrameScheduler.mRequests = 0;

        layout();
        assertThat(mLayoutManager.getViewForPosition(3).getLeft()).isEqualTo(100);
        mAdapterAnimator.onFrameLaidOut();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void onFrameLaidOut_whileStillAnimating_keepsTheAnimationGoing() {
        layOutCenterSnappingList();
        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(16));
        layout();

        mAdapterAnimator.onFrameLaidOut();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);
    }

    @Test
    public void onFrameLaidOut_whileDragging_keepsScrolling() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(45f), 45f, 0f);
        layout();

        mAdapterAnimator.onFrameLaidOut();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.scrolling);
    }

    @Test
    public void onFrameLaidOut_atRest_staysAtRest() {
        layOutCenterSnappingList();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.onFrameLaidOut();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void computeScrollOffset_afterTheScrollerEndedInAnEarlierFrame_stillHandsOff() {
        layOutCenterSnappingList();
        mAdapterAnimator.setAnimateToDistance(-45);
        ShadowSystemClock.advanceBy(Duration.ofMillis(MAX_SNAP_DURATION + 1));
        layout();

        assertThat(nextFrameDisplacement()).isEqualTo(0);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
    }

    @Test
    public void computeScrollOffset_atRestAwayFromTheSnapPosition_startsTheSnap() {
        layOutCenterSnappingList();
        mAnimation.newAnimation();
        mAnimation.setDisplacement(-45);
        mLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);

        assertThat(nextFrameDisplacement()).isEqualTo(0);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
    }

    @Test
    public void getAnimation_afterTheScrollOffsetIsComputed_keepsFlinging() {
        mAdapterAnimator.onFling(down(), up(), 1000f, 0f);
        mAdapterAnimator.computeScrollOffset();

        mAdapterAnimator.getAnimation();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);
    }

    @Test
    public void onScroll_twiceBeforeALayout_accumulatesTheDisplacement() {
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(100f), -100f, 0f);
        mAdapterAnimator.onScroll(down(), moveTo(130f), -30f, 0f);

        assertThat(nextFrameDisplacement()).isEqualTo(130);
    }

    @Test
    public void getAnimation_whileScrolling_consumesTheDisplacement() {
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(100f), -100f, 0f);
        assertThat(nextFrameDisplacement()).isEqualTo(100);

        assertThat(nextFrameDisplacement()).isEqualTo(0);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.scrolling);
    }

    @Test
    public void onScroll_afterTheDisplacementWasConsumed_startsFromZero() {
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(100f), -100f, 0f);
        nextFrameDisplacement();

        mAdapterAnimator.onScroll(down(), moveTo(120f), -20f, 0f);

        assertThat(nextFrameDisplacement()).isEqualTo(20);
    }

    @Test
    public void onDown_dropsAScrollDisplacementThatWasNeverLaidOut() {
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(100f), -100f, 0f);

        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(105f), -5f, 0f);

        assertThat(nextFrameDisplacement()).isEqualTo(5);
    }

    @Test
    public void onFling_dropsAScrollDisplacementThatWasNeverLaidOut() {
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(100f), -100f, 0f);
        mAdapterAnimator.onFling(down(), up(), 1000f, 0f);

        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(105f), -5f, 0f);

        assertThat(nextFrameDisplacement()).isEqualTo(5);
    }

    @Test
    public void onFling_requestsAnAnimationFrameInsteadOfALayout() {
        mAdapterAnimator.onFling(down(), up(), 1000f, 0f);

        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void onScroll_requestsAnAnimationFrameInsteadOfALayout() {
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(100f), -100f, 0f);

        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void onSingleTapUp_onAView_requestsAnAnimationFrameInsteadOfALayout() {
        mAdapterAnimator.onSingleTapUp(up(), new View(mContext));

        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void setAnimateToDistance_requestsAnAnimationFrameInsteadOfALayout() {
        mAdapterAnimator.setAnimateToDistance(50);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.animatingTo);
        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void onUp_afterADragThatNeedsASnap_requestsAnAnimationFrameInsteadOfALayout() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(45f), 45f, 0f);
        layout();
        assertThat(mLayoutManager.getViewForPosition(0).getLeft()).isEqualTo(55);
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.onUp();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void onUp_afterADragThatEndsOnTheSnapPosition_doesNotRequestAFrame() {
        layOutCenterSnappingList();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onUp();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void scrollState_whileTheFingerDragsPastTheTouchSlop_isDragging() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(45f), 45f, 0f);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.scrolling);
        assertThat(scrollState()).isEqualTo(ScrollState.dragging);
    }

    @Test
    public void scrollState_forEveryAnimatorStateThatMovesTheContentOnItsOwn_isSettling() {
        assertThat(ScrollState.from(AdapterAnimator.State.flinging))
                .isEqualTo(ScrollState.settling);
        assertThat(ScrollState.from(AdapterAnimator.State.snapingTo))
                .isEqualTo(ScrollState.settling);
        assertThat(ScrollState.from(AdapterAnimator.State.animatingTo))
                .isEqualTo(ScrollState.settling);
        assertThat(ScrollState.from(AdapterAnimator.State.jumpingTo))
                .isEqualTo(ScrollState.settling);
        assertThat(ScrollState.from(AdapterAnimator.State.seekingTo))
                .isEqualTo(ScrollState.settling);
    }

    @Test
    public void scrollState_atRest_isIdle() {
        layOutCenterSnappingList();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(scrollState()).isEqualTo(ScrollState.idle);
    }

    @Test
    public void scrollState_afterAReleaseThatNeedsASnap_isSettlingWithoutPassingThroughIdle() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(45f), 45f, 0f);
        layout();
        assertThat(scrollState()).isEqualTo(ScrollState.dragging);

        mAdapterAnimator.onUp();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
        assertThat(scrollState()).isEqualTo(ScrollState.settling);
    }

    @Test
    public void scrollState_afterAReleaseThatNeedsNoSnap_isIdle() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(VIEW_SIZE), VIEW_SIZE, 0f);
        layout();
        assertThat(scrollState()).isEqualTo(ScrollState.dragging);

        mAdapterAnimator.onUp();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(scrollState()).isEqualTo(ScrollState.idle);
    }

    @Test
    public void scrollState_afterAFling_isSettlingUntilTheFlingComesToRest() {
        layOutCenterSnappingList();

        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();

        assertThat(scrollState()).isEqualTo(ScrollState.settling);

        ShadowSystemClock.advanceBy(Duration.ofMillis(FLING_DURATION + 1));
        layout();
        mAdapterAnimator.onFrameLaidOut();

        assertThat(scrollState()).isEqualTo(ScrollState.idle);
    }

    @Test
    public void scrollState_afterATapToSnapFromRest_isSettlingAndNeverDragging() {
        layOutCenterSnappingList();
        final View tappedView = mLayoutManager.getViewForPosition(2);

        mAdapterAnimator.onDown(down());
        assertThat(scrollState()).isEqualTo(ScrollState.idle);

        mAdapterAnimator.onSingleTapUp(up(), tappedView);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);
        assertThat(scrollState()).isEqualTo(ScrollState.settling);
    }

    @Test
    public void scrollState_forAGestureUnderTheTouchSlop_staysIdle() {
        layOutCenterSnappingList();

        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(1f), 1f, 0f);
        mAdapterAnimator.onUp();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(scrollState()).isEqualTo(ScrollState.idle);
    }

    @Test
    public void scrollState_afterAProgrammaticMoveFromRest_isSettlingAndNeverDragging() {
        layOutCenterSnappingList();

        mAdapterAnimator.setAnimateToDistance(-VIEW_SIZE);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.animatingTo);
        assertThat(scrollState()).isEqualTo(ScrollState.settling);
    }

    @Test
    public void onUp_afterADragThatNeedsNoSnap_withNoScrollListener_requestsNoFrame() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(VIEW_SIZE), VIEW_SIZE, 0f);
        layout();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.onUp();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void smoothScrollToPosition_toADrawnCell_landsWithoutASeek() {
        layOutCenterSnappingList();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.smoothScrollToPosition(SECOND_CELL);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.animatingTo);
        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void smoothScrollToPosition_toACellNotDrawn_seeksFirst() {
        layOutCenterSnappingList();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.seekingTo);
        assertThat(mFrameScheduler.mRequests).isEqualTo(1);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    @Test
    public void smoothScrollToPosition_theFrameTheTargetIsDrawn_handsOffToTheLandingInThatFrame() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        ShadowSystemClock.advanceBy(Duration.ofMillis(FRAMES_UNTIL_THE_SEVENTH_CELL_IS_DRAWN));

        frame();

        final View target = mLayoutManager.getViewForPosition(SEVENTH_CELL);
        assertThat(target).isNotNull();
        assertThat(target.getLeft()).isGreaterThan(CENTRED_CELL_START);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.animatingTo);

        ShadowSystemClock.advanceBy(Duration.ofMillis(A_WHOLE_ANIMATION));
        frame();

        assertThat(target.getLeft()).isEqualTo(CENTRED_CELL_START);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
    }

    @Test
    public void
            smoothScrollToPosition_duringAPageFling_takesAnAnimationOfItsOwnAndCrossesThePages() {
        layOutList(true, SnapPosition.start, true, ONE_CELL_PER_GESTURE);
        layout();
        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        final int theFlingsAnimationId = mAdapterAnimator.getAnimation().getId();

        mAdapterAnimator.smoothScrollToPosition(SIXTH_CELL);

        final Animation animation = mAdapterAnimator.getAnimation();
        assertThat(animation.isAGesture()).isFalse();
        assertThat(animation.getId()).isNotEqualTo(theFlingsAnimationId);

        runToRest();

        assertThat(mLayoutManager.getViewForPosition(SIXTH_CELL).getLeft())
                .isEqualTo(START_OF_THE_VIEW);
    }

    @Test
    public void smoothScrollToPosition_aSeekFrameTheLayoutRefuses_restsInsteadOfSeekingForever() {
        layOutList(
                true,
                SnapPosition.center,
                false,
                VIEWPORT_PAGING,
                ADAPTER_SIZE,
                NO_SELECT_ON_SNAP,
                SCROLL_PAST_CONTENT,
                NOT_CIRCULAR,
                null,
                new TestAdapter(),
                REFUSES_EVERY_FRAME);
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.seekingTo);

        ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
        frame();

        assertThat(mAdapterAnimator.getState()).isNotEqualTo(AdapterAnimator.State.seekingTo);
        runToRest();
    }

    @Test
    public void smoothScrollToPosition_aSeekThatEndsShortOfTheTarget_seeksAgainWithoutStopping() {
        layOutCenterSnappingList(new FarTailAdapter());
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        final List<Integer> seekDisplacements = runTheSeek();

        assertThat(seekDisplacements.size()).isGreaterThan(FRAMES_OF_THE_FIRST_SEEK);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.animatingTo);

        runToRest();

        assertThat(mLayoutManager.getViewForPosition(SEVENTH_CELL).getLeft())
                .isEqualTo(CENTRED_FAR_CELL_START);
    }

    @Test
    public void smoothScrollToPosition_aReSeek_keepsEveryFrameMovingAFullStep() {
        layOutCenterSnappingList(new FarTailAdapter());
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        final List<Integer> seekDisplacements = runTheSeek();

        assertThat(seekDisplacements.size()).isGreaterThan(FRAMES_OF_THE_FIRST_SEEK);
        assertThat(Collections.min(seekDisplacements)).isGreaterThanOrEqualTo(A_FULL_SEEK_STEP);
    }

    @Test
    public void smoothScrollToPosition_aSeekFrameThatLandsExactlyOnTheTarget_restsInThatFrame() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        ShadowSystemClock.advanceBy(Duration.ofMillis(MILLISECONDS_TO_SEEK_SIX_CELLS));
        mFrameScheduler.mRequests = 0;

        frame();

        assertThat(mLayoutManager.getViewForPosition(SEVENTH_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void smoothScrollToPosition_thenAnEmptyAdapterIsSet_rests() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        final TestAdapter emptyAdapter = new TestAdapter();
        mAdapterViewManager.setAdapter(emptyAdapter);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.seekingTo);

        ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
        frame();

        assertThat(mAdapterAnimator.getState()).isNotEqualTo(AdapterAnimator.State.seekingTo);
        runToRest();
    }

    @Test
    public void smoothScrollToPosition_theLandingEndsOnTheTargetsSnapPoint_andRestsWithoutASnap() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SECOND_CELL);
        ShadowSystemClock.advanceBy(Duration.ofMillis(A_WHOLE_ANIMATION));
        mFrameScheduler.mRequests = 0;

        frame();

        assertThat(mLayoutManager.getViewForPosition(SECOND_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void
            smoothScrollToPosition_toTheCellAlreadyAtTheSnapPosition_changesNoStateAndRequestsNoFrame() {
        layOutCenterSnappingList();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.smoothScrollToPosition(FIRST_CELL);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void smoothScrollToPosition_pastTheEnd_scrollsToTheLastCell() {
        layOutCenterSnappingList();

        mAdapterAnimator.smoothScrollToPosition(PAST_THE_END);
        runToRest();

        assertThat(mLayoutManager.getViewForPosition(LAST_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
    }

    @Test
    public void smoothScrollToPosition_negative_scrollsToTheFirstCell() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        runToRest();
        assertThat(mLayoutManager.getViewForPosition(FIRST_CELL)).isNull();

        mAdapterAnimator.smoothScrollToPosition(A_NEGATIVE_POSITION);
        runToRest();

        assertThat(mLayoutManager.getViewForPosition(FIRST_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
    }

    @Test
    public void smoothScrollToPosition_onAnEmptyAdapter_doesNothing() {
        layOutList(true, SnapPosition.center, false, VIEWPORT_PAGING, EMPTY_ADAPTER);
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.smoothScrollToPosition(FIRST_CELL);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void smoothScrollToPosition_beforeAnyLayout_doesNothing() {
        setup(true, SnapPosition.center);
        final TestAdapter adapter = new TestAdapter();
        mAdapterViewManager.setAdapter(adapter);
        adapter.setAdapterSize(ADAPTER_SIZE);
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
    }

    @Test
    public void smoothScrollToPosition_thenATouchDown_stopsAndSnapsLikeAnInterruptedFling() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
        frame();
        assertThat(mLayoutManager.getViewForPosition(FIRST_CELL).getLeft())
                .isNotEqualTo(CENTRED_CELL_START);

        mAdapterAnimator.onDown(down());

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);

        runToRest();

        assertThat(cellStarts()).contains(CENTRED_CELL_START);
    }

    @Test
    public void smoothScrollToPosition_thenASetSelection_restsAtTheJump() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        mLayoutManager.setSelected(FOURTH_CELL, mViewGroup);

        ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
        frame();

        assertThat(mLayoutManager.getViewForPosition(FOURTH_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
    }

    @Test
    public void
            smoothScrollToPosition_thenADataSetChangeRemovingTheTarget_restsWithoutSeekingForever() {
        final TestAdapter adapter = new TestAdapter();
        layOutCenterSnappingList(adapter);
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        adapter.setAdapterSize(TWO_CELLS);

        ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
        frame();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
    }

    @Test
    public void smoothScrollToPosition_thenASmallerAdapterIsSet_restsOnItsLastCell() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        final TestAdapter smallerAdapter = new TestAdapter();
        smallerAdapter.setAdapterSize(TWO_CELLS);
        mAdapterViewManager.setAdapter(smallerAdapter);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.seekingTo);

        runToRest();

        assertThat(mLayoutManager.getViewForPosition(SECOND_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
    }

    @Test
    public void smoothScrollToPosition_withSelectOnSnap_selectsTheTargetOnceWhenItLands() {
        final RecordingSelectedListener selections = new RecordingSelectedListener();
        layOutList(
                true,
                SnapPosition.center,
                false,
                VIEWPORT_PAGING,
                ADAPTER_SIZE,
                SELECT_ON_SNAP,
                SCROLL_PAST_CONTENT,
                NOT_CIRCULAR,
                selections,
                new TestAdapter());
        selections.mSelections = 0;

        mAdapterAnimator.smoothScrollToPosition(SECOND_CELL);
        assertThat(selections.mSelections).isEqualTo(0);
        runToRest();

        assertThat(mLayoutManager.getSelectedPosition()).isEqualTo(SECOND_CELL);
    }

    @Test
    public void smoothScrollToPosition_withoutSnapToPosition_stillLandsOnTheSnapPoint() {
        layOutList(false, SnapPosition.center, false, VIEWPORT_PAGING);

        mAdapterAnimator.smoothScrollToPosition(SECOND_CELL);
        runToRest();

        assertThat(mLayoutManager.getViewForPosition(SECOND_CELL).getLeft())
                .isEqualTo(CENTRED_CELL_START);
    }

    @Test
    public void smoothScrollToPosition_asAViewPager_crossesMoreThanOnePage() {
        layOutList(true, SnapPosition.start, true, ONE_CELL_PER_GESTURE);
        layout();

        mAdapterAnimator.smoothScrollToPosition(FOURTH_CELL);
        runToRest();

        assertThat(mLayoutManager.getViewForPosition(FOURTH_CELL).getLeft())
                .isEqualTo(START_OF_THE_VIEW);
    }

    @Test
    public void
            smoothScrollToPosition_scrollWithinContent_toACellHeldShortOfItsSnapPoint_restsAtTheBound() {
        layOutList(
                true,
                SnapPosition.start,
                false,
                VIEWPORT_PAGING,
                ADAPTER_SIZE,
                NO_SELECT_ON_SNAP,
                SCROLL_WITHIN_CONTENT,
                NOT_CIRCULAR,
                null,
                new TestAdapter());

        mAdapterAnimator.smoothScrollToPosition(LAST_CELL);
        runToRest();

        assertThat(mLayoutManager.getViewForPosition(LAST_CELL).getRight())
                .isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void smoothScrollToPosition_circular_takesTheShorterWayRound() {
        layOutList(
                true,
                SnapPosition.center,
                false,
                VIEWPORT_PAGING,
                ADAPTER_SIZE,
                NO_SELECT_ON_SNAP,
                SCROLL_PAST_CONTENT,
                CIRCULAR,
                null,
                new TestAdapter());

        mAdapterAnimator.smoothScrollToPosition(LAST_CELL);
        runToRest();

        final int lastCellStart = mLayoutManager.getViewForPosition(LAST_CELL).getLeft();
        final int firstCellStart = mLayoutManager.getViewForPosition(FIRST_CELL).getLeft();
        assertThat(lastCellStart).isGreaterThanOrEqualTo(START_OF_THE_VIEW);
        assertThat(lastCellStart).isLessThan(THE_END_ALIGNED_CELL_START);
        assertThat(firstCellStart).isEqualTo(lastCellStart + VIEW_SIZE);
    }

    @Test
    public void getAnimation_whileSeeking_carriesTheSeekDisplacement() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);
        ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));

        assertThat(nextFrameDisplacement()).isLessThan(0);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.seekingTo);
    }

    @Test
    public void getAnimation_beforeTheSeekOffsetIsComputed_appliesNothingAndKeepsSeeking() {
        layOutCenterSnappingList();
        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        final Animation animation = mAdapterAnimator.getAnimation();

        assertThat(animation.getDisplacement()).isEqualTo(0);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.seekingTo);
    }

    @Test
    public void smoothScrollToPosition_isIgnoredWhileTheFingerIsDown() {
        layOutCenterSnappingList();
        mAdapterAnimator.onDown(down());
        mAdapterAnimator.onScroll(down(), moveTo(A_DRAG), A_DRAG, 0f);
        layout();
        mFrameScheduler.mRequests = 0;

        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.scrolling);
        assertThat(mFrameScheduler.mRequests).isEqualTo(0);
        assertThat(nextFrameDisplacement()).isEqualTo(0);
    }

    @Test
    public void isAGesture_forTheStatesAFingerStarts_isTrue() {
        assertThat(AdapterAnimator.State.scrolling.isAGesture()).isTrue();
        assertThat(AdapterAnimator.State.flinging.isAGesture()).isTrue();
        assertThat(AdapterAnimator.State.snapingTo.isAGesture()).isTrue();
    }

    @Test
    public void isAGesture_forTheProgrammaticStates_isFalse() {
        assertThat(AdapterAnimator.State.animatingTo.isAGesture()).isFalse();
        assertThat(AdapterAnimator.State.jumpingTo.isAGesture()).isFalse();
        assertThat(AdapterAnimator.State.seekingTo.isAGesture()).isFalse();
    }

    @Test
    public void anAnimation_startedByAGesture_isAGesture() {
        mAnimation.newAnimation(A_GESTURE);

        assertThat(mAnimation.isAGesture()).isTrue();
    }

    @Test
    public void anAnimation_startedProgrammatically_isNotAGesture() {
        mAnimation.newAnimation(NOT_A_GESTURE);

        assertThat(mAnimation.isAGesture()).isFalse();
    }

    @Test
    public void anAnimation_startedWithoutSaying_isAGesture() {
        mAnimation.newAnimation();

        assertThat(mAnimation.isAGesture()).isTrue();
    }

    @Test
    public void aSmoothScroll_startsAnAnimationThatIsNotAGesture() {
        layOutCenterSnappingList();

        mAdapterAnimator.smoothScrollToPosition(SEVENTH_CELL);

        assertThat(mAdapterAnimator.getAnimation().isAGesture()).isFalse();
    }

    @Test
    public void aFling_startsAnAnimationThatIsAGesture() {
        layOutCenterSnappingList();

        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);

        assertThat(mAdapterAnimator.getAnimation().isAGesture()).isTrue();
    }

    private ScrollState scrollState() {
        return ScrollState.from(mAdapterAnimator.getState());
    }

    private int nextFrameDisplacement() {
        mAdapterAnimator.computeScrollOffset();
        return mAdapterAnimator.getAnimation().getDisplacement();
    }

    private static MotionEvent down() {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0);
    }

    private static MotionEvent moveTo(final float x) {
        return MotionEvent.obtain(0, 10, MotionEvent.ACTION_MOVE, x, 0f, 0);
    }

    private static MotionEvent up() {
        return MotionEvent.obtain(0, 50, MotionEvent.ACTION_UP, 200f, 0f, 0);
    }

    private static final class RecordingFrameScheduler implements AnimationFrameScheduler {
        private int mRequests;

        @Override
        public void requestAnimationFrame() {
            mRequests++;
        }
    }

    public static final class MyViewGroup extends FrameLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();

        public MyViewGroup(final Context context) {
            super(context);
        }

        @Override
        public boolean addViewInAdapterView(
                final View view, final int index, final ViewGroup.LayoutParams layoutParams) {
            mViews.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(final View view) {
            mViews.remove(view);
        }
    }

    public static final class TestAdapter extends BaseAdapter {
        private int mAdapterSize;

        public void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAdapterSize;
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
            final FrameLayout view = new FrameLayout(ApplicationProvider.getApplicationContext());
            view.setTag(position);
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(VIEW_SIZE, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }

    private static final class RecordingSelectedListener implements OnSelectedListener {
        private int mSelections;

        @Override
        public void onSelected(final View view) {
            mSelections++;
        }
    }

    /**
     * A bridge whose layout refuses every frame, standing in for a clamp the animator cannot see.
     */
    private static final class RefusingBridge extends LayoutManagerBridge {
        private RefusingBridge(final LayoutManager<?> layoutManager) {
            super(layoutManager);
        }

        @Override
        public int getFrameDisplacement() {
            return 0;
        }
    }

    /**
     * Three cells of the usual size and then cells so large that an estimate from the usual size,
     * even with its overshoot, runs short of the target.
     */
    public static final class FarTailAdapter extends BaseAdapter {
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
            final FrameLayout view = new FrameLayout(ApplicationProvider.getApplicationContext());
            view.setTag(position);
            final int size = sizeOf(position);
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        private static int sizeOf(final int position) {
            final boolean isInTheTail = position >= FIRST_FAR_CELL;
            if (isInTheTail) return FAR_CELL_SIZE;
            return VIEW_SIZE;
        }
    }
}
