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
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManager;
import mobi.parchment.widget.adapterview.gridview.GridLayoutManagerAttributes;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * The geometry a smooth scroll to a position is built from: the distance to a drawn cell, the
 * estimate to one that is not, and the questions the animator asks before it starts. The animator's
 * own flows live in AdapterAnimatorTest.
 */
@RunWith(RobolectricTestRunner.class)
public class ListLayoutManagerScrollToPositionTest {

    private static final int VIEW_GROUP_SIZE = 300;
    private static final int VIEW_SIZE = 100;
    private static final int ADAPTER_SIZE = 10;
    private static final int NINE_CELLS = 9;
    private static final int EMPTY_ADAPTER = 0;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean CIRCULAR = true;
    private static final boolean SNAP_TO_POSITION = true;
    private static final boolean NO_SNAP_TO_POSITION = false;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final int VIEWPORT_PAGING = 0;
    private static final boolean NO_SELECT_ON_SNAP = false;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    private static final boolean HORIZONTAL = false;
    private static final boolean SCROLL_PAST_CONTENT = false;
    private static final boolean NO_GRAVITY_EDGE = false;
    private static final int NO_CELL_SPACING = 0;
    private static final int CELL_SPACING = 10;
    private static final int START_PADDING = 10;
    private static final int END_PADDING = 30;
    private static final int VIEWS_PER_CELL = 2;
    private static final float SQUARE_UNITS = 1f;
    private static final int ONE_UNIT = 1;
    private static final int THE_FIRST_ROW = 0;
    private static final int THE_FIRST_COLUMN = 0;
    private static final int THE_SECOND_ROW = 1;
    private static final int THE_THIRD_CELL_START = 300;

    private static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final int THIRD_POSITION = 2;
    private static final int FOURTH_POSITION = 3;
    private static final int FIFTH_POSITION = 4;
    private static final int SIXTH_POSITION = 5;
    private static final int SEVENTH_POSITION = 6;
    private static final int EIGHTH_POSITION = 7;
    private static final int TENTH_POSITION = 9;
    private static final int LAST_POSITION = ADAPTER_SIZE - 1;
    private static final int PAST_THE_END = 50;
    private static final int A_NEGATIVE_POSITION = -3;
    private static final int A_TIE_BETWEEN_THE_TWO_WAYS_ROUND = 6;
    private static final int A_CELL_AS_MANY_CELLS_AWAY_EITHER_WAY = 6;

    private static final int NO_DISTANCE = 0;
    private static final int START_OF_THE_VIEW = 0;
    private static final int CENTRED_CELL_START = (VIEW_GROUP_SIZE - VIEW_SIZE) / 2;
    private static final int ONE_CELL_FORWARD = -VIEW_SIZE;
    private static final int FIVE_CELLS_BACK = -500;
    private static final int PART_OF_A_CELL_FORWARD = -50;
    private static final int PAST_THE_DRAWN_CELLS = -800;
    private static final int CELL_FIVE_INTO_THE_CENTRE = -500;
    private static final int CELL_ONE_BACK_INTO_THE_CENTRE = 400;
    private static final int CELL_SIX_TO_THE_START = -600;
    private static final int CELL_TWO_BACK_TO_THE_START = 300;
    private static final int CELL_FOUR_TO_THE_END = -400;
    private static final int CELL_THREE_BACK_ON_SCREEN = -50;
    private static final int CELL_ZERO_BACK_ON_SCREEN = 50;
    private static final int CELL_FOUR_INTO_THE_CENTRE_WITH_SPACING = -440;
    private static final int CELL_ZERO_BACK_INTO_THE_PADDED_CENTRE = 50;
    private static final int THE_LAST_CELL_ONE_STEP_BACK = 100;
    private static final int CELL_FIVE_ON_SCREEN_THE_SHORT_WAY = -300;
    private static final int THE_TIE_TAKEN_FORWARD = -400;
    private static final int THE_SHORTER_WAY_BACK = 300;
    private static final int CELL_ONE_AFTER_THE_WRAP = -100;
    private static final int THE_THIRD_ROW_INTO_THE_CENTRE = -200;
    private static final int THE_FIFTH_ROW_INTO_THE_CENTRE = -400;
    private static final int THE_FOURTH_GROUP_TO_THE_START = -450;

