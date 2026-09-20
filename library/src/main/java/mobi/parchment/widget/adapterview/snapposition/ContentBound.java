// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.Move;

public final class ContentBound {

    private ContentBound() {}

    public static <Cell> int getAbsoluteSnapPosition(
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
