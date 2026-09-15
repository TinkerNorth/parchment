// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

/** parchment_numberOfViewsPerCell: views per row when vertical, per column when horizontal. */
public enum ViewsPerCellOption implements RadioOption {
    two(R.id.playground_views_per_cell_two, R.style.Playground_ViewsPerCell_Two, "2"),
    three(R.id.playground_views_per_cell_three, R.style.Playground_ViewsPerCell_Three, "3"),
    four(R.id.playground_views_per_cell_four, R.style.Playground_ViewsPerCell_Four, "4");

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final String mXmlValue;

    ViewsPerCellOption(final int radioButtonId, final int styleResourceId, final String xmlValue) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
        mXmlValue = xmlValue;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }

    public String getXmlValue() {
        return mXmlValue;
    }
}
