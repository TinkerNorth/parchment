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
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GridLayoutManagerTest {

    public static final int VIEW_GROUP_SIZE = 300;
    public static final int VIEW_SIZE = 100;
    public static final int CELL_SPACING = 10;
    public static final int NUMBER_OF_COLUMNS = 2;
    private static final int ONE_GROUP_PER_GESTURE = 1;
    private static final int THREE_GROUPS_PER_GESTURE = 3;
    private static final int PAGER_ADAPTER_SIZE = 10;
    private static final int NOT_DRAWN = Integer.MIN_VALUE;
    private static final int ZERO_VIEW_SIZE = 0;
    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    GridLayoutManagerAttributes attributes;
    GridLayoutManager listLayoutManager;

    @Before
    public void setup() {
        attributes =
                new GridLayoutManagerAttributes(
                        NUMBER_OF_COLUMNS,
                        false,
                        true,
                        false,
                        0,
                        SnapPosition.onScreen,
                        CELL_SPACING,
                        true,
                        true,
                        true,
                        true,
                        false,
                        false,
                        false);
        listLayoutManager = new GridLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout(VIEW_GROUP_SIZE);
    }

    @Test
    public void layout_addsSingleRowInRightSpot() {
        mTestAdapter.setAdapterSize(2);
        doLayout(new Animation());
        final View firstView = mViewGroup.mViews.get(0);
        final View secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(100);
        assertThat(firstView.getBottom()).isEqualTo(200);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(100);
        assertThat(secondView.getBottom()).isEqualTo(200);
    }

    @Test
    public void layout_addsRowInRightSpotAfterMove() {
        mTestAdapter.setAdapterSize(2);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        int displacement = 10;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(100);
        assertThat(firstView.getBottom()).isEqualTo(200);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(100);
        assertThat(secondView.getBottom()).isEqualTo(200);
    }

    @Test
    public void layout_handlesOverDrawWithOnScreenWithCellSpacing() {
        mTestAdapter.setAdapterSize(2);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(100);
        assertThat(firstView.getBottom()).isEqualTo(200);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(100);
        assertThat(secondView.getBottom()).isEqualTo(200);

        int displacement = -10;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        firstView = mViewGroup.mViews.get(0);
        secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(100);
        assertThat(firstView.getBottom()).isEqualTo(200);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(100);
        assertThat(secondView.getBottom()).isEqualTo(200);

        displacement = -displacement;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        firstView = mViewGroup.mViews.get(0);
        secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(100);
        assertThat(firstView.getBottom()).isEqualTo(200);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(100);
        assertThat(secondView.getBottom()).isEqualTo(200);
    }

    @Test
    public void layoutViewTestCorrectIndexIncrementationForGroups2() {
        mTestAdapter.setAdapterSize(0);

        mTestAdapter.setAdapterSize(0);

        final Animation animation = new Animation();
        animation.newAnimation();
        animation.newAnimation();
        doLayout(animation);
        animation.newAnimation();
        animation.newAnimation();
        doLayout(animation);

        mTestAdapter.setAdapterSize(8);
        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(0);
        doLayout(animation);

        mTestAdapter.setAdapterSize(16);
        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(0);
        doLayout(animation);

        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(-62);
        doLayout(animation);
        animation.setDisplacement(-74);
        doLayout(animation);
        animation.setDisplacement(-19);
        doLayout(animation);
        animation.setDisplacement(-14);
        doLayout(animation);
        animation.setDisplacement(-9);
        doLayout(animation);
        animation.setDisplacement(-6);
        doLayout(animation);
    }

    @Test
    public void layoutViewTestCorrectIndexIncrementationForGroups() {
        mTestAdapter.setAdapterSize(0);

        mTestAdapter.setAdapterSize(0);

        final Animation animation = new Animation();
        animation.newAnimation();
        animation.newAnimation();
        doLayout(animation);
        animation.newAnimation();
        animation.newAnimation();
        doLayout(animation);

        mTestAdapter.setAdapterSize(8);
        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(0);
        doLayout(animation);

        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(-43);
        doLayout(animation);

        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(-9);
        doLayout(animation);

        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(-132);
        doLayout(animation);
    }

    @Test
    public void simulation() {
        mTestAdapter.setAdapterSize(0);

        final Animation animation = new Animation();
        animation.newAnimation();
        animation.newAnimation();
        doLayout(animation);
        animation.newAnimation();
        animation.newAnimation();
        doLayout(animation);

        mTestAdapter.setAdapterSize(8);
        int displacement = 0;
        animation.newAnimation();
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);
    }

    @Test
    public void layout_RowsInRightSpotAfterMoveWithAllItemsOnScreen() {
        mTestAdapter.setAdapterSize(8);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);

        int displacement = -110;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        firstView = mViewGroup.mViews.get(0);
        secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);

        displacement = -displacement;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(6);

        firstView = mViewGroup.mViews.get(0);
        secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);
    }

    @Test
    public void OverDrawWithItemsOnScreen() {
        mTestAdapter.setAdapterSize(8);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        int displacement = 120;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);
    }

    @Test
    public void layout_RowsInRightSpotAfterMoveWithItemsOffScreen() {
        mTestAdapter.setAdapterSize(8);

        final Animation animation = new Animation();
        animation.newAnimation();
        doLayout(animation);

        View firstView = mViewGroup.mViews.get(0);
        View secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);

        int displacement = -120;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        firstView = mViewGroup.mViews.get(0);
        secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(-10);
        assertThat(firstView.getBottom()).isEqualTo(90);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(-10);
        assertThat(secondView.getBottom()).isEqualTo(90);

        displacement = -displacement;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(6);

        firstView = mViewGroup.mViews.get(0);
        secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(45);
        assertThat(firstView.getRight()).isEqualTo(145);
        assertThat(firstView.getTop()).isEqualTo(0);
        assertThat(firstView.getBottom()).isEqualTo(100);
        assertThat(secondView.getLeft()).isEqualTo(155);
        assertThat(secondView.getRight()).isEqualTo(255);
        assertThat(secondView.getTop()).isEqualTo(0);
        assertThat(secondView.getBottom()).isEqualTo(100);
    }

    @Test
    public void aGridThatIsNotAViewPager_withZeroSizedViews_laysOutWithoutReadingACellsView() {
        final MyViewGroup viewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
        final AdapterViewManager zeroSizeAdapterViewManager = new AdapterViewManager();
        final GridLayoutManagerAttributes notAViewPager =
                new GridLayoutManagerAttributes(
                        NUMBER_OF_COLUMNS,
                        false,
                        false,
                        false,
                        ONE_GROUP_PER_GESTURE,
                        SnapPosition.start,
                        CELL_SPACING,
                        false,
                        false,
                        true,
                        true,
                        false,
                        false,
                        false);
        final GridLayoutManager zeroSizeLayoutManager =
                new GridLayoutManager(viewGroup, null, zeroSizeAdapterViewManager, notAViewPager);
        final TestAdapter zeroSizeAdapter = new TestAdapter(ZERO_VIEW_SIZE);
        zeroSizeAdapterViewManager.setAdapter(zeroSizeAdapter);
        zeroSizeAdapter.setAdapterSize(PAGER_ADAPTER_SIZE);

        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        viewGroup.measure(measureSpec, measureSpec);
        viewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);

        final Animation animation = new Animation();
        animation.newAnimation();
        zeroSizeLayoutManager.layout(viewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);

        animation.newAnimation();
        zeroSizeLayoutManager.layout(viewGroup, animation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);

        assertThat(zeroSizeLayoutManager.getViewPagerScrollDistance(Move.forward)).isEqualTo(0);
    }

    @Test
    public void viewPagerGesture_onAGrid_advancesOneWholeGroup() {
        final GridPager pager = new GridPager(ONE_GROUP_PER_GESTURE);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward)).isEqualTo(-(VIEW_SIZE + CELL_SPACING));

        pager.page(Move.forward);

        assertThat(pager.topOf(2)).isEqualTo(0);
        assertThat(pager.topOf(3)).isEqualTo(0);
    }

    @Test
    public void viewPagerGesture_onAGridWithAnIntervalOfThree_advancesThreeWholeGroups() {
        final GridPager pager = new GridPager(THREE_GROUPS_PER_GESTURE);

        pager.startGesture();

        assertThat(pager.pageDistance(Move.forward))
                .isEqualTo(-THREE_GROUPS_PER_GESTURE * (VIEW_SIZE + CELL_SPACING));

        pager.page(Move.forward);

        assertThat(pager.topOf(6)).isEqualTo(0);
    }

    private static final class GridPager {
        private final MyViewGroup mPagerViewGroup;
        private final GridLayoutManager mPagerLayoutManager;
        private final Animation mPagerAnimation = new Animation();

        private GridPager(final int viewPagerInterval) {
            mPagerViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
            final AdapterViewManager pagerAdapterViewManager = new AdapterViewManager();
            final GridLayoutManagerAttributes pagerAttributes =
                    new GridLayoutManagerAttributes(
                            NUMBER_OF_COLUMNS,
                            false,
                            false,
                            true,
                            viewPagerInterval,
                            SnapPosition.start,
                            CELL_SPACING,
                            false,
                            false,
                            true,
                            true,
                            false,
                            false,
                            false);
            mPagerLayoutManager =
                    new GridLayoutManager(
                            mPagerViewGroup, null, pagerAdapterViewManager, pagerAttributes);
            final TestAdapter pagerAdapter = new TestAdapter(VIEW_SIZE);
            pagerAdapterViewManager.setAdapter(pagerAdapter);
            pagerAdapter.setAdapterSize(PAGER_ADAPTER_SIZE);

            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
            mPagerViewGroup.measure(measureSpec, measureSpec);
            mPagerViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);

            layout();
        }

        private void layout() {
            mPagerLayoutManager.layout(
                    mPagerViewGroup, mPagerAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
        }

        private void startGesture() {
            mPagerAnimation.newAnimation();
            layout();
        }

        private int pageDistance(final Move move) {
            return mPagerLayoutManager.getViewPagerScrollDistance(move);
        }

        private void page(final Move move) {
            mPagerAnimation.setDisplacement(pageDistance(move));
            layout();
        }

        private int topOf(final int adapterPosition) {
            for (final View view : mPagerViewGroup.mViews) {
                final Object tag = view.getTag();
                final boolean isTheWantedView = tag.equals(Integer.valueOf(adapterPosition));
                if (isTheWantedView) return view.getTop();
            }
            return NOT_DRAWN;
        }
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
        private final int mViewSize;
        private int mAdapterSize;

        public TestAdapter(int viewSize) {
            mViewSize = viewSize;
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FrameLayout outer = new FrameLayout(ApplicationProvider.getApplicationContext());
            outer.setTag(position);
            outer.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));

            // TODO: necessary to have an outer and an inner?
            final FrameLayout inner = new FrameLayout(ApplicationProvider.getApplicationContext());
            inner.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));
            outer.addView(inner);
            return outer;
        }
    }
}
