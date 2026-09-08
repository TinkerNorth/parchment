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
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Created by Anthony Tarantini */
@RunWith(RobolectricTestRunner.class)
public class GridPatternLayoutManagerNoDefinitionTest {

    public static final int VIEW_GROUP_HEIGHT = 300;
    public static final int VIEW_GROUP_WIDTH = 145;
    public static final int VIEW_SIZE = 145;
    public static final int CELL_SPACING = 10;
    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    GridPatternLayoutManagerAttributes attributes;
    GridPatternLayoutManager listLayoutManager;

    @Before
    public void setup() {
        attributes =
                new GridPatternLayoutManagerAttributes(
                        false,
                        true,
                        false,
                        0,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        true,
                        true,
                        true,
                        1f);
        listLayoutManager =
                new GridPatternLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout();
    }

    @Test
    public void shouldDefaultToNormalListView() {
        mTestAdapter.setAdapterSize(3);
        doLayout();

        final View firstView = mViewGroup.mViews.get(0);
        final View secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getWidth()).isEqualTo(145);
        assertThat(firstView.getHeight()).isEqualTo(145);

        assertThat(secondView.getWidth()).isEqualTo(145);
        assertThat(secondView.getHeight()).isEqualTo(145);

        assertThat(mViewGroup.mViews.size()).isEqualTo(2);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        listLayoutManager.layout(
                mViewGroup, animation, false, 0, 0, VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);
    }

    private void doFirstLayout() {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_HEIGHT, View.MeasureSpec.EXACTLY);
        final int measureSpec2 =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_WIDTH, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec2, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);
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
