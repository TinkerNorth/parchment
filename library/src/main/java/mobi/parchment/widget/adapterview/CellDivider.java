// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

public final class CellDivider {

    protected static final int INTRINSIC_SIZE = -1;

    private static final int NO_DIVIDER_SIZE = 0;
    private static final int FIRST_CELL_WITH_A_DIVIDER_BEFORE_IT = 1;
    private static final int ONE_CELL = 1;
    private static final int HALVES = 2;

    private final Rect mBounds = new Rect();
    private final ScrollDirectionManager mScrollDirectionManager;
    private final Drawable mDivider;
    private final int mDividerSize;

    public CellDivider(
            final Drawable divider,
            final int dividerSize,
            final ScrollDirectionManager scrollDirectionManager) {
        mDivider = divider;
        mScrollDirectionManager = scrollDirectionManager;
        mDividerSize = getResolvedDividerSize(divider, dividerSize, scrollDirectionManager);
    }

    private static int getResolvedDividerSize(
            final Drawable divider,
            final int dividerSize,
            final ScrollDirectionManager scrollDirectionManager) {
        if (divider == null) return NO_DIVIDER_SIZE;

        final boolean isDividerSizeDeclared = dividerSize != INTRINSIC_SIZE;
        if (isDividerSizeDeclared) {
            return getDeclaredDividerSize(dividerSize);
        }
        return getIntrinsicDividerSize(divider, scrollDirectionManager);
    }

    private static int getDeclaredDividerSize(final int dividerSize) {
        return Math.max(dividerSize, NO_DIVIDER_SIZE);
    }

    private static int getIntrinsicDividerSize(
            final Drawable divider, final ScrollDirectionManager scrollDirectionManager) {
        final int intrinsicSize = scrollDirectionManager.getDrawableSize(divider);
        return Math.max(intrinsicSize, NO_DIVIDER_SIZE);
    }

    protected static int getDividerStart(
            final int gapStart, final int gapEnd, final int dividerSize) {
        final int gapSize = gapEnd - gapStart;
        final int halfGapSize = gapSize / HALVES;
        final int gapMiddle = gapStart + halfGapSize;
        final int halfDividerSize = dividerSize / HALVES;
        final int dividerStart = gapMiddle - halfDividerSize;
        return dividerStart;
    }

    public void draw(
            final Canvas canvas, final ViewGroup viewGroup, final LayoutManager<?> layoutManager) {
        if (mDividerSize == NO_DIVIDER_SIZE) return;

        final int breadth =
                mScrollDirectionManager.getDrawBreadth(
                        viewGroup.getLeft(),
                        viewGroup.getTop(),
                        viewGroup.getRight(),
                        viewGroup.getBottom());
        final int breadthStart = layoutManager.getStartBreadthPadding();
        final int endBreadthPadding = layoutManager.getEndBreadthPadding();
        final int breadthEnd = breadth - endBreadthPadding;
        final int cellCount = layoutManager.getDrawnCellCount();

        for (int cellIndex = FIRST_CELL_WITH_A_DIVIDER_BEFORE_IT;
                cellIndex < cellCount;
                cellIndex++) {
            final int previousCellIndex = cellIndex - ONE_CELL;
            final int gapStart = layoutManager.getDrawnCellEnd(previousCellIndex);
            final int gapEnd = layoutManager.getDrawnCellStart(cellIndex);
            final int dividerStart = getDividerStart(gapStart, gapEnd, mDividerSize);
            final int dividerEnd = dividerStart + mDividerSize;

            mScrollDirectionManager.setDrawBounds(
                    mBounds, dividerStart, dividerEnd, breadthStart, breadthEnd);
            mDivider.setBounds(mBounds);
            mDivider.draw(canvas);
        }
    }
}
