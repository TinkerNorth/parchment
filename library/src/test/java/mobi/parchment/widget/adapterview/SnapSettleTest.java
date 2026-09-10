// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
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

        fling(view);

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

        fling(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
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

        fling(view);

        assertSettled(view);
        assertThat(childTops(view)).contains(CENTRED_LIST_CELL_START);
    }

    private void assertSettled(final SettleView view) {
        assertThat(view.getFrameRequests()).isLessThanOrEqualTo(FRAME_BUDGET);
        assertThat(view.getGestureListener().getState()).isEqualTo(AdapterAnimator.State.notMoving);
    }

    private List<Integer> cellStarts(final SettleGridPatternView view) {
        final List<Integer> starts = new ArrayList<Integer>();
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final int childBreadth = child.getRight() - child.getLeft();
            final boolean isTheCellsFirstView = childBreadth == VIEW_BREADTH;
            if (isTheCellsFirstView) starts.add(child.getTop());
        }
        return starts;
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

    private SettleListView listView() {
        return listView(R.layout.settle_list_center, LIST_CELL_SIZE);
    }

    private SettleListView tallCellListView() {
        return listView(R.layout.settle_list_tall_cells, ODD_TALL_CELL_SIZE);
    }

    private SettleListView evenTallCellListView() {
        return listView(R.layout.settle_list_tall_cells, EVEN_TALL_CELL_SIZE);
    }

    private SettleListView listView(final int layoutId, final int cellSize) {
        final SettleListView view = (SettleListView) View.inflate(mActivity, layoutId, null);
        view.setAdapter(new FixedSizeAdapter(mActivity, ADAPTER_SIZE, cellSize));
        attachAndLayout(view);
        return view;
    }

    private SettleGridView gridView() {
        final SettleGridView view =
                (SettleGridView) View.inflate(mActivity, R.layout.settle_grid_center, null);
        view.setAdapter(new UnevenAdapter(mActivity, ADAPTER_SIZE));
        attachAndLayout(view);
        return view;
    }

    private void attachAndLayout(final View view) {
        mContent.addView(view, new FrameLayout.LayoutParams(VIEW_BREADTH, VIEW_SIZE));
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

    private void fling(final SettleView view) {
        view.reset();
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        gestureListener.onFling(down(), moveBy(DRAG_DISTANCE), FLING_VELOCITY, FLING_VELOCITY);
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

    private interface SettleView {
        void reset();

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
                final boolean isVerticalScroll) {
            final AdapterViewInitializer<GridPatternGroup> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll);
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
                final boolean isVerticalScroll) {
            final AdapterViewInitializer<View> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll);
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
                final boolean isVerticalScroll) {
            final AdapterViewInitializer<Group> initializer =
                    super.createAdapterViewInitializer(
                            context,
                            isViewPager,
                            adapterViewManager,
                            layoutManager,
                            isVerticalScroll);
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
            if (convertView != null) return convertView;
            final boolean isTall = position % 2 == 0;
            final int size = isTall ? TALL_SIZE : SHORT_SIZE;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            return view;
        }
    }
}
