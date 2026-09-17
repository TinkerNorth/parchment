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
    private static final int TEN_INCHES = 10 * ONE_INCH_AT_ROBOLECTRIC_DENSITY;
    private static final int A_HUNDRED_INCHES = 100 * ONE_INCH_AT_ROBOLECTRIC_DENSITY;
    private static final int A_LONG_DISTANCE = 5000;
    private static final int A_SNAP_DISTANCE = 100;
    private static final int HALVES = 2;
    private static final int TEN_MILLISECONDS = 10;
    private static final int SNAP_DURATION_FOR_ONE_INCH = 298;
    private static final int SEEK_DURATION_FOR_TEN_INCHES = 250;
    private static final int LANDING_DURATION_FOR_TEN_INCHES = 500;
    private static final int LANDING_DURATION_FOR_A_LONG_DISTANCE = 1563;
    private static final int THREE_QUARTERS_OF_TEN_INCHES = 1200;
    private static final int TEN_MILLISECONDS_OF_SEEKING = 64;

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

    @Test
    public void seekBy_takesTwentyFiveMillisecondsPerInchAtAConstantSpeed() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.seekBy(TEN_INCHES);

        assertThat(animator.getDuration()).isEqualTo(SEEK_DURATION_FOR_TEN_INCHES);
        ShadowSystemClock.advanceBy(Duration.ofMillis(SEEK_DURATION_FOR_TEN_INCHES / HALVES));
        assertThat(animator.computeScrollOffset()).isTrue();
        assertThat(animator.getCurrrentOffset()).isEqualTo(TEN_INCHES / HALVES);
    }

    @Test
    public void seekBy_backwardsTakesTheSameTimeAsForwards() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.seekBy(-TEN_INCHES);

        assertThat(animator.getDuration()).isEqualTo(SEEK_DURATION_FOR_TEN_INCHES);
    }

    @Test
    public void seekBy_runsToTheDistanceItWasGivenAndFinishes() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.seekBy(TEN_INCHES);

        ShadowSystemClock.advanceBy(Duration.ofMillis(SEEK_DURATION_FOR_TEN_INCHES));
        assertThat(animator.computeScrollOffset()).isTrue();

        assertThat(animator.getCurrrentOffset()).isEqualTo(TEN_INCHES);
        assertThat(animator.isFinished()).isTrue();
    }

    @Test
    public void landBy_stretchesTheSeekTimeByTheLandingRatio() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.landBy(TEN_INCHES);

        assertThat(animator.getDuration()).isEqualTo(LANDING_DURATION_FOR_TEN_INCHES);
    }

    @Test
    public void landBy_deceleratesOntoTheTarget() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.landBy(TEN_INCHES);

        ShadowSystemClock.advanceBy(Duration.ofMillis(LANDING_DURATION_FOR_TEN_INCHES / HALVES));
        assertThat(animator.computeScrollOffset()).isTrue();
        assertThat(animator.getCurrrentOffset()).isEqualTo(THREE_QUARTERS_OF_TEN_INCHES);

        ShadowSystemClock.advanceBy(Duration.ofMillis(LANDING_DURATION_FOR_TEN_INCHES));
        assertThat(animator.computeScrollOffset()).isTrue();
        assertThat(animator.getCurrrentOffset()).isEqualTo(TEN_INCHES);
        assertThat(animator.isFinished()).isTrue();
    }

    @Test
    public void landBy_isNotCappedAtHalfASecond() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);

        animator.landBy(A_LONG_DISTANCE);

        assertThat(animator.getDuration()).isEqualTo(LANDING_DURATION_FOR_A_LONG_DISTANCE);
    }

    @Test
    public void landBy_startsAtTheSeekSpeed() {
        final ScrollAnimator seek = new ScrollAnimator(mContext, HORIZONTAL);
        final ScrollAnimator landing = new ScrollAnimator(mContext, HORIZONTAL);
        seek.seekBy(A_HUNDRED_INCHES);
        landing.landBy(A_HUNDRED_INCHES);

        ShadowSystemClock.advanceBy(Duration.ofMillis(TEN_MILLISECONDS));
        seek.computeScrollOffset();
        landing.computeScrollOffset();

        assertThat(seek.getCurrrentOffset()).isEqualTo(TEN_MILLISECONDS_OF_SEEKING);
        assertThat(landing.getCurrrentOffset()).isEqualTo(TEN_MILLISECONDS_OF_SEEKING);
    }

    @Test
    public void seekBy_vertical_movesTheVerticalOffset() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, VERTICAL);
        animator.seekBy(TEN_INCHES);

        ShadowSystemClock.advanceBy(Duration.ofMillis(animator.getDuration()));
        animator.computeScrollOffset();

        assertThat(animator.getCurrrentOffset()).isEqualTo(TEN_INCHES);
    }

    @Test
    public void landBy_vertical_movesTheVerticalOffset() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, VERTICAL);
        animator.landBy(TEN_INCHES);

        ShadowSystemClock.advanceBy(Duration.ofMillis(animator.getDuration()));
        animator.computeScrollOffset();

        assertThat(animator.getCurrrentOffset()).isEqualTo(TEN_INCHES);
    }

    @Test
    public void seekBy_thenSnapTo_theSnapIsTheAnimationThatRuns() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.seekBy(TEN_INCHES);

        animator.snapTo(A_SNAP_DISTANCE);

        assertThat(animator.getFinalOffset()).isEqualTo(A_SNAP_DISTANCE);
        ShadowSystemClock.advanceBy(Duration.ofMillis(animator.getDuration()));
        animator.computeScrollOffset();
        assertThat(animator.getCurrrentOffset()).isEqualTo(A_SNAP_DISTANCE);
    }

    @Test
    public void landBy_thenSeekBy_theSeekIsTheAnimationThatRuns() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.landBy(A_SNAP_DISTANCE);

        animator.seekBy(TEN_INCHES);

        assertThat(animator.getFinalOffset()).isEqualTo(TEN_INCHES);
        assertThat(animator.getDuration()).isEqualTo(SEEK_DURATION_FOR_TEN_INCHES);
    }

    @Test
    public void forceFinished_endsARunningSeek() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.seekBy(TEN_INCHES);
        assertThat(animator.isFinished()).isFalse();

        animator.forceFinished(true);

        assertThat(animator.isFinished()).isTrue();
    }

    @Test
    public void snapTo_isUnchangedByTheSeekScroller() {
        final ScrollAnimator animator = new ScrollAnimator(mContext, HORIZONTAL);
        animator.seekBy(TEN_INCHES);

        animator.snapTo(ONE_INCH_AT_ROBOLECTRIC_DENSITY);

        assertThat(animator.getDuration()).isEqualTo(SNAP_DURATION_FOR_ONE_INCH);
    }
}
