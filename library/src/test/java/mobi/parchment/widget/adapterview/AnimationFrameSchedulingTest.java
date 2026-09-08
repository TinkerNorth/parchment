// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.time.Duration;
import mobi.parchment.test.R;
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

    private void fling() {
        mListView.mGestureListener.onDown(down());
        mListView.mGestureListener.onFling(down(), moveTo(100f), FLING_VELOCITY, 0f);
        mListView.mGestureListener.onUp();
        assertThat(mListView.mGestureListener.getState()).isEqualTo(AdapterAnimator.State.flinging);
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
                final boolean isVerticalScroll) {
            final AdapterViewInitializer<View> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll);
            mGestureListener = initializer.getChildTouchListener();
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
