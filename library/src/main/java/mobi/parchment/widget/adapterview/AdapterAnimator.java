// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class AdapterAnimator implements OnGestureListener, AnimationStoppedListener {

    public static enum State {
        scrolling,
        animatingTo,
        jumpingTo,
        flinging,
        snapingTo,
        notMoving
    }

    private static final int FINAL_ANIMATE_TO_DURATION_IN_MILLISECONDS = 500;

    private static final int ANIMATION_DURATION = 500;
    private final int mScaledTouchSlop;
    private final boolean mIsVerticalScroll;
    private final Animation mAnimation = new Animation();
    private final ScrollAnimator mScrollAnimator;
    private final ViewGroup mViewGroup;
    private final LayoutManagerBridge mLayoutManagerBridge;
    private final boolean mIsViewPager;
    private boolean mTouchSlopExceeded = false;

    private int mPreviousDisplacement;
    private State mState = State.notMoving;

    private boolean mComputedOffsetReady;

    public AdapterAnimator(
            final ViewGroup view,
            final boolean isViewPager,
            final boolean isVerticalScroll,
            final LayoutManagerBridge layoutManagerBridge,
            ViewConfiguration viewConfiguration) {
        mLayoutManagerBridge = layoutManagerBridge;
        mLayoutManagerBridge.setAnimationStoppedListener(this);
        mViewGroup = view;
        mIsViewPager = isViewPager;
        mIsVerticalScroll = isVerticalScroll;
        mScrollAnimator = new ScrollAnimator(view.getContext(), isVerticalScroll);
        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        mTouchSlopExceeded = false;
        setState(State.notMoving);
        return false;
    }

    @Override
    public boolean onFling(
            final MotionEvent e1,
            final MotionEvent e2,
            final float velocityX,
            final float velocityY) {
        setState(State.flinging);
        if (mIsViewPager) {
            final int viewPageDistance =
                    mLayoutManagerBridge.getViewPagerScrollDistance(velocityX, velocityY);
            mScrollAnimator.startScroll(viewPageDistance, ANIMATION_DURATION);
        } else {
            mScrollAnimator.flingBy(velocityX, velocityY);
        }

        mViewGroup.requestLayout();
        return true;
    }

    @Override
    public void onLongPress(final MotionEvent e) {
        setState(State.notMoving);
    }

    @Override
    public boolean onScroll(
            final MotionEvent e1,
            final MotionEvent e2,
            final float distanceX,
            final float distanceY) {
        if (mIsVerticalScroll) {
            updateYTouchSlop(e1, e2);
        } else {
            updateXTouchSlop(e1, e2);
        }

        if (!mTouchSlopExceeded) {
            return false;
        }

        setState(State.scrolling);
        final int displacement =
                (int) mLayoutManagerBridge.getScrollDisplacement(distanceX, distanceY);
        mAnimation.setDisplacement(displacement);
        mViewGroup.requestLayout();
        return true;
    }

    @Override
    public void onShowPress(final MotionEvent e) {
        setState(State.notMoving);
    }

    public boolean onSingleTapUp(final MotionEvent motionEvent, final View view) {
        if (mLayoutManagerBridge == null) return onSingleTapUp(motionEvent);

        final int scrollDistance = mLayoutManagerBridge.onSingleTapUp(mViewGroup, view);
        setState(State.flinging);

        mScrollAnimator.startScroll(scrollDistance, ANIMATION_DURATION);
        mViewGroup.requestLayout();

        return true;
    }

    @Override
    public boolean onSingleTapUp(final MotionEvent e) {
        setState(State.notMoving);
        return false;
    }

    public void onUp() {
        mTouchSlopExceeded = false;
        if (getState() == State.scrolling) {
            setState(State.notMoving);
        }
    }

    public State getState() {
        return mState;
    }

    void setState(final State state) {
        if (!mScrollAnimator.isFinished()) mScrollAnimator.forceFinished(true);

        final boolean isNotMoving = mState.equals(State.notMoving);
        if (isNotMoving) mAnimation.newAnimation();

        mState = state;
        mPreviousDisplacement = 0;

        // This snaps to the position when the animation is finished.
        if (state == State.notMoving && mLayoutManagerBridge != null) {
            final int scrollDistance = mLayoutManagerBridge.snapTo(mViewGroup);
            if (scrollDistance == 0) return;
            setState(State.snapingTo);
            mScrollAnimator.startScroll(scrollDistance, ANIMATION_DURATION);
            mViewGroup.requestLayout();
        }
    }

    public void computeScrollOffset() {
        mComputedOffsetReady =
                !mScrollAnimator.isFinished() && mScrollAnimator.computeScrollOffset();

        if (!mComputedOffsetReady) {
            final boolean isScrolling = mState.equals(State.scrolling);
            if (!isScrolling) setState(State.notMoving);
            return;
        }

        final int currentOffset = mScrollAnimator.getCurrrentOffset();

        mAnimation.setDisplacement(currentOffset - mPreviousDisplacement);
        mPreviousDisplacement = currentOffset;
    }

    public Animation getAnimation() {
        switch (mState) {
            case scrolling:
                return mAnimation;
            case jumpingTo:
            case animatingTo:
            case snapingTo:
            case flinging:
                if (mComputedOffsetReady) return mAnimation;
                setState(State.notMoving);
                mAnimation.newAnimation();
                return mAnimation;
            case notMoving:
            default:
                mAnimation.newAnimation();
                return mAnimation;
        }
    }

    // Is this used at all?
    public void setAnimateToDistance(final int animate) {
        setState(State.animatingTo);
        mScrollAnimator.startScroll(animate, FINAL_ANIMATE_TO_DURATION_IN_MILLISECONDS);
        mViewGroup.requestLayout();
    }

    protected ViewGroup getViewGroup() {
        return mViewGroup;
    }

    @Override
    public void onAnimationStopped() {
        setState(State.notMoving);
    }

    public static float getXTouchSlop(final MotionEvent e1, final MotionEvent e2) {
        if (e1 == null || e2 == null) {
            return 0;
        }
        return Math.abs(e1.getX() - e2.getX());
    }

    public static float getYTouchSlop(final MotionEvent e1, final MotionEvent e2) {
        if (e1 == null || e2 == null) {
            return 0;
        }
        return Math.abs(e1.getY() - e2.getY());
    }

    private void updateYTouchSlop(final MotionEvent e1, final MotionEvent e2) {
        if (!mTouchSlopExceeded) {
            final float eventDifference = getYTouchSlop(e1, e2);
            mTouchSlopExceeded = eventDifference > mScaledTouchSlop;
        }
    }

    private void updateXTouchSlop(final MotionEvent e1, final MotionEvent e2) {
        if (!mTouchSlopExceeded) {
            final float eventDifference = getXTouchSlop(e1, e2);
            mTouchSlopExceeded = eventDifference > mScaledTouchSlop;
        }
    }
}
