package org.chromium.device.battery;

import android.content.Context;
import android.util.Log;
import java.util.HashSet;
import java.util.Iterator;
import org.chromium.base.ThreadUtils;
import org.chromium.mojom.device.BatteryMonitor;
import org.chromium.mojom.device.BatteryStatus;

public class BatteryMonitorFactory {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final String TAG = "BatteryMonitorFactory";
    private final BatteryStatusCallback mCallback;
    private final BatteryStatusManager mManager;
    private final HashSet<BatteryMonitorImpl> mSubscribedMonitors;

    /* renamed from: org.chromium.device.battery.BatteryMonitorFactory.1 */
    class C06121 implements BatteryStatusCallback {
        C06121() {
        }

        public void onBatteryStatusChanged(BatteryStatus batteryStatus) {
            ThreadUtils.assertOnUiThread();
            Iterator i$ = BatteryMonitorFactory.this.mSubscribedMonitors.iterator();
            while (i$.hasNext()) {
                ((BatteryMonitorImpl) i$.next()).didChange(batteryStatus);
            }
        }
    }

    static {
        $assertionsDisabled = !BatteryMonitorFactory.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public BatteryMonitorFactory(Context applicationContext) {
        this.mSubscribedMonitors = new HashSet();
        this.mCallback = new C06121();
        if ($assertionsDisabled || applicationContext != null) {
            this.mManager = new BatteryStatusManager(applicationContext, this.mCallback);
            return;
        }
        throw new AssertionError();
    }

    public BatteryMonitor createMonitor() {
        ThreadUtils.assertOnUiThread();
        if (this.mSubscribedMonitors.isEmpty() && !this.mManager.start()) {
            Log.e(TAG, "BatteryStatusManager failed to start.");
        }
        BatteryMonitorImpl monitor = new BatteryMonitorImpl(this);
        this.mSubscribedMonitors.add(monitor);
        return monitor;
    }

    void unsubscribe(BatteryMonitorImpl monitor) {
        ThreadUtils.assertOnUiThread();
        if ($assertionsDisabled || this.mSubscribedMonitors.contains(monitor)) {
            this.mSubscribedMonitors.remove(monitor);
            if (this.mSubscribedMonitors.isEmpty()) {
                this.mManager.stop();
                return;
            }
            return;
        }
        throw new AssertionError();
    }
}
