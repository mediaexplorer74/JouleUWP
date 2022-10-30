package org.chromium.base;

import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

@JNINamespace("base::android")
public class TraceEvent {
    private static volatile boolean sATraceEnabled;
    private static volatile boolean sEnabled;

    private static class BasicLooperMonitor implements Printer {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !TraceEvent.class.desiredAssertionStatus();
        }

        private BasicLooperMonitor() {
        }

        public void println(String line) {
            if (line.startsWith(">")) {
                beginHandling(line);
            } else if ($assertionsDisabled || line.startsWith("<")) {
                endHandling(line);
            } else {
                throw new AssertionError();
            }
        }

        void beginHandling(String line) {
            if (TraceEvent.sEnabled) {
                TraceEvent.nativeBeginToplevel();
            }
        }

        void endHandling(String line) {
            if (TraceEvent.sEnabled) {
                TraceEvent.nativeEndToplevel();
            }
        }
    }

    private static final class LooperMonitorHolder {
        private static final BasicLooperMonitor sInstance;

        private LooperMonitorHolder() {
        }

        static {
            sInstance = CommandLine.getInstance().hasSwitch(BaseSwitches.ENABLE_IDLE_TRACING) ? new IdleTracingLooperMonitor() : new BasicLooperMonitor();
        }
    }

    private static final class IdleTracingLooperMonitor extends BasicLooperMonitor implements IdleHandler {
        private static final long FRAME_DURATION_MILLIS = 16;
        private static final String IDLE_EVENT_NAME = "Looper.queueIdle";
        private static final long MIN_INTERESTING_BURST_DURATION_MILLIS = 48;
        private static final long MIN_INTERESTING_DURATION_MILLIS = 16;
        private static final String TAG = "TraceEvent.LooperMonitor";
        private boolean mIdleMonitorAttached;
        private long mLastIdleStartedAt;
        private long mLastWorkStartedAt;
        private int mNumIdlesSeen;
        private int mNumTasksSeen;
        private int mNumTasksSinceLastIdle;

        private IdleTracingLooperMonitor() {
            super();
            this.mLastIdleStartedAt = 0;
            this.mLastWorkStartedAt = 0;
            this.mNumTasksSeen = 0;
            this.mNumIdlesSeen = 0;
            this.mNumTasksSinceLastIdle = 0;
            this.mIdleMonitorAttached = false;
        }

        private final void syncIdleMonitoring() {
            if (TraceEvent.sEnabled && !this.mIdleMonitorAttached) {
                this.mLastIdleStartedAt = SystemClock.elapsedRealtime();
                Looper.myQueue().addIdleHandler(this);
                this.mIdleMonitorAttached = true;
                Log.v(TAG, "attached idle handler");
            } else if (this.mIdleMonitorAttached && !TraceEvent.sEnabled) {
                Looper.myQueue().removeIdleHandler(this);
                this.mIdleMonitorAttached = false;
                Log.v(TAG, "detached idle handler");
            }
        }

        final void beginHandling(String line) {
            if (this.mNumTasksSinceLastIdle == 0) {
                TraceEvent.end(IDLE_EVENT_NAME);
            }
            this.mLastWorkStartedAt = SystemClock.elapsedRealtime();
            syncIdleMonitoring();
            super.beginHandling(line);
        }

        final void endHandling(String line) {
            long elapsed = SystemClock.elapsedRealtime() - this.mLastWorkStartedAt;
            if (elapsed > MIN_INTERESTING_DURATION_MILLIS) {
                traceAndLog(5, "observed a task that took " + elapsed + "ms: " + line);
            }
            super.endHandling(line);
            syncIdleMonitoring();
            this.mNumTasksSeen++;
            this.mNumTasksSinceLastIdle++;
        }

        private static void traceAndLog(int level, String message) {
            TraceEvent.instant("TraceEvent.LooperMonitor:IdleStats", message);
            Log.println(level, TAG, message);
        }

        public final boolean queueIdle() {
            long now = SystemClock.elapsedRealtime();
            if (this.mLastIdleStartedAt == 0) {
                this.mLastIdleStartedAt = now;
            }
            long elapsed = now - this.mLastIdleStartedAt;
            this.mNumIdlesSeen++;
            TraceEvent.begin(IDLE_EVENT_NAME, this.mNumTasksSinceLastIdle + " tasks since last idle.");
            if (elapsed > MIN_INTERESTING_BURST_DURATION_MILLIS) {
                traceAndLog(3, this.mNumTasksSeen + " tasks and " + this.mNumIdlesSeen + " idles processed so far, " + this.mNumTasksSinceLastIdle + " tasks bursted and " + elapsed + "ms elapsed since last idle");
            }
            this.mLastIdleStartedAt = now;
            this.mNumTasksSinceLastIdle = 0;
            return true;
        }
    }

    private static native void nativeBegin(String str, String str2);

    private static native void nativeBeginToplevel();

    private static native void nativeEnd(String str, String str2);

    private static native void nativeEndToplevel();

    private static native void nativeFinishAsync(String str, long j);

    private static native void nativeInstant(String str, String str2);

    private static native void nativeRegisterEnabledObserver();

    private static native void nativeStartATrace();

    private static native void nativeStartAsync(String str, long j);

    private static native void nativeStopATrace();

    static {
        sEnabled = false;
        sATraceEnabled = false;
    }

    public static void registerNativeEnabledObserver() {
        nativeRegisterEnabledObserver();
    }

    @CalledByNative
    public static void setEnabled(boolean enabled) {
        sEnabled = enabled;
        if (!sATraceEnabled) {
            ThreadUtils.getUiThreadLooper().setMessageLogging(enabled ? LooperMonitorHolder.sInstance : null);
        }
    }

    public static void setATraceEnabled(boolean enabled) {
        if (sATraceEnabled != enabled) {
            sATraceEnabled = enabled;
            if (enabled) {
                nativeStartATrace();
            } else {
                nativeStopATrace();
            }
        }
    }

    public static boolean enabled() {
        return sEnabled;
    }

    public static void instant(String name) {
        if (sEnabled) {
            nativeInstant(name, null);
        }
    }

    public static void instant(String name, String arg) {
        if (sEnabled) {
            nativeInstant(name, arg);
        }
    }

    public static void startAsync(String name, long id) {
        if (sEnabled) {
            nativeStartAsync(name, id);
        }
    }

    public static void finishAsync(String name, long id) {
        if (sEnabled) {
            nativeFinishAsync(name, id);
        }
    }

    public static void begin(String name) {
        if (sEnabled) {
            nativeBegin(name, null);
        }
    }

    public static void begin(String name, String arg) {
        if (sEnabled) {
            nativeBegin(name, arg);
        }
    }

    public static void end(String name) {
        if (sEnabled) {
            nativeEnd(name, null);
        }
    }

    public static void end(String name, String arg) {
        if (sEnabled) {
            nativeEnd(name, arg);
        }
    }
}
