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
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that parchment_orientation and parchment_cellSpacing, parsed by a real LayoutInflater,
 * move real children to real pixels.
 */
@RunWith(AndroidJUnit4.class)
public final class OrientationAndSpacingInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_WIDTH = 200;
    private static final int ITEM_HEIGHT = 100;
    private static final int ITEM_COUNT = 30;
    private static final int CELL_SPACING = 24;
    private static final int NO_CELL_SPACING = 0;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void horizontalOrientationInXml_laysTheChildrenOutAlongTheXAxis() {
        final LaidOutChildren children =
                layOutList(R.layout.instrumented_orientation_horizontal, ITEM_WIDTH, MATCH_PARENT);

        final String laidOut = children.toString();
        assertTrue(laidOut, children.count() > 1);
        assertEquals(laidOut, VIEWPORT_HEIGHT, children.height(0));
        for (int index = 1; index < children.count(); index++) {
            assertEquals(laidOut, children.left(index - 1) + ITEM_WIDTH, children.left(index));
            assertEquals(laidOut, children.top(index - 1), children.top(index));
        }
    }

    @Test
    public void verticalOrientationInXml_laysTheChildrenOutAlongTheYAxis() {
        final LaidOutChildren children =
                layOutList(R.layout.instrumented_orientation_vertical, MATCH_PARENT, ITEM_HEIGHT);

        final String laidOut = children.toString();
        assertTrue(laidOut, children.count() > 1);
        assertEquals(laidOut, VIEWPORT_WIDTH, children.width(0));
        for (int index = 1; index < children.count(); index++) {
            assertEquals(laidOut, children.top(index - 1) + ITEM_HEIGHT, children.top(index));
            assertEquals(laidOut, children.left(index - 1), children.left(index));
        }
    }

    @Test
    public void cellSpacingInXml_putsExactlyThatManyPixelsBetweenVerticalCells() {
        final LaidOutChildren children =
                layOutList(R.layout.instrumented_cell_spacing_vertical, MATCH_PARENT, ITEM_HEIGHT);

        final String laidOut = children.toString();
        assertTrue(laidOut, children.count() > 1);
        for (int index = 1; index < children.count(); index++) {
            final int gap = children.top(index) - children.bottom(index - 1);
            assertEquals(laidOut, CELL_SPACING, gap);
        }
    }

    @Test
    public void cellSpacingInXml_putsExactlyThatManyPixelsBetweenHorizontalCells() {
        final LaidOutChildren children =
                layOutList(R.layout.instrumented_cell_spacing_horizontal, ITEM_WIDTH, MATCH_PARENT);

        final String laidOut = children.toString();
        assertTrue(laidOut, children.count() > 1);
        for (int index = 1; index < children.count(); index++) {
            final int gap = children.left(index) - children.right(index - 1);
            assertEquals(laidOut, CELL_SPACING, gap);
        }
    }

    @Test
    public void aCellSpacingOfZeroInXml_leavesNoGapBetweenCells() {
        final LaidOutChildren children =
                layOutList(R.layout.instrumented_cell_spacing_none, MATCH_PARENT, ITEM_HEIGHT);

        final String laidOut = children.toString();
        assertTrue(laidOut, children.count() > 1);
        for (int index = 1; index < children.count(); index++) {
            final int gap = children.top(index) - children.bottom(index - 1);
            assertEquals(laidOut, NO_CELL_SPACING, gap);
        }
    }

    @Test
    public void cellSpacingInXml_fitsFewerCellsInTheSameViewportThanNoSpacing() {
        final LaidOutChildren withoutSpacing =
                layOutList(R.layout.instrumented_cell_spacing_none, MATCH_PARENT, ITEM_HEIGHT);
        final LaidOutChildren withSpacing =
                layOutList(R.layout.instrumented_cell_spacing_vertical, MATCH_PARENT, ITEM_HEIGHT);

        assertTrue(
                "spacing should push cells out of the viewport: without="
                        + withoutSpacing.count()
                        + " with="
                        + withSpacing.count(),
                withSpacing.count() < withoutSpacing.count());
    }

    private LaidOutChildren layOutList(
            final int layoutResource, final int itemWidth, final int itemHeight) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, itemWidth, itemHeight);
        harness.setAdapter(adapter);
        return harness.children();
    }
}
