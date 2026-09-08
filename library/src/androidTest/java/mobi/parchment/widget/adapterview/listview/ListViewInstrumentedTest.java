// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.listview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import mobi.parchment.test.R;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Smoke test on a real framework: inflates a Parchment ListView from XML (so the custom attribute
 * parsing runs for real), measures and lays it out on the main thread, and checks that views were
 * pulled from the adapter and positioned. Robolectric covers the layout maths; this guards the
 * device-side inflation and measurement path that Robolectric cannot fully reproduce.
 */
@RunWith(AndroidJUnit4.class)
public class ListViewInstrumentedTest {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 300;
    private static final int ITEM_WIDTH = 200;
    private static final int ITEM_COUNT = 50;

    @Test
    public void inflatedHorizontalListView_laysOutOnlyVisibleChildren() {
        final Context context = ApplicationProvider.getApplicationContext();
        final View inflated =
                LayoutInflater.from(context).inflate(R.layout.list_view_horizontal, null);
        assertNotNull(inflated);

        @SuppressWarnings("unchecked")
        final ListView<BaseAdapter> listView = (ListView<BaseAdapter>) inflated;

        InstrumentationRegistry.getInstrumentation()
                .runOnMainSync(
                        () -> {
                            listView.setAdapter(new FixedWidthAdapter(context));
                            final int widthSpec =
                                    View.MeasureSpec.makeMeasureSpec(
                                            WIDTH, View.MeasureSpec.EXACTLY);
                            final int heightSpec =
                                    View.MeasureSpec.makeMeasureSpec(
                                            HEIGHT, View.MeasureSpec.EXACTLY);
                            listView.measure(widthSpec, heightSpec);
                            listView.layout(0, 0, WIDTH, HEIGHT);
                        });

        final int childCount = listView.getChildCount();
        assertTrue("expected some children, got " + childCount, childCount > 0);
        assertTrue(
                "recycling should keep the child count near the viewport, got " + childCount,
                childCount < ITEM_COUNT);
        assertEquals(0, listView.getChildAt(0).getLeft());
        assertEquals(ITEM_WIDTH, listView.getChildAt(0).getWidth());
        assertEquals(0, listView.getPositionForView(listView.getChildAt(0)));
    }

    /** Fifty fixed-width items so the viewport can only ever show a handful of them. */
    private static final class FixedWidthAdapter extends BaseAdapter {
        private final Context context;

        FixedWidthAdapter(final Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
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
            final TextView view =
                    convertView instanceof TextView
                            ? (TextView) convertView
                            : new TextView(context);
            view.setText(String.valueOf(position));
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(ITEM_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }
}
