// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
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
    private static final boolean ATTACH_TO_CONTAINER = true;

    private View mXmlPanel;

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
        view.setAdapter(createAdapter(viewKind.getCell(options.getOrientation())));

        mXmlPanel = findViewById(R.id.demo_xml_panel);
        showXml(options, wasXmlShown(savedInstanceState));
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_XML_SHOWN, isXmlShown());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (UpIsBack.handles(this, item)) {
            return true;
        }
        if (item.getItemId() == R.id.demo_show_xml) {
            setXmlShown(!isXmlShown());
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

    private void showXml(final PlaygroundOptions options, final boolean isShown) {
        final TextView xmlView = findViewById(R.id.demo_xml);
        xmlView.setText(PlaygroundXml.of(options, getResources()));
        setXmlShown(isShown);
    }

    private static boolean wasXmlShown(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }
        return savedInstanceState.getBoolean(STATE_IS_XML_SHOWN);
    }

    private boolean isXmlShown() {
        return mXmlPanel.getVisibility() == View.VISIBLE;
    }

    private void setXmlShown(final boolean isShown) {
        if (isShown) {
            mXmlPanel.setVisibility(View.VISIBLE);
        } else {
            mXmlPanel.setVisibility(View.GONE);
        }
    }
}
