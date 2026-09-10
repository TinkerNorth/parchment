// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

/**
 * An immutable snapshot of where a Parchment view's children landed, read on the main thread so a
 * test never reads live view geometry from the instrumentation thread.
 */
public final class LaidOutChildren {

    private final int[] mLefts;
    private final int[] mTops;
    private final int[] mRights;
    private final int[] mBottoms;
    private final int[] mAdapterPositions;
    private final int mViewWidth;
    private final int mViewHeight;

    LaidOutChildren(
            final int[] lefts,
            final int[] tops,
            final int[] rights,
            final int[] bottoms,
            final int[] adapterPositions,
            final int viewWidth,
            final int viewHeight) {
        mLefts = lefts;
        mTops = tops;
        mRights = rights;
        mBottoms = bottoms;
        mAdapterPositions = adapterPositions;
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
    }

    public int count() {
        return mLefts.length;
    }

    public int left(final int index) {
        return mLefts[requireLaidOut(index)];
    }

    public int top(final int index) {
        return mTops[requireLaidOut(index)];
    }

    public int right(final int index) {
        return mRights[requireLaidOut(index)];
    }

    public int bottom(final int index) {
        return mBottoms[requireLaidOut(index)];
    }

    public int width(final int index) {
        return right(index) - left(index);
    }

    public int height(final int index) {
        return bottom(index) - top(index);
    }

    public int adapterPosition(final int index) {
        return mAdapterPositions[requireLaidOut(index)];
    }

    private int requireLaidOut(final int index) {
        final boolean isAChild = index >= 0 && index < mLefts.length;
        if (!isAChild) {
            throw new AssertionError("there is no child at index " + index + ": " + this);
        }
        return index;
    }

    public int viewWidth() {
        return mViewWidth;
    }

    public int viewHeight() {
        return mViewHeight;
    }

    /**
     * The index of the child holding the given adapter position, or -1 when the position is not
     * laid out.
     */
    public int indexOfAdapterPosition(final int adapterPosition) {
        for (int index = 0; index < mAdapterPositions.length; index++) {
            if (mAdapterPositions[index] == adapterPosition) {
                return index;
            }
        }
        return -1;
    }

    public int occurrencesOfAdapterPosition(final int adapterPosition) {
        int occurrences = 0;
        for (int index = 0; index < mAdapterPositions.length; index++) {
            if (mAdapterPositions[index] == adapterPosition) {
                occurrences++;
            }
        }
        return occurrences;
    }

    public int lowestAdapterPosition() {
        if (mAdapterPositions.length == 0) {
            throw new AssertionError("nothing is laid out: " + this);
        }
        int lowest = Integer.MAX_VALUE;
        for (int index = 0; index < mAdapterPositions.length; index++) {
            lowest = Math.min(lowest, mAdapterPositions[index]);
        }
        return lowest;
    }

    /** The top of the child holding the given adapter position. */
    public int topOfAdapterPosition(final int adapterPosition) {
        final int index = indexOfAdapterPosition(adapterPosition);
        if (index < 0) {
            throw new AssertionError(
                    "adapter position " + adapterPosition + " is not laid out: " + this);
        }
        return mTops[index];
    }

    /**
     * True when the given adapter position is laid out with its top at the given pixel. False when
     * the position is not laid out at all, so a caller can ask about a cell that may have been
     * recycled.
     */
    public boolean isAdapterPositionAtTop(final int adapterPosition, final int top) {
        final int index = indexOfAdapterPosition(adapterPosition);
        if (index < 0) {
            return false;
        }
        return mTops[index] == top;
    }

    /** True when some child rests with its top at the given pixel. */
    public boolean hasChildWithTop(final int top) {
        for (int index = 0; index < mTops.length; index++) {
            if (mTops[index] == top) {
                return true;
            }
        }
        return false;
    }

    /** True when every child sits exactly where the other snapshot's children sit. */
    public boolean sameGeometryAs(final LaidOutChildren other) {
        if (other.count() != count()) {
            return false;
        }
        for (int index = 0; index < mLefts.length; index++) {
            final boolean sameBounds =
                    mLefts[index] == other.mLefts[index]
                            && mTops[index] == other.mTops[index]
                            && mRights[index] == other.mRights[index]
                            && mBottoms[index] == other.mBottoms[index];
            if (!sameBounds) {
                return false;
            }
            if (mAdapterPositions[index] != other.mAdapterPositions[index]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("children in a ")
                .append(mViewWidth)
                .append('x')
                .append(mViewHeight)
                .append(" view:");
        for (int index = 0; index < mLefts.length; index++) {
            builder.append("\n  [")
                    .append(index)
                    .append("] adapterPosition=")
                    .append(mAdapterPositions[index])
                    .append(" left=")
                    .append(mLefts[index])
                    .append(" top=")
                    .append(mTops[index])
                    .append(" right=")
                    .append(mRights[index])
                    .append(" bottom=")
                    .append(mBottoms[index]);
        }
        return builder.toString();
    }
}
