// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import mobi.parchment.sample.R;

/** Created by emir on 15/03/14. */
public class MenuActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void startActivity(Class<?> activityClass) {
        final Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    public void onClickListView(final View view) {
        startActivity(SimpleListViewActivity.class);
    }

    public void onClickGridView(final View view) {
        startActivity(SimpleGridViewActivity.class);
    }

    public void onCLickGridPatternView(final View view) {
        startActivity(SimpleGridPatternViewActivity.class);
    }

    public void onClickViewPager(final View view) {
        startActivity(SimpleViewPagerActivity.class);
    }
}
