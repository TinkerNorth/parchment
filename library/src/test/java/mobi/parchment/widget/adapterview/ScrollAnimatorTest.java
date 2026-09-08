// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystemClock;

@RunWith(RobolectricTestRunner.class)
public class ScrollAnimatorTest {

    private static final boolean HORIZONTAL = false;
    private static final boolean VERTICAL = true;
    private static final int ONE_INCH_AT_ROBOLECTRIC_DENSITY = 160;

    private final Context mContext = ApplicationProvider.getApplicationContext();

    @Test
    public void snapTo_takesOneHundredMillisecondsPerInchStretchedForDeceleration() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.snapTo(ONE_INCH_AT_ROBOLECTRIC_DENSITY);

        assertThat(animator.getDuration()).isEqualTo(298);
    }

    @Test
    public void snapTo_backwardsTakesTheSameTimeAsForwards() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.snapTo(-ONE_INCH_AT_ROBOLECTRIC_DENSITY);

        assertThat(animator.getDuration()).isEqualTo(298);
    }

    @Test
    public void snapTo_isCappedAtHalfASecond() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.snapTo(5000);

        assertThat(animator.getDuration()).isEqualTo(500);
    }

    @Test
    public void snapTo_deceleratesInsteadOfCrawlingAtTheEnd() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.snapTo(100);
        final int duration = animator.getDuration();

        ShadowSystemClock.advanceBy(Duration.ofMillis(duration / 2));
        assertThat(animator.computeScrollOffset()).isTrue();
        assertThat(animator.getCurrrentOffset()).isEqualTo(75);

        ShadowSystemClock.advanceBy(Duration.ofMillis(duration / 4));
        assertThat(animator.computeScrollOffset()).isTrue();
        assertThat(animator.getCurrrentOffset()).isEqualTo(93);

        ShadowSystemClock.advanceBy(Duration.ofMillis(duration));
        assertThat(animator.computeScrollOffset()).isTrue();
        assertThat(animator.getCurrrentOffset()).isEqualTo(100);
        assertThat(animator.isFinished()).isTrue();
    }

    @Test
    public void snapTo_vertical_movesTheVerticalOffset() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, VERTICAL);
        animator.snapTo(100);

        ShadowSystemClock.advanceBy(Duration.ofMillis(animator.getDuration()));
        animator.computeScrollOffset();

        assertThat(animator.getCurrrentOffset()).isEqualTo(100);
    }

    @Test
    public void flingBy_keepsTheFlingPhysicsUnchangedByTheSnapCurve() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.flingBy(-1000f, 0f);

        assertThat(animator.getDuration()).isEqualTo(555);
        ShadowSystemClock.advanceBy(Duration.ofMillis(555));
        animator.computeScrollOffset();
        assertThat(animator.getCurrrentOffset()).isEqualTo(-194);
    }

    @Test
    public void getFinalOffset_reportsWhereTheFlingWillEnd() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.flingBy(-1000f, 0f);

        assertThat(animator.getFinalOffset()).isEqualTo(-194);
    }

    @Test
    public void setFinalOffset_retargetsTheFlingAndKeepsItsDuration() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.flingBy(-1000f, 0f);

        animator.setFinalOffset(-231);

        assertThat(animator.getFinalOffset()).isEqualTo(-231);
        assertThat(animator.getDuration()).isEqualTo(555);
        ShadowSystemClock.advanceBy(Duration.ofMillis(555));
        animator.computeScrollOffset();
        assertThat(animator.getCurrrentOffset()).isEqualTo(-231);
        assertThat(animator.isFinished()).isTrue();
    }

    @Test
    public void setFinalOffset_vertical_retargetsTheVerticalFling() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, VERTICAL);
        animator.flingBy(0f, -1000f);

        animator.setFinalOffset(-231);

        assertThat(animator.getFinalOffset()).isEqualTo(-231);
        ShadowSystemClock.advanceBy(Duration.ofMillis(555));
        animator.computeScrollOffset();
        assertThat(animator.getCurrrentOffset()).isEqualTo(-231);
    }

    @Test
    public void flingBy_vertical_movesTheVerticalOffset() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, VERTICAL);

        animator.flingBy(0f, -1000f);

        ShadowSystemClock.advanceBy(Duration.ofMillis(animator.getDuration()));
        animator.computeScrollOffset();
        assertThat(animator.getCurrrentOffset()).isEqualTo(-194);
    }
}
