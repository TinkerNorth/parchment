package mobi.parchment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import mobi.parchment.widget.adapterview.listview.ListView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class HorizontalListViewTest {

    public static final int TEST_ADAPTER_COUNT = 12;
    public static final int HORIZONTAL_LIST_VIEW_WIDTH = 1000;

    @Test
    public void testBasicIntegration() {
        final ActivityController<TestActivity> controller = Robolectric.buildActivity(TestActivity.class);
        final TestActivity testActivity = controller.get();
        testActivity.setLayoutId(R.layout.basic);
        controller.create();

        final ListView horizontalListView = (ListView) testActivity.findViewById(R.id.horizontal_list_view);

        final int measureSpec = View.MeasureSpec.makeMeasureSpec(HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        horizontalListView.measure(measureSpec, measureSpec);
        horizontalListView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);

        assertThat(horizontalListView.getVisibility()).isEqualTo(View.VISIBLE);

        final int childCount = horizontalListView.getChildCount();
        assertThat(childCount).isGreaterThan(0);
    }

    @Test
    public void testSnapPositionOnScreenWithMargin() {
        final ActivityController<TestActivity> controller = Robolectric.buildActivity(TestActivity.class);
        final TestActivity testActivity = controller.get();
        testActivity.setLayoutId(R.layout.on_screen_cell_spacing);
        controller.create().start().resume().visible();

        final ListView horizontalListView = (ListView) testActivity.findViewById(R.id.horizontal_list_view);

        final int measureSpec = View.MeasureSpec.makeMeasureSpec(HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        horizontalListView.measure(measureSpec, measureSpec);
        horizontalListView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);

        final Resources resources = testActivity.getResources();
        final int viewWidth = resources.getDimensionPixelSize(R.dimen.list_item_test_width);
        final int margin = resources.getDimensionPixelSize(R.dimen.horizontal_list_view_test_margin);

        assertThat(horizontalListView.getWidth()).isEqualTo(HORIZONTAL_LIST_VIEW_WIDTH);
        assertThat(horizontalListView.getChildAt(0).getLeft()).isEqualTo(margin);
        assertThat(horizontalListView.getChildAt(0).getMeasuredWidth()).isEqualTo(viewWidth);
        assertThat(horizontalListView.getChildAt(1).getLeft()).isEqualTo(margin * 3 + viewWidth);

        final TestActivity.TestAdapter testAdapter = (TestActivity.TestAdapter) horizontalListView.getAdapter();

        testAdapter.setAdapterSize(0);
        horizontalListView.measure(measureSpec, measureSpec);
        horizontalListView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);

        assertThat(horizontalListView.getChildCount()).isEqualTo(0);

        testAdapter.setAdapterSize(1);
        horizontalListView.measure(measureSpec, measureSpec);
        horizontalListView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);

        assertThat(horizontalListView.getChildCount()).isEqualTo(1);
        assertThat(horizontalListView.getChildAt(0).getLeft()).isEqualTo(margin);
    }

    public static class TestActivity extends Activity {

        private int layoutId;

        public TestActivity() {
            super();
        }

        public void setLayoutId(int layoutId) {
            this.layoutId = layoutId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(layoutId);
            final ListView horizontalScrollView = (ListView) findViewById(R.id.horizontal_list_view);
            final TestAdapter testAdapter = new TestAdapter();
            horizontalScrollView.setAdapter(testAdapter);
        }

        public class TestAdapter extends BaseAdapter {

            private int mAdapterSize = TEST_ADAPTER_COUNT;

            public void setAdapterSize(final int adapterSize) {
                mAdapterSize = adapterSize;
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                return mAdapterSize;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(parent.getContext(), R.layout.text_view, null);
                convertView.setTag("position: " + position);
                return convertView;
            }
        }
    }
}
