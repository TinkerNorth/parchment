// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.view.AbsSavedState;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.Group;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * The breadth a layout manager measures for its view across the scroll axis: the height of a
 * horizontal view and the width of a vertical one. The harness view group is measured EXACTLY at
 * its size first, which stands in for the spec size {@code AbstractAdapterView} gives itself before
 * it asks, and it runs whatever the manager posts to it at once, counting the layouts asked for.
 */
@RunWith(RobolectricTestRunner.class)
public class MeasuredBreadthTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int WIDE_VIEW_GROUP_SIZE = 800;
    private static final int CELL_SIZE = 100;
    private static final int CELL_SPACING = 10;
    private static final int PADDING = 30;
    private static final int BREADTH_PADDING = 2 * PADDING;
    private static final int NO_PADDING = 0;
    private static final int FIRST_CELL_BREADTH = 60;
    private static final int TALLEST_CELL_BREADTH = 100;
    private static final int THIRD_CELL_BREADTH = 80;
    private static final int CELL_BEYOND_THE_VIEWPORT_BREADTH = 90;
    private static final int LAST_CELL_BREADTH = 70;
    private static final int[] LIST_BREADTHS = {
        FIRST_CELL_BREADTH,
        TALLEST_CELL_BREADTH,
        THIRD_CELL_BREADTH,
        CELL_BEYOND_THE_VIEWPORT_BREADTH,
        LAST_CELL_BREADTH,
        LAST_CELL_BREADTH
    };
    private static final int[] TWO_CELLS = {FIRST_CELL_BREADTH, TALLEST_CELL_BREADTH};
    private static final int[] NO_CELLS_AT_ALL = {};
    private static final int VIEWS_PER_CELL = 2;
    private static final int TALLEST_GROUP_FIRST_VIEW_BREADTH = 100;
    private static final int TALLEST_GROUP_SECOND_VIEW_BREADTH = 80;
    private static final int[] GRID_BREADTHS = {
        60, 40, TALLEST_GROUP_FIRST_VIEW_BREADTH, TALLEST_GROUP_SECOND_VIEW_BREADTH, 50, 20, 30, 120
    };
    private static final int TALLEST_GROUP_BREADTH =
            TALLEST_GROUP_FIRST_VIEW_BREADTH + CELL_SPACING + TALLEST_GROUP_SECOND_VIEW_BREADTH;
    private static final int EVERY_VIEW_IN_THE_VIEWPORT = 6;
    private static final List<Integer> THE_LIST_CELLS_IN_THE_VIEWPORT = Arrays.asList(0, 1, 2);
    private static final List<Integer> BOTH_CELLS_ONCE = Arrays.asList(0, 1);
    private static final List<Integer> THE_GRID_VIEWS_IN_THE_VIEWPORT =
            Arrays.asList(0, 1, 2, 3, 4, 5);
    private static final int THE_VIEWS_ONE_VIEWPORT_NEEDS = 3;
    private static final int THE_VIEWS_ONE_GRID_VIEWPORT_NEEDS = 6;
    private static final int ONE_RECYCLED_VIEW = 1;
    private static final int ONE_RECYCLED_GROUP = VIEWS_PER_CELL;
    private static final int A_SPEC_SMALLER_THAN_THE_TALLEST_CELL = 70;
    private static final int UNSPECIFIED_SIZE = 0;
    private static final int A_SPEC_LARGER_THAN_THE_VIEW_GROUP = 500;
    private static final int NO_CELLS = 0;
    private static final int NO_BREADTH = 0;
    private static final int NO_VIEWS = 0;
    private static final int RESTORED_OFFSET = 0;
    private static final int RESTORED_START_CELL = 2;
    private static final int A_START_CELL_PAST_THE_ADAPTER = 10;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean NOT_SNAP_TO_POSITION = false;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final int VIEWPORT_PAGING = 0;
    private static final boolean SCROLL_PAST_CONTENT = false;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final boolean GRAVITY_TOP = true;
    private static final boolean NO_GRAVITY = false;
    private static final float SQUARE = 1f;
    private static final int PATTERN_ITEM = 1;
    private static final int PATTERN_COLUMNS = 2;
    private static final int FIRST_VIEW = 0;
    private static final int THIRD_VIEW = 2;
    private static final int FOURTH_VIEW = 3;
    private static final int FIRST_POSITION = 0;
    private static final int GRID_PATTERN_ADAPTER_SIZE = 4;
    private static final int EMPTY_ADAPTER = 0;
    private static final int SHORT_CELL = 50;
    private static final int TALL_CELL = 90;
    private static final int[] A_TALL_SIXTH_CELL = {
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        TALL_CELL,
        SHORT_CELL,
        SHORT_CELL
    };
    private static final int[] A_TALL_THIRD_CELL = {
        SHORT_CELL, SHORT_CELL, TALL_CELL, SHORT_CELL, SHORT_CELL
    };
    private static final int[] A_TALL_FIRST_CELL = {TALL_CELL, 60, 70, SHORT_CELL, 40};
    private static final int A_TALL_FIRST_CELL_VIEWPORT_ESTIMATE = 70;
    private static final int[] A_TALL_FOURTH_CELL = {SHORT_CELL, SHORT_CELL, SHORT_CELL, TALL_CELL};
    private static final int[] EQUAL_CELLS = {
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL,
        SHORT_CELL
    };
    private static final int A_SPEC_TALLER_THAN_EVERY_CELL = 200;
    private static final int A_SPEC_SHORTER_THAN_THE_TALL_CELL = 70;
    private static final int BACK_FILLED_OFFSET = -90;
    private static final int PART_WAY_INTO_THE_SECOND_CELL = -10;
    private static final int SECOND_CELL = 1;
    private static final int THIRD_CELL = 2;
    private static final int LAST_OF_FIVE_CELLS = 4;
    private static final int ONE_CELL_BACK = -(CELL_SIZE + CELL_SPACING);
    private static final int A_SMALL_SCROLL = -30;
    private static final int MANY_FRAMES = 10;
    private static final int NO_LAYOUT_REQUESTED = 0;
    private static final int ONE_LAYOUT_REQUESTED = 1;
    private static final boolean RUNS_POSTS_AT_ONCE = true;
    private static final boolean HOLDS_POSTS = false;

    @Test
    public void listHorizontal_atMostHeight_withCellsLaidOut_wrapsToTheTallestCellPlusPadding() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listVertical_atMostWidth_withCellsLaidOut_wrapsToTheWidestCellPlusPadding() {
        final ListHarness harness = new ListHarness(VERTICAL, PADDING, LIST_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_atMostHeightSmallerThanTheTallestCell_isCappedAtTheSpec() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.layout();

        final int breadth =
                harness.measureBreadth(
                        View.MeasureSpec.AT_MOST, A_SPEC_SMALLER_THAN_THE_TALLEST_CELL);

        assertThat(breadth).isEqualTo(A_SPEC_SMALLER_THAN_THE_TALLEST_CELL);
    }

    @Test
    public void listHorizontal_unspecifiedHeight_wrapsToTheTallestCellWithoutACap() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.UNSPECIFIED, UNSPECIFIED_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_exactHeight_isTheSpecWhateverTheCellsMeasure() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.EXACTLY, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void
            listHorizontal_atMostHeight_beforeAnyLayout_wrapsToTheTallestCellThatFillsTheViewport() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void
            listVertical_atMostWidth_beforeAnyLayout_wrapsToTheWidestCellThatFillsTheViewport() {
        final ListHarness harness = new ListHarness(VERTICAL, PADDING, LIST_BREADTHS);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_measuredBeforeAnyLayout_doesNotObtainACellBeyondTheViewport() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);

        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mAdapter.getRequestedPositions())
                .containsExactlyElementsOf(THE_LIST_CELLS_IN_THE_VIEWPORT);
    }

    @Test
    public void
            listHorizontal_measuredBeforeAnyLayout_adapterShorterThanTheViewport_measuresEveryCellOnce() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, TWO_CELLS);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mAdapter.getRequestedPositions())
                .containsExactlyElementsOf(BOTH_CELLS_ONCE);
        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_circularScroll_measuredBeforeAnyLayout_walksEveryCellOnceAndStops() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, TWO_CELLS, CIRCULAR);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mAdapter.getRequestedPositions())
                .containsExactlyElementsOf(BOTH_CELLS_ONCE);
        assertThat(breadth).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_measuredWithAStartCellPastTheAdapter_measuresFromTheLastCell() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.restore(RESTORED_OFFSET, A_START_CELL_PAST_THE_ADAPTER);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(LAST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_measuredBeforeAnyLayout_leavesNoViewInTheViewGroup() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);

        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mViewGroup.mViews).hasSize(NO_VIEWS);
        assertThat(harness.mLayoutManager.getDrawnCellCount()).isEqualTo(NO_CELLS);
    }

    @Test
    public void listHorizontal_measuredBeforeAnyLayout_measuresEveryCellThroughOneRecycledView() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);

        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mAdapter.getCreatedViews()).hasSize(ONE_RECYCLED_VIEW);
    }

    @Test
    public void listHorizontal_measuredBeforeAnyLayout_theLayoutThatFollowsReusesTheMeasuredView() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);
        final View measuredView = harness.mAdapter.getCreatedViews().get(FIRST_VIEW);

        harness.layout();

        assertThat(harness.mViewGroup.mViews).hasSize(THE_VIEWS_ONE_VIEWPORT_NEEDS);
        assertThat(harness.mViewGroup.mViews).contains(measuredView);
        assertThat(harness.mAdapter.getCreatedViews()).hasSize(THE_VIEWS_ONE_VIEWPORT_NEEDS);
    }

    @Test
    public void listHorizontal_atMostHeight_withAnEmptyAdapter_wrapsToThePaddingAlone() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, NO_CELLS_AT_ALL);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(BREADTH_PADDING);
    }

    @Test
    public void listHorizontal_atMostHeight_withAnEmptyAdapterAndNoPadding_wrapsToNothing() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, NO_CELLS_AT_ALL);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(NO_BREADTH);
    }

    @Test
    public void
            listHorizontal_measuredAfterRestoringState_measuresFromTheRestoredCellAndLaysItOutFirst() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);
        harness.restore(RESTORED_OFFSET, RESTORED_START_CELL);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);
        harness.layout();

        assertThat(breadth).isEqualTo(CELL_BEYOND_THE_VIEWPORT_BREADTH + BREADTH_PADDING);
        assertThat(harness.mViewGroup.mViews.get(FIRST_VIEW).getTag())
                .isEqualTo(RESTORED_START_CELL);
    }

    @Test
    public void listHorizontal_childHeightSpec_followsTheModeOfTheHeightSpec() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);

        harness.measure(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        final int childHeightMeasureSpec =
                harness.mLayoutManager.getChildHeightMeasureSpec(FIRST_POSITION);
        assertThat(View.MeasureSpec.getMode(childHeightMeasureSpec))
                .isEqualTo(View.MeasureSpec.AT_MOST);
    }

    @Test
    public void
            listHorizontal_withAnAtMostWidthAndAnExactHeight_matchParentChildrenGetTheExactHeight() {
        final ListHarness harness = new ListHarness(HORIZONTAL, PADDING, LIST_BREADTHS);

        harness.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.EXACTLY, VIEW_GROUP_SIZE);

        final int childHeightMeasureSpec =
                harness.mLayoutManager.getChildHeightMeasureSpec(FIRST_POSITION);
        assertThat(View.MeasureSpec.getMode(childHeightMeasureSpec))
                .isEqualTo(View.MeasureSpec.EXACTLY);
    }

    @Test
    public void listVertical_childWidthSpec_followsTheModeOfTheWidthSpec() {
        final ListHarness harness = new ListHarness(VERTICAL, PADDING, LIST_BREADTHS);

        harness.measure(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        final int childWidthMeasureSpec =
                harness.mLayoutManager.getChildWidthMeasureSpec(FIRST_POSITION);
        assertThat(View.MeasureSpec.getMode(childWidthMeasureSpec))
                .isEqualTo(View.MeasureSpec.AT_MOST);
    }

    @Test
    public void
            listHorizontal_restoredWithANegativeOffset_aLargerCellTheLayoutBackFills_asksForOneLayoutAndThenWrapsToIt() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, A_TALL_SIXTH_CELL);
        harness.restore(BACK_FILLED_OFFSET, THIRD_CELL);
        final int estimate =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);

        harness.layout();
        final int breadth =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();

        assertThat(estimate).isEqualTo(SHORT_CELL);
        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(ONE_LAYOUT_REQUESTED);
        assertThat(breadth).isEqualTo(TALL_CELL);
    }

    @Test
    public void
            listHorizontal_restoredAtTheLastCell_aLargerCellTheOverScrollCorrectionPullsIn_asksForOneLayoutAndThenWrapsToIt() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, A_TALL_THIRD_CELL);
        harness.restore(RESTORED_OFFSET, LAST_OF_FIVE_CELLS);
        final int estimate =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);

        harness.layout();
        final int breadth =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();

        assertThat(estimate).isEqualTo(SHORT_CELL);
        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(ONE_LAYOUT_REQUESTED);
        assertThat(breadth).isEqualTo(TALL_CELL);
    }

    @Test
    public void
            listHorizontal_restoredIntoAWiderView_aLargerCellTheLayoutBackFills_asksForOneLayoutAndThenWrapsToIt() {
        final ListHarness harness =
                new ListHarness(HORIZONTAL, NO_PADDING, A_TALL_FIRST_CELL, WIDE_VIEW_GROUP_SIZE);
        harness.restore(PART_WAY_INTO_THE_SECOND_CELL, SECOND_CELL);
        final int estimate =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);

        harness.layout();
        final int breadth =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();

        assertThat(estimate).isEqualTo(A_TALL_FIRST_CELL_VIEWPORT_ESTIMATE);
        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(ONE_LAYOUT_REQUESTED);
        assertThat(breadth).isEqualTo(TALL_CELL);
    }

    @Test
    public void
            listHorizontal_aScrollThatRevealsALargerCell_asksForOneLayoutAndTheNextMeasureWrapsToIt() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, A_TALL_FOURTH_CELL);
        final int estimate =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();
        final int requestsBeforeTheScroll = harness.mViewGroup.getLayoutRequests();

        harness.layout(ONE_CELL_BACK);
        final int breadth =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout(A_SMALL_SCROLL);

        assertThat(estimate).isEqualTo(SHORT_CELL);
        assertThat(requestsBeforeTheScroll).isEqualTo(NO_LAYOUT_REQUESTED);
        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(ONE_LAYOUT_REQUESTED);
        assertThat(breadth).isEqualTo(TALL_CELL);
    }

    @Test
    public void listHorizontal_scrollsThatRevealNothingLarger_askForNoLayout() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, EQUAL_CELLS);
        harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();

        for (int frame = 0; frame < MANY_FRAMES; frame++) {
            harness.layout(A_SMALL_SCROLL);
        }

        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(NO_LAYOUT_REQUESTED);
    }

    @Test
    public void listHorizontal_exactHeight_aScrollThatRevealsALargerCell_asksForNoLayout() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, A_TALL_FOURTH_CELL);
        harness.measureBreadth(View.MeasureSpec.EXACTLY, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();

        harness.layout(ONE_CELL_BACK);
        harness.layout(A_SMALL_SCROLL);

        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(NO_LAYOUT_REQUESTED);
    }

    @Test
    public void listHorizontal_atMostHeightSmallerThanTheRevealedCell_asksForOneLayoutAndNoMore() {
        final ListHarness harness = new ListHarness(HORIZONTAL, NO_PADDING, A_TALL_FOURTH_CELL);
        harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_SHORTER_THAN_THE_TALL_CELL);
        harness.layout();

        harness.layout(ONE_CELL_BACK);
        final int breadth =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_SHORTER_THAN_THE_TALL_CELL);
        for (int frame = 0; frame < MANY_FRAMES; frame++) {
            harness.layout(A_SMALL_SCROLL);
        }

        assertThat(breadth).isEqualTo(A_SPEC_SHORTER_THAN_THE_TALL_CELL);
        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(ONE_LAYOUT_REQUESTED);
    }

    @Test
    public void
            listHorizontal_aLayoutRequestPendingWhenTheManagerIsDestroyed_isDroppedAndNeverAsks() {
        final ListHarness harness =
                ListHarness.holdingPosts(HORIZONTAL, NO_PADDING, A_TALL_FOURTH_CELL);
        harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_TALLER_THAN_EVERY_CELL);
        harness.layout();
        harness.layout(ONE_CELL_BACK);
        final boolean aRequestIsPending = harness.mViewGroup.hasAPendingPost();

        harness.mLayoutManager.destroy();
        harness.mViewGroup.runThePendingPost();

        assertThat(aRequestIsPending).isTrue();
        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(NO_LAYOUT_REQUESTED);
    }

    @Test
    public void gridHorizontal_atMostHeight_withGroupsLaidOut_wrapsToTheTallestGroupPlusPadding() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_GROUP_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void gridHorizontal_theWrappedBreadth_isTheExtentLayoutCellGivesTheTallestGroup() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.layout();
        final View firstViewOfTheTallestGroup = harness.mViewGroup.mViews.get(THIRD_VIEW);
        final View lastViewOfTheTallestGroup = harness.mViewGroup.mViews.get(FOURTH_VIEW);
        final int groupTop = firstViewOfTheTallestGroup.getTop();
        final int groupBottom = lastViewOfTheTallestGroup.getBottom();
        final int groupExtent = groupBottom - groupTop;

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(groupExtent).isEqualTo(TALLEST_GROUP_BREADTH);
        assertThat(breadth).isEqualTo(groupExtent + BREADTH_PADDING);
    }

    @Test
    public void gridHorizontal_aShorterLastDrawnGroup_doesNotShrinkTheWrappedBreadth() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mViewGroup.mViews).hasSize(EVERY_VIEW_IN_THE_VIEWPORT);
        assertThat(breadth).isEqualTo(TALLEST_GROUP_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void gridVertical_atMostWidth_withGroupsLaidOut_wrapsToTheWidestGroupPlusPadding() {
        final GridHarness harness = new GridHarness(VERTICAL, PADDING, GRID_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_GROUP_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void gridHorizontal_atMostHeightSmallerThanTheTallestGroup_isCappedAtTheSpec() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.layout();

        final int breadth =
                harness.measureBreadth(
                        View.MeasureSpec.AT_MOST, A_SPEC_SMALLER_THAN_THE_TALLEST_CELL);

        assertThat(breadth).isEqualTo(A_SPEC_SMALLER_THAN_THE_TALLEST_CELL);
    }

    @Test
    public void gridHorizontal_unspecifiedHeight_wrapsToTheTallestGroupWithoutACap() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.UNSPECIFIED, UNSPECIFIED_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_GROUP_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void gridHorizontal_exactHeight_isTheSpecWhateverTheGroupsMeasure() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.EXACTLY, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void
            gridHorizontal_atMostHeight_beforeAnyLayout_wrapsToTheTallestGroupThatFillsTheViewport() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(TALLEST_GROUP_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void gridHorizontal_measuredBeforeAnyLayout_doesNotObtainAGroupBeyondTheViewport() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);

        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mAdapter.getRequestedPositions())
                .containsExactlyElementsOf(THE_GRID_VIEWS_IN_THE_VIEWPORT);
    }

    @Test
    public void gridHorizontal_measuredBeforeAnyLayout_measuresEveryGroupThroughOneRecycledGroup() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);

        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(harness.mViewGroup.mViews).hasSize(NO_VIEWS);
        assertThat(harness.mAdapter.getCreatedViews()).hasSize(ONE_RECYCLED_GROUP);
    }

    @Test
    public void gridHorizontal_measuredBeforeAnyLayout_theLayoutThatFollowsReusesTheGroupsViews() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);
        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);
        final List<View> measuredViews = harness.mAdapter.getCreatedViews();

        harness.layout();

        assertThat(harness.mViewGroup.mViews).hasSize(THE_VIEWS_ONE_GRID_VIEWPORT_NEEDS);
        assertThat(harness.mViewGroup.mViews).containsAll(measuredViews);
        assertThat(harness.mAdapter.getCreatedViews()).hasSize(THE_VIEWS_ONE_GRID_VIEWPORT_NEEDS);
    }

    @Test
    public void gridHorizontal_atMostHeight_withAnEmptyAdapter_wrapsToThePaddingAlone() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, NO_CELLS_AT_ALL);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(BREADTH_PADDING);
    }

    @Test
    public void
            gridHorizontal_withAnAtMostWidthAndAnExactHeight_childHeightSpecFollowsTheHeightSpec() {
        final GridHarness harness = new GridHarness(HORIZONTAL, PADDING, GRID_BREADTHS);

        harness.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.EXACTLY, VIEW_GROUP_SIZE);

        final int childHeightMeasureSpec =
                harness.mLayoutManager.getChildHeightMeasureSpec(FIRST_POSITION);
        assertThat(View.MeasureSpec.getMode(childHeightMeasureSpec))
                .isEqualTo(View.MeasureSpec.EXACTLY);
    }

    @Test
    public void gridPattern_atMostBreadth_withCellsLaidOut_fillsTheSpec() {
        final GridPatternHarness harness = new GridPatternHarness(PADDING);
        harness.layout();

        final int breadth =
                harness.measureBreadth(View.MeasureSpec.AT_MOST, A_SPEC_LARGER_THAN_THE_VIEW_GROUP);

        assertThat(breadth).isEqualTo(A_SPEC_LARGER_THAN_THE_VIEW_GROUP);
    }

    @Test
    public void gridPattern_atMostBreadth_withAnEmptyAdapter_stillFillsTheSpec() {
        final GridPatternHarness harness = new GridPatternHarness(PADDING);
        harness.mAdapter.setAdapterSize(EMPTY_ADAPTER);

        final int breadth = harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void gridPattern_atMostBreadth_theLayoutsThatFollow_askForNoLayout() {
        final GridPatternHarness harness = new GridPatternHarness(PADDING);
        harness.measureBreadth(View.MeasureSpec.AT_MOST, VIEW_GROUP_SIZE);

        harness.layout();
        harness.layout(A_SMALL_SCROLL);

        assertThat(harness.mViewGroup.getLayoutRequests()).isEqualTo(NO_LAYOUT_REQUESTED);
    }

    @Test
    public void gridPattern_unspecifiedBreadth_fillsTheSpecRatherThanWrapping() {
        final GridPatternHarness harness = new GridPatternHarness(PADDING);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.UNSPECIFIED, UNSPECIFIED_SIZE);

        assertThat(breadth).isEqualTo(UNSPECIFIED_SIZE);
    }

    @Test
    public void gridPattern_exactBreadth_isTheSpec() {
        final GridPatternHarness harness = new GridPatternHarness(PADDING);
        harness.layout();

        final int breadth = harness.measureBreadth(View.MeasureSpec.EXACTLY, VIEW_GROUP_SIZE);

        assertThat(breadth).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void
            gridPattern_aCellsBreadth_isTheViewsBreadthInsideThePadding_soThereIsNothingToWrapTo() {
        final GridPatternHarness harness = new GridPatternHarness(PADDING);
        final GridPatternGroup group = harness.aGroup();

        final int cellBreadth = harness.mLayoutManager.getCellBreadth(group);

        assertThat(cellBreadth).isEqualTo(VIEW_GROUP_SIZE - BREADTH_PADDING);
    }

    private static LayoutManagerAttributes listAttributes(
            final boolean isVertical, final boolean isCircular) {
        return new LayoutManagerAttributes(
                isCircular,
                NOT_SNAP_TO_POSITION,
                NOT_VIEW_PAGER,
                VIEWPORT_PAGING,
                SnapPosition.onScreen,
                SCROLL_PAST_CONTENT,
                CELL_SPACING,
                NO_SELECT_ON_SNAP,
                NO_SELECT_WHILE_SCROLLING,
                isVertical);
    }

    private static GridLayoutManagerAttributes gridAttributes(final boolean isVertical) {
        return new GridLayoutManagerAttributes(
                VIEWS_PER_CELL,
                NOT_CIRCULAR,
                NOT_SNAP_TO_POSITION,
                NOT_VIEW_PAGER,
                VIEWPORT_PAGING,
                SnapPosition.onScreen,
                SCROLL_PAST_CONTENT,
                CELL_SPACING,
                NO_SELECT_ON_SNAP,
                NO_SELECT_WHILE_SCROLLING,
                isVertical,
                GRAVITY_TOP,
                NO_GRAVITY,
                NO_GRAVITY,
                NO_GRAVITY);
    }

    private static GridPatternLayoutManagerAttributes gridPatternAttributes() {
        return new GridPatternLayoutManagerAttributes(
                NOT_CIRCULAR,
                NOT_SNAP_TO_POSITION,
                NOT_VIEW_PAGER,
                VIEWPORT_PAGING,
                SnapPosition.onScreen,
                SCROLL_PAST_CONTENT,
                CELL_SPACING,
                NO_SELECT_ON_SNAP,
                NO_SELECT_WHILE_SCROLLING,
                VERTICAL,
                SQUARE);
    }

    /**
     * A layout manager over a view group and an adapter, laid out at the view group's size. The
     * two-step construction is what lets the layout manager be built from the view group and the
     * recycler the harness will hold, with every field final.
     */
    private abstract static class Harness<Cell> {
        final MyViewGroup mViewGroup;
        final AdapterViewManager mAdapterViewManager;
        final BreadthsAdapter mAdapter;
        final boolean mIsVertical;
        final int mSize;
        final LayoutManager<Cell> mLayoutManager;

        Harness(
                final Parts parts,
                final boolean isVertical,
                final int[] breadths,
                final LayoutManager<Cell> layoutManager) {
            mViewGroup = parts.mViewGroup;
            mAdapterViewManager = parts.mAdapterViewManager;
            mSize = parts.mSize;
            mIsVertical = isVertical;
            mAdapter = new BreadthsAdapter(isVertical, breadths);
            mLayoutManager = layoutManager;
            attach();
        }

        private void attach() {
            mAdapterViewManager.setAdapter(mAdapter);
            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(mSize, View.MeasureSpec.EXACTLY);
            mViewGroup.measure(measureSpec, measureSpec);
            mViewGroup.layout(0, 0, mSize, mSize);
            mViewGroup.resetLayoutRequests();
        }

        final void restore(final int offset, final int startCellPosition) {
            final LayoutManagerState<Cell> state =
                    new LayoutManagerState<Cell>(
                            AbsSavedState.EMPTY_STATE, offset, startCellPosition);
            mLayoutManager.onRestoreInstanceState(state);
        }

        final void layout() {
            layout(NO_BREADTH);
        }

        final void layout(final int displacement) {
            final Animation animation = new Animation();
            animation.newAnimation();
            animation.setDisplacement(displacement);
            mLayoutManager.layout(mViewGroup, animation, 0, 0, mSize, mSize);
        }

        final void measure(final int breadthMode, final int breadthSize) {
            measure(View.MeasureSpec.EXACTLY, breadthMode, breadthSize);
        }

        final void measure(final int sizeMode, final int breadthMode, final int breadthSize) {
            final int sizeMeasureSpec = View.MeasureSpec.makeMeasureSpec(mSize, sizeMode);
            final int breadthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(breadthSize, breadthMode);
            if (mIsVertical) {
                mLayoutManager.measure(mViewGroup, breadthMeasureSpec, sizeMeasureSpec);
            } else {
                mLayoutManager.measure(mViewGroup, sizeMeasureSpec, breadthMeasureSpec);
            }
        }

        final int measureBreadth(final int breadthMode, final int breadthSize) {
            measure(breadthMode, breadthSize);
            final int breadthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(breadthSize, breadthMode);
            return mLayoutManager.measureBreadth(mViewGroup, breadthMeasureSpec);
        }
    }

    /** The view group and the recycler a layout manager is built over. */
    private static final class Parts {
        final MyViewGroup mViewGroup;
        final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
        final int mSize;

        Parts(final int padding, final int size) {
            this(padding, size, RUNS_POSTS_AT_ONCE);
        }

        Parts(final int padding, final int size, final boolean runsPostsAtOnce) {
            mViewGroup =
                    new MyViewGroup(
                            ApplicationProvider.getApplicationContext(), padding, runsPostsAtOnce);
            mSize = size;
        }
    }

    private static final class ListHarness extends Harness<View> {
        ListHarness(final boolean isVertical, final int padding, final int[] breadths) {
            this(new Parts(padding, VIEW_GROUP_SIZE), isVertical, breadths, NOT_CIRCULAR);
        }

        ListHarness(
                final boolean isVertical,
                final int padding,
                final int[] breadths,
                final boolean isCircular) {
            this(new Parts(padding, VIEW_GROUP_SIZE), isVertical, breadths, isCircular);
        }

        static ListHarness holdingPosts(
                final boolean isVertical, final int padding, final int[] breadths) {
            return new ListHarness(
                    new Parts(padding, VIEW_GROUP_SIZE, HOLDS_POSTS),
                    isVertical,
                    breadths,
                    NOT_CIRCULAR);
        }

        ListHarness(
                final boolean isVertical, final int padding, final int[] breadths, final int size) {
            this(new Parts(padding, size), isVertical, breadths, NOT_CIRCULAR);
        }

        private ListHarness(
                final Parts parts,
                final boolean isVertical,
                final int[] breadths,
                final boolean isCircular) {
            super(
                    parts,
                    isVertical,
                    breadths,
                    new ListLayoutManager(
                            parts.mViewGroup,
                            null,
                            parts.mAdapterViewManager,
                            listAttributes(isVertical, isCircular)));
        }
    }

    private static final class GridHarness extends Harness<Group> {
        GridHarness(final boolean isVertical, final int padding, final int[] breadths) {
            this(new Parts(padding, VIEW_GROUP_SIZE), isVertical, breadths);
        }

        private GridHarness(final Parts parts, final boolean isVertical, final int[] breadths) {
            super(
                    parts,
                    isVertical,
                    breadths,
                    new GridLayoutManager(
                            parts.mViewGroup,
                            null,
                            parts.mAdapterViewManager,
                            gridAttributes(isVertical)));
        }
    }

    private static final class GridPatternHarness extends Harness<GridPatternGroup> {
        private final GridPatternGroupDefinition mDefinition;

        GridPatternHarness(final int padding) {
            this(new Parts(padding, VIEW_GROUP_SIZE), pairDefinition());
        }

        private GridPatternHarness(final Parts parts, final GridPatternGroupDefinition definition) {
            super(
                    parts,
                    VERTICAL,
                    new int[GRID_PATTERN_ADAPTER_SIZE],
                    gridPatternLayoutManager(parts, definition));
            mDefinition = definition;
        }

        private static GridPatternGroupDefinition pairDefinition() {
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(new GridPatternItemDefinition(0, 0, PATTERN_ITEM, PATTERN_ITEM));
            items.add(new GridPatternItemDefinition(0, PATTERN_ITEM, PATTERN_ITEM, PATTERN_ITEM));
            return new GridPatternGroupDefinition(VERTICAL, items);
        }

        private static GridPatternLayoutManager gridPatternLayoutManager(
                final Parts parts, final GridPatternGroupDefinition definition) {
            final GridPatternLayoutManager gridPatternLayoutManager =
                    new GridPatternLayoutManager(
                            parts.mViewGroup,
                            null,
                            parts.mAdapterViewManager,
                            gridPatternAttributes());
            gridPatternLayoutManager.addGridPatternGroupDefinition(definition);
            return gridPatternLayoutManager;
        }

        GridPatternGroup aGroup() {
            final GridPatternGroup group =
                    new GridPatternGroup(mDefinition, mViewGroup, VERTICAL, SQUARE, CELL_SPACING);
            for (int index = 0; index < PATTERN_COLUMNS; index++) {
                group.addView(new FrameLayout(ApplicationProvider.getApplicationContext()));
            }
            return group;
        }
    }

    /**
     * Stands in for the view: what the layout manager posts to it runs at once, or is held until a
     * test runs it, and every layout asked for is counted.
     */
    public static final class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();
        private final boolean mRunsPostsAtOnce;
        private Runnable mPendingPost;
        private int mLayoutRequests;

        public MyViewGroup(
                final Context context, final int padding, final boolean runsPostsAtOnce) {
            super(context);
            mRunsPostsAtOnce = runsPostsAtOnce;
            setPadding(padding, padding, padding, padding);
        }

        boolean hasAPendingPost() {
            return mPendingPost != null;
        }

        void runThePendingPost() {
            final Runnable pendingPost = mPendingPost;
            mPendingPost = null;
            if (pendingPost != null) pendingPost.run();
        }

        int getLayoutRequests() {
            return mLayoutRequests;
        }

        void resetLayoutRequests() {
            mLayoutRequests = NO_LAYOUT_REQUESTED;
        }

        @Override
        public void requestLayout() {
            super.requestLayout();
            mLayoutRequests++;
        }

        @Override
        public boolean post(final Runnable action) {
            if (mRunsPostsAtOnce) {
                action.run();
            } else {
                mPendingPost = action;
            }
            return true;
        }

        @Override
        public boolean removeCallbacks(final Runnable action) {
            final boolean isThePendingPost = action == mPendingPost;
            if (isThePendingPost) mPendingPost = null;
            return super.removeCallbacks(action);
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

    /**
     * Cells of {@link #CELL_SIZE} along the scroll axis and the given breadths across it. It
     * records the positions it is asked for and the views it creates, so a test can say what was
     * obtained and whether the layout that followed created anything new.
     */
    private static final class BreadthsAdapter extends BaseAdapter {
        private final boolean mIsVertical;
        private final int[] mBreadths;
        private final List<View> mCreatedViews = new ArrayList<View>();
        private final List<Integer> mRequestedPositions = new ArrayList<Integer>();
        private int mAdapterSize;

        BreadthsAdapter(final boolean isVertical, final int[] breadths) {
            mIsVertical = isVertical;
            mBreadths = breadths;
            mAdapterSize = breadths.length;
        }

        void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
        }

        List<View> getCreatedViews() {
            return new ArrayList<View>(mCreatedViews);
        }

        List<Integer> getRequestedPositions() {
            return new ArrayList<Integer>(mRequestedPositions);
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
            mRequestedPositions.add(position);
            final View view = viewToFill(convertView);
            view.setTag(position);
            final int breadth = mBreadths[position];
            view.setLayoutParams(layoutParams(breadth));
            return view;
        }

        private ViewGroup.LayoutParams layoutParams(final int breadth) {
            if (mIsVertical) {
                return new ViewGroup.LayoutParams(breadth, CELL_SIZE);
            }
            return new ViewGroup.LayoutParams(CELL_SIZE, breadth);
        }

        private View viewToFill(final View convertView) {
            if (convertView != null) return convertView;
            final View view = new FrameLayout(ApplicationProvider.getApplicationContext());
            mCreatedViews.add(view);
            return view;
        }
    }
}
