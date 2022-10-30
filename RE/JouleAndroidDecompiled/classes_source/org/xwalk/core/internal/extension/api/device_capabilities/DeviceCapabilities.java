package org.xwalk.core.internal.extension.api.device_capabilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.ConnectionResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.XWalkExtensionWithActivityStateListener;

public class DeviceCapabilities extends XWalkExtensionWithActivityStateListener {
    public static final String JS_API_PATH = "jsapi/device_capabilities_api.js";
    private static final String NAME = "xwalk.experimental.system";
    private static final String TAG = "DeviceCapabilities";
    private DeviceCapabilitiesCPU mCPU;
    private DeviceCapabilitiesCodecs mCodecs;
    private DeviceCapabilitiesDisplay mDisplay;
    private DeviceCapabilitiesMemory mMemory;
    private DeviceCapabilitiesStorage mStorage;

    public DeviceCapabilities(String jsApiContent, Activity activity) {
        super(NAME, jsApiContent, activity);
        Context context = activity.getApplicationContext();
        this.mCPU = new DeviceCapabilitiesCPU(this);
        this.mCodecs = new DeviceCapabilitiesCodecs(this);
        this.mDisplay = new DeviceCapabilitiesDisplay(this, context);
        this.mMemory = new DeviceCapabilitiesMemory(this, context);
        this.mStorage = new DeviceCapabilitiesStorage(this, activity);
    }

    private void handleMessage(int instanceID, String message) {
        try {
            JSONObject jsonInput = new JSONObject(message);
            String cmd = jsonInput.getString("cmd");
            if (cmd.equals("addEventListener")) {
                handleAddEventListener(jsonInput.getString("eventName"));
            } else {
                handleGetDeviceInfo(instanceID, jsonInput.getString("asyncCallId"), cmd);
            }
        } catch (JSONException e) {
            printErrorMessage(e);
        }
    }

    private void handleGetDeviceInfo(int instanceID, String asyncCallId, String cmd) {
        try {
            JSONObject jsonOutput = new JSONObject();
            if (cmd.equals("getCPUInfo")) {
                jsonOutput.put(PushConstants.PARSE_COM_DATA, this.mCPU.getInfo());
            } else if (cmd.equals("getCodecsInfo")) {
                jsonOutput.put(PushConstants.PARSE_COM_DATA, this.mCodecs.getInfo());
            } else if (cmd.equals("getDisplayInfo")) {
                jsonOutput.put(PushConstants.PARSE_COM_DATA, this.mDisplay.getInfo());
            } else if (cmd.equals("getMemoryInfo")) {
                jsonOutput.put(PushConstants.PARSE_COM_DATA, this.mMemory.getInfo());
            } else if (cmd.equals("getStorageInfo")) {
                jsonOutput.put(PushConstants.PARSE_COM_DATA, this.mStorage.getInfo());
            }
            jsonOutput.put("asyncCallId", asyncCallId);
            postMessage(instanceID, jsonOutput.toString());
        } catch (JSONException e) {
            printErrorMessage(e);
        }
    }

    private void handleAddEventListener(String eventName) {
        if (eventName.equals("storageattach") || eventName.equals("storagedetach")) {
            this.mStorage.registerListener();
        }
    }

    protected void printErrorMessage(JSONException e) {
        Log.e(TAG, e.toString());
    }

    protected JSONObject setErrorMessage(String error) {
        JSONObject out = new JSONObject();
        JSONObject errorMessage = new JSONObject();
        try {
            errorMessage.put(PushConstants.MESSAGE, error);
            out.put("error", errorMessage);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return out;
    }

    public void onMessage(int instanceID, String message) {
        if (!message.isEmpty()) {
            handleMessage(instanceID, message);
        }
    }

    public void onActivityStateChange(Activity activity, int newState) {
        switch (newState) {
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                this.mDisplay.onResume();
                this.mStorage.onResume();
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                this.mDisplay.onPause();
                this.mStorage.onPause();
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                this.mDisplay.onDestroy();
                this.mStorage.onDestroy();
            default:
        }
    }

    public String onSyncMessage(int instanceID, String message) {
        return null;
    }
}
