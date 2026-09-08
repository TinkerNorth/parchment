// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import mobi.parchment.sample.R;

public class MenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.menu_list_view)
                .setOnClickListener(v -> start(SimpleListViewActivity.class));
        findViewById(R.id.menu_grid_view)
                .setOnClickListener(v -> start(SimpleGridViewActivity.class));
        findViewById(R.id.menu_grid_pattern_view)
                .setOnClickListener(v -> start(SimpleGridPatternViewActivity.class));
        findViewById(R.id.menu_view_pager)
                .setOnClickListener(v -> start(SimpleViewPagerActivity.class));
    }

    private void start(final Class<? extends Activity> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}
