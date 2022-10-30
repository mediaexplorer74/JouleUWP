package org.chromium.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.WindowManager;
import org.chromium.base.TraceEvent;

@SuppressLint({"NewApi"})
public class VSyncMonitor {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long NANOSECONDS_PER_MICROSECOND = 1000;
    private static final long NANOSECONDS_PER_MILLISECOND = 1000000;
    private static final long NANOSECONDS_PER_SECOND = 1000000000;
    private final Choreographer mChoreographer;
    private boolean mConsecutiveVSync;
    private long mGoodStartingPointNano;
    private final Handler mHandler;
    private boolean mHaveRequestInFlight;
    private boolean mInsideVSync;
    private long mLastPostedNano;
    private long mLastVSyncCpuTimeNano;
    private Listener mListener;
    private long mRefreshPeriodNano;
    private final Runnable mSyntheticVSyncRunnable;
    private final FrameCallback mVSyncFrameCallback;
    private final Runnable mVSyncRunnableCallback;

    /* renamed from: org.chromium.ui.VSyncMonitor.1 */
    class C04101 implements FrameCallback {
        final /* synthetic */ boolean val$useEstimatedRefreshPeriod;

        C04101(boolean z) {
            this.val$useEstimatedRefreshPeriod = z;
        }

        public void doFrame(long frameTimeNanos) {
            TraceEvent.begin("VSync");
            VSyncMonitor.this.mHandler.removeCallbacks(VSyncMonitor.this.mSyntheticVSyncRunnable);
            if (this.val$useEstimatedRefreshPeriod && VSyncMonitor.this.mConsecutiveVSync) {
                VSyncMonitor.access$414(VSyncMonitor.this, (long) (((float) ((frameTimeNanos - VSyncMonitor.this.mGoodStartingPointNano) - VSyncMonitor.this.mRefreshPeriodNano)) * 0.1f));
            }
            VSyncMonitor.this.mGoodStartingPointNano = frameTimeNanos;
            VSyncMonitor.this.onVSyncCallback(frameTimeNanos, VSyncMonitor.this.getCurrentNanoTime());
            TraceEvent.end("VSync");
        }
    }

    /* renamed from: org.chromium.ui.VSyncMonitor.2 */
    class C04112 implements Runnable {
        C04112() {
        }

        public void run() {
            TraceEvent.begin("VSyncTimer");
            long currentTime = VSyncMonitor.this.getCurrentNanoTime();
            VSyncMonitor.this.onVSyncCallback(currentTime, currentTime);
            TraceEvent.end("VSyncTimer");
        }
    }

    /* renamed from: org.chromium.ui.VSyncMonitor.3 */
    class C04123 implements Runnable {
        C04123() {
        }

        public void run() {
            TraceEvent.begin("VSyncSynthetic");
            VSyncMonitor.this.mChoreographer.removeFrameCallback(VSyncMonitor.this.mVSyncFrameCallback);
            long currentTime = VSyncMonitor.this.getCurrentNanoTime();
            VSyncMonitor.this.onVSyncCallback(VSyncMonitor.this.estimateLastVSyncTime(currentTime), currentTime);
            TraceEvent.end("VSyncSynthetic");
        }
    }

    public interface Listener {
        void onVSync(VSyncMonitor vSyncMonitor, long j);
    }

