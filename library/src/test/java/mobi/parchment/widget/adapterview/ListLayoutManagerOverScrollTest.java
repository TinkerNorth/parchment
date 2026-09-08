// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ListLayoutManagerOverScrollTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int VIEW_SIZE = 100;
    private static final int CELL_SPACING = 0;
    private static final int ADAPTER_SIZE = 10;
    private static final int LAST_POSITION = ADAPTER_SIZE - 1;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean CIRCULAR = true;
    private static final boolean SNAP_TO_POSITION = true;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final boolean HORIZONTAL = false;

    private final MyViewGroup mViewGroup =
            new MyViewGroup(ApplicationProvider.getApplicationContext());
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final CountingAnimationStoppedListener mStoppedListener =
            new CountingAnimationStoppedListener();
    private final TestAdapter mTestAdapter = new TestAdapter();
    private final Animation mAnimation = new Animation();
    private ListLayoutManager mListLayoutManager;

    private void setup(final SnapPosition snapPosition, final boolean isCircularScroll) {
        setup(snapPosition, isCircularScroll, ADAPTER_SIZE, 0, 0);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean isCircularScroll,
            final int adapterSize,
            final int startPadding,
            final int endPadding) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        isCircularScroll,
                        SNAP_TO_POSITION,
                        NOT_VIEW_PAGER,
                        0,
                        snapPosition,
                        CELL_SPACING,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL);
        mViewGroup.setPadding(startPadding, 0, endPadding, 0);
        mListLayoutManager =
                new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mListLayoutManager.setAnimationStoppedListener(mStoppedListener);
        mAdapterViewManager.setAdapter(mTestAdapter);
        mTestAdapter.setAdapterSize(adapterSize);

        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);

        mAnimation.newAnimation();
        layout();
    }

    private void layout() {
        mListLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void scrollBy(final int displacement) {
        mAnimation.setDisplacement(displacement);
        layout();
    }

    private void scrollInFrames(final int frames, final int displacementPerFrame) {
        for (int frame = 0; frame < frames; frame++) {
            scrollBy(displacementPerFrame);
        }
    }

    private View view(final int position) {
        return mListLayoutManager.getViewForPosition(position);
    }

    @Test
    public void centerSnap_scrollingAwayFromTheStart_doesNotStopTheAnimation() {
        setup(SnapPosition.center, NOT_CIRCULAR);
        assertThat(view(0).getLeft()).isEqualTo(100);

        scrollBy(-50);

        assertThat(mStoppedListener.mCount).isEqualTo(0);
        assertThat(view(0).getLeft()).isEqualTo(50);
    }

    @Test
    public void centerSnap_lastCellBeyondTheCenter_doesNotStopTheAnimation() {
        setup(SnapPosition.center, NOT_CIRCULAR);

        scrollInFrames(17, -50);

        assertThat(mStoppedListener.mCount).isEqualTo(0);
        assertThat(view(LAST_POSITION).getLeft()).isEqualTo(150);
    }

    @Test
    public void centerSnap_lastCellPulledPastTheCenter_isHeldAtTheCenter() {
        setup(SnapPosition.center, NOT_CIRCULAR);
        scrollInFrames(17, -50);

        scrollBy(-120);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(LAST_POSITION).getLeft()).isEqualTo(100);
    }

    @Test
    public void centerSnap_firstCellPulledPastTheCenter_isHeldAtTheCenter() {
        setup(SnapPosition.center, NOT_CIRCULAR);

        scrollBy(70);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(0).getLeft()).isEqualTo(100);
    }

    @Test
    public void endSnap_lastCellBeyondTheEnd_doesNotStopTheAnimation() {
        setup(SnapPosition.end, NOT_CIRCULAR);
        assertThat(view(0).getRight()).isEqualTo(300);

        scrollInFrames(16, -50);

        assertThat(mStoppedListener.mCount).isEqualTo(0);
        assertThat(view(LAST_POSITION).getRight()).isEqualTo(400);
    }

    @Test
    public void endSnap_lastCellPulledPastTheEnd_isHeldAtTheEnd() {
        setup(SnapPosition.end, NOT_CIRCULAR);
        scrollInFrames(16, -50);

        scrollBy(-200);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(LAST_POSITION).getRight()).isEqualTo(300);
    }

    @Test
    public void onScreen_frameOvershootingTheEnd_isCorrectedInTheSameFrame() {
        setup(SnapPosition.onScreen, NOT_CIRCULAR);
        scrollBy(-500);
        assertThat(view(LAST_POSITION)).isNull();

        scrollBy(-350);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(LAST_POSITION).getRight()).isEqualTo(300);
        assertThat(view(LAST_POSITION - 2).getLeft()).isEqualTo(0);
    }

    @Test
    public void onScreen_frameOvershootingTheStart_isCorrectedInTheSameFrame() {
        setup(SnapPosition.onScreen, NOT_CIRCULAR);
        scrollBy(-250);
        assertThat(view(0)).isNull();

        scrollBy(400);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(0).getLeft()).isEqualTo(0);
        assertThat(view(2).getRight()).isEqualTo(300);
    }

    @Test
    public void onScreen_withPadding_overshootIsCorrectedToThePaddedEnd() {
        setup(SnapPosition.onScreen, NOT_CIRCULAR, ADAPTER_SIZE, 10, 30);
        scrollBy(-500);

        scrollBy(-350);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(LAST_POSITION).getRight()).isEqualTo(270);
    }

    @Test
    public void startSnap_frameOvershootingTheEnd_isCorrectedInTheSameFrame() {
        setup(SnapPosition.start, NOT_CIRCULAR);
        scrollBy(-500);

        scrollBy(-500);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(LAST_POSITION).getLeft()).isEqualTo(0);
    }

    @Test
    public void overScrollCorrection_keepsFillingCellsWhenScrollingBack() {
        setup(SnapPosition.end, NOT_CIRCULAR);
        scrollInFrames(16, -50);
        scrollBy(-200);
        assertThat(view(LAST_POSITION).getRight()).isEqualTo(300);

        scrollBy(100);

        assertThat(view(LAST_POSITION).getRight()).isEqualTo(400);
        assertThat(view(LAST_POSITION - 3).getLeft()).isEqualTo(0);
        assertThat(mListLayoutManager.getScrollBarOffset()).isEqualTo(600f);
    }

    @Test
    public void onScreen_contentThatFits_isCenteredOnFirstLayout() {
        setup(SnapPosition.onScreen, NOT_CIRCULAR, 2, 0, 0);

        assertThat(view(0).getLeft()).isEqualTo(50);
        assertThat(view(1).getRight()).isEqualTo(250);
    }

    @Test
    public void startSnap_contentThatFits_canScrollUntilTheLastCellIsAtTheStart() {
        setup(SnapPosition.start, NOT_CIRCULAR, 2, 10, 0);

        scrollBy(-40);
        assertThat(mStoppedListener.mCount).isEqualTo(0);
        assertThat(view(0).getLeft()).isEqualTo(-30);

        scrollBy(-100);
        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(1).getLeft()).isEqualTo(10);
    }

    @Test
    public void circularScroll_isNeverCorrected() {
        setup(SnapPosition.start, CIRCULAR);

        scrollBy(-500);
        scrollBy(-500);

        assertThat(mStoppedListener.mCount).isEqualTo(0);
        assertThat(view(0).getLeft()).isEqualTo(0);
    }

    @Test
    public void scrollingEveryCellOffTheStart_resetsToTheLastCell() {
        setup(SnapPosition.onScreen, NOT_CIRCULAR);

        scrollBy(-5000);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(LAST_POSITION).getRight()).isEqualTo(300);
    }

    @Test
    public void scrollingEveryCellOffTheEnd_resetsToTheFirstCell() {
        setup(SnapPosition.onScreen, NOT_CIRCULAR);

        scrollBy(5000);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(view(0).getLeft()).isEqualTo(0);
    }

    private static final class CountingAnimationStoppedListener
            implements AnimationStoppedListener {
        private int mCount;

        @Override
        public void onAnimationStopped() {
            mCount++;
        }
    }

    public static final class MyViewGroup extends LinearLayout implements AdapterViewHandler {
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
