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
    private static final long FLING_DURATION = 555;
    private static final long MAX_SNAP_DURATION = 500;

    private final Context mContext = ApplicationProvider.getApplicationContext();
    private final RecordingFrameScheduler mFrameScheduler = new RecordingFrameScheduler();
    private final MyViewGroup mViewGroup = new MyViewGroup(mContext);
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final Animation mAnimation = new Animation();
    private ListLayoutManager mLayoutManager;
    private AdapterAnimator mAdapterAnimator;

    @Before
    public void setup() {
        setup(false, SnapPosition.onScreen);
    }

    private void setup(final boolean snapToPosition, final SnapPosition snapPosition) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        false, snapToPosition, false, 0, snapPosition, 0, false, false, false);
        mLayoutManager = new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mAdapterAnimator =
                new AdapterAnimator(
                        mViewGroup,
                        mFrameScheduler,
                        false,
                        false,
                        new LayoutManagerBridge(mLayoutManager),
                        ViewConfiguration.get(mContext));
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        assertThat(mViewGroup.isLayoutRequested()).isFalse();
    }

    private void layOutCenterSnappingList() {
        setup(true, SnapPosition.center);
        final TestAdapter adapter = new TestAdapter();
        mAdapterViewManager.setAdapter(adapter);
        adapter.setAdapterSize(ADAPTER_SIZE);
        layout();
    }

    private void layout() {
        mAdapterAnimator.computeScrollOffset();
        final Animation animation = mAdapterAnimator.getAnimation();
        mLayoutManager.layout(mViewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
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
    public void aFlingThatEndsThisFrame_handsOffToItsSnapInTheSameFrame() {
        layOutCenterSnappingList();
        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(FLING_DURATION + 1));
        mFrameScheduler.mRequests = 0;

        layout();
        assertThat(mLayoutManager.getViewForPosition(0).getLeft()).isEqualTo(100 + FLING_DISTANCE);
        mAdapterAnimator.onFrameLaidOut();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
        assertThat(mFrameScheduler.mRequests).isEqualTo(1);

        ShadowSystemClock.advanceBy(Duration.ofMillis(16));
        assertThat(nextFrameDisplacement()).isNotEqualTo(0);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.snapingTo);
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
        mAdapterAnimator.onFling(down(), up(), FLING_VELOCITY, 0f);
        mAdapterAnimator.onUp();
        ShadowSystemClock.advanceBy(Duration.ofMillis(FLING_DURATION + 1));
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
}
