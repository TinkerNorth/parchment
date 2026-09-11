// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.pageinterval;

import mobi.parchment.widget.adapterview.LayoutManager;

public final class ViewportPageInterval<Cell> implements PageIntervalInterface<Cell> {

    private static final int ONE_CELL = 1;

    @Override
    public long getPageCellIndexForward(
            final LayoutManager<Cell> layoutManager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart) {
        final int cellSpacing = layoutManager.getCellSpacing();
        long pageCellIndex = anchorIndex + ONE_CELL;
        int pageCellStart = layoutManager.getCellStartAtIndex(pageCellIndex);
        long nextCellIndex = pageCellIndex + ONE_CELL;
        int nextCellStart = layoutManager.getCellStartAtIndex(nextCellIndex);

        while (nextCellJoinsThePage(
                maximumPageSize, cellSpacing, anchorStart, pageCellStart, nextCellStart)) {
            pageCellIndex = nextCellIndex;
            pageCellStart = nextCellStart;
            nextCellIndex = pageCellIndex + ONE_CELL;
            nextCellStart = layoutManager.getCellStartAtIndex(nextCellIndex);
        }

        return pageCellIndex;
    }

    @Override
    public long getPageCellIndexBack(
            final LayoutManager<Cell> layoutManager,
            final int maximumPageSize,
            final int anchorIndex,
            final int anchorStart) {
        final int cellSpacing = layoutManager.getCellSpacing();
        long pageCellIndex = anchorIndex - ONE_CELL;
        int pageCellStart = layoutManager.getCellStartAtIndex(pageCellIndex);
        long previousCellIndex = pageCellIndex - ONE_CELL;
        int previousCellStart = layoutManager.getCellStartAtIndex(previousCellIndex);

        while (previousCellJoinsThePage(
                maximumPageSize, cellSpacing, anchorStart, pageCellStart, previousCellStart)) {
            pageCellIndex = previousCellIndex;
            pageCellStart = previousCellStart;
            previousCellIndex = pageCellIndex - ONE_CELL;
            previousCellStart = layoutManager.getCellStartAtIndex(previousCellIndex);
        }

        return pageCellIndex;
    }

    public static boolean nextCellJoinsThePage(
            final int maximumPageSize,
            final int cellSpacing,
            final int pageStart,
            final int pageCellStart,
            final int nextCellStart) {
        final boolean hasAFurtherCell = nextCellStart > pageCellStart;
        if (!hasAFurtherCell) return false;
        return pageFits(maximumPageSize, cellSpacing, pageStart, nextCellStart);
    }

    public static boolean previousCellJoinsThePage(
            final int maximumPageSize,
            final int cellSpacing,
            final int pageLimitStart,
            final int pageCellStart,
            final int previousCellStart) {
        final boolean hasAFurtherCell = previousCellStart < pageCellStart;
        if (!hasAFurtherCell) return false;
        return pageFits(maximumPageSize, cellSpacing, previousCellStart, pageLimitStart);
    }

    public static boolean pageFits(
            final int maximumPageSize,
            final int cellSpacing,
            final long pageStart,
            final long pageLimitStart) {
        final long pageEnd = pageLimitStart - cellSpacing;
        final long pageSize = pageEnd - pageStart;
        return pageSize <= maximumPageSize;
    }
}
