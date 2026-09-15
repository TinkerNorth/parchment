// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.OnScrollListener;
import mobi.parchment.widget.adapterview.ScrollState;

/**
 * One line of what the demo's view reports through its listeners: the scroll state, the last
 * frame's displacement and the sum of all of them, the selected position, and the tapped one.
 */
final class DemoStatus
        implements OnScrollListener,
                AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {

    private final TextView mTextView;
    private ScrollState mScrollState = ScrollState.idle;
    private int mLastDisplacement;
    private int mTotalDisplacement;
    private int mSelectedPosition = AdapterView.INVALID_POSITION;
    private int mTappedPosition = AdapterView.INVALID_POSITION;

    DemoStatus(final TextView textView) {
        mTextView = textView;
        render();
    }

    @Override
    public void onScrolled(final AbstractAdapterView<?, ?> view, final int displacement) {
        mLastDisplacement = displacement;
        mTotalDisplacement += displacement;
        render();
    }

    @Override
    public void onScrollStateChanged(
            final AbstractAdapterView<?, ?> view, final ScrollState scrollState) {
        mScrollState = scrollState;
        render();
    }

    @Override
    public void onItemSelected(
            final AdapterView<?> parent, final View view, final int position, final long id) {
        mSelectedPosition = position;
        render();
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {
        mSelectedPosition = AdapterView.INVALID_POSITION;
        render();
    }

    @Override
    public void onItemClick(
            final AdapterView<?> parent, final View view, final int position, final long id) {
        mTappedPosition = position;
        render();
    }

    private void render() {
        final Resources resources = mTextView.getResources();
        final String selected = positionText(resources, mSelectedPosition);
        final String tapped = positionText(resources, mTappedPosition);
        final String status =
                resources.getString(
                        R.string.demo_status,
                        mScrollState.name(),
                        mLastDisplacement,
                        mTotalDisplacement,
                        selected,
                        tapped);
        mTextView.setText(status);
    }

    private static String positionText(final Resources resources, final int position) {
        if (position == AdapterView.INVALID_POSITION) {
            return resources.getString(R.string.demo_nothing);
        }
        return Integer.toString(position);
    }
}
