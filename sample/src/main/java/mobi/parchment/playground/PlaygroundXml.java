// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.playground;

import android.content.res.Resources;
import android.util.TypedValue;
import java.util.Locale;
import mobi.parchment.sample.R;

public final class PlaygroundXml {

    private static final String INDENT = "    ";
    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String PARCHMENT_NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private static final String PARCHMENT_PREFIX = "parchment:parchment_";
    private static final String MATCH_PARENT = "match_parent";
    private static final String TRUE = "true";
    private static final String DP = "dp";
    private static final String COLOUR_FORMAT = "#%08x";
    private static final String CLOSING_TAG = " />";
    private static final String PATTERN_COMMENT_START =
            "<!-- The pattern groups are added in code; see PatternOption.";
    private static final String PATTERN_COMMENT_END = " -->";

    private PlaygroundXml() {}

    public static String of(final PlaygroundOptions options, final Resources resources) {
        final StringBuilder xml = new StringBuilder();
        appendOpeningTag(xml, options.getViewKind());
        appendSharedAttributes(xml, options, resources);
        appendGridAttributes(xml, options);
        appendGridPatternAttributes(xml, options, resources);
        appendClosingTag(xml, options);
        return xml.toString();
    }

    private static void appendOpeningTag(final StringBuilder xml, final ViewKind viewKind) {
        xml.append('<').append(viewKind.getXmlTag());
        attribute(xml, "xmlns:android", ANDROID_NAMESPACE);
        attribute(xml, "xmlns:parchment", PARCHMENT_NAMESPACE);
        attribute(xml, "android:layout_width", MATCH_PARENT);
        attribute(xml, "android:layout_height", MATCH_PARENT);
    }

    private static void appendSharedAttributes(
            final StringBuilder xml, final PlaygroundOptions options, final Resources resources) {
        final int cellSpacingDimension = options.getCellSpacing().getDimensionResourceId();
        parchment(xml, "orientation", options.getOrientation().name());
        parchment(xml, "cellSpacing", dp(resources, cellSpacingDimension));
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
            appendDividerAttributes(xml, resources);
        }
    }

    private static void appendDividerAttributes(
            final StringBuilder xml, final Resources resources) {
        parchment(xml, "divider", colour(resources, R.color.cell_divider));
        parchment(
                xml, "dividerSize", dp(resources, R.dimen.activity_sample_parchment_divider_size));
    }

    private static void appendGridAttributes(
            final StringBuilder xml, final PlaygroundOptions options) {
        final boolean isGridView = options.getViewKind() == ViewKind.gridView;
        if (isGridView) {
            parchment(xml, "numberOfViewsPerCell", options.getViewsPerCell().getXmlValue());
            parchment(xml, "gravity", options.getGravity().name());
        }
    }

    private static void appendGridPatternAttributes(
            final StringBuilder xml, final PlaygroundOptions options, final Resources resources) {
        final boolean isGridPatternView = options.getViewKind() == ViewKind.gridPatternView;
        if (isGridPatternView) {
            parchment(xml, "ratio", ratio(resources, options.getRatio().getFloatResourceId()));
        }
    }

    private static void appendClosingTag(final StringBuilder xml, final PlaygroundOptions options) {
        xml.append(CLOSING_TAG);
        final boolean isGridPatternView = options.getViewKind() == ViewKind.gridPatternView;
        if (isGridPatternView) {
            xml.append('\n')
                    .append(PATTERN_COMMENT_START)
                    .append(options.getPattern().name())
                    .append(PATTERN_COMMENT_END);
        }
    }

    private static void flag(final StringBuilder xml, final String name, final boolean isOn) {
        if (isOn) {
            parchment(xml, name, TRUE);
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
        return dp + DP;
    }

    private static String ratio(final Resources resources, final int floatResourceId) {
        final TypedValue value = new TypedValue();
        resources.getValue(floatResourceId, value, true);
        return Float.toString(value.getFloat());
    }

    private static String colour(final Resources resources, final int colourResourceId) {
        final TypedValue value = new TypedValue();
        resources.getValue(colourResourceId, value, true);
        return String.format(Locale.ROOT, COLOUR_FORMAT, value.data);
    }
}
