package org.xwalk.core.internal.extension.api.device_capabilities;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.json.JSONException;
import org.json.JSONObject;

class DeviceCapabilitiesMemory {
    private static final String MEM_INFO_FILE = "/proc/meminfo";
    private static final String TAG = "DeviceCapabilitiesMemory";
    private long mAvailableCapacity;
    private long mCapacity;
    private Context mContext;
    private DeviceCapabilities mDeviceCapabilities;

    public DeviceCapabilitiesMemory(DeviceCapabilities instance, Context context) {
        this.mAvailableCapacity = 0;
        this.mCapacity = 0;
        this.mDeviceCapabilities = instance;
        this.mContext = context;
    }

    public JSONObject getInfo() {
        readMemoryInfo();
        JSONObject out = new JSONObject();
        try {
            out.put("capacity", this.mCapacity);
            out.put("availCapacity", this.mAvailableCapacity);
            return out;
        } catch (JSONException e) {
            return this.mDeviceCapabilities.setErrorMessage(e.toString());
        }
    }

    private void readMemoryInfo() {
        MemoryInfo mem_info = new MemoryInfo();
        ((ActivityManager) this.mContext.getSystemService("activity")).getMemoryInfo(mem_info);
        if (VERSION.SDK_INT >= 16) {
            this.mCapacity = mem_info.totalMem;
        } else {
            this.mCapacity = getTotalMemFromFile();
        }
        this.mAvailableCapacity = mem_info.availMem;
    }

    private long getTotalMemFromFile() {
        IOException e;
        Throwable th;
        RandomAccessFile file = null;
        long capacity;
        try {
            RandomAccessFile file2 = new RandomAccessFile(MEM_INFO_FILE, "r");
            try {
                String[] arrs = file2.readLine().split(":");
                if (arrs[0].equals("MemTotal")) {
                    capacity = Long.parseLong(arrs[1].trim().split("\\s+")[0]) * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                    if (file2 != null) {
                        try {
                            file2.close();
                        } catch (IOException e2) {
                            Log.e(TAG, e2.toString());
                            file = file2;
                        }
                    }
                    file = file2;
                    return capacity;
                }
                if (file2 != null) {
                    try {
                        file2.close();
                    } catch (IOException e22) {
                        Log.e(TAG, e22.toString());
                    }
                }
                file = file2;
                return 0;
            } catch (IOException e3) {
                e22 = e3;
                file = file2;
                capacity = 0;
                try {
                    Log.e(TAG, e22.toString());
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e222) {
                            Log.e(TAG, e222.toString());
                        }
                    }
                    return capacity;
                } catch (Throwable th2) {
                    th = th2;
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e2222) {
                            Log.e(TAG, e2222.toString());
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                file = file2;
                if (file != null) {
                    file.close();
                }
                throw th;
            }
        } catch (IOException e4) {
            e2222 = e4;
            capacity = 0;
            Log.e(TAG, e2222.toString());
            if (file != null) {
                file.close();
            }
            return capacity;
        }
    }
}
