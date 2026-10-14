// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.widget.adapterview;

import static mobi.parchment.widget.adapterview.PatternFixture.ADAPTER_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
import mobi.parchment.test.R;
import mobi.parchment.widget.adapterview.PatternFixture.LaidOutChild;
import mobi.parchment.widget.adapterview.PatternFixture.LaidOutPattern;
import mobi.parchment.widget.adapterview.PatternFixture.ResizableAdapter;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLooper;

public final class PatternViewJourney {

    private static final int PATTERN_BREADTH = 400;
    private static final int PATTERN_SIZE = 300;
    private static final int OFF_SCREEN_SELECTION = 14;
    private static final int SCROLL_BACK_TARGET = 9;
    private static final int SHRUNK_ADAPTER_SIZE = 3;
    private static final int LAST_SHRUNK_POSITION = SHRUNK_ADAPTER_SIZE - 1;
    private static final int GROWN_ADAPTER_SIZE = 30;
    private static final int NO_CHILDREN = 0;
    private static final int NOTHING_SELECTED = AdapterView.INVALID_POSITION;
    private static final int ORIGIN = 0;
    private static final int REST_TASK_LIMIT = 500;
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    public static final AttachedLayout VERTICAL_PATTERN =
            new AttachedLayout(
                    R.layout.start_snap_grid_pattern_vertical,
                    VERTICAL,
                    PATTERN_BREADTH,
                    PATTERN_SIZE);
    public static final AttachedLayout HORIZONTAL_PATTERN =
            new AttachedLayout(
                    R.layout.start_snap_grid_pattern_horizontal,
                    HORIZONTAL,
                    PATTERN_SIZE,
                    PATTERN_BREADTH);

    private PatternViewJourney() {}

    public static void assertRunsLikeTheOracle(
            final AttachedLayout layout,
            final ViewDefinitions definitions,
            final ViewDefinitions oracleDefinitions) {
        final PatternView oracle = PatternView.laidOut(layout, oracleDefinitions);
        final PatternView pattern = PatternView.laidOut(layout, definitions);

        assertLaysOutLikeTheOracle(pattern, oracle);
        assertSelectsOffScreenLikeTheOracle(pattern, oracle);
        assertScrollsLikeTheOracle(pattern, oracle);
        assertShrinksLikeTheOracle(pattern, oracle);
        assertGrowsLikeTheOracle(pattern, oracle);
    }

    private static void assertLaysOutLikeTheOracle(
            final PatternView pattern, final PatternView oracle) {
        final LaidOutPattern oracleLayout = oracle.snapshot();
        assertThat(oracleLayout.childCount()).isGreaterThan(NO_CHILDREN);
        assertThat(pattern.snapshot()).isEqualTo(oracleLayout);
    }

    private static void assertSelectsOffScreenLikeTheOracle(
            final PatternView pattern, final PatternView oracle) {
        final LaidOutPattern oracleBefore = oracle.snapshot();
        assertThat(oracleBefore.isLaidOut(OFF_SCREEN_SELECTION)).isFalse();

        oracle.setSelection(OFF_SCREEN_SELECTION);
        pattern.setSelection(OFF_SCREEN_SELECTION);

        final LaidOutPattern oracleLayout = oracle.snapshot();
        assertThat(oracleLayout.isLaidOut(OFF_SCREEN_SELECTION)).isTrue();
        assertThat(pattern.snapshot()).isEqualTo(oracleLayout);
    }

    private static void assertScrollsLikeTheOracle(
            final PatternView pattern, final PatternView oracle) {
        final LaidOutPattern oracleBefore = oracle.snapshot();
        assertThat(oracleBefore.isLaidOut(SCROLL_BACK_TARGET)).isFalse();

        oracle.smoothScrollToPosition(SCROLL_BACK_TARGET);
        pattern.smoothScrollToPosition(SCROLL_BACK_TARGET);

        final LaidOutPattern oracleLayout = oracle.snapshot();
        assertThat(oracleLayout.isLaidOut(SCROLL_BACK_TARGET)).isTrue();
        assertThat(pattern.snapshot()).isEqualTo(oracleLayout);
    }

