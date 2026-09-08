// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.os.Bundle;
import android.widget.BaseAdapter;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.gridview.GridView;

public class SimpleGridViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_gridview);

        final GridView<BaseAdapter> gridView = findViewById(R.id.parchment_view);
        gridView.setAdapter(getProductsAdapter());
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.list_item_gridview_picture;
    }
}
