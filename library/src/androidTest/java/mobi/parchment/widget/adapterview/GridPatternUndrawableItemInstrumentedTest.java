// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.harness.PatternGroups.group;
import static mobi.parchment.harness.PatternItems.ITEM_AT_THE_ORIGIN;
import static mobi.parchment.harness.PatternItems.ITEM_REACHING_ABOVE_THE_FIRST_ROW;
import static mobi.parchment.harness.PatternItems.ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN;
import static mobi.parchment.harness.PatternItems.ITEM_TWO_COLUMNS_LEFT_OF_THE_FIRST_COLUMN;
import static mobi.parchment.harness.PatternItems.ITEM_TWO_ROWS_ABOVE_THE_FIRST_ROW;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_A_NEGATIVE_HEIGHT;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_A_NEGATIVE_LEFT;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_A_NEGATIVE_TOP;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_A_NEGATIVE_WIDTH;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_NO_HEIGHT;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_NO_WIDTH;
import static mobi.parchment.harness.PatternItems.everyUndrawableItem;
import static mobi.parchment.harness.PatternItems.noItems;
import static mobi.parchment.harness.PatternItems.twoOneByOnes;
import static mobi.parchment.harness.PatternRun.HORIZONTAL_VIEWPORT;
import static mobi.parchment.harness.PatternRun.ITEM_COUNT;
import static mobi.parchment.harness.PatternRun.VERTICAL_VIEWPORT;
import static mobi.parchment.harness.ReachableGridPatternView.layoutManagerOf;
import static org.junit.Assert.assertEquals;

