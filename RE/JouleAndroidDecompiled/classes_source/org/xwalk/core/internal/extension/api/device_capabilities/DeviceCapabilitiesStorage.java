package org.xwalk.core.internal.extension.api.device_capabilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.util.SparseArray;
import com.adobe.phonegap.push.PushConstants;
import java.io.File;
import java.lang.ref.WeakReference;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

class DeviceCapabilitiesStorage {
    private static final String TAG = "DeviceCapabilitiesStorage";
    private static int mStorageCount;
    private WeakReference<Activity> mActivity;
    private DeviceCapabilities mDeviceCapabilities;
    private IntentFilter mIntentFilter;
    private boolean mIsListening;
    private final SparseArray<StorageUnit> mStorageList;
    private final BroadcastReceiver mStorageListener;

    /* renamed from: org.xwalk.core.internal.extension.api.device_capabilities.DeviceCapabilitiesStorage.1 */
    class C04951 extends BroadcastReceiver {
        C04951() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                DeviceCapabilitiesStorage.this.notifyAndSaveAttachedStorage();
            }
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(action) || "android.intent.action.MEDIA_REMOVED".equals(action) || "android.intent.action.MEDIA_BAD_REMOVAL".equals(action)) {
                DeviceCapabilitiesStorage.this.notifyAndRemoveDetachedStorage();
            }
        }
    }

    class StorageUnit {
        private long mAvailCapacity;
        private long mCapacity;
        private int mId;
        private String mName;
        private String mPath;
        private String mType;

        public StorageUnit(int id, String name, String type) {
            this.mId = id;
            this.mName = name;
            this.mType = type;
            this.mPath = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
            this.mCapacity = 0;
            this.mAvailCapacity = 0;
        }

        public int getId() {
            return this.mId;
        }

        public String getName() {
            return this.mName;
        }

        public String getType() {
            return this.mType;
        }

        public String getPath() {
            return this.mPath;
        }

        public long getCapacity() {
            return this.mCapacity;
        }

        public long getAvailCapacity() {
            return this.mAvailCapacity;
        }

        public void setType(String type) {
            this.mType = type;
        }

        public void setPath(String path) {
            this.mPath = path;
            updateCapacity();
        }

        public boolean isSame(StorageUnit unit) {
            return this.mPath == unit.getPath();
        }

        public boolean isValid() {
            if (this.mPath != null && !this.mPath.isEmpty()) {
                return new File(this.mPath).canRead();
            }
            this.mCapacity = 0;
            this.mAvailCapacity = 0;
            return false;
        }

        public void updateCapacity() {
            if (isValid()) {
                StatFs stat = new StatFs(this.mPath);
                long blockSize;
                if (VERSION.SDK_INT >= 18) {
                    blockSize = stat.getBlockSizeLong();
                    this.mCapacity = stat.getBlockCountLong() * blockSize;
                    this.mAvailCapacity = stat.getAvailableBlocksLong() * blockSize;
                    return;
                }
                blockSize = (long) stat.getBlockSize();
                this.mCapacity = ((long) stat.getBlockCount()) * blockSize;
                this.mAvailCapacity = ((long) stat.getAvailableBlocks()) * blockSize;
            }
        }

        public JSONObject convertToJSON() {
            JSONObject out = new JSONObject();
            try {
                out.put("id", Integer.toString(this.mId + 1));
                out.put("name", this.mName);
                out.put(MessagingSmsConsts.TYPE, this.mType);
                out.put("capacity", this.mCapacity);
                out.put("availCapacity", this.mAvailCapacity);
                return out;
            } catch (JSONException e) {
                return DeviceCapabilitiesStorage.this.mDeviceCapabilities.setErrorMessage(e.toString());
            }
        }
    }

    static {
        mStorageCount = 0;
    }

    public DeviceCapabilitiesStorage(DeviceCapabilities instance, Activity activity) {
        this.mStorageList = new SparseArray();
        this.mIsListening = false;
        this.mIntentFilter = new IntentFilter();
        this.mStorageListener = new C04951();
        this.mDeviceCapabilities = instance;
        this.mActivity = new WeakReference(activity);
        registerIntentFilter();
        initStorageList();
    }

    public JSONObject getInfo() {
        JSONObject out = new JSONObject();
        JSONArray arr = new JSONArray();
        int i = 0;
        while (i < this.mStorageList.size()) {
            try {
                arr.put(((StorageUnit) this.mStorageList.valueAt(i)).convertToJSON());
                i++;
            } catch (JSONException e) {
                return this.mDeviceCapabilities.setErrorMessage(e.toString());
            }
        }
        out.put("storages", arr);
        return out;
    }

    private void initStorageList() {
        this.mStorageList.clear();
        mStorageCount = 0;
        StorageUnit unit = new StorageUnit(mStorageCount, "Internal", "fixed");
        unit.setPath(Environment.getRootDirectory().getAbsolutePath());
        this.mStorageList.put(mStorageCount, unit);
        mStorageCount++;
        unit = new StorageUnit(mStorageCount, new String("sdcard" + Integer.toString(mStorageCount - 1)), "fixed");
        if (Environment.isExternalStorageRemovable()) {
            unit.setType("removable");
        }
        unit.setPath(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (unit.isValid()) {
            this.mStorageList.put(mStorageCount, unit);
            mStorageCount++;
        }
        attemptAddExternalStorage();
    }

    private void registerIntentFilter() {
        this.mIntentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        this.mIntentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        this.mIntentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        this.mIntentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        this.mIntentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        this.mIntentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        this.mIntentFilter.addDataScheme(AndroidProtocolHandler.FILE_SCHEME);
    }

    private boolean attemptAddExternalStorage() {
        int sdcardNum = mStorageCount - 1;
        StorageUnit unit = new StorageUnit(mStorageCount, new String("sdcard" + Integer.toString(sdcardNum)), "removable");
        unit.setPath("/storage/sdcard" + Integer.toString(sdcardNum));
        if (!unit.isValid()) {
            return false;
        }
        for (int i = 0; i < this.mStorageList.size(); i++) {
            if (unit.isSame((StorageUnit) this.mStorageList.valueAt(i))) {
                return false;
            }
        }
        this.mStorageList.put(mStorageCount, unit);
        mStorageCount++;
        return true;
    }

    public void registerListener() {
        if (!this.mIsListening) {
            this.mIsListening = true;
            Activity activity = (Activity) this.mActivity.get();
            if (activity != null) {
                activity.registerReceiver(this.mStorageListener, this.mIntentFilter);
            }
        }
    }

    public void unregisterListener() {
        if (this.mIsListening) {
            this.mIsListening = false;
            Activity activity = (Activity) this.mActivity.get();
            if (activity != null) {
                activity.unregisterReceiver(this.mStorageListener);
            }
        }
    }

    private void notifyAndSaveAttachedStorage() {
        if (attemptAddExternalStorage()) {
            StorageUnit unit = (StorageUnit) this.mStorageList.valueAt(this.mStorageList.size() - 1);
            JSONObject out = new JSONObject();
            try {
                out.put("reply", "attachStorage");
                out.put("eventName", "storageattach");
                out.put(PushConstants.PARSE_COM_DATA, unit.convertToJSON());
                this.mDeviceCapabilities.broadcastMessage(out.toString());
            } catch (JSONException e) {
                this.mDeviceCapabilities.printErrorMessage(e);
            }
        }
    }

    private void notifyAndRemoveDetachedStorage() {
        StorageUnit unit = (StorageUnit) this.mStorageList.valueAt(this.mStorageList.size() - 1);
        if (unit.getType() == "removable") {
            JSONObject out = new JSONObject();
            try {
                out.put("reply", "detachStorage");
                out.put("eventName", "storagedetach");
                out.put(PushConstants.PARSE_COM_DATA, unit.convertToJSON());
                this.mDeviceCapabilities.broadcastMessage(out.toString());
                this.mStorageList.remove(unit.getId());
                mStorageCount--;
            } catch (JSONException e) {
                this.mDeviceCapabilities.printErrorMessage(e);
            }
        }
    }

    public void onResume() {
        if (!((StorageUnit) this.mStorageList.valueAt(this.mStorageList.size() - 1)).isValid()) {
            notifyAndRemoveDetachedStorage();
        }
        notifyAndSaveAttachedStorage();
        registerListener();
    }

    public void onPause() {
        unregisterListener();
    }

    public void onDestroy() {
    }
}
