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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GridLayoutManagerCircularScrollTest {

    public static final int VIEW_GROUP_SIZE = 300;
    public static final int VIEW_SIZE = 100;
    public static final int CELL_SPACING = 10;
    public static final int NUMBER_OF_COLUMNS = 2;
    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    GridLayoutManagerAttributes attributes;
    GridLayoutManager listLayoutManager;

    @Before
    public void setup() {
        attributes =
                new GridLayoutManagerAttributes(
                        NUMBER_OF_COLUMNS,
                        true,
                        true,
                        false,
                        0,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        true,
                        true,
                        true,
                        true,
                        false,
                        false,
                        false);
        listLayoutManager = new GridLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout(VIEW_GROUP_SIZE);
    }

    @Test
    public void scrollUpAboveCellSpacing() {
        mTestAdapter.setAdapterSize(100);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        int displacement = -10;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(-10);
        assertThat(firstView.getBottom()).isEqualTo(90);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(-10);
        assertThat(secondView.getBottom()).isEqualTo(90);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        listLayoutManager.layout(mViewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void doFirstLayout(int viewGroupSize) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, viewGroupSize, viewGroupSize);
    }

    public class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();

        public MyViewGroup(Context context) {
            super(context);
        }

        public View forPosition(int position) {
            Collections.sort(
                    mViews,
                    new Comparator<View>() {
                        @Override
                        public int compare(View lhs, View rhs) {
                            return lhs.getLeft() - rhs.getLeft();
                        }
                    });

            return mViews.get(position);
        }

        @Override
        public boolean addViewInAdapterView(
                View view, int index, ViewGroup.LayoutParams layoutParams) {
            mViews.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(View view) {
            mViews.remove(view);
        }
    }

    public class TestAdapter extends BaseAdapter {
        private int mAdapterSize;
        private int mViewSize;

        public TestAdapter(int viewSize) {
            mViewSize = viewSize;
        }

        public void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAdapterSize;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FrameLayout outer = new FrameLayout(ApplicationProvider.getApplicationContext());
            outer.setTag(position);
            outer.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));

            // TODO: necessary to have an outer and an inner?
            final FrameLayout inner = new FrameLayout(ApplicationProvider.getApplicationContext());
            inner.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));
            outer.addView(inner);
            return outer;
        }
    }
}
