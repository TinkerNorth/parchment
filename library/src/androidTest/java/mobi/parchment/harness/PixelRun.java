// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

/** An unbroken run of one colour along a single row or column of a {@link PaintedPixels}. */
public final class PixelRun {

    private final int mStart;
    private final int mEnd;

    public PixelRun(final int start, final int end) {
        mStart = start;
        mEnd = end;
    }

    /** The first pixel of the run. */
    public int start() {
        return mStart;
    }

    /** One past the last pixel of the run, so it reads like a view's right or bottom. */
    public int end() {
        return mEnd;
    }

    public int size() {
        return mEnd - mStart;
    }

    @Override
    public String toString() {
        return "[" + mStart + ".." + mEnd + ")";
    }
}
