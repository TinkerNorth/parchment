// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.os.Parcelable;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mobi.parchment.widget.adapterview.pageinterval.PageIntervalInterface;
import mobi.parchment.widget.adapterview.pageinterval.PageIntervalSelector;
import mobi.parchment.widget.adapterview.snapposition.SnapPositionInterface;
import mobi.parchment.widget.adapterview.snapposition.SnapPositionSelector;

public abstract class LayoutManager<Cell> extends AdapterViewDataSetObserver {
    public static final int INVALID_POSITION = -1;
    private static final int NO_BREADTH = 0;
    private static final int NOT_WRAPPING = -1;
    private static final int FIRST_VIEW_IN_CELL = 0;

    private final Map<View, Integer> mPositions = new HashMap<View, Integer>();

    private boolean mIsFirstLayout = true;
    public float mLayoutSize;
    public float mLayoutCellCount;
    private final int MAX = Integer.MAX_VALUE / 2;
    private int mAnimationId = -1;
    private boolean mAnimationIsAGesture;
    private boolean mAStopIsOwedToAJump;
    private int mOffset = 0;
    private int mStartCellPosition;
    protected int mViewPageDistanceForward;
    protected int mViewPageDistanceBack;
    private int mAnimationDisplacement;
    private int mFrameDisplacement;
    private Cell mHeldCell;
    protected final ViewGroup mViewGroup;
    private final ScrollDirectionManager mScrollDirectionManager;

    protected final SelectedPositionManager mSelectedPositionManager;
    private AnimationStoppedListener mAnimationStoppedListener;
    private final LayoutManagerAttributes mLayoutManagerAttributes;

    protected final List<Cell> mCells = new ArrayList<Cell>();
    private final SnapPositionInterface<Cell> mSnapPositionInterface;
    private final PageIntervalInterface<Cell> mPageIntervalInterface;
    private View mPressedView;
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private int mMeasuredContentBreadth = NOT_WRAPPING;
    private final RequestLayoutRunnable mRequestLayoutRunnable;

    public LayoutManager(
            final ViewGroup viewGroup,
            final OnSelectedListener onSelectedListener,
            final AdapterViewManager adapterViewManager,
            final LayoutManagerAttributes layoutManagerAttributes) {
        super(adapterViewManager);
        mViewGroup = viewGroup;
        mRequestLayoutRunnable = new RequestLayoutRunnable(viewGroup);
        mStartCellPosition = 0;
        mSelectedPositionManager = new SelectedPositionManager(onSelectedListener);
        mScrollDirectionManager = new ScrollDirectionManager(layoutManagerAttributes);
        mLayoutManagerAttributes = layoutManagerAttributes;

        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();
        final SnapPosition snapPosition = getSnapPosition(isCircularScroll);
        final boolean scrollWithinContent = mLayoutManagerAttributes.scrollWithinContent();
        mSnapPositionInterface =
                SnapPositionSelector.getSnapPositionInterface(snapPosition, scrollWithinContent);

        final int viewPagerInterval = mLayoutManagerAttributes.getViewPagerInterval();
        mPageIntervalInterface = PageIntervalSelector.getPageIntervalInterface(viewPagerInterval);
    }

    public int getSelectedPosition() {
        return mSelectedPositionManager.getSelectedPosition();
    }

    public int getWidthMeasureSpec() {
        return mWidthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return mHeightMeasureSpec;
    }

    public int getCellSpacing() {
        final int cellSpacing = mLayoutManagerAttributes.getCellSpacing();
        return cellSpacing;
    }

    protected ViewGroup getViewGroup() {
        return mViewGroup;
    }

    protected int getViewStart(final View view) {
        return mScrollDirectionManager.getViewStart(view);
    }

    protected int getViewEnd(final View view) {
        return mScrollDirectionManager.getViewEnd(view);
    }

    protected int getViewSize(final View view) {
        return mScrollDirectionManager.getViewSize(view);
    }

    protected int getViewBreadth(final View view) {
        return mScrollDirectionManager.getViewBreadth(view);
    }

    @Override
    public void destroy() {
        mPositions.clear();
        super.destroy();
        mCells.clear();
        mAStopIsOwedToAJump = false;
        mViewGroup.removeCallbacks(mRequestLayoutRunnable);
    }

    public void measure(
            final ViewGroup viewGroup, final int widthMeasureSpec, final int heightMeasureSpec) {
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        for (final Cell cell : mCells) {
            measure(cell, viewGroup);
        }
    }

    public int measureBreadth(final ViewGroup viewGroup, final int breadthMeasureSpec) {
        final int mode = View.MeasureSpec.getMode(breadthMeasureSpec);
        final int specBreadth = View.MeasureSpec.getSize(breadthMeasureSpec);
        switch (mode) {
            case View.MeasureSpec.AT_MOST:
                return getWrappedBreadthWithin(viewGroup, specBreadth);
            case View.MeasureSpec.UNSPECIFIED:
                return getWrappedBreadth(viewGroup);
            case View.MeasureSpec.EXACTLY:
            default:
                return useTheSpecBreadth(specBreadth);
        }
    }

    private int useTheSpecBreadth(final int specBreadth) {
        mMeasuredContentBreadth = NOT_WRAPPING;
        return specBreadth;
    }

    private int getWrappedBreadthWithin(final ViewGroup viewGroup, final int specBreadth) {
        final int wrappedBreadth = getWrappedBreadth(viewGroup);
        return Math.min(wrappedBreadth, specBreadth);
    }

    private int getWrappedBreadth(final ViewGroup viewGroup) {
        final int contentBreadth = getContentBreadth(viewGroup);
        final int breadthPadding = mScrollDirectionManager.getViewGroupBreadthPadding(viewGroup);
        mMeasuredContentBreadth = contentBreadth;
        return contentBreadth + breadthPadding;
    }

    private void requestLayoutWhenADrawnCellOutgrowsTheMeasure() {
        final boolean isWrapping = mMeasuredContentBreadth != NOT_WRAPPING;
        if (!isWrapping) return;

        final int largestDrawnCellBreadth = getLargestDrawnCellBreadth();
        final boolean aDrawnCellOutgrewTheMeasure =
                largestDrawnCellBreadth > mMeasuredContentBreadth;
        if (!aDrawnCellOutgrewTheMeasure) return;

        mMeasuredContentBreadth = largestDrawnCellBreadth;
        mViewGroup.post(mRequestLayoutRunnable);
    }

    private int getContentBreadth(final ViewGroup viewGroup) {
        final boolean cellsAreDrawn = !mCells.isEmpty();
        if (cellsAreDrawn) return getLargestDrawnCellBreadth();
        return getLargestViewportCellBreadth(viewGroup);
    }

    private int getLargestDrawnCellBreadth() {
        int largestBreadth = NO_BREADTH;
        for (int cellIndex = 0; cellIndex < mCells.size(); cellIndex++) {
            final Cell cell = mCells.get(cellIndex);
            final int cellBreadth = getCellBreadth(cell);
            largestBreadth = Math.max(largestBreadth, cellBreadth);
        }
        return largestBreadth;
    }

