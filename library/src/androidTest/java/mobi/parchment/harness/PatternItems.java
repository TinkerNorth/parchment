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

    private PatternItems() {}

    public static List<GridPatternItemDefinition> noItems() {
        return new ArrayList<GridPatternItemDefinition>();
    }

    public static List<GridPatternItemDefinition> twoOneByOnes(final boolean isVertical) {
        final GridPatternItemDefinition first =
                new GridPatternItemDefinition(
                        FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        final GridPatternItemDefinition second = secondOneByOneAcrossTheBreadth(isVertical);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(first);
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
}
