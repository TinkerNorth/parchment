// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * An adapter of identically sized views, labelled with their adapter position so a failure message
 * says which item landed where.
 */
public final class FixedSizeAdapter extends BaseAdapter {

    private final Context mContext;
    private final int mCount;
    private final int mItemWidth;
    private final int mItemHeight;

    public FixedSizeAdapter(
            final Context context, final int count, final int itemWidth, final int itemHeight) {
        mContext = context;
        mCount = count;
        mItemWidth = itemWidth;
        mItemHeight = itemHeight;
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
