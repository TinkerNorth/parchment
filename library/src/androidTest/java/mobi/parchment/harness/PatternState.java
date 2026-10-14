// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

public final class PatternState {

    private final LaidOutChildren mChildren;
    private final int mSelectedPosition;
    private final VisibleSurface mSurface;
    private final List<Integer> mSelections;

    private PatternState(
            final LaidOutChildren children,
            final int selectedPosition,
            final VisibleSurface surface,
            final List<Integer> selections) {
        mChildren = children;
        mSelectedPosition = selectedPosition;
        mSurface = surface;
        mSelections = selections;
    }

    public static PatternState of(
            final ParchmentViewHarness<?> harness, final RecordingItemSelectedListener selections) {
        final LaidOutChildren children = harness.children();
        final int selectedPosition = harness.selectedItemPosition();
        final VisibleSurface surface = harness.surface();
        final List<Integer> selectedPositions = selections.positions();
        return new PatternState(children, selectedPosition, surface, selectedPositions);
    }

    public static void assertSameState(final PatternState actual, final PatternState oracle) {
        final String both = "\nthe pattern: " + actual + "\nthe oracle: " + oracle;
        final boolean sameChildren = actual.mChildren.sameGeometryAs(oracle.mChildren);
        final VisibleSurface actualSurface = actual.mSurface;
        final VisibleSurface oracleSurface = oracle.mSurface;
        assertTrue(both, sameChildren);
        assertEquals(both, oracle.mSelectedPosition, actual.mSelectedPosition);
        assertEquals(
                both, oracleSurface.firstVisiblePosition(), actualSurface.firstVisiblePosition());
        assertEquals(
                both, oracleSurface.lastVisiblePosition(), actualSurface.lastVisiblePosition());
        assertEquals(both, oracle.mSelections, actual.mSelections);
    }

    public LaidOutChildren children() {
        return mChildren;
    }

    public int selectedPosition() {
        return mSelectedPosition;
    }

    public VisibleSurface surface() {
        return mSurface;
    }

    public List<Integer> selections() {
        return mSelections;
    }

    @Override
    public String toString() {
        return mChildren
                + "\n  selected "
                + mSelectedPosition
                + ", "
                + mSurface
                + ", selections reported "
                + mSelections;
    }
}
