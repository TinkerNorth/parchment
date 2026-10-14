// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.ADAPTER_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

import mobi.parchment.widget.adapterview.PatternFixture.Configuration;
import mobi.parchment.widget.adapterview.PatternFixture.Definitions;
import mobi.parchment.widget.adapterview.PatternFixture.LaidOutPattern;
import mobi.parchment.widget.adapterview.PatternFixture.PatternAndOracle;

public final class PatternJourney {

    private static final int NO_ITEMS = 0;
    private static final int OFF_SCREEN_SELECTION = 14;
    private static final int SCROLL_BACK_DISTANCE = 5;
    private static final int SHRUNK_ADAPTER_SIZE = 3;
    private static final int LAST_SHRUNK_POSITION = SHRUNK_ADAPTER_SIZE - 1;
    private static final int GROWN_ADAPTER_SIZE = 30;
    private static final float BACKWARD_DRAG_TRAVEL = 150f;
    private static final float FORWARD_DRAG_TRAVEL = -150f;
    private static final long LAYOUT_TIME_LIMIT_MILLISECONDS = 10000;
    private static final long DRAG_TIME_LIMIT_MILLISECONDS = 10000;

    private PatternJourney() {}

    public static void assertRunsLikeTheOracle(
            final Configuration configuration,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternAndOracle patterns =
                PatternAndOracle.laidOut(
                        configuration, definitions, oracleDefinitions, ADAPTER_SIZE);

        assertLaysOutLikeTheOracle(patterns);
        assertRunsOnLikeTheOracle(patterns);
    }

    public static void assertRunsLikeTheOracleFromAnEmptyAdapter(
            final Configuration configuration,
            final Definitions definitions,
            final Definitions oracleDefinitions) {
        final PatternAndOracle patterns =
                PatternAndOracle.laidOut(configuration, definitions, oracleDefinitions, NO_ITEMS);
        final LaidOutPattern emptyOracleLayout = patterns.oracleLayout();
        assertThat(emptyOracleLayout.childCount()).isEqualTo(NO_ITEMS);
        assertThat(patterns.patternLayout()).isEqualTo(emptyOracleLayout);

        patterns.setAdapterSize(ADAPTER_SIZE);

        assertLaysOutLikeTheOracle(patterns);
        assertRunsOnLikeTheOracle(patterns);
    }

    public static void assertRunsLikeTheOracleOnceLaidOutInTime(
            final Configuration configuration,
            final Definitions definitions,
            final Definitions oracleDefinitions)
            throws InterruptedException {
        final PatternAndOracle patterns =
                PatternAndOracle.laidOutWithin(
                        configuration,
                        definitions,
                        oracleDefinitions,
                        LAYOUT_TIME_LIMIT_MILLISECONDS);

        assertLaysOutLikeTheOracle(patterns);
        assertRunsOnLikeTheOracle(patterns);
    }

    public static void assertDragsForwardInTimeLikeTheOracle(
            final Configuration configuration,
            final Definitions definitions,
            final Definitions oracleDefinitions)
            throws InterruptedException {
        final PatternAndOracle patterns =
                PatternAndOracle.bothLaidOutWithin(
                        configuration,
                        definitions,
                        oracleDefinitions,
                        LAYOUT_TIME_LIMIT_MILLISECONDS);
        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        final LaidOutPattern patternLayout = patterns.patternLayout();

        patterns.dragWithin(FORWARD_DRAG_TRAVEL, DRAG_TIME_LIMIT_MILLISECONDS);

        final LaidOutPattern oracleDragged = patterns.oracleLayout();
        assertThat(oracleLayout.childCount()).isGreaterThan(NO_ITEMS);
        assertThat(patternLayout).isEqualTo(oracleLayout);
        assertThat(oracleDragged).isNotEqualTo(oracleLayout);
        assertThat(patterns.patternLayout()).isEqualTo(oracleDragged);
    }

    private static void assertLaysOutLikeTheOracle(final PatternAndOracle patterns) {
        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.childCount()).isGreaterThan(NO_ITEMS);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertRunsOnLikeTheOracle(final PatternAndOracle patterns) {
        assertSelectsOffScreenLikeTheOracle(patterns);
        assertScrollsLikeTheOracle(patterns);
        assertDragsLikeTheOracle(patterns);
        assertShrinksLikeTheOracle(patterns);
        assertGrowsLikeTheOracle(patterns);
    }

    private static void assertSelectsOffScreenLikeTheOracle(final PatternAndOracle patterns) {
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        assertThat(oracleBefore.isLaidOut(OFF_SCREEN_SELECTION)).isFalse();

        patterns.setSelection(OFF_SCREEN_SELECTION);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_SELECTION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertScrollsLikeTheOracle(final PatternAndOracle patterns) {
        final LaidOutPattern oracleBefore = patterns.oracleLayout();
        final int firstVisiblePosition = oracleBefore.firstVisiblePosition();
        final int scrollBackTarget = firstVisiblePosition - SCROLL_BACK_DISTANCE;
        assertThat(oracleBefore.isLaidOut(scrollBackTarget)).isFalse();

        patterns.smoothScrollToPosition(scrollBackTarget);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(scrollBackTarget)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertDragsLikeTheOracle(final PatternAndOracle patterns) {
        final LaidOutPattern oracleBefore = patterns.oracleLayout();

        patterns.drag(BACKWARD_DRAG_TRAVEL);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout).isNotEqualTo(oracleBefore);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertShrinksLikeTheOracle(final PatternAndOracle patterns) {
        patterns.setAdapterSize(SHRUNK_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout.isLaidOut(LAST_SHRUNK_POSITION)).isTrue();
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }

    private static void assertGrowsLikeTheOracle(final PatternAndOracle patterns) {
        final LaidOutPattern oracleBefore = patterns.oracleLayout();

        patterns.setAdapterSize(GROWN_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = patterns.oracleLayout();
        assertThat(oracleLayout).isNotEqualTo(oracleBefore);
        assertThat(patterns.patternLayout()).isEqualTo(oracleLayout);
    }
}