    private final MyViewGroup mViewGroup =
            new MyViewGroup(ApplicationProvider.getApplicationContext());
    private final AdapterViewManager mAdapterViewManager = new AdapterViewManager();
    private final TestAdapter mTestAdapter = new TestAdapter();
    private final Animation mAnimation = new Animation();
    private LayoutManager<?> mLayoutManager;

    @Test
    public void centerSnap_scrollToADrawnCell_isThatCellsSnapDistance() {
        setup(SnapPosition.center);
        assertThat(startOf(FIRST_POSITION)).isEqualTo(CENTRED_CELL_START);

        assertThat(distanceTo(SECOND_POSITION)).isEqualTo(ONE_CELL_FORWARD);
    }

    @Test
    public void centerSnap_scrollToACellBeyondTheDrawnCells_extrapolatesWithTheLastCellSize() {
        setup(SnapPosition.center);
        assertThat(mLayoutManager.isPositionDrawn(SIXTH_POSITION)).isFalse();

        assertThat(distanceTo(SIXTH_POSITION)).isEqualTo(CELL_FIVE_INTO_THE_CENTRE);
    }

    @Test
    public void centerSnap_scrollToACellBeforeTheDrawnCells_extrapolatesWithTheFirstCellSize() {
        setup(SnapPosition.center);
        scrollBy(FIVE_CELLS_BACK);
        assertThat(startOf(SIXTH_POSITION)).isEqualTo(CENTRED_CELL_START);
        assertThat(mLayoutManager.isPositionDrawn(SECOND_POSITION)).isFalse();

        assertThat(distanceTo(SECOND_POSITION)).isEqualTo(CELL_ONE_BACK_INTO_THE_CENTRE);
    }

    @Test
    public void startSnap_scrollToALaterCell_isNegative() {
        setup(SnapPosition.start);
        assertThat(startOf(FIRST_POSITION)).isEqualTo(START_OF_THE_VIEW);

        assertThat(distanceTo(SEVENTH_POSITION)).isEqualTo(CELL_SIX_TO_THE_START);
    }

    @Test
    public void startSnap_scrollToAnEarlierCell_isPositive() {
        setup(SnapPosition.start);
        scrollBy(FIVE_CELLS_BACK);
        assertThat(startOf(SIXTH_POSITION)).isEqualTo(START_OF_THE_VIEW);

        assertThat(distanceTo(THIRD_POSITION)).isEqualTo(CELL_TWO_BACK_TO_THE_START);
    }

    @Test
    public void endSnap_scrollToACell_bringsItsEndToTheViewEnd() {
        setup(SnapPosition.end);
        assertThat(endOf(FIRST_POSITION)).isEqualTo(VIEW_GROUP_SIZE);

        assertThat(distanceTo(FIFTH_POSITION)).isEqualTo(CELL_FOUR_TO_THE_END);
    }

    @Test
    public void onScreenSnap_scrollToACellFullyVisible_isZero() {
        setup(SnapPosition.onScreen);
        assertThat(startOf(SECOND_POSITION)).isEqualTo(VIEW_SIZE);

        assertThat(distanceTo(SECOND_POSITION)).isEqualTo(NO_DISTANCE);
    }

    @Test
    public void onScreenSnap_scrollToACellPartlyOffTheEnd_isTheMinimumThatShowsItWhole() {
        setup(SnapPosition.onScreen);
        scrollBy(PART_OF_A_CELL_FORWARD);

        assertThat(distanceTo(FOURTH_POSITION)).isEqualTo(CELL_THREE_BACK_ON_SCREEN);
    }

    @Test
    public void onScreenSnap_scrollToACellPartlyOffTheStart_isTheMinimumThatShowsItWhole() {
        setup(SnapPosition.onScreen);
        scrollBy(PART_OF_A_CELL_FORWARD);

        assertThat(distanceTo(FIRST_POSITION)).isEqualTo(CELL_ZERO_BACK_ON_SCREEN);
    }

    @Test
    public void cellSpacing_isPartOfTheExtrapolation() {
        setup(SnapPosition.center, SNAP_TO_POSITION, NOT_CIRCULAR, CELL_SPACING, ADAPTER_SIZE);
        assertThat(mLayoutManager.isPositionDrawn(FIFTH_POSITION)).isFalse();

        assertThat(distanceTo(FIFTH_POSITION)).isEqualTo(CELL_FOUR_INTO_THE_CENTRE_WITH_SPACING);
    }

