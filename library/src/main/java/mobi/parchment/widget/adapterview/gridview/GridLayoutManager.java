// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridview;

import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.OnSelectedListener;
import mobi.parchment.widget.adapterview.utilities.ViewGroupUtilities;

/** Created by Emir Hasanbegovic */
public class GridLayoutManager extends LayoutManager<Group> {
    private final GridLayoutManagerAttributes mGridLayoutManagerAttributes;

    public GridLayoutManager(
            final ViewGroup viewGroup,
            final OnSelectedListener onSelectedListener,
            final AdapterViewManager adapterViewManager,
            final GridLayoutManagerAttributes gridLayoutManagerAttributes) {
        super(viewGroup, onSelectedListener, adapterViewManager, gridLayoutManagerAttributes);
        mGridLayoutManagerAttributes = gridLayoutManagerAttributes;
    }

    @Override
    public View getView(final Group group) {
        final View view = getRepresentative(group);
        return view;
    }

    private int getMaxMeasureWidth() {
        final ViewGroup viewGroup = getViewGroup();
        final int viewGroupWidth = ViewGroupUtilities.getViewGroupMeasuredWidthPadding(viewGroup);
        final int cellSpacing = mGridLayoutManagerAttributes.getCellSpacing();

        if (isVerticalScroll()) {
            final int numberOfViewsPerCell = getNumberOfViewsPerCell();
            final int cellSpacingCount = numberOfViewsPerCell - 1;
            final int availableSpace = viewGroupWidth - cellSpacingCount * cellSpacing;
            final int maxWidth = availableSpace / numberOfViewsPerCell;
            return maxWidth;
        }

        return viewGroupWidth;
    }

    private int getMaxMeasureHeight() {
        final ViewGroup viewGroup = getViewGroup();
        final int viewGroupHeight = ViewGroupUtilities.getViewGroupMeasuredHeightPadding(viewGroup);
        final int cellSpacing = mGridLayoutManagerAttributes.getCellSpacing();

        if (isVerticalScroll()) {
            return viewGroupHeight;
        }

        final int numberOfViewsPerCell = getNumberOfViewsPerCell();
        final int cellSpacingCount = numberOfViewsPerCell - 1;
        final int availableSpace = viewGroupHeight - cellSpacingCount * cellSpacing;
        final int maxHeight = availableSpace / numberOfViewsPerCell;
        return maxHeight;
    }

    @Override
    protected int getChildHeightMeasureSpecSize(int position) {
        return getMaxMeasureHeight();
    }

    @Override
    protected int getChildWidthMeasureSpecSize(int position) {
        return getMaxMeasureWidth();
    }

    @Override
    public void measure(final Group group, final ViewGroup viewGroup) {
        final int verticalMeasureSpec = getChildHeightMeasureSpec();
        final int horizontalMeasureSpec = getChildWidthMeasureSpec();
        final List<View> views = group.getViews();
        for (final View view : views) {
            mAdapterViewManager.measureView(
                    viewGroup, view, horizontalMeasureSpec, verticalMeasureSpec);
        }
    }

    @Override
    public View getLastAdapterPositionView(Group group) {
        return group.getLastView();
    }

    @Override
    public View getFirstAdapterPositionView(Group group) {
        return group.getFirstView();
    }

    @Override
    protected int getFirstAdapterPositionInCell(int cellPosition) {
        final int numberOfItemsPerCell = mGridLayoutManagerAttributes.getNumberOfViewsPerCell();
        final int adapterPosition = numberOfItemsPerCell * cellPosition;
        return adapterPosition;
    }

    @Override
    protected int getDrawPosition(List<Group> groups, int drawCellPosition) {
        final int numberOfItemsPerCell = mGridLayoutManagerAttributes.getNumberOfViewsPerCell();
        final int drawPosition = numberOfItemsPerCell * drawCellPosition;
        return drawPosition;
    }

    @Override
    protected int getChildWidthMeasureSpecMode() {
        final int widthMeasureSpec = getWidthMeasureSpec();
        return View.MeasureSpec.getMode(widthMeasureSpec);
    }

    @Override
    protected int getChildHeightMeasureSpecMode() {
        final int widthMeasureSpec = getWidthMeasureSpec();
        return View.MeasureSpec.getMode(widthMeasureSpec);
    }

    @Override
    protected int getLastAdapterPositionInCell(final int cellPosition) {
        final int numberOfItemsPerCell = mGridLayoutManagerAttributes.getNumberOfViewsPerCell();
        final int adapterPosition = numberOfItemsPerCell * (cellPosition + 1) - 1;
        return adapterPosition;
    }

    @Override
    protected int getCellCount() {
        final AdapterViewManager adapterViewManager = getAdapterViewManager();
        final int adapterCount = adapterViewManager.getAdapterCount();
        final int numberOfViewsPerCell = getNumberOfViewsPerCell();
        final int remainderViews = adapterCount % numberOfViewsPerCell;
        final int numberOfFullCells = adapterCount / numberOfViewsPerCell;

        return numberOfFullCells + Math.min(remainderViews, 1);
    }

    @Override
    protected int getCellPosition(int adapterPosition) {
        final int numberOfItemsPerCell = mGridLayoutManagerAttributes.getNumberOfViewsPerCell();
        final int cellPosition = adapterPosition / numberOfItemsPerCell;
        return cellPosition;
    }

