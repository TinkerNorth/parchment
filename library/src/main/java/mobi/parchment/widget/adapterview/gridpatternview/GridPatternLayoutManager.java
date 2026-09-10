// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridpatternview;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.AdapterViewManager;
import mobi.parchment.widget.adapterview.LayoutManager;
import mobi.parchment.widget.adapterview.LayoutManagerAttributes;
import mobi.parchment.widget.adapterview.OnSelectedListener;

public class GridPatternLayoutManager extends LayoutManager<GridPatternGroup> {

    private List<GridPatternGroupDefinition> mGridPatternGroupDefinitions =
            new ArrayList<GridPatternGroupDefinition>();
    private int mNumberOfGridItemsPerRepetition;
    private GridPatternLayoutManagerAttributes mGridPatternLayoutManagerAttributes;
    private boolean mGridPatternsSet = false;

    public GridPatternLayoutManager(
            final ViewGroup viewGroup,
            final OnSelectedListener onSelectedListener,
            final AdapterViewManager adapterViewManager,
            final LayoutManagerAttributes layoutManagerAttributes) {
        super(viewGroup, onSelectedListener, adapterViewManager, layoutManagerAttributes);
        mGridPatternLayoutManagerAttributes =
                (GridPatternLayoutManagerAttributes) layoutManagerAttributes;
    }

    public void addGridPatternGroupDefinition(
            final GridPatternGroupDefinition gridPatternGroupDefinition) {
        if (gridPatternGroupDefinition == null) {
            return;
        }
        mGridPatternGroupDefinitions.add(gridPatternGroupDefinition);
        mNumberOfGridItemsPerRepetition += gridPatternGroupDefinition.getNumberOfItems();
        mGridPatternsSet = true;
    }

    public void setGridPatternGroupDefinitions(
            final List<GridPatternGroupDefinition> gridPatternGroupDefinitions) {

        if (gridPatternGroupDefinitions == null) {
            mGridPatternGroupDefinitions.clear();
            mNumberOfGridItemsPerRepetition = 0;
            return;
        }

        mGridPatternGroupDefinitions =
                new ArrayList<GridPatternGroupDefinition>(gridPatternGroupDefinitions);
        int numberOfGridItemsPerRepetition = 0;
        for (final GridPatternGroupDefinition gridPatternGroupDefinition :
                mGridPatternGroupDefinitions) {
            numberOfGridItemsPerRepetition += gridPatternGroupDefinition.getNumberOfItems();
        }
        mNumberOfGridItemsPerRepetition = numberOfGridItemsPerRepetition;
        mGridPatternsSet = true;
    }

    @Override
    public View getLastView(final GridPatternGroup gridPatternGroup) {
        final View lastView = gridPatternGroup.getLastView();
        return lastView;
    }

    @Override
    public View getView(final GridPatternGroup gridPatternGroup) {
        final View representativeView = gridPatternGroup.getFirstView();
        return representativeView;
    }

    @Override
    public View getFirstView(final GridPatternGroup gridPatternGroup) {
        final View firstView = gridPatternGroup.getFirstView();
        return firstView;
    }

    @Override
    public int getCellStart(final GridPatternGroup gridPatternGroup) {
        if (isVerticalScroll()) {
            return gridPatternGroup.getTop();
        }
        return gridPatternGroup.getLeft();
    }

    @Override
    public int getCellEnd(final GridPatternGroup gridPatternGroup) {
        if (isVerticalScroll()) {
            return gridPatternGroup.getBottom();
        }
        return gridPatternGroup.getRight();
    }

    @Override
    public int getCellSize(final GridPatternGroup gridPatternGroup) {
        if (isVerticalScroll()) {
            return gridPatternGroup.getHeight();
        }
        return gridPatternGroup.getWidth();
    }

    @Override
    public List<View> getViews(final GridPatternGroup gridPatternGroup) {
        final List<View> views = gridPatternGroup.getViews();
        return views;
    }