    private int getLargestViewportCellBreadth(final ViewGroup viewGroup) {
        final boolean adapterIsEmpty = mAdapterViewManager.isEmpty();
        if (adapterIsEmpty) return NO_BREADTH;

        final int viewGroupSize = mScrollDirectionManager.getViewGroupSize(viewGroup);
        final int cellSpacing = getCellSpacing();
        final int cellCount = getCellCount();
        final int adapterCount = getAdapterCount();
        int largestBreadth = NO_BREADTH;
        int nextCellStart = getStartSizePadding();
        int cellPosition = getStartCellPositionToDraw();
        for (int cellsMeasured = 0; cellsMeasured < cellCount; cellsMeasured++) {
            final boolean viewportIsFilled = nextCellStart > viewGroupSize;
            if (viewportIsFilled) break;

            final int adapterPosition = getFirstAdapterPositionInCell(cellPosition);
            final boolean aboveCount = adapterPosition >= adapterCount;
            if (aboveCount) break;

            final Cell cell = getCell(adapterPosition);
            final int cellBreadth = getCellBreadth(cell);
            final int cellSize = getCellSize(cell);
            recycleViews(cell);
            final int cellStep = cellSize + cellSpacing;
            largestBreadth = Math.max(largestBreadth, cellBreadth);
            nextCellStart += cellStep;
            cellPosition = incrementCellPosition(cellPosition);
        }
        return largestBreadth;
    }

    private int getStartCellPositionToDraw() {
        final int adapterCount = getAdapterCount();
        final int firstAdapterPosition = getFirstAdapterPositionInCell(mStartCellPosition);
        final boolean startIsPastTheAdapter = firstAdapterPosition >= adapterCount;
        if (!startIsPastTheAdapter) return mStartCellPosition;

        final int lastAdapterPosition = Math.max(adapterCount - 1, 0);
        return getCellPosition(lastAdapterPosition);
    }

    private void recycleViews(final Cell cell) {
        final List<View> views = getViews(cell);
        for (final View view : views) {
            mAdapterViewManager.recycle(view);
        }
    }

    public abstract View getLastView(final Cell cell);

    public abstract View getView(final Cell cell);

    public abstract View getFirstView(final Cell cell);

    public abstract int getCellStart(final Cell cell);

    public abstract int getCellEnd(final Cell cell);

    public abstract int getCellSize(final Cell cell);

    public abstract int getCellBreadth(final Cell cell);

    public abstract List<View> getViews(final Cell cell);

    public abstract int getCellViewCount(final Cell cell);

    public abstract View getCellView(final Cell cell, final int viewIndex);

    protected abstract Cell getCell(final int adapterPosition);

    public abstract void layoutCell(
            final Cell cell,
            final int cellStart,
            final int cellEnd,
            final int firstAdapterPositionInCell,
            final int breadth,
            final int cellSpacing);

    public abstract void measure(final Cell cell, final ViewGroup viewGroup);

    public abstract View getLastAdapterPositionView(final Cell cell);

    public abstract View getFirstAdapterPositionView(final Cell cell);

    protected abstract int getCellCount();

    protected abstract int getChildHeightMeasureSpecSize(final int position);

    protected abstract int getChildWidthMeasureSpecSize(final int position);

    protected abstract int getCellPosition(final int adapterPosition);

    protected abstract int getFirstAdapterPositionInCell(final int cellPosition);

    protected abstract int getDrawPosition(final List<Cell> cells, final int drawCellPosition);

    protected abstract int getChildWidthMeasureSpecMode();

    protected abstract int getChildHeightMeasureSpecMode();

    public void layout(
            final AdapterViewHandler adapterViewHandler,
            final Animation animation,
            final int left,
            final int top,
            final int right,
            final int bottom) {

        mStartCellPosition = getStartCellPositionToDraw();

        final int size = mScrollDirectionManager.getDrawSize(left, top, right, bottom);
        final int displacement = getDisplacementToApply(animation);

        if (mIsFirstLayout) {
            final boolean hasViews = getAdapterCount() != 0;
            if (hasViews) {
                mIsFirstLayout = false;
                final Move move = Move.none;

                final Cell cell = getCell(0);
                final int cellSize = getCellSize(cell);
                mOffset =
                        mSnapPositionInterface.getAbsoluteSnapPosition(this, size, cellSize, move);
                recycleViews(cell);
            }
        }

        final int animationId = animation.getId();
        final boolean continuedAnimation = animationId == mAnimationId;

        final int startSizePadding = getStartSizePadding();
        final int endSizePadding = getEndSizePadding();

        final int newSize = size - startSizePadding - endSizePadding;

        if (!continuedAnimation && !mCells.isEmpty()) {
            mAnimationId = animationId;
            mAnimationIsAGesture = animation.isAGesture();
            setViewPageDistances(newSize);
            mAnimationDisplacement = displacement;

        } else if (continuedAnimation) {
            mAnimationDisplacement += displacement;
        }

        final int adjust = setOffset(displacement, newSize);

        mAnimationDisplacement += adjust;

        mFrameDisplacement = displacement + adjust;

        final int breadth = mScrollDirectionManager.getDrawBreadth(left, top, right, bottom);
        layoutCells(adapterViewHandler, newSize, breadth);

        if (resetWhenNoCellsAreDrawn(newSize, displacement)) {
            onAnimationStopped();
            mAnimationDisplacement = 0;
            mFrameDisplacement = 0;
            layoutCells(adapterViewHandler, newSize, breadth);
        }

        correctOverScroll(adapterViewHandler, newSize, breadth);
        holdTheSeekTarget(adapterViewHandler, animation, displacement, newSize, breadth);
        requestLayoutWhenADrawnCellOutgrowsTheMeasure();
        stopWhenAJumpIsOwed();
        stopAndSelectWhenHeld(newSize);

        checkSelectWhileScrollingAttribute(newSize);
        mSelectedPositionManager.onViewsDrawn(mPositions);
    }

    public int getStartSizePadding() {
        if (isVerticalScroll()) {
            return mViewGroup.getPaddingTop();
        }
        return mViewGroup.getPaddingLeft();
    }

    public int getEndSizePadding() {
        if (isVerticalScroll()) {
            return mViewGroup.getPaddingBottom();
        }
        return mViewGroup.getPaddingRight();
    }

    public int getStartBreadthPadding() {
        if (isVerticalScroll()) {
            return mViewGroup.getPaddingLeft();
        }
        return mViewGroup.getPaddingTop();
    }

    public int getEndBreadthPadding() {
        if (isVerticalScroll()) {
            return mViewGroup.getPaddingRight();
        }
        return mViewGroup.getPaddingBottom();
    }

    private boolean resetWhenNoCellsAreDrawn(final int size, final int scrollDisplacement) {
        final int adapterCount = mAdapterViewManager.getAdapterCount();
        final boolean haveCellsToDraw = adapterCount > 0;
        final boolean noCellsBeingDrawn = mCells.isEmpty();
        if (!haveCellsToDraw || !noCellsBeingDrawn) {
            return false;
        }

        final Move direction = getMove(scrollDisplacement);
        if (scrollDisplacement > 0) {
            final Cell cell = getCell(0);
            final int cellSize = getCellSize(cell);
            mOffset =
                    mSnapPositionInterface.getAbsoluteSnapPosition(this, size, cellSize, direction);
            mStartCellPosition = 0;
        } else if (scrollDisplacement < 0) {
            final Cell cell = getCell(adapterCount - 1);
            final int cellSize = getCellSize(cell);
            mOffset =
                    mSnapPositionInterface.getAbsoluteSnapPosition(this, size, cellSize, direction);
            mStartCellPosition = getCellCount() - 1;
        }
        return true;
    }

