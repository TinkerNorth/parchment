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
import mobi.parchment.widget.adapterview.divideraxis.BreadthDividerAxis;
import mobi.parchment.widget.adapterview.divideraxis.DividerAxisInterface;
import mobi.parchment.widget.adapterview.divideraxis.SizeDividerAxis;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
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
    private static final int ONE_DRAWN_GROUP = 1;
    private static final int TWO_DRAWN_GROUPS = 2;
    private static final float SQUARE_RATIO = 1f;
    private static final boolean IS_VERTICAL = true;
    private static final boolean IS_HORIZONTAL = false;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean NOT_SNAP_TO_POSITION = false;
    private static final boolean NOT_A_VIEW_PAGER = false;
    private static final int VIEWPORT_VIEW_PAGER_INTERVAL = 0;
    private static final boolean NOT_SELECT_ON_SNAP = false;
    private static final boolean NOT_SELECT_WHILE_SCROLLING = false;
    private static final boolean GRAVITY_TOP = true;
    private static final boolean GRAVITY_BOTTOM = false;
    private static final boolean GRAVITY_RIGHT = false;
    private static final boolean GRAVITY_CENTRE = false;
    private static final int TALL_VIEW_SIZE = 100;
    private static final int SHORT_VIEW_SIZE = 60;
    private static final boolean CIRCULAR = true;
    private static final int NO_CELL_SPACING = 0;
    private static final int THICK_DIVIDER_SIZE = 20;
    private static final int TWO_ITEMS = 2;
    private static final int ONE_ITEM = 1;
    private static final int NO_ITEMS = 0;
    private static final int START_BREADTH_PADDING = 20;
    private static final int START_SIZE_PADDING = 30;
    private static final int END_BREADTH_PADDING = 40;
    private static final int END_SIZE_PADDING = 50;
    private static final int A_FORWARD_SCROLL = -150;
    private static final int SIX_ITEMS = 6;
    private static final int FIRST_CELL = 0;
    private static final int FIRST_VIEW = 0;

    private final Canvas mCanvas = new Canvas();

    @Test
    public void gridDivider_inAVerticalGrid_separatesTheItemsWithinARowAsWellAsTheRows() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_VERTICAL, GRAVITY_TOP);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(SIX_DRAWN_VIEWS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(THREE_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(7);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 103, 145, 107));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(148, 0, 152, 100));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(155, 103, 300, 107));
        assertThat(divider.getDrawnBounds(3)).isEqualTo(new Rect(0, 213, 145, 217));
        assertThat(divider.getDrawnBounds(4)).isEqualTo(new Rect(148, 110, 152, 210));
        assertThat(divider.getDrawnBounds(5)).isEqualTo(new Rect(155, 213, 300, 217));
        assertThat(divider.getDrawnBounds(6)).isEqualTo(new Rect(148, 220, 152, 320));
    }

    @Test
    public void gridDivider_inAHorizontalGrid_separatesTheItemsWithinAColumnAsWellAsTheColumns() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_HORIZONTAL, GRAVITY_TOP);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(SIX_DRAWN_VIEWS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(THREE_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(7);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(103, 0, 107, 145));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 148, 100, 152));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(103, 155, 107, 300));
        assertThat(divider.getDrawnBounds(3)).isEqualTo(new Rect(213, 0, 217, 145));
        assertThat(divider.getDrawnBounds(4)).isEqualTo(new Rect(110, 148, 210, 152));
        assertThat(divider.getDrawnBounds(5)).isEqualTo(new Rect(213, 155, 217, 300));
        assertThat(divider.getDrawnBounds(6)).isEqualTo(new Rect(220, 148, 320, 152));
    }

    @Test
    public void gridDivider_atTheOuterBoundaryOfAVerticalGrid_isNotDrawn() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_VERTICAL, GRAVITY_TOP);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        final ScrollDirectionManager scrollDirectionManager =
                layoutManager.getScrollDirectionManager();
        final DividerAxisInterface sizeAxis = new SizeDividerAxis(scrollDirectionManager);
        final DividerAxisInterface breadthAxis = new BreadthDividerAxis(scrollDirectionManager);
        final int contentStart = contentStart(viewGroup, sizeAxis);
        final int contentEnd = contentEnd(viewGroup, sizeAxis);
        final int contentBreadthStart = contentStart(viewGroup, breadthAxis);
        final int contentBreadthEnd = contentEnd(viewGroup, breadthAxis);
        for (int index = 0; index < divider.getDrawCount(); index++) {
            final Rect bounds = divider.getDrawnBounds(index);
            final boolean isASizeDivider = bounds.height() == DIVIDER_SIZE;
            if (isASizeDivider) {
                assertThat(bounds.top).isGreaterThan(contentStart);
                assertThat(bounds.bottom).isLessThan(contentEnd);
            } else {
                assertThat(bounds.left).isGreaterThan(contentBreadthStart);
                assertThat(bounds.right).isLessThan(contentBreadthEnd);
            }
        }
    }

    @Test
    public void gridDivider_onAnEdgeSharedByTwoItems_isDrawnOnlyOnce() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_VERTICAL, GRAVITY_TOP);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        final List<Rect> drawn = new ArrayList<Rect>();
        for (int index = 0; index < divider.getDrawCount(); index++) {
            drawn.add(divider.getDrawnBounds(index));
        }
        assertThat(drawn).doesNotHaveDuplicates();
    }

    private static int contentStart(final MyViewGroup viewGroup, final DividerAxisInterface axis) {
        int start = Integer.MAX_VALUE;
        for (final View view : viewGroup.mViews) {
            final int viewStart = axis.getStart(view);
            if (viewStart < start) start = viewStart;
        }
        return start;
    }

    private static int contentEnd(final MyViewGroup viewGroup, final DividerAxisInterface axis) {
        int end = Integer.MIN_VALUE;
        for (final View view : viewGroup.mViews) {
            final int viewEnd = axis.getEnd(view);
            if (viewEnd > end) end = viewEnd;
        }
        return end;
    }

    @Test
    public void gridPatternDivider_inAVerticalViewWithATeeJunction_dividesEveryInternalEdgeOnce() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGridPatternTee(viewGroup, IS_VERTICAL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(THREE_ITEMS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(ONE_DRAWN_GROUP);
        assertThat(divider.getDrawCount()).isEqualTo(3);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(148, 0, 152, 145));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(148, 155, 152, 300));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(155, 148, 300, 152));
    }

    @Test
    public void
            gridPatternDivider_inAHorizontalViewWithATeeJunction_dividesEveryInternalEdgeOnce() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGridPatternTee(viewGroup, IS_HORIZONTAL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(viewGroup.mViews).hasSize(THREE_ITEMS);
        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(ONE_DRAWN_GROUP);
        assertThat(divider.getDrawCount()).isEqualTo(3);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 148, 145, 152));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(155, 148, 300, 152));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(148, 155, 152, 300));
    }

    @Test
    public void gridDivider_withAShortViewPlacedByGravity_followsTheViewRatherThanTheCell() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutUnevenGrid(viewGroup);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 103, 145, 107));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(155, 83, 300, 87));
    }

    @Test
    public void gridDivider_withNoCellSpacing_straddlesTheSharedEdgeOnBothAxes() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, NO_CELL_SPACING, EIGHT_ITEMS);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 98, 150, 102));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(148, 0, 152, 100));
    }

    @Test
    public void gridDivider_thickerThanTheGap_overlapsTheItemsOnBothAxes() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_VERTICAL, GRAVITY_TOP);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, layoutManager, THICK_DIVIDER_SIZE);

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 95, 145, 115));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(140, 0, 160, 100));
    }

    @Test
    public void gridDivider_withASingleRow_dividesTheRowButNothingElse() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, CELL_SPACING, TWO_ITEMS);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(ONE_DRAWN_GROUP);
        assertThat(divider.getDrawCount()).isEqualTo(1);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(148, 100, 152, 200));
    }

    @Test
    public void gridDivider_withASingleItem_drawsNothing() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, CELL_SPACING, ONE_ITEM);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(ONE_DRAWN_GROUP);
        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void gridDivider_withAnEmptyAdapter_drawsNothing() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, CELL_SPACING, NO_ITEMS);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(NO_ITEMS);
        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void gridDivider_withCircularScroll_dividesEveryGapBetweenDrawnRows() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutCircularGrid(viewGroup);
        scroll(viewGroup, layoutManager, A_FORWARD_SCROLL);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        final int drawnCellCount = layoutManager.getDrawnCellCount();
        final int gapsBetweenDrawnRows = drawnCellCount - ONE_DRAWN_GROUP;
        final int lastCellIndex = drawnCellCount - ONE_DRAWN_GROUP;
        final View firstDrawnView = layoutManager.getDrawnCellView(FIRST_CELL, FIRST_VIEW);
        final View lastDrawnView = layoutManager.getDrawnCellView(lastCellIndex, FIRST_VIEW);
        assertThat(layoutManager.getPosition(lastDrawnView))
                .isLessThan(layoutManager.getPosition(firstDrawnView));
        assertThat(countSizeDividers(divider)).isEqualTo(gapsBetweenDrawnRows * VIEWS_PER_CELL);
    }

    @Test
    public void gridDivider_withPaddingSet_followsTheItemsInsideThePadding() {
        final MyViewGroup viewGroup = newViewGroup();
        viewGroup.setPadding(
                START_BREADTH_PADDING, START_SIZE_PADDING, END_BREADTH_PADDING, END_SIZE_PADDING);
        final LayoutManager<?> layoutManager = layOutCentredGrid(viewGroup);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(30, 133, 145, 137));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(148, 30, 152, 130));
    }

    @Test
    public void gridPatternDivider_whereAWideItemMeetsTwoNarrowOnes_dividesEachOverlapSeparately() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGridPatternPair(viewGroup);
        final RecordingDrawable divider = new RecordingDrawable();

        draw(divider, viewGroup, layoutManager);

        assertThat(layoutManager.getDrawnCellCount()).isEqualTo(TWO_DRAWN_GROUPS);
        assertThat(divider.getDrawCount()).isEqualTo(3);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 148, 145, 152));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(148, 0, 152, 145));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(155, 148, 300, 152));
    }

    @Test
    public void everyGridDividerOfEveryFrame_isMeasuredIntoTheSameBoundsRect() {
        final MyViewGroup viewGroup = newViewGroup();
        final LayoutManager<?> layoutManager = layOutGrid(viewGroup, IS_VERTICAL, GRAVITY_TOP);
        final RecordingDrawable divider = new RecordingDrawable();
        final CellDivider cellDivider =
                new CellDivider(divider, DIVIDER_SIZE, layoutManager.getScrollDirectionManager());

        cellDivider.draw(mCanvas, layoutManager);
        cellDivider.draw(mCanvas, layoutManager);

        final List<Rect> boundsInstances = divider.getBoundsInstances();
        assertThat(boundsInstances).hasSize(14);
        for (final Rect bounds : boundsInstances) {
            assertThat(bounds).isSameAs(boundsInstances.get(0));
        }
    }

    private void draw(
            final RecordingDrawable divider,
            final MyViewGroup viewGroup,
            final LayoutManager<?> layoutManager) {
        draw(divider, layoutManager, DIVIDER_SIZE);
    }

    private void draw(
            final RecordingDrawable divider,
            final LayoutManager<?> layoutManager,
            final int dividerSize) {
        final ScrollDirectionManager scrollDirectionManager =
                layoutManager.getScrollDirectionManager();
        final CellDivider cellDivider =
                new CellDivider(divider, dividerSize, scrollDirectionManager);
        cellDivider.draw(mCanvas, layoutManager);
    }

    private static LayoutManager<?> layOutGrid(
            final MyViewGroup viewGroup, final boolean isVertical, final boolean isTop) {
        return newGridLayoutManager(
                viewGroup,
                isVertical,
                isTop,
                new TestAdapter(isVertical),
                CELL_SPACING,
                NOT_CIRCULAR,
                EIGHT_ITEMS);
    }

    private static LayoutManager<?> layOutGrid(
            final MyViewGroup viewGroup, final int cellSpacing, final int adapterSize) {
        return newGridLayoutManager(
                viewGroup,
                IS_VERTICAL,
                GRAVITY_TOP,
                new TestAdapter(IS_VERTICAL),
                cellSpacing,
                NOT_CIRCULAR,
                adapterSize);
    }

    private static LayoutManager<?> layOutCentredGrid(final MyViewGroup viewGroup) {
        return newGridLayoutManager(
                viewGroup,
                IS_VERTICAL,
                GRAVITY_CENTRE,
                new TestAdapter(IS_VERTICAL),
                CELL_SPACING,
                NOT_CIRCULAR,
                EIGHT_ITEMS);
    }

    private static LayoutManager<?> layOutCircularGrid(final MyViewGroup viewGroup) {
        return newGridLayoutManager(
                viewGroup,
                IS_VERTICAL,
                GRAVITY_TOP,
                new TestAdapter(IS_VERTICAL),
                CELL_SPACING,
                CIRCULAR,
                SIX_ITEMS);
    }

    private static LayoutManager<?> layOutUnevenGrid(final MyViewGroup viewGroup) {
        return newGridLayoutManager(
                viewGroup,
                IS_VERTICAL,
                GRAVITY_TOP,
                new UnevenAdapter(IS_VERTICAL),
                CELL_SPACING,
                NOT_CIRCULAR,
                EIGHT_ITEMS);
    }

    private static GridLayoutManager newGridLayoutManager(
            final MyViewGroup viewGroup,
            final boolean isVertical,
            final boolean isTop,
            final TestAdapter testAdapter,
            final int cellSpacing,
            final boolean isCircular,
            final int adapterSize) {
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridLayoutManagerAttributes attributes =
                new GridLayoutManagerAttributes(
                        VIEWS_PER_CELL,
                        isCircular,
                        NOT_SNAP_TO_POSITION,
                        NOT_A_VIEW_PAGER,
                        VIEWPORT_VIEW_PAGER_INTERVAL,
                        SnapPosition.onScreen,
                        cellSpacing,
                        NOT_SELECT_ON_SNAP,
                        NOT_SELECT_WHILE_SCROLLING,
                        isVertical,
                        isTop,
                        GRAVITY_BOTTOM,
                        isTop,
                        GRAVITY_RIGHT);
        final GridLayoutManager layoutManager =
                new GridLayoutManager(viewGroup, null, adapterViewManager, attributes);
        adapterViewManager.setAdapter(testAdapter);
        layOut(viewGroup);
        layoutManager.measure(viewGroup, measureSpec(), measureSpec());
        testAdapter.setAdapterSize(adapterSize);
        layoutManager.layout(viewGroup, newAnimation(), 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        return layoutManager;
    }

    private static void scroll(
            final MyViewGroup viewGroup,
            final LayoutManager<?> layoutManager,
            final int displacement) {
        final Animation animation = new Animation();
        animation.newAnimation();
        animation.setDisplacement(displacement);
        layoutManager.layout(viewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private static int countSizeDividers(final RecordingDrawable divider) {
        int count = 0;
        for (int index = 0; index < divider.getDrawCount(); index++) {
            final Rect bounds = divider.getDrawnBounds(index);
            final boolean isASizeDivider = bounds.height() == DIVIDER_SIZE;
            if (isASizeDivider) count++;
        }
        return count;
    }

    private static LayoutManager<?> layOutGridPatternTee(
            final MyViewGroup viewGroup, final boolean isVertical) {
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridPatternLayoutManagerAttributes attributes =
                new GridPatternLayoutManagerAttributes(
                        NOT_CIRCULAR,
                        NOT_SNAP_TO_POSITION,
                        NOT_A_VIEW_PAGER,
                        VIEWPORT_VIEW_PAGER_INTERVAL,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        NOT_SELECT_ON_SNAP,
                        NOT_SELECT_WHILE_SCROLLING,
                        isVertical,
                        SQUARE_RATIO);
        final GridPatternLayoutManager layoutManager =
                new GridPatternLayoutManager(viewGroup, null, adapterViewManager, attributes);
        layoutManager.addGridPatternGroupDefinition(newTeeGroupDefinition(isVertical));
        final TestAdapter testAdapter = new TestAdapter(GRID_PATTERN_VIEW_SIZE, isVertical);
        adapterViewManager.setAdapter(testAdapter);
        layOut(viewGroup);
        layoutManager.measure(viewGroup, measureSpec(), measureSpec());
        testAdapter.setAdapterSize(THREE_ITEMS);
        layoutManager.layout(viewGroup, newAnimation(), 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        return layoutManager;
    }

    private static LayoutManager<?> layOutGridPatternPair(final MyViewGroup viewGroup) {
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridPatternLayoutManagerAttributes attributes =
                new GridPatternLayoutManagerAttributes(
                        NOT_CIRCULAR,
                        NOT_SNAP_TO_POSITION,
                        NOT_A_VIEW_PAGER,
                        VIEWPORT_VIEW_PAGER_INTERVAL,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        NOT_SELECT_ON_SNAP,
                        NOT_SELECT_WHILE_SCROLLING,
                        IS_VERTICAL,
                        SQUARE_RATIO);
        final GridPatternLayoutManager layoutManager =
                new GridPatternLayoutManager(viewGroup, null, adapterViewManager, attributes);
        layoutManager.addGridPatternGroupDefinition(newSideBySideGroupDefinition());
        layoutManager.addGridPatternGroupDefinition(newWideGroupDefinition());
        final TestAdapter testAdapter = new TestAdapter(GRID_PATTERN_VIEW_SIZE, IS_VERTICAL);
        adapterViewManager.setAdapter(testAdapter);
        layOut(viewGroup);
        layoutManager.measure(viewGroup, measureSpec(), measureSpec());
        testAdapter.setAdapterSize(THREE_ITEMS);
        layoutManager.layout(viewGroup, newAnimation(), 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        return layoutManager;
    }

    private static GridPatternGroupDefinition newSideBySideGroupDefinition() {
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
        itemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
        return new GridPatternGroupDefinition(IS_VERTICAL, itemDefinitions);
    }

    private static GridPatternGroupDefinition newWideGroupDefinition() {
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 2));
        return new GridPatternGroupDefinition(IS_VERTICAL, itemDefinitions);
    }

    private static GridPatternGroupDefinition newTeeGroupDefinition(final boolean isVertical) {
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        if (isVertical) {
            itemDefinitions.add(new GridPatternItemDefinition(0, 0, 2, 1));
            itemDefinitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
            itemDefinitions.add(new GridPatternItemDefinition(1, 1, 1, 1));
        } else {
            itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 2));
            itemDefinitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
            itemDefinitions.add(new GridPatternItemDefinition(1, 1, 1, 1));
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

    private static int measureSpec() {
        return View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
    }

    private static void layOut(final MyViewGroup viewGroup) {
        viewGroup.measure(measureSpec(), measureSpec());
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

    private static class TestAdapter extends BaseAdapter {
        private final int mViewSize;
        private final boolean mIsVertical;
        private int mAdapterSize;

        private TestAdapter(final boolean isVertical) {
            this(GRID_VIEW_SIZE, isVertical);
        }

        private TestAdapter(final int viewSize, final boolean isVertical) {
            mViewSize = viewSize;
            mIsVertical = isVertical;
        }

        private void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
        }

        protected int getViewSize(final int position) {
            return mViewSize;
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
            final int fill = ViewGroup.LayoutParams.MATCH_PARENT;
            final int size = getViewSize(position);
            if (mIsVertical) {
                view.setLayoutParams(new ViewGroup.LayoutParams(fill, size));
            } else {
                view.setLayoutParams(new ViewGroup.LayoutParams(size, fill));
            }
            return view;
        }
    }

    private static final class UnevenAdapter extends TestAdapter {
        private UnevenAdapter(final boolean isVertical) {
            super(TALL_VIEW_SIZE, isVertical);
        }

        @Override
        protected int getViewSize(final int position) {
            final boolean isOdd = position % 2 == 1;
            if (isOdd) return SHORT_VIEW_SIZE;
            return TALL_VIEW_SIZE;
        }
    }
}
