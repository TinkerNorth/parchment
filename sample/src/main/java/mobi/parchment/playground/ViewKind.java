// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import mobi.parchment.widget.adapterview.gridview.GridView;
import mobi.parchment.widget.adapterview.listview.ListView;

public enum ViewKind implements RadioOption {
    listView(
            R.id.playground_view_kind_list_view,
            R.layout.playground_list_view,
            R.string.playground_list_view,
            ListView.class,
            Cell.horizontal,
            Cell.verticalList),
    gridView(
            R.id.playground_view_kind_grid_view,
            R.layout.playground_grid_view,
            R.string.playground_grid_view,
            GridView.class,
            Cell.horizontal,
            Cell.verticalGrid),
    gridPatternView(
            R.id.playground_view_kind_grid_pattern_view,
            R.layout.playground_grid_pattern_view,
            R.string.playground_grid_pattern_view,
            GridPatternView.class,
            Cell.pattern,
            Cell.pattern);

    private final int mRadioButtonId;
    private final int mLayoutResourceId;
    private final int mTitleResourceId;
    private final Class<?> mViewClass;
    private final Cell mHorizontalCell;
    private final Cell mVerticalCell;

    ViewKind(
            final int radioButtonId,
            final int layoutResourceId,
            final int titleResourceId,
            final Class<?> viewClass,
            final Cell horizontalCell,
            final Cell verticalCell) {
        mRadioButtonId = radioButtonId;
        mLayoutResourceId = layoutResourceId;
        mTitleResourceId = titleResourceId;
        mViewClass = viewClass;
        mHorizontalCell = horizontalCell;
        mVerticalCell = verticalCell;
    }

    @Override
    public int getRadioButtonId() {
        return mRadioButtonId;
    }

    public int getLayoutResourceId() {
        return mLayoutResourceId;
    }

    public int getTitleResourceId() {
        return mTitleResourceId;
    }

    public String getXmlTag() {
        return mViewClass.getName();
    }

    public Cell getCell(final OrientationOption orientation) {
        if (orientation == OrientationOption.horizontal) {
            return mHorizontalCell;
        }
        return mVerticalCell;
    }
}
