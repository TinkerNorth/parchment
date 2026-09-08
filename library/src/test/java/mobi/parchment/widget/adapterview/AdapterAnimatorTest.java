// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.test.core.app.ApplicationProvider;
import mobi.parchment.widget.adapterview.listview.ListLayoutManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AdapterAnimatorTest {

    private AdapterAnimator mAdapterAnimator;

    @Before
    public void setup() {
        final Context context = ApplicationProvider.getApplicationContext();
        final FrameLayout viewGroup = new FrameLayout(context);
        final LayoutManagerAttributes attributes =
                new LayoutManagerAttributes(
                        false, false, false, 0, SnapPosition.onScreen, 0, false, false, false);
        final ListLayoutManager layoutManager =
                new ListLayoutManager(viewGroup, null, new AdapterViewManager(), attributes);
        mAdapterAnimator =
                new AdapterAnimator(
                        viewGroup,
                        false,
                        false,
                        new LayoutManagerBridge(layoutManager),
                        ViewConfiguration.get(context));
    }

    @Test
    public void getAnimation_beforeAnyScrollOffsetIsComputed_stopsTheFling() {
        mAdapterAnimator.onFling(down(), up(), 1000f, 0f);
        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);

        final Animation animation = mAdapterAnimator.getAnimation();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.notMoving);
        assertThat(animation.getDisplacement()).isEqualTo(0);
    }

    @Test
    public void getAnimation_afterTheScrollOffsetIsComputed_keepsFlinging() {
        mAdapterAnimator.onFling(down(), up(), 1000f, 0f);
        mAdapterAnimator.computeScrollOffset();

        mAdapterAnimator.getAnimation();

        assertThat(mAdapterAnimator.getState()).isEqualTo(AdapterAnimator.State.flinging);
    }

    private static MotionEvent down() {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0);
    }

    private static MotionEvent up() {
        return MotionEvent.obtain(0, 50, MotionEvent.ACTION_UP, 200f, 0f, 0);
    }
}
