// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
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
import mobi.parchment.harness.PositionRecordingAdapter;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that parchment_isCircularScroll wraps. The list is jumped to its last item and then asked
 * to fill the rest of the viewport: a wrapping list comes back round to item 0, a non-wrapping one
 * stops.
 */
@RunWith(AndroidJUnit4.class)
public final class CircularScrollInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = 100;
    private static final int ITEM_COUNT = 8;
    private static final int LAST_POSITION = ITEM_COUNT - 1;
    private static final int SHORTER_THAN_VIEWPORT_COUNT = 3;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void circularScrollInXml_fillsPastTheLastItemByComingBackRoundToTheFirst() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_circular, ITEM_COUNT);
        harness.setSelection(LAST_POSITION);
        final LaidOutChildren children = harness.children();

        final int lastItemIndex = children.indexOfAdapterPosition(LAST_POSITION);
        assertTrue("the last item should be laid out: " + children, lastItemIndex >= 0);
        assertTrue(
                "the wrap should lay out cells after the last item: " + children,
                lastItemIndex < children.count() - 1);
        assertEquals(
                "item 0 should follow the last item: " + children,
                0,
                children.adapterPosition(lastItemIndex + 1));
    }

    @Test
    public void circularScrollInXml_leavesNoGapAcrossTheWrapPoint() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_circular, ITEM_COUNT);
        harness.setSelection(LAST_POSITION);
        final LaidOutChildren children = harness.children();

        for (int index = 1; index < children.count(); index++) {
            final int previous = children.adapterPosition(index - 1);
            final int expected = (previous + 1) % ITEM_COUNT;
            assertEquals(
                    "the wrap should hand out positions in order: " + children,
                    expected,
                    children.adapterPosition(index));
            assertEquals(
                    "the wrap should leave no gap: " + children,
                    children.bottom(index - 1),
                    children.top(index));
        }
    }

    @Test
    public void circularScrollTurnedOffInXml_stopsAtTheLastItem() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_not_circular, ITEM_COUNT);
        harness.setSelection(LAST_POSITION);
        final LaidOutChildren children = harness.children();

        final int lastItemIndex = children.indexOfAdapterPosition(LAST_POSITION);
        assertTrue("the last item should be laid out: " + children, lastItemIndex >= 0);
        assertEquals(
                "nothing should follow the last item: " + children,
                children.count() - 1,
                lastItemIndex);
        assertEquals(
                "item 0 should not come back round: " + children,
                0,
                children.occurrencesOfAdapterPosition(0));
        assertEquals(
                "the content should be clamped against the end edge: " + children,
                VIEWPORT_HEIGHT,
                children.bottom(lastItemIndex));
    }

    @Test
    public void circularScrollInXml_neverAsksTheAdapterForAWrappedPosition() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_circular,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final PositionRecordingAdapter adapter =
                new PositionRecordingAdapter(context, ITEM_COUNT, MATCH_PARENT, ITEM_HEIGHT);
        harness.setAdapter(adapter);
        harness.setSelection(LAST_POSITION);
        final LaidOutChildren children = harness.children();

        final int lastItemIndex = children.indexOfAdapterPosition(LAST_POSITION);
        assertEquals(
                "the wrap should have happened: " + children,
                0,
                children.adapterPosition(lastItemIndex + 1));
        assertEquals(
                "the adapter should never see a wrapped position, saw " + adapter.requestedRange(),
                0,
                adapter.lowestRequestedPosition());
        assertEquals(
                "the adapter should never see a wrapped position, saw " + adapter.requestedRange(),
                LAST_POSITION,
                adapter.highestRequestedPosition());
    }

    /**
     * The forward fill stops rather than drawing an item that is already on screen, so a list
     * shorter than its viewport does not repeat itself at rest even with wrapping on.
     */
    @Test
    public void circularScrollInXml_withFewerItemsThanFillTheViewport_drawsEachItemOnlyOnce() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_circular, SHORTER_THAN_VIEWPORT_COUNT);
        final LaidOutChildren children = harness.children();

        assertEquals(children.toString(), SHORTER_THAN_VIEWPORT_COUNT, children.count());
        for (int position = 0; position < SHORTER_THAN_VIEWPORT_COUNT; position++) {
            assertEquals(
                    "position " + position + " should be drawn once: " + children,
                    1,
                    children.occurrencesOfAdapterPosition(position));
        }
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attach(
            final int layoutResource, final int itemCount) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, itemCount, MATCH_PARENT, ITEM_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }
}
