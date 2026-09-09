// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.os.Bundle;
import android.widget.BaseAdapter;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.listview.ListView;

public class SimpleViewPagerActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_view_pager);

        final ListView<BaseAdapter> viewPager = findViewById(R.id.parchment_view);
        viewPager.setAdapter(getProductsAdapter());
    }

    @Override
    public int getPictureRequestWidthDimension() {
        return R.dimen.picture_request_view_pager_width;
    }

    @Override
    public int getPictureRequestHeightDimension() {
        return R.dimen.picture_request_view_pager_height;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.list_item_view_pager_picture;
    }
}
