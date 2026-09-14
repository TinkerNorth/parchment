// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.divideraxis;

import android.graphics.Rect;
import android.view.View;

public interface DividerAxisInterface {

    public int getStart(final View view);

    public int getEnd(final View view);

    public int getBandStart(final View view);

    public int getBandEnd(final View view);

    public void setDividerBounds(
            final Rect bounds,
            final int dividerStart,
            final int dividerEnd,
            final int bandStart,
            final int bandEnd);
}
