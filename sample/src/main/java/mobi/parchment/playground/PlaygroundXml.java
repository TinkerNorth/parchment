// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.content.res.Resources;
import android.util.TypedValue;
import java.util.Locale;
import mobi.parchment.sample.R;

/**
 * The layout XML that reproduces a demo without the playground: the view's tag with a literal for
 * every attribute the options set, and none for the ones they leave at Parchment's default.
 */
public final class PlaygroundXml {

    private static final String INDENT = "    ";
    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String PARCHMENT_NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private static final String PARCHMENT_PREFIX = "parchment:parchment_";
    private static final String PATTERN_COMMENT =
            "<!-- The pattern groups are added in code; see PatternOption.";

    private PlaygroundXml() {}

    public static String of(final PlaygroundOptions options, final Resources resources) {
        final ViewKind viewKind = options.getViewKind();
        final StringBuilder xml = new StringBuilder();
        xml.append('<').append(viewKind.getXmlTag());
        attribute(xml, "xmlns:android", ANDROID_NAMESPACE);
        attribute(xml, "xmlns:parchment", PARCHMENT_NAMESPACE);
        attribute(xml, "android:layout_width", "match_parent");
        attribute(xml, "android:layout_height", "match_parent");
        parchment(xml, "orientation", options.getOrientation().name());
        parchment(
                xml,
                "cellSpacing",
                dp(resources, options.getCellSpacing().getDimensionResourceId()));
        parchment(xml, "snapPosition", options.getSnapPosition().name());
        flag(xml, "snapToPosition", options.isSnapToPosition());
        flag(xml, "isCircularScroll", options.isCircularScroll());
        flag(xml, "isViewPager", options.isViewPager());
        if (options.isViewPager()) {
            parchment(xml, "viewPagerInterval", options.getViewPagerInterval().getXmlValue());
        }
        flag(xml, "selectOnSnap", options.selectOnSnap());
        flag(xml, "selectWhileScrolling", options.selectWhileScrolling());
        if (options.hasDivider()) {
            parchment(xml, "divider", color(resources, R.color.cell_divider));
            parchment(
                    xml,
                    "dividerSize",
                    dp(resources, R.dimen.activity_sample_parchment_divider_size));
        }
        if (viewKind == ViewKind.gridView) {
            parchment(xml, "numberOfViewsPerCell", options.getViewsPerCell().getXmlValue());
            parchment(xml, "gravity", options.getGravity().name());
        }
        if (viewKind == ViewKind.gridPatternView) {
            parchment(xml, "ratio", ratio(resources, options.getRatio().getFloatResourceId()));
        }
        xml.append(" />");
        if (viewKind == ViewKind.gridPatternView) {
            xml.append('\n')
                    .append(PATTERN_COMMENT)
                    .append(options.getPattern().name())
                    .append(" -->");
        }
        return xml.toString();
    }

    private static void flag(final StringBuilder xml, final String name, final boolean isOn) {
        if (isOn) {
            parchment(xml, name, "true");
        }
    }

    private static void parchment(final StringBuilder xml, final String name, final String value) {
        attribute(xml, PARCHMENT_PREFIX + name, value);
    }

    private static void attribute(final StringBuilder xml, final String name, final String value) {
        xml.append('\n').append(INDENT).append(name).append("=\"").append(value).append('"');
    }

    private static String dp(final Resources resources, final int dimensionResourceId) {
        final float pixels = resources.getDimension(dimensionResourceId);
        final float density = resources.getDisplayMetrics().density;
        final int dp = Math.round(pixels / density);
        return dp + "dp";
    }

    private static String ratio(final Resources resources, final int floatResourceId) {
        final TypedValue value = new TypedValue();
        resources.getValue(floatResourceId, value, true);
        return Float.toString(value.getFloat());
    }

    private static String color(final Resources resources, final int colorResourceId) {
        final TypedValue value = new TypedValue();
        resources.getValue(colorResourceId, value, true);
        return String.format(Locale.ROOT, "#%08x", value.data);
    }
}