    static {
        $assertionsDisabled = !VSyncMonitor.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    static /* synthetic */ long access$414(VSyncMonitor x0, long x1) {
        long j = x0.mRefreshPeriodNano + x1;
        x0.mRefreshPeriodNano = j;
        return j;
    }

    public VSyncMonitor(Context context, Listener listener) {
        this(context, listener, true);
    }

    public VSyncMonitor(Context context, Listener listener, boolean enableJBVSync) {
        boolean useEstimatedRefreshPeriod = $assertionsDisabled;
        this.mInsideVSync = $assertionsDisabled;
        this.mConsecutiveVSync = $assertionsDisabled;
        this.mHandler = new Handler();
        this.mListener = listener;
        float refreshRate = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRefreshRate();
        if (refreshRate < 30.0f) {
            useEstimatedRefreshPeriod = true;
        }
        if (refreshRate <= 0.0f) {
            refreshRate = 60.0f;
        }
        this.mRefreshPeriodNano = (long) (1.0E9f / refreshRate);
        if (!enableJBVSync || VERSION.SDK_INT < 16) {
            this.mChoreographer = null;
            this.mVSyncFrameCallback = null;
            this.mVSyncRunnableCallback = new C04112();
            this.mLastPostedNano = 0;
        } else {
            this.mChoreographer = Choreographer.getInstance();
            this.mVSyncFrameCallback = new C04101(useEstimatedRefreshPeriod);
            this.mVSyncRunnableCallback = null;
        }
        this.mSyntheticVSyncRunnable = new C04123();
        this.mGoodStartingPointNano = getCurrentNanoTime();
    }

    public long getVSyncPeriodInMicroseconds() {
        return this.mRefreshPeriodNano / NANOSECONDS_PER_MICROSECOND;
    }

    private boolean isVSyncSignalAvailable() {
        return this.mChoreographer != null ? true : $assertionsDisabled;
    }

    public void requestUpdate() {
        if ($assertionsDisabled || this.mHandler.getLooper() == Looper.myLooper()) {
            postCallback();
            return;
        }
        throw new AssertionError();
    }

    public void setVSyncPointForICS(long goodStartingPointNano) {
        this.mGoodStartingPointNano = goodStartingPointNano;
    }

    public boolean isInsideVSync() {
        return this.mInsideVSync;
    }

    private long getCurrentNanoTime() {
        return System.nanoTime();
    }

    private void onVSyncCallback(long frameTimeNanos, long currentTimeNanos) {
        if ($assertionsDisabled || this.mHaveRequestInFlight) {
            this.mInsideVSync = true;
            this.mHaveRequestInFlight = $assertionsDisabled;
            this.mLastVSyncCpuTimeNano = currentTimeNanos;
            try {
                if (this.mListener != null) {
                    this.mListener.onVSync(this, frameTimeNanos / NANOSECONDS_PER_MICROSECOND);
                }
                this.mInsideVSync = $assertionsDisabled;
            } catch (Throwable th) {
                this.mInsideVSync = $assertionsDisabled;
            }
        } else {
            throw new AssertionError();
        }
    }

    private void postCallback() {
        if (!this.mHaveRequestInFlight) {
            this.mHaveRequestInFlight = true;
            this.mConsecutiveVSync = this.mInsideVSync;
            if (isVSyncSignalAvailable()) {
                this.mConsecutiveVSync = this.mInsideVSync;
                postSyntheticVSyncIfNecessary();
                this.mChoreographer.postFrameCallback(this.mVSyncFrameCallback);
                return;
            }
            postRunnableCallback();
        }
    }

    private void postSyntheticVSyncIfNecessary() {
        long currentTime = getCurrentNanoTime();
        if (currentTime - this.mLastVSyncCpuTimeNano >= this.mRefreshPeriodNano * 2 && currentTime - estimateLastVSyncTime(currentTime) <= this.mRefreshPeriodNano / 2) {
            this.mHandler.post(this.mSyntheticVSyncRunnable);
        }
    }

    private long estimateLastVSyncTime(long currentTime) {
        return this.mGoodStartingPointNano + (((currentTime - this.mGoodStartingPointNano) / this.mRefreshPeriodNano) * this.mRefreshPeriodNano);
    }

    private void postRunnableCallback() {
        if ($assertionsDisabled || !isVSyncSignalAvailable()) {
            long currentTime = getCurrentNanoTime();
            long delay = (this.mRefreshPeriodNano + estimateLastVSyncTime(currentTime)) - currentTime;
            if ($assertionsDisabled || (delay > 0 && delay <= this.mRefreshPeriodNano)) {
                if (currentTime + delay <= this.mLastPostedNano + (this.mRefreshPeriodNano / 2)) {
                    delay += this.mRefreshPeriodNano;
                }
                this.mLastPostedNano = currentTime + delay;
                if (delay == 0) {
                    this.mHandler.post(this.mVSyncRunnableCallback);
                    return;
                } else {
                    this.mHandler.postDelayed(this.mVSyncRunnableCallback, delay / NANOSECONDS_PER_MILLISECOND);
                    return;
                }
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }
}
