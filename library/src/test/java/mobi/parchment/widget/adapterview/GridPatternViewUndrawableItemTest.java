// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_HEIGHT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_LEFT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_TOP;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_WIDTH;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_NO_HEIGHT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_NO_WIDTH;
import static mobi.parchment.widget.adapterview.PatternFixture.everyUndrawableItem;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternViewJourney.HORIZONTAL_PATTERN;
import static mobi.parchment.widget.adapterview.PatternViewJourney.VERTICAL_PATTERN;
import static mobi.parchment.widget.adapterview.PatternViewJourney.assertRunsLikeTheOracle;

import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.PatternViewJourney.AddNoGroup;
import mobi.parchment.widget.adapterview.PatternViewJourney.InflatedGridPatternView;
import mobi.parchment.widget.adapterview.PatternViewJourney.ViewDefinitions;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternViewUndrawableItemTest {

    private static final int AFTER_THE_FIRST_ITEM = 1;

    @Test
    public void aGroupOfAnItemWithNoHeightAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAGroupOfTheItem(ITEM_WITH_NO_HEIGHT), new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithNoHeightAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAGroupOfTheItem(ITEM_WITH_NO_HEIGHT), new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeightAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeightAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new AddNoGroup());
    }

    @Test
    public void aGroupOfAnItemWithNoWidthAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAGroupOfTheItem(ITEM_WITH_NO_WIDTH), new AddNoGroup());
    }

    @Test
    public void aGroupOfAnItemWithNoWidthAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAGroupOfTheItem(ITEM_WITH_NO_WIDTH), new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidthAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_WIDTH),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidthAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_WIDTH),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeTopAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_TOP),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeTopAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_TOP),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeLeftAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_LEFT),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeLeftAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN,
                new AddAGroupOfTheItem(ITEM_WITH_A_NEGATIVE_LEFT),
                new AddNoGroup());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAGroupOfEveryUndrawableItemWithAdd(), new AddNoGroup());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAGroupOfEveryUndrawableItemWithAdd(), new AddNoGroup());
    }

    @Test
    public void
            everyUndrawableItemBetweenTwoItemsAddedToTheView_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddEveryUndrawableItemBetweenTwoItems(), new AddTwoItems());
    }

    @Test
    public void
            everyUndrawableItemBetweenTwoItemsAddedToTheView_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddEveryUndrawableItemBetweenTwoItems(), new AddTwoItems());
    }

    private static final class AddTwoItems implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
        }
    }

    private static final class AddAGroupOfTheItem implements ViewDefinitions {
        private final GridPatternItemDefinition mItem;

        private AddAGroupOfTheItem(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(mItem);
            view.addGridPatternGroupDefinition(items);
        }
    }

    private static final class AddAGroupOfEveryUndrawableItemWithAdd implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            view.add(undrawableItems);
        }
    }

    private static final class AddEveryUndrawableItemBetweenTwoItems implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            items.addAll(AFTER_THE_FIRST_ITEM, undrawableItems);
            view.addGridPatternGroupDefinition(items);
        }
    }
}
