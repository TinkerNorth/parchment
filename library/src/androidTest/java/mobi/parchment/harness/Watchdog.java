// SPDX-License-Identifier: Apache-2.0
// Copyright (C) 2014 Emir Hasanbegovic and Parchment contributors.

package mobi.parchment.harness;

import android.os.Looper;
import android.os.SystemClock;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Watchdog implements Runnable {
    private final String mWatched;
    private final long mTimeLimitMilliseconds;
    private final AtomicBoolean mIsStoodDown = new AtomicBoolean();

    private Watchdog(final String watched, final long timeLimitMilliseconds) {
        mWatched = watched;
        mTimeLimitMilliseconds = timeLimitMilliseconds;
    }

    public static Watchdog watchFor(final String watched, final long timeLimitMilliseconds) {
        final Watchdog watchdog = new Watchdog(watched, timeLimitMilliseconds);
        final Thread watching = new Thread(watchdog);
        watching.setDaemon(true);
        watching.start();
        return watchdog;
    }

    public void standDown() {
        mIsStoodDown.set(true);
    }

    @Override
    public void run() {
        SystemClock.sleep(mTimeLimitMilliseconds);
        final boolean isStoodDown = mIsStoodDown.get();
        if (isStoodDown) return;
        final Looper mainLooper = Looper.getMainLooper();
        final Thread mainThread = mainLooper.getThread();
        final AssertionError stillRunning =
                new AssertionError(
                        mWatched + ", was still running after " + mTimeLimitMilliseconds + " ms");
        stillRunning.setStackTrace(mainThread.getStackTrace());
        // A hung main thread blocks the scenario's teardown; only an uncaught throw reports.
        throw stillRunning;
    }
}
