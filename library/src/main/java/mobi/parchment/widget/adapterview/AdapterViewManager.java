// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AdapterViewManager {

    private final DataSetObserverManager mDataSetObserverManager = new DataSetObserverManager();
    private final Map<View, Integer> mViewTypeMap = new HashMap<View, Integer>();
    private Adapter mAdapter;
    private List<Queue<View>> mViews = new ArrayList<Queue<View>>();

    public void recycle(final View removedView) {
        final Integer type = mViewTypeMap.remove(removedView);
        if (type == null) {
            return;
        }
        final Queue<View> views = mViews.get(type);
        views.add(removedView);
    }

    public View getView(
            final ViewGroup viewGroup,
            final int position,
            final int widthMeasureSpec,
            final int heightMeasureSpec) {
        final int type = mAdapter.getItemViewType(position);

        final View view = getView(viewGroup, position, type, widthMeasureSpec, heightMeasureSpec);
        mViewTypeMap.put(view, type);

        return view;
    }

    private View getView(
            final ViewGroup viewGroup,
            final int position,
            final int type,
            final int widthMeasureSpec,
            final int heightMeasureSpec) {
        final Queue<View> views = mViews.get(type);
        final View convertView = views.poll();
        final View view = mAdapter.getView(position, convertView, viewGroup);
        final boolean isRecycled = view == convertView;
        if (!isRecycled || view.isLayoutRequested()) {
            measureView(view, widthMeasureSpec, heightMeasureSpec);
        }

        return view;
    }

    private <KEY, VALUE> void remove(final Map<KEY, VALUE> map, final VALUE value) {
        if (map == null || value == null) return;

        if (map.containsValue(value)) {
            final KEY key = getKeyForValue(map, value);
            if (key != null) {
                map.remove(key);
            }
        }
    }

    private <KEY, VALUE> KEY getKeyForValue(final Map<KEY, VALUE> map, final VALUE value) {
        if (value == null) {
            return null;
        }

        for (final KEY key : map.keySet()) {
            final VALUE valueToReset = map.get(key);
            if (value == valueToReset) {
                return key;
            }
        }

        return null;
    }

    public LayoutParams measureView(
            final View view, final int widthMeasureSpec, final int heightMeasureSpec) {
        LayoutParams layoutParams = view.getLayoutParams();

        if (layoutParams == null) {
            layoutParams = new LayoutParams(widthMeasureSpec, heightMeasureSpec);
            view.setLayoutParams(layoutParams);
        }

        final int childWidthMeasureSpec =
                ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, layoutParams.width);
        final int childHeightMeasureSpec =
                ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, layoutParams.height);

        view.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        return layoutParams;
    }

    public int getAdapterCount() {
        if (mAdapter == null) return 0;
        return mAdapter.getCount();
    }

    public boolean isEmpty() {
        return getAdapterCount() == 0;
    }

    public void setAdapter(final Adapter adapter) {
        mDataSetObserverManager.setAdapter(adapter);
        mAdapter = adapter;

        final int typeCount = adapter.getViewTypeCount();

        mViews = new ArrayList<Queue<View>>(typeCount);
        for (int index = 0; index < typeCount; index++) mViews.add(new LinkedList<View>());

        mViewTypeMap.clear();
    }

    public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
        mDataSetObserverManager.registerDataSetObserver(dataSetObserver);
    }

    public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
        mDataSetObserverManager.unregisterDataSetObserver(dataSetObserver);
    }
}
