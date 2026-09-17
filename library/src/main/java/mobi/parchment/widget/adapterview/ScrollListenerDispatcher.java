// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

public final class ScrollListenerDispatcher {

    public static final int NO_DISPLACEMENT = 0;

    private OnScrollListener mOnScrollListener;
    private ScrollState mDispatchedScrollState = ScrollState.idle;

    public void setOnScrollListener(final OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public boolean hasUndispatchedScrollState(final ScrollState scrollState) {
        if (mOnScrollListener == null) return false;
        return scrollState != mDispatchedScrollState;
    }

    public void dispatch(
            final AbstractAdapterView<?, ?> view,
            final int displacement,
            final ScrollState scrollStateDuringTheFrame,
            final ScrollState scrollStateAtTheEnd) {
        if (mOnScrollListener == null) return;

        final boolean contentMoved = displacement != NO_DISPLACEMENT;
        if (contentMoved) reportTheMovement(view, displacement, scrollStateDuringTheFrame);

        report(view, scrollStateAtTheEnd);
    }

    private void reportTheMovement(
            final AbstractAdapterView<?, ?> view,
            final int displacement,
            final ScrollState scrollStateDuringTheFrame) {
        mOnScrollListener.onScrolled(view, displacement);
        report(view, scrollStateDuringTheFrame);
    }

    private void report(final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
        if (mOnScrollListener == null) return;
        if (scrollState == mDispatchedScrollState) return;

        mDispatchedScrollState = scrollState;
        mOnScrollListener.onScrollStateChanged(view, scrollState);
    }
}
