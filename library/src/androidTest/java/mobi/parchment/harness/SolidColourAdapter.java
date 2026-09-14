// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An adapter of identically sized views filled with one solid colour, so a test that reads pixels
 * can tell a cell from whatever a view paints over it.
 */
public final class SolidColourAdapter extends BaseAdapter {

    private final Context mContext;
    private final int mCount;
    private final int mItemWidth;
    private final int mItemHeight;
    private final int mColour;

    public SolidColourAdapter(
            final Context context,
            final int count,
            final int itemWidth,
            final int itemHeight,
            final int colour) {
        mContext = context;
        mCount = count;
        mItemWidth = itemWidth;
        mItemHeight = itemHeight;
        mColour = colour;
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
        final View view = convertView == null ? new View(mContext) : convertView;
        view.setBackgroundColor(mColour);
        sizeTo(view, mItemWidth, mItemHeight);
        return view;
    }

    private static void sizeTo(final View view, final int width, final int height) {
        final ViewGroup.LayoutParams existing = view.getLayoutParams();
        if (existing == null) {
            giveNewLayoutParams(view, width, height);
        } else {
            resizeLayoutParams(existing, width, height);
        }
    }

    private static void giveNewLayoutParams(final View view, final int width, final int height) {
        view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
    }

    private static void resizeLayoutParams(
            final ViewGroup.LayoutParams layoutParams, final int width, final int height) {
        layoutParams.width = width;
        layoutParams.height = height;
    }
}
