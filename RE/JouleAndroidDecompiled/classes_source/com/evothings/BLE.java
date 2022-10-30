package com.evothings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.ui.base.ime.TextInputType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class BLE extends CordovaPlugin implements LeScanCallback {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID;
    private static final String TAG = "CSBLE";
    private Context mContext;
    HashMap<Integer, GattHandler> mGatt;
    int mNextGattHandle;
    private Runnable mOnPowerOn;
    private CallbackContext mPowerOnCallbackContext;
    private boolean mRegisteredReceiver;
    private CallbackContext mResetCallbackContext;
    private CallbackContext mScanCallbackContext;

    /* renamed from: com.evothings.BLE.1 */
    class C01361 implements Runnable {
        final /* synthetic */ BluetoothAdapter val$adapter;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ LeScanCallback val$self;

        C01361(BluetoothAdapter bluetoothAdapter, LeScanCallback leScanCallback, CallbackContext callbackContext) {
            this.val$adapter = bluetoothAdapter;
            this.val$self = leScanCallback;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            Log.v(BLE.TAG, "Scanning!!!");
            if (this.val$adapter.startLeScan(this.val$self)) {
                BLE.this.mScanCallbackContext = this.val$callbackContext;
            } else {
                this.val$callbackContext.error("Android function startLeScan failed");
            }
        }
    }

    /* renamed from: com.evothings.BLE.2 */
    class C01372 implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ BluetoothAdapter val$adapter;
        final /* synthetic */ CordovaArgs val$args;
        final /* synthetic */ CallbackContext val$callbackContext;

        static {
            $assertionsDisabled = !BLE.class.desiredAssertionStatus() ? true : BLE.$assertionsDisabled;
        }

        C01372(CallbackContext callbackContext, BluetoothAdapter bluetoothAdapter, CordovaArgs cordovaArgs) {
            this.val$callbackContext = callbackContext;
            this.val$adapter = bluetoothAdapter;
            this.val$args = cordovaArgs;
        }

        public void run() {
            try {
                GattHandler gh = new GattHandler(BLE.this.mNextGattHandle, this.val$callbackContext);
                gh.mGatt = this.val$adapter.getRemoteDevice(this.val$args.getString(0)).connectGatt(BLE.this.mContext, BLE.$assertionsDisabled, gh);
                if (BLE.this.mGatt == null) {
                    BLE.this.mGatt = new HashMap();
                }
                Object res = BLE.this.mGatt.put(Integer.valueOf(BLE.this.mNextGattHandle), gh);
                if ($assertionsDisabled || res == null) {
                    BLE ble = BLE.this;
                    ble.mNextGattHandle++;
                    return;
                }
                throw new AssertionError();
            } catch (Exception e) {
                e.printStackTrace();
                this.val$callbackContext.error(e.toString());
            }
        }
    }

    /* renamed from: com.evothings.BLE.3 */
    class C01383 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ GattHandler val$gh;

        C01383(GattHandler gattHandler, CallbackContext callbackContext) {
            this.val$gh = gattHandler;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            this.val$gh.mCurrentOpContext = this.val$callbackContext;
            if (!this.val$gh.mGatt.discoverServices()) {
                this.val$gh.mCurrentOpContext = null;
                this.val$callbackContext.error("discoverServices");
                this.val$gh.process();
            }
        }
    }

    /* renamed from: com.evothings.BLE.4 */
    class C01394 implements Runnable {
        final /* synthetic */ CordovaArgs val$args;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ GattHandler val$gh;

        C01394(GattHandler gattHandler, CallbackContext callbackContext, CordovaArgs cordovaArgs) {
            this.val$gh = gattHandler;
            this.val$callbackContext = callbackContext;
            this.val$args = cordovaArgs;
        }

        public void run() {
            try {
                this.val$gh.mCurrentOpContext = this.val$callbackContext;
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) this.val$gh.mCharacteristics.get(Integer.valueOf(this.val$args.getInt(1)));
                Log.v(BLE.TAG, "Reading characteristic " + ch.getUuid());
                if (!this.val$gh.mGatt.readCharacteristic(ch)) {
                    this.val$gh.mCurrentOpContext = null;
                    this.val$callbackContext.error("readCharacteristic");
                    this.val$gh.process();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.val$callbackContext.error(e.toString());
                this.val$gh.process();
            }
        }
    }

    /* renamed from: com.evothings.BLE.5 */
    class C01405 implements Runnable {
        final /* synthetic */ CordovaArgs val$args;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ GattHandler val$gh;

        C01405(GattHandler gattHandler, CallbackContext callbackContext, CordovaArgs cordovaArgs) {
            this.val$gh = gattHandler;
            this.val$callbackContext = callbackContext;
            this.val$args = cordovaArgs;
        }

        public void run() {
            try {
                this.val$gh.mCurrentOpContext = this.val$callbackContext;
                if (!this.val$gh.mGatt.readDescriptor((BluetoothGattDescriptor) this.val$gh.mDescriptors.get(Integer.valueOf(this.val$args.getInt(1))))) {
                    this.val$gh.mCurrentOpContext = null;
                    this.val$callbackContext.error("readDescriptor");
                    this.val$gh.process();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.val$callbackContext.error(e.toString());
                this.val$gh.process();
            }
        }
    }

    /* renamed from: com.evothings.BLE.6 */
    class C01416 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ int val$characteristicHandle;
        final /* synthetic */ GattHandler val$gh;
        final /* synthetic */ byte[] val$toWrite;

        C01416(GattHandler gattHandler, CallbackContext callbackContext, int i, byte[] bArr) {
            this.val$gh = gattHandler;
            this.val$callbackContext = callbackContext;
            this.val$characteristicHandle = i;
            this.val$toWrite = bArr;
        }

        public void run() {
            try {
                this.val$gh.mCurrentOpContext = this.val$callbackContext;
                BluetoothGattCharacteristic c = (BluetoothGattCharacteristic) this.val$gh.mCharacteristics.get(Integer.valueOf(this.val$characteristicHandle));
                byte[] toSend = this.val$toWrite;
                Log.v(BLE.TAG, "Sending '" + BLE.this.format(toSend) + "' which is length " + toSend.length);
                c.setValue(toSend);
                if (!this.val$gh.mGatt.writeCharacteristic(c)) {
                    this.val$gh.mCurrentOpContext = null;
                    Log.e(BLE.TAG, "Call to writeCharacteristic failing with no indication of why!!!");
                    this.val$callbackContext.error("writeCharacteristic");
                    this.val$gh.process();
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.val$callbackContext.error(e.toString());
                this.val$gh.process();
            }
        }
    }

    /* renamed from: com.evothings.BLE.7 */
    class C01427 implements Runnable {
        final /* synthetic */ CordovaArgs val$args;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ GattHandler val$gh;

        C01427(GattHandler gattHandler, CallbackContext callbackContext, CordovaArgs cordovaArgs) {
            this.val$gh = gattHandler;
            this.val$callbackContext = callbackContext;
            this.val$args = cordovaArgs;
        }

        public void run() {
            try {
                this.val$gh.mCurrentOpContext = this.val$callbackContext;
                BluetoothGattDescriptor d = (BluetoothGattDescriptor) this.val$gh.mDescriptors.get(Integer.valueOf(this.val$args.getInt(1)));
                d.setValue(this.val$args.getArrayBuffer(2));
                if (!this.val$gh.mGatt.writeDescriptor(d)) {
                    this.val$gh.mCurrentOpContext = null;
                    this.val$callbackContext.error("writeDescriptor");
                    this.val$gh.process();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.val$callbackContext.error(e.toString());
                this.val$gh.process();
            }
        }
    }

    class BluetoothStateReceiver extends BroadcastReceiver {
        BluetoothStateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
            int state = a.getState();
            System.out.println("BluetoothState: " + a);
            if (BLE.this.mResetCallbackContext != null) {
                if (state == 10 && !a.enable()) {
                    BLE.this.mResetCallbackContext.error("enable");
                    BLE.this.mResetCallbackContext = null;
                }
                if (state == 12) {
                    BLE.this.mResetCallbackContext.success();
                    BLE.this.mResetCallbackContext = null;
                }
            }
        }
    }

    private class GattHandler extends BluetoothGattCallback {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final String TAG = "CSBLE_GH";
        HashMap<Integer, BluetoothGattCharacteristic> mCharacteristics;
        CallbackContext mConnectContext;
        CallbackContext mCurrentOpContext;
        HashMap<Integer, BluetoothGattDescriptor> mDescriptors;
        BluetoothGatt mGatt;
        final int mHandle;
        int mNextHandle;
        HashMap<BluetoothGattCharacteristic, CallbackContext> mNotifications;
        LinkedList<Runnable> mOperations;
        CallbackContext mRssiContext;
        HashMap<Integer, BluetoothGattService> mServices;

        static {
            $assertionsDisabled = !BLE.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        }

        GattHandler(int h, CallbackContext cc) {
            this.mOperations = new LinkedList();
            this.mNextHandle = 1;
            this.mNotifications = new HashMap();
            Log.v(TAG, "Constructing GattHandler " + h);
            this.mHandle = h;
            this.mConnectContext = cc;
        }

        void process() {
            Log.v(TAG, "GattHandler.process()");
            if (this.mCurrentOpContext != null) {
                Log.v(TAG, "  No op context, returning");
                return;
            }
            Runnable r = (Runnable) this.mOperations.poll();
            if (r == null) {
                Log.v(TAG, "  No runnable, returning");
            } else {
                r.run();
            }
        }

        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Log.v(TAG, "Received MTU change confirmation, new MTU " + mtu + ", status (ideal 0)" + status);
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.v(TAG, "Received connection state change with status " + status);
            Log.v(TAG, "Received connection state change with newStatus " + newState);
            if (status == 0 || newState == 2) {
                try {
                    JSONObject o = new JSONObject();
                    o.put("deviceHandle", this.mHandle);
                    o.put("state", newState);
                    BLE.this.keepCallback(this.mConnectContext, o);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                    return;
                }
            }
            this.mConnectContext.error(status);
        }

        public void onReadRemoteRssi(BluetoothGatt g, int rssi, int status) {
            Log.v(TAG, "Received remote rssi " + rssi);
            CallbackContext c = this.mRssiContext;
            this.mRssiContext = null;
            if (status == 0) {
                Log.v(TAG, "Responding with " + rssi);
                c.success(rssi);
                return;
            }
            Log.v(TAG, "Responding with error, status code " + status);
            c.error(status);
        }

        public void onServicesDiscovered(BluetoothGatt g, int status) {
            Log.v(TAG, "Received service discovery notification with status " + status);
            if (status == 0) {
                List<BluetoothGattService> services = g.getServices();
                JSONArray a = new JSONArray();
                for (BluetoothGattService s : services) {
                    if (this.mServices == null) {
                        this.mServices = new HashMap();
                    }
                    Object res = this.mServices.put(Integer.valueOf(this.mNextHandle), s);
                    if ($assertionsDisabled || res == null) {
                        try {
                            JSONObject o = new JSONObject();
                            o.put("handle", this.mNextHandle);
                            o.put("uuid", s.getUuid().toString());
                            o.put(MessagingSmsConsts.TYPE, s.getType());
                            this.mNextHandle++;
                            a.put(o);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (!$assertionsDisabled) {
                                throw new AssertionError();
                            }
                        }
                    } else {
                        throw new AssertionError();
                    }
                }
                Log.v(TAG, "Responding with " + a);
                this.mCurrentOpContext.success(a);
            } else {
                Log.v(TAG, "Responding with error, status code " + status);
                this.mCurrentOpContext.error(status);
            }
            this.mCurrentOpContext = null;
            process();
        }

        public void onCharacteristicRead(BluetoothGatt g, BluetoothGattCharacteristic c, int status) {
            Log.v(TAG, "Received characteristic read operation result");
            if (status == 0) {
                byte[] val = c.getValue();
                Log.v(TAG, "Responding with " + BLE.this.format(val) + ", which is length " + val.length);
                if (val.length == 0) {
                    Log.v(TAG, "No data, not returning anything");
                } else {
                    this.mCurrentOpContext.success(val);
                }
            } else {
                Log.v(TAG, "Responding with error, status code " + status);
                this.mCurrentOpContext.error(status);
            }
            this.mCurrentOpContext = null;
            process();
        }

        public void onDescriptorRead(BluetoothGatt g, BluetoothGattDescriptor d, int status) {
            Log.v(TAG, "Received descriptor read operation results");
            if (status == 0) {
                Log.v(TAG, "Responding with " + d.getValue());
                this.mCurrentOpContext.success(d.getValue());
            } else {
                Log.v(TAG, "Responding with error, status code " + status);
                this.mCurrentOpContext.error(status);
            }
            this.mCurrentOpContext = null;
            process();
        }

        public void onCharacteristicWrite(BluetoothGatt g, BluetoothGattCharacteristic c, int status) {
            Log.v(TAG, "Received characteristic write operation result");
            if (status == 0) {
                Log.v(TAG, "Responding with success");
                this.mCurrentOpContext.success();
            } else {
                Log.v(TAG, "Responding with error, status code " + status);
                this.mCurrentOpContext.error(status);
            }
            this.mCurrentOpContext = null;
            process();
        }

        public void onDescriptorWrite(BluetoothGatt g, BluetoothGattDescriptor d, int status) {
            Log.v(TAG, "Received descriptor write operation result");
            if (status == 0) {
                Log.v(TAG, "Responding with success");
                BLE.this.keepCallback(this.mCurrentOpContext, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            } else {
                Log.v(TAG, "Responding with error, status code " + status);
                this.mCurrentOpContext.error(status);
            }
            this.mCurrentOpContext = null;
            process();
        }

        public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c) {
            byte[] val = c.getValue();
            Log.v(TAG, "Received characteristic change with value " + c.getValue());
            CallbackContext cc = (CallbackContext) this.mNotifications.get(c);
            Log.v(TAG, "Responding with " + BLE.this.format(val) + ", which is length " + val.length);
            BLE.this.keepCallback(cc, c.getValue());
        }
    }

    static {
        $assertionsDisabled = !BLE.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }

    public BLE() {
        this.mRegisteredReceiver = $assertionsDisabled;
        this.mNextGattHandle = 1;
        this.mGatt = null;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.mContext = webView.getContext();
        Log.v(TAG, "Initializing BLE plugin");
        if (!this.mRegisteredReceiver) {
            this.mContext.registerReceiver(new BluetoothStateReceiver(), new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            this.mRegisteredReceiver = true;
        }
    }

    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if ("powerStatus".equals(action)) {
            return powerStatus(args, callbackContext);
        }
        if ("startScan".equals(action)) {
            startScan(args, callbackContext);
            return true;
        } else if ("stopScan".equals(action)) {
            stopScan(args, callbackContext);
            return true;
        } else if ("connect".equals(action)) {
            connect(args, callbackContext);
            return true;
        } else if ("close".equals(action)) {
            close(args, callbackContext);
            return true;
        } else if ("rssi".equals(action)) {
            rssi(args, callbackContext);
            return true;
        } else if ("services".equals(action)) {
            services(args, callbackContext);
            return true;
        } else if ("characteristics".equals(action)) {
            characteristics(args, callbackContext);
            return true;
        } else if ("descriptors".equals(action)) {
            descriptors(args, callbackContext);
            return true;
        } else if ("readCharacteristic".equals(action)) {
            readCharacteristic(args, callbackContext);
            return true;
        } else if ("readDescriptor".equals(action)) {
            readDescriptor(args, callbackContext);
            return true;
        } else if ("writeCharacteristic".equals(action)) {
            writeCharacteristic(args, callbackContext);
            return true;
        } else if ("writeDescriptor".equals(action)) {
            writeDescriptor(args, callbackContext);
            return true;
        } else if ("enableNotification".equals(action)) {
            enableNotification(args, callbackContext);
            return true;
        } else if ("disableNotification".equals(action)) {
            disableNotification(args, callbackContext);
            return true;
        } else if ("testCharConversion".equals(action)) {
            testCharConversion(args, callbackContext);
            return true;
        } else if (!"reset".equals(action)) {
            return $assertionsDisabled;
        } else {
            reset(args, callbackContext);
            return true;
        }
    }

    public void onReset() {
        Log.v(TAG, "Inside onReset");
        if (this.mScanCallbackContext != null) {
            BluetoothAdapter.getDefaultAdapter().stopLeScan(this);
            this.mScanCallbackContext = null;
        }
        if (this.mGatt != null) {
            for (GattHandler gh : this.mGatt.values()) {
                if (gh.mGatt != null) {
                    gh.mGatt.close();
                }
            }
            this.mGatt.clear();
        }
    }

    private boolean powerStatus(CordovaArgs args, CallbackContext cc) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            cc.error("No bluetooth adapter");
            return $assertionsDisabled;
        }
        int toReturn;
        switch (adapter.getState()) {
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                toReturn = 1;
                break;
            case TextInputType.TIME /*12*/:
                toReturn = 0;
                break;
            default:
                toReturn = 5;
                break;
        }
        try {
            JSONObject dict = new JSONObject();
            dict.put("state", toReturn);
            keepCallback(cc, dict);
            return true;
        } catch (JSONException e) {
            cc.error("Error sending power status: " + e.getMessage());
            return $assertionsDisabled;
        }
    }

    private void checkPowerState(BluetoothAdapter adapter, CallbackContext cc, Runnable onPowerOn) {
        Log.v(TAG, "Inside checkPowerState");
        if (adapter != null) {
            if (adapter.getState() == 12) {
                onPowerOn.run();
                return;
            }
            this.mOnPowerOn = onPowerOn;
            this.mPowerOnCallbackContext = cc;
            this.cordova.startActivityForResult(this, new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.v(TAG, "Inside onActivityResult");
        Runnable onPowerOn = this.mOnPowerOn;
        CallbackContext cc = this.mPowerOnCallbackContext;
        this.mOnPowerOn = null;
        this.mPowerOnCallbackContext = null;
        if (resultCode == -1) {
            onPowerOn.run();
        } else if (resultCode == 0) {
            cc.error("Bluetooth power-on canceled");
        } else {
            cc.error("Bluetooth power-on failed, code " + resultCode);
        }
    }

    private void keepCallback(CallbackContext callbackContext, JSONObject message) {
        PluginResult r = new PluginResult(Status.OK, message);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
    }

    private void keepCallback(CallbackContext callbackContext, String message) {
        Log.v(TAG, "Inside keepCallback with String " + message);
        PluginResult r = new PluginResult(Status.OK, message);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
    }

    private void keepCallback(CallbackContext callbackContext, byte[] message) {
        Log.v(TAG, "Inside keepCallback with byte array " + message);
        PluginResult r = new PluginResult(Status.OK, message);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
    }

    private void startScan(CordovaArgs args, CallbackContext callbackContext) {
        Log.v(TAG, "Inside startScan");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        checkPowerState(adapter, callbackContext, new C01361(adapter, this, callbackContext));
    }

    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (this.mScanCallbackContext != null) {
            try {
                JSONObject o = new JSONObject();
                o.put(MessagingSmsConsts.ADDRESS, device.getAddress());
                o.put("rssi", rssi);
                o.put("name", device.getName());
                JSONObject srMap = convertAdvertisementDataToJsonObject(scanRecord);
                o.put("scanRecord", format(scanRecord));
                o.put("advertisementData", srMap);
                if (this.mScanCallbackContext != null) {
                    keepCallback(this.mScanCallbackContext, o);
                }
            } catch (JSONException e) {
                this.mScanCallbackContext.error(e.toString());
            } catch (UnsupportedEncodingException e2) {
                this.mScanCallbackContext.error(e2.toString());
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.json.JSONObject convertAdvertisementDataToJsonObject(byte[] r23) throws org.json.JSONException, java.io.UnsupportedEncodingException {
        /*
        r22 = this;
        r12 = new org.json.JSONObject;
        r12.<init>();
        r7 = 0;
    L_0x0006:
        r0 = r23;
        r0 = r0.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0016;
    L_0x000f:
        r8 = r7 + 1;
        r10 = r23[r7];
        if (r10 != 0) goto L_0x0017;
    L_0x0015:
        r7 = r8;
    L_0x0016:
        return r12;
    L_0x0017:
        r13 = r23[r8];
        if (r13 != 0) goto L_0x001d;
    L_0x001b:
        r7 = r8;
        goto L_0x0016;
    L_0x001d:
        r17 = r8 + 1;
        r18 = r8 + r10;
        r0 = r23;
        r1 = r17;
        r2 = r18;
        r6 = java.util.Arrays.copyOfRange(r0, r1, r2);
        r17 = 9;
        r0 = r17;
        if (r13 != r0) goto L_0x0044;
    L_0x0031:
        r17 = "kCBAdvDataLocalName";
        r18 = new java.lang.String;
        r0 = r18;
        r0.<init>(r6);
        r0 = r17;
        r1 = r18;
        r12.put(r0, r1);
    L_0x0041:
        r7 = r8 + r10;
        goto L_0x0006;
    L_0x0044:
        r17 = 7;
        r0 = r17;
        if (r13 != r0) goto L_0x00e9;
    L_0x004a:
        r9 = 0;
        r14 = new org.json.JSONArray;
        r14.<init>();
    L_0x0050:
        r0 = r6.length;
        r17 = r0;
        r0 = r17;
        if (r9 >= r0) goto L_0x00e0;
    L_0x0057:
        r17 = r9 + 16;
        r0 = r6.length;
        r18 = r0;
        r0 = r17;
        r1 = r18;
        if (r0 > r1) goto L_0x00e0;
    L_0x0062:
        r17 = r9 + 16;
        r0 = r17;
        r15 = java.util.Arrays.copyOfRange(r6, r9, r0);
        r17 = 5;
        r0 = r17;
        r5 = new int[r0];
        r5 = {4, 2, 2, 2, 6};
        r16 = "";
        r0 = r15.length;
        r17 = r0;
        r11 = r17 + -1;
        r4 = 0;
    L_0x007b:
        r0 = r5.length;
        r17 = r0;
        r0 = r17;
        if (r4 >= r0) goto L_0x00d7;
    L_0x0082:
        if (r4 == 0) goto L_0x009b;
    L_0x0084:
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r0 = r17;
        r1 = r16;
        r17 = r0.append(r1);
        r18 = "-";
        r17 = r17.append(r18);
        r16 = r17.toString();
    L_0x009b:
        r3 = 0;
    L_0x009c:
        r17 = r5[r4];
        r0 = r17;
        if (r3 >= r0) goto L_0x00d4;
    L_0x00a2:
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r0 = r17;
        r1 = r16;
        r17 = r0.append(r1);
        r18 = "%02x";
        r19 = 1;
        r0 = r19;
        r0 = new java.lang.Object[r0];
        r19 = r0;
        r20 = 0;
        r21 = r15[r11];
        r21 = java.lang.Byte.valueOf(r21);
        r19[r20] = r21;
        r18 = java.lang.String.format(r18, r19);
        r17 = r17.append(r18);
        r16 = r17.toString();
        r3 = r3 + 1;
        r11 = r11 + -1;
        goto L_0x009c;
    L_0x00d4:
        r4 = r4 + 1;
        goto L_0x007b;
    L_0x00d7:
        r0 = r16;
        r14.put(r0);
        r9 = r9 + 16;
        goto L_0x0050;
    L_0x00e0:
        r17 = "kCBAdvDataServiceUUIDs";
        r0 = r17;
        r12.put(r0, r14);
        goto L_0x0041;
    L_0x00e9:
        r17 = "kCBAdvDataManufacturerData";
        r18 = 2;
        r0 = r18;
        r18 = android.util.Base64.encodeToString(r6, r0);
        r0 = r17;
        r1 = r18;
        r12.put(r0, r1);
        goto L_0x0041;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.evothings.BLE.convertAdvertisementDataToJsonObject(byte[]):org.json.JSONObject");
    }

    private void stopScan(CordovaArgs args, CallbackContext callbackContext) {
        Log.v(TAG, "Inside stopScan");
        BluetoothAdapter.getDefaultAdapter().stopLeScan(this);
        this.mScanCallbackContext = null;
    }

    private void connect(CordovaArgs args, CallbackContext callbackContext) {
        Log.v(TAG, "Inside connect with " + args);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        checkPowerState(adapter, callbackContext, new C01372(callbackContext, adapter, args));
    }

    private void close(CordovaArgs args, CallbackContext callbackContext) {
        Log.v(TAG, "Inside close with " + args);
        try {
            ((GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)))).mGatt.close();
            this.mGatt.remove(Integer.valueOf(args.getInt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void rssi(CordovaArgs args, CallbackContext callbackContext) {
        Log.v(TAG, "Inside rssi with " + args);
        try {
            GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
            if (gh.mRssiContext != null) {
                callbackContext.error("Previous call to rssi() not yet completed!");
                return;
            }
            gh.mRssiContext = callbackContext;
            if (!gh.mGatt.readRemoteRssi()) {
                gh.mRssiContext = null;
                callbackContext.error("readRemoteRssi");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != null) {
                null.mRssiContext = null;
            }
            callbackContext.error(e.toString());
        }
    }

    private void services(CordovaArgs args, CallbackContext callbackContext) {
        Log.v(TAG, "Inside services");
        try {
            GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
            gh.mOperations.add(new C01383(gh, callbackContext));
            gh.process();
        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void characteristics(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside characteristics");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        JSONArray a = new JSONArray();
        for (BluetoothGattCharacteristic c : ((BluetoothGattService) gh.mServices.get(Integer.valueOf(args.getInt(1)))).getCharacteristics()) {
            if (gh.mCharacteristics == null) {
                gh.mCharacteristics = new HashMap();
            }
            Object res = gh.mCharacteristics.put(Integer.valueOf(gh.mNextHandle), c);
            if ($assertionsDisabled || res == null) {
                JSONObject o = new JSONObject();
                o.put("handle", gh.mNextHandle);
                o.put("uuid", c.getUuid().toString());
                o.put("permissions", c.getPermissions());
                o.put("properties", c.getProperties());
                o.put("writeType", c.getWriteType());
                gh.mNextHandle++;
                a.put(o);
            } else {
                throw new AssertionError();
            }
        }
        callbackContext.success(a);
    }

    private void descriptors(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside descriptors");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        JSONArray a = new JSONArray();
        for (BluetoothGattDescriptor d : ((BluetoothGattCharacteristic) gh.mCharacteristics.get(Integer.valueOf(args.getInt(1)))).getDescriptors()) {
            if (gh.mDescriptors == null) {
                gh.mDescriptors = new HashMap();
            }
            Object res = gh.mDescriptors.put(Integer.valueOf(gh.mNextHandle), d);
            if ($assertionsDisabled || res == null) {
                JSONObject o = new JSONObject();
                o.put("handle", gh.mNextHandle);
                o.put("uuid", d.getUuid().toString());
                o.put("permissions", d.getPermissions());
                gh.mNextHandle++;
                a.put(o);
            } else {
                throw new AssertionError();
            }
        }
        callbackContext.success(a);
    }

    private void readCharacteristic(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside readCharacteristic with args [" + args.getInt(0) + ", " + args.getInt(1) + "]");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        gh.mOperations.add(new C01394(gh, callbackContext, args));
        gh.process();
    }

    private void readDescriptor(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside readDescriptor");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        gh.mOperations.add(new C01405(gh, callbackContext, args));
        gh.process();
    }

    private void writeCharacteristic(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside writeCharacteristic(" + args.getInt(0) + ", " + args.getInt(1) + ", " + args.getArrayBuffer(2) + ")");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        byte[] toWrite = args.getArrayBuffer(2);
        gh.mOperations.add(new C01416(gh, callbackContext, args.getInt(1), toWrite));
        gh.process();
    }

    private void writeDescriptor(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside writeDescriptor");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        gh.mOperations.add(new C01427(gh, callbackContext, args));
        gh.process();
    }

    private void enableNotification(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside enableNotification with " + args);
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        BluetoothGattCharacteristic c = (BluetoothGattCharacteristic) gh.mCharacteristics.get(Integer.valueOf(args.getInt(1)));
        gh.mNotifications.put(c, callbackContext);
        if (gh.mGatt.setCharacteristicNotification(c, true)) {
            gh.mCurrentOpContext = callbackContext;
            BluetoothGattDescriptor desc = c.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (!gh.mGatt.writeDescriptor(desc)) {
                Log.e(TAG, "Failed to write notification descriptor");
                callbackContext.error("writeDescriptor failed");
                return;
            }
            return;
        }
        Log.e(TAG, "Failed to enable characteristic notification");
        callbackContext.error("setCharacteristicNotification failed");
    }

    private void disableNotification(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside disableNotification");
        GattHandler gh = (GattHandler) this.mGatt.get(Integer.valueOf(args.getInt(0)));
        BluetoothGattCharacteristic c = (BluetoothGattCharacteristic) gh.mCharacteristics.get(Integer.valueOf(args.getInt(1)));
        gh.mNotifications.remove(c);
        if (gh.mGatt.setCharacteristicNotification(c, $assertionsDisabled)) {
            gh.mCurrentOpContext = callbackContext;
            BluetoothGattDescriptor desc = c.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
            desc.setValue(new byte[]{(byte) 0, (byte) 0});
            if (gh.mGatt.writeDescriptor(desc)) {
                callbackContext.success();
                return;
            }
            Log.e(TAG, "Failed to write disable notification descriptor");
            callbackContext.error("writeDescriptor failed");
            return;
        }
        Log.e(TAG, "Failed to disable characteristic notification");
        callbackContext.error("setCharacteristicNotification failed");
    }

    private void testCharConversion(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Inside testCharConversion");
        callbackContext.success(new byte[]{(byte) args.getInt(0)});
    }

    private String format(byte[] a) {
        String valStr = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        for (byte b : a) {
            valStr = valStr + String.format("%02x ", new Object[]{Byte.valueOf(b)});
        }
        return valStr;
    }

    private void reset(CordovaArgs args, CallbackContext cc) throws JSONException {
        Log.v(TAG, "Inside reset");
        this.mResetCallbackContext = null;
        BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
        if (this.mScanCallbackContext != null) {
            a.stopLeScan(this);
            this.mScanCallbackContext = null;
        }
        int state = a.getState();
        if (state == 11) {
            this.mResetCallbackContext = cc;
        } else if (state == 13) {
            this.mResetCallbackContext = cc;
        } else if (state == 10) {
            if (a.enable()) {
                this.mResetCallbackContext = cc;
            } else {
                cc.error("enable");
            }
        } else if (state != 12) {
            cc.error("Unknown state: " + state);
        } else if (a.disable()) {
            this.mResetCallbackContext = cc;
        } else {
            cc.error("disable");
        }
    }
}
