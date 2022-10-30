package org.xwalk.core.internal.extension.api.device_capabilities;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.json.JSONException;
import org.json.JSONObject;

class DeviceCapabilitiesCPU {
    private static final String SYSTEM_INFO_STAT_FILE = "/proc/stat";
    private static final String TAG = "DeviceCapabilitiesCPU";
    private String mCPUArch;
    private double mCPULoad;
    private int mCoreNum;
    private DeviceCapabilities mDeviceCapabilities;

    public DeviceCapabilitiesCPU(DeviceCapabilities instance) {
        this.mCoreNum = 0;
        this.mCPUArch = "Unknown";
        this.mCPULoad = 0.0d;
        this.mDeviceCapabilities = instance;
        this.mCoreNum = Runtime.getRuntime().availableProcessors();
        this.mCPUArch = System.getProperty("os.arch");
    }

    public JSONObject getInfo() {
        getCPULoad();
        JSONObject out = new JSONObject();
        try {
            out.put("numOfProcessors", this.mCoreNum);
            out.put("archName", this.mCPUArch);
            out.put("load", this.mCPULoad);
            return out;
        } catch (JSONException e) {
            return this.mDeviceCapabilities.setErrorMessage(e.toString());
        }
    }

    private boolean getCPULoad() {
        try {
            int i;
            RandomAccessFile file = new RandomAccessFile(SYSTEM_INFO_STAT_FILE, "r");
            String[] arrs = file.readLine().split("\\s+");
            long total1 = 0;
            for (i = 1; i < arrs.length; i++) {
                total1 += Long.parseLong(arrs[i]);
            }
            long used1 = total1 - Long.parseLong(arrs[4]);
            try {
                Thread.sleep(1000);
                file.seek(0);
                String line = file.readLine();
                file.close();
                arrs = line.split("\\s+");
                long total2 = 0;
                for (i = 1; i < arrs.length; i++) {
                    total2 += Long.parseLong(arrs[i]);
                }
                long used2 = total2 - Long.parseLong(arrs[4]);
                if (total2 == total1) {
                    this.mCPULoad = 0.0d;
                } else {
                    this.mCPULoad = ((double) (used2 - used1)) / ((double) (total2 - total1));
                }
                return true;
            } catch (Exception e) {
                this.mCPULoad = 0.0d;
                return false;
            }
        } catch (IOException e2) {
            this.mCPULoad = 0.0d;
            return false;
        }
    }
}
