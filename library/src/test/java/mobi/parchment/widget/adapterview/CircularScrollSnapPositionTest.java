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

/**
 * Circular scrolling swaps the snap strategy for onScreen, so parchment_snapPosition does not
 * decide where a cell comes to rest. It still decides whether the settle snap runs at all, because
 * getSnapDistanceToNearestView reads the declared position rather than the strategy, so the
 * attribute is not redundant on a circular view.
 */
@RunWith(RobolectricTestRunner.class)
public class CircularScrollSnapPositionTest {

    private static final int VIEWPORT_SIZE = 300;
    private static final int CELL_SIZE = VIEWPORT_SIZE;
    private static final int ADAPTER_SIZE = 10;
    private static final int DRAG_OFF_THE_BOUNDARY = -50;
    private static final int NO_CELL_SPACING = 0;
    private static final boolean CIRCULAR = true;
    private static final boolean SNAP_TO_POSITION = true;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final int VIEWPORT_PAGING = 0;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final boolean HORIZONTAL = false;

    private final MyViewGroup mViewGroup =
            new MyViewGroup(ApplicationProvider.getApplicationContext());
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final CellAdapter mAdapter = new CellAdapter();
    private final Animation mAnimation = new Animation();
    private ListLayoutManager mListLayoutManager;

    @Test
    public void circularScroll_withADeclaredSnapPosition_stillSettlesAStraddlingCell() {
        setup(SnapPosition.start);

        dragBy(DRAG_OFF_THE_BOUNDARY);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(-DRAG_OFF_THE_BOUNDARY);
    }

    @Test
    public void circularScroll_withNoDeclaredSnapPosition_leavesAStraddlingCellWhereItIs() {
        setup(SnapPosition.onScreen);

        dragBy(DRAG_OFF_THE_BOUNDARY);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(0);
    }

    private void setup(final SnapPosition snapPosition) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        CIRCULAR,
                        SNAP_TO_POSITION,
                        NOT_VIEW_PAGER,
                        VIEWPORT_PAGING,
                        snapPosition,
                        NO_CELL_SPACING,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL);
        mListLayoutManager =
                new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mAdapterViewManager.setAdapter(mAdapter);

        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEWPORT_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, VIEWPORT_SIZE, VIEWPORT_SIZE);

        mAnimation.newAnimation();
        layout();
    }

    private void layout() {
        mListLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEWPORT_SIZE, VIEWPORT_SIZE);
    }

    private void dragBy(final int displacement) {
        mAnimation.setDisplacement(displacement);
        layout();
    }

    private static final class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        private final List<View> mViews = new ArrayList<View>();

        private MyViewGroup(final Context context) {
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

    private static final class CellAdapter extends BaseAdapter {
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
            view.setLayoutParams(new ViewGroup.LayoutParams(CELL_SIZE, CELL_SIZE));
            return view;
        }
    }
}
