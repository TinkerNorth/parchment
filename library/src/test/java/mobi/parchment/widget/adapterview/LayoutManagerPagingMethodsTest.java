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
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.Group;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import mobi.parchment.widget.adapterview.pageinterval.CellCountPageInterval;
import mobi.parchment.widget.adapterview.pageinterval.PageIntervalInterface;
import mobi.parchment.widget.adapterview.pageinterval.PageIntervalSelector;
import mobi.parchment.widget.adapterview.pageinterval.ViewportPageInterval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Exercises each method the ViewPager page measurement is built from on its own. End to end paging
 * lives in ViewPagerTest; this pins every method's own contract, including states a whole gesture
 * cannot easily be driven into.
 */
@RunWith(RobolectricTestRunner.class)
public class LayoutManagerPagingMethodsTest {

    private static final int THREE_CELL_VIEWPORT = 300;
    private static final int SMALL_VIEWPORT = 100;
    private static final int CELL_SIZE = 100;
    private static final int LARGE_CELL_SIZE = 250;
    private static final int CELL_SPACING = 10;
    private static final int NO_CELL_SPACING = 0;
    private static final int TEN_CELLS = 10;
    private static final int FOUR_CELLS = 4;
    private static final int VIEWPORT_PAGING = 0;
    private static final int ONE_CELL_PER_GESTURE = 1;
    private static final int TWO_CELLS_PER_GESTURE = 2;
    private static final int THREE_CELLS_PER_GESTURE = 3;
    private static final int MORE_CELLS_THAN_THE_ADAPTER_HAS = 50;
    private static final int A_NEGATIVE_INTERVAL = -2;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean HORIZONTAL = false;
    private static final boolean VERTICAL = true;
    private static final boolean IS_A_VIEW_PAGER = true;
    private static final boolean IS_NOT_A_VIEW_PAGER = false;
    private static final boolean NOT_SNAP_TO_POSITION = false;
    private static final boolean NOT_SELECT_ON_SNAP = false;
    private static final boolean NOT_SELECT_WHILE_SCROLLING = false;
    private static final boolean NO_GRAVITY_EDGE = false;
    private static final int NO_VIEWPORT_PADDING = 0;
    private static final int VIEWPORT_PADDING = 20;
    private static final int NO_DISTANCE = 0;
    private static final int NUMBER_OF_VIEWS_PER_CELL = 2;
    private static final LayoutManager<View> NO_LAYOUT_MANAGER = null;

    private static final int FIRST_CELL = 0;
    private static final int SECOND_CELL = 1;
    private static final int THIRD_CELL = 2;
    private static final int FOURTH_CELL = 3;
    private static final int LAST_OF_TEN_CELLS = TEN_CELLS - 1;
    private static final int ONE_PAST_TEN_CELLS = TEN_CELLS;

    /** The room a page has when a start snap puts the anchor at an unpadded viewport's start. */
    private static final int A_WHOLE_VIEWPORT = THREE_CELL_VIEWPORT;

    private static final int ONE_PIXEL = 1;
    private static final int SPACED_CELL_STEP = CELL_SIZE + CELL_SPACING;
    private static final int ROOM_FOR_TWO_SPACED_CELLS = CELL_SIZE + CELL_SPACING + CELL_SIZE;
    private static final int A_PAGE_THAT_MISSES_BY_A_PIXEL = A_WHOLE_VIEWPORT - ONE_PIXEL;
    private static final int NO_PAGE_ROOM = 0;
    private static final int NEGATIVE_PAGE_ROOM = -100;
    private static final int A_REST_PART_WAY_THROUGH_A_CELL = -50;
    private static final int THE_VIEWPORT_INSIDE_THE_PADDING =
            THREE_CELL_VIEWPORT - VIEWPORT_PADDING - VIEWPORT_PADDING;

    /** LayoutManager clamps an extrapolated cell start to half the int range either way. */
    private static final int THE_DRAWABLE_LIMIT = Integer.MAX_VALUE / 2;

    private static final int THE_CENTRE_SNAP_POSITION = (THREE_CELL_VIEWPORT - CELL_SIZE) / 2;
    private static final int THE_END_SNAP_POSITION = THREE_CELL_VIEWPORT - CELL_SIZE;

    private static final int A_DISTANCE_NOTHING_WOULD_MEASURE = 4321;
    private static final int A_SECOND_DISTANCE_NOTHING_WOULD_MEASURE = 8765;

    /** A cell size that makes two cells plus the padding fill the viewport exactly. */
    private static final int PADDING_FIT_CELL_SIZE = THE_VIEWPORT_INSIDE_THE_PADDING / 2;

    /** A cell size that puts a cell start between the snapped and the unsnapped page limit. */
    private static final int OFF_SNAP_CELL_SIZE = 110;

    private static final int CELLS_THAT_FIT_FROM_AN_OFF_SNAP_ANCHOR = 2;

    private static final int[] UNEQUAL_CELL_SIZES = {
        100, 50, 150, 100, 100, 100, 100, 100, 100, 100
    };
    private static final int UNEQUAL_CELLS_THAT_FILL_THE_VIEWPORT = 300;

    private static final long BEYOND_THE_INT_RANGE = 3000000000L;

    // ------------------------------------------------------------------- pagesByCellCount

