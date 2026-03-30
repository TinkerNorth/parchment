package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import mobi.parchment.widget.adapterview.listview.ListLayoutManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import androidx.test.core.app.ApplicationProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Emir Hasanbegovic
 */
@RunWith(RobolectricTestRunner.class)
public class ListLayoutManagerTest {

    public static final int VIEW_GROUP_SIZE = 299;
    public static final int VIEW_SIZE = 100;
    public static final int CELL_SPACING = 10;
    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    LayoutManagerAttributes attributes;
    ListLayoutManager listLayoutManager;

    @Before
    public void setup() {
        attributes = new LayoutManagerAttributes(true, true, true, 0, SnapPosition.onScreen, CELL_SPACING, true, true, false);
        listLayoutManager = new ListLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout(VIEW_GROUP_SIZE);
    }

    @Test
    public void layout_addsViewsToAdapterViewHandler() {
        assertThat(mViewGroup.mViews.size()).isEqualTo(0);
        mTestAdapter.setAdapterSize(1);
        doLayout(new Animation());
        assertThat(mViewGroup.mViews.size()).isEqualTo(1);
    }

    @Test
    public void layout_shouldMeasureTheChildViews() {
        mTestAdapter.setAdapterSize(1);
        doLayout(new Animation());
        assertThat(mViewGroup.mViews.get(0).getMeasuredWidth()).isEqualTo(VIEW_SIZE);
    }

    @Test
    public void layout_shouldOnlyAddViewsThatCanBeVisible() {
        mTestAdapter.setAdapterSize(4);
        doLayout(new Animation());
        assertThat(mViewGroup.mViews.size()).isEqualTo(3);
    }

    @Test
    public void layout_shouldLayoutTheChildViews() {
        mTestAdapter.setAdapterSize(4);
        doLayout(new Animation());
        final View firstView = mViewGroup.mViews.get(0);
        final View secondView = mViewGroup.mViews.get(1);
        assertThat(firstView.getLeft()).isEqualTo(0);
        assertThat(firstView.getRight()).isEqualTo(VIEW_SIZE);
        assertThat(secondView.getLeft()).isEqualTo(VIEW_SIZE + CELL_SPACING);
        assertThat(secondView.getRight()).isEqualTo(VIEW_SIZE + CELL_SPACING + VIEW_SIZE);
    }

    @Test
    public void layout_shouldDisplaceBasedOnTheAnimation() {
        mTestAdapter.setAdapterSize(5);
        final Animation animation = new Animation();

        animation.newAnimation();
        doLayout(animation);

        animation.newAnimation();
        animation.setDisplacement(-20);
        doLayout(animation);

        final View firstView = mViewGroup.forPosition(0);
        assertThat(firstView.getLeft()).isEqualTo(-20 );
    }

    @Test
    public void layout_shouldStartFirstViewInRightPlace_afterAdapterIsCleared() {
        mTestAdapter.setAdapterSize(2);
        doLayout();
        mTestAdapter.setAdapterSize(0);
        doLayout();
        mTestAdapter.setAdapterSize(1);
        doLayout();

        final View firstView = mViewGroup.forPosition(0);
        assertThat(firstView.getLeft()).isEqualTo(0);
    }



    @Test
    public void layouts_removedFromViewGroupAfterAdapterClear(){
        final Animation animation = new Animation();

        mTestAdapter.setAdapterSize(4);
        animation.newAnimation();
        doLayout(animation);

        mTestAdapter.setAdapterSize(0);
        animation.newAnimation();
        doLayout(animation);

        assertThat(mViewGroup.mViews).isEmpty();
    }

    @Test
    public void layouts_removedFromViewGroupAfterAdapterSizeChange(){
        final Animation animation = new Animation();

        mTestAdapter.setAdapterSize(4);
        animation.newAnimation();
        doLayout(animation);

        mTestAdapter.setAdapterSize(3);
        animation.newAnimation();
        doLayout(animation);

        assertThat(mViewGroup.mViews.size()).isEqualTo(3);
    }

    @Test
    public void layout_shouldStartFirstViewInRightPlace_afterDisplacing_andAdapterIsCleared() {
        final Animation animation = new Animation();
        mTestAdapter.setAdapterSize(4);

        animation.newAnimation();
        doLayout(animation);

        final int displacement = -20;
        animation.newAnimation();
        animation.setDisplacement(displacement);
        doLayout(animation);

        mTestAdapter.setAdapterSize(0);
        animation.newAnimation();
        doLayout(animation);

        mTestAdapter.setAdapterSize(1);
        animation.newAnimation();
        doLayout(animation);

        View firstView = mViewGroup.forPosition(0);
        assertThat(firstView.getLeft()).isEqualTo(0);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        listLayoutManager.layout(mViewGroup, animation, false, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void doFirstLayout(int viewGroupSize) {
        final int measureSpec = View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, viewGroupSize, viewGroupSize);
    }

    public class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();

        public MyViewGroup(Context context) {
            super(context);
        }

        public View forPosition(int position) {
            Collections.sort(mViews, new Comparator<View>() {
                @Override
                public int compare(View lhs, View rhs) {
                    return lhs.getLeft() - rhs.getLeft();
                }
            });

            return mViews.get(position);
        }

        @Override
        public boolean addViewInAdapterView(View view, int index, ViewGroup.LayoutParams layoutParams) {
            mViews.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(View view) {
            mViews.remove(view);
        }
    }

    public class TestAdapter extends BaseAdapter {
        private int mAdapterSize;
        private int mViewSize;

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
            outer.setLayoutParams(new android.view.ViewGroup.LayoutParams(mViewSize, mViewSize));

            // TODO: necessary to have an outer and an inner?
            final FrameLayout inner = new FrameLayout(ApplicationProvider.getApplicationContext());
            inner.setLayoutParams(new android.view.ViewGroup.LayoutParams(mViewSize, mViewSize));
            outer.addView(inner);
            return outer;
        }
    }

}
