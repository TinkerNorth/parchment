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
public class GridPatternOverScrollTest {

    public static final int VIEW_GROUP_WIDTH = 1080;
    public static final int VIEW_GROUP_HEIGHT = 1557;
    public static final int CELL_SPACING = 12;
    public static final boolean IS_CIRCULAR_SCROLL = false;
    public static final boolean IS_VERTICAL_SCROLL = true;
    public static final float RATIO = 0.5f;
    public static final SnapPosition SNAP_POSITION = SnapPosition.onScreen;
    public static final boolean SNAP_TO_POSITION = false;
    public static final boolean IS_VIEW_PAGER = false;
    public static final boolean SELECT_ON_SNAP = false;
    public static final boolean SELECT_WHILE_SCROLLING = false;
    public static final int ADAPTER_SIZE = 7;

    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    GridPatternLayoutManagerAttributes attributes;
    GridPatternLayoutManager gridPatternLayoutManager;

    @Before
    public void setup() {
        attributes =
                new GridPatternLayoutManagerAttributes(
                        IS_CIRCULAR_SCROLL,
                        SNAP_TO_POSITION,
                        IS_VIEW_PAGER,
                        0,
                        SNAP_POSITION,
                        CELL_SPACING,
                        SELECT_ON_SNAP,
                        SELECT_WHILE_SCROLLING,
                        IS_VERTICAL_SCROLL,
                        RATIO);
        gridPatternLayoutManager =
                new GridPatternLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter();
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout(VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);

        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 2, 2));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 2, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(1, 2, 1, 1));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            gridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 1, 2, 2));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            gridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
        {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 3, 3));
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    new GridPatternGroupDefinition(true, gridPatternItemDefinitions);
            gridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
        }
    }

    @Test
    public void initialLayoutIsCentered() {
        mTestAdapter.setAdapterSize(ADAPTER_SIZE);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(ADAPTER_SIZE);
        final View lastView = mViewGroup.mViews.get(mViewGroup.mViews.size() - 1);
        final View firstView = mViewGroup.mViews.get(0);

        assertThat(firstView.getTop()).isIn(126, 127);
        assertThat(lastView.getBottom()).isIn(1430, 1431);
    }

    @Test
    public void smallScrollDownAfterInitialLayout() {
        mTestAdapter.setAdapterSize(ADAPTER_SIZE);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        animation.newAnimation();
        animation.setDisplacement(100);
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(ADAPTER_SIZE);
        final View lastView = mViewGroup.mViews.get(mViewGroup.mViews.size() - 1);
        final View firstView = mViewGroup.mViews.get(0);

        assertThat(firstView.getTop()).isIn(126, 127);
        assertThat(lastView.getBottom()).isIn(1430, 1431);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        gridPatternLayoutManager.layout(
                mViewGroup, animation, 0, 0, VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);
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
            FrameLayout frameLayout = new FrameLayout(ApplicationProvider.getApplicationContext());
            frameLayout.setTag(position);
            frameLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            return frameLayout;
        }
    }
}
