// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CellEdgesTest {

    private static final int ITEM_START = 0;
    private static final int ITEM_END = 100;
    private static final int AFTER_A_GAP_START = 110;
    private static final int AFTER_A_GAP_END = 210;
    private static final int TOUCHING_START = 100;
    private static final int TOUCHING_END = 200;
    private static final int OVERLAPPING_START = 90;
    private static final int OVERLAPPING_END = 190;
    private static final int NESTED_START = 20;
    private static final int NESTED_END = 80;
    private static final int BEFORE_START = -110;
    private static final int BEFORE_END = -10;
    private static final int FURTHER_START = 220;
    private static final int FURTHER_END = 320;
    private static final int BAND_START = 0;
    private static final int BAND_END = 145;

    @Test
    public void isAcrossTheEndEdge_forANeighbourSeparatedByAGap_isTrue() {
        final boolean isAcross =
                CellEdges.isAcrossTheEndEdge(
                        ITEM_START, ITEM_END, AFTER_A_GAP_START, AFTER_A_GAP_END);

        assertThat(isAcross).isTrue();
    }

    @Test
    public void isAcrossTheEndEdge_forANeighbourTouchingTheEndEdge_isTrue() {
        final boolean isAcross =
                CellEdges.isAcrossTheEndEdge(ITEM_START, ITEM_END, TOUCHING_START, TOUCHING_END);

        assertThat(isAcross).isTrue();
    }

    @Test
    public void isAcrossTheEndEdge_forANeighbourOverlappingButReachingFurther_isTrue() {
        final boolean isAcross =
                CellEdges.isAcrossTheEndEdge(
                        ITEM_START, ITEM_END, OVERLAPPING_START, OVERLAPPING_END);

        assertThat(isAcross).isTrue();
    }

    @Test
    public void isAcrossTheEndEdge_forTheItemItself_isFalse() {
        final boolean isAcross =
                CellEdges.isAcrossTheEndEdge(ITEM_START, ITEM_END, ITEM_START, ITEM_END);

        assertThat(isAcross).isFalse();
    }

    @Test
    public void isAcrossTheEndEdge_forANeighbourBeforeTheItem_isFalse() {
        final boolean isAcross =
                CellEdges.isAcrossTheEndEdge(ITEM_START, ITEM_END, BEFORE_START, BEFORE_END);

        assertThat(isAcross).isFalse();
    }

    @Test
    public void isAcrossTheEndEdge_forANeighbourNestedInsideTheItem_isFalse() {
        final boolean isAcross =
                CellEdges.isAcrossTheEndEdge(ITEM_START, ITEM_END, NESTED_START, NESTED_END);

        assertThat(isAcross).isFalse();
    }

    @Test
    public void isAcrossTheEndEdge_forAPairOfItems_holdsInOneDirectionOnly() {
        final boolean forwards =
                CellEdges.isAcrossTheEndEdge(
                        ITEM_START, ITEM_END, AFTER_A_GAP_START, AFTER_A_GAP_END);
        final boolean backwards =
                CellEdges.isAcrossTheEndEdge(
                        AFTER_A_GAP_START, AFTER_A_GAP_END, ITEM_START, ITEM_END);

        assertThat(forwards).isTrue();
        assertThat(backwards).isFalse();
    }

    @Test
    public void overlap_ofTwoPartlyOverlappingSpans_isTheSharedRun() {
        final int overlapStart = CellEdges.getOverlapStart(ITEM_START, NESTED_START);
        final int overlapEnd = CellEdges.getOverlapEnd(ITEM_END, AFTER_A_GAP_END);

        assertThat(overlapStart).isEqualTo(NESTED_START);
        assertThat(overlapEnd).isEqualTo(ITEM_END);
        assertThat(CellEdges.isAnOverlap(overlapStart, overlapEnd)).isTrue();
    }

    @Test
    public void isAnOverlap_forSpansThatOnlyTouch_isFalse() {
        final int overlapStart = CellEdges.getOverlapStart(ITEM_START, TOUCHING_START);
        final int overlapEnd = CellEdges.getOverlapEnd(ITEM_END, TOUCHING_END);

        assertThat(overlapStart).isEqualTo(ITEM_END);
        assertThat(overlapEnd).isEqualTo(ITEM_END);
        assertThat(CellEdges.isAnOverlap(overlapStart, overlapEnd)).isFalse();
    }

    @Test
    public void isAnOverlap_forDisjointSpans_isFalse() {
        final int overlapStart = CellEdges.getOverlapStart(ITEM_START, AFTER_A_GAP_START);
        final int overlapEnd = CellEdges.getOverlapEnd(ITEM_END, AFTER_A_GAP_END);

        assertThat(CellEdges.isAnOverlap(overlapStart, overlapEnd)).isFalse();
    }

    @Test
    public void isInTheGap_forAnItemBetweenTheTwo_isTrue() {
        final boolean isInTheGap =
                CellEdges.isInTheGap(
                        ITEM_START,
                        ITEM_END,
                        FURTHER_START,
                        FURTHER_END,
                        AFTER_A_GAP_START,
                        AFTER_A_GAP_END);

        assertThat(isInTheGap).isTrue();
    }

    @Test
    public void isInTheGap_forTheNeighbourItself_isFalse() {
        final boolean isInTheGap =
                CellEdges.isInTheGap(
                        ITEM_START,
                        ITEM_END,
                        AFTER_A_GAP_START,
                        AFTER_A_GAP_END,
                        AFTER_A_GAP_START,
                        AFTER_A_GAP_END);

        assertThat(isInTheGap).isFalse();
    }

    @Test
    public void isInTheGap_forAnItemBeyondTheNeighbour_isFalse() {
        final boolean isInTheGap =
                CellEdges.isInTheGap(
                        ITEM_START,
                        ITEM_END,
                        AFTER_A_GAP_START,
                        AFTER_A_GAP_END,
                        FURTHER_START,
                        FURTHER_END);

        assertThat(isInTheGap).isFalse();
    }

    @Test
    public void isInTheGap_forAnItemBeforeTheItem_isFalse() {
        final boolean isInTheGap =
                CellEdges.isInTheGap(
                        ITEM_START,
                        ITEM_END,
                        AFTER_A_GAP_START,
                        AFTER_A_GAP_END,
                        BEFORE_START,
                        BEFORE_END);

        assertThat(isInTheGap).isFalse();
    }

    @Test
    public void reachesTheBand_forACandidateInsideTheBand_isTrue() {
        final boolean reaches =
                CellEdges.reachesTheBand(BAND_START, BAND_END, NESTED_START, NESTED_END);

        assertThat(reaches).isTrue();
    }

    @Test
    public void reachesTheBand_forACandidateOnlyTouchingTheBand_isFalse() {
        final boolean reaches =
                CellEdges.reachesTheBand(BAND_START, BAND_END, BAND_END, FURTHER_END);

        assertThat(reaches).isFalse();
    }

    @Test
    public void reachesTheBand_forACandidateOutsideTheBand_isFalse() {
        final boolean reaches =
                CellEdges.reachesTheBand(BAND_START, BAND_END, FURTHER_START, FURTHER_END);

        assertThat(reaches).isFalse();
    }
}
