// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import androidx.test.core.app.ApplicationProvider;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import mobi.parchment.playground.Preset;
import mobi.parchment.sample.R;
import mobi.parchment.widget.adapterview.AbstractAdapterView;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;

// SampleApplication installs a Picasso singleton, which a JVM accepts once.
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class)
public class DemoScrollToTest {

    private static final int FIRST_POSITION = 0;
    private static final int A_LATER_POSITION = 5;
    private static final int NOT_DRAWN = Integer.MIN_VALUE;
    private static final String NO_POSITION = "";

    @Test
    public void theScrollToAction_opensADialogOfferingTheLastPosition() {
        final DemoActivity activity = demo(Preset.listView);

        openScrollTo(activity);

        final Dialog dialog = ShadowDialog.getLatestDialog();
        assertThat(dialog.isShowing()).isTrue();
        assertThat(offeredPosition(dialog)).isEqualTo(Integer.toString(lastPosition(activity)));
    }

    @Test
    public void scrollingToAPosition_bringsThatCellToTheSnapPosition() {
        final DemoActivity activity = demo(Preset.listView);
        final AbstractAdapterView<BaseAdapter, ?> view = parchmentView(activity);
        assertThat(startOf(view, A_LATER_POSITION)).isEqualTo(NOT_DRAWN);

        openScrollTo(activity);
        scrollTo(ShadowDialog.getLatestDialog(), Integer.toString(A_LATER_POSITION));
        idleMainLooper();

        assertThat(startOf(view, A_LATER_POSITION)).isEqualTo(view.getPaddingLeft());
    }

    @Test
    public void theScrollToDialog_offersThePositionEnteredLastTime() {
        final DemoActivity activity = demo(Preset.listView);
        openScrollTo(activity);
        scrollTo(ShadowDialog.getLatestDialog(), Integer.toString(A_LATER_POSITION));
        idleMainLooper();

        openScrollTo(activity);

        assertThat(offeredPosition(ShadowDialog.getLatestDialog()))
                .isEqualTo(Integer.toString(A_LATER_POSITION));
    }

    @Test
    public void scrollingToNothing_leavesTheViewWhereItIs() {
        final DemoActivity activity = demo(Preset.listView);
        final AbstractAdapterView<BaseAdapter, ?> view = parchmentView(activity);
        final int firstCellStartBefore = startOf(view, FIRST_POSITION);

        openScrollTo(activity);
        scrollTo(ShadowDialog.getLatestDialog(), NO_POSITION);
        idleMainLooper();

        assertThat(startOf(view, FIRST_POSITION)).isEqualTo(firstCellStartBefore);
    }

    @Test
    public void aRecreatedDemo_stillOffersThePositionEntered() {
        final ActivityController<DemoActivity> controller = controller(Preset.listView);
        openScrollTo(controller.get());
        scrollTo(ShadowDialog.getLatestDialog(), Integer.toString(A_LATER_POSITION));
        idleMainLooper();

        controller.recreate();
        openScrollTo(controller.get());

        assertThat(offeredPosition(ShadowDialog.getLatestDialog()))
                .isEqualTo(Integer.toString(A_LATER_POSITION));
    }

    @Test
    public void aRecreatedDemo_reopensTheScrollToDialogItWasShowing() {
        final ActivityController<DemoActivity> controller = controller(Preset.listView);
        openScrollTo(controller.get());

        controller.recreate();

        final Dialog dialog = ShadowDialog.getLatestDialog();
        assertThat(dialog.isShowing()).isTrue();
        assertThat(offeredPosition(dialog))
                .isEqualTo(Integer.toString(lastPosition(controller.get())));
    }

    private static DemoActivity demo(final Preset preset) {
        return controller(preset).get();
    }

    private static ActivityController<DemoActivity> controller(final Preset preset) {
        final Context context = ApplicationProvider.getApplicationContext();
        installAPicassoThatLoadsNothing(context);
        final Intent intent = DemoActivity.intentFor(context, preset.getOptions());
        return Robolectric.buildActivity(DemoActivity.class, intent).setup();
    }

    private static void openScrollTo(final DemoActivity activity) {
        shadowOf(activity).clickMenuItem(R.id.demo_scroll_to);
    }

    private static void scrollTo(final Dialog dialog, final String position) {
        final EditText positionView = dialog.findViewById(R.id.demo_scroll_to_position);
        positionView.setText(position);
        final AlertDialog alertDialog = (AlertDialog) dialog;
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
    }

    private static String offeredPosition(final Dialog dialog) {
        final EditText positionView = dialog.findViewById(R.id.demo_scroll_to_position);
        return positionView.getText().toString();
    }

    private static AbstractAdapterView<BaseAdapter, ?> parchmentView(final DemoActivity activity) {
        return activity.findViewById(R.id.parchment_view);
    }

    private static int lastPosition(final DemoActivity activity) {
        final AbstractAdapterView<BaseAdapter, ?> view = parchmentView(activity);
        return view.getAdapter().getCount() - 1;
    }

    private static int startOf(final AbstractAdapterView<BaseAdapter, ?> view, final int position) {
        for (int index = 0; index < view.getChildCount(); index++) {
            final View child = view.getChildAt(index);
            final boolean isThePosition = view.getPositionForView(child) == position;
            if (isThePosition) return child.getLeft();
        }
        return NOT_DRAWN;
    }

    private static void idleMainLooper() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    // The demo adapter asks Picasso for every photo, and a JVM accepts one singleton: the first
    // demo built here installs one that never reaches the network, and every later one keeps it.
    private static void installAPicassoThatLoadsNothing(final Context context) {
        try {
            final Picasso picasso =
                    new Picasso.Builder(context).downloader(new NoDownloads()).build();
            Picasso.setSingletonInstance(picasso);
        } catch (final IllegalStateException alreadyInstalled) {
            // The first demo of this JVM installed it.
        }
    }

    private static final class NoDownloads implements Downloader {
        @Override
        public Response load(final Request request) throws IOException {
            throw new IOException("unit tests load no photos");
        }

        @Override
        public void shutdown() {}
    }
}
