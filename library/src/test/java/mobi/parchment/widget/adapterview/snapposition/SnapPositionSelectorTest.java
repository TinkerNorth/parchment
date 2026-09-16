// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.snapposition;

import static org.assertj.core.api.Assertions.assertThat;

import android.view.View;
import mobi.parchment.widget.adapterview.SnapPosition;
import org.junit.Test;

public class SnapPositionSelectorTest {

    private static final boolean SCROLL_WITHIN_CONTENT = true;
    private static final boolean SCROLL_PAST_CONTENT = false;

    @Test
    public void startSnap_scrollWithinContent_isHeldWithinTheContent() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.start, SCROLL_WITHIN_CONTENT);

        assertThat(snapPosition).isInstanceOf(ScrollWithinContentSnapPosition.class);
    }

    @Test
    public void endSnap_scrollWithinContent_isHeldWithinTheContent() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.end, SCROLL_WITHIN_CONTENT);

        assertThat(snapPosition).isInstanceOf(ScrollWithinContentSnapPosition.class);
    }

    @Test
    public void centerSnap_scrollWithinContent_isHeldWithinTheContent() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.center, SCROLL_WITHIN_CONTENT);

        assertThat(snapPosition).isInstanceOf(ScrollWithinContentSnapPosition.class);
    }

    @Test
    public void onScreenSnap_scrollWithinContent_alreadyBoundsTheContent_soIsLeftAsItIs() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.onScreen, SCROLL_WITHIN_CONTENT);

        assertThat(snapPosition).isInstanceOf(OnScreenSnapPosition.class);
    }

    @Test
    public void startSnap_withoutScrollWithinContent_isTheStartSnapPosition() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.start, SCROLL_PAST_CONTENT);

        assertThat(snapPosition).isInstanceOf(StartSnapPosition.class);
    }

    @Test
    public void endSnap_withoutScrollWithinContent_isTheEndSnapPosition() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.end, SCROLL_PAST_CONTENT);

        assertThat(snapPosition).isInstanceOf(EndSnapPosition.class);
    }

    @Test
    public void centerSnap_withoutScrollWithinContent_isTheCenterSnapPosition() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.center, SCROLL_PAST_CONTENT);

        assertThat(snapPosition).isInstanceOf(CenterSnapPosition.class);
    }

    @Test
    public void onScreenSnap_withoutScrollWithinContent_isTheOnScreenSnapPosition() {
        final SnapPositionInterface<View> snapPosition =
                SnapPositionSelector.getSnapPositionInterface(
                        SnapPosition.onScreen, SCROLL_PAST_CONTENT);

        assertThat(snapPosition).isInstanceOf(OnScreenSnapPosition.class);
    }
}
