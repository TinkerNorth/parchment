// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import mobi.parchment.widget.adapterview.divideraxis.BreadthDividerAxis;
import mobi.parchment.widget.adapterview.divideraxis.DividerAxisInterface;
import mobi.parchment.widget.adapterview.divideraxis.SizeDividerAxis;

public final class CellDivider {

    protected static final int INTRINSIC_SIZE = -1;

    private static final int NO_DIVIDER_SIZE = 0;
    private static final int FIRST_CELL = 0;
    private static final int ONE_CELL = 1;
    private static final int FIRST_VIEW = 0;
    private static final int HALVES = 2;

    private final Rect mBounds = new Rect();
    private final DividerAxisInterface mSizeAxis;
    private final DividerAxisInterface mBreadthAxis;
    private final Drawable mDivider;
    private final int mDividerSize;

    public CellDivider(
            final Drawable divider,
            final int dividerSize,
            final ScrollDirectionManager scrollDirectionManager) {
        mDivider = divider;
        mSizeAxis = new SizeDividerAxis(scrollDirectionManager);
        mBreadthAxis = new BreadthDividerAxis(scrollDirectionManager);
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

    protected static int getLastCandidateCellIndex(final int cellIndex, final int cellCount) {
        final int nextCellIndex = cellIndex + ONE_CELL;
        final int lastCellIndex = cellCount - ONE_CELL;
        return Math.min(nextCellIndex, lastCellIndex);
    }

    public void draw(final Canvas canvas, final LayoutManager<?> layoutManager) {
        if (mDividerSize == NO_DIVIDER_SIZE) return;

        final int cellCount = layoutManager.getDrawnCellCount();
        for (int cellIndex = FIRST_CELL; cellIndex < cellCount; cellIndex++) {
            drawCellEdges(canvas, layoutManager, cellIndex, cellCount);
        }
    }

    private void drawCellEdges(
            final Canvas canvas,
            final LayoutManager<?> layoutManager,
            final int cellIndex,
            final int cellCount) {
        final int viewCount = layoutManager.getDrawnCellViewCount(cellIndex);
        for (int viewIndex = FIRST_VIEW; viewIndex < viewCount; viewIndex++) {
            final View item = layoutManager.getDrawnCellView(cellIndex, viewIndex);
            drawEdges(canvas, layoutManager, item, cellIndex, cellCount, mSizeAxis);
            drawEdges(canvas, layoutManager, item, cellIndex, cellCount, mBreadthAxis);
        }
    }

    private void drawEdges(
            final Canvas canvas,
            final LayoutManager<?> layoutManager,
            final View item,
            final int cellIndex,
            final int cellCount,
            final DividerAxisInterface axis) {
        final int lastCellIndex = getLastCandidateCellIndex(cellIndex, cellCount);
        for (int candidateCellIndex = cellIndex;
                candidateCellIndex <= lastCellIndex;
                candidateCellIndex++) {
            final int candidateCount = layoutManager.getDrawnCellViewCount(candidateCellIndex);
            for (int candidateIndex = FIRST_VIEW;
                    candidateIndex < candidateCount;
                    candidateIndex++) {
                final View neighbour =
                        layoutManager.getDrawnCellView(candidateCellIndex, candidateIndex);
                drawEdge(canvas, layoutManager, item, neighbour, cellIndex, lastCellIndex, axis);
            }
        }
    }

    private void drawEdge(
            final Canvas canvas,
            final LayoutManager<?> layoutManager,
            final View item,
            final View neighbour,
            final int cellIndex,
            final int lastCellIndex,
            final DividerAxisInterface axis) {
        final int itemStart = axis.getStart(item);
        final int itemEnd = axis.getEnd(item);
        final int neighbourStart = axis.getStart(neighbour);
        final int neighbourEnd = axis.getEnd(neighbour);
        final boolean isAcrossTheEndEdge =
                CellEdges.isAcrossTheEndEdge(itemStart, itemEnd, neighbourStart, neighbourEnd);
        if (!isAcrossTheEndEdge) return;

        final int itemBandStart = axis.getBandStart(item);
        final int itemBandEnd = axis.getBandEnd(item);
        final int neighbourBandStart = axis.getBandStart(neighbour);
        final int neighbourBandEnd = axis.getBandEnd(neighbour);
        final int bandStart = CellEdges.getOverlapStart(itemBandStart, neighbourBandStart);
        final int bandEnd = CellEdges.getOverlapEnd(itemBandEnd, neighbourBandEnd);
        if (!CellEdges.isAnOverlap(bandStart, bandEnd)) return;

        final boolean isOccupied =
                isGapOccupied(
                        layoutManager,
                        cellIndex,
                        lastCellIndex,
                        itemStart,
                        itemEnd,
                        neighbourStart,
                        neighbourEnd,
                        bandStart,
                        bandEnd,
                        axis);
        if (isOccupied) return;

        final int dividerStart = getDividerStart(itemEnd, neighbourStart, mDividerSize);
        final int dividerEnd = dividerStart + mDividerSize;
        axis.setDividerBounds(mBounds, dividerStart, dividerEnd, bandStart, bandEnd);
        mDivider.setBounds(mBounds);
        mDivider.draw(canvas);
    }

    private boolean isGapOccupied(
            final LayoutManager<?> layoutManager,
            final int cellIndex,
            final int lastCellIndex,
            final int itemStart,
            final int itemEnd,
            final int neighbourStart,
            final int neighbourEnd,
            final int bandStart,
            final int bandEnd,
            final DividerAxisInterface axis) {
        for (int candidateCellIndex = cellIndex;
                candidateCellIndex <= lastCellIndex;
                candidateCellIndex++) {
            final int candidateCount = layoutManager.getDrawnCellViewCount(candidateCellIndex);
            for (int candidateIndex = FIRST_VIEW;
                    candidateIndex < candidateCount;
                    candidateIndex++) {
                final View candidate =
                        layoutManager.getDrawnCellView(candidateCellIndex, candidateIndex);
                final int candidateStart = axis.getStart(candidate);
                final int candidateEnd = axis.getEnd(candidate);
                final boolean isInTheGap =
                        CellEdges.isInTheGap(
                                itemStart,
                                itemEnd,
                                neighbourStart,
                                neighbourEnd,
                                candidateStart,
                                candidateEnd);
                if (!isInTheGap) continue;

                final int candidateBandStart = axis.getBandStart(candidate);
                final int candidateBandEnd = axis.getBandEnd(candidate);
                final boolean reachesTheBand =
                        CellEdges.reachesTheBand(
                                bandStart, bandEnd, candidateBandStart, candidateBandEnd);
                if (reachesTheBand) return true;
            }
        }
        return false;
    }
}
