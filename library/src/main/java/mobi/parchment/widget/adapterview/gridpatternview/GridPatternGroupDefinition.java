// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridpatternview;

import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.widget.adapterview.utilities.ViewGroupUtilities;

public class GridPatternGroupDefinition {

    private final boolean mIsVerticalScroll;
    private final List<GridPatternItemDefinition> mGridPatternItemDefinitions =
            new ArrayList<GridPatternItemDefinition>();

    public GridPatternGroupDefinition(
            final boolean isVerticalScroll,
            final List<GridPatternItemDefinition> gridPatternItemDefinitions) {
        mIsVerticalScroll = isVerticalScroll;
        for (final GridPatternItemDefinition gridPatternItemDefinition :
                gridPatternItemDefinitions) {
            addGridPatternItemDefinition(gridPatternItemDefinition);
        }
    }

    private List<Integer> mFirstGridGroupPositions = new ArrayList<Integer>();
    private List<Integer> mLastGridGroupPositions = new ArrayList<Integer>();

    private int mHeight;
    private int mWidth;

    public int getFirstGridGroupPosition(final int gridPosition) {

        for (int index = 0; index < mFirstGridGroupPositions.size(); index++) {
            final int gridGroupPosition = mFirstGridGroupPositions.get(index);
            if (gridGroupPosition < gridPosition) {
                return gridGroupPosition;
            }
        }

        return -1;
    }

    public int getLastGridGroupPosition(final int gridPosition) {
        for (int index = 0; index < mLastGridGroupPositions.size(); index++) {
            final int gridGroupPosition = mLastGridGroupPositions.get(index);
            if (gridGroupPosition < gridPosition) {
                return gridGroupPosition;
            }
        }

        return -1;
    }

    public int getFirstGridGroupPosition() {
        return mFirstGridGroupPositions.get(0);
    }

    public int getLastGridGroupPosition() {
        return mLastGridGroupPositions.get(0);
    }

    public void addGridPatternItemDefinition(
            final GridPatternItemDefinition gridPatternItemDefinition) {
        mGridPatternItemDefinitions.add(gridPatternItemDefinition);
        final int gridPosition = mGridPatternItemDefinitions.size() - 1;

        final int top = gridPatternItemDefinition.getTop();
        final int left = gridPatternItemDefinition.getLeft();
        final int width = gridPatternItemDefinition.getWidth();
        final int height = gridPatternItemDefinition.getHeight();
        final int bottom = top + height - 1;
        final int right = left + width - 1;
        final int maxHeight = bottom + 1;
        final int maxWidth = right + 1;

        if (mFirstGridGroupPositions.isEmpty() && mLastGridGroupPositions.isEmpty()) {
            mFirstGridGroupPositions.add(0);
            mLastGridGroupPositions.add(0);
            mWidth = maxWidth;
            mHeight = maxHeight;
            return;
        }

        if (mHeight < maxHeight) {
            mHeight = maxHeight;
        }

        if (mWidth < maxWidth) {
            mWidth = maxWidth;
        }

        {
            for (int index = 0; index < mFirstGridGroupPositions.size(); index++) {
                final GridPatternItemDefinition currentFirstGridPatternItemDefinition =
                        mGridPatternItemDefinitions.get(index);
                final int currentTop = currentFirstGridPatternItemDefinition.getTop();
                final int currentLeft = currentFirstGridPatternItemDefinition.getLeft();

                if (mIsVerticalScroll && top <= currentTop
                        || !mIsVerticalScroll && left <= currentLeft) {
                    mFirstGridGroupPositions.add(index, gridPosition);
                    break;
                }
            }
        }

        {
            for (int index = 0; index < mLastGridGroupPositions.size(); index++) {
                final GridPatternItemDefinition currentLastGridPatternItemDefinition =
                        mGridPatternItemDefinitions.get(index);
                final int currentTop = currentLastGridPatternItemDefinition.getTop();
                final int currentLeft = currentLastGridPatternItemDefinition.getLeft();
                final int currentWidth = currentLastGridPatternItemDefinition.getWidth();
                final int currentHeight = currentLastGridPatternItemDefinition.getHeight();
                final int currentBottom = currentTop + currentHeight - 1;
                final int currentRight = currentLeft + currentWidth - 1;

                if (!mIsVerticalScroll && right >= currentRight
                        || mIsVerticalScroll && bottom >= currentBottom) {
                    mLastGridGroupPositions.add(index, gridPosition);
                    break;
                }
            }
        }
    }

    public int getNumberOfItems() {
        return mGridPatternItemDefinitions.size();
    }

