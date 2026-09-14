// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.widget.BaseAdapter;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.OnScrollListener;

/** Sets a scroll listener on the view, on the main thread, wherever the harness runs a setup. */
public final class AttachScrollListener<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
        implements ParchmentViewHarness.ViewSetup<VIEW> {

    private final OnScrollListener mListener;

    public AttachScrollListener(final OnScrollListener listener) {
        mListener = listener;
    }

    @Override
    public void setUp(final VIEW view) {
        view.setOnScrollListener(mListener);
    }
}
