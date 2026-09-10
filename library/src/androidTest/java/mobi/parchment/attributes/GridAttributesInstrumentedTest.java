// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import mobi.parchment.harness.AlternatingHeightAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridview.GridView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that parchment_numberOfViewsPerCell and parchment_gravity shape a real grid row. The
 * adapter alternates tall and short items so the gravity that places a view inside its row is
 * visible, and the items are narrower than their share of the breadth so the gravity that places
 * the row across the breadth is visible too.
 */
@RunWith(AndroidJUnit4.class)
public final class GridAttributesInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_WIDTH = 200;
    private static final int TALL_HEIGHT = 200;
    private static final int SHORT_HEIGHT = 100;
    private static final int ITEM_COUNT = 12;
    private static final int VIEWS_PER_CELL = 3;
    private static final int ROW_BREADTH = ITEM_WIDTH * VIEWS_PER_CELL;
    private static final int CENTRED_ROW_LEFT = (VIEWPORT_WIDTH - ROW_BREADTH) / 2;
    private static final int RIGHT_ROW_LEFT = VIEWPORT_WIDTH - ROW_BREADTH;
    private static final int LEFT_ROW_LEFT = 0;
    private static final int FIRST_ROW_TOP = 0;
    private static final int SHORT_ITEM_POSITION = 1;
    private static final int BOTTOM_GRAVITY_TOP = TALL_HEIGHT - SHORT_HEIGHT;
    private static final int CENTRED_IN_ROW_TOP = (TALL_HEIGHT - SHORT_HEIGHT) / 2;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void numberOfViewsPerCellInXml_putsThatManyItemsSideBySideInOneRow() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_default);

        for (int position = 0; position < VIEWS_PER_CELL; position++) {
            assertEquals(
                    "position " + position + " belongs to the first row: " + children,
                    FIRST_ROW_TOP,
                    children.topOfAdapterPosition(position));
        }
        assertEquals(
                "the row is as tall as its tallest item: " + children,
                TALL_HEIGHT,
                children.topOfAdapterPosition(VIEWS_PER_CELL));
    }

    @Test
    public void oneViewPerCellInXml_putsEachItemInItsOwnRow() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_one_per_cell);

        assertEquals(
                "the first item starts the list: " + children,
                FIRST_ROW_TOP,
                children.topOfAdapterPosition(0));
        assertEquals(
                "the second item follows the first down the list: " + children,
                TALL_HEIGHT,
                children.topOfAdapterPosition(1));
        assertNotEquals(
                "one view per cell should not put two items side by side: " + children,
                children.topOfAdapterPosition(0),
                children.topOfAdapterPosition(1));
    }

    @Test
    public void aGridRowWithNoGravityInXml_isCentredAcrossTheBreadth() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_default);

        assertRowIsPackedFrom(children, CENTRED_ROW_LEFT);
    }

    @Test
    public void leftGravityInXml_packsTheRowAgainstTheStartOfTheBreadth() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_left_bottom);

        assertRowIsPackedFrom(children, LEFT_ROW_LEFT);
    }

    @Test
    public void rightGravityInXml_packsTheRowAgainstTheEndOfTheBreadth() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_right_top);

        assertRowIsPackedFrom(children, RIGHT_ROW_LEFT);
    }

    /**
     * Top gravity is what a grid does with no gravity at all, so the vertical half of the attribute
     * is shown by a layout that names only a horizontal flag: the short item is then centred in its
     * row rather than aligned to the top.
     */
    @Test
    public void aGridRowWithNoVerticalGravityInXml_centresAShortItemInItsRow() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_right_only);

        assertEquals(
                "a short item with no vertical gravity is centred in its row: " + children,
                CENTRED_IN_ROW_TOP,
                children.topOfAdapterPosition(SHORT_ITEM_POSITION));
    }

    @Test
    public void bottomGravityInXml_pushesAShortItemDownItsRow() {
        final LaidOutChildren children = layOutGrid(R.layout.instrumented_grid_left_bottom);

        assertEquals(
                "a bottom-gravity short item is pushed down its row: " + children,
                BOTTOM_GRAVITY_TOP,
                children.topOfAdapterPosition(SHORT_ITEM_POSITION));
        assertNotEquals(
                "bottom gravity should not leave the short item at the top: " + children,
                FIRST_ROW_TOP,
                children.topOfAdapterPosition(SHORT_ITEM_POSITION));
    }

    private static void assertRowIsPackedFrom(final LaidOutChildren children, final int rowLeft) {
        for (int position = 0; position < VIEWS_PER_CELL; position++) {
            final int index = children.indexOfAdapterPosition(position);
            assertEquals(
                    "item " + position + " sits in its column: " + children,
                    rowLeft + position * ITEM_WIDTH,
                    children.left(index));
            assertEquals(
                    "item " + position + " keeps its width: " + children,
                    ITEM_WIDTH,
                    children.width(index));
        }
    }

    private LaidOutChildren layOutGrid(final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final AlternatingHeightAdapter adapter =
                new AlternatingHeightAdapter(
                        context, ITEM_COUNT, ITEM_WIDTH, TALL_HEIGHT, SHORT_HEIGHT);
        harness.setAdapter(adapter);
        return harness.children();
    }
}
