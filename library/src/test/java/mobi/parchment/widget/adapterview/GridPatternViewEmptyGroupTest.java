// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.noItems;
import static mobi.parchment.widget.adapterview.PatternFixture.oneWideItem;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternViewJourney.HORIZONTAL_PATTERN;
import static mobi.parchment.widget.adapterview.PatternViewJourney.VERTICAL_PATTERN;
import static mobi.parchment.widget.adapterview.PatternViewJourney.assertRunsLikeTheOracle;

import java.util.List;
import mobi.parchment.widget.adapterview.PatternViewJourney.AddNoGroup;
import mobi.parchment.widget.adapterview.PatternViewJourney.InflatedGridPatternView;
import mobi.parchment.widget.adapterview.PatternViewJourney.ViewDefinitions;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternViewEmptyGroupTest {

    @Test
    public void anEmptyGroupAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_PATTERN, new AddAnEmptyGroup(), new AddNoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_PATTERN, new AddAnEmptyGroup(), new AddNoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_PATTERN, new AddAnEmptyGroupWithAdd(), new AddNoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_PATTERN, new AddAnEmptyGroupWithAdd(), new AddNoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewAfterAClear_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAnEmptyGroupAfterAClear(), new AddNoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewAfterAClear_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAnEmptyGroupAfterAClear(), new AddNoGroup());
    }

    @Test
    public void
            anEmptyGroupAddedToTheViewBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                VERTICAL_PATTERN, new AddAnEmptyGroupBetweenTwoGroups(), new AddTwoGroups());
    }

    @Test
    public void
            anEmptyGroupAddedToTheViewBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                HORIZONTAL_PATTERN, new AddAnEmptyGroupBetweenTwoGroups(), new AddTwoGroups());
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

    private static final class AddAnEmptyGroup implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> noItems = noItems();
            view.addGridPatternGroupDefinition(noItems);
        }
    }

    private static final class AddAnEmptyGroupWithAdd implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> noItems = noItems();
            view.add(noItems);
        }
    }

    private static final class AddAnEmptyGroupAfterAClear implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.clear();
            view.addGridPatternGroupDefinition(noItems);
        }
    }

    private static final class AddAnEmptyGroupBetweenTwoGroups implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(noItems);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }
}
