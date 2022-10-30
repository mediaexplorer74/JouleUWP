package org.xwalk.core.internal.extension.api.device_capabilities;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import com.adobe.phonegap.push.PushConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager.DisplayListener;

class DeviceCapabilitiesDisplay {
    private static final String TAG = "DeviceCapabilitiesDisplay";
    private DeviceCapabilities mDeviceCapabilities;
    private final SparseArray<Display> mDisplayList;
    private final DisplayListener mDisplayListener;
    private XWalkDisplayManager mDisplayManager;

    /* renamed from: org.xwalk.core.internal.extension.api.device_capabilities.DeviceCapabilitiesDisplay.1 */
    class C06541 implements DisplayListener {
        C06541() {
        }

        public void onDisplayAdded(int displayId) {
            DeviceCapabilitiesDisplay.this.notifyAndSaveConnectedDisplay(DeviceCapabilitiesDisplay.this.mDisplayManager.getDisplay(displayId));
        }

        public void onDisplayRemoved(int displayId) {
            Display disp = (Display) DeviceCapabilitiesDisplay.this.mDisplayList.get(displayId);
            if (disp != null) {
                DeviceCapabilitiesDisplay.this.notifyAndRemoveDisconnectedDisplay(disp);
            }
        }

        public void onDisplayChanged(int displayId) {
        }
    }

    public DeviceCapabilitiesDisplay(DeviceCapabilities instance, Context context) {
        this.mDisplayList = new SparseArray();
        this.mDisplayListener = new C06541();
        this.mDeviceCapabilities = instance;
        this.mDisplayManager = XWalkDisplayManager.getInstance(context);
        initDisplayList();
    }

    public JSONObject getInfo() {
        JSONObject out = new JSONObject();
        JSONArray arr = new JSONArray();
        int i = 0;
        while (i < this.mDisplayList.size()) {
            try {
                arr.put(convertDisplayToJSON((Display) this.mDisplayList.valueAt(i)));
                i++;
            } catch (JSONException e) {
                return this.mDeviceCapabilities.setErrorMessage(e.toString());
            }
        }
        out.put("displays", arr);
        return out;
    }

    public JSONObject convertDisplayToJSON(Display disp) {
        boolean z = true;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        disp.getRealMetrics(displayMetrics);
        Point realSize = new Point();
        disp.getRealSize(realSize);
        Point availSize = new Point();
        disp.getSize(availSize);
        JSONObject out = new JSONObject();
        try {
            boolean z2;
            out.put("id", Integer.toString(disp.getDisplayId()));
            out.put("name", disp.getName());
            String str = "primary";
            if (disp.getDisplayId() == 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            out.put(str, z2);
            String str2 = "external";
            if (disp.getDisplayId() == 0) {
                z = false;
            }
            out.put(str2, z);
            out.put("deviceXDPI", (int) displayMetrics.xdpi);
            out.put("deviceYDPI", (int) displayMetrics.ydpi);
            out.put("width", realSize.x);
            out.put("height", realSize.y);
            out.put("availWidth", availSize.x);
            out.put("availHeight", availSize.y);
            out.put("colorDepth", 24);
            out.put("pixelDepth", 24);
            return out;
        } catch (JSONException e) {
            return this.mDeviceCapabilities.setErrorMessage(e.toString());
        }
    }

    private void initDisplayList() {
        for (Display disp : this.mDisplayManager.getDisplays()) {
            this.mDisplayList.put(disp.getDisplayId(), disp);
        }
    }

    private void notifyAndSaveConnectedDisplay(Display disp) {
        if (disp != null) {
            JSONObject out = new JSONObject();
            try {
                out.put("reply", "connectDisplay");
                out.put("eventName", "displayconnect");
                out.put(PushConstants.PARSE_COM_DATA, convertDisplayToJSON(disp));
                this.mDeviceCapabilities.broadcastMessage(out.toString());
                this.mDisplayList.put(disp.getDisplayId(), disp);
            } catch (JSONException e) {
                this.mDeviceCapabilities.printErrorMessage(e);
            }
        }
    }

    private void notifyAndRemoveDisconnectedDisplay(Display disp) {
        JSONObject out = new JSONObject();
        try {
            out.put("reply", "disconnectDisplay");
            out.put("eventName", "displaydisconnect");
            out.put(PushConstants.PARSE_COM_DATA, convertDisplayToJSON(disp));
            this.mDeviceCapabilities.broadcastMessage(out.toString());
            this.mDisplayList.remove(disp.getDisplayId());
        } catch (JSONException e) {
            this.mDeviceCapabilities.printErrorMessage(e);
        }
    }

    public void onResume() {
        Display[] displays = this.mDisplayManager.getDisplays();
        for (Display disp : displays) {
            if (((Display) this.mDisplayList.get(disp.getDisplayId())) == null) {
                notifyAndSaveConnectedDisplay(disp);
            } else {
                this.mDisplayList.put(disp.getDisplayId(), disp);
            }
        }
        for (int i = 0; i < this.mDisplayList.size(); i++) {
            boolean found = false;
            for (Display disp2 : displays) {
                if (((Display) this.mDisplayList.valueAt(i)).getDisplayId() == disp2.getDisplayId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notifyAndRemoveDisconnectedDisplay((Display) this.mDisplayList.valueAt(i));
            }
        }
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener);
    }

    public void onPause() {
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
    }

    public void onDestroy() {
    }
}
