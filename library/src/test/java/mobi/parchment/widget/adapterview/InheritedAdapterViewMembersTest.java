// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * The rest of the inherited {@code android.widget.AdapterView} surface: what the items, the
 * selection and the listener getters answer, whether a layout animation can run, and whether the
 * focusable state follows the adapter the way it does on a real {@code android.widget.ListView}.
 */
@RunWith(RobolectricTestRunner.class)
public class InheritedAdapterViewMembersTest {

    private static final int VIEW_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int ANIMATION_MILLISECONDS = 100;
    private static final int INVALID_POSITION = android.widget.AdapterView.INVALID_POSITION;
    private static final long INVALID_ROW_ID = android.widget.AdapterView.INVALID_ROW_ID;
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
    public void getItemAtPosition_readsTheAdapter() {
        final InheritedListView listView = listView(3);

        assertThat(listView.getItemAtPosition(1)).isEqualTo("item1");
        assertThat(listView.getItemIdAtPosition(1)).isEqualTo(10L);
    }

    @Test
    public void getItemAtPosition_withNoAdapter_isNullAndAnInvalidRowId() {
        final InheritedListView listView = new InheritedListView(mActivity, null);

        assertThat(listView.getItemAtPosition(0)).isNull();
        assertThat(listView.getItemIdAtPosition(0)).isEqualTo(INVALID_ROW_ID);
    }

    @Test
    public void getSelectedItem_afterSetSelection_readsTheAdapter() {
        final InheritedListView listView = listView(5);

        listView.setSelection(2);
        layOut(listView);

        assertThat(listView.getSelectedItemPosition()).isEqualTo(2);
        assertThat(listView.getSelectedItem()).isEqualTo("item2");
        assertThat(listView.getSelectedItemId()).isEqualTo(20L);
    }

    @Test
    public void getSelectedItem_withNothingSelected_isNullAndAnInvalidRowId() {
        final InheritedListView listView = listView(5);

        assertThat(listView.getSelectedItemPosition()).isEqualTo(INVALID_POSITION);
        assertThat(listView.getSelectedItem()).isNull();
        assertThat(listView.getSelectedItemId()).isEqualTo(INVALID_ROW_ID);
        assertThat(listView.getSelectedView()).isNull();
    }

    @Test
    public void getPositionForView_readsTheEnginesOwnViewToPositionMap() {
        final InheritedListView listView = listView(5);

        final View firstChild = listView.getChildAt(FIRST_CHILD);

        assertThat(listView.getPositionForView(firstChild)).isEqualTo(0);
        assertThat(listView.getPositionForView(new View(mActivity))).isEqualTo(INVALID_POSITION);
    }

    @Test
    public void theInheritedListenerGetters_returnTheListenersThatWereSet() {
        final InheritedListView listView = listView(3);
        final RecordingItemListener listener = new RecordingItemListener();

        listView.setOnItemClickListener(listener);
        listView.setOnItemLongClickListener(listener);
        listView.setOnItemSelectedListener(listener);

        assertThat(listView.getOnItemClickListener()).isSameAs(listener);
        assertThat(listView.getOnItemLongClickListener()).isSameAs(listener);
        assertThat(listView.getOnItemSelectedListener()).isSameAs(listener);
    }

    @Test
    public void canAnimate_withNoLayoutAnimation_isFalse() {
        final InheritedListView listView = listView(5);

        assertThat(listView.getLayoutAnimation()).isNull();
        assertThat(listView.exposedCanAnimate()).isFalse();
    }

    @Test
    public void canAnimate_withALayoutAnimationAndItems_isTrue() {
        final InheritedListView listView = listView(5);

        listView.setLayoutAnimation(layoutAnimation());

        assertThat(listView.exposedCanAnimate()).isTrue();
    }

    @Test
    public void canAnimate_withALayoutAnimationAndAnEmptyAdapter_isFalse() {
        final InheritedListView listView = listView(0);

        listView.setLayoutAnimation(layoutAnimation());

        assertThat(listView.exposedCanAnimate()).isFalse();
    }

