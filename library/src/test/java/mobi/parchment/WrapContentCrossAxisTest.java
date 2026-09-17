// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.AdapterViewInitializer;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.ChildTouchGestureListener;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.GraphicsMode;

/**
 * The #59 repro: a Parchment view with {@code wrap_content} across its scroll axis, inflated into a
 * {@code LinearLayout} above a sibling, must be as tall (or as wide) as its largest cell and leave
 * the sibling directly after it.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class WrapContentCrossAxisTest {

    private static final int ROOT_SIZE = 400;
    private static final int CELL_SIZE = 100;
    private static final int CELL_SPACING = 10;
    private static final int TALLEST_CELL_BREADTH = 80;
    private static final int[] LIST_BREADTHS = {60, TALLEST_CELL_BREADTH, 40};
    private static final int[] EQUAL_LIST_BREADTHS = {
        TALLEST_CELL_BREADTH, TALLEST_CELL_BREADTH, 40
    };
    private static final int TALLEST_GROUP_FIRST_VIEW_BREADTH = 80;
    private static final int TALLEST_GROUP_SECOND_VIEW_BREADTH = 30;
    private static final int[] GRID_BREADTHS = {
        60, 40, TALLEST_GROUP_FIRST_VIEW_BREADTH, TALLEST_GROUP_SECOND_VIEW_BREADTH, 50
    };
    private static final int TALLEST_GROUP_BREADTH =
            TALLEST_GROUP_FIRST_VIEW_BREADTH + CELL_SPACING + TALLEST_GROUP_SECOND_VIEW_BREADTH;
    private static final int HALF_THE_CELL_SPACING = CELL_SPACING / 2;
    private static final int SHORT_CELL = 50;
    private static final int TALL_CELL = 90;
    private static final int[] A_TALL_FIFTH_CELL = {
        SHORT_CELL, SHORT_CELL, SHORT_CELL, SHORT_CELL, TALL_CELL
    };
    private static final float A_DRAG_THAT_REVEALS_THE_FIFTH_CELL = 150f;
    private static final int ONE_LAYOUT_REQUESTED = 1;
    private static final int NO_LAYOUT_REQUESTED = 0;
    private static final int EXACTLY_THE_ROOT =
            View.MeasureSpec.makeMeasureSpec(ROOT_SIZE, View.MeasureSpec.EXACTLY);
    private static final int AT_MOST_THE_ROOT =
            View.MeasureSpec.makeMeasureSpec(ROOT_SIZE, View.MeasureSpec.AT_MOST);
    private static final int ONE_CELL = 1;
    private static final int PADDING = 20;
    private static final int BREADTH_PADDING = 2 * PADDING;
    private static final int PATTERN_ROWS = 2;
    private static final int PATTERN_ITEM = 1;
    private static final int THE_FIRST_COLUMN = 0;
    private static final int THE_FIRST_CELL = 0;
    private static final int PATTERN_ADAPTER_SIZE = 6;
    private static final int INSIDE_THE_WRAPPED_BREADTH = TALLEST_CELL_BREADTH / 2;
    private static final int THE_LAST_WRAPPED_ROW = TALLEST_CELL_BREADTH - 1;
    private static final int THE_FIRST_ROW_OF_THE_SIBLING = TALLEST_CELL_BREADTH;
    private static final int DIVIDER_COLOUR = 0xFF00FF00;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    @Test
    public void horizontalListWithWrapContentHeight_isAsTallAsItsTallestCell() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_horizontal_list, listAdapter(HORIZONTAL));

        assertThat(root.mView.getHeight()).isEqualTo(TALLEST_CELL_BREADTH);
        assertThat(root.mView.getWidth()).isEqualTo(ROOT_SIZE);
    }

    @Test
    public void horizontalListWithWrapContentHeight_leavesTheSiblingDirectlyBelowIt() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_horizontal_list, listAdapter(HORIZONTAL));

        assertThat(root.mSibling.getTop()).isEqualTo(TALLEST_CELL_BREADTH);
    }

    @Test
    public void horizontalListWithWrapContentHeightAndPadding_addsThePaddingToTheTallestCell() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_horizontal_list_padded, listAdapter(HORIZONTAL));

        assertThat(root.mView.getHeight()).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
        assertThat(root.mSibling.getTop()).isEqualTo(TALLEST_CELL_BREADTH + BREADTH_PADDING);
    }

    @Test
    public void verticalListWithWrapContentWidth_isAsWideAsItsWidestCell() {
        final LaidOutRoot root = layOut(R.layout.wrap_content_vertical_list, listAdapter(VERTICAL));

        assertThat(root.mView.getWidth()).isEqualTo(TALLEST_CELL_BREADTH);
        assertThat(root.mView.getHeight()).isEqualTo(ROOT_SIZE);
        assertThat(root.mSibling.getLeft()).isEqualTo(TALLEST_CELL_BREADTH);
    }

    @Test
    public void horizontalGridWithWrapContentHeight_isAsTallAsItsTallestColumn() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_horizontal_grid, gridAdapter(HORIZONTAL));

        assertThat(root.mView.getHeight()).isEqualTo(TALLEST_GROUP_BREADTH);
        assertThat(root.mSibling.getTop()).isEqualTo(TALLEST_GROUP_BREADTH);
    }

    @Test
    public void verticalGridWithWrapContentWidth_isAsWideAsItsWidestRow() {
        final LaidOutRoot root = layOut(R.layout.wrap_content_vertical_grid, gridAdapter(VERTICAL));

        assertThat(root.mView.getWidth()).isEqualTo(TALLEST_GROUP_BREADTH);
        assertThat(root.mSibling.getLeft()).isEqualTo(TALLEST_GROUP_BREADTH);
    }

    @Test
    public void horizontalGridPatternWithWrapContentHeight_stillFillsTheParent() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_horizontal_grid_pattern, patternAdapter());

        assertThat(root.mView.getHeight()).isEqualTo(ROOT_SIZE);
        assertThat(root.mSibling.getTop()).isEqualTo(ROOT_SIZE);
    }

    @Test
    public void horizontalListWithWrapContentWidth_stillFillsTheParentAlongTheScrollAxis() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_scroll_axis_list, listAdapter(HORIZONTAL));

        assertThat(root.mView.getWidth()).isEqualTo(ROOT_SIZE);
        assertThat(root.mSibling.getLeft()).isEqualTo(ROOT_SIZE);
    }

    @Test
    public void horizontalGridWithWrapContentWidth_stillFillsTheParentAlongTheScrollAxis() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_scroll_axis_grid, gridAdapter(HORIZONTAL));

        assertThat(root.mView.getWidth()).isEqualTo(ROOT_SIZE);
        assertThat(root.mSibling.getLeft()).isEqualTo(ROOT_SIZE);
    }

    @Test
    public void horizontalGridPatternWithWrapContentWidth_stillFillsTheParentAlongTheScrollAxis() {
        final LaidOutRoot root =
                layOut(R.layout.wrap_content_scroll_axis_grid_pattern, patternAdapter());

        assertThat(root.mView.getWidth()).isEqualTo(ROOT_SIZE);
        assertThat(root.mSibling.getLeft()).isEqualTo(ROOT_SIZE);
    }

    @Test
    public void dividerOnAWrappedHorizontalList_spansTheWrappedHeightAndNoFurther() {
        final LaidOutRoot root =
                layOut(
                        R.layout.wrap_content_horizontal_list_divider,
                        new BreadthsAdapter(HORIZONTAL, EQUAL_LIST_BREADTHS));

        final Bitmap bitmap = paint(root.mRoot);
        final ViewGroup view = (ViewGroup) root.mView;
        final View firstCell = view.getChildAt(THE_FIRST_CELL);
        final int firstGapStart = firstCell.getRight();
        final int firstGapCentre = firstGapStart + HALF_THE_CELL_SPACING;

        assertThat(bitmap.getPixel(firstGapCentre, INSIDE_THE_WRAPPED_BREADTH))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(firstGapCentre, THE_LAST_WRAPPED_ROW)).isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(firstGapCentre, THE_FIRST_ROW_OF_THE_SIBLING))
                .isNotEqualTo(DIVIDER_COLOUR);
    }

    @Test
    public void
            horizontalListUnderAnAtMostHeight_measuresAMatchParentChildAgainstTheHeightOffered() {
        final SpecRecordingAdapter adapter = new SpecRecordingAdapter();
        final ViewGroup root = inflate(R.layout.wrap_content_horizontal_list, adapter);

        root.measure(EXACTLY_THE_ROOT, EXACTLY_THE_ROOT);

        assertThat(adapter.getLastHeightMeasureSpec()).isEqualTo(AT_MOST_THE_ROOT);
    }

    @Test
    public void
            horizontalListWithWrapContentHeight_aScrollThatRevealsATallerCell_asksForOneLayoutAndGrowsToIt() {
        final AttachedRoot root =
                attachAndLayOut(
                        R.layout.wrap_content_horizontal_probe_list,
                        new BreadthsAdapter(HORIZONTAL, A_TALL_FIFTH_CELL));
        final int heightBeforeTheScroll = root.mView.getHeight();
        root.mView.resetLayoutRequests();

        drag(root.mView, A_DRAG_THAT_REVEALS_THE_FIFTH_CELL);
        final int layoutRequests = root.mView.getLayoutRequests();
        root.layOut();

        assertThat(heightBeforeTheScroll).isEqualTo(SHORT_CELL);
        assertThat(layoutRequests).isEqualTo(ONE_LAYOUT_REQUESTED);
        assertThat(root.mView.getHeight()).isEqualTo(TALL_CELL);
        assertThat(root.mSibling.getTop()).isEqualTo(TALL_CELL);
    }

    @Test
    public void
            horizontalListWithWrapContentHeight_aScrollThatRevealsNothingTaller_asksForNoLayout() {
        final AttachedRoot root =
                attachAndLayOut(
                        R.layout.wrap_content_horizontal_probe_list,
                        new BreadthsAdapter(HORIZONTAL, EQUAL_LIST_BREADTHS));
        root.mView.resetLayoutRequests();

        drag(root.mView, A_DRAG_THAT_REVEALS_THE_FIFTH_CELL);

        assertThat(root.mView.getLayoutRequests()).isEqualTo(NO_LAYOUT_REQUESTED);
    }

    private static BaseAdapter listAdapter(final boolean isVertical) {
        return new BreadthsAdapter(isVertical, LIST_BREADTHS);
    }

    private static BaseAdapter gridAdapter(final boolean isVertical) {
        return new BreadthsAdapter(isVertical, GRID_BREADTHS);
    }

    private static BaseAdapter patternAdapter() {
        return new BreadthsAdapter(HORIZONTAL, new int[PATTERN_ADAPTER_SIZE]);
    }

    private static ViewGroup inflate(final int layoutId, final BaseAdapter adapter) {
        final ActivityController<WrapContentActivity> controller =
                Robolectric.buildActivity(WrapContentActivity.class);
        final WrapContentActivity activity = controller.get();
        activity.setLayoutId(layoutId);
        activity.setAdapter(adapter);
        controller.create();
        return activity.findViewById(R.id.wrap_content_root);
    }

    private static LaidOutRoot layOut(final int layoutId, final BaseAdapter adapter) {
        final ViewGroup root = inflate(layoutId, adapter);
        root.measure(EXACTLY_THE_ROOT, EXACTLY_THE_ROOT);
        root.layout(0, 0, ROOT_SIZE, ROOT_SIZE);
        final View view = root.findViewById(R.id.wrap_content_view);
        final View sibling = root.findViewById(R.id.wrap_content_sibling);
        return new LaidOutRoot(root, view, sibling);
    }

    private static Bitmap paint(final View root) {
        final Bitmap bitmap = Bitmap.createBitmap(ROOT_SIZE, ROOT_SIZE, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        root.draw(canvas);
        return bitmap;
    }

    private static AttachedRoot attachAndLayOut(final int layoutId, final BaseAdapter adapter) {
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        final ViewGroup root = (ViewGroup) View.inflate(activity, layoutId, null);
        final ProbeListView view = root.findViewById(R.id.wrap_content_view);
        final View sibling = root.findViewById(R.id.wrap_content_sibling);
        view.setAdapter(adapter);
        activity.setContentView(root);
        final AttachedRoot attachedRoot = new AttachedRoot(root, view, sibling);
        attachedRoot.layOut();
        return attachedRoot;
    }

    private static void drag(final ProbeListView view, final float distance) {
        final ChildTouchGestureListener gestureListener = view.getGestureListener();
        gestureListener.onDown(down());
        gestureListener.onScroll(down(), moveBy(distance), distance, distance);
        idleMainLooper();
        gestureListener.onUp();
        idleMainLooper();
    }

    private static void idleMainLooper() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    private static MotionEvent down() {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 200f, 20f, 0);
    }

    private static MotionEvent moveBy(final float distance) {
        return MotionEvent.obtain(0, 10, MotionEvent.ACTION_MOVE, 200f - distance, 20f, 0);
    }

    private static final class AttachedRoot {
        private final ViewGroup mRoot;
        private final ProbeListView mView;
        private final View mSibling;

        private AttachedRoot(final ViewGroup root, final ProbeListView view, final View sibling) {
            mRoot = root;
            mView = view;
            mSibling = sibling;
        }

        private void layOut() {
            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(ROOT_SIZE, View.MeasureSpec.EXACTLY);
            mRoot.measure(measureSpec, measureSpec);
            mRoot.layout(0, 0, ROOT_SIZE, ROOT_SIZE);
        }
    }

    /** A ListView that hands out its gesture listener and counts the layouts asked of it. */
    public static final class ProbeListView extends ListView<BaseAdapter> {
        private ChildTouchGestureListener mGestureListener;
        private int mLayoutRequests;

        public ProbeListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        ChildTouchGestureListener getGestureListener() {
            return mGestureListener;
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

    private static final class LaidOutRoot {
        private final ViewGroup mRoot;
        private final View mView;
        private final View mSibling;

        private LaidOutRoot(final ViewGroup root, final View view, final View sibling) {
            mRoot = root;
            mView = view;
            mSibling = sibling;
        }
    }

    public static class WrapContentActivity extends Activity {
        private int mLayoutId;
        private BaseAdapter mAdapter;

        public void setLayoutId(final int layoutId) {
            mLayoutId = layoutId;
        }

        public void setAdapter(final BaseAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(mLayoutId);
            final AbstractAdapterView<BaseAdapter, ?> view = findViewById(R.id.wrap_content_view);
            addPatternDefinition();
            view.setAdapter(mAdapter);
        }

        private void addPatternDefinition() {
            final View view = findViewById(R.id.wrap_content_view);
            final boolean isAGridPattern = view instanceof GridPatternView;
            if (!isAGridPattern) return;

            final GridPatternView<BaseAdapter> gridPatternView =
                    findViewById(R.id.wrap_content_view);
            final List<GridPatternItemDefinition> column =
                    new ArrayList<GridPatternItemDefinition>();
            for (int row = 0; row < PATTERN_ROWS; row++) {
                column.add(
                        new GridPatternItemDefinition(
                                row, THE_FIRST_COLUMN, PATTERN_ITEM, PATTERN_ITEM));
            }
            gridPatternView.addGridPatternGroupDefinition(column);
        }
    }

    /** Cells of {@link #CELL_SIZE} along the scroll axis and the given breadths across it. */
    private static final class BreadthsAdapter extends BaseAdapter {
        private final boolean mIsVertical;
        private final int[] mBreadths;

        private BreadthsAdapter(final boolean isVertical, final int[] breadths) {
            mIsVertical = isVertical;
            mBreadths = breadths;
        }

        @Override
        public int getCount() {
            return mBreadths.length;
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
            final View view = viewToFill(convertView, parent);
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

        private static View viewToFill(final View convertView, final ViewGroup parent) {
            if (convertView != null) return convertView;
            return new FrameLayout(parent.getContext());
        }
    }

    /** One match_parent-tall cell that remembers the height spec it was last measured with. */
    private static final class SpecRecordingAdapter extends BaseAdapter {
        private final SpecRecordingView mView =
                new SpecRecordingView(ApplicationProvider.getApplicationContext());

        int getLastHeightMeasureSpec() {
            return mView.getLastHeightMeasureSpec();
        }

        @Override
        public int getCount() {
            return ONE_CELL;
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
            mView.setLayoutParams(
                    new ViewGroup.LayoutParams(CELL_SIZE, ViewGroup.LayoutParams.MATCH_PARENT));
            return mView;
        }
    }

    private static final class SpecRecordingView extends View {
        private int mLastHeightMeasureSpec;

        private SpecRecordingView(final Context context) {
            super(context);
        }

        int getLastHeightMeasureSpec() {
            return mLastHeightMeasureSpec;
        }

        @Override
        protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
            mLastHeightMeasureSpec = heightMeasureSpec;
            setMeasuredDimension(CELL_SIZE, SHORT_CELL);
        }
    }
}
