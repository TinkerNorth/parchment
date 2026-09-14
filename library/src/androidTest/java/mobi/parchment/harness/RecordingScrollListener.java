// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.OnScrollListener;
import mobi.parchment.widget.adapterview.ScrollState;

/**
 * Records every callback a Parchment view makes, in order, with the thread it arrived on, so a test
 * on the instrumentation thread can read back what the main thread reported.
 */
public final class RecordingScrollListener implements OnScrollListener {

    private final List<ScrollState> mStates = new ArrayList<>();
    private final List<String> mCalls = new ArrayList<>();
    private int mDisplacementSum;
    private int mCallsOffTheMainThread;

    @Override
    public synchronized void onScrolled(
            final AbstractAdapterView<?, ?> view, final int displacement) {
        mDisplacementSum += displacement;
        mCalls.add("scrolled:" + displacement);
        noteTheThread();
    }

    @Override
    public synchronized void onScrollStateChanged(
            final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
        mStates.add(scrollState);
        mCalls.add("state:" + scrollState);
        noteTheThread();
    }

    private void noteTheThread() {
        final Thread mainThread = Looper.getMainLooper().getThread();
        final boolean onTheMainThread = Thread.currentThread() == mainThread;
        if (!onTheMainThread) {
            mCallsOffTheMainThread++;
        }
    }

    /** The states reported so far, in order. */
    public synchronized List<ScrollState> states() {
        return new ArrayList<>(mStates);
    }

    /** Every callback so far, in order, as "scrolled:N" or "state:S". */
    public synchronized List<String> calls() {
        return new ArrayList<>(mCalls);
    }

    /** The sum of every displacement reported so far. */
    public synchronized int displacementSum() {
        return mDisplacementSum;
    }

    public synchronized int callsOffTheMainThread() {
        return mCallsOffTheMainThread;
    }

    @Override
    public synchronized String toString() {
        return "calls=" + mCalls + " displacementSum=" + mDisplacementSum;
    }
}
