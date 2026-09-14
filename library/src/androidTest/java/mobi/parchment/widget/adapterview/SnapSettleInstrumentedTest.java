// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.harness.FixedSizeAdapter;
import mobi.parchment.harness.HarnessActivity;
import mobi.parchment.harness.LaidOutChildren;
import mobi.parchment.harness.ParchmentViewHarness;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Proves on a real framework that a snap comes to rest on the start its draw limit allows. The
 * settle position is read off the children, and the expected one is derived the way the strategy
 * derives it, from the viewport and the cell the children span, so a cell the engine holds one
 * pixel short of its target fails here rather than being waved through by a tolerance.
 */
@RunWith(AndroidJUnit4.class)
public final class SnapSettleInstrumentedTest {

    private static final int VIEWPORT_WIDTH = 900;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int HALVES = 2;

    private static final int PATTERN_ITEM_COUNT = 30;
    private static final int ITEMS_PER_GROUP = 3;
    private static final int LAST_ITEM_OFFSET = ITEMS_PER_GROUP - 1;
    private static final int FIRST_GROUP_ITEM = 0;

    private static final int LIST_ITEM_COUNT = 10;
    private static final int ODD_TALL_CELL_HEIGHT = VIEWPORT_HEIGHT + 5;
    private static final int EVEN_TALL_CELL_HEIGHT = VIEWPORT_HEIGHT + 6;

    private static final int GESTURE_X = VIEWPORT_WIDTH / 2;
    private static final int DRAG_FROM_Y = 500;
    private static final int DRAG_TO_Y = 350;
    private static final int DRAG_STEPS = 8;

    @Rule
    public final ActivityScenarioRule<HarnessActivity> mActivityRule =
            new ActivityScenarioRule<>(HarnessActivity.class);

    @Test
    public void gridPatternCenterSnap_afterADrag_restsWithAGroupCentred() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                attachPattern(R.layout.instrumented_settle_pattern_center);

        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
        final LaidOutChildren after = harness.settle();

        assertTrue("no group is centred: " + after, isAnyGroupCentred(after));
    }

    @Test
    public void gridPatternEndSnap_afterADrag_restsWithAGroupAtTheEnd() {
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                attachPattern(R.layout.instrumented_settle_pattern_end);

        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
        final LaidOutChildren after = harness.settle();

        assertTrue("no group ends at the viewport's end: " + after, isAnyGroupAtTheEnd(after));
    }

    @Test
    public void listCenterSnap_withACellTallerThanTheViewportByAnOddNumberOfPixels_restsCentred() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(ODD_TALL_CELL_HEIGHT);

        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
        final LaidOutChildren after = harness.settle();

        assertTrue("no cell is centred: " + after, isAnyCellCentred(after));
    }

    @Test
    public void listCenterSnap_withACellTallerThanTheViewportByAnEvenNumberOfPixels_restsCentred() {
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                attachList(EVEN_TALL_CELL_HEIGHT);

        harness.dragAndRelease(GESTURE_X, DRAG_FROM_Y, GESTURE_X, DRAG_TO_Y, DRAG_STEPS);
        final LaidOutChildren after = harness.settle();

        assertTrue("no cell is centred: " + after, isAnyCellCentred(after));
    }

    private static int centredTop(final int viewportHeight, final int cellHeight) {
        final int freeSpace = viewportHeight - cellHeight;
        return freeSpace / HALVES;
    }

    private static boolean isAnyCellCentred(final LaidOutChildren children) {
        for (int index = 0; index < children.count(); index++) {
            final int cellHeight = children.height(index);
            final int expectedTop = centredTop(children.viewHeight(), cellHeight);
            if (children.top(index) == expectedTop) return true;
        }
        return false;
    }

    private static boolean isAnyGroupCentred(final LaidOutChildren children) {
        for (int index = 0; index < children.count(); index++) {
            if (!isAWholeDrawnGroup(children, index)) continue;
            final int groupTop = children.top(index);
            final int groupBottom = lastItemBottom(children, index);
            final int groupHeight = groupBottom - groupTop;
            final int expectedTop = centredTop(children.viewHeight(), groupHeight);
            if (groupTop == expectedTop) return true;
        }
        return false;
    }

    private static boolean isAnyGroupAtTheEnd(final LaidOutChildren children) {
        for (int index = 0; index < children.count(); index++) {
            if (!isAWholeDrawnGroup(children, index)) continue;
            final int groupBottom = lastItemBottom(children, index);
            if (groupBottom == children.viewHeight()) return true;
        }
        return false;
    }

    private static boolean isAWholeDrawnGroup(final LaidOutChildren children, final int index) {
        final int adapterPosition = children.adapterPosition(index);
        final boolean isTheGroupsFirstItem = adapterPosition % ITEMS_PER_GROUP == FIRST_GROUP_ITEM;
        if (!isTheGroupsFirstItem) return false;
        final int lastItemIndex =
                children.indexOfAdapterPosition(adapterPosition + LAST_ITEM_OFFSET);
        return lastItemIndex >= 0;
    }

    private static int lastItemBottom(final LaidOutChildren children, final int firstItemIndex) {
        final int adapterPosition = children.adapterPosition(firstItemIndex);
        final int lastItemIndex =
                children.indexOfAdapterPosition(adapterPosition + LAST_ITEM_OFFSET);
        return children.bottom(lastItemIndex);
    }

    private ParchmentViewHarness<GridPatternView<BaseAdapter>> attachPattern(final int layout) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<GridPatternView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(), layout, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        harness.apply(new AddATwoRowGroup());
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, PATTERN_ITEM_COUNT, MATCH_PARENT, MATCH_PARENT);
        harness.setAdapter(adapter);
        return harness;
    }

    private ParchmentViewHarness<ListView<BaseAdapter>> attachList(final int cellHeight) {
        final Context context = ApplicationProvider.getApplicationContext();
        final ParchmentViewHarness<ListView<BaseAdapter>> harness =
                ParchmentViewHarness.attach(
                        mActivityRule.getScenario(),
                        R.layout.instrumented_settle_list_center,
                        VIEWPORT_WIDTH,
                        VIEWPORT_HEIGHT);
        final FixedSizeAdapter adapter =
                new FixedSizeAdapter(context, LIST_ITEM_COUNT, MATCH_PARENT, cellHeight);
        harness.setAdapter(adapter);
        return harness;
    }

    /**
     * A group two rows tall: one item across the top row and two unit squares below it. The cell is
     * twice as tall as its representative view, which is the arrangement that used to snap forever.
     */
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
