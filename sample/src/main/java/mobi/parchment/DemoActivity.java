// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import mobi.parchment.playground.Cell;
import mobi.parchment.playground.PatternOption;
import mobi.parchment.playground.PlaygroundOptions;
import mobi.parchment.playground.PlaygroundTheme;
import mobi.parchment.playground.PlaygroundXml;
import mobi.parchment.playground.ViewKind;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import mobi.parchment.widget.adapterview.gridpatternview.GridPatternView;

public final class DemoActivity extends Activity {

    private static final String EXTRA_OPTIONS = "options";
    private static final String STATE_IS_XML_SHOWN = "isXmlShown";
    private static final String STATE_IS_SCROLL_TO_SHOWN = "isScrollToShown";
    private static final String STATE_SCROLL_TO_POSITION = "scrollToPosition";
    private static final boolean ATTACH_TO_CONTAINER = true;
    private static final int NO_POSITION = -1;

    private Dialog mXmlDialog;
    private Dialog mScrollToDialog;
    private EditText mScrollToPositionView;

    public static Intent intentFor(final Context context, final PlaygroundOptions options) {
        final Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra(EXTRA_OPTIONS, options.toBundle());
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        final PlaygroundOptions options = optionsFromIntent();
        final ViewKind viewKind = options.getViewKind();
        setTitle(viewKind.getTitleResourceId());
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final AbstractAdapterView<BaseAdapter, ?> view = inflate(options);
        report(view);
        final ProductsAdapter adapter = createAdapter(viewKind.getCell(options.getOrientation()));
        view.setAdapter(adapter);

        mXmlDialog = createXmlDialog(options);
        findViewById(R.id.demo_show_xml).setOnClickListener(new ShowDialog(mXmlDialog));
        if (wasShown(savedInstanceState, STATE_IS_XML_SHOWN)) {
            mXmlDialog.show();
        }

        final ViewGroup contentRoot = findViewById(android.R.id.content);
        final View scrollToContent =
                getLayoutInflater().inflate(R.layout.dialog_scroll_to, contentRoot, false);
        mScrollToPositionView = scrollToContent.findViewById(R.id.demo_scroll_to_position);
        mScrollToDialog = createScrollToDialog(view, scrollToContent, mScrollToPositionView);
        final int lastPosition = adapter.getCount() - 1;
        final String offeredPosition = savedScrollToPosition(savedInstanceState, lastPosition);
        mScrollToPositionView.setText(offeredPosition);
        if (wasShown(savedInstanceState, STATE_IS_SCROLL_TO_SHOWN)) {
            mScrollToDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_XML_SHOWN, mXmlDialog.isShowing());
        outState.putBoolean(STATE_IS_SCROLL_TO_SHOWN, mScrollToDialog.isShowing());
        outState.putString(STATE_SCROLL_TO_POSITION, offeredScrollToPosition());
    }

