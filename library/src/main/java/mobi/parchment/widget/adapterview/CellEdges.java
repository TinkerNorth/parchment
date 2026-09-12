// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

public final class CellEdges {

    private CellEdges() {}

    public static boolean isAcrossTheEndEdge(
            final int itemStart,
            final int itemEnd,
            final int neighbourStart,
            final int neighbourEnd) {
        final boolean neighbourStartsNoEarlier = neighbourStart >= itemStart;
        final boolean neighbourReachesFurther = neighbourEnd > itemEnd;
        return neighbourStartsNoEarlier && neighbourReachesFurther;
    }

    public static int getOverlapStart(final int itemStart, final int neighbourStart) {
        return Math.max(itemStart, neighbourStart);
    }

    public static int getOverlapEnd(final int itemEnd, final int neighbourEnd) {
        return Math.min(itemEnd, neighbourEnd);
    }

    public static boolean isAnOverlap(final int overlapStart, final int overlapEnd) {
        return overlapEnd > overlapStart;
    }

    public static boolean isInTheGap(
            final int itemStart,
            final int itemEnd,
            final int neighbourStart,
            final int neighbourEnd,
            final int candidateStart,
            final int candidateEnd) {
        final boolean isAfterTheItem =
                isAcrossTheEndEdge(itemStart, itemEnd, candidateStart, candidateEnd);
        final boolean isBeforeTheNeighbour =
                isAcrossTheEndEdge(candidateStart, candidateEnd, neighbourStart, neighbourEnd);
        return isAfterTheItem && isBeforeTheNeighbour;
    }

    public static boolean reachesTheBand(
            final int bandStart,
            final int bandEnd,
            final int candidateStart,
            final int candidateEnd) {
        final int overlapStart = getOverlapStart(bandStart, candidateStart);
        final int overlapEnd = getOverlapEnd(bandEnd, candidateEnd);
        return isAnOverlap(overlapStart, overlapEnd);
    }
}
