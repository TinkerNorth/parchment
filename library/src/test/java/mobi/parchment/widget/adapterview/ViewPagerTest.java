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
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ViewPagerTest {

    public static final int VIEW_GROUP_SIZE = 100;
    public static final int VIEW_SIZE = 100;
    public static final int CELL_SPACING = 10;

    private static final int THREE_CELL_VIEWPORT = 300;
    private static final int TWO_AND_A_HALF_CELL_VIEWPORT = 250;
    private static final int SMALL_CELL_SIZE = 100;
    private static final int LARGE_CELL_SIZE = 250;
    private static final int SMALL_VIEWPORT = 100;
    private static final int NO_CELL_SPACING = 0;
    private static final int ONE_CELL_PER_GESTURE = 1;
    private static final int THREE_CELLS_PER_GESTURE = 3;
    private static final int MORE_CELLS_THAN_THE_ADAPTER_HAS = 10;
    private static final int NO_INTERVAL = 0;
    private static final int NEGATIVE_INTERVAL = -2;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final int TEN_CELLS = 10;
    private static final int FOUR_CELLS = 4;
    private static final int START_OF_THE_VIEWPORT = 0;
    private static final int NOT_DRAWN = Integer.MIN_VALUE;
    private static final int NO_VIEWPORT_PADDING = 0;
    private static final int ENORMOUS_INTERVAL = Integer.MAX_VALUE;
    private static final int HUGE_INTERVAL = 30000000;
    private static final int VIEWPORT_PADDING = 20;

    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    LayoutManagerAttributes attributes;
    ListLayoutManager listLayoutManager;

    @Before
    public void setup() {
        attributes =
                new LayoutManagerAttributes(
                        true,
                        true,
                        true,
                        0,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        true,
                        true,
                        false);
        listLayoutManager = new ListLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout(VIEW_GROUP_SIZE);
    }

    @Test
    public void doesPageCorrectlyDown() {
        mTestAdapter.setAdapterSize(10);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        final int displacement = -10060;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        final View currentView = mViewGroup.mViews.get(0);
        assertThat(currentView.getTop()).isEqualTo(0);
        assertThat(currentView.getTag()).isEqualTo(1);
        assertThat(currentView.getBottom()).isEqualTo(100);
    }

    @Test
    public void doesPageCorrectlyUpWithCircularScroll() {
        mTestAdapter.setAdapterSize(10);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        final int displacement = 10060;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        final View currentView = mViewGroup.mViews.get(0);
    }

    @Test
    public void doesPageCorrectlyUp() {
        mTestAdapter.setAdapterSize(10);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        final int displacement = -60;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        final View currentView = mViewGroup.mViews.get(0);
        assertThat(currentView.getTop()).isEqualTo(0);
        assertThat(currentView.getBottom()).isEqualTo(100);
    }

    @Test
    public void viewPagerGesture_withThreeCellsOnScreen_advancesOneCellForward() {
        final Pager pager = equalCellPager(ONE_CELL_PER_GESTURE, NOT_CIRCULAR);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withThreeCellsOnScreen_advancesOneCellBack() {
        final Pager pager = equalCellPager(ONE_CELL_PER_GESTURE, NOT_CIRCULAR);

        pager.startGesture();
        pager.page(Move.forward);
        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(2)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.back)).isEqualTo(SMALL_CELL_SIZE);

        pager.page(Move.back);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withAnIntervalOfThree_advancesThreeCells() {
        final Pager pager =
                new Pager(
                        TWO_AND_A_HALF_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        THREE_CELLS_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward))
                .isEqualTo(-THREE_CELLS_PER_GESTURE * SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(3)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withCellsOfUnequalSize_advancesByEachCellsOwnSize() {
        final int[] cellSizes = new int[] {50, 75, 100, 125, 150, 175, 200, 225, 250, 275};
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        cellSizes,
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-50);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-75);

        pager.page(Move.forward);

        assertThat(pager.startOf(2)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withACellLargerThanTheViewport_advancesOneCell() {
        final Pager pager =
                new Pager(
                        SMALL_VIEWPORT,
                        equalSizes(LARGE_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-LARGE_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withCellSpacing_advancesOneCellAndOneSpacing() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-(SMALL_CELL_SIZE + CELL_SPACING));

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_startingPartWayThroughACell_landsOnACellBoundary() {
        final Pager pager = equalCellPager(ONE_CELL_PER_GESTURE, NOT_CIRCULAR);

        pager.startGesture();
        pager.page(Move.forward);
        pager.startGesture();
        pager.page(Move.forward);

        pager.startGesture();
        pager.dragBy(-40);

        assertThat(pager.startOf(2)).isEqualTo(-40);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-60);

        pager.page(Move.forward);

        assertThat(pager.startOf(3)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_atTheFirstCellWithoutCircularScroll_staysOnTheFirstCell() {
        final Pager pager = equalCellPager(ONE_CELL_PER_GESTURE, NOT_CIRCULAR);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.back)).isEqualTo(0);

        pager.page(Move.back);

        assertThat(pager.startOf(0)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_atTheLastCellWithoutCircularScroll_stopsAtTheLastCell() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, FOUR_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();
        pager.page(Move.forward);
        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(3)).isEqualTo(START_OF_THE_VIEWPORT);
        assertThat(pager.startOf(0)).isEqualTo(NOT_DRAWN);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(0);

        pager.page(Move.forward);

        assertThat(pager.startOf(3)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_pastTheLastCellWithCircularScroll_wrapsToTheFirstCell() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, FOUR_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();
        pager.page(Move.forward);
        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(3)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(0)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withAnIntervalLargerThanTheAdapter_stopsAtTheLastCell() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, FOUR_CELLS),
                        NO_CELL_SPACING,
                        MORE_CELLS_THAN_THE_ADAPTER_HAS,
                        NOT_CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(3)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(0);
    }

    @Test
    public void viewPagerGesture_withAnIntervalOfZero_advancesOneCell() {
        final Pager pager = equalCellPager(NO_INTERVAL, NOT_CIRCULAR);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withANegativeInterval_advancesOneCell() {
        final Pager pager = equalCellPager(NEGATIVE_INTERVAL, NOT_CIRCULAR);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withACentreSnapPosition_advancesOneCell() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.center);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(SMALL_CELL_SIZE);
    }

    @Test
    public void viewPagerGesture_withAnOnScreenSnapPosition_advancesOneCell() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.onScreen);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.back)).isEqualTo(SMALL_CELL_SIZE);

        pager.page(Move.back);

        assertThat(pager.startOf(0)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    @Test
    public void viewPagerGesture_withAnEnormousIntervalAtTheLastCell_asksForNoMovement() {
        final Pager pager = equalCellPager(ENORMOUS_INTERVAL, NOT_CIRCULAR);

        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(TEN_CELLS - 1)).isEqualTo(START_OF_THE_VIEWPORT);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(0);
    }

    @Test
    public void viewPagerGesture_withAHugeIntervalAndCircularScroll_stillPagesForward() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        CELL_SPACING,
                        HUGE_INTERVAL,
                        CIRCULAR,
                        SnapPosition.start);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isLessThan(0);
        assertThat(pager.pageDistance(Move.back)).isGreaterThan(0);
    }

    @Test
    public void
            viewPagerGesture_withViewportPaddingAndAStartSnap_advancesOneCellInsideThePadding() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.start,
                        VIEWPORT_PADDING);

        assertThat(pager.startOf(0)).isEqualTo(VIEWPORT_PADDING);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-SMALL_CELL_SIZE);

        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(VIEWPORT_PADDING);
    }

    @Test
    public void viewPagerGesture_withViewportPaddingAndACentreSnap_centresInsideThePadding() {
        final Pager pager =
                new Pager(
                        THREE_CELL_VIEWPORT,
                        equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                        NO_CELL_SPACING,
                        ONE_CELL_PER_GESTURE,
                        NOT_CIRCULAR,
                        SnapPosition.center,
                        VIEWPORT_PADDING);

        final int sizeInsidePadding = THREE_CELL_VIEWPORT - VIEWPORT_PADDING - VIEWPORT_PADDING;
        final int centredStart = VIEWPORT_PADDING + (sizeInsidePadding - SMALL_CELL_SIZE) / 2;

        pager.startGesture();
        pager.page(Move.forward);

        assertThat(pager.startOf(1)).isEqualTo(centredStart);
    }

    private static Pager equalCellPager(final int viewPagerInterval, final boolean isCircular) {
        return new Pager(
                THREE_CELL_VIEWPORT,
                equalSizes(SMALL_CELL_SIZE, TEN_CELLS),
                NO_CELL_SPACING,
                viewPagerInterval,
                isCircular,
                SnapPosition.start);
    }

    private static int[] equalSizes(final int cellSize, final int cellCount) {
        final int[] sizes = new int[cellCount];
        for (int index = 0; index < cellCount; index++) {
            sizes[index] = cellSize;
        }
        return sizes;
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        listLayoutManager.layout(mViewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void doFirstLayout(int viewGroupSize) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, viewGroupSize, viewGroupSize);
    }

    private static final class Pager {
        private final MyViewGroup mPagerViewGroup;
        private final ListLayoutManager mPagerLayoutManager;
        private final Animation mPagerAnimation = new Animation();
        private final int mViewportSize;

        private Pager(
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
                    NO_VIEWPORT_PADDING);
        }

        private Pager(
                final int viewportSize,
                final int[] cellSizes,
                final int cellSpacing,
                final int viewPagerInterval,
                final boolean isCircularScroll,
                final SnapPosition snapPosition,
                final int viewportPadding) {
            mViewportSize = viewportSize;
            mPagerViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
            final AdapterViewManager pagerAdapterViewManager = new AdapterViewManager();
            final LayoutManagerAttributes pagerAttributes =
                    new LayoutManagerAttributes(
                            isCircularScroll,
                            false,
                            true,
                            viewPagerInterval,
                            snapPosition,
                            cellSpacing,
                            false,
                            false,
                            false);
            mPagerLayoutManager =
                    new ListLayoutManager(
                            mPagerViewGroup, null, pagerAdapterViewManager, pagerAttributes);
            pagerAdapterViewManager.setAdapter(new TestAdapter(cellSizes));

            mPagerViewGroup.setPadding(
                    viewportPadding, viewportPadding, viewportPadding, viewportPadding);
            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(viewportSize, View.MeasureSpec.EXACTLY);
            mPagerViewGroup.measure(measureSpec, measureSpec);
            mPagerViewGroup.layout(0, 0, viewportSize, viewportSize);

            layout();
        }

        private void layout() {
            mPagerLayoutManager.layout(
                    mPagerViewGroup, mPagerAnimation, 0, 0, mViewportSize, mViewportSize);
        }

        private void startGesture() {
            mPagerAnimation.newAnimation();
            layout();
        }

        private void dragBy(final int displacement) {
            mPagerAnimation.setDisplacement(displacement);
            layout();
        }

        private int pageDistance(final Move move) {
            return mPagerLayoutManager.getViewPagerScrollDistance(move);
        }

        private void page(final Move move) {
            dragBy(pageDistance(move));
        }

        private int startOf(final int adapterPosition) {
            for (final View view : mPagerViewGroup.mViews) {
                final Object tag = view.getTag();
                final boolean isTheWantedView = tag.equals(Integer.valueOf(adapterPosition));
                if (isTheWantedView) return view.getLeft();
            }
            return NOT_DRAWN;
        }
    }

    public static class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();

        public MyViewGroup(Context context) {
            super(context);
        }

        public View forPosition(int position) {
            Collections.sort(mViews, new LeftToRightComparator());

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

    private static final class LeftToRightComparator implements Comparator<View> {
        @Override
        public int compare(final View lhs, final View rhs) {
            return lhs.getLeft() - rhs.getLeft();
        }
    }

    public static class TestAdapter extends BaseAdapter {
        private final int[] mCellSizes;
        private final int mViewSize;
        private int mAdapterSize;

        public TestAdapter(int viewSize) {
            mViewSize = viewSize;
            mCellSizes = null;
        }

        private TestAdapter(final int[] cellSizes) {
            mCellSizes = cellSizes;
            mViewSize = 0;
            mAdapterSize = cellSizes.length;
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

        private int getViewSize(final int position) {
            if (mCellSizes == null) return mViewSize;
            return mCellSizes[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int viewSize = getViewSize(position);
            FrameLayout outer = new FrameLayout(ApplicationProvider.getApplicationContext());
            outer.setTag(position);
            outer.setLayoutParams(new android.view.ViewGroup.LayoutParams(viewSize, viewSize));

            // TODO: necessary to have an outer and an inner?
            final FrameLayout inner = new FrameLayout(ApplicationProvider.getApplicationContext());
            inner.setLayoutParams(new android.view.ViewGroup.LayoutParams(viewSize, viewSize));
            outer.addView(inner);
            return outer;
        }
    }
}
