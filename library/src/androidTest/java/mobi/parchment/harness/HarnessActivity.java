// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * The Activity every instrumented test attaches its Parchment view to. It holds nothing but a
 * full-size {@link FrameLayout}, so a view added to it is measured with exactly the layout
 * parameters the harness gives it.
 */
public final class HarnessActivity extends Activity {

    private FrameLayout mContent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FrameLayout content = new FrameLayout(this);
        final ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        content.setLayoutParams(layoutParams);
        setContentView(content);
        mContent = content;
    }

    public FrameLayout content() {
        return mContent;
    }
}
