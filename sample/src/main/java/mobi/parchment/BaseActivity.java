// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;

/** Created by emir on 16/03/14. */
public abstract class BaseActivity extends Activity {
    private ProductsAdapter mProductsAdapter = new ProductsAdapter(getLayoutResourceId());

    public ProductsAdapter getProductsAdapter() {
        return mProductsAdapter;
    }

    public abstract int getLayoutResourceId();
}