    @Test
    public void pagesByCellCount_withAnIntervalOfZero_choosesTheViewportAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(VIEWPORT_PAGING)).isFalse();
    }

    @Test
    public void pagesByCellCount_withAnIntervalOfOne_choosesTheCellCountAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(ONE_CELL_PER_GESTURE)).isTrue();
    }

    @Test
    public void pagesByCellCount_withAnIntervalOfTwo_choosesTheCellCountAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(TWO_CELLS_PER_GESTURE)).isTrue();
    }

    @Test
    public void pagesByCellCount_withAnIntervalLargerThanTheAdapter_choosesTheCellCountAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(MORE_CELLS_THAN_THE_ADAPTER_HAS)).isTrue();
    }

    @Test
    public void pagesByCellCount_withTheLargestInterval_choosesTheCellCountAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(Integer.MAX_VALUE)).isTrue();
    }

    @Test
    public void pagesByCellCount_withANegativeInterval_choosesTheCellCountAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(A_NEGATIVE_INTERVAL)).isTrue();
    }

    @Test
    public void pagesByCellCount_withTheMostNegativeInterval_choosesTheCellCountAlgorithm() {
        assertThat(PageIntervalSelector.pagesByCellCount(Integer.MIN_VALUE)).isTrue();
    }

    @Test
    public void anAttributeIntervalOfZero_reachesTheDispatchUnchanged() {
        assertThat(intervalAfterAttributeParsing(VIEWPORT_PAGING)).isEqualTo(VIEWPORT_PAGING);
    }

    @Test
    public void anAttributeIntervalOfOne_reachesTheDispatchUnchanged() {
        assertThat(intervalAfterAttributeParsing(ONE_CELL_PER_GESTURE))
                .isEqualTo(ONE_CELL_PER_GESTURE);
    }

    @Test
    public void theLargestAttributeInterval_reachesTheDispatchUnchanged() {
        assertThat(intervalAfterAttributeParsing(Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void aNegativeAttributeInterval_isClampedToViewportPagingBeforeTheDispatchSeesIt() {
        assertThat(intervalAfterAttributeParsing(A_NEGATIVE_INTERVAL)).isEqualTo(VIEWPORT_PAGING);
    }

    @Test
    public void
            theMostNegativeAttributeInterval_isClampedToViewportPagingBeforeTheDispatchSeesIt() {
        assertThat(intervalAfterAttributeParsing(Integer.MIN_VALUE)).isEqualTo(VIEWPORT_PAGING);
    }

    // ------------------------------------------ the selected strategy, paging forward

    @Test
    public void getPageCellIndexForward_withAnIntervalOfZero_runsTheViewportWalk() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(forward(manager, VIEWPORT_PAGING)).isEqualTo(FOURTH_CELL);
    }

    @Test
    public void getPageCellIndexForward_withAnIntervalOfOne_countsOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(forward(manager, ONE_CELL_PER_GESTURE)).isEqualTo(SECOND_CELL);
    }

    @Test
    public void getPageCellIndexForward_withAnIntervalOfTwo_countsTwoCells() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(forward(manager, TWO_CELLS_PER_GESTURE)).isEqualTo(THIRD_CELL);
    }

    @Test
    public void getPageCellIndexForward_withALargeInterval_countsThatManyCells() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(forward(manager, MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(MORE_CELLS_THAN_THE_ADAPTER_HAS);
    }

    @Test
    public void getPageCellIndexForward_withANegativeInterval_countsBackwards() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(forward(manager, A_NEGATIVE_INTERVAL)).isEqualTo(A_NEGATIVE_INTERVAL);
    }

    @Test
    public void getPageCellIndexForward_atTheIntervalThatSelectsEachMode_switchesAlgorithms() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(forward(manager, VIEWPORT_PAGING)).isEqualTo(FOURTH_CELL);
        assertThat(forward(manager, VIEWPORT_PAGING + ONE_CELL_PER_GESTURE)).isEqualTo(SECOND_CELL);
    }

    // --------------------------------------------- the selected strategy, paging back

    @Test
    public void getPageCellIndexBack_withAnIntervalOfZero_runsTheViewportWalk() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(back(manager, VIEWPORT_PAGING)).isEqualTo(FIRST_CELL);
    }

    @Test
    public void getPageCellIndexBack_withAnIntervalOfOne_countsOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(back(manager, ONE_CELL_PER_GESTURE)).isEqualTo(THIRD_CELL);
    }

    @Test
    public void getPageCellIndexBack_withAnIntervalOfTwo_countsTwoCells() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(back(manager, TWO_CELLS_PER_GESTURE)).isEqualTo(SECOND_CELL);
    }

    @Test
    public void getPageCellIndexBack_withALargeInterval_countsThatManyCells() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(back(manager, MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(FOURTH_CELL - MORE_CELLS_THAN_THE_ADAPTER_HAS);
    }

    @Test
    public void getPageCellIndexBack_withANegativeInterval_countsForwards() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(back(manager, A_NEGATIVE_INTERVAL)).isEqualTo(FOURTH_CELL - A_NEGATIVE_INTERVAL);
    }

    @Test
    public void getPageCellIndexBack_atTheIntervalThatSelectsEachMode_switchesAlgorithms() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(back(manager, VIEWPORT_PAGING)).isEqualTo(FIRST_CELL);
        assertThat(back(manager, VIEWPORT_PAGING + ONE_CELL_PER_GESTURE)).isEqualTo(THIRD_CELL);
    }

    // --------------------------------------------------------------- the cell count counts

    @Test
    public void forwardByCellCount_fromTheFirstCell_addsTheInterval() {
        assertThat(forwardByCellCount(ONE_CELL_PER_GESTURE, FIRST_CELL)).isEqualTo(SECOND_CELL);
    }

    @Test
    public void forwardByCellCount_fromACellInTheMiddle_addsTheInterval() {
        assertThat(forwardByCellCount(THREE_CELLS_PER_GESTURE, FOURTH_CELL))
                .isEqualTo(FOURTH_CELL + THREE_CELLS_PER_GESTURE);
    }

    @Test
    public void forwardByCellCount_fromTheLastCell_namesACellBeyondTheAdapterEnd() {
        assertThat(forwardByCellCount(ONE_CELL_PER_GESTURE, LAST_OF_TEN_CELLS))
                .isEqualTo(ONE_PAST_TEN_CELLS);
    }

    @Test
    public void forwardByCellCount_withAnIntervalLargerThanTheAdapter_namesACellFarBeyondTheEnd() {
        assertThat(forwardByCellCount(MORE_CELLS_THAN_THE_ADAPTER_HAS, FIRST_CELL))
                .isEqualTo(MORE_CELLS_THAN_THE_ADAPTER_HAS);
    }

    @Test
    public void forwardByCellCount_withTheLargestInterval_doesNotWrapToANegativeIndex() {
        final long index = forwardByCellCount(Integer.MAX_VALUE, FOURTH_CELL);

        assertThat(index).isEqualTo((long) FOURTH_CELL + Integer.MAX_VALUE);
        assertThat(index).isGreaterThan(Integer.MAX_VALUE);
    }

    @Test
    public void forwardByCellCount_withTheMostNegativeInterval_doesNotWrapToAPositiveIndex() {
        final long index = forwardByCellCount(Integer.MIN_VALUE, FOURTH_CELL);

        assertThat(index).isEqualTo((long) FOURTH_CELL + Integer.MIN_VALUE);
        assertThat(index).isLessThan(0L);
    }

    @Test
    public void backByCellCount_fromTheFirstCell_namesACellBeforeTheAdapterStart() {
        assertThat(backByCellCount(ONE_CELL_PER_GESTURE, FIRST_CELL))
                .isEqualTo(-ONE_CELL_PER_GESTURE);
    }

    @Test
    public void backByCellCount_fromACellInTheMiddle_subtractsTheInterval() {
        assertThat(backByCellCount(THREE_CELLS_PER_GESTURE, FOURTH_CELL))
                .isEqualTo(FOURTH_CELL - THREE_CELLS_PER_GESTURE);
    }

    @Test
    public void backByCellCount_fromTheLastCell_subtractsTheInterval() {
        assertThat(backByCellCount(ONE_CELL_PER_GESTURE, LAST_OF_TEN_CELLS))
                .isEqualTo(LAST_OF_TEN_CELLS - ONE_CELL_PER_GESTURE);
    }

    @Test
    public void backByCellCount_withTheLargestInterval_doesNotWrapToAPositiveIndex() {
        final long index = backByCellCount(Integer.MAX_VALUE, FOURTH_CELL);

        assertThat(index).isEqualTo((long) FOURTH_CELL - Integer.MAX_VALUE);
        assertThat(index).isLessThan(0L);
    }

    @Test
    public void backByCellCount_withTheMostNegativeInterval_doesNotWrapToANegativeIndex() {
        final long index = backByCellCount(Integer.MIN_VALUE, FOURTH_CELL);

        assertThat(index).isEqualTo((long) FOURTH_CELL - Integer.MIN_VALUE);
        assertThat(index).isGreaterThan(Integer.MAX_VALUE);
    }

    @Test
    public void cellCountPaging_pastTheAdapterEndWithoutCircularScroll_isCappedAtTheLastCell() {
        final LayoutManager<View> manager = equalCellManager();
        final long index = forwardByCellCount(MORE_CELLS_THAN_THE_ADAPTER_HAS, FIRST_CELL);

        assertThat(manager.getCellStartAtIndex(index)).isEqualTo(LAST_OF_TEN_CELLS * CELL_SIZE);
    }

    @Test
    public void cellCountPaging_pastTheAdapterEndWithCircularScroll_keepsExtrapolating() {
        final LayoutManager<View> manager = circularEqualCellManager();
        final long index = forwardByCellCount(MORE_CELLS_THAN_THE_ADAPTER_HAS, FIRST_CELL);

        assertThat(manager.getCellStartAtIndex(index))
                .isEqualTo(MORE_CELLS_THAN_THE_ADAPTER_HAS * CELL_SIZE);
    }

    @Test
    public void
            cellCountPaging_beforeTheAdapterStartWithoutCircularScroll_isCappedAtTheFirstCell() {
        final LayoutManager<View> manager = equalCellManager();
        final long index = backByCellCount(MORE_CELLS_THAN_THE_ADAPTER_HAS, FIRST_CELL);

        assertThat(manager.getCellStartAtIndex(index)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void cellCountPaging_beforeTheAdapterStartWithCircularScroll_keepsExtrapolating() {
        final LayoutManager<View> manager = circularEqualCellManager();
        final long index = backByCellCount(MORE_CELLS_THAN_THE_ADAPTER_HAS, FIRST_CELL);

        assertThat(manager.getCellStartAtIndex(index))
                .isEqualTo(-MORE_CELLS_THAN_THE_ADAPTER_HAS * CELL_SIZE);
    }

    @Test
    public void cellCountPaging_withTheLargestIntervalPastTheEnd_isStillCappedAtTheLastCell() {
        final LayoutManager<View> manager = equalCellManager();
        final long index = forwardByCellCount(Integer.MAX_VALUE, FIRST_CELL);

        assertThat(manager.getCellStartAtIndex(index)).isEqualTo(LAST_OF_TEN_CELLS * CELL_SIZE);
    }

    // ----------------------------------------------------------------- the viewport walks

    @Test
    public void forwardByViewport_withAPageThatFitsExactly_takesTheCellThatFits() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportForward(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(FOURTH_CELL);
    }

    @Test
    public void forwardByViewport_withAPageThatMissesByOnePixel_leavesThatCellBehind() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportForward(manager, A_PAGE_THAT_MISSES_BY_A_PIXEL, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(THIRD_CELL);
    }

    @Test
    public void forwardByViewport_withASingleCellAdapter_findsNoFurtherCell() {
        final LayoutManager<View> manager = singleCellManager();

        assertThat(viewportForward(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(SECOND_CELL);
        assertThat(manager.getCellStartAtIndex(SECOND_CELL)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void forwardByViewport_withACellLargerThanTheViewport_advancesExactlyOneCell() {
        final LayoutManager<View> manager = largeCellManager();

        assertThat(viewportForward(manager, SMALL_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void forwardByViewport_withCellSpacing_countsTheSpacingThatComesWithEachCell() {
        final LayoutManager<View> manager = spacedCellManager();

        assertThat(viewportForward(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(THIRD_CELL);
    }

    @Test
    public void forwardByViewport_withAPageThatOnlyFitsBecauseTheSpacingIsNotPartOfIt_takesIt() {
        final LayoutManager<View> manager = spacedCellManager();

        assertThat(manager.getCellStartAtIndex(THIRD_CELL))
                .isEqualTo(ROOM_FOR_TWO_SPACED_CELLS + CELL_SPACING);
        assertThat(viewportForward(manager, ROOM_FOR_TWO_SPACED_CELLS, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(THIRD_CELL);
    }

    @Test
    public void forwardByViewport_withCellsOfUnequalSize_fitsEachCellsOwnSize() {
        final LayoutManager<View> manager = unequalCellManager();

        assertThat(manager.getCellStartAtIndex(FOURTH_CELL))
                .isEqualTo(UNEQUAL_CELLS_THAT_FILL_THE_VIEWPORT);
        assertThat(viewportForward(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(FOURTH_CELL);
    }

    @Test
    public void forwardByViewport_withViewportPadding_fitsTheCellsInsideThePaddingOnly() {
        final LayoutManager<View> manager = paddedManager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL)).isEqualTo(VIEWPORT_PADDING);
        assertThat(
                        viewportForward(
                                manager,
                                THE_VIEWPORT_INSIDE_THE_PADDING,
                                FIRST_CELL,
                                VIEWPORT_PADDING))
                .isEqualTo(THIRD_CELL);
    }

    @Test
    public void forwardByViewport_fromAnAnchorPartWayThroughACell_measuresFromItsCurrentStart() {
        final PagingHarness harness = equalCellHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL))
                .isEqualTo(A_REST_PART_WAY_THROUGH_A_CELL);
        assertThat(
                        viewportForward(
                                manager,
                                A_WHOLE_VIEWPORT,
                                FIRST_CELL,
                                A_REST_PART_WAY_THROUGH_A_CELL))
                .isEqualTo(FOURTH_CELL);
    }

    @Test
    public void forwardByViewport_withNoPageRoom_advancesExactlyOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportForward(manager, NO_PAGE_ROOM, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void forwardByViewport_withNegativePageRoom_advancesExactlyOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportForward(manager, NEGATIVE_PAGE_ROOM, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void forwardByViewport_atTheLastCellWithoutCircularScroll_findsNoFurtherCell() {
        final LayoutManager<View> manager = equalCellManager();
        final int lastCellStart = LAST_OF_TEN_CELLS * CELL_SIZE;

        final long index =
                viewportForward(manager, A_WHOLE_VIEWPORT, LAST_OF_TEN_CELLS, lastCellStart);

        assertThat(index).isEqualTo(ONE_PAST_TEN_CELLS);
        assertThat(manager.getCellStartAtIndex(index)).isEqualTo(lastCellStart);
    }

    @Test
    public void forwardByViewport_walkingIntoTheAdapterEnd_stopsWhenTheCapFreezesTheCellStarts() {
        final LayoutManager<View> manager = equalCellManager();
        final int anchorIndex = LAST_OF_TEN_CELLS - TWO_CELLS_PER_GESTURE;
        final int anchorStart = anchorIndex * CELL_SIZE;

        final long index = viewportForward(manager, A_WHOLE_VIEWPORT, anchorIndex, anchorStart);

        assertThat(index).isEqualTo(LAST_OF_TEN_CELLS);
        assertThat(manager.getCellStartAtIndex(index)).isEqualTo(LAST_OF_TEN_CELLS * CELL_SIZE);
    }

    @Test
    public void forwardByViewport_withCircularScroll_walksPastTheAdapterEnd() {
        final LayoutManager<View> manager = circularFourCellManager();
        final int anchorStart = FOURTH_CELL * CELL_SIZE;

        assertThat(viewportForward(manager, A_WHOLE_VIEWPORT, FOURTH_CELL, anchorStart))
                .isEqualTo(FOURTH_CELL + THREE_CELLS_PER_GESTURE);
    }

    @Test
    public void forwardByViewport_inAVerticalList_walksTheSameCells() {
        final LayoutManager<View> manager = verticalEqualCellManager();

        assertThat(viewportForward(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(FOURTH_CELL);
    }

    @Test
    public void backByViewport_withAPageThatFitsExactly_takesTheCellThatFits() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportBack(manager, A_WHOLE_VIEWPORT, FOURTH_CELL, FOURTH_CELL * CELL_SIZE))
                .isEqualTo(FIRST_CELL);
    }

    @Test
    public void backByViewport_withAPageThatMissesByOnePixel_leavesThatCellBehind() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(
                        viewportBack(
                                manager,
                                A_PAGE_THAT_MISSES_BY_A_PIXEL,
                                FOURTH_CELL,
                                FOURTH_CELL * CELL_SIZE))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void backByViewport_withASingleCellAdapter_findsNoFurtherCell() {
        final LayoutManager<View> manager = singleCellManager();

        assertThat(viewportBack(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(-ONE_CELL_PER_GESTURE);
        assertThat(manager.getCellStartAtIndex(-ONE_CELL_PER_GESTURE)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void backByViewport_withACellLargerThanTheViewport_advancesExactlyOneCell() {
        final LayoutManager<View> manager = largeCellManager();

        assertThat(viewportBack(manager, SMALL_VIEWPORT, SECOND_CELL, LARGE_CELL_SIZE))
                .isEqualTo(FIRST_CELL);
    }

    @Test
    public void backByViewport_withCellSpacing_countsTheSpacingThatComesWithEachCell() {
        final LayoutManager<View> manager = spacedCellManager();
        final int anchorStart = FOURTH_CELL * SPACED_CELL_STEP;

        assertThat(manager.getCellStartAtIndex(FOURTH_CELL)).isEqualTo(anchorStart);
        assertThat(viewportBack(manager, A_WHOLE_VIEWPORT, FOURTH_CELL, anchorStart))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void backByViewport_withAPageThatOnlyFitsBecauseTheSpacingIsNotPartOfIt_takesIt() {
        final LayoutManager<View> manager = spacedCellManager();
        final int anchorStart = FOURTH_CELL * SPACED_CELL_STEP;

        assertThat(viewportBack(manager, ROOM_FOR_TWO_SPACED_CELLS, FOURTH_CELL, anchorStart))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void backByViewport_withCellsOfUnequalSize_fitsEachCellsOwnSize() {
        final LayoutManager<View> manager = unequalCellManager();

        assertThat(
                        viewportBack(
                                manager,
                                A_WHOLE_VIEWPORT,
                                FOURTH_CELL,
                                UNEQUAL_CELLS_THAT_FILL_THE_VIEWPORT))
                .isEqualTo(FIRST_CELL);
    }

    @Test
    public void backByViewport_withViewportPadding_fitsTheCellsInsideThePaddingOnly() {
        final LayoutManager<View> manager = paddedManager();
        final int anchorStart = VIEWPORT_PADDING + FOURTH_CELL * CELL_SIZE;

        assertThat(viewportBack(manager, THE_VIEWPORT_INSIDE_THE_PADDING, FOURTH_CELL, anchorStart))
                .isEqualTo(SECOND_CELL);
    }

    @Test
    public void backByViewport_fromAnAnchorPartWayThroughACell_measuresToItsCurrentStart() {
        final PagingHarness harness = equalCellHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();
        final int anchorStart = FOURTH_CELL * CELL_SIZE + A_REST_PART_WAY_THROUGH_A_CELL;

        assertThat(manager.getCellStartAtIndex(FOURTH_CELL)).isEqualTo(anchorStart);
        assertThat(viewportBack(manager, A_WHOLE_VIEWPORT, FOURTH_CELL, anchorStart))
                .isEqualTo(FIRST_CELL);
    }

    @Test
    public void backByViewport_withNoPageRoom_advancesExactlyOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportBack(manager, NO_PAGE_ROOM, FOURTH_CELL, FOURTH_CELL * CELL_SIZE))
                .isEqualTo(THIRD_CELL);
    }

    @Test
    public void backByViewport_withNegativePageRoom_advancesExactlyOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(viewportBack(manager, NEGATIVE_PAGE_ROOM, FOURTH_CELL, FOURTH_CELL * CELL_SIZE))
                .isEqualTo(THIRD_CELL);
    }

    @Test
    public void backByViewport_atTheFirstCellWithoutCircularScroll_findsNoFurtherCell() {
        final LayoutManager<View> manager = equalCellManager();

        final long index = viewportBack(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE);

        assertThat(index).isEqualTo(-ONE_CELL_PER_GESTURE);
        assertThat(manager.getCellStartAtIndex(index)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void backByViewport_withCircularScroll_walksPastTheAdapterStart() {
        final LayoutManager<View> manager = circularFourCellManager();

        assertThat(viewportBack(manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE))
                .isEqualTo(-THREE_CELLS_PER_GESTURE);
    }

    @Test
    public void backByViewport_inAVerticalList_walksTheSameCells() {
        final LayoutManager<View> manager = verticalEqualCellManager();

        assertThat(viewportBack(manager, A_WHOLE_VIEWPORT, FOURTH_CELL, FOURTH_CELL * CELL_SIZE))
                .isEqualTo(FIRST_CELL);
    }

    // --------------------------------------------------------------- the join predicates

    @Test
    public void nextCellJoinsThePage_whenTheNextStartEqualsTheCurrentOne_hasNoFurtherCell() {
        assertThat(
                        ViewportPageInterval.nextCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                CELL_SIZE,
                                CELL_SIZE))
                .isFalse();
    }

    @Test
    public void nextCellJoinsThePage_whenTheNextStartIsOnTheWrongSide_hasNoFurtherCell() {
        assertThat(
                        ViewportPageInterval.nextCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                CELL_SIZE,
                                NO_DISTANCE))
                .isFalse();
    }

    @Test
    public void nextCellJoinsThePage_withAFurtherCellThatFits_joinsThePage() {
        assertThat(
                        ViewportPageInterval.nextCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                CELL_SIZE,
                                CELL_SIZE + CELL_SIZE))
                .isTrue();
    }

    @Test
    public void nextCellJoinsThePage_withAFurtherCellThatFitsExactly_joinsThePage() {
        assertThat(
                        ViewportPageInterval.nextCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                CELL_SIZE,
                                A_WHOLE_VIEWPORT))
                .isTrue();
    }

    @Test
    public void nextCellJoinsThePage_withAFurtherCellThatDoesNotFit_doesNotJoinThePage() {
        assertThat(
                        ViewportPageInterval.nextCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                CELL_SIZE,
                                A_WHOLE_VIEWPORT + ONE_PIXEL))
                .isFalse();
    }

    @Test
    public void
            previousCellJoinsThePage_whenThePreviousStartEqualsTheCurrentOne_hasNoFurtherCell() {
        assertThat(
                        ViewportPageInterval.previousCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                A_WHOLE_VIEWPORT,
                                CELL_SIZE,
                                CELL_SIZE))
                .isFalse();
    }

    @Test
    public void previousCellJoinsThePage_whenThePreviousStartIsOnTheWrongSide_hasNoFurtherCell() {
        assertThat(
                        ViewportPageInterval.previousCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                A_WHOLE_VIEWPORT,
                                CELL_SIZE,
                                CELL_SIZE + CELL_SIZE))
                .isFalse();
    }

    @Test
    public void previousCellJoinsThePage_withAFurtherCellThatFits_joinsThePage() {
        assertThat(
                        ViewportPageInterval.previousCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                A_WHOLE_VIEWPORT,
                                CELL_SIZE,
                                CELL_SIZE / 2))
                .isTrue();
    }

    @Test
    public void previousCellJoinsThePage_withAFurtherCellThatFitsExactly_joinsThePage() {
        assertThat(
                        ViewportPageInterval.previousCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                A_WHOLE_VIEWPORT,
                                CELL_SIZE,
                                NO_DISTANCE))
                .isTrue();
    }

    @Test
    public void previousCellJoinsThePage_withAFurtherCellThatDoesNotFit_doesNotJoinThePage() {
        assertThat(
                        ViewportPageInterval.previousCellJoinsThePage(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                A_WHOLE_VIEWPORT,
                                CELL_SIZE,
                                -ONE_PIXEL))
                .isFalse();
    }

    // -------------------------------------------------------------------------- pageFits

    @Test
    public void pageFits_withAPageExactlyTheMaximumSize_fits() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT, NO_CELL_SPACING, NO_DISTANCE, A_WHOLE_VIEWPORT))
                .isTrue();
    }

    @Test
    public void pageFits_withAPageOnePixelOverTheMaximum_doesNotFit() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                A_WHOLE_VIEWPORT + ONE_PIXEL))
                .isFalse();
    }

    @Test
    public void pageFits_withAPageOnePixelUnderTheMaximum_fits() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                NO_DISTANCE,
                                A_WHOLE_VIEWPORT - ONE_PIXEL))
                .isTrue();
    }

    @Test
    public void pageFits_measuringBackwardsAtTheBoundary_fits() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT, NO_CELL_SPACING, -A_WHOLE_VIEWPORT, NO_DISTANCE))
                .isTrue();
    }

    @Test
    public void pageFits_measuringBackwardsOnePixelOverTheMaximum_doesNotFit() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT,
                                NO_CELL_SPACING,
                                -A_WHOLE_VIEWPORT - ONE_PIXEL,
                                NO_DISTANCE))
                .isFalse();
    }

    @Test
    public void pageFits_withANegativePageSize_fits() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                NO_PAGE_ROOM, NO_CELL_SPACING, A_WHOLE_VIEWPORT, NO_DISTANCE))
                .isTrue();
    }

    @Test
    public void pageFits_withANegativeMaximumPageSize_rejectsAPageOfNoSize() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                -ONE_PIXEL, NO_CELL_SPACING, NO_DISTANCE, NO_DISTANCE))
                .isFalse();
    }

    @Test
    public void pageFits_withANegativeMaximumPageSize_stillFitsAPageThatMeasuresLessThanIt() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                NEGATIVE_PAGE_ROOM, NO_CELL_SPACING, A_WHOLE_VIEWPORT, NO_DISTANCE))
                .isTrue();
    }

    @Test
    public void pageFits_withCellSpacing_takesTheSpacingOffTheEndOfThePage() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT,
                                CELL_SPACING,
                                NO_DISTANCE,
                                A_WHOLE_VIEWPORT + CELL_SPACING))
                .isTrue();
    }

    @Test
    public void pageFits_withCellSpacing_rejectsThePixelPastTheSpacing() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                A_WHOLE_VIEWPORT,
                                CELL_SPACING,
                                NO_DISTANCE,
                                A_WHOLE_VIEWPORT + CELL_SPACING + ONE_PIXEL))
                .isFalse();
    }

    @Test
    public void pageFits_withCellSpacingWiderThanTheGap_measuresANegativePageAndFits() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                NO_PAGE_ROOM, CELL_SPACING, NO_DISTANCE, CELL_SPACING / 2))
                .isTrue();
    }

    @Test
    public void pageFits_acrossTheWholeDrawableRange_measuresItWithoutOverflowing() {
        final long wholeRange = (long) THE_DRAWABLE_LIMIT + THE_DRAWABLE_LIMIT;

        assertThat(
                        ViewportPageInterval.pageFits(
                                Integer.MAX_VALUE,
                                NO_CELL_SPACING,
                                -THE_DRAWABLE_LIMIT,
                                THE_DRAWABLE_LIMIT))
                .isTrue();
        assertThat(
                        ViewportPageInterval.pageFits(
                                (int) wholeRange - ONE_PIXEL,
                                NO_CELL_SPACING,
                                -THE_DRAWABLE_LIMIT,
                                THE_DRAWABLE_LIMIT))
                .isFalse();
    }

    @Test
    public void pageFits_withStartsBeyondTheIntRange_doesNotWrapToASmallPage() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                Integer.MAX_VALUE,
                                NO_CELL_SPACING,
                                -BEYOND_THE_INT_RANGE,
                                BEYOND_THE_INT_RANGE))
                .isFalse();
    }

    @Test
    public void pageFits_withABackwardsPageBeyondTheIntRange_doesNotWrapToALargePage() {
        assertThat(
                        ViewportPageInterval.pageFits(
                                NO_PAGE_ROOM,
                                NO_CELL_SPACING,
                                BEYOND_THE_INT_RANGE,
                                -BEYOND_THE_INT_RANGE))
                .isTrue();
    }

    // ------------------------------------------------------------------ getCellStartAtIndex

    @Test
    public void getCellStartAtIndex_atTheFirstDrawnCell_returnsThatCellsStart() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void getCellStartAtIndex_atADrawnCellInTheMiddle_returnsThatCellsStart() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(THIRD_CELL)).isEqualTo(THIRD_CELL * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_pastTheDrawnCells_continuesTheCellRun() {
        final LayoutManager<View> manager = equalCellManager();
        final int fifthCell = FOURTH_CELL + ONE_CELL_PER_GESTURE;

        assertThat(manager.getCellStartAtIndex(fifthCell)).isEqualTo(fifthCell * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_atTheLastAdapterCell_isThatCellsStart() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(LAST_OF_TEN_CELLS))
                .isEqualTo(LAST_OF_TEN_CELLS * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_oneCellPastTheAdapterEnd_isCappedAtTheLastCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(ONE_PAST_TEN_CELLS))
                .isEqualTo(LAST_OF_TEN_CELLS * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_farPastTheAdapterEnd_isCappedAtTheLastCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(LAST_OF_TEN_CELLS * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_oneCellBeforeTheAdapterStart_isCappedAtTheFirstCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(-ONE_CELL_PER_GESTURE)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void getCellStartAtIndex_farBeforeTheAdapterStart_isCappedAtTheFirstCell() {
        final LayoutManager<View> manager = equalCellManager();

        assertThat(manager.getCellStartAtIndex(-MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(NO_DISTANCE);
    }

    @Test
    public void getCellStartAtIndex_withCellSpacing_stepsByTheCellSizeAndTheSpacing() {
        final LayoutManager<View> manager = spacedCellManager();

        assertThat(manager.getCellStartAtIndex(LAST_OF_TEN_CELLS))
                .isEqualTo(LAST_OF_TEN_CELLS * (CELL_SIZE + CELL_SPACING));
    }

    @Test
    public void getCellStartAtIndex_withCircularScroll_extrapolatesPastTheAdapterEnd() {
        final LayoutManager<View> manager = circularEqualCellManager();

        assertThat(manager.getCellStartAtIndex(MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(MORE_CELLS_THAN_THE_ADAPTER_HAS * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_withCircularScroll_extrapolatesBeforeTheAdapterStart() {
        final LayoutManager<View> manager = circularEqualCellManager();

        assertThat(manager.getCellStartAtIndex(-MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(-MORE_CELLS_THAN_THE_ADAPTER_HAS * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_withCircularScroll_keepsCountingRatherThanWrappingTheIndex() {
        final LayoutManager<View> manager = circularFourCellManager();
        final int oncePastTheAdapter = FOUR_CELLS + THREE_CELLS_PER_GESTURE;

        assertThat(manager.getCellStartAtIndex(oncePastTheAdapter))
                .isEqualTo(oncePastTheAdapter * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_farPastTheEndWithCircularScroll_isClampedToTheDrawableLimit() {
        final LayoutManager<View> manager = circularEqualCellManager();

        assertThat(manager.getCellStartAtIndex(Integer.MAX_VALUE)).isEqualTo(THE_DRAWABLE_LIMIT);
    }

    @Test
    public void getCellStartAtIndex_farBeforeTheStartWithCircularScroll_isClampedToTheLimit() {
        final LayoutManager<View> manager = circularEqualCellManager();

        assertThat(manager.getCellStartAtIndex(-(long) Integer.MAX_VALUE))
                .isEqualTo(-THE_DRAWABLE_LIMIT);
    }

    @Test
    public void getCellStartAtIndex_inAVerticalList_returnsTheTopOfTheCell() {
        final LayoutManager<View> manager = verticalEqualCellManager();

        assertThat(manager.getCellStartAtIndex(THIRD_CELL)).isEqualTo(THIRD_CELL * CELL_SIZE);
    }

    @Test
    public void getCellStartAtIndex_afterADrag_returnsTheMovedCellStarts() {
        final PagingHarness harness = equalCellHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL))
                .isEqualTo(A_REST_PART_WAY_THROUGH_A_CELL);
        assertThat(manager.getCellStartAtIndex(FOURTH_CELL))
                .isEqualTo(FOURTH_CELL * CELL_SIZE + A_REST_PART_WAY_THROUGH_A_CELL);
    }

    @Test
    public void getCellStartExtrapolatedBeforeFirst_stepsBackByTheFirstCellSize() {
        final LayoutManager<View> manager = circularEqualCellManager();

        assertThat(manager.getCellStartExtrapolatedBeforeFirst(-ONE_CELL_PER_GESTURE))
                .isEqualTo(-CELL_SIZE);
    }

    @Test
    public void getCellStartExtrapolatedBeforeFirst_withCellSpacing_stepsBackBySizeAndSpacing() {
        final LayoutManager<View> manager = circularSpacedCellManager();

        assertThat(manager.getCellStartExtrapolatedBeforeFirst(-ONE_CELL_PER_GESTURE))
                .isEqualTo(-(CELL_SIZE + CELL_SPACING));
    }

    @Test
    public void
            getCellStartAtIndex_withCellSpacingAndCircularScroll_extrapolatesBackBySizeAndSpacing() {
        final LayoutManager<View> manager = circularSpacedCellManager();

        assertThat(manager.getCellStartAtIndex(-MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(-MORE_CELLS_THAN_THE_ADAPTER_HAS * (CELL_SIZE + CELL_SPACING));
    }

    @Test
    public void getCellStartExtrapolatedAfterLast_stepsOnByTheLastCellSize() {
        final LayoutManager<View> manager = circularEqualCellManager();

        assertThat(manager.getCellStartExtrapolatedAfterLast(MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(MORE_CELLS_THAN_THE_ADAPTER_HAS * CELL_SIZE);
    }

    @Test
    public void getCellStartExtrapolatedAfterLast_withCellSpacing_stepsOnBySizeAndSpacing() {
        final LayoutManager<View> manager = circularSpacedCellManager();

        assertThat(manager.getCellStartExtrapolatedAfterLast(MORE_CELLS_THAN_THE_ADAPTER_HAS))
                .isEqualTo(MORE_CELLS_THAN_THE_ADAPTER_HAS * (CELL_SIZE + CELL_SPACING));
    }

    // ---------------------------------------------------------------- getSnapDisplacement

    @Test
    public void getSnapDisplacement_forACellAlreadyAtTheSnapPosition_isZero() {
        final PagingHarness harness = equalCellHarness();
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getSnapDisplacement(A_WHOLE_VIEWPORT, harness.cell(FIRST_CELL)))
                .isEqualTo(NO_DISTANCE);
    }

    @Test
    public void getSnapDisplacement_forACellRestingPartWayThrough_isTheDistanceBackToTheSnap() {
        final PagingHarness harness = equalCellHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getSnapDisplacement(A_WHOLE_VIEWPORT, harness.cell(FIRST_CELL)))
                .isEqualTo(-A_REST_PART_WAY_THROUGH_A_CELL);
    }

    @Test
    public void getSnapDisplacement_withAnEndSnapPosition_measuresToTheEndOfTheViewport() {
        final PagingHarness harness = endSnapHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL))
                .isEqualTo(THE_END_SNAP_POSITION + A_REST_PART_WAY_THROUGH_A_CELL);
        assertThat(manager.getSnapDisplacement(A_WHOLE_VIEWPORT, harness.cell(FIRST_CELL)))
                .isEqualTo(-A_REST_PART_WAY_THROUGH_A_CELL);
    }

    @Test
    public void getSnapDisplacement_withACentreSnapPosition_measuresToTheCentreOfTheViewport() {
        final PagingHarness harness = centreSnapHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL))
                .isEqualTo(THE_CENTRE_SNAP_POSITION + A_REST_PART_WAY_THROUGH_A_CELL);
        assertThat(manager.getSnapDisplacement(A_WHOLE_VIEWPORT, harness.cell(FIRST_CELL)))
                .isEqualTo(-A_REST_PART_WAY_THROUGH_A_CELL);
    }

    @Test
    public void getSnapDisplacement_forACellWithNoViews_isZeroRatherThanACrash() {
        final LayoutManager<Group> manager = groupManager();
        final Group emptyGroup = new Group(HORIZONTAL);

        assertThat(manager.getView(emptyGroup)).isNull();
        assertThat(manager.getSnapDisplacement(A_WHOLE_VIEWPORT, emptyGroup))
                .isEqualTo(NO_DISTANCE);
    }

    @Test
    public void getSnapDisplacement_forAGroupOfZeroSizedViews_findsNoRepresentativeAndIsZero() {
        final LayoutManager<Group> manager = groupManager();
        final Group zeroSizedGroup = new Group(HORIZONTAL);
        zeroSizedGroup.addView(new View(ApplicationProvider.getApplicationContext()));

        assertThat(manager.getView(zeroSizedGroup)).isNull();
        assertThat(manager.getSnapDisplacement(A_WHOLE_VIEWPORT, zeroSizedGroup))
                .isEqualTo(NO_DISTANCE);
    }

    // --------------------------------------------------------------- setViewPageDistances

    @Test
    public void setViewPageDistances_whenTheViewIsNotAViewPager_leavesTheDistancesAlone() {
        final LayoutManager<View> manager = notAViewPagerManager();
        manager.mViewPageDistanceForward = A_DISTANCE_NOTHING_WOULD_MEASURE;
        manager.mViewPageDistanceBack = A_SECOND_DISTANCE_NOTHING_WOULD_MEASURE;

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(A_DISTANCE_NOTHING_WOULD_MEASURE);
        assertThat(manager.mViewPageDistanceBack)
                .isEqualTo(A_SECOND_DISTANCE_NOTHING_WOULD_MEASURE);
    }

    @Test
    public void setViewPageDistances_withViewportPaging_measuresAWholeViewportForward() {
        final LayoutManager<View> manager = equalCellManager();

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(A_WHOLE_VIEWPORT);
        assertThat(manager.mViewPageDistanceBack).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void setViewPageDistances_withCellCountPaging_measuresThatManyCells() {
        final LayoutManager<View> manager = intervalManager(TWO_CELLS_PER_GESTURE);

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(TWO_CELLS_PER_GESTURE * CELL_SIZE);
        assertThat(manager.mViewPageDistanceBack).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void setViewPageDistances_withViewportPadding_measuresThePageInsideThePadding() {
        final LayoutManager<View> manager = paddedManager();

        manager.setViewPageDistances(THE_VIEWPORT_INSIDE_THE_PADDING);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(TWO_CELLS_PER_GESTURE * CELL_SIZE);
    }

    @Test
    public void setViewPageDistances_fromAnAnchorPartWayThroughACell_measuresFromTheSnappedStart() {
        final PagingHarness harness = equalCellHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward)
                .isEqualTo(A_WHOLE_VIEWPORT + A_REST_PART_WAY_THROUGH_A_CELL);
        assertThat(manager.mViewPageDistanceBack).isEqualTo(-A_REST_PART_WAY_THROUGH_A_CELL);
    }

    @Test
    public void
            setViewPageDistances_withACentreSnapPosition_leavesThePageOnlyTheRoomAfterTheAnchor() {
        final LayoutManager<View> manager = centreSnapHarness().manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL)).isEqualTo(THE_CENTRE_SNAP_POSITION);

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(TWO_CELLS_PER_GESTURE * CELL_SIZE);
    }

    @Test
    public void
            setViewPageDistances_withAnEndSnapPosition_leavesThePageOnlyTheRoomAfterTheAnchor() {
        final LayoutManager<View> manager = endSnapHarness().manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL)).isEqualTo(THE_END_SNAP_POSITION);

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(ONE_CELL_PER_GESTURE * CELL_SIZE);
    }

    @Test
    public void setViewPageDistances_withViewportPadding_startsTheViewportAtThePaddingNotAtZero() {
        final LayoutManager<View> manager = paddedCellsThatFillInsideThePaddingManager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL)).isEqualTo(VIEWPORT_PADDING);

        manager.setViewPageDistances(THE_VIEWPORT_INSIDE_THE_PADDING);

        assertThat(manager.mViewPageDistanceForward)
                .isEqualTo(TWO_CELLS_PER_GESTURE * PADDING_FIT_CELL_SIZE);
    }

    @Test
    public void setViewPageDistances_fromAnAnchorPartWayThroughACell_countsFromWhereItSitsNow() {
        final PagingHarness harness = offSnapCellHarness();
        harness.dragBy(A_REST_PART_WAY_THROUGH_A_CELL);
        final LayoutManager<View> manager = harness.manager();

        assertThat(manager.getCellStartAtIndex(FIRST_CELL))
                .isEqualTo(A_REST_PART_WAY_THROUGH_A_CELL);

        manager.setViewPageDistances(A_WHOLE_VIEWPORT);

        assertThat(manager.mViewPageDistanceForward)
                .isEqualTo(
                        CELLS_THAT_FIT_FROM_AN_OFF_SNAP_ANCHOR * OFF_SNAP_CELL_SIZE
                                + A_REST_PART_WAY_THROUGH_A_CELL);
    }

    @Test
    public void setViewPageDistances_withASizeThePaddingHasEatenAway_stillAdvancesOneCell() {
        final LayoutManager<View> manager = equalCellManager();

        manager.setViewPageDistances(NEGATIVE_PAGE_ROOM);

        assertThat(manager.mViewPageDistanceForward).isEqualTo(CELL_SIZE);
        assertThat(manager.mViewPageDistanceBack).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void paddingLargerThanTheViewport_leavesANegativePageRoomAndStillAdvancesOneCell() {
        final LayoutManager<View> manager = paddingLargerThanTheViewportManager();
        final int paddingTotal = manager.getStartSizePadding() + manager.getEndSizePadding();

        assertThat(paddingTotal).isGreaterThan(SMALL_VIEWPORT);
        assertThat(manager.mViewPageDistanceForward).isEqualTo(CELL_SIZE);
    }

    // ------------------------------------------------------------------------- harnesses

    private static long forward(final LayoutManager<View> manager, final int viewPagerInterval) {
        final PageIntervalInterface<View> pageInterval =
                PageIntervalSelector.getPageIntervalInterface(viewPagerInterval);
        return pageInterval.getPageCellIndexForward(
                manager, A_WHOLE_VIEWPORT, FIRST_CELL, NO_DISTANCE);
    }

    private static long back(final LayoutManager<View> manager, final int viewPagerInterval) {
        final PageIntervalInterface<View> pageInterval =
                PageIntervalSelector.getPageIntervalInterface(viewPagerInterval);
        return pageInterval.getPageCellIndexBack(
                manager, A_WHOLE_VIEWPORT, FOURTH_CELL, FOURTH_CELL * CELL_SIZE);
    }

    private static long viewportForward(
            final LayoutManager<View> manager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart) {
        final ViewportPageInterval<View> pageInterval = new ViewportPageInterval<View>();
        return pageInterval.getPageCellIndexForward(
                manager, maximumPageSize, anchorIndex, anchorStart);
    }

    private static long viewportBack(
            final LayoutManager<View> manager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart) {
        final ViewportPageInterval<View> pageInterval = new ViewportPageInterval<View>();
        return pageInterval.getPageCellIndexBack(
                manager, maximumPageSize, anchorIndex, anchorStart);
    }

    private static long forwardByCellCount(final int viewPagerInterval, final int anchorIndex) {
        final CellCountPageInterval<View> pageInterval =
                new CellCountPageInterval<View>(viewPagerInterval);
        return pageInterval.getPageCellIndexForward(
                NO_LAYOUT_MANAGER, NO_PAGE_ROOM, anchorIndex, NO_DISTANCE);
    }

    private static long backByCellCount(final int viewPagerInterval, final int anchorIndex) {
        final CellCountPageInterval<View> pageInterval =
                new CellCountPageInterval<View>(viewPagerInterval);
        return pageInterval.getPageCellIndexBack(
                NO_LAYOUT_MANAGER, NO_PAGE_ROOM, anchorIndex, NO_DISTANCE);
    }

    private static int intervalAfterAttributeParsing(final int viewPagerInterval) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        NOT_CIRCULAR,
                        NOT_SNAP_TO_POSITION,
                        IS_A_VIEW_PAGER,
                        viewPagerInterval,
                        SnapPosition.start,
                        NO_CELL_SPACING,
                        NOT_SELECT_ON_SNAP,
                        NOT_SELECT_WHILE_SCROLLING,
                        HORIZONTAL);
        return attributes.getViewPagerInterval();
    }

    private static PagingHarness equalCellHarness() {
        return new PagingHarness(
                THREE_CELL_VIEWPORT,
                equalSizes(CELL_SIZE, TEN_CELLS),
                NO_CELL_SPACING,
                VIEWPORT_PAGING,
                NOT_CIRCULAR,
                SnapPosition.start);
    }

    private static LayoutManager<View> equalCellManager() {
        return equalCellHarness().manager();
    }

    private static LayoutManager<View> intervalManager(final int viewPagerInterval) {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        viewPagerInterval,
                        NOT_CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> spacedCellManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> unequalCellManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        UNEQUAL_CELL_SIZES,
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> singleCellManager() {
        final int[] oneCell = {CELL_SIZE};
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        oneCell,
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> largeCellManager() {
        return new PagingHarness(
                        SMALL_VIEWPORT,
                        equalSizes(LARGE_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> circularEqualCellManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> circularSpacedCellManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        CELL_SPACING,
                        VIEWPORT_PAGING,
                        CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> paddedCellsThatFillInsideThePaddingManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(PADDING_FIT_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start,
                        VIEWPORT_PADDING,
                        HORIZONTAL,
                        IS_A_VIEW_PAGER)
                .manager();
    }

    private static PagingHarness offSnapCellHarness() {
        return new PagingHarness(
                THREE_CELL_VIEWPORT,
                equalSizes(OFF_SNAP_CELL_SIZE, TEN_CELLS),
                NO_CELL_SPACING,
                VIEWPORT_PAGING,
                NOT_CIRCULAR,
                SnapPosition.start);
    }

    private static LayoutManager<View> circularFourCellManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, FOUR_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        CIRCULAR,
                        SnapPosition.start)
                .manager();
    }

    private static LayoutManager<View> verticalEqualCellManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start,
                        NO_VIEWPORT_PADDING,
                        VERTICAL,
                        IS_A_VIEW_PAGER)
                .manager();
    }

    private static LayoutManager<View> paddedManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start,
                        VIEWPORT_PADDING,
                        HORIZONTAL,
                        IS_A_VIEW_PAGER)
                .manager();
    }

    private static LayoutManager<View> notAViewPagerManager() {
        return new PagingHarness(
                        THREE_CELL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start,
                        NO_VIEWPORT_PADDING,
                        HORIZONTAL,
                        IS_NOT_A_VIEW_PAGER)
                .manager();
    }

    private static LayoutManager<View> paddingLargerThanTheViewportManager() {
        return new PagingHarness(
                        SMALL_VIEWPORT,
                        equalSizes(CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        VIEWPORT_PAGING,
                        NOT_CIRCULAR,
                        SnapPosition.start,
                        SMALL_VIEWPORT,
                        HORIZONTAL,
                        IS_A_VIEW_PAGER)
                .manager();
    }

    private static PagingHarness endSnapHarness() {
        return new PagingHarness(
                THREE_CELL_VIEWPORT,
                equalSizes(CELL_SIZE, TEN_CELLS),
                NO_CELL_SPACING,
                VIEWPORT_PAGING,
                NOT_CIRCULAR,
                SnapPosition.end);
    }

    private static PagingHarness centreSnapHarness() {
        return new PagingHarness(
                THREE_CELL_VIEWPORT,
                equalSizes(CELL_SIZE, TEN_CELLS),
                NO_CELL_SPACING,
                VIEWPORT_PAGING,
                NOT_CIRCULAR,
                SnapPosition.center);
    }

    private static LayoutManager<Group> groupManager() {
        final Context context = ApplicationProvider.getApplicationContext();
        final PagingViewGroup gridViewGroup = new PagingViewGroup(context);
        final AdapterViewManager gridAdapterViewManager = new AdapterViewManager();
        final GridLayoutManagerAttributes gridAttributes =
                new GridLayoutManagerAttributes(
                        NUMBER_OF_VIEWS_PER_CELL,
                        NOT_CIRCULAR,
                        NOT_SNAP_TO_POSITION,
                        IS_A_VIEW_PAGER,
                        VIEWPORT_PAGING,
                        SnapPosition.start,
                        NO_CELL_SPACING,
                        NOT_SELECT_ON_SNAP,
                        NOT_SELECT_WHILE_SCROLLING,
                        HORIZONTAL,
                        NO_GRAVITY_EDGE,
                        NO_GRAVITY_EDGE,
                        NO_GRAVITY_EDGE,
                        NO_GRAVITY_EDGE);
        return new GridLayoutManager(gridViewGroup, null, gridAdapterViewManager, gridAttributes);
    }

    private static int[] equalSizes(final int cellSize, final int cellCount) {
        final int[] sizes = new int[cellCount];
        for (int index = 0; index < cellCount; index++) {
            sizes[index] = cellSize;
        }
        return sizes;
    }

    private static final class PagingHarness {
        private final PagingViewGroup mHarnessViewGroup;
        private final LayoutManager<View> mHarnessLayoutManager;
        private final Animation mHarnessAnimation = new Animation();
        private final int mHarnessViewportSize;

        private PagingHarness(
                final int viewportSize,
                final int[] cellSizes,
                final int cellSpacing,
                final int viewPagerInterval,
                final boolean isCircularScroll,
                final SnapPosition snapPosition) {
            this(
                    viewportSize,
                    cellSizes,
                    cellSpacing,
                    viewPagerInterval,
                    isCircularScroll,
                    snapPosition,
                    NO_VIEWPORT_PADDING,
                    HORIZONTAL,
                    IS_A_VIEW_PAGER);
        }

        private PagingHarness(
                final int viewportSize,
                final int[] cellSizes,
                final int cellSpacing,
                final int viewPagerInterval,
                final boolean isCircularScroll,
                final SnapPosition snapPosition,
                final int viewportPadding,
                final boolean isVertical,
                final boolean isViewPager) {
            mHarnessViewportSize = viewportSize;
            mHarnessViewGroup = new PagingViewGroup(ApplicationProvider.getApplicationContext());
            final AdapterViewManager harnessAdapterViewManager = new AdapterViewManager();
            final LayoutManagerAttributes harnessAttributes =
                    new LayoutManagerAttributes(
                            isCircularScroll,
                            NOT_SNAP_TO_POSITION,
                            isViewPager,
                            viewPagerInterval,
                            snapPosition,
                            cellSpacing,
                            NOT_SELECT_ON_SNAP,
                            NOT_SELECT_WHILE_SCROLLING,
                            isVertical);
            mHarnessLayoutManager =
                    new ListLayoutManager(
                            mHarnessViewGroup, null, harnessAdapterViewManager, harnessAttributes);
            harnessAdapterViewManager.setAdapter(new SizedCellAdapter(cellSizes));

            mHarnessViewGroup.setPadding(
                    viewportPadding, viewportPadding, viewportPadding, viewportPadding);
            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(viewportSize, View.MeasureSpec.EXACTLY);
            mHarnessViewGroup.measure(measureSpec, measureSpec);
            mHarnessViewGroup.layout(0, 0, viewportSize, viewportSize);

            layout();
            mHarnessAnimation.newAnimation();
            layout();
        }

        private void layout() {
            mHarnessLayoutManager.layout(
                    mHarnessViewGroup,
                    mHarnessAnimation,
                    0,
                    0,
                    mHarnessViewportSize,
                    mHarnessViewportSize);
        }

        private LayoutManager<View> manager() {
            return mHarnessLayoutManager;
        }

        private View cell(final int cellIndex) {
            return mHarnessLayoutManager.mCells.get(cellIndex);
        }

        private void dragBy(final int displacement) {
            mHarnessAnimation.setDisplacement(displacement);
            layout();
        }
    }

    private static final class PagingViewGroup extends LinearLayout implements AdapterViewHandler {
        private final List<View> mChildren = new ArrayList<View>();

        private PagingViewGroup(final Context context) {
            super(context);
        }

        @Override
        public boolean addViewInAdapterView(
                final View view, final int index, final ViewGroup.LayoutParams layoutParams) {
            mChildren.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(final View view) {
            mChildren.remove(view);
        }
    }

    private static final class SizedCellAdapter extends BaseAdapter {
        private final int[] mAdapterCellSizes;

        private SizedCellAdapter(final int[] cellSizes) {
            mAdapterCellSizes = cellSizes;
        }

        @Override
        public int getCount() {
            return mAdapterCellSizes.length;
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
            final int cellSize = mAdapterCellSizes[position];
            final FrameLayout view = new FrameLayout(ApplicationProvider.getApplicationContext());
            view.setTag(position);
            view.setLayoutParams(new ViewGroup.LayoutParams(cellSize, cellSize));
            return view;
        }
    }
}
