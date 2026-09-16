// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import static org.assertj.core.api.Assertions.assertThat;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

// SampleApplication installs a Picasso singleton, which a JVM accepts once.
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class)
public class PlaygroundXmlTest {

    private static final String SELECT_ON_SNAP = "parchment:parchment_selectOnSnap=\"true\"";
    private static final String SELECT_WHILE_SCROLLING =
            "parchment:parchment_selectWhileScrolling=\"true\"";
    private static final String SCROLL_WITHIN_CONTENT =
            "parchment:parchment_scrollWithinContent=\"true\"";
    private static final String VIEW_PAGER_INTERVAL = "parchment:parchment_viewPagerInterval=";
    private static final String SNAP_POSITION = "parchment:parchment_snapPosition=";
    private static final String GRAVITY = "parchment:parchment_gravity=";

    @Test
    public void selectOnSnap_withoutSnapToPosition_isNotPrinted() {
        final String xml = xml(options().selectOnSnap(true).snapToPosition(false));

        assertThat(xml).doesNotContain(SELECT_ON_SNAP);
    }

    @Test
    public void selectOnSnap_withSnapToPosition_isPrinted() {
        final String xml =
                xml(
                        options()
                                .selectOnSnap(true)
                                .snapToPosition(true)
                                .snapPosition(SnapPositionOption.center));

        assertThat(xml).contains(SELECT_ON_SNAP);
    }

    @Test
    public void selectOnSnap_onScreenAndCircular_isNotPrinted() {
        final String xml =
                xml(
                        options()
                                .selectOnSnap(true)
                                .snapToPosition(true)
                                .snapPosition(SnapPositionOption.onScreen)
                                .isCircularScroll(true));

        assertThat(xml).doesNotContain(SELECT_ON_SNAP);
    }

    @Test
    public void selectOnSnap_offAndRelevant_isNotPrinted() {
        final String xml =
                xml(
                        options()
                                .selectOnSnap(false)
                                .snapToPosition(true)
                                .snapPosition(SnapPositionOption.center));

        assertThat(xml).doesNotContain(SELECT_ON_SNAP);
    }

    @Test
    public void selectWhileScrolling_withOnScreen_isNotPrinted() {
        final String xml =
                xml(options().selectWhileScrolling(true).snapPosition(SnapPositionOption.onScreen));

        assertThat(xml).doesNotContain(SELECT_WHILE_SCROLLING);
    }

    @Test
    public void selectWhileScrolling_withAnotherPosition_isPrinted() {
        final String xml =
                xml(options().selectWhileScrolling(true).snapPosition(SnapPositionOption.start));

        assertThat(xml).contains(SELECT_WHILE_SCROLLING);
    }

    @Test
    public void selectWhileScrolling_offAndRelevant_isNotPrinted() {
        final String xml =
                xml(options().selectWhileScrolling(false).snapPosition(SnapPositionOption.start));

        assertThat(xml).doesNotContain(SELECT_WHILE_SCROLLING);
    }

    @Test
    public void scrollWithinContent_withAnotherPosition_isPrinted() {
        final String xml =
                xml(options().scrollWithinContent(true).snapPosition(SnapPositionOption.start));

        assertThat(xml).contains(SCROLL_WITHIN_CONTENT);
    }

    @Test
    public void scrollWithinContent_withOnScreen_isNotPrinted() {
        final String xml =
                xml(options().scrollWithinContent(true).snapPosition(SnapPositionOption.onScreen));

        assertThat(xml).doesNotContain(SCROLL_WITHIN_CONTENT);
    }

    @Test
    public void scrollWithinContent_underCircularScroll_isNotPrinted() {
        final String xml =
                xml(
                        options()
                                .scrollWithinContent(true)
                                .snapPosition(SnapPositionOption.start)
                                .isCircularScroll(true));

        assertThat(xml).doesNotContain(SCROLL_WITHIN_CONTENT);
    }

    @Test
    public void scrollWithinContent_offAndRelevant_isNotPrinted() {
        final String xml =
                xml(options().scrollWithinContent(false).snapPosition(SnapPositionOption.start));

        assertThat(xml).doesNotContain(SCROLL_WITHIN_CONTENT);
    }

    @Test
    public void viewPagerInterval_withoutViewPager_isNotPrinted() {
        final String xml =
                xml(options().isViewPager(false).viewPagerInterval(ViewPagerIntervalOption.one));

        assertThat(xml).doesNotContain(VIEW_PAGER_INTERVAL);
    }

    @Test
    public void viewPagerInterval_withViewPager_isPrinted() {
        final String xml =
                xml(options().isViewPager(true).viewPagerInterval(ViewPagerIntervalOption.one));

        assertThat(xml).contains(VIEW_PAGER_INTERVAL + "\"1\"");
    }

    @Test
    public void snapPosition_underCircularScroll_isStillPrinted() {
        final String xml =
                xml(options().isCircularScroll(true).snapPosition(SnapPositionOption.center));

        assertThat(xml).contains(SNAP_POSITION + "\"center\"");
    }

    @Test
    public void gravity_inAGridView_isAlwaysPrinted() {
        final String xml =
                xml(
                        options()
                                .viewKind(ViewKind.gridView)
                                .orientation(OrientationOption.horizontal)
                                .gravity(GravityOption.left));

        assertThat(xml).contains(GRAVITY + "\"left\"");
    }

    private static PlaygroundOptions.Builder options() {
        return new PlaygroundOptions.Builder();
    }

    private static String xml(final PlaygroundOptions.Builder options) {
        final Context context = ApplicationProvider.getApplicationContext();
        final Resources resources = context.getResources();
        return PlaygroundXml.of(options.build(), resources);
    }
}
