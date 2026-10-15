// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.widget.BaseAdapter;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;

public interface Definitions {
    public void defineOn(final GridPatternView<BaseAdapter> view, final boolean isVertical);
}
