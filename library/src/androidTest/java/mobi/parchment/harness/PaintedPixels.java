// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import java.util.ArrayList;
import java.util.List;

/**
 * An immutable snapshot of what a view actually painted, read on the main thread so a test never
 * rasterises a live view from the instrumentation thread. Geometry is asserted through the runs of
 * a colour along a row or a column: one run per thing painted, with the pixels it covers.
 */
public final class PaintedPixels {

    private static final int NO_RUN = -1;

    private final int[] mPixels;
    private final int mWidth;
    private final int mHeight;

    public PaintedPixels(final int[] pixels, final int width, final int height) {
        mPixels = pixels.clone();
        mWidth = width;
        mHeight = height;
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

    public int colourAt(final int x, final int y) {
        requireInside(x, y);
        final int index = y * mWidth + x;
        return mPixels[index];
    }

    /** The runs of the colour down the column at x, in top-to-bottom order. */
    public List<PixelRun> runsDownColumn(final int x, final int colour) {
        requireInside(x, 0);
        final int firstIndex = x;
        final int stride = mWidth;
        return runsAlong(firstIndex, stride, mHeight, colour);
    }

    /** The runs of the colour across the row at y, in left-to-right order. */
    public List<PixelRun> runsAcrossRow(final int y, final int colour) {
        requireInside(0, y);
        final int firstIndex = y * mWidth;
        final int stride = 1;
        return runsAlong(firstIndex, stride, mWidth, colour);
    }

    /** Every pixel of the colour, anywhere in the snapshot. */
    public int countOfColour(final int colour) {
        int count = 0;
        for (int index = 0; index < mPixels.length; index++) {
            if (mPixels[index] == colour) {
                count++;
            }
        }
        return count;
    }

    private List<PixelRun> runsAlong(
            final int firstIndex, final int stride, final int length, final int colour) {
        final List<PixelRun> runs = new ArrayList<PixelRun>();
        int runStart = NO_RUN;
        for (int step = 0; step < length; step++) {
            final int index = firstIndex + step * stride;
            final boolean isColour = mPixels[index] == colour;
            if (isColour && runStart == NO_RUN) {
                runStart = step;
            }
            if (!isColour && runStart != NO_RUN) {
                runs.add(new PixelRun(runStart, step));
                runStart = NO_RUN;
            }
        }
        if (runStart != NO_RUN) {
            runs.add(new PixelRun(runStart, length));
        }
        return runs;
    }

    private void requireInside(final int x, final int y) {
        final boolean isInside = x >= 0 && x < mWidth && y >= 0 && y < mHeight;
        if (!isInside) {
            throw new AssertionError(
                    "(" + x + "," + y + ") is outside a " + mWidth + "x" + mHeight + " snapshot");
        }
    }

    @Override
    public String toString() {
        return "painted pixels of a " + mWidth + "x" + mHeight + " view";
    }
}
