// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import android.view.View;
import java.util.List;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.Move;
import mobi.parchment.widget.adapterview.ScrollDirectionManager;

public class EndSnapPosition<Cell> implements SnapPositionInterface<Cell> {

    @Override
    public int getDrawLimitMoveForwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int cellSize = layoutManager.getCellSize(cell);
        final int snappedCellStart = getSnappedCellStart(layoutManager, size, cellSize);
        return snappedCellStart + cellSize;
    }

    @Override
    public int getDrawLimitMoveBackwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int cellSize = layoutManager.getCellSize(cell);
        return getSnappedCellStart(layoutManager, size, cellSize);
    }

    private int getSnappedCellStart(
            final LayoutManager<Cell> layoutManager, final int size, final int cellSize) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        return startSizePadding + size - cellSize;
    }

    private int getCellDisplacementFromSnapPosition(
            LayoutManager<Cell> layoutManager, int size, Cell cell) {
        final int currentCellEnd = layoutManager.getCellEnd(cell);
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int displacement = startSizePadding + size - currentCellEnd;
        return displacement;
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
        final int cellSize = layoutManager.getCellSize(cell);
        final int snappedCellStart = getSnappedCellStart(layoutManager, size, cellSize);
        final int cellStart = layoutManager.getCellStart(cell);

        return snappedCellStart - cellStart;
    }

    @Override
    public int getRedrawOffset(
            final ScrollDirectionManager scrollDirectionManager,
            final View incomingView,
            final View outgoingView) {
        final int outgoingViewStart = scrollDirectionManager.getViewStart(outgoingView);
        final int incomingViewSize = scrollDirectionManager.getViewSize(incomingView);
        final int outgoingViewSize = scrollDirectionManager.getViewSize(outgoingView);

        return outgoingViewStart + outgoingViewSize - incomingViewSize;
    }

    @Override
    public int getAbsoluteSnapPosition(
            final LayoutManager<Cell> layoutManager,
            final int size,
            final int cellSize,
            final Move move) {
        return getSnappedCellStart(layoutManager, size, cellSize);
    }
}
