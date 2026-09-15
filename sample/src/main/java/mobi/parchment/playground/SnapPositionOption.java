// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

/** parchment_snapPosition. */
public enum SnapPositionOption implements RadioOption {
    center(R.id.playground_snap_position_center, R.style.Playground_SnapPosition_Center),
    start(R.id.playground_snap_position_start, R.style.Playground_SnapPosition_Start),
    end(R.id.playground_snap_position_end, R.style.Playground_SnapPosition_End),
    onScreen(R.id.playground_snap_position_on_screen, R.style.Playground_SnapPosition_OnScreen);

    private final int mRadioButtonId;
    private final int mStyleResourceId;

    SnapPositionOption(final int radioButtonId, final int styleResourceId) {
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
