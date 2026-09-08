// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class ChildTouchGestureListener extends AdapterAnimator {

    private final OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private int mScaledTouchSlop;
    private View mInitialTouchView;
    private boolean mIsChildConsumingTouch;
    private boolean mIsSingleTapUp;

    public ChildTouchGestureListener(
            final ViewGroup viewGroup,
            final AnimationFrameScheduler frameScheduler,
            final boolean isViewPager,
            final boolean isVertical,
            final OnClickListener onClickListener,
            final OnLongClickListener onLongClickListener,
            final LayoutManagerBridge layoutManagerBridge,
            final ViewConfiguration viewConfiguration) {
        super(
                viewGroup,
                frameScheduler,
                isViewPager,
                isVertical,
                layoutManagerBridge,
                viewConfiguration);
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    public void onLongPress(final MotionEvent motionEvent) {
        if (mInitialTouchView != null) {
            mInitialTouchView.setPressed(false);
            onItemLongClick(mInitialTouchView);
            super.onLongPress(motionEvent);
        }
        mInitialTouchView = null;
        super.onLongPress(motionEvent);
    }

    @Override
    public boolean onScroll(
            final MotionEvent e1,
            final MotionEvent e2,
            final float distanceX,
            final float distanceY) {
        final float xTouchSlop = getXTouchSlop(e1, e2);
        final float yTouchSlop = getYTouchSlop(e1, e2);
        if (xTouchSlop > mScaledTouchSlop || yTouchSlop > mScaledTouchSlop) {
            if (mInitialTouchView != null) {
                mInitialTouchView.setPressed(false);
                mInitialTouchView = null;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        mIsSingleTapUp = true;
        if (!mIsChildConsumingTouch && mInitialTouchView != null) {
            onItemClick(mInitialTouchView);
            return super.onSingleTapUp(motionEvent, mInitialTouchView);
        }
        mInitialTouchView = null;
        return super.onSingleTapUp(motionEvent);
    }

    @Override
    public void onShowPress(final MotionEvent event) {
        if (!mIsChildConsumingTouch && mInitialTouchView != null) {
            mInitialTouchView.setPressed(true);
        }
        super.onShowPress(event);
    }

    @Override
    public boolean onDown(final MotionEvent event) {
        mInitialTouchView = null;
        mIsSingleTapUp = false;

        final ViewGroup viewGroup = getViewGroup();

        final int pointerCount = event.getPointerCount();

        for (int p = 0; p < pointerCount; p++) {
            final double touchX = event.getX(p);
            final double touchY = event.getY(p);

            for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                final View child = viewGroup.getChildAt(i);
                final float childLeft = child.getLeft();
                final float childTop = child.getTop();
                final float childRight = child.getWidth() + childLeft;
                final float childBottom = child.getHeight() + childTop;

                final boolean xFits = touchX >= childLeft && touchX <= childRight;
                final boolean yFits = touchY >= childTop && touchY <= childBottom;

                if (xFits && yFits) {
                    mInitialTouchView = child;
                    return super.onDown(event);
                }
            }
        }
        return super.onDown(event);
    }

    private void onItemLongClick(final View view) {
        if (mOnLongClickListener == null) {
            return;
        }

        mOnLongClickListener.onLongClick(view);
    }

    private void onItemClick(final View view) {
        if (mOnClickListener == null) {
            return;
        }

        mOnClickListener.onClick(view);
    }

    public void onCancel() {
        stopTouch();
    }

    @Override
    public void onUp() {
        stopTouch();
        super.onUp();
    }

    private void stopTouch() {
        if (!mIsSingleTapUp && mInitialTouchView != null) {
            mInitialTouchView.setPressed(false);
        }
        mIsSingleTapUp = false;
        mInitialTouchView = null;
        mIsChildConsumingTouch = false;
    }

    public void setIsChildConsumingTouch(final boolean isChildConsumingTouch) {
        mIsChildConsumingTouch = isChildConsumingTouch;
    }
}
