// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import mobi.parchment.R;
import mobi.parchment.widget.adapterview.Attributes;

public class GridAttributes extends Attributes {

    private final int mNumberOfViewsPerCell;
    private boolean mIsLeft;
    private boolean mIsRight;
    private boolean mIsTop;
    private boolean mIsBottom;

    private static class DefaultValues {
        private static final int NUMBER_OF_VIEWS_PER_CELL = 1;
        private static final int GRAVITY = Gravity.TOP;
    }

    public GridAttributes(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (attributeSet != null) {
            final TypedArray typedArray =
                    context.obtainStyledAttributes(attributeSet, R.styleable.GridView, 0, 0);

            try {
                final int numberOfColumns =
                        typedArray.getInteger(
                                R.styleable.GridView_parchment_numberOfViewsPerCell,
                                DefaultValues.NUMBER_OF_VIEWS_PER_CELL);
                mNumberOfViewsPerCell =
                        Math.max(numberOfColumns, DefaultValues.NUMBER_OF_VIEWS_PER_CELL);

                final int gravity =
                        typedArray.getInt(
                                R.styleable.GridView_parchment_gravity, DefaultValues.GRAVITY);
                setGravityValues(gravity);

            } finally {
                typedArray.recycle();
            }
        } else {
            setGravityValues(DefaultValues.GRAVITY);
            mNumberOfViewsPerCell = DefaultValues.NUMBER_OF_VIEWS_PER_CELL;
        }
    }

    private void setGravityValues(int gravity) {
        mIsLeft = (gravity & Gravity.LEFT) != 0x0;
        mIsRight = (gravity & Gravity.RIGHT) != 0x0 && !mIsLeft;
        mIsTop = (gravity & Gravity.TOP) != 0x0;
        mIsBottom = (gravity & Gravity.BOTTOM) != 0x0 && !mIsTop;
    }

    public int getNumberOfViewsPerCell() {
        return mNumberOfViewsPerCell;
    }

    public boolean isLeft() {
        return mIsLeft;
    }

    public boolean isRight() {
        return mIsRight;
    }

    public boolean isTop() {
        return mIsTop;
    }

    public boolean isBottom() {
        return mIsBottom;
    }
}
