// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.view.MenuItem;

// Android's own Up navigation restarts a standard-launch parent from a bare intent, and a fresh
// playground carries neither its preset nor the user's edits.
final class UpIsBack {

    private UpIsBack() {}

    static boolean handles(final Activity activity, final MenuItem item) {
        final boolean isUp = item.getItemId() == android.R.id.home;
        if (isUp) {
            activity.finish();
        }
        return isUp;
    }
}