    private static void assertShrinksLikeTheOracle(
            final PatternView pattern, final PatternView oracle) {
        oracle.setAdapterSize(SHRUNK_ADAPTER_SIZE);
        pattern.setAdapterSize(SHRUNK_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = oracle.snapshot();
        assertThat(oracleLayout.isLaidOut(LAST_SHRUNK_POSITION)).isTrue();
        assertThat(pattern.snapshot()).isEqualTo(oracleLayout);
    }

    private static void assertGrowsLikeTheOracle(
            final PatternView pattern, final PatternView oracle) {
        final LaidOutPattern oracleBefore = oracle.snapshot();

        oracle.setAdapterSize(GROWN_ADAPTER_SIZE);
        pattern.setAdapterSize(GROWN_ADAPTER_SIZE);

        final LaidOutPattern oracleLayout = oracle.snapshot();
        assertThat(oracleLayout).isNotEqualTo(oracleBefore);
        assertThat(pattern.snapshot()).isEqualTo(oracleLayout);
    }

    public static final class AttachedLayout {
        private final int mLayoutResource;
        private final boolean mIsVertical;
        private final int mWidth;
        private final int mHeight;

        private AttachedLayout(
                final int layoutResource,
                final boolean isVertical,
                final int width,
                final int height) {
            mLayoutResource = layoutResource;
            mIsVertical = isVertical;
            mWidth = width;
            mHeight = height;
        }
    }

    private static final class PatternView {
        private final InflatedGridPatternView mView;
        private final ResizableAdapter mAdapter;
        private final RecordingItemSelectedListener mSelections;
        private final AttachedLayout mLayout;

        private PatternView(
                final InflatedGridPatternView view,
                final ResizableAdapter adapter,
                final RecordingItemSelectedListener selections,
                final AttachedLayout layout) {
            mView = view;
            mAdapter = adapter;
            mSelections = selections;
            mLayout = layout;
        }

        private static PatternView laidOut(
                final AttachedLayout layout, final ViewDefinitions definitions) {
            final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
            final FrameLayout content = new FrameLayout(activity);
            activity.setContentView(content);
            final InflatedGridPatternView view =
                    (InflatedGridPatternView) View.inflate(activity, layout.mLayoutResource, null);
            final ResizableAdapter adapter = new ResizableAdapter(activity, ADAPTER_SIZE);
            final RecordingItemSelectedListener selections = new RecordingItemSelectedListener();
            definitions.defineOn(view, layout.mIsVertical);
            view.setOnItemSelectedListener(selections);
            view.setAdapter(adapter);
            final FrameLayout.LayoutParams exactSize =
                    new FrameLayout.LayoutParams(layout.mWidth, layout.mHeight);
            content.addView(view, exactSize);
            final PatternView pattern = new PatternView(view, adapter, selections, layout);
            pattern.layOut();
            return pattern;
        }

        private void setSelection(final int position) {
            mView.setSelection(position);
            layOut();
        }

        private void smoothScrollToPosition(final int position) {
            mView.smoothScrollToPosition(position);
            runToRest();
        }

        private void setAdapterSize(final int adapterSize) {
            mAdapter.setCount(adapterSize);
            layOut();
        }

        private void layOut() {
            final int widthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(mLayout.mWidth, View.MeasureSpec.EXACTLY);
            final int heightMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(mLayout.mHeight, View.MeasureSpec.EXACTLY);
            mView.forceLayout();
            mView.measure(widthMeasureSpec, heightMeasureSpec);
            mView.layout(ORIGIN, ORIGIN, mLayout.mWidth, mLayout.mHeight);
            runToRest();
        }

        private void runToRest() {
            final Looper mainLooper = Looper.getMainLooper();
            final ShadowLooper shadowMainLooper = shadowOf(mainLooper);
            for (int task = 0; task < REST_TASK_LIMIT; task++) {
                if (shadowMainLooper.isIdle()) return;
                shadowMainLooper.runOneTask();
            }
            throw new AssertionError("the view never came to rest: " + snapshot());
        }

        private LaidOutPattern snapshot() {
            final List<LaidOutChild> children = new ArrayList<LaidOutChild>();
            final int childCount = mView.getChildCount();
            for (int index = 0; index < childCount; index++) {
                final View child = mView.getChildAt(index);
                final int position = mView.getPositionForView(child);
                final LaidOutChild laidOutChild =
                        new LaidOutChild(
                                position,
                                child.getLeft(),
                                child.getTop(),
                                child.getRight(),
                                child.getBottom());
                children.add(laidOutChild);
            }
            final int selectedPosition = mView.getSelectedItemPosition();
            final List<Integer> selections = mSelections.positions();
            final int firstVisiblePosition = mView.getFirstVisiblePosition();
            final int lastVisiblePosition = mView.getLastVisiblePosition();
            return new LaidOutPattern(
                    children,
                    selectedPosition,
                    selections,
                    firstVisiblePosition,
                    lastVisiblePosition);
        }
    }

    public interface ViewDefinitions {
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical);
    }

    public static final class AddNoGroup implements ViewDefinitions {
        @Override
        public void defineOn(final InflatedGridPatternView view, final boolean isVertical) {}
    }

    private static final class RecordingItemSelectedListener
            implements AdapterView.OnItemSelectedListener {
        private final List<Integer> mPositions = new ArrayList<Integer>();

        @Override
        public void onItemSelected(
                final AdapterView<?> parent, final View view, final int position, final long id) {
            mPositions.add(position);
        }

        @Override
        public void onNothingSelected(final AdapterView<?> parent) {
            mPositions.add(NOTHING_SELECTED);
        }

        private List<Integer> positions() {
            return new ArrayList<Integer>(mPositions);
        }
    }

    public static final class InflatedGridPatternView extends GridPatternView<BaseAdapter> {
        public InflatedGridPatternView(final Context context, final AttributeSet attributeSet) {
            super(context, attributeSet);
        }
    }
}
