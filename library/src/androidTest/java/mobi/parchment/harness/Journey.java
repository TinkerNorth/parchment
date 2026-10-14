// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

public final class Journey {
    private final PatternState mLaidOut;
    private final PatternState mSelected;
    private final int mScrollBackTarget;
    private final PatternState mScrolled;
    private final PatternState mDragged;
    private final PatternState mShrunk;
    private final PatternState mGrown;

    public Journey(
            final PatternState laidOut,
            final PatternState selected,
            final int scrollBackTarget,
            final PatternState scrolled,
            final PatternState dragged,
            final PatternState shrunk,
            final PatternState grown) {
        mLaidOut = laidOut;
        mSelected = selected;
        mScrollBackTarget = scrollBackTarget;
        mScrolled = scrolled;
        mDragged = dragged;
        mShrunk = shrunk;
        mGrown = grown;
    }

    public PatternState laidOut() {
        return mLaidOut;
    }

    public PatternState selected() {
        return mSelected;
    }

    public int scrollBackTarget() {
        return mScrollBackTarget;
    }

    public PatternState scrolled() {
        return mScrolled;
    }

    public PatternState dragged() {
        return mDragged;
    }

    public PatternState shrunk() {
        return mShrunk;
    }

    public PatternState grown() {
        return mGrown;
    }
}
