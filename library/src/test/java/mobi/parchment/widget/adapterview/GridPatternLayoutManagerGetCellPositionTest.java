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
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GridPatternLayoutManagerGetCellPositionTest {

    public static final int VIEW_GROUP_SIZE = 300;
    public static final int VIEW_SIZE = 100;
    public static final int CELL_SPACING = 10;
    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    GridPatternLayoutManagerAttributes attributes;
    GridPatternLayoutManager gridPatternLayoutManager;

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
        gridPatternLayoutManager =
                new GridPatternLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout(VIEW_GROUP_SIZE);

        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            gridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 2));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            gridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(1, 1, 1, 1));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            gridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
    }

    @Test
    public void getCellPosition() {
        mTestAdapter.setAdapterSize(14);

        int cellPosition = gridPatternLayoutManager.getCellPosition(10);
        assertThat(cellPosition).isEqualTo(5);

        cellPosition = gridPatternLayoutManager.getCellPosition(11);
        assertThat(cellPosition).isEqualTo(5);

        cellPosition = gridPatternLayoutManager.getCellPosition(4);
        assertThat(cellPosition).isEqualTo(2);

        cellPosition = gridPatternLayoutManager.getCellPosition(3);
        assertThat(cellPosition).isEqualTo(2);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        gridPatternLayoutManager.layout(
                mViewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
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
