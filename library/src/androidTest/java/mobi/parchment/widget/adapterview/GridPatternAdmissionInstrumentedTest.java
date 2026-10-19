// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.harness.PatternGroups.group;
import static mobi.parchment.harness.PatternItems.ITEM_AT_THE_ORIGIN;
import static mobi.parchment.harness.PatternItems.ITEM_IN_THE_SECOND_COLUMN;
import static mobi.parchment.harness.PatternItems.ITEM_ON_THE_SECOND_ROW;
import static mobi.parchment.harness.PatternItems.ITEM_THREE_UNITS_HIGH;
import static mobi.parchment.harness.PatternItems.ITEM_THREE_UNITS_WIDE;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_A_NEGATIVE_LEFT;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_A_NEGATIVE_TOP;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_NO_HEIGHT;
import static mobi.parchment.harness.PatternItems.ITEM_WITH_NO_WIDTH;
import static mobi.parchment.harness.PatternItems.noItems;
import static mobi.parchment.harness.PatternItems.oneWideItem;
import static mobi.parchment.harness.PatternItems.twoOneByOnes;
import static mobi.parchment.harness.PatternRun.HORIZONTAL_VIEWPORT;
import static mobi.parchment.harness.PatternRun.ITEM_COUNT;
import static mobi.parchment.harness.PatternRun.VERTICAL_VIEWPORT;
import static mobi.parchment.harness.ReachableGridPatternView.layoutManagerOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.widget.BaseAdapter;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
public final class GridPatternAdmissionInstrumentedTest {

