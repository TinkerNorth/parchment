// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.os.Parcelable;
import android.view.AbsSavedState;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystemClock;

@RunWith(RobolectricTestRunner.class)
public class GridPatternLayoutManagerNoDefinitionTest {

    public static final int VIEW_GROUP_HEIGHT = 300;
    public static final int VIEW_GROUP_WIDTH = 145;
    public static final int VIEW_SIZE = 145;
    public static final int CELL_SPACING = 10;
    private static final int PATTERN_BREADTH = 400;
    private static final int PATTERN_SIZE = 300;
    private static final float PATTERN_RATIO = 0.25f;
    private static final int ADAPTER_SIZE = 20;
    private static final int FIRST_LAYOUT_CELLS = 3;
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
    private static final float GESTURE_X = 150f;
    private static final float GESTURE_Y = 150f;
    private static final float GESTURE_TRAVEL = 45f;
    private static final long GESTURE_START = 0;
    private static final long GESTURE_END = 10;
    private static final int NO_META_STATE = 0;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final int ITEMS_IN_ONE_REPETITION = 3;
    private static final int REST_FRAME_LIMIT = 500;
    private static final long ONE_FRAME = 16;
    private static final int NOTHING_SELECTED = LayoutManager.INVALID_POSITION;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final boolean SNAPS = true;
    private static final boolean DOES_NOT_SNAP = false;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final int VIEWPORT_PAGING = 0;
    private static final boolean SCROLL_PAST_CONTENT = false;
    private static final boolean SELECT_ON_SNAP = true;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final Configuration VERTICAL_DEFAULT =
            new Configuration(VERTICAL, SnapPosition.onScreen, DOES_NOT_SNAP, NOT_CIRCULAR);
    private static final Configuration VERTICAL_START_SNAP =
            new Configuration(VERTICAL, SnapPosition.start, SNAPS, NOT_CIRCULAR);
    private static final Configuration VERTICAL_CENTER_SNAP =
            new Configuration(VERTICAL, SnapPosition.center, SNAPS, NOT_CIRCULAR);
    private static final Configuration VERTICAL_END_SNAP =
            new Configuration(VERTICAL, SnapPosition.end, SNAPS, NOT_CIRCULAR);
    private static final Configuration VERTICAL_ON_SCREEN_SNAP =
            new Configuration(VERTICAL, SnapPosition.onScreen, SNAPS, NOT_CIRCULAR);
    private static final Configuration VERTICAL_CIRCULAR =
            new Configuration(VERTICAL, SnapPosition.center, SNAPS, CIRCULAR);
    private static final Configuration HORIZONTAL_DEFAULT =
            new Configuration(HORIZONTAL, SnapPosition.onScreen, DOES_NOT_SNAP, NOT_CIRCULAR);
    private static final Configuration HORIZONTAL_START_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.start, SNAPS, NOT_CIRCULAR);
    private static final Configuration HORIZONTAL_CENTER_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.center, SNAPS, NOT_CIRCULAR);
    private static final Configuration HORIZONTAL_END_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.end, SNAPS, NOT_CIRCULAR);
    private static final Configuration HORIZONTAL_ON_SCREEN_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.onScreen, SNAPS, NOT_CIRCULAR);
    private static final Configuration HORIZONTAL_CIRCULAR =
            new Configuration(HORIZONTAL, SnapPosition.center, SNAPS, CIRCULAR);
    final MyViewGroup mViewGroup = new MyViewGroup(ApplicationProvider.getApplicationContext());
    final AdapterViewManager adapterViewManager = new AdapterViewManager();
    TestAdapter mTestAdapter;
    GridPatternLayoutManagerAttributes attributes;
    GridPatternLayoutManager listLayoutManager;

    @Before
    public void setup() {
        attributes =
                new GridPatternLayoutManagerAttributes(
                        false,
                        true,
                        false,
                        0,
                        SnapPosition.onScreen,
                        false,
                        CELL_SPACING,
                        true,
                        true,
                        true,
                        1f);
        listLayoutManager =
                new GridPatternLayoutManager(mViewGroup, null, adapterViewManager, attributes);
        mTestAdapter = new TestAdapter(VIEW_SIZE);
        adapterViewManager.setAdapter(mTestAdapter);
        doFirstLayout();
    }

    @Test
    public void shouldDefaultToNormalListView() {
        mTestAdapter.setAdapterSize(3);
        doLayout();

        final View firstView = mViewGroup.mViews.get(0);
        final View secondView = mViewGroup.mViews.get(1);

        assertThat(firstView.getWidth()).isEqualTo(145);
        assertThat(firstView.getHeight()).isEqualTo(145);

        assertThat(secondView.getWidth()).isEqualTo(145);
        assertThat(secondView.getHeight()).isEqualTo(145);

        assertThat(mViewGroup.mViews.size()).isEqualTo(2);
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
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);

        patterns.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_POSITION)).isTrue();
        assertThat(oracleLayout.selectedPosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertSetSelectionOffScreenLandsOnTheStartLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);

        patterns.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.firstVisiblePosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(oracleLayout.selectedPosition()).isEqualTo(OFF_SCREEN_POSITION);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
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
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        assertThat(patterns.oracleLayout().isLaidOut(ON_SCREEN_POSITION)).isTrue();

        patterns.setSelection(ON_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.selectedPosition()).isEqualTo(ON_SCREEN_POSITION);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertSetSelectionOffScreenWithoutSnappingStaysLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();

        patterns.setSelection(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout).isEqualTo(oracleBefore);
        assertThat(oracleLayout.selectedPosition()).isEqualTo(NOTHING_SELECTED);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAShrinkToAnUndrawnEndRedrawsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.firstVisiblePosition()).isGreaterThan(LAST_SHRUNK_POSITION);
        assertThat(oracleBefore.isLaidOut(LAST_SHRUNK_POSITION)).isFalse();

        patterns.setAdapterSize(SHRUNK_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.lastVisiblePosition()).isEqualTo(LAST_SHRUNK_POSITION);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAShrinkOfACircleToAnUndrawnEndRedrawsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.firstVisiblePosition()).isGreaterThan(LAST_SHRUNK_POSITION);
        assertThat(oracleBefore.isLaidOut(LAST_SHRUNK_POSITION)).isFalse();

        patterns.setAdapterSize(SHRUNK_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_SHRUNK_POSITION)).isTrue();
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAShrinkToADrawnEndRedrawsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.selectedPosition()).isEqualTo(LATE_POSITION);
        assertThat(oracleBefore.isLaidOut(POSITION_BEFORE_THE_LATE_POSITION)).isTrue();

        patterns.setAdapterSize(SIZE_ENDING_BEFORE_THE_LATE_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.lastVisiblePosition()).isEqualTo(POSITION_BEFORE_THE_LATE_POSITION);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertEmptyingTheDataSetClearsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);

        patterns.setAdapterSize(NO_ITEMS);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.childCount()).isEqualTo(NO_ITEMS);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAGrowingDataSetStaysLikeTheOracle(final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        patterns.smoothScrollToPosition(LATE_POSITION);

        patterns.setAdapterSize(GROWN_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LATE_POSITION)).isTrue();
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertARestoreAfterSetSelectionOffScreenLandsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        patterns.setSelection(OFF_SCREEN_POSITION);

        final FallbackAndOracle restored = patterns.restoredIntoNewPatterns();

        final LaidOutPattern oracleLayout = restored.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_POSITION)).isTrue();
        assertThat(restored.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertASmoothScrollLandsLikeTheOracle(final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);

        patterns.smoothScrollToPosition(OFF_SCREEN_POSITION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_POSITION)).isTrue();
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAFlingRestsLikeTheOracle(final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);

        patterns.fling(FORWARD_FLING_VELOCITY);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.firstVisiblePosition()).isGreaterThan(FIRST_POSITION);
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertAFlingBackAcrossTheWrapRestsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);

        patterns.fling(BACKWARD_FLING_VELOCITY);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_POSITION)).isTrue();
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private static void assertADragBackAcrossTheWrapRestsLikeTheOracle(
            final Configuration configuration) {
        final FallbackAndOracle patterns = FallbackAndOracle.laidOut(configuration);
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.isLaidOut(LAST_POSITION)).isFalse();

        patterns.drag(BACKWARD_DRAG_TRAVEL);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_POSITION)).isTrue();
        assertThat(patterns.fallbackLayout()).isEqualTo(oracleLayout);
    }

    private void doLayout() {
        doLayout(new Animation());
    }

    private void doLayout(Animation animation) {
        listLayoutManager.layout(mViewGroup, animation, 0, 0, VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);
    }

    private void doFirstLayout() {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_HEIGHT, View.MeasureSpec.EXACTLY);
        final int measureSpec2 =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_WIDTH, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec2, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_WIDTH, VIEW_GROUP_HEIGHT);
    }

    public class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();

        public MyViewGroup(Context context) {
            super(context);
        }

        public View forPosition(int position) {
            Collections.sort(
                    mViews,
                    new Comparator<View>() {
                        @Override
                        public int compare(View lhs, View rhs) {
                            return lhs.getLeft() - rhs.getLeft();
                        }
                    });

            return mViews.get(position);
        }

        @Override
        public boolean addViewInAdapterView(
                View view, int index, ViewGroup.LayoutParams layoutParams) {
            mViews.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(View view) {
            mViews.remove(view);
        }
    }

    public class TestAdapter extends BaseAdapter {
        private int mAdapterSize;
        private int mViewSize;

        public TestAdapter(int viewSize) {
            mViewSize = viewSize;
        }

        public void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAdapterSize;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FrameLayout outer = new FrameLayout(ApplicationProvider.getApplicationContext());
            outer.setTag(position);
            outer.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));

            // TODO: necessary to have an outer and an inner?
            final FrameLayout inner = new FrameLayout(ApplicationProvider.getApplicationContext());
            inner.setLayoutParams(new ViewGroup.LayoutParams(mViewSize, mViewSize));
            outer.addView(inner);
            return outer;
        }
    }

    private static final class Configuration {
        private final boolean mIsVertical;
        private final SnapPosition mSnapPosition;
        private final boolean mSnapToPosition;
        private final boolean mIsCircularScroll;

        private Configuration(
                final boolean isVertical,
                final SnapPosition snapPosition,
                final boolean snapToPosition,
                final boolean isCircularScroll) {
            mIsVertical = isVertical;
            mSnapPosition = snapPosition;
            mSnapToPosition = snapToPosition;
            mIsCircularScroll = isCircularScroll;
        }

        private GridPatternLayoutManagerAttributes attributes() {
            return new GridPatternLayoutManagerAttributes(
                    mIsCircularScroll,
                    mSnapToPosition,
                    NOT_VIEW_PAGER,
                    VIEWPORT_PAGING,
                    mSnapPosition,
                    SCROLL_PAST_CONTENT,
                    CELL_SPACING,
                    SELECT_ON_SNAP,
                    NO_SELECT_WHILE_SCROLLING,
                    mIsVertical,
                    PATTERN_RATIO);
        }

        private int width() {
            if (mIsVertical) return PATTERN_BREADTH;
            return PATTERN_SIZE;
        }

        private int height() {
            if (mIsVertical) return PATTERN_SIZE;
            return PATTERN_BREADTH;
        }
    }

    private static final class PatternFixture {
        private final Configuration mConfiguration;
        private final PatternViewGroup mViewGroup;
        private final ResizableAdapter mAdapter;
        private final RecordingSelectedListener mSelections;
        private final GridPatternLayoutManager mLayoutManager;
        private final AdapterAnimator mAdapterAnimator;

        private PatternFixture(final Configuration configuration) {
            final Context context = ApplicationProvider.getApplicationContext();
            final AdapterViewManager adapterViewManager = new AdapterViewManager();
            final GridPatternLayoutManagerAttributes attributes = configuration.attributes();
            mConfiguration = configuration;
            mViewGroup = new PatternViewGroup(context);
            mAdapter = new ResizableAdapter(context, ADAPTER_SIZE);
            mSelections = new RecordingSelectedListener();
            mLayoutManager =
                    new GridPatternLayoutManager(
                            mViewGroup, mSelections, adapterViewManager, attributes);
            final LayoutManagerBridge layoutManagerBridge = new LayoutManagerBridge(mLayoutManager);
            mAdapterAnimator =
                    new AdapterAnimator(
                            mViewGroup,
                            new UnusedFrameScheduler(),
                            NOT_VIEW_PAGER,
                            configuration.mIsVertical,
                            layoutManagerBridge,
                            ViewConfiguration.get(context),
                            new ScrollListenerDispatcher());
            adapterViewManager.setAdapter(mAdapter);
        }

        private static PatternFixture withNoDefinition(final Configuration configuration) {
            final PatternFixture fixture = new PatternFixture(configuration);
            fixture.layOut();
            return fixture;
        }

        private static PatternFixture withOneOneByOneDefinition(final Configuration configuration) {
            final PatternFixture fixture = new PatternFixture(configuration);
            fixture.addOneOneByOneDefinition();
            fixture.layOut();
            return fixture;
        }

        private static PatternFixture withTwoGroups(final Configuration configuration) {
            final PatternFixture fixture = new PatternFixture(configuration);
            fixture.addOneTwoByTwoGroup();
            fixture.addTwoOneByOnesGroup();
            fixture.layOut();
            return fixture;
        }

        private static PatternFixture withClearedDefinitions(final Configuration configuration) {
            final PatternFixture fixture = new PatternFixture(configuration);
            fixture.addOneTwoByTwoGroup();
            fixture.addTwoOneByOnesGroup();
            fixture.mLayoutManager.clearGridPatternGroupDefinition();
            fixture.layOut();
            return fixture;
        }

        private static PatternFixture restoredWithNoDefinition(
                final Configuration configuration, final Parcelable state) {
            final PatternFixture fixture = new PatternFixture(configuration);
            fixture.mLayoutManager.onRestoreInstanceState(state);
            fixture.layOut();
            return fixture;
        }

        private static PatternFixture restoredWithOneOneByOneDefinition(
                final Configuration configuration, final Parcelable state) {
            final PatternFixture fixture = new PatternFixture(configuration);
            fixture.addOneOneByOneDefinition();
            fixture.mLayoutManager.onRestoreInstanceState(state);
            fixture.layOut();
            return fixture;
        }

        private void addOneOneByOneDefinition() {
            final GridPatternItemDefinition oneByOne =
                    new GridPatternItemDefinition(
                            FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(oneByOne);
            addGroup(items);
        }

        private void addOneTwoByTwoGroup() {
            final GridPatternItemDefinition twoByTwo =
                    new GridPatternItemDefinition(
                            FIRST_ROW, FIRST_COLUMN, TWO_GRID_UNITS, TWO_GRID_UNITS);
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(twoByTwo);
            addGroup(items);
        }

        private void addTwoOneByOnesGroup() {
            final GridPatternItemDefinition first =
                    new GridPatternItemDefinition(
                            FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
            final GridPatternItemDefinition second = secondOneByOneAcrossTheBreadth();
            final List<GridPatternItemDefinition> items =
                    new ArrayList<GridPatternItemDefinition>();
            items.add(first);
            items.add(second);
            addGroup(items);
        }

        private GridPatternItemDefinition secondOneByOneAcrossTheBreadth() {
            if (mConfiguration.mIsVertical) {
                return new GridPatternItemDefinition(
                        FIRST_ROW, SECOND_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
            }
            return new GridPatternItemDefinition(
                    SECOND_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        }

        private void addGroup(final List<GridPatternItemDefinition> items) {
            final GridPatternGroupDefinition definition =
                    new GridPatternGroupDefinition(mConfiguration.mIsVertical, items);
            mLayoutManager.addGridPatternGroupDefinition(definition);
        }

        private void setSelection(final int position) {
            mLayoutManager.setSelected(position, mViewGroup);
            runToRest();
        }

        private void setAdapterSize(final int adapterSize) {
            mAdapter.setCount(adapterSize);
            runToRest();
        }

        private void smoothScrollToPosition(final int position) {
            mAdapterAnimator.smoothScrollToPosition(position);
            runToRest();
        }

        private void fling(final float velocity) {
            final MotionEvent down =
                    MotionEvent.obtain(
                            GESTURE_START,
                            GESTURE_START,
                            MotionEvent.ACTION_DOWN,
                            GESTURE_X,
                            GESTURE_Y,
                            NO_META_STATE);
            final MotionEvent move =
                    MotionEvent.obtain(
                            GESTURE_START,
                            GESTURE_END,
                            MotionEvent.ACTION_MOVE,
                            GESTURE_X - GESTURE_TRAVEL,
                            GESTURE_Y - GESTURE_TRAVEL,
                            NO_META_STATE);
            mAdapterAnimator.onDown(down);
            mAdapterAnimator.onFling(down, move, velocity, velocity);
            mAdapterAnimator.onUp();
            runToRest();
        }

        private void drag(final float travel) {
            final MotionEvent down =
                    MotionEvent.obtain(
                            GESTURE_START,
                            GESTURE_START,
                            MotionEvent.ACTION_DOWN,
                            GESTURE_X,
                            GESTURE_Y,
                            NO_META_STATE);
            final MotionEvent move =
                    MotionEvent.obtain(
                            GESTURE_START,
                            GESTURE_END,
                            MotionEvent.ACTION_MOVE,
                            GESTURE_X + travel,
                            GESTURE_Y + travel,
                            NO_META_STATE);
            mAdapterAnimator.onDown(down);
            mAdapterAnimator.onScroll(down, move, -travel, -travel);
            frame();
            mAdapterAnimator.onUp();
            runToRest();
        }

        private Parcelable saveState() {
            return mLayoutManager.onSaveInstanceState(AbsSavedState.EMPTY_STATE);
        }

        private List<GridPatternGroupDefinition> drawnCellDefinitions() {
            final List<GridPatternGroupDefinition> definitions =
                    new ArrayList<GridPatternGroupDefinition>();
            for (final GridPatternGroup cell : mLayoutManager.mCells) {
                final GridPatternGroupDefinition definition = cell.getGridPatternGroupDefinition();
                definitions.add(definition);
            }
            return definitions;
        }

        private List<ItemSize> childMeasureSpecSizes() {
            final List<ItemSize> sizes = new ArrayList<ItemSize>();
            for (final View view : mViewGroup.mViews) {
                final int position = mLayoutManager.getPosition(view);
                final int widthMeasureSpec = mLayoutManager.getChildWidthMeasureSpec(position);
                final int heightMeasureSpec = mLayoutManager.getChildHeightMeasureSpec(position);
                final int width = View.MeasureSpec.getSize(widthMeasureSpec);
                final int height = View.MeasureSpec.getSize(heightMeasureSpec);
                final ItemSize size = new ItemSize(position, width, height);
                sizes.add(size);
            }
            return sizes;
        }

        private List<ItemSize> laidOutSizes() {
            final List<ItemSize> sizes = new ArrayList<ItemSize>();
            for (final View view : mViewGroup.mViews) {
                final int position = mLayoutManager.getPosition(view);
                final ItemSize size = new ItemSize(position, view.getWidth(), view.getHeight());
                sizes.add(size);
            }
            return sizes;
        }

        private void layOut() {
            final int width = mConfiguration.width();
            final int height = mConfiguration.height();
            final int widthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            final int heightMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            mViewGroup.measure(widthMeasureSpec, heightMeasureSpec);
            mViewGroup.layout(0, 0, width, height);
            runToRest();
        }

        private void runToRest() {
            frame();
            for (int frame = 0; frame < REST_FRAME_LIMIT; frame++) {
                if (isAtRest()) return;
                ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
                frame();
            }
            throw new AssertionError("the pattern never came to rest: " + snapshot());
        }

        private void frame() {
            final int width = mConfiguration.width();
            final int height = mConfiguration.height();
            mAdapterAnimator.computeScrollOffset();
            final Animation animation = mAdapterAnimator.getAnimation();
            mLayoutManager.layout(mViewGroup, animation, 0, 0, width, height);
            mAdapterAnimator.onFrameLaidOut();
            reportTheSelectionAtRest();
        }

        private void reportTheSelectionAtRest() {
            if (!isAtRest()) return;
            mLayoutManager.reportTheSelectionAtRest();
        }

        private boolean isAtRest() {
            final AdapterAnimator.State state = mAdapterAnimator.getState();
            return state == AdapterAnimator.State.notMoving;
        }

        private LaidOutPattern snapshot() {
            final List<LaidOutChild> children = new ArrayList<LaidOutChild>();
            for (final View view : mViewGroup.mViews) {
                final int position = mLayoutManager.getPosition(view);
                final LaidOutChild child =
                        new LaidOutChild(
                                position,
                                view.getLeft(),
                                view.getTop(),
                                view.getRight(),
                                view.getBottom());
                children.add(child);
            }
            final int selectedPosition = mLayoutManager.getSelectedPosition();
            final List<Integer> selections = mSelections.positions();
            final int firstVisiblePosition = mLayoutManager.getFirstVisibleAdapterPosition();
            final int lastVisiblePosition = mLayoutManager.getLastVisibleAdapterPosition();
            return new LaidOutPattern(
                    children,
                    selectedPosition,
                    selections,
                    firstVisiblePosition,
                    lastVisiblePosition);
        }
    }

    private static final class FallbackAndOracle {
        private final Configuration mConfiguration;
        private final PatternFixture mFallback;
        private final PatternFixture mOracle;

        private FallbackAndOracle(
                final Configuration configuration,
                final PatternFixture fallback,
                final PatternFixture oracle) {
            mConfiguration = configuration;
            mFallback = fallback;
            mOracle = oracle;
        }

        private static FallbackAndOracle laidOut(final Configuration configuration) {
            final PatternFixture oracle = PatternFixture.withOneOneByOneDefinition(configuration);
            final PatternFixture fallback = PatternFixture.withNoDefinition(configuration);
            return new FallbackAndOracle(configuration, fallback, oracle);
        }

        private FallbackAndOracle restoredIntoNewPatterns() {
            final Parcelable oracleState = mOracle.saveState();
            final Parcelable fallbackState = mFallback.saveState();
            final PatternFixture oracle =
                    PatternFixture.restoredWithOneOneByOneDefinition(mConfiguration, oracleState);
            final PatternFixture fallback =
                    PatternFixture.restoredWithNoDefinition(mConfiguration, fallbackState);
            return new FallbackAndOracle(mConfiguration, fallback, oracle);
        }

        private void setSelection(final int position) {
            mOracle.setSelection(position);
            mFallback.setSelection(position);
        }

        private void setAdapterSize(final int adapterSize) {
            mOracle.setAdapterSize(adapterSize);
            mFallback.setAdapterSize(adapterSize);
        }

        private void smoothScrollToPosition(final int position) {
            mOracle.smoothScrollToPosition(position);
            mFallback.smoothScrollToPosition(position);
        }

        private void fling(final float velocity) {
            mOracle.fling(velocity);
            mFallback.fling(velocity);
        }

        private void drag(final float travel) {
            mOracle.drag(travel);
            mFallback.drag(travel);
        }

        private LaidOutPattern fallbackLayout() {
            return mFallback.snapshot();
        }

        private LaidOutPattern oracleLayout() {
            return mOracle.snapshot();
        }
    }

    private static final class ItemSize {
        private final int mPosition;
        private final int mWidth;
        private final int mHeight;

        private ItemSize(final int position, final int width, final int height) {
            mPosition = position;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof ItemSize)) return false;
            final ItemSize size = (ItemSize) other;
            final boolean samePosition = mPosition == size.mPosition;
            final boolean sameWidth = mWidth == size.mWidth;
            final boolean sameHeight = mHeight == size.mHeight;
            return samePosition && sameWidth && sameHeight;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mPosition, mWidth, mHeight);
        }

        @Override
        public String toString() {
            return "position " + mPosition + " " + mWidth + "x" + mHeight;
        }
    }

    private static final class LaidOutChild {
        private final int mPosition;
        private final int mLeft;
        private final int mTop;
        private final int mRight;
        private final int mBottom;

        private LaidOutChild(
                final int position,
                final int left,
                final int top,
                final int right,
                final int bottom) {
            mPosition = position;
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof LaidOutChild)) return false;
            final LaidOutChild child = (LaidOutChild) other;
            final boolean samePosition = mPosition == child.mPosition;
            final boolean sameStart = mLeft == child.mLeft && mTop == child.mTop;
            final boolean sameEnd = mRight == child.mRight && mBottom == child.mBottom;
            return samePosition && sameStart && sameEnd;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mPosition, mLeft, mTop, mRight, mBottom);
        }

        @Override
        public String toString() {
            return "position "
                    + mPosition
                    + " at ["
                    + mLeft
                    + ", "
                    + mTop
                    + ", "
                    + mRight
                    + ", "
                    + mBottom
                    + "]";
        }
    }

    private static final class LaidOutPattern {
        private final List<LaidOutChild> mChildren;
        private final int mSelectedPosition;
        private final List<Integer> mSelections;
        private final int mFirstVisiblePosition;
        private final int mLastVisiblePosition;

        private LaidOutPattern(
                final List<LaidOutChild> children,
                final int selectedPosition,
                final List<Integer> selections,
                final int firstVisiblePosition,
                final int lastVisiblePosition) {
            mChildren = children;
            mSelectedPosition = selectedPosition;
            mSelections = selections;
            mFirstVisiblePosition = firstVisiblePosition;
            mLastVisiblePosition = lastVisiblePosition;
        }

        private int childCount() {
            return mChildren.size();
        }

        private boolean isLaidOut(final int position) {
            for (final LaidOutChild child : mChildren) {
                final boolean holdsThePosition = child.mPosition == position;
                if (holdsThePosition) return true;
            }
            return false;
        }

        private int selectedPosition() {
            return mSelectedPosition;
        }

        private int firstVisiblePosition() {
            return mFirstVisiblePosition;
        }

        private int lastVisiblePosition() {
            return mLastVisiblePosition;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof LaidOutPattern)) return false;
            final LaidOutPattern pattern = (LaidOutPattern) other;
            final boolean sameChildren = mChildren.equals(pattern.mChildren);
            final boolean sameSelection = mSelectedPosition == pattern.mSelectedPosition;
            final boolean sameSelections = mSelections.equals(pattern.mSelections);
            final boolean sameFirst = mFirstVisiblePosition == pattern.mFirstVisiblePosition;
            final boolean sameLast = mLastVisiblePosition == pattern.mLastVisiblePosition;
            return sameChildren && sameSelection && sameSelections && sameFirst && sameLast;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    mChildren,
                    mSelectedPosition,
                    mSelections,
                    mFirstVisiblePosition,
                    mLastVisiblePosition);
        }

        @Override
        public String toString() {
            return "children "
                    + mChildren
                    + ", selected "
                    + mSelectedPosition
                    + ", selections reported "
                    + mSelections
                    + ", first visible "
                    + mFirstVisiblePosition
                    + ", last visible "
                    + mLastVisiblePosition;
        }
    }

    private static final class PatternViewGroup extends LinearLayout implements AdapterViewHandler {
        private final List<View> mViews = new ArrayList<View>();

        private PatternViewGroup(final Context context) {
            super(context);
        }

        @Override
        public boolean addViewInAdapterView(
                final View view, final int index, final ViewGroup.LayoutParams layoutParams) {
            mViews.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(final View view) {
            mViews.remove(view);
        }
    }

    private static final class RecordingSelectedListener implements OnSelectedListener {
        private final List<Integer> mPositions = new ArrayList<Integer>();

        @Override
        public void onSelected(final View view) {
            final Integer position = positionOf(view);
            mPositions.add(position);
        }

        private static Integer positionOf(final View view) {
            if (view == null) return NOTHING_SELECTED;
            return (Integer) view.getTag();
        }

        private List<Integer> positions() {
            return new ArrayList<Integer>(mPositions);
        }
    }

    private static final class UnusedFrameScheduler implements AnimationFrameScheduler {
        @Override
        public void requestAnimationFrame() {}
    }

    private static final class ResizableAdapter extends BaseAdapter {
        private final Context mContext;
        private int mCount;

        private ResizableAdapter(final Context context, final int count) {
            mContext = context;
            mCount = count;
        }

        private void setCount(final int count) {
            mCount = count;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(final int position) {
            return position;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final FrameLayout view = new FrameLayout(mContext);
            final ViewGroup.LayoutParams layoutParams =
                    new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            view.setTag(position);
            return view;
        }
    }
}
