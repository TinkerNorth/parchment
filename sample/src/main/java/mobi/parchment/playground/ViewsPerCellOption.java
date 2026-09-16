// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

public enum ViewsPerCellOption implements RadioOption {
    two(R.id.playground_views_per_cell_two, R.style.Playground_ViewsPerCell_Two, 2),
    three(R.id.playground_views_per_cell_three, R.style.Playground_ViewsPerCell_Three, 3),
    four(R.id.playground_views_per_cell_four, R.style.Playground_ViewsPerCell_Four, 4);

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final int mCount;

    ViewsPerCellOption(final int radioButtonId, final int styleResourceId, final int count) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
        mCount = count;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }

    public int getCount() {
        return mCount;
    }

    public String getXmlValue() {
        return Integer.toString(mCount);
    }
}
