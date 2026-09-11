// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridpatternview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Adapter;
import java.util.List;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.AdapterViewHandler;
import mobi.parchment.widget.adapterview.AdapterViewInitializer;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.OnSelectedListener;
import mobi.parchment.widget.adapterview.SnapPosition;

public class GridPatternView<ADAPTER extends Adapter>
        extends AbstractAdapterView<ADAPTER, GridPatternGroup>
        implements OnLongClickListener, OnClickListener, OnSelectedListener, AdapterViewHandler {

    private GridPatternLayoutManager mGridPatternLayoutManager;
    private boolean mIsVerticalScroll;

    public GridPatternView(Context context) {
        super(context);
    }

    public GridPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GridPatternView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected AdapterViewInitializer<GridPatternGroup> getAdapterViewInitializer(
            Context context, AttributeSet attributeSet) {
        final GridPatternAttributes gridPatternAttributes =
                new GridPatternAttributes(context, attributeSet);

        final boolean isViewPager = gridPatternAttributes.isViewPager();
        final int cellSpacing = (int) gridPatternAttributes.getCellSpacing();
        final boolean isCircularScroll = gridPatternAttributes.isCircularScroll();
        final boolean snapToPosition = gridPatternAttributes.isSnapToPosition();
        final int viewPagerInterval = gridPatternAttributes.getViewPagerInterval();
        final SnapPosition snapPosition = gridPatternAttributes.getSnapPosition();
        final boolean selectOnSnap = gridPatternAttributes.selectOnSnap();
        final boolean selectWhileScrolling = gridPatternAttributes.selectWhileScrolling();
        final Drawable divider = gridPatternAttributes.getDivider();
        final int dividerSize = gridPatternAttributes.getDividerSize();
        final float ratio = gridPatternAttributes.getRatio();
        mIsVerticalScroll = gridPatternAttributes.isVertical();

        final GridPatternLayoutManagerAttributes gridLayoutManagerAttributes =
                new GridPatternLayoutManagerAttributes(
                        isCircularScroll,
                        snapToPosition,
                        isViewPager,
                        viewPagerInterval,
                        snapPosition,
                        cellSpacing,
                        selectOnSnap,
                        selectWhileScrolling,
                        mIsVerticalScroll,
                        ratio);
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        mGridPatternLayoutManager =
                new GridPatternLayoutManager(
                        this, this, adapterViewManager, gridLayoutManagerAttributes);

        final AdapterViewInitializer<GridPatternGroup> adapterViewAdapterInitializer =
                createAdapterViewInitializer(
                        context,
                        isViewPager,
                        adapterViewManager,
                        mGridPatternLayoutManager,
                        mIsVerticalScroll,
                        divider,
                        dividerSize);
        return adapterViewAdapterInitializer;
    }

    public void addGridPatternGroupDefinition(
            final List<GridPatternItemDefinition> gridPatternItemDefinitions) {
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                new GridPatternGroupDefinition(mIsVerticalScroll, gridPatternItemDefinitions);
        mGridPatternLayoutManager.addGridPatternGroupDefinition(gridPatternGroupDefinition);
    }

    // ===================
    // Added By Tom Yu
    // ===================
    public void add(final List<GridPatternItemDefinition> gridPatternItemDefinitions) {
        addGridPatternGroupDefinition(gridPatternItemDefinitions);
    }

    public void clear() {
        mGridPatternLayoutManager.clearGridPatternGroupDefinition();
    }
}