    private void correctOverScroll(
            final AdapterViewHandler adapterViewHandler, final int size, final int breadth) {
        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();
        if (mCells.isEmpty() || isCircularScroll) {
            return;
        }

        final int forwardCorrection = getMoveForwardOverDrawAdjust(size, 0);
        applyOverScrollCorrection(adapterViewHandler, size, breadth, forwardCorrection);

        final int backwardCorrection = getMoveBackwardOverDrawAdjust(size, 0);
        applyOverScrollCorrection(adapterViewHandler, size, breadth, backwardCorrection);
    }

    private void applyOverScrollCorrection(
            final AdapterViewHandler adapterViewHandler,
            final int size,
            final int breadth,
            final int correction) {
        if (correction == 0) {
            return;
        }
        mAnimationDisplacement = 0;
        mFrameDisplacement += correction;
        mOffset += correction;
        layoutCells(adapterViewHandler, size, breadth);
    }

    private void holdTheSeekTarget(
            final AdapterViewHandler adapterViewHandler,
            final Animation animation,
            final int displacement,
            final int size,
            final int breadth) {
        final int seekTarget = animation.getSeekTarget();
        final boolean isSeeking = seekTarget != Animation.NO_SEEK_TARGET;
        if (!isSeeking) return;

        final boolean isDrawn = isPositionDrawn(seekTarget);
        if (!isDrawn) return;

        final int overshoot = getSeekOvershoot(seekTarget, displacement, size);
        applyOverScrollCorrection(adapterViewHandler, size, breadth, overshoot);
    }

    private int getSeekOvershoot(final int seekTarget, final int displacement, final int size) {
        final int cellPosition = getCellPosition(seekTarget);
        final long cellIndex = getCellIndexOf(cellPosition);
        final int distanceLeft = getScrollDistanceToADrawnCell(size, cellIndex);
        final int seekDirection = Integer.signum(displacement);
        final int directionLeft = Integer.signum(distanceLeft);
        final boolean theFramePassedTheTarget = directionLeft == -seekDirection;
        if (theFramePassedTheTarget) return distanceLeft;
        return 0;
    }

    private int getDisplacementToApply(final Animation animation) {
        final boolean theJumpSupersedesTheFrame = isAJumpStoppingAnAnimation();
        if (theJumpSupersedesTheFrame) return 0;
        return animation.getDisplacement();
    }

    private void stopWhenAJumpIsOwed() {
        final boolean stops = isAJumpStoppingAnAnimation();
        mAStopIsOwedToAJump = false;
        if (!stops) return;

        mHeldCell = null;
        onAnimationStopped();
    }

    private boolean isAJumpStoppingAnAnimation() {
        if (!mAStopIsOwedToAJump) return false;

        final boolean aFingerIsDown = isAFingerDown();
        return !aFingerIsDown;
    }

    private boolean isAFingerDown() {
        if (mAnimationStoppedListener == null) return false;
        return mAnimationStoppedListener.isAFingerDown();
    }

    private void stopAndSelectWhenHeld(final int size) {
        final Cell heldCell = mHeldCell;
        mHeldCell = null;
        if (heldCell == null) return;

        onAnimationStopped();
        selectTheCellAtTheSnapPosition(size, heldCell);
    }

    private void selectTheCellAtTheSnapPosition(final int size, final Cell heldCell) {
        final boolean selectOnSnap = mLayoutManagerAttributes.selectOnSnap();
        final boolean snapToPosition = mLayoutManagerAttributes.isSnapToPosition();
        final boolean selectsWhenHeld = selectOnSnap && snapToPosition;
        if (!selectsWhenHeld) return;

        final Cell cellAtTheSnapPosition = getCellAtTheSnapPositionWhenHeld(size, heldCell);
        final View selectedView = getView(cellAtTheSnapPosition);
        setSelected(selectedView);
    }

    private Cell getCellAtTheSnapPositionWhenHeld(final int size, final Cell heldCell) {
        final Cell cellToSnapTo = getCellToSnapTo(size);
        if (cellToSnapTo == null) return heldCell;
        return cellToSnapTo;
    }

    private void checkSelectWhileScrollingAttribute(final int newWidth) {
        final boolean shouldSelectWhileScrolling =
                mLayoutManagerAttributes.selectWhileScrolling()
                        && !mLayoutManagerAttributes.isSnapPositionOnScreen();
        if (shouldSelectWhileScrolling) {
            final View view = getNearestViewToSnapPosition(newWidth);
            final int position = getPosition(view);
            mSelectedPositionManager.setSelectedPosition(position);
        }
    }

    protected void setViewPageDistances(final int size) {
        final boolean isViewPager = mLayoutManagerAttributes.isViewPager();
        if (!isViewPager) return;

        final int anchorIndex = getNearestCellIndexToSnapPosition(size);
        final Cell anchorCell = mCells.get(anchorIndex);
        final int anchorStart = getCellStart(anchorCell);
        final int anchorSnapDisplacement = getSnapDisplacement(size, anchorCell);
        final int anchorSnappedStart = anchorStart + anchorSnapDisplacement;
        final int viewportEnd = getStartSizePadding() + size;
        final int maximumPageSize = viewportEnd - anchorSnappedStart;

        final long forwardCellIndex =
                mPageIntervalInterface.getPageCellIndexForward(
                        this, maximumPageSize, anchorIndex, anchorStart);
        final int forwardCellStart = getCellStartAtIndex(forwardCellIndex);
        mViewPageDistanceForward = forwardCellStart - anchorSnappedStart;

        final long backCellIndex =
                mPageIntervalInterface.getPageCellIndexBack(
                        this, maximumPageSize, anchorIndex, anchorStart);
        final int backCellStart = getCellStartAtIndex(backCellIndex);
        mViewPageDistanceBack = anchorSnappedStart - backCellStart;
    }

    protected int getSnapDisplacement(final int size, final Cell cell) {
        return mSnapPositionInterface.getUnboundedSnapToPixelDistance(this, size, cell);
    }

    public int getCellStartAtIndex(final long cellIndex) {
        final int lastCellIndex = mCells.size() - 1;

        final boolean isBeforeTheFirstCell = cellIndex < 0;
        if (isBeforeTheFirstCell) return getCellStartExtrapolatedBeforeFirst(cellIndex);

        final boolean isAfterTheLastCell = cellIndex > lastCellIndex;
        if (isAfterTheLastCell) return getCellStartExtrapolatedAfterLast(cellIndex);

        return getCellStart(mCells.get((int) cellIndex));
    }

    protected int getCellStartExtrapolatedBeforeFirst(final long cellIndex) {
        final int cellSpacing = getCellSpacing();
        final Cell firstCell = mCells.get(0);
        final long step = getCellSize(firstCell) + cellSpacing;
        final long steps = Math.min(-cellIndex, getCellsBeforeFirst());
        final long cellStart = getCellStart(firstCell) - steps * step;
        return getDrawableCellStart(cellStart);
    }

    protected int getCellStartExtrapolatedAfterLast(final long cellIndex) {
        final int cellSpacing = getCellSpacing();
        final int lastCellIndex = mCells.size() - 1;
        final Cell lastCell = mCells.get(lastCellIndex);
        final long step = getCellSize(lastCell) + cellSpacing;
        final long steps = Math.min(cellIndex - lastCellIndex, getCellsAfterLast());
        final long cellStart = getCellStart(lastCell) + steps * step;
        return getDrawableCellStart(cellStart);
    }

    private int getDrawableCellStart(final long cellStart) {
        if (cellStart > MAX) return MAX;
        if (cellStart < -MAX) return -MAX;
        return (int) cellStart;
    }

