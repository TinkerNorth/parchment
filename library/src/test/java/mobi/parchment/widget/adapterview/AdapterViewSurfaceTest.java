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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.util.ArrayList;
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

/**
 * What the inherited {@code android.widget.AdapterView} counts answer: {@code getCount()}, {@code
 * getFirstVisiblePosition()} and {@code getLastVisiblePosition()}, across the three views, both
 * orientations, padding, cell spacing, circular scrolling and every way an adapter can change.
 *
 * <p>A real {@code android.widget.ListView} is laid out beside the Parchment one wherever the two
 * are meant to agree, so the rule is pinned against the widget rather than against its
 * documentation.
 */
@RunWith(RobolectricTestRunner.class)
public class AdapterViewSurfaceTest {

    private static final int VIEW_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int PADDING = 30;
    private static final int NO_ITEMS = 0;
    private static final int NO_CHILDREN = 0;
    private static final int INVALID_POSITION = android.widget.AdapterView.INVALID_POSITION;
    private static final int FIRST_CHILD = 0;

    private Activity mActivity;
    private FrameLayout mContent;

    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(Activity.class).setup().get();
        mContent = new FrameLayout(mActivity);
        mActivity.setContentView(mContent);
    }

    @Test
    public void getCount_withAnAdapter_isTheAdaptersCount() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 3);

        assertThat(listView.getAdapter().getCount()).isEqualTo(3);
        assertThat(listView.getCount()).isEqualTo(3);
    }

    @Test
    public void getCount_withNoAdapter_isZero() {
        final SurfaceListView listView = inflateListView(R.layout.surface_list_view);
        attach(listView);
        layOut(listView);

        assertThat(listView.getAdapter()).isNull();
        assertThat(listView.getCount()).isEqualTo(NO_ITEMS);
    }

    @Test
    public void getCount_withNoAdapterAndNoLayout_isZero() {
        final SurfaceListView listView = new SurfaceListView(mActivity, null);

        assertThat(listView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void getCount_afterADataSetChange_followsTheAdapter() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);
        assertThat(listView.getCount()).isEqualTo(3);

        adapter.setCount(7);
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(7);
    }

    @Test
    public void getCount_matchesThePlatformListViewThroughTheSameSequence() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        final ResizableAdapter platformAdapter = new ResizableAdapter(mActivity, 3);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);
        final android.widget.ListView platformListView = platformListView(platformAdapter);

        assertThat(listView.getCount()).isEqualTo(platformListView.getCount());

        adapter.setCount(7);
        platformAdapter.setCount(7);
        layOut(listView);
        layOut(platformListView);

        assertThat(listView.getCount()).isEqualTo(platformListView.getCount());

        adapter.setCount(0);
        platformAdapter.setCount(0);
        layOut(listView);
        layOut(platformListView);

        assertThat(listView.getCount()).isEqualTo(platformListView.getCount());
    }

    @Test
    public void theEnginesInvalidPosition_isTheFrameworksInvalidPosition() {
        assertThat(LayoutManager.INVALID_POSITION).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void anEmptyAdapter_reportsNoCountAndNoVisiblePositions() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 0);

        assertThat(listView.getChildCount()).isEqualTo(NO_CHILDREN);
        assertThat(listView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void anEmptyAdapter_reportsTheSamePairAsThePlatformListView() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 0);
        final android.widget.ListView platformListView = platformListView(0);

        assertThat(listView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aSingleItem_reportsThatItemAsFirstAndLastVisible() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 1);

        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(0);
    }

    @Test
    public void fewerItemsThanFillTheViewport_reportsEveryItemAsVisible() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 2);

        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(1);
    }

    @Test
    public void moreItemsThanFillTheViewport_leavesOutTheCellAttachedAtTheEndEdge() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);

        assertThat(listView.getChildCount()).isEqualTo(4);
        assertThat(childStart(listView, 3)).isEqualTo(VIEW_SIZE);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void onTheFirstLayout_reportsTheSamePairAsThePlatformListView() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);
        final android.widget.ListView platformListView = platformListView(10);

        assertThat(listView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aCellOnePixelPastTheStartEdge_matchesThePlatformListView() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);
        final android.widget.ListView platformListView = platformListView(10);

        animateBy(listView, -CELL_SIZE - 1);
        platformListView.scrollListBy(CELL_SIZE + 1);

        assertThat(childStart(listView, FIRST_CHILD)).isEqualTo(-1);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(4);
        assertThat(listView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aCellWithItsEndOnTheStartEdge_hasNoPixelOnScreenAndIsNotVisible() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);

        animateBy(listView, -CELL_SIZE);

        assertThat(childStart(listView, FIRST_CHILD)).isEqualTo(-CELL_SIZE);
        assertThat(childEnd(listView, FIRST_CHILD)).isEqualTo(0);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(3);
    }

    @Test
    public void aCellWithItsEndOnTheStartEdge_isWhereParchmentAndThePlatformListViewDisagree() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);
        final android.widget.ListView platformListView = platformListView(10);

        animateBy(listView, -CELL_SIZE);
        platformListView.scrollListBy(CELL_SIZE);

        assertThat(platformListView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aCellWithItsEndOnTheStartEdge_readsTheSameWhicheverWayTheContentArrived() {
        final SurfaceListView forwards = listView(R.layout.surface_list_view, 20);
        final SurfaceListView backwards = listView(R.layout.surface_list_view, 20);

        animateBy(forwards, -CELL_SIZE);
        animateBy(backwards, -2 * CELL_SIZE);
        animateBy(backwards, CELL_SIZE);

        assertThat(childStart(backwards, FIRST_CHILD))
                .isNotEqualTo(childStart(forwards, FIRST_CHILD));
        assertThat(backwards.getFirstVisiblePosition())
                .isEqualTo(forwards.getFirstVisiblePosition());
        assertThat(backwards.getLastVisiblePosition()).isEqualTo(forwards.getLastVisiblePosition());
    }

    @Test
    public void scrolledSoTheFirstCellIsPartlyOffScreen_countsThatCellAsVisible() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);

        animateBy(listView, -255);

        assertThat(childStart(listView, FIRST_CHILD)).isEqualTo(-55);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(2);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(5);
    }

    @Test
    public void aCellHeldOffScreenForItsSpacingGap_isNotCountedAsVisible() {
        final SurfaceListView listView = listView(R.layout.surface_spaced_list_view, 10);

        animateBy(listView, -110);

        assertThat(listView.getChildCount()).isEqualTo(4);
        assertThat(childEnd(listView, FIRST_CHILD)).isEqualTo(-10);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(3);
    }

    @Test
    public void atTheVeryEndOfTheContent_reportsTheLastAdapterPosition() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 5);

        animateBy(listView, -10000);

        assertThat(listView.getCount()).isEqualTo(5);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(2);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(4);
    }

    @Test
    public void withPadding_everyCellInsideThePaddingIsVisible() {
        final SurfaceListView listView = listView(R.layout.surface_padded_list_view, 10);

        assertThat(childStart(listView, FIRST_CHILD)).isEqualTo(PADDING);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void withPadding_onTheFirstLayout_matchesThePlatformListView() {
        final SurfaceListView listView = listView(R.layout.surface_padded_list_view, 10);
        final android.widget.ListView platformListView = paddedPlatformListView(10);

        assertThat(listView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void withPadding_aCellEndingInsideTheStartPadding_isNotVisible() {
        final SurfaceListView listView = listView(R.layout.surface_padded_list_view, 10);
        final android.widget.ListView platformListView = paddedPlatformListView(10);

        animateBy(listView, -110);
        platformListView.scrollListBy(110);

        assertThat(childEnd(listView, FIRST_CHILD)).isEqualTo(20);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(3);
        assertThat(listView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aVerticalListView_reportsTheSameVisibleRangeAsAHorizontalOne() {
        final SurfaceListView verticalListView = listView(R.layout.surface_vertical_list_view, 10);
        final SurfaceListView horizontalListView = listView(R.layout.surface_list_view, 10);

        assertThat(verticalListView.getChildAt(FIRST_CHILD).getTop()).isEqualTo(0);
        assertThat(verticalListView.getFirstVisiblePosition())
                .isEqualTo(horizontalListView.getFirstVisiblePosition());
        assertThat(verticalListView.getLastVisiblePosition())
                .isEqualTo(horizontalListView.getLastVisiblePosition());
    }

    @Test
    public void aVerticalListView_scrolledPastItsFirstCell_reportsTheVisibleCells() {
        final SurfaceListView verticalListView = listView(R.layout.surface_vertical_list_view, 10);
        final android.widget.ListView platformListView = platformListView(10);

        animateBy(verticalListView, -CELL_SIZE - 1);
        platformListView.scrollListBy(CELL_SIZE + 1);

        assertThat(verticalListView.getChildAt(FIRST_CHILD).getTop()).isEqualTo(-1);
        assertThat(verticalListView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(verticalListView.getLastVisiblePosition()).isEqualTo(4);
        assertThat(verticalListView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(verticalListView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aVerticalListView_measuresItsVisibleRangeInsideItsVerticalPadding() {
        final SurfaceListView verticalListView =
                listView(R.layout.surface_vertical_padded_list_view, 10);

        animateBy(verticalListView, -110);

        assertThat(verticalListView.getChildAt(FIRST_CHILD).getBottom()).isEqualTo(20);
        assertThat(verticalListView.getFirstVisiblePosition()).isEqualTo(1);
        assertThat(verticalListView.getLastVisiblePosition()).isEqualTo(3);
    }

    @Test
    public void aViewportSmallerThanItsPadding_reportsNothingVisible() {
        final SurfaceListView listView = listView(R.layout.surface_fat_padding_list_view, 10);

        assertThat(listView.getChildCount()).isGreaterThan(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aViewportSmallerThanItsPadding_reportsTheSamePairAsThePlatformListView() {
        final SurfaceListView listView = listView(R.layout.surface_fat_padding_list_view, 10);
        final android.widget.ListView platformListView = fatPaddedPlatformListView(10);

        assertThat(platformListView.getChildCount()).isEqualTo(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition())
                .isEqualTo(platformListView.getFirstVisiblePosition());
        assertThat(listView.getLastVisiblePosition())
                .isEqualTo(platformListView.getLastVisiblePosition());
    }

    @Test
    public void aViewLaidOutLargerThanItWasMeasured_readsItsViewportFromTheLayout() {
        final SurfaceListView listView = inflateListView(R.layout.surface_list_view);
        listView.setAdapter(new ResizableAdapter(mActivity, 10));
        attach(listView);

        final int unspecified = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        listView.forceLayout();
        listView.measure(unspecified, unspecified);
        listView.layout(0, 0, VIEW_SIZE, VIEW_SIZE);

        assertThat(listView.getMeasuredWidth()).isEqualTo(0);
        assertThat(listView.getWidth()).isEqualTo(VIEW_SIZE);
        assertThat(listView.getChildCount()).isGreaterThan(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void circularScroll_acrossTheWrapPoint_reportsRealAdapterPositions() {
        final SurfaceListView listView = listView(R.layout.surface_circular_list_view, 4);

        animateBy(listView, -250);

        assertThat(listView.getChildCount()).isEqualTo(4);
        assertThat(listView.getPositionForView(listView.getChildAt(FIRST_CHILD))).isEqualTo(2);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(2);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(1);
    }

    @Test
    public void circularScroll_neverReportsAPositionOutsideTheAdapter() {
        final SurfaceListView listView = listView(R.layout.surface_circular_list_view, 4);

        for (int step = 0; step < 12; step++) {
            animateBy(listView, -CELL_SIZE - 5);

            assertThat(listView.getFirstVisiblePosition()).isBetween(0, listView.getCount() - 1);
            assertThat(listView.getLastVisiblePosition()).isBetween(0, listView.getCount() - 1);
        }
    }

    @Test
    public void circularScroll_scrolledBackwards_neverReportsAPositionOutsideTheAdapter() {
        final SurfaceListView listView = listView(R.layout.surface_circular_list_view, 4);

        for (int step = 0; step < 12; step++) {
            animateBy(listView, CELL_SIZE + 5);

            assertThat(listView.getFirstVisiblePosition()).isBetween(0, listView.getCount() - 1);
            assertThat(listView.getLastVisiblePosition()).isBetween(0, listView.getCount() - 1);
        }
    }

    @Test
    public void aGridView_reportsThePositionsInsideTheVisibleCellsNotTheChildren() {
        final SurfaceGridView gridView = gridView(12);

        assertThat(gridView.getChildCount()).isEqualTo(8);
        assertThat(gridView.getCount()).isEqualTo(12);
        assertThat(gridView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(gridView.getLastVisiblePosition()).isEqualTo(5);
    }

    @Test
    public void aGridView_scrolledPastItsFirstCell_reportsTheFirstPositionInTheVisibleCell() {
        final SurfaceGridView gridView = gridView(12);

        animateBy(gridView, -155);

        assertThat(gridView.getFirstVisiblePosition()).isEqualTo(2);
        assertThat(gridView.getLastVisiblePosition()).isEqualTo(9);
    }

    @Test
    public void aGridView_withAPartlyFilledLastCell_reportsTheLastRealAdapterPosition() {
        final SurfaceGridView gridView = gridView(5);

        assertThat(gridView.getCount()).isEqualTo(5);
        assertThat(gridView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(gridView.getLastVisiblePosition()).isEqualTo(4);
    }

    @Test
    public void aGridView_withASingleItem_reportsThatItem() {
        final SurfaceGridView gridView = gridView(1);

        assertThat(gridView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(gridView.getLastVisiblePosition()).isEqualTo(0);
    }

    @Test
    public void aGridView_withAnEmptyAdapter_reportsNoVisiblePositions() {
        final SurfaceGridView gridView = gridView(0);

        assertThat(gridView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(gridView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(gridView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aGridPatternView_reportsThePositionsInsideTheVisibleCells() {
        final SurfaceGridPatternView patternView = gridPatternView(12);

        assertThat(patternView.getChildCount()).isEqualTo(6);
        assertThat(childStart(patternView, 4)).isEqualTo(VIEW_SIZE);
        assertThat(patternView.getCount()).isEqualTo(12);
        assertThat(patternView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(patternView.getLastVisiblePosition()).isEqualTo(3);
    }

    @Test
    public void aGridPatternView_scrolledPastItsFirstGroup_reportsTheVisibleGroupsPositions() {
        final SurfaceGridPatternView patternView = gridPatternView(12);

        animateBy(patternView, -155);

        assertThat(childStart(patternView, FIRST_CHILD)).isEqualTo(-5);
        assertThat(patternView.getFirstVisiblePosition()).isEqualTo(2);
        assertThat(patternView.getLastVisiblePosition()).isEqualTo(7);
    }

    @Test
    public void aGridPatternView_withASingleItem_reportsThatItem() {
        final SurfaceGridPatternView patternView = gridPatternView(1);

        assertThat(patternView.getCount()).isEqualTo(1);
        assertThat(patternView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(patternView.getLastVisiblePosition()).isEqualTo(0);
    }

    @Test
    public void aGridPatternView_withAPartlyFilledLastGroup_reportsTheLastRealAdapterPosition() {
        final SurfaceGridPatternView patternView = gridPatternView(3);

        assertThat(patternView.getCount()).isEqualTo(3);
        assertThat(patternView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(patternView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void aGridPatternView_withAnEmptyAdapter_reportsNoVisiblePositions() {
        final SurfaceGridPatternView patternView = gridPatternView(0);

        assertThat(patternView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(patternView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(patternView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aGridPatternView_afterADataSetChange_redrawsAndReportsTheNewPositions() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 12);
        final SurfaceGridPatternView patternView = gridPatternView(adapter);
        animateBy(patternView, -10000);
        final int firstBeforeTheChange = patternView.getFirstVisiblePosition();

        adapter.setCount(4);
        layOut(patternView);

        assertThat(firstBeforeTheChange).isGreaterThan(0);
        assertThat(patternView.getCount()).isEqualTo(4);
        assertThat(patternView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(patternView.getLastVisiblePosition()).isEqualTo(3);
    }

    @Test
    public void aDataSetChangeThatShrinksTheAdapter_reportsPositionsInsideTheNewCount() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 10);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);
        animateBy(listView, -10000);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(7);

        adapter.setCount(2);
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(2);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(1);
    }

    @Test
    public void aDataSetChangeThatEmptiesTheAdapter_reportsNoVisiblePositions() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 5);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);

        adapter.setCount(0);
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(listView.getChildCount()).isEqualTo(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aDataSetChangeThatRefillsTheAdapter_reportsTheNewPositions() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 0);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);

        adapter.setCount(10);
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(10);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void aDetachedView_reportsNoVisiblePositions() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);

        mContent.removeView(listView);

        assertThat(listView.isAttachedToWindow()).isFalse();
        assertThat(listView.getCount()).isEqualTo(10);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aReAttachedView_reportsItsVisiblePositionsAgain() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);
        mContent.removeView(listView);

        attach(listView);
        layOut(listView);

        assertThat(listView.isAttachedToWindow()).isTrue();
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void setAdapterToNull_reportsNothingVisibleEvenThoughTheEngineKeepsItsCells() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 5);

        listView.setAdapter(null);
        layOut(listView);

        assertThat(listView.getAdapter()).isNull();
        assertThat(listView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(listView.getChildCount()).isGreaterThan(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void replacingTheAdapterWithOneThatIsShorterThanTheDrawnCells_reportsNothingVisible() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 20);
        animateBy(listView, -1000);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(10);

        listView.setAdapter(new ResizableAdapter(mActivity, 3));
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(3);
        assertThat(listView.getChildCount()).isGreaterThan(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void replacingTheAdapterWithOneThatHasOnlySomeOfTheDrawnCells_stopsAtTheCount() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 20);
        animateBy(listView, -1000);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(10);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(12);

        listView.setAdapter(new ResizableAdapter(mActivity, 12));
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(12);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(10);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aDataSetChangeAfterAReAttach_reachesTheLayoutEngine() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 10);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);
        mContent.removeView(listView);
        attach(listView);
        layOut(listView);

        adapter.setCount(2);
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(2);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(1);
    }

    @Test
    public void aDataSetChangeThatEmptiesTheAdapterAfterAReAttach_reachesTheLayoutEngine() {
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 10);
        final SurfaceListView listView = listView(R.layout.surface_list_view, adapter);
        mContent.removeView(listView);
        attach(listView);
        layOut(listView);

        adapter.setCount(0);
        layOut(listView);

        assertThat(listView.getCount()).isEqualTo(NO_ITEMS);
        assertThat(listView.getChildCount()).isEqualTo(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void aDetachedView_reportsNothingVisible_whereAPlatformListViewKeepsItsChildren() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);
        final android.widget.ListView platformListView = platformListView(10);

        mContent.removeView(listView);
        mContent.removeView(platformListView);

        assertThat(listView.getChildCount()).isGreaterThan(NO_CHILDREN);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(INVALID_POSITION);
        assertThat(platformListView.getLastVisiblePosition()).isEqualTo(2);
    }

    @Test
    public void setSelection_doesNotChangeWhatIsReportedAsVisible() {
        final SurfaceListView listView = listView(R.layout.surface_list_view, 10);

        listView.setSelection(1);
        layOut(listView);

        assertThat(listView.getSelectedItemPosition()).isEqualTo(1);
        assertThat(listView.getFirstVisiblePosition()).isEqualTo(0);
        assertThat(listView.getLastVisiblePosition()).isEqualTo(2);
    }

    private SurfaceListView inflateListView(final int layoutId) {
        return (SurfaceListView) View.inflate(mActivity, layoutId, null);
    }

    private void attach(final View view) {
        mContent.addView(view, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
    }

    private SurfaceListView listView(final int layoutId, final int count) {
        return listView(layoutId, new ResizableAdapter(mActivity, count));
    }

    private SurfaceListView listView(final int layoutId, final ResizableAdapter adapter) {
        final SurfaceListView listView = inflateListView(layoutId);
        listView.setAdapter(adapter);
        attach(listView);
        layOut(listView);
        return listView;
    }

    private android.widget.ListView platformListView(final int count) {
        return platformListView(new ResizableAdapter(mActivity, count));
    }

    private android.widget.ListView platformListView(final ResizableAdapter adapter) {
        final android.widget.ListView platformListView = new android.widget.ListView(mActivity);
        platformListView.setDividerHeight(0);
        platformListView.setAdapter(adapter);
        attach(platformListView);
        layOut(platformListView);
        return platformListView;
    }

    private android.widget.ListView paddedPlatformListView(final int count) {
        final android.widget.ListView platformListView = new android.widget.ListView(mActivity);
        platformListView.setDividerHeight(0);
        platformListView.setPadding(0, PADDING, 0, PADDING);
        platformListView.setAdapter(new ResizableAdapter(mActivity, count));
        attach(platformListView);
        layOut(platformListView);
        return platformListView;
    }

    private android.widget.ListView fatPaddedPlatformListView(final int count) {
        final android.widget.ListView platformListView = new android.widget.ListView(mActivity);
        platformListView.setDividerHeight(0);
        platformListView.setPadding(0, 200, 0, 200);
        platformListView.setAdapter(new ResizableAdapter(mActivity, count));
        attach(platformListView);
        layOut(platformListView);
        return platformListView;
    }

    private SurfaceGridView gridView(final int count) {
        final SurfaceGridView gridView =
                (SurfaceGridView) View.inflate(mActivity, R.layout.surface_grid_view, null);
        gridView.setAdapter(new ResizableAdapter(mActivity, count));
        attach(gridView);
        layOut(gridView);
        return gridView;
    }

    private SurfaceGridPatternView gridPatternView(final int count) {
        return gridPatternView(new ResizableAdapter(mActivity, count));
    }

    private SurfaceGridPatternView gridPatternView(final ResizableAdapter adapter) {
        final SurfaceGridPatternView patternView =
                (SurfaceGridPatternView)
                        View.inflate(mActivity, R.layout.surface_grid_pattern_view, null);
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
        itemDefinitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
        patternView.addGridPatternGroupDefinition(itemDefinitions);
        patternView.setAdapter(adapter);
        attach(patternView);
        layOut(patternView);
        return patternView;
    }

    private static int childStart(final ViewGroup viewGroup, final int index) {
        return viewGroup.getChildAt(index).getLeft();
    }

    private static int childEnd(final ViewGroup viewGroup, final int index) {
        return viewGroup.getChildAt(index).getRight();
    }

    private static void animateBy(final SurfaceView view, final int distance) {
        view.getGestureListener().setAnimateToDistance(distance);
        shadowOf(Looper.getMainLooper()).idle();
    }

    private static void layOut(final View view) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY);
        view.forceLayout();
        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, VIEW_SIZE, VIEW_SIZE);
    }

    private interface SurfaceView {
        ChildTouchGestureListener getGestureListener();
    }

    public static final class SurfaceListView extends ListView<BaseAdapter> implements SurfaceView {
        private ChildTouchGestureListener mGestureListener;

        public SurfaceListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
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
            return initializer;
        }
    }

    public static final class SurfaceGridView extends GridView<BaseAdapter> implements SurfaceView {
        private ChildTouchGestureListener mGestureListener;

        public SurfaceGridView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
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
            return initializer;
        }
    }

    public static final class SurfaceGridPatternView extends GridPatternView<BaseAdapter>
            implements SurfaceView {
        private ChildTouchGestureListener mGestureListener;

        public SurfaceGridPatternView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override
        public ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
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
            return initializer;
        }
    }

    private static final class ResizableAdapter extends BaseAdapter {
        private final Context mContext;
        private int mCount;

        private ResizableAdapter(final Context context, final int count) {
            mContext = context;
            mCount = count;
        }

        private void setCount(final int count) {
            mCount = count;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(final int position) {
            return "item" + position;
        }

        @Override
        public long getItemId(final int position) {
            return position * 10L;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            return viewToFill(convertView);
        }

        private View viewToFill(final View convertView) {
            if (convertView != null) return convertView;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(CELL_SIZE, CELL_SIZE));
            return view;
        }
    }
}
