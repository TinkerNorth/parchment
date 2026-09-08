// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;

/** Created by Emir Hasanbegovic */
public class AdapterViewGestureDetector extends GestureDetector {
    private ChildTouchGestureListener mChildTouchGestureListener;

    public AdapterViewGestureDetector(
            final Context context, final ChildTouchGestureListener listener) {
        super(context, listener);
        initialize(listener);
    }

    public AdapterViewGestureDetector(
            final Context context,
            final ChildTouchGestureListener listener,
            final Handler handler) {
        super(context, listener, handler);
        initialize(listener);
    }

    public AdapterViewGestureDetector(
            final Context context,
            final ChildTouchGestureListener listener,
            final Handler handler,
            final boolean unused) {
        super(context, listener, handler, unused);
        initialize(listener);
    }

    @java.lang.Deprecated()
    @SuppressWarnings("deprecation")
    public AdapterViewGestureDetector(final ChildTouchGestureListener listener) {
        super(listener);
        initialize(listener);
    }

    @java.lang.Deprecated()
    @SuppressWarnings("deprecation")
    public AdapterViewGestureDetector(
            final ChildTouchGestureListener listener, final Handler handler) {
        super(listener, handler);
        initialize(listener);
    }

    private void initialize(final ChildTouchGestureListener onGestureListenerListener) {
        mChildTouchGestureListener = onGestureListenerListener;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final boolean isUsed = super.onTouchEvent(motionEvent);
        final int action = motionEvent.getAction();
        final boolean up = action == MotionEvent.ACTION_UP;
        final boolean cancel = action == MotionEvent.ACTION_CANCEL;

        if (mChildTouchGestureListener == null) return isUsed;
        // GestureDetector does not report the on Up so we report it ourselves.
        if (up) mChildTouchGestureListener.onUp();
        else if (cancel) mChildTouchGestureListener.onCancel();

        return isUsed;
    }
}
