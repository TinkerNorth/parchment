// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import static org.assertj.core.api.Assertions.assertThat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.Attributes;
import mobi.parchment.widget.adapterview.Orientation;
import mobi.parchment.widget.adapterview.SnapPosition;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternAttributes;
import mobi.parchment.widget.adapterview.gridview.GridAttributes;
import mobi.parchment.widget.adapterview.listview.ListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RunWith(RobolectricTestRunner.class)
public class HorizontalListViewTest {

    public static final int TEST_ADAPTER_COUNT = 12;
    public static final int HORIZONTAL_LIST_VIEW_WIDTH = 1000;
    private static final int TEST_VIEWS_PER_CELL = 3;
    private static final float TEST_RATIO = 1.5f;
    private static final float DEFAULT_CELL_SPACING = 0f;
    private static final int DEFAULT_VIEWS_PER_CELL = 1;
    private static final int TEST_VIEW_PAGER_INTERVAL = 3;
    private static final int DEFAULT_VIEW_PAGER_INTERVAL = 1;
    private static final int TEST_GRID_VIEW_PAGER_INTERVAL = 4;
    private static final int TEST_GRID_PATTERN_VIEW_PAGER_INTERVAL = 2;
    private static final float DEFAULT_RATIO = 1.0f;
    private static final int NO_ID_RESOURCE = 0;
    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String RES_AUTO_NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private static final String ID_ATTRIBUTE = "id";
    private static final String ORIENTATION_ATTRIBUTE = "parchment_orientation";
    private static final String SNAP_POSITION_ATTRIBUTE = "parchment_snapPosition";
    private static final int TEST_DIVIDER_COLOUR = 0xff112233;
    private static final int DEFAULT_DIVIDER_SIZE = -1;

    @Test
    public void testBasicIntegration() {
        final ActivityController<TestActivity> controller =
                Robolectric.buildActivity(TestActivity.class);
        final TestActivity testActivity = controller.get();
        testActivity.setLayoutId(R.layout.basic);
        controller.create();

        final ListView<?> horizontalListView = testActivity.findViewById(R.id.horizontal_list_view);

        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(
                        HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        horizontalListView.measure(measureSpec, measureSpec);
        horizontalListView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);

        assertThat(horizontalListView.getVisibility()).isEqualTo(View.VISIBLE);

        final int childCount = horizontalListView.getChildCount();
        assertThat(childCount).isGreaterThan(0);
    }

    @Test
    public void testSnapPositionOnScreenWithMargin() {
        final ActivityController<TestActivity> controller =
                Robolectric.buildActivity(TestActivity.class);
        final TestActivity testActivity = controller.get();
        testActivity.setLayoutId(R.layout.on_screen_cell_spacing);
        controller.create();

        final ListView<?> horizontalListView = testActivity.findViewById(R.id.horizontal_list_view);

        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(
                        HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        horizontalListView.measure(measureSpec, measureSpec);
        horizontalListView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);

        final Resources resources = testActivity.getResources();
        final int viewWidth = resources.getDimensionPixelSize(R.dimen.list_item_test_width);
        final int margin =
                resources.getDimensionPixelSize(R.dimen.horizontal_list_view_test_margin);

        assertThat(horizontalListView.getWidth()).isEqualTo(HORIZONTAL_LIST_VIEW_WIDTH);
        assertThat(horizontalListView.getChildCount()).isGreaterThan(0);
        // onScreen snap positions the first child at offset 0 (no leading padding)
        assertThat(horizontalListView.getChildAt(0).getLeft()).isEqualTo(0);
    }

    @Test
    public void onMeasure_withAnExactSpec_reportsTheSizeWithoutStateBits() {
        final ListView<?> horizontalListView = inflateBasicListView();

        final int widthSpec =
                View.MeasureSpec.makeMeasureSpec(
                        HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY);
        horizontalListView.measure(widthSpec, heightSpec);

        assertThat(horizontalListView.getMeasuredWidth()).isEqualTo(HORIZONTAL_LIST_VIEW_WIDTH);
        assertThat(horizontalListView.getMeasuredHeight()).isEqualTo(300);
        assertThat(horizontalListView.getMeasuredState()).isEqualTo(0);
    }

    @Test
    public void onMeasure_withAnAtMostSpec_takesTheAvailableSizeWithoutStateBits() {
        final ListView<?> horizontalListView = inflateBasicListView();

        final int widthSpec =
                View.MeasureSpec.makeMeasureSpec(
                        HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.AT_MOST);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.AT_MOST);
        horizontalListView.measure(widthSpec, heightSpec);

