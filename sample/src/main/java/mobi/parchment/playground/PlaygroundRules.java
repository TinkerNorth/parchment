// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

public final class PlaygroundRules {

    private static final int NOTHING_LEFT_OVER = 0;

    private PlaygroundRules() {}

    public static boolean viewPagerIntervalHasAnEffect(final PlaygroundOptions options) {
        return options.isViewPager();
    }

    public static boolean selectOnSnapHasAnEffect(final PlaygroundOptions options) {
        final boolean isSnapToPosition = options.isSnapToPosition();
        final boolean settlesOnACell = settlesOnACell(options);
        final boolean hasAnEndToPushAgainst = !options.isCircularScroll();
        final boolean aSnapCanLand = settlesOnACell || hasAnEndToPushAgainst;
        return isSnapToPosition && aSnapCanLand;
    }

    public static boolean selectWhileScrollingHasAnEffect(final PlaygroundOptions options) {
        return settlesOnACell(options);
    }

    public static boolean snapPositionsAreForcedOnScreen(final PlaygroundOptions options) {
        return options.isCircularScroll();
    }

    public static boolean pagerRestsAfterADrag(final PlaygroundOptions options) {
        final boolean isSnapToPosition = options.isSnapToPosition();
        final boolean settlesOnACell = settlesOnACell(options);
        return isSnapToPosition && settlesOnACell;
    }

    public static boolean gravityHasAnEffect(
            final PlaygroundOptions options, final GravityOption gravity, final int itemCount) {
        final OrientationOption orientation = options.getOrientation();
        final boolean isAlongTheScrollAxis = gravity.getAxis() == orientation;
        if (isAlongTheScrollAxis) {
            return aViewCanBeSmallerThanItsLine(orientation);
        }
        return hasAShortLastLine(options, itemCount);
    }

    public static boolean hasAShortLastLine(final PlaygroundOptions options, final int itemCount) {
        final int viewsPerLine = options.getViewsPerCell().getCount();
        final int leftOver = itemCount % viewsPerLine;
        return leftOver != NOTHING_LEFT_OVER;
    }

    public static GravityOption gravityToDemo(
            final PlaygroundOptions options, final int itemCount) {
        final GravityOption chosen = options.getGravity();
        final boolean chosenHasAnEffect = gravityHasAnEffect(options, chosen, itemCount);
        if (chosenHasAnEffect) {
            return chosen;
        }
        return firstGravityWithAnEffect(options, itemCount, chosen);
    }

    private static GravityOption firstGravityWithAnEffect(
            final PlaygroundOptions options, final int itemCount, final GravityOption fallback) {
        for (final GravityOption gravity : GravityOption.values()) {
            final boolean hasAnEffect = gravityHasAnEffect(options, gravity, itemCount);
            if (hasAnEffect) {
                return gravity;
            }
        }
        return fallback;
    }

    private static boolean settlesOnACell(final PlaygroundOptions options) {
        final boolean isOnScreen = options.getSnapPosition() == SnapPositionOption.onScreen;
        return !isOnScreen;
    }

    private static boolean aViewCanBeSmallerThanItsLine(final OrientationOption orientation) {
        final Cell cell = ViewKind.gridView.getCell(orientation);
        return cell.variesAlongTheScrollAxis();
    }
}
