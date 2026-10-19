// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.VERTICAL_CIRCULAR_WITHOUT_SPACING;
import static mobi.parchment.widget.adapterview.PatternFixture.twoOneByOnes;
import static mobi.parchment.widget.adapterview.PatternJourney.assertRunsLikeTheOracleOnceLaidOutInTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import mobi.parchment.widget.adapterview.PatternFixture.Definitions;
import mobi.parchment.widget.adapterview.PatternFixture.NoGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class PatternJourneyTest {

    private static final String THE_ORACLES_FIRST_LAYOUT_STOPPED_AT_THE_TIME_LIMIT =
            "the oracle's first layout was still running after 10000 ms";

    @Test
    public void
            laidOutInTime_anOracleWhoseFirstLayoutNeverEnds_failsAtTheTimeLimitNamingTheOracle() {
        final ThrowingCallable journey = new RunWithAnOracleWhoseFirstLayoutNeverEnds();

        final Throwable failure = catchThrowable(journey);

        assertThat(failure).isInstanceOf(AssertionError.class);
        assertThat(failure).hasMessage(THE_ORACLES_FIRST_LAYOUT_STOPPED_AT_THE_TIME_LIMIT);
    }

    private static final class RunWithAnOracleWhoseFirstLayoutNeverEnds
            implements ThrowingCallable {
        @Override
        public void call() throws InterruptedException {
            assertRunsLikeTheOracleOnceLaidOutInTime(
                    VERTICAL_CIRCULAR_WITHOUT_SPACING,
                    new NoGroup(),
                    new AGroupWhoseFillNeverEndsAdded());
        }
    }

    private static final class AGroupWhoseFillNeverEndsAdded implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final boolean isVertical = pattern.isVertical();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            pattern.addGroup(twoOneByOnes);
            pattern.holdTheFillFromNowOn();
        }
    }
}