    @Test
    public void padding_movesTheTargetInsideThePadding() {
        setupWithPadding(SnapPosition.center, START_PADDING, END_PADDING);
        scrollBy(PART_OF_A_CELL_FORWARD);

        assertThat(distanceTo(FIRST_POSITION)).isEqualTo(CELL_ZERO_BACK_INTO_THE_PADDED_CENTRE);
    }

    @Test
    public void withoutSnapToPosition_stillAnswersTheDistance() {
        setup(
                SnapPosition.center,
                NO_SNAP_TO_POSITION,
                NOT_CIRCULAR,
                NO_CELL_SPACING,
                ADAPTER_SIZE);

        assertThat(distanceTo(SECOND_POSITION)).isEqualTo(ONE_CELL_FORWARD);
    }

    @Test
    public void circularScroll_aCellNearerBackwards_isMeasuredBackwards() {
        setup(SnapPosition.center, SNAP_TO_POSITION, CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);

        assertThat(distanceTo(LAST_POSITION)).isEqualTo(THE_LAST_CELL_ONE_STEP_BACK);
    }

    @Test
    public void circularScroll_aCellNearerForwards_isMeasuredForwards() {
        setup(SnapPosition.center, SNAP_TO_POSITION, CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);

        assertThat(distanceTo(SIXTH_POSITION)).isEqualTo(CELL_FIVE_ON_SCREEN_THE_SHORT_WAY);
    }

    @Test
    public void circularScroll_aTie_goesForward() {
        setup(SnapPosition.center, SNAP_TO_POSITION, CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);

        assertThat(distanceTo(A_TIE_BETWEEN_THE_TWO_WAYS_ROUND)).isEqualTo(THE_TIE_TAKEN_FORWARD);
    }

    @Test
    public void circularScroll_theShorterWayInPixels_wins() {
        setup(SnapPosition.center, SNAP_TO_POSITION, CIRCULAR, NO_CELL_SPACING, NINE_CELLS);

        assertThat(distanceTo(A_CELL_AS_MANY_CELLS_AWAY_EITHER_WAY))
                .isEqualTo(THE_SHORTER_WAY_BACK);
    }

    @Test
    public void seekDistance_isTheEstimatePlusAViewportInItsDirection() {
        setup(SnapPosition.center);

        assertThat(seekDistanceTo(SIXTH_POSITION))
                .isEqualTo(CELL_FIVE_INTO_THE_CENTRE - VIEW_GROUP_SIZE);
    }

    @Test
    public void seekDistance_backwards_overshootsBackwards() {
        setup(SnapPosition.center);
        scrollBy(FIVE_CELLS_BACK);

        assertThat(seekDistanceTo(SECOND_POSITION))
                .isEqualTo(CELL_ONE_BACK_INTO_THE_CENTRE + VIEW_GROUP_SIZE);
    }

    @Test
    public void seekRunway_isTheViewportInsideThePadding() {
        setupWithPadding(SnapPosition.center, START_PADDING, END_PADDING);

        assertThat(mLayoutManager.getSeekRunway(mViewGroup))
                .isEqualTo(VIEW_GROUP_SIZE - START_PADDING - END_PADDING);
    }

    @Test
    public void circularScroll_aDrawnCellAfterTheWrap_isMeasuredWhereItIsDrawn() {
        setup(SnapPosition.center, SNAP_TO_POSITION, CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);
        scrollBy(PAST_THE_DRAWN_CELLS);
        assertThat(startOf(SECOND_POSITION)).isEqualTo(VIEW_GROUP_SIZE);

        assertThat(mLayoutManager.isPositionDrawn(SECOND_POSITION)).isTrue();
        assertThat(distanceTo(SECOND_POSITION)).isEqualTo(CELL_ONE_AFTER_THE_WRAP);
    }

    @Test
    public void isPositionDrawn_forADrawnPosition_isTrue() {
        setup(SnapPosition.center);

        assertThat(mLayoutManager.isPositionDrawn(THIRD_POSITION)).isTrue();
    }

