// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

public enum ScrollState {
    idle,
    dragging,
    settling;

    public static ScrollState from(final AdapterAnimator.State state) {
        switch (state) {
            case scrolling:
                return dragging;
            case flinging:
            case snapingTo:
            case animatingTo:
            case jumpingTo:
            case seekingTo:
                return settling;
            case notMoving:
            default:
                return idle;
        }
    }
}