    private int getNearestCellIndexToSnapPosition(final int size) {
        int nearestCellIndex = 0;
        int nearestCellDistance = getCellSettleDistance(size, nearestCellIndex);

        for (int cellIndex = 1; cellIndex < mCells.size(); cellIndex++) {
            final int currentCellDistance = getCellSettleDistance(size, cellIndex);
            final boolean currentCellIsCloser =
                    isCloserToTheSnapPosition(
                            size,
                            cellIndex,
                            currentCellDistance,
                            nearestCellIndex,
                            nearestCellDistance);

            if (currentCellIsCloser) {
                nearestCellDistance = currentCellDistance;
                nearestCellIndex = cellIndex;
            }
        }

        return nearestCellIndex;
    }

    private int getCellSettleDistance(final int size, final int cellIndex) {
        final Cell cell = mCells.get(cellIndex);
        return mSnapPositionInterface.getCellSettleDistance(this, mCells, size, cell);
    }

    private boolean isCloserToTheSnapPosition(
            final int size,
            final int cellIndex,
            final int cellDistance,
            final int nearestCellIndex,
            final int nearestCellDistance) {
        final boolean settlesNearer = cellDistance < nearestCellDistance;
        if (settlesNearer) return true;

        final boolean settlesAsNear = cellDistance == nearestCellDistance;
        if (!settlesAsNear) return false;

        final int distanceFromTheSnapPosition = getCellDistanceFromSnapPosition(size, cellIndex);
        final int nearestDistanceFromTheSnapPosition =
                getCellDistanceFromSnapPosition(size, nearestCellIndex);
        return distanceFromTheSnapPosition < nearestDistanceFromTheSnapPosition;
    }

    private int getCellDistanceFromSnapPosition(final int size, final int cellIndex) {
        final Cell cell = mCells.get(cellIndex);
        return mSnapPositionInterface.getCellDistanceFromSnapPosition(this, size, cell);
    }

    private View getNearestViewToSnapPosition(final int size) {
        final Cell nearestCell = getNearestCellToSnapPosition(size);
        if (nearestCell == null) {
            return null;
        }

        return getView(nearestCell);
    }

    private Cell getNearestCellToSnapPosition(final int size) {
        if (mCells.isEmpty()) {
            return null;
        }

        final int nearestCellIndex = getNearestCellIndexToSnapPosition(size);
        return mCells.get(nearestCellIndex);
    }

    private int setOffset(final int displacement, final int size) {
        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();
        final int overDrawAdjust = getOverDrawAdjust(isCircularScroll, size, displacement);
        mOffset += displacement + overDrawAdjust;
        return overDrawAdjust;
    }

    private SnapPosition getSnapPosition(final boolean isCircularScroll) {
        if (isCircularScroll) return SnapPosition.onScreen;
        return mLayoutManagerAttributes.getSnapPosition();
    }

    public int getCellSizeTotal() {
        int viewSizeTotal = 0;
        for (final Cell cell : mCells) viewSizeTotal += getCellSize(cell);
        final int cellSpacingCount = Math.max(0, mCells.size() - 1);
        final int cellSpacing = getCellSpacing();
        return viewSizeTotal + cellSpacingCount * cellSpacing;
    }

    private int getOverDrawAdjust(
            final boolean isCircularScroll, final int size, final int displacement) {
        final boolean viewsBeingDrawn = !mCells.isEmpty();
        if (!viewsBeingDrawn || isCircularScroll) return 0;

        final boolean isViewPager = mLayoutManagerAttributes.isViewPager();
        final boolean isHeldToAPage = isViewPager && mAnimationIsAGesture;
        if (isHeldToAPage) {
            final int viewPageDistance = getViewPageDistanceForAnimation();
            final int animationDistance = Math.abs(mAnimationDisplacement);
            final boolean isPastTheViewPage = animationDistance > viewPageDistance;
            if (isPastTheViewPage) return -displacement;
        }

        final Move move = getMove(displacement);
        switch (move) {
            case none:
            case back:
                return getMoveBackwardOverDrawAdjust(size, displacement);
            case forward:
                return getMoveForwardOverDrawAdjust(size, displacement);
        }

        return 0;
    }

    private int getMoveBackwardOverDrawAdjust(final int size, final int displacement) {
        final int index = mCells.size() - 1;

        final Cell cell = mCells.get(index);
        final View view = getLastAdapterPositionView(cell);

        final int position = mPositions.get(view);
        final boolean isLastPosition = position == mAdapterViewManager.getAdapterCount() - 1;
        if (!isLastPosition) {
            return 0;
        }

        final int drawLimit =
                mSnapPositionInterface.getDrawLimitMoveBackwardOverDrawAdjust(
                        this, mCells, size, cell);

        final int startMostPixel = getCellStart(cell) + displacement;
        final boolean isOverDrawn = startMostPixel < drawLimit;
        if (!isOverDrawn) {
            return 0;
        }

        mHeldCell = cell;

        final int overDrawAdjust = drawLimit - startMostPixel;
        return overDrawAdjust;
    }

    private int getMoveForwardOverDrawAdjust(final int size, final int displacement) {

        final int firstIndex = 0;
        final Cell firstCell = mCells.get(firstIndex);

        final View firstViewInCell = getFirstAdapterPositionView(firstCell);

        final int position = mPositions.get(firstViewInCell);
        final boolean isFirstPosition = position == 0;
        if (!isFirstPosition) {
            return 0;
        }

        final int drawLimit =
                mSnapPositionInterface.getDrawLimitMoveForwardOverDrawAdjust(
                        this, mCells, size, firstCell);

        final int endMostPixel = getCellEnd(firstCell) + displacement;
        final boolean isOverDrawn = endMostPixel > drawLimit;
        if (!isOverDrawn) {
            return 0;
        }

        mHeldCell = firstCell;

        final int overDrawAdjust = drawLimit - endMostPixel;
        return overDrawAdjust;
    }

    private Cell getCellToSnapTo(final int size) {
        if (mCells.size() == 0) return null;

        final SnapPosition snapPosition = mLayoutManagerAttributes.getSnapPosition();
        final boolean snapPositionIsOnScreen = snapPosition == SnapPosition.onScreen;
        if (snapPositionIsOnScreen) return null;

        final Cell nearestCell = getNearestCellToSnapPosition(size);
        return nearestCell;
    }

    protected final ScrollDirectionManager getScrollDirectionManager() {
        return mScrollDirectionManager;
    }

    protected final int getFrameDisplacement() {
        return mFrameDisplacement;
    }

    protected final int getDrawnCellCount() {
        return mCells.size();
    }

    protected final int getFirstVisibleAdapterPosition() {
        final int drawnCellCount = mCells.size();
        for (int cellIndex = 0; cellIndex < drawnCellCount; cellIndex++) {
            final Cell cell = mCells.get(cellIndex);
            final boolean isOnScreen = isCellOnScreen(cell);
            if (isOnScreen) {
                final View firstView = getCellView(cell, FIRST_VIEW_IN_CELL);
                return getPosition(firstView);
            }
        }
        return INVALID_POSITION;
    }

    protected final int getLastVisibleAdapterPosition() {
        final int lastCellIndex = mCells.size() - 1;
        for (int cellIndex = lastCellIndex; cellIndex >= 0; cellIndex--) {
            final Cell cell = mCells.get(cellIndex);
            final boolean isOnScreen = isCellOnScreen(cell);
            if (isOnScreen) {
                final int viewCount = getCellViewCount(cell);
                final View lastView = getCellView(cell, viewCount - 1);
                return getPosition(lastView);
            }
        }
        return INVALID_POSITION;
    }

