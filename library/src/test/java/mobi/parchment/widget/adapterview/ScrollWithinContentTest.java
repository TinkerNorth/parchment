// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ScrollWithinContentTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int VIEW_SIZE = 100;
    private static final int WIDE_VIEW_SIZE = 120;
    private static final int UNEVEN_VIEW_SIZE = 110;
    private static final int OVERSIZED_VIEW_SIZE = 500;
    private static final int CELL_SPACING = 0;
    private static final int ADAPTER_SIZE = 10;
    private static final int SHORT_ADAPTER_SIZE = 2;
    private static final int OVERSIZED_ADAPTER_SIZE = 4;
    private static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final int THIRD_POSITION = 2;
    private static final int LAST_POSITION = ADAPTER_SIZE - 1;
    private static final int SECOND_LAST_POSITION = LAST_POSITION - 1;
    private static final int THIRD_LAST_POSITION = LAST_POSITION - 2;
    private static final int LAST_OVERSIZED_POSITION = OVERSIZED_ADAPTER_SIZE - 1;
    private static final int NO_PADDING = 0;
    private static final int START_PADDING = 10;
    private static final int END_PADDING = 30;
    private static final int PADDED_VIEW_END = VIEW_GROUP_SIZE - END_PADDING;
    private static final int FRAMES_TO_THE_CONTENT_END = 14;
    private static final int FRAMES_SHORT_OF_THE_CONTENT_END = 13;
    private static final int FRAMES_TO_THE_UNEVEN_CONTENT_END = 16;
    private static final int FRAMES_TO_THE_LAST_CELL = 17;
    private static final int FRAME_DISPLACEMENT = -50;
    private static final int PAST_THE_CONTENT_END = -60;
    private static final int PAST_THE_CONTENT_START = 70;
    private static final int HALF_A_CELL_PAST_THE_CONTENT_END = -80;
    private static final int PAST_THE_LAST_CELL = -120;
    private static final int A_WHOLE_VIEW_AND_MORE = -500;
    private static final int PAST_EVERY_CELL_FORWARD = -5000;
    private static final int PAST_EVERY_CELL_BACK = 5000;
    private static final int FROM_THE_MIDDLE_PAST_THE_END = -350;
    private static final int UNEVEN_CELL_START_SHORT_OF_THE_CONTENT_END = -30;
    private static final int OVERSIZED_CELL_BACK_TO_ITS_START =
            OVERSIZED_VIEW_SIZE - VIEW_GROUP_SIZE;
    private static final int OVERSIZED_CELL_BACK_TO_ITS_END = VIEW_GROUP_SIZE - OVERSIZED_VIEW_SIZE;
    private static final int CENTRED_UNEVEN_CELL_START = (VIEW_GROUP_SIZE - UNEVEN_VIEW_SIZE) / 2;
    private static final int SIXTH_POSITION = 5;
    private static final int CENTRED_CELL_START = (VIEW_GROUP_SIZE - VIEW_SIZE) / 2;
    private static final int CENTRED_SHORT_CONTENT_START = (VIEW_GROUP_SIZE - 2 * VIEW_SIZE) / 2;
    private static final int CENTRED_SHORT_CONTENT_END =
            VIEW_GROUP_SIZE - CENTRED_SHORT_CONTENT_START;
    private static final int NO_MOVEMENT = 0;
    private static final int NOTHING_SELECTED = -1;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean CIRCULAR = true;
    private static final boolean SNAP_TO_POSITION = true;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final int VIEWPORT_PAGING = 0;
    private static final boolean SCROLL_WITHIN_CONTENT = true;
    private static final boolean SCROLL_PAST_CONTENT = false;
    private static final boolean SELECT_ON_SNAP = true;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final boolean HORIZONTAL = false;
    private static final boolean VERTICAL = true;

    private final MyViewGroup mViewGroup =
            new MyViewGroup(ApplicationProvider.getApplicationContext());
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final CountingAnimationStoppedListener mStoppedListener =
            new CountingAnimationStoppedListener();
    private final TestAdapter mTestAdapter = new TestAdapter();
    private final Animation mAnimation = new Animation();
    private ListLayoutManager mListLayoutManager;
    private boolean mIsVertical;

    @Test
    public void startSnap_scrollWithinContent_lastCellPulledPastTheEnd_isHeldAtTheEnd() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);
        assertThat(mStoppedListener.mCount).isEqualTo(0);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
        assertThat(start(THIRD_LAST_POSITION)).isEqualTo(0);
    }

    @Test
    public void startSnap_scrollWithinContent_firstCellPulledPastTheStart_isHeldAtTheStart() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void startSnap_scrollWithinContent_contentShorterThanTheView_sitsAtTheStart() {
        setup(
                SnapPosition.start,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);

        assertThat(start(FIRST_POSITION)).isEqualTo(START_PADDING);
        assertThat(start(SECOND_POSITION)).isEqualTo(START_PADDING + VIEW_SIZE);
    }

    @Test
    public void startSnap_scrollWithinContent_contentShorterThanTheView_doesNotScroll() {
        setup(
                SnapPosition.start,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);

        scrollBy(PAST_THE_CONTENT_END);
        assertThat(start(FIRST_POSITION)).isEqualTo(START_PADDING);

        scrollBy(PAST_THE_CONTENT_START);
        assertThat(start(FIRST_POSITION)).isEqualTo(START_PADDING);
    }

    @Test
    public void startSnap_scrollWithinContent_withPadding_holdsTheLastCellAtThePaddedEnd() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT, ADAPTER_SIZE, START_PADDING, END_PADDING);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(PADDED_VIEW_END);
    }

    @Test
    public void startSnap_scrollWithinContent_scrollingVertically_holdsTheLastCellAtTheBottom() {
        setup(
                SnapPosition.start,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                NO_SELECT_ON_SNAP,
                VERTICAL);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void startSnap_scrollWithinContent_heldAtTheEnd_asksForNoFurtherSnap() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);
        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void startSnap_scrollWithinContent_heldAtTheEndBetweenCellStarts_restsAtTheEnd() {
        setupWithUnevenCells(SnapPosition.start, NO_SELECT_ON_SNAP);
        scrollInFrames(FRAMES_TO_THE_UNEVEN_CONTENT_END, FRAME_DISPLACEMENT);
        scrollBy(PAST_THE_CONTENT_END);
        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(THIRD_LAST_POSITION))
                .isEqualTo(UNEVEN_CELL_START_SHORT_OF_THE_CONTENT_END);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void
            startSnap_scrollWithinContent_heldAtTheEndBetweenCellStarts_selectsTheCellNearestTheStartThatRestsThere() {
        setupWithUnevenCells(SnapPosition.start, SELECT_ON_SNAP);
        scrollInFrames(FRAMES_TO_THE_UNEVEN_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(SECOND_LAST_POSITION);
    }

    @Test
    public void startSnap_scrollWithinContent_heldAtTheEnd_selectsTheCellAtTheStart() {
        setup(
                SnapPosition.start,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);
        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(NOTHING_SELECTED);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(THIRD_LAST_POSITION);
    }

    @Test
    public void startSnap_scrollWithinContent_setSelectionFromTheEndBound_putsTheCellAtTheStart() {
        setupWithUnevenCells(SnapPosition.start, NO_SELECT_ON_SNAP);
        scrollInFrames(FRAMES_TO_THE_UNEVEN_CONTENT_END, FRAME_DISPLACEMENT);
        scrollBy(PAST_THE_CONTENT_END);
        assertThat(start(THIRD_LAST_POSITION))
                .isEqualTo(UNEVEN_CELL_START_SHORT_OF_THE_CONTENT_END);

        mListLayoutManager.setSelected(THIRD_LAST_POSITION, mViewGroup);
        scrollBy(NO_MOVEMENT);

        assertThat(start(THIRD_LAST_POSITION)).isEqualTo(0);
        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void startSnap_setSelectionFromARest_putsTheCellAtTheStart() {
        setup(SnapPosition.start, SCROLL_PAST_CONTENT);

        mListLayoutManager.setSelected(SIXTH_POSITION, mViewGroup);
        scrollBy(NO_MOVEMENT);

        assertThat(start(SIXTH_POSITION)).isEqualTo(0);
        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void startSnap_scrollWithinContent_lastCellLargerThanTheView_itsEndIsReachable() {
        setupWithAnOversizedCell(SnapPosition.start, LAST_OVERSIZED_POSITION);

        scrollBy(PAST_EVERY_CELL_FORWARD);

        assertThat(end(LAST_OVERSIZED_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void
            startSnap_scrollWithinContent_lastCellLargerThanTheView_releasedAtItsEnd_restsAtItsStart() {
        setupWithAnOversizedCell(SnapPosition.start, LAST_OVERSIZED_POSITION);
        scrollBy(PAST_EVERY_CELL_FORWARD);

        final int snapDistance = mListLayoutManager.snapTo(mViewGroup);

        assertThat(snapDistance).isEqualTo(OVERSIZED_CELL_BACK_TO_ITS_START);
        scrollBy(snapDistance);
        assertThat(start(LAST_OVERSIZED_POSITION)).isEqualTo(0);
    }

    @Test
    public void
            startSnap_scrollWithinContent_everyCellScrolledOffTheEnd_resetsWithTheLastCellAtTheEnd() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT);

        scrollBy(PAST_EVERY_CELL_FORWARD);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
        assertThat(start(THIRD_LAST_POSITION)).isEqualTo(0);
    }

    @Test
    public void
            startSnap_scrollWithinContent_everyCellScrolledOffTheStart_resetsWithTheFirstCellAtTheStart() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(PAST_EVERY_CELL_BACK);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
        assertThat(end(THIRD_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void endSnap_scrollWithinContent_firstLayout_putsTheContentAtTheStart() {
        setup(SnapPosition.end, SCROLL_WITHIN_CONTENT);

        assertThat(start(FIRST_POSITION)).isEqualTo(0);
        assertThat(end(THIRD_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void endSnap_scrollWithinContent_firstCellPulledPastTheStart_isHeldAtTheStart() {
        setup(SnapPosition.end, SCROLL_WITHIN_CONTENT);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void endSnap_scrollWithinContent_lastCellPulledPastTheEnd_isHeldAtTheEnd() {
        setup(SnapPosition.end, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);
        assertThat(mStoppedListener.mCount).isEqualTo(0);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void endSnap_scrollWithinContent_contentShorterThanTheView_sitsAtTheEnd() {
        setup(
                SnapPosition.end,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);

        assertThat(end(SECOND_POSITION)).isEqualTo(PADDED_VIEW_END);
        assertThat(start(FIRST_POSITION)).isEqualTo(PADDED_VIEW_END - 2 * VIEW_SIZE);
    }

    @Test
    public void endSnap_scrollWithinContent_contentShorterThanTheView_doesNotScroll() {
        setup(
                SnapPosition.end,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);

        scrollBy(PAST_THE_CONTENT_END);
        assertThat(end(SECOND_POSITION)).isEqualTo(PADDED_VIEW_END);

        scrollBy(PAST_THE_CONTENT_START);
        assertThat(end(SECOND_POSITION)).isEqualTo(PADDED_VIEW_END);
    }

    @Test
    public void endSnap_scrollWithinContent_contentShorterThanTheView_selectsOnTheFirstLayout() {
        setup(
                SnapPosition.end,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(SECOND_POSITION);
    }

    @Test
    public void
            endSnap_withoutScrollWithinContent_contentShorterThanTheView_selectsNothingOnTheFirstLayout() {
        setup(
                SnapPosition.end,
                SCROLL_PAST_CONTENT,
                SHORT_ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(NOTHING_SELECTED);
    }

    @Test
    public void endSnap_scrollWithinContent_withPadding_holdsTheFirstCellAtThePaddedStart() {
        setup(SnapPosition.end, SCROLL_WITHIN_CONTENT, ADAPTER_SIZE, START_PADDING, END_PADDING);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(FIRST_POSITION)).isEqualTo(START_PADDING);
    }

    @Test
    public void endSnap_scrollWithinContent_scrollingVertically_holdsTheFirstCellAtTheTop() {
        setup(
                SnapPosition.end,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                NO_SELECT_ON_SNAP,
                VERTICAL);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void endSnap_scrollWithinContent_heldAtTheStart_asksForNoFurtherSnap() {
        setup(SnapPosition.end, SCROLL_WITHIN_CONTENT);
        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void endSnap_scrollWithinContent_nearestSnapPointBeyondTheStart_restsAtTheStart() {
        setup(
                SnapPosition.end,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                WIDE_VIEW_SIZE,
                NO_SELECT_ON_SNAP,
                HORIZONTAL);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
        assertThat(start(SECOND_POSITION)).isEqualTo(WIDE_VIEW_SIZE);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void endSnap_scrollWithinContent_cellsThatDoNotTileTheView_firstLayoutAsksForNoSnap() {
        setupWithUnevenCells(SnapPosition.end, NO_SELECT_ON_SNAP);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void
            endSnap_scrollWithinContent_heldAtTheStartBetweenCellEnds_selectsTheCellNearestTheEndThatRestsThere() {
        setupWithUnevenCells(SnapPosition.end, SELECT_ON_SNAP);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(SECOND_POSITION);
    }

    @Test
    public void endSnap_scrollWithinContent_heldAtTheStart_selectsTheCellAtTheEnd() {
        setup(
                SnapPosition.end,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);
        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(NOTHING_SELECTED);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(THIRD_POSITION);
    }

    @Test
    public void
            endSnap_scrollWithinContent_setSelectionFromTheStartBound_putsTheCellsEndAtTheEnd() {
        setupWithUnevenCells(SnapPosition.end, NO_SELECT_ON_SNAP);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);

        mListLayoutManager.setSelected(THIRD_POSITION, mViewGroup);
        scrollBy(NO_MOVEMENT);

        assertThat(end(THIRD_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void endSnap_scrollWithinContent_firstCellLargerThanTheView_itsStartIsReachable() {
        setupWithAnOversizedCell(SnapPosition.end, FIRST_POSITION);

        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void
            endSnap_scrollWithinContent_firstCellLargerThanTheView_releasedAtItsStart_restsAtItsEnd() {
        setupWithAnOversizedCell(SnapPosition.end, FIRST_POSITION);

        final int snapDistance = mListLayoutManager.snapTo(mViewGroup);

        assertThat(snapDistance).isEqualTo(OVERSIZED_CELL_BACK_TO_ITS_END);
        scrollBy(snapDistance);
        assertThat(end(FIRST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void centerSnap_scrollWithinContent_firstLayout_putsTheContentAtTheStart() {
        setup(SnapPosition.center, SCROLL_WITHIN_CONTENT);

        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void centerSnap_scrollWithinContent_lastCellPulledPastTheEnd_isHeldAtTheEnd() {
        setup(SnapPosition.center, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);
        assertThat(mStoppedListener.mCount).isEqualTo(0);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
    }

    @Test
    public void centerSnap_scrollWithinContent_firstCellPulledPastTheStart_isHeldAtTheStart() {
        setup(SnapPosition.center, SCROLL_WITHIN_CONTENT);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void centerSnap_scrollWithinContent_contentShorterThanTheView_sitsCentred() {
        setup(
                SnapPosition.center,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);
        final int sizeInsidePadding = VIEW_GROUP_SIZE - START_PADDING - END_PADDING;
        final int contentSize = SHORT_ADAPTER_SIZE * VIEW_SIZE;
        final int centredContentStart = START_PADDING + (sizeInsidePadding - contentSize) / 2;

        assertThat(start(FIRST_POSITION)).isEqualTo(centredContentStart);
        assertThat(end(SECOND_POSITION)).isEqualTo(centredContentStart + contentSize);
    }

    @Test
    public void centerSnap_scrollWithinContent_contentShorterThanTheView_doesNotScroll() {
        setup(
                SnapPosition.center,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);
        final int contentStartBefore = start(FIRST_POSITION);

        scrollBy(PAST_THE_CONTENT_END);
        assertThat(start(FIRST_POSITION)).isEqualTo(contentStartBefore);

        scrollBy(PAST_THE_CONTENT_START);
        assertThat(start(FIRST_POSITION)).isEqualTo(contentStartBefore);
    }

    @Test
    public void centerSnap_scrollWithinContent_contentShorterThanTheView_asksForNoSnap() {
        setup(
                SnapPosition.center,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                START_PADDING,
                END_PADDING);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void centerSnap_scrollWithinContent_heldAtTheEnd_asksForNoFurtherSnap() {
        setup(SnapPosition.center, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);
        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void
            centerSnap_scrollWithinContent_cellsThatDoNotTileTheView_firstLayoutAsksForNoSnap() {
        setupWithUnevenCells(SnapPosition.center, NO_SELECT_ON_SNAP);

        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void centerSnap_scrollWithinContent_heldAtTheEnd_selectsTheCellAtTheCentre() {
        setup(
                SnapPosition.center,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(SECOND_LAST_POSITION);
    }

    @Test
    public void centerSnap_scrollWithinContent_setSelectionFromTheEndBound_centresTheCell() {
        setupWithUnevenCells(SnapPosition.center, NO_SELECT_ON_SNAP);
        scrollInFrames(FRAMES_TO_THE_UNEVEN_CONTENT_END, FRAME_DISPLACEMENT);
        scrollBy(PAST_THE_CONTENT_END);
        assertThat(start(THIRD_LAST_POSITION))
                .isEqualTo(UNEVEN_CELL_START_SHORT_OF_THE_CONTENT_END);

        mListLayoutManager.setSelected(THIRD_LAST_POSITION, mViewGroup);
        scrollBy(NO_MOVEMENT);

        assertThat(start(THIRD_LAST_POSITION)).isEqualTo(CENTRED_UNEVEN_CELL_START);
        assertThat(mListLayoutManager.snapTo(mViewGroup)).isEqualTo(NO_MOVEMENT);
    }

    @Test
    public void centerSnap_scrollWithinContent_withPadding_holdsTheLastCellAtThePaddedEnd() {
        setup(SnapPosition.center, SCROLL_WITHIN_CONTENT, ADAPTER_SIZE, START_PADDING, END_PADDING);
        scrollInFrames(FRAMES_TO_THE_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(PAST_THE_CONTENT_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(PADDED_VIEW_END);
    }

    @Test
    public void frameDisplacement_scrollWithinContent_forAFrameHeldAtTheEnd_isOnlyTheMovement() {
        setup(SnapPosition.start, SCROLL_WITHIN_CONTENT);
        scrollInFrames(FRAMES_SHORT_OF_THE_CONTENT_END, FRAME_DISPLACEMENT);

        scrollBy(HALF_A_CELL_PAST_THE_CONTENT_END);

        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
        assertThat(mListLayoutManager.getFrameDisplacement()).isEqualTo(FRAME_DISPLACEMENT);
    }

    @Test
    public void circularScroll_scrollWithinContent_isNeverCorrected() {
        setup(
                SnapPosition.start,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                CIRCULAR);

        scrollBy(A_WHOLE_VIEW_AND_MORE);
        scrollBy(A_WHOLE_VIEW_AND_MORE);

        assertThat(mStoppedListener.mCount).isEqualTo(0);
        assertThat(start(FIRST_POSITION)).isEqualTo(0);
    }

    @Test
    public void onScreen_scrollWithinContent_contentThatFits_isStillCentred() {
        setup(
                SnapPosition.onScreen,
                SCROLL_WITHIN_CONTENT,
                SHORT_ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING);

        assertThat(start(FIRST_POSITION)).isEqualTo(CENTRED_SHORT_CONTENT_START);
        assertThat(end(SECOND_POSITION)).isEqualTo(CENTRED_SHORT_CONTENT_END);
    }

    @Test
    public void onScreen_scrollWithinContent_frameOvershootingTheEnd_isStillCorrectedToTheEnd() {
        setup(SnapPosition.onScreen, SCROLL_WITHIN_CONTENT);
        scrollBy(A_WHOLE_VIEW_AND_MORE);

        scrollBy(FROM_THE_MIDDLE_PAST_THE_END);

        assertThat(mStoppedListener.mCount).isEqualTo(1);
        assertThat(end(LAST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);
        assertThat(start(THIRD_LAST_POSITION)).isEqualTo(0);
    }

    @Test
    public void onScreen_heldAtTheEnd_stillSelectsTheLastCell() {
        setup(
                SnapPosition.onScreen,
                SCROLL_PAST_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);
        scrollBy(A_WHOLE_VIEW_AND_MORE);

        scrollBy(FROM_THE_MIDDLE_PAST_THE_END);

        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(LAST_POSITION);
    }

    @Test
    public void centerSnap_lastCellHeldAtTheCentre_stillSelectsTheLastCell() {
        setup(
                SnapPosition.center,
                SCROLL_PAST_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);
        scrollInFrames(FRAMES_TO_THE_LAST_CELL, FRAME_DISPLACEMENT);

        scrollBy(PAST_THE_LAST_CELL);

        assertThat(start(LAST_POSITION)).isEqualTo(CENTRED_CELL_START);
        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(LAST_POSITION);
    }

    @Test
    public void centerSnap_firstCellHeldAtTheCentre_stillSelectsTheFirstCell() {
        setup(
                SnapPosition.center,
                SCROLL_PAST_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                SELECT_ON_SNAP,
                HORIZONTAL);

        scrollBy(PAST_THE_CONTENT_START);

        assertThat(start(FIRST_POSITION)).isEqualTo(CENTRED_CELL_START);
        assertThat(mListLayoutManager.getSelectedPosition()).isEqualTo(FIRST_POSITION);
    }

    private void setupWithUnevenCells(final SnapPosition snapPosition, final boolean selectOnSnap) {
        setup(
                snapPosition,
                SCROLL_WITHIN_CONTENT,
                ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                UNEVEN_VIEW_SIZE,
                selectOnSnap,
                HORIZONTAL);
    }

    private void setupWithAnOversizedCell(
            final SnapPosition snapPosition, final int oversizedPosition) {
        mTestAdapter.setOversizedView(oversizedPosition, OVERSIZED_VIEW_SIZE);
        setup(
                snapPosition,
                SCROLL_WITHIN_CONTENT,
                OVERSIZED_ADAPTER_SIZE,
                NO_PADDING,
                NO_PADDING,
                VIEW_SIZE,
                NO_SELECT_ON_SNAP,
                HORIZONTAL);
    }

    private void setup(final SnapPosition snapPosition, final boolean scrollWithinContent) {
        setup(snapPosition, scrollWithinContent, ADAPTER_SIZE, NO_PADDING, NO_PADDING);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean scrollWithinContent,
            final int adapterSize,
            final int startPadding,
            final int endPadding) {
        setup(
                snapPosition,
                scrollWithinContent,
                adapterSize,
                startPadding,
                endPadding,
                VIEW_SIZE,
                NO_SELECT_ON_SNAP,
                HORIZONTAL);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean scrollWithinContent,
            final int adapterSize,
            final int startPadding,
            final int endPadding,
            final boolean isCircularScroll) {
        setup(
                snapPosition,
                scrollWithinContent,
                isCircularScroll,
                adapterSize,
                startPadding,
                endPadding,
                VIEW_SIZE,
                NO_SELECT_ON_SNAP,
                HORIZONTAL);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean scrollWithinContent,
            final int adapterSize,
            final int startPadding,
            final int endPadding,
            final int viewSize,
            final boolean selectOnSnap,
            final boolean isVertical) {
        setup(
                snapPosition,
                scrollWithinContent,
                NOT_CIRCULAR,
                adapterSize,
                startPadding,
                endPadding,
                viewSize,
                selectOnSnap,
                isVertical);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean scrollWithinContent,
            final boolean isCircularScroll,
            final int adapterSize,
            final int startPadding,
            final int endPadding,
            final int viewSize,
            final boolean selectOnSnap,
            final boolean isVertical) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        isCircularScroll,
                        SNAP_TO_POSITION,
                        NOT_VIEW_PAGER,
                        VIEWPORT_PAGING,
                        snapPosition,
                        scrollWithinContent,
                        CELL_SPACING,
                        selectOnSnap,
                        NO_SELECT_WHILE_SCROLLING,
                        isVertical);
        mIsVertical = isVertical;
        mTestAdapter.setViewSize(viewSize, isVertical);
        if (isVertical) {
            mViewGroup.setPadding(0, startPadding, 0, endPadding);
        } else {
            mViewGroup.setPadding(startPadding, 0, endPadding, 0);
        }
        mListLayoutManager =
                new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mListLayoutManager.setAnimationStoppedListener(mStoppedListener);
        mAdapterViewManager.setAdapter(mTestAdapter);
        mTestAdapter.setAdapterSize(adapterSize);

        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);

        mAnimation.newAnimation();
        layout();
        mStoppedListener.mCount = 0;
    }

    private void layout() {
        mListLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void scrollBy(final int displacement) {
        mAnimation.setDisplacement(displacement);
        layout();
    }

    private void scrollInFrames(final int frames, final int displacementPerFrame) {
        for (int frame = 0; frame < frames; frame++) {
            scrollBy(displacementPerFrame);
        }
    }

    private int start(final int position) {
        final View view = mListLayoutManager.getViewForPosition(position);
        if (mIsVertical) return view.getTop();
        return view.getLeft();
    }

    private int end(final int position) {
        final View view = mListLayoutManager.getViewForPosition(position);
        if (mIsVertical) return view.getBottom();
        return view.getRight();
    }

    private static final class CountingAnimationStoppedListener
            implements AnimationStoppedListener {
        private int mCount;

        @Override
        public void onAnimationStopped() {
            mCount++;
        }
    }

    public static final class MyViewGroup extends LinearLayout implements AdapterViewHandler {
        public final List<View> mViews = new ArrayList<View>();

        public MyViewGroup(final Context context) {
            super(context);
        }

        @Override
        public boolean addViewInAdapterView(
                final View view, final int index, final ViewGroup.LayoutParams layoutParams) {
            mViews.add(index, view);
            return true;
        }

        @Override
        public void removeViewInAdapterView(final View view) {
            mViews.remove(view);
        }
    }

    public static final class TestAdapter extends BaseAdapter {
        private static final int NO_OVERSIZED_VIEW = -1;

        private int mAdapterSize;
        private int mViewSize = VIEW_SIZE;
        private int mOversizedPosition = NO_OVERSIZED_VIEW;
        private int mOversizedViewSize;
        private boolean mIsVertical;

        public void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
        }

        public void setViewSize(final int viewSize, final boolean isVertical) {
            mViewSize = viewSize;
            mIsVertical = isVertical;
        }

        public void setOversizedView(final int position, final int viewSize) {
            mOversizedPosition = position;
            mOversizedViewSize = viewSize;
        }

        @Override
        public int getCount() {
            return mAdapterSize;
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
            final int viewSize = viewSizeAt(position);
            view.setLayoutParams(layoutParams(viewSize));
            return view;
        }

        private int viewSizeAt(final int position) {
            final boolean isOversized = position == mOversizedPosition;
            if (isOversized) return mOversizedViewSize;
            return mViewSize;
        }

        private ViewGroup.LayoutParams layoutParams(final int viewSize) {
            if (mIsVertical) {
                return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewSize);
            }
            return new ViewGroup.LayoutParams(viewSize, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
