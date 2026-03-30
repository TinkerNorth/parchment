package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import androidx.test.core.app.ApplicationProvider;

import mobi.parchment.R;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by emir on 22/03/14.
 */
@RunWith(RobolectricTestRunner.class)
public class AbstractAdapterViewManagerTest {

    private AdapterViewManager mAdapterViewManager;
    private Adapter mAdapter = new MeasuringAdapter();
    private ViewGroup mViewGroup;

    @Before
    public void setup(){
        mAdapterViewManager = new AdapterViewManager();
        mAdapterViewManager.setAdapter(mAdapter);
        final Context context = ApplicationProvider.getApplicationContext();
        mViewGroup = new ViewGroup(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        doFirstLayout(1000,1000);
    }

    public class MeasuringAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = ApplicationProvider.getApplicationContext();
            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return imageView;
        }
    }

    @Test
    public void MeasuringTest() {
        final View view = mAdapterViewManager.getView(mViewGroup,0,100,100);

//        assertThat(view.getGroupHeight()).isEqualTo(100);
//        assertThat(view.getGroupWidth()).isEqualTo(100);

    }

    private void doFirstLayout(int viewGroupWidth, int viewGroupHeight) {
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewGroupWidth, View.MeasureSpec.EXACTLY);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewGroupHeight, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(widthMeasureSpec, heightMeasureSpec);
        mViewGroup.layout(0, 0, viewGroupWidth, viewGroupHeight);
    }
}
