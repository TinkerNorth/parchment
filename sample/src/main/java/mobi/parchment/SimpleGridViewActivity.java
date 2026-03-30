package mobi.parchment;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.gridview.GridView;

public class SimpleGridViewActivity extends BaseActivity {


	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_gridview);
		
		final GridView gridView = (GridView<BaseAdapter>) findViewById(R.id.parchment_view);
		gridView.setAdapter(getProductsAdapter());
	}

    @Override
    public int getLayoutResourceId() {
        return R.layout.list_item_gridview_picture;
    }

}
