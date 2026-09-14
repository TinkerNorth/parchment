// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.listview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.AdapterViewInitializer;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.ChildTouchGestureListener;
import mobi.parchment.widget.adapterview.LayoutManager;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListViewInstrumentedTest {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 300;
    private static final int ITEM_WIDTH = 200;
    private static final int ITEM_HEIGHT = 100;
    private static final int ITEM_COUNT = 50;
    private static final int CENTERED_TOP = (HEIGHT - ITEM_HEIGHT) / 2;
    private static final int DRAG_DISTANCE = 45;

    @Test
    public void inflatedHorizontalListView_laysOutOnlyVisibleChildren() {
        final Context context = ApplicationProvider.getApplicationContext();
        final InflateAndLayOutHorizontally inflateAndLayOut =
                new InflateAndLayOutHorizontally(context);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(inflateAndLayOut);

        final ListView<BaseAdapter> listView = inflateAndLayOut.listView();
        assertNotNull(listView);
        final int childCount = listView.getChildCount();
        assertTrue("expected some children, got " + childCount, childCount > 0);
        assertTrue(
                "recycling should keep the child count near the viewport, got " + childCount,
                childCount < ITEM_COUNT);
        assertEquals(0, listView.getChildAt(0).getLeft());
        assertEquals(ITEM_WIDTH, listView.getChildAt(0).getWidth());
        assertEquals(0, listView.getPositionForView(listView.getChildAt(0)));
    }

    @Test
    public void animationFrames_moveTheChildrenWithoutALayoutPass() throws InterruptedException {
        final Context context = ApplicationProvider.getApplicationContext();
        final CreateAndLayOutVertically create = new CreateAndLayOutVertically(context);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(create);
        final FrameListView listView = create.listView();
        assertEquals(CENTERED_TOP, listView.getChildAt(0).getTop());
        assertFalse(listView.isLayoutRequested());

        final DragThenFrame dragThenFrame = new DragThenFrame(listView);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(dragThenFrame);
        assertEquals(CENTERED_TOP - DRAG_DISTANCE, listView.getChildAt(0).getTop());
        assertFalse(listView.isLayoutRequested());
        assertEquals(0, listView.getPositionForView(listView.getChildAt(0)));

        final FlingThenFrame flingThenFrame = new FlingThenFrame(listView);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(flingThenFrame);
        Thread.sleep(2000);
        final RunFrame runFrame = new RunFrame(listView);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runFrame);
        assertTrue(
                "the fling should have moved the content further than the drag",
                listView.getChildAt(0).getTop() < CENTERED_TOP - DRAG_DISTANCE
                        || listView.getPositionForView(listView.getChildAt(0)) > 0);
        assertFalse(listView.isLayoutRequested());
        assertTrue(listView.getChildCount() < ITEM_COUNT);
    }

    public static final class FrameListView extends ListView<BaseAdapter> {
        private ChildTouchGestureListener mGestureListener;

        public FrameListView(final Context context) {
            super(context);
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

        ChildTouchGestureListener gestureListener() {
            return mGestureListener;
        }

        void runFrame() {
            onAnimationFrame();
        }
    }

    private static final class InflateAndLayOutHorizontally implements Runnable {
        private final Context mContext;
        private ListView<BaseAdapter> mListView;

        InflateAndLayOutHorizontally(final Context context) {
            mContext = context;
        }

        ListView<BaseAdapter> listView() {
            return mListView;
        }

        @Override
        public void run() {
            final View inflated =
                    LayoutInflater.from(mContext).inflate(R.layout.list_view_horizontal, null);
            // The layout names the ListView, so the inflated root is that view.
            @SuppressWarnings("unchecked")
            final ListView<BaseAdapter> listView = (ListView<BaseAdapter>) inflated;
            listView.setAdapter(new FixedWidthAdapter(mContext));
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY);
            final int heightSpec =
                    View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY);
            listView.measure(widthSpec, heightSpec);
            listView.layout(0, 0, WIDTH, HEIGHT);
            mListView = listView;
        }
    }

    private static final class CreateAndLayOutVertically implements Runnable {
        private final Context mContext;
        private FrameListView mListView;

        CreateAndLayOutVertically(final Context context) {
            mContext = context;
        }

        FrameListView listView() {
            return mListView;
        }

        @Override
        public void run() {
            mListView = new FrameListView(mContext);
            mListView.setAdapter(new FixedHeightAdapter(mContext));
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY);
            final int heightSpec =
                    View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY);
            mListView.measure(widthSpec, heightSpec);
            mListView.layout(0, 0, WIDTH, HEIGHT);
        }
    }

    private static final class DragThenFrame implements Runnable {
        private final FrameListView mListView;

        DragThenFrame(final FrameListView listView) {
            mListView = listView;
        }

        @Override
        public void run() {
            final MotionEvent down =
                    MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0);
            final MotionEvent move =
                    MotionEvent.obtain(0, 10, MotionEvent.ACTION_MOVE, 100f, 155f, 0);
            mListView.gestureListener().onDown(down);
            mListView.gestureListener().onScroll(down, move, 0f, 45f);
            mListView.runFrame();
        }
    }

    private static final class FlingThenFrame implements Runnable {
        private final FrameListView mListView;

        FlingThenFrame(final FrameListView listView) {
            mListView = listView;
        }

        @Override
        public void run() {
            final MotionEvent down =
                    MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0);
            final MotionEvent up = MotionEvent.obtain(0, 40, MotionEvent.ACTION_UP, 100f, 100f, 0);
            mListView.gestureListener().onDown(down);
            mListView.gestureListener().onFling(down, up, 0f, -2000f);
            mListView.gestureListener().onUp();
            mListView.runFrame();
        }
    }

    private static final class RunFrame implements Runnable {
        private final FrameListView mListView;

        RunFrame(final FrameListView listView) {
            mListView = listView;
        }

        @Override
        public void run() {
            mListView.runFrame();
        }
    }

    private static final class FixedHeightAdapter extends BaseAdapter {
        private final Context context;

        FixedHeightAdapter(final Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
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
            final TextView view =
                    convertView instanceof TextView
                            ? (TextView) convertView
                            : new TextView(context);
            view.setText(String.valueOf(position));
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ITEM_HEIGHT));
            return view;
        }
    }

    private static final class FixedWidthAdapter extends BaseAdapter {
        private final Context context;

        FixedWidthAdapter(final Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
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
            final TextView view =
                    convertView instanceof TextView
                            ? (TextView) convertView
                            : new TextView(context);
            view.setText(String.valueOf(position));
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(ITEM_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }
}