    private boolean isCellOnScreen(final Cell cell) {
        final int visibleStart = getStartSizePadding();
        final int laidOutSize = mScrollDirectionManager.getViewGroupLaidOutSize(mViewGroup);
        final int visibleEnd = laidOutSize - getEndSizePadding();
        final int cellStart = getCellStart(cell);
        final int cellEnd = getCellEnd(cell);
        final boolean endsAfterTheStartEdge = cellEnd > visibleStart;
        final boolean startsBeforeTheEndEdge = cellStart < visibleEnd;
        return endsAfterTheStartEdge && startsBeforeTheEndEdge;
    }

    protected final int getDrawnCellViewCount(final int cellIndex) {
        final Cell cell = mCells.get(cellIndex);
        return getCellViewCount(cell);
    }

    protected final View getDrawnCellView(final int cellIndex, final int viewIndex) {
        final Cell cell = mCells.get(cellIndex);
        return getCellView(cell, viewIndex);
    }

    protected final int getDrawnCellStart(final int cellIndex) {
        final Cell cell = mCells.get(cellIndex);
        return getCellStart(cell);
    }

    protected final int getDrawnCellEnd(final int cellIndex) {
        final Cell cell = mCells.get(cellIndex);
        return getCellEnd(cell);
    }

    public int getCellCenter(final Cell cell) {
        return (getCellStart(cell) + getCellEnd(cell)) / 2;
    }

    private int getSnapToPixelDistance(final int size, final Cell cell) {
        return mSnapPositionInterface.getSnapToPixelDistance(this, mCells, size, cell);
    }

    public int getSnapToPixelDistanceForView(final int size, final View view) {
        final Cell cell = getCellContainingView(view);
        if (cell == null) return 0;

        return getSnapToPixelDistance(size, cell);
    }

    private Cell getCellContainingView(final View view) {
        for (final Cell cell : mCells) {
            final List<View> views = getViews(cell);
            final boolean cellHoldsTheView = views.contains(view);
            if (cellHoldsTheView) return cell;
        }

        return null;
    }

    public int getAdapterCount() {
        return mAdapterViewManager.getAdapterCount();
    }

    /**
     * When moving left, every time a view is removed, this means that we are removing the leftMost
     * view and therefore have to increment the mOffset by the removed view's width
     */
    private void layoutCells(
            final AdapterViewHandler adapterViewHandler, final int size, final int breadth) {
        final int startSizePadding = getStartSizePadding();
        final int endSizePadding = getEndSizePadding();
        mLayoutCellCount = 0;
        mLayoutSize = 0;

        final int cellSpacing = mLayoutManagerAttributes.getCellSpacing();
        int currentOffset = mOffset;
        int endCellPosition = mStartCellPosition;

        for (final Cell cell : new ArrayList<Cell>(mCells)) {
            final int cellStart = currentOffset;
            final int cellSize = getCellSize(cell);
            final int cellEnd = cellStart + cellSize;

            currentOffset = cellEnd + cellSpacing;
            final boolean cellIsOffScreenBehind = cellEnd < 0;
            final boolean cellIsOffScreenAhead =
                    cellStart > endSizePadding + size + startSizePadding;

            if (cellIsOffScreenBehind) {
                mOffset = currentOffset;
                mCells.remove(cell);

                final List<View> viewsToRemove = getViews(cell);
                for (final View view : viewsToRemove) {
                    adapterViewHandler.removeViewInAdapterView(view);
                    mAdapterViewManager.recycle(view);
                    mPositions.remove(view);
                }

                incrementStartCellPosition();
                endCellPosition = incrementCellPosition(endCellPosition);
            } else if (cellIsOffScreenAhead) {
                mCells.remove(cell);

                final List<View> viewsToRemove = getViews(cell);
                for (final View view : viewsToRemove) {
                    adapterViewHandler.removeViewInAdapterView(view);
                    mAdapterViewManager.recycle(view);
                    mPositions.remove(view);
                }
            } else {
                final int firstViewInCell = getFirstAdapterPositionInCell(endCellPosition);
                layoutCell(cell, cellStart, cellEnd, firstViewInCell, breadth, cellSpacing);
                endCellPosition = incrementCellPosition(endCellPosition);
                mLayoutCellCount++;
                mLayoutSize += cellSize;
            }
        }

        while (currentOffset <= size + startSizePadding + endSizePadding) {
            final int firstAdapterPosition = getFirstAdapterPositionInCell(endCellPosition);
            final int adapterCount = mAdapterViewManager.getAdapterCount();
            final boolean aboveCount = firstAdapterPosition >= adapterCount;
            if (aboveCount) break;

            final boolean isPositionBeingDrawn = isPositionBeingDrawn(firstAdapterPosition);
            if (isPositionBeingDrawn) break;

            final Cell cell = getCell(firstAdapterPosition);
            final int cellStart = currentOffset;
            final int cellSize = getCellSize(cell);
            final int cellEnd = cellStart + cellSize;
            currentOffset = cellEnd + cellSpacing;

            final boolean removeCell = cellEnd < 0;

            if (removeCell) {
                mOffset = currentOffset;
                final List<View> views = getViews(cell);
                for (final View view : views) {
                    mAdapterViewManager.recycle(view);
                }
                incrementStartCellPosition();
            } else {

                layoutCell(cell, cellStart, cellEnd, firstAdapterPosition, breadth, cellSpacing);

                final int drawCellPosition = mCells.size();
                int drawPosition = getDrawPosition(mCells, drawCellPosition);
                int position = firstAdapterPosition;
                final List<View> views = getViews(cell);
                for (final View view : views) {
                    adapterViewHandler.addViewInAdapterView(
                            view, drawPosition++, view.getLayoutParams());
                    mPositions.put(view, position++);
                }

                mCells.add(cell);
                mLayoutCellCount++;
                mLayoutSize += cellSize;
            }

            endCellPosition = incrementCellPosition(endCellPosition);
        }

        currentOffset = mOffset;
        int cellPosition = decrementCellPosition(mStartCellPosition);

        while (currentOffset > 0) {
            final int adapterPosition = getFirstAdapterPositionInCell(cellPosition);
            final boolean belowCount = adapterPosition < 0;
            if (belowCount) break;

            final boolean isPositionBeingDrawn = isPositionBeingDrawn(adapterPosition);
            if (isPositionBeingDrawn) break;

            final Cell cell = getCell(adapterPosition);

            final int cellEnd = currentOffset - cellSpacing;
            final int cellSize = getCellSize(cell);
            final int cellStart = cellEnd - cellSize;
            currentOffset = cellStart;

            final boolean cellIsOnScreen = cellStart <= size + startSizePadding + endSizePadding;

            if (cellIsOnScreen) {
                layoutCell(cell, cellStart, cellEnd, cellPosition, breadth, cellSpacing);

                int position = adapterPosition;
                int drawPosition = 0;
                final List<View> views = getViews(cell);
                for (final View view : views) {
                    adapterViewHandler.addViewInAdapterView(
                            view, drawPosition++, view.getLayoutParams());
                    mPositions.put(view, position++);
                }
                mCells.add(0, cell);
                mStartCellPosition = cellPosition;
                mLayoutCellCount++;
                mLayoutSize += cellSize;
            } else {
                final List<View> views = getViews(cell);
                for (final View view : views) {
                    mAdapterViewManager.recycle(view);
                }
            }
            cellPosition = decrementCellPosition(cellPosition);
        }
        mOffset = currentOffset;
    }

    protected abstract int getLastAdapterPositionInCell(final int cellPosition);

