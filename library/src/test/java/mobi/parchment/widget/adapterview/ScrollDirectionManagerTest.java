// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.view.View;
import android.widget.FrameLayout;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ScrollDirectionManagerTest {

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private static final int SIZE = 500;
    private static final int BREADTH = 80;
    private static final int PADDING_LEFT = 1;
    private static final int PADDING_TOP = 2;
    private static final int PADDING_RIGHT = 4;
    private static final int PADDING_BOTTOM = 8;
    private static final int WIDTH_MEASURE_SPEC =
            View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY);
    private static final int HEIGHT_MEASURE_SPEC =
            View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.AT_MOST);

    @Test
    public void getSizeMeasureSpec_scrollingVertically_isTheHeightSpec() {
        final ScrollDirectionManager scrollDirectionManager = new ScrollDirectionManager(VERTICAL);

        final int sizeMeasureSpec =
                scrollDirectionManager.getSizeMeasureSpec(WIDTH_MEASURE_SPEC, HEIGHT_MEASURE_SPEC);

        assertThat(sizeMeasureSpec).isEqualTo(HEIGHT_MEASURE_SPEC);
    }

    @Test
    public void getSizeMeasureSpec_scrollingHorizontally_isTheWidthSpec() {
        final ScrollDirectionManager scrollDirectionManager =
                new ScrollDirectionManager(HORIZONTAL);

        final int sizeMeasureSpec =
                scrollDirectionManager.getSizeMeasureSpec(WIDTH_MEASURE_SPEC, HEIGHT_MEASURE_SPEC);

        assertThat(sizeMeasureSpec).isEqualTo(WIDTH_MEASURE_SPEC);
    }

    @Test
    public void getBreadthMeasureSpec_scrollingVertically_isTheWidthSpec() {
        final ScrollDirectionManager scrollDirectionManager = new ScrollDirectionManager(VERTICAL);

        final int breadthMeasureSpec =
                scrollDirectionManager.getBreadthMeasureSpec(
                        WIDTH_MEASURE_SPEC, HEIGHT_MEASURE_SPEC);

        assertThat(breadthMeasureSpec).isEqualTo(WIDTH_MEASURE_SPEC);
    }

    @Test
    public void getBreadthMeasureSpec_scrollingHorizontally_isTheHeightSpec() {
        final ScrollDirectionManager scrollDirectionManager =
                new ScrollDirectionManager(HORIZONTAL);

        final int breadthMeasureSpec =
                scrollDirectionManager.getBreadthMeasureSpec(
                        WIDTH_MEASURE_SPEC, HEIGHT_MEASURE_SPEC);

        assertThat(breadthMeasureSpec).isEqualTo(HEIGHT_MEASURE_SPEC);
    }

    @Test
    public void getViewGroupBreadthPadding_scrollingVertically_isTheLeftAndRightPadding() {
        final ScrollDirectionManager scrollDirectionManager = new ScrollDirectionManager(VERTICAL);
        final FrameLayout viewGroup = paddedViewGroup();

        final int breadthPadding = scrollDirectionManager.getViewGroupBreadthPadding(viewGroup);

        assertThat(breadthPadding).isEqualTo(PADDING_LEFT + PADDING_RIGHT);
    }

    @Test
    public void getViewGroupBreadthPadding_scrollingHorizontally_isTheTopAndBottomPadding() {
        final ScrollDirectionManager scrollDirectionManager =
                new ScrollDirectionManager(HORIZONTAL);
        final FrameLayout viewGroup = paddedViewGroup();

        final int breadthPadding = scrollDirectionManager.getViewGroupBreadthPadding(viewGroup);

        assertThat(breadthPadding).isEqualTo(PADDING_TOP + PADDING_BOTTOM);
    }

    @Test
    public void toWidth_scrollingVertically_isTheBreadth() {
        final ScrollDirectionManager scrollDirectionManager = new ScrollDirectionManager(VERTICAL);

        final int width = scrollDirectionManager.toWidth(SIZE, BREADTH);

        assertThat(width).isEqualTo(BREADTH);
    }

    @Test
    public void toWidth_scrollingHorizontally_isTheSize() {
        final ScrollDirectionManager scrollDirectionManager =
                new ScrollDirectionManager(HORIZONTAL);

        final int width = scrollDirectionManager.toWidth(SIZE, BREADTH);

        assertThat(width).isEqualTo(SIZE);
    }

    @Test
    public void toHeight_scrollingVertically_isTheSize() {
        final ScrollDirectionManager scrollDirectionManager = new ScrollDirectionManager(VERTICAL);

        final int height = scrollDirectionManager.toHeight(SIZE, BREADTH);

        assertThat(height).isEqualTo(SIZE);
    }

    @Test
    public void toHeight_scrollingHorizontally_isTheBreadth() {
        final ScrollDirectionManager scrollDirectionManager =
                new ScrollDirectionManager(HORIZONTAL);

        final int height = scrollDirectionManager.toHeight(SIZE, BREADTH);

        assertThat(height).isEqualTo(BREADTH);
    }

    private static FrameLayout paddedViewGroup() {
        final FrameLayout viewGroup = new FrameLayout(ApplicationProvider.getApplicationContext());
        viewGroup.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        return viewGroup;
    }
}
