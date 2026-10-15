// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CENTER_SNAP_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CIRCULAR;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CENTER_SNAP_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.noItems;
import static mobi.parchment.widget.adapterview.PatternFixture.oneWideItem;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternJourney.assertRunsLikeTheOracle;
import static mobi.parchment.widget.adapterview.PatternJourney.assertRunsLikeTheOracleFromAnEmptyAdapter;
import static mobi.parchment.widget.adapterview.PatternJourney.assertRunsLikeTheOracleOnceLaidOutInTime;

import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.PatternFixture.Definitions;
import mobi.parchment.widget.adapterview.PatternFixture.NoGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternLayoutManagerEmptyGroupTest {

    private static final List<GridPatternGroupDefinition> NULL_LIST = null;

    @Test
    public void anEmptyGroupAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_START_SNAP, new AnEmptyGroupAdded(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_START_SNAP, new AnEmptyGroupAdded(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_verticalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_CIRCULAR, new AnEmptyGroupAdded(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_CIRCULAR, new AnEmptyGroupAdded(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_verticalStartSnapFromAnEmptyAdapter_runsLikeNoDefinition() {
        assertRunsLikeTheOracleFromAnEmptyAdapter(
                VERTICAL_START_SNAP, new AnEmptyGroupAdded(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalStartSnapFromAnEmptyAdapter_runsLikeNoDefinition() {
        assertRunsLikeTheOracleFromAnEmptyAdapter(
                HORIZONTAL_START_SNAP, new AnEmptyGroupAdded(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedAfterAClear_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnEmptyGroupAddedAfterAClear(), new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedAfterAClear_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AnEmptyGroupAddedAfterAClear(), new NoGroup());
    }

    @Test
    public void aListOfAnEmptyGroupSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_START_SNAP, new AListOfAnEmptyGroupSet(), new NoGroup());
    }

    @Test
    public void aListOfAnEmptyGroupSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_START_SNAP, new AListOfAnEmptyGroupSet(), new NoGroup());
    }

    @Test
    public void anEmptyListSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_START_SNAP, new AnEmptyListSet(), new NoGroup());
    }

    @Test
    public void anEmptyListSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_START_SNAP, new AnEmptyListSet(), new NoGroup());
    }

    @Test
    public void anEmptyListSetAfterAGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnEmptyListSetAfterAGroup(), new NoGroup());
    }

    @Test
    public void anEmptyListSetAfterAGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AnEmptyListSetAfterAGroup(), new NoGroup());
    }

    @Test
    public void aNullListSetAfterAGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_START_SNAP, new ANullListSetAfterAGroup(), new NoGroup());
    }

    @Test
    public void aNullListSetAfterAGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new ANullListSetAfterAGroup(), new NoGroup());
    }

    @Test
    public void aNullListSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(VERTICAL_START_SNAP, new ANullListSet(), new NoGroup());
    }

    @Test
    public void aNullListSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(HORIZONTAL_START_SNAP, new ANullListSet(), new NoGroup());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnEmptyGroupAddedBetweenTwoGroups(), new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_verticalCircular_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                VERTICAL_CIRCULAR, new AnEmptyGroupAddedBetweenTwoGroups(), new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_horizontalCircular_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                HORIZONTAL_CIRCULAR, new AnEmptyGroupAddedBetweenTwoGroups(), new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnEmptyGroupAddedBeforeAGroup(), new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AnEmptyGroupAddedBeforeAGroup(), new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_verticalCircular_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_CIRCULAR, new AnEmptyGroupAddedBeforeAGroup(), new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_horizontalCircular_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_CIRCULAR, new AnEmptyGroupAddedBeforeAGroup(), new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_verticalCircularWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone()
                    throws InterruptedException {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                VERTICAL_CIRCULAR_WITHOUT_SPACING,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_horizontalCircularWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone()
                    throws InterruptedException {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                HORIZONTAL_CIRCULAR_WITHOUT_SPACING,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_verticalCenterSnapWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone()
                    throws InterruptedException {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                VERTICAL_CENTER_SNAP_WITHOUT_SPACING,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_horizontalCenterSnapWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone()
                    throws InterruptedException {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                HORIZONTAL_CENTER_SNAP_WITHOUT_SPACING,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupAfterAGroup_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new AnEmptyGroupAddedAfterAGroup(), new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupAfterAGroup_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new AnEmptyGroupAddedAfterAGroup(), new OneGroupAdded());
    }

    @Test
    public void aGroupAddedAfterAClear_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP, new OneGroupAddedAfterAClear(), new OneGroupAdded());
    }

    @Test
    public void aGroupAddedAfterAClear_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP, new OneGroupAddedAfterAClear(), new OneGroupAdded());
    }

    @Test
    public void aListOfOneGroupSetAfterAnotherGroup_verticalStartSnap_runsLikeTheListAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AListOfOneGroupSetAfterAnotherGroup(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfOneGroupSetAfterAnotherGroup_horizontalStartSnap_runsLikeTheListAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AListOfOneGroupSetAfterAnotherGroup(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfAnEmptyGroupAndAGroupSet_verticalStartSnap_runsLikeAListOfTheGroupAlone() {
        assertRunsLikeTheOracle(
                VERTICAL_START_SNAP,
                new AListOfAnEmptyGroupAndAGroupSet(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfAnEmptyGroupAndAGroupSet_horizontalStartSnap_runsLikeAListOfTheGroupAlone() {
        assertRunsLikeTheOracle(
                HORIZONTAL_START_SNAP,
                new AListOfAnEmptyGroupAndAGroupSet(),
                new AListOfOneGroupSet());
    }

    private static final class OneGroupAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            pattern.addGroup(twoOneByOnes);
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

    private static final class AListOfOneGroupSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(twoOneByOnesGroup);
            pattern.setGroups(groups);
        }
    }

    private static final class OneGroupAddedAfterAClear implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            pattern.addGroup(oneWideItem);
            pattern.clearGroups();
            pattern.addGroup(twoOneByOnes);
        }
    }

    private static final class AListOfOneGroupSetAfterAnotherGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(twoOneByOnesGroup);
            pattern.addGroup(oneWideItem);
            pattern.setGroups(groups);
        }
    }

    private static final class AnEmptyGroupAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            pattern.addGroup(noItems);
        }
    }

    private static final class AnEmptyGroupAddedAfterAClear implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            final List<GridPatternItemDefinition> noItems = noItems();
            pattern.addGroup(twoOneByOnes);
            pattern.clearGroups();
            pattern.addGroup(noItems);
        }
    }

    private static final class AListOfAnEmptyGroupSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final GridPatternGroupDefinition emptyGroup = pattern.group(noItems);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(emptyGroup);
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

    private static final class AnEmptyListSetAfterAGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            pattern.addGroup(twoOneByOnes);
            pattern.setGroups(noGroups);
        }
    }

    private static final class ANullListSetAfterAGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            pattern.addGroup(twoOneByOnes);
            pattern.setGroups(NULL_LIST);
        }
    }

    private static final class ANullListSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            pattern.setGroups(NULL_LIST);
        }
    }

    private static final class AnEmptyGroupAddedBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            pattern.addGroup(twoOneByOnes);
            pattern.addGroup(noItems);
            pattern.addGroup(oneWideItem);
        }
    }

    private static final class AnEmptyGroupAddedBeforeAGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            pattern.addGroup(noItems);
            pattern.addGroup(twoOneByOnes);
        }
    }

    private static final class AnEmptyGroupAddedAfterAGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            final List<GridPatternItemDefinition> noItems = noItems();
            pattern.addGroup(twoOneByOnes);
            pattern.addGroup(noItems);
        }
    }

    private static final class AListOfAnEmptyGroupAndAGroupSet implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            final GridPatternGroupDefinition emptyGroup = pattern.group(noItems);
            final GridPatternGroupDefinition twoOneByOnesGroup = pattern.group(twoOneByOnes);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(emptyGroup);
            groups.add(twoOneByOnesGroup);
            pattern.setGroups(groups);
        }
    }
}