    @Test
    public void isPositionDrawn_pastTheDrawnCells_isFalse() {
        setup(SnapPosition.center);

        assertThat(mLayoutManager.isPositionDrawn(FOURTH_POSITION)).isFalse();
    }

    @Test
    public void isPositionDrawn_beforeTheDrawnCells_isFalse() {
        setup(SnapPosition.center);
        scrollBy(FIVE_CELLS_BACK);

        assertThat(mLayoutManager.isPositionDrawn(THIRD_POSITION)).isFalse();
    }

    @Test
    public void clampToAdapter_pastTheEnd_isTheLastPosition() {
        setup(SnapPosition.center);

        assertThat(mLayoutManager.clampToAdapter(PAST_THE_END)).isEqualTo(LAST_POSITION);
    }

    @Test
    public void clampToAdapter_negative_isTheFirstPosition() {
        setup(SnapPosition.center);

        assertThat(mLayoutManager.clampToAdapter(A_NEGATIVE_POSITION)).isEqualTo(FIRST_POSITION);
    }

    @Test
    public void clampToAdapter_insideTheAdapter_isUnchanged() {
        setup(SnapPosition.center);

        assertThat(mLayoutManager.clampToAdapter(SEVENTH_POSITION)).isEqualTo(SEVENTH_POSITION);
    }

    @Test
    public void emptyAdapter_hasNoScrollTarget() {
        setup(SnapPosition.center, SNAP_TO_POSITION, NOT_CIRCULAR, NO_CELL_SPACING, EMPTY_ADAPTER);

        assertThat(mLayoutManager.hasAScrollTarget()).isFalse();
    }

    @Test
    public void beforeTheFirstLayout_hasNoScrollTarget() {
        createListLayoutManager(
                SnapPosition.center, SNAP_TO_POSITION, NOT_CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);

        assertThat(mLayoutManager.hasAScrollTarget()).isFalse();
    }

    @Test
    public void aLaidOutAdapter_hasAScrollTarget() {
        setup(SnapPosition.center);

        assertThat(mLayoutManager.hasAScrollTarget()).isTrue();
    }

    @Test
    public void gridScrollToPosition_targetsTheRowHoldingThePosition() {
        setupGrid();
        assertThat(startOf(FIFTH_POSITION)).isEqualTo(THE_THIRD_CELL_START);

        assertThat(distanceTo(FIFTH_POSITION)).isEqualTo(THE_THIRD_ROW_INTO_THE_CENTRE);
        assertThat(distanceTo(SIXTH_POSITION)).isEqualTo(THE_THIRD_ROW_INTO_THE_CENTRE);
        assertThat(distanceTo(TENTH_POSITION)).isEqualTo(THE_FIFTH_ROW_INTO_THE_CENTRE);
    }

    @Test
    public void gridPatternScrollToPosition_targetsTheGroupHoldingThePosition() {
        setupGridPattern();
        assertThat(startOf(FIFTH_POSITION)).isEqualTo(THE_THIRD_CELL_START);

        assertThat(distanceTo(SEVENTH_POSITION)).isEqualTo(THE_FOURTH_GROUP_TO_THE_START);
        assertThat(distanceTo(SEVENTH_POSITION)).isEqualTo(distanceTo(EIGHTH_POSITION));
    }

    private int distanceTo(final int position) {
        return mLayoutManager.getScrollToPositionDistance(mViewGroup, position);
    }

    private int seekDistanceTo(final int position) {
        return mLayoutManager.getSeekDistance(mViewGroup, position);
    }

    private void setup(final SnapPosition snapPosition) {
        setup(snapPosition, SNAP_TO_POSITION, NOT_CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);
    }

    private void setupWithPadding(
            final SnapPosition snapPosition, final int startPadding, final int endPadding) {
        mViewGroup.setPadding(startPadding, 0, endPadding, 0);
        setup(snapPosition, SNAP_TO_POSITION, NOT_CIRCULAR, NO_CELL_SPACING, ADAPTER_SIZE);
    }

    private void setup(
            final SnapPosition snapPosition,
            final boolean snapToPosition,
            final boolean isCircularScroll,
            final int cellSpacing,
            final int adapterSize) {
        createListLayoutManager(
                snapPosition, snapToPosition, isCircularScroll, cellSpacing, adapterSize);
        layOut();
    }

