// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;

public enum PatternOption implements RadioOption {
    hero(
            R.id.playground_pattern_hero,
            groups(group(item(0, 0, 4, 4), item(4, 0, 2, 2), item(4, 2, 2, 2)))),
    magazine(
            R.id.playground_pattern_magazine,
            groups(
                    group(item(0, 0, 6, 4)),
                    group(
                            item(0, 0, 2, 2),
                            item(2, 0, 2, 2),
                            item(4, 0, 2, 3),
                            item(0, 2, 4, 4),
                            item(4, 3, 2, 3)),
                    group(
                            item(0, 0, 2, 3),
                            item(2, 0, 4, 4),
                            item(0, 3, 2, 3),
                            item(2, 4, 2, 2),
                            item(4, 4, 2, 2))));

    private final int mRadioButtonId;
    private final List<List<GridPatternItemDefinition>> mGroups;

    PatternOption(final int radioButtonId, final List<List<GridPatternItemDefinition>> groups) {
        mRadioButtonId = radioButtonId;
        mGroups = groups;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public void addTo(final GridPatternView<?> gridPatternView) {
        for (final List<GridPatternItemDefinition> group : mGroups) {
            gridPatternView.addGridPatternGroupDefinition(group);
        }
    }

    private static GridPatternItemDefinition item(
            final int top, final int left, final int height, final int width) {
        return new GridPatternItemDefinition(top, left, height, width);
    }

    private static List<GridPatternItemDefinition> group(final GridPatternItemDefinition... items) {
        return Collections.unmodifiableList(Arrays.asList(items));
    }

    @SafeVarargs
    private static List<List<GridPatternItemDefinition>> groups(
            final List<GridPatternItemDefinition>... groups) {
        final List<List<GridPatternItemDefinition>> list = new ArrayList<>(groups.length);
        for (final List<GridPatternItemDefinition> group : groups) {
            list.add(group);
        }
        return Collections.unmodifiableList(list);
    }
}
