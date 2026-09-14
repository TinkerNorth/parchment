// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import android.view.View;
import java.util.List;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.Move;
import mobi.parchment.widget.adapterview.ScrollDirectionManager;

public class StartSnapPosition<Cell> implements SnapPositionInterface<Cell> {

    @Override
    public int getDrawLimitMoveForwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        final int cellSize = layoutManager.getCellSize(cell);
        final int snappedCellStart = getSnappedCellStart(layoutManager);
        return snappedCellStart + cellSize;
    }

    @Override
    public int getDrawLimitMoveBackwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell) {
        return getSnappedCellStart(layoutManager);
    }

    private int getSnappedCellStart(final LayoutManager<Cell> layoutManager) {
        return layoutManager.getStartSizePadding();
    }

    private int getCellDisplacementFromSnapPosition(
            final LayoutManager<Cell> layoutManager, final Cell cell) {
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int currentCellStart = layoutManager.getCellStart(cell);
        return startSizePadding - currentCellStart;
    }

    @Override
    public int getCellDistanceFromSnapPosition(
            LayoutManager<Cell> layoutManager, int size, Cell cell) {
        return Math.abs(getCellDisplacementFromSnapPosition(layoutManager, cell));
    }

    @Override
    public int getSnapToPixelDistance(
            final LayoutManager<Cell> layoutManager, final int size, final Cell cell) {
        final int snappedCellStart = getSnappedCellStart(layoutManager);
        final int cellStart = layoutManager.getCellStart(cell);

        return snappedCellStart - cellStart;
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
        return getSnappedCellStart(layoutManager);
    }
}
