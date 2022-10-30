package org.chromium.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import org.chromium.base.ApplicationStatus.ApplicationStateListener;

@JNINamespace("base::android")
public class PowerMonitor implements ApplicationStateListener {
    private static final long SUSPEND_DELAY_MS = 60000;
    private static PowerMonitor sInstance;
    private static final Runnable sSuspendTask;
    private final Handler mHandler;
    private boolean mIsBatteryPower;

    /* renamed from: org.chromium.base.PowerMonitor.1 */
    static class C03081 implements Runnable {
        C03081() {
        }

        public void run() {
            PowerMonitor.nativeOnMainActivitySuspended();
        }
    }

    private static class LazyHolder {
        private static final PowerMonitor INSTANCE;

        private LazyHolder() {
        }

        static {
            INSTANCE = new PowerMonitor();
        }
    }

    private static native void nativeOnBatteryChargingChanged();

    private static native void nativeOnMainActivityResumed();

    private static native void nativeOnMainActivitySuspended();

    static {
        sSuspendTask = new C03081();
    }

    public static void createForTests(Context context) {
        sInstance = LazyHolder.INSTANCE;
    }

    public static void create(Context context) {
        context = context.getApplicationContext();
        if (sInstance == null) {
            sInstance = LazyHolder.INSTANCE;
            ApplicationStatus.registerApplicationStateListener(sInstance);
            onBatteryChargingChanged(context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")));
        }
    }

    private PowerMonitor() {
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public static void onBatteryChargingChanged(Intent intent) {
        boolean z = true;
        if (sInstance != null) {
            int chargePlug = intent.getIntExtra("plugged", -1);
            PowerMonitor powerMonitor = sInstance;
            if (chargePlug == 2 || chargePlug == 1) {
                z = false;
            }
            powerMonitor.mIsBatteryPower = z;
            nativeOnBatteryChargingChanged();
        }
    }

    public void onApplicationStateChange(int newState) {
        if (newState == 1) {
            this.mHandler.removeCallbacks(sSuspendTask);
            nativeOnMainActivityResumed();
        } else if (newState == 2) {
            this.mHandler.postDelayed(sSuspendTask, SUSPEND_DELAY_MS);
        }
    }

    @CalledByNative
    private static boolean isBatteryPower() {
        return sInstance.mIsBatteryPower;
    }
}
