// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

    private ProductsAdapter mProductsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Resources resources = getResources();
        final int widthPixels = resources.getDimensionPixelSize(getPictureRequestWidthDimension());
        final int heightPixels =
                resources.getDimensionPixelSize(getPictureRequestHeightDimension());
        mProductsAdapter = new ProductsAdapter(getLayoutResourceId(), widthPixels, heightPixels);
    }

    public ProductsAdapter getProductsAdapter() {
        return mProductsAdapter;
    }

    public abstract int getLayoutResourceId();

    public abstract int getPictureRequestWidthDimension();

    public abstract int getPictureRequestHeightDimension();
}
