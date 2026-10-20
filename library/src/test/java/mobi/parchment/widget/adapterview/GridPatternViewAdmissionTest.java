// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_AT_THE_ORIGIN;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_THREE_UNITS_HIGH;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_THREE_UNITS_WIDE;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_LEFT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_TOP;
import static mobi.parchment.widget.adapterview.PatternFixture.noItems;
import static mobi.parchment.widget.adapterview.PatternFixture.oneWideItem;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternViewJourney.HORIZONTAL_PATTERN;
import static mobi.parchment.widget.adapterview.PatternViewJourney.VERTICAL_PATTERN;
import static mobi.parchment.widget.adapterview.PatternViewJourney.assertRunsLikeTheOracle;

import java.util.List;
import mobi.parchment.widget.adapterview.PatternFixture.ChangingItemDefinition;
import mobi.parchment.widget.adapterview.PatternViewJourney.AddNoGroup;
import mobi.parchment.widget.adapterview.PatternViewJourney.InflatedGridPatternView;
import mobi.parchment.widget.adapterview.PatternViewJourney.ViewDefinitions;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternViewAdmissionTest {

    private static final List<GridPatternItemDefinition> NULL_LIST = null;
    private static final GridPatternItemDefinition NULL_ITEM = null;
    private static final int THE_FIRST_ITEM = 0;
    private static final int AFTER_THE_FIRST_ITEM = 1;

    @Test
    public void aNullListAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_PATTERN, new AddANullList(), new AddNoGroup());
    }

    @Test
    public void aNullListAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_PATTERN, new AddANullList(), new AddNoGroup());
    }

    @Test
    public void aNullListAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_PATTERN, new AddANullListWithAdd(), new AddNoGroup());
    }

    @Test
    public void aNullListAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_PATTERN, new AddANullListWithAdd(), new AddNoGroup());
    }

    @Test
    public void aNullListAddedToTheViewBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddANullListBetweenTwoGroups(), new AddTwoGroups());
    }

    @Test
    public void aNullListAddedToTheViewBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddANullListBetweenTwoGroups(), new AddTwoGroups());
    }

    @Test
    public void aNullItemBetweenTwoItemsAddedToTheView_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddANullItemBetweenTwoItems(), new AddTwoItems());
    }

    @Test
    public void aNullItemBetweenTwoItemsAddedToTheView_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddANullItemBetweenTwoItems(), new AddTwoItems());
    }

    @Test
    public void
            aGroupOfOnlyNullItemsAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAGroupOfOnlyNullItemsWithAdd(), new AddNoGroup());
    }

    @Test
    public void
            aGroupOfOnlyNullItemsAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAGroupOfOnlyNullItemsWithAdd(), new AddNoGroup());
    }

    @Test
    public void aListClearedAfterItIsAddedToTheView_verticalStartSnap_runsLikeTheListAsAdded() {
        assertRunsLikeTheOracle(VERTICAL_PATTERN, new AddAListAndThenClearIt(), new AddTwoItems());
    }

    @Test
    public void aListClearedAfterItIsAddedToTheView_horizontalStartSnap_runsLikeTheListAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAListAndThenClearIt(), new AddTwoItems());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterTheViewTakesIt_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN,
                new AddTwoItemsAndChangeTheFirst(ITEM_THREE_UNITS_WIDE),
                new AddTwoItems());
    }

    @Test
    public void
            anItemGrowingAcrossTheBreadthAfterTheViewTakesIt_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN,
                new AddTwoItemsAndChangeTheFirst(ITEM_THREE_UNITS_HIGH),
                new AddTwoItems());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterTheViewTakesIt_verticalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN,
                new AddTwoItemsAndChangeTheFirst(ITEM_WITH_A_NEGATIVE_TOP),
                new AddTwoItems());
    }

    @Test
    public void
            anItemMovingBeforeItsGroupAfterTheViewTakesIt_horizontalStartSnap_runsLikeTheItemAsAdded() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN,
                new AddTwoItemsAndChangeTheFirst(ITEM_WITH_A_NEGATIVE_LEFT),
                new AddTwoItems());
    }

    @Test
    public void aGroupAddedAfterTheViewIsCleared_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAGroupClearTheViewAndAddAnother(), new AddOneWideItem());
    }

    @Test
    public void aGroupAddedAfterTheViewIsCleared_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAGroupClearTheViewAndAddAnother(), new AddOneWideItem());
    }

    private static final class AddTwoItems implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
        }
    }

    private static final class AddTwoGroups implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddANullList implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            view.addGridPatternGroupDefinition(NULL_LIST);
        }
    }

    private static final class AddANullListWithAdd implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            view.add(NULL_LIST);
        }
    }

    private static final class AddANullListBetweenTwoGroups implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(NULL_LIST);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddANullItemBetweenTwoItems implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.add(AFTER_THE_FIRST_ITEM, NULL_ITEM);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AddAGroupOfOnlyNullItemsWithAdd implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = noItems();
            items.add(NULL_ITEM);
            items.add(NULL_ITEM);
            view.add(items);
        }
    }

    private static final class AddAListAndThenClearIt implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            view.addGridPatternGroupDefinition(items);
            items.clear();
        }
    }

    private static final class AddTwoItemsAndChangeTheFirst implements ViewDefinitions {
        private final GridPatternItemDefinition mChanged;

        private AddTwoItemsAndChangeTheFirst(final GridPatternItemDefinition changed) {
            mChanged = changed;
        }

        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final ChangingItemDefinition item =
                    new ChangingItemDefinition(ITEM_AT_THE_ORIGIN, mChanged);
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.set(THE_FIRST_ITEM, item);
            view.addGridPatternGroupDefinition(items);
            item.change();
        }
    }

    private static final class AddOneWideItem implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddAGroupClearTheViewAndAddAnother implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.clear();
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }
}
