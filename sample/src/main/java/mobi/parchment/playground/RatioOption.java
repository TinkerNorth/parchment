// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

/** parchment_ratio: a grid cell's size along the scroll axis over its size across it. */
public enum RatioOption implements RadioOption {
    square(
            R.id.playground_ratio_square,
            R.style.Playground_Ratio_Square,
            R.dimen.playground_ratio_square),
    golden(
            R.id.playground_ratio_golden,
            R.style.Playground_Ratio_Golden,
            R.dimen.playground_ratio_golden),
    inverseGolden(
            R.id.playground_ratio_inverse_golden,
            R.style.Playground_Ratio_InverseGolden,
            R.dimen.playground_ratio_inverse_golden);

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final int mFloatResourceId;

    RatioOption(final int radioButtonId, final int styleResourceId, final int floatResourceId) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
        mFloatResourceId = floatResourceId;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }

    public int getFloatResourceId() {
        return mFloatResourceId;
    }
}
