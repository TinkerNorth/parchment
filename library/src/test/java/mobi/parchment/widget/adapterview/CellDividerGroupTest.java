// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CellDividerGroupTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int GRID_VIEW_SIZE = 100;
    private static final int GRID_PATTERN_VIEW_SIZE = 145;
    private static final int CELL_SPACING = 10;
    private static final int DIVIDER_SIZE = 4;
    private static final int VIEWS_PER_CELL = 2;
    private static final int EIGHT_ITEMS = 8;
    private static final int THREE_ITEMS = 3;
    private static final int SIX_DRAWN_VIEWS = 6;
    private static final int THREE_DRAWN_GROUPS = 3;
    private static final int TWO_DRAWN_GROUPS = 2;
    private static final int NO_PIXEL = 0;
    private static final float SQUARE_RATIO = 1f;
    private static final boolean IS_VERTICAL = true;
    private static final boolean IS_HORIZONTAL = false;

    private final Canvas mCanvas = new Canvas();

    @Test
    public void gridDivider_inAVerticalGrid_separatesRowsRatherThanTheItemsWithinARow() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_VERTICAL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(SIX_DRAWN_VIEWS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(THREE_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(2);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 103, VIEW_GROUP_SIZE, 107));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 213, VIEW_GROUP_SIZE, 217));
    }

    @Test
    public void gridDivider_inAHorizontalGrid_separatesColumnsRatherThanTheItemsWithinAColumn() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_HORIZONTAL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(SIX_DRAWN_VIEWS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(THREE_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(2);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(103, 0, 107, VIEW_GROUP_SIZE));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(213, 0, 217, VIEW_GROUP_SIZE));
    }

    @Test
    public void gridPatternDivider_inAVerticalView_separatesGroupsNotTheItemsWithinAGroup() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGridPattern(viewGroup, IS_VERTICAL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(THREE_ITEMS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(TWO_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(1);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 148, VIEW_GROUP_SIZE, 152));
    }

    @Test
    public void gridPatternDivider_inAHorizontalView_separatesGroupsNotTheItemsWithinAGroup() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGridPattern(viewGroup, IS_HORIZONTAL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(THREE_ITEMS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(TWO_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(1);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(148, 0, 152, VIEW_GROUP_SIZE));
    }

    @Test
    public void anEmptyGroup_reportsNoPixelForEveryBound() {
        final Group group = new Group(IS_VERTICAL);

        assertThat(group.getTop()).isEqualTo(NO_PIXEL);
        assertThat(group.getBottom()).isEqualTo(NO_PIXEL);
        assertThat(group.getLeft()).isEqualTo(NO_PIXEL);
        assertThat(group.getRight()).isEqualTo(NO_PIXEL);
    }

    private void draw(
            final RecordingDrawable divider,
            final MyViewGroup viewGroup,
            final LayoutManager<?> layoutManager) {
        final ScrollDirectionManager scrollDirectionManager =
                layoutManager.getScrollDirectionManager();
        final CellDivider cellDivider =
                new CellDivider(divider, DIVIDER_SIZE, scrollDirectionManager);
        cellDivider.draw(mCanvas, viewGroup, layoutManager);
    }

    private static LayoutManager<?> layOutGrid(
            final MyViewGroup viewGroup, final boolean isVertical) {
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridLayoutManagerAttributes attributes =
                new GridLayoutManagerAttributes(
                        VIEWS_PER_CELL,
                        false,
                        false,
                        false,
                        1,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        false,
                        false,
                        isVertical,
                        true,
                        false,
                        false,
                        false);
        final GridLayoutManager layoutManager =
                new GridLayoutManager(viewGroup, null, adapterViewManager, attributes);
        final TestAdapter testAdapter = new TestAdapter(GRID_VIEW_SIZE);
        adapterViewManager.setAdapter(testAdapter);
        layOut(viewGroup);
        testAdapter.setAdapterSize(EIGHT_ITEMS);
        layoutManager.layout(viewGroup, newAnimation(), 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        return layoutManager;
    }

    private static LayoutManager<?> layOutGridPattern(
            final MyViewGroup viewGroup, final boolean isVertical) {
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridPatternLayoutManagerAttributes attributes =
                new GridPatternLayoutManagerAttributes(
                        false,
                        false,
                        false,
                        1,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        false,
                        false,
                        isVertical,
                        SQUARE_RATIO);
        final GridPatternLayoutManager layoutManager =
                new GridPatternLayoutManager(viewGroup, null, adapterViewManager, attributes);
        layoutManager.addGridPatternGroupDefinition(newPairGroupDefinition(isVertical));
        layoutManager.addGridPatternGroupDefinition(newLongGroupDefinition(isVertical));
        final TestAdapter testAdapter = new TestAdapter(GRID_PATTERN_VIEW_SIZE);
        adapterViewManager.setAdapter(testAdapter);
        layOut(viewGroup);
        testAdapter.setAdapterSize(THREE_ITEMS);
        layoutManager.layout(viewGroup, newAnimation(), 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        return layoutManager;
    }

    private static GridPatternGroupDefinition newPairGroupDefinition(final boolean isVertical) {
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
        if (isVertical) {
            itemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
        } else {
            itemDefinitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
        }
        return new GridPatternGroupDefinition(isVertical, itemDefinitions);
    }

    private static GridPatternGroupDefinition newLongGroupDefinition(final boolean isVertical) {
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        if (isVertical) {
            itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 2));
        } else {
            itemDefinitions.add(new GridPatternItemDefinition(0, 0, 2, 1));
        }
        return new GridPatternGroupDefinition(isVertical, itemDefinitions);
    }

    private static MyViewGroup newViewGroup() {
        return new MyViewGroup(ApplicationProvider.getApplicationContext());
    }

    private static Animation newAnimation() {
        final Animation animation = new Animation();
        animation.newAnimation();
        return animation;
    }

    private static void layOut(final MyViewGroup viewGroup) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        viewGroup.measure(measureSpec, measureSpec);
        viewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
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

    private static final class TestAdapter extends BaseAdapter {
        private final int mViewSize;
        private int mAdapterSize;

        private TestAdapter(final int viewSize) {
            mViewSize = viewSize;
        }

        private void setAdapterSize(final int adapterSize) {
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
            final FrameLayout view = new FrameLayout(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));
            return view;
        }
    }
}
