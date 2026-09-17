// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mobi.parchment.harness.AttachScrollListener;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.harness.RecordingScrollListener;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves on a real framework that smoothScrollToPosition seeks a cell that is not drawn and lands
 * it on its snap point, reading the rest position off the children the way
 * SnapSettleInstrumentedTest does.
 */
@RunWith(AndroidJUnit4.class)
public final class SmoothScrollToPositionInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int HALVES = 2;

    private static final int LIST_ITEM_COUNT = 10;
    private static final int LIST_CELL_HEIGHT = 200;
    private static final int AN_OFF_SCREEN_CELL = 7;

    private static final int PATTERN_ITEM_COUNT = 30;
    private static final int ITEMS_PER_GROUP = 3;
    private static final int LAST_ITEM_OFFSET = ITEMS_PER_GROUP - 1;
    private static final int A_LATER_GROUP = 5;
    private static final int FIRST_ITEM_OF_THE_LATER_GROUP = A_LATER_GROUP * ITEMS_PER_GROUP;

    private static final List<ScrollState> SETTLE_REST =
            Arrays.asList(ScrollState.settling, ScrollState.idle);

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void listCenterSnap_smoothScrollToAnOffScreenCell_restsWithItCentred() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_settle_list_center);
        final LaidOutChildren before = harness.children();
        assertTrue(
                "the target is already drawn: " + before, isNotDrawn(before, AN_OFF_SCREEN_CELL));

        harness.smoothScrollToPosition(AN_OFF_SCREEN_CELL);
        final LaidOutChildren after = harness.settle();

        final int targetIndex = after.indexOfAdapterPosition(AN_OFF_SCREEN_CELL);
        final int expectedTop = centredTop(after.viewHeight(), LIST_CELL_HEIGHT);
        assertEquals("the target is not centred: " + after, expectedTop, after.top(targetIndex));
    }

    @Test
    public void listCenterSnap_smoothScrollToAnOffScreenCell_reportsSettlingThenIdle() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_settle_list_center);
        final RecordingScrollListener listener = new RecordingScrollListener();
        harness.apply(new AttachScrollListener<ListView<BaseAdapter>>(listener));

        harness.smoothScrollToPosition(AN_OFF_SCREEN_CELL);
        harness.settle();

        assertEquals(listener.toString(), SETTLE_REST, listener.states());
    }

    @Test
    public void gridPatternCenterSnap_smoothScrollToALaterGroup_restsWithItCentred() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness = attachPattern();

        harness.smoothScrollToPosition(FIRST_ITEM_OF_THE_LATER_GROUP);
        final LaidOutChildren after = harness.settle();

        final int firstItemIndex = after.indexOfAdapterPosition(FIRST_ITEM_OF_THE_LATER_GROUP);
        final int groupTop = after.top(firstItemIndex);
        final int groupBottom = lastItemBottom(after, firstItemIndex);
        final int groupHeight = groupBottom - groupTop;
        final int expectedTop = centredTop(after.viewHeight(), groupHeight);
        assertEquals("the group is not centred: " + after, expectedTop, groupTop);
    }

    @Test
    public void onScreen_smoothScrollToACellPastTheEnd_restsWithItsEndAtTheViewEnd() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(R.layout.instrumented_snap_on_screen);

        harness.smoothScrollToPosition(AN_OFF_SCREEN_CELL);
        final LaidOutChildren after = harness.settle();

        final int targetIndex = after.indexOfAdapterPosition(AN_OFF_SCREEN_CELL);
        assertEquals(
                "the target does not end at the view's end: " + after,
                after.viewHeight(),
                after.bottom(targetIndex));
    }

    private static boolean isNotDrawn(final LaidOutChildren children, final int adapterPosition) {
        final int index = children.indexOfAdapterPosition(adapterPosition);
        return index < 0;
    }

    private static int centredTop(final int viewportHeight, final int cellHeight) {
        final int freeSpace = viewportHeight - cellHeight;
        return freeSpace / HALVES;
    }

    private static int lastItemBottom(final LaidOutChildren children, final int firstItemIndex) {
        final int adapterPosition = children.adapterPosition(firstItemIndex);
        final int lastItemIndex =
                children.indexOfAdapterPosition(adapterPosition + LAST_ITEM_OFFSET);
        return children.bottom(lastItemIndex);
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(final int layout) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(), layout, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, LIST_ITEM_COUNT, MATCH_PARENT, LIST_CELL_HEIGHT);
        harness.setAdapter(adapter);
        return harness;
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachPattern() {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_settle_pattern_center,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        harness.apply(new AddATwoRowGroup());
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, PATTERN_ITEM_COUNT, MATCH_PARENT, MATCH_PARENT);
        harness.setAdapter(adapter);
        return harness;
    }

    /** A group two rows tall: one item across the top row and two unit squares below it. */
    private static final class AddATwoRowGroup
            implements ParchmentViewHarness.ViewSetup<GridPatternView<BaseAdapter>> {

        private static final int TOP_ROW = 0;
        private static final int BOTTOM_ROW = 1;
        private static final int LEFT_COLUMN = 0;
        private static final int RIGHT_COLUMN = 1;
        private static final int ONE_UNIT = 1;
        private static final int TWO_UNITS = 2;

        @Override
        public void setUp(final GridPatternView<BaseAdapter> view) {
            final List<GridPatternItemDefinition> definitions = new ArrayList<>();
            definitions.add(
                    new GridPatternItemDefinition(TOP_ROW, LEFT_COLUMN, ONE_UNIT, TWO_UNITS));
            definitions.add(
                    new GridPatternItemDefinition(BOTTOM_ROW, LEFT_COLUMN, ONE_UNIT, ONE_UNIT));
            definitions.add(
                    new GridPatternItemDefinition(BOTTOM_ROW, RIGHT_COLUMN, ONE_UNIT, ONE_UNIT));
            view.addGridPatternGroupDefinition(definitions);
        }
    }
}
