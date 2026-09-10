// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

final class ScrollListenerDispatcher {

    static final int NO_DISPLACEMENT = 0;

    private OnScrollListener mOnScrollListener;
    private ScrollState mDispatchedScrollState = ScrollState.idle;

    void setOnScrollListener(final OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    boolean hasUndispatchedScrollState(final ScrollState scrollState) {
        if (mOnScrollListener == null) return false;
        return scrollState != mDispatchedScrollState;
    }

    void dispatch(
            final AbstractAdapterView<?, ?> view,
            final int displacement,
            final ScrollState scrollState) {
        if (mOnScrollListener == null) return;

        final boolean contentMoved = displacement != NO_DISPLACEMENT;
        if (contentMoved) mOnScrollListener.onScrolled(view, displacement);

        if (mOnScrollListener == null) return;
        if (scrollState == mDispatchedScrollState) return;

        mDispatchedScrollState = scrollState;
        mOnScrollListener.onScrollStateChanged(view, scrollState);
    }
}
