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

/** Created by Emir Hasanbegovic */
@RunWith(RobolectricTestRunner.class)
public class GridPatternLayoutManagerOverScrollTest {

    public static final int VIEW_GROUP_WIDTH = 230;
    public static final int VIEW_GROUP_HEIGHT = 100;
    public static final int VIEW_SIZE = 110;
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
        doFirstLayout(VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);

        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            listLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 2));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            listLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
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
            listLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
    }

    @Test
    public void scrollItemOffScreen() {
        mTestAdapter.setAdapterSize(8);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(2);

        View firstView = mViewGroup.mViews.get(0);
        assertThat(firstView.getTop()).isEqualTo(0);

        int displacement = -1300;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(1);

        firstView = mViewGroup.mViews.get(0);

        assertThat(firstView.getTop()).isEqualTo(-10);
        assertThat(firstView.getBottom()).isEqualTo(100);

        displacement = -50;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        firstView = mViewGroup.mViews.get(0);

        assertThat(firstView.getTop()).isEqualTo(-10);
        assertThat(firstView.getBottom()).isEqualTo(100);
    }

    @Test
    public void scrollOffScreen() {
        mTestAdapter.setAdapterSize(5);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        int displacement = -1300;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        displacement = -10;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        final View lastView = mViewGroup.mViews.get(mViewGroup.mViews.size() - 1);

        assertThat(mViewGroup.mViews.size()).isEqualTo(2);
        assertThat(lastView.getBottom()).isEqualTo(100);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        listLayoutManager.layout(
                mViewGroup, animation, false, 0, 0, VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);
    }

    private void doFirstLayout(int viewGroupWidth, int viewGroupHeight) {
        final int widthMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(viewGroupWidth, View.MeasureSpec.EXACTLY);
        final int heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(viewGroupHeight, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(widthMeasureSpec, heightMeasureSpec);
        mViewGroup.layout(0, 0, viewGroupWidth, viewGroupHeight);
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
