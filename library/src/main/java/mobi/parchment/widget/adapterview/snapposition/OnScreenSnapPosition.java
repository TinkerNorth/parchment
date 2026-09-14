// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import android.view.View;
import java.util.List;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.Move;
import mobi.parchment.widget.adapterview.ScrollDirectionManager;

public class OnScreenSnapPosition<Cell> implements SnapPositionInterface<Cell> {

    @Override
    public int getDrawLimitMoveForwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int endSizePadding = layoutManager.getEndSizePadding();
        final int cellSize = layoutManager.getCellSize(cell);

        final int drawLimit = startSizePadding + cellSize;

        final int lastCellIndex = cells.size() - 1;
        final Cell lastCell = cells.get(lastCellIndex);
        final View lastView = layoutManager.getLastAdapterPositionView(lastCell);
        final int lastViewPosition = layoutManager.getPosition(lastView);
        final boolean isLastItemOnScreen = lastViewPosition == layoutManager.getAdapterCount() - 1;

        final Cell firstCell = cells.get(0);
        final View firstView = layoutManager.getFirstAdapterPositionView(firstCell);
        final int firstViewPosition = layoutManager.getPosition(firstView);
        final boolean isFirstItemOnScreen = firstViewPosition == 0;

        if (isFirstItemOnScreen && isLastItemOnScreen) {
            final int cellSizeTotal = layoutManager.getCellSizeTotal();
            final int sizeTotal = size + startSizePadding + endSizePadding;
            if (cellSizeTotal <= sizeTotal) {
                final int firstCellSize = layoutManager.getCellSize(firstCell);
                return firstCellSize + (sizeTotal - cellSizeTotal) / 2;
            }
        } else if (isLastItemOnScreen) {
            final int cellSizeTotal = layoutManager.getCellSizeTotal();
            return Math.max(drawLimit, startSizePadding + size - (cellSizeTotal - cellSize));
        }

        return drawLimit;
    }

    @Override
    public int getDrawLimitMoveBackwardOverDrawAdjust(
            LayoutManager<Cell> layoutManager, List<Cell> cells, int size, Cell cell) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int endSizePadding = layoutManager.getEndSizePadding();
        final int cellSize = layoutManager.getCellSize(cell);
        final int drawLimit = startSizePadding + size - cellSize;

        final Cell firstCell = cells.get(0);
        final View firstView = layoutManager.getFirstAdapterPositionView(firstCell);
        final int firstViewPosition = layoutManager.getPosition(firstView);
        final boolean isFirstItemOnScreen = firstViewPosition == 0;

        final int lastCellIndex = cells.size() - 1;
        final Cell lastCell = cells.get(lastCellIndex);
        final View lastView = layoutManager.getLastAdapterPositionView(lastCell);
        final int lastViewPosition = layoutManager.getPosition(lastView);
        final boolean isLastItemOnScreen = lastViewPosition == layoutManager.getAdapterCount() - 1;

        if (isFirstItemOnScreen && isLastItemOnScreen) {
            final int cellSizeTotal = layoutManager.getCellSizeTotal();
            final int sizeTotal = size + startSizePadding + endSizePadding;
            if (cellSizeTotal <= sizeTotal) {
                final int x = (sizeTotal - cellSizeTotal) / 2;
                return sizeTotal - x - cellSize;
            }
        }

        if (isFirstItemOnScreen) {
            return Math.min(
                    drawLimit, startSizePadding + layoutManager.getCellSizeTotal() - cellSize);
        }

        return drawLimit;
    }

    private int getCellDisplacementFromSnapPosition(
            LayoutManager<Cell> layoutManager, int size, Cell cell) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int currentCellStart = layoutManager.getCellStart(cell);
        final int currentCellEnd = layoutManager.getCellEnd(cell);
        if (currentCellStart < startSizePadding && currentCellEnd < startSizePadding + size) {
            final int displacement = startSizePadding - currentCellStart;
            return displacement;
        } else if (currentCellEnd > startSizePadding + size
                && currentCellStart > startSizePadding) {
            final int displacement = startSizePadding + size - currentCellEnd;
            return displacement;
        }
        return 0;
    }

    @Override
    public int getCellDistanceFromSnapPosition(
            LayoutManager<Cell> layoutManager, int size, Cell cell) {
        final int displacement = getCellDisplacementFromSnapPosition(layoutManager, size, cell);
        return Math.abs(displacement);
    }

    @Override
    public int getSnapToPixelDistance(
            final LayoutManager<Cell> layoutManager, final int size, final Cell cell) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int endSizePadding = layoutManager.getEndSizePadding();
        final int cellStart = layoutManager.getCellStart(cell);
        final int cellEnd = layoutManager.getCellEnd(cell);
        final int cellSize = layoutManager.getCellSize(cell);

        final int actualSize = size - startSizePadding - endSizePadding;
        final boolean cellIsLargerThanViewGroup = actualSize <= cellSize;
        if (cellStart < startSizePadding || cellIsLargerThanViewGroup) {
            return -cellStart + startSizePadding;
        } else if (cellEnd > startSizePadding + size) {
            return startSizePadding + size - cellEnd;
        }

        return 0;
    }

    @Override
    public int getRedrawOffset(
            final ScrollDirectionManager scrollDirectionManager,
            final View incomingView,
            final View outgoingView) {
        final int outgoingViewStart = scrollDirectionManager.getViewStart(outgoingView);
        return outgoingViewStart;
    }

    @Override
    public int getAbsoluteSnapPosition(
            final LayoutManager<Cell> layoutManager,
            final int size,
            final int cellSize,
            final Move move) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        switch (move) {
            case back:
                return startSizePadding + size - cellSize;
            case forward:
            case none:
            default:
                return startSizePadding;
        }
    }
}
