// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.pageinterval;

public final class PageIntervalSelector {

    private static final int VIEWPORT_VIEW_PAGER_INTERVAL = 0;

    private PageIntervalSelector() {}

    public static boolean pagesByCellCount(final int viewPagerInterval) {
        return viewPagerInterval != VIEWPORT_VIEW_PAGER_INTERVAL;
    }

    public static <Cell> PageIntervalInterface<Cell> getPageIntervalInterface(
            final int viewPagerInterval) {
        if (pagesByCellCount(viewPagerInterval)) {
            return new CellCountPageInterval<Cell>(viewPagerInterval);
        }
        return new ViewportPageInterval<Cell>();
    }
}
