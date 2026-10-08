// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public final class ResizableAdapter extends BaseAdapter {

    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

    private final FixedSizeAdapter mItems;
    private int mCount;

    public ResizableAdapter(final Context context, final int count) {
        mItems = new FixedSizeAdapter(context, count, MATCH_PARENT, MATCH_PARENT);
        mCount = count;
    }

    public void setCount(final int count) {
        mCount = count;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(final int position) {
        return mItems.getItem(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        return mItems.getView(position, convertView, parent);
    }
}