    private boolean isPositionBeingDrawn(final int position) {
        for (final Integer mappedPosition : mPositions.values()) {
            if (position == mappedPosition) return true;
        }

        return false;
    }

    private int incrementCellPosition(int endCellPosition) {
        if (mAdapterViewManager.isEmpty()) {
            return endCellPosition;
        }

        endCellPosition++;

        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();

        final int cellCount = getCellCount();
        if (endCellPosition >= cellCount && isCircularScroll) {
            return 0;
        }

        if (!isCircularScroll) {
            endCellPosition = Math.min(cellCount, endCellPosition);
        }

        return endCellPosition;
    }

    private void incrementStartCellPosition() {
        if (mAdapterViewManager.isEmpty()) {
            return;
        }

        mStartCellPosition++;

        final int cellCount = getCellCount();
        if (mStartCellPosition >= cellCount || mStartCellPosition < 0) {
            mStartCellPosition = 0;
        }
    }

    private int decrementCellPosition(int position) {
        if (mAdapterViewManager.isEmpty()) {
            return position;
        }

        position--;

        final int cellCount = getCellCount();
        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();
        if (position < 0 && isCircularScroll) {
            position = cellCount - 1;
        }

        if (!isCircularScroll) {
            position = Math.max(-1, position);
        }
        return position;
    }

    protected boolean isViewSelected(final int position) {
        final boolean isSelected = position == mSelectedPositionManager.getSelectedPosition();
        return isSelected;
    }

    public int getPosition(final View view) {
        if (mPositions.containsKey(view)) return mPositions.get(view);
        return INVALID_POSITION;
    }

    public int getViewPagerScrollDistance(final Move move) {
        switch (move) {
            case forward:
                return -mViewPageDistanceForward - mAnimationDisplacement;
            case back:
            case none:
            default:
                return mViewPageDistanceBack - mAnimationDisplacement;
        }
    }

    private int getViewPageDistanceForAnimation() {
        final boolean isMovingToLaterCells = mAnimationDisplacement < 0;
        if (isMovingToLaterCells) return mViewPageDistanceForward;
        return mViewPageDistanceBack;
    }

    public int getFlingSnapAdjustment(final ViewGroup viewGroup, final int displacement) {
        final boolean isSnapToPosition = mLayoutManagerAttributes.isSnapToPosition();
        final boolean isOnScreen = mLayoutManagerAttributes.isSnapPositionOnScreen();
        if (!isSnapToPosition || isOnScreen || mCells.isEmpty()) return 0;

        final int size = getSizeInsidePadding(viewGroup);
        final int cellSpacing = getCellSpacing();
        final Cell firstCell = mCells.get(0);
        final Cell lastCell = mCells.get(mCells.size() - 1);
        final int firstDistance = getSnapToPixelDistance(size, firstCell);
        final int lastDistance = getSnapToPixelDistance(size, lastCell);

        if (displacement > firstDistance) {
            final int step = getCellSize(firstCell) + cellSpacing;
            final int steps =
                    getExtrapolationSteps(
                            displacement - firstDistance, step, getCellsBeforeFirst());
            return firstDistance + steps * step - displacement;
        }

        if (displacement < lastDistance) {
            final int step = getCellSize(lastCell) + cellSpacing;
            final int steps =
                    getExtrapolationSteps(lastDistance - displacement, step, getCellsAfterLast());
            return lastDistance - steps * step - displacement;
        }

        int nearestDistance = firstDistance;
        for (final Cell cell : mCells) {
            final int distance = getSnapToPixelDistance(size, cell);
            final boolean isNearer =
                    Math.abs(distance - displacement) < Math.abs(nearestDistance - displacement);
            if (isNearer) nearestDistance = distance;
        }
        return nearestDistance - displacement;
    }

    private int getExtrapolationSteps(final int distance, final int step, final int cellLimit) {
        final int steps = Math.round(distance / (float) step);
        return Math.min(steps, cellLimit);
    }

    private int getCellsBeforeFirst() {
        if (mLayoutManagerAttributes.isCircularScroll()) return Integer.MAX_VALUE;
        return mStartCellPosition;
    }

    private int getCellsAfterLast() {
        if (mLayoutManagerAttributes.isCircularScroll()) return Integer.MAX_VALUE;
        final int lastCellPosition = mStartCellPosition + mCells.size() - 1;
        return getCellCount() - 1 - lastCellPosition;
    }

    public boolean hasAScrollTarget() {
        final boolean adapterHasCells = getAdapterCount() > 0;
        final boolean cellsAreDrawn = !mCells.isEmpty();
        return adapterHasCells && cellsAreDrawn;
    }

    public int clampToAdapter(final int position) {
        final int lastPosition = getAdapterCount() - 1;
        final int atMostTheLast = Math.min(position, lastPosition);
        return Math.max(atMostTheLast, 0);
    }

    public boolean isPositionDrawn(final int position) {
        final int cellPosition = getCellPosition(position);
        final long cellIndex = getCellIndexOf(cellPosition);
        return isDrawn(cellIndex);
    }

    private boolean isDrawn(final long cellIndex) {
        final boolean isBeforeTheFirstCell = cellIndex < 0;
        final boolean isAfterTheLastCell = cellIndex >= mCells.size();
        return !isBeforeTheFirstCell && !isAfterTheLastCell;
    }

    public int getScrollToPositionDistance(final ViewGroup viewGroup, final int position) {
        final int size = getSizeInsidePadding(viewGroup);
        final int cellPosition = getCellPosition(position);
        final long cellIndex = getCellIndexOf(cellPosition);

        final boolean isDrawn = isDrawn(cellIndex);
        if (isDrawn) return getScrollDistanceToADrawnCell(size, cellIndex);

        final long way = getWayToAnUndrawnCell(size, cellIndex);
        return getScrollDistanceToAnUndrawnCell(size, way);
    }

    public int getSeekStepLimit(final ViewGroup viewGroup, final int position) {
        final int size = getSizeInsidePadding(viewGroup);
        final int cellPosition = getCellPosition(position);
        final long cellIndex = getCellIndexOf(cellPosition);

        final boolean isDrawn = isDrawn(cellIndex);
        if (isDrawn) return getScrollDistanceToADrawnCell(size, cellIndex);

        final long way = getWayToAnUndrawnCell(size, cellIndex);
        return getSeekStepLimitToAnUndrawnCell(viewGroup, size, way);
    }

    public int getSeekDistance(final ViewGroup viewGroup, final int position) {
        final int distance = getScrollToPositionDistance(viewGroup, position);
        final int direction = Integer.signum(distance);
        final int runway = getSeekRunway(viewGroup);
        final int overshoot = direction * runway;
        return distance + overshoot;
    }

    public int getSeekRunway(final ViewGroup viewGroup) {
        return getSizeInsidePadding(viewGroup);
    }

    private int getScrollDistanceToADrawnCell(final int size, final long cellIndex) {
        final Cell cell = mCells.get((int) cellIndex);
        return getSnapToPixelDistance(size, cell);
    }

    private long getWayToAnUndrawnCell(final int size, final long cellIndex) {
        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();
        if (isCircularScroll) return getShorterWayRound(size, cellIndex);
        return cellIndex;
    }

    private long getShorterWayRound(final int size, final long cellsForward) {
        final int cellCount = getCellCount();
        final long cellsBack = cellsForward - cellCount;
        final int forward = getScrollDistanceExtrapolatedAfterLast(size, cellsForward);
        final int back = getScrollDistanceExtrapolatedBeforeFirst(size, cellsBack);
        final boolean backIsShorter = Math.abs(back) < Math.abs(forward);
        if (backIsShorter) return cellsBack;
        return cellsForward;
    }

