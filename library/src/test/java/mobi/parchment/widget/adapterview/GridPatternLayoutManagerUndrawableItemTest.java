// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CIRCULAR;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_AT_THE_ORIGIN;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_REACHING_ABOVE_THE_FIRST_ROW;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_TWO_COLUMNS_LEFT_OF_THE_FIRST_COLUMN;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_TWO_ROWS_ABOVE_THE_FIRST_ROW;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_HEIGHT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_LEFT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_TOP;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_A_NEGATIVE_WIDTH;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_NO_HEIGHT;
import static mobi.parchment.widget.adapterview.PatternFixture.ITEM_WITH_NO_WIDTH;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.everyUndrawableItem;
import static mobi.parchment.widget.adapterview.PatternFixture.noItems;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternJourney.assertDragsForwardInTimeLikeTheOracle;
import static mobi.parchment.widget.adapterview.PatternJourney.assertRunsLikeTheOracle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.PatternFixture.Configuration;
import mobi.parchment.widget.adapterview.PatternFixture.Definitions;
import mobi.parchment.widget.adapterview.PatternFixture.LaidOutChild;
import mobi.parchment.widget.adapterview.PatternFixture.LaidOutPattern;
import mobi.parchment.widget.adapterview.PatternFixture.NoGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternLayoutManagerUndrawableItemTest {

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

    @Test
    public void anItemWithNoHeightBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnItemBeforeTwoItems(ITEM_WITH_NO_HEIGHT), new TwoItems());
    }

    @Test
    public void anItemWithNoHeightBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeHeightBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeHeightBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnItemBeforeTwoItems(ITEM_WITH_NO_WIDTH), new TwoItems());
    }

    @Test
    public void anItemWithNoWidthBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeWidthBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeWidthBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeLeftBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeLeftBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingAboveTheFirstRowBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_REACHING_ABOVE_THE_FIRST_ROW),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingAboveTheFirstRowBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_REACHING_ABOVE_THE_FIRST_ROW),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingLeftOfTheFirstColumnBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN),
                new TwoItems());
    }

    @Test
    public void
            anItemReachingLeftOfTheFirstColumnBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemBeforeTwoItems(ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN),
                new TwoItems());
    }

    @Test
    public void anItemWithNoHeightAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoHeightAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_HEIGHT),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeHeightAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeHeightAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithNoWidthAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_NO_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeWidthAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeWidthAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_WIDTH),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeTopAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_TOP),
                new TwoItems());
    }

    @Test
    public void anItemWithANegativeLeftAddedAfterTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void
            anItemWithANegativeLeftAddedAfterTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnItemAddedAfterTwoItems(ITEM_WITH_A_NEGATIVE_LEFT),
                new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_verticalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new EveryUndrawableItemBetweenTwoItems(), new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_horizontalStartSnap_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new EveryUndrawableItemBetweenTwoItems(), new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_verticalCircular_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_CIRCULAR, new EveryUndrawableItemBetweenTwoItems(), new TwoItems());
    }

    @Test
    public void everyUndrawableItemBetweenTwoItems_horizontalCircular_runsLikeTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_CIRCULAR, new EveryUndrawableItemBetweenTwoItems(), new TwoItems());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AGroupOfEveryUndrawableItem(), new NoGroup());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AGroupOfEveryUndrawableItem(), new NoGroup());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_verticalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_CIRCULAR, new AGroupOfEveryUndrawableItem(), new NoGroup());
    }

    @Test
    public void aGroupOfEveryUndrawableItem_horizontalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_CIRCULAR, new AGroupOfEveryUndrawableItem(), new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithNoHeight_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                VERTICAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_WITH_NO_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeight_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                VERTICAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidth_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                VERTICAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemTwoColumnsLeftOfTheFirstColumn_verticalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                VERTICAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_TWO_COLUMNS_LEFT_OF_THE_FIRST_COLUMN),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithNoWidth_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                HORIZONTAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_WITH_NO_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeHeight_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                HORIZONTAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_HEIGHT),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemWithANegativeWidth_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                HORIZONTAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_WITH_A_NEGATIVE_WIDTH),
                new NoGroup());
    }

    @Test
    public void
            aGroupOfAnItemTwoRowsAboveTheFirstRow_horizontalCircularWithoutSpacing_dragsForwardInTimeLikeNoDefinition()
                    throws InterruptedException {
        assertDragsForwardInTimeLikeTheOracle(
                HORIZONTAL_CIRCULAR_WITHOUT_SPACING,
                new AGroupOfTheItem(ITEM_TWO_ROWS_ABOVE_THE_FIRST_ROW),
                new NoGroup());
    }

    @Test
    public void everyUndrawableItemAddedToAnEmptyGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new EveryUndrawableItemAddedToAnEmptyGroup(), new NoGroup());
    }

    @Test
    public void everyUndrawableItemAddedToAnEmptyGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new EveryUndrawableItemAddedToAnEmptyGroup(), new NoGroup());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemBeforeTwoItems_verticalStartSnap_runsLikeTheTwoItemsAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AGroupOfEveryUndrawableItemBeforeTwoItems(),
                new TwoItems());
    }

    @Test
    public void
            aGroupOfEveryUndrawableItemBeforeTwoItems_horizontalStartSnap_runsLikeTheTwoItemsAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AGroupOfEveryUndrawableItemBeforeTwoItems(),
                new TwoItems());
    }

    @Test
    public void
            aListOfEveryUndrawableItemBeforeTwoItemsSet_verticalStartSnap_runsLikeAListOfTheTwoItems() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AListOfEveryUndrawableItemBeforeTwoItemsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            aListOfEveryUndrawableItemBeforeTwoItemsSet_horizontalStartSnap_runsLikeAListOfTheTwoItems() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AListOfEveryUndrawableItemBeforeTwoItemsSet(),
                new AListOfTwoItemsSet());
    }

    @Test
    public void
            anItemOneCellHighBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(VERTICAL_START_SNAP, ONE_CELL_HIGH);
    }

    @Test
    public void
            anItemOneCellHighBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(HORIZONTAL_START_SNAP, ONE_CELL_HIGH);
    }

    @Test
    public void
            anItemOneCellWideBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(VERTICAL_START_SNAP, ONE_CELL_WIDE);
    }

    @Test
    public void
            anItemOneCellWideBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(HORIZONTAL_START_SNAP, ONE_CELL_WIDE);
    }

    @Test
    public void
            anItemOnTheFirstRowBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(VERTICAL_START_SNAP, ON_THE_FIRST_ROW);
    }

    @Test
    public void
            anItemOnTheFirstRowBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(HORIZONTAL_START_SNAP, ON_THE_FIRST_ROW);
    }

    @Test
    public void
            anItemInTheFirstColumnBesideAnItemAtTheOrigin_verticalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(VERTICAL_START_SNAP, IN_THE_FIRST_COLUMN);
    }

    @Test
    public void
            anItemInTheFirstColumnBesideAnItemAtTheOrigin_horizontalStartSnap_isDrawnOnTheGridCellsItNames() {
        assertDrawnOnTheGridCellsItNames(HORIZONTAL_START_SNAP, IN_THE_FIRST_COLUMN);
    }

    private static void assertDrawnOnTheGridCellsItNames(
            final Configuration configuration, final GridPatternItemDefinition item) {
        final Definitions definitions = new AnItemBesideAnItemAtTheOrigin(item);
        final PatternFixture pattern = PatternFixture.withDefinitions(configuration, definitions);
        final LaidOutPattern laidOut = pattern.snapshot();
        final String description = laidOut.toString();
        final LaidOutChild origin = laidOut.childHolding(THE_ITEM_AT_THE_ORIGIN);
        final LaidOutChild drawnItem = laidOut.childHolding(THE_ITEM_BESIDE_IT);
        final int cellSpacing = pattern.cellSpacing();
        final int originLeft = origin.left();
        final int originTop = origin.top();
        final int gridCellWidth = origin.width();
        final int gridCellHeight = origin.height();
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

        final int itemLeft = drawnItem.left();
        final int itemTop = drawnItem.top();
        final int itemWidth = drawnItem.width();
        final int itemHeight = drawnItem.height();

        assertThat(itemLeft).as(description).isEqualTo(expectedLeft);
        assertThat(itemTop).as(description).isEqualTo(expectedTop);
        assertThat(itemWidth).as(description).isEqualTo(expectedWidth);
        assertThat(itemHeight).as(description).isEqualTo(expectedHeight);
    }

    private static final class TwoItems implements Definitions {
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
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(twoOneByOnesGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class AGroupOfTheItem implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AGroupOfTheItem(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(mItem);
            pattern.addGroup(items);
        }
    }

    private static final class AnItemBesideAnItemAtTheOrigin implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AnItemBesideAnItemAtTheOrigin(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(ITEM_AT_THE_ORIGIN);
            items.add(mItem);
            pattern.addGroup(items);
        }
    }

    private static final class AnItemBeforeTwoItems implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AnItemBeforeTwoItems(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            items.add(THE_FIRST_PLACE, mItem);
            pattern.addGroup(items);
        }
    }

    private static final class AnItemAddedAfterTwoItems implements Definitions {
        private final GridPatternItemDefinition mItem;

        private AnItemAddedAfterTwoItems(final GridPatternItemDefinition item) {
            mItem = item;
        }

        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition group = pattern.group(twoOneByOnes);
            group.addGridPatternItemDefinition(mItem);
            pattern.addGroup(group);
        }
    }

    private static final class EveryUndrawableItemBetweenTwoItems implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            items.addAll(AFTER_THE_FIRST_ITEM, undrawableItems);
            pattern.addGroup(items);
        }
    }

    private static final class AGroupOfEveryUndrawableItem implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            pattern.addGroup(undrawableItems);
        }
    }

    private static final class EveryUndrawableItemAddedToAnEmptyGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition group = pattern.group(noItems);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            for (final GridPatternItemDefinition undrawableItem : undrawableItems) {
                group.addGridPatternItemDefinition(undrawableItem);
            }
            pattern.addGroup(group);
        }
    }

    private static final class AGroupOfEveryUndrawableItemBeforeTwoItems implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            pattern.addGroup(undrawableItems);
            pattern.addGroup(twoOneByOnes);
        }
    }

    private static final class AListOfEveryUndrawableItemBeforeTwoItemsSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> items = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> undrawableItems = everyUndrawableItem();
            items.addAll(THE_FIRST_PLACE, undrawableItems);
            final GridPatternGroupDefinition group = pattern.group(items);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group);
            pattern.setGroups(groups);
        }
    }
}
