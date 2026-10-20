// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_AT_THE_ORIGIN;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_IN_THE_SECOND_COLUMN;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_ON_THE_SECOND_ROW;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_THREE_UNITS_HIGH;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_THREE_UNITS_WIDE;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_LEFT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_TOP;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_NO_HEIGHT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_NO_WIDTH;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.noItems;
import static mobi.parchment.widget.adapterview.PatternFixture.oneWideItem;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternJourney.assertDragsForwardInTimeLikeTheOracle;
import static mobi.parchment.widget.adapterview.PatternJourney.assertRunsLikeTheOracle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import mobi.parchment.widget.adapterview.PatternFixture.ChangingItemDefinition;
import mobi.parchment.widget.adapterview.PatternFixture.Definitions;
import mobi.parchment.widget.adapterview.PatternFixture.NoGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternLayoutManagerAdmissionTest {

    private static final GridPatternGroupDefinition NULL_GROUP = null;
    private static final GridPatternItemDefinition NULL_ITEM = null;
    private static final List<GridPatternItemDefinition> NULL_ITEMS = null;
    private static final int THE_FIRST_ITEM = 0;
    private static final int THE_SECOND_ITEM = 1;
    private static final int AFTER_THE_FIRST_ITEM = 1;
    private static final int AFTER_THE_FIRST_GROUP = 1;
    private static final int NO_ITEMS = 0;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    @Test
    public void anItemAddedToAGroupAfterItIsAdded_verticalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnItemAddedToAGroupAfterItIsAdded(), new TwoItemsAdded());
    }

    @Test
    public void anItemAddedToAGroupAfterItIsAdded_horizontalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedToAGroupAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void anItemAddedToAGroupAfterItIsSet_verticalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedToAGroupAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void anItemAddedToAGroupAfterItIsSet_horizontalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedToAGroupAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void theItemsOfAGroupClearedAfterItIsAdded_verticalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new TheItemsOfAGroupClearedAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            theItemsOfAGroupClearedAfterItIsAdded_horizontalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new TheItemsOfAGroupClearedAfterItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void theItemsOfAGroupClearedAfterItIsSet_verticalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new TheItemsOfAGroupClearedAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void theItemsOfAGroupClearedAfterItIsSet_horizontalStartSnap_runsLikeTheGroupAsSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new TheItemsOfAGroupClearedAfterItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnItemAddedToAnEmptyGroupAfterItIsAdded(), new NoGroup());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedToAnEmptyGroupAfterItIsAdded(),
                new NoGroup());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnItemAddedToAnEmptyGroupAfterItIsSet(), new NoGroup());
    }

    @Test
    public void anItemAddedToAnEmptyGroupAfterItIsSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AnItemAddedToAnEmptyGroupAfterItIsSet(), new NoGroup());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterItIsAdded_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new TwoItemsAddedAndTheFirstChanged(ITEM_THREE_UNITS_WIDE),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterItIsAdded_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new TwoItemsAddedAndTheFirstChanged(ITEM_THREE_UNITS_HIGH),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingAlongTheScrollAxisAfterItIsAdded_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new TwoItemsAddedAndTheFirstChanged(ITEM_ON_THE_SECOND_ROW),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingAlongTheScrollAxisAfterItIsAdded_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new TwoItemsAddedAndTheFirstChanged(ITEM_IN_THE_SECOND_COLUMN),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterItIsAdded_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new TwoItemsAddedAndTheFirstChanged(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterItIsAdded_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new TwoItemsAddedAndTheFirstChanged(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemLosingItsHeightAfterItIsAdded_verticalCircularWithoutSpacing_dragsForwardInTimeLikeTheItemAsAdded()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                VERTICAL_CIRCULAR_WITHOUT_SPACING,
                new AnItemAddedAndChanged(ITEM_WITH_NO_HEIGHT),
                new AnItemAdded());
    }

    @Test
    public void
            anItemLosingItsWidthAfterItIsAdded_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeTheItemAsAdded()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                HORIZONTAL_CIRCULAR_WITHOUT_SPACING,
                new AnItemAddedAndChanged(ITEM_WITH_NO_WIDTH),
                new AnItemAdded());
    }

    @Test
    public void aGroupSubclassReturningOtherItems_verticalStartSnap_runsLikeTheItemsItHolds() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupSubclassReturningOtherItemsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void aGroupSubclassReturningOtherItems_horizontalStartSnap_runsLikeTheItemsItHolds() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupSubclassReturningOtherItemsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void aGroupChangedAndThenSetAgain_verticalStartSnap_runsLikeTheChangedGroupSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupChangedAndThenSetAgain(),
                new AListOfThreeItemsSet());
    }

    @Test
    public void aGroupChangedAndThenSetAgain_horizontalStartSnap_runsLikeTheChangedGroupSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupChangedAndThenSetAgain(),
                new AListOfThreeItemsSet());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsAdded_verticalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsAdded_horizontalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsSet_verticalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAGroupBeforeItIsSet_horizontalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new ANullItemPutIntoTheItemsOfAGroupBeforeItIsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            anUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded_verticalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            anUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded_horizontalStartSnap_runsLikeTheGroupWithoutIt() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new ANullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded(),
                new NoGroup());
    }

    @Test
    public void
            aNullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new ANullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded(),
                new NoGroup());
    }

    @Test
    public void
            theSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded_verticalStartSnap_runsLikeTheFirstItemAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new TheSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded(),
                new AnItemAdded());
    }

    @Test
    public void
            theSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded_horizontalStartSnap_runsLikeTheFirstItemAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new TheSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded(),
                new AnItemAdded());
    }

    @Test
    public void
            anItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded_verticalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded_horizontalStartSnap_runsLikeTheGroupAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            anItemGrowingTheListOfGroupsWhileTheListIsSet_verticalStartSnap_runsLikeTheListAsSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemGrowingTheListOfGroupsWhileTheListIsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            anItemGrowingTheListOfGroupsWhileTheListIsSet_horizontalStartSnap_runsLikeTheListAsSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemGrowingTheListOfGroupsWhileTheListIsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationAdded_verticalStartSnap_runsLikeTheGroupBuiltForTheView() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationAdded_horizontalStartSnap_runsLikeTheGroupBuiltForTheView() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationAdded(),
                new TwoItemsAdded());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationSet_verticalStartSnap_runsLikeTheGroupBuiltForTheViewSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationSet_horizontalStartSnap_runsLikeTheGroupBuiltForTheViewSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationAddedBeforeAGroupBuiltForTheView_verticalStartSnap_runsLikeTheTwoGroupsBuiltForTheView() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationAddedBeforeAGroupBuiltForTheView(),
                new TwoGroupsAdded());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationAddedBeforeAGroupBuiltForTheView_horizontalStartSnap_runsLikeTheTwoGroupsBuiltForTheView() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationAddedBeforeAGroupBuiltForTheView(),
                new TwoGroupsAdded());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationSetBeforeAGroupBuiltForTheView_verticalStartSnap_runsLikeTheTwoGroupsBuiltForTheViewSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationSetBeforeAGroupBuiltForTheView(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            aGroupBuiltForTheOtherOrientationSetBeforeAGroupBuiltForTheView_horizontalStartSnap_runsLikeTheTwoGroupsBuiltForTheViewSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupBuiltForTheOtherOrientationSetBeforeAGroupBuiltForTheView(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void aListOfANullGroupSet_verticalStartSnap_runsLikeAnEmptyListSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AListOfANullGroupSet(), new AnEmptyListSet());
    }

    @Test
    public void aListOfANullGroupSet_horizontalStartSnap_runsLikeAnEmptyListSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AListOfANullGroupSet(), new AnEmptyListSet());
    }

    @Test
    public void
            aListOfANullGroupBetweenTwoGroupsSet_verticalStartSnap_runsLikeAListOfTheTwoGroupsSet() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AListOfANullGroupBetweenTwoGroupsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void
            aListOfANullGroupBetweenTwoGroupsSet_horizontalStartSnap_runsLikeAListOfTheTwoGroupsSet() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AListOfANullGroupBetweenTwoGroupsSet(),
                new AListOfTwoGroupsSet());
    }

    @Test
    public void aNullGroupAddedBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new ANullGroupAddedBetweenTwoGroups(), new TwoGroupsAdded());
    }

    @Test
    public void aNullGroupAddedBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new ANullGroupAddedBetweenTwoGroups(), new TwoGroupsAdded());
    }

    @Test
    public void aGroupSubclassCopiedFromANullGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AGroupSubclassCopiedFromANullGroupAdded(), new NoGroup());
    }

    @Test
    public void aGroupSubclassCopiedFromANullGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupSubclassCopiedFromANullGroupAdded(),
                new NoGroup());
    }

    @Test
    public void aGroupDefinitionCopiedFromANullGroup_holdsNoItems() {
        final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup(VERTICAL);

        assertHoldsNoItems(group);
    }

    @Test
    public void aGroupDefinitionCopiedFromANullGroup_vertical_ordersItsItemsForVerticalScroll() {
        final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup(VERTICAL);
        group.addGridPatternItemDefinition(ITEM_IN_THE_SECOND_COLUMN);
        group.addGridPatternItemDefinition(ITEM_ON_THE_SECOND_ROW);

        final int firstGridGroupPosition = group.getFirstGridGroupPosition();

        assertThat(firstGridGroupPosition).isEqualTo(THE_FIRST_ITEM);
    }

    @Test
    public void
            aGroupDefinitionCopiedFromANullGroup_horizontal_ordersItsItemsForHorizontalScroll() {
        final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup(HORIZONTAL);
        group.addGridPatternItemDefinition(ITEM_IN_THE_SECOND_COLUMN);
        group.addGridPatternItemDefinition(ITEM_ON_THE_SECOND_ROW);

        final int firstGridGroupPosition = group.getFirstGridGroupPosition();

        assertThat(firstGridGroupPosition).isEqualTo(THE_SECOND_ITEM);
    }

    @Test
    public void
            aGroupDefinitionCopiedForVerticalScrollFromOneBuiltForHorizontal_ordersItsItemsForVerticalScroll() {
        final List<GridPatternItemDefinition> items = oneInTheSecondColumnThenOneOnTheSecondRow();
        final GridPatternGroupDefinition source = new GridPatternGroupDefinition(HORIZONTAL, items);
        final GridPatternGroupDefinition copy = new AGroupCopiedFromAnother(source, VERTICAL);

        final int firstGridGroupPosition = copy.getFirstGridGroupPosition();

        assertThat(firstGridGroupPosition).isEqualTo(THE_FIRST_ITEM);
    }

    @Test
    public void
            aGroupDefinitionCopiedForHorizontalScrollFromOneBuiltForVertical_ordersItsItemsForHorizontalScroll() {
        final List<GridPatternItemDefinition> items = oneInTheSecondColumnThenOneOnTheSecondRow();
        final GridPatternGroupDefinition source = new GridPatternGroupDefinition(VERTICAL, items);
        final GridPatternGroupDefinition copy = new AGroupCopiedFromAnother(source, HORIZONTAL);

        final int firstGridGroupPosition = copy.getFirstGridGroupPosition();

        assertThat(firstGridGroupPosition).isEqualTo(THE_SECOND_ITEM);
    }

    @Test
    public void aGroupBuiltFromANullListAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AGroupBuiltFromANullListAdded(), new NoGroup());
    }

    @Test
    public void aGroupBuiltFromANullListAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AGroupBuiltFromANullListAdded(), new NoGroup());
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

        final GridPatternGroupDefinition group = new GridPatternGroupDefinition(VERTICAL, items);

        final int numberOfItems = group.getNumberOfItems();
        final int otherItemCount = otherItems.size();
        final List<GridPatternItemDefinition> heldItems = group.getGridPatternItemDefinitions();
        assertThat(numberOfItems).isEqualTo(otherItemCount);
        assertThat(heldItems).containsExactlyElementsOf(otherItems);
    }

    @Test
    public void aNullItemBetweenTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new ANullItemBetweenTwoItemsAdded(), new TwoItemsAdded());
    }

    @Test
    public void aNullItemBetweenTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new ANullItemBetweenTwoItemsAdded(), new TwoItemsAdded());
    }

    @Test
    public void aGroupOfOnlyNullItems_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AGroupOfOnlyNullItemsAdded(), new NoGroup());
    }

    @Test
    public void aGroupOfOnlyNullItems_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AGroupOfOnlyNullItemsAdded(), new NoGroup());
    }

    @Test
    public void aNullItemAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new ANullItemAddedAfterTwoItems(), new TwoItemsAdded());
    }

    @Test
    public void aNullItemAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new ANullItemAddedAfterTwoItems(), new TwoItemsAdded());
    }

    private static void assertHoldsNoItems(final GridPatternGroupDefinition group) {
        final int numberOfItems = group.getNumberOfItems();
        final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();

        assertThat(numberOfItems).isEqualTo(NO_ITEMS);
        assertThat(items).isEmpty();
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

    private static GridPatternGroupDefinition groupForTheOtherOrientation(
            final boolean isVertical, final List<GridPatternItemDefinition> items) {
        final boolean isTheOtherOrientationVertical = !isVertical;
        return new GridPatternGroupDefinition(isTheOtherOrientationVertical, items);
    }

    private static List<GridPatternItemDefinition> oneInTheSecondColumnThenOneOnTheSecondRow() {
        final List<GridPatternItemDefinition> items = noItems();
        items.add(ITEM_IN_THE_SECOND_COLUMN);
        items.add(ITEM_ON_THE_SECOND_ROW);
        return items;
    }

    private static final class TwoItemsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            pattern.addGroup(twoOneByOnes);
        }
    }

    private static final class AListOfTwoItemsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(twoOneByOnesGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class AListOfThreeItemsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            items.add(thirdItem);
            final GridPatternGroupDefinition threeItemsGroup = pattern.group(items);
            final List<GridPatternGroupDefinition> groups = listOf(threeItemsGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class TwoGroupsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            pattern.addGroup(twoOneByOnes);
            pattern.addGroup(oneWideItem);
        }
    }

    private static final class AListOfTwoGroupsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = pattern.group(oneWideItem);
            final List<GridPatternGroupDefinition> groups = listOf(twoOneByOnesGroup);
            groups.add(oneWideItemGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class AnEmptyListSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            pattern.setGroups(noGroups);
        }
    }

    private static final class AnItemAddedToAGroupAfterItIsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            pattern.addGroup(group);
            group.addGridPatternItemDefinition(thirdItem);
        }
    }

    private static final class AnItemAddedToAGroupAfterItIsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            pattern.setGroups(groups);
            group.addGridPatternItemDefinition(thirdItem);
        }
    }

    private static final class TheItemsOfAGroupClearedAfterItIsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            pattern.addGroup(group);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.clear();
        }
    }

    private static final class TheItemsOfAGroupClearedAfterItIsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            pattern.setGroups(groups);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.clear();
        }
    }

    private static final class AnItemAddedToAnEmptyGroupAfterItIsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = pattern.group(noItems);
            pattern.addGroup(group);
            group.addGridPatternItemDefinition(ITEM_AT_THE_ORIGIN);
        }
    }

    private static final class AnItemAddedToAnEmptyGroupAfterItIsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = pattern.group(noItems);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            pattern.setGroups(groups);
            group.addGridPatternItemDefinition(ITEM_AT_THE_ORIGIN);
        }
    }

    private static final class AnItemAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> items = noItems();
            items.add(ITEM_AT_THE_ORIGIN);
            pattern.addGroup(items);
        }
    }

    private static final class AnItemAddedAndChanged implements Definitions {
        private final GridPatternItemDefinition mChanged;

        private AnItemAddedAndChanged(final GridPatternItemDefinition changed) {
            mChanged = changed;
        }

        @Override
        public void defineOn(final PatternFixture pattern) {
            final ChangingItemDefinition item =
                    new ChangingItemDefinition(ITEM_AT_THE_ORIGIN, mChanged);
            final List<GridPatternItemDefinition> items = noItems();
            items.add(item);
            pattern.addGroup(items);
            item.change();
        }
    }

    private static final class TwoItemsAddedAndTheFirstChanged implements Definitions {
        private final GridPatternItemDefinition mChanged;

        private TwoItemsAddedAndTheFirstChanged(final GridPatternItemDefinition changed) {
            mChanged = changed;
        }

        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final ChangingItemDefinition item =
                    new ChangingItemDefinition(ITEM_AT_THE_ORIGIN, mChanged);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.set(THE_FIRST_ITEM, item);
            pattern.addGroup(items);
            item.change();
        }
    }

    private static final class AGroupSubclassReturningOtherItemsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition group =
                    new AGroupReturningOtherItems(isVertical, twoOneByOnes, oneWideItem);
            pattern.addGroup(group);
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
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            pattern.addGroup(group);
            group.addGridPatternItemDefinition(thirdItem);
            pattern.setGroups(groups);
        }
    }

    private static final class ANullItemPutIntoTheItemsOfAGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            pattern.addGroup(group);
        }
    }

    private static final class ANullItemPutIntoTheItemsOfAGroupBeforeItIsSet
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(group);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            pattern.setGroups(groups);
        }
    }

    private static final class AnUndrawableItemPutIntoTheItemsOfAGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(AFTER_THE_FIRST_ITEM, ITEM_WITH_NO_HEIGHT);
            pattern.addGroup(group);
        }
    }

    private static final class ANullItemPutIntoTheItemsOfAnEmptyGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = pattern.group(noItems);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.add(NULL_ITEM);
            pattern.addGroup(group);
        }
    }

    private static final class TheSecondItemRemovedFromTheItemsOfAGroupBeforeItIsAdded
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            final List<GridPatternItemDefinition> items = group.getGridPatternItemDefinitions();
            items.remove(THE_SECOND_ITEM);
            pattern.addGroup(group);
        }
    }

    private static final class AnItemGrowingTheItemsOfItsGroupWhileTheGroupIsAdded
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternItemDefinition secondItem = twoOneByOnes.get(THE_SECOND_ITEM);
            final GridPatternItemDefinition thirdItem = itemAfterTheTwoItems(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = pattern.group(noItems);
            final List<GridPatternItemDefinition> itemsOfTheGroup =
                    group.getGridPatternItemDefinitions();
            final GrowingItemDefinition<GridPatternItemDefinition> growingItem =
                    new GrowingItemDefinition<GridPatternItemDefinition>(
                            ITEM_AT_THE_ORIGIN, itemsOfTheGroup, thirdItem);
            group.addGridPatternItemDefinition(growingItem);
            group.addGridPatternItemDefinition(secondItem);
            growingItem.growOnItsNextRead();
            pattern.addGroup(group);
        }
    }

    private static final class AnItemGrowingTheListOfGroupsWhileTheListIsSet
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final GridPatternItemDefinition secondItem = items.remove(THE_SECOND_ITEM);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> thirdGroupItems = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition thirdGroup = pattern.group(thirdGroupItems);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            final GrowingItemDefinition<GridPatternGroupDefinition> growingItem =
                    new GrowingItemDefinition<GridPatternGroupDefinition>(
                            secondItem, groups, thirdGroup);
            items.add(growingItem);
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(items);
            final GridPatternGroupDefinition oneWideItemGroup = pattern.group(oneWideItem);
            groups.add(twoOneByOnesGroup);
            groups.add(oneWideItemGroup);
            growingItem.growOnItsNextRead();
            pattern.setGroups(groups);
        }
    }

    private static final class AGroupBuiltForTheOtherOrientationAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition otherOrientationGroup =
                    groupForTheOtherOrientation(isVertical, twoOneByOnes);
            pattern.addGroup(otherOrientationGroup);
        }
    }

    private static final class AGroupBuiltForTheOtherOrientationSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition otherOrientationGroup =
                    groupForTheOtherOrientation(isVertical, twoOneByOnes);
            final List<GridPatternGroupDefinition> groups = listOf(otherOrientationGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class AGroupBuiltForTheOtherOrientationAddedBeforeAGroupBuiltForTheView
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition otherOrientationGroup =
                    groupForTheOtherOrientation(isVertical, twoOneByOnes);
            pattern.addGroup(otherOrientationGroup);
            pattern.addGroup(oneWideItem);
        }
    }

    private static final class AGroupBuiltForTheOtherOrientationSetBeforeAGroupBuiltForTheView
            implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition otherOrientationGroup =
                    groupForTheOtherOrientation(isVertical, twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = pattern.group(oneWideItem);
            final List<GridPatternGroupDefinition> groups = listOf(otherOrientationGroup);
            groups.add(oneWideItemGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class AListOfANullGroupSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternGroupDefinition> groups = listOf(NULL_GROUP);
            pattern.setGroups(groups);
        }
    }

    private static final class AListOfANullGroupBetweenTwoGroupsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(twoOneByOnes);
            final GridPatternGroupDefinition oneWideItemGroup = pattern.group(oneWideItem);
            final List<GridPatternGroupDefinition> groups = listOf(twoOneByOnesGroup);
            groups.add(oneWideItemGroup);
            groups.add(AFTER_THE_FIRST_GROUP, NULL_GROUP);
            pattern.setGroups(groups);
        }
    }

    private static final class ANullGroupAddedBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            pattern.addGroup(twoOneByOnes);
            pattern.addGroup(NULL_GROUP);
            pattern.addGroup(oneWideItem);
        }
    }

    private static final class AGroupSubclassCopiedFromANullGroupAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final GridPatternGroupDefinition group = new AGroupCopiedFromANullGroup(isVertical);
            pattern.addGroup(group);
        }
    }

    private static final class AGroupCopiedFromANullGroup extends GridPatternGroupDefinition {
        private AGroupCopiedFromANullGroup(final boolean isVertical) {
            super(NULL_GROUP, isVertical);
        }
    }

    private static final class AGroupCopiedFromAnother extends GridPatternGroupDefinition {
        private AGroupCopiedFromAnother(
                final GridPatternGroupDefinition source, final boolean isVertical) {
            super(source, isVertical);
        }
    }

    private static final class AGroupBuiltFromANullListAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            pattern.addGroup(NULL_ITEMS);
        }
    }

    private static final class ANullItemBetweenTwoItemsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            pattern.addGroup(items);
        }
    }

    private static final class AGroupOfOnlyNullItemsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> items = noItems();
            items.add(NULL_ITEM);
            items.add(NULL_ITEM);
            pattern.addGroup(items);
        }
    }

    private static final class ANullItemAddedAfterTwoItems implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            group.addGridPatternItemDefinition(NULL_ITEM);
            pattern.addGroup(group);
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
