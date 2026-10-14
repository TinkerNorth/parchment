// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import mobi.parchment.widget.adapterview.AdapterViewInitializer;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;

public final class ReachableGridPatternView extends GridPatternView<BaseAdapter> {

    private GridPatternLayoutManager mGridPatternLayoutManager;
    private boolean mIsWaitingForAFrame;

    public ReachableGridPatternView(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected AdapterViewInitializer<GridPatternGroup> createAdapterViewInitializer(
            final Context context,
            final boolean isViewPager,
            final AdapterViewManager adapterViewManager,
            final LayoutManager<GridPatternGroup> layoutManager,
            final boolean isVerticalScroll,
            final Drawable divider,
            final int dividerSize) {
        mGridPatternLayoutManager = (GridPatternLayoutManager) layoutManager;
        return super.createAdapterViewInitializer(
                context,
                isViewPager,
                adapterViewManager,
                layoutManager,
                isVerticalScroll,
                divider,
                dividerSize);
    }

    @Override
    public void requestAnimationFrame() {
        mIsWaitingForAFrame = true;
        super.requestAnimationFrame();
    }

    @Override
    protected void onAnimationFrame() {
        mIsWaitingForAFrame = false;
        super.onAnimationFrame();
    }

    public static GridPatternLayoutManager layoutManagerOf(
            final GridPatternView<BaseAdapter> view) {
        final ReachableGridPatternView reachableView = (ReachableGridPatternView) view;
        return reachableView.mGridPatternLayoutManager;
    }

    public boolean isAtRest() {
        final boolean isLayoutRequested = isLayoutRequested();
        return !mIsWaitingForAFrame && !isLayoutRequested;
    }
}
