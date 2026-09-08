// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.view.GestureDetector;

/** Created by Emir Hasanbegovic on 2014-03-10. */
public class AdapterViewInitializer<Cell> {

    private final ChildTouchGestureListener mChildTouchListener;
    private final GestureDetector mGestureDetector;
    private final LayoutManager<Cell> mLayoutManager;
    private final AdapterViewManager mAdapterViewManager;

    public AdapterViewInitializer(
            final ChildTouchGestureListener childTouchListener,
            final GestureDetector gestureDetector,
            final LayoutManager<Cell> layoutManager,
            final AdapterViewManager adapterViewManager) {
        mChildTouchListener = childTouchListener;
        mGestureDetector = gestureDetector;
        mLayoutManager = layoutManager;
        mAdapterViewManager = adapterViewManager;
    }

    public ChildTouchGestureListener getChildTouchListener() {
        return mChildTouchListener;
    }

    public GestureDetector getGestureDetector() {
        return mGestureDetector;
    }

    public LayoutManager<Cell> getLayoutManager() {
        return mLayoutManager;
    }

    public AdapterViewManager getAdapterViewManager() {
        return mAdapterViewManager;
    }
}
