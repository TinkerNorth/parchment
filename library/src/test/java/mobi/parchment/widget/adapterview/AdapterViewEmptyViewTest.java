// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * {@code setEmptyView(View)} past the moment it is called: on the first layout, when an adapter
 * arrives, when the data set changes in either direction, and when a view is attached after a
 * change it missed while detached. A real {@code android.widget.ListView} runs beside the Parchment
 * one through the same sequence.
 */
@RunWith(RobolectricTestRunner.class)
public class AdapterViewEmptyViewTest {

    private static final int VIEW_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int ONE_CALL = 1;

    private Activity mActivity;
    private FrameLayout mContent;

    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(Activity.class).setup().get();
        mContent = new FrameLayout(mActivity);
        mActivity.setContentView(mContent);
    }

    @Test
    public void theFrameworksOwnEmptyStatusAndFocusCheck_areOutOfReach() throws Exception {
        final Method updateEmptyStatus =
                android.widget.AdapterView.class.getDeclaredMethod(
                        "updateEmptyStatus", boolean.class);
        final Method checkFocus = android.widget.AdapterView.class.getDeclaredMethod("checkFocus");

        assertThat(Modifier.isPrivate(updateEmptyStatus.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(checkFocus.getModifiers())).isFalse();
        assertThat(Modifier.isProtected(checkFocus.getModifiers())).isFalse();
        assertThat(Modifier.isPrivate(checkFocus.getModifiers())).isFalse();
        assertThat(android.widget.AdapterView.class.getPackage().getName())
                .isNotEqualTo(AbstractAdapterView.class.getPackage().getName());
    }

    @Test
    public void anEmptyAdapterAtLayout_showsTheEmptyViewAndHidesTheAdapterView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();

        listView.setEmptyView(emptyView);
        listView.setAdapter(new ResizableAdapter(mActivity, 0));
        layOut(listView);

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void anEmptyViewSetBeforeAnAdapterWithItems_isHiddenWhenTheAdapterArrives() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();

        listView.setEmptyView(emptyView);
        listView.setAdapter(new ResizableAdapter(mActivity, 3));
        layOut(listView);

        assertThat(emptyView.getVisibility()).isEqualTo(View.GONE);
        assertThat(listView.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void anEmptyViewSetAfterAnEmptyAdapter_isShownStraightAway() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        listView.setAdapter(new ResizableAdapter(mActivity, 0));
        layOut(listView);

        listView.setEmptyView(emptyView);

        assertThat(listView.getEmptyView()).isSameAs(emptyView);
        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void aDataSetChangeThatEmptiesTheAdapter_showsTheEmptyView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        layOut(listView);
        assertThat(listView.getVisibility()).isEqualTo(View.VISIBLE);

        adapter.setCount(0);

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void aDataSetChangeThatRefillsTheAdapter_hidesTheEmptyView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 0);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        layOut(listView);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);

        adapter.setCount(3);

        assertThat(emptyView.getVisibility()).isEqualTo(View.GONE);
        assertThat(listView.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void anInvalidatedAdapter_showsTheEmptyView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        layOut(listView);

        adapter.emptyAndInvalidate();

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void replacingTheAdapterWithAnEmptyOne_showsTheEmptyView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        listView.setAdapter(new ResizableAdapter(mActivity, 3));
        listView.setEmptyView(emptyView);
        layOut(listView);

        listView.setAdapter(new ResizableAdapter(mActivity, 0));

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void theEmptyViewFollowsTheAdapterLikeThePlatformListView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final View platformEmptyView = attachedEmptyView();
        final android.widget.ListView platformListView = attachedPlatformListView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        final ResizableAdapter platformAdapter = new ResizableAdapter(mActivity, 3);

        listView.setEmptyView(emptyView);
        platformListView.setEmptyView(platformEmptyView);
        listView.setAdapter(adapter);
        platformListView.setAdapter(platformAdapter);
        assertVisibilitiesMatch(listView, emptyView, platformListView, platformEmptyView);

        adapter.setCount(0);
        platformAdapter.setCount(0);
        assertVisibilitiesMatch(listView, emptyView, platformListView, platformEmptyView);

        adapter.setCount(4);
        platformAdapter.setCount(4);
        assertVisibilitiesMatch(listView, emptyView, platformListView, platformEmptyView);
    }

    @Test
    public void withNoEmptyView_aDataSetChangeLeavesTheAdapterViewsVisibilityAlone() {
        final EmptyViewListView listView = attachedListView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        layOut(listView);
        listView.setVisibility(View.INVISIBLE);

        adapter.setCount(0);

        assertThat(listView.getEmptyView()).isNull();
        assertThat(listView.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void withNoEmptyView_settingAnAdapterLeavesTheAdapterViewsVisibilityAlone() {
        final EmptyViewListView listView = attachedListView();
        listView.setVisibility(View.INVISIBLE);

        listView.setAdapter(new ResizableAdapter(mActivity, 3));

        assertThat(listView.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void afterTheEmptyViewIsTakenAway_aDataSetChangeLeavesTheVisibilityAlone() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        layOut(listView);

        listView.setEmptyView(null);
        listView.setVisibility(View.INVISIBLE);
        adapter.setCount(0);

        assertThat(listView.getEmptyView()).isNull();
        assertThat(listView.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void aDataSetChangeWhileDetached_isPickedUpWhenTheViewIsAttached() {
        final EmptyViewListView listView = new EmptyViewListView(mActivity, null);
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        assertThat(listView.isAttachedToWindow()).isFalse();

        adapter.setCount(0);
        attach(listView);

        assertThat(listView.isAttachedToWindow()).isTrue();
        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void aRefillWhileDetached_isPickedUpWhenTheViewIsAttached() {
        final EmptyViewListView listView = new EmptyViewListView(mActivity, null);
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 0);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);

        adapter.setCount(3);
        attach(listView);

        assertThat(emptyView.getVisibility()).isEqualTo(View.GONE);
        assertThat(listView.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void setAdapterToNull_showsTheEmptyView() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        listView.setAdapter(new ResizableAdapter(mActivity, 3));
        listView.setEmptyView(emptyView);
        layOut(listView);
        assertThat(listView.getVisibility()).isEqualTo(View.VISIBLE);

        listView.setAdapter(null);

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void aDataSetChangeWhileDetached_isPickedUpWhereAPlatformListViewMissesIt() {
        final EmptyViewListView listView = new EmptyViewListView(mActivity, null);
        final View emptyView = attachedEmptyView();
        final View platformEmptyView = attachedEmptyView();
        final android.widget.ListView platformListView = attachedPlatformListView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        final ResizableAdapter platformAdapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        platformListView.setAdapter(platformAdapter);
        platformListView.setEmptyView(platformEmptyView);
        mContent.removeView(platformListView);

        adapter.setCount(0);
        platformAdapter.setCount(0);
        attach(listView);
        attach(platformListView);

        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(platformListView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(platformEmptyView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void aGridViewsEmptyView_followsItsAdapterTheSameWay() {
        final EmptyViewGridView gridView =
                (EmptyViewGridView)
                        View.inflate(mActivity, R.layout.surface_empty_view_grid_view, null);
        attach(gridView);
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 4);
        gridView.setAdapter(adapter);
        gridView.setEmptyView(emptyView);
        layOut(gridView);
        assertThat(gridView.getVisibility()).isEqualTo(View.VISIBLE);

        adapter.setCount(0);

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(gridView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void aGridPatternViewsEmptyView_followsItsAdapterTheSameWay() {
        final EmptyViewGridPatternView patternView = attachedGridPatternView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 4);
        patternView.setAdapter(adapter);
        patternView.setEmptyView(emptyView);
        layOut(patternView);
        assertThat(patternView.getVisibility()).isEqualTo(View.VISIBLE);

        adapter.setCount(0);

        assertThat(emptyView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(patternView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void reEvaluatingTheEmptyView_neverCallsSetEmptyViewAgain() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        layOut(listView);

        adapter.setCount(0);
        adapter.setCount(3);
        listView.setAdapter(new ResizableAdapter(mActivity, 2));

        assertThat(listView.setEmptyViewCalls()).isEqualTo(ONE_CALL);
    }

    @Test
    public void reEvaluatingTheEmptyView_drivesNoLayoutPassOfItsOwn() {
        final EmptyViewListView listView = attachedListView();
        final View emptyView = attachedEmptyView();
        final ResizableAdapter adapter = new ResizableAdapter(mActivity, 3);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        layOut(listView);
        listView.resetLayoutPasses();

        adapter.setCount(0);

        assertThat(listView.getVisibility()).isEqualTo(View.GONE);
        assertThat(listView.layoutPasses()).isEqualTo(0);
    }

    private static void assertVisibilitiesMatch(
            final EmptyViewListView listView,
            final View emptyView,
            final android.widget.ListView platformListView,
            final View platformEmptyView) {
        assertThat(emptyView.getVisibility()).isEqualTo(platformEmptyView.getVisibility());
        assertThat(listView.getVisibility()).isEqualTo(platformListView.getVisibility());
    }

    private View attachedEmptyView() {
        final View emptyView = new View(mActivity);
        attach(emptyView);
        return emptyView;
    }

    private EmptyViewListView attachedListView() {
        final EmptyViewListView listView =
                (EmptyViewListView)
                        View.inflate(mActivity, R.layout.surface_empty_view_list_view, null);
        attach(listView);
        return listView;
    }

    private EmptyViewGridPatternView attachedGridPatternView() {
        final EmptyViewGridPatternView patternView =
                (EmptyViewGridPatternView)
                        View.inflate(
                                mActivity, R.layout.surface_empty_view_grid_pattern_view, null);
        final List<GridPatternItemDefinition> itemDefinitions =
                new ArrayList<GridPatternItemDefinition>();
        itemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
        itemDefinitions.add(new GridPatternItemDefinition(1, 0, 1, 1));
        patternView.addGridPatternGroupDefinition(itemDefinitions);
        attach(patternView);
        return patternView;
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

    /** A {@link ListView} that records what the framework and the view itself asked of it. */
    public static final class EmptyViewListView extends ListView<BaseAdapter> {
        private int mSetEmptyViewCalls;
        private int mLayoutPasses;

        public EmptyViewListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public int setEmptyViewCalls() {
            return mSetEmptyViewCalls;
        }

        public int layoutPasses() {
            return mLayoutPasses;
        }

        public void resetLayoutPasses() {
            mLayoutPasses = 0;
        }

        @Override
        public void setEmptyView(final View emptyView) {
            mSetEmptyViewCalls++;
            super.setEmptyView(emptyView);
        }

        @Override
        protected void onLayout(
                final boolean changed,
                final int left,
                final int top,
                final int right,
                final int bottom) {
            mLayoutPasses++;
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    public static final class EmptyViewGridView extends GridView<BaseAdapter> {
        public EmptyViewGridView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }
    }

    public static final class EmptyViewGridPatternView extends GridPatternView<BaseAdapter> {
        public EmptyViewGridPatternView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
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

        private void emptyAndInvalidate() {
            mCount = 0;
            notifyDataSetInvalidated();
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
