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
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves that parchment_ratio sizes a pattern cell. The pattern is two one-by-one grid units side
 * by side, so the breadth fixes the unit's width and the ratio alone decides its height.
 */
@RunWith(AndroidJUnit4.class)
public final class GridPatternRatioInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int ITEM_COUNT = 10;
    private static final int UNITS_ACROSS = 2;
    private static final int UNIT_WIDTH = VIEWPORT_WIDTH / UNITS_ACROSS;
    private static final int HALF_RATIO_UNIT_HEIGHT = UNIT_WIDTH / 2;
    private static final int DOUBLE_RATIO_UNIT_HEIGHT = UNIT_WIDTH * 2;
    private static final int RATIO_FACTOR = 4;
    private static final int FIRST_CELL_TOP = 0;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void aRatioOfTwoInXml_makesTheGridUnitTwiceAsTallAsItIsWide() {
        final LaidOutChildren children = layOutPattern(R.layout.instrumented_pattern_ratio_two);

        assertEquals(children.toString(), UNIT_WIDTH, children.width(0));
        assertEquals(children.toString(), DOUBLE_RATIO_UNIT_HEIGHT, children.height(0));
    }

    @Test
    public void aRatioOfAHalfInXml_makesTheGridUnitHalfAsTallAsItIsWide() {
        final LaidOutChildren children = layOutPattern(R.layout.instrumented_pattern_ratio_half);

        assertEquals(children.toString(), UNIT_WIDTH, children.width(0));
        assertEquals(children.toString(), HALF_RATIO_UNIT_HEIGHT, children.height(0));
    }

    @Test
    public void theRatioInXml_changesTheCellHeightWithoutChangingItsWidth() {
        final LaidOutChildren tall = layOutPattern(R.layout.instrumented_pattern_ratio_two);
        final LaidOutChildren half = layOutPattern(R.layout.instrumented_pattern_ratio_half);

        assertEquals(tall.width(0), half.width(0));
        assertNotEquals(tall.height(0), half.height(0));
        assertEquals(tall.height(0), half.height(0) * RATIO_FACTOR);
    }

    @Test
    public void aTwoUnitPatternInXml_putsTheSecondItemBesideTheFirst() {
        final LaidOutChildren children = layOutPattern(R.layout.instrumented_pattern_ratio_half);

        final int firstIndex = children.indexOfAdapterPosition(0);
        final int secondIndex = children.indexOfAdapterPosition(1);
        assertEquals(children.toString(), 0, children.left(firstIndex));
        assertEquals(children.toString(), UNIT_WIDTH, children.left(secondIndex));
        assertEquals(children.toString(), FIRST_CELL_TOP, children.top(firstIndex));
        assertEquals(children.toString(), FIRST_CELL_TOP, children.top(secondIndex));
    }

    @Test
    public void aTwoUnitPatternInXml_startsTheNextGroupBelowTheFirst() {
        final LaidOutChildren children = layOutPattern(R.layout.instrumented_pattern_ratio_half);

        assertEquals(
                "the third item opens the second group: " + children,
                HALF_RATIO_UNIT_HEIGHT,
                children.topOfAdapterPosition(UNITS_ACROSS));
    }

    private LaidOutChildren layOutPattern(final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        layoutResource,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AddTwoUnitSquares());
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, MATCH_PARENT, MATCH_PARENT);
        harness.setAdapter(adapter);
        return harness.children();
    }

    /**
     * Adds a pattern of two one-by-one grid units side by side. The definition takes its arguments
     * as top, left, height, width.
     */
    private static final class AddTwoUnitSquares
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {

        private static final int ONE_UNIT = 1;

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final List<GridPatternItemDefinition> definitions = new ArrayList<>();
            for (int column = 0; column < UNITS_ACROSS; column++) {
                definitions.add(new GridPatternItemDefinition(0, column, ONE_UNIT, ONE_UNIT));
            }
            view.addGridPatternGroupDefinition(definitions);
        }
    }
}
