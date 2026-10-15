// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.harness.PatternGroups.group;
import static mobi.parchment.harness.PatternItems.noItems;
import static mobi.parchment.harness.PatternItems.oneWideItem;
import static mobi.parchment.harness.PatternItems.twoOneByOnes;
import static mobi.parchment.harness.PatternRun.HORIZONTAL_VIEWPORT;
import static mobi.parchment.harness.PatternRun.ITEM_COUNT;
import static mobi.parchment.harness.PatternRun.NO_ITEMS;
import static mobi.parchment.harness.PatternRun.VERTICAL_VIEWPORT;
import static mobi.parchment.harness.PatternRun.assertTravelsLikeTheOracle;
import static mobi.parchment.harness.PatternState.assertSameState;
import static mobi.parchment.harness.ReachableGridPatternView.layoutManagerOf;
import static org.junit.Assert.assertEquals;

import android.widget.BaseAdapter;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.harness.Definitions;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.Journey;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.NoGroup;
import mobi.parchment.harness.PatternRun;
import mobi.parchment.harness.PatternState;
import mobi.parchment.harness.Viewport;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class GridPatternEmptyGroupInstrumentedTest {

    private static final List<GridPatternGroupDefinition> NULL_LIST = null;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void anEmptyGroupAddedToTheView_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheView(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheView_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheView(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewWithAdd_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewWithAdd_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewWithAdd(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewAfterAClear_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewAfterAClear(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedToTheViewAfterAClear_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewAfterAClear(),
                new NoGroup());
    }

    @Test
    public void
            anEmptyGroupAddedToTheViewBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewBetweenTwoGroups(),
                new AddTwoGroupsToTheView());
    }

    @Test
    public void
            anEmptyGroupAddedToTheViewBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AddAnEmptyGroupToTheViewBetweenTwoGroups(),
                new AddTwoGroupsToTheView());
    }

    @Test
    public void anEmptyGroupAdded_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_verticalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalCircular_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_verticalStartSnapFromAnEmptyAdapter_runsLikeNoDefinition() {
        assertRunsLikeTheOracleFromAnEmptyAdapter(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAdded_horizontalStartSnapFromAnEmptyAdapter_runsLikeNoDefinition() {
        assertRunsLikeTheOracleFromAnEmptyAdapter(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAdded(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedAfterAClear_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAClear(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupAddedAfterAClear_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAClear(),
                new NoGroup());
    }

    @Test
    public void aListOfAnEmptyGroupSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfAnEmptyGroupSet(),
                new NoGroup());
    }

    @Test
    public void aListOfAnEmptyGroupSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfAnEmptyGroupSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyListSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyListSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSetAfterAGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void anEmptyListSetAfterAGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void aNullListSetAfterAGroup_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void aNullListSetAfterAGroup_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullListSetAfterAGroup(),
                new NoGroup());
    }

    @Test
    public void aNullListSet_verticalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new ANullListSet(),
                new NoGroup());
    }

    @Test
    public void aNullListSet_horizontalStartSnap_runsLikeNoDefinition() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new ANullListSet(),
                new NoGroup());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_verticalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_horizontalStartSnap_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_verticalCircular_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBetweenTwoGroups_horizontalCircular_runsLikeTheTwoGroups() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBetweenTwoGroups(),
                new TwoGroupsAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_verticalCircular_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_circular,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupBeforeAGroup_horizontalCircular_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_circular,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_verticalCircularWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_horizontalCircularWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_horizontal_circular_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_verticalCenterSnapWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_vertical_center_no_spacing,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void
            anEmptyGroupBeforeAGroup_horizontalCenterSnapWithoutSpacing_laysOutInTimeAndRunsLikeTheGroupAlone() {
        assertRunsLikeTheOracleOnceLaidOutInTime(
                R.layout.instrumented_pattern_reachable_horizontal_center_no_spacing,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedBeforeAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupAfterAGroup_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void anEmptyGroupAfterAGroup_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AnEmptyGroupAddedAfterAGroup(),
                new OneGroupAdded());
    }

    @Test
    public void aGroupAddedAfterAClear_verticalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new OneGroupAddedAfterAClear(),
                new OneGroupAdded());
    }

    @Test
    public void aGroupAddedAfterAClear_horizontalStartSnap_runsLikeTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new OneGroupAddedAfterAClear(),
                new OneGroupAdded());
    }

    @Test
    public void aListOfOneGroupSetAfterAnotherGroup_verticalStartSnap_runsLikeTheListAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfOneGroupSetAfterAnotherGroup(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfOneGroupSetAfterAnotherGroup_horizontalStartSnap_runsLikeTheListAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfOneGroupSetAfterAnotherGroup(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfAnEmptyGroupAndAGroupSet_verticalStartSnap_runsLikeAListOfTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_vertical_start,
                VERTICAL_VIEWPORT,
                new AListOfAnEmptyGroupAndAGroupSet(),
                new AListOfOneGroupSet());
    }

    @Test
    public void aListOfAnEmptyGroupAndAGroupSet_horizontalStartSnap_runsLikeAListOfTheGroupAlone() {
        assertRunsLikeTheOracle(
                R.layout.instrumented_pattern_reachable_horizontal_start,
                HORIZONTAL_VIEWPORT,
                new AListOfAnEmptyGroupAndAGroupSet(),
                new AListOfOneGroupSet());
    }

    private void assertRunsLikeTheOracle(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        PatternRun.assertRunsLikeTheOracle(
                scenario, layoutResource, viewport, definitions, oracleDefinitions);
    }

    private void assertRunsLikeTheOracleOnceLaidOutInTime(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternRun oracleRun = start(layoutResource, viewport, oracleDefinitions, ITEM_COUNT);
        final PatternState oracleLayout = oracleRun.state();
        final Journey oracle = oracleRun.travelOn(oracleLayout);
        final PatternRun patternRun = startInTime(layoutResource, viewport, definitions);
        final PatternState patternLayout = patternRun.state();
        final Journey pattern = patternRun.travelOn(patternLayout);

        assertTravelsLikeTheOracle(pattern, oracle);
    }

    private void assertRunsLikeTheOracleFromAnEmptyAdapter(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternRun oracleRun = start(layoutResource, viewport, oracleDefinitions, NO_ITEMS);
        final PatternState emptyOracleLayout = oracleRun.state();
        final PatternState filledOracleLayout = oracleRun.changeTheCount(ITEM_COUNT);
        final Journey oracle = oracleRun.travelOn(filledOracleLayout);
        final PatternRun patternRun = start(layoutResource, viewport, definitions, NO_ITEMS);
        final PatternState emptyPatternLayout = patternRun.state();
        final PatternState filledPatternLayout = patternRun.changeTheCount(ITEM_COUNT);
        final Journey pattern = patternRun.travelOn(filledPatternLayout);

        final LaidOutChildren emptyOracleChildren = emptyOracleLayout.children();
        final int emptyChildCount = emptyOracleChildren.count();
        assertEquals(emptyOracleLayout.toString(), NO_ITEMS, emptyChildCount);
        assertSameState(emptyPatternLayout, emptyOracleLayout);
        assertTravelsLikeTheOracle(pattern, oracle);
    }

    private PatternRun startInTime(
            final int layoutResource, final Viewport viewport, final Definitions definitions) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        return PatternRun.startInTime(scenario, layoutResource, viewport, definitions);
    }

    private PatternRun start(
            final int layoutResource,
            final Viewport viewport,
            final Definitions definitions,
            final int itemCount) {
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();
        return PatternRun.start(scenario, layoutResource, viewport, definitions, itemCount);
    }

    private static final class AddTwoGroupsToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class AddAnEmptyGroupToTheView implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> noItems = noItems();
            view.addGridPatternGroupDefinition(noItems);
        }
    }

    private static final class AddAnEmptyGroupToTheViewWithAdd implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> noItems = noItems();
            view.add(noItems);
        }
    }

    private static final class AddAnEmptyGroupToTheViewAfterAClear implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.clear();
            view.addGridPatternGroupDefinition(noItems);
        }
    }

    private static final class AddAnEmptyGroupToTheViewBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            view.addGridPatternGroupDefinition(twoOneByOnes);
            view.addGridPatternGroupDefinition(noItems);
            view.addGridPatternGroupDefinition(oneWideItem);
        }
    }

    private static final class OneGroupAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
        }
    }

    private static final class TwoGroupsAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
        }
    }

    private static final class AListOfOneGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class OneGroupAddedAfterAClear implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
            layoutManager.clearGridPatternGroupDefinition();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
        }
    }

    private static final class AListOfOneGroupSetAfterAnotherGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AnEmptyGroupAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
        }
    }

    private static final class AnEmptyGroupAddedAfterAClear implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.clearGridPatternGroupDefinition();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
        }
    }

    private static final class AListOfAnEmptyGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, noItems));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }

    private static final class AnEmptyListSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            layoutManager.setGridPatternGroupDefinitions(noGroups);
        }
    }

    private static final class AnEmptyListSetAfterAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> noGroups =
                    new ArrayList<GridPatternGroupDefinition>();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(noGroups);
        }
    }

    private static final class ANullListSetAfterAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(NULL_LIST);
        }
    }

    private static final class ANullListSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            layoutManager.setGridPatternGroupDefinitions(NULL_LIST);
        }
    }

    private static final class AnEmptyGroupAddedBetweenTwoGroups implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> oneWideItem = oneWideItem(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, oneWideItem));
        }
    }

    private static final class AnEmptyGroupAddedBeforeAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
        }
    }

    private static final class AnEmptyGroupAddedAfterAGroup implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternItemDefinition> noItems = noItems();
            layoutManager.addGridPatternGroupDefinition(group(isVertical, twoOneByOnes));
            layoutManager.addGridPatternGroupDefinition(group(isVertical, noItems));
        }
    }

    private static final class AListOfAnEmptyGroupAndAGroupSet implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> noItems = noItems();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final List<GridPatternGroupDefinition> groups =
                    new ArrayList<GridPatternGroupDefinition>();
            groups.add(group(isVertical, noItems));
            groups.add(group(isVertical, twoOneByOnes));
            layoutManager.setGridPatternGroupDefinitions(groups);
        }
    }
}
