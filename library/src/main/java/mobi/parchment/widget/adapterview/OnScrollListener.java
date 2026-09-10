// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

public interface OnScrollListener {

    void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement);

    void onScrollStateChanged(final AbstractAdapterView<?, ?> view, final ScrollState scrollState);
}
