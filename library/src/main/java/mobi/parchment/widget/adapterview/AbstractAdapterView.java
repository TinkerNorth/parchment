// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.widget.Adapter;

public abstract class AbstractAdapterView<ADAPTER extends Adapter, Cell>
        extends android.widget.AdapterView<ADAPTER>
        implements OnLongClickListener, OnClickListener, OnSelectedListener, AdapterViewHandler {

    private OnItemSelectedListener mOnItemSelectedListener;
    private ADAPTER mAdapter;
    private AdapterViewInitializer<Cell> mAdapterViewInitializer;

    private Runnable mRequestLayout =
            new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            };

    private final DataSetObserver mDataSetObserver =
            new DataSetObserver() {
                @Override
                public void onChanged() {
                    removeAllViewsInLayout();
                    requestLayout();
                    invalidate();
                }

                @Override
                public void onInvalidated() {
                    removeAllViewsInLayout();
                    requestLayout();
                    invalidate();
                }
            };

    public AbstractAdapterView(Context context) {
        super(context);
        initialize(context, null);
    }

    public AbstractAdapterView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context, attributeSet);
    }

    public AbstractAdapterView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initialize(context, attributeSet);
    }

    private void initialize(final Context context, final AttributeSet attributeSet) {
        mAdapterViewInitializer = getAdapterViewInitializer(context, attributeSet);
    }

    protected AdapterViewInitializer<Cell> createAdapterViewInitializer(
            final Context context,
            final boolean isViewPager,
            final AdapterViewManager adapterViewManager,
            final LayoutManager<Cell> layoutManager,
            final boolean isVerticalScroll) {
        final LayoutManagerBridge layoutManagerBridge = new LayoutManagerBridge(layoutManager);
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        final ChildTouchGestureListener childTouchGestureListener =
                new ChildTouchGestureListener(
                        this,
                        isViewPager,
                        isVerticalScroll,
                        this,
                        this,
                        layoutManagerBridge,
                        viewConfiguration);
        final AdapterViewGestureDetector adapterViewGestureDetector =
                new AdapterViewGestureDetector(context, childTouchGestureListener);

        return new AdapterViewInitializer<Cell>(
                childTouchGestureListener,
                adapterViewGestureDetector,
                layoutManager,
                adapterViewManager);
    }

    protected abstract AdapterViewInitializer<Cell> getAdapterViewInitializer(
            final Context context, final AttributeSet attributeSet);

    @Override
    public int getSelectedItemPosition() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return layoutManager.getSelectedPosition();
    }

    @Override
    public ADAPTER getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        final int selectedPosition = getSelectedItemPosition();
        if (selectedPosition == INVALID_POSITION) {
            return null;
        }
        return layoutManager.getViewForPosition(selectedPosition);
    }

    @Override
    public long getSelectedItemId() {
        final Adapter adapter = getAdapter();
        final int selectedPosition = getSelectedItemPosition();
        if (adapter == null || selectedPosition == INVALID_POSITION) {
            return INVALID_ROW_ID;
        }
        return adapter.getItemId(selectedPosition);
    }

    @Override
    public void setSelection(final int position) {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        layoutManager.setSelected(position, this);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final AdapterViewManager adapterViewManager =
                mAdapterViewInitializer.getAdapterViewManager();
        adapterViewManager.registerDataSetObserver(mDataSetObserver);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final AdapterViewManager adapterViewManager =
                mAdapterViewInitializer.getAdapterViewManager();
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        adapterViewManager.unregisterDataSetObserver(mDataSetObserver);
        layoutManager.destroy();
    }

    @Override
    public void setAdapter(ADAPTER adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            final AdapterViewManager adapterViewManager =
                    mAdapterViewInitializer.getAdapterViewManager();
            adapterViewManager.setAdapter(adapter);
        }

        requestLayout();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.measure(this, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setOnItemSelectedListener(final OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
        super.setOnItemSelectedListener(onItemSelectedListener);
    }

    @Override
    protected void onLayout(
            final boolean changed,
            final int left,
            final int top,
            final int right,
            final int bottom) {

        final ChildTouchGestureListener childTouchListener =
                mAdapterViewInitializer.getChildTouchListener();
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();

        childTouchListener.computeScrollOffset();
        final Animation animation = childTouchListener.getAnimation();

        final int leftSize = MeasureSpec.getSize(left);
        final int rightSize = MeasureSpec.getSize(right);
        final int topSize = MeasureSpec.getSize(top);
        final int bottomSize = MeasureSpec.getSize(bottom);

        if (layoutManager != null) {
            layoutManager.layout(this, animation, leftSize, topSize, rightSize, bottomSize);
        }
        final AdapterAnimator.State state = childTouchListener.getState();
        switch (state) {
            case animatingTo:
            case flinging:
            case jumpingTo:
            case snapingTo:
                post(mRequestLayout);
                awakenScrollBars();
                break;
            case scrolling:
                awakenScrollBars();
                break;
            case notMoving:
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        final boolean isChildConsumingTouch = super.dispatchTouchEvent(motionEvent);
        final ChildTouchGestureListener childTouchListener =
                mAdapterViewInitializer.getChildTouchListener();
        childTouchListener.setIsChildConsumingTouch(isChildConsumingTouch);

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        final GestureDetector gestureDetector = mAdapterViewInitializer.getGestureDetector();
        final boolean gestureConsumed = gestureDetector.onTouchEvent(motionEvent);

        return gestureConsumed || super.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        final GestureDetector gestureDetector = mAdapterViewInitializer.getGestureDetector();
        final boolean gestureConsumed = gestureDetector.onTouchEvent(motionEvent);

        if (gestureConsumed) {
            final ChildTouchGestureListener childTouchListener =
                    mAdapterViewInitializer.getChildTouchListener();
            childTouchListener.setIsChildConsumingTouch(false);
        }

        return gestureConsumed || super.onTouchEvent(motionEvent);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public void onClick(final View view) {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        if (layoutManager == null || mAdapter == null) return;

        final int position = getPositionForView(view);
        final long id = mAdapter.getItemId(position);
        if (position != android.widget.AdapterView.INVALID_POSITION) {
            layoutManager.onItemClick(view, position, id);
            performItemClick(view, position, id);
        }
    }

    @Override
    public boolean addViewInAdapterView(
            final View child, final int index, final LayoutParams layoutParams) {
        final int childCount = getChildCount();
        final int drawPosition = Math.min(index, childCount);
        final boolean success = addViewInLayout(child, drawPosition, layoutParams, true);
        invalidate();
        return success;
    }

    @Override
    public void removeViewInAdapterView(final View view) {
        removeViewInLayout(view);
        invalidate();
    }

    @Override
    public int getPositionForView(final View view) {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        if (layoutManager == null) {
            return INVALID_POSITION;
        }

        final int position = layoutManager.getPosition(view);
        if (position == LayoutManager.INVALID_POSITION) {
            return INVALID_POSITION;
        }

        return position;
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        super.setOnItemLongClickListener(onItemLongClickListener);
        if (isLongClickable()) {
            setLongClickable(false);
        }
    }

    @Override
    public void onSelected(final View view) {
        if (mOnItemSelectedListener == null) {
            return;
        }

        if (view != null) {
            final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
            if (layoutManager == null || mAdapter == null) return;

            final int position = getPositionForView(view);
            if (position == android.widget.AdapterView.INVALID_POSITION) return;

            final long id = mAdapter.getItemId(position);
            mOnItemSelectedListener.onItemSelected(this, view, position, id);
        } else {
            mOnItemSelectedListener.onNothingSelected(this);
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        final OnItemLongClickListener onItemLongClickListener = getOnItemLongClickListener();
        if (onItemLongClickListener == null || view == null) return false;

        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        if (layoutManager == null || mAdapter == null) return false;

        final int position = getPositionForView(view);
        if (position == android.widget.AdapterView.INVALID_POSITION) return false;

        final long id = mAdapter.getItemId(position);

        return onItemLongClickListener.onItemLongClick(this, view, position, id);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable parcelable = super.onSaveInstanceState();
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return layoutManager.onSaveInstanceState(parcelable);
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        final BaseSavedState baseSavedState = (BaseSavedState) parcelable;
        final Parcelable superState = baseSavedState.getSuperState();
        super.onRestoreInstanceState(superState);
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        layoutManager.onRestoreInstanceState(parcelable);
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return (int) layoutManager.getScrollBarExtent();
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return (int) layoutManager.getScrollBarOffset();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return (int) layoutManager.getScrollBarRange();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return computeHorizontalScrollExtent();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return computeHorizontalScrollOffset();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return computeHorizontalScrollRange();
    }

    @Override
    public boolean isHorizontalScrollBarEnabled() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return !layoutManager.isVerticalScroll();
    }

    @Override
    public boolean isVerticalScrollBarEnabled() {
        final LayoutManager<Cell> layoutManager = mAdapterViewInitializer.getLayoutManager();
        return layoutManager.isVerticalScroll();
    }
}
