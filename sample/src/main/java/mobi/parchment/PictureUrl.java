// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

public final class PictureUrl {

    private static final String PHOTO_PREFIX = "https://images.unsplash.com/photo-";
    private static final int QUALITY = 75;

    private PictureUrl() {}

    public static String forSize(final String slug, final int widthPixels, final int heightPixels) {
        final String size = "?w=" + widthPixels + "&h=" + heightPixels;
        final String crop = "&fit=crop";
        final String quality = "&q=" + QUALITY;
        return PHOTO_PREFIX + slug + size + crop + quality;
    }
}
