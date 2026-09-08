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
public class ListLayoutManagerFlingSnapTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int VIEW_SIZE = 100;
    private static final int ADAPTER_SIZE = 10;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean CIRCULAR = true;
    private static final boolean SNAP_TO_POSITION = true;
    private static final boolean NO_SNAP_TO_POSITION = false;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final boolean HORIZONTAL = false;

    private final MyViewGroup mViewGroup =
            new MyViewGroup(ApplicationProvider.getApplicationContext());
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final TestAdapter mTestAdapter = new TestAdapter();
    private final Animation mAnimation = new Animation();
    private ListLayoutManager mListLayoutManager;

    private void setup(final SnapPosition snapPosition) {
        setup(snapPosition, SNAP_TO_POSITION, NOT_CIRCULAR, 0, 0, 0, ADAPTER_SIZE);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean snapToPosition,
            final boolean isCircularScroll,
            final int cellSpacing,
            final int startPadding,
            final int endPadding,
            final int adapterSize) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        isCircularScroll,
                        snapToPosition,
                        NOT_VIEW_PAGER,
                        0,
                        snapPosition,
                        cellSpacing,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL);
        mViewGroup.setPadding(startPadding, 0, endPadding, 0);
        mListLayoutManager =
                new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
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

    private int adjustmentFor(final int flingDisplacement) {
        return mListLayoutManager.getFlingSnapAdjustment(mViewGroup, flingDisplacement);
    }

    @Test
    public void centerSnap_flingEndingBetweenVisibleCells_endsOnTheNearerOne() {
        setup(SnapPosition.center);
        assertThat(mListLayoutManager.getViewForPosition(0).getLeft()).isEqualTo(100);

        assertThat(adjustmentFor(-230)).isEqualTo(30);
        assertThat(adjustmentFor(-160)).isEqualTo(-40);
    }

    @Test
    public void centerSnap_flingEndingOnAVisibleCell_needsNoAdjustment() {
        setup(SnapPosition.center);

        assertThat(adjustmentFor(-100)).isEqualTo(0);
    }

    @Test
    public void centerSnap_flingEndingBeyondTheVisibleCells_extrapolatesWithTheLastCellSize() {
        setup(SnapPosition.center);

        assertThat(adjustmentFor(-730)).isEqualTo(30);
    }

    @Test
    public void centerSnap_flingEndingPastTheLastCell_endsOnTheLastCell() {
        setup(SnapPosition.center);

        assertThat(adjustmentFor(-2000)).isEqualTo(1100);
    }

    @Test
    public void centerSnap_flingBackBeyondTheVisibleCells_extrapolatesWithTheFirstCellSize() {
        setup(SnapPosition.center);
        scrollBy(-500);
        assertThat(mListLayoutManager.getViewForPosition(5).getLeft()).isEqualTo(100);

        assertThat(adjustmentFor(330)).isEqualTo(-30);
    }

    @Test
    public void centerSnap_flingBackPastTheFirstCell_endsOnTheFirstCell() {
        setup(SnapPosition.center);
        scrollBy(-500);

        assertThat(adjustmentFor(2000)).isEqualTo(-1500);
    }

    @Test
    public void cellSpacing_isPartOfTheExtrapolationStep() {
        setup(SnapPosition.center, SNAP_TO_POSITION, NOT_CIRCULAR, 10, 0, 0, ADAPTER_SIZE);

        assertThat(adjustmentFor(-560)).isEqualTo(10);
    }

    @Test
    public void padding_movesTheSnapPositionInsideThePadding() {
        setup(SnapPosition.center, SNAP_TO_POSITION, NOT_CIRCULAR, 0, 10, 30, ADAPTER_SIZE);
        assertThat(mListLayoutManager.getViewForPosition(0).getLeft()).isEqualTo(90);

        assertThat(adjustmentFor(-230)).isEqualTo(30);
    }

    @Test
    public void startSnap_flingEndsOnACellStart() {
        setup(SnapPosition.start);
        assertThat(mListLayoutManager.getViewForPosition(0).getLeft()).isEqualTo(0);

        assertThat(adjustmentFor(-140)).isEqualTo(40);
        assertThat(adjustmentFor(-660)).isEqualTo(-40);
    }

    @Test
    public void endSnap_flingEndsOnACellEnd() {
        setup(SnapPosition.end);
        assertThat(mListLayoutManager.getViewForPosition(0).getRight()).isEqualTo(300);

        assertThat(adjustmentFor(-140)).isEqualTo(40);
    }

    @Test
    public void onScreenSnap_neverAdjustsAFling() {
        setup(SnapPosition.onScreen);

        assertThat(adjustmentFor(-230)).isEqualTo(0);
    }

    @Test
    public void withoutSnapToPosition_neverAdjustsAFling() {
        setup(SnapPosition.center, NO_SNAP_TO_POSITION, NOT_CIRCULAR, 0, 0, 0, ADAPTER_SIZE);

        assertThat(adjustmentFor(-230)).isEqualTo(0);
    }

    @Test
    public void circularScroll_extrapolatesWithoutAnEnd() {
        setup(SnapPosition.center, SNAP_TO_POSITION, CIRCULAR, 0, 0, 0, ADAPTER_SIZE);

        assertThat(adjustmentFor(-2030)).isEqualTo(30);
        assertThat(adjustmentFor(2030)).isEqualTo(-30);
    }

    @Test
    public void emptyAdapter_neverAdjustsAFling() {
        setup(SnapPosition.center, SNAP_TO_POSITION, NOT_CIRCULAR, 0, 0, 0, 0);

        assertThat(adjustmentFor(-230)).isEqualTo(0);
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