import android.widget.BaseAdapter;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.harness.Definitions;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.NoGroup;
import mobi.parchment.harness.PatternRun;
import mobi.parchment.harness.PatternState;
import mobi.parchment.harness.Viewport;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class GridPatternUndrawableItemInstrumentedTest {

    private static final int THE_FIRST_PLACE = 0;
    private static final int AFTER_THE_FIRST_ITEM = 1;
    private static final int THE_ITEM_AT_THE_ORIGIN = 0;
    private static final int THE_ITEM_BESIDE_IT = 1;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final GridPatternItemDefinition ONE_CELL_HIGH =
            new GridPatternItemDefinition(SECOND_ROW, SECOND_COLUMN, ONE_GRID_UNIT, TWO_GRID_UNITS);
    private static final GridPatternItemDefinition ONE_CELL_WIDE =
            new GridPatternItemDefinition(SECOND_ROW, SECOND_COLUMN, TWO_GRID_UNITS, ONE_GRID_UNIT);
    private static final GridPatternItemDefinition ON_THE_FIRST_ROW =
            new GridPatternItemDefinition(FIRST_ROW, SECOND_COLUMN, TWO_GRID_UNITS, TWO_GRID_UNITS);
    private static final GridPatternItemDefinition IN_THE_FIRST_COLUMN =
            new GridPatternItemDefinition(SECOND_ROW, FIRST_COLUMN, TWO_GRID_UNITS, TWO_GRID_UNITS);

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void aGroupOfAnItemWithNoHeightAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_NO_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithNoHeightAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_NO_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeightAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeightAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new NoGroup());
    }

    @Test
    public void aGroupOfAnItemWithNoWidthAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_NO_WIDTH),
                new NoGroup());
    }

    @Test
    public void aGroupOfAnItemWithNoWidthAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_NO_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidthAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidthAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeTopAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_TOP),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeTopAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_TOP),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeLeftAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_LEFT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeLeftAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfTheItemToTheView(ITEM_WITH_A_NEGATIVE_LEFT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfEveryUndrawableItemToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfEveryUndrawableItemToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void
            everyUndrawableItemBetweenTwoItemsAddedToTheView_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddEveryUndrawableItemBetweenTwoItemsToTheView(),
                new AddTwoItemsToTheView());
    }

    @Test
    public void
            everyUndrawableItemBetweenTwoItemsAddedToTheView_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddEveryUndrawableItemBetweenTwoItemsToTheView(),
                new AddTwoItemsToTheView());
    }

    @Test
    public void anItemWithNoHeightBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoHeightBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeHeightBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeHeightBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeWidthBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeWidthBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeLeftBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeLeftBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingAboveTheFirstRowBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_REACHING_ABOVE_THE_FIRST_ROW),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingAboveTheFirstRowBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_REACHING_ABOVE_THE_FIRST_ROW),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingLeftOfTheFirstColumnBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingLeftOfTheFirstColumnBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemBeforeTwoItems(ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN),
                new TwoItems());
    }

    @Test
    public void anItemWithNoHeightAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoHeightAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeHeightAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeHeightAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeWidthAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeWidthAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeLeftAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeLeftAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new EveryUndrawableItemBetweenTwoItems(),
                new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new EveryUndrawableItemBetweenTwoItems(),
                new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_verticalCircular_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new EveryUndrawableItemBetweenTwoItems(),
                new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_horizontalCircular_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new EveryUndrawableItemBetweenTwoItems(),
                new TwoItems());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupOfEveryUndrawableItem(),
                new NoGroup());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupOfEveryUndrawableItem(),
                new NoGroup());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_verticalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AGroupOfEveryUndrawableItem(),
                new NoGroup());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_horizontalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AGroupOfEveryUndrawableItem(),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithNoHeight_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_WITH_NO_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeight_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidth_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemTwoColumnsLeftOfTheFirstColumn_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_TWO_COLUMNS_LEFT_OF_THE_FIRST_COLUMN),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithNoWidth_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_WITH_NO_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeight_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidth_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemTwoRowsAboveTheFirstRow_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AGroupOfTheItem(ITEM_TWO_ROWS_ABOVE_THE_FIRST_ROW),
                new NoGroup());
    }

    @Test
    public void everyUndrawableItemAddedToAnEmptyGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new EveryUndrawableItemAddedToAnEmptyGroup(),
                new NoGroup());
    }

    @Test
    public void everyUndrawableItemAddedToAnEmptyGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new EveryUndrawableItemAddedToAnEmptyGroup(),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItemsAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupOfEveryUndrawableItemBeforeTwoItems(),
                new TwoItems());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItemsAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupOfEveryUndrawableItemBeforeTwoItems(),
                new TwoItems());
    }

    @Test
    public void
            aListOfEveryUndrawableItemBeforeTwoItemsSet_verticalStartSnap_runsLikeAListOfTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfEveryUndrawableItemBeforeTwoItemsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            aListOfEveryUndrawableItemBeforeTwoItemsSet_horizontalStartSnap_runsLikeAListOfTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfEveryUndrawableItemBeforeTwoItemsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            anItemOneCellHighBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                ONE_CELL_HIGH);
    }

    @Test
    public void
            anItemOneCellHighBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                ONE_CELL_HIGH);
    }

    @Test
    public void
            anItemOneCellWideBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                ONE_CELL_WIDE);
    }

    @Test
    public void
            anItemOneCellWideBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                ONE_CELL_WIDE);
    }

    @Test
    public void
            anItemOnTheFirstRowBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                ON_THE_FIRST_ROW);
    }

    @Test
    public void
            anItemOnTheFirstRowBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                ON_THE_FIRST_ROW);
    }

    @Test
    public void
            anItemInTheFirstColumnBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                IN_THE_FIRST_COLUMN);
    }

    @Test
    public void
            anItemInTheFirstColumnBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                IN_THE_FIRST_COLUMN);
    }

    private void assertRunsLikeTheOracle(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        PatternRun.assertRunsLikeTheOracle(
                scenario, layoutResource, viewport, definitions, oracleDefinitions);
    }

    private void assertDragsForwardInTimeLikeTheOracle(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        PatternRun.assertDragsForwardInTimeLikeTheOracle(
                scenario, layoutResource, viewport, definitions, oracleDefinitions);
    }

    private void assertDrawnOnTheGridCellsItNames(
            final int layoutResource,
            final Viewport viewport,
            final GridPatternItemDefinition item) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        final Definitions definitions = new AddAnItemBesideAnItemAtTheOrigin(item);
        final PatternRun run =
                PatternRun.start(scenario, layoutResource, viewport, definitions, ITEM_COUNT);
        final PatternState laidOut = run.state();
        final String description = laidOut.toString();
        final LaidOutChildren children = laidOut.children();
        final int originIndex = children.indexOfAdapterPosition(THE_ITEM_AT_THE_ORIGIN);
        final int itemIndex = children.indexOfAdapterPosition(THE_ITEM_BESIDE_IT);
        final int cellSpacing = run.cellSpacing();
        final int originLeft = children.left(originIndex);
        final int originTop = children.top(originIndex);
        final int gridCellWidth = children.width(originIndex);
        final int gridCellHeight = children.height(originIndex);
        final int columnStep = gridCellWidth + cellSpacing;
        final int rowStep = gridCellHeight + cellSpacing;
        final int columnsBeforeTheItem = item.getLeft();
        final int rowsAboveTheItem = item.getTop();
        final int columnsTheItemSpans = item.getWidth();
        final int rowsTheItemSpans = item.getHeight();
        final int offsetToItsColumn = columnsBeforeTheItem * columnStep;
        final int offsetToItsRow = rowsAboveTheItem * rowStep;
        final int widthWithTheTrailingSpacing = columnsTheItemSpans * columnStep;
        final int heightWithTheTrailingSpacing = rowsTheItemSpans * rowStep;
        final int expectedLeft = originLeft + offsetToItsColumn;
        final int expectedTop = originTop + offsetToItsRow;
        final int expectedWidth = widthWithTheTrailingSpacing - cellSpacing;
        final int expectedHeight = heightWithTheTrailingSpacing - cellSpacing;

        final int itemLeft = children.left(itemIndex);
        final int itemTop = children.top(itemIndex);
        final int itemWidth = children.width(itemIndex);
        final int itemHeight = children.height(itemIndex);

        assertEquals(description, expectedLeft, itemLeft);
        assertEquals(description, expectedTop, itemTop);
        assertEquals(description, expectedWidth, itemWidth);
        assertEquals(description, expectedHeight, itemHeight);
    }

    private static final class TwoItems implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            layoutManager.addGridPatternGroupDefinition(twoOneByOnesGroup);
        }
    }

    private static final class AListOfTwoItemsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(twoOneByOnesGroup);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AGroupOfTheItem implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AGroupOfTheItem(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(mItem);
            final GridPatternGroupDefinition itemGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemGroup);
        }
    }

    private static final class AddTwoItemsToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
        }
    }

    private static final class AddAGroupOfTheItemToTheView implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AddAGroupOfTheItemToTheView(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(mItem);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AddAGroupOfEveryUndrawableItemToTheViewWithAdd
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            view.add(undrawableItems);
        }
    }

    private static final class AddEveryUndrawableItemBetweenTwoItemsToTheView
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            items.addAll(AFTER_THE_FIRST_ITEM, undrawableItems);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AddAnItemBesideAnItemAtTheOrigin implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AddAnItemBesideAnItemAtTheOrigin(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(ITEM_AT_THE_ORIGIN);
            items.add(mItem);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AnItemBeforeTwoItems implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AnItemBeforeTwoItems(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.add(THE_FIRST_PLACE, mItem);
            final GridPatternGroupDefinition itemsGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemsGroup);
        }
    }

    private static final class AnItemAddedAfterTwoItems implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AnItemAddedAfterTwoItems(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            twoOneByOnesGroup.addGridPatternItemDefinition(mItem);
            layoutManager.addGridPatternGroupDefinition(twoOneByOnesGroup);
        }
    }

    private static final class EveryUndrawableItemBetweenTwoItems implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            items.addAll(AFTER_THE_FIRST_ITEM, undrawableItems);
            final GridPatternGroupDefinition itemsGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemsGroup);
        }
    }

    private static final class AGroupOfEveryUndrawableItem implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            final GridPatternGroupDefinition undrawableGroup = group(isVertical, undrawableItems);
            layoutManager.addGridPatternGroupDefinition(undrawableGroup);
        }
    }

    private static final class EveryUndrawableItemAddedToAnEmptyGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition emptyGroup = group(isVertical, noItems);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            for (final GridPatternItemDefinition undrawableItem : undrawableItems) {
                emptyGroup.addGridPatternItemDefinition(undrawableItem);
            }
            layoutManager.addGridPatternGroupDefinition(emptyGroup);
        }
    }

    private static final class AGroupOfEveryUndrawableItemBeforeTwoItems implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition undrawableGroup = group(isVertical, undrawableItems);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            layoutManager.addGridPatternGroupDefinition(undrawableGroup);
            layoutManager.addGridPatternGroupDefinition(twoOneByOnesGroup);
        }
    }

    private static final class AListOfEveryUndrawableItemBeforeTwoItemsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            items.addAll(THE_FIRST_PLACE, undrawableItems);
            final GridPatternGroupDefinition itemsGroup = group(isVertical, items);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(itemsGroup);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }
}
