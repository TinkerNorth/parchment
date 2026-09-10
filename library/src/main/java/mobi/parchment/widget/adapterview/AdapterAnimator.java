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

    private final int mScaledTouchSlop;
    private final boolean mIsVerticalScroll;
    private final Animation mAnimation = new Animation();
    private final ScrollAnimator mScrollAnimator;
    private final ViewGroup mViewGroup;
    private final AnimationFrameScheduler mFrameScheduler;
    private final LayoutManagerBridge mLayoutManagerBridge;
    private final ScrollListenerDispatcher mScrollListenerDispatcher;
    private final boolean mIsViewPager;
    private boolean mTouchSlopExceeded = false;
    private boolean mIsInsideAFrame;

    private int mPreviousDisplacement;
    private int mPendingScrollDisplacement;
    private State mState = State.notMoving;

    private boolean mComputedOffsetReady;

    public AdapterAnimator(
            final ViewGroup view,
            final AnimationFrameScheduler frameScheduler,
            final boolean isViewPager,
            final boolean isVerticalScroll,
            final LayoutManagerBridge layoutManagerBridge,
            final ViewConfiguration viewConfiguration,
            final ScrollListenerDispatcher scrollListenerDispatcher) {
        mLayoutManagerBridge = layoutManagerBridge;
        mLayoutManagerBridge.setAnimationStoppedListener(this);
        mScrollListenerDispatcher = scrollListenerDispatcher;
        mViewGroup = view;
        mFrameScheduler = frameScheduler;
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
            mScrollAnimator.snapTo(viewPageDistance);
        } else {
            mScrollAnimator.flingBy(velocityX, velocityY);
            endTheFlingOnASnapPosition();
        }

        mFrameScheduler.requestAnimationFrame();
        return true;
    }

    private void endTheFlingOnASnapPosition() {
        final int finalOffset = mScrollAnimator.getFinalOffset();
        final int adjustment = mLayoutManagerBridge.getFlingSnapAdjustment(mViewGroup, finalOffset);
        if (adjustment == 0) return;
        mScrollAnimator.setFinalOffset(finalOffset + adjustment);
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
        mPendingScrollDisplacement += displacement;
        mFrameScheduler.requestAnimationFrame();
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

        mScrollAnimator.snapTo(scrollDistance);
        mFrameScheduler.requestAnimationFrame();

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
        moveToState(state);
        requestFrameForUndispatchedScrollState();
    }

    private void moveToState(final State state) {
        if (!mScrollAnimator.isFinished()) mScrollAnimator.forceFinished(true);

        final boolean isNotMoving = mState.equals(State.notMoving);
        if (isNotMoving) mAnimation.newAnimation();

        mState = state;
        mPreviousDisplacement = 0;
        if (state != State.scrolling) mPendingScrollDisplacement = 0;

        // This snaps to the position when the animation is finished.
        if (state != State.notMoving || mLayoutManagerBridge == null) return;

        final int scrollDistance = mLayoutManagerBridge.snapTo(mViewGroup);
        if (scrollDistance == 0) return;

        moveToState(State.snapingTo);
        mScrollAnimator.snapTo(scrollDistance);
        mFrameScheduler.requestAnimationFrame();
    }

    void requestFrameForUndispatchedScrollState() {
        if (mIsInsideAFrame) return;

        final ScrollState scrollState = ScrollState.from(mState);
        if (!mScrollListenerDispatcher.hasUndispatchedScrollState(scrollState)) return;
        mFrameScheduler.requestAnimationFrame();
    }

    public void computeScrollOffset() {
        mIsInsideAFrame = true;
        mComputedOffsetReady = false;
        if (mState == State.scrolling) return;

        if (mScrollAnimator.isFinished()) setState(State.notMoving);
        if (mState == State.notMoving) return;

        mComputedOffsetReady = mScrollAnimator.computeScrollOffset();
        if (!mComputedOffsetReady) return;

        final int currentOffset = mScrollAnimator.getCurrrentOffset();
        mAnimation.setDisplacement(currentOffset - mPreviousDisplacement);
        mPreviousDisplacement = currentOffset;
    }

    public void onFrameLaidOut() {
        final boolean isAnimating = mState != State.scrolling && mState != State.notMoving;
        if (isAnimating && mScrollAnimator.isFinished()) setState(State.notMoving);
        mIsInsideAFrame = false;
    }

    public Animation getAnimation() {
        switch (mState) {
            case scrolling:
                mAnimation.setDisplacement(mPendingScrollDisplacement);
                mPendingScrollDisplacement = 0;
                return mAnimation;
            case jumpingTo:
            case animatingTo:
            case snapingTo:
            case flinging:
                if (!mComputedOffsetReady) mAnimation.setDisplacement(0);
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
        mScrollAnimator.snapTo(animate);
        mFrameScheduler.requestAnimationFrame();
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
