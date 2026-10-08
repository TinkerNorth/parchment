// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.ADAPTER_SIZE;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CENTER_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_CIRCULAR;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_DEFAULT;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_END_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_ON_SCREEN_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.HORIZONTAL_START_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CENTER_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_DEFAULT;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_END_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_ON_SCREEN_SNAP;
import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_START_SNAP;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.PatternFixture.Configuration;
import mobi.parchment.widget.adapterview.PatternFixture.ItemSize;
import mobi.parchment.widget.adapterview.PatternFixture.LaidOutPattern;
import mobi.parchment.widget.adapterview.PatternFixture.PatternAndOracle;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class GridPatternLayoutManagerNoDefinitionTest {

    private static final int FIRST_LAYOUT_CELLS = 3;
    private static final int LIST_ITEM_BREADTH_IN_PIXELS = 400;
    private static final int LIST_ITEM_LENGTH_IN_PIXELS = 100;
    private static final int FIRST_CELL_INDEX = 0;
    private static final int FIRST_POSITION = 0;
    private static final int ON_SCREEN_POSITION = 1;
    private static final int OFF_SCREEN_POSITION = 10;
    private static final int LATE_POSITION = 15;
    private static final int POSITION_BEFORE_THE_LATE_POSITION = LATE_POSITION - 1;
    private static final int SIZE_ENDING_BEFORE_THE_LATE_POSITION = LATE_POSITION;
    private static final int LAST_POSITION = ADAPTER_SIZE - 1;
    private static final int SHRUNK_ADAPTER_SIZE = 3;
    private static final int LAST_SHRUNK_POSITION = SHRUNK_ADAPTER_SIZE - 1;
    private static final int GROWN_ADAPTER_SIZE = 30;
    private static final int NO_ITEMS = 0;
    private static final float FORWARD_FLING_VELOCITY = -1000f;
    private static final float BACKWARD_FLING_VELOCITY = 1000f;
    private static final float BACKWARD_DRAG_TRAVEL = 150f;
    private static final int ITEMS_IN_ONE_REPETITION = 3;
    private static final int NOTHING_SELECTED = LayoutManager.INVALID_POSITION;

    @Test
    public void noDefinition_vertical_firstLayout_listsFullBreadthItemsOneGridUnitLong() {
        final PatternFixture fallback = PatternFixture.withNoDefinition(VERTICAL_ON_SCREEN_SNAP);
        final List<ItemSize> listSizes = new ArrayList<ItemSize>();
        for (int position = FIRST_POSITION; position < FIRST_LAYOUT_CELLS; position++) {
            final ItemSize listSize =
                    new ItemSize(position, LIST_ITEM_BREADTH_IN_PIXELS, LIST_ITEM_LENGTH_IN_PIXELS);
            listSizes.add(listSize);
        }

        final List<ItemSize> laidOutSizes = fallback.laidOutSizes();

        assertThat(laidOutSizes).isEqualTo(listSizes);
    }

    @Test
    public void noDefinition_vertical_firstLayout_isTheLayoutOfOneOneByOneDefinition() {
        final PatternFixture fallback = PatternFixture.withNoDefinition(VERTICAL_START_SNAP);
        final PatternFixture oracle = PatternFixture.withOneOneByOneDefinition(VERTICAL_START_SNAP);

        final LaidOutPattern fallbackLayout = fallback.snapshot();
        final LaidOutPattern oracleLayout = oracle.snapshot();

        assertThat(oracleLayout.childCount()).isEqualTo(FIRST_LAYOUT_CELLS);
        assertThat(fallbackLayout).isEqualTo(oracleLayout);
    }

    @Test
    public void noDefinition_horizontal_firstLayout_isTheLayoutOfOneOneByOneDefinition() {
        final PatternFixture fallback = PatternFixture.withNoDefinition(HORIZONTAL_START_SNAP);
        final PatternFixture oracle =
                PatternFixture.withOneOneByOneDefinition(HORIZONTAL_START_SNAP);

        final LaidOutPattern fallbackLayout = fallback.snapshot();
        final LaidOutPattern oracleLayout = oracle.snapshot();

        assertThat(oracleLayout.childCount()).isEqualTo(FIRST_LAYOUT_CELLS);
        assertThat(fallbackLayout).isEqualTo(oracleLayout);
    }

    @Test
    public void noDefinition_vertical_everyDrawnCell_isBuiltFromTheOneHeldDefinition() {
        assertEveryDrawnCellIsBuiltFromOneDefinition(VERTICAL_START_SNAP);
    }

    @Test
    public void noDefinition_horizontal_everyDrawnCell_isBuiltFromTheOneHeldDefinition() {
        assertEveryDrawnCellIsBuiltFromOneDefinition(HORIZONTAL_START_SNAP);
    }

    @Test
    public void noDefinition_vertical_childMeasureSpecs_areTheSizesTheLayoutGivesTheItems() {
        final PatternFixture fallback = PatternFixture.withNoDefinition(VERTICAL_START_SNAP);

        final List<ItemSize> measuredSizes = fallback.childMeasureSpecSizes();

        assertThat(measuredSizes).isEqualTo(fallback.laidOutSizes());
    }

    @Test
    public void noDefinition_horizontal_childMeasureSpecs_areTheSizesTheLayoutGivesTheItems() {
        final PatternFixture fallback = PatternFixture.withNoDefinition(HORIZONTAL_START_SNAP);

        final List<ItemSize> measuredSizes = fallback.childMeasureSpecSizes();

        assertThat(measuredSizes).isEqualTo(fallback.laidOutSizes());
    }

    @Test
    public void twoGroups_vertical_childMeasureSpecs_areTheSizesTheLayoutGivesTheItems() {
        final PatternFixture pattern = PatternFixture.withTwoGroups(VERTICAL_START_SNAP);

        final List<ItemSize> measuredSizes = pattern.childMeasureSpecSizes();

        assertThat(pattern.laidOutSizes()).hasSizeGreaterThan(ITEMS_IN_ONE_REPETITION);
        assertThat(measuredSizes).isEqualTo(pattern.laidOutSizes());
    }

    @Test
    public void twoGroups_horizontal_childMeasureSpecs_areTheSizesTheLayoutGivesTheItems() {
        final PatternFixture pattern = PatternFixture.withTwoGroups(HORIZONTAL_START_SNAP);

        final List<ItemSize> measuredSizes = pattern.childMeasureSpecSizes();

        assertThat(pattern.laidOutSizes()).hasSizeGreaterThan(ITEMS_IN_ONE_REPETITION);
        assertThat(measuredSizes).isEqualTo(pattern.laidOutSizes());
    }

    @Test
    public void
            noDefinition_verticalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsOnTheStartLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalCenterSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(VERTICAL_CENTER_SNAP);
    }

    @Test
    public void
            noDefinition_verticalEndSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(VERTICAL_END_SNAP);
    }

    @Test
    public void
            noDefinition_verticalOnScreenSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(VERTICAL_ON_SCREEN_SNAP);
    }

    @Test
    public void
            noDefinition_verticalCircular_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(VERTICAL_CIRCULAR);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsOnTheStartLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalCenterSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(HORIZONTAL_CENTER_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalEndSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(HORIZONTAL_END_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalOnScreenSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(HORIZONTAL_ON_SCREEN_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalCircular_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenLandsLikeTheOracle(HORIZONTAL_CIRCULAR);
    }

    @Test
    public void
            clearedDefinitions_verticalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenAfterAClearLandsLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void
            clearedDefinitions_horizontalStartSnap_setSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOffScreenAfterAClearLandsLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalDefault_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(VERTICAL_DEFAULT);
    }

    @Test
    public void
            noDefinition_verticalStartSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalCenterSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(VERTICAL_CENTER_SNAP);
    }

    @Test
    public void
            noDefinition_verticalEndSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(VERTICAL_END_SNAP);
    }

    @Test
    public void
            noDefinition_verticalOnScreenSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(VERTICAL_ON_SCREEN_SNAP);
    }

    @Test
    public void
            noDefinition_verticalCircular_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkOfACircleToAnUndrawnEndRedrawsLikeTheOracle(VERTICAL_CIRCULAR);
    }

    @Test
    public void
            noDefinition_horizontalDefault_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(HORIZONTAL_DEFAULT);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalCenterSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(HORIZONTAL_CENTER_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalEndSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(HORIZONTAL_END_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalOnScreenSnap_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(HORIZONTAL_ON_SCREEN_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalCircular_dataSetShrinksBelowTheNearestDrawnPositionToAnUndrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkOfACircleToAnUndrawnEndRedrawsLikeTheOracle(HORIZONTAL_CIRCULAR);
    }

    @Test
    public void
            noDefinition_verticalCenterSnap_dataSetShrinksBelowTheNearestDrawnPositionToADrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToADrawnEndRedrawsLikeTheOracle(VERTICAL_CENTER_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalCenterSnap_dataSetShrinksBelowTheNearestDrawnPositionToADrawnEnd_redrawsLikeOneOneByOneDefinition() {
        assertAShrinkToADrawnEndRedrawsLikeTheOracle(HORIZONTAL_CENTER_SNAP);
    }

    @Test
    public void noDefinition_verticalDefault_dataSetEmptied_clearsLikeOneOneByOneDefinition() {
        assertEmptyingTheDataSetClearsLikeTheOracle(VERTICAL_DEFAULT);
    }

    @Test
    public void noDefinition_horizontalDefault_dataSetEmptied_clearsLikeOneOneByOneDefinition() {
        assertEmptyingTheDataSetClearsLikeTheOracle(HORIZONTAL_DEFAULT);
    }

    @Test
    public void noDefinition_verticalStartSnap_dataSetGrows_staysLikeOneOneByOneDefinition() {
        assertAGrowingDataSetStaysLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void noDefinition_horizontalStartSnap_dataSetGrows_staysLikeOneOneByOneDefinition() {
        assertAGrowingDataSetStaysLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalStartSnap_restoredAfterSetSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertARestoreAfterSetSelectionOffScreenLandsLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_restoredAfterSetSelectionOffScreen_landsWhereOneOneByOneDefinitionLands() {
        assertARestoreAfterSetSelectionOffScreenLandsLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalStartSnap_setSelectionOnScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOnScreenLandsLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_setSelectionOnScreen_landsWhereOneOneByOneDefinitionLands() {
        assertSetSelectionOnScreenLandsLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalDefault_setSelectionOffScreen_selectsNothingAndStaysLikeOneOneByOneDefinition() {
        assertSetSelectionOffScreenWithoutSnappingStaysLikeTheOracle(VERTICAL_DEFAULT);
    }

    @Test
    public void
            noDefinition_horizontalDefault_setSelectionOffScreen_selectsNothingAndStaysLikeOneOneByOneDefinition() {
        assertSetSelectionOffScreenWithoutSnappingStaysLikeTheOracle(HORIZONTAL_DEFAULT);
    }

    @Test
    public void
            noDefinition_verticalDefault_setSelectionOnScreen_selectsInPlaceLikeOneOneByOneDefinition() {
        assertSetSelectionOnScreenLandsLikeTheOracle(VERTICAL_DEFAULT);
    }

    @Test
    public void
            noDefinition_horizontalDefault_setSelectionOnScreen_selectsInPlaceLikeOneOneByOneDefinition() {
        assertSetSelectionOnScreenLandsLikeTheOracle(HORIZONTAL_DEFAULT);
    }

    @Test
    public void
            noDefinition_verticalStartSnap_smoothScrollToPosition_landsWhereOneOneByOneDefinitionLands() {
        assertASmoothScrollLandsLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_horizontalStartSnap_smoothScrollToPosition_landsWhereOneOneByOneDefinitionLands() {
        assertASmoothScrollLandsLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void noDefinition_verticalStartSnap_fling_restsWhereOneOneByOneDefinitionRests() {
        assertAFlingRestsLikeTheOracle(VERTICAL_START_SNAP);
    }

    @Test
    public void noDefinition_horizontalStartSnap_fling_restsWhereOneOneByOneDefinitionRests() {
        assertAFlingRestsLikeTheOracle(HORIZONTAL_START_SNAP);
    }

    @Test
    public void
            noDefinition_verticalCircular_flingBackAcrossTheWrap_restsWhereOneOneByOneDefinitionRests() {
        assertAFlingBackAcrossTheWrapRestsLikeTheOracle(VERTICAL_CIRCULAR);
    }

    @Test
    public void
            noDefinition_horizontalCircular_flingBackAcrossTheWrap_restsWhereOneOneByOneDefinitionRests() {
        assertAFlingBackAcrossTheWrapRestsLikeTheOracle(HORIZONTAL_CIRCULAR);
    }

    @Test
    public void
            noDefinition_verticalCircular_dragBackAcrossTheWrap_restsWhereOneOneByOneDefinitionRests() {
        assertADragBackAcrossTheWrapRestsLikeTheOracle(VERTICAL_CIRCULAR);
    }

    @Test
    public void
            noDefinition_horizontalCircular_dragBackAcrossTheWrap_restsWhereOneOneByOneDefinitionRests() {
        assertADragBackAcrossTheWrapRestsLikeTheOracle(HORIZONTAL_CIRCULAR);
    }

    private static void assertEveryDrawnCellIsBuiltFromOneDefinition(
            final Configuration configuration) {
        final PatternFixture fallback = PatternFixture.withNoDefinition(configuration);

        final List<GridPatternGroupDefinition> definitions = fallback.drawnCellDefinitions();

        assertThat(definitions).hasSize(FIRST_LAYOUT_CELLS);
        final GridPatternGroupDefinition firstCellDefinition = definitions.get(FIRST_CELL_INDEX);
        for (final GridPatternGroupDefinition definition : definitions) {
            assertThat(definition).isSameAs(firstCellDefinition);
        }
    }

    private static void assertSetSelectionOffScreenLandsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);

        patterns.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_POSITION)).isTrue();
        assertThat(oracleLayout.selectedPosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertSetSelectionOffScreenLandsOnTheStartLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);

        patterns.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.firstVisiblePosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(oracleLayout.selectedPosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertSetSelectionOffScreenAfterAClearLandsLikeTheOracle(
            final Configuration configuration) {
        final PatternFixture cleared = PatternFixture.withClearedDefinitions(configuration);
        final PatternFixture oracle = PatternFixture.withOneOneByOneDefinition(configuration);
        assertThat(cleared.snapshot()).isEqualTo(oracle.snapshot());

        oracle.setSelection(OFF_SCREEN_POSITION);
        cleared.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = oracle.snapshot();
        assertThat(oracleLayout.selectedPosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(cleared.snapshot()).isEqualTo(oracleLayout);
    }

    private static void assertSetSelectionOnScreenLandsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        assertThat(patterns.oracleLayout().isLaidOut(ON_SCREEN_POSITION)).isTrue();

        patterns.setSelection(ON_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.selectedPosition()).isEqualTo(ON_SCREEN_POSITION);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertSetSelectionOffScreenWithoutSnappingStaysLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();

        patterns.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout).isEqualTo(oracleBefore);
        assertThat(oracleLayout.selectedPosition()).isEqualTo(NOTHING_SELECTED);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.firstVisiblePosition()).isGreaterThan(LAST_SHRUNK_POSITION);
        assertThat(oracleBefore.isLaidOut(LAST_SHRUNK_POSITION)).isFalse();

        patterns.setAdapterSize(SHRUNK_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.lastVisiblePosition()).isEqualTo(LAST_SHRUNK_POSITION);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAShrinkOfACircleToAnUndrawnEndRedrawsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.firstVisiblePosition()).isGreaterThan(LAST_SHRUNK_POSITION);
        assertThat(oracleBefore.isLaidOut(LAST_SHRUNK_POSITION)).isFalse();

        patterns.setAdapterSize(SHRUNK_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_SHRUNK_POSITION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAShrinkToADrawnEndRedrawsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.selectedPosition()).isEqualTo(LATE_POSITION);
        assertThat(oracleBefore.isLaidOut(POSITION_BEFORE_THE_LATE_POSITION)).isTrue();

        patterns.setAdapterSize(SIZE_ENDING_BEFORE_THE_LATE_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.lastVisiblePosition()).isEqualTo(POSITION_BEFORE_THE_LATE_POSITION);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertEmptyingTheDataSetClearsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);

        patterns.setAdapterSize(NO_ITEMS);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.childCount()).isEqualTo(NO_ITEMS);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAGrowingDataSetStaysLikeTheOracle(final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);

        patterns.setAdapterSize(GROWN_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LATE_POSITION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertARestoreAfterSetSelectionOffScreenLandsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        patterns.setSelection(OFF_SCREEN_POSITION);

        final PatternAndOracle restored = patterns.restoredIntoNewPatterns();

        final LaidOutPattern oracleLayout = restored.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_POSITION)).isTrue();
        assertThat(restored.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertASmoothScrollLandsLikeTheOracle(final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);

        patterns.smoothScrollToPosition(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_POSITION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAFlingRestsLikeTheOracle(final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);

        patterns.fling(FORWARD_FLING_VELOCITY);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.firstVisiblePosition()).isGreaterThan(FIRST_POSITION);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAFlingBackAcrossTheWrapRestsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);

        patterns.fling(BACKWARD_FLING_VELOCITY);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_POSITION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertADragBackAcrossTheWrapRestsLikeTheOracle(
            final Configuration configuration) {
        final PatternAndOracle patterns = PatternAndOracle.laidOutWithNoDefinition(configuration);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.isLaidOut(LAST_POSITION)).isFalse();

        patterns.drag(BACKWARD_DRAG_TRAVEL);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_POSITION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }
}