        assertThat(horizontalListView.getMeasuredWidth()).isEqualTo(HORIZONTAL_LIST_VIEW_WIDTH);
        assertThat(horizontalListView.getMeasuredHeight()).isEqualTo(300);
        assertThat(horizontalListView.getMeasuredState()).isEqualTo(0);
    }

    @Test
    public void onLayout_atANegativeOffsetInsideItsParent_laysOutTheChildren() {
        final ListView<?> horizontalListView = inflateBasicListView();
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(
                        HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        horizontalListView.measure(measureSpec, measureSpec);

        horizontalListView.layout(
                -50, -50, HORIZONTAL_LIST_VIEW_WIDTH - 50, HORIZONTAL_LIST_VIEW_WIDTH - 50);

        assertThat(horizontalListView.getChildCount()).isGreaterThan(0);
        assertThat(horizontalListView.getChildAt(0).getLeft()).isEqualTo(0);
        assertThat(horizontalListView.getChildAt(0).getRight())
                .isEqualTo(HORIZONTAL_LIST_VIEW_WIDTH);
    }

    @Test
    public void theDeclaredAttributeNames_areAllNamespacedToParchment() {
        final Field[] fields = mobi.parchment.R.attr.class.getFields();
        final List<String> attributeNames = new ArrayList<>();
        for (final Field field : fields) {
            attributeNames.add(field.getName());
        }

        assertThat(attributeNames)
                .containsExactlyInAnyOrder(
                        "parchment_orientation",
                        "parchment_cellSpacing",
                        "parchment_isCircularScroll",
                        "parchment_snapToPosition",
                        "parchment_selectOnSnap",
                        "parchment_isViewPager",
                        "parchment_viewPagerInterval",
                        "parchment_snapPosition",
                        "parchment_selectWhileScrolling",
                        "parchment_divider",
                        "parchment_dividerSize",
                        "parchment_numberOfViewsPerCell",
                        "parchment_gravity",
                        "parchment_ratio");
    }

    @Test
    public void listViewAttributesSetInXml_reachTheAttributesGetters() {
        final Attributes attributes =
                listViewAttributesFrom(
                        R.layout.attribute_parsing_list_view, R.id.horizontal_list_view);

        assertThat(attributes.getOrientation()).isEqualTo(Orientation.horizontal);
        assertThat(attributes.isVertical()).isFalse();
        assertThat(attributes.getCellSpacing())
                .isEqualTo((float) dimensionPixelSize(R.dimen.horizontal_list_view_test_margin));
        assertThat(attributes.isCircularScroll()).isTrue();
        assertThat(attributes.isSnapToPosition()).isTrue();
        assertThat(attributes.selectOnSnap()).isTrue();
        assertThat(attributes.isViewPager()).isTrue();
        assertThat(attributes.getViewPagerInterval()).isEqualTo(TEST_VIEW_PAGER_INTERVAL);
        assertThat(attributes.getSnapPosition()).isEqualTo(SnapPosition.end);
        assertThat(attributes.selectWhileScrolling()).isTrue();
    }

    @Test
    public void dividerAttributesSetInXml_reachTheAttributesGetters() {
        final Attributes attributes =
                listViewAttributesFrom(
                        R.layout.attribute_parsing_list_view, R.id.horizontal_list_view);

        final Drawable divider = attributes.getDivider();
        assertThat(divider).isInstanceOf(ColorDrawable.class);
        assertThat(((ColorDrawable) divider).getColor()).isEqualTo(TEST_DIVIDER_COLOUR);
        assertThat(attributes.getDividerSize())
                .isEqualTo(dimensionPixelSize(R.dimen.divider_test_size));
    }

    @Test
    public void snapPositionInXml_eachEnumValue_mapsToTheMatchingConstant() {
        final int layoutId = R.layout.attribute_parsing_snap_positions;

        assertThat(listViewAttributesFrom(layoutId, R.id.snap_position_center).getSnapPosition())
                .isEqualTo(SnapPosition.center);
        assertThat(listViewAttributesFrom(layoutId, R.id.snap_position_start).getSnapPosition())
                .isEqualTo(SnapPosition.start);
        assertThat(listViewAttributesFrom(layoutId, R.id.snap_position_end).getSnapPosition())
                .isEqualTo(SnapPosition.end);
        assertThat(listViewAttributesFrom(layoutId, R.id.snap_position_on_screen).getSnapPosition())
                .isEqualTo(SnapPosition.onScreen);
        assertAttributeIsDeclared(layoutId, R.id.snap_position_on_screen, SNAP_POSITION_ATTRIBUTE);
    }

    @Test
    public void gridViewAttributesSetInXml_reachTheGridAttributesGetters() {
        final GridAttributes gridAttributes =
                gridAttributesFrom(
                        R.layout.attribute_parsing_grid_view, R.id.grid_view_left_bottom);

        assertThat(gridAttributes.getNumberOfViewsPerCell()).isEqualTo(TEST_VIEWS_PER_CELL);
        assertThat(gridAttributes.getOrientation()).isEqualTo(Orientation.horizontal);
    }

    @Test
    public void leftAndBottomGravityFlagsInXml_setTheMatchingGravityGetters() {
        final GridAttributes gridAttributes =
                gridAttributesFrom(
                        R.layout.attribute_parsing_grid_view, R.id.grid_view_left_bottom);

        assertThat(gridAttributes.isLeft()).isTrue();
        assertThat(gridAttributes.isRight()).isFalse();
        assertThat(gridAttributes.isBottom()).isTrue();
        assertThat(gridAttributes.isTop()).isFalse();
    }

    @Test
    public void rightAndTopGravityFlagsInXml_setTheMatchingGravityGetters() {
        final GridAttributes gridAttributes =
                gridAttributesFrom(R.layout.attribute_parsing_grid_view, R.id.grid_view_right_top);

        assertThat(gridAttributes.isRight()).isTrue();
        assertThat(gridAttributes.isLeft()).isFalse();
        assertThat(gridAttributes.isTop()).isTrue();
        assertThat(gridAttributes.isBottom()).isFalse();
    }

    @Test
    public void everyGravityFlagInXml_letsLeftAndTopWinTheirOppositePair() {
        final GridAttributes gridAttributes =
                gridAttributesFrom(R.layout.attribute_parsing_grid_view, R.id.grid_view_every_flag);

        assertThat(gridAttributes.isLeft()).isTrue();
        assertThat(gridAttributes.isRight()).isFalse();
        assertThat(gridAttributes.isTop()).isTrue();
        assertThat(gridAttributes.isBottom()).isFalse();
    }

    @Test
    public void viewPagerIntervalOfZeroInXml_fallsBackToOneCellPerGesture() {
        final Attributes attributes =
                listViewAttributesFrom(
                        R.layout.attribute_parsing_view_pager_intervals,
                        R.id.view_pager_interval_zero);

        assertThat(attributes.getViewPagerInterval()).isEqualTo(DEFAULT_VIEW_PAGER_INTERVAL);
    }

    @Test
    public void aNegativeViewPagerIntervalInXml_fallsBackToOneCellPerGesture() {
        final Attributes attributes =
                listViewAttributesFrom(
                        R.layout.attribute_parsing_view_pager_intervals,
                        R.id.view_pager_interval_negative);

        assertThat(attributes.getViewPagerInterval()).isEqualTo(DEFAULT_VIEW_PAGER_INTERVAL);
    }

    @Test
    public void viewPagerIntervalSetInXml_reachesTheGridAttributesGetter() {
        final GridAttributes gridAttributes =
                gridAttributesFrom(
                        R.layout.attribute_parsing_view_pager_intervals,
                        R.id.view_pager_interval_grid_view);

        assertThat(gridAttributes.getViewPagerInterval()).isEqualTo(TEST_GRID_VIEW_PAGER_INTERVAL);
    }

    @Test
    public void viewPagerIntervalSetInXml_reachesTheGridPatternAttributesGetter() {
        final GridPatternAttributes gridPatternAttributes =
                gridPatternAttributesFrom(
                        R.layout.attribute_parsing_view_pager_intervals,
                        R.id.view_pager_interval_grid_pattern_view);

        assertThat(gridPatternAttributes.getViewPagerInterval())
                .isEqualTo(TEST_GRID_PATTERN_VIEW_PAGER_INTERVAL);
    }

    @Test
    public void gridPatternViewRatioSetInXml_reachesTheRatioGetter() {
        final GridPatternAttributes gridPatternAttributes =
                gridPatternAttributesFrom(
                        R.layout.attribute_parsing_grid_pattern_view, R.id.grid_pattern_view);

        assertThat(gridPatternAttributes.getRatio()).isEqualTo(TEST_RATIO);
        assertThat(gridPatternAttributes.getOrientation()).isEqualTo(Orientation.horizontal);
    }

    @Test
    public void listViewAttributesAbsentFromAnAttributeSet_fallBackToTheDefaultsWithOnScreenSnap() {
        final Attributes attributes =
                listViewAttributesFrom(
                        R.layout.attribute_parsing_snap_positions, R.id.no_parchment_attributes);

        assertThat(attributes.getOrientation()).isEqualTo(Orientation.vertical);
        assertThat(attributes.isVertical()).isTrue();
        assertThat(attributes.getCellSpacing()).isEqualTo(DEFAULT_CELL_SPACING);
        assertThat(attributes.isCircularScroll()).isFalse();
        assertThat(attributes.isSnapToPosition()).isFalse();
        assertThat(attributes.selectOnSnap()).isFalse();
        assertThat(attributes.isViewPager()).isFalse();
        assertThat(attributes.getViewPagerInterval()).isEqualTo(DEFAULT_VIEW_PAGER_INTERVAL);
        assertThat(attributes.selectWhileScrolling()).isFalse();
        assertThat(attributes.getSnapPosition()).isEqualTo(SnapPosition.onScreen);
        assertThat(attributes.getDivider()).isNull();
        assertThat(attributes.getDividerSize()).isEqualTo(DEFAULT_DIVIDER_SIZE);
    }

    @Test
    public void listViewAttributesWithoutAnAttributeSet_fallBackToTheDefaultsWithCenterSnap() {
        final Attributes attributes = new Attributes(RuntimeEnvironment.getApplication(), null);

        assertThat(attributes.getOrientation()).isEqualTo(Orientation.vertical);
        assertThat(attributes.isVertical()).isTrue();
        assertThat(attributes.getCellSpacing()).isEqualTo(DEFAULT_CELL_SPACING);
        assertThat(attributes.isCircularScroll()).isFalse();
        assertThat(attributes.isSnapToPosition()).isFalse();
        assertThat(attributes.selectOnSnap()).isFalse();
        assertThat(attributes.isViewPager()).isFalse();
        assertThat(attributes.getViewPagerInterval()).isEqualTo(DEFAULT_VIEW_PAGER_INTERVAL);
        assertThat(attributes.selectWhileScrolling()).isFalse();
        assertThat(attributes.getSnapPosition()).isEqualTo(SnapPosition.center);
        assertThat(attributes.getDivider()).isNull();
        assertThat(attributes.getDividerSize()).isEqualTo(DEFAULT_DIVIDER_SIZE);
    }

    @Test
    public void gridAttributesWithoutAnAttributeSet_fallBackToTheDefaults() {
        final GridAttributes gridAttributes =
                new GridAttributes(RuntimeEnvironment.getApplication(), null);

        assertThat(gridAttributes.getNumberOfViewsPerCell()).isEqualTo(DEFAULT_VIEWS_PER_CELL);
        assertThat(gridAttributes.isTop()).isTrue();
        assertThat(gridAttributes.isBottom()).isFalse();
        assertThat(gridAttributes.isLeft()).isFalse();
        assertThat(gridAttributes.isRight()).isFalse();
    }

    @Test
    public void gridPatternAttributesWithoutAnAttributeSet_fallBackToTheDefault() {
        final GridPatternAttributes gridPatternAttributes =
                new GridPatternAttributes(RuntimeEnvironment.getApplication(), null);

        assertThat(gridPatternAttributes.getRatio()).isEqualTo(DEFAULT_RATIO);
    }

    @Test
    public void inflatedListView_withHorizontalOrientationInXml_laysOutChildrenAcrossTheWidth() {
        final ListView<?> listView =
                inflateAndLayOut(R.layout.attribute_parsing_inflated_horizontal);

        assertThat(listView.getChildCount()).isGreaterThan(1);
        final View firstChild = listView.getChildAt(0);
        final View secondChild = listView.getChildAt(1);
        assertThat(firstChild.getLeft()).isEqualTo(0);
        assertThat(secondChild.getLeft()).isEqualTo(firstChild.getWidth());
        assertThat(secondChild.getTop()).isEqualTo(firstChild.getTop());
    }

    @Test
    public void inflatedListView_withVerticalOrientationInXml_laysOutChildrenDownTheHeight() {
        assertAttributeIsDeclared(
                R.layout.attribute_parsing_inflated_vertical,
                R.id.horizontal_list_view,
                ORIENTATION_ATTRIBUTE);
        final ListView<?> listView = inflateAndLayOut(R.layout.attribute_parsing_inflated_vertical);

        assertThat(listView.getChildCount()).isGreaterThan(1);
        final View firstChild = listView.getChildAt(0);
        final View secondChild = listView.getChildAt(1);
        assertThat(firstChild.getTop()).isEqualTo(0);
        assertThat(secondChild.getTop()).isEqualTo(firstChild.getHeight());
        assertThat(secondChild.getLeft()).isEqualTo(firstChild.getLeft());
    }

    private static ListView<?> inflateAndLayOut(final int layoutId) {
        final ListView<?> listView = inflate(layoutId);
        final int measureSpec =
                View.MeasureSpec.makeMeasureSpec(
                        HORIZONTAL_LIST_VIEW_WIDTH, View.MeasureSpec.EXACTLY);
        listView.measure(measureSpec, measureSpec);
        listView.layout(0, 0, HORIZONTAL_LIST_VIEW_WIDTH, HORIZONTAL_LIST_VIEW_WIDTH);
        return listView;
    }

    private static Attributes listViewAttributesFrom(final int layoutId, final int idResource) {
        final Context context = RuntimeEnvironment.getApplication();
        final XmlResourceParser parser = context.getResources().getLayout(layoutId);
        try {
            final AttributeSet attributeSet = advanceToTagWithId(parser, idResource);
            return new Attributes(context, attributeSet);
        } finally {
            parser.close();
        }
    }

    private static GridAttributes gridAttributesFrom(final int layoutId, final int idResource) {
        final Context context = RuntimeEnvironment.getApplication();
        final XmlResourceParser parser = context.getResources().getLayout(layoutId);
        try {
            final AttributeSet attributeSet = advanceToTagWithId(parser, idResource);
            return new GridAttributes(context, attributeSet);
        } finally {
            parser.close();
        }
    }

    private static GridPatternAttributes gridPatternAttributesFrom(
            final int layoutId, final int idResource) {
        final Context context = RuntimeEnvironment.getApplication();
        final XmlResourceParser parser = context.getResources().getLayout(layoutId);
        try {
            final AttributeSet attributeSet = advanceToTagWithId(parser, idResource);
            return new GridPatternAttributes(context, attributeSet);
        } finally {
            parser.close();
        }
    }

    private static void assertAttributeIsDeclared(
            final int layoutId, final int idResource, final String attributeName) {
        final Context context = RuntimeEnvironment.getApplication();
        final XmlResourceParser parser = context.getResources().getLayout(layoutId);
        try {
            final AttributeSet attributeSet = advanceToTagWithId(parser, idResource);
            final String value = attributeSet.getAttributeValue(RES_AUTO_NAMESPACE, attributeName);
            assertThat(value).isNotNull();
        } finally {
            parser.close();
        }
    }

    private static int dimensionPixelSize(final int dimenId) {
        final Resources resources = RuntimeEnvironment.getApplication().getResources();
        return resources.getDimensionPixelSize(dimenId);
    }

    private static AttributeSet advanceToTagWithId(
            final XmlResourceParser parser, final int idResource) {
        try {
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    final int tagId =
                            parser.getAttributeResourceValue(
                                    ANDROID_NAMESPACE, ID_ATTRIBUTE, NO_ID_RESOURCE);
                    if (tagId == idResource) {
                        return parser;
                    }
                }
                event = parser.next();
            }
        } catch (final XmlPullParserException | IOException exception) {
            throw new IllegalStateException("could not read the layout", exception);
        }
        throw new IllegalStateException("the layout has no tag with the requested id");
    }

    private static ListView<?> inflateBasicListView() {
        return inflate(R.layout.basic);
    }

    private static ListView<?> inflate(final int layoutId) {
        final ActivityController<TestActivity> controller =
                Robolectric.buildActivity(TestActivity.class);
        final TestActivity testActivity = controller.get();
        testActivity.setLayoutId(layoutId);
        controller.create();
        return testActivity.findViewById(R.id.horizontal_list_view);
    }

    public static class TestActivity extends Activity {

        private int layoutId;

        public TestActivity() {
            super();
        }

        public void setLayoutId(int layoutId) {
            this.layoutId = layoutId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(layoutId);
            final ListView<TestAdapter> horizontalScrollView =
                    findViewById(R.id.horizontal_list_view);
            final TestAdapter testAdapter = new TestAdapter();
            horizontalScrollView.setAdapter(testAdapter);
        }

        public class TestAdapter extends BaseAdapter {

            private int mAdapterSize = TEST_ADAPTER_COUNT;

            public void setAdapterSize(final int adapterSize) {
                mAdapterSize = adapterSize;
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                return mAdapterSize;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(parent.getContext(), R.layout.text_view, null);
                convertView.setTag("position: " + position);
                return convertView;
            }
        }
    }
}