    private void createListLayoutManager(
            final SnapPosition snapPosition,
            final boolean snapToPosition,
            final boolean isCircularScroll,
            final int cellSpacing,
            final int adapterSize) {
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        isCircularScroll,
                        snapToPosition,
                        NOT_VIEW_PAGER,
                        VIEWPORT_PAGING,
                        snapPosition,
                        SCROLL_PAST_CONTENT,
                        cellSpacing,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL);
        mLayoutManager = new ListLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mAdapterViewManager.setAdapter(mTestAdapter);
        mTestAdapter.setAdapterSize(adapterSize);
        measureTheViewGroup();
    }

    private void setupGrid() {
        final GridLayoutManagerAttributes attributes =
                new GridLayoutManagerAttributes(
                        VIEWS_PER_CELL,
                        NOT_CIRCULAR,
                        SNAP_TO_POSITION,
                        NOT_VIEW_PAGER,
                        VIEWPORT_PAGING,
                        SnapPosition.center,
                        SCROLL_PAST_CONTENT,
                        NO_CELL_SPACING,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL,
                        NO_GRAVITY_EDGE,
                        NO_GRAVITY_EDGE,
                        NO_GRAVITY_EDGE,
                        NO_GRAVITY_EDGE);
        mLayoutManager = new GridLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        mAdapterViewManager.setAdapter(mTestAdapter);
        mTestAdapter.setAdapterSize(ADAPTER_SIZE);
        measureTheViewGroup();
        layOut();
    }

    private void setupGridPattern() {
        final GridPatternLayoutManagerAttributes attributes =
                new GridPatternLayoutManagerAttributes(
                        NOT_CIRCULAR,
                        SNAP_TO_POSITION,
                        NOT_VIEW_PAGER,
                        VIEWPORT_PAGING,
                        SnapPosition.start,
                        SCROLL_PAST_CONTENT,
                        NO_CELL_SPACING,
                        NO_SELECT_ON_SNAP,
                        NO_SELECT_WHILE_SCROLLING,
                        HORIZONTAL,
                        SQUARE_UNITS);
        final GridPatternLayoutManager gridPatternLayoutManager =
                new GridPatternLayoutManager(mViewGroup, null, mAdapterViewManager, attributes);
        final List<GridPatternItemDefinition> twoStacked =
                new ArrayList<GridPatternItemDefinition>();
        twoStacked.add(
                new GridPatternItemDefinition(THE_FIRST_ROW, THE_FIRST_COLUMN, ONE_UNIT, ONE_UNIT));
        twoStacked.add(
                new GridPatternItemDefinition(
                        THE_SECOND_ROW, THE_FIRST_COLUMN, ONE_UNIT, ONE_UNIT));
        gridPatternLayoutManager.addGridPatternGroupDefinition(
                new GridPatternGroupDefinition(HORIZONTAL, twoStacked));
        mLayoutManager = gridPatternLayoutManager;
        mAdapterViewManager.setAdapter(mTestAdapter);
        mTestAdapter.setAdapterSize(ADAPTER_SIZE);
        measureTheViewGroup();
        layOut();
    }

    private void measureTheViewGroup() {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEW_GROUP_SIZE, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(measureSpec, measureSpec);
        mViewGroup.layout(0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void layOut() {
        mAnimation.newAnimation();
        layout();
    }

    private void layout() {
        mLayoutManager.layout(mViewGroup, mAnimation, 0, 0, VIEW_GROUP_SIZE, VIEW_GROUP_SIZE);
    }

    private void scrollBy(final int displacement) {
        mAnimation.setDisplacement(displacement);
        layout();
    }

    private int startOf(final int position) {
        final View view = mLayoutManager.getViewForPosition(position);
        return view.getLeft();
    }

    private int endOf(final int position) {
        final View view = mLayoutManager.getViewForPosition(position);
        return view.getRight();
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
        private int mAdapterSize;

        public void setAdapterSize(final int adapterSize) {
            mAdapterSize = adapterSize;
            notifyDataSetChanged();
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
            view.setLayoutParams(new ViewGroup.LayoutParams(VIEW_SIZE, VIEW_SIZE));
            return view;
        }
    }
}