    private int getScrollDistanceToAnUndrawnCell(final int size, final long way) {
        final boolean isBeforeTheFirstCell = way < 0;
        if (isBeforeTheFirstCell) return getScrollDistanceExtrapolatedBeforeFirst(size, way);
        return getScrollDistanceExtrapolatedAfterLast(size, way);
    }

    private int getSeekStepLimitToAnUndrawnCell(
            final ViewGroup viewGroup, final int size, final long way) {
        final int distance = getScrollDistanceToAnUndrawnCell(size, way);
        final int keepsTheEdgeCellDrawn = getDistanceThatKeepsTheEdgeCellDrawn(viewGroup, way);
        return nearer(distance, keepsTheEdgeCellDrawn);
    }

    private int getDistanceThatKeepsTheEdgeCellDrawn(final ViewGroup viewGroup, final long way) {
        final boolean isBeforeTheFirstCell = way < 0;
        if (isBeforeTheFirstCell) return getDistanceThatKeepsTheFirstCellDrawn(viewGroup);
        return getDistanceThatKeepsTheLastCellDrawn();
    }

    private int getDistanceThatKeepsTheFirstCellDrawn(final ViewGroup viewGroup) {
        final Cell firstCell = mCells.get(0);
        final int firstStart = getCellStart(firstCell);
        final int viewSize = getViewGroupSize(viewGroup);
        return viewSize - firstStart;
    }

    private int getDistanceThatKeepsTheLastCellDrawn() {
        final int lastCellIndex = mCells.size() - 1;
        final Cell lastCell = mCells.get(lastCellIndex);
        final int lastEnd = getCellEnd(lastCell);
        return -lastEnd;
    }

    private static int nearer(final int distance, final int otherDistance) {
        final boolean theOtherIsNearer = Math.abs(otherDistance) < Math.abs(distance);
        if (theOtherIsNearer) return otherDistance;
        return distance;
    }

    private int getScrollDistanceExtrapolatedBeforeFirst(final int size, final long cellIndex) {
        final Cell firstCell = mCells.get(0);
        final int firstDistance = getSnapToPixelDistance(size, firstCell);
        final int firstStart = getCellStart(firstCell);
        final int targetStart = getCellStartAtIndex(cellIndex);
        final int stepsBack = firstStart - targetStart;
        return firstDistance + stepsBack;
    }

    private int getScrollDistanceExtrapolatedAfterLast(final int size, final long cellIndex) {
        final int lastCellIndex = mCells.size() - 1;
        final Cell lastCell = mCells.get(lastCellIndex);
        final int lastDistance = getSnapToPixelDistance(size, lastCell);
        final int lastStart = getCellStart(lastCell);
        final int targetStart = getCellStartAtIndex(cellIndex);
        final int stepsOn = lastStart - targetStart;
        return lastDistance + stepsOn;
    }

    private long getCellIndexOf(final int cellPosition) {
        final boolean isCircularScroll = mLayoutManagerAttributes.isCircularScroll();
        if (isCircularScroll) return getCircularCellIndexOf(cellPosition);
        return getLinearCellIndexOf(cellPosition);
    }

    private long getLinearCellIndexOf(final int cellPosition) {
        return (long) cellPosition - mStartCellPosition;
    }

    private long getCircularCellIndexOf(final int cellPosition) {
        final long cellCount = getCellCount();
        final long cellsAhead = (long) cellPosition - mStartCellPosition;
        return Math.floorMod(cellsAhead, cellCount);
    }

    public int snapTo(final ViewGroup viewGroup) {
        final boolean isSnapToPosition = mLayoutManagerAttributes.isSnapToPosition();
        if (!isSnapToPosition) return 0;

        final int size = getSizeInsidePadding(viewGroup);

        final Cell nearestCell = getCellToSnapTo(size);
        if (nearestCell == null) return 0;

        selectTheCellTheSnapLandsOn(size, nearestCell);

        final int distance = getSnapToPixelDistance(size, nearestCell);
        return distance;
    }

    private void selectTheCellTheSnapLandsOn(final int size, final Cell nearestCell) {
        final boolean selectOnSnap = mLayoutManagerAttributes.selectOnSnap();
        if (!selectOnSnap) return;

        final boolean isShared = isTheSnapPositionShared(size);
        if (isShared) return;

        final View selectedView = getView(nearestCell);
        setSelected(selectedView);
    }

    private boolean isTheSnapPositionShared(final int size) {
        int cellsAtTheSnapPosition = 0;
        for (int cellIndex = 0; cellIndex < mCells.size(); cellIndex++) {
            final boolean isAtTheSnapPosition = isAtTheSnapPosition(size, cellIndex);
            if (isAtTheSnapPosition) cellsAtTheSnapPosition++;
        }
        return cellsAtTheSnapPosition > 1;
    }

    private boolean isAtTheSnapPosition(final int size, final int cellIndex) {
        final int settleDistance = getCellSettleDistance(size, cellIndex);
        final boolean settlesThere = settleDistance == 0;
        if (!settlesThere) return false;

        final int distanceFromTheSnapPosition = getCellDistanceFromSnapPosition(size, cellIndex);
        return distanceFromTheSnapPosition == 0;
    }

    public void reportTheSelectionAtRest() {
        mSelectedPositionManager.onViewsDrawn(mPositions);
    }

    @Override
    protected void onDataSetChanged() {
        if (mAdapterViewManager.isEmpty()) {
            mStartCellPosition = 0;
            mOffset = 0;
            mSelectedPositionManager.setSelectNothing();

            final AdapterViewHandler adapterViewHandler = (AdapterViewHandler) mViewGroup;
            recycleCells(adapterViewHandler);

            mAStopIsOwedToAJump = true;
            return;
        }

        final int adapterCount = mAdapterViewManager.getAdapterCount();
        final int lastItemIndex = adapterCount - 1;

        final int size = getSizeInsidePadding(mViewGroup);
        final View nearestViewToSnapPosition = getNearestViewToSnapPosition(size);
        final int positionOfNearestView = getPosition(nearestViewToSnapPosition);

        if (positionOfNearestView == INVALID_POSITION) return;

        int incomingPosition = positionOfNearestView;
        if (positionOfNearestView > lastItemIndex) incomingPosition = lastItemIndex;

        if (incomingPosition != INVALID_POSITION) {
            final AdapterViewHandler adapterViewHandler = (AdapterViewHandler) mViewGroup;
            jumpToPosition(adapterViewHandler, incomingPosition);
            mAStopIsOwedToAJump = true;
        }

        final int currentlySelectedPosition = mSelectedPositionManager.getSelectedPosition();
        if (currentlySelectedPosition > lastItemIndex)
            mSelectedPositionManager.setSelectedPosition(lastItemIndex);
    }

    private View getView(final int position) {
        final boolean isPositionBeingDrawn = isPositionBeingDrawn(position);
        if (isPositionBeingDrawn) return getDrawnView(position);

        final int widthMeasureSpec = getChildWidthMeasureSpec(position);
        final int heightMeasureSpec = getChildHeightMeasureSpec(position);

        return mAdapterViewManager.getView(
                mViewGroup, position, widthMeasureSpec, heightMeasureSpec);
    }

    public int getChildWidthMeasureSpec(final int position) {
        final int size = getChildWidthMeasureSpecSize(position);
        final int mode = getChildWidthMeasureSpecMode();
        return View.MeasureSpec.makeMeasureSpec(size, mode);
    }

