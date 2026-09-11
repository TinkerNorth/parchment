// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.pageinterval;

import mobi.parchment.widget.adapterview.LayoutManager;

public interface PageIntervalInterface<Cell> {

    public long getPageCellIndexForward(
            final LayoutManager<Cell> layoutManager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart);

    public long getPageCellIndexBack(
            final LayoutManager<Cell> layoutManager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart);
}
