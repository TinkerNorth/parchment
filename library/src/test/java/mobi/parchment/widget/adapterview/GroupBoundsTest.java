// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import mobi.parchment.widget.adapterview.gridview.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GroupBoundsTest {

    private static final boolean IS_VERTICAL = true;
    private static final int NO_PIXEL = 0;
    private static final int ONLY_VIEW_LEFT = 11;
    private static final int ONLY_VIEW_TOP = 22;
    private static final int ONLY_VIEW_RIGHT = 33;
    private static final int ONLY_VIEW_BOTTOM = 44;
    private static final int LOWEST_LEFT = 5;
    private static final int LOWEST_TOP = 7;
    private static final int HIGHEST_RIGHT = 400;
    private static final int HIGHEST_BOTTOM = 500;
    private static final int INNER_LEFT = 100;
    private static final int INNER_TOP = 110;
    private static final int INNER_RIGHT = 200;
    private static final int INNER_BOTTOM = 210;
    private static final int OTHER_INNER_LEFT = 150;
    private static final int OTHER_INNER_TOP = 160;
    private static final int OTHER_INNER_RIGHT = 250;
    private static final int OTHER_INNER_BOTTOM = 260;

    @Test
    public void anEmptyGroup_reportsNoPixelForEveryBound() {
        final Group group = new Group(IS_VERTICAL);

        assertThat(group.getTop()).isEqualTo(NO_PIXEL);
        assertThat(group.getBottom()).isEqualTo(NO_PIXEL);
        assertThat(group.getLeft()).isEqualTo(NO_PIXEL);
        assertThat(group.getRight()).isEqualTo(NO_PIXEL);
        assertThat(group.getMeasuredWidth()).isEqualTo(NO_PIXEL);
        assertThat(group.getMeasuredHeight()).isEqualTo(NO_PIXEL);
    }

    @Test
    public void aGroupOfOneView_reportsThatViewsBounds() {
        final Group group = new Group(IS_VERTICAL);
        group.addView(
                laidOutView(ONLY_VIEW_LEFT, ONLY_VIEW_TOP, ONLY_VIEW_RIGHT, ONLY_VIEW_BOTTOM));

        assertThat(group.getLeft()).isEqualTo(ONLY_VIEW_LEFT);
        assertThat(group.getTop()).isEqualTo(ONLY_VIEW_TOP);
        assertThat(group.getRight()).isEqualTo(ONLY_VIEW_RIGHT);
        assertThat(group.getBottom()).isEqualTo(ONLY_VIEW_BOTTOM);
    }

    @Test
    public void aGroupWhoseFirstViewIsNotTheExtreme_reportsTheExtremeOfEveryView() {
        final Group group = new Group(IS_VERTICAL);
        group.addView(laidOutView(INNER_LEFT, INNER_TOP, INNER_RIGHT, INNER_BOTTOM));
        group.addView(laidOutView(LOWEST_LEFT, LOWEST_TOP, HIGHEST_RIGHT, HIGHEST_BOTTOM));
        group.addView(
                laidOutView(
                        OTHER_INNER_LEFT, OTHER_INNER_TOP, OTHER_INNER_RIGHT, OTHER_INNER_BOTTOM));

        assertThat(group.getLeft()).isEqualTo(LOWEST_LEFT);
        assertThat(group.getTop()).isEqualTo(LOWEST_TOP);
        assertThat(group.getRight()).isEqualTo(HIGHEST_RIGHT);
        assertThat(group.getBottom()).isEqualTo(HIGHEST_BOTTOM);
    }

    @Test
    public void aGroupWhoseFirstViewIsTheExtreme_keepsTheFirstViewsBounds() {
        final Group group = new Group(IS_VERTICAL);
        group.addView(laidOutView(LOWEST_LEFT, LOWEST_TOP, HIGHEST_RIGHT, HIGHEST_BOTTOM));
        group.addView(laidOutView(INNER_LEFT, INNER_TOP, INNER_RIGHT, INNER_BOTTOM));
        group.addView(
                laidOutView(
                        OTHER_INNER_LEFT, OTHER_INNER_TOP, OTHER_INNER_RIGHT, OTHER_INNER_BOTTOM));

        assertThat(group.getLeft()).isEqualTo(LOWEST_LEFT);
        assertThat(group.getTop()).isEqualTo(LOWEST_TOP);
        assertThat(group.getRight()).isEqualTo(HIGHEST_RIGHT);
        assertThat(group.getBottom()).isEqualTo(HIGHEST_BOTTOM);
    }

    private static View laidOutView(
            final int left, final int top, final int right, final int bottom) {
        final Context context = ApplicationProvider.getApplicationContext();
        final View view = new View(context);
        view.layout(left, top, right, bottom);
        return view;
    }
}
