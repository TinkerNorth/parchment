// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import mobi.parchment.widget.adapterview.gridview.Group;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SnapSettleTest {

    private static final int VIEW_BREADTH = 400;
    private static final int VIEW_SIZE = 300;
    private static final int LIST_CELL_SIZE = 101;
    private static final int SHORT_ADAPTER_SIZE = 3;
    private static final int ADAPTER_SIZE = 9;
    private static final int FLING_THEN_SNAP_FRAME_REQUESTS = 36;
    private static final int FRAME_BUDGET = FLING_THEN_SNAP_FRAME_REQUESTS * 3;
    private static final float FLING_VELOCITY = -1000f;
    private static final float DRAG_DISTANCE = 45f;
    private static final int CENTRED_CELL_START = 48;
    private static final int PADDED_CENTRED_CELL_START = 53;
    private static final int PADDED_CELL_BREADTH_START = 15;
    private static final int STARTED_CELL_START = 0;
    private static final int ENDED_CELL_START = 96;
    private static final int ON_SCREEN_CELL_START = 0;
    private static final int HORIZONTAL_CENTRED_CELL_START = 123;
    private static final int CENTRED_LIST_CELL_START = 99;
    private static final int DRAGGED_CELL_START = -45;
    private static final int ODD_TALL_CELL_SIZE = VIEW_SIZE + 5;
    private static final int EVEN_TALL_CELL_SIZE = VIEW_SIZE + 6;
    private static final int CENTRED_TALL_CELL_START = -2;
    private static final int CENTRED_EVEN_TALL_CELL_START = -3;
    private static final int FIRST_VIEW_OF_SECOND_CELL = 3;
    private static final int LAST_VIEW_OF_SECOND_CELL = 5;
    private static final int PADDED_STARTED_CELL_START = 20;
    private static final int HORIZONTAL_PADDED_CENTRED_CELL_START = 118;
    private static final float FLING_TO_THE_END_VELOCITY = -2500f;
    private static final int WITHIN_CONTENT_CELL_SIZE = 100;
    private static final int SHORT_WITHIN_CONTENT_ADAPTER_SIZE = 4;
    private static final int UNEVEN_CELL_SIZE = 110;
    private static final int FIRST_ROW_START = 0;
    private static final int SECOND_ROW_START = WITHIN_CONTENT_CELL_SIZE;
    private static final int THIRD_ROW_START = 2 * WITHIN_CONTENT_CELL_SIZE;
    private static final int SECOND_UNEVEN_ROW_START = UNEVEN_CELL_SIZE;
    private static final int THIRD_UNEVEN_ROW_START = 2 * UNEVEN_CELL_SIZE;
    private static final int LAST_UNEVEN_ROW_START = VIEW_SIZE - UNEVEN_CELL_SIZE;
    private static final int MIDDLE_UNEVEN_ROW_START = LAST_UNEVEN_ROW_START - UNEVEN_CELL_SIZE;
    private static final int CUT_UNEVEN_ROW_START = MIDDLE_UNEVEN_ROW_START - UNEVEN_CELL_SIZE;
    private static final int THIRD_CELL = 2;
    private static final float DRAG_INTO_THE_CLAMP = 180f;
    private static final float DRAG_FURTHER_INTO_THE_CLAMP = 20f;
    private static final float DRAG_NEAR_THE_END = 650f;
    private static final float DRAG_INTO_THE_END = 250f;
    private static final int CENTRED_LAST_LIST_CELL_END = CENTRED_LIST_CELL_START + LIST_CELL_SIZE;
    private static final int SECOND_CELL = 1;
    private static final int LAST_CELL = ADAPTER_SIZE - 1;
    private static final float DRAG_STEP_PAST_THE_LAST_CELL = 150f;
    private static final int DRAG_STEPS_PAST_THE_LAST_CELL = 6;
    private static final int SEVENTH_CELL = 6;
    private static final int FIRST_CELL = 0;
    private static final int FOURTH_CELL = 3;
    private static final int SIXTH_CELL = 5;
    private static final int FIRST_VIEW_OF_THE_THIRD_CELL = 6;
    private static final int FIRST_VIEW_OF_THE_FOURTH_ROW = 6;
    private static final int LAST_VIEW_OF_THE_FOURTH_ROW = 7;
    private static final int NON_UNIFORM_ADAPTER_SIZE = 20;
    private static final int A_POSITION_IN_A_LATE_TALL_GROUP = 16;
    private static final int FIRST_POSITION_OF_THAT_TALL_GROUP = 15;
    private static final int NARROW_CELL_BREADTH = 100;
    private static final int WIDE_CELL_BREADTH = 200;
    private static final int FIRST_WIDE_CELL = 6;
    // Recorded on main at a0688d7, before smoothScrollToPosition, with a scroll listener attached:
    // a
    // change to any of them is a change to a gesture. They count requests, not frames; the
    // listener's
    // undispatched-state request adds one call to a fling that the view dedups, which is the one
    // more
    // than FLING_THEN_SNAP_FRAME_REQUESTS.
    private static final int FLING_FRAME_REQUESTS = FLING_THEN_SNAP_FRAME_REQUESTS + 1;
    private static final List<Integer> FLING_DISPLACEMENTS =
            Arrays.asList(
                    -17, -17, -16, -15, -15, -13, -12, -12, -10, -10, -8, -8, -8, -6, -6, -6, -5,
                    -4, -5, -3, -4, -3, -3, -2, -3, -2, -1, -2, -1, -1, -1, -1, -1, -1);
    private static final int DRAG_RELEASE_SNAP_FRAME_REQUESTS = 9;
    private static final List<Integer> DRAG_RELEASE_SNAP_DISPLACEMENTS =
            Arrays.asList(-45, 15, 11, 9, 6, 3, 1);
    private static final int TAP_TO_SNAP_FRAME_REQUESTS = 16;
    private static final List<Integer> TAP_TO_SNAP_DISPLACEMENTS =
            Arrays.asList(-16, -14, -13, -12, -11, -10, -8, -7, -7, -5, -3, -3, -2);

    private Activity mActivity;
    private FrameLayout mContent;

    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(Activity.class).setup().get();
        mContent = new FrameLayout(mActivity);
        mActivity.setContentView(mContent);
    }

    @Test
    public void gridPatternCenterSnap_afterADrag_settlesWithTheCellCentred() {
        final SettleGridPatternView view = centerGridPatternView(ADAPTER_SIZE);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(CENTRED_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_afterAFling_settlesWithTheCellCentred() {
        final SettleGridPatternView view = centerGridPatternView(ADAPTER_SIZE);

        fling(view, FLING_VELOCITY);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(CENTRED_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_afterTappingACell_settlesWithThatCellCentred() {
        final SettleGridPatternView view = centerGridPatternView(ADAPTER_SIZE);
        final View firstViewOfTheSecondCell = view.getChildAt(FIRST_VIEW_OF_SECOND_CELL);

        tap(view, firstViewOfTheSecondCell);

        assertSettled(view);
        assertThat(firstViewOfTheSecondCell.getTop()).isEqualTo(CENTRED_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_onceTheCellIsCentred_asksForNoFurtherMovement() {
        final SettleGridPatternView view = centerGridPatternView(ADAPTER_SIZE);

        drag(view);

        assertThat(view.getLayoutManager().snapTo(view)).isEqualTo(0);
    }

    @Test
    public void gridPatternEndSnap_onceTheCellIsAtTheEnd_asksForNoFurtherMovement() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_end, ADAPTER_SIZE, true);

        drag(view);

        assertThat(view.getLayoutManager().snapTo(view)).isEqualTo(0);
    }

    @Test
    public void gridPatternCenterSnap_withContentShorterThanTheViewport_centresTheOnlyCell() {
        final SettleGridPatternView view = centerGridPatternView(SHORT_ADAPTER_SIZE);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).containsExactly(CENTRED_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_withPadding_centresTheCellInsideThePadding() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_padded, ADAPTER_SIZE, true);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(PADDED_CENTRED_CELL_START);
        assertThat(view.getChildAt(0).getLeft()).isEqualTo(PADDED_CELL_BREADTH_START);
    }

    @Test
    public void gridPatternCenterSnap_withCircularScroll_settlesWithTheCellOnScreen() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_circular, ADAPTER_SIZE, true);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(ON_SCREEN_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_scrollingHorizontally_settlesWithTheCellCentred() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_horizontal, ADAPTER_SIZE, false);

        drag(view);

        assertSettled(view);
        assertThat(horizontalCellStarts(view)).contains(HORIZONTAL_CENTRED_CELL_START);
    }

    @Test
    public void gridPatternStartSnap_afterADrag_settlesWithTheCellAtTheStart() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_start, ADAPTER_SIZE, true);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(STARTED_CELL_START);
    }

    @Test
    public void gridPatternEndSnap_afterADrag_settlesWithTheCellAtTheEnd() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_end, ADAPTER_SIZE, true);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(ENDED_CELL_START);
    }

    @Test
    public void gridPatternStartSnap_withPadding_settlesWithTheCellAtThePaddingEdge() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_start_padded, ADAPTER_SIZE, true);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(PADDED_STARTED_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_scrollingHorizontallyWithPadding_centresInsideThePadding() {
        final SettleGridPatternView view =
                gridPatternView(
                        R.layout.settle_grid_pattern_horizontal_padded, ADAPTER_SIZE, false);

        drag(view);

        assertSettled(view);
        assertThat(horizontalCellStarts(view)).contains(HORIZONTAL_PADDED_CENTRED_CELL_START);
    }

    @Test
    public void gridPatternOnScreenSnap_afterADrag_settlesWhereTheDragLeftIt() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_on_screen, ADAPTER_SIZE, true);

        drag(view);

        assertSettled(view);
        assertThat(cellStarts(view)).contains(DRAGGED_CELL_START);
    }

    @Test
    public void centerSnap_cellTallerThanTheViewportByAnOddNumberOfPixels_settles() {
        final SettleListView view = tallCellListView();

        drag(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_TALL_CELL_START);
    }

    @Test
    public void centerSnap_cellTallerThanTheViewportByAnEvenNumberOfPixels_settles() {
        final SettleListView view = evenTallCellListView();

        drag(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_EVEN_TALL_CELL_START);
    }

    @Test
    public void gridPatternCenterSnap_afterTappingAViewThatIsNotItsCellsFirst_centresThatCell() {
        final SettleGridPatternView view = centerGridPatternView(ADAPTER_SIZE);
        final View lastViewOfTheSecondCell = view.getChildAt(LAST_VIEW_OF_SECOND_CELL);
        final int startInsideItsCell = lastViewOfTheSecondCell.getTop() - cellStarts(view).get(1);

        tap(view, lastViewOfTheSecondCell);

        assertSettled(view);
        assertThat(lastViewOfTheSecondCell.getTop())
                .isEqualTo(CENTRED_CELL_START + startInsideItsCell);
    }

    @Test
    public void snapping_toAViewThatBelongsToNoDrawnCell_asksForNoMovement() {
        final SettleGridPatternView view = centerGridPatternView(ADAPTER_SIZE);
        final View viewOutsideTheAdapter = new FrameLayout(mActivity);
        final LayoutManager<?> layoutManager = view.getLayoutManager();
        final int size = layoutManager.getSizeInsidePadding(view);

        final int distance =
                layoutManager.getSnapToPixelDistanceForView(size, viewOutsideTheAdapter);

        assertThat(distance).isEqualTo(0);
    }

    @Test
    public void listCenterSnap_afterADrag_settlesWithTheCellCentred() {
        final SettleListView view = listView();

        drag(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
    }

    @Test
    public void listCenterSnap_afterAFling_settlesWithTheCellCentred() {
        final SettleListView view = listView();

        fling(view, FLING_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
    }

    @Test
    public void listCenterSnap_draggedIntoTheEnd_holdsTheLastCellCentredUnderTheFinger() {
        final SettleListView view = listView();

        pressAndDragBy(view, DRAG_NEAR_THE_END);
        dragOnBy(view, DRAG_INTO_THE_END);
        assertThat(contentEnd(view)).isEqualTo(CENTRED_LAST_LIST_CELL_END);

        release(view);

        assertSettled(view);
        assertThat(contentEnd(view)).isEqualTo(CENTRED_LAST_LIST_CELL_END);
    }

    @Test
    public void gridCenterSnap_afterADrag_settlesWithTheTallestViewOfTheCellCentred() {
        final SettleGridView view = gridView();

        drag(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
    }

    @Test
    public void gridCenterSnap_afterAFling_settlesWithTheTallestViewOfTheCellCentred() {
        final SettleGridView view = gridView();

        fling(view, FLING_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
    }

    @Test
    public void
            listStartSnap_scrollWithinContent_afterAFlingToTheEnd_restsWithTheContentEndAtTheViewEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_within_content,
                        WITHIN_CONTENT_CELL_SIZE,
                        ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view)).contains(FIRST_ROW_START, SECOND_ROW_START, THIRD_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listEndSnap_scrollWithinContent_afterAFlingToTheEnd_restsWithTheContentEndAtTheViewEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_end_within_content,
                        WITHIN_CONTENT_CELL_SIZE,
                        ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view)).contains(FIRST_ROW_START, SECOND_ROW_START, THIRD_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listCenterSnap_scrollWithinContent_afterAFlingToTheEnd_restsWithTheContentEndAtTheViewEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_center_within_content,
                        WITHIN_CONTENT_CELL_SIZE,
                        ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view)).contains(FIRST_ROW_START, SECOND_ROW_START, THIRD_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listStartSnap_scrollWithinContent_cellsThatDoNotTileTheView_afterAFlingToTheEnd_restsAtTheContentEnd() {
        final SettleListView view =
                listView(R.layout.settle_list_start_within_content, UNEVEN_CELL_SIZE, ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listEndSnap_scrollWithinContent_cellsThatDoNotTileTheView_afterAFlingToTheEnd_restsAtTheContentEnd() {
        final SettleListView view =
                listView(R.layout.settle_list_end_within_content, UNEVEN_CELL_SIZE, ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listCenterSnap_scrollWithinContent_cellsThatDoNotTileTheView_afterAFlingToTheEnd_restsAtTheContentEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_center_within_content, UNEVEN_CELL_SIZE, ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listStartSnap_scrollWithinContent_scrollingHorizontallyWithCellsThatDoNotTileTheView_afterAFlingToTheEnd_restsAtTheContentEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_within_content_horizontal,
                        UNEVEN_CELL_SIZE,
                        ADAPTER_SIZE);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(horizontalContentEnd(view)).isEqualTo(VIEW_BREADTH);
    }

    @Test
    public void
            listEndSnap_scrollWithinContent_cellsThatDoNotTileTheView_aTouchAfterTheFirstLayoutMovesNothing() {
        final SettleListView view =
                listView(R.layout.settle_list_end_within_content, UNEVEN_CELL_SIZE, ADAPTER_SIZE);
        final List<Integer> topsAfterTheFirstLayout = childTops(view);

        touch(view);

        assertSettled(view);
        assertThat(childTops(view)).isEqualTo(topsAfterTheFirstLayout);
    }

    @Test
    public void listStartSnap_scrollWithinContent_draggingFurtherIntoTheClamp_neverMovesBack() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_within_content,
                        UNEVEN_CELL_SIZE,
                        SHORT_WITHIN_CONTENT_ADAPTER_SIZE);

        pressAndDragBy(view, DRAG_INTO_THE_CLAMP);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);

        dragOnBy(view, DRAG_FURTHER_INTO_THE_CLAMP);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);

        release(view);
        assertSettled(view);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listStartSnap_scrollWithinContent_afterTappingACellBeyondTheClamp_restsAtTheContentEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_within_content,
                        WITHIN_CONTENT_CELL_SIZE,
                        SHORT_WITHIN_CONTENT_ADAPTER_SIZE);
        final int lastChildIndex = view.getChildCount() - 1;
        final View lastCell = view.getChildAt(lastChildIndex);
        assertThat(lastCell.getTop()).isEqualTo(VIEW_SIZE);

        tap(view, lastCell);

        assertSettled(view);
        assertThat(lastCell.getBottom()).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listStartSnap_scrollWithinContent_withSelectOnSnap_tappingACellBeyondTheClamp_restsAtTheClampAndSelectsItOnce() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_within_content_select_on_snap,
                        UNEVEN_CELL_SIZE,
                        SHORT_WITHIN_CONTENT_ADAPTER_SIZE);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        view.setOnItemSelectedListener(selections);
        final View cellBeyondTheClamp = childAtTop(view, THIRD_UNEVEN_ROW_START);

        tap(view, cellBeyondTheClamp);

        assertSettled(view);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
        assertThat(cellBeyondTheClamp.getTop()).isEqualTo(MIDDLE_UNEVEN_ROW_START);
        assertThat(selections.mSelectionCount).isEqualTo(1);
        assertThat(selections.mLastPosition).isEqualTo(THIRD_CELL);
    }

    @Test
    public void
            viewPagerStartSnap_scrollWithinContent_cellsThatDoNotTileTheView_pagesOntoTheLastPageAndStaysThere() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_view_pager_within_content,
                        UNEVEN_CELL_SIZE,
                        SHORT_WITHIN_CONTENT_ADAPTER_SIZE);

        page(view);
        assertSettled(view);
        assertThat(childTops(view))
                .contains(FIRST_ROW_START, SECOND_UNEVEN_ROW_START, THIRD_UNEVEN_ROW_START);

        page(view);
        assertSettled(view);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);

        page(view);
        assertSettled(view);
        assertThat(childTops(view))
                .contains(CUT_UNEVEN_ROW_START, MIDDLE_UNEVEN_ROW_START, LAST_UNEVEN_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            viewPagerStartSnap_scrollWithinContent_backFromTheContentEnd_returnsToTheCellItCameFrom() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_view_pager_within_content,
                        UNEVEN_CELL_SIZE,
                        SHORT_WITHIN_CONTENT_ADAPTER_SIZE);
        page(view);
        assertThat(topOfPosition(view, SECOND_CELL)).isEqualTo(FIRST_ROW_START);
        page(view);
        assertThat(topOfPosition(view, SECOND_CELL)).isEqualTo(CUT_UNEVEN_ROW_START);

        pageBack(view);

        assertSettled(view);
        assertThat(topOfPosition(view, SECOND_CELL)).isEqualTo(FIRST_ROW_START);
    }

    @Test
    public void
            listStartSnap_scrollWithinContent_setSelectionFromTheEndBound_putsTheCellAtTheStartAndATouchMovesNothing() {
        final SettleListView view =
                listView(R.layout.settle_list_start_within_content, UNEVEN_CELL_SIZE, ADAPTER_SIZE);
        select(view, LAST_CELL);
        assertThat(topOfPosition(view, LAST_CELL)).isEqualTo(LAST_UNEVEN_ROW_START);

        select(view, SEVENTH_CELL);
        assertThat(topOfPosition(view, SEVENTH_CELL)).isEqualTo(FIRST_ROW_START);

        touch(view);

        assertSettled(view);
        assertThat(topOfPosition(view, SEVENTH_CELL)).isEqualTo(FIRST_ROW_START);
    }

    @Test
    public void
            listStartSnap_withSelectOnSnap_draggedPastTheLastCell_staysHeldOnItAndSelectsItOnce() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_select_on_snap,
                        WITHIN_CONTENT_CELL_SIZE,
                        ADAPTER_SIZE);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        final RecordingScrollListener scrollStates = new RecordingScrollListener();
        view.setOnItemSelectedListener(selections);
        view.setOnScrollListener(scrollStates);

        pressAndDragBy(view, DRAG_STEP_PAST_THE_LAST_CELL);
        for (int step = 1; step < DRAG_STEPS_PAST_THE_LAST_CELL; step++) {
            dragOnBy(view, DRAG_STEP_PAST_THE_LAST_CELL);
        }
        assertThat(topOfPosition(view, LAST_CELL)).isEqualTo(FIRST_ROW_START);

        release(view);

        assertSettled(view);
        assertThat(topOfPosition(view, LAST_CELL)).isEqualTo(FIRST_ROW_START);
        assertThat(selections.mSelectionCount).isEqualTo(1);
        assertThat(selections.mLastPosition).isEqualTo(LAST_CELL);
        assertThat(scrollStates.mStates).doesNotContain(ScrollState.settling);
    }

    @Test
    public void
            gridStartSnap_scrollWithinContent_withSpacingAndAShortLastRow_afterAFlingToTheEnd_restsAtTheContentEnd() {
        final SettleGridView view =
                gridView(R.layout.settle_grid_start_within_content, fixedSizeCells());

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            gridPatternStartSnap_scrollWithinContent_afterAFlingToTheEnd_restsWithTheContentEndAtTheViewEnd() {
        final SettleGridPatternView view =
                gridPatternView(
                        R.layout.settle_grid_pattern_start_within_content, ADAPTER_SIZE, true);

        fling(view, FLING_TO_THE_END_VELOCITY);

        assertSettled(view);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void aFling_runsTheSameFramesAndDisplacementsAsBeforeSmoothScrolling() {
        final SettleListView view = listView();
        final RecordingScrollListener scrollListener = new RecordingScrollListener();
        view.setOnScrollListener(scrollListener);

        fling(view, FLING_VELOCITY);

        assertSettled(view);
        assertThat(view.getFrameRequests()).isEqualTo(FLING_FRAME_REQUESTS);
        assertThat(scrollListener.mDisplacements).isEqualTo(FLING_DISPLACEMENTS);
    }

    @Test
    public void aDragReleaseSnap_runsTheSameFramesAndDisplacementsAsBefore() {
        final SettleListView view = listView();
        final RecordingScrollListener scrollListener = new RecordingScrollListener();
        view.setOnScrollListener(scrollListener);

        drag(view);

        assertSettled(view);
        assertThat(view.getFrameRequests()).isEqualTo(DRAG_RELEASE_SNAP_FRAME_REQUESTS);
        assertThat(scrollListener.mDisplacements).isEqualTo(DRAG_RELEASE_SNAP_DISPLACEMENTS);
    }

    @Test
    public void aTapToSnap_runsTheSameFramesAndDisplacementsAsBefore() {
        final SettleListView view = listView();
        final RecordingScrollListener scrollListener = new RecordingScrollListener();
        view.setOnScrollListener(scrollListener);
        final View secondCell = view.getChildAt(SECOND_CELL);

        tap(view, secondCell);

        assertSettled(view);
        assertThat(view.getFrameRequests()).isEqualTo(TAP_TO_SNAP_FRAME_REQUESTS);
        assertThat(scrollListener.mDisplacements).isEqualTo(TAP_TO_SNAP_DISPLACEMENTS);
    }

    @Test
    public void listCenterSnap_smoothScrollToACellOffScreen_settlesWithThatCellCentred() {
        final SettleListView view = listView();
        assertThat(view.getLayoutManager().getViewForPosition(SEVENTH_CELL)).isNull();

        smoothScroll(view, SEVENTH_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, SEVENTH_CELL)).isEqualTo(CENTRED_LIST_CELL_START);
    }

    @Test
    public void listStartSnap_smoothScrollBackToAnEarlierCell_settlesWithItAtTheStart() {
        final SettleListView view =
                listView(R.layout.settle_list_start, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);
        select(view, SEVENTH_CELL);
        assertThat(topOfPosition(view, SEVENTH_CELL)).isEqualTo(FIRST_ROW_START);

        smoothScroll(view, THIRD_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, THIRD_CELL)).isEqualTo(FIRST_ROW_START);
    }

    @Test
    public void listEndSnap_smoothScrollToALaterCell_settlesWithItsEndAtTheViewEnd() {
        final SettleListView view =
                listView(R.layout.settle_list_end, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);

        smoothScroll(view, SEVENTH_CELL);

        assertSettled(view);
        assertThat(bottomOfPosition(view, SEVENTH_CELL)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void listOnScreenSnap_smoothScrollToACellPastTheEnd_settlesWithItsEndAtTheViewEnd() {
        final SettleListView view =
                listView(R.layout.settle_list_on_screen, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);

        smoothScroll(view, SEVENTH_CELL);

        assertSettled(view);
        assertThat(bottomOfPosition(view, SEVENTH_CELL)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void
            listOnScreenSnap_smoothScrollToACellBeforeTheStart_settlesWithItsStartAtTheViewStart() {
        final SettleListView view =
                listView(R.layout.settle_list_on_screen, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);
        smoothScroll(view, SEVENTH_CELL);
        assertThat(view.getLayoutManager().getViewForPosition(THIRD_CELL)).isNull();

        smoothScroll(view, THIRD_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, THIRD_CELL)).isEqualTo(FIRST_ROW_START);
    }

    @Test
    public void gridCenterSnap_smoothScrollToAPositionInALaterRow_settlesWithThatRowCentred() {
        final SettleGridView view = gridView();

        smoothScroll(view, LAST_VIEW_OF_THE_FOURTH_ROW);

        assertSettled(view);
        assertThat(topOfPosition(view, FIRST_VIEW_OF_THE_FOURTH_ROW))
                .isEqualTo(CENTRED_LIST_CELL_START);
    }

    @Test
    public void
            gridPatternCenterSnap_smoothScrollToAPositionInALaterGroup_settlesWithThatGroupCentred() {
        final SettleGridPatternView view = nonUniformGridPatternView();
        assertThat(view.getLayoutManager().getViewForPosition(A_POSITION_IN_A_LATE_TALL_GROUP))
                .isNull();

        smoothScroll(view, A_POSITION_IN_A_LATE_TALL_GROUP);

        assertSettled(view);
        assertThat(topOfPosition(view, FIRST_POSITION_OF_THAT_TALL_GROUP))
                .isEqualTo(CENTRED_CELL_START);
    }

    @Test
    public void
            gridPatternCenterSnap_scrollingHorizontally_smoothScroll_settlesWithTheGroupCentred() {
        final SettleGridPatternView view =
                gridPatternView(R.layout.settle_grid_pattern_horizontal, ADAPTER_SIZE, false);

        smoothScroll(view, FIRST_VIEW_OF_THE_THIRD_CELL);

        assertSettled(view);
        assertThat(leftOfPosition(view, FIRST_VIEW_OF_THE_THIRD_CELL))
                .isEqualTo(HORIZONTAL_CENTRED_CELL_START);
    }

    @Test
    public void listStartSnap_scrollWithinContent_smoothScrollToTheLastCell_restsAtTheContentEnd() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_within_content,
                        WITHIN_CONTENT_CELL_SIZE,
                        ADAPTER_SIZE);

        smoothScroll(view, LAST_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, LAST_CELL)).isEqualTo(THIRD_ROW_START);
        assertThat(contentEnd(view)).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void circularScroll_smoothScrollToTheLastCellFromTheFirst_goesBackOneCell() {
        final SettleListView view =
                listView(R.layout.settle_list_circular, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);

        smoothScroll(view, LAST_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, LAST_CELL)).isEqualTo(FIRST_ROW_START);
        assertThat(topOfPosition(view, FIRST_CELL)).isEqualTo(SECOND_ROW_START);
    }

    @Test
    public void viewPager_smoothScrollToACellThreePagesAway_crossesThePages() {
        final SettleListView view =
                listView(R.layout.settle_list_view_pager, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);
        layOutAtRest(view);

        smoothScroll(view, FOURTH_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, FOURTH_CELL)).isEqualTo(FIRST_ROW_START);
    }

    @Test
    public void viewPager_smoothScrollDuringAPageFling_landsOnTheTargetWithOneSettling() {
        final SettleListView view =
                listView(R.layout.settle_list_view_pager, WITHIN_CONTENT_CELL_SIZE, ADAPTER_SIZE);
        layOutAtRest(view);
        final RecordingScrollListener scrollStates = new RecordingScrollListener();
        view.setOnScrollListener(scrollStates);
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        gestureListener.onFling(down(), moveBy(DRAG_DISTANCE), FLING_VELOCITY, FLING_VELOCITY);
        gestureListener.onUp();
        runOneFrame();
        runOneFrame();

        smoothScroll(view, SIXTH_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, SIXTH_CELL)).isEqualTo(FIRST_ROW_START);
        assertThat(scrollStates.mStates).containsExactly(ScrollState.settling, ScrollState.idle);
    }

    @Test
    public void smoothScroll_thenATouchDown_settlesOnTheNearestCell() {
        final SettleListView view = listView();
        view.smoothScrollToPosition(SEVENTH_CELL);
        runOneFrame();
        runOneFrame();
        assertThat(childTops(view)).doesNotContain(CENTRED_LIST_CELL_START);

        touch(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
    }

    @Test
    public void smoothScroll_withSelectOnSnap_selectsTheTargetOnce() {
        final SettleListView view =
                listView(
                        R.layout.settle_list_start_select_on_snap,
                        WITHIN_CONTENT_CELL_SIZE,
                        ADAPTER_SIZE);
        final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
        view.setOnItemSelectedListener(selections);

        smoothScroll(view, SEVENTH_CELL);

        assertSettled(view);
        assertThat(topOfPosition(view, SEVENTH_CELL)).isEqualTo(FIRST_ROW_START);
        assertThat(selections.mSelectionCount).isEqualTo(1);
        assertThat(selections.mLastPosition).isEqualTo(SEVENTH_CELL);
    }

    @Test
    public void smoothScroll_toTheCellAlreadyAtTheSnapPosition_requestsNoFrame() {
        final SettleListView view = listView();
        assertThat(topOfPosition(view, FIRST_CELL)).isEqualTo(CENTRED_LIST_CELL_START);

        smoothScroll(view, FIRST_CELL);

        assertThat(view.getFrameRequests()).isEqualTo(0);
        assertThat(view.getGestureListener().getState()).isEqualTo(AdapterAnimator.State.notMoving);
    }

    @Test
    public void smoothScroll_onAWrapContentViewThatRevealsALargerCell_stillSettlesOnTheTarget() {
        final SettleListView view = wrappingListView();
        assertThat(view.getWidth()).isEqualTo(NARROW_CELL_BREADTH);

        smoothScroll(view, LAST_CELL);

        assertSettled(view);
        assertThat(view.getWidth()).isEqualTo(WIDE_CELL_BREADTH);
        assertThat(topOfPosition(view, LAST_CELL)).isEqualTo(SECOND_ROW_START);
    }

    private void assertSettled(final SettleView view) {
        assertThat(view.getFrameRequests()).isLessThanOrEqualTo(FRAME_BUDGET);
        assertThat(view.getGestureListener().getState()).isEqualTo(AdapterAnimator.State.notMoving);
    }

    private List<Integer> cellStarts(final SettleGridPatternView view) {
        final List<Integer> starts = new ArrayList<Integer>();
        final int widestBreadth = widestChildBreadth(view);
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final int childBreadth = child.getRight() - child.getLeft();
            final boolean isTheCellsFirstView = childBreadth == widestBreadth;
            if (isTheCellsFirstView) starts.add(child.getTop());
        }
        return starts;
    }

    private int widestChildBreadth(final SettleGridPatternView view) {
        int widest = 0;
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final int childBreadth = child.getRight() - child.getLeft();
            if (childBreadth > widest) widest = childBreadth;
        }
        return widest;
    }

    private List<Integer> horizontalCellStarts(final SettleGridPatternView view) {
        final List<Integer> starts = new ArrayList<Integer>();
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final int childBreadth = child.getBottom() - child.getTop();
            final boolean isTheCellsFirstView = childBreadth == VIEW_SIZE;
            if (isTheCellsFirstView) starts.add(child.getLeft());
        }
        return starts;
    }

    private int contentEnd(final ViewGroup view) {
        return Collections.max(childBottoms(view));
    }

    private int horizontalContentEnd(final ViewGroup view) {
        final List<Integer> rights = new ArrayList<Integer>();
        for (int index = 0; index < view.getChildCount(); index++) {
            rights.add(view.getChildAt(index).getRight());
        }
        return Collections.max(rights);
    }

    private int topOfPosition(final ViewGroup view, final int position) {
        final View child = childOfPosition(view, position);
        return child.getTop();
    }

    private int bottomOfPosition(final ViewGroup view, final int position) {
        final View child = childOfPosition(view, position);
        return child.getBottom();
    }

    private int leftOfPosition(final ViewGroup view, final int position) {
        final View child = childOfPosition(view, position);
        return child.getLeft();
    }

    private View childOfPosition(final ViewGroup view, final int position) {
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final boolean isThePosition = child.getTag().equals(position);
            if (isThePosition) return child;
        }
        throw new IllegalStateException("position " + position + " is not laid out");
    }

    private View childAtTop(final ViewGroup view, final int top) {
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final boolean isAtTheTop = child.getTop() == top;
            if (isAtTheTop) return child;
        }
        throw new IllegalStateException("no child starts at " + top);
    }

    private List<Integer> childBottoms(final ViewGroup view) {
        final List<Integer> bottoms = new ArrayList<Integer>();
        for (int index = 0; index < view.getChildCount(); index++) {
            bottoms.add(view.getChildAt(index).getBottom());
        }
        return bottoms;
    }

    private List<Integer> childTops(final ViewGroup view) {
        final List<Integer> tops = new ArrayList<Integer>();
        for (int index = 0; index < view.getChildCount(); index++) {
            tops.add(view.getChildAt(index).getTop());
        }
        return tops;
    }

    private SettleGridPatternView centerGridPatternView(final int adapterSize) {
        return gridPatternView(R.layout.settle_grid_pattern_center, adapterSize, true);
    }

    private SettleGridPatternView gridPatternView(
            final int layoutId, final int adapterSize, final boolean isVertical) {
        final SettleGridPatternView view =
                (SettleGridPatternView) View.inflate(mActivity, layoutId, null);
        final List<GridPatternItemDefinition> definitions =
                new ArrayList<GridPatternItemDefinition>();
        if (isVertical) {
            definitions.add(new GridPatternItemDefinition(0, 0, 1, 2));
            definitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
            definitions.add(new GridPatternItemDefinition(1, 1, 1, 1));
        } else {
            definitions.add(new GridPatternItemDefinition(0, 0, 2, 1));
            definitions.add(new GridPatternItemDefinition(0, 1, 1, 1));
            definitions.add(new GridPatternItemDefinition(1, 1, 1, 1));
        }
        view.addGridPatternGroupDefinition(definitions);
        view.setAdapter(new StretchAdapter(mActivity, adapterSize));
        attachAndLayout(view);
        return view;
    }

    /**
     * Groups of two rows and groups of one row in turn, so an estimate made from the size of one
     * kind of group runs short or long of the other.
     */
    private SettleGridPatternView nonUniformGridPatternView() {
        final SettleGridPatternView view =
                (SettleGridPatternView)
                        View.inflate(mActivity, R.layout.settle_grid_pattern_center, null);
        final List<GridPatternItemDefinition> twoRows = new ArrayList<GridPatternItemDefinition>();
        twoRows.add(new GridPatternItemDefinition(0, 0, 1, 2));
        twoRows.add(new GridPatternItemDefinition(1, 0, 1, 1));
        twoRows.add(new GridPatternItemDefinition(1, 1, 1, 1));
        view.addGridPatternGroupDefinition(twoRows);
        final List<GridPatternItemDefinition> oneRow = new ArrayList<GridPatternItemDefinition>();
        oneRow.add(new GridPatternItemDefinition(0, 0, 1, 1));
        oneRow.add(new GridPatternItemDefinition(0, 1, 1, 1));
        view.addGridPatternGroupDefinition(oneRow);
        view.setAdapter(new StretchAdapter(mActivity, NON_UNIFORM_ADAPTER_SIZE));
        attachAndLayout(view);
        return view;
    }

    private SettleListView wrappingListView() {
        final SettleListView view =
                (SettleListView) View.inflate(mActivity, R.layout.settle_list_wrap_content, null);
        view.setAdapter(new WideningAdapter(mActivity, ADAPTER_SIZE));
        mContent.addView(
                view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, VIEW_SIZE));
        layOutWrapping(view);
        return view;
    }

    private void layOutWrapping(final View view) {
        final int breadthSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_BREADTH, View.MeasureSpec.AT_MOST);
        final int sizeSpec = View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY);
        view.forceLayout();
        view.measure(breadthSpec, sizeSpec);
        final int measuredBreadth = view.getMeasuredWidth();
        view.layout(0, 0, measuredBreadth, VIEW_SIZE);
    }

    private SettleListView listView() {
        return listView(R.layout.settle_list_center, LIST_CELL_SIZE, ADAPTER_SIZE);
    }

    private SettleListView tallCellListView() {
        return listView(R.layout.settle_list_tall_cells, ODD_TALL_CELL_SIZE, ADAPTER_SIZE);
    }

    private SettleListView evenTallCellListView() {
        return listView(R.layout.settle_list_tall_cells, EVEN_TALL_CELL_SIZE, ADAPTER_SIZE);
    }

    private SettleListView listView(final int layoutId, final int cellSize, final int adapterSize) {
        final SettleListView view = (SettleListView) View.inflate(mActivity, layoutId, null);
        view.setAdapter(new FixedSizeAdapter(mActivity, adapterSize, cellSize));
        attachAndLayout(view);
        return view;
    }

    private SettleGridView gridView() {
        return gridView(R.layout.settle_grid_center, new UnevenAdapter(mActivity, ADAPTER_SIZE));
    }

    private SettleGridView gridView(final int layoutId, final BaseAdapter adapter) {
        final SettleGridView view = (SettleGridView) View.inflate(mActivity, layoutId, null);
        view.setAdapter(adapter);
        attachAndLayout(view);
        return view;
    }

    private BaseAdapter fixedSizeCells() {
        return new FixedSizeAdapter(mActivity, ADAPTER_SIZE, WITHIN_CONTENT_CELL_SIZE);
    }

    private void attachAndLayout(final View view) {
        mContent.addView(view, new FrameLayout.LayoutParams(VIEW_BREADTH, VIEW_SIZE));
        layOutAtRest(view);
    }

    private void layOutAtRest(final View view) {
        final int breadthSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_BREADTH, View.MeasureSpec.EXACTLY);
        final int sizeSpec = View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY);
        view.forceLayout();
        view.measure(breadthSpec, sizeSpec);
        view.layout(0, 0, VIEW_BREADTH, VIEW_SIZE);
    }

    private void drag(final SettleView view) {
        view.reset();
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        gestureListener.onScroll(down(), moveBy(DRAG_DISTANCE), DRAG_DISTANCE, DRAG_DISTANCE);
        idleMainLooper();
        gestureListener.onUp();
        idleMainLooper();
    }

    private void fling(final SettleView view, final float velocity) {
        view.reset();
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        gestureListener.onFling(down(), moveBy(DRAG_DISTANCE), velocity, velocity);
        gestureListener.onUp();
        idleMainLooper();
    }

    // A pager measures its page on a layout taken at rest, which the framework gives a resting view
    // and this harness does not, so each page gesture starts from one.
    private void page(final SettleListView view) {
        layOutAtRest(view);
        fling(view, FLING_VELOCITY);
    }

    private void pageBack(final SettleListView view) {
        layOutAtRest(view);
        fling(view, -FLING_VELOCITY);
    }

    private void select(final SettleListView view, final int position) {
        view.setSelection(position);
        layOutAtRest(view);
    }

    private void touch(final SettleView view) {
        view.reset();
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        idleMainLooper();
    }

    private void pressAndDragBy(final SettleView view, final float distance) {
        view.reset();
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        dragOnBy(view, distance);
    }

    private void dragOnBy(final SettleView view, final float distance) {
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onScroll(down(), moveBy(distance), distance, distance);
        idleMainLooper();
    }

    private void release(final SettleView view) {
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onUp();
        idleMainLooper();
    }

    private void tap(final SettleView view, final View child) {
        view.reset();
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        gestureListener.onSingleTapUp(down(), child);
        gestureListener.onUp();
        idleMainLooper();
    }

    private void smoothScroll(final SettleView view, final int position) {
        view.reset();
        view.smoothScrollToPosition(position);
        idleMainLooper();
    }

    private void runOneFrame() {
        shadowOf(Looper.getMainLooper()).runOneTask();
    }

    private void idleMainLooper() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    private static MotionEvent down() {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 200f, 150f, 0);
    }

    private static MotionEvent moveBy(final float distance) {
        return MotionEvent.obtain(
                0, 10, MotionEvent.ACTION_MOVE, 200f - distance, 150f - distance, 0);
    }

    private static final class RecordingItemSelectedListener
            implements AdapterView.OnItemSelectedListener {
        private int mSelectionCount;
        private int mLastPosition = AdapterView.INVALID_POSITION;

        @Override
        public void onItemSelected(
                final AdapterView<?> parent, final View view, final int position, final long id) {
            mSelectionCount++;
            mLastPosition = position;
        }

        @Override
        public void onNothingSelected(final AdapterView<?> parent) {
            mLastPosition = AdapterView.INVALID_POSITION;
        }
    }

    private static final class RecordingScrollListener implements OnScrollListener {
        private final List<ScrollState> mStates = new ArrayList<ScrollState>();
        private final List<Integer> mDisplacements = new ArrayList<Integer>();

        @Override
        public void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement) {
            mDisplacements.add(displacement);
        }

        @Override
        public void onScrollStateChanged(
                final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
            mStates.add(scrollState);
        }
    }

    private interface SettleView {
        void reset();

        void smoothScrollToPosition(int position);

        int getFrameRequests();

        ChildTouchGestureListener getGestureListener();

        LayoutManager<?> getLayoutManager();
    }

    public static final class SettleGridPatternView extends GridPatternView<BaseAdapter>
            implements SettleView {
        private int mFrameRequests;
        private ChildTouchGestureListener mGestureListener;
        private LayoutManager<GridPatternGroup> mLayoutManager;

        public SettleGridPatternView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public void reset() {
            mFrameRequests = 0;
        }

        @Override
        public int getFrameRequests() {
            return mFrameRequests;
        }

        @Override
        public ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
        }

        @Override
        public LayoutManager<?> getLayoutManager() {
            return mLayoutManager;
        }

        @Override
        protected AdapterViewInitializer<GridPatternGroup> createAdapterViewInitializer(
                final Context context,
                final boolean isViewPager,
                final AdapterViewManager adapterViewManager,
                final LayoutManager<GridPatternGroup> layoutManager,
                final boolean isVerticalScroll,
                final Drawable divider,
                final int dividerSize) {
            final AdapterViewInitializer<GridPatternGroup> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll,
                            divider,
                            dividerSize);
            mGestureListener = initializer.getChildTouchListener();
            mLayoutManager = initializer.getLayoutManager();
            return initializer;
        }

        @Override
        public void requestAnimationFrame() {
            mFrameRequests++;
            if (mFrameRequests > FRAME_BUDGET) return;
            super.requestAnimationFrame();
        }
    }

    public static final class SettleListView extends ListView<BaseAdapter> implements SettleView {
        private int mFrameRequests;
        private ChildTouchGestureListener mGestureListener;
        private LayoutManager<View> mLayoutManager;

        public SettleListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public void reset() {
            mFrameRequests = 0;
        }

        @Override
        public int getFrameRequests() {
            return mFrameRequests;
        }

        @Override
        public ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
        }

        @Override
        public LayoutManager<?> getLayoutManager() {
            return mLayoutManager;
        }

        @Override
        protected AdapterViewInitializer<View> createAdapterViewInitializer(
                final Context context,
                final boolean isViewPager,
                final AdapterViewManager adapterViewManager,
                final LayoutManager<View> layoutManager,
                final boolean isVerticalScroll,
                final Drawable divider,
                final int dividerSize) {
            final AdapterViewInitializer<View> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll,
                            divider,
                            dividerSize);
            mGestureListener = initializer.getChildTouchListener();
            mLayoutManager = initializer.getLayoutManager();
            return initializer;
        }

        @Override
        public void requestAnimationFrame() {
            mFrameRequests++;
            if (mFrameRequests > FRAME_BUDGET) return;
            super.requestAnimationFrame();
        }
    }

    public static final class SettleGridView extends GridView<BaseAdapter> implements SettleView {
        private int mFrameRequests;
        private ChildTouchGestureListener mGestureListener;
        private LayoutManager<Group> mLayoutManager;

        public SettleGridView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public void reset() {
            mFrameRequests = 0;
        }

        @Override
        public int getFrameRequests() {
            return mFrameRequests;
        }

        @Override
        public ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
        }

        @Override
        public LayoutManager<?> getLayoutManager() {
            return mLayoutManager;
        }

        @Override
        protected AdapterViewInitializer<Group> createAdapterViewInitializer(
                final Context context,
                final boolean isViewPager,
                final AdapterViewManager adapterViewManager,
                final LayoutManager<Group> layoutManager,
                final boolean isVerticalScroll,
                final Drawable divider,
                final int dividerSize) {
            final AdapterViewInitializer<Group> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll,
                            divider,
                            dividerSize);
            mGestureListener = initializer.getChildTouchListener();
            mLayoutManager = initializer.getLayoutManager();
            return initializer;
        }

        @Override
        public void requestAnimationFrame() {
            mFrameRequests++;
            if (mFrameRequests > FRAME_BUDGET) return;
            super.requestAnimationFrame();
        }
    }

    private static final class StretchAdapter extends BaseAdapter {
        private final Context mContext;
        private final int mCount;

        StretchAdapter(final Context context, final int count) {
            mContext = context;
            mCount = count;
        }

        @Override
        public int getCount() {
            return mCount;
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
            final View view = viewToFill(convertView);
            view.setTag(position);
            return view;
        }

        private View viewToFill(final View convertView) {
            if (convertView != null) return convertView;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }

    private static final class FixedSizeAdapter extends BaseAdapter {
        private final Context mContext;
        private final int mCount;
        private final int mCellSize;

        FixedSizeAdapter(final Context context, final int count, final int cellSize) {
            mContext = context;
            mCount = count;
            mCellSize = cellSize;
        }

        @Override
        public int getCount() {
            return mCount;
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
            final View view = viewToFill(convertView);
            view.setTag(position);
            return view;
        }

        private View viewToFill(final View convertView) {
            if (convertView != null) return convertView;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(mCellSize, mCellSize));
            return view;
        }
    }

    private static final class UnevenAdapter extends BaseAdapter {
        private static final int SHORT_SIZE = 60;
        private static final int TALL_SIZE = 101;
        private final Context mContext;
        private final int mCount;

        UnevenAdapter(final Context context, final int count) {
            mContext = context;
            mCount = count;
        }

        @Override
        public int getCount() {
            return mCount;
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
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(final int position) {
            return position % 2;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View view = viewToFill(position, convertView);
            view.setTag(position);
            return view;
        }

        private View viewToFill(final int position, final View convertView) {
            if (convertView != null) return convertView;
            final boolean isTall = position % 2 == 0;
            final int size = isTall ? TALL_SIZE : SHORT_SIZE;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            return view;
        }
    }

    /** Cells of one height whose breadth doubles from the seventh cell on. */
    private static final class WideningAdapter extends BaseAdapter {
        private static final int NARROW_VIEW_TYPE = 0;
        private static final int WIDE_VIEW_TYPE = 1;
        private static final int VIEW_TYPE_COUNT = 2;
        private final Context mContext;
        private final int mCount;

        WideningAdapter(final Context context, final int count) {
            mContext = context;
            mCount = count;
        }

        @Override
        public int getCount() {
            return mCount;
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
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(final int position) {
            if (isWide(position)) return WIDE_VIEW_TYPE;
            return NARROW_VIEW_TYPE;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View view = viewToFill(position, convertView);
            view.setTag(position);
            return view;
        }

        private View viewToFill(final int position, final View convertView) {
            if (convertView != null) return convertView;
            final int breadth = breadthOf(position);
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(breadth, WITHIN_CONTENT_CELL_SIZE));
            return view;
        }

        private static int breadthOf(final int position) {
            if (isWide(position)) return WIDE_CELL_BREADTH;
            return NARROW_CELL_BREADTH;
        }

        private static boolean isWide(final int position) {
            return position >= FIRST_WIDE_CELL;
        }
    }
}
