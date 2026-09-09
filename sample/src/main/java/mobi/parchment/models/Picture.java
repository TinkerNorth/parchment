// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.models;

public final class Picture {
    public final String mSlug;
    public final String mCaption;

    public Picture(final String slug, final String caption) {
        mSlug = slug;
        mCaption = caption;
    }
}
