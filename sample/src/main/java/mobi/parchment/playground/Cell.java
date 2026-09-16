// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.content.res.Resources;
import mobi.parchment.sample.R;

public enum Cell {
    horizontal(
            R.layout.list_item_horizontal_picture,
            R.dimen.picture_request_horizontal_width,
            R.dimen.picture_request_horizontal_height,
            Extent.fixed),
    verticalList(
            R.layout.list_item_vertical_picture,
            R.dimen.picture_request_vertical_width,
            R.dimen.picture_request_vertical_height,
            Extent.fixed),
    verticalGrid(
            R.layout.list_item_gridview_picture,
            R.dimen.picture_request_gridview_width,
            R.dimen.picture_request_gridview_height,
            Extent.wrapsTheCaption),
    pattern(
            R.layout.list_item_gridpatternview_picture,
            R.dimen.picture_request_grid_pattern_width,
            R.dimen.picture_request_grid_pattern_height,
            Extent.fixed);

    public enum Extent {
        fixed,
        wrapsTheCaption
    }

    private final int mLayoutResourceId;
    private final int mRequestWidthDimension;
    private final int mRequestHeightDimension;
    private final Extent mExtentAlongTheScrollAxis;

    Cell(
            final int layoutResourceId,
            final int requestWidthDimension,
            final int requestHeightDimension,
            final Extent extentAlongTheScrollAxis) {
        mLayoutResourceId = layoutResourceId;
        mRequestWidthDimension = requestWidthDimension;
        mRequestHeightDimension = requestHeightDimension;
        mExtentAlongTheScrollAxis = extentAlongTheScrollAxis;
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

    public boolean variesAlongTheScrollAxis() {
        return mExtentAlongTheScrollAxis == Extent.wrapsTheCaption;
    }
}
