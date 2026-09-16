// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PlaygroundRulesTest {

    private static final int SIX_ITEMS = 6;
    private static final int SEVEN_ITEMS = 7;

    @Test
    public void viewPagerInterval_withViewPager_hasAnEffect() {
        final PlaygroundOptions options = options().isViewPager(true).build();

        assertThat(PlaygroundRules.viewPagerIntervalHasAnEffect(options)).isTrue();
    }

    @Test
    public void viewPagerInterval_withoutViewPager_hasNoEffect() {
        final PlaygroundOptions options = options().isViewPager(false).build();

        assertThat(PlaygroundRules.viewPagerIntervalHasAnEffect(options)).isFalse();
    }

    @Test
    public void selectOnSnap_withoutSnapToPosition_hasNoEffect() {
        final PlaygroundOptions options =
                options()
                        .snapToPosition(false)
                        .snapPosition(SnapPositionOption.center)
                        .isCircularScroll(false)
                        .build();

        assertThat(PlaygroundRules.selectOnSnapHasAnEffect(options)).isFalse();
    }

    @Test
    public void selectOnSnap_snappingToAPositionUnderCircularScroll_hasAnEffect() {
        final PlaygroundOptions options =
                options()
                        .snapToPosition(true)
                        .snapPosition(SnapPositionOption.center)
                        .isCircularScroll(true)
                        .build();

        assertThat(PlaygroundRules.selectOnSnapHasAnEffect(options)).isTrue();
    }

    @Test
    public void selectOnSnap_snappingOnScreenWithAnEndToPushAgainst_hasAnEffect() {
        final PlaygroundOptions options =
                options()
                        .snapToPosition(true)
                        .snapPosition(SnapPositionOption.onScreen)
                        .isCircularScroll(false)
                        .build();

        assertThat(PlaygroundRules.selectOnSnapHasAnEffect(options)).isTrue();
    }

    @Test
    public void selectOnSnap_snappingOnScreenUnderCircularScroll_hasNoEffect() {
        final PlaygroundOptions options =
                options()
                        .snapToPosition(true)
                        .snapPosition(SnapPositionOption.onScreen)
                        .isCircularScroll(true)
                        .build();

        assertThat(PlaygroundRules.selectOnSnapHasAnEffect(options)).isFalse();
    }

    @Test
    public void selectWhileScrolling_withOnScreen_hasNoEffect() {
        final PlaygroundOptions options =
                options().snapToPosition(true).snapPosition(SnapPositionOption.onScreen).build();

        assertThat(PlaygroundRules.selectWhileScrollingHasAnEffect(options)).isFalse();
    }

    @Test
    public void selectWhileScrolling_withAnotherPositionAndNoSnapToPosition_hasAnEffect() {
        final PlaygroundOptions options =
                options().snapToPosition(false).snapPosition(SnapPositionOption.end).build();

        assertThat(PlaygroundRules.selectWhileScrollingHasAnEffect(options)).isTrue();
    }

    @Test
    public void scrollWithinContent_withAPositionOtherThanOnScreenAndEnds_hasAnEffect() {
        final PlaygroundOptions options =
                options().snapPosition(SnapPositionOption.start).isCircularScroll(false).build();

        assertThat(PlaygroundRules.scrollWithinContentHasAnEffect(options)).isTrue();
    }

    @Test
    public void scrollWithinContent_withOnScreen_hasNoEffect() {
        final PlaygroundOptions options =
                options().snapPosition(SnapPositionOption.onScreen).isCircularScroll(false).build();

        assertThat(PlaygroundRules.scrollWithinContentHasAnEffect(options)).isFalse();
    }

    @Test
    public void scrollWithinContent_underCircularScroll_hasNoEffect() {
        final PlaygroundOptions options =
                options().snapPosition(SnapPositionOption.start).isCircularScroll(true).build();

        assertThat(PlaygroundRules.scrollWithinContentHasAnEffect(options)).isFalse();
    }

    @Test
    public void snapPositions_underCircularScroll_areForcedOnScreen() {
        final PlaygroundOptions options = options().isCircularScroll(true).build();

        assertThat(PlaygroundRules.snapPositionsAreForcedOnScreen(options)).isTrue();
    }

    @Test
    public void snapPositions_withoutCircularScroll_areNotForced() {
        final PlaygroundOptions options = options().isCircularScroll(false).build();

        assertThat(PlaygroundRules.snapPositionsAreForcedOnScreen(options)).isFalse();
    }

    @Test
    public void pager_snappingToAPosition_restsAfterADrag() {
        final PlaygroundOptions options =
                options().snapToPosition(true).snapPosition(SnapPositionOption.start).build();

        assertThat(PlaygroundRules.pagerRestsAfterADrag(options)).isTrue();
    }

    @Test
    public void pager_withoutSnapToPosition_doesNotRestAfterADrag() {
        final PlaygroundOptions options =
                options().snapToPosition(false).snapPosition(SnapPositionOption.start).build();

        assertThat(PlaygroundRules.pagerRestsAfterADrag(options)).isFalse();
    }

    @Test
    public void pager_snappingOnScreen_doesNotRestAfterADrag() {
        final PlaygroundOptions options =
                options().snapToPosition(true).snapPosition(SnapPositionOption.onScreen).build();

        assertThat(PlaygroundRules.pagerRestsAfterADrag(options)).isFalse();
    }

    @Test
    public void topAndBottom_inAVerticalGridWithFullRows_haveAnEffect() {
        final PlaygroundOptions options = verticalGrid().build();

        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.top, SIX_ITEMS))
                .isTrue();
        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.bottom, SIX_ITEMS))
                .isTrue();
    }

    @Test
    public void leftAndRight_inAVerticalGridWithAShortRow_haveAnEffect() {
        final PlaygroundOptions options = verticalGrid().build();

        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.left, SEVEN_ITEMS))
                .isTrue();
        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.right, SEVEN_ITEMS))
                .isTrue();
    }

    @Test
    public void leftAndRight_inAVerticalGridWithFullRows_haveNoEffect() {
        final PlaygroundOptions options = verticalGrid().build();

        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.left, SIX_ITEMS))
                .isFalse();
        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.right, SIX_ITEMS))
                .isFalse();
    }

    @Test
    public void leftAndRight_inAHorizontalGridWithAShortColumn_haveNoEffect() {
        final PlaygroundOptions options = horizontalGrid().build();

        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.left, SEVEN_ITEMS))
                .isFalse();
        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.right, SEVEN_ITEMS))
                .isFalse();
    }

    @Test
    public void topAndBottom_inAHorizontalGridWithAShortColumn_haveAnEffect() {
        final PlaygroundOptions options = horizontalGrid().build();

        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.top, SEVEN_ITEMS))
                .isTrue();
        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.bottom, SEVEN_ITEMS))
                .isTrue();
    }

    @Test
    public void topAndBottom_inAHorizontalGridWithFullColumns_haveNoEffect() {
        final PlaygroundOptions options = horizontalGrid().build();

        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.top, SIX_ITEMS))
                .isFalse();
        assertThat(PlaygroundRules.gravityHasAnEffect(options, GravityOption.bottom, SIX_ITEMS))
                .isFalse();
    }

    @Test
    public void theLastLine_withItemsLeftOver_isShort() {
        final PlaygroundOptions options = verticalGrid().build();

        assertThat(PlaygroundRules.hasAShortLastLine(options, SEVEN_ITEMS)).isTrue();
    }

    @Test
    public void theLastLine_withNoItemsLeftOver_isFull() {
        final PlaygroundOptions options = verticalGrid().build();

        assertThat(PlaygroundRules.hasAShortLastLine(options, SIX_ITEMS)).isFalse();
    }

    @Test
    public void gravityToDemo_whenTheChosenOneHasAnEffect_isTheChosenOne() {
        final PlaygroundOptions options = verticalGrid().gravity(GravityOption.right).build();

        assertThat(PlaygroundRules.gravityToDemo(options, SEVEN_ITEMS))
                .isEqualTo(GravityOption.right);
    }

    @Test
    public void gravityToDemo_whenTheChosenOneHasNoEffect_isTheFirstWithAnEffect() {
        final PlaygroundOptions options = horizontalGrid().gravity(GravityOption.right).build();

        assertThat(PlaygroundRules.gravityToDemo(options, SEVEN_ITEMS))
                .isEqualTo(GravityOption.top);
    }

    @Test
    public void gravityToDemo_whenNoneHasAnEffect_isTheChosenOne() {
        final PlaygroundOptions options = horizontalGrid().gravity(GravityOption.right).build();

        assertThat(PlaygroundRules.gravityToDemo(options, SIX_ITEMS))
                .isEqualTo(GravityOption.right);
    }

    private static PlaygroundOptions.Builder options() {
        return new PlaygroundOptions.Builder();
    }

    private static PlaygroundOptions.Builder verticalGrid() {
        return options()
                .viewKind(ViewKind.gridView)
                .orientation(OrientationOption.vertical)
                .viewsPerCell(ViewsPerCellOption.three);
    }

    private static PlaygroundOptions.Builder horizontalGrid() {
        return options()
                .viewKind(ViewKind.gridView)
                .orientation(OrientationOption.horizontal)
                .viewsPerCell(ViewsPerCellOption.three);
    }
}
