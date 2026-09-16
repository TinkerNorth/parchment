// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

public enum CellSpacingOption implements RadioOption {
    none(
            R.id.playground_cell_spacing_none,
            R.style.Playground_CellSpacing_None,
            R.dimen.playground_cell_spacing_none),
    small(
            R.id.playground_cell_spacing_small,
            R.style.Playground_CellSpacing_Small,
            R.dimen.playground_cell_spacing_small),
    large(
            R.id.playground_cell_spacing_large,
            R.style.Playground_CellSpacing_Large,
            R.dimen.playground_cell_spacing_large);

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final int mDimensionResourceId;

    CellSpacingOption(
            final int radioButtonId, final int styleResourceId, final int dimensionResourceId) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
        mDimensionResourceId = dimensionResourceId;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }

    public int getDimensionResourceId() {
        return mDimensionResourceId;
    }
}