    @Override
    protected void onDestroy() {
        mXmlDialog.dismiss();
        mScrollToDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (UpIsBack.handles(this, item)) {
            return true;
        }
        final boolean isScrollTo = item.getItemId() == R.id.demo_scroll_to;
        if (isScrollTo) {
            mScrollToDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private PlaygroundOptions optionsFromIntent() {
        final Bundle bundle = getIntent().getBundleExtra(EXTRA_OPTIONS);
        return PlaygroundOptions.fromBundle(bundle);
    }

    private AbstractAdapterView<BaseAdapter, ?> inflate(final PlaygroundOptions options) {
        final ViewGroup container = findViewById(R.id.demo_container);
        final Context themedContext = PlaygroundTheme.contextFor(this, options);
        final LayoutInflater inflater = LayoutInflater.from(themedContext);
        final ViewKind viewKind = options.getViewKind();
        inflater.inflate(viewKind.getLayoutResourceId(), container, ATTACH_TO_CONTAINER);
        if (viewKind == ViewKind.gridPatternView) {
            addPattern(container, options.getPattern());
        }
        return container.findViewById(R.id.parchment_view);
    }

    private static void addPattern(final ViewGroup container, final PatternOption pattern) {
        final GridPatternView<BaseAdapter> gridPatternView =
                container.findViewById(R.id.parchment_view);
        pattern.addTo(gridPatternView);
    }

    private void report(final AbstractAdapterView<BaseAdapter, ?> view) {
        final TextView statusView = findViewById(R.id.demo_status);
        final DemoStatus status = new DemoStatus(statusView);
        view.setOnScrollListener(status);
        view.setOnItemSelectedListener(status);
        view.setOnItemClickListener(status);
    }

    private ProductsAdapter createAdapter(final Cell cell) {
        final Resources resources = getResources();
        final int widthPixels = cell.getRequestWidthPixels(resources);
        final int heightPixels = cell.getRequestHeightPixels(resources);
        return new ProductsAdapter(cell.getLayoutResourceId(), widthPixels, heightPixels);
    }

    private Dialog createXmlDialog(final PlaygroundOptions options) {
        final String xml = PlaygroundXml.of(options, getResources());
        final CharSequence title = getTitle();
        final View content = getLayoutInflater().inflate(R.layout.dialog_xml, null);
        final TextView xmlView = content.findViewById(R.id.demo_xml);
        xmlView.setText(xml);
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(content)
                .setPositiveButton(R.string.demo_copy, new CopyXml(this, title, xml))
                .setNegativeButton(R.string.demo_close, null)
                .create();
    }

    private Dialog createScrollToDialog(
            final AbstractAdapterView<BaseAdapter, ?> view,
            final View content,
            final EditText positionView) {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.demo_scroll_to)
                .setView(content)
                .setPositiveButton(R.string.demo_scroll, new ScrollTo(view, positionView))
                .setNegativeButton(R.string.demo_close, null)
                .create();
    }

    private String offeredScrollToPosition() {
        final CharSequence positionText = mScrollToPositionView.getText();
        return positionText.toString();
    }

    private static String savedScrollToPosition(
            final Bundle savedInstanceState, final int lastPosition) {
        final String lastPositionText = Integer.toString(lastPosition);
        if (savedInstanceState == null) {
            return lastPositionText;
        }
        return savedInstanceState.getString(STATE_SCROLL_TO_POSITION, lastPositionText);
    }

    private static boolean wasShown(final Bundle savedInstanceState, final String key) {
        if (savedInstanceState == null) {
            return false;
        }
        return savedInstanceState.getBoolean(key);
    }

    private static final class ShowDialog implements View.OnClickListener {

        private final Dialog mDialog;

        private ShowDialog(final Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public void onClick(final View view) {
            mDialog.show();
        }
    }

    private static final class CopyXml implements DialogInterface.OnClickListener {

        private final Context mContext;
        private final CharSequence mLabel;
        private final String mXml;

        private CopyXml(final Context context, final CharSequence label, final String xml) {
            mContext = context;
            mLabel = label;
            mXml = xml;
        }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            final ClipboardManager clipboard =
                    (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newPlainText(mLabel, mXml);
            clipboard.setPrimaryClip(clip);
            if (!systemConfirmsCopy()) {
                confirm();
            }
        }

        private static boolean systemConfirmsCopy() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
        }

        private void confirm() {
            Toast.makeText(mContext, R.string.demo_copied, Toast.LENGTH_SHORT).show();
        }
    }

    private static final class ScrollTo implements DialogInterface.OnClickListener {

        private final AbstractAdapterView<BaseAdapter, ?> mView;
        private final EditText mPositionView;

        private ScrollTo(
                final AbstractAdapterView<BaseAdapter, ?> view, final EditText positionView) {
            mView = view;
            mPositionView = positionView;
        }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            final CharSequence positionText = mPositionView.getText();
            final int position = parsePosition(positionText);
            final boolean isAPosition = position != NO_POSITION;
            if (isAPosition) {
                mView.smoothScrollToPosition(position);
            }
        }

        private static int parsePosition(final CharSequence positionText) {
            final String trimmed = positionText.toString().trim();
            try {
                return Integer.parseInt(trimmed);
            } catch (final NumberFormatException notAPosition) {
                return NO_POSITION;
            }
        }
    }
}
