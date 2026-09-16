// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.attributes;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The #59 repro on a real framework: a ListView with wrap_content across its scroll axis, inflated
 * by a real LayoutInflater into a LinearLayout above a sibling, is as tall (or as wide) as its
 * largest cell and leaves the sibling directly after it, rather than taking every pixel the parent
 * has left.
 */
@RunWith(AndroidJUnit4.class)
public final class WrapContentInstrumentedTest {

    private static final int PARENT_WIDTH = 900;
    private static final int PARENT_HEIGHT = 600;
    private static final int ITEM_WIDTH = 200;
    private static final int ITEM_HEIGHT = 100;
    private static final int ITEM_COUNT = 30;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void horizontalListWithWrapContentHeightInXml_isAsTallAsItsCells() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_wrap_content_horizontal);

        final LaidOutChildren children = harness.children();

        assertEquals(children.toString(), ITEM_HEIGHT, children.viewHeight());
        assertEquals(children.toString(), PARENT_WIDTH, children.viewWidth());
    }

    @Test
    public void horizontalListWithWrapContentHeightInXml_leavesTheSiblingDirectlyBelowIt() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_wrap_content_horizontal);

        final ReadSiblingTop readSiblingTop = new ReadSiblingTop();
        harness.apply(readSiblingTop);

        assertEquals(ITEM_HEIGHT, readSiblingTop.top());
    }

    @Test
    public void verticalListWithWrapContentWidthInXml_isAsWideAsItsCells() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_wrap_content_vertical);

        final LaidOutChildren children = harness.children();

        assertEquals(children.toString(), ITEM_WIDTH, children.viewWidth());
        assertEquals(children.toString(), PARENT_HEIGHT, children.viewHeight());
    }

    @Test
    public void verticalListWithWrapContentWidthInXml_leavesTheSiblingDirectlyBesideIt() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attach(R.layout.instrumented_wrap_content_vertical);

        final ReadSiblingLeft readSiblingLeft = new ReadSiblingLeft();
        harness.apply(readSiblingLeft);

        assertEquals(ITEM_WIDTH, readSiblingLeft.left());
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attach(final int layoutResource) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attachInsideAParent(
                        mActivityRule.getScenario(), layoutResource, PARENT_WIDTH, PARENT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, ITEM_COUNT, ITEM_WIDTH, ITEM_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }

    private static View sibling(final ListView<BaseAdapter> view) {
        final ViewGroup parent = (ViewGroup) view.getParent();
        return parent.findViewById(R.id.wrap_content_sibling);
    }

    private static final class ReadSiblingTop
            implements ParchmentViewHarness.ViewSetup<ListView<BaseAdapter>> {
        private int mTop;

        @Override
        public void setUp(final ListView<BaseAdapter> view) {
            final View sibling = sibling(view);
            mTop = sibling.getTop();
        }

        int top() {
            return mTop;
        }
    }

    private static final class ReadSiblingLeft
            implements ParchmentViewHarness.ViewSetup<ListView<BaseAdapter>> {
        private int mLeft;

        @Override
        public void setUp(final ListView<BaseAdapter> view) {
            final View sibling = sibling(view);
            mLeft = sibling.getLeft();
        }

        int left() {
            return mLeft;
        }
    }
}