    private static final GridPatternGroupDefinition NULL_GROUP = null;
    private static final GridPatternItemDefinition NULL_ITEM = null;
    private static final List<GridPatternItemDefinition> NULL_ITEMS = null;
    private static final int THE_FIRST_ITEM = 0;
    private static final int THE_SECOND_ITEM = 1;
    private static final int AFTER_THE_FIRST_ITEM = 1;
    private static final int AFTER_THE_FIRST_GROUP = 1;
    private static final int NO_ITEMS = 0;
    private static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void anItemAddedToAGroupAfterItIsAdded_verticalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedToAGroupAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void anItemAddedToAGroupAfterItIsAdded_horizontalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedToAGroupAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void anItemAddedToAGroupAfterItIsSet_verticalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedToAGroupAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void anItemAddedToAGroupAfterItIsSet_horizontalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedToAGroupAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void theItemsOfAGroupClearedAfterItIsAdded_verticalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new TheItemsOfAGroupClearedAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            theItemsOfAGroupClearedAfterItIsAdded_horizontalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new TheItemsOfAGroupClearedAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void theItemsOfAGroupClearedAfterItIsSet_verticalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new TheItemsOfAGroupClearedAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void theItemsOfAGroupClearedAfterItIsSet_horizontalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new TheItemsOfAGroupClearedAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedToAnEmptyGroupAfterItIsAdded(),
                new NoGroup());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedToAnEmptyGroupAfterItIsAdded(),
                new NoGroup());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemAddedToAnEmptyGroupAfterItIsSet(),
                new NoGroup());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedToAnEmptyGroupAfterItIsSet(),
                new NoGroup());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterItIsAdded_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new TwoItemsAddedAndTheFirstChanged(ITEM_THREE_UNITS_WIDE),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterItIsAdded_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new TwoItemsAddedAndTheFirstChanged(ITEM_THREE_UNITS_HIGH),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingAlongTheScrollAxisAfterItIsAdded_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new TwoItemsAddedAndTheFirstChanged(ITEM_ON_THE_SECOND_ROW),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingAlongTheScrollAxisAfterItIsAdded_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new TwoItemsAddedAndTheFirstChanged(ITEM_IN_THE_SECOND_COLUMN),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterItIsAdded_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new TwoItemsAddedAndTheFirstChanged(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterItIsAdded_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new TwoItemsAddedAndTheFirstChanged(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemLosingItsHeightAfterItIsAdded_verticalCircularWithoutSpacing_dragsForwardInTimeLikeTheItemAsAdded() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AnItemAddedAndChanged(ITEM_WITH_NO_HEIGHT),
                new AnItemAdded());
    }

    @Test
    public void
            anItemLosingItsWidthAfterItIsAdded_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeTheItemAsAdded() {
        assertDragsForwardInTimeLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AnItemAddedAndChanged(ITEM_WITH_NO_WIDTH),
                new AnItemAdded());
    }

    @Test
    public void aGroupSubclassReturningOtherItems_verticalStartSnap_runsLikeTheItemsItHolds() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupSubclassReturningOtherItemsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void aGroupSubclassReturningOtherItems_horizontalStartSnap_runsLikeTheItemsItHolds() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupSubclassReturningOtherItemsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void aGroupChangedAndThenSetAgain_verticalStartSnap_runsLikeTheChangedGroupSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupChangedAndThenSetAgain(),
                new AListOfThreeItemsSet());
    }

    @Test
    public void aGroupChangedAndThenSetAgain_horizontalStartSnap_runsLikeTheChangedGroupSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupChangedAndThenSetAgain(),
                new AListOfThreeItemsSet());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsAdded_verticalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsAdded_horizontalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsSet_verticalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsSet_horizontalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            anUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded_verticalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            anUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded_horizontalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded(),
                new NoGroup());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded(),
                new NoGroup());
    }

    @Test
    public void
            theSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded_verticalStartSnap_runsLikeTheFirstItemAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new TheSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded(),
                new AnItemAdded());
    }

    @Test
    public void
            theSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded_horizontalStartSnap_runsLikeTheFirstItemAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new TheSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded(),
                new AnItemAdded());
    }

    @Test
    public void
            anItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded_verticalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded_horizontalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemGrowingTheListOfGroupsWhileTheListIsSet_verticalStartSnap_runsLikeTheListAsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnItemGrowingTheListOfGroupsWhileTheListIsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            anItemGrowingTheListOfGroupsWhileTheListIsSet_horizontalStartSnap_runsLikeTheListAsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnItemGrowingTheListOfGroupsWhileTheListIsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientation_verticalStartSnap_spansTheViewAlongTheScrollAxis() {
        assertSpansTheViewAlongTheScrollAxis(
                R.layout.instrumented_pattern_reachable_vertical_start, VERTICAL_VIEWPORT);
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientation_horizontalStartSnap_spansTheViewAlongTheScrollAxis() {
        assertSpansTheViewAlongTheScrollAxis(
                R.layout.instrumented_pattern_reachable_horizontal_start, HORIZONTAL_VIEWPORT);
    }

    @Test
    public void aListOfANullGroupSet_verticalStartSnap_runsLikeAnEmptyListSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfANullGroupSet(),
                new AnEmptyListSet());
    }

    @Test
    public void aListOfANullGroupSet_horizontalStartSnap_runsLikeAnEmptyListSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfANullGroupSet(),
                new AnEmptyListSet());
    }

    @Test
    public void
            aListOfANullGroupBetweenTwoGroupsSet_verticalStartSnap_runsLikeAListOfTheTwoGroupsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfANullGroupBetweenTwoGroupsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            aListOfANullGroupBetweenTwoGroupsSet_horizontalStartSnap_runsLikeAListOfTheTwoGroupsSet() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfANullGroupBetweenTwoGroupsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void aNullGroupAddedBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void aNullGroupAddedBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void aGroupSubclassCopiedFromANullGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupSubclassCopiedFromANullGroupAdded(),
                new NoGroup());
    }

    @Test
    public void aGroupSubclassCopiedFromANullGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupSubclassCopiedFromANullGroupAdded(),
                new NoGroup());
    }

    @Test
    public void aGroupDefinitionCopiedFromANullGroup_holdsNoItems() {
        final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup();

        assertHoldsNoItems(group);
    }

    @Test
    public void aGroupDefinitionCopiedFromANullGroup_ordersItsItemsForHorizontalScroll() {
        final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup();
        group.addGridPatternItemDefinition(ITEM_IN_THE_SECOND_COLUMN);
        group.addGridPatternItemDefinition(ITEM_ON_THE_SECOND_ROW);

        final int firstGridGroupPosition = group.getFirstGridGroupPosition();

        assertEquals(THE_SECOND_ITEM, firstGridGroupPosition);
    }

    @Test
    public void aGroupBuiltFromANullListAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupBuiltFromANullListAdded(),
                new NoGroup());
    }

    @Test
    public void aGroupBuiltFromANullListAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupBuiltFromANullListAdded(),
                new NoGroup());
    }

    @Test
    public void aGroupDefinitionBuiltFromANullList_vertical_holdsNoItems() {
        final GridPatternGroupDefinition group =
                new GridPatternGroupDefinition(VERTICAL, NULL_ITEMS);

        assertHoldsNoItems(group);
    }

    @Test
    public void aGroupDefinitionBuiltFromANullList_horizontal_holdsNoItems() {
        final GridPatternGroupDefinition group =
                new GridPatternGroupDefinition(HORIZONTAL, NULL_ITEMS);

        assertHoldsNoItems(group);
    }

    @Test
    public void aGroupDefinitionBuiltFromAListWithANullItem_holdsOnlyTheOtherItems() {
        final List<GridPatternItemDefinition> otherItems = twoOneByOnes(VERTICAL);
        final List<GridPatternItemDefinition> items =
                new ArrayList<GridPatternItemDefinition>(otherItems);
        items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);

        final GridPatternGroupDefinition group = group(VERTICAL, items);

        final int numberOfItems = group.getNumberOfItems();
        final int otherItemCount = otherItems.size();
        final List<GridPatternItemDefinition> heldItems = group.getGridPatternItemDefinitions();
        assertEquals(otherItemCount, numberOfItems);
        assertEquals(otherItems, heldItems);
    }

    @Test
    public void aNullItemBetweenTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullItemBetweenTwoItemsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void aNullItemBetweenTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullItemBetweenTwoItemsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void aGroupOfOnlyNullItems_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AGroupOfOnlyNullItemsAdded(),
                new NoGroup());
    }

    @Test
    public void aGroupOfOnlyNullItems_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AGroupOfOnlyNullItemsAdded(),
                new NoGroup());
    }

    @Test
    public void aNullItemAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullItemAddedAfterTwoItems(),
                new TwoItemsAdded());
    }

    @Test
    public void aNullItemAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullItemAddedAfterTwoItems(),
                new TwoItemsAdded());
    }

    @Test
    public void aNullListAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddANullListToTheView(),
                new NoGroup());
    }

    @Test
    public void aNullListAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddANullListToTheView(),
                new NoGroup());
    }

    @Test
    public void aNullListAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddANullListToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void aNullListAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddANullListToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void aNullListAddedToTheViewBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddANullListToTheViewBetweenTwoGroups(),
                new AddTwoGroupsToTheView());
    }

    @Test
    public void aNullListAddedToTheViewBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddANullListToTheViewBetweenTwoGroups(),
                new AddTwoGroupsToTheView());
    }

    @Test
    public void aNullItemBetweenTwoItemsAddedToTheView_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddANullItemBetweenTwoItemsToTheView(),
                new AddTwoItemsToTheView());
    }

    @Test
    public void aNullItemBetweenTwoItemsAddedToTheView_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddANullItemBetweenTwoItemsToTheView(),
                new AddTwoItemsToTheView());
    }

    @Test
    public void
            aGroupOfOnlyNullItemsAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupOfOnlyNullItemsToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfOnlyNullItemsAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupOfOnlyNullItemsToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void aListClearedAfterItIsAddedToTheView_verticalStartSnap_runsLikeTheListAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAListToTheViewAndThenClearIt(),
                new AddTwoItemsToTheView());
    }

    @Test
    public void aListClearedAfterItIsAddedToTheView_horizontalStartSnap_runsLikeTheListAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAListToTheViewAndThenClearIt(),
                new AddTwoItemsToTheView());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterTheViewTakesIt_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddTwoItemsToTheViewAndChangeTheFirst(ITEM_THREE_UNITS_WIDE),
                new AddTwoItemsToTheView());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterTheViewTakesIt_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddTwoItemsToTheViewAndChangeTheFirst(ITEM_THREE_UNITS_HIGH),
                new AddTwoItemsToTheView());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterTheViewTakesIt_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddTwoItemsToTheViewAndChangeTheFirst(ITEM_WITH_A_NEGATIVE_TOP),
                new AddTwoItemsToTheView());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterTheViewTakesIt_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddTwoItemsToTheViewAndChangeTheFirst(ITEM_WITH_A_NEGATIVE_LEFT),
                new AddTwoItemsToTheView());
    }

    @Test
    public void aGroupAddedAfterTheViewIsCleared_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAGroupToTheViewClearItAndAddAnother(),
                new AddOneWideItemToTheView());
    }

    @Test
    public void aGroupAddedAfterTheViewIsCleared_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAGroupToTheViewClearItAndAddAnother(),
                new AddOneWideItemToTheView());
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

    private void assertSpansTheViewAlongTheScrollAxis(
            final int layoutResource, final Viewport viewport) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        final Definitions definitions = new AGroupBuiltForTheOtherOrientationAdded();
        final PatternRun run =
                PatternRun.start(scenario, layoutResource, viewport, definitions, ITEM_COUNT);
        final PatternState laidOut = run.state();
        final String description = laidOut.toString();
        final LaidOutChildren children = laidOut.children();
        final boolean isVertical = viewport.isVertical();
        final int viewLength = viewLengthAlongTheScrollAxis(children, isVertical);
        final int firstIndex = children.indexOfAdapterPosition(FIRST_POSITION);
        final int secondIndex = children.indexOfAdapterPosition(SECOND_POSITION);

        final int firstLength = lengthAlongTheScrollAxis(children, firstIndex, isVertical);
        final int secondLength = lengthAlongTheScrollAxis(children, secondIndex, isVertical);

        assertEquals(description, viewLength, firstLength);
        assertEquals(description, viewLength, secondLength);
    }

    private static int viewLengthAlongTheScrollAxis(
            final LaidOutChildren children, final boolean isVertical) {
        if (isVertical) return children.viewHeight();
        return children.viewWidth();
    }

    private static int lengthAlongTheScrollAxis(
            final LaidOutChildren children, final int index, final boolean isVertical) {
        if (isVertical) return children.height(index);
        return children.width(index);
    }

    private static void assertHoldsNoItems(final GridPatternGroupDefinition group) {
        final int numberOfItems = group.getNumberOfItems();
        final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
        final boolean holdsNoItems = items.isEmpty();

        assertEquals(NO_ITEMS, numberOfItems);
        assertTrue(items.toString(), holdsNoItems);
    }

    private static GridPatternItemDefinition itemAfterTheTwoItems(final boolean isVertical) {
        if (isVertical) {
            return new GridPatternItemDefinition(
                    SECOND_ROW, FIRST_COLUMN, ONE_GRID_UNIT, TWO_GRID_UNITS);
        }
        return new GridPatternItemDefinition(
                FIRST_ROW, SECOND_COLUMN, TWO_GRID_UNITS, ONE_GRID_UNIT);
    }

    private static List<GridPatternGroupDefinition> listOf(final GridPatternGroupDefinition group) {
        final List<GridPatternGroupDefinition> groups = new ArrayList<GridPatternGroupDefinition>();
        groups.add(group);
        return groups;
    }

    private static final class TwoItemsAdded implements Definitions {
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
            final List<GridPatternGroupDefinition> groups = listOf(twoOneByOnesGroup);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AListOfThreeItemsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            items.add(thirdItem);
            final GridPatternGroupDefinition threeItemsGroup = group(isVertical, items);
            final List<GridPatternGroupDefinition> groups = listOf(threeItemsGroup);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class TwoGroupsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = group(isVertical, oneWideItem);
            layoutManager.addGridPatternGroupDefinition(twoOneByOnesGroup);
            layoutManager.addGridPatternGroupDefinition(oneWideItemGroup);
        }
    }

    private static final class AListOfTwoGroupsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = group(isVertical, oneWideItem);
            final List<GridPatternGroupDefinition> groups = listOf(twoOneByOnesGroup);
            groups.add(oneWideItemGroup);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AnEmptyListSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            layoutManager.setGridPatternGroupDefinitions(noGroups);
        }
    }

    private static final class AnItemAddedToAGroupAfterItIsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            layoutManager.addGridPatternGroupDefinition(group);
            group.addGridPatternItemDefinition(thirdItem);
        }
    }

    private static final class AnItemAddedToAGroupAfterItIsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            layoutManager.setGridPatternGroupDefinitions(groups);
            group.addGridPatternItemDefinition(thirdItem);
        }
    }

    private static final class TheItemsOfAGroupClearedAfterItIsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            layoutManager.addGridPatternGroupDefinition(group);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.clear();
        }
    }

    private static final class TheItemsOfAGroupClearedAfterItIsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            layoutManager.setGridPatternGroupDefinitions(groups);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.clear();
        }
    }

    private static final class AnItemAddedToAnEmptyGroupAfterItIsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = group(isVertical, noItems);
            layoutManager.addGridPatternGroupDefinition(group);
            group.addGridPatternItemDefinition(ITEM_AT_THE_ORIGIN);
        }
    }

    private static final class AnItemAddedToAnEmptyGroupAfterItIsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = group(isVertical, noItems);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            layoutManager.setGridPatternGroupDefinitions(groups);
            group.addGridPatternItemDefinition(ITEM_AT_THE_ORIGIN);
        }
    }

    private static final class AnItemAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = noItems();
            items.add(ITEM_AT_THE_ORIGIN);
            final GridPatternGroupDefinition itemGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemGroup);
        }
    }

    private static final class AnItemAddedAndChanged implements Definitions {
        private final GridPatternItemDefinition mChanged;

        private AnItemAddedAndChanged(final GridPatternItemDefinition changed) {
            mChanged = changed;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final ChangingItemDefinition item =
                    new ChangingItemDefinition(ITEM_AT_THE_ORIGIN, mChanged);
            final List<GridPatternItemDefinition> items = noItems();
            items.add(item);
            final GridPatternGroupDefinition itemGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemGroup);
            item.change();
        }
    }

    private static final class TwoItemsAddedAndTheFirstChanged implements Definitions {
        private final GridPatternItemDefinition mChanged;

        private TwoItemsAddedAndTheFirstChanged(final GridPatternItemDefinition changed) {
            mChanged = changed;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final ChangingItemDefinition item =
                    new ChangingItemDefinition(ITEM_AT_THE_ORIGIN, mChanged);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.set(THE_FIRST_ITEM, item);
            final GridPatternGroupDefinition itemsGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemsGroup);
            item.change();
        }
    }

    private static final class AGroupSubclassReturningOtherItemsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition group =
                    new AGroupReturningOtherItems(isVertical, twoOneByOnes, oneWideItem);
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class AGroupReturningOtherItems extends GridPatternGroupDefinition {
        private final List<GridPatternItemDefinition> mOtherItems;

        private AGroupReturningOtherItems(
                final boolean isVertical,
                final List<GridPatternItemDefinition> items,
                final List<GridPatternItemDefinition> otherItems) {
            super(isVertical, items);
            mOtherItems = otherItems;
        }

        @Override
        public List<GridPatternItemDefinition> getGridPatternItemDefinitions() {
            return mOtherItems;
        }
    }

    private static final class AGroupChangedAndThenSetAgain implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            layoutManager.addGridPatternGroupDefinition(group);
            group.addGridPatternItemDefinition(thirdItem);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class ANullItemPutIntoTheItemsOfAGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class ANullItemPutIntoTheItemsOfAGroupBeforeItIsSet
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AnUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(AFTER_THE_FIRST_ITEM, ITEM_WITH_NO_HEIGHT);
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class ANullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = group(isVertical, noItems);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(NULL_ITEM);
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class TheSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.remove(THE_SECOND_ITEM);
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class AnItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternItemDefinition secondItem = twoOneByOnes.get(THE_SECOND_ITEM);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = group(isVertical, noItems);
            final List<GridPatternItemDefinition> itemsOfTheGroup =
                    group.getGridPatternItemDefinitions();
            final GrowingItemDefinition<GridPatternItemDefinition> growingItem =
                    new GrowingItemDefinition<GridPatternItemDefinition>(
                            ITEM_AT_THE_ORIGIN, itemsOfTheGroup, thirdItem);
            group.addGridPatternItemDefinition(growingItem);
            group.addGridPatternItemDefinition(secondItem);
            growingItem.growOnItsNextRead();
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class AnItemGrowingTheListOfGroupsWhileTheListIsSet
            implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final GridPatternItemDefinition secondItem = items.remove(THE_SECOND_ITEM);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> thirdGroupItems = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition thirdGroup = group(isVertical, thirdGroupItems);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            final GrowingItemDefinition<GridPatternGroupDefinition> growingItem =
                    new GrowingItemDefinition<GridPatternGroupDefinition>(
                            secondItem, groups, thirdGroup);
            items.add(growingItem);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, items);
            final GridPatternGroupDefinition oneWideItemGroup = group(isVertical, oneWideItem);
            groups.add(twoOneByOnesGroup);
            groups.add(oneWideItemGroup);
            growingItem.growOnItsNextRead();
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AGroupBuiltForTheOtherOrientationAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final boolean isTheOtherOrientationVertical = !isVertical;
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition otherOrientationGroup =
                    group(isTheOtherOrientationVertical, twoOneByOnes);
            layoutManager.addGridPatternGroupDefinition(otherOrientationGroup);
        }
    }

    private static final class AListOfANullGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternGroupDefinition> groups = listOf(NULL_GROUP);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AListOfANullGroupBetweenTwoGroupsSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = group(isVertical, oneWideItem);
            final List<GridPatternGroupDefinition> groups = listOf(twoOneByOnesGroup);
            groups.add(oneWideItemGroup);
            groups.add(AFTER_THE_FIRST_GROUP, NULL_GROUP);
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class ANullGroupAddedBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = group(isVertical, twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = group(isVertical, oneWideItem);
            layoutManager.addGridPatternGroupDefinition(twoOneByOnesGroup);
            layoutManager.addGridPatternGroupDefinition(NULL_GROUP);
            layoutManager.addGridPatternGroupDefinition(oneWideItemGroup);
        }
    }

    private static final class AGroupSubclassCopiedFromANullGroupAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup();
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class AGroupCopiedFromANullGroup extends GridPatternGroupDefinition {
        private AGroupCopiedFromANullGroup() {
            super(NULL_GROUP);
        }
    }

    private static final class AGroupBuiltFromANullListAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final GridPatternGroupDefinition nullListGroup = group(isVertical, NULL_ITEMS);
            layoutManager.addGridPatternGroupDefinition(nullListGroup);
        }
    }

    private static final class ANullItemBetweenTwoItemsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            final GridPatternGroupDefinition itemsGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(itemsGroup);
        }
    }

    private static final class AGroupOfOnlyNullItemsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> items = noItems();
            items.add(NULL_ITEM);
            items.add(NULL_ITEM);
            final GridPatternGroupDefinition nullItemsGroup = group(isVertical, items);
            layoutManager.addGridPatternGroupDefinition(nullItemsGroup);
        }
    }

    private static final class ANullItemAddedAfterTwoItems implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = group(isVertical, twoOneByOnes);
            group.addGridPatternItemDefinition(NULL_ITEM);
            layoutManager.addGridPatternGroupDefinition(group);
        }
    }

    private static final class AddTwoItemsToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
        }
    }

    private static final class AddTwoGroupsToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddANullListToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            view.addGridPatternGroupDefinition(NULL_ITEMS);
        }
    }

    private static final class AddANullListToTheViewWithAdd implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            view.add(NULL_ITEMS);
        }
    }

    private static final class AddANullListToTheViewBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(NULL_ITEMS);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddANullItemBetweenTwoItemsToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AddAGroupOfOnlyNullItemsToTheViewWithAdd implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = noItems();
            items.add(NULL_ITEM);
            items.add(NULL_ITEM);
            view.add(items);
        }
    }

    private static final class AddAListToTheViewAndThenClearIt implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            view.addGridPatternGroupDefinition(items);
            items.clear();
        }
    }

    private static final class AddTwoItemsToTheViewAndChangeTheFirst implements Definitions {
        private final GridPatternItemDefinition mChanged;

        private AddTwoItemsToTheViewAndChangeTheFirst(final GridPatternItemDefinition changed) {
            mChanged = changed;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final ChangingItemDefinition item =
                    new ChangingItemDefinition(ITEM_AT_THE_ORIGIN, mChanged);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.set(THE_FIRST_ITEM, item);
            view.addGridPatternGroupDefinition(items);
            item.change();
        }
    }

    private static final class AddOneWideItemToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddAGroupToTheViewClearItAndAddAnother implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.clear();
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class ChangingItemDefinition extends GridPatternItemDefinition {
        private final GridPatternItemDefinition mChanged;
        private final AtomicBoolean mHasChanged = new AtomicBoolean();

        private ChangingItemDefinition(
                final GridPatternItemDefinition asMade, final GridPatternItemDefinition changed) {
            super(asMade.getTop(), asMade.getLeft(), asMade.getHeight(), asMade.getWidth());
            mChanged = changed;
        }

        private void change() {
            mHasChanged.set(true);
        }

        @Override
        public int getTop() {
            final boolean hasChanged = mHasChanged.get();
            if (hasChanged) return mChanged.getTop();
            return super.getTop();
        }

        @Override
        public int getLeft() {
            final boolean hasChanged = mHasChanged.get();
            if (hasChanged) return mChanged.getLeft();
            return super.getLeft();
        }

        @Override
        public int getHeight() {
            final boolean hasChanged = mHasChanged.get();
            if (hasChanged) return mChanged.getHeight();
            return super.getHeight();
        }

        @Override
        public int getWidth() {
            final boolean hasChanged = mHasChanged.get();
            if (hasChanged) return mChanged.getWidth();
            return super.getWidth();
        }
    }

    private static final class GrowingItemDefinition<ELEMENT> extends GridPatternItemDefinition {
        private final List<ELEMENT> mListToGrow;
        private final ELEMENT mGrowth;
        private final AtomicBoolean mGrowsOnItsNextRead = new AtomicBoolean();

        private GrowingItemDefinition(
                final GridPatternItemDefinition asMade,
                final List<ELEMENT> listToGrow,
                final ELEMENT growth) {
            super(asMade.getTop(), asMade.getLeft(), asMade.getHeight(), asMade.getWidth());
            mListToGrow = listToGrow;
            mGrowth = growth;
        }

        private void growOnItsNextRead() {
            mGrowsOnItsNextRead.set(true);
        }

        @Override
        public int getTop() {
            final boolean growsNow = mGrowsOnItsNextRead.getAndSet(false);
            if (growsNow) mListToGrow.add(mGrowth);
            return super.getTop();
        }
    }
}
