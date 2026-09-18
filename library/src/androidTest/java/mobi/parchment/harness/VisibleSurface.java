// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

/**
 * An immutable snapshot of what the inherited {@code android.widget.AdapterView} surface reports,
 * read in one main-thread pass so the three answers cannot disagree with each other.
 */
public final class VisibleSurface {

    private final int mCount;
    private final int mFirstVisiblePosition;
    private final int mLastVisiblePosition;
    private final int mVisibility;

    VisibleSurface(
            final int count,
            final int firstVisiblePosition,
            final int lastVisiblePosition,
            final int visibility) {
        mCount = count;
        mFirstVisiblePosition = firstVisiblePosition;
        mLastVisiblePosition = lastVisiblePosition;
        mVisibility = visibility;
    }

    public int count() {
        return mCount;
    }

    public int firstVisiblePosition() {
        return mFirstVisiblePosition;
    }

    public int lastVisiblePosition() {
        return mLastVisiblePosition;
    }

    public int visibility() {
        return mVisibility;
    }

    @Override
    public String toString() {
        return "VisibleSurface{count="
                + mCount
                + ", first="
                + mFirstVisiblePosition
                + ", last="
                + mLastVisiblePosition
                + ", visibility="
                + mVisibility
                + "}";
    }
}
