// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.database.DataSetObserver;
import android.widget.Adapter;
import java.util.Collection;
import java.util.HashSet;

/** Created by Emir Hasanbegovic */
public class DataSetObserverManager {
    private final Collection<DataSetObserver> mDataSetObservers = new HashSet<DataSetObserver>();
    private Adapter mAdapter;

    public void setAdapter(final Adapter adapter) {
        if (adapter == mAdapter) return;

        if (mAdapter != null) unregisterAdapter(mAdapter);
        mAdapter = adapter;
        registerAdapter(mAdapter);
    }

    private void registerAdapter(final Adapter adapter) {
        if (adapter == null) return;

        for (final DataSetObserver dataSetObserver : mDataSetObservers)
            adapter.registerDataSetObserver(dataSetObserver);
    }

    private void unregisterAdapter(final Adapter adapter) {
        if (adapter == null) return;

        for (final DataSetObserver dataSetObserver : mDataSetObservers)
            adapter.unregisterDataSetObserver(dataSetObserver);
    }

    public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
        final boolean added = mDataSetObservers.add(dataSetObserver);
        final boolean registerDataSetObserver = added && mAdapter != null;
        if (registerDataSetObserver) mAdapter.registerDataSetObserver(dataSetObserver);
    }

    public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
        final boolean inDataSetObservers = mDataSetObservers.remove(dataSetObserver);
        final boolean unregisterDataSerObserver = inDataSetObservers && mAdapter != null;
        if (unregisterDataSerObserver) mAdapter.unregisterDataSetObserver(dataSetObserver);
    }
}
