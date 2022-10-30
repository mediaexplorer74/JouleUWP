package org.chromium.content.browser;

import android.content.Context;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.device.battery.BatteryMonitorFactory;
import org.chromium.mojom.device.BatteryMonitor;

@JNINamespace("content")
class ServiceRegistrar {
    static final /* synthetic */ boolean $assertionsDisabled;

    private static class BatteryMonitorImplementationFactory implements ImplementationFactory<BatteryMonitor> {
        private final BatteryMonitorFactory mFactory;

        BatteryMonitorImplementationFactory(Context applicationContext) {
            this.mFactory = new BatteryMonitorFactory(applicationContext);
        }

        public BatteryMonitor createImpl() {
            return this.mFactory.createMonitor();
        }
    }

    static {
        $assertionsDisabled = !ServiceRegistrar.class.desiredAssertionStatus();
    }

    ServiceRegistrar() {
    }

    @CalledByNative
    static void registerProcessHostServices(ServiceRegistry registry, Context applicationContext) {
        if ($assertionsDisabled || applicationContext != null) {
            registry.addService(BatteryMonitor.MANAGER, new BatteryMonitorImplementationFactory(applicationContext));
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    static void registerFrameHostServices(ServiceRegistry registry, Context applicationContext) {
        if (!$assertionsDisabled && applicationContext == null) {
            throw new AssertionError();
        }
    }
}
