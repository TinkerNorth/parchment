// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Adapter;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.AdapterViewHandler;
import mobi.parchment.widget.adapterview.AdapterViewInitializer;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.OnSelectedListener;
import mobi.parchment.widget.adapterview.SnapPosition;

public class GridView<ADAPTER extends Adapter> extends AbstractAdapterView<ADAPTER, Group>
        implements OnLongClickListener, OnClickListener, OnSelectedListener, AdapterViewHandler {

    public GridView(Context context) {
        super(context);
    }

    public GridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GridView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected AdapterViewInitializer<Group> getAdapterViewInitializer(
            Context context, AttributeSet attributeSet) {
        final GridAttributes gridAttributes = new GridAttributes(context, attributeSet);

        final int numberOfViewsPerCell = gridAttributes.getNumberOfViewsPerCell();
        final boolean isViewPager = gridAttributes.isViewPager();
        final boolean isVertical = gridAttributes.isVertical();
        final int cellSpacing = (int) gridAttributes.getCellSpacing();
        final boolean isCircularScroll = gridAttributes.isCircularScroll();
        final boolean snapToPosition = gridAttributes.isSnapToPosition();
        final int viewPagerInterval = gridAttributes.getViewPagerInterval();
        final SnapPosition snapPosition = gridAttributes.getSnapPosition();
        final boolean selectOnSnap = gridAttributes.selectOnSnap();
        final boolean selectWhileScrolling = gridAttributes.selectWhileScrolling();
        final Drawable divider = gridAttributes.getDivider();
        final int dividerSize = gridAttributes.getDividerSize();
        final boolean isTop = gridAttributes.isTop();
        final boolean isBottom = gridAttributes.isBottom();
        final boolean isLeft = gridAttributes.isLeft();
        final boolean isRight = gridAttributes.isRight();
        final GridLayoutManagerAttributes gridLayoutManagerAttributes =
                new GridLayoutManagerAttributes(
                        numberOfViewsPerCell,
                        isCircularScroll,
                        snapToPosition,
                        isViewPager,
                        viewPagerInterval,
                        snapPosition,
                        cellSpacing,
                        selectOnSnap,
                        selectWhileScrolling,
                        isVertical,
                        isTop,
                        isBottom,
                        isLeft,
                        isRight);

        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, this, adapterViewManager, gridLayoutManagerAttributes);

        final AdapterViewInitializer<Group> adapterViewAdapterViewInitializer =
                createAdapterViewInitializer(
                        context,
                        isViewPager,
                        adapterViewManager,
                        gridLayoutManager,
                        isVertical,
                        divider,
                        dividerSize);
        return adapterViewAdapterViewInitializer;
    }
}
