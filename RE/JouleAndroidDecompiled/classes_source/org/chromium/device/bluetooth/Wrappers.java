package org.chromium.device.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;

@JNINamespace("device")
class Wrappers {
    private static final String TAG = "cr.Bluetooth";

    static class BluetoothAdapterWrapper {
        private final BluetoothAdapter mAdapter;

        @CalledByNative("BluetoothAdapterWrapper")
        public static BluetoothAdapterWrapper createWithDefaultAdapter(Context context) {
            boolean hasPermissions;
            if (context.checkCallingOrSelfPermission("android.permission.BLUETOOTH") == 0 && context.checkCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN") == 0) {
                hasPermissions = true;
            } else {
                hasPermissions = false;
            }
            if (hasPermissions) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null) {
                    return new BluetoothAdapterWrapper(adapter);
                }
                Log.m33i(Wrappers.TAG, "BluetoothAdapterWrapper.create failed: Default adapter not found.", new Object[0]);
                return null;
            }
            Log.m42w(Wrappers.TAG, "BluetoothAdapterWrapper.create failed: Lacking Bluetooth permissions.", new Object[0]);
            return null;
        }

        public BluetoothAdapterWrapper(BluetoothAdapter adapter) {
            this.mAdapter = adapter;
        }

        public boolean isEnabled() {
            return this.mAdapter.isEnabled();
        }

        public String getAddress() {
            return this.mAdapter.getAddress();
        }

        public String getName() {
            return this.mAdapter.getName();
        }

        public int getScanMode() {
            return this.mAdapter.getScanMode();
        }

        public boolean isDiscovering() {
            return this.mAdapter.isDiscovering();
        }
    }

    Wrappers() {
    }
}
