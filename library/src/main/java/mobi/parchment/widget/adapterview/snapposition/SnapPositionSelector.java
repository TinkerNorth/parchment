// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import mobi.parchment.widget.adapterview.SnapPosition;

public final class SnapPositionSelector {

    private SnapPositionSelector() {}

    public static <Cell> SnapPositionInterface<Cell> getSnapPositionInterface(
            final SnapPosition snapPosition, final boolean scrollWithinContent) {
        if (needsTheContentBound(snapPosition, scrollWithinContent)) {
            return getScrollWithinContentSnapPosition(snapPosition);
        }
        return getUnboundedSnapPosition(snapPosition);
    }

    private static <Cell> SnapPositionInterface<Cell> getScrollWithinContentSnapPosition(
            final SnapPosition snapPosition) {
        final SnapPositionInterface<Cell> snapPositionInterface =
                getUnboundedSnapPosition(snapPosition);
        return new ScrollWithinContentSnapPosition<Cell>(snapPositionInterface);
    }

    private static boolean needsTheContentBound(
            final SnapPosition snapPosition, final boolean scrollWithinContent) {
        final boolean alreadyBoundsTheContent = snapPosition == SnapPosition.onScreen;
        return scrollWithinContent && !alreadyBoundsTheContent;
    }

    private static <Cell> SnapPositionInterface<Cell> getUnboundedSnapPosition(
            final SnapPosition snapPosition) {
        switch (snapPosition) {
            case center:
                return new CenterSnapPosition<Cell>();
            case end:
                return new EndSnapPosition<Cell>();
            case start:
                return new StartSnapPosition<Cell>();
            case onScreen:
            default:
                return new OnScreenSnapPosition<Cell>();
        }
    }
}
