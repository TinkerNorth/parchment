// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CellDividerTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int VIEW_SIZE = 100;
    private static final int CELL_SPACING = 10;
    private static final int NO_CELL_SPACING = 0;
    private static final int DIVIDER_SIZE = 4;
    private static final int THICK_DIVIDER_SIZE = 20;
    private static final int INTRINSIC_BREADTH = 7;
    private static final int INTRINSIC_THICKNESS = 3;
    private static final int START_BREADTH_PADDING = 20;
    private static final int END_BREADTH_PADDING = 40;
    private static final int START_SIZE_PADDING = 30;
    private static final int END_SIZE_PADDING = 50;
    private static final int BREADTH_END = VIEW_GROUP_SIZE - END_BREADTH_PADDING;
    private static final boolean IS_VERTICAL = true;
    private static final boolean IS_HORIZONTAL = false;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final int SIX_CELLS = 6;
    private static final int THREE_CELLS = 3;
    private static final int ONE_CELL = 1;
    private static final int NO_CELLS = 0;
    private static final int FORWARD_SCROLL = -150;
    private static final int SCROLL_PAST_THE_FIRST_CELL = -105;
    private static final int SCROLL_CLEAR_OF_THE_FIRST_CELL = -110;
    private static final int FOUR_CELLS = 4;
    private static final int NO_DIVIDER_SIZE = 0;
    private static final int NEGATIVE_DIVIDER_SIZE = -4;
    private static final int LAST_DRAWN_CELL_INDEX = 2;
    private static final int FIRST_ADAPTER_POSITION = 0;
    private static final boolean NOT_SNAP_TO_POSITION = false;
    private static final boolean NOT_A_VIEW_PAGER = false;
    private static final int VIEWPORT_VIEW_PAGER_INTERVAL = 0;
    private static final boolean NOT_SELECT_ON_SNAP = false;
    private static final boolean NOT_SELECT_WHILE_SCROLLING = false;
    private static final int GAP_START = 100;
    private static final int GAP_END = 110;
    private static final int GAP_START_ACROSS_THE_ORIGIN = -5;
    private static final int GAP_END_ACROSS_THE_ORIGIN = 4;
    private static final int ODD_DIVIDER_SIZE = 3;
    private static final int OVERLAPPING_GAP_START = 110;
    private static final int OVERLAPPING_GAP_END = 100;
    private static final int NEGATIVE_CELL_SPACING = -10;
    private static final int FEWER_CELLS = 2;

    @Test
    public void dividerStart_inAGapAwayFromTheOrigin_centresTheDividerInTheGap() {
        final int dividerStart = CellDivider.getDividerStart(GAP_START, GAP_END, DIVIDER_SIZE);

        assertThat(dividerStart).isEqualTo(103);
    }

    @Test
    public void dividerStart_inAGapThatStartsBeforeTheOrigin_centresTheDividerInTheGap() {
        final int dividerStart =
                CellDivider.getDividerStart(
                        GAP_START_ACROSS_THE_ORIGIN, GAP_END_ACROSS_THE_ORIGIN, DIVIDER_SIZE);

        assertThat(dividerStart).isEqualTo(-3);
    }

    @Test
    public void dividerStart_withADividerWiderThanTheGap_overlapsBothCellsEvenly() {
        final int dividerStart =
                CellDivider.getDividerStart(GAP_START, GAP_END, THICK_DIVIDER_SIZE);

        assertThat(dividerStart).isEqualTo(95);
    }

    @Test
    public void dividerStart_withAnOddDividerSize_roundsTheBandTowardTheGapEnd() {
        final int dividerStart = CellDivider.getDividerStart(GAP_START, GAP_END, ODD_DIVIDER_SIZE);

        assertThat(dividerStart).isEqualTo(104);
    }

    @Test
    public void dividerStart_inAGapThatRunsBackwards_staysBetweenTheTwoCells() {
        final int dividerStart =
                CellDivider.getDividerStart(
                        OVERLAPPING_GAP_START, OVERLAPPING_GAP_END, DIVIDER_SIZE);

        assertThat(dividerStart).isEqualTo(103);
    }

    @Test
    public void divider_withANegativeCellSpacing_isDrawnWhereTheCellsOverlap() {
        final Harness harness = new Harness(IS_VERTICAL, NEGATIVE_CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 93, VIEW_GROUP_SIZE, 97));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 183, VIEW_GROUP_SIZE, 187));
    }

    @Test
    public void divider_afterTheAdapterShrinks_isDrawnOnceFewerThanTheRemainingCells() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);
        harness.draw();
        assertThat(divider.getDrawCount()).isEqualTo(2);

        harness.layout(FEWER_CELLS);
        harness.draw();

        assertThat(harness.getDrawnCellCount()).isEqualTo(FEWER_CELLS);
        assertThat(divider.getDrawCount()).isEqualTo(3);
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(0, 148, VIEW_GROUP_SIZE, 152));
    }

    @Test
    public void divider_betweenThreeDrawnCells_isDrawnOnceFewerThanTheCells() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(harness.getDrawnCellCount()).isEqualTo(THREE_CELLS);
        assertThat(divider.getDrawCount()).isEqualTo(2);
    }

    @Test
    public void divider_inAVerticalList_isCentredInTheGapAndSpansTheBreadth() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 103, VIEW_GROUP_SIZE, 107));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 213, VIEW_GROUP_SIZE, 217));
    }

    @Test
    public void divider_inAHorizontalList_isCentredInTheGapAndSpansTheBreadth() {
        final Harness harness = new Harness(IS_HORIZONTAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(103, 0, 107, VIEW_GROUP_SIZE));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(213, 0, 217, VIEW_GROUP_SIZE));
    }

    @Test
    public void divider_inAVerticalListWithPadding_staysInsideTheBreadthPadding() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.setPadding();
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0))
                .isEqualTo(new Rect(START_BREADTH_PADDING, 133, BREADTH_END, 137));
        assertThat(divider.getDrawnBounds(1))
                .isEqualTo(new Rect(START_BREADTH_PADDING, 243, BREADTH_END, 247));
    }

    @Test
    public void divider_inAHorizontalListWithPadding_staysInsideTheBreadthPadding() {
        final Harness harness = new Harness(IS_HORIZONTAL, CELL_SPACING, NOT_CIRCULAR);
        harness.setPadding();
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0))
                .isEqualTo(new Rect(133, START_BREADTH_PADDING, 137, BREADTH_END));
        assertThat(divider.getDrawnBounds(1))
                .isEqualTo(new Rect(243, START_BREADTH_PADDING, 247, BREADTH_END));
    }

    @Test
    public void dividerSizeOfZero_drawsNothing() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider =
                new RecordingDrawable(INTRINSIC_BREADTH, INTRINSIC_THICKNESS);
        harness.setDivider(divider, NO_DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void aNegativeDividerSize_drawsNothing() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider =
                new RecordingDrawable(INTRINSIC_BREADTH, INTRINSIC_THICKNESS);
        harness.setDivider(divider, NEGATIVE_DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void divider_whenACellScrollsOffTheStart_isStillDrawnWhileItsGapIsOnScreen() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        harness.scroll(SCROLL_PAST_THE_FIRST_CELL);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(harness.getDrawnCellCount()).isEqualTo(FOUR_CELLS);
        assertThat(divider.getDrawCount()).isEqualTo(3);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, -2, VIEW_GROUP_SIZE, 2));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 108, VIEW_GROUP_SIZE, 112));
        assertThat(divider.getDrawnBounds(2)).isEqualTo(new Rect(0, 218, VIEW_GROUP_SIZE, 222));
    }

    @Test
    public void divider_whenACellScrollsClearOfTheStart_isNotDrawnAboveTheFirstDrawnCell() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        harness.scroll(SCROLL_CLEAR_OF_THE_FIRST_CELL);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(harness.getDrawnCellCount()).isEqualTo(THREE_CELLS);
        assertThat(divider.getDrawCount()).isEqualTo(2);
        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 103, VIEW_GROUP_SIZE, 107));
    }

    @Test
    public void dividerSizeAbsent_inAVerticalList_takesTheIntrinsicHeight() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider =
                new RecordingDrawable(INTRINSIC_BREADTH, INTRINSIC_THICKNESS);
        harness.setDivider(divider, CellDivider.INTRINSIC_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 104, VIEW_GROUP_SIZE, 107));
    }

    @Test
    public void dividerSizeAbsent_inAHorizontalList_takesTheIntrinsicWidth() {
        final Harness harness = new Harness(IS_HORIZONTAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider =
                new RecordingDrawable(INTRINSIC_BREADTH, INTRINSIC_THICKNESS);
        harness.setDivider(divider, CellDivider.INTRINSIC_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(102, 0, 109, VIEW_GROUP_SIZE));
    }

    @Test
    public void dividerSizeAbsent_withADrawableThatHasNoIntrinsicSize_drawsNothing() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, CellDivider.INTRINSIC_SIZE);

        harness.draw();

        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void dividerSizeSet_overridesTheDrawablesIntrinsicSize() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider =
                new RecordingDrawable(INTRINSIC_BREADTH, INTRINSIC_THICKNESS);
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 103, VIEW_GROUP_SIZE, 107));
    }

    @Test
    public void dividerThickerThanTheCellSpacing_overlapsBothCellsEvenly() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, THICK_DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 95, VIEW_GROUP_SIZE, 115));
    }

    @Test
    public void dividerWithNoCellSpacing_isCentredOnTheBoundaryBetweenTheCells() {
        final Harness harness = new Harness(IS_VERTICAL, NO_CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(divider.getDrawnBounds(0)).isEqualTo(new Rect(0, 98, VIEW_GROUP_SIZE, 102));
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 198, VIEW_GROUP_SIZE, 202));
    }

    @Test
    public void divider_withCircularScroll_isDrawnBetweenTheLastAndTheFirstCell() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, CIRCULAR);
        harness.layout(THREE_CELLS);
        harness.scroll(FORWARD_SCROLL);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(harness.getAdapterPositionOfDrawnCell(LAST_DRAWN_CELL_INDEX))
                .isEqualTo(FIRST_ADAPTER_POSITION);
        assertThat(divider.getDrawCount()).isEqualTo(2);
        assertThat(divider.getDrawnBounds(1)).isEqualTo(new Rect(0, 173, VIEW_GROUP_SIZE, 177));
    }

    @Test
    public void divider_withASingleCell_drawsNothing() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(ONE_CELL);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(harness.getDrawnCellCount()).isEqualTo(ONE_CELL);
        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void divider_withAnEmptyAdapter_drawsNothing() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(NO_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();

        assertThat(harness.getDrawnCellCount()).isEqualTo(NO_CELLS);
        assertThat(divider.getDrawCount()).isEqualTo(0);
    }

    @Test
    public void everyDividerOfEveryFrame_isMeasuredIntoTheSameBoundsRect() {
        final Harness harness = new Harness(IS_VERTICAL, CELL_SPACING, NOT_CIRCULAR);
        harness.layout(SIX_CELLS);
        final RecordingDrawable divider = new RecordingDrawable();
        harness.setDivider(divider, DIVIDER_SIZE);

        harness.draw();
        harness.draw();

        final List<Rect> boundsInstances = divider.getBoundsInstances();
        assertThat(boundsInstances).hasSize(4);
        for (final Rect bounds : boundsInstances) {
            assertThat(bounds).isSameAs(boundsInstances.get(0));
        }
    }

    private static final class Harness {
        private final boolean mIsVertical;
        private final Canvas mCanvas = new Canvas();
        private final MyViewGroup mViewGroup =
                new MyViewGroup(ApplicationProvider.getApplicationContext());
        private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
        private final TestAdapter mTestAdapter = new TestAdapter(VIEW_SIZE);
        private final LayoutManager<View> mLayoutManager;
        private final Animation mAnimation = new Animation();
        private CellDivider mCellDivider;

        private Harness(
                final boolean isVertical, final int cellSpacing, final boolean isCircularScroll) {
            mIsVertical = isVertical;
            final LayoutManagerAttributes attributes =
                    new LayoutManagerAttributes(
                            isCircularScroll,
                            NOT_SNAP_TO_POSITION,
                            NOT_A_VIEW_PAGER,
                            VIEWPORT_VIEW_PAGER_INTERVAL,
                            SnapPosition.onScreen,
                            cellSpacing,
                            NOT_SELECT_ON_SNAP,
                            NOT_SELECT_WHILE_SCROLLING,
                            isVertical);
            mLayoutManager =
                    new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
            mAdapterViewManager.setAdapter(mTestAdapter);
        }

        private void setPadding() {
            if (mIsVertical) {
                setVerticalPadding();
            } else {
                setHorizontalPadding();
            }
        }

        private void setVerticalPadding() {
            mViewGroup.setPadding(
                    START_BREADTH_PADDING,
                    START_SIZE_PADDING,
                    END_BREADTH_PADDING,
                    END_SIZE_PADDING);
        }

        private void setHorizontalPadding() {
            mViewGroup.setPadding(
                    START_SIZE_PADDING,
                    START_BREADTH_PADDING,
                    END_SIZE_PADDING,
                    END_BREADTH_PADDING);
        }

        private void layout(final int adapterSize) {
            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
            mViewGroup.measure(measureSpec, measureSpec);
            mViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
            mTestAdapter.setAdapterSize(adapterSize);
            mAnimation.newAnimation();
            mLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        }

        private void scroll(final int displacement) {
            mAnimation.newAnimation();
            mAnimation.setDisplacement(displacement);
            mLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        }

        private void setDivider(final Drawable divider, final int dividerSize) {
            final ScrollDirectionManager scrollDirectionManager =
                    mLayoutManager.getScrollDirectionManager();
            mCellDivider = new CellDivider(divider, dividerSize, scrollDirectionManager);
        }

        private void draw() {
            mCellDivider.draw(mCanvas, mViewGroup, mLayoutManager);
        }

        private int getDrawnCellCount() {
            return mLayoutManager.getDrawnCellCount();
        }

        private int getAdapterPositionOfDrawnCell(final int cellIndex) {
            final ScrollDirectionManager scrollDirectionManager =
                    mLayoutManager.getScrollDirectionManager();
            final int cellStart = mLayoutManager.getDrawnCellStart(cellIndex);
            for (final View view : mViewGroup.mViews) {
                final int viewStart = scrollDirectionManager.getViewStart(view);
                if (viewStart == cellStart) return mLayoutManager.getPosition(view);
            }
            return LayoutManager.INVALID_POSITION;
        }
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
