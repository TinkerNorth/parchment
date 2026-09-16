// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import android.view.View;
import java.util.List;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.Move;
import mobi.parchment.widget.adapterview.ScrollDirectionManager;

public final class ScrollWithinContentSnapPosition<Cell> implements SnapPositionInterface<Cell> {

    private static final int FIRST_ADAPTER_POSITION = 0;
    private static final int NO_LOWEST_CELL_START = Integer.MIN_VALUE;
    private static final int NO_HIGHEST_CELL_START = Integer.MAX_VALUE;

    private final SnapPositionInterface<Cell> mSnapPosition;

    public ScrollWithinContentSnapPosition(final SnapPositionInterface<Cell> snapPosition) {
        mSnapPosition = snapPosition;
    }

    @Override
    public int getDrawLimitMoveForwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int contentStartLimit = getContentStartLimit(layoutManager, cells, size);
        final int cellSize = layoutManager.getCellSize(cell);
        return contentStartLimit + cellSize;
    }

    @Override
    public int getDrawLimitMoveBackwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int contentEndLimit = getContentEndLimit(layoutManager, cells, size);
        final int cellSize = layoutManager.getCellSize(cell);
        return contentEndLimit - cellSize;
    }

    @Override
    public int getCellDistanceFromSnapPosition(
            final LayoutManager<Cell> layoutManager, final int size, final Cell cell) {
        return mSnapPosition.getCellDistanceFromSnapPosition(layoutManager, size, cell);
    }

    @Override
    public int getCellSettleDistance(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int snapDistance = getSnapToPixelDistance(layoutManager, cells, size, cell);
        return Math.abs(snapDistance);
    }

    @Override
    public int getSnapToPixelDistance(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int snapDistance = getUnboundedSnapToPixelDistance(layoutManager, size, cell);
        final int cellStart = layoutManager.getCellStart(cell);
        final int snappedCellStart = cellStart + snapDistance;
        final int lowestCellStart = getLowestCellStart(layoutManager, cells, size, cell);
        final int highestCellStart = getHighestCellStart(layoutManager, cells, size, cell);
        final int reachableCellStart = clamp(snappedCellStart, lowestCellStart, highestCellStart);
        return reachableCellStart - cellStart;
    }

    @Override
    public int getUnboundedSnapToPixelDistance(
            final LayoutManager<Cell> layoutManager, final int size, final Cell cell) {
        return mSnapPosition.getUnboundedSnapToPixelDistance(layoutManager, size, cell);
    }

    @Override
    public int getRedrawOffset(
            final ScrollDirectionManager scrollDirectionManager,
            final View incomingView,
            final View outgoingView) {
        return mSnapPosition.getRedrawOffset(scrollDirectionManager, incomingView, outgoingView);
    }

    @Override
    public int getAbsoluteSnapPosition(
            final LayoutManager<Cell> layoutManager,
            final int size,
            final int cellSize,
            final Move move) {
        return ContentBound.getAbsoluteSnapPosition(layoutManager, size, cellSize, move);
    }

    private int getContentStartLimit(
            final LayoutManager<Cell> layoutManager, final List<Cell> cells, final int size) {
        if (contentFitsTheView(layoutManager, cells, size)) {
            return getFittedContentStart(layoutManager, size);
        }
        return layoutManager.getStartSizePadding();
    }

    private int getContentEndLimit(
            final LayoutManager<Cell> layoutManager, final List<Cell> cells, final int size) {
        if (contentFitsTheView(layoutManager, cells, size)) {
            return getFittedContentEnd(layoutManager, size);
        }
        final int startSizePadding = layoutManager.getStartSizePadding();
        return startSizePadding + size;
    }

    private int getFittedContentStart(final LayoutManager<Cell> layoutManager, final int size) {
        final int contentSize = layoutManager.getCellSizeTotal();
        return mSnapPosition.getAbsoluteSnapPosition(layoutManager, size, contentSize, Move.none);
    }

    private int getFittedContentEnd(final LayoutManager<Cell> layoutManager, final int size) {
        final int contentSize = layoutManager.getCellSizeTotal();
        final int fittedContentStart = getFittedContentStart(layoutManager, size);
        return fittedContentStart + contentSize;
    }

    private static <Cell> boolean contentFitsTheView(
            final LayoutManager<Cell> layoutManager, final List<Cell> cells, final int size) {
        final boolean isFirstCellDrawn = isFirstCellDrawn(layoutManager, cells);
        final boolean isLastCellDrawn = isLastCellDrawn(layoutManager, cells);
        final boolean isWholeContentDrawn = isFirstCellDrawn && isLastCellDrawn;
        if (!isWholeContentDrawn) {
            return false;
        }
        final int contentSize = layoutManager.getCellSizeTotal();
        return contentSize <= size;
    }

    private static <Cell> boolean isFirstCellDrawn(
            final LayoutManager<Cell> layoutManager, final List<Cell> cells) {
        final Cell firstCell = cells.get(0);
        final View firstView = layoutManager.getFirstAdapterPositionView(firstCell);
        final int firstPosition = layoutManager.getPosition(firstView);
        return firstPosition == FIRST_ADAPTER_POSITION;
    }

    private static <Cell> boolean isLastCellDrawn(
            final LayoutManager<Cell> layoutManager, final List<Cell> cells) {
        final int lastCellIndex = cells.size() - 1;
        final Cell lastCell = cells.get(lastCellIndex);
        final View lastView = layoutManager.getLastAdapterPositionView(lastCell);
        final int lastPosition = layoutManager.getPosition(lastView);
        final int lastAdapterPosition = layoutManager.getAdapterCount() - 1;
        return lastPosition == lastAdapterPosition;
    }

    private int getHighestCellStart(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final boolean isFirstCellDrawn = isFirstCellDrawn(layoutManager, cells);
        if (!isFirstCellDrawn) {
            return NO_HIGHEST_CELL_START;
        }
        final Cell firstCell = cells.get(0);
        final int firstCellStart = layoutManager.getCellStart(firstCell);
        final int cellStart = layoutManager.getCellStart(cell);
        final int cellStartInsideTheContent = cellStart - firstCellStart;
        final int contentStartLimit = getContentStartLimit(layoutManager, cells, size);
        return contentStartLimit + cellStartInsideTheContent;
    }

    private int getLowestCellStart(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final boolean isLastCellDrawn = isLastCellDrawn(layoutManager, cells);
        if (!isLastCellDrawn) {
            return NO_LOWEST_CELL_START;
        }
        final int lastCellIndex = cells.size() - 1;
        final Cell lastCell = cells.get(lastCellIndex);
        final int lastCellEnd = layoutManager.getCellEnd(lastCell);
        final int cellStart = layoutManager.getCellStart(cell);
        final int contentFromTheCellStart = lastCellEnd - cellStart;
        final int contentEndLimit = getContentEndLimit(layoutManager, cells, size);
        return contentEndLimit - contentFromTheCellStart;
    }

    private static int clamp(final int value, final int lowest, final int highest) {
        final int atLeastTheLowest = Math.max(value, lowest);
        return Math.min(atLeastTheLowest, highest);
    }
}
