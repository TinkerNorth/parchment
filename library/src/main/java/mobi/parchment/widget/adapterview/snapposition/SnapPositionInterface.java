// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import android.view.View;
import java.util.List;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.Move;
import mobi.parchment.widget.adapterview.ScrollDirectionManager;

public interface SnapPositionInterface<Cell> {

    public int getDrawLimitMoveForwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell);

    public int getDrawLimitMoveBackwardOverDrawAdjust(
            final LayoutManager<Cell> layoutManager,
            final List<Cell> cells,
            final int size,
            final Cell cell);

    public int getCellDistanceFromSnapPosition(
            final LayoutManager<Cell> layoutManager, final int size, final Cell cell);

    public int getSnapToPixelDistance(
            final LayoutManager<Cell> layoutManager,
            final ScrollDirectionManager scrollDirectionManager,
            final int size,
            final View view);

    public int getRedrawOffset(
            final ScrollDirectionManager scrollDirectionManager,
            final View incomingView,
            final View outgoingView);

    public int getAbsoluteSnapPosition(
            final LayoutManager<Cell> layoutManager,
            final int size,
            final int cellSize,
            final Move move);
}