    @Override
    public View getLastView(final Group group) {
        final View lastView = group.getLastView();
        return lastView;
    }

    @Override
    public View getFirstView(final Group group) {
        final View view = group.getFirstView();
        return view;
    }

    @Override
    public List<View> getViews(final Group group) {
        return group.getViews();
    }

    public int getNumberOfViewsPerCell() {
        return mGridLayoutManagerAttributes.getNumberOfViewsPerCell();
    }

    private int getChildWidthMeasureSpec() {
        final int widthMeasureSpec = getWidthMeasureSpec();
        if (isVerticalScroll()) {
            final int maxMeasureWidth = getMaxMeasureWidth();
            final int measureSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            return View.MeasureSpec.makeMeasureSpec(maxMeasureWidth, measureSpecMode);
        }
        return View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    }

    private int getChildHeightMeasureSpec() {
        final int heightMeasureSpec = getHeightMeasureSpec();
        if (isVerticalScroll()) {
            return View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        final int maxMeasureHeight = getMaxMeasureHeight();
        final int measureSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        return View.MeasureSpec.makeMeasureSpec(maxMeasureHeight, measureSpecMode);
    }

    @Override
    protected Group getCell(final int adapterPosition) {
        final int heightMeasureSpec = getChildHeightMeasureSpec();
        final int widthMeasureSpec = getChildWidthMeasureSpec();
        final int adapterCount = getAdapterCount();
        final int numberOfCells = getNumberOfViewsPerCell();
        final Group group = new Group(isVerticalScroll());
        final int positionLimit = Math.min(adapterPosition + numberOfCells, adapterCount);
        for (int index = adapterPosition; index < positionLimit; index++) {
            final View view =
                    mAdapterViewManager.getView(
                            mViewGroup, index, widthMeasureSpec, heightMeasureSpec);
            group.addView(view);
        }

        return group;
    }

    @Override
    public void layoutCell(
            final Group group,
            final int cellStart,
            final int cellEnd,
            final int firstAdapterPositionInCell,
            final int breadth,
            final int cellSpacing) {
        final List<View> views = group.getViews();
        final int groupBreadth = group.getBreadth(cellSpacing);
        int viewBreadthOffset = getBreadthOffset(breadth, groupBreadth);
        int adapterPosition = firstAdapterPositionInCell;
        for (final View view : views) {

            final boolean isSelected = isViewSelected(adapterPosition++);
            view.setSelected(isSelected);

            final int groupSize = cellEnd - cellStart;
            final int viewSize = getViewSize(view);
            final int viewStartOffset = getSizeOffset(groupSize, viewSize, cellSpacing);
            final int viewStart = cellStart + viewStartOffset;
            final int viewEnd = viewStart + viewSize;

            final int viewBreadth = getViewBreadth(view);
            final int viewBreadthStart = viewBreadthOffset;
            final int viewBreadthEnd = viewBreadthStart + viewBreadth;

            viewBreadthOffset = viewBreadthEnd + cellSpacing;

            if (isVerticalScroll()) {
                view.layout(viewBreadthStart, viewStart, viewBreadthEnd, viewEnd);
            } else {
                view.layout(viewStart, viewBreadthStart, viewEnd, viewBreadthEnd);
            }
        }
    }

    private int getSizeOffset(final int parentSize, final int childSize, final int cellSpacing) {
        if (!isVerticalScroll()) {
            if (mGridLayoutManagerAttributes.isRight()) {
                return parentSize - cellSpacing - childSize;
            } else if (mGridLayoutManagerAttributes.isLeft()) {
                return 0;
            } else {
                return (parentSize - childSize) / 2;
            }
        } else {
            if (mGridLayoutManagerAttributes.isBottom()) {
                return parentSize - cellSpacing - childSize;
            } else if (mGridLayoutManagerAttributes.isTop()) {
                return 0;
            } else {
                return (parentSize - childSize) / 2;
            }
        }
    }

    private int getBreadthOffset(final int breadth, final int groupBreadth) {
        if (isVerticalScroll()) {
            if (mGridLayoutManagerAttributes.isRight()) {
                return breadth - groupBreadth;
            } else if (mGridLayoutManagerAttributes.isLeft()) {
                return 0;
            } else {
                return (breadth - groupBreadth) / 2;
            }
        } else {
            if (mGridLayoutManagerAttributes.isBottom()) {
                return breadth - groupBreadth;
            } else if (mGridLayoutManagerAttributes.isTop()) {
                return 0;
            } else {
                return (breadth - groupBreadth) / 2;
            }
        }
    }

    @Override
    public int getCellStart(final Group group) {
        if (isVerticalScroll()) return group.getTop();
        return group.getLeft();
    }

    @Override
    public int getCellEnd(final Group group) {
        if (isVerticalScroll()) return group.getBottom();
        return group.getRight();
    }

    @Override
    public int getCellSize(final Group group) {
        if (isVerticalScroll()) return group.getMeasuredHeight();
        return group.getMeasuredWidth();
    }

    protected int getGroupBreadth(final Group group) {
        if (isVerticalScroll()) return group.getMeasuredWidth();
        return group.getMeasuredHeight();
    }

    private View getRepresentative(final Group group) {
        if (isVerticalScroll()) return group.getVerticalScrollRepresentative();
        return group.getHorizontalScrollRepresentative();
    }
}