    public List<GridPatternItemDefinition> getGridPatternItemDefinitions() {
        return mGridPatternItemDefinitions;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getGroupHeight(final ViewGroup viewGroup, final int cellSpacing, final float ratio) {
        if (mIsVerticalScroll) {
            final int gridItemHeight = getGridHeight(viewGroup, cellSpacing, ratio);
            final int numberOfGridItemsPerColumn = getHeight();
            final int numberOfCellSpacings = numberOfGridItemsPerColumn - 1;
            return gridItemHeight * numberOfGridItemsPerColumn + numberOfCellSpacings * cellSpacing;
        }

        final int viewGroupHeight = ViewGroupUtilities.getViewGroupMeasuredHeightPadding(viewGroup);
        return viewGroupHeight;
    }

    public int getGridHeight(final ViewGroup viewGroup, final int cellSpacing, final float ratio) {
        if (mIsVerticalScroll) {
            final int gridWidth = getGridWidth(viewGroup, cellSpacing, ratio);
            return (int) (gridWidth * ratio);
        }

        final int viewGroupHeight = getGroupHeight(viewGroup, cellSpacing, ratio);
        final int numberOfGridItemsPerColumn = getHeight();
        final int numberOfCellSpacings = numberOfGridItemsPerColumn - 1;
        return (viewGroupHeight - numberOfCellSpacings * cellSpacing) / numberOfGridItemsPerColumn;
    }

    public int getGridWidth(final ViewGroup viewGroup, final int cellSpacing, final float ratio) {
        if (mIsVerticalScroll) {
            final int viewGroupWidth = getGroupWidth(viewGroup, cellSpacing, ratio);
            final int numberOfGridItemsPerRow = getWidth();
            final int numberOfCellSpacings = numberOfGridItemsPerRow - 1;
            return (viewGroupWidth - numberOfCellSpacings * cellSpacing) / numberOfGridItemsPerRow;
        }

        final int gridHeight = getGridHeight(viewGroup, cellSpacing, ratio);
        return (int) (gridHeight * ratio);
    }

    public int getGroupWidth(final ViewGroup viewGroup, final int cellSpacing, final float ratio) {
        if (mIsVerticalScroll) {
            final int viewGroupWidth =
                    ViewGroupUtilities.getViewGroupMeasuredWidthPadding(viewGroup);
            return viewGroupWidth;
        }

        final int gridItemWidth = getGridWidth(viewGroup, cellSpacing, ratio);
        final int numberOfGridItemsPerRow = getWidth();
        final int numberOfCellSpacings = numberOfGridItemsPerRow - 1;
        return gridItemWidth * numberOfGridItemsPerRow + numberOfCellSpacings * cellSpacing;
    }

    public int getTopOffset(
            final ViewGroup viewGroup, final int cellSpacing, final float ratio, final int index) {

        final GridPatternItemDefinition gridPatternItemDefinition =
                mGridPatternItemDefinitions.get(index);
        final int top = gridPatternItemDefinition.getTop();
        final int numberOfCellSpacings = top;
        final int gridItemHeight = getGridHeight(viewGroup, cellSpacing, ratio);
        final int numberOfGridItems = top;
        final int topOffset =
                numberOfGridItems * gridItemHeight + numberOfCellSpacings * cellSpacing;

        if (!mIsVerticalScroll) {
            return topOffset + cellSpacing;
        }

        return topOffset;
    }

    public int getLeftOffset(
            final ViewGroup viewGroup, final int cellSpacing, final float ratio, final int index) {

        final GridPatternItemDefinition gridPatternItemDefinition =
                mGridPatternItemDefinitions.get(index);
        final int left = gridPatternItemDefinition.getLeft();
        final int numberOfCellSpacings = left;
        final int gridItemWidth = getGridWidth(viewGroup, cellSpacing, ratio);
        final int numberOfGridItems = left;
        final int leftOffset =
                numberOfGridItems * gridItemWidth + numberOfCellSpacings * cellSpacing;

        if (mIsVerticalScroll) {
            return leftOffset + cellSpacing;
        }

        return leftOffset;
    }

    public int getItemWidth(
            final ViewGroup viewGroup,
            final int cellSpacing,
            final float stretchRatio,
            final int index) {

        final GridPatternItemDefinition gridPatternItemDefinition =
                mGridPatternItemDefinitions.get(index);
        final int gridWidth = gridPatternItemDefinition.getWidth();
        final int gridItemWidth = getGridWidth(viewGroup, cellSpacing, stretchRatio);

        final int width = gridWidth * gridItemWidth + Math.max(gridWidth - 1, 0) * cellSpacing;
        return width;
    }

    public int getItemHeight(
            final ViewGroup viewGroup,
            final int cellSpacing,
            final float stretchRatio,
            final int index) {

        final GridPatternItemDefinition gridPatternItemDefinition =
                mGridPatternItemDefinitions.get(index);
        final int gridHeight = gridPatternItemDefinition.getHeight();
        final int gridItemHeight = getGridHeight(viewGroup, cellSpacing, stretchRatio);

        final int height = gridHeight * gridItemHeight + Math.max(gridHeight - 1, 0) * cellSpacing;
        return height;
    }
}
