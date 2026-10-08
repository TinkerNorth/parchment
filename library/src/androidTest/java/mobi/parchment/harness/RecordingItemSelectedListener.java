// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.view.View;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;

public final class RecordingItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private static final int NOTHING_SELECTED = AdapterView.INVALID_POSITION;

    private final List<Integer> mPositions = new ArrayList<Integer>();

    @Override
    public synchronized void onItemSelected(
            final AdapterView<?> parent, final View view, final int position, final long id) {
        mPositions.add(position);
    }

    @Override
    public synchronized void onNothingSelected(final AdapterView<?> parent) {
        mPositions.add(NOTHING_SELECTED);
    }

    public synchronized List<Integer> positions() {
        return new ArrayList<Integer>(mPositions);
    }
}
