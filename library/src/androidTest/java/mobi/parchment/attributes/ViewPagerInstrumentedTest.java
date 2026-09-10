// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that parchment_isViewPager pages. Each item fills the viewport, so one page is one item: a
 * paging list answers any fling with exactly one item of travel, while the same fling on an
 * ordinary list keeps going.
 */
@RunWith(AndroidJUnit4.class)
public final class ViewPagerInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = VIEWPORT_HEIGHT;
    private static final int ITEM_COUNT = 10;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int PAGE_TOP = 0;
    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;
    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int FLING_FROM_Y = 550;
    private static final int FLING_TO_Y = 50;
    private static final int FLING_STEPS = 5;
    private static final int SHORT_ITEM_HEIGHT = 100;
    private static final int CENTRED_TOP = (VIEWPORT_HEIGHT - SHORT_ITEM_HEIGHT) / 2;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void viewPagerInXml_answersAFlingWithExactlyOnePage() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), PAGE_TOP, before.topOfAdapterPosition(FIRST_PAGE));

        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "one fling should advance exactly one page: " + after,
                PAGE_TOP,
                after.topOfAdapterPosition(SECOND_PAGE));
        assertEquals(
                "the second page should fill the viewport: " + after,
                VIEWPORT_HEIGHT,
                after.bottom(after.indexOfAdapterPosition(SECOND_PAGE)));
    }

    @Test
    public void viewPagerTurnedOffInXml_letsTheSameFlingRunPastOnePage() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_not_view_pager);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), PAGE_TOP, before.topOfAdapterPosition(FIRST_PAGE));

        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        final LaidOutChildren after = harness.settle();

        final int travelled = travelledPixels(after);
        assertTrue(
                "an ordinary fling should travel further than the one page a pager would: "
                        + travelled
                        + "px\n"
                        + after,
                travelled > ITEM_HEIGHT);
    }

    /** How far the content has moved from rest, read off whichever cell is still laid out. */
    private static int travelledPixels(final LaidOutChildren children) {
        final int firstLaidOutPosition = children.lowestAdapterPosition();
        final int restingTop = firstLaidOutPosition * ITEM_HEIGHT;
        return restingTop - children.topOfAdapterPosition(firstLaidOutPosition);
    }

    @Test
    public void viewPagerInXml_pagesBackTheWayItCame() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_view_pager);

        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        final LaidOutChildren forward = harness.settle();
        assertEquals(
                "the forward fling should have paged: " + forward,
                PAGE_TOP,
                forward.topOfAdapterPosition(SECOND_PAGE));

        harness.fling(GESTURE_X, FLING_TO_Y, GESTURE_X, FLING_FROM_Y, FLING_STEPS);
        final LaidOutChildren back = harness.settle();

        assertEquals(
                "the backward fling should page back to the first item: " + back,
                PAGE_TOP,
                back.topOfAdapterPosition(FIRST_PAGE));
    }

    /**
     * Paging does not override the snap position, which the architecture notes claim it forces to
     * the start: a paging list asked to snap to the centre rests its cells on the centre both
     * before and after it pages.
     */
    @Test
    public void viewPagerInXml_keepsTheCentreSnapItWasGivenRatherThanForcingTheStart() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_view_pager_centered,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, MATCH_PARENT, SHORT_ITEM_HEIGHT);
        harness.setAdapter(adapter);
        final LaidOutChildren atRest = harness.children();

        assertEquals(
                "a paging list should rest on the centre it was given: " + atRest,
                CENTRED_TOP,
                atRest.topOfAdapterPosition(FIRST_PAGE));

        harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
        final LaidOutChildren after = harness.settle();

        assertTrue(
                "it should still rest a cell on the centre after paging: " + after,
                after.hasChildWithTop(CENTRED_TOP));
        assertFalse(
                "the centred cell after paging should not still be the first one: " + after,
                after.isAdapterPositionAtTop(FIRST_PAGE, CENTRED_TOP));
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attach(final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, MATCH_PARENT, ITEM_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }
}
