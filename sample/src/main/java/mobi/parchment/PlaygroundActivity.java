// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import mobi.parchment.playground.PlaygroundOptions;
import mobi.parchment.playground.Preset;
import mobi.parchment.sample.R;

public final class PlaygroundActivity extends Activity {

    private static final String EXTRA_PRESET = "preset";

    private PlaygroundForm mForm;

    public static Intent intentFor(final Context context, final Preset preset) {
        final Intent intent = new Intent(context, PlaygroundActivity.class);
        intent.putExtra(EXTRA_PRESET, preset.name());
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        setTitle(R.string.playground_title);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mForm = new PlaygroundForm(this);
        final boolean isFirstCreation = savedInstanceState == null;
        if (isFirstCreation) {
            mForm.show(presetFromIntent().getOptions());
        }
        findViewById(R.id.playground_show).setOnClickListener(new ShowDemo(mForm));
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mForm.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (UpIsBack.handles(this, item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Preset presetFromIntent() {
        final String name = getIntent().getStringExtra(EXTRA_PRESET);
        return Preset.valueOf(name);
    }

    private static final class ShowDemo implements View.OnClickListener {

        private final PlaygroundForm mForm;

        private ShowDemo(final PlaygroundForm form) {
            mForm = form;
        }

        @Override
        public void onClick(final View view) {
            final Context context = view.getContext();
            final PlaygroundOptions options = mForm.read();
            context.startActivity(DemoActivity.intentFor(context, options));
        }
    }
}
