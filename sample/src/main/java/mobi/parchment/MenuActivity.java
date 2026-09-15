// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import mobi.parchment.playground.Preset;
import mobi.parchment.sample.R;

public final class MenuActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final ViewGroup items = findViewById(R.id.menu_items);
        final LayoutInflater inflater = getLayoutInflater();
        for (final Preset.Group group : Preset.Group.values()) {
            addGroup(inflater, items, group);
        }
    }

    private static void addGroup(
            final LayoutInflater inflater, final ViewGroup items, final Preset.Group group) {
        addHeading(inflater, items, group);
        for (final Preset preset : Preset.in(group)) {
            addButton(inflater, items, preset);
        }
    }

    private static void addHeading(
            final LayoutInflater inflater, final ViewGroup items, final Preset.Group group) {
        final TextView heading = (TextView) inflater.inflate(R.layout.menu_heading, items, false);
        heading.setText(group.getTitleResourceId());
        items.addView(heading);
    }

    private static void addButton(
            final LayoutInflater inflater, final ViewGroup items, final Preset preset) {
        final Button button = (Button) inflater.inflate(R.layout.menu_button, items, false);
        button.setText(preset.getTitleResourceId());
        button.setOnClickListener(new OpenPlayground(preset));
        items.addView(button);
    }

    private static final class OpenPlayground implements View.OnClickListener {

        private final Preset mPreset;

        private OpenPlayground(final Preset preset) {
            mPreset = preset;
        }

        @Override
        public void onClick(final View view) {
            final Context context = view.getContext();
            context.startActivity(PlaygroundActivity.intentFor(context, mPreset));
        }
    }
}
