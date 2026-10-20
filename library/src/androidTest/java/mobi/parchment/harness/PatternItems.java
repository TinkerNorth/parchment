// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;

public final class PatternItems {

    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final int THREE_GRID_UNITS = 3;
    private static final int NO_GRID_UNITS = 0;
    private static final int NEGATIVE_GRID_UNITS = -1;
    private static final int ROW_ABOVE_THE_GROUP = -1;
    private static final int COLUMN_LEFT_OF_THE_GROUP = -1;
    private static final int SECOND_ROW_ABOVE_THE_GROUP = -2;
    private static final int SECOND_COLUMN_LEFT_OF_THE_GROUP = -2;
    public static final GridPatternItemDefinition ITEM_AT_THE_ORIGIN =
            new GridPatternItemDefinition(FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_WITH_NO_HEIGHT =
            new GridPatternItemDefinition(FIRST_ROW, FIRST_COLUMN, NO_GRID_UNITS, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_WITH_A_NEGATIVE_HEIGHT =
            new GridPatternItemDefinition(
                    FIRST_ROW, FIRST_COLUMN, NEGATIVE_GRID_UNITS, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_WITH_NO_WIDTH =
            new GridPatternItemDefinition(FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, NO_GRID_UNITS);
    public static final GridPatternItemDefinition ITEM_WITH_A_NEGATIVE_WIDTH =
            new GridPatternItemDefinition(
                    FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, NEGATIVE_GRID_UNITS);
    public static final GridPatternItemDefinition ITEM_WITH_A_NEGATIVE_TOP =
            new GridPatternItemDefinition(
                    ROW_ABOVE_THE_GROUP, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_WITH_A_NEGATIVE_LEFT =
            new GridPatternItemDefinition(
                    FIRST_ROW, COLUMN_LEFT_OF_THE_GROUP, ONE_GRID_UNIT, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_REACHING_ABOVE_THE_FIRST_ROW =
            new GridPatternItemDefinition(
                    ROW_ABOVE_THE_GROUP, FIRST_COLUMN, TWO_GRID_UNITS, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN =
            new GridPatternItemDefinition(
                    FIRST_ROW, COLUMN_LEFT_OF_THE_GROUP, ONE_GRID_UNIT, TWO_GRID_UNITS);
    public static final GridPatternItemDefinition ITEM_TWO_ROWS_ABOVE_THE_FIRST_ROW =
            new GridPatternItemDefinition(
                    SECOND_ROW_ABOVE_THE_GROUP, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_TWO_COLUMNS_LEFT_OF_THE_FIRST_COLUMN =
            new GridPatternItemDefinition(
                    FIRST_ROW, SECOND_COLUMN_LEFT_OF_THE_GROUP, ONE_GRID_UNIT, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_THREE_UNITS_WIDE =
            new GridPatternItemDefinition(FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, THREE_GRID_UNITS);
    public static final GridPatternItemDefinition ITEM_THREE_UNITS_HIGH =
            new GridPatternItemDefinition(FIRST_ROW, FIRST_COLUMN, THREE_GRID_UNITS, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_ON_THE_SECOND_ROW =
            new GridPatternItemDefinition(SECOND_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    public static final GridPatternItemDefinition ITEM_IN_THE_SECOND_COLUMN =
            new GridPatternItemDefinition(FIRST_ROW, SECOND_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);

    private PatternItems() {}

    public static List<GridPatternItemDefinition> noItems() {
        return new ArrayList<GridPatternItemDefinition>();
    }

    public static List<GridPatternItemDefinition> twoOneByOnes(final boolean isVertical) {
        final GridPatternItemDefinition second = secondOneByOneAcrossTheBreadth(isVertical);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(ITEM_AT_THE_ORIGIN);
        items.add(second);
        return items;
    }

    private static GridPatternItemDefinition secondOneByOneAcrossTheBreadth(
            final boolean isVertical) {
        if (isVertical) {
            return new GridPatternItemDefinition(
                    FIRST_ROW, SECOND_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        }
        return new GridPatternItemDefinition(
                SECOND_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    }

    public static List<GridPatternItemDefinition> oneWideItem(final boolean isVertical) {
        final GridPatternItemDefinition wide = oneItemAcrossTheBreadth(isVertical);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(wide);
        return items;
    }

    private static GridPatternItemDefinition oneItemAcrossTheBreadth(final boolean isVertical) {
        if (isVertical) {
            return new GridPatternItemDefinition(
                    FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, TWO_GRID_UNITS);
        }
        return new GridPatternItemDefinition(
                FIRST_ROW, FIRST_COLUMN, TWO_GRID_UNITS, ONE_GRID_UNIT);
    }

    public static List<GridPatternItemDefinition> everyUndrawableItem() {
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(ITEM_WITH_NO_HEIGHT);
        items.add(ITEM_WITH_A_NEGATIVE_HEIGHT);
        items.add(ITEM_WITH_NO_WIDTH);
        items.add(ITEM_WITH_A_NEGATIVE_WIDTH);
        items.add(ITEM_WITH_A_NEGATIVE_TOP);
        items.add(ITEM_WITH_A_NEGATIVE_LEFT);
        items.add(ITEM_REACHING_ABOVE_THE_FIRST_ROW);
        items.add(ITEM_REACHING_LEFT_OF_THE_FIRST_COLUMN);
        return items;
    }
}
