// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview.gridpatternview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import mobi.parchment.R;
import mobi.parchment.widget.adapterview.Attributes;

public class GridPatternAttributes extends Attributes {

    private final float mRatio;

    private static class DefaultValues {
        private static final float RATIO = 1.0f;
    }

    public GridPatternAttributes(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (attributeSet != null) {
            final TypedArray typedArray =
                    context.obtainStyledAttributes(attributeSet, R.styleable.GridPatternView, 0, 0);

            try {

                final float ratio =
                        typedArray.getFloat(
                                R.styleable.GridPatternView_parchment_ratio, DefaultValues.RATIO);
                mRatio = ratio;
            } finally {
                typedArray.recycle();
            }
        } else {
            mRatio = DefaultValues.RATIO;
        }
    }

    public float getRatio() {
        return mRatio;
    }
}
