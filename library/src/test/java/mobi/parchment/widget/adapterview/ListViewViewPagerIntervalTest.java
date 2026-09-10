// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import androidx.test.core.app.ApplicationProvider;
import java.io.IOException;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * ListView reads parchment_viewPagerInterval and hands it to LayoutManagerAttributes through a
 * nine-argument positional constructor, so these drive the value from real inflated XML rather than
 * from a hand-built LayoutManagerAttributes.
 */
@RunWith(RobolectricTestRunner.class)
public class ListViewViewPagerIntervalTest {

    private static final int VIEWPORT_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int ADAPTER_SIZE = 10;
    private static final int TWO_CELLS_PER_GESTURE = 2;
    private static final int START_OF_THE_VIEWPORT = 0;
    private static final int NOT_DRAWN = Integer.MIN_VALUE;
    private static final int NO_ID_RESOURCE = 0;
    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String ID_ATTRIBUTE = "id";

    @Test
    public void anIntervalOfTwoInXml_makesOneGestureAdvanceTwoCells() {
        final PagingListView listView = pagingListViewFrom(R.id.view_pager_interval_two);

        assertThat(listView.pageDistance(Move.forward))
                .isEqualTo(-TWO_CELLS_PER_GESTURE * CELL_SIZE);

        listView.page(Move.forward);

        assertThat(listView.startOf(2)).isEqualTo(START_OF_THE_VIEWPORT);
        assertThat(listView.startOf(0)).isEqualTo(NOT_DRAWN);
    }

    @Test
    public void noIntervalInXml_makesOneGestureAdvanceOneCell() {
        final PagingListView listView = pagingListViewFrom(R.id.view_pager_no_interval);

        assertThat(listView.pageDistance(Move.forward)).isEqualTo(-CELL_SIZE);

        listView.page(Move.forward);

        assertThat(listView.startOf(1)).isEqualTo(START_OF_THE_VIEWPORT);
    }

    private static PagingListView pagingListViewFrom(final int idResource) {
        final Context context = RuntimeEnvironment.getApplication();
        final XmlResourceParser parser =
                context.getResources().getLayout(R.layout.attribute_parsing_view_pager_intervals);
        try {
            final AttributeSet attributeSet = advanceToTagWithId(parser, idResource);
            final PagingListView listView = new PagingListView(context, attributeSet);
            listView.start(context, attributeSet);
            return listView;
        } finally {
            parser.close();
        }
    }

    private static AttributeSet advanceToTagWithId(
            final XmlResourceParser parser, final int idResource) {
        try {
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    final int tagId =
                            parser.getAttributeResourceValue(
                                    ANDROID_NAMESPACE, ID_ATTRIBUTE, NO_ID_RESOURCE);
                    if (tagId == idResource) {
                        return parser;
                    }
                }
                event = parser.next();
            }
        } catch (final XmlPullParserException | IOException exception) {
            throw new IllegalStateException("could not read the layout", exception);
        }
        throw new IllegalStateException("the layout has no tag with the requested id");
    }

    private static final class PagingListView extends ListView<BaseAdapter> {
        private final Animation mAnimation = new Animation();
        private LayoutManager<View> mPagingLayoutManager;

        private PagingListView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        private void start(final Context context, final AttributeSet attributeSet) {
            final AdapterViewInitializer<View> initializer =
                    getAdapterViewInitializer(context, attributeSet);
            mPagingLayoutManager = initializer.getLayoutManager();
            initializer.getAdapterViewManager().setAdapter(new CellAdapter());

            final int measureSpec =
                    View.MeasureSpec.makeMeasureSpec(VIEWPORT_SIZE, View.MeasureSpec.EXACTLY);
            measure(measureSpec, measureSpec);
            layout(0, 0, VIEWPORT_SIZE, VIEWPORT_SIZE);

            layoutOnce();
            mAnimation.newAnimation();
            layoutOnce();
        }

        private void layoutOnce() {
            mPagingLayoutManager.layout(this, mAnimation, 0, 0, VIEWPORT_SIZE, VIEWPORT_SIZE);
        }

        private int pageDistance(final Move move) {
            return mPagingLayoutManager.getViewPagerScrollDistance(move);
        }

        private void page(final Move move) {
            mAnimation.setDisplacement(pageDistance(move));
            layoutOnce();
        }

        private int startOf(final int adapterPosition) {
            for (int index = 0; index < getChildCount(); index++) {
                final View view = getChildAt(index);
                final Object tag = view.getTag();
                final boolean isTheWantedView = tag.equals(Integer.valueOf(adapterPosition));
                if (isTheWantedView) return view.getLeft();
            }
            return NOT_DRAWN;
        }
    }

    private static final class CellAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ADAPTER_SIZE;
        }

        @Override
        public Object getItem(final int position) {
            return position;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final FrameLayout view = new FrameLayout(ApplicationProvider.getApplicationContext());
            view.setTag(position);
            view.setLayoutParams(new ViewGroup.LayoutParams(CELL_SIZE, CELL_SIZE));
            return view;
        }
    }
}
