// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * An adapter of identically sized views that remembers the widest range of positions the layout
 * engine asked it for, so a test can prove that a wrapped position never reaches the adapter.
 */
public final class PositionRecordingAdapter extends BaseAdapter {

    private final Context mContext;
    private final int mCount;
    private final int mItemWidth;
    private final int mItemHeight;

    // Written on the main thread by getView and read from the instrumentation thread.
    private volatile int mLowestRequestedPosition = Integer.MAX_VALUE;
    private volatile int mHighestRequestedPosition = Integer.MIN_VALUE;

    public PositionRecordingAdapter(
            final Context context, final int count, final int itemWidth, final int itemHeight) {
        mContext = context;
        mCount = count;
        mItemWidth = itemWidth;
        mItemHeight = itemHeight;
    }

    public int lowestRequestedPosition() {
        return mLowestRequestedPosition;
    }

    public int highestRequestedPosition() {
        return mHighestRequestedPosition;
    }

    public String requestedRange() {
        return mLowestRequestedPosition + ".." + mHighestRequestedPosition;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(final int position) {
        return Integer.valueOf(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        mLowestRequestedPosition = Math.min(mLowestRequestedPosition, position);
        mHighestRequestedPosition = Math.max(mHighestRequestedPosition, position);
        final TextView view =
                convertView instanceof TextView ? (TextView) convertView : new TextView(mContext);
        view.setText(String.valueOf(position));
        sizeTo(view, mItemWidth, mItemHeight);
        return view;
    }

    private static void sizeTo(final View view, final int width, final int height) {
        final ViewGroup.LayoutParams existing = view.getLayoutParams();
        if (existing == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            return;
        }
        existing.width = width;
        existing.height = height;
    }
}