    @Override
    protected GridPatternGroup getCell(final int adapterPosition) {
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                getGridPatternGroupDefinition(adapterPosition);
        final ViewGroup viewGroup = getViewGroup();
        final int cellSpacing = getCellSpacing();
        final float ratio = mGridPatternLayoutManagerAttributes.getRatio();
        final boolean isVerticalScroll = isVerticalScroll();
        final GridPatternGroup gridPatternGroup =
                new GridPatternGroup(
                        gridPatternGroupDefinition,
                        viewGroup,
                        isVerticalScroll,
                        ratio,
                        cellSpacing);

        final AdapterViewManager adapterViewManager = getAdapterViewManager();
        final int adapterCount = adapterViewManager.getAdapterCount();
        final int numberOfItems = gridPatternGroupDefinition.getNumberOfItems();
        final int positionLimit = Math.min(adapterCount, adapterPosition + numberOfItems);
        for (int position = adapterPosition; position < positionLimit; position++) {
            final int gridPatternItemDefinitionPosition = position - adapterPosition;
            final GridPatternItemDefinition gridPatternItemDefinition =
                    gridPatternGroupDefinition
                            .getGridPatternItemDefinitions()
                            .get(gridPatternItemDefinitionPosition);
            final int maxMeasureWidth =
                    getMaxMeasuredWidth(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int maxMeasureHeight =
                    getMaxMeasuredHeight(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int horizontalMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(maxMeasureWidth, View.MeasureSpec.EXACTLY);
            final int verticalMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(maxMeasureHeight, View.MeasureSpec.EXACTLY);
            final View view =
                    adapterViewManager.getView(
                            mViewGroup, position, horizontalMeasureSpec, verticalMeasureSpec);
            gridPatternGroup.addView(view);
        }

        return gridPatternGroup;
    }

    private int getGridPatternItemDefinitionBreadthStart(
            final GridPatternItemDefinition gridPatternItemDefinition) {
        if (isVerticalScroll()) {
            return gridPatternItemDefinition.getLeft();
        }
        return gridPatternItemDefinition.getTop();
    }

    private int getGridPatternItemDefinitionSizeStart(
            final GridPatternItemDefinition gridPatternItemDefinition) {
        if (isVerticalScroll()) {
            return gridPatternItemDefinition.getTop();
        }
        return gridPatternItemDefinition.getLeft();
    }

    private int getGridPatternItemDefinitionSize(
            final GridPatternItemDefinition gridPatternItemDefinition) {
        if (isVerticalScroll()) {
            return gridPatternItemDefinition.getHeight();
        }
        return gridPatternItemDefinition.getWidth();
    }

    private int getGridPatternItemDefinitionBreadth(
            final GridPatternItemDefinition gridPatternItemDefinition) {
        if (isVerticalScroll()) {
            return gridPatternItemDefinition.getWidth();
        }
        return gridPatternItemDefinition.getHeight();
    }

    private int getGridItemSize(final GridPatternGroupDefinition gridPatternGroupDefinition) {
        final ViewGroup viewGroup = getViewGroup();
        final int cellSpacing = mGridPatternLayoutManagerAttributes.getCellSpacing();
        final float ratio = mGridPatternLayoutManagerAttributes.getRatio();
        if (isVerticalScroll()) {
            return gridPatternGroupDefinition.getGridHeight(viewGroup, cellSpacing, ratio);
        }
        return gridPatternGroupDefinition.getGridWidth(viewGroup, cellSpacing, ratio);
    }

    private int getGridItemBreadth(final GridPatternGroupDefinition gridPatternGroupDefinition) {
        final ViewGroup viewGroup = getViewGroup();
        final int cellSpacing = mGridPatternLayoutManagerAttributes.getCellSpacing();
        final float ratio = mGridPatternLayoutManagerAttributes.getRatio();
        if (isVerticalScroll()) {
            return gridPatternGroupDefinition.getGridWidth(viewGroup, cellSpacing, ratio);
        }
        return gridPatternGroupDefinition.getGridHeight(viewGroup, cellSpacing, ratio);
    }

    private int getGridItemSizeStartOffset(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final GridPatternItemDefinition gridPatternItemDefinition,
            final int cellSpacing) {
        final int gridPatternItemDefinitionStart =
                getGridPatternItemDefinitionSizeStart(gridPatternItemDefinition);
        final int cellSpacingCount = gridPatternItemDefinitionStart;
        final int gridItemPixelSize = getGridItemSize(gridPatternGroupDefinition);
        final int gridItemStartOffset =
                cellSpacingCount * cellSpacing + gridPatternItemDefinitionStart * gridItemPixelSize;
        return gridItemStartOffset;
    }

    private int getGridItemBreadthStartOffset(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final GridPatternItemDefinition gridPatternItemDefinition,
            final int cellSpacing) {
        final int gridPatternItemDefinitionStart =
                getGridPatternItemDefinitionBreadthStart(gridPatternItemDefinition);
        final int cellSpacingCount = gridPatternItemDefinitionStart;
        final int gridItemPixelSize = getGridItemBreadth(gridPatternGroupDefinition);
        final int gridItemStartOffset =
                getStartBreadthPadding()
                        + cellSpacingCount * cellSpacing
                        + gridPatternItemDefinitionStart * gridItemPixelSize;
        return gridItemStartOffset;
    }

    private int getViewSize(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final GridPatternItemDefinition gridPatternItemDefinition,
            final int cellSpacing) {
        final int gridPatternItemDefinitionSize =
                getGridPatternItemDefinitionSize(gridPatternItemDefinition);
        final int cellSpacingCount = gridPatternItemDefinitionSize - 1;
        final int gridItemSize = getGridItemSize(gridPatternGroupDefinition);
        final int viewSize =
                gridPatternItemDefinitionSize * gridItemSize + cellSpacingCount * cellSpacing;

        return viewSize;
    }

    private int getViewBreadth(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final GridPatternItemDefinition gridPatternItemDefinition,
            final int cellSpacing) {
        final int gridPatternItemDefinitionSize =
                getGridPatternItemDefinitionBreadth(gridPatternItemDefinition);
        final int cellSpacingCount = gridPatternItemDefinitionSize - 1;
        final int gridItemSize = getGridItemBreadth(gridPatternGroupDefinition);
        final int viewSize =
                gridPatternItemDefinitionSize * gridItemSize + cellSpacingCount * cellSpacing;

        return viewSize;
    }

    @Override
    public void layoutCell(
            final GridPatternGroup gridPatternGroup,
            final int cellStart,
            final int cellEnd,
            final int firstAdapterPositionInCell,
            final int breadth,
            final int cellSpacing) {
        gridPatternGroup.setStartOffset(cellStart);
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                gridPatternGroup.getGridPatternGroupDefinition();
        final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                gridPatternGroupDefinition.getGridPatternItemDefinitions();

        final List<View> views = gridPatternGroup.getViews();
        final int limit = views.size();
        int adapterPosition = firstAdapterPositionInCell;
        for (int index = 0; index < limit; index++) {
            final View view = views.get(index);
            final GridPatternItemDefinition gridPatternItemDefinition =
                    gridPatternItemDefinitions.get(index);

            final int viewSize =
                    getViewSize(gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int gridItemSizeStartOffset =
                    getGridItemSizeStartOffset(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int viewSizeStart = cellStart + gridItemSizeStartOffset;
            final int viewSizeEnd = viewSizeStart + viewSize;

            final int viewBreadth =
                    getViewBreadth(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int gridItemBreadthStartOffset =
                    getGridItemBreadthStartOffset(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int viewBreadthStart = gridItemBreadthStartOffset;
            final int viewBreadthEnd = viewBreadthStart + viewBreadth;

            final boolean isSelected = isViewSelected(adapterPosition++);
            view.setSelected(isSelected);

            if (isVerticalScroll()) {
                view.layout(viewBreadthStart, viewSizeStart, viewBreadthEnd, viewSizeEnd);
            } else {
                view.layout(viewSizeStart, viewBreadthStart, viewSizeEnd, viewBreadthEnd);
            }
        }
    }

    @Override
    protected int getChildHeightMeasureSpecSize(final int adapterPosition) {
        final int cellPosition = getCellPosition(adapterPosition);
        final int cellPositionOffset = cellPosition % mGridPatternGroupDefinitions.size();
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                mGridPatternGroupDefinitions.get(cellPositionOffset);
        final int firstAdapterPosition = getFirstAdapterPositionInCell(cellPosition);
        final int gridItemPosition = adapterPosition - firstAdapterPosition;
        final GridPatternItemDefinition gridPatternItemDefinition =
                gridPatternGroupDefinition.getGridPatternItemDefinitions().get(gridItemPosition);
        final int cellSpacing = mGridPatternLayoutManagerAttributes.getCellSpacing();
        return getMaxMeasuredHeight(
                gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
    }

    @Override
    protected int getChildWidthMeasureSpecSize(final int adapterPosition) {
        final int cellPosition = getCellPosition(adapterPosition);
        final int cellPositionOffset = cellPosition % mGridPatternGroupDefinitions.size();
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                mGridPatternGroupDefinitions.get(cellPositionOffset);
        final int firstAdapterPosition = getFirstAdapterPositionInCell(cellPosition);
        final int gridItemPosition = adapterPosition - firstAdapterPosition;
        final GridPatternItemDefinition gridPatternItemDefinition =
                gridPatternGroupDefinition.getGridPatternItemDefinitions().get(gridItemPosition);
        final int cellSpacing = mGridPatternLayoutManagerAttributes.getCellSpacing();
        return getMaxMeasuredWidth(
                gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
    }

    private int getMaxMeasuredHeight(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final GridPatternItemDefinition gridPatternItemDefinition,
            final int cellSpacing) {
        if (isVerticalScroll()) {
            return getViewSize(gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
        }
        return getViewBreadth(gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
    }

    private int getMaxMeasuredWidth(
            final GridPatternGroupDefinition gridPatternGroupDefinition,
            final GridPatternItemDefinition gridPatternItemDefinition,
            final int cellSpacing) {
        if (isVerticalScroll()) {
            return getViewBreadth(
                    gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
        }
        return getViewSize(gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
    }

    @Override
    public int getChildWidthMeasureSpecMode() {
        return View.MeasureSpec.EXACTLY;
    }

    @Override
    public int getChildHeightMeasureSpecMode() {
        return View.MeasureSpec.EXACTLY;
    }

    @Override
    public void measure(final GridPatternGroup gridPatternGroup, final ViewGroup viewGroup) {
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                gridPatternGroup.getGridPatternGroupDefinition();
        final List<View> views = gridPatternGroup.getViews();
        final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                gridPatternGroupDefinition.getGridPatternItemDefinitions();
        final AdapterViewManager adapterViewManager = getAdapterViewManager();
        final int cellSpacing = mGridPatternLayoutManagerAttributes.getCellSpacing();
        for (int index = 0; index < views.size(); index++) {
            final View view = views.get(index);
            final GridPatternItemDefinition gridPatternItemDefinition =
                    gridPatternItemDefinitions.get(index);
            final int maxMeasureWidth =
                    getMaxMeasuredWidth(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int maxMeasureHeight =
                    getMaxMeasuredHeight(
                            gridPatternGroupDefinition, gridPatternItemDefinition, cellSpacing);
            final int widthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(
                            maxMeasureWidth, getChildWidthMeasureSpecMode());
            final int heightMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(
                            maxMeasureHeight, getChildHeightMeasureSpecMode());

            adapterViewManager.measureView(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public View getLastAdapterPositionView(final GridPatternGroup gridPatternGroup) {
        final List<View> views = gridPatternGroup.getViews();
        final int index = views.size() - 1;
        return views.get(index);
    }

    @Override
    public View getFirstAdapterPositionView(final GridPatternGroup gridPatternGroup) {
        final List<View> views = gridPatternGroup.getViews();
        return views.get(0);
    }

    @Override
    protected int getFirstAdapterPositionInCell(final int cellPosition) {
        if (!mGridPatternsSet) {
            return cellPosition;
        }
        final int gridPatternGroupDefinitionSize = mGridPatternGroupDefinitions.size();
        final int gridPatternGroupDefinitionsRepeated =
                cellPosition / gridPatternGroupDefinitionSize;
        final int gridPatternGroupDefinitionPosition =
                cellPosition % gridPatternGroupDefinitionSize;

        int adapterPositionOffset = 0;
        for (int index = 0; index < gridPatternGroupDefinitionPosition; index++) {
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    mGridPatternGroupDefinitions.get(index);
            final int numberOfItems = gridPatternGroupDefinition.getNumberOfItems();
            adapterPositionOffset += numberOfItems;
        }

        final int adapterPosition =
                gridPatternGroupDefinitionsRepeated * mNumberOfGridItemsPerRepetition
                        + adapterPositionOffset;

        return adapterPosition;
    }

    @Override
    protected int getDrawPosition(
            final List<GridPatternGroup> gridPatternGroups, final int drawCellPosition) {

        int drawPosition = 0;

        for (int index = 0; index < drawCellPosition; index++) {
            final GridPatternGroup gridPatternGroup = gridPatternGroups.get(index);
            final int numberOfItems = gridPatternGroup.getNumberOfItems();
            drawPosition += numberOfItems;
        }

        return drawPosition;
    }

    @Override
    protected int getLastAdapterPositionInCell(final int cellPosition) {
        if (!mGridPatternsSet) {
            return cellPosition;
        }
        final int gridPatternGroupDefinitionSize = mGridPatternGroupDefinitions.size();
        final int gridPatternGroupDefinitionsRepeated =
                cellPosition / gridPatternGroupDefinitionSize;
        final int gridPatternGroupDefinitionPosition =
                cellPosition % gridPatternGroupDefinitionSize;

        int adapterPositionOffset = 0;
        for (int index = 0; index <= gridPatternGroupDefinitionPosition; index++) {
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    mGridPatternGroupDefinitions.get(index);
            final int numberOfItems = gridPatternGroupDefinition.getNumberOfItems();
            adapterPositionOffset += numberOfItems;
        }

        final int adapterPosition =
                gridPatternGroupDefinitionsRepeated * mNumberOfGridItemsPerRepetition
                        + adapterPositionOffset
                        - 1;

        return adapterPosition;
    }

    @Override
    protected int getCellCount() {
        final AdapterViewManager adapterViewManager = getAdapterViewManager();
        final int adapterCount = adapterViewManager.getAdapterCount();
        if (adapterCount <= 0) {
            return -1;
        }

        final int endPosition = Math.max(0, adapterCount - 1);
        return getCellPosition(endPosition) + 1;
    }

    @Override
    public int getCellPosition(final int adapterPosition) {
        if (!mGridPatternsSet) {
            return adapterPosition;
        }
        final int adapterPositionOffset = adapterPosition % mNumberOfGridItemsPerRepetition;
        final int numberOfRepetitions = adapterPosition / mNumberOfGridItemsPerRepetition;

        int position = 0;
        int cellOffsetCounter = 0;
        int cellOffset = 0;

        while (position <= adapterPositionOffset) {
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    mGridPatternGroupDefinitions.get(cellOffsetCounter);
            final int numberOfItems = gridPatternGroupDefinition.getNumberOfItems();
            position += numberOfItems;
            cellOffset = cellOffsetCounter++;
        }

        final int numberOfCellsRepeated = numberOfRepetitions * mGridPatternGroupDefinitions.size();
        final int cellPosition = numberOfCellsRepeated + cellOffset;
        return cellPosition;
    }

    private GridPatternGroupDefinition getGridPatternGroupDefinition(final int adapterPosition) {
        if (!mGridPatternsSet) {
            final List<GridPatternItemDefinition> gridPatternItemDefinitions =
                    new ArrayList<GridPatternItemDefinition>();
            gridPatternItemDefinitions.add(new GridPatternItemDefinition(0, 0, 1, 1));
            return new GridPatternGroupDefinition(isVerticalScroll(), gridPatternItemDefinitions);
        }
        int views = 0;

        int index = 0;
        while (views < adapterPosition) {
            final GridPatternGroupDefinition gridPatternGroupDefinition =
                    mGridPatternGroupDefinitions.get(index);
            final int viewsInCell = gridPatternGroupDefinition.getNumberOfItems();
            views += viewsInCell;
            index = (index + 1) % mGridPatternGroupDefinitions.size();
        }
        final GridPatternGroupDefinition gridPatternGroupDefinition =
                mGridPatternGroupDefinitions.get(index);
        return gridPatternGroupDefinition;
    }

    public void clearGridPatternGroupDefinition() {
        mGridPatternGroupDefinitions.clear();
        mNumberOfGridItemsPerRepetition = 0;
        mGridPatternsSet = false;
    }
}
