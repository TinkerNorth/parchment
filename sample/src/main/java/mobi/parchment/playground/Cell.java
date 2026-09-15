// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.content.res.Resources;
import mobi.parchment.sample.R;

/**
 * The layout each adapter item is inflated from, and the size its photo is requested at. A view
 * measures its cells UNSPECIFIED along the axis it scrolls on, so every cell layout fixes its size
 * along that axis and lets the view size it across.
 */
public enum Cell {
    horizontal(
            R.layout.list_item_horizontal_picture,
            R.dimen.picture_request_horizontal_width,
            R.dimen.picture_request_horizontal_height),
    verticalList(
            R.layout.list_item_vertical_picture,
            R.dimen.picture_request_vertical_width,
            R.dimen.picture_request_vertical_height),
    verticalGrid(
            R.layout.list_item_gridview_picture,
            R.dimen.picture_request_gridview_width,
            R.dimen.picture_request_gridview_height),
    pattern(
            R.layout.list_item_gridpatternview_picture,
            R.dimen.picture_request_grid_pattern_width,
            R.dimen.picture_request_grid_pattern_height);

    private final int mLayoutResourceId;
    private final int mRequestWidthDimension;
    private final int mRequestHeightDimension;

    Cell(
            final int layoutResourceId,
            final int requestWidthDimension,
            final int requestHeightDimension) {
        mLayoutResourceId = layoutResourceId;
        mRequestWidthDimension = requestWidthDimension;
        mRequestHeightDimension = requestHeightDimension;
    }

    public int getLayoutResourceId() {
        return mLayoutResourceId;
    }

    public int getRequestWidthPixels(final Resources resources) {
        return resources.getDimensionPixelSize(mRequestWidthDimension);
    }

    public int getRequestHeightPixels(final Resources resources) {
        return resources.getDimensionPixelSize(mRequestHeightDimension);
    }
}
