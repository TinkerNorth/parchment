// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternItemDefinition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class CellDividerPaintTest {

    private static final int LIST_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int LIST_ADAPTER_SIZE = 6;
    private static final int GRID_PATTERN_ADAPTER_SIZE = 3;
    private static final int CELL_COLOUR = 0xFF0000FF;
    private static final int DIVIDER_COLOUR = 0xFF00FF00;
    private static final int NOTHING_PAINTED = 0;
    private static final int DIVIDER_SIZE = 6;
    private static final int NO_INTRINSIC_SIZE = -1;
    private static final int FILLS_THE_BREADTH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int NEAR_THE_START_OF_EVERY_CELL = 5;
    private static final int BETWEEN_THE_GRID_COLUMNS = 150;
    private static final int THE_START_BREADTH_EDGE = 0;
    private static final int THE_END_BREADTH_EDGE = LIST_SIZE - 1;
    private static final int PATTERN_GROUP_GAP_CENTRE = 150;
    private static final int INSIDE_A_LIST_CELL = 150;
    private static final int INSIDE_A_GRID_CELL = 100;
    private static final int FIRST_GAP_CENTRE = 105;
    private static final int INSIDE_THE_FIRST_CELL = 50;
    private static final int INSIDE_THE_FIRST_CELL_UNDER_A_THICK_DIVIDER = 97;
    private static final int PADDED_FIRST_GAP_CENTRE = 165;
    private static final int PADDED_SECOND_GAP_CENTRE = 275;
    private static final int OUTSIDE_THE_START_BREADTH_PADDING = 10;
    private static final int OUTSIDE_THE_END_BREADTH_PADDING = 290;

    @Test
    public void dividerInXml_isPaintedAcrossTheGapBetweenTheCells() {
        final Bitmap bitmap = paintList(R.id.divider_list_view);

        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, FIRST_GAP_CENTRE)).isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(OUTSIDE_THE_START_BREADTH_PADDING, FIRST_GAP_CENTRE))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
        assertThat(countRowsOfColour(bitmap, NEAR_THE_START_OF_EVERY_CELL))
                .isEqualTo(2 * DIVIDER_SIZE);
    }

    @Test
    public void noDividerInXml_paintsNothingBetweenTheCells() {
        final Bitmap bitmap = paintList(R.id.no_divider_list_view);

        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, FIRST_GAP_CENTRE))
                .isEqualTo(NOTHING_PAINTED);
        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
    }

    @Test
    public void aColourDividerWithNoDividerSize_paintsNothing() {
        final ColorDrawable colour = new ColorDrawable(Color.GREEN);
        assertThat(colour.getIntrinsicHeight()).isEqualTo(NO_INTRINSIC_SIZE);
        assertThat(colour.getIntrinsicWidth()).isEqualTo(NO_INTRINSIC_SIZE);

        final Bitmap bitmap = paintList(R.id.sizeless_colour_divider_list_view);

        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, FIRST_GAP_CENTRE))
                .isEqualTo(NOTHING_PAINTED);
        assertThat(countRowsOfColour(bitmap, NEAR_THE_START_OF_EVERY_CELL)).isEqualTo(0);
    }

    @Test
    public void dividerThickerThanTheCellSpacing_isPaintedOverTheCells() {
        final Bitmap bitmap = paintList(R.id.thick_divider_list_view);

        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, INSIDE_THE_FIRST_CELL_UNDER_A_THICK_DIVIDER))
                .isEqualTo(DIVIDER_COLOUR);
    }

    @Test
    public void dividerWithClipToPaddingOn_isPaintedIntoThePaddingItWouldClipACellOutOf() {
        final Bitmap bitmap = paintList(R.id.padded_clipped_divider_list_view);

        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, PADDED_FIRST_GAP_CENTRE))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(INSIDE_A_LIST_CELL, PADDED_SECOND_GAP_CENTRE))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(OUTSIDE_THE_START_BREADTH_PADDING, PADDED_FIRST_GAP_CENTRE))
                .isEqualTo(NOTHING_PAINTED);
        assertThat(bitmap.getPixel(OUTSIDE_THE_END_BREADTH_PADDING, PADDED_FIRST_GAP_CENTRE))
                .isEqualTo(NOTHING_PAINTED);
    }

    @Test
    public void dividerWithClipToPaddingOff_paintsTheSameSurfaceAsWithClipToPaddingOn() {
        final Bitmap clipped = paintList(R.id.padded_clipped_divider_list_view);
        final Bitmap unclipped = paintList(R.id.padded_unclipped_divider_list_view);

        assertThat(countRowsOfColour(unclipped, INSIDE_A_LIST_CELL)).isEqualTo(2 * DIVIDER_SIZE);
        assertThat(sameDividerRows(clipped, unclipped)).isTrue();
    }

    @Test
    public void dividerInXmlOnAGridView_isPaintedBetweenTheItemsOfARowAsWellAsBetweenTheRows() {
        final Bitmap bitmap = paintList(R.id.divider_grid_view);

        assertThat(bitmap.getPixel(INSIDE_A_GRID_CELL, FIRST_GAP_CENTRE)).isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(BETWEEN_THE_GRID_COLUMNS, INSIDE_THE_FIRST_CELL))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(INSIDE_A_GRID_CELL, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
        assertThat(countRowsOfColour(bitmap, NEAR_THE_START_OF_EVERY_CELL))
                .isEqualTo(2 * DIVIDER_SIZE);
        assertThat(countColumnsOfColour(bitmap, INSIDE_THE_FIRST_CELL)).isEqualTo(DIVIDER_SIZE);
    }

    @Test
    public void dividerInXmlOnAGridView_isNotPaintedAtTheOuterBreadthEdges() {
        final Bitmap bitmap = paintList(R.id.divider_grid_view);

        assertThat(bitmap.getPixel(THE_START_BREADTH_EDGE, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
        assertThat(bitmap.getPixel(THE_END_BREADTH_EDGE, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
    }

    @Test
    public void dividerInXmlOnAGridView_whereTwoGapsCross_paintsNothing() {
        final Bitmap bitmap = paintList(R.id.divider_grid_view);

        assertThat(bitmap.getPixel(BETWEEN_THE_GRID_COLUMNS, FIRST_GAP_CENTRE))
                .isEqualTo(NOTHING_PAINTED);
    }

    @Test
    public void dividerInXmlOnAGridPatternView_isPaintedInsideAGroupAsWellAsBetweenGroups() {
        final Bitmap bitmap = paintGridPattern();

        assertThat(bitmap.getPixel(BETWEEN_THE_GRID_COLUMNS, INSIDE_THE_FIRST_CELL))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(bitmap.getPixel(INSIDE_A_GRID_CELL, PATTERN_GROUP_GAP_CENTRE))
                .isEqualTo(DIVIDER_COLOUR);
        assertThat(countRowsOfColour(bitmap, NEAR_THE_START_OF_EVERY_CELL)).isEqualTo(DIVIDER_SIZE);
    }

    @Test
    public void dividerInXmlOnAGridPatternView_isNotPaintedAtTheOuterBreadthEdges() {
        final Bitmap bitmap = paintGridPattern();

        assertThat(bitmap.getPixel(THE_START_BREADTH_EDGE, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
        assertThat(bitmap.getPixel(THE_END_BREADTH_EDGE, INSIDE_THE_FIRST_CELL))
                .isEqualTo(CELL_COLOUR);
    }

    private static int countRowsOfColour(final Bitmap bitmap, final int column) {
        int rows = 0;
        for (int row = 0; row < LIST_SIZE; row++) {
            if (bitmap.getPixel(column, row) == DIVIDER_COLOUR) rows++;
        }
        return rows;
    }

    private static int countColumnsOfColour(final Bitmap bitmap, final int row) {
        int columns = 0;
        for (int column = 0; column < LIST_SIZE; column++) {
            if (bitmap.getPixel(column, row) == DIVIDER_COLOUR) columns++;
        }
        return columns;
    }

    private static boolean sameDividerRows(final Bitmap first, final Bitmap second) {
        for (int row = 0; row < LIST_SIZE; row++) {
            for (int column = 0; column < LIST_SIZE; column++) {
                final boolean firstIsDivider = first.getPixel(column, row) == DIVIDER_COLOUR;
                final boolean secondIsDivider = second.getPixel(column, row) == DIVIDER_COLOUR;
                if (firstIsDivider != secondIsDivider) return false;
            }
        }
        return true;
    }

    private static Bitmap paintList(final int viewId) {
        final View root = inflate();
        final AbstractAdapterView<BaseAdapter, ?> adapterView = root.findViewById(viewId);
        adapterView.setAdapter(
                new ColouredAdapter(FILLS_THE_BREADTH, CELL_SIZE, LIST_ADAPTER_SIZE));
        return paint(adapterView);
    }

    private static Bitmap paintGridPattern() {
        final View root = inflate();
        final GridPatternView<BaseAdapter> gridPatternView =
                root.findViewById(R.id.divider_grid_pattern_view);
        final List<GridPatternItemDefinition> pairGroup =
                new ArrayList<GridPatternItemDefinition>();
        pairGroup.add(new GridPatternItemDefinition(0, 0, 1, 1));
        pairGroup.add(new GridPatternItemDefinition(0, 1, 1, 1));
        gridPatternView.addGridPatternGroupDefinition(pairGroup);
        final List<GridPatternItemDefinition> tallGroup =
                new ArrayList<GridPatternItemDefinition>();
        tallGroup.add(new GridPatternItemDefinition(0, 0, 1, 2));
        gridPatternView.addGridPatternGroupDefinition(tallGroup);
        gridPatternView.setAdapter(
                new ColouredAdapter(
                        FILLS_THE_BREADTH, FILLS_THE_BREADTH, GRID_PATTERN_ADAPTER_SIZE));
        return paint(gridPatternView);
    }

    private static Bitmap paint(final View adapterView) {
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(LIST_SIZE, View.MeasureSpec.EXACTLY);
        adapterView.measure(measureSpec, measureSpec);
        adapterView.layout(0, 0, LIST_SIZE, LIST_SIZE);

        final Bitmap bitmap = Bitmap.createBitmap(LIST_SIZE, LIST_SIZE, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        adapterView.draw(canvas);
        return bitmap;
    }

    private static View inflate() {
        final Context context = ApplicationProvider.getApplicationContext();
        return View.inflate(context, R.layout.divider_paint, null);
    }

    private static final class ColouredAdapter extends BaseAdapter {
        private final int mCellWidth;
        private final int mCellHeight;
        private final int mAdapterSize;

        private ColouredAdapter(final int cellWidth, final int cellHeight, final int adapterSize) {
            mCellWidth = cellWidth;
            mCellHeight = cellHeight;
            mAdapterSize = adapterSize;
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
            final View view = new View(parent.getContext());
            view.setBackgroundColor(CELL_COLOUR);
            view.setLayoutParams(new ViewGroup.LayoutParams(mCellWidth, mCellHeight));
            return view;
        }
    }
}
