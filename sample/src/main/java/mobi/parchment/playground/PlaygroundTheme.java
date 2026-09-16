// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import mobi.parchment.sample.R;

public final class PlaygroundTheme {

    private static final boolean OVERRIDE_EARLIER_VALUES = true;

    private PlaygroundTheme() {}

    public static Context contextFor(final Context context, final PlaygroundOptions options) {
        final ContextThemeWrapper themedContext =
                new ContextThemeWrapper(context, R.style.Playground);
        final Resources.Theme theme = themedContext.getTheme();
        apply(theme, options.getOrientation().getStyleResourceId());
        apply(theme, options.getCellSpacing().getStyleResourceId());
        apply(theme, options.getSnapPosition().getStyleResourceId());
        apply(theme, options.getViewPagerInterval().getStyleResourceId());
        apply(theme, options.getViewsPerCell().getStyleResourceId());
        apply(theme, options.getGravity().getStyleResourceId());
        apply(theme, options.getRatio().getStyleResourceId());
        applyIf(theme, options.isSnapToPosition(), R.style.Playground_SnapToPosition);
        applyIf(theme, options.isCircularScroll(), R.style.Playground_CircularScroll);
        applyIf(theme, options.isViewPager(), R.style.Playground_ViewPager);
        applyIf(theme, options.selectOnSnap(), R.style.Playground_SelectOnSnap);
        applyIf(theme, options.selectWhileScrolling(), R.style.Playground_SelectWhileScrolling);
        applyIf(theme, options.scrollWithinContent(), R.style.Playground_ScrollWithinContent);
        applyIf(theme, options.hasDivider(), R.style.Playground_Divider);
        return themedContext;
    }

    private static void apply(final Resources.Theme theme, final int styleResourceId) {
        theme.applyStyle(styleResourceId, OVERRIDE_EARLIER_VALUES);
    }

    private static void applyIf(
            final Resources.Theme theme, final boolean isOn, final int styleResourceId) {
        if (isOn) {
            apply(theme, styleResourceId);
        }
    }
}
