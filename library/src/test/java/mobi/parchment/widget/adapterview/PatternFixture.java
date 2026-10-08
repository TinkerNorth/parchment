// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.content.Context;
import android.os.Parcelable;
import android.view.AbsSavedState;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroup;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternGroupDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManager;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternLayoutManagerAttributes;
import org.robolectric.shadows.ShadowSystemClock;

public final class PatternFixture {

    public static final int ADAPTER_SIZE = 20;
    private static final int CELL_SPACING = 10;
    private static final int PATTERN_BREADTH = 400;
    private static final int PATTERN_SIZE = 300;
    private static final float PATTERN_RATIO = 0.25f;
    private static final int ORIGIN = 0;
    private static final int REST_FRAME_LIMIT = 500;
    private static final long ONE_FRAME = 16;
    private static final float GESTURE_X = 150f;
    private static final float GESTURE_Y = 150f;
    private static final float GESTURE_TRAVEL = 45f;
    private static final long GESTURE_START = 0;
    private static final long GESTURE_END = 10;
    private static final int NO_META_STATE = 0;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int ONE_GRID_UNIT = 1;
    private static final int TWO_GRID_UNITS = 2;
    private static final int NOTHING_SELECTED = LayoutManager.INVALID_POSITION;
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final boolean SNAPS = true;
    private static final boolean DOES_NOT_SNAP = false;
    private static final boolean CIRCULAR = true;
    private static final boolean NOT_CIRCULAR = false;
    private static final boolean NOT_VIEW_PAGER = false;
    private static final int VIEWPORT_PAGING = 0;
    private static final boolean SCROLL_PAST_CONTENT = false;
    private static final boolean SELECT_ON_SNAP = true;
    private static final boolean NO_SELECT_WHILE_SCROLLING = false;
    public static final Configuration VERTICAL_DEFAULT =
            new Configuration(VERTICAL, SnapPosition.onScreen, DOES_NOT_SNAP, NOT_CIRCULAR);
    public static final Configuration VERTICAL_START_SNAP =
            new Configuration(VERTICAL, SnapPosition.start, SNAPS, NOT_CIRCULAR);
    public static final Configuration VERTICAL_CENTER_SNAP =
            new Configuration(VERTICAL, SnapPosition.center, SNAPS, NOT_CIRCULAR);
    public static final Configuration VERTICAL_END_SNAP =
            new Configuration(VERTICAL, SnapPosition.end, SNAPS, NOT_CIRCULAR);
    public static final Configuration VERTICAL_ON_SCREEN_SNAP =
            new Configuration(VERTICAL, SnapPosition.onScreen, SNAPS, NOT_CIRCULAR);
    public static final Configuration VERTICAL_CIRCULAR =
            new Configuration(VERTICAL, SnapPosition.center, SNAPS, CIRCULAR);
    public static final Configuration HORIZONTAL_DEFAULT =
            new Configuration(HORIZONTAL, SnapPosition.onScreen, DOES_NOT_SNAP, NOT_CIRCULAR);
    public static final Configuration HORIZONTAL_START_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.start, SNAPS, NOT_CIRCULAR);
    public static final Configuration HORIZONTAL_CENTER_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.center, SNAPS, NOT_CIRCULAR);
    public static final Configuration HORIZONTAL_END_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.end, SNAPS, NOT_CIRCULAR);
    public static final Configuration HORIZONTAL_ON_SCREEN_SNAP =
            new Configuration(HORIZONTAL, SnapPosition.onScreen, SNAPS, NOT_CIRCULAR);
    public static final Configuration HORIZONTAL_CIRCULAR =
            new Configuration(HORIZONTAL, SnapPosition.center, SNAPS, CIRCULAR);

    private final Configuration mConfiguration;
    private final PatternViewGroup mViewGroup;
    private final ResizableAdapter mAdapter;
    private final RecordingSelectedListener mSelections;
    private final GridPatternLayoutManager mLayoutManager;
    private final AdapterAnimator mAdapterAnimator;

    private PatternFixture(final Configuration configuration, final int adapterSize) {
        final Context context = ApplicationProvider.getApplicationContext();
        final AdapterViewManager adapterViewManager = new AdapterViewManager();
        final GridPatternLayoutManagerAttributes attributes = configuration.attributes();
        mConfiguration = configuration;
        mViewGroup = new PatternViewGroup(context);
        mAdapter = new ResizableAdapter(context, adapterSize);
        mSelections = new RecordingSelectedListener();
        mLayoutManager =
                new GridPatternLayoutManager(
                        mViewGroup, mSelections, adapterViewManager, attributes);
        final LayoutManagerBridge layoutManagerBridge = new LayoutManagerBridge(mLayoutManager);
        mAdapterAnimator =
                new AdapterAnimator(
                        mViewGroup,
                        new UnusedFrameScheduler(),
                        NOT_VIEW_PAGER,
                        configuration.mIsVertical,
                        layoutManagerBridge,
                        ViewConfiguration.get(context),
                        new ScrollListenerDispatcher());
        adapterViewManager.setAdapter(mAdapter);
    }

    public static PatternFixture withNoDefinition(final Configuration configuration) {
        final Definitions noGroup = new NoGroup();
        return laidOut(configuration, noGroup, ADAPTER_SIZE);
    }

    public static PatternFixture withOneOneByOneDefinition(final Configuration configuration) {
        final Definitions oneOneByOneGroup = new OneOneByOneGroup();
        return laidOut(configuration, oneOneByOneGroup, ADAPTER_SIZE);
    }

    public static PatternFixture withTwoGroups(final Configuration configuration) {
        final Definitions twoGroups = new TwoByTwoThenTwoOneByOnes();
        return laidOut(configuration, twoGroups, ADAPTER_SIZE);
    }

    public static PatternFixture withClearedDefinitions(final Configuration configuration) {
        final Definitions clearedGroups = new TwoGroupsCleared();
        return laidOut(configuration, clearedGroups, ADAPTER_SIZE);
    }

    private static PatternFixture laidOut(
            final Configuration configuration,
            final Definitions definitions,
            final int adapterSize) {
        final PatternFixture fixture = defined(configuration, definitions, adapterSize);
        fixture.layOut();
        return fixture;
    }

    private static PatternFixture defined(
            final Configuration configuration,
            final Definitions definitions,
            final int adapterSize) {
        final PatternFixture fixture = new PatternFixture(configuration, adapterSize);
        definitions.defineOn(fixture);
        return fixture;
    }

    private PatternFixture restoredWith(final Definitions definitions) {
        final Parcelable state = mLayoutManager.onSaveInstanceState(AbsSavedState.EMPTY_STATE);
        final int adapterSize = mAdapter.getCount();
        final PatternFixture fixture = defined(mConfiguration, definitions, adapterSize);
        fixture.mLayoutManager.onRestoreInstanceState(state);
        fixture.layOut();
        return fixture;
    }

    private static List<GridPatternItemDefinition> oneOneByOne() {
        final GridPatternItemDefinition oneByOne =
                new GridPatternItemDefinition(
                        FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(oneByOne);
        return items;
    }

    private static List<GridPatternItemDefinition> oneTwoByTwo() {
        final GridPatternItemDefinition twoByTwo =
                new GridPatternItemDefinition(
                        FIRST_ROW, FIRST_COLUMN, TWO_GRID_UNITS, TWO_GRID_UNITS);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(twoByTwo);
        return items;
    }

    private static List<GridPatternItemDefinition> twoOneByOnes(final boolean isVertical) {
        final GridPatternItemDefinition first =
                new GridPatternItemDefinition(
                        FIRST_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        final GridPatternItemDefinition second = secondOneByOneAcrossTheBreadth(isVertical);
        final List<GridPatternItemDefinition> items = new ArrayList<GridPatternItemDefinition>();
        items.add(first);
        items.add(second);
        return items;
    }

    private static GridPatternItemDefinition secondOneByOneAcrossTheBreadth(
            final boolean isVertical) {
        if (isVertical) {
            return new GridPatternItemDefinition(
                    FIRST_ROW, SECOND_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
        }
        return new GridPatternItemDefinition(
                SECOND_ROW, FIRST_COLUMN, ONE_GRID_UNIT, ONE_GRID_UNIT);
    }

    private boolean isVertical() {
        return mConfiguration.mIsVertical;
    }

    private GridPatternGroupDefinition group(final List<GridPatternItemDefinition> items) {
        return new GridPatternGroupDefinition(mConfiguration.mIsVertical, items);
    }

    private void addGroup(final List<GridPatternItemDefinition> items) {
        final GridPatternGroupDefinition definition = group(items);
        mLayoutManager.addGridPatternGroupDefinition(definition);
    }

    private void clearGroups() {
        mLayoutManager.clearGridPatternGroupDefinition();
    }

    public void setSelection(final int position) {
        mLayoutManager.setSelected(position, mViewGroup);
        runToRest();
    }

    private void setAdapterSize(final int adapterSize) {
        mAdapter.setCount(adapterSize);
        runToRest();
    }

    private void smoothScrollToPosition(final int position) {
        mAdapterAnimator.smoothScrollToPosition(position);
        runToRest();
    }

    private void fling(final float velocity) {
        final MotionEvent down =
                MotionEvent.obtain(
                        GESTURE_START,
                        GESTURE_START,
                        MotionEvent.ACTION_DOWN,
                        GESTURE_X,
                        GESTURE_Y,
                        NO_META_STATE);
        final MotionEvent move =
                MotionEvent.obtain(
                        GESTURE_START,
                        GESTURE_END,
                        MotionEvent.ACTION_MOVE,
                        GESTURE_X - GESTURE_TRAVEL,
                        GESTURE_Y - GESTURE_TRAVEL,
                        NO_META_STATE);
        mAdapterAnimator.onDown(down);
        mAdapterAnimator.onFling(down, move, velocity, velocity);
        mAdapterAnimator.onUp();
        runToRest();
    }

    private void drag(final float travel) {
        final MotionEvent down =
                MotionEvent.obtain(
                        GESTURE_START,
                        GESTURE_START,
                        MotionEvent.ACTION_DOWN,
                        GESTURE_X,
                        GESTURE_Y,
                        NO_META_STATE);
        final MotionEvent move =
                MotionEvent.obtain(
                        GESTURE_START,
                        GESTURE_END,
                        MotionEvent.ACTION_MOVE,
                        GESTURE_X + travel,
                        GESTURE_Y + travel,
                        NO_META_STATE);
        mAdapterAnimator.onDown(down);
        mAdapterAnimator.onScroll(down, move, -travel, -travel);
        frame();
        mAdapterAnimator.onUp();
        runToRest();
    }

    public List<GridPatternGroupDefinition> drawnCellDefinitions() {
        final List<GridPatternGroupDefinition> definitions =
                new ArrayList<GridPatternGroupDefinition>();
        for (final GridPatternGroup cell : mLayoutManager.mCells) {
            final GridPatternGroupDefinition definition = cell.getGridPatternGroupDefinition();
            definitions.add(definition);
        }
        return definitions;
    }

    public List<ItemSize> childMeasureSpecSizes() {
        final List<ItemSize> sizes = new ArrayList<ItemSize>();
        for (final View view : mViewGroup.mViews) {
            final int position = mLayoutManager.getPosition(view);
            final int widthMeasureSpec = mLayoutManager.getChildWidthMeasureSpec(position);
            final int heightMeasureSpec = mLayoutManager.getChildHeightMeasureSpec(position);
            final int width = View.MeasureSpec.getSize(widthMeasureSpec);
            final int height = View.MeasureSpec.getSize(heightMeasureSpec);
            final ItemSize size = new ItemSize(position, width, height);
            sizes.add(size);
        }
        return sizes;
    }

    public List<ItemSize> laidOutSizes() {
        final List<ItemSize> sizes = new ArrayList<ItemSize>();
        for (final View view : mViewGroup.mViews) {
            final int position = mLayoutManager.getPosition(view);
            final ItemSize size = new ItemSize(position, view.getWidth(), view.getHeight());
            sizes.add(size);
        }
        return sizes;
    }

    private void layOut() {
        final int width = mConfiguration.width();
        final int height = mConfiguration.height();
        final int widthMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        final int heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        mViewGroup.measure(widthMeasureSpec, heightMeasureSpec);
        mViewGroup.layout(ORIGIN, ORIGIN, width, height);
        runToRest();
    }

    private void runToRest() {
        frame();
        for (int frame = 0; frame < REST_FRAME_LIMIT; frame++) {
            if (isAtRest()) return;
            ShadowSystemClock.advanceBy(Duration.ofMillis(ONE_FRAME));
            frame();
        }
        throw new AssertionError("the pattern never came to rest: " + snapshot());
    }

    private void frame() {
        final int width = mConfiguration.width();
        final int height = mConfiguration.height();
        mAdapterAnimator.computeScrollOffset();
        final Animation animation = mAdapterAnimator.getAnimation();
        mLayoutManager.layout(mViewGroup, animation, ORIGIN, ORIGIN, width, height);
        mAdapterAnimator.onFrameLaidOut();
        reportTheSelectionAtRest();
    }

    private void reportTheSelectionAtRest() {
        if (!isAtRest()) return;
        mLayoutManager.reportTheSelectionAtRest();
    }

    private boolean isAtRest() {
        final AdapterAnimator.State state = mAdapterAnimator.getState();
        return state == AdapterAnimator.State.notMoving;
    }

    public LaidOutPattern snapshot() {
        final List<LaidOutChild> children = new ArrayList<LaidOutChild>();
        for (final View view : mViewGroup.mViews) {
            final int position = mLayoutManager.getPosition(view);
            final LaidOutChild child =
                    new LaidOutChild(
                            position,
                            view.getLeft(),
                            view.getTop(),
                            view.getRight(),
                            view.getBottom());
            children.add(child);
        }
        final int selectedPosition = mLayoutManager.getSelectedPosition();
        final List<Integer> selections = mSelections.positions();
        final int firstVisiblePosition = mLayoutManager.getFirstVisibleAdapterPosition();
        final int lastVisiblePosition = mLayoutManager.getLastVisibleAdapterPosition();
        return new LaidOutPattern(
                children, selectedPosition, selections, firstVisiblePosition, lastVisiblePosition);
    }

    private interface Definitions {
        public void defineOn(final PatternFixture pattern);
    }

    private static final class NoGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {}
    }

    private static final class OneOneByOneGroup implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> oneOneByOne = oneOneByOne();
            pattern.addGroup(oneOneByOne);
        }
    }

    private static final class TwoByTwoThenTwoOneByOnes implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> oneTwoByTwo = oneTwoByTwo();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            pattern.addGroup(oneTwoByTwo);
            pattern.addGroup(twoOneByOnes);
        }
    }

    private static final class TwoGroupsCleared implements Definitions {
        @Override
        public void defineOn(final PatternFixture pattern) {
            final List<GridPatternItemDefinition> oneTwoByTwo = oneTwoByTwo();
            final List<GridPatternItemDefinition> twoOneByOnes = twoOneByOnes(pattern.isVertical());
            pattern.addGroup(oneTwoByTwo);
            pattern.addGroup(twoOneByOnes);
            pattern.clearGroups();
        }
    }

    public static final class Configuration {
        private final boolean mIsVertical;
        private final SnapPosition mSnapPosition;
        private final boolean mSnapToPosition;
        private final boolean mIsCircularScroll;

        private Configuration(
                final boolean isVertical,
                final SnapPosition snapPosition,
                final boolean snapToPosition,
                final boolean isCircularScroll) {
            mIsVertical = isVertical;
            mSnapPosition = snapPosition;
            mSnapToPosition = snapToPosition;
            mIsCircularScroll = isCircularScroll;
        }

        private GridPatternLayoutManagerAttributes attributes() {
            return new GridPatternLayoutManagerAttributes(
                    mIsCircularScroll,
                    mSnapToPosition,
                    NOT_VIEW_PAGER,
                    VIEWPORT_PAGING,
                    mSnapPosition,
                    SCROLL_PAST_CONTENT,
                    CELL_SPACING,
                    SELECT_ON_SNAP,
                    NO_SELECT_WHILE_SCROLLING,
                    mIsVertical,
                    PATTERN_RATIO);
        }

        private int width() {
            if (mIsVertical) return PATTERN_BREADTH;
            return PATTERN_SIZE;
        }

        private int height() {
            if (mIsVertical) return PATTERN_SIZE;
            return PATTERN_BREADTH;
        }
    }

    public static final class PatternAndOracle {
        private final PatternFixture mPattern;
        private final PatternFixture mOracle;
        private final Definitions mDefinitions;
        private final Definitions mOracleDefinitions;

        private PatternAndOracle(
                final PatternFixture pattern,
                final PatternFixture oracle,
                final Definitions definitions,
                final Definitions oracleDefinitions) {
            mPattern = pattern;
            mOracle = oracle;
            mDefinitions = definitions;
            mOracleDefinitions = oracleDefinitions;
        }

        public static PatternAndOracle laidOutWithNoDefinition(final Configuration configuration) {
            final Definitions noGroup = new NoGroup();
            final Definitions oneOneByOneGroup = new OneOneByOneGroup();
            return laidOut(configuration, noGroup, oneOneByOneGroup, ADAPTER_SIZE);
        }

        private static PatternAndOracle laidOut(
                final Configuration configuration,
                final Definitions definitions,
                final Definitions oracleDefinitions,
                final int adapterSize) {
            final PatternFixture oracle =
                    PatternFixture.laidOut(configuration, oracleDefinitions, adapterSize);
            final PatternFixture pattern =
                    PatternFixture.laidOut(configuration, definitions, adapterSize);
            return new PatternAndOracle(pattern, oracle, definitions, oracleDefinitions);
        }

        public PatternAndOracle restoredIntoNewPatterns() {
            final PatternFixture oracle = mOracle.restoredWith(mOracleDefinitions);
            final PatternFixture pattern = mPattern.restoredWith(mDefinitions);
            return new PatternAndOracle(pattern, oracle, mDefinitions, mOracleDefinitions);
        }

        public void setSelection(final int position) {
            mOracle.setSelection(position);
            mPattern.setSelection(position);
        }

        public void setAdapterSize(final int adapterSize) {
            mOracle.setAdapterSize(adapterSize);
            mPattern.setAdapterSize(adapterSize);
        }

        public void smoothScrollToPosition(final int position) {
            mOracle.smoothScrollToPosition(position);
            mPattern.smoothScrollToPosition(position);
        }

        public void fling(final float velocity) {
            mOracle.fling(velocity);
            mPattern.fling(velocity);
        }

        public void drag(final float travel) {
            mOracle.drag(travel);
            mPattern.drag(travel);
        }

        public LaidOutPattern patternLayout() {
            return mPattern.snapshot();
        }

        public LaidOutPattern oracleLayout() {
            return mOracle.snapshot();
        }
    }

    public static final class ItemSize {
        private final int mPosition;
        private final int mWidth;
        private final int mHeight;

        public ItemSize(final int position, final int width, final int height) {
            mPosition = position;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof ItemSize)) return false;
            final ItemSize size = (ItemSize) other;
            final boolean samePosition = mPosition == size.mPosition;
            final boolean sameWidth = mWidth == size.mWidth;
            final boolean sameHeight = mHeight == size.mHeight;
            return samePosition && sameWidth && sameHeight;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mPosition, mWidth, mHeight);
        }

        @Override
        public String toString() {
            return "position " + mPosition + " " + mWidth + "x" + mHeight;
        }
    }

    private static final class LaidOutChild {
        private final int mPosition;
        private final int mLeft;
        private final int mTop;
        private final int mRight;
        private final int mBottom;

        private LaidOutChild(
                final int position,
                final int left,
                final int top,
                final int right,
                final int bottom) {
            mPosition = position;
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof LaidOutChild)) return false;
            final LaidOutChild child = (LaidOutChild) other;
            final boolean samePosition = mPosition == child.mPosition;
            final boolean sameStart = mLeft == child.mLeft && mTop == child.mTop;
            final boolean sameEnd = mRight == child.mRight && mBottom == child.mBottom;
            return samePosition && sameStart && sameEnd;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mPosition, mLeft, mTop, mRight, mBottom);
        }

        @Override
        public String toString() {
            return "position "
                    + mPosition
                    + " at ["
                    + mLeft
                    + ", "
                    + mTop
                    + ", "
                    + mRight
                    + ", "
                    + mBottom
                    + "]";
        }
    }

    public static final class LaidOutPattern {
        private final List<LaidOutChild> mChildren;
        private final int mSelectedPosition;
        private final List<Integer> mSelections;
        private final int mFirstVisiblePosition;
        private final int mLastVisiblePosition;

        private LaidOutPattern(
                final List<LaidOutChild> children,
                final int selectedPosition,
                final List<Integer> selections,
                final int firstVisiblePosition,
                final int lastVisiblePosition) {
            mChildren = children;
            mSelectedPosition = selectedPosition;
            mSelections = selections;
            mFirstVisiblePosition = firstVisiblePosition;
            mLastVisiblePosition = lastVisiblePosition;
        }

        public int childCount() {
            return mChildren.size();
        }

        public boolean isLaidOut(final int position) {
            for (final LaidOutChild child : mChildren) {
                final boolean holdsThePosition = child.mPosition == position;
                if (holdsThePosition) return true;
            }
            return false;
        }

        public int selectedPosition() {
            return mSelectedPosition;
        }

        public int firstVisiblePosition() {
            return mFirstVisiblePosition;
        }

        public int lastVisiblePosition() {
            return mLastVisiblePosition;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof LaidOutPattern)) return false;
            final LaidOutPattern pattern = (LaidOutPattern) other;
            final boolean sameChildren = mChildren.equals(pattern.mChildren);
            final boolean sameSelection = mSelectedPosition == pattern.mSelectedPosition;
            final boolean sameSelections = mSelections.equals(pattern.mSelections);
            final boolean sameFirst = mFirstVisiblePosition == pattern.mFirstVisiblePosition;
            final boolean sameLast = mLastVisiblePosition == pattern.mLastVisiblePosition;
            return sameChildren && sameSelection && sameSelections && sameFirst && sameLast;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    mChildren,
                    mSelectedPosition,
                    mSelections,
                    mFirstVisiblePosition,
                    mLastVisiblePosition);
        }

        @Override
        public String toString() {
            return "children "
                    + mChildren
                    + ", selected "
                    + mSelectedPosition
                    + ", selections reported "
                    + mSelections
                    + ", first visible "
                    + mFirstVisiblePosition
                    + ", last visible "
                    + mLastVisiblePosition;
        }
    }

    private static final class ResizableAdapter extends BaseAdapter {
        private final Context mContext;
        private int mCount;

        private ResizableAdapter(final Context context, final int count) {
            mContext = context;
            mCount = count;
        }

        private void setCount(final int count) {
            mCount = count;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCount;
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
            final FrameLayout view = new FrameLayout(mContext);
            final ViewGroup.LayoutParams layoutParams =
                    new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            view.setTag(position);
            return view;
        }
    }

    private static final class PatternViewGroup extends LinearLayout implements AdapterViewHandler {
        private final List<View> mViews = new ArrayList<View>();

        private PatternViewGroup(final Context context) {
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

    private static final class RecordingSelectedListener implements OnSelectedListener {
        private final List<Integer> mPositions = new ArrayList<Integer>();

        @Override
        public void onSelected(final View view) {
            final Integer position = positionOf(view);
            mPositions.add(position);
        }

        private static Integer positionOf(final View view) {
            if (view == null) return NOTHING_SELECTED;
            return (Integer) view.getTag();
        }

        private List<Integer> positions() {
            return new ArrayList<Integer>(mPositions);
        }
    }

    private static final class UnusedFrameScheduler implements AnimationFrameScheduler {
        @Override
        public void requestAnimationFrame() {}
    }
}
