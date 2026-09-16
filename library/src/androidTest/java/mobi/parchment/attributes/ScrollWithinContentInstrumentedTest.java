// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;

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
 * Proves that parchment_scrollWithinContent holds the content at its end on a real framework. The
 * items do not tile the viewport, so the end of the content is not a cell start: without the
 * attribute a start snap carries the last item all the way to the top edge, with it the content
 * stops with the last item's bottom on the bottom edge and rests there.
 */
@RunWith(AndroidJUnit4.class)
public final class ScrollWithinContentInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = 130;
    private static final int ITEM_COUNT = 9;
    private static final int LAST_ITEM = ITEM_COUNT - 1;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int START_TOP = 0;
    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int FLING_FROM_Y = 550;
    private static final int FLING_TO_Y = 50;
    private static final int FLING_STEPS = 5;
    private static final int FLINGS_TO_REACH_THE_END = 3;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void
            scrollWithinContentInXml_flungToTheEnd_restsWithTheLastItemsBottomOnTheBottomEdge() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_scroll_within_content);

        final LaidOutChildren atTheEnd = flingToTheEnd(harness);

        final int lastItemIndex = atTheEnd.indexOfAdapterPosition(LAST_ITEM);
        assertEquals(
                "the content should stop with its end on the bottom edge: " + atTheEnd,
                VIEWPORT_HEIGHT,
                atTheEnd.bottom(lastItemIndex));
    }

    @Test
    public void scrollWithinContentAbsentFromXml_flungToTheEnd_carriesTheLastItemToTheTopEdge() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_scroll_past_content);

        final LaidOutChildren atTheEnd = flingToTheEnd(harness);

        assertEquals(
                "a start snap should still bring the last item to the top edge: " + atTheEnd,
                START_TOP,
                atTheEnd.topOfAdapterPosition(LAST_ITEM));
    }

    private LaidOutChildren flingToTheEnd(
            final ParchmentViewHarness<ListView<BaseAdapter>> harness) {
        for (int fling = 0; fling < FLINGS_TO_REACH_THE_END; fling++) {
            harness.fling(GESTURE_X, FLING_FROM_Y, GESTURE_X, FLING_TO_Y, FLING_STEPS);
            harness.settle();
        }
        return harness.children();
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
