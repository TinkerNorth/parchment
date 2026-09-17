// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

public class Animation {
    public static final int NO_SEEK_TARGET = -1;
    private static final int ANIMATION_ID_LIMIT = 100;
    private static final boolean A_GESTURE = true;
    private int mId;
    private int mDisplacement;
    private boolean mIsAGesture = A_GESTURE;
    private int mSeekTarget = NO_SEEK_TARGET;

    public int getId() {
        return mId;
    }

    protected void setDisplacement(final int displacemente) {
        mDisplacement = displacemente;
    }

    public int getDisplacement() {
        return mDisplacement;
    }

    public boolean isAGesture() {
        return mIsAGesture;
    }

    public int getSeekTarget() {
        return mSeekTarget;
    }

    protected void setSeekTarget(final int seekTarget) {
        mSeekTarget = seekTarget;
    }

    protected void newAnimation() {
        newAnimation(A_GESTURE);
    }

    protected void newAnimation(final boolean isAGesture) {
        mId = (mId + 1) % ANIMATION_ID_LIMIT;
        mDisplacement = 0;
        mIsAGesture = isAGesture;
        mSeekTarget = NO_SEEK_TARGET;
    }

    @Override
    public String toString() {
        return " [ mid: " + mId + " mDisplacement: " + mDisplacement + " ] ";
    }
}
