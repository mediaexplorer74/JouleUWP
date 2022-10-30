package org.chromium.content.browser;

import android.content.Context;
import java.util.concurrent.FutureTask;
import org.chromium.base.CalledByNative;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.LocationProviderFactory.LocationProvider;

@VisibleForTesting
public class LocationProviderAdapter {
    static final /* synthetic */ boolean $assertionsDisabled;
    private LocationProvider mImpl;

    /* renamed from: org.chromium.content.browser.LocationProviderAdapter.1 */
    class C03431 implements Runnable {
        final /* synthetic */ boolean val$gpsEnabled;

        C03431(boolean z) {
            this.val$gpsEnabled = z;
        }

        public void run() {
            LocationProviderAdapter.this.mImpl.start(this.val$gpsEnabled);
        }
    }

    /* renamed from: org.chromium.content.browser.LocationProviderAdapter.2 */
    class C03442 implements Runnable {
        C03442() {
        }

        public void run() {
            LocationProviderAdapter.this.mImpl.stop();
        }
    }

    private static native void nativeNewErrorAvailable(String str);

    private static native void nativeNewLocationAvailable(double d, double d2, double d3, boolean z, double d4, boolean z2, double d5, boolean z3, double d6, boolean z4, double d7);

    static {
        $assertionsDisabled = !LocationProviderAdapter.class.desiredAssertionStatus();
    }

    private LocationProviderAdapter(Context context) {
        this.mImpl = LocationProviderFactory.get(context);
    }

    @CalledByNative
    static LocationProviderAdapter create(Context context) {
        return new LocationProviderAdapter(context);
    }

    @CalledByNative
    public boolean start(boolean gpsEnabled) {
        ThreadUtils.runOnUiThread(new FutureTask(new C03431(gpsEnabled), null));
        return true;
    }

    @CalledByNative
    public void stop() {
        ThreadUtils.runOnUiThread(new FutureTask(new C03442(), null));
    }

    public boolean isRunning() {
        if ($assertionsDisabled || ThreadUtils.runningOnUiThread()) {
            return this.mImpl.isRunning();
        }
        throw new AssertionError();
    }

    public static void newLocationAvailable(double latitude, double longitude, double timestamp, boolean hasAltitude, double altitude, boolean hasAccuracy, double accuracy, boolean hasHeading, double heading, boolean hasSpeed, double speed) {
        nativeNewLocationAvailable(latitude, longitude, timestamp, hasAltitude, altitude, hasAccuracy, accuracy, hasHeading, heading, hasSpeed, speed);
    }

    public static void newErrorAvailable(String message) {
        nativeNewErrorAvailable(message);
    }
}
