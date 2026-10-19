// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.harness.PatternGroups.group;
import static mobi.parchment.harness.PatternItems.twoOneByOnes;
import static mobi.parchment.harness.PatternRun.VERTICAL_VIEWPORT;
import static mobi.parchment.harness.PatternRun.assertRunsLikeTheOracleOnceLaidOutInTime;
import static mobi.parchment.harness.ReachableGridPatternView.layoutManagerOf;
import static org.junit.Assert.assertTrue;

import android.widget.BaseAdapter;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import mobi.parchment.harness.Definitions;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class PatternJourneyInstrumentedTest {

    private static final String THE_ORACLES_WATCHDOG =
            "starting the oracle, from attaching it to its first layout coming to rest";
    private static final String THREADS_STARTED_BEFORE_THE_ORACLES_FIRST_FILL =
            "threads started between the test's start and the oracle's first fill: ";

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void laidOutInTime_theOraclesFirstLayout_runsUnderAWatchdogNamingTheOracle() {
        final Set<Thread> threadsAtTheStart = liveThreads();
        final ThreadLookout lookout = new ThreadLookout(threadsAtTheStart);
        final ActivityScenario<HarnessActivity> scenario = mActivityRule.getScenario();

        assertRunsLikeTheOracleOnceLaidOutInTime(
                scenario,
                R.layout.instrumented_pattern_reachable_vertical_circular_no_spacing,
                VERTICAL_VIEWPORT,
                new OneGroupAdded(),
                new OneGroupLookingOutAdded(lookout));

        final List<String> threadsStarted = lookout.threadsStarted();
        final String threadsStartedMessage =
                THREADS_STARTED_BEFORE_THE_ORACLES_FIRST_FILL + threadsStarted;
        final boolean theOraclesWatchdogWasRunning = threadsStarted.contains(THE_ORACLES_WATCHDOG);
        assertTrue(threadsStartedMessage, theOraclesWatchdogWasRunning);
    }

    private static Set<Thread> liveThreads() {
        final Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        return stackTraces.keySet();
    }

    private static final class OneGroupAdded implements Definitions {
        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition oneGroup = group(isVertical, twoOneByOnes);
            layoutManager.addGridPatternGroupDefinition(oneGroup);
        }
    }

    private static final class OneGroupLookingOutAdded implements Definitions {
        private final ThreadLookout mLookout;

        private OneGroupLookingOutAdded(final ThreadLookout lookout) {
            mLookout = lookout;
        }

        @Override
        public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical) {
            final GridPatternLayoutManager layoutManager = layoutManagerOf(view);
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(isVertical);
            final GridPatternGroupDefinition oneGroup =
                    new LookingOutGroupDefinition(isVertical, twoOneByOnes, mLookout);
            layoutManager.addGridPatternGroupDefinition(oneGroup);
        }
    }

    private static final class LookingOutGroupDefinition extends GridPatternGroupDefinition {
        private final ThreadLookout mLookout;

        private LookingOutGroupDefinition(
                final boolean isVertical,
                final List<GridPatternItemDefinition> items,
                final ThreadLookout lookout) {
            super(isVertical, items);
            mLookout = lookout;
        }

        @Override
        public List<GridPatternItemDefinition> getGridPatternItemDefinitions() {
            mLookout.lookOutOnce();
            return super.getGridPatternItemDefinitions();
        }
    }

    private static final class ThreadLookout {
        private final Set<Thread> mThreadsAtTheStart;
        private final AtomicBoolean mHasLookedOut = new AtomicBoolean();
        private final List<String> mThreadsStarted = new CopyOnWriteArrayList<String>();

        private ThreadLookout(final Set<Thread> threadsAtTheStart) {
            mThreadsAtTheStart = threadsAtTheStart;
        }

        private void lookOutOnce() {
            final boolean isTheFirstLook = mHasLookedOut.compareAndSet(false, true);
            if (!isTheFirstLook) return;
            final Set<Thread> liveThreads = liveThreads();
            final Set<Thread> threadsStarted = new HashSet<Thread>(liveThreads);
            threadsStarted.removeAll(mThreadsAtTheStart);
            for (final Thread thread : threadsStarted) {
                final String name = thread.getName();
                mThreadsStarted.add(name);
            }
        }

        private List<String> threadsStarted() {
            return mThreadsStarted;
        }
    }
}