    public int getChildHeightMeasureSpec(final int position) {
        final int size = getChildHeightMeasureSpecSize(position);
        final int mode = getChildHeightMeasureSpecMode();
        return View.MeasureSpec.makeMeasureSpec(size, mode);
    }

    private View getDrawnView(final int position) {
        for (final View view : mPositions.keySet()) {
            final int drawnPosition = mPositions.get(view);
            if (drawnPosition == position) return view;
        }
        return null;
    }

    public void setSelected(final int position, final AdapterViewHandler adapterViewHandler) {
        final boolean isViewOnScreen = mPositions.containsValue(position);
        final boolean isSnapToPosition = mLayoutManagerAttributes.isSnapToPosition();
        if (!isSnapToPosition && !isViewOnScreen) {
            return;
        }

        mSelectedPositionManager.setSelectedPosition(position);

        final boolean snapToPosition = mLayoutManagerAttributes.isSnapToPosition();
        if (!snapToPosition) return;

        jumpToPosition(adapterViewHandler, position);
        mAStopIsOwedToAJump = true;
    }

    public boolean setSelected(final View view) {
        final int position = getPosition(view);
        return mSelectedPositionManager.setSelectedPosition(position);
    }

    private void jumpToPosition(final AdapterViewHandler adapterViewHandler, final int position) {
        final int size = getSizeInsidePadding(mViewGroup);

        final Cell outgoingCell = getNearestCellToSnapPosition(size);
        final View nearestViewToSnapPosition = getNearestViewToSnapPosition(size);
        final int positionOfNearestView = getPosition(nearestViewToSnapPosition);

        if (positionOfNearestView != INVALID_POSITION) {
            final int incomingPosition = position;
            final int outgoingPosition = positionOfNearestView;

            final View outgoingView = getDrawnView(outgoingPosition);
            final View incomingView = getView(incomingPosition);

            final int redrawOffset =
                    mSnapPositionInterface.getRedrawOffset(
                            mScrollDirectionManager, incomingView, outgoingView);
            final int outgoingSnapDistance =
                    mSnapPositionInterface.getUnboundedSnapToPixelDistance(
                            this, size, outgoingCell);
            final int outgoingReachableSnapDistance = getSnapToPixelDistance(size, outgoingCell);
            final int heldShortOfTheSnapPoint =
                    outgoingSnapDistance - outgoingReachableSnapDistance;
            mOffset = redrawOffset + heldShortOfTheSnapPoint;

            mStartCellPosition = getCellPosition(incomingPosition);

            // TODO - FIX THIS BUG!!!! this should potentially be outside of the if statement
            recycleCells(adapterViewHandler);
        }
    }

    private void recycleCells(final AdapterViewHandler adapterViewHandler) {
        for (final Cell cell : mCells) {
            final List<View> views = getViews(cell);
            for (final View view : views) {
                adapterViewHandler.removeViewInAdapterView(view);
                mAdapterViewManager.recycle(view);
            }
        }
        mCells.clear();
        mPositions.clear();
    }

    protected Move getMove(final int displacement) {
        if (displacement < 0) {
            return Move.back;
        }

        if (displacement > 0) {
            return Move.forward;
        }

        return Move.none;
    }

    public void setAnimationStoppedListener(
            final AnimationStoppedListener animationStoppedListener) {
        mAnimationStoppedListener = animationStoppedListener;
    }

    protected void onAnimationStopped() {
        if (mAnimationStoppedListener != null) mAnimationStoppedListener.onAnimationStopped();
    }

    public boolean isSnapToPosition() {
        return mLayoutManagerAttributes.isSnapToPosition();
    }

    public int getViewGroupSize(final ViewGroup viewGroup) {
        return mScrollDirectionManager.getViewGroupSize(viewGroup);
    }

    public int getSizeInsidePadding(final ViewGroup viewGroup) {
        final int size = mScrollDirectionManager.getViewGroupSize(viewGroup);
        return size - getStartSizePadding() - getEndSizePadding();
    }

    public boolean isVerticalScroll() {
        return mScrollDirectionManager.isVerticalScroll();
    }

    public Parcelable onSaveInstanceState(final Parcelable parcelable) {
        final LayoutManagerState<Cell> layoutManagerState =
                new LayoutManagerState<Cell>(parcelable, mOffset, mStartCellPosition);
        return layoutManagerState;
    }

    @SuppressWarnings("unchecked")
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof LayoutManagerState)) {
            return;
        }

        final LayoutManagerState<Cell> layoutManagerState = (LayoutManagerState<Cell>) parcelable;
        mOffset = layoutManagerState.getOffset();
        mStartCellPosition = layoutManagerState.getStartCellPosition();
        mIsFirstLayout = false;
    }

    public void onItemClick(final View view, final int position, final long id) {
        if (mPressedView != null) {
            mPressedView.setPressed(false);
        }
        mPressedView = view;
        mPressedView.setPressed(true);

        mViewGroup.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        view.setPressed(false);
                        mPressedView = null;
                    }
                },
                ViewConfiguration.getPressedStateDuration());
    }

    public float getScrollBarExtent() {
        if (mAdapterViewManager.getAdapterCount() == 0 || mCells.isEmpty()) {
            return 0;
        }

        final float averageVisibleCellSize = mLayoutSize / mLayoutCellCount;
        final float totalCellCount = getCellCount();
        final float maxAverageCellSize = MAX / totalCellCount;
        if (averageVisibleCellSize < maxAverageCellSize) {
            return getViewGroupSize(mViewGroup);
        }

        final float ratio = mLayoutCellCount / totalCellCount;
        return MAX * ratio;
    }

    public float getScrollBarRange() {
        if (mAdapterViewManager.getAdapterCount() == 0 || mCells.isEmpty()) {
            return 0;
        }

        final float averageVisibleCellSize = mLayoutSize / mLayoutCellCount;
        final float totalCellCount = getCellCount();
        final float maxAverageCellSize = MAX / totalCellCount;

        return Math.min(averageVisibleCellSize, maxAverageCellSize) * totalCellCount;
    }

    public float getScrollBarOffset() {
        if (mAdapterViewManager.getAdapterCount() == 0 || mCells.isEmpty()) {
            return 0;
        }

        final Cell cell = mCells.get(0);
        final float totalCellCount = getCellCount();
        final float averageVisibleCellSize = mLayoutSize / mLayoutCellCount;
        final float maxAverageCellSize = MAX / totalCellCount;
        final float startPosition = mStartCellPosition;
        final float offset = mOffset;

        if (averageVisibleCellSize < maxAverageCellSize) {
            return averageVisibleCellSize * startPosition - offset;
        } else {
            final float viewSize = Math.max(getCellSize(cell), 1);
            final float ratio = maxAverageCellSize / viewSize;
            return maxAverageCellSize * startPosition - offset * ratio;
        }
    }

    public View getViewForPosition(final int position) {
        if (!mPositions.containsValue(position)) {
            return null;
        }
        for (final View view : mPositions.keySet()) {
            final Integer viewPosition = mPositions.get(view);
            if (viewPosition == position) {
                return view;
            }
        }

        return null;
    }

    private static final class RequestLayoutRunnable implements Runnable {
        private final ViewGroup mViewGroup;

        private RequestLayoutRunnable(final ViewGroup viewGroup) {
            mViewGroup = viewGroup;
        }

        @Override
        public void run() {
            mViewGroup.requestLayout();
        }
    }
}
