// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

public enum OrientationOption implements RadioOption {
    horizontal(R.id.playground_orientation_horizontal, R.style.Playground_Horizontal),
    vertical(R.id.playground_orientation_vertical, R.style.Playground_Vertical);

    private final int mRadioButtonId;
    private final int mStyleResourceId;

    OrientationOption(final int radioButtonId, final int styleResourceId) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }
}
