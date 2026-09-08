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
public class ListLayoutManagerSnapPaddingTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int START_PADDING = 10;
    private static final int END_PADDING = 30;
    private static final int VIEW_SIZE = 100;
    private static final int CELL_SPACING = 0;
    private static final int ADAPTER_SIZE = 10;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean SNAP_TO_POSITION = true;
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
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        NOT_CIRCULAR,
                        SNAP_TO_POSITION,
                        NOT_VIEW_PAGER,
                        0,
                        snapPosition,
                        CELL_SPACING,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL);
        mViewGroup.setPadding(START_PADDING, 0, END_PADDING, 0);
        mListLayoutManager =
                new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mAdapterViewManager.setAdapter(mTestAdapter);
        mTestAdapter.setAdapterSize(ADAPTER_SIZE);

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

    private View view(final int position) {
        return mListLayoutManager.getViewForPosition(position);
    }

    @Test
    public void firstLayout_centerSnapWithPadding_centersTheFirstCellInsideThePadding() {
        setup(SnapPosition.center);

        assertThat(view(0).getLeft()).isEqualTo(90);
    }

    @Test
    public void snapTo_centerSnapWithPadding_targetsTheCellNearestThePaddedCenter() {
        setup(SnapPosition.center);
        scrollBy(-45);
        assertThat(view(0).getLeft()).isEqualTo(45);
        assertThat(view(1).getLeft()).isEqualTo(145);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(45);
    }

    @Test
    public void snapTo_endSnapWithPadding_targetsThePaddedEnd() {
        setup(SnapPosition.end);
        assertThat(view(0).getRight()).isEqualTo(270);
        scrollBy(-30);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(30);
    }

    @Test
    public void onSingleTapUp_withPadding_snapsTheTappedViewToThePaddedCenter() {
        setup(SnapPosition.center);
        scrollBy(-45);
        final LayoutManagerBridge bridge = new LayoutManagerBridge(mListLayoutManager);

        assertThat(bridge.onSingleTapUp(mViewGroup, view(1))).isEqualTo(-55);
    }

    @Test
    public void setSelected_withPadding_replacesTheCellNearestThePaddedSnapPosition() {
        setup(SnapPosition.center);
        scrollBy(-45);

        mListLayoutManager.setSelected(5, mViewGroup);
        scrollBy(0);

        assertThat(view(5).getLeft()).isEqualTo(45);
    }

    @Test
    public void dataSetChange_withPadding_keepsTheCellNearestThePaddedSnapPositionInPlace() {
        setup(SnapPosition.center);
        scrollBy(-45);

        mTestAdapter.setAdapterSize(ADAPTER_SIZE);
        scrollBy(0);

        assertThat(view(0).getLeft()).isEqualTo(45);
        assertThat(view(1).getLeft()).isEqualTo(145);
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
