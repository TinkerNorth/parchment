// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

public final class Viewport {

    private final boolean mIsVertical;
    private final int mWidth;
    private final int mHeight;
    private final int mForwardFromX;
    private final int mForwardFromY;
    private final int mForwardToX;
    private final int mForwardToY;

    public Viewport(
            final boolean isVertical,
            final int width,
            final int height,
            final int forwardFromX,
            final int forwardFromY,
            final int forwardToX,
            final int forwardToY) {
        mIsVertical = isVertical;
        mWidth = width;
        mHeight = height;
        mForwardFromX = forwardFromX;
        mForwardFromY = forwardFromY;
        mForwardToX = forwardToX;
        mForwardToY = forwardToY;
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

    public int startOf(final LaidOutChildren children, final int index) {
        if (mIsVertical) return children.top(index);
        return children.left(index);
    }

    public void flingForward(final ParchmentViewHarness<?> harness, final int steps) {
        harness.fling(mForwardFromX, mForwardFromY, mForwardToX, mForwardToY, steps);
    }

    public void dragBack(final ParchmentViewHarness<?> harness, final int steps) {
        harness.dragAndRelease(mForwardToX, mForwardToY, mForwardFromX, mForwardFromY, steps);
    }

    public void dragForward(final ParchmentViewHarness<?> harness, final int steps) {
        harness.dragAndRelease(mForwardFromX, mForwardFromY, mForwardToX, mForwardToY, steps);
    }
}
