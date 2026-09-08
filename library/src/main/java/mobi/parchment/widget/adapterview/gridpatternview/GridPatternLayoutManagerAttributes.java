// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridpatternview;

import mobi.parchment.widget.adapterview.LayoutManagerAttributes;
import mobi.parchment.widget.adapterview.SnapPosition;

public class GridPatternLayoutManagerAttributes extends LayoutManagerAttributes {
    private final float mRatio;

    public GridPatternLayoutManagerAttributes(
            final boolean isCircularScroll,
            final boolean snapToPosition,
            final boolean isViewPager,
            final int viewPagerInterval,
            final SnapPosition snapPosition,
            final int cellSpacing,
            final boolean selectOnSnap,
            final boolean selectWhileScrolling,
            final boolean isVertical,
            final float ratio) {
        super(
                isCircularScroll,
                snapToPosition,
                isViewPager,
                viewPagerInterval,
                snapPosition,
                cellSpacing,
                selectOnSnap,
                selectWhileScrolling,
                isVertical);
        mRatio = ratio;
    }

    public float getRatio() {
        return mRatio;
    }
}
