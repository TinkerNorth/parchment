// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import java.util.List;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;

public final class PatternGroups {

    private PatternGroups() {}

    public static GridPatternGroupDefinition group(
            final boolean isVertical, final List<GridPatternItemDefinition> items) {
        return new GridPatternGroupDefinition(isVertical, items);
    }
}
