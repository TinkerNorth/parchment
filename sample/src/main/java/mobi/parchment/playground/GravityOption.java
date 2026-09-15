// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

public enum GravityOption implements RadioOption {
    top(R.id.playground_gravity_top, R.style.Playground_Gravity_Top, OrientationOption.vertical),
    bottom(
            R.id.playground_gravity_bottom,
            R.style.Playground_Gravity_Bottom,
            OrientationOption.vertical),
    left(
            R.id.playground_gravity_left,
            R.style.Playground_Gravity_Left,
            OrientationOption.horizontal),
    right(
            R.id.playground_gravity_right,
            R.style.Playground_Gravity_Right,
            OrientationOption.horizontal);

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final OrientationOption mAxis;

    GravityOption(
            final int radioButtonId, final int styleResourceId, final OrientationOption axis) {
        mRadioButtonId = radioButtonId;
        mStyleResourceId = styleResourceId;
        mAxis = axis;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getStyleResourceId() {
        return mStyleResourceId;
    }

    public OrientationOption getAxis() {
        return mAxis;
    }
}
