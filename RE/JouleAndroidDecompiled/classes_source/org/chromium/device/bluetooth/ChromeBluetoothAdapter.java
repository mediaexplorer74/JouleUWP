package org.chromium.device.bluetooth;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

@JNINamespace("device")
final class ChromeBluetoothAdapter {
    private static final String TAG = "cr.Bluetooth";
    private BluetoothAdapterWrapper mAdapter;

    private ChromeBluetoothAdapter(BluetoothAdapterWrapper adapterWrapper) {
        this.mAdapter = adapterWrapper;
        if (adapterWrapper == null) {
            Log.m33i(TAG, "ChromeBluetoothAdapter created with no adapterWrapper.", new Object[0]);
        } else {
            Log.m33i(TAG, "ChromeBluetoothAdapter created with provided adapterWrapper.", new Object[0]);
        }
    }

    @CalledByNative
    public static ChromeBluetoothAdapter create(Object adapterWrapper) {
        return new ChromeBluetoothAdapter((BluetoothAdapterWrapper) adapterWrapper);
    }

    @CalledByNative
    private String getAddress() {
        if (isPresent()) {
            return this.mAdapter.getAddress();
        }
        return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
    }

    @CalledByNative
    private String getName() {
        if (isPresent()) {
            return this.mAdapter.getName();
        }
        return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
    }

    @CalledByNative
    private boolean isPresent() {
        return this.mAdapter != null;
    }

    @CalledByNative
    private boolean isPowered() {
        return isPresent() && this.mAdapter.isEnabled();
    }

    @CalledByNative
    private boolean isDiscoverable() {
        return isPresent() && this.mAdapter.getScanMode() == 23;
    }

    @CalledByNative
    private boolean isDiscovering() {
        return isPresent() && this.mAdapter.isDiscovering();
    }
}
