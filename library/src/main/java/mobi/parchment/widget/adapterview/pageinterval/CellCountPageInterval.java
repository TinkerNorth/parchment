// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.pageinterval;

import mobi.parchment.widget.adapterview.LayoutManager;

public final class CellCountPageInterval<Cell> implements PageIntervalInterface<Cell> {

    private final int mViewPagerInterval;

    public CellCountPageInterval(final int viewPagerInterval) {
        mViewPagerInterval = viewPagerInterval;
    }

    @Override
    public long getPageCellIndexForward(
            final LayoutManager<Cell> layoutManager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart) {
        return (long) anchorIndex + mViewPagerInterval;
    }

    @Override
    public long getPageCellIndexBack(
            final LayoutManager<Cell> layoutManager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart) {
        return (long) anchorIndex - mViewPagerInterval;
    }
}
