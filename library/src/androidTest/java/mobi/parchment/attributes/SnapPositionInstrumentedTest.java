// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
 * Proves that each parchment_snapPosition value rests the content somewhere different, on the very
 * first layout pass and with no gesture. The content is deliberately shorter than the viewport,
 * because that is the case in which all four values disagree.
 */
@RunWith(AndroidJUnit4.class)
public final class SnapPositionInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = 100;
    private static final int SHORT_ITEM_COUNT = 3;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int CONTENT_HEIGHT = SHORT_ITEM_COUNT * ITEM_HEIGHT;
    private static final int START_TOP = 0;
    private static final int CENTER_TOP = (VIEWPORT_HEIGHT - ITEM_HEIGHT) / 2;
    private static final int END_TOP = VIEWPORT_HEIGHT - ITEM_HEIGHT;
    private static final int ON_SCREEN_TOP = (VIEWPORT_HEIGHT - CONTENT_HEIGHT) / 2;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void startSnapInXml_restsTheFirstCellAgainstTheStartEdge() {
        final LaidOutChildren children = layOutList(R.layout.instrumented_snap_start);

        assertEquals(children.toString(), SHORT_ITEM_COUNT, children.count());
        assertEquals(children.toString(), START_TOP, children.top(0));
    }

    @Test
    public void centerSnapInXml_restsTheFirstCellInTheMiddleOfTheViewport() {
        final LaidOutChildren children = layOutList(R.layout.instrumented_snap_center);

        assertEquals(children.toString(), CENTER_TOP, children.top(0));
        final int firstCellCenter = children.top(0) + ITEM_HEIGHT / 2;
        assertEquals(children.toString(), VIEWPORT_HEIGHT / 2, firstCellCenter);
    }

    @Test
    public void endSnapInXml_restsTheFirstCellAgainstTheEndEdge() {
        final LaidOutChildren children = layOutList(R.layout.instrumented_snap_end);

        assertEquals(children.toString(), END_TOP, children.top(0));
        assertEquals(children.toString(), VIEWPORT_HEIGHT, children.bottom(0));
    }

    @Test
    public void onScreenSnapInXml_centresAContentRunShorterThanTheViewport() {
        final LaidOutChildren children = layOutList(R.layout.instrumented_snap_on_screen);

        assertEquals(children.toString(), SHORT_ITEM_COUNT, children.count());
        assertEquals(children.toString(), ON_SCREEN_TOP, children.top(0));
        final int lastIndex = children.count() - 1;
        final int spaceAbove = children.top(0);
        final int spaceBelow = VIEWPORT_HEIGHT - children.bottom(lastIndex);
        assertEquals(children.toString(), spaceAbove, spaceBelow);
    }

    @Test
    public void theFourSnapPositionsInXml_eachRestTheContentSomewhereDifferent() {
        final LaidOutChildren start = layOutList(R.layout.instrumented_snap_start);
        final LaidOutChildren center = layOutList(R.layout.instrumented_snap_center);
        final LaidOutChildren end = layOutList(R.layout.instrumented_snap_end);
        final LaidOutChildren onScreen = layOutList(R.layout.instrumented_snap_on_screen);

        assertNotEquals(start.top(0), center.top(0));
        assertNotEquals(start.top(0), end.top(0));
        assertNotEquals(start.top(0), onScreen.top(0));
        assertNotEquals(center.top(0), end.top(0));
        assertNotEquals(center.top(0), onScreen.top(0));
        assertNotEquals(end.top(0), onScreen.top(0));
    }

    private LaidOutChildren layOutList(final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, SHORT_ITEM_COUNT, MATCH_PARENT, ITEM_HEIGHT);
        harness.setAdapter(adapter);
        return harness.children();
    }
}
