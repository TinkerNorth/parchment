// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.utilities;

import android.view.View;
import android.view.ViewGroup;

/** Created by Emir Hasanbegovic on 2014-03-27. */
public class ViewGroupUtilities {

    public static int getViewGroupMeasuredHeight(final ViewGroup viewGroup) {
        final int viewGroupHeightMeasureSpec = viewGroup.getMeasuredHeight();
        final int viewGroupHeightSize = View.MeasureSpec.getSize(viewGroupHeightMeasureSpec);
        return viewGroupHeightSize;
    }

    public static int getViewGroupMeasuredWidth(final ViewGroup viewGroup) {
        final int viewGroupWidthMeasureSpec = viewGroup.getMeasuredWidth();
        final int viewGroupWidthSize = View.MeasureSpec.getSize(viewGroupWidthMeasureSpec);
        return viewGroupWidthSize;
    }

    public static int getViewGroupMeasuredWidthPadding(final ViewGroup viewGroup) {
        final int viewGroupWidthSize = getViewGroupMeasuredWidth(viewGroup);
        final int paddingRight = viewGroup.getPaddingRight();
        final int paddingLeft = viewGroup.getPaddingLeft();
        return viewGroupWidthSize - paddingLeft - paddingRight;
    }

    public static int getViewGroupMeasuredHeightPadding(final ViewGroup viewGroup) {

        final int viewGroupHeightSize = getViewGroupMeasuredHeight(viewGroup);
        final int paddingTop = viewGroup.getPaddingTop();
        final int paddingBottom = viewGroup.getPaddingBottom();
        return viewGroupHeightSize - paddingBottom - paddingTop;
    }
}
