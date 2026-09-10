// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;

public final class RecordingDrawable extends Drawable {

    public static final int NO_INTRINSIC_SIZE = -1;

    private final List<Rect> mDrawnBounds = new ArrayList<Rect>();
    private final List<Rect> mBoundsInstances = new ArrayList<Rect>();
    private final int mIntrinsicWidth;
    private final int mIntrinsicHeight;

    public RecordingDrawable() {
        this(NO_INTRINSIC_SIZE, NO_INTRINSIC_SIZE);
    }

    public RecordingDrawable(final int intrinsicWidth, final int intrinsicHeight) {
        mIntrinsicWidth = intrinsicWidth;
        mIntrinsicHeight = intrinsicHeight;
    }

    @Override
    public void setBounds(final Rect bounds) {
        mBoundsInstances.add(bounds);
        super.setBounds(bounds);
    }

    @Override
    public void draw(final Canvas canvas) {
        final Rect bounds = getBounds();
        mDrawnBounds.add(new Rect(bounds));
    }

    @Override
    public void setAlpha(final int alpha) {}

    @Override
    public void setColorFilter(final ColorFilter colorFilter) {}

    @Deprecated
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    public int getDrawCount() {
        return mDrawnBounds.size();
    }

    public Rect getDrawnBounds(final int index) {
        return mDrawnBounds.get(index);
    }

    public List<Rect> getBoundsInstances() {
        return mBoundsInstances;
    }
}
