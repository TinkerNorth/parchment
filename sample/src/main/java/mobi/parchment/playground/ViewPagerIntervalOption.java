// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;

/** parchment_viewPagerInterval: a whole viewport per gesture, or a fixed number of cells. */
public enum ViewPagerIntervalOption implements RadioOption {
    viewport(
            R.id.playground_view_pager_interval_viewport,
            R.style.Playground_ViewPagerInterval_Viewport,
            "viewport"),
    one(R.id.playground_view_pager_interval_one, R.style.Playground_ViewPagerInterval_One, "1"),
    two(R.id.playground_view_pager_interval_two, R.style.Playground_ViewPagerInterval_Two, "2");

    private final int mRadioButtonId;
    private final int mStyleResourceId;
    private final String mXmlValue;

    ViewPagerIntervalOption(
            final int radioButtonId, final int styleResourceId, final String xmlValue) {
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
