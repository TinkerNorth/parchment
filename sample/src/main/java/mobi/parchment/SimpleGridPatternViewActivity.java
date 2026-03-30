package mobi.parchment;

import android.os.Bundle;
import android.widget.BaseAdapter;

import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;

import java.util.ArrayList;
import java.util.List;

public class SimpleGridPatternViewActivity extends BaseActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_grid_pattern_view);

        final GridPatternView<BaseAdapter> gridPatternView = (GridPatternView<BaseAdapter>) findViewById(R.id.parchment_view);

        final List<GridPatternItemDefinition> gridPatternItemDefinitions = new ArrayList<GridPatternItemDefinition>();

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
