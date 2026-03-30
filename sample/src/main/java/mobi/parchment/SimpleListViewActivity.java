package mobi.parchment;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.listview.ListView;

public class SimpleListViewActivity extends BaseActivity {

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_horizontal);
		
		final ListView horizontalListView = (ListView<BaseAdapter>) findViewById(R.id.parchment_view);
		horizontalListView.setAdapter(getProductsAdapter());
	}

    @Override
    public int getLayoutResourceId() {
        return R.layout.list_item_horizontal_picture;
    }

}
