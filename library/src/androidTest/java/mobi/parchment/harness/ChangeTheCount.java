// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.widget.BaseAdapter;
import mobi.parchment.widget.adapterview.AbstractAdapterView;

public final class ChangeTheCount<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
        implements ParchmentViewHarness.ViewSetup<VIEW> {

    private final ResizableAdapter mAdapter;
    private final int mItemCount;

    public ChangeTheCount(final ResizableAdapter adapter, final int itemCount) {
        mAdapter = adapter;
        mItemCount = itemCount;
    }

    @Override
    public void setUp(final VIEW view) {
        mAdapter.setCount(mItemCount);
    }
}
