// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.divideraxis;

import android.graphics.Rect;
import android.view.View;
import mobi.parchment.widget.adapterview.ScrollDirectionManager;

public final class SizeDividerAxis implements DividerAxisInterface {

    private final ScrollDirectionManager mScrollDirectionManager;

    public SizeDividerAxis(final ScrollDirectionManager scrollDirectionManager) {
        mScrollDirectionManager = scrollDirectionManager;
    }

    @Override
    public int getStart(final View view) {
        return mScrollDirectionManager.getViewStart(view);
    }

    @Override
    public int getEnd(final View view) {
        return mScrollDirectionManager.getViewEnd(view);
    }

    @Override
    public int getBandStart(final View view) {
        return mScrollDirectionManager.getViewBreadthStart(view);
    }

    @Override
    public int getBandEnd(final View view) {
        return mScrollDirectionManager.getViewBreadthEnd(view);
    }

    @Override
    public void setDividerBounds(
            final Rect bounds,
            final int dividerStart,
            final int dividerEnd,
            final int bandStart,
            final int bandEnd) {
        mScrollDirectionManager.setDrawBounds(bounds, dividerStart, dividerEnd, bandStart, bandEnd);
    }
}
