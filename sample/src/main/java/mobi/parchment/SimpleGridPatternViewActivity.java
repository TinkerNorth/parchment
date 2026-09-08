// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.os.Bundle;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;

public class SimpleGridPatternViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_grid_pattern_view);

        final GridPatternView<BaseAdapter> gridPatternView = findViewById(R.id.parchment_view);

        final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                new ArrayList<GridPatternItemDefinition>();

        gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 6, 4));
        gridPatternView.addGridPatternGroupDefinition(gridPatternItemDefinitions);
        gridPatternItemDefinitions.clear();

        gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 2, 2));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(2, 0, 2, 2));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(4, 0, 2, 3));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 2, 4, 4));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(4, 3, 2, 3));
        gridPatternView.addGridPatternGroupDefinition(gridPatternItemDefinitions);
        gridPatternItemDefinitions.clear();

        gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 2, 3));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(2, 0, 4, 4));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 3, 2, 3));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(2, 4, 2, 2));
        gridPatternItemDefinitions.add(new GridPatternItemDefinition(4, 4, 2, 2));
        gridPatternView.addGridPatternGroupDefinition(gridPatternItemDefinitions);

        gridPatternView.setAdapter(getProductsAdapter());
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.list_item_gridpatternview_picture;
    }
}
