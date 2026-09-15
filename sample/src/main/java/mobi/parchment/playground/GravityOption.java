// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

/** parchment_gravity: where a view shorter than its row, or narrower than its column, sits. */
public enum GravityOption implements RadioOption {
    top(R.id.playground_gravity_top, R.style.Playground_Gravity_Top),
    bottom(R.id.playground_gravity_bottom, R.style.Playground_Gravity_Bottom),
    left(R.id.playground_gravity_left, R.style.Playground_Gravity_Left),
    right(R.id.playground_gravity_right, R.style.Playground_Gravity_Right);

    private final int mRadioButtonId;
    private final int mStyleResourceId;

    GravityOption(final int radioButtonId, final int styleResourceId) {
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
