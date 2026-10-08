// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.widget.BaseAdapter;
import mobi.parchment.widget.adapterview.AbstractAdapterView;

public final class ListenForSelections<VIEW extends AbstractAdapterView<BaseAdapter, ?>>
        implements ParchmentViewHarness.ViewSetup<VIEW> {

    private final RecordingItemSelectedListener mSelections;

    public ListenForSelections(final RecordingItemSelectedListener selections) {
        mSelections = selections;
    }

    @Override
    public void setUp(final VIEW view) {
        view.setOnItemSelectedListener(mSelections);
    }
}
