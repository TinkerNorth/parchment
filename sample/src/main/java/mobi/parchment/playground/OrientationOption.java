// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

public enum OrientationOption implements RadioOption {
    horizontal(
            R.id.playground_orientation_horizontal,
            R.style.Playground_Horizontal,
            R.string.playground_gravity_hint_horizontal,
            R.string.playground_gravity_hint_horizontal_full_columns),
    vertical(
            R.id.playground_orientation_vertical,
            R.style.Playground_Vertical,
            R.string.playground_gravity_hint_vertical,
            R.string.playground_gravity_hint_vertical_full_rows);

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final int mGravityHintResourceId;
    private final int mFullLinesGravityHintResourceId;

    OrientationOption(
            final int radioButtonId,
            final int styleResourceId,
            final int gravityHintResourceId,
            final int fullLinesGravityHintResourceId) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
        mGravityHintResourceId = gravityHintResourceId;
        mFullLinesGravityHintResourceId = fullLinesGravityHintResourceId;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }

    public int getGravityHintResourceId() {
        return mGravityHintResourceId;
    }

    public int getFullLinesGravityHintResourceId() {
        return mFullLinesGravityHintResourceId;
    }
}
