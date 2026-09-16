// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import static org.assertj.core.api.Assertions.assertThat;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.core.app.ApplicationProvider;
import java.io.IOException;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.Attributes;
import mobi.parchment.widget.adapterview.SnapPosition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternAttributes;
import mobi.parchment.widget.adapterview.gridview.GridAttributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

// SampleApplication installs a Picasso singleton, which a JVM accepts once.
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PlaygroundThemeTest {

    private static final int VIEWPORT_SIZE = 300;
    private static final int CELL_SIZE = 100;
    private static final int SHORT_CELL_SIZE = 60;
    private static final int ADAPTER_SIZE = 12;
    private static final int CELL_COLOUR = 0xFF0000FF;
    private static final int FILLS_THE_BREADTH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int NO_DIVIDER = 0;
    private static final int VIEWPORT_INTERVAL = 0;
    private static final int ONE_CELL_PER_GESTURE = 1;
    private static final int TWO_CELLS_PER_GESTURE = 2;
    private static final int TWO_PER_ROW = 2;
    private static final int THREE_PER_ROW = 3;
    private static final int FOUR_PER_ROW = 4;
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int FOURTH = 3;

    @Test
    public void theOrientationOverlay_reachesTheEnumAttribute() {
        assertThat(list(options().orientation(OrientationOption.horizontal)).isVertical())
                .isFalse();
        assertThat(list(options().orientation(OrientationOption.vertical)).isVertical()).isTrue();
    }

    @Test
    public void theCellSpacingOverlay_reachesTheDimensionAttribute() {
        final Attributes attributes = list(options().cellSpacing(CellSpacingOption.small));

        assertThat((int) attributes.getCellSpacing())
                .isEqualTo(pixels(R.dimen.playground_cell_spacing_small));
    }

    @Test
    public void theSnapPositionOverlay_reachesTheEnumAttribute() {
        for (final SnapPositionOption option : SnapPositionOption.values()) {
            final Attributes attributes = list(options().snapPosition(option));
            final SnapPosition expected = SnapPosition.valueOf(option.name());

            assertThat(attributes.getSnapPosition()).isEqualTo(expected);
        }
    }

    @Test
    public void theBaseTheme_leavesEveryBooleanAttributeOff() {
        final Attributes attributes = list(options());

        assertThat(attributes.isSnapToPosition()).isFalse();
        assertThat(attributes.isCircularScroll()).isFalse();
        assertThat(attributes.isViewPager()).isFalse();
        assertThat(attributes.selectOnSnap()).isFalse();
        assertThat(attributes.selectWhileScrolling()).isFalse();
        assertThat(attributes.scrollWithinContent()).isFalse();
    }

    @Test
    public void theBooleanOverlays_reachTheirAttributes() {
        final Attributes attributes =
                list(
                        options()
                                .snapToPosition(true)
                                .isCircularScroll(true)
                                .isViewPager(true)
                                .selectOnSnap(true)
                                .selectWhileScrolling(true)
                                .scrollWithinContent(true));

        assertThat(attributes.isSnapToPosition()).isTrue();
        assertThat(attributes.isCircularScroll()).isTrue();
        assertThat(attributes.isViewPager()).isTrue();
        assertThat(attributes.selectOnSnap()).isTrue();
        assertThat(attributes.selectWhileScrolling()).isTrue();
        assertThat(attributes.scrollWithinContent()).isTrue();
    }

    @Test
    public void theIntervalOverlay_reachesTheIntegerAttribute() {
        assertThat(
                        list(options().viewPagerInterval(ViewPagerIntervalOption.viewport))
                                .getViewPagerInterval())
                .isEqualTo(VIEWPORT_INTERVAL);
        assertThat(
                        list(options().viewPagerInterval(ViewPagerIntervalOption.one))
                                .getViewPagerInterval())
                .isEqualTo(ONE_CELL_PER_GESTURE);
        assertThat(
                        list(options().viewPagerInterval(ViewPagerIntervalOption.two))
                                .getViewPagerInterval())
                .isEqualTo(TWO_CELLS_PER_GESTURE);
    }

    @Test
    public void theDividerOverlay_reachesTheDrawableAndItsSize() {
        final Attributes attributes = list(options().hasDivider(true));
        final Drawable divider = attributes.getDivider();

        assertThat(divider).isInstanceOf(ColorDrawable.class);
        assertThat(((ColorDrawable) divider).getColor()).isEqualTo(color(R.color.cell_divider));
        assertThat(attributes.getDividerSize())
                .isEqualTo(pixels(R.dimen.activity_sample_parchment_divider_size));
    }

    @Test
    public void theBaseTheme_hasNoDividerToPaint() {
        final Attributes attributes = list(options());

        assertThat(attributes.getDividerSize()).isEqualTo(NO_DIVIDER);
    }

    @Test
    public void theViewsPerCellOverlay_reachesTheIntegerAttribute() {
        assertThat(grid(options().viewsPerCell(ViewsPerCellOption.two)).getNumberOfViewsPerCell())
                .isEqualTo(TWO_PER_ROW);
        assertThat(grid(options().viewsPerCell(ViewsPerCellOption.three)).getNumberOfViewsPerCell())
                .isEqualTo(THREE_PER_ROW);
        assertThat(grid(options().viewsPerCell(ViewsPerCellOption.four)).getNumberOfViewsPerCell())
                .isEqualTo(FOUR_PER_ROW);
    }

    @Test
    public void theGravityOverlay_reachesTheFlagAttribute() {
        assertThat(grid(options().gravity(GravityOption.top)).isTop()).isTrue();
        assertThat(grid(options().gravity(GravityOption.bottom)).isBottom()).isTrue();
        assertThat(grid(options().gravity(GravityOption.bottom)).isTop()).isFalse();
        assertThat(grid(options().gravity(GravityOption.left)).isLeft()).isTrue();
        assertThat(grid(options().gravity(GravityOption.right)).isRight()).isTrue();
        assertThat(grid(options().gravity(GravityOption.right)).isLeft()).isFalse();
    }

    @Test
    public void theRatioOverlay_reachesTheFloatAttribute() {
        for (final RatioOption option : RatioOption.values()) {
            final GridPatternAttributes attributes = gridPattern(options().ratio(option));
            final float expected = floatValue(option.getFloatResourceId());

            assertThat(attributes.getRatio()).isEqualTo(expected);
        }
    }

    @Test
    public void aListViewInflatedUnderTheTheme_scrollsOnTheChosenAxis() {
        final AbstractAdapterView<BaseAdapter, ?> horizontal =
                layOut(options().orientation(OrientationOption.horizontal), horizontalCells());
        final AbstractAdapterView<BaseAdapter, ?> vertical =
                layOut(options().orientation(OrientationOption.vertical), verticalCells());

        assertThat(horizontal.isHorizontalScrollBarEnabled()).isTrue();
        assertThat(horizontal.isVerticalScrollBarEnabled()).isFalse();
        assertThat(vertical.isVerticalScrollBarEnabled()).isTrue();
        assertThat(vertical.isHorizontalScrollBarEnabled()).isFalse();
    }

    @Test
    public void aListViewInflatedUnderTheTheme_leavesTheChosenGapBetweenCells() {
        final AbstractAdapterView<BaseAdapter, ?> view =
                layOut(
                        options()
                                .orientation(OrientationOption.horizontal)
                                .cellSpacing(CellSpacingOption.large),
                        horizontalCells());

        final int gap = cell(view, SECOND).getLeft() - cell(view, FIRST).getRight();

        assertThat(gap).isEqualTo(pixels(R.dimen.playground_cell_spacing_large));
    }

    @Test
    public void aGridViewInflatedUnderTheTheme_laysOutTheChosenNumberOfViewsPerRow() {
        final AbstractAdapterView<BaseAdapter, ?> view =
                layOut(
                        options()
                                .viewKind(ViewKind.gridView)
                                .orientation(OrientationOption.vertical)
                                .viewsPerCell(ViewsPerCellOption.three),
                        verticalCells());

        assertThat(cell(view, THIRD).getTop()).isEqualTo(cell(view, FIRST).getTop());
        assertThat(cell(view, FOURTH).getTop()).isGreaterThan(cell(view, FIRST).getTop());
    }

    @Test
    public void aGridViewInflatedUnderTheTheme_sitsAShorterViewWhereTheGravitySays() {
        final PlaygroundOptions.Builder grid =
                options()
                        .viewKind(ViewKind.gridView)
                        .orientation(OrientationOption.vertical)
                        .viewsPerCell(ViewsPerCellOption.two);
        final AbstractAdapterView<BaseAdapter, ?> top =
                layOut(grid.gravity(GravityOption.top), unevenVerticalCells());
        final AbstractAdapterView<BaseAdapter, ?> bottom =
                layOut(grid.gravity(GravityOption.bottom), unevenVerticalCells());

        assertThat(cell(top, SECOND).getTop()).isEqualTo(cell(top, FIRST).getTop());
        assertThat(cell(bottom, SECOND).getTop())
                .isEqualTo(cell(bottom, FIRST).getTop() + CELL_SIZE - SHORT_CELL_SIZE);
    }

    @Test
    public void aListViewInflatedUnderTheTheme_paintsTheDividerOnlyWhenTheOverlayIsApplied() {
        final PlaygroundOptions.Builder list =
                options()
                        .orientation(OrientationOption.horizontal)
                        .cellSpacing(CellSpacingOption.large);
        final AbstractAdapterView<BaseAdapter, ?> bare = layOut(list, horizontalCells());
        final AbstractAdapterView<BaseAdapter, ?> divided =
                layOut(list.hasDivider(true), horizontalCells());
        final int gapCentre =
                cell(bare, FIRST).getRight() + pixels(R.dimen.playground_cell_spacing_large) / 2;
        final int midway = VIEWPORT_SIZE / 2;

        assertThat(paint(bare).getPixel(gapCentre, midway))
                .isEqualTo(color(R.color.activity_background));
        assertThat(paint(divided).getPixel(gapCentre, midway))
                .isNotEqualTo(color(R.color.activity_background));
    }

    private static PlaygroundOptions.Builder options() {
        return new PlaygroundOptions.Builder();
    }

    private static Attributes list(final PlaygroundOptions.Builder options) {
        final XmlResourceParser parser = rootTagOf(R.layout.playground_list_view);
        try {
            return new Attributes(themedContext(options), parser);
        } finally {
            parser.close();
        }
    }

    private static GridAttributes grid(final PlaygroundOptions.Builder options) {
        final XmlResourceParser parser = rootTagOf(R.layout.playground_grid_view);
        try {
            return new GridAttributes(themedContext(options), parser);
        } finally {
            parser.close();
        }
    }

    private static GridPatternAttributes gridPattern(final PlaygroundOptions.Builder options) {
        final XmlResourceParser parser = rootTagOf(R.layout.playground_grid_pattern_view);
        try {
            return new GridPatternAttributes(themedContext(options), parser);
        } finally {
            parser.close();
        }
    }

    private static XmlResourceParser rootTagOf(final int layoutResourceId) {
        final XmlResourceParser parser = resources().getLayout(layoutResourceId);
        try {
            int event = parser.next();
            while (event != XmlPullParser.START_TAG) {
                event = parser.next();
            }
            return parser;
        } catch (final XmlPullParserException | IOException exception) {
            throw new IllegalStateException("could not read the layout", exception);
        }
    }

    private static AbstractAdapterView<BaseAdapter, ?> layOut(
            final PlaygroundOptions.Builder options, final BaseAdapter adapter) {
        final PlaygroundOptions built = options.build();
        final Context context = themedContext(options);
        final View root = View.inflate(context, built.getViewKind().getLayoutResourceId(), null);
        final AbstractAdapterView<BaseAdapter, ?> view = root.findViewById(R.id.parchment_view);
        view.setAdapter(adapter);
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(VIEWPORT_SIZE, View.MeasureSpec.EXACTLY);
        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, VIEWPORT_SIZE, VIEWPORT_SIZE);
        return view;
    }

    private static Bitmap paint(final View view) {
        final Bitmap bitmap =
                Bitmap.createBitmap(VIEWPORT_SIZE, VIEWPORT_SIZE, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private static View cell(final ViewGroup view, final int adapterPosition) {
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final boolean isTheWantedCell = child.getTag().equals(adapterPosition);
            if (isTheWantedCell) {
                return child;
            }
        }
        throw new IllegalStateException("position " + adapterPosition + " is not laid out");
    }

    private static Context themedContext(final PlaygroundOptions.Builder options) {
        final Context context = ApplicationProvider.getApplicationContext();
        return PlaygroundTheme.contextFor(context, options.build());
    }

    private static Resources resources() {
        final Context context = ApplicationProvider.getApplicationContext();
        return context.getResources();
    }

    private static int pixels(final int dimensionResourceId) {
        return resources().getDimensionPixelSize(dimensionResourceId);
    }

    private static int color(final int colorResourceId) {
        final Context context = ApplicationProvider.getApplicationContext();
        return context.getColor(colorResourceId);
    }

    private static float floatValue(final int floatResourceId) {
        final TypedValue value = new TypedValue();
        resources().getValue(floatResourceId, value, true);
        return value.getFloat();
    }

    private static BaseAdapter horizontalCells() {
        return new CellAdapter(CELL_SIZE, FILLS_THE_BREADTH, FILLS_THE_BREADTH);
    }

    private static BaseAdapter verticalCells() {
        return new CellAdapter(FILLS_THE_BREADTH, CELL_SIZE, CELL_SIZE);
    }

    private static BaseAdapter unevenVerticalCells() {
        return new CellAdapter(FILLS_THE_BREADTH, CELL_SIZE, SHORT_CELL_SIZE);
    }

    private static final class CellAdapter extends BaseAdapter {

        private final int mCellWidth;
        private final int mEvenCellHeight;
        private final int mOddCellHeight;

        private CellAdapter(
                final int cellWidth, final int evenCellHeight, final int oddCellHeight) {
            mCellWidth = cellWidth;
            mEvenCellHeight = evenCellHeight;
            mOddCellHeight = oddCellHeight;
        }

        @Override
        public int getCount() {
            return ADAPTER_SIZE;
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
            view.setLayoutParams(new ViewGroup.LayoutParams(mCellWidth, heightOf(position)));
            view.setTag(position);
            return view;
        }

        private int heightOf(final int position) {
            final boolean isEven = position % 2 == 0;
            if (isEven) {
                return mEvenCellHeight;
            }
            return mOddCellHeight;
        }
    }
}
