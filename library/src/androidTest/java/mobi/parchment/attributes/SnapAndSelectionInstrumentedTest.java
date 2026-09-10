// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * Proves that parchment_snapToPosition, parchment_selectWhileScrolling and parchment_selectOnSnap
 * take effect on a real framework.
 *
 * <p>The drag is deliberately not a whole number of cells. Dragging by a whole cell would leave the
 * content already sitting on a snap position, and every assertion about snapping would then be
 * satisfied by the drag alone.
 */
@RunWith(AndroidJUnit4.class)
public final class SnapAndSelectionInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_HEIGHT = 100;
    private static final int ITEM_COUNT = 20;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int CENTRED_TOP = (VIEWPORT_HEIGHT - ITEM_HEIGHT) / 2;
    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int DRAG_FROM_Y = 500;
    private static final int DRAG_DISTANCE = 245;
    private static final int DRAG_TO_Y = DRAG_FROM_Y - DRAG_DISTANCE;
    private static final int DRAGGED_FIRST_CELL_TOP = CENTRED_TOP - DRAG_DISTANCE;
    // Each touch event's displacement is applied as a whole number of pixels, so a drag spread over
    // several events lands a few pixels short of the finger. The snap it is being told apart from
    // moves the content 45px, well outside this.
    private static final int DRAG_ROUNDING_TOLERANCE = 20;
    private static final int DRAG_STEPS = 8;
    private static final int FIRST_CELL = 0;
    private static final int NEAREST_CELL_AFTER_THE_DRAG = 2;
    private static final int NOTHING_SELECTED = AdapterView.INVALID_POSITION;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void snapToPositionInXml_bringsTheNearestCellOntoTheSnapPositionWhenADragIsReleased() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_snap_to_position);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), CENTRED_TOP, before.topOfAdapterPosition(FIRST_CELL));

        drag(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the cell nearest the centre should be pulled onto it: " + after,
                CENTRED_TOP,
                after.topOfAdapterPosition(NEAREST_CELL_AFTER_THE_DRAG));
        assertFalse(
                "the first cell should no longer be the centred one: " + after,
                after.isAdapterPositionAtTop(FIRST_CELL, CENTRED_TOP));
    }

    @Test
    public void snapToPositionTurnedOffInXml_leavesTheContentWhereTheDragLeftIt() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_no_snap_to_position);
        final LaidOutChildren before = harness.children();
        assertEquals(before.toString(), CENTRED_TOP, before.topOfAdapterPosition(FIRST_CELL));

        drag(harness);
        final LaidOutChildren after = harness.settle();

        final int firstCellTop = after.topOfAdapterPosition(FIRST_CELL);
        final int shortfall = Math.abs(firstCellTop - DRAGGED_FIRST_CELL_TOP);
        assertTrue(
                "the content should stay where the drag left it, expected near "
                        + DRAGGED_FIRST_CELL_TOP
                        + " but was "
                        + firstCellTop
                        + " in "
                        + after,
                shortfall <= DRAG_ROUNDING_TOLERANCE);
        assertFalse(
                "without snapping no cell should come to rest on the centre: " + after,
                after.hasChildWithTop(CENTRED_TOP));
    }

    @Test
    public void snapToPositionInXml_isWhatLetsSetSelectionMoveTheContent() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_snap_to_position);

        harness.setSelection(NEAREST_CELL_AFTER_THE_DRAG);
        final LaidOutChildren after = harness.children();

        assertEquals(
                "the selected cell should be brought to the snap position: " + after,
                CENTRED_TOP,
                after.topOfAdapterPosition(NEAREST_CELL_AFTER_THE_DRAG));
    }

    @Test
    public void snapToPositionTurnedOffInXml_leavesTheContentWhereItIsOnSetSelection() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_no_snap_to_position);
        final LaidOutChildren before = harness.children();

        harness.setSelection(NEAREST_CELL_AFTER_THE_DRAG);
        final LaidOutChildren after = harness.children();

        assertEquals(
                "the content should not have moved: " + after,
                before.topOfAdapterPosition(FIRST_CELL),
                after.topOfAdapterPosition(FIRST_CELL));
    }

    @Test
    public void selectWhileScrollingInXml_selectsTheCellNearestTheSnapPositionWithNoGesture() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_select_while_scrolling);

        assertEquals(FIRST_CELL, harness.selectedItemPosition());
    }

    @Test
    public void selectWhileScrollingInXml_followsTheContentAsItIsDragged() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_select_while_scrolling);
        assertEquals(FIRST_CELL, harness.selectedItemPosition());

        drag(harness);
        harness.settle();

        assertEquals(
                "the selection should follow the cell nearest the centre",
                NEAREST_CELL_AFTER_THE_DRAG,
                harness.selectedItemPosition());
    }

    @Test
    public void selectWhileScrollingTurnedOffInXml_selectsNothing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_no_select_while_scrolling);

        drag(harness);
        harness.settle();

        assertEquals(NOTHING_SELECTED, harness.selectedItemPosition());
    }

    /**
     * A view inflated from XML without a snap position gets onScreen, and selecting while scrolling
     * is switched off under onScreen, so the attribute on its own selects nothing.
     */
    @Test
    public void selectWhileScrollingInXml_withTheDefaultOnScreenSnap_selectsNothing() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_select_while_scrolling_on_screen);

        assertEquals(NOTHING_SELECTED, harness.selectedItemPosition());

        drag(harness);
        harness.settle();

        assertEquals(NOTHING_SELECTED, harness.selectedItemPosition());
    }

    @Test
    public void selectOnSnapInXml_selectsTheCellTheContentSnapsTo() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_select_on_snap);
        assertEquals(NOTHING_SELECTED, harness.selectedItemPosition());

        drag(harness);
        final LaidOutChildren after = harness.settle();

        assertEquals(
                "the content should have snapped onto the centre: " + after,
                CENTRED_TOP,
                after.topOfAdapterPosition(NEAREST_CELL_AFTER_THE_DRAG));
        assertEquals(NEAREST_CELL_AFTER_THE_DRAG, harness.selectedItemPosition());
    }

    @Test
    public void selectOnSnapTurnedOffInXml_snapsWithoutSelectingAnything() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_no_select_on_snap);

        drag(harness);
        final LaidOutChildren after = harness.settle();

        assertTrue(
                "the content should still snap onto the centre: " + after,
                after.hasChildWithTop(CENTRED_TOP));
        assertEquals(NOTHING_SELECTED, harness.selectedItemPosition());
    }

    private static void drag(final ParchmentViewHarness<ListView<BaseAdapter>> harness) {
        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
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