    @Test
    public void canAnimate_withALayoutAnimationAndNoAdapter_isFalse() {
        final InheritedListView listView = inflateListView();
        attach(listView);
        layOut(listView);

        listView.setLayoutAnimation(layoutAnimation());

        assertThat(listView.exposedCanAnimate()).isFalse();
    }

    @Test
    public void aLayoutAnimation_runsOnTheCellsTheEngineDrew() {
        final InheritedListView listView = listView(5);
        listView.setLayoutAnimation(layoutAnimation());
        layOut(listView);

        listView.draw(new android.graphics.Canvas());

        assertThat(listView.getChildCount()).isGreaterThan(0);
        assertThat(listView.getChildAt(FIRST_CHILD).getAnimation()).isNotNull();
    }

    @Test
    public void withNoLayoutAnimation_noCellIsGivenAnAnimation() {
        final InheritedListView listView = listView(5);

        listView.draw(new android.graphics.Canvas());

        assertThat(listView.getChildCount()).isGreaterThan(0);
        assertThat(listView.getChildAt(FIRST_CHILD).getAnimation()).isNull();
    }

    @Test
    public void theFocusableState_isNotRefreshedOnADataSetChange_unlikeThePlatformListView() {
        final InheritedListView listView = inflateListView();
        attach(listView);
        final android.widget.ListView platformListView = attachedPlatformListView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        final ResizableAdapter platformAdapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        platformListView.setAdapter(platformAdapter);
        listView.setFocusableInTouchMode(true);
        platformListView.setFocusableInTouchMode(true);
        assertThat(listView.isFocusable()).isTrue();
        assertThat(platformListView.isFocusable()).isTrue();

        adapter.setCount(0);
        platformAdapter.setCount(0);

        assertThat(platformListView.isFocusable()).isFalse();
        assertThat(listView.isFocusable()).isTrue();
    }

    private static LayoutAnimationController layoutAnimation() {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(ANIMATION_MILLISECONDS);
        return new LayoutAnimationController(alphaAnimation);
    }

    private InheritedListView inflateListView() {
        return (InheritedListView)
                View.inflate(mActivity, R.layout.surface_inherited_list_view, null);
    }

    private InheritedListView listView(final int count) {
        final InheritedListView listView = inflateListView();
        listView.setAdapter(new ResizableAdapter(mActivity, count));
        attach(listView);
        layOut(listView);
        return listView;
    }

    private android.widget.ListView attachedPlatformListView() {
        final android.widget.ListView platformListView = new android.widget.ListView(mActivity);
        platformListView.setDividerHeight(0);
        attach(platformListView);
        return platformListView;
    }

    private void attach(final View view) {
        mContent.addView(view, new FrameLayout.LayoutParams(VIEW_SIZE, VIEW_SIZE));
    }

    private static void layOut(final View view) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY);
        view.forceLayout();
        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, VIEW_SIZE, VIEW_SIZE);
    }

    public static final class InheritedListView extends ListView<BaseAdapter> {

        public InheritedListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public boolean exposedCanAnimate() {
            return canAnimate();
        }
    }

    private static final class RecordingItemListener
            implements android.widget.AdapterView.OnItemClickListener,
                    android.widget.AdapterView.OnItemLongClickListener,
                    android.widget.AdapterView.OnItemSelectedListener {

        @Override
        public void onItemClick(
                final android.widget.AdapterView<?> parent,
                final View view,
                final int position,
                final long id) {}

        @Override
        public boolean onItemLongClick(
                final android.widget.AdapterView<?> parent,
                final View view,
                final int position,
                final long id) {
            return false;
        }

        @Override
        public void onItemSelected(
                final android.widget.AdapterView<?> parent,
                final View view,
                final int position,
                final long id) {}

        @Override
        public void onNothingSelected(final android.widget.AdapterView<?> parent) {}
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
            if (convertView != null) return convertView;
            final FrameLayout view = new FrameLayout(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(CELL_SIZE, CELL_SIZE));
            return view;
        }
    }
}
