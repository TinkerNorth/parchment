// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public final class SampleApplication extends Application {

    private static final int BYTES_PER_MEGABYTE = 1024 * 1024;

    // Picasso sizes its own cache at a seventh of the heap. Now that a photo is requested at the
    // size of the view, a GridPatternView cell is over 11MB and only three of them fit, so every
    // scroll back evicts. A third of the heap holds a screenful and its neighbours.
    private static final int HEAP_FRACTION = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final int memoryClassMegabytes = activityManager.getMemoryClass();
        final int cacheBytes = memoryClassMegabytes * BYTES_PER_MEGABYTE / HEAP_FRACTION;
        final LruCache memoryCache = new LruCache(cacheBytes);
        final Picasso picasso = new Picasso.Builder(this).memoryCache(memoryCache).build();
        Picasso.setSingletonInstance(picasso);
    }
}
