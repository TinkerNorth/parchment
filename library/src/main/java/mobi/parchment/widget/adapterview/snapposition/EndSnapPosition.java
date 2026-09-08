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
        final int startSizePadding = layoutManager.getStartSizePadding();
        return startSizePadding + size;
    }

    @Override
    public int getDrawLimitMoveBackwardOverDrawAdjust(
            LayoutManager<Cell> layoutManager, List<Cell> cells, int size, Cell cell) {
        final int cellSize = layoutManager.getCellSize(cell);
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
            LayoutManager<Cell> layoutManager,
            ScrollDirectionManager scrollDirectionManager,
            int size,
            View view) {
        final int startPixel = scrollDirectionManager.getViewStart(view);
        final int viewSize = scrollDirectionManager.getViewSize(view);
        final int startSizePadding = layoutManager.getStartSizePadding();

        return startSizePadding + size - viewSize - startPixel;
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
        final int startSizePadding = layoutManager.getStartSizePadding();
        final int snapPosition = startSizePadding + size - cellSize;
        return snapPosition;
    }
}
